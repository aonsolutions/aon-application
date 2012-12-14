package com.code.aon.ui.customer.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.finance.InvoicingGroup;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.stat.controller.RegistryStatEngineController;
import com.code.aon.ui.util.AonUtil;

public class CustomerController extends RegistryController implements ICustomerConstants {
    /** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.ui.registry.i18n.messages";
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_customer_report";

    private InvoicingGroup invoicingGroup;

    private boolean showAlumnData;
    
    private boolean showAlumnUpdateConfirmWindow;

    private Integer courseAlumnCount;

    public InvoicingGroup getInvoicingGroup() {
    	return invoicingGroup;
    }

    public void setInvoicingGroup(InvoicingGroup invoicingGroup) {
    	this.invoicingGroup = invoicingGroup;
    }
    
	public boolean isShowAlumnData() {
		return showAlumnData;
	}

	public void setShowAlumnData(boolean showAlumnData) {
		this.showAlumnData = showAlumnData;
	}
	
	public boolean isShowAlumnUpdateConfirmWindow() {
		return showAlumnUpdateConfirmWindow;
	}

	public void setShowAlumnUpdateConfirmWindow(boolean showAlumnUpdateConfirmWindow) {
		this.showAlumnUpdateConfirmWindow = showAlumnUpdateConfirmWindow;
	}

	public Integer getCourseAlumnCount() {
		return courseAlumnCount;
	}

	public void setCourseAlumnCount(Integer courseAlumnCount) {
		this.courseAlumnCount = courseAlumnCount;
	}

	public String getReportTitle(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX);
	}

	public void onCustomerHistory(ActionEvent e){
		RegistryStatEngineController controller =(RegistryStatEngineController)AonUtil.getRegisteredBean("registryStat");
		controller.setRegistry(((Customer)this.getTo()).getRegistry());
		controller.getRegistryData();
	}

	public void onLoadInvoicingGroup(ActionEvent event) throws ManagerBeanException {
		if (getInvoicingGroup() != null && getInvoicingGroup().getId() != null) {
			BasicController customerController = (BasicController)AonUtil.getRegisteredBean(INVOICING_GROUP_CONTROLLER_NAME);
			customerController.onLoad(event, getInvoicingGroup().getId(), CUSTOMER_FORM_NAME, CUSTOMER_CONTROLLER_NAME + ".select");
		}
	}

	@Override
	public void accept(ActionEvent event) {
		Customer customer = (Customer) getTo();
		if(isShowAlumnData() && customer.getStatus()==CustomerStatus.INACTIVE && getCourseAlumnCount()>0 ){
			setShowAlumnUpdateConfirmWindow(true);
		} else {
			super.accept(event);
		}
	}
	public void acceptOnly(ActionEvent event) {
		setCourseAlumnCount(0);
		super.accept(event);
	}
	public void acceptAndUpdate(ActionEvent event) {
		super.accept(event);
	}
	

}