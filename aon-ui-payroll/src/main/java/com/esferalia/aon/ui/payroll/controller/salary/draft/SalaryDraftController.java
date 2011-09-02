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
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.HierarchyPayments;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.calculator.OutOfDateException;
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
import com.esferalia.aon.ui.payroll.event.salary.draft.SalaryDraftComparatorPrinter;

public class SalaryDraftController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryDraftController.class.getName());

	private int 								year;		
	private Month 								month;		 
	private SalaryType 							salaryType = SalaryType.SALARY ; 
	
	
	private ISalary 							salary;				// online salary
	private ISalary 							dbSalary;			// saved ( database ) salary
	
	private SortedSalaryItems<PaymentType>		payments;
	
	private List<SelectItem> 					draftMonths;		
	private List<SelectItem> 					salaryDraftTypes;	

	private DataModel 							paymentsModel;
//	private DataModel 							deductionsModel;
	private SalaryDraftComparatorPrinter 		printer;
	
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

	public Date getEndDate() {
		return salary != null ? salary.getEndDate() : null;
	}
	
	public int getMinYear() {
		if ( this.salaryType  == SalaryType.SETTLE ) {
			// current year...
			return Calendar.getInstance().get(Calendar.YEAR);
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
			ctx = contract.getSalaryCalculatorContext(
					getYear(),
					getMonth(),
					getSalaryType());
			
			salary = ctx.getSalaryProxy().getSalary();
			
			paymentsModel = null;
			printer = null;
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
		rebuildDraftMonths();
		calculateSalary();
		searchSavedDraftSalary();
		initPayments();
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
	
	public SortedSalaryItems<PaymentType> getSortedSalaryPayments() {
		return payments;
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
	
	public SalaryDraftComparatorPrinter getPrinter() {
		if(printer==null){
			Contract contract = (Contract) getTo();
			Date startDate = getStartDate().before(contract.getStartDate())?contract.getStartDate():getStartDate(); 
			Date endDate = (contract.getEndDate() != null && getEndDate().after(contract.getEndDate()))?contract.getEndDate():getEndDate(); 
			Date issueDate = getIssueDate(); 
			printer = new SalaryDraftComparatorPrinter(contract, getBdSalary(), startDate, endDate, issueDate);
		}
		return printer;
	}
	public void setPrinter(SalaryDraftComparatorPrinter printer) {
		this.printer = printer;
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
		payments.setPayments(newSalaryItems, oldSalaryItems);
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
		Contract contract = (Contract) this.getTo();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		salaryDraftTypes = new LinkedList<SelectItem>();
		SalaryType types [] = {SalaryType.SALARY, 
					SalaryType.EXTRA, 
					SalaryType.DELAY,
					SalaryType.SETTLE};
		for( SalaryType salaryType : types ) {
				String name = salaryType.getName(locale);
				SelectItem item = new SelectItem(salaryType, name);
				salaryDraftTypes.add(item);			
		}
		return salaryDraftTypes;
	}
	
	private void rebuildDraftMonths(){
		
		// fix year, if it's out of range
		this.year = Math.max(this.year, getMinYear());
		this.year = Math.min(this.year, getMaxYear());
		
		List<Month> availableMonths ;

		SalaryType salaryType = getSalaryType();
		
		availableMonths = 
			salaryType.accept(new SalaryTypeVisitor<List<Month>>() {
				@Override
				public List<Month> visitSalary(SalaryType salaryType) {
					return getSalaryMonths();
				}
				@Override
				public List<Month> visitDelay(SalaryType salaryType) {
					return getSalaryMonths();
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
					AonUtil.addErrorMessage("no implementado");
					return Collections.emptyList();
				}
		});
		
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
		Date contractStart = contract.getStartDate();
		Date contractEnd = contract.getEndDate();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, this.year);
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
	
	public void onSaveSalary(ActionEvent event){
		Contract contract = (Contract) this.getTo();
		SalaryLauncherParams params = new SalaryLauncherParams();
		params.setStartDate(getStartDate());
		params.setEndDate(getEndDate());
		params.setIssueMonth(getMonth());
		params.setIssueYear(getYear());
		params.setSalaryType(getSalaryType());
		params.setPerson(contract.getPerson());
		SalaryLauncher launcher = (SalaryLauncher) AonUtil.getRegisteredBean(IPayrollConstants.SALARY_LAUNCHER_CONTROLLER);
		try {
			launcher.saveSalary(params);
			searchSavedDraftSalary();
		} catch (Throwable e) {
			LOGGER.error(">>>> onSaveSalary exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onUpdateSalary(ActionEvent event){
		onDeleteSalary(event);
		onSaveSalary(event);
	}
	
	public void onDeleteSalary(ActionEvent event){
		SalaryRemoverController controller = (SalaryRemoverController) AonUtil.getRegisteredBean(IPayrollConstants.SALARY_REMOVER_CONTROLLER);
		controller.setSelectedSalaries(new ArrayList<Salary>());
		controller.getSelectedSalaries().add((Salary)getBdSalary());
		controller.removeSelected();
	}
	
}
