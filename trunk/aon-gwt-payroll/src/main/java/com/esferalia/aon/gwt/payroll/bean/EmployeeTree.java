package com.esferalia.aon.gwt.payroll.bean;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;

public class EmployeeTree {

	private static enum Selection {
		EMPLOYEE, ACTIVITY, WORKPLACE, ENTERPRISE
	}

	private Selection selection;

	private Integer workplaceId;
	private Integer employeeId;
	private Integer activityId;

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

	public void onEnterpriseSelected(ActionEvent event) {
		this.selection = Selection.ENTERPRISE;
	}

	public void onShowNewContractModal(ActionEvent event) {
		EnterpriseController ec = (EnterpriseController) AonUtil
				.getRegisteredBean(IPayrollConstants.ENTERPRISE_CONTROLLER);
		ContractController controller = (ContractController) AonUtil
				.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER);
		controller.onReset(event);
		controller.setEnterprise((Enterprise) ec.getTo());
		controller.onShowNewContractModal(event);

	}

}
