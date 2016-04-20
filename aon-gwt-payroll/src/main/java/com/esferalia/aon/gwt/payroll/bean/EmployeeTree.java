package com.esferalia.aon.gwt.payroll.bean;

import java.io.Serializable;
import java.util.Random;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.PayrollWorkPlaceController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;

public class EmployeeTree implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static enum Selection {
		EMPLOYEE, ACTIVITY, WORKPLACE, ENTERPRISE, PAYMENT_CONCEPTS, DEDUCTION_CONCEPTS, BONUS_CONCEPTS
	}
	

	private Selection selection;

	private Integer enterpriseId;
	private Integer workplaceId;
	private Integer employeeId;
	private Integer activityId;
	
	
	
	public Integer getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getWorkplaceId() {
		return workplaceId;
	}

	public void setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
	}

	public boolean isEmployeeSelected() {
		return selection == Selection.EMPLOYEE;
	}

	public void setEmployeeSelected(boolean employeeSelected) {
		selection = employeeSelected ? Selection.EMPLOYEE : null;
	}

	public boolean isWorkplaceSelected() {
		return selection == Selection.WORKPLACE;
	}

	public void setWorkplaceSelected(boolean workPlaceSelected) {
		selection = workPlaceSelected ? Selection.WORKPLACE : null;
	}

	public boolean isEnterpriseSelected() {
		return selection == Selection.ENTERPRISE;
	}

	public void setEnterpriseSelected(boolean enterpriseSelected) {
		selection = enterpriseSelected ? Selection.ENTERPRISE : null;
	}

	public boolean isActivitySelected() {
		return selection == Selection.ACTIVITY;
	}

	public void setActivitySelected(boolean activitySelected) {
		selection = activitySelected ? Selection.ACTIVITY : null;
	}

	public boolean isBonusConceptsSelected() {
		return selection == Selection.BONUS_CONCEPTS;
	}

	public void setBonusConceptsSelected(boolean bonusConceptsSelected) {
		selection = bonusConceptsSelected ? Selection.BONUS_CONCEPTS: null;
	}

	public boolean isPaymentConceptsSelected() {
		return selection == Selection.PAYMENT_CONCEPTS;
	}

	public void setPaymentConceptsSelected(boolean paymentConceptsSelected) {
		selection = paymentConceptsSelected ? Selection.PAYMENT_CONCEPTS : null;
	}

	public boolean isDeductionConceptsSelected() {
		return selection == Selection.DEDUCTION_CONCEPTS;
	}

	public void setDeductionConceptsSelected(boolean deductionConceptsSelected) {
		selection = deductionConceptsSelected ? Selection.DEDUCTION_CONCEPTS: null;
	}

	public void onEmployeeSelected(ActionEvent event)
			throws ManagerBeanException {

		BasicController contractController = (BasicController) AonUtil
				.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER);
		contractController.load(event, employeeId);

		Integer personId = ((Contract) contractController.getTo()).getPerson()
				.getId();
		BasicController personController = (BasicController) AonUtil
				.getRegisteredBean(IPayrollConstants.PERSON_CONTROLLER_NAME);
		
		personController.load(event, personId);
		

		this.selection = Selection.EMPLOYEE;
	}

	public void onActivitySelected(ActionEvent event)
			throws ManagerBeanException {

		BasicController controller = (BasicController) AonUtil
				.getRegisteredBean(IPayrollConstants.ENTERPRISE_ACTIVITY_CONTROLLER);
		controller.load(event, activityId);

		this.selection = Selection.ACTIVITY;
	}

	public void onWorkplaceSelected(ActionEvent event)
			throws ManagerBeanException {

		BasicController controller = (BasicController) AonUtil
				.getRegisteredBean(IPayrollConstants.PAYROLL_WORK_PLACE_CONTROLLER);
		controller.load(event, workplaceId);

		this.selection = Selection.WORKPLACE;

	}

	public void onEnterpriseSelected(ActionEvent event)
			throws ManagerBeanException {
		BasicController controller = (BasicController) AonUtil
				.getRegisteredBean(IPayrollConstants.ENTERPRISE_CONTROLLER);
		controller.load(event, enterpriseId);

		this.selection = Selection.ENTERPRISE;
	}

	public void onShowNewContractModal(ActionEvent event) {
		EnterpriseController enterpriseController = (EnterpriseController) AonUtil
				.getRegisteredBean(IPayrollConstants.ENTERPRISE_CONTROLLER);
		ContractController contractController = (ContractController) AonUtil
				.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER);

		contractController.onReset(event);
		contractController.setEnterprise((Enterprise) enterpriseController.getTo());
		contractController.onShowNewContractModal(event);

	}

	public void onShowNewWorkplaceModal(ActionEvent event) {
		PayrollWorkPlaceController payrollWorkPlaceController = (PayrollWorkPlaceController) AonUtil
				.getRegisteredBean(IPayrollConstants.PAYROLL_WORK_PLACE_CONTROLLER);
		payrollWorkPlaceController.onReset(event);
	}

	public void onShowNewActivityModal(ActionEvent event) {

	}

	public void onPaymentConceptsSelected(ActionEvent event)
			throws ManagerBeanException {

		this.selection = Selection.PAYMENT_CONCEPTS;
	}

	public void onDeductionConceptsSelected(ActionEvent event)
			throws ManagerBeanException {

		this.selection = Selection.DEDUCTION_CONCEPTS;
	}

	public void onBonusConceptsSelected(ActionEvent event)
			throws ManagerBeanException {

		this.selection = Selection.BONUS_CONCEPTS;
	}
	
	
	
	private static final Random RANDOM = new Random();
	// I use this at 'gwt.xhtml'. This way I can force load of GWT javascript each time.
	// src="...aon_gwt_payroll.nocache.js?...&amp;horribleFix=#{employeeTree.nextInt}"
	public int getNextInt() {
        return RANDOM.nextInt();
    }
	
}
