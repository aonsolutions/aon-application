package com.code.aon.ui.employee.controller;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.employee.Contract;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.SalaryCalculatorContext;

public class SalaryDraftController extends BasicController {

	private Date issueDate;
	private Date startDate;
	private Date endDate;
	private ISalary salary;

	public Date getIssueDate() {
		if (issueDate == null) {
			issueDate = new Date();
		}
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		setStartDate(CommonUtil.getMonthFirstDay(issueDate));
		setEndDate(CommonUtil.getMonthLastDay(issueDate));
	}

	public Date getStartDate() {
		if (startDate == null) {
			startDate = CommonUtil.getMonthFirstDay(getIssueDate());
		}
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		if (endDate == null) {
			endDate = CommonUtil.getMonthLastDay(getIssueDate());
		}
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isShowBackground() {
		return true;
	}
	
	public ISalary getSalary() {
		try {
			if (salary == null) {
				Contract contract = (Contract) getTo();
				SalaryCalculatorContext ctx = contract.getSalaryCalculatorContext();
				ctx.setIssueDate(getIssueDate());
				ctx.setStartDate(getStartDate());
				ctx.setEndDate(getEndDate());
				salary = contract.getSalary();
			}
			return salary;
		} catch (SalaryException e) {
			throw new AbortProcessingException("Imposible mostrar el borrador de la nómina");
		}
	}

	public void setSalary(ISalary salary) {
		this.salary = salary;
	}

	@Override
	public Collection<ITransferObject> getCollection() {
		List<ITransferObject> l = new LinkedList<ITransferObject>();
		l.add((ITransferObject) getSalary());
		return l;
	}
	@Override
	public Collection<ITransferObject> getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
	public void onChangeDates(ActionEvent event) {
		try {
			setSalary(null);
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireAfterBeanSelected(evt);
		} catch (ControllerListenerException e) {
			throw new AbortProcessingException("Imposible mostrar la simulación de la nómina");
		}
	}
}
