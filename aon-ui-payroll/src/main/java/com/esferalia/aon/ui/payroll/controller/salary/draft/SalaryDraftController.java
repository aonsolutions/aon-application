package com.esferalia.aon.ui.payroll.controller.salary.draft;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
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
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.AgreementPayment;
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
import com.esferalia.aon.ui.payroll.controller.launcher.SalaryLauncher;
import com.esferalia.aon.ui.payroll.controller.launcher.SalaryLauncherParams;
import com.esferalia.aon.ui.payroll.controller.launcher.SalaryRemoverController;
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
//	private DataModel deductionsModel;
	
	private boolean showSalaryDifference;
	private ISalary bdSalary;
	private SalaryDraftComparatorPrinter printer;
	private SalaryType salaryType;
	private boolean validSalaryDraftPeriod;
	private List<SelectItem> draftMonths;
	private List<SelectItem> salaryDraftTypes;
	
	public List<SelectItem> getDraftMonths() {
		return draftMonths;
	}
	
	public boolean isValidSalaryDraftPeriod() {
		return validSalaryDraftPeriod;
	}
	public void setValidSalaryDraftPeriod(boolean validSalaryDraftPeriod) {
		this.validSalaryDraftPeriod = validSalaryDraftPeriod;
	}
	
	public SalaryType getSalaryType() {
		return salaryType;
	}
	public void setSalaryType(SalaryType salaryType) {
		this.salaryType = salaryType;
		setSalary(null);
	}
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
		//setStartDate(CommonUtil.getMonthFirstDay(issueDate));
		//setEndDate(CommonUtil.getMonthLastDay(issueDate));
		setMonth(Month.getMonthByValue(CommonUtil.getMonth(issueDate)));
		setYear(CommonUtil.getYear(issueDate));
	}

	public Date getStartDate() {
		if (startDate == null) {
			return CommonUtil.getMonthFirstDay(getIssueDate());
		}
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		if (endDate == null) {
			return CommonUtil.getMonthLastDay(getIssueDate());
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
				Date startDate = getStartDate(); 
				Date endDate = getEndDate();
				Date issueDate = getIssueDate(); 
				ISalaryCalculatorContext ctx;
				ctx = contract.getSalaryCalculatorContext(startDate,endDate,issueDate, getSalaryType());
				salary = ctx.getSalaryProxy().getSalary();
				paymentsModel = null;
				printer = null;
				paymentsList = null;
				initializePaymentModel();
			}
			return salary;
		} catch (SalaryException e) {
			String msg = "Error en el calculo del borrador de la nómina";
			setValidSalaryDraftPeriod(false);
			setSalary(null);
			LOGGER.error(msg, e);
			return null;
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
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_TYPE), getSalaryType());
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

	
	// *********************************************************************
	// *********************************************************************
	// Filtro del borrador por tipo, mes y anio
	// *********************************************************************
	// *********************************************************************
	
	public void onReloadDraft(ActionEvent event) {
		try {
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireAfterBeanSelected(evt);
		} catch (ControllerListenerException e) {
			throw new AbortProcessingException("Imposible mostrar la simulación de la nómina");
		}
	}
	
	public void checkValidDraftPeriod() {
		Contract contract = (Contract) this.getTo();
		Date draftStart = CommonUtil.getDate(CommonUtil.getYear(getStartDate()), CommonUtil.getMonth(getStartDate()),CommonUtil.getDay(getStartDate()));
		Date draftEnd = CommonUtil.getDate(CommonUtil.getYear(getEndDate()), CommonUtil.getMonth(getEndDate()),CommonUtil.getDay(getEndDate()));
		Date contractStart = CommonUtil.getDate(CommonUtil.getYear(contract.getStartDate()), CommonUtil.getMonth(contract.getStartDate()),CommonUtil.getDay(contract.getStartDate()));
		Date contractEnd = contract.getEndDate()==null?null:CommonUtil.getDate(CommonUtil.getYear(contract.getEndDate()), CommonUtil.getMonth(contract.getEndDate()),CommonUtil.getDay(contract.getEndDate()));

		rebuildDraftMonths();
		
		boolean isValid	= ( contract != null ) && 
			( draftStart.before(draftEnd) || draftStart.equals(draftEnd) ) && 
			( draftStart.before(draftEnd) || draftStart.equals(draftEnd) ) && 
			( contractStart.before(draftEnd) || contractStart.equals(draftEnd) ) &&
			( contractEnd == null ||  ( contractEnd.after(draftStart) || contractEnd.equals(draftStart) ) ) &&
			!draftMonths.isEmpty()
			;
		
		if(getSalary()==null){
			isValid = false;
		}
		setValidSalaryDraftPeriod(isValid);
	}
	
	public List<SelectItem> getSalaryDraftTypes() {
		Contract contract = (Contract) this.getTo();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		salaryDraftTypes = new LinkedList<SelectItem>();
		for( SalaryType salaryType : SalaryType.values() ) {
			if(salaryType==SalaryType.SETTLE){
				if(contract.getEndDate()!=null){
					String name = salaryType.getName(locale);
					SelectItem item = new SelectItem(salaryType, name);
					salaryDraftTypes.add(item);			
				}
			}else if(salaryType!=SalaryType.NOT_ENJOYED_VACATIONS){
				String name = salaryType.getName(locale);
				SelectItem item = new SelectItem(salaryType, name);
				salaryDraftTypes.add(item);			
			}
		}
		return salaryDraftTypes;
	}
	
	public void rebuildDraftMonths(){
		draftMonths = new LinkedList<SelectItem>();
		if(getSalaryType()==SalaryType.SALARY){
			draftMonths.addAll(getSalaryMonths());
		} else if(getSalaryType()==SalaryType.EXTRA){
			draftMonths.addAll(getExtraMonths());
		} else if(getSalaryType()==SalaryType.SETTLE){
			draftMonths.addAll(getSettleMonths());
		} else if(getSalaryType()==SalaryType.DELAY){
			draftMonths.addAll(getDelayMonths());
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(getIssueDate());
		c.set(Calendar.YEAR, getYear());
		c.set(Calendar.MONTH, getMonth().getValue());
		setIssueDate(c.getTime());
	}
	
	private List<SelectItem> getDelayMonths() {
		// TODO Auto-generated method stub
		AonUtil.addErrorMessage("no implementado");
		setValidSalaryDraftPeriod(false);
		return new LinkedList<SelectItem>();
	}
	private List<SelectItem> getSettleMonths() {
		try {
			Contract contract = (Contract) this.getTo();
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_TYPE), SalaryType.SETTLE);
			List<ITransferObject> salaries = bean.getList(criteria);
			if(salaries.isEmpty()){
				return new LinkedList<SelectItem>();
			} else {
				List<SelectItem> list = new LinkedList<SelectItem>();				
				Salary settle = (Salary) salaries.get(0);
				Month m = Month.getMonthByValue(settle.getIssueMonth()-1);
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				String name = m.getName(locale);
				SelectItem item = new SelectItem(m, name);
				list.add(item);
				setYear(settle.getIssueYear());
				setMonth(Month.getMonthByValue(settle.getIssueMonth()-1));
				return list;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getSettleMonths exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	private List<SelectItem> getExtraMonths() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		List<Month> months = new LinkedList<Month>();
		Contract contract = (Contract) this.getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(AgreementExtra.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_EXTRA_AGREEMENT_ID), contract.getAgreementLevelCategory().getLevel().getAgreement().getId());
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			for(ITransferObject to: bean.getList(criteria)){
				AgreementExtra ae = (AgreementExtra) to;
				if(ae.getAgreementPayment().getSalaryType()==SalaryType.EXTRA){
					Date agreementStart = ae.getAgreementPayment().getStartDate();
					Date agreementEnd = ae.getAgreementPayment().getEndDate();
					Month month = ae.getAgreementPayment().getMonth();
					if(CommonUtil.getYear(agreementStart)<=getYear()
							&& ( agreementEnd==null || CommonUtil.getYear(agreementEnd)>=getYear() )
							&& ( CommonUtil.getMonth(contract.getStartDate())<=month.ordinal() ) 
							&& ( contract.getEndDate() == null || CommonUtil.getMonth(contract.getEndDate())>=month.ordinal() ) ){
						months.add(month);
					}
				}
			}
			for(Month m: months){
				String name = m.getName(locale);
				SelectItem item = new SelectItem(m, name);
				list.add(item);
			}
			if(!months.isEmpty() && !months.contains(getMonth())){
				setMonth(months.get(months.size()-1));
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getExtraMonths exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return list;
	}
	private List<SelectItem> getSalaryMonths() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		Contract contract = (Contract) this.getTo();
		int maxMonth=11;
		int skipMonth=0;
		if(contract.getEndDate()!=null){
			Date contractStart = contract.getStartDate();
			Date contractEnd = contract.getEndDate();
			if( CommonUtil.getYear(contractEnd) < getYear()
					&& CommonUtil.getYear(contractStart) > getYear() ){
				maxMonth=11;
			}else if( CommonUtil.getYear(contractEnd)==getYear() ){
				maxMonth = CommonUtil.getMonth(contractEnd);
				if(getMonth().ordinal()>CommonUtil.getMonth(contractEnd)){
					setMonth(Month.getMonthByValue(CommonUtil.getMonth(contractEnd)));
				} else if(getMonth().ordinal()<CommonUtil.getMonth(contractStart)){
					setMonth(Month.getMonthByValue(CommonUtil.getMonth(contractStart)));
				}
			}else{
				maxMonth=-1;
			}
			if( CommonUtil.getYear(contractStart) == getYear() ){
				skipMonth = CommonUtil.getMonth(contractStart);
			}
		} 
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for(int i=0; i<=maxMonth; i++){
			if(skipMonth>0){
				skipMonth--;
			}else{
				String name = Month.getMonthByValue(i).getName(locale);
				SelectItem item = new SelectItem(Month.getMonthByValue(i), name);
				list.add(item);
			}
		}
		return list;
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
			setSalary(null);
			setShowSalaryDifference(true);
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
		setShowSalaryDifference(false);
	}
	
}
