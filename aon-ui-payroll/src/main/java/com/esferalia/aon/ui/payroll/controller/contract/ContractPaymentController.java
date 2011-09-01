package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.SystemPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.HierarchyPayments;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.UndefinedVariableException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class ContractPaymentController extends ContractDetailAbstractController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractPaymentController.class.getName());
	
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
	
	public void initialize(){
		setPaymentsModel(null);
	}
	
	public boolean isReadOnly(){
		if(this.getPaymentsModel().getRowCount()>0){
			return ((IContractPayment)this.getPaymentsModel().getRowData()).getScope()!=ExpressionScope.CONTRACT;
		}
		return true;
	}

	public void initialize(ActionEvent event) {
		initialize();
	}
	
	@Override
	public void onSave(ActionEvent event) {
		IController master = FormUtil.getController("contract");
		Contract contract = (Contract) master.getTo();
		ContractPayment cd  = (ContractPayment) getTo();
		cd.setContract(contract);
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
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_TYPE), cp.getType());
			criteria.addOrder(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_CODE));
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
				initializeVariables(event);
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
			}
			reset(true);
		} catch (ManagerBeanException e) {
			String msg = "Imposible seleccionar la percepcion";
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
	
	@SuppressWarnings("unchecked")
	private void initializePaymentModel() {
		try {
			IManagerBean sBean = BeanManager.getManagerBean(SystemPayment.class);
			IManagerBean aBean = BeanManager.getManagerBean(AgreementPayment.class);
			IManagerBean cBean = BeanManager.getManagerBean(ContractPayment.class);
			IController master = FormUtil.getController("contract");
			Contract contract = (Contract) master.getTo();
			Criteria sCriteria = new Criteria();
			sCriteria.addOrder(sBean.getFieldName(IPayrollAlias.SYSTEM_PAYMENT_START_DATE), false);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(sBean.getFieldName(IPayrollAlias.SYSTEM_PAYMENT_END_DATE), new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(sBean.getFieldName(IPayrollAlias.SYSTEM_PAYMENT_END_DATE));
			sCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			Criteria aCriteria = new Criteria();
			aCriteria.addEqualExpression(aBean.getFieldName(IPayrollAlias.AGREEMENT_PAYMENT_AGREEMENT_ID), contract.getAgreementLevelCategory().getLevel().getAgreement().getId());
			aCriteria.addOrder(aBean.getFieldName(IPayrollAlias.AGREEMENT_PAYMENT_START_DATE), false);
			expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(aBean.getFieldName(IPayrollAlias.AGREEMENT_PAYMENT_END_DATE), new Date());
			expr2 = ExpressionUtilities.getNullExpression(aBean.getFieldName(IPayrollAlias.AGREEMENT_PAYMENT_END_DATE));
			aCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			Criteria cCriteria = new Criteria();
			cCriteria.addEqualExpression(cBean.getFieldName(IPayrollAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
			cCriteria.addOrder(cBean.getFieldName(IPayrollAlias.CONTRACT_PAYMENT_START_DATE), false);
			if(isSearchCurrent()){
				expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(cBean.getFieldName(IPayrollAlias.CONTRACT_PAYMENT_END_DATE), new Date());
				expr2 = ExpressionUtilities.getNullExpression(cBean.getFieldName(IPayrollAlias.CONTRACT_PAYMENT_END_DATE));
				cCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			} else {
				if(getInactiveDate()!=null){
					expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(cBean.getFieldName(IPayrollAlias.CONTRACT_PAYMENT_END_DATE), getInactiveDate());
					expr2 = ExpressionUtilities.getNullExpression(cBean.getFieldName(IPayrollAlias.CONTRACT_PAYMENT_END_DATE));
					cCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
				}
			}
			List<?> sl = sBean.getList(sCriteria);
			List<?> al = aBean.getList(aCriteria);
			List<?> cl = cBean.getList(cCriteria);
			HierarchyPayments payments = new HierarchyPayments( ((List<IContractPayment>) cl).iterator(), ((List<IContractPayment>) al).iterator(), ((List<IContractPayment>) sl).iterator());
			List<IContractPayment> list = new LinkedList<IContractPayment>();
			for (IContractPayment p: payments) {
				if(p.getScope()!=ExpressionScope.SYSTEM || (p.getScope()==ExpressionScope.SYSTEM && isSystemPaymentVisible(p))){
					list.add(p);
				}
			}
			paymentsModel = new ListDataModel(list);
		} catch (ManagerBeanException e) {
			String msg = "Imposible inicializar lar percepciones";
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
			List<ITimedObject<Object>> list = ctx.getExpressionContext().eval(sp.getPaymentConcept().getExpression(), startCal.getTime(), endCal.getTime());
			for(ITimedObject<Object> o: list){
				if(((Number)o.getValue()).intValue()>0){
					return true;
				}
			}
		} catch (UndefinedVariableException uve) {
			return false;
		} catch (ExpressionException e) {
			return false;
		} catch (SalaryException e) {
			String msg = "Imposible evaluar lar percepciones de sistema";
			AonUtil.addErrorMessage(msg);
		}
		return false;
	}
	
	//**********************************************
	// VARIABLES
	//**********************************************
	
	@Override
	protected void initializeVariables(ActionEvent event) {
		ContractPayment payment = (ContractPayment)this.getTo();
		Contract contract = payment.getContract();
		List<ContractData> dataList;
		try {
			setVariablesModel(null);
			setUndefinedVariablesModel(null);
			if(payment.getExpression()!=null || payment.getPaymentConcept().getExpression()!=null){
				dataList = new LinkedList<ContractData>();
				Set<String> vl = ExpressionContext.getVariables(payment.getExpression()==null?payment.getPaymentConcept().getExpression():payment.getExpression());
				List<ContractData> undefined = new LinkedList<ContractData>();
				if(!vl.isEmpty()){
					for(String s: vl){
						List<ITransferObject> list = existingContractData(s, contract);
						if(!list.isEmpty()){
							for(ITransferObject to: list){
								dataList.add((ContractData) to);
							}
						} else {
							Calendar startCal = Calendar.getInstance();
							Calendar endCal = Calendar.getInstance();
							startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
							endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
							ContractData data = new ContractData();
							data.setContract(contract);
							data.setName(s);
							data.setStartDate(startCal.getTime());
							data.setEndDate(contract.getEndDate()!=null?contract.getEndDate():endCal.getTime());
							ContractSalaryCalculatorContext ctx = (ContractSalaryCalculatorContext) contract.getSalaryCalculatorContext(contract.getStartDate(), data.getEndDate(), data.getEndDate());
							Object o = ctx.getExpressionContext().getVariable(s, startCal.getTime(), endCal.getTime(), Object.class);
							if(o==null){
								undefined.add(data);
							} else {
								data.setExpression(o.toString());
								dataList.add(data);
							}
						}
					}
				}
				setVariablesModel(new ListDataModel(dataList));
				if(!undefined.isEmpty()){
					setUndefinedVariablesModel(new ListDataModel(undefined));
				}
			}
		} catch (SalaryException e) {
			String msg = "Imposible cargar las variables del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar las variables del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
		
}
