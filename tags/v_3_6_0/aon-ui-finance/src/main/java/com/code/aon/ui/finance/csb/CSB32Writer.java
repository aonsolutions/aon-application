package com.code.aon.ui.finance.csb;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.csb.fd0.core.Account;
import com.code.aon.csb.fd0.model.FileFiller;
import com.code.aon.csb.fd0.model.CSB32.CSB32;
import com.code.aon.csb.fd0.model.CSB32.data.Delivery;
import com.code.aon.csb.fd0.model.CSB32.data.Individual;
import com.code.aon.csb.fd0.model.CSB32.data.Lot;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.csb.CSBOutput;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.finance.controller.FBatchDetailController;
import com.code.aon.ui.form.FormUtil;

public class CSB32Writer {

	private static final String FINANCE_BATCH_DETAIL_CONTROLLER_NAME = "fBatchDetail";

	@SuppressWarnings("unchecked")
	public CSBOutput createCSB32(Company company, FinanceBatch fbatch) throws ManagerBeanException {
		FBatchDetailController fBatchDetailController = (FBatchDetailController)FormUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
		return createCSB32(company, fbatch, (List)fBatchDetailController.getModel().getWrappedData());
	}
	
	@SuppressWarnings("unchecked")
	public CSBOutput createCSB32(Company company, FinanceBatch fbatch, Collection fbatchDetailCollection) throws ManagerBeanException {
		Lot lot = new Lot();
		RegistryBank companyRBank = fbatch.getRegistryBank();
		lot.setEntity(new Integer(companyRBank.getBankAccount().getEntity()));
		lot.setOffice(new Integer(companyRBank.getBankAccount().getOffice()));
		lot.setFileDate(new Date());
		lot.setFileNumber(new Integer(1));

		Delivery delivery = new Delivery();
		delivery.setDeliveyNumber(fbatch.getId());
		delivery.setGiverCode(company.getDocument());
		Account ccc1 = new Account();
		ccc1.parse(companyRBank.getBankAccount().getValue());
		delivery.setNotPayedAccount(ccc1);
		Account ccc2 = new Account();
		ccc2.parse(companyRBank.getBankAccount().getValue());
		delivery.setOweAccount(ccc2);
		Account ccc3 = new Account();
		ccc3.parse(companyRBank.getBankAccount().getValue());
		delivery.setPaymentAccount(ccc3);
		delivery.setTruncatedEffects(new Integer(1));

		Iterator iter = fbatchDetailCollection.iterator();
		while(iter.hasNext()){
			FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)iter.next();
			Individual individual = createIndividual(company, fBatchDetail);
			delivery.addIndividual(individual);
		}
		lot.addDelivery(delivery);

		try {
			File file = File.createTempFile("CSB32_", ".txt");
			FileFiller csb32 = new CSB32(lot, file.getAbsolutePath());
			CSBOutput output = new CSBOutput();
			output.setFile(file);
			output.setErrors(csb32.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private Individual createIndividual(Company company, FinanceBatchDetail fBatchDetail) throws ManagerBeanException {
		Individual individual = new Individual();
		Account ccc = new Account();
		ccc.parse(fBatchDetail.getFinance().getBankAccount().getValue());
		individual.setAccount(ccc);
		individual.setAceptedCode(new Integer(2));
		individual.setAditionalData(fBatchDetail.getFinance().getId().toString());
		individual.setAmount(new Double(fBatchDetail.getFinance().getTotalAmount()));
		individual.setDocumentNumber(fBatchDetail.getFinance().getInvoice().getReferenceCode());
		individual.setDocumentType(new Integer(2)); // RECIBO
		individual.setEfectPayed(fBatchDetail.getFinance().getInvoice().getRegistryName());
		individual.setEfectPayer(company.getName());
		individual.setExpenseClause(new Integer(0));
		individual.setExpiryDate(fBatchDetail.getFinanceBatch().getIssueDate());
		individual.setPayedDocument(fBatchDetail.getFinance().getInvoice().getRegistryDocument());
		IAddress detailAddress = obtainInvoiceAddress(fBatchDetail.getFinance().getInvoice());
		if(detailAddress != null){
			individual.setPayedPost(detailAddress.getCity());
			try {
				individual.setPayedPostPostalCode(new Integer(detailAddress.getZip()));
				individual.setPayedPostProvince(new Integer(detailAddress.getZip().substring(0, 1)));
			} catch (NumberFormatException e) {
				individual.setPayedPostPostalCode(new Integer(0));
				individual.setPayedPostProvince(new Integer(0));
			}
			individual.setPayedAddress(detailAddress.getAddress() + detailAddress.getAddress2());
		}
		individual.setPaymentDate(fBatchDetail.getFinance().getDueDate());
		IAddress companyAddress = obtainRegistryAddress(company.getId());
		if(companyAddress != null){
			individual.setPaymentPost(companyAddress.getCity());
			try {
				individual.setProvinceNumber(new Integer(companyAddress.getZip().substring(0, 1)));
			} catch (NumberFormatException e) {
				individual.setProvinceNumber(new Integer(0));
			}
		}
		return individual;
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
