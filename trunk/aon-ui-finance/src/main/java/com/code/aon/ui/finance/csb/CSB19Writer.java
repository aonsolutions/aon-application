package com.code.aon.ui.finance.csb;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.csb.fd0.model.FileFiller;
import com.code.aon.csb.fd0.model.CSB19.CSB19;
import com.code.aon.csb.fd0.model.CSB19.data.Account;
import com.code.aon.csb.fd0.model.CSB19.data.Individual;
import com.code.aon.csb.fd0.model.CSB19.data.Lot;
import com.code.aon.csb.fd0.model.CSB19.data.Orderer;
import com.code.aon.csb.fd0.model.CSB19.data.Presenter;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.finance.controller.FBatchDetailController;
import com.code.aon.ui.util.AonUtil;

public class CSB19Writer {
	
	private static final String FINANCE_BATCH_DETAIL_CONTROLLER_NAME = "fBatchDetail";

	public File createCSB19(Company company, FinanceBatch fbatch) throws ManagerBeanException {
		Lot lot = new Lot();
		try {
		if(fbatch.getFinanceBatchType().equals(FinanceBatchType.CSB_19_D)){
			lot.setType(Lot.EXTENDED);
		}
		if(fbatch.getFinanceBatchType().equals(FinanceBatchType.CSB_19)){
			lot.setType(Lot.RESUMED);
		}
		RegistryBank companyRBank = fbatch.getRegistryBank();
		Presenter presenter = new Presenter();
		presenter.setCode(company.getDocument());
		presenter.setSufix(companyRBank.getSufix());
		presenter.setMakeDate(fbatch.getIssueDate());
		presenter.setName(company.getName());
		presenter.setEntity(companyRBank.getBankAccount().substring(0,4));
		presenter.setOffice(companyRBank.getBankAccount().substring(4,8));
		
		lot.setPresenter(presenter);
		
		Orderer orderer = new Orderer();
		Account companyAccount = new Account();
		companyAccount.parse(companyRBank.getBankAccount());
		orderer.setAccount(companyAccount);
		orderer.setCode(company.getDocument());
        orderer.setName(company.getName());
		orderer.setMakeDate(fbatch.getIssueDate());
		orderer.setProcedure(new Integer(1));
		orderer.setStartDate(fbatch.getIssueDate());
		orderer.setSufix(companyRBank.getSufix());
		FBatchDetailController fBatchDetailController = (FBatchDetailController)AonUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator iter = ((List)fBatchDetailController.getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)iter.next();
			Individual individual  = createIndividual(fBatchDetail);
			orderer.addIndividual(individual);
		}
		
		lot.addOrderer(orderer);
		
		File file = File.createTempFile("CSB19_", ".txt");
		FileFiller csb19 = new CSB19(lot, file.getAbsolutePath());
		csb19.create();
		return file;
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error creating CSB", e);
		} catch (IOException e) {
			throw new ManagerBeanException("Error creating CSB", e);
		}
	}
	
	private Individual createIndividual(FinanceBatchDetail fBatchDetail) throws ManagerBeanException {
		Individual individual = new Individual();
		individual.setAmount(new Double(fBatchDetail.getFinance().getAmount()));
		Account detailAccount = new Account();
		detailAccount.parse(fBatchDetail.getFinance().getBankAccount());
		individual.setAccount(detailAccount);
		individual.setConcept(fBatchDetail.getFinance().getConcept());
		individual.setInternalCode(fBatchDetail.getFinance().getInvoice().getSeries() + "/" + fBatchDetail.getFinance().getInvoice().getNumber());
		individual.setName(fBatchDetail.getFinance().getRegistry().getName() + " " + fBatchDetail.getFinance().getRegistry().getSurname());
		individual.setReferenceCode(fBatchDetail.getFinance().getRegistry().getId().toString());
		individual.setReturnCode(fBatchDetail.getFinance().getRegistry().getId().toString());
		RegistryAddress detailAddress = obtainRegistryAddress(fBatchDetail.getFinance().getRegistry().getId());
		individual.setAccountUserName(fBatchDetail.getFinance().getRegistry().getName() + " " + fBatchDetail.getFinance().getRegistry().getSurname());
		if(detailAddress != null){
			individual.setAccountUserAddress(detailAddress.getAddress() + " " + detailAddress.getAddress2() + " " + detailAddress.getAddress3());
			individual.setAccountUserAddress2(detailAddress.getCity());
            individual.setAccountUserPCode(new Integer(detailAddress.getZip()));
		}
		return individual;
	}
	
	private RegistryAddress obtainRegistryAddress(Integer registryId) throws ManagerBeanException {
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), registryId);
		Iterator iter = rAddressBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAddress)iter.next();
		}
		return null;
	}
}
