package com.code.aon.ui.registry.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.LinesController;

public class RegistryPayMethodController extends LinesController {

	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPay = (PayMethod) event.getOldValue();
		PayMethod newPay = (PayMethod) event.getNewValue();
		if (oldPay == null || newPay == null || oldPay.getType() != newPay.getType()) {
			RegistryPayMethod rpm = (RegistryPayMethod) getTo();
			rpm.setRegistryBank(new RegistryBank());
		}
	}

	protected boolean isBankTransfer() {
		RegistryPayMethod rpm = (RegistryPayMethod) getTo();
		PayMethod payMethod = rpm.getPayment();
		return (payMethod != null && payMethod.getType() == PayMethodType.BANK_TRANSFER);
	}
	
	protected List<SelectItem> getBanks(Integer id) throws ManagerBeanException {
		LinkedList<SelectItem> rBanks = new LinkedList<SelectItem>();
		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), id);
		Iterator<?> iter = rBankBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			RegistryBank rBank = (RegistryBank) iter.next();
			SelectItem item = new SelectItem(rBank, rBank.getFullName());
			rBanks.add(item);
		}
		return rBanks;
	}
	
}