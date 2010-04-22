package com.code.aon.ui.accounting.controller;

import com.code.aon.accounting.AmortizationType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

public class AmortizationTypeImporterManager {
	
	private IManagerBean bean;
	
	private IManagerBean getManagerBean() throws ManagerBeanException {
		if (bean == null) {
			bean = BeanManager.getManagerBean(AmortizationType.class);
		}
		return bean;
	}
	
	public void addAmortizationType(AmortizationType amortizationType) throws ManagerBeanException {
		if (amortizationType != null) {
			AmortizationType a = (AmortizationType) getManagerBean().get(amortizationType.getId());
			if (a == null) {
				bean.insert(amortizationType);
			} else {
				a.setDescription(amortizationType.getDescription());
				a.setFixedAssetAccount(amortizationType.getFixedAssetAccount());
				a.setAccumulatedAccount(amortizationType.getAccumulatedAccount());
				a.setAllocationAccount(amortizationType.getAllocationAccount());
				a.setPercentage(amortizationType.getPercentage());
				
				bean.update(a);
			}
		}
	}
	
}
