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
import com.code.aon.csb.fd0.model.CSB58.CSB58;
import com.code.aon.csb.fd0.model.CSB58.data.Individual;
import com.code.aon.csb.fd0.model.CSB58.data.Lot;
import com.code.aon.csb.fd0.model.CSB58.data.Orderer;
import com.code.aon.csb.fd0.model.CSB58.data.Presenter;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.csb.CSBOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.finance.controller.FBatchDetailController;
import com.code.aon.ui.form.FormUtil;

public class CSB58Writer {

	private static final String FINANCE_BATCH_DETAIL_CONTROLLER_NAME = "fBatchDetail";

	@SuppressWarnings("unchecked")
	public CSBOutput createCSB58(Company company, FinanceBatch fbatch) throws ManagerBeanException {
		FBatchDetailController fBatchDetailController = (FBatchDetailController)FormUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
		return createCSB58(company, fbatch, (List)fBatchDetailController.getModel().getWrappedData());
	}
	
	@SuppressWarnings("unchecked")
	public CSBOutput createCSB58(Company company, FinanceBatch fbatch, Collection fbatchDetailCollection) throws ManagerBeanException {
		Lot lot = new Lot();
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
			Individual individual  = createIndividual(fBatchDetail);
			orderer.addIndividual(individual);
		}
		lot.addOrderer(orderer);

		try {
			File file = File.createTempFile("CSB58_", ".txt");
			FileFiller csb58 = new CSB58(lot, file.getAbsolutePath());
			CSBOutput output = new CSBOutput();
			output.setFile(file);
			output.setErrors(csb58.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private Individual createIndividual(FinanceBatchDetail fBatchDetail) throws ManagerBeanException {
		Individual individual = new Individual();
		individual.setAmount(new Double(fBatchDetail.getFinance().getTotalAmount()));
		Account ccc = new Account();
		if (fBatchDetail.getFinance().getBankAccount() != null && !fBatchDetail.getFinance().getBankAccount().equals("")) {
            ccc.parse(fBatchDetail.getFinance().getBankAccount().getValue());
            individual.setAccount(ccc);
        }
		individual.setConcept("FRA:" + fBatchDetail.getFinance().getInvoice().getSeries() + "/" + fBatchDetail.getFinance().getInvoice().getNumber());
		individual.setInternalCode(fBatchDetail.getFinance().getInvoice().getSeries() + "/" + fBatchDetail.getFinance().getInvoice().getNumber());
		individual.setName(fBatchDetail.getFinance().getInvoice().getRegistryName());
		individual.setReferenceCode(fBatchDetail.getFinance().getInvoice().getRegistryDocument()); 
		individual.setReturnCode(fBatchDetail.getFinance().getId().toString());
		individual.setExpiryDate(fBatchDetail.getFinance().getDueDate());
		RegistryAddress customerAddress = obtainRegistryAddress(fBatchDetail.getFinance().getRegistry().getId());
		if(customerAddress != null){
			individual.setAccountUserAddress(customerAddress.getAddress());
			individual.setAccountUserAddress2(customerAddress.getAddress2());
			try {
				individual.setAccountUserPCode(new Integer(customerAddress.getZip()));
			} catch (NumberFormatException e) {
				individual.setAccountUserPCode(new Integer(0));
			}
			individual.setOrdererCounty(customerAddress.getCity());
		}
		individual.setInitDate(new Date());
		return individual;
	}
	
	@SuppressWarnings("unchecked")
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