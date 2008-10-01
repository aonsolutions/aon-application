package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class PurchaseFinanceController extends LinesController {

	private static final Logger LOGGER = Logger.getLogger(PurchaseFinanceController.class.getName());

	private Integer registryBankId;

	public Integer getRegistryBankId() {
		return registryBankId;
	}

	public void setRegistryBankId(Integer registryBankId) {
		this.registryBankId = registryBankId;
	}
	
	public List getCompanyBanks(){
		List<SelectItem> rBanks = new LinkedList<SelectItem>();
		Company company = obtainCompany();
		if(company != null){
			try {
				IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_REGISTRY_ID), company.getId());
				Iterator iter = rBankBean.getList(criteria).iterator();
				while(iter.hasNext()){
					RegistryBank rBank = (RegistryBank)iter.next();
					SelectItem item = new SelectItem(rBank.getId(), rBank.getBank().getName());
					rBanks.add(item);
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error obtaining Banks for the company", e);
			}
			
		}
		return rBanks;
	}

	private Company obtainCompany() {
		try {
			IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
			Iterator iter = companyBean.getList(null).iterator();
			if(iter.hasNext()){
				return (Company)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error the company", e);
		}
		return null;
	}
}