package com.esferalia.aon.ui.payroll.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;

public class SalaryDraftController extends BasicController {

	private Month month;
	private int year;
	
	private Date issueDate;
	private Date startDate;
	private Date endDate;
	private ISalary salary;

	public Date getIssueDate() {
		if (issueDate == null) {
			setIssueDate( new Date());
		}
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		setStartDate(CommonUtil.getMonthFirstDay(issueDate));
		setEndDate(CommonUtil.getMonthLastDay(issueDate));
		setMonth(Month.getMonthByValue(CommonUtil.getMonth(issueDate)));
		setYear(CommonUtil.getYear(issueDate));
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

	public Month getMonth() {
		return month;
	}
	public void setMonth(Month month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

	public boolean isShowBackground() {
		return true;
	}
	
//	public ISalary getSalary() {
//		try {
//			if (salary == null) {
//				Contract contract = (Contract) getTo();
//				SalaryCalculatorContext ctx = contract.getSalaryCalculatorContext();
//				ctx.setIssueDate(getIssueDate());
//				ctx.setStartDate(getStartDate().before(contract.getStartDate())?contract.getStartDate():getStartDate());
//				ctx.setEndDate((contract.getEndDate() != null && getEndDate().after(contract.getEndDate()))?contract.getEndDate():getEndDate());
//				salary = contract.getSalary();
//			}
//			return salary;
//		} catch (SalaryException e) {
//			throw new AbortProcessingException("Imposible mostrar el borrador de la nómina");
//		}
//	}
	
	public ISalary getSalary() {
		try {
			if (salary == null) {
				Contract contract = (Contract) getTo();
				Date startDate = getStartDate().before(contract.getStartDate())?contract.getStartDate():getStartDate(); 
				Date endDate = (contract.getEndDate() != null && getEndDate().after(contract.getEndDate()))?contract.getEndDate():getEndDate(); 
				Date issueDate = getIssueDate(); 
				ISalaryCalculatorContext ctx = contract.getSalaryCalculatorContext(startDate,endDate,issueDate);
				salary = ctx.getSalaryProxy().getSalary();
			}
			return salary;
		} catch (SalaryException e) {
			e.printStackTrace();
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
	
	public void onChangeMonth(ActionEvent event) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(getIssueDate());
			c.set(Calendar.MONTH, getMonth().ordinal());
			setIssueDate(c.getTime());
			setSalary(null);
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireAfterBeanSelected(evt);
		} catch (ControllerListenerException e) {
			throw new AbortProcessingException("Imposible mostrar la simulación de la nómina");
		}
	}
	public void onChangeYear(ActionEvent event) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(getIssueDate());
			c.set(Calendar.YEAR, getYear());
			setIssueDate(c.getTime());
			setSalary(null);
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireAfterBeanSelected(evt);
		} catch (ControllerListenerException e) {
			throw new AbortProcessingException("Imposible mostrar la simulación de la nómina");
		}
	}
}
