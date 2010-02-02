package com.code.aon.ui.sales.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.sales.PointOfSale;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CompanyPOSListener extends ControllerAdapter {
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			CompanyController companyController = (CompanyController)event.getController();
			IManagerBean posBean = BeanManager.getManagerBean(PointOfSale.class);
			PointOfSale pos = new PointOfSale();
			pos.setRAddress(companyController.getMainAddress());
			pos.setDescription("Principal");
			posBean.insert(pos);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error adding point of sale", e);
		}
	}
}