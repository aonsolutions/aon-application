package com.code.aon.ui.customer.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.stat.controller.RegistryStatEngineController;
import com.code.aon.ui.util.AonUtil;

public class CustomerController extends RegistryController implements ICustomerConstants {

	private static final String BASE_NAME = "com.code.aon.ui.registry.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_customer_report";

    private boolean showAlumnUpdateConfirmWindow;
    private Integer courseAlumnCount;
	private boolean updateCourseAlumn;
	
	public boolean isUpdateCourseAlumn() {
		return updateCourseAlumn;
	}
	
	public void setUpdateCourseAlumn(boolean updateCourseAlumn) {
		this.updateCourseAlumn = updateCourseAlumn;
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

	public boolean isAccountSynchronizable() {
		return isAccountSynchronizable((Customer)getTo());
	}

	protected boolean isAccountSynchronizable(Customer customer) {
		Account account = customer.getAccount();
		return (account != null && account.getId() != null && !customer.getRegistry().getFullName().equals(account.getDescription()));
	}

	public void onAccountSynchronize(ActionEvent event) {
		onAccountSynchronize((Customer)getTo());
	}

	protected void onAccountSynchronize(Customer customer) {
		try {
			customer.getAccount().setDescription(customer.getRegistry().getFullName());
			customer.getAccount().setAlias(customer.getRegistry().getAlias());
			BeanManager.getManagerBean(Account.class).update(customer.getAccount());
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo sincronizar la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	public void onNewAccount(ActionEvent event) {
		onNewAccount((Customer)getTo());
	}

	protected void onNewAccount(Customer customer) {
		try {
			AccountBridgeUtil accountBridgeUtil = new AccountBridgeUtil();
			customer.setAccount(accountBridgeUtil.obtainNewCustomerAccount(customer));
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo crear la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}
	
	@Override
	public void accept(ActionEvent event) {
		setUpdateCourseAlumn(false);
		Customer customer = (Customer)getTo();
		if (customer.getStatus() == CustomerStatus.INACTIVE && getCourseAlumnCount() > 0) {
			setShowAlumnUpdateConfirmWindow(true);
		} else {
			super.accept(event);
		}
	}
	
	public void acceptOnly(ActionEvent event) {
		setUpdateCourseAlumn(false);
		super.accept(event);
	}

	public void acceptAndUpdate(ActionEvent event) {
		setUpdateCourseAlumn(true);
		super.accept(event);
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
		Customer customer = (Customer)getTo();
		BasicController invoicingGroupController = (BasicController)AonUtil.getRegisteredBean(INVOICING_GROUP_CONTROLLER_NAME);
		invoicingGroupController.onLoad(event, customer.getInvoicingGroup().getId(), CUSTOMER_FORM_NAME, CUSTOMER_CONTROLLER_NAME + ".select");
	}

}