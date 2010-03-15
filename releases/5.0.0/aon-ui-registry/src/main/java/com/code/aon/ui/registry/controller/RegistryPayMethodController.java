package com.code.aon.ui.registry.controller;

import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class RegistryPayMethodController extends LinesController {

	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPay = (PayMethod) event.getOldValue();
		PayMethod newPay = (PayMethod) event.getNewValue();
		if (oldPay == null || newPay == null || oldPay.getType() != newPay.getType()) {
			RegistryPayMethod rpm = (RegistryPayMethod) getTo();
			rpm.setRegistryBank(new RegistryBank());
		}
	}

	protected boolean isNegotiableDocument() {
		RegistryPayMethod rpm = (RegistryPayMethod) getTo();
		PayMethod payMethod = rpm.getPayment();
		return (payMethod != null && payMethod.getType() == PayMethodType.NEGOTIABLE_DOCUMENT);
	}
	
	protected boolean isBankTransfer() {
		RegistryPayMethod rpm = (RegistryPayMethod) getTo();
		PayMethod payMethod = rpm.getPayment();
		return (payMethod != null && payMethod.getType() == PayMethodType.BANK_TRANSFER);
	}
	
	protected List<SelectItem> getBanks(Registry registry) throws ManagerBeanException {
		RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getRegistryBanks(registry);
	}
	
}