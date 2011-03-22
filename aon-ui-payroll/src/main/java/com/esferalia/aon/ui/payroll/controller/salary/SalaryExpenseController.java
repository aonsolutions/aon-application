package com.esferalia.aon.ui.payroll.controller.salary;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;

public class SalaryExpenseController implements Serializable, ICollectionProvider{

	private static final long serialVersionUID = 3030775088000990987L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryExpenseController.class.getName());
	
	private boolean showSalaryExpenseWindow;
	private Month month;
	private Integer year;
	private List<ISalary> list;
	private boolean expenseDraft;
	
	public boolean isShowSalaryExpenseWindow() {
		return showSalaryExpenseWindow;
	}

	public void setShowSalaryExpenseWindow(boolean showSalaryExpenseWindow) {
		this.showSalaryExpenseWindow = showSalaryExpenseWindow;
	}

	public boolean isExpenseDraft() {
		return expenseDraft;
	}

	public void setExpenseDraft(boolean expenseDraft) {
		this.expenseDraft = expenseDraft;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	private Date getStartDate(){
		GregorianCalendar cal= new GregorianCalendar();
		cal.set(getYear(), getMonth().getValue(), 1);
		return cal.getTime();
	}
	private Date getEndDate(){
		GregorianCalendar cal= new GregorianCalendar();
		cal.set(Calendar.YEAR, getYear());
		cal.set(Calendar.MONTH, getMonth().getValue());
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}
	
	public List<ISalary> getList() {
		return list;
	}
	
	public void setList(List<ISalary> list) {
		this.list = list;
	}
	
	public void loadList() throws ManagerBeanException, SalaryException {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean("enterprise");
		Enterprise e = (Enterprise) controller.getTo();
		Criteria criteria = new Criteria();
		if(isExpenseDraft()){
			SalaryDraftController draft = (SalaryDraftController) FormUtil.getController("salaryDraft");
			String alias = draft.getFieldName(IPayrollAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID);
			draft.getCriteria().addEqualExpression(alias, e.getId());
			draft.getCriteria().addOrder(draft.getFieldName(IPayrollAlias.CONTRACT_WORK_PLACE_ID));
			draft.getCriteria().addOrder(draft.getFieldName(IPayrollAlias.CONTRACT_PERSON_FIRST_SURNAME));
			draft.initializeModel();
			Calendar c = Calendar.getInstance();
			c.setTime(draft.getIssueDate());
			c.set(Calendar.YEAR, getYear());
			c.set(Calendar.MONTH, getMonth().ordinal());
			draft.setIssueDate(c.getTime());
			draft.setSalary(null);
			draft.setMonth(getMonth());
			draft.setYear(getYear());
			setList(new LinkedList<ISalary>());
			while(!draft.isInLast()){
				if(getList().isEmpty()){
					draft.onSelectFirst(null);
					getList().add(draft.getSalary());
				} else {
					draft.onSelectNext(null);
					getList().add(draft.getSalary());
				}
			}
		} else {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			String alias = bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID);
			criteria.addEqualExpression(alias, e.getId());
			alias = bean.getFieldName(IPayrollAlias.SALARY_ISSUE_DATE);
			criteria.addGreaterThanOrEqualExpression(alias, getStartDate());
			alias = bean.getFieldName(IPayrollAlias.SALARY_ISSUE_DATE);
			criteria.addLessThanOrEqualExpression(alias, getEndDate());
			criteria.addOrder(bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_WORK_PLACE_ID));
			criteria.addOrder(bean.getFieldName(IPayrollAlias.SALARY_EMPLOYEE_NAME));
			setList(new LinkedList<ISalary>());
			for( ITransferObject to : bean.getList(criteria) ) {
				Salary s = (Salary) to;
				getList().add(s);
			}	
		}
	}
	
	/*
	 *  COLLECTION PARA EL JASPERREPORT
	 */
	@Override
	public Collection<?> getCollection() {
		try {
			loadList();
		} catch (ManagerBeanException e) {
			String msg = "error on loading list";
			LOGGER.error(msg);
		} catch (SalaryException e) {
			String msg = "error on loading list";
			LOGGER.error(msg);
		}
		return getList();
	}

	@Override
	public Collection<?> getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
	
	
	/*
	 * ACTION LISTENER
	 */
	public void onInitialize(ActionEvent event){
		GregorianCalendar cal= new GregorianCalendar();
		setMonth(Month.getMonthByValue(cal.get(Calendar.MONTH)-1));
		setYear(cal.get(Calendar.YEAR));	
	}
	
}
