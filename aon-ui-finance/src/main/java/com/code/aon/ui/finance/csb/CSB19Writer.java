package com.code.aon.ui.finance.csb;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.csb.fd0.core.Account;
import com.code.aon.csb.fd0.model.FileFiller;
import com.code.aon.csb.fd0.model.CSB19.CSB19;
import com.code.aon.csb.fd0.model.CSB19.data.Individual;
import com.code.aon.csb.fd0.model.CSB19.data.Lot;
import com.code.aon.csb.fd0.model.CSB19.data.Orderer;
import com.code.aon.csb.fd0.model.CSB19.data.Presenter;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.csb.CSBOutput;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.finance.controller.FBatchDetailController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.form.FormUtil;

public class CSB19Writer implements IFinanceConstants {
	
	@SuppressWarnings("unchecked")
	public CSBOutput createCSB19(Company company, FinanceBatch fbatch) throws ManagerBeanException {
		FBatchDetailController fBatchDetailController = (FBatchDetailController)FormUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
		return createCSB19(company, fbatch, (List)fBatchDetailController.getModel().getWrappedData());
	}
	
	@SuppressWarnings("unchecked")
	public CSBOutput createCSB19(Company company, FinanceBatch fbatch, Collection fbatchDetailCollection) throws ManagerBeanException {
		Lot lot = new Lot();
		if(fbatch.getFinanceBatchType().equals(FinanceBatchType.CSB_19)) {
			lot.setType(Lot.RESUMED);
		} else {
			lot.setType(Lot.EXTENDED);
		}

		RegistryBank companyRBank = fbatch.getRegistryBank();
		Presenter presenter = new Presenter();
		presenter.setCode(company.getDocument());
		presenter.setSufix(companyRBank.getSufix());
		presenter.setMakeDate(fbatch.getIssueDate());
		presenter.setName(company.getName());
		presenter.setEntity(companyRBank.getBankAccount().getEntity());
		presenter.setOffice(companyRBank.getBankAccount().getOffice());
		lot.setPresenter(presenter);

		Orderer orderer = new Orderer();
		Account companyAccount = new Account();
		companyAccount.parse(companyRBank.getBankAccount().getValue());
		orderer.setAccount(companyAccount);
		orderer.setCode(company.getDocument());
        orderer.setName(company.getName());
		orderer.setMakeDate(fbatch.getIssueDate());
		orderer.setProcedure(new Integer(1));
		orderer.setStartDate(fbatch.getIssueDate());
		orderer.setSufix(companyRBank.getSufix());

		Iterator iter = fbatchDetailCollection.iterator();
		while(iter.hasNext()){
			FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)iter.next();
			Individual individual  = createIndividual(fBatchDetail, lot.getType());
			orderer.addIndividual(individual);
		}
		lot.addOrderer(orderer);

		try {
			File file = File.createTempFile("CSB19_", ".txt");
			FileFiller csb19 = new CSB19(lot, file.getAbsolutePath());
			CSBOutput output = new CSBOutput();
			output.setFile(file);
			output.setErrors(csb19.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	private Individual createIndividual(FinanceBatchDetail fBatchDetail, int lotType) throws ManagerBeanException {
		Individual individual = new Individual();
		individual.setAmount(new Double(fBatchDetail.getFinance().getTotalAmount()));
		Account detailAccount = new Account();
		detailAccount.parse(fBatchDetail.getFinance().getBankAccount().getValue());
		individual.setAccount(detailAccount);
		individual.setConcept(createIndividualConcept(fBatchDetail.getFinance()));
		individual.setInternalCode(fBatchDetail.getFinance().getInvoice().getReferenceCode());
		individual.setName(fBatchDetail.getFinance().getInvoice().getRegistryName());
		individual.setReferenceCode(fBatchDetail.getFinance().getRegistry().getId().toString());
		individual.setReturnCode(fBatchDetail.getFinance().getRegistry().getId().toString());
		IAddress detailAddress = obtainInvoiceAddress(fBatchDetail.getFinance().getInvoice());
		individual.setAccountUserName(fBatchDetail.getFinance().getInvoice().getRegistryName());
		if(detailAddress != null){
			individual.setAccountUserAddress(detailAddress.getAddress() + " " + detailAddress.getAddress2());
			individual.setAccountUserAddress2(detailAddress.getCity());
			try {
				individual.setAccountUserPCode(new Integer(detailAddress.getZip()));
			} catch (NumberFormatException e) {
				individual.setAccountUserPCode(new Integer(0));
			}
		}
		if(lotType == Lot.EXTENDED){
			addExtendedData(individual, fBatchDetail.getFinance().getInvoice());
		}
		return individual;
	}

	private String createIndividualConcept(Finance finance) {
		String concept = "";
		if (finance.getInvoice() != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
			String invoice = finance.getInvoice().getReferenceCode();
			String date = formatter.format(finance.getInvoice().getDate());
			String document = finance.getInvoice().getRegistryDocument();

			concept = "FRA:" + invoice + " " + date + " NIF:" + document;
		}
		return (concept.length() > 40)?concept.substring(0, 39):concept;
	}

	@SuppressWarnings("unchecked")
	private void addExtendedData(Individual individual, Invoice invoice) throws ManagerBeanException {
		NumberFormat formatter = new DecimalFormat("###,###,##0.00");

		individual.addConcept("         CANT.   PRECIO   %DTO     TOTAL");
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		InvoicePriceStrategy strategy = new InvoicePriceStrategy();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator iter = invoiceDetailBean.getList(criteria).iterator();
		for(int i=0; i<5&&iter.hasNext(); i++){
			InvoiceDetail detail = (InvoiceDetail)iter.next();
			String descConcept = detail.getDescription();
			descConcept = descConcept.replace("\r", " ");
			descConcept = descConcept.replace("\n", " ");
			String priceConcept = new String();
			if(descConcept.length() > 40){
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
		if(iter.hasNext()){
			double total = 0.0;
			while(iter.hasNext()){
				InvoiceDetail detail = (InvoiceDetail)iter.next();
				total += detail.getTaxableBase();
			}
			individual.addConcept("OTROS CONCEPTOS NO DETALLADOS...");
			String base = (total==0)?"":formatter.format(total);
			individual.addConcept(StringUtils.repeat(" ", 30) + StringUtils.repeat(" ", 10-base.length()) + base);
		}
		Iterator taxIter = strategy.getTaxBreakDowns(invoice, invoice).iterator();
		for(int i=0;i<2;i++){
			if(taxIter.hasNext()){
				TaxBreakDown taxBreakDown = (TaxBreakDown)taxIter.next();
				if(taxBreakDown.getTaxType().equals(TaxType.VAT)){
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
				if(taxBreakDown.getTaxType().equals(TaxType.RETENTION)){
					String taxConcept = formatter.format(taxBreakDown.getTaxPercent()) + "% IRPF SOBRE  " + 
										formatter.format(taxBreakDown.getBase()) + "  =  " + 
										formatter.format(taxBreakDown.getTaxQuota());
					individual.addConcept(taxConcept);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private IAddress obtainInvoiceAddress(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAddressBean.getFieldName(IFinanceAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
		Iterator iterator = invoiceAddressBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (InvoiceAddress)iterator.next();
		}
		return obtainRegistryAddress(invoice.getRegistry().getId());
	}

	@SuppressWarnings("unchecked")
	private IAddress obtainRegistryAddress(Integer registryId) throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), registryId);
		Iterator iterator = rAddressBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (RegistryAddress)iterator.next();
		}
		return null;
	}

}
