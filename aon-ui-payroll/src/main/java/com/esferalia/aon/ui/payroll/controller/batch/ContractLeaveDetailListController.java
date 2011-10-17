package com.esferalia.aon.ui.payroll.controller.batch;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.LeaveBatch;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractLeaveStatus;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class ContractLeaveDetailListController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ContractLeaveDetailListController.class);

	private LeaveReportType[] reportTypes = { LeaveReportType.LEAVE,LeaveReportType.CONFIRM, LeaveReportType.DISCHARGE };
	private Enterprise enterprise;
	private Person person;
	private boolean showLeaveSearchWindow;
	private boolean showLeaveFractionWindow;
	private ArrayList<ContractLeaveDetail> checks = new ArrayList<ContractLeaveDetail>();
	
	public LeaveReportType[] getReportTypes() {
		return reportTypes;
	}
	
	public void setReportTypes(LeaveReportType[] reportTypes) {
		this.reportTypes = reportTypes;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public boolean isShowLeaveSearchWindow() {
		return showLeaveSearchWindow;
	}

	public void setShowLeaveSearchWindow(boolean value) {
		if(value){
			try {
				IManagerBean bean = BeanManager.getManagerBean(Person.class);
				setPerson((Person) bean.createNewTo());
				bean = BeanManager.getManagerBean(Enterprise.class);
				setEnterprise((Enterprise) bean.createNewTo());
			} catch (ManagerBeanException e) {
				LOGGER.error(">>>> error on setShowLeaveSearchWindow: ",e);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}
		this.showLeaveSearchWindow = value;
	}

	public boolean isShowLeaveFractionWindow() {
		return showLeaveFractionWindow;
	}

	public void setShowLeaveFractionWindow(boolean value) {
		this.showLeaveFractionWindow = value;
	}

	public void onSearch(ActionEvent event) {
		clearCheckedLeaves();
		try {
			LeaveBatchController controller = (LeaveBatchController) FormUtil.getController(IPayrollConstants.LEAVE_BATCH_CONTROLLER_NAME);
			LeaveBatch batch = (LeaveBatch) controller.getTo();
			getCriteria().addLessThanOrEqualExpression(getFieldName(IPayrollAlias.CONTRACT_LEAVE_DETAIL_DATE), batch.getDate());
			getCriteria().addNotEqualExpression(getFieldName(IPayrollAlias.CONTRACT_LEAVE_DETAIL_STATUS),ContractLeaveStatus.BATCHED);
			if ((getPerson() != null) && (getPerson().getId() != null)) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_CONTRACT_PERSON_ID), getPerson().getId());			
			}
			if ((getEnterprise() != null) && (getEnterprise().getId() != null)) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());			
			}
			
			Expression expr = null;
			for(LeaveReportType t: getReportTypes()){
				if(expr == null){
					expr = ExpressionUtilities.getEqualExpression(getFieldName(IPayrollAlias.CONTRACT_LEAVE_DETAIL_TYPE), t); 
				} else {
					expr = ExpressionUtilities.getOrExpression(expr, ExpressionUtilities.getEqualExpression(getFieldName(IPayrollAlias.CONTRACT_LEAVE_DETAIL_TYPE), t));
				}
			}
			if(expr!=null){
				getCriteria().addExpression(expr);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		super.onSearch(event);
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getRowChecked() {
		ContractLeaveDetail to = (ContractLeaveDetail) model.getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			ContractLeaveDetail to = (ContractLeaveDetail) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			ContractLeaveDetail to = (ContractLeaveDetail) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public ArrayList<ContractLeaveDetail> getCheckedLeaves() {
		return checks;
	}

	public void clearCheckedLeaves() {
		checks = new ArrayList<ContractLeaveDetail>();
	}

	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		Iterator iterator = this.getManagerBean().getList(this.getCriteria())
				.iterator();
		while (iterator.hasNext()) {
			ContractLeaveDetail finance = (ContractLeaveDetail) iterator.next();
			if (!checks.contains(finance)) {
				checks.add(finance);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedLeaves();
	}

}
