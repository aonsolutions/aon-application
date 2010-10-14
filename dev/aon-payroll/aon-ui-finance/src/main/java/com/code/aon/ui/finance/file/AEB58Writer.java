package com.code.aon.ui.finance.file;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.file.format.core.Account;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.bank.model.CSB58.CSB58;
import com.code.aon.file.bank.model.CSB58.data.Individual;
import com.code.aon.file.bank.model.CSB58.data.Lot;
import com.code.aon.file.bank.model.CSB58.data.Orderer;
import com.code.aon.file.bank.model.CSB58.data.Presenter;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
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

public class AEB58Writer implements IFinanceConstants {

	@SuppressWarnings("unchecked")
	public FileOutput createAEB58(Company company, FinanceBatch fbatch) throws ManagerBeanException {
		FBatchDetailController fBatchDetailController = (FBatchDetailController)FormUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
		return createAEB58(company, fbatch, (List)fBatchDetailController.getModel().getWrappedData());
	}
	
	@SuppressWarnings("unchecked")
	public FileOutput createAEB58(Company company, FinanceBatch fbatch, Collection fbatchDetailCollection) throws ManagerBeanException {
		Lot lot = new Lot();
		if(fbatch.getFinanceBatchType().equals(FinanceBatchType.AEB_58)) {
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
		orderer.setSufix(companyRBank.getSufix());
		orderer.setCodeINE(new Integer(1));

		Iterator iter = fbatchDetailCollection.iterator();
		while(iter.hasNext()){
			FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)iter.next();
			Individual individual  = createIndividual(fBatchDetail, lot.getType());
			orderer.addIndividual(individual);
		}
		lot.addOrderer(orderer);

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

	private Individual createIndividual(FinanceBatchDetail fBatchDetail, int lotType) throws ManagerBeanException {
		Individual individual = new Individual();
		individual.setAmount(new Double(fBatchDetail.getFinance().getTotalAmount()));
		Account ccc = new Account();
		if (fBatchDetail.getFinance().getBankAccount() != null && !fBatchDetail.getFinance().getBankAccount().equals("")) {
            ccc.parse(fBatchDetail.getFinance().getBankAccount().getValue());
            individual.setAccount(ccc);
        }
		individual.setConcept("FRA:" + fBatchDetail.getFinance().getInvoice().getReferenceCode());
		individual.setInternalCode(fBatchDetail.getFinance().getInvoice().getReferenceCode());
		individual.setName(fBatchDetail.getFinance().getInvoice().getRegistryName());
		individual.setReferenceCode(fBatchDetail.getFinance().getInvoice().getRegistryDocument()); 
		individual.setReturnCode(fBatchDetail.getFinance().getId().toString());
		if (fBatchDetail.getFinance().getDueDate().before(fBatchDetail.getFinanceBatch().getIssueDate())) {
			individual.setExpiryDate(fBatchDetail.getFinanceBatch().getIssueDate());
		} else {
			individual.setExpiryDate(fBatchDetail.getFinance().getDueDate());
		}
		IAddress detailAddress = obtainInvoiceAddress(fBatchDetail.getFinance().getInvoice());
		if(detailAddress != null){
			individual.setAccountUserAddress(detailAddress.getAddress());
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
		individual.setInitDate(new Date());
		return individual;
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