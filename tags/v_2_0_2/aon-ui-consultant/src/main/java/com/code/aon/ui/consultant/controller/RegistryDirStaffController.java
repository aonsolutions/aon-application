package com.code.aon.ui.consultant.controller;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.consultant.RegistryDirStaff;
import com.code.aon.consultant.dao.IConsultantAlias;
import com.code.aon.customer.Customer;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class RegistryDirStaffController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(RegistryDirStaffController.class.getName());
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";

	@SuppressWarnings("unused")
	public void onRDirStaff(ActionEvent event){
		CustomerController customerController = (CustomerController)AonUtil.getController(CUSTOMER_CONTROLLER_NAME);
		Customer customer = (Customer)customerController.getTo();
		try {
			IManagerBean rDirStaffBean = BeanManager.getManagerBean(RegistryDirStaff.class);
			this.clearCriteria();
			getCriteria().addEqualExpression(rDirStaffBean.getFieldName(IConsultantAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), customer.getId());
			this.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading fiscal data related with customer with id= " + customer.getId(), e);
		}
	}
	
	public double getTotalPercentShare() throws ManagerBeanException{
		double total = 0.0;
		Iterator iter = ((List)getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			RegistryDirStaff rDirStaff = (RegistryDirStaff)iter.next();
			total += rDirStaff.getPercentShare();
		}
		return total;
	}
	
	public int getTotalShareNumber() throws ManagerBeanException{
		int total = 0;
		Iterator iter = ((List)getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			RegistryDirStaff rDirStaff = (RegistryDirStaff)iter.next();
			total += rDirStaff.getShareNumber();
		}
		return total;
	}
	
	public double getTotalNominalValue() throws ManagerBeanException{
		double total = 0.0;
		Iterator iter = ((List)getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			RegistryDirStaff rDirStaff = (RegistryDirStaff)iter.next();
			total += rDirStaff.getNominalValue();
		}
		return total;
	}
}
