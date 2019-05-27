package com.code.aon.ui.customer.controller;

import static com.code.aon.ui.common.ICommonMessages.CUSTOMER_REPORT;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryNote;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.RegistryObservationController;
import com.code.aon.ui.stat.controller.RegistryStatEngineController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerController extends CustomerListController implements ICustomerConstants, IAuditableController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory
			.getLogger(CustomerController.class);
	
    private boolean showAlumnData;
    private boolean showAlumnUpdateConfirmWindow;
    private Integer courseAlumnCount;
	private boolean updateCourseAlumn;
	private boolean showAuditInfoWindow;
	
	public boolean isUpdateCourseAlumn() {
		return updateCourseAlumn;
	}
	
	public void setUpdateCourseAlumn(boolean updateCourseAlumn) {
		this.updateCourseAlumn = updateCourseAlumn;
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

	public boolean isAccountSynchronizable() {
		return isAccountSynchronizable((Customer)getTo());
	}

	protected boolean isAccountSynchronizable(Customer customer) {
		Account account = customer.getAccount();
		return account != null 
			&& account.getId() != null 
			&& account.getDomain() == customer.getDomain()
			&& !customer.getRegistry().getFullName().equals(account.getDescription());
	}

	public boolean isEdiSupportEnabled() {
		return StringUtils.isNotBlank(AppParamUtil.getValue(AppParam.EDI_SUPPORT));
	}
	
	public String getRowCustomerComments(){
		RegistryObservationController controller = (RegistryObservationController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_OBSERVATION_CONTROLLER_NAME);
		try {
			if(this.getModel().isRowAvailable()){
				Customer customer = (Customer) this.getModel().getRowData();
				RegistryNote rObservation = controller.getRegistryObservation(customer.getRegistry());
				if(rObservation!=null){
					return rObservation.getComments();
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
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

	public boolean isInvoicingGroupInMyScopes() {
		Customer customer = (Customer)getTo();
		return UserUtils.getInstance().getCurrentUserScopes().contains(customer.getInvoicingGroup().getCustomer().getScope());
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
    	return AonUtil.getMessage(CUSTOMER_REPORT);
	}
    
    public void onAlumnEditSearch(ActionEvent event){
    	super.onEditSearch(event);
    	setShowAlumnData(true);
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
	
	public void eInvoiceChange(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue()!=null && (boolean) event.getNewValue()){
			setSelectedTab(CUSTOMER_EINVOICE_TAB);
		} else {
			setSelectedTab(null);
		}
	}
	
	public void ediChange(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue()!=null && (boolean) event.getNewValue()){
			setSelectedTab(CUSTOMER_EDI_TAB);
		} else {
			setSelectedTab(null);
		}
	}

	@Override
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	@Override
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
	public boolean isTarget() throws ManagerBeanException {
		return obtainTargetId() > 0;
	}
	
	public void onLoadTarget(ActionEvent event) throws ManagerBeanException {
		Integer targetId = obtainTargetId();
		BasicController targetController = (BasicController)AonUtil.getRegisteredBean(TARGET_CONTROLLER_NAME);
		targetController.onLoad(event, targetId, CUSTOMER_FORM_NAME, CUSTOMER_CONTROLLER_NAME + ".refresh");	
	}
	
	private int obtainTargetId() throws ManagerBeanException {
		Integer customerId = ((Customer)getTo()).getId();
		IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(targetBean.getFieldName(IEntityAlias.TARGET_REGISTRY_ID), customerId);
		List<ITransferObject> list = targetBean.getList(criteria);
		if(list.size()>0)
			return ((Target)list.get(0)).getId();
		return -1;
	}
	
}