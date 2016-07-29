package com.code.aon.ui.finance.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.file.bank.model.CSB32.CSB32;
import com.code.aon.file.bank.model.CSB32.data.Delivery;
import com.code.aon.file.bank.model.CSB32.data.Individual;
import com.code.aon.file.bank.model.CSB32.data.Lot;
import com.code.aon.file.format.core.Account;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.esferalia.aon.entity.IEntityAlias;

public class AEB32Writer implements IFinanceConstants {

	public FileOutput createAEB32(Company company, FinanceBatch fBatch, List<FinanceBatchDetail> fbatchDetails) throws ManagerBeanException {
		Lot lot = new Lot();
		RegistryBank companyRBank = fBatch.getRegistryBank();
		lot.setEntity(new Integer(companyRBank.getBankAccount().getBban1()));
		lot.setOffice(new Integer(companyRBank.getBankAccount().getBban2()));
		lot.setFileDate(new Date());
		lot.setFileNumber(new Integer(1));

		Delivery delivery = new Delivery();
		delivery.setDeliveyNumber(fBatch.getId());
		delivery.setGiverCode(company.getDocument());
		Account ccc1 = new Account();
		ccc1.parse(companyRBank.getBankAccount().getBban());
		delivery.setNotPayedAccount(ccc1);
		Account ccc2 = new Account();
		ccc2.parse(companyRBank.getBankAccount().getBban());
		delivery.setOweAccount(ccc2);
		Account ccc3 = new Account();
		ccc3.parse(companyRBank.getBankAccount().getBban());
		delivery.setPaymentAccount(ccc3);
		delivery.setTruncatedEffects(new Integer(1));

		for( FinanceBatchDetail fBatchDetail : fbatchDetails ) {
			Individual individual = createIndividual(company, fBatchDetail.getFinance(), fBatch.getIssueDate());
			delivery.addIndividual(individual);
		}
		lot.addDelivery(delivery);

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);
			FileFiller csb32 = new CSB32(lot, writer);
			FileOutput output = new FileOutput();
			output.setErrors(csb32.create());
			output.setContent(outputStream.toByteArray());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private Individual createIndividual(Company company, Finance finance, Date expiryDate) throws ManagerBeanException {
		Individual individual = new Individual();
		Account ccc = new Account();
		ccc.parse(finance.getBankAccount().getBban());
		individual.setAccount(ccc);
		individual.setAceptedCode(new Integer(2));
		individual.setAditionalData(finance.getId().toString());
		individual.setAmount(new Double(finance.getTotalAmount()));
		individual.setDocumentNumber(finance.getId().toString());
		individual.setDocumentType(new Integer(2)); // RECIBO
		individual.setEfectPayed(finance.getRegistryName());
		individual.setEfectPayer(company.getName());
		individual.setExpenseClause(new Integer(0));
		individual.setExpiryDate(expiryDate);
		individual.setPayedDocument(finance.getRegistryDocument());
		IAddress iAddress = obtainInvoiceAddress(finance.getInvoice(), finance.getRegistry());
		individual.setPaymentDate(finance.getDueDate());
		if (iAddress != null) {
			individual.setPayedAddress(iAddress.getFullAddress());
			individual.setPayedPost(iAddress.getCity());
			try {
				individual.setPayedPostPostalCode(new Integer(iAddress.getZip()));
				individual.setPayedPostProvince(new Integer(iAddress.getZip().substring(0, 1)));
			} catch (NumberFormatException e) {
				individual.setPayedPostPostalCode(new Integer(0));
				individual.setPayedPostProvince(new Integer(0));
			}
		}
		IAddress companyAddress = obtainRegistryAddress(company.getId());
		if (companyAddress != null) {
			individual.setPaymentPost(companyAddress.getCity());
			try {
				individual.setProvinceNumber(new Integer(companyAddress.getZip().substring(0, 1)));
			} catch (NumberFormatException e) {
				individual.setProvinceNumber(new Integer(0));
			}
		}
		return individual;
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
