package com.esferalia.aon.ui.payroll.controller.salary;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.EnterpriseParamsController;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class SalaryExpenseController implements Serializable, ICollectionProvider{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryExpenseController.class.getName());
	
	private boolean showSalaryExpenseWindow;
	private Enterprise enterprise;
	private Person person;
	private boolean groupByPerson;
	private Month month;
	private Integer year;
	private Date startDate;
	private Date endDate;
	private List<ISalary> list;
	private boolean expenseDraft;
	private Integer activeEmployeeCount;
	private Integer salaryCount;
	
	
	public Person getPerson() {
		return person;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public boolean isGroupByPerson() {
		return groupByPerson;
	}

	public void setGroupByPerson(boolean groupByPerson) {
		this.groupByPerson = groupByPerson;
	}

	public Integer getActiveEmployeeCount() {
		return activeEmployeeCount;
	}

	public void setActiveEmployeeCount(Integer activeEmployeeCount) {
		this.activeEmployeeCount = activeEmployeeCount;
	}

	public Integer getSalaryCount() {
		return salaryCount;
	}

	public void setSalaryCount(Integer salaryCount) {
		this.salaryCount = salaryCount;
	}
	
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
		if(month==null){
			month = Month.getMonthByValue(CommonUtil.getMonth(Calendar.getInstance().getTime()));
		}
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
		populateMonth();
	}

	private void populateMonth() {
		if(startDate==null){
			startDate = new Date();
		}
		if(endDate==null){
			endDate = new Date();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.set(Calendar.MONTH, this.month.getValue());
		cal.set(Calendar.DAY_OF_MONTH, 1);
		startDate = cal.getTime();
		cal.setTime(endDate);
		cal.set(Calendar.MONTH, this.month.getValue());
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		endDate = cal.getTime();
	}

	public Integer getYear() {
		if(year==null){
			year = CommonUtil.getYear(Calendar.getInstance().getTime());
		}
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
		populateYear();
	}
	
	private void populateYear() {
		if(startDate==null){
			startDate = new Date();
		}
		if(endDate==null){
			endDate = new Date();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.set(Calendar.YEAR, this.year);
		startDate = cal.getTime();
		cal.setTime(endDate);
		cal.set(Calendar.YEAR, this.year);
		endDate = cal.getTime();
	}

	private Date getStartDate(){
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	private Date getEndDate(){
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<ISalary> getList() {
		return list;
	}
	
	public void setList(List<ISalary> list) {
		this.list = list;
	}
	
	
	public void loadList() throws ManagerBeanException, SalaryException {
		if(enterprise==null){
			EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
			enterprise = (Enterprise) controller.getTo();
		}
		Criteria criteria = new Criteria();
		if(isExpenseDraft()){
			SalaryDraftController draft = 
				(SalaryDraftController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
			String alias = draft.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID);
			draft.getCriteria().addEqualExpression(alias, enterprise.getId());
			if(getPerson()!=null && getPerson().getId()!=null){
				alias = draft.getFieldName(IEntityAlias.SALARY_CONTRACT_PERSON_ID);
				criteria.addEqualExpression(alias, getPerson().getId());
			}
			draft.getCriteria().addOrder(draft.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ID));
			draft.getCriteria().addOrder(draft.getFieldName(IEntityAlias.CONTRACT_PERSON_FIRST_SURNAME));
			draft.initializeModel();
			
			draft.setYear(getYear());
			draft.setMonth(getMonth());

			draft.reset();
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
			String alias = null;
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DOMAIN), enterprise.getDomain());
			criteria.setSkipDomainFilter(true);
			if(getPerson()!=null && getPerson().getId()!=null){
				alias = bean.getFieldName(IEntityAlias.SALARY_CONTRACT_PERSON_ID);
				criteria.addEqualExpression(alias, getPerson().getId());
			}
			alias = bean.getFieldName(IEntityAlias.SALARY_ISSUE_DATE);
			criteria.addGreaterThanOrEqualExpression(alias, getStartDate());
			alias = bean.getFieldName(IEntityAlias.SALARY_ISSUE_DATE);
			criteria.addLessThanOrEqualExpression(alias, getEndDate());
			if(isGroupByPerson()){
				criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_EMPLOYEE_NAME));
				criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID));
				criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_ISSUE_DATE));
			} else {
				criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_ISSUE_DATE));
				criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID));
				criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_EMPLOYEE_NAME));
			}
			setList(new LinkedList<ISalary>());
			
			for( ITransferObject to : bean.getList(criteria) ) {
				Salary s = (Salary) to;
				getList().add(s);
			}	
		}
	}
	
	public boolean isAllSalaryCalculated() throws ManagerBeanException{
		countActiveEmployee();
		countCalculatedSalary();
		return getActiveEmployeeCount().equals(getSalaryCount());
	}

	private List<ITransferObject> getActiveEmployees() throws ManagerBeanException {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
		Enterprise enterprise = (Enterprise) controller.getTo();
		IManagerBean bean = BeanManager.getManagerBean(Contract.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), enterprise.getId());
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_START_DATE), getStartDate());
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE), getEndDate());
		Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		return bean.getList(criteria);
	}
	private void countActiveEmployee() throws ManagerBeanException {
		setActiveEmployeeCount(getActiveEmployees().size());
	}

	private List<ITransferObject> getCalculatedSalaries() throws ManagerBeanException {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
		Enterprise enterprise = (Enterprise) controller.getTo();
		Criteria criteria = new Criteria();
		IManagerBean bean = BeanManager.getManagerBean(Salary.class);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID), enterprise.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), SalaryType.SALARY);
		criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_ISSUE_DATE), getStartDate());
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_ISSUE_DATE), getEndDate());
		return bean.getList(criteria);
	}
	private void countCalculatedSalary() throws ManagerBeanException {
		setSalaryCount(getCalculatedSalaries().size());
	}
	
	public void launchRemainingSalaries(ActionEvent event) throws ManagerBeanException{
		List<ITransferObject> employeeList = getActiveEmployees();
		List<ITransferObject> salaryList = getCalculatedSalaries();
		for(ITransferObject to: salaryList){
			Salary salary = (Salary) to;
			employeeList.remove(salary.getContract());
		}
		PayrollUtils utils = PayrollUtils.getInstance();
		for(ITransferObject to: employeeList){
			Contract contract = (Contract) to;
			Salary salary = (Salary) utils.calculateSalary(contract, getStartDate(), getEndDate());
			if(salary!=null){
				salary.setContract(contract);
				IManagerBean bean = BeanManager.getManagerBean(Salary.class);
				bean.insert((ITransferObject) salary);
			} else {
				AonUtil.addErrorMessage("No se ha podido calcular la nomina de "+contract.getPerson().getFullName());
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
	
	public String getReportKey() {
		try {
			EnterpriseParamsController enterpriseParams = (EnterpriseParamsController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_PARAMS_CONTROLLER_NAME);
			String reportKey = enterpriseParams.getParameter("PAY_REPORT_enterpriseSalary_PAY").getExpression();
			if(StringUtils.isBlank(reportKey) || reportKey.equals("salaryExpense")) {
				return IPayrollConstants.COST_REPORT;
			} else if(reportKey.equals("salaryExpenseExtended")) {
				return IPayrollConstants.COST_REPORT_DETAILED;
			}
			return StringUtils.isNotBlank(reportKey)?reportKey:IPayrollConstants.COST_REPORT;
		} catch (ManagerBeanException e) {
			LOGGER.error("*** Unable to load salary cost reportKey -> " + e.getMessage());
		}
		return IPayrollConstants.COST_REPORT;
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
