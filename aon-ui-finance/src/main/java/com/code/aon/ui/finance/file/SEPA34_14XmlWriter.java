package com.code.aon.ui.finance.file;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.IProgression;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.company.Company;
import com.code.aon.file.bank.model.CSB34.data.Detail;
import com.code.aon.file.bank.model.CSB34.data.Master;
import com.code.aon.file.bank.model.CSB34.data.Orderer;
import com.code.aon.file.bank.model.CSB34.data.Receiver;
import com.code.aon.file.bank.model.SEPA.Address;
import com.code.aon.file.bank.model.SEPA.SEPA34_14Xml;
import com.code.aon.file.format.core.Account;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.util.AonUtil;

public class SEPA34_14XmlWriter {
	
	private IProgression progression;
	
	public void setProgression(IProgression progression) {
		this.progression = progression;
	}

	public FileOutput createXml(Company company, FinanceBatch fBatch, List<FinanceBatchDetail> fbatchDetails) throws ManagerBeanException {
		AEB34Writer aeb34Writer = new AEB34Writer();
		aeb34Writer.setProgression(progression);
		Master master = aeb34Writer.getMaster(company, fBatch, fbatchDetails);
		updateMaster(master, company, fBatch, fbatchDetails);
		try {
			File file = File.createTempFile("SEPA34_14_", ".xml");
			FileFiller sepa3414 = new SEPA34_14Xml(master, file);
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(sepa3414.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	private void updateMaster( Master master, Company company, FinanceBatch fBatch, List<FinanceBatchDetail> fbatchDetails ) throws ManagerBeanException {
		master.setId(createId(company, fBatch, true));
		String companyId = createIdentification(company.getDocument(), fBatch.getRegistryBank().getSufix());
		master.setCompanyId(companyId);
		RegistryBank companyRBank = fBatch.getRegistryBank();		
		Account account = master.getAccount();
		account.setIban(companyRBank.getBankAccount().getIban());
		account.setBic(companyRBank.getBic());
		
		Orderer orderer = master.getOrderer();
		orderer.setId(SEPA34_14XmlWriter.createId(company, fBatch, false));		
		orderer.setDocument(company.getDocument()+"001");
		Address address = getAddress(company.getDefaultAddress());
		orderer.setSEPAAddress(address);

		Locale locale = AonUtil.getCurrentLocale();
		double total = 0.0;
		Iterator<Detail> ir = master.getReceiversIterator();
		for( FinanceBatchDetail fBatchDetail : fbatchDetails ) {
			Finance finance = fBatchDetail.getFinance();
			Detail detail = ir.next();
			Receiver receiver = detail.getReceiver();
			total += detail.getAmount();
			detail.setCategoryPurposeCode(getCategoryPurposeCode(finance));
			Account detailAccount = detail.getAccount();
			detailAccount.setBic(finance.getBic());
			detailAccount.setIban(finance.getBankAccount().getIban());			
			detail.getReceiver().setReferenceCode(SEPA34_14XmlWriter.createId(finance));
			receiver.setOrganisation(finance.getRegistry().getType()==RegistryType.LEGAL);
			receiver.setDocumentType(finance.getRegistryDocumentType().getName(locale));
			IAddress iAddress = AEB34Writer.obtainInvoiceAddress(finance.getInvoice(), finance.getRegistry());
			if (iAddress != null) {
				receiver.setSEPAAddress(SEPA34_14XmlWriter.getAddress(iAddress));
			}	
			detail.setDocumentNumber(finance.getReferenceCode());
		}
		master.setAmount(total);
	}
	
	private static String getTimestampString( Date date ) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		df.setTimeZone(tz);	
		return df.format(date);		
	}

	private static String getDateString( Date date ) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		df.setTimeZone(tz);	
		return df.format(date);		
	}
	
	public static String createId( Company company, FinanceBatch fbatch, boolean includeId ) {
		StringBuffer sb = new StringBuffer();
		sb.append('A').append(StringUtils.leftPad(fbatch.getId().toString(), 10 ,'0'));
		sb.append(getTimestampString(fbatch.getIssueDate()));
		String value = null;
		if ( includeId ) {
			value = company.getId().toString();
		} else{
			value = company.getDocument();
		}
		sb.append(StringUtils.leftPad(value, 10 ,'0'));
		return sb.toString();
	}

	public static String createId( Finance finance ) {
		StringBuffer sb = new StringBuffer();
		sb.append(finance.getId()).append('/');
		sb.append(finance.getRegistryDocument()).append('/');
		sb.append(getDateString(finance.getDueDate()));
		return sb.toString();
	}
	
	public static String createIdentification( String document, String suffix ) {
		String _suffix = "000";
		if (! StringUtils.isEmpty(suffix)) {
			_suffix = StringUtils.leftPad(suffix, 3 ,'0');
		}
		return StringUtils.leftPad(document, 9 ,'0') + _suffix;
	}

	public static Address getAddress( IAddress iAddress ) {
		String countryCode = Country.ES.getValue();
		Address result = new Address();
		if ( iAddress.getGeozone() != null ) {
			GeoZone country = iAddress.getGeozone().getGeoZoneCountry();
			if ( country != null ) {
				countryCode = country.getCode();	
			}
		}
		result.setCountry(countryCode);
		StringBuffer sb = new StringBuffer();
		sb.append(iAddress.getFullAddress());
		if (! StringUtils.isEmpty(iAddress.getZip()) ) {
			sb.append(" ").append(iAddress.getZip());
		}
		String location = iAddress.getLocation();
		if (! StringUtils.isEmpty(location) ) {
			sb.append(" ").append(location);
		}
		result.setAddressLine(sb.toString());
		return result;
	}
	
	private String getCategoryPurposeCode( Finance finance ) {
		String code = null;
		if ( finance.isPayroll() ) {
			code = "SALA";
		} else {
			switch( finance.getPayMethod().getType() ) {
				case CREDIT_CARD:
					code = "CCRD";
					break;
				case DEBIT_CARD:
					code = "DCRD";
					break;
				case CASH_BASIS:
				case BANK_TRANSFER:
				case CHEQUE:				
				case NEGOTIABLE_DOCUMENT:
				case OTHER:
				default:
					code = "CASH";
			}			
		}
		return code;
	}
	
}
