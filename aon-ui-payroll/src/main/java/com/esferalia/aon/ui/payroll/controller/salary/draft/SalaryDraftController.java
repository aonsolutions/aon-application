package com.esferalia.aon.ui.payroll.controller.salary.draft;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.AonException;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.payment.IPayment;

public class SalaryDraftController extends BasicController {

	private Month month;
	private int year;
	
	private Date issueDate;
	private Date startDate;
	private Date endDate;
	private ISalary salary;
	private DataModel paymentsModel;
	private DataModel deductionsModel;

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

	// *********
	// IMPRESION
	// *********
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
	public boolean isShowBackground() {
		return true;
	}
	
	// **********************
	// OBTENCION DE LA NOMINA
	// **********************
	public ISalary getSalary() {
		try {
			if (salary == null) {
				Contract contract = (Contract) getTo();
				Date startDate = getStartDate().before(contract.getStartDate())?contract.getStartDate():getStartDate(); 
				Date endDate = (contract.getEndDate() != null && getEndDate().after(contract.getEndDate()))?contract.getEndDate():getEndDate(); 
				Date issueDate = getIssueDate(); 
				ISalaryCalculatorContext ctx = contract.getSalaryCalculatorContext(startDate,endDate,issueDate);
				salary = ctx.getSalaryProxy().getSalary();
				paymentsModel = null;
			}
			return salary;
		} catch (SalaryException e) {
			e.printStackTrace();
			throw new AbortProcessingException("Imposible mostrar el borrador de la nómina");
		}
	}
	public void setSalary(ISalary salary) {
		this.salary = salary;
		if (salary == null) {
			Contract c = (Contract) getTo();
			if (c != null) {
				c.setSalaryCalculatorContext(null);		
			}
		}
	}

	public DataModel getPaymentsModel() {
		if (paymentsModel == null) {
			initializePaymentModel();
		}
		return paymentsModel;
	}
	private void initializePaymentModel() {
		try {
			Contract contract = (Contract) getTo();
			ContractSalaryCalculatorContext ctx = (ContractSalaryCalculatorContext) contract.getSalaryCalculatorContext();
			Collection<IContractPayment> payments = ctx.getContractPayments();
			List<ContractPayment> list = new LinkedList<ContractPayment>();
			for (IContractPayment payment: payments) {
				ContractPayment cp = new ContractPayment();
				cp.setContract(contract);
				cp.setStartDate(getStartDate());
				cp.setEndDate(getEndDate());
				cp.setType(payment.getType());
				cp.setDescription(payment.getDescription());
				cp.setExpression(payment.getExpression());
//				cp.setPaymentConcept(payment.getPaymentConcept());
				cp.setIrpfExpression(payment.getIrpfExpression());
				cp.setQuoteExpression(payment.getQuoteExpression());
				cp.setMonth(payment.getMonth());
				cp.setSalaryType(payment.getSalaryType());
				cp.setDescriptionDecorable(payment.isDescriptionDecorable());
				list.add(cp);
			}
			paymentsModel = new ListDataModel(list);
		} catch (SalaryException e) {
			e.printStackTrace();
			throw new AbortProcessingException("Imposible mostrar los devengos de la nómina");
		} catch (AonException e) {
			e.printStackTrace();
			throw new AbortProcessingException("Imposible mostrar los devengos de la nómina");
		}
	}
}
