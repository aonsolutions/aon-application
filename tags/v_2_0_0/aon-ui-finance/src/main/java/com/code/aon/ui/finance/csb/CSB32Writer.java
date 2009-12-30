package com.code.aon.ui.finance.csb;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.csb.fd0.model.FileFiller;
import com.code.aon.csb.fd0.model.CSB32.CSB32;
import com.code.aon.csb.fd0.model.CSB32.data.Account;
import com.code.aon.csb.fd0.model.CSB32.data.Delivery;
import com.code.aon.csb.fd0.model.CSB32.data.Individual;
import com.code.aon.csb.fd0.model.CSB32.data.Lot;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.RegistryBank;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.finance.controller.FBatchDetailController;
import com.code.aon.ui.util.AonUtil;

public class CSB32Writer {

	private static final String FINANCE_BATCH_DETAIL_CONTROLLER_NAME = "fBatchDetail";

	public File createCSB32(Company company, FinanceBatch fbatch) throws ManagerBeanException {
		try {
			Lot lot = new Lot();
			RegistryBank companyRBank = fbatch.getRegistryBank();
			lot.setEntity(new Integer(companyRBank.getBankAccount().substring(0, 3)));
			lot.setOffice(new Integer(companyRBank.getBankAccount().substring(4, 7)));
			lot.setFileDate(new Date());
			lot.setFileNumber(new Integer(1));
			
			Delivery delivery = new Delivery();
			delivery.setDeliveyNumber(fbatch.getId());
			delivery.setGiverCode(company.getDocument());
			Account ccc1 = new Account();
			ccc1.parse(companyRBank.getBankAccount());
			delivery.setNotPayedAccount(ccc1);
			Account ccc2 = new Account();
			ccc2.parse(companyRBank.getBankAccount());
			delivery.setOweAccount(ccc2);
			Account ccc3 = new Account();
			ccc3.parse(companyRBank.getBankAccount());
			delivery.setPaymentAccount(ccc3);
			delivery.setTruncatedEffects(new Integer(1));
	
			FBatchDetailController fBatchDetailController = (FBatchDetailController)AonUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
			Iterator iter = ((List)fBatchDetailController.getModel().getWrappedData()).iterator();
			while(iter.hasNext()){
				FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)iter.next();
				Individual individual = createIndividual(company, fBatchDetail);
				delivery.addIndividual(individual);
			}
			
			lot.addDelivery(delivery);
			
			File file = File.createTempFile("CSB32_", ".txt");
			FileFiller csb32 = new CSB32(lot, file.getAbsolutePath());
			csb32.create();
			return file;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private Individual createIndividual(Company company, FinanceBatchDetail fBatchDetail) throws ManagerBeanException {
		Individual individual = new Individual();
		Account ccc = new Account();
		ccc.parse(fBatchDetail.getFinance().getBankAccount());
		individual.setAccount(ccc);
		individual.setAceptedCode(new Integer(2));
		individual.setAditionalData(fBatchDetail.getFinance().getId().toString());
		individual.setAmount(new Double(fBatchDetail.getFinance().getTotalAmount()));
		individual.setDocumentNumber(fBatchDetail.getFinance().getInvoice().getSeries() + "/" + fBatchDetail.getFinance().getInvoice().getNumber());
		individual.setDocumentType(new Integer(2)); // RECIBO
		individual.setEfectPayed(fBatchDetail.getFinance().getRegistry().getName() + " " + fBatchDetail.getFinance().getRegistry().getSurname());
		individual.setEfectPayer(company.getName());
		individual.setExpenseClause(new Integer(0));
		individual.setExpiryDate(fBatchDetail.getFinanceBatch().getIssueDate());
		individual.setPayedDocument(fBatchDetail.getFinance().getInvoice().getRegistryDocument());
		RegistryAddress customerAddress = obtainRegistryAddress(fBatchDetail.getFinance().getRegistry().getId());
		if(customerAddress != null){
			individual.setPayedPost(customerAddress.getCity());
			individual.setPayedPostPostalCode(new Integer(customerAddress.getZip()));
			individual.setPayedPostProvince(new Integer(customerAddress.getZip().substring(0, 1)));
			individual.setPayedAddress(customerAddress.getAddress() + customerAddress.getAddress2());
		}
		individual.setPaymentDate(fBatchDetail.getFinance().getDueDate());
		RegistryAddress companyAddress = obtainRegistryAddress(company.getId());
		if(companyAddress != null){
			individual.setPaymentPost(companyAddress.getCity());
			individual.setProvinceNumber(new Integer(companyAddress.getZip().substring(0, 1)));
		}
		return individual;
	}

	private RegistryAddress obtainRegistryAddress(Integer id) throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
		Iterator iter = rAddressBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAddress)iter.next();
		}
		return null;
	}
}
