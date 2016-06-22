package com.code.aon.ui.finance.file;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.file.bank.model.CSB58.CSB58;
import com.code.aon.file.bank.model.CSB58.data.Individual;
import com.code.aon.file.bank.model.CSB58.data.Lot;
import com.code.aon.file.bank.model.CSB58.data.Orderer;
import com.code.aon.file.bank.model.CSB58.data.Presenter;
import com.code.aon.file.format.core.Account;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.esferalia.aon.entity.IEntityAlias;

public class AEB58Writer implements IFinanceConstants {

	private IProgression progression;
	
	public void setProgression(IProgression progression) {
		this.progression = progression;
	}

	private void updateProgress( int current, int total ) {
		if ( this.progression != null ) {
			this.progression.setProgressionCurrentValue(Math.round((current * 100.0)/total));
		}
	}
	
	public FileOutput createAEB58(Company company, FinanceBatch fBatch, List<FinanceBatchDetail> fbatchDetails) throws ManagerBeanException {
		Lot lot = getLot(company, fBatch, fbatchDetails);

		try {
			File file = File.createTempFile("AEB58_", ".txt");
			FileFiller csb58 = new CSB58(lot, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(csb58.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	public Lot getLot(Company company, FinanceBatch fBatch,  List<FinanceBatchDetail> fbatchDetails)  throws ManagerBeanException{
		Lot lot = new Lot();
		if (fBatch.getFinanceBatchType().equals(FinanceBatchType.AEB_58)) {
			lot.setType(Lot.RESUMED);
		} else {
			lot.setType(Lot.EXTENDED);
		}

		RegistryBank companyRBank = fBatch.getRegistryBank();
		Presenter presenter = new Presenter();
		presenter.setCode(company.getDocument());
		presenter.setSufix(companyRBank.getSufix());
		presenter.setMakeDate(fBatch.getIssueDate());
		presenter.setName(company.getName());
		presenter.setEntity(companyRBank.getBankAccount().getBban1());
		presenter.setOffice(companyRBank.getBankAccount().getBban2());
		lot.setPresenter(presenter);

		Orderer orderer = new Orderer();
		Account companyAccount = new Account();
		companyAccount.parse(companyRBank.getBankAccount().getBban());
		orderer.setAccount(companyAccount);
		orderer.setCode(company.getDocument());
		orderer.setName(company.getName());
		orderer.setSufix(companyRBank.getSufix());
		orderer.setCodeINE(new Integer(1));

		int current = 0;
		for( FinanceBatchDetail fBatchDetail : fbatchDetails ) {
			Individual individual  = createIndividual(fBatchDetail.getFinance(), fBatch.getIssueDate(), lot.getType());
			orderer.addIndividual(individual);
			updateProgress(++current, fbatchDetails.size());
		}
		lot.addOrderer(orderer);
		return lot;
	}
	
	public Lot getLot(Company company, FinanceBatch fBatch,  List<FinanceBatchDetail> fbatchDetails, Date date)  throws ManagerBeanException{
		Lot lot = new Lot();
		if (fBatch.getFinanceBatchType().equals(FinanceBatchType.AEB_58)) {
			lot.setType(Lot.RESUMED);
		} else {
			lot.setType(Lot.EXTENDED);
		}

		RegistryBank companyRBank = fBatch.getRegistryBank();
		Presenter presenter = new Presenter();
		presenter.setCode(company.getDocument());
		presenter.setSufix(companyRBank.getSufix());
		presenter.setMakeDate(date);
		presenter.setName(company.getName());
		presenter.setEntity(companyRBank.getBankAccount().getBban1());
		presenter.setOffice(companyRBank.getBankAccount().getBban2());
		lot.setPresenter(presenter);

		Orderer orderer = new Orderer();
		Account companyAccount = new Account();
		companyAccount.parse(companyRBank.getBankAccount().getBban());
		orderer.setAccount(companyAccount);
		orderer.setCode(company.getDocument());
		orderer.setName(company.getName());
		orderer.setSufix(companyRBank.getSufix());
		orderer.setCodeINE(new Integer(1));

		int current = 0;
		for( FinanceBatchDetail fBatchDetail : fbatchDetails ) {
			Individual individual  = createIndividual(fBatchDetail.getFinance(), fBatch.getIssueDate(), lot.getType());
			orderer.addIndividual(individual);
			updateProgress(++current, fbatchDetails.size());
		}
		lot.addOrderer(orderer);
		return lot;
	}

	private Individual createIndividual(Finance finance, Date expiryDate, int lotType) throws ManagerBeanException {
		Individual individual = new Individual();
		individual.setAmount(new Double(finance.getTotalAmount()));
		Account ccc = new Account();
		if (finance.getBankAccount() != null && !finance.getBankAccount().equals("")) {
            ccc.parse(finance.getBankAccount().getBban());
            individual.setAccount(ccc);
        }
		individual.setConcept(obtainConcept(finance));
		individual.setInitDate((!finance.isEmptyInvoice())?finance.getInvoice().getIssueDate():expiryDate);
		individual.setInternalCode(finance.getId().toString());
		individual.setName(finance.getRegistryName());
		individual.setReferenceCode(finance.getRegistry().getId().toString()); 
		individual.setReturnCode(finance.getRegistry().getId().toString());
		if (finance.getDueDate().before(expiryDate)) {
			individual.setExpiryDate(expiryDate);
		} else {
			individual.setExpiryDate(finance.getDueDate());
		}
		IAddress iAddress = obtainInvoiceAddress(finance.getInvoice(), finance.getRegistry());
		if (iAddress != null) {
			individual.setAccountUserAddress(iAddress.getFullAddress());
			individual.setAccountUserAddress2(iAddress.getCity());
			try {
				individual.setAccountUserPCode(new Integer(iAddress.getZip()));
			} catch (NumberFormatException e) {
				individual.setAccountUserPCode(new Integer(0));
			}
		}
		if (lotType == Lot.EXTENDED) {
			addExtendedData(individual, finance.getInvoice());
		}
		return individual;
	}
	
	private String obtainConcept(Finance finance) {
		String concept = finance.getConcept();
		if (!finance.isEmptyInvoice()) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
			String invoice = finance.getInvoice().getReferenceCode();
			String date = formatter.format(finance.getInvoice().getDate());
			String document = finance.getRegistryDocument();

			concept = "FRA:" + invoice + " " + date + " NIF:" + document;
		}
		return (concept.length() > 40)?concept.substring(0, 39):concept;
	}

	@SuppressWarnings("rawtypes")
	private void addExtendedData(Individual individual, Invoice invoice) throws ManagerBeanException {
		if (invoice != null && invoice.getId() != null) {
			NumberFormat formatter = new DecimalFormat("###,###,##0.00");

			individual.addConcept("         CANT.   PRECIO   %DTO     TOTAL");
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			InvoicePriceStrategy strategy = new InvoicePriceStrategy();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			Iterator iterator = invoiceDetailBean.getList(criteria).iterator();
			for (int i=0; i<5&&iterator.hasNext(); i++) {
				InvoiceDetail detail = (InvoiceDetail)iterator.next();
				String descConcept = detail.getDescription();
				descConcept = descConcept.replace("\r", " ");
				descConcept = descConcept.replace("\n", " ");
				String priceConcept = new String();
				if (descConcept.length() > 40) {
					priceConcept = (descConcept.length() > 48)?descConcept.substring(40, 48):descConcept.substring(40, descConcept.length());
					descConcept = descConcept.substring(0, 40);
				}
				individual.addConcept(descConcept);

				String quantity = (detail.getQuantity()==0)?"":formatter.format(detail.getQuantity());
				String price = (detail.getPrice()==0)?"":formatter.format(detail.getPrice());
				double[] discounts = detail.getDiscountExpression().getDiscounts();
				String discount = (discounts==null||discounts[0]==0)?"":formatter.format(discounts[0]);
				String base = (strategy.getBasePrice(detail)==0)?"":formatter.format(strategy.getBasePrice(detail));
				priceConcept = priceConcept + StringUtils.repeat(" ", 8-priceConcept.length()) +
								StringUtils.repeat(" ", 6-quantity.length()) + quantity +
								StringUtils.repeat(" ", 9-price.length()) + price +
								StringUtils.repeat(" ", 7-discount.length()) + discount +
								StringUtils.repeat(" ", 10-base.length()) + base;
				individual.addConcept(priceConcept);
			}
			if (iterator.hasNext()) {
				double total = 0.0;
				while (iterator.hasNext()) {
					InvoiceDetail detail = (InvoiceDetail)iterator.next();
					total += detail.getTaxableBase();
				}
				individual.addConcept("OTROS CONCEPTOS NO DETALLADOS...");
				String base = (total==0)?"":formatter.format(total);
				individual.addConcept(StringUtils.repeat(" ", 30) + StringUtils.repeat(" ", 10-base.length()) + base);
			}
			Iterator taxIterator = strategy.getTaxBreakDowns(invoice, invoice).iterator();
			for (int i=0; i<2; i++) {
				if (taxIterator.hasNext()) {
					TaxBreakDown taxBreakDown = (TaxBreakDown)taxIterator.next();
					if (taxBreakDown.getTaxType().equals(TaxType.VAT)) {
						String taxConcept = formatter.format(taxBreakDown.getTaxPercent()) + "% IVA  SOBRE  " + 
											formatter.format(taxBreakDown.getBase()) + "  =  " + 
											formatter.format(taxBreakDown.getTaxQuota());
						individual.addConcept(taxConcept);

						if (invoice.isSurcharge()) {
							String surchargeConcept = formatter.format(taxBreakDown.getSurchargePercent()) + "% RE   SOBRE  " +
													  formatter.format(taxBreakDown.getBase()) + "  =  " +
													  formatter.format(taxBreakDown.getSurchargeQuota());
							individual.addConcept(surchargeConcept);
						}
					}
					if (taxBreakDown.getTaxType().equals(TaxType.RETENTION)) {
						String taxConcept = formatter.format(taxBreakDown.getTaxPercent()) + "% IRPF SOBRE  " + 
											formatter.format(taxBreakDown.getBase()) + "  =  " + 
											formatter.format(taxBreakDown.getTaxQuota());
						individual.addConcept(taxConcept);
					}
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private IAddress obtainInvoiceAddress(Invoice invoice, Registry registry) throws ManagerBeanException {
		if (invoice != null && invoice.getId() != null) {
			IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceAddressBean.getFieldName(IEntityAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
			Iterator iterator = invoiceAddressBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				return (InvoiceAddress)iterator.next();
			}
		}
		return obtainRegistryAddress(registry.getId());
	}

	@SuppressWarnings("rawtypes")
	private IAddress obtainRegistryAddress(Integer registryId) throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), registryId);
		Iterator iterator = rAddressBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (RegistryAddress)iterator.next();
		}
		return null;
	}

}