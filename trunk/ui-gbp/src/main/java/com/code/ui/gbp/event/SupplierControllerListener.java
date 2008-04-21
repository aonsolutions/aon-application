package com.code.ui.gbp.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.gbp.Supplier;
import com.code.gbp.dao.IGBPAlias;
import com.code.ui.gbp.controller.SupplierController;

public class SupplierControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SupplierController supplierController = (SupplierController)event.getController();
		String bankAccount = ((Supplier)supplierController.getTo()).getBankAccount();
		try{
			supplierController.setEntity(bankAccount.substring(0, 4));
			supplierController.setOffice(bankAccount.substring(4, 8));
			supplierController.setControl(bankAccount.substring(8, 10));
			supplierController.setAccount(bankAccount.substring(10, 20));
		}catch (Exception e) {
			supplierController.setEntity("");
			supplierController.setOffice("");
			supplierController.setControl("");
			supplierController.setAccount("");
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SupplierController supplierController = (SupplierController)event.getController();
		supplierController.setEntity("");
		supplierController.setOffice("");
		supplierController.setControl("");
		supplierController.setAccount("");
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.SUPPLIER_NAME));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}
