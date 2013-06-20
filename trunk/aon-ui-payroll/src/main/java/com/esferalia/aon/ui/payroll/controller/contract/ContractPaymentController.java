package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.SystemPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.TaxationType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.ui.payroll.controller.IPaymentHandler;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class ContractPaymentController extends ContractDetailVariableController implements IPaymentHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractPaymentController.class.getName());
	
	private boolean quoteExpressionEdition;
	private boolean irpfExpressionEdition;
	private boolean enableExpressionEdition;
	private TaxationType taxation;
	private QuoteType quote;
	

	
	private DataModel paymentsModel;
	
	public DataModel getPaymentsModel() {
		if (paymentsModel == null) {
			initializePaymentModel();
		}
		return paymentsModel;
	}
	public void setPaymentsModel(DataModel paymentsModel) {
		this.paymentsModel = paymentsModel;
	}
	
	public boolean isReadOnly(){
		if(this.getPaymentsModel().isRowAvailable() && this.getPaymentsModel().getRowCount()>0){
			return ((IContractPayment)this.getPaymentsModel().getRowData()).getScope()!=ExpressionScope.CONTRACT;
		}
		return true;
	}

	public boolean isEnableExpressionEdition() {
		return enableExpressionEdition;
	}
	public void setEnableExpressionEdition(boolean enableExpressionEdition) {
		this.enableExpressionEdition = enableExpressionEdition;
	}
	public boolean isQuoteExpressionEdition() {
		return quoteExpressionEdition;
	}
	public void setQuoteExpressionEdition(boolean quoteExpressionEdition) {
		this.quoteExpressionEdition = quoteExpressionEdition;
	}
	public boolean isIrpfExpressionEdition() {
		return irpfExpressionEdition;
	}
	public void setIrpfExpressionEdition(boolean irpfExpressionEdition) {
		this.irpfExpressionEdition = irpfExpressionEdition;
	}
	@Override
	public TaxationType getTaxation() {
		if(taxation==null){
			taxation = obtainTaxationType();
		}
		return taxation;
	}

	public void setTaxation(TaxationType taxation) {
		this.taxation = taxation;
		changeIrpfExpression();
	}

	@Override
	public QuoteType getQuote() {
		if(quote==null){
			quote = obtainQuoteType();
		}
		return quote;
	}

	public void setQuote(QuoteType quote) {
		this.quote = quote;
		changeQuoteExpression();
	}
	
	private void changeQuoteExpression() {
		if(quote==QuoteType.QUOTE){
			this.setQuoteExpression(this.getExpression());
		}else if(quote==QuoteType.NO_QUOTE){
			this.setQuoteExpression("0");
		}else if(quote==QuoteType.IPREM_EXCESS){
			this.setQuoteExpression(IPayrollConstants.IPREM_FORMMULA);
		} else {
			this.setQuoteExpression(null);
		}
	}

	private void changeIrpfExpression() {
		if(taxation==TaxationType.TAXED){
			this.setIrpfExpression(this.getExpression());
		}else if(taxation==TaxationType.NO_TAXED){
			this.setIrpfExpression(IPayrollConstants.ZERO_VALUE);
		} else {
			this.setIrpfExpression(null);
		}
	}
	
	private QuoteType obtainQuoteType() {
		if(this.getQuoteExpression()==null) {
			return null;
		} else if(this.getQuoteExpression().equals(this.getExpression())){
			return QuoteType.QUOTE;
		} else if(this.getQuoteExpression().equals(IPayrollConstants.ZERO_VALUE)){
			return QuoteType.NO_QUOTE;
		} else if(this.getQuoteExpression().equals(IPayrollConstants.IPREM_FORMMULA)){
			return QuoteType.IPREM_EXCESS;
		} else {
			return QuoteType.MANUAL;
		}
	}
	
	private TaxationType obtainTaxationType() {
		if(this.getIrpfExpression()==null){
			return null;
		} else if(this.getIrpfExpression().equals(this.getExpression())){
			return TaxationType.TAXED;
		} else if(this.getIrpfExpression().equals(IPayrollConstants.ZERO_VALUE)){
			return TaxationType.NO_TAXED;
		} else {
			return TaxationType.MANUAL;
		}
	}

	
	@Override
	public void onSelectExpressionEdition(ActionEvent event){
		setEnableExpressionEdition( !isEnableExpressionEdition() );
	}
	
	
	public void initialize(){
		setPaymentsModel(null);
	}
	public void initialize(ActionEvent event) {
		initialize();
	}
	
	@Override
	public void onSave(ActionEvent event) {
		IController master = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		Contract contract = (Contract) master.getTo();
		ContractPayment cp  = (ContractPayment) getTo();
		cp.setContract(contract);
		this.setInactiveDate(cp.getStartDate());
		super.onSave(event);
		initializePaymentModel();
	}
	
	@Override
	protected void initialiceConcepts() {
		setConcepts(new LinkedList<SelectItem>());
		try {
			ContractPayment cp = (ContractPayment) getTo();
			IManagerBean bean = BeanManager.getManagerBean(PaymentConcept.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYMENT_CONCEPT_TYPE), cp.getType());
			criteria.addOrder(bean.getFieldName(IEntityAlias.PAYMENT_CONCEPT_CODE));
			List<ITransferObject> list = bean.getList(criteria);
			for (ITransferObject to: list) {
				PaymentConcept pc = (PaymentConcept) to;
				getConcepts().add(new SelectItem(pc, pc.getCode() + " - "+pc.getDescription()));
			}
		} catch (ManagerBeanException e) {
			// Se devuelve la lista vacia.
		} 
	}
	
	public void onPaymentConceptChange(ActionEvent event) {
		ContractPayment cp = (ContractPayment) getTo();
		if (cp.getType() != null && StringUtils.isEmpty(cp.getDescription())) {
			cp.setDescription( cp.getPaymentConcept().getDescription() );
		}
	}
	
	@Override
	public void onEdit(ActionEvent event) {
		IContractPayment row = (IContractPayment) getPaymentsModel().getRowData();
		try {
			if(row.getScope()==ExpressionScope.CONTRACT){
				this.select(event, (ITransferObject) row);
				onReloadExpression(event);
			} else {
				super.onReset(event);
				IController master = FormUtil.getController("contract");
				Contract contract = (Contract) master.getTo();
				ContractPayment payment = (ContractPayment) this.getTo();
				payment.setContract(contract);
				payment.setType(row.getType());
				payment.setDescription(row.getDescription());
				payment.setExpression(row.getExpression());
				payment.setIrpfExpression(row.getIrpfExpression());
				payment.setQuoteExpression(row.getQuoteExpression());
				payment.setStartDate(row.getStartDate());
				payment.setEndDate(row.getEndDate());
				payment.setMonth(row.getMonth());
				payment.setDescriptionDecorable(row.isDescriptionDecorable());
				payment.setSalaryType(row.getSalaryType());
				if(row.getScope()==ExpressionScope.AGREEMENT){
					AgreementPayment alp = (AgreementPayment) row;
					payment.setPaymentConcept(alp.getPaymentConcept());
				} else if(row.getScope()==ExpressionScope.SYSTEM){
					SystemPayment sp = (SystemPayment) row;
					payment.setPaymentConcept(sp.getPaymentConcept());
				}
				onReloadExpression(event);
			}
			reset(true);
		} catch (ManagerBeanException e) {
			String msg = "Imposible seleccionar la percepcion";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		initializePaymentModel();
	}
	
	@Override
	protected void completeCiteria() {
		
	}
	
	private void initializePaymentModel() {
		try {
			IController master = FormUtil.getController("contract");
			Contract contract = (Contract) master.getTo();
			
			// system payments
			IManagerBean spBean = BeanManager.getManagerBean(SystemPayment.class);
			Criteria spCriteria = new Criteria();
			spCriteria.addOrder(spBean.getFieldName(IEntityAlias.SYSTEM_PAYMENT_START_DATE), false);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(spBean.getFieldName(IEntityAlias.SYSTEM_PAYMENT_END_DATE), new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(spBean.getFieldName(IEntityAlias.SYSTEM_PAYMENT_END_DATE));
			spCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			
			// agreement payments
			IManagerBean apBean = BeanManager.getManagerBean(AgreementPayment.class);
			Criteria apCriteria = null;
			if(contract.getAgreementLevelCategory()!=null){
				apCriteria = new Criteria();
				apCriteria.addEqualExpression(apBean.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_AGREEMENT_ID), contract.getAgreementLevelCategory().getLevel().getAgreement().getId());
				apCriteria.addOrder(apBean.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_START_DATE), false);
				expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(apBean.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_END_DATE), new Date());
				expr2 = ExpressionUtilities.getNullExpression(apBean.getFieldName(IEntityAlias.AGREEMENT_PAYMENT_END_DATE));
				apCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			}
			
			// contract payments
			IManagerBean cpBean = BeanManager.getManagerBean(ContractPayment.class);
			Criteria cpCriteria = new Criteria();
			cpCriteria.addEqualExpression(cpBean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
			cpCriteria.addOrder(cpBean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_START_DATE), false);
			if(isSearchCurrent()){
				expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(cpBean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_END_DATE), new Date());
				expr2 = ExpressionUtilities.getNullExpression(cpBean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_END_DATE));
				cpCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			} else {
				if(getInactiveDate()!=null){
					expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(cpBean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_END_DATE), getInactiveDate());
					expr2 = ExpressionUtilities.getNullExpression(cpBean.getFieldName(IEntityAlias.CONTRACT_PAYMENT_END_DATE));
					cpCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
				}
			}
			List<IContractPayment> list = new LinkedList<IContractPayment>();
			List<ITransferObject> systemPayments = spBean.getList(spCriteria);
			List<ITransferObject> agreementPayments = apCriteria!=null?apBean.getList(apCriteria):new LinkedList<ITransferObject>();
			List<ITransferObject> contractPayments = cpBean.getList(cpCriteria);
			for (ITransferObject to: contractPayments) {
				IContractPayment p = (IContractPayment) to;
				if(p.getScope()!=ExpressionScope.SYSTEM || (p.getScope()==ExpressionScope.SYSTEM && isSystemPaymentVisible(p))){
					list.add(p);
				}
			}
			for (ITransferObject to: agreementPayments) {
				IContractPayment p = (IContractPayment) to;
				if(p.getScope()!=ExpressionScope.SYSTEM || (p.getScope()==ExpressionScope.SYSTEM && isSystemPaymentVisible(p))){
					list.add(p);
				}
			}
			for (ITransferObject to: systemPayments) {
				IContractPayment p = (IContractPayment) to;
				if(p.getScope()!=ExpressionScope.SYSTEM || (p.getScope()==ExpressionScope.SYSTEM && isSystemPaymentVisible(p))){
					list.add(p);
				}
			}
			paymentsModel = new ListDataModel(list);
		} catch (ManagerBeanException e) {
			String msg = "Imposible inicializar las percepciones";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	private boolean isSystemPaymentVisible(IContractPayment p) {
		SystemPayment sp = (SystemPayment) p;
		IController master = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		Contract contract = (Contract) master.getTo();
		Date date = contract.getEndDate()!=null?contract.getEndDate():new Date();
		ContractSalaryCalculatorContext ctx;
		try {
			ctx = (ContractSalaryCalculatorContext) contract.getSalaryCalculatorContext(contract.getStartDate(), date, date);
			Calendar startCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
			endCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			List<ITimedResult<Object>> list = ctx.getExpressionContext().eval(sp.getPaymentConcept().getExpression(), startCal.getTime(), endCal.getTime());
			for(ITimedResult<Object> o: list){
				if(((Number)o.getValue()).intValue()>0){
					return true;
				}
			}
		} catch (UndefinedVariablesException uve) {
			return false;
		} catch (ExpressionException e) {
			return false;
		} catch (SalaryException e) {
			String msg = "Imposible evaluar las percepciones de sistema";
			AonUtil.addErrorMessage(msg);
		}
		return false;
	}
	
	//**********************************************
	// VARIABLES
	//**********************************************

	private ContractPaymentVariableHandler handler;
	
	@Override
	public void initializeVariables(ActionEvent event) {
		getHandler().initializeVariables(event);
		ContractPayment payment = (ContractPayment) this.getTo();
		setEnableExpressionEdition( StringUtils.isNotBlank(payment.getExpression()) );
	}
	
	@Override
	public ContractPaymentVariableHandler getHandler() {
		if(handler==null){
			handler = new ContractPaymentVariableHandler(this);
		}
		return handler;
	}
	@Override
	public List<?> expressionContext(Object suggest) {
		return getHandler().expressionContext(suggest);
	}
	@Override
	public IManagerBean getVariableManagerBean() throws ManagerBeanException {
		return getHandler().getVariableManagerBean();
	}
	@Override
	public void resetVariable() {
		getHandler().resetVariable();
	}
	@Override
	public SalaryType getSalaryType() {
		return ((ContractPayment)getTo()).getSalaryType();
	}

	@Override
	public String getExpression() {
		ContractPayment cp = ((ContractPayment) getTo());
		return StringUtils.isNotBlank(cp.getExpression()) ? cp.getExpression() : cp.getPaymentConcept().getExpression();
	}
	@Override
	public String getQuoteExpression() {
		ContractPayment cp = ((ContractPayment) getTo());
		return cp.getQuoteExpression();
	}
	@Override
	public String getIrpfExpression() {
		ContractPayment cp = ((ContractPayment) getTo());
		return cp.getIrpfExpression();
	}
	@Override
	public void setExpression(String expression) {
		ContractPayment cp = ((ContractPayment) getTo());
		cp.setExpression(expression);
	}
	@Override
	public void setQuoteExpression(String expression) {
		ContractPayment cp = ((ContractPayment) getTo());
		cp.setQuoteExpression(expression);
	}
	@Override
	public void setIrpfExpression(String expression) {
		ContractPayment cp = ((ContractPayment) getTo());
		cp.setIrpfExpression(expression);
	}
		
}
