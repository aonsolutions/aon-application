package com.code.aon.ui.employee.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.company.Enterprise;
import com.code.aon.employee.Salary;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.util.AonUtil;

public class SalaryExpenseController implements Serializable, ICollectionProvider{

	private static final long serialVersionUID = 3030775088000990987L;
	
//	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryExpenseController.class.getName());
	
	private boolean showSalaryExpenseWindow;
	private Month month;
	private Integer year;
	private List<Salary> list;
	
	public boolean isShowSalaryExpenseWindow() {
		return showSalaryExpenseWindow;
	}

	public void setShowSalaryExpenseWindow(boolean showSalaryExpenseWindow) {
		this.showSalaryExpenseWindow = showSalaryExpenseWindow;
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
	
	public List<Salary> getList() {
		return list;
	}
	
	public void setList(List<Salary> list) {
		this.list = list;
	}
	
	public void loadList() throws ManagerBeanException {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean("enterprise");
		Enterprise e = (Enterprise) controller.getTo();
		IManagerBean bean = BeanManager.getManagerBean(Salary.class);		
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEmployeeAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID);
		criteria.addEqualExpression(alias, e.getId());
		alias = bean.getFieldName(IEmployeeAlias.SALARY_ISSUE_DATE);
		criteria.addGreaterThanOrEqualExpression(alias, getStartDate());
		alias = bean.getFieldName(IEmployeeAlias.SALARY_ISSUE_DATE);
		criteria.addLessThanOrEqualExpression(alias, getEndDate());
		criteria.addOrder(bean.getFieldName(IEmployeeAlias.SALARY_CONTRACT_WORK_PLACE_ID));
		criteria.addOrder(bean.getFieldName(IEmployeeAlias.SALARY_EMPLOYEE_NAME));
		setList(new LinkedList<Salary>());
		for( ITransferObject to : bean.getList(criteria) ) {
			Salary s = (Salary) to;
			getList().add(s);
		}	
	}
	
	/*
	 *  COLLECTION PARA EL JASPERREPORT
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
		try {
			loadList();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return getList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean forceRefresh)
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
