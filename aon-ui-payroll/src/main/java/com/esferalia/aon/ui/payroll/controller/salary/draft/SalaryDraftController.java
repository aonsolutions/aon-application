package com.esferalia.aon.ui.payroll.controller.salary.draft;

import static com.code.aon.ui.common.ICommonMessages.PAYROLL_SALARY;
import static com.code.aon.ui.common.ICommonMessages.PAYROLL_SALARY_BONUS;
import static com.code.aon.ui.common.ICommonMessages.PAYROLL_SALARY_DEDUCTIONS;
import static com.code.aon.ui.common.ICommonMessages.PAYROLL_SALARY_PAYMENTS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.EnterpriseData;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.registry.controller.PersonController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.Pair;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryEmbargo;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.HierarchyPayments;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.calculator.OutOfDateException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.salary.expression.ExpressionContext.RemovedExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.SalarySupplements;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.salary.SortedSalaryItems;

public class SalaryDraftController extends BasicController implements ContractSalaryCalculator.IListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryDraftController.class.getName());
	
	
	public static class Warning {
		
		
		private String title;
		private String message;
		private String description;
		private String variable;
		
		public String getTitle() {
			return title;
		}
		
		public String getMessage() {
			return message;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getVariable() {
			return variable;
		}
		
		public String getAction() {
			Pair<String, Method> pair = 
					WARNING_ACTIONS.get(variable);
			return pair != null ? pair.getFirst() : null;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null)  {
				return false;
			}
			if (this == obj) {
				return true;
			}
			if (obj.getClass() != getClass()) {
				return false;
			}
			final Warning warning = (Warning) obj;
			
			return equals(title, warning.title) &&  
					equals(variable, warning.variable) && 
					equals(message, warning.message) && 
					equals(description, warning.description) ;
		}
		
		private boolean equals ( Object one, Object another ) {
			if ( one == another ) {
				return true;
			}
	        
			if (one == null || another == null) {
	            return false;
	        }
			
			return one.equals(another);
		}

	}

	private int 								year;			
	private Month 								month;		 
	private int 								toYear;		
	private Month 								toMonth;		 
	private SalaryType 							salaryType = SalaryType.SALARY ;
	private Date								startDate;
	private Date								endDate;
	
	
	private ISalary 							salary;				// online salary
	private ISalary 							dbSalary;			// saved ( database ) salary
	
	private SortedSalaryItems<PaymentType>		payments;
	private SortedSalaryItems<DeductionType>	deductions;
	private SortedSalaryItems<DeductionType>	costs;
	private SortedSalaryItems<BonusType>		bonuses;
	
	
	private List<SelectItem> 					draftMonths;		
	private List<SelectItem> 					toDraftMonths;		
	private List<SelectItem> 					salaryDraftTypes;	

	private DataModel 							paymentsModel;
//	private DataModel 							deductionsModel;
	
	private List<Warning>						warnings ;
	
	private Warning 							warning;
	
	
	public boolean gethasWarnings(){
		return warnings.size() > 0;
	}
	
	public List<Warning> getWarnings() {
		return warnings;
	}
	
	public List<SelectItem> getDraftMonths() {
		return draftMonths;
	}
	
	
	public List<SelectItem> getToDraftMonths() {
		return toDraftMonths;
	}
	
	public boolean isValidSalaryDraftPeriod() {
		return getSalary() != null;
	}
	
	public boolean isShowSalaryDifference() {
		return getBdSalary() != null;
	}
	
	public ISalary getSalary() {
		return salary;
	}
	
	public ISalary getBdSalary() {
		return dbSalary;
	}

	public Date getIssueDate() {
		ISalary salary = getSalary();
		return salary != null ? salary.getIssueDate() : null;
	}
	
	public Date getStartDate() {
		return salary != null ? salary.getStartDate() : null;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getEndDate() {
		return salary != null ? salary.getEndDate() : null;
	}
	
	public int getMinYear() {
		if ( this.salaryType  == SalaryType.SETTLE ) {
			// contract end...
			Contract contract = (Contract) getTo();
			Date endDate = contract.getEndDate();
			return CommonUtil.getYear(endDate);
		}
		else {
			Contract contract = (Contract) getTo();
			Date startDate = contract.getStartDate();
			return startDate != null ?
					CommonUtil.getYear(startDate):
					Calendar.getInstance().getMinimum(Calendar.YEAR);
		}
	}
	
	public int getMaxYear() {
		Contract contract = (Contract) getTo();
		Date endDate = contract.getEndDate();
		return endDate != null ?
				CommonUtil.getYear(endDate):
				Calendar.getInstance().getMaximum(Calendar.YEAR);
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
	
	public Month getToMonth() {
		return toMonth;
	}
	
	public void setToMonth(Month toMonth) {
		this.toMonth = toMonth;
	}
	
	public int getToYear() {
		return toYear;
	}
	
	public void setToYear(int toYear) {
		this.toYear = toYear;
	}
	
	public SalaryType getSalaryType() {
		return salaryType ;
	}
	public void setSalaryType(SalaryType salaryType) {
		this.salaryType = salaryType;
	}
	
	public void setWarning(Warning warning) {
		this.warning = warning;
	}
	
	public boolean isContractScope(){
		if(this.getPaymentsModel().isRowAvailable()){
			return ((IContractPayment)this.getPaymentsModel().getRowData()).getScope()==ExpressionScope.CONTRACT;
		}
		return false;
	}
	
	@Override
	protected void accept() {
		super.accept();
		reset();
	}

	@Override
	public void select(ActionEvent event) {
		super.select(event);
		reset();
	}

	@Override
	protected void remove() throws ManagerBeanException {
		super.remove();
		reset();
	}

	public void onReloadDraft(ActionEvent event) {
		reset();
	}
	
	public void onShowPayments( ActionEvent event ) {
		try {
			Contract to = (Contract) getTo();
			SalaryDraftPaymentController c = (SalaryDraftPaymentController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_PAYMENT_CONTROLLER);
			c.reset(false);
			c.onEditSearch(event);
			c.getCriteria().addEqualExpression(c.getFieldName(IEntityAlias.CONTRACT_PAYMENT_CONTRACT_ID), to.getId());
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
			c.getCriteria().addEqualExpression(c.getFieldName(IEntityAlias.CONTRACT_DEDUCTION_CONTRACT_ID), to.getId());
			c.getCriteria().addLessThanOrEqualExpression(c.getFieldName(IEntityAlias.CONTRACT_DEDUCTION_START_DATE), this.getStartDate());
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(c.getFieldName(IEntityAlias.CONTRACT_DEDUCTION_END_DATE), this.getEndDate());
			Expression expr2 = ExpressionUtilities.getNullExpression(c.getFieldName(IEntityAlias.CONTRACT_DEDUCTION_END_DATE));
			c.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
			c.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar las percepciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}		
	}
	
	public void onShowBonus( ActionEvent event ) {
//		try {
//			SalaryDraftBonusController c = (SalaryDraftBonusController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_BONUS_CONTROLLER);
//			c.reset(false);
//			c.onEditSearch(event);
//			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(c.getFieldName(IEntityAlias.CONTRACT_BONUS_END_DATE), getStartDate());
//			Expression expr2 = ExpressionUtilities.getNullExpression(c.getFieldName(IEntityAlias.CONTRACT_BONUS_END_DATE));
//			c.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
//			c.onSearch(event);
//			c.getModel();
//		} catch (ManagerBeanException e) {
//			String msg = "Imposible mostrar las bonificaciones del contrato (" + e.getMessage() +")";
//			LOGGER.error(msg);
//			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg,e);
//		}						
	}

	@Override
	public void onCheckError(String message) {
		Warning warning = new Warning();
		warning.title = AonUtil.getMessage(PAYROLL_SALARY );
		warning.message = message;
		warning.description = "";
		warnings.add(warning);
	}
	
	@Override
	public void onInvalidData(String variableName, String message) {
		
		Warning warning = new Warning();
		warning.title = AonUtil.getMessage(PAYROLL_SALARY );
		
		
		if ( message == null ) {
			String description = variableName ; 
			ContextVariable variable = ContextVariable.getVariableByName(variableName);
			if ( variable != null ) {
				try {
					description  = variable.getDescription(getLocale());
				} catch (MissingResourceException e) {
				}
			}
			warning.message = String.format("Introduzca la(o)s %s" , description );
		}
		else {
			warning.message = message;			
		}
		
		warning.description = variableName;
		warnings.add(warning);
	}
	
	@Override
	public void onCheckError(IContractBonus bonus, String message) {
		Warning warning = new Warning();
		warning.title = AonUtil.getMessage(PAYROLL_SALARY_BONUS );
		warning.message = message;
		warning.description = bonus.getDescription();
		warnings.add(warning);
	}
	@Override
	public void onInvalidData(IContractBonus bonus, String variableName,
			String message) {
		Warning warning = new Warning();
		warning.title = AonUtil.getMessage(PAYROLL_SALARY_BONUS );
		warning.message = message;
		warning.description = bonus.getDescription();
		warning.variable = variableName;
		warnings.add(warning);
		
	}
	
	@Override
	public void onRemove(IContractPayment payment) {
	}

	@Override
	public void onRemove(IContractBonus payment) {
	}

	@Override
	public void onCheckError(IContractPayment payment, String message) {
		Warning warning = new Warning();
		warning.title = AonUtil.getMessage(PAYROLL_SALARY_PAYMENTS);
		warning.message = message;
		warning.description = payment.getDescription();
		warnings.add(warning);
	}
	
	@Override
	public void onInvalidData(IContractPayment payment,
			String variableName, String message) {
		Warning warning = new Warning();
		warning.title = AonUtil.getMessage(PAYROLL_SALARY_PAYMENTS);
		warning.message = message;
		warning.description = payment.getDescription();
		warning.variable = variableName;
		warnings.add(warning);
	}

	@Override
	public void onRemove(IContractDeduction deduction) {
	}

	@Override
	public void onCheckError(IContractDeduction deduction, String message) {
		Warning warning = new Warning();
		warning.title = AonUtil.getMessage(PAYROLL_SALARY_DEDUCTIONS);
		warning.message = message;
		warning.description = deduction.getDescription();
		warnings.add(warning);
	}
	
	@Override
	public void onInvalidData(IContractDeduction dedcution,
			String variableName, String message) {
		Warning warning = new Warning();
		warning.title = AonUtil.getMessage(PAYROLL_SALARY_DEDUCTIONS);
		warning.message = message;
		warning.description = dedcution.getDescription();
		warning.variable = variableName;
		warnings.add(warning);
	}

	public void onWarning(ActionEvent event) {
		Pair<String, Method> pair = 
				WARNING_ACTIONS.get(warning.getVariable());
		if ( pair == null ) {
			return;
		}
		
		Method actionListener = pair.getSecond();
		try {
			actionListener.invoke(this, event);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onPersonQuickFix(ActionEvent event) {
		try {
			PersonController controller = 
				(PersonController) FormUtil.getController(IPayrollConstants.PERSON_CONTROLLER_NAME);
			controller.onEditSearch(event);
			
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					controller.getManagerBean().getFieldName(IEntityAlias.PERSON_ID), 
					getPerson().getId());
			controller.clearCriteria();
			controller.setCriteria(criteria);
			controller.onSearch(event);
			controller.getModel().setRowIndex(0);
			controller.onSelect(event);http://www.jpackage.org/
			controller.setBackAction(IPayrollConstants.SALARY_DRAFT_FORM);
			
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
	// ------------------------------------------
	// IMPRESION
	// ------------------------------------------
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

	
	private Contract getContract() {
		return  (Contract) getTo();
	}
	
	private Person getPerson() {
		return  getContract().getPerson();
	}
	
	private Date getStartDraftDate(int year, Month month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month.getValue());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}
	
	private Date getEndDraftDate(int year, Month month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month.getValue());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int lastMonthday = 
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, lastMonthday);
		return calendar.getTime();
	}

	// ------------------------------------------
	// Gets ( calculate ) salary draft.
	// ------------------------------------------
	private ISalary calculateSalary() {
		try {
			Contract contract = (Contract) getTo();

			IContractSalaryCalculatorContext ctx;
			if ( getSalaryType() == SalaryType.DELAY ){
				Date startDraftDate = 
						getStartDraftDate(year, month);
				Date endDraftDate = 
						getEndDraftDate(toYear, toMonth);
				ctx = contract.getSalaryCalculatorContext(
						startDraftDate,
						endDraftDate,
						getSalaryType());
			}
			else { 
				ctx = contract.getSalaryCalculatorContext(
						getYear(),
						getMonth(),
						getSalaryType());
			}
			
			
			ContractSalaryCalculator<Salary> sc = 
					new ContractSalaryCalculator<Salary>();
			sc.setSalaryBuilder(new SalaryBuilder(){
				@Override
				public void createNewSalary() {
					super.createNewSalary();
					salary.setContract(getContract());
				}
			});
			
			warnings= new LinkedList<Warning>() {
				@Override
				public boolean add(Warning e) {
					return contains(e) ? false : super.add(e);
				}
			};
			sc.setListener(this);
			
			salary = sc.calculate( ctx );
			
			
			paymentsModel = null;
			paymentsList = null;
			initializePaymentModel();

			return salary;
		} 
		catch (OutOfDateException e) {
			return null;
		}catch (SalaryException e) {
			String msg = "Error en el calculo del borrador de la nómina";
			LOGGER.error(msg, e);
			return null;
		}
	}

	public void reset() {
		initDraftTypes();
		rebuildDraftMonths();
		rebuildToDraftMonths();
		calculateSalary();
		searchSavedDraftSalary();
		initPayments();
		initDeductions();
		initCosts();
		initBonuses();
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
	
	public SortedSalaryItems<BonusType> getSortedSalaryBonuses() {
		return bonuses;
	}

	public SortedSalaryItems<DeductionType> getSortedSalaryCosts() {
		return costs;
	}

	public SortedSalaryItems<PaymentType> getSortedSalaryPayments() {
		return payments;
	}
	
	public SortedSalaryItems<DeductionType> getSortedSalaryDeductions() {
		return deductions;
	}

	
	@SuppressWarnings("unchecked")
	private void initializePaymentModel() {
		try {
			IManagerBean aBean = BeanManager.getManagerBean(AgreementPayment.class);
			IManagerBean cBean = BeanManager.getManagerBean(ContractPayment.class);
			Contract contract = (Contract) getTo();
			Criteria aCriteria = new Criteria();
			aCriteria.addEqualExpression(aBean.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_AGREEMENT_ID), contract.getAgreementLevel().getAgreement().getId());
			aCriteria.addOrder(aBean.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_START_DATE), false);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(aBean.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_END_DATE), new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(aBean.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_END_DATE));
			aCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			Criteria cCriteria = new Criteria();
			cCriteria.addEqualExpression(cBean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
			cCriteria.addOrder(cBean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_START_DATE), false);
			expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(cBean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_END_DATE), new Date());
			expr2 = ExpressionUtilities.getNullExpression(cBean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_END_DATE));
			cCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			List<?> al = aBean.getList(aCriteria);
			List<?> cl = cBean.getList(cCriteria);
			HierarchyPayments payments = new HierarchyPayments( ((List<IContractPayment>) cl).iterator(), ((List<IContractPayment>) al).iterator());
			List<IContractPayment> list = new LinkedList<IContractPayment>();
			for (IContractPayment payment: payments) {
				list.add(payment);
			}
			paymentsModel = new SerializableListDataModel(list);
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
	
	private void searchSavedDraftSalary(){
		Contract contract = (Contract) this.getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), contract.getId());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_START_DATE), getStartDate());
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_END_DATE), getEndDate());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), getSalaryType());
			List<ITransferObject> list = bean.getList(criteria);
			if(list.isEmpty()){
				this.dbSalary = null;
			} 
			else {
				this.dbSalary = (ISalary) list.get(0);
			}
		} catch (ManagerBeanException e) {
			String msg = "Fallo en la obtención de la nómina calculada";
			AonUtil.addErrorMessage(msg);
		}
	}
	
	
	private void initCosts() {
		costs = new SortedSalaryItems<DeductionType>();
		
		Collection<SalaryCost> newSalaryItems = Collections.emptyList(); 
		
		if ( salary != null )  {
			newSalaryItems = ( ( Salary ) salary ).getSalaryCosts(); 
		}
		Collection<SalaryCost> oldSalaryItems = Collections.emptyList();
		if ( dbSalary != null )  {
			try {
				oldSalaryItems = getSalaryCosts( ( Salary ) dbSalary );
			} catch (ManagerBeanException e) {
				String msg = "Fallo en la obtención de las deducciones calculadas";
				AonUtil.addErrorMessage(msg);
			} 
		}
		
		costs.setItems(newSalaryItems, oldSalaryItems);
	}


	private void initBonuses() {
		bonuses = new SortedSalaryItems<BonusType>(BonusType.values());
		Collection<SalaryBonus> newSalaryItems = Collections.emptyList(); 
		
		if ( salary != null )  {
			newSalaryItems = ( ( Salary ) salary ).getSalaryBonus(); 
		}
		Collection<SalaryBonus> oldSalaryItems = Collections.emptyList();
		if ( dbSalary != null )  {
			try {
				oldSalaryItems = ( ( Salary ) dbSalary ).getBonus();
			} catch (SalaryException e) {
				String msg = "Fallo en la obtención de las bonificaciones calculadas";
				AonUtil.addErrorMessage(msg);
			} 
		}
		bonuses.setItems(newSalaryItems, oldSalaryItems);
	}

	private void initPayments() {
		payments = new SortedSalaryItems<PaymentType>(PaymentType.values());
		Collection<SalaryPayment> newSalaryItems = Collections.emptyList(); 
		
		if ( salary != null )  {
			newSalaryItems = ( ( Salary ) salary ).getSalaryPayments(); 
		}
		Collection<SalaryPayment> oldSalaryItems = Collections.emptyList();
		if ( dbSalary != null )  {
			try {
				oldSalaryItems = getSalaryPayments ( ( Salary ) dbSalary );
			} catch (ManagerBeanException e) {
				String msg = "Fallo en la obtención de las percepcions calculadas";
				AonUtil.addErrorMessage(msg);
			} 
		}
		payments.setItems(newSalaryItems, oldSalaryItems);
	}
	
	private void initDeductions() {
		deductions = new SortedSalaryItems<DeductionType>(DeductionType.values());
		
		Collection<SalaryDeduction> newSalaryItems = Collections.emptyList(); 
		
		if ( salary != null )  {
			newSalaryItems = new HashSet<SalaryDeduction>();
			newSalaryItems.addAll(( ( Salary ) salary ).getSalaryDeductions()); 
			newSalaryItems.addAll(getConvertedEmbarbos( ( ( Salary ) salary ).getSalaryEmbargos())); 
		}
		Collection<SalaryDeduction> oldSalaryItems = Collections.emptyList();
		if ( dbSalary != null )  {
			try {
				oldSalaryItems = getSalaryDeductions( ( Salary ) dbSalary );
			} catch (ManagerBeanException e) {
				String msg = "Fallo en la obtención de las deducciones calculadas";
				AonUtil.addErrorMessage(msg);
			} 
		}
		deductions.setItems(newSalaryItems, oldSalaryItems);
	}

	private Collection<SalaryPayment> getSalaryPayments(Salary salary) throws ManagerBeanException {
		String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
		// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
		// el FrameWork.
		if (  session.contains(salary)  || salary.getId() == null ) {
			return  salary.getSalaryPayments();
		} else {
			IManagerBean bean = BeanManager.getManagerBean(SalaryPayment.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_PAYMENT_SALARY_ID), salary.getId());
			List<?> list = bean.getList(c);
			return (Collection<SalaryPayment>) list;
		}
	}
	
	private Collection<SalaryDeduction> getSalaryDeductions(Salary salary) throws ManagerBeanException {
		String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
		// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
		// el FrameWork.
		if (  session.contains(salary)  || salary.getId() == null ) {
			return  salary.getSalaryDeductions();
		} else {
			IManagerBean bean = BeanManager.getManagerBean(SalaryDeduction.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DEDUCTION_SALARY_ID), salary.getId());
			List<SalaryDeduction> list = new LinkedList<SalaryDeduction>();
			for(ITransferObject to: bean.getList(c)){
				SalaryDeduction d = (SalaryDeduction) to;
				list.add( d );
			}
			list.addAll(getSalaryEmbargos(salary));
			return (Collection<SalaryDeduction>) list;
		}
	}
	
	private Collection<SalaryDeduction> getSalaryEmbargos (Salary salary) throws ManagerBeanException {
		String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
		// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
		// el FrameWork.
		List<?> embargosList ;
		if (  session.contains(salary)  || salary.getId() == null ) {
			embargosList = (List<SalaryEmbargo>) salary.getSalaryEmbargos();
		} else {
			IManagerBean bean = BeanManager.getManagerBean(SalaryEmbargo.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_EMBARGO_SALARY_ID), salary.getId());
			embargosList = bean.getList(c);
		}
		return getConvertedEmbarbos(embargosList) ;
		
	}
	
	private Collection<SalaryDeduction> getConvertedEmbarbos( Collection<?> embargosList ){
		List<SalaryDeduction> list = new LinkedList<SalaryDeduction>();
		for(Object o: embargosList){
			SalaryEmbargo e = (SalaryEmbargo) o;
			SalaryDeduction d = new SalaryDeduction();
			d.setDeductionConcept(e.getName());
			d.setDescription(null);
			d.setAmount(e.getAmount());
			d.setType(DeductionType.OTHER);
			list.add(d);
		}
		return list;
	}
	

	private Collection<SalaryCost> getSalaryCosts(Salary salary) throws ManagerBeanException {
		String sessionName = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		// Si el Salary está conectado a la session de Hibernate utilizamos la potencia
		// que nos da la obtención de colecciones tipo LAZY. En caso contrario vamos por 
		// el FrameWork.
		if (  session.contains(salary)  || salary.getId() == null ) {
			return  salary.getSalaryCosts();
		} else {
			IManagerBean bean = BeanManager.getManagerBean(SalaryCost.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_COST_SALARY_ID), salary.getId());
			List<?> list = bean.getList(c);
			return (Collection<SalaryCost>) list;
		}
	}

	// *********************************************************************
	// Plantilla de impresion de la nomina
	// *********************************************************************
	public String getSalaryTemplate(){
		try {
			EnterpriseData enterpriseData = getEnterpriseDataTemplate();
			if(enterpriseData == null || enterpriseData.getExpression() == null){
				ApplicationParameter appParam = getAppDefaultTemplate();
				if(appParam == null || appParam.getValue() == null){
					return IPayrollConstants.DEFAULT_SALARY_DRAFT_TEMPLATE;
				}
				return appParam.getValue();
			}
			return enterpriseData.getExpression();
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getSalaryTemplate ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private ApplicationParameter getAppDefaultTemplate() throws ManagerBeanException {
		IManagerBean dataBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(dataBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), ICompanyConstants.REPORT_SALARY_DRAFT_PARAM);
		List<ITransferObject> list = dataBean.getList(criteria);
		if(list.isEmpty()){
			return null;
		}
		return (ApplicationParameter) dataBean.getList(criteria).get(0);
	}

	private EnterpriseData getEnterpriseDataTemplate() throws ManagerBeanException {
		IManagerBean dataBean = BeanManager.getManagerBean(EnterpriseData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(dataBean.getFieldName(IEntityAlias.ENTERPRISE_DATA_ENTERPRISE_ID), ((Contract)getTo()).getWorkPlace().getEnterprise().getId());
		criteria.addEqualExpression(dataBean.getFieldName(IEntityAlias.ENTERPRISE_DATA_NAME), ICompanyConstants.REPORT_SALARY_DRAFT_PARAM);
		List<ITransferObject> list = dataBean.getList(criteria);
		if(list.isEmpty()){
			return null;
		}
		return (EnterpriseData) dataBean.getList(criteria).get(0);
	}
	
	
	
	
	public List<SelectItem> getSalaryDraftTypes() {
		return salaryDraftTypes;
	}
	
	private void initDraftTypes () {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		
		List<SalaryType> availableTypes = 
			new LinkedList<SalaryType>();
		
		salaryDraftTypes = new LinkedList<SelectItem>();
		for ( SalaryType salaryType: SalaryType.values() ) {
			
			List<Month> availableMonths  = 
				getAvailableMonths(salaryType, this.year);
			if ( ! availableMonths.isEmpty() ) {
				availableTypes.add(salaryType);
				SelectItem selectItem = new SelectItem(salaryType, 
						salaryType.getName(locale));
				salaryDraftTypes.add( selectItem );
			}
		}
		
		//TODO: What happens if no available types ?.
		
		// fix type, if it's out of range 
		if ( ! availableTypes.contains(this.salaryType) ){
			this.salaryType = availableTypes.get(0);
		}
		
		
	}
	
	
	private List<Month> getAvailableMonths(SalaryType salaryType, final int year) {
		List<Month> availableMonths ;

		availableMonths = 
			salaryType.accept(new SalaryTypeVisitor<List<Month>>() {
				@Override
				public List<Month> visitSalary(SalaryType salaryType) {
					return getSalaryMonths(year);
				}
				@Override
				public List<Month> visitDelay(SalaryType salaryType) {
					return getDelayMonths(year);
				}
				@Override
				public List<Month> visitExtra(SalaryType salaryType) {
					return getExtraMonths(year);
				}
				@Override
				public List<Month> visitSettle(SalaryType salaryType) {
					return getSettleMonths(year);
				}
		});
		return availableMonths;
	}

	private void rebuildDraftMonths(){
		
		// fix years, if it's out of range
		this.year = Math.max(this.year, getMinYear());
		this.year = Math.min(this.year, getMaxYear());
		
		this.toYear = Math.min(this.toYear, getMaxYear());
		this.toYear = Math.max(this.toYear, this.year); 	// toYear >= year
		

		SalaryType salaryType = getSalaryType();

		List<Month> availableMonths  = 
			getAvailableMonths(salaryType, this.year);
		
		Collections.sort(availableMonths);
		
		// fix month, if it's out of range 
		this.month = getClosestMonth(availableMonths, this.month);
		
		this.draftMonths = createSelectItemList(availableMonths);
		
	}
	
	private void rebuildToDraftMonths(){
		
		// fix years, if it's out of range
		this.toYear = Math.min(this.toYear, getMaxYear());
		this.toYear = Math.max(this.toYear, this.year); 	// toYear >= year
		
		
		SalaryType salaryType = getSalaryType();

		List<Month> availableMonths  = 
			getAvailableMonths(salaryType, this.toYear);
		
		Collections.sort(availableMonths);
		
		// fix month, if it's out of range 
		
		this.toMonth = getClosestMonth(availableMonths, this.toMonth );
		if ( this.toMonth == null ) {
			this.toMonth = this.month; 
		}
		
		this.toDraftMonths = createSelectItemList(availableMonths);
	}
	
	
	
	private List<Month> getSettleMonths(int year) {
		Contract contract = (Contract) this.getTo();
		Date contractEnd = contract.getEndDate();
		
		List<Month> months = new LinkedList<Month>();

		if ( contractEnd != null ) {
			int value = CommonUtil.getMonth(contractEnd);
			months.add(Month.getMonthByValue(value));
		}
		
		return months;
	}
	private List<Month> getExtraMonths(int year) {
		Contract contract = (Contract) this.getTo();

		List<Month> months = new LinkedList<Month>();
		try {
			Criteria criteria = new Criteria();
			AgreementLevel level = contract.getAgreementLevel();
			Agreement agreement = level.getAgreement(); 

			IManagerBean bean = BeanManager.getManagerBean(AgreementExtra.class);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_EXTRA_AGREEMENT_ID), 
					agreement.getId());
			
			for(ITransferObject to: bean.getList(criteria)){
				AgreementExtra extra = (AgreementExtra) to;
				Date extraStart = extra.getStartDate(year);
				Date extraEnd = extra.getEndDate(year);
				if ( contract.isActive(extraStart, extraEnd))
				{
					Date extraDate = extra.getIssueDate(year);
					int extraMonth = CommonUtil.getMonth( extraDate ); 
					months.add( Month.getMonthByValue(extraMonth));
				}
			}
			
			bean = BeanManager.getManagerBean(Salary.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), 
					contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), 
					SalaryType.EXTRA);
			criteria.addGreaterThanExpression(bean.getFieldName(IEntityAlias.SALARY_ISSUE_DATE), 
					CommonUtil.getYearFirstDay(year));
			criteria.addLessThanExpression(bean.getFieldName(IEntityAlias.SALARY_ISSUE_DATE), 
					CommonUtil.getYearLastDay(year));
			
			
			for(ITransferObject to: bean.getList(criteria)){
				Salary extra = ( Salary ) to;
				int extraMonthValue = CommonUtil.getMonth( extra.getIssueDate() );
				Month extraMonth = Month.getMonthByValue(extraMonthValue);
				if ( !months.contains(extraMonth )) {
					months.add( extraMonth );
				}
			}
			

		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getExtraMonths exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return months;
	}
	
	private List<Month> getDelayMonths(int year ) {
		return getSalaryMonths(year); //Collections.emptyList();
	}
	
	
	private List<Month> getSalaryMonths(int year) {
		Contract contract = (Contract) this.getTo();
		Date contractStart = contract.getStartDate();
		Date contractEnd = contract.getEndDate();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
		Date draftStart = calendar.getTime(); 

		calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
		Date draftEnd = calendar.getTime(); 
		
		Date start = contractStart.after(draftStart) ? contractStart : draftStart;
		Date end = contractEnd != null && contractEnd.before(draftEnd) ? contractEnd : draftEnd;
		
		List<Month> months = new LinkedList<Month>();
		for ( int month = CommonUtil.getMonth(start);
			month <= CommonUtil.getMonth(end); 
			month++ )
		{
			months.add(Month.getMonthByValue(month));
		}
		return months;
	}

	public void onSaveSalary(ActionEvent event) throws ManagerBeanException{
		
		try {
			saveSalary();
			reset();
		} catch (Throwable e) {
			LOGGER.error(">>>> onSaveSalary exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void saveSalary() throws ManagerBeanException {
		BeanManager.getManagerBean(Salary.class).insertOrUpdate(( Salary ) salary);
	}

	private Session getHibernateSession4Class(Class clazz) {
		String sessionName = HibernateUtil.getSessionFactoryName(clazz.getName());
		return HibernateUtil.getSession(sessionName);
	}
	
	public void onUpdateSalary(ActionEvent event) throws ManagerBeanException, SalaryException{
		deleteSalary();
		saveSalary();
		reset();
	}
	
	public void onDeleteSalary(ActionEvent event){
		deleteSalary();
		reset();
	}
	
	private void deleteSalary(){
		
	}
	
	
	private static Locale getLocale (){
		return FacesContext.getCurrentInstance().getViewRoot().getLocale();
	}

	private static Map<String, Pair<String, Method>> WARNING_ACTIONS = 
			new HashMap<String, Pair<String, Method>>(){
		{
			try {
				put(AGE.getName(), 
					new Pair<String, Method>(IPayrollConstants.PERSON_FORM, 
							getActionListenerMethod("onPersonQuickFix")));
			} catch ( Exception e ) {}
		}
	};


	
	private static Method getActionListenerMethod(String name ) 
			throws SecurityException, NoSuchMethodException{
		return SalaryDraftController.class.getMethod(name, ActionEvent.class);
	}
	
	
	private static Month getClosestMonth(List<Month> months, Month month ) {
		if ( month == null ) {
			return null;
		}
		if ( months.contains(month) ){
			return month;
		}

		if ( months.isEmpty() ) {
			return null;
		}

		int index = Collections.binarySearch(months, month);
		int insertIndex = -( index + 1);
		return months.get(Math.min(insertIndex, months.size()-1));
	}
	
	private static List<SelectItem> createSelectItemList(List<Month> months ) {
		Locale locale = getLocale();
		List<SelectItem> selectItems = new LinkedList<SelectItem>();
		for (Month month : months) {
			selectItems.add(new SelectItem(month, month.getName(locale)));
		}
		return selectItems;
	}

	@Override
	public void onCompileError(String variableName, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompileError(IContractPayment payment, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUndefinedData(IContractPayment payment,
			RemovedExpressionVariable<?> var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUndefinedData(IContractPayment payment, String variableName,
			String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompileError(IContractDeduction deduction, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUndefinedData(IContractDeduction deduction,
			RemovedExpressionVariable<?> var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUndefinedData(IContractDeduction deduction,
			String variableName, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompileError(IContractBonus bonus, String message) {
		// TODO Auto-generated method stub
		
	}
}
