package com.esferalia.aon.ui.payroll.controller.salary.draft;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractDetailAbstractController;

public class SalaryDraftPaymentController extends ContractDetailAbstractController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryDraftPaymentController.class.getName());

	public void onPaymentConceptChange(ActionEvent event) {
		ContractPayment cp = (ContractPayment) getTo();
		if (cp.getType() != null && StringUtils.isEmpty(cp.getDescription())) {
			cp.setDescription( cp.getPaymentConcept().getDescription() );
		}
	}
	
	@Override
	public void onEdit(ActionEvent event) {
		reset(true);
		setSelectedPayment(event);
		initializeVariables(event);
	}

	public void onSave(ActionEvent event) {
		ContractPayment cp = (ContractPayment) this.getTo();
		if(cp.getDescription().isEmpty()){
			cp.setDescription(null);
		}
		SalaryDraftController master = (SalaryDraftController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
		cp.setContract((Contract) master.getTo());
		super.onAccept(event);
		reset(false);
		master.setPaymentsModel(null);
	}

	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		reset(false);
		SalaryDraftController master = (SalaryDraftController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
		master.setPaymentsModel(null);
	}
	
	public void onReset(ActionEvent event) {
		super.onReset(event);
		reset(true);
		initializeVariables(event);
	}
	
	public void reset(boolean panelVisible) {
		setModalPanelVisible(panelVisible);
	}
	
	private void setSelectedPayment(ActionEvent event){
		SalaryDraftController controller = (SalaryDraftController) AonUtil.getRegisteredBean(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
		IContractPayment payment = (IContractPayment) controller.getPaymentsModel().getRowData();
		if(payment.getScope()==ExpressionScope.CONTRACT){
			this.setTo((ContractPayment) payment);
		} else if(payment.getScope()==ExpressionScope.AGREEMENT){
			super.onReset(event);
			ContractPayment cp = (ContractPayment) this.getTo();
			AgreementPayment alp = (AgreementPayment) payment;
			cp.setContract((Contract) controller.getTo()); 
			cp.setType(alp.getType()); 
			cp.setPaymentConcept(alp.getPaymentConcept()); 
			cp.setDescription(alp.getDescription()); 
			cp.setExpression(alp.getExpression()); 
			cp.setIrpfExpression(alp.getIrpfExpression()); 
			cp.setQuoteExpression(alp.getQuoteExpression()); 
			cp.setStartDate(alp.getStartDate()); 
			cp.setEndDate(alp.getEndDate()); 
			cp.setMonth(alp.getMonth()); 
			cp.setDescriptionDecorable(alp.isDescriptionDecorable());
			cp.setSalaryType(alp.getSalaryType());
		}
	}
	
	@Override
	protected void initialiceConcepts() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void completeCiteria() {
		try {
			this.clearCriteria();
			IController master = FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
			Contract contract = (Contract) master.getTo();
			getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
			if(isSearchCurrent()){
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IPayrollAlias.CONTRACT_PAYMENT_END_DATE), new Date());
				Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IPayrollAlias.CONTRACT_PAYMENT_END_DATE));
				getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar las percepciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
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
							data.setEndDate(endCal.getTime());
							ContractSalaryCalculatorContext ctx = (ContractSalaryCalculatorContext) contract.getSalaryCalculatorContext(contract.getStartDate(), new Date(), new Date());
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
