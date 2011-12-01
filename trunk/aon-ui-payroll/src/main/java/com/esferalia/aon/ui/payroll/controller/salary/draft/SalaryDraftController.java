package com.esferalia.aon.ui.payroll.controller.salary.draft;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.EnterpriseData;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelCategory;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryEmbargo;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.HierarchyPayments;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.calculator.OutOfDateException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.SalarySupplements;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.launcher.SalaryLauncher;
import com.esferalia.aon.ui.payroll.controller.launcher.SalaryLauncherParams;
import com.esferalia.aon.ui.payroll.controller.launcher.SalaryRemoverController;
import com.esferalia.aon.ui.payroll.controller.salary.SortedSalaryItems;

public class SalaryDraftController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryDraftController.class.getName());

	private int 								year;		
	private Month 								month;		 
	private SalaryType 							salaryType = SalaryType.SALARY ;
	private Date								startDate;
	private Date								endDate;
	
	
	private ISalary 							salary;				// online salary
	private ISalary 							dbSalary;			// saved ( database ) salary
	
	private SortedSalaryItems<PaymentType>		payments;
	private SortedSalaryItems<DeductionType>	deductions;
	private SortedSalaryItems<DeductionType>	costs;
	private SortedSalaryItems<BonusType>	bonuses;
	
	private List<SelectItem> 					draftMonths;		
	private List<SelectItem> 					salaryDraftTypes;	

	private DataModel 							paymentsModel;
//	private DataModel 							deductionsModel;
	
	public List<SelectItem> getDraftMonths() {
		return draftMonths;
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
	
	public SalaryType getSalaryType() {
		return salaryType ;
	}
	public void setSalaryType(SalaryType salaryType) {
		this.salaryType = salaryType;
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
	
	public void onShowBonus( ActionEvent event ) {
		try {
			SalaryDraftBonusController c = (SalaryDraftBonusController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_BONUS_CONTROLLER);
			c.reset(false);
			c.onEditSearch(event);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(c.getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE), getStartDate());
			Expression expr2 = ExpressionUtilities.getNullExpression(c.getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE));
			c.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			c.onSearch(event);
			c.getModel();
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar las bonificaciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
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
	
	// ------------------------------------------
	// Gets ( calculate ) salary draft.
	// ------------------------------------------
	private ISalary calculateSalary() {
		try {
			Contract contract = (Contract) getTo();

			ISalaryCalculatorContext ctx;
			if ( getSalaryType() == SalaryType.DELAY )
				ctx = contract.getSalaryCalculatorContext(
						startDate,
						endDate,
						getSalaryType());
			else 
				ctx = contract.getSalaryCalculatorContext(
						getYear(),
						getMonth(),
						getSalaryType());
			
			salary = ctx.getSalaryProxy().getSalary();
			
			( ( Salary ) salary).setContract(contract);
			
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
			aCriteria.addEqualExpression(aBean.getFieldName(IPayrollAlias.AGREEMENT_PAYMENT_AGREEMENT_ID), contract.getAgreementLevelCategory().getLevel().getAgreement().getId());
			aCriteria.addOrder(aBean.getFieldName(IPayrollAlias.AGREEMENT_PAYMENT_START_DATE), false);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(aBean.getFieldName(IPayrollAlias.AGREEMENT_PAYMENT_END_DATE), new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(aBean.getFieldName(IPayrollAlias.AGREEMENT_PAYMENT_END_DATE));
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
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_ID), contract.getId());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_START_DATE), getStartDate());
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_END_DATE), getEndDate());
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_TYPE), getSalaryType());
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
			newSalaryItems = ( ( Salary ) salary ).getSalaryDeductions(); 
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
			c.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_PAYMENT_SALARY_ID), salary.getId());
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
			c.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_DEDUCTION_SALARY_ID), salary.getId());
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
			c.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_EMBARGO_SALARY_ID), salary.getId());
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
			d.setDescription(e.getDescription());
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
			c.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_COST_SALARY_ID), salary.getId());
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
		criteria.addEqualExpression(dataBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), ICompanyConstants.REPORT_SALARY_DRAFT_PARAM);
		List<ITransferObject> list = dataBean.getList(criteria);
		if(list.isEmpty()){
			return null;
		}
		return (ApplicationParameter) dataBean.getList(criteria).get(0);
	}

	private EnterpriseData getEnterpriseDataTemplate() throws ManagerBeanException {
		IManagerBean dataBean = BeanManager.getManagerBean(EnterpriseData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(dataBean.getFieldName(ICompanyAlias.ENTERPRISE_DATA_ENTERPRISE_ID), ((Contract)getTo()).getWorkPlace().getEnterprise().getId());
		criteria.addEqualExpression(dataBean.getFieldName(ICompanyAlias.ENTERPRISE_DATA_NAME), ICompanyConstants.REPORT_SALARY_DRAFT_PARAM);
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
				getAvailableMonths(salaryType);
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
	
	private List<Month> getAvailableMonths(SalaryType salaryType) {
		List<Month> availableMonths ;

		availableMonths = 
			salaryType.accept(new SalaryTypeVisitor<List<Month>>() {
				@Override
				public List<Month> visitSalary(SalaryType salaryType) {
					return getSalaryMonths();
				}
				@Override
				public List<Month> visitDelay(SalaryType salaryType) {
					return getDelayMonths();
				}
				@Override
				public List<Month> visitExtra(SalaryType salaryType) {
					return getExtraMonths();
				}
				@Override
				public List<Month> visitSettle(SalaryType salaryType) {
					return getSettleMonths();
				}
				@Override
				public List<Month> visitNotEnjoyedVacations(
						SalaryType salaryType) {
					return Collections.emptyList();
				}
		});
		return availableMonths;
	}
	
	private void rebuildDraftMonths(){
		
		// fix year, if it's out of range
		this.year = Math.max(this.year, getMinYear());
		this.year = Math.min(this.year, getMaxYear());
		

		SalaryType salaryType = getSalaryType();

		List<Month> availableMonths  = 
			getAvailableMonths(salaryType);
		
		Collections.sort(availableMonths);
		
		// fix month, if it's out of range 
		if ( !availableMonths.contains(month) ){
			if ( availableMonths.isEmpty() ) {
				this.month = null;
			}
			else {
				int index = Collections.binarySearch(availableMonths, month);
				int insertIndex = -( index + 1);
				this.month = availableMonths.get(Math.min(insertIndex, availableMonths.size()-1));
			}
		}
		
		
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		draftMonths = new LinkedList<SelectItem>();
		for (Month month : availableMonths) {
			draftMonths.add(new SelectItem(month, month.getName(locale)));
		}
	}
	
	
	private List<Month> getSettleMonths() {
		Contract contract = (Contract) this.getTo();
		Date contractEnd = contract.getEndDate();
		
		List<Month> months = new LinkedList<Month>();

		if ( contractEnd != null ) {
			int value = CommonUtil.getMonth(contractEnd);
			months.add(Month.getMonthByValue(value));
		}
		
		return months;
	}
	private List<Month> getExtraMonths() {
		Contract contract = (Contract) this.getTo();

		List<Month> months = new LinkedList<Month>();
		try {
			Criteria criteria = new Criteria();
			AgreementLevelCategory category = contract.getAgreementLevelCategory();
			AgreementLevel level = category.getLevel();
			Agreement agreement = level.getAgreement(); 

			IManagerBean bean = BeanManager.getManagerBean(AgreementExtra.class);
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_EXTRA_AGREEMENT_ID), 
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

		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getExtraMonths exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return months;
	}
	
	private List<Month> getDelayMonths() {
		return getSalaryMonths(); //Collections.emptyList();
	}
	private List<Month> getSalaryMonths() {
		Contract contract = (Contract) this.getTo();
		Date contractStart = contract.getStartDate();
		Date contractEnd = contract.getEndDate();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, this.year);
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
	
	private void saveSalary() throws SalaryException {
		Contract contract = (Contract) this.getTo();
		SalaryLauncherParams params = new SalaryLauncherParams();
		params.setStartDate(getStartDate());
		params.setEndDate(getEndDate());
		params.setIssueMonth(getMonth());
		params.setIssueYear(getYear());
		params.setSalaryType(getSalaryType());
		params.setPerson(contract.getPerson());
		SalaryLauncher launcher = (SalaryLauncher) AonUtil.getRegisteredBean(IPayrollConstants.SALARY_LAUNCHER_CONTROLLER);
		launcher.saveSalary(params);
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
		SalaryRemoverController controller = (SalaryRemoverController) AonUtil.getRegisteredBean(IPayrollConstants.SALARY_REMOVER_CONTROLLER);
		controller.setSelectedSalaries(new ArrayList<Salary>());
		controller.getSelectedSalaries().add((Salary)getBdSalary());
		controller.removeSelected();
	}

}
