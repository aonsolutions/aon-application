package com.code.aon.ui.finance.file;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.company.Company;
import com.code.aon.config.BankAccount;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.file.bank.model.CSB19.data.Individual;
import com.code.aon.file.bank.model.CSB19.data.Lot;
import com.code.aon.file.bank.model.CSB19.data.Orderer;
import com.code.aon.file.bank.model.CSB19.data.Presenter;
import com.code.aon.file.bank.model.SEPA.Address;
import com.code.aon.file.bank.model.SEPA.SEPA19_14CoreXml;
import com.code.aon.file.format.core.Account;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.util.AonUtil;

public class SEPA19_14CoreXmlWriter {

	private LogPanelController logPanel;
	
	public void setLogPanel(LogPanelController logPanel) {
		this.logPanel = logPanel;
	}
	
	
	public FileOutput createXml(Company company, Date bankDate, FinanceBatch fBatch, List<FinanceBatchDetail> fbatchDetails) throws ManagerBeanException {
		AEB19Writer aeb19Writer = new AEB19Writer();
		aeb19Writer.setLogPanel(logPanel);
		Lot lot = aeb19Writer.getLot(company, fBatch, fbatchDetails);
		updateLot(lot, company, bankDate, fBatch, fbatchDetails);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		boolean cor1 = fBatch.getFinanceBatchType() == FinanceBatchType.SEPA_19_14_COR1_XML;
		FileFiller sepa1914 = new SEPA19_14CoreXml(lot, cor1, writer);
		FileOutput output = new FileOutput();
		output.setErrors(sepa1914.create());
		output.setContent(outputStream.toByteArray());
		return output;
	}

	private void updateLot( Lot lot, Company company, Date bankDate, FinanceBatch fBatch, List<FinanceBatchDetail> fbatchDetails ) throws ManagerBeanException {
		lot.setId(SEPA34_14XmlWriter.createId(company, fBatch, true));
		
		Presenter presenter = lot.getPresenter();
		presenter.setId(SEPA34_14XmlWriter.createId(company, fBatch, false));
		RegistryBank companyRBank = fBatch.getRegistryBank();
		Account account = new Account();
		account.setIban(companyRBank.getBankAccount().getIban());
		account.setBic(companyRBank.getBic());
		presenter.setAccount(account);
		
		Orderer orderer = lot.getOrderer();
		String id = createIdentification(company.getDocumentCountry(), presenter.getSufix(), company.getDocument());
		orderer.setId(id);
		Address address = SEPA34_14XmlWriter.getAddress(company.getDefaultAddress());
		orderer.setSEPAAddress(address);	
		orderer.setMakeDate(bankDate);
		
		Locale locale = AonUtil.getCurrentLocale();
		Iterator<Individual> ii = orderer.getIndividualsIterator();
		for( FinanceBatchDetail fBatchDetail : fbatchDetails ) {
			Finance finance = fBatchDetail.getFinance();
			Individual individual = ii.next();
			individual.setOrganisation(finance.getRegistry().getType()==RegistryType.LEGAL);
			individual.setDocument(finance.getRegistryDocument());		
			individual.setDocumentType(finance.getRegistryDocumentType().getName(locale));
			individual.setInternalCode(SEPA34_14XmlWriter.createId(finance));
			Account detailAccount = individual.getAccount();
			detailAccount.setBic(finance.getBic());
			detailAccount.setIban(finance.getBankAccount().getIban());
			IAddress iAddress = AEB19Writer.obtainInvoiceAddress(finance.getInvoice(), finance.getRegistry());
			if (iAddress != null) {
				individual.setSEPAAddress(SEPA34_14XmlWriter.getAddress(iAddress));
			}
			individual.setDocumentNumber(finance.getDocumentNumber());
		}
	}
	
	public static String createIdentification( Country country, String suffix, String document ) {
		BankAccount ba = new BankAccount();
		ba.setCountry(country);
		ba.setBban1(document);
		String controlDigit = ba.calculateIbanControlDigit();
		String _suffix = "000";
		if (! StringUtils.isEmpty(suffix)) {
			_suffix = StringUtils.leftPad(suffix, 3 ,'0');
		}
		return country.getValue() + controlDigit + _suffix + StringUtils.leftPad(document, 9 ,'0');
	}

}
