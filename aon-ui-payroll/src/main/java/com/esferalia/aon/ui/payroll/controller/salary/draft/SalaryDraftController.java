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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.AgreementLevelPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.HierarchyPayments;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.SalarySupplements;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.event.salary.draft.SalaryDraftComparatorPrinter;

public class SalaryDraftController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryDraftController.class.getName());

	private Month month;
	private int year;
	
	private Date issueDate;
	private Date startDate;
	private Date endDate;
	private ISalary salary;
	private DataModel paymentsModel;
	private DataModel deductionsModel;
	
	private boolean showSalaryDifference;
	private ISalary bdSalary;
	private SalaryDraftComparatorPrinter printer;
	
	public boolean isShowSalaryDifference() {
		return showSalaryDifference;
	}
	public void setShowSalaryDifference(boolean showSalaryDifference) {
		this.showSalaryDifference = showSalaryDifference;
	}
	public ISalary getBdSalary() {
		return bdSalary;
	}
	public void setBdSalary(ISalary bdSalary) {
		this.bdSalary = bdSalary;
	}

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
	
	public boolean isContractScope(){
		if(this.getPaymentsModel().isRowAvailable()){
			return ((IContractPayment)this.getPaymentsModel().getRowData()).getScope()==ExpressionScope.CONTRACT;
		}
		return false;
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
	
	public void onShowPayments( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			SalaryDraftPaymentController c = (SalaryDraftPaymentController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_PAYMENT_CONTROLLER);
			c.reset(false);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IPayrollAlias.CONTRACT_PAYMENT_CONTRACT_ID), to.getId());
			c.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar las percepciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}						
	}
	
	public void onShowDeductions( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			SalaryDraftDeductionController c = (SalaryDraftDeductionController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_DEDUCTION_CONTROLLER);
			c.reset(false);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IPayrollAlias.CONTRACT_DEDUCTION_CONTRACT_ID), to.getId());
			c.getCriteria().addLessThanOrEqualExpression(c.getFieldName(IPayrollAlias.CONTRACT_DEDUCTION_START_DATE), this.getStartDate());
			
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(c.getFieldName(IPayrollAlias.CONTRACT_DEDUCTION_END_DATE), this.getEndDate());
			Expression expr2 = ExpressionUtilities.getNullExpression(c.getFieldName(IPayrollAlias.CONTRACT_DEDUCTION_END_DATE));
			c.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
			
			c.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar las percepciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
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
	
	// *********************************
	// OBTENCION DEL BORRADOR DE NOMINA
	// *********************************
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
				printer = null;
				paymentsList = null;
				initializePaymentModel();
			}
			return salary;
		} catch (SalaryException e) {
			String msg = "Imposible mostrar el borrador de la nómina";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
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
	public void setPaymentsModel(DataModel paymentsModel) {
		this.paymentsModel = paymentsModel;
	}
	
	@SuppressWarnings("unchecked")
	private void initializePaymentModel() {
		try {
			IManagerBean aBean = BeanManager.getManagerBean(AgreementLevelPayment.class);
			IManagerBean cBean = BeanManager.getManagerBean(ContractPayment.class);
			Contract contract = (Contract) getTo();
			Criteria aCriteria = new Criteria();
			aCriteria.addEqualExpression(aBean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_PAYMENT_LEVEL_ID), contract.getAgreementLevelCategory().getLevel().getId());
			aCriteria.addOrder(aBean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_PAYMENT_START_DATE), false);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(aBean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_PAYMENT_END_DATE), new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(aBean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_PAYMENT_END_DATE));
			aCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			Criteria cCriteria = new Criteria();
			cCriteria.addEqualExpression(cBean.getFieldName(IPayrollAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
			cCriteria.addOrder(cBean.getFieldName(IPayrollAlias.CONTRACT_PAYMENT_START_DATE), false);
			expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(cBean.getFieldName(IPayrollAlias.CONTRACT_PAYMENT_END_DATE), new Date());
			expr2 = ExpressionUtilities.getNullExpression(cBean.getFieldName(IPayrollAlias.CONTRACT_PAYMENT_END_DATE));
			cCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			List<?> al = aBean.getList(aCriteria);
			List<?> cl = cBean.getList(cCriteria);
			HierarchyPayments payments = new HierarchyPayments( ((List<IContractPayment>) cl).iterator(), ((List<IContractPayment>) al).iterator());
			List<IContractPayment> list = new LinkedList<IContractPayment>();
			for (IContractPayment payment: payments) {
				list.add(payment);
			}
			paymentsModel = new ListDataModel(list);
		} catch (ManagerBeanException e) {
			String msg = "Imposible inicializar lar percepciones";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	// *********************************************************************
	// OBTENCION DE LA DIFERENCIA DEL BORRADOR CON SU CORRESPONDIENTE NOMINA
	// *********************************************************************
	private List<IPayment> paymentsList;
	public List<IPayment> getPaymentsList(){
		if (paymentsList == null) {
			initializeList();
		}
		return paymentsList;
	}
	public void initializeList(){
		try {
			Contract contract = (Contract) getTo();
			contract.setSalaryCalculatorContext(null);
			Date startDate = getStartDate().before(contract.getStartDate())?contract.getStartDate():getStartDate(); 
			Date endDate = (contract.getEndDate() != null && getEndDate().after(contract.getEndDate()))?contract.getEndDate():getEndDate(); 
			Date issueDate = getIssueDate(); 
			ISalaryCalculatorContext ctx = contract.getSalaryCalculatorContext(startDate,endDate,issueDate);
			List<IPayment> payments = ctx.getSalaryProxy().getSalary().getPayments().getSalarySupplements().getValues();
			
			paymentsList = new LinkedList<IPayment>();
			SalarySupplements ss = null;
			ss = new SalarySupplements();
			for(IPayment p: payments){
				ss.addPayment(p);
			}
			paymentsList = ss.getValues();
		} catch (SalaryException e) {
			// TODO como tratar esto?
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public SalaryDraftComparatorPrinter getPrinter() {
		if(printer==null){
			Contract contract = (Contract) getTo();
			Date startDate = getStartDate().before(contract.getStartDate())?contract.getStartDate():getStartDate(); 
			Date endDate = (contract.getEndDate() != null && getEndDate().after(contract.getEndDate()))?contract.getEndDate():getEndDate(); 
			Date issueDate = getIssueDate(); 
			printer = new SalaryDraftComparatorPrinter(contract, getBdSalary(), startDate, endDate, issueDate);
			contract.setSalaryCalculatorContext(null); 
		}
		return printer;
	}
	public void setPrinter(SalaryDraftComparatorPrinter printer) {
		this.printer = printer;
	}
	
	public void searchSavedDraftSalary(){
		Contract contract = (Contract) this.getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_ID), contract.getId());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_START_DATE), getStartDate());
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_END_DATE), getEndDate());
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_TYPE), SalaryType.SALARY);
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				setBdSalary((ISalary) list.get(0));
				setShowSalaryDifference(true);
			} else {
				setBdSalary(null);
				setShowSalaryDifference(false);
			}
		} catch (ManagerBeanException e) {
			setBdSalary(null);
			setShowSalaryDifference(false);
			String msg = "Fallo en la obtención de la nómina calculada";
			AonUtil.addErrorMessage(msg);
		}
	}
}
