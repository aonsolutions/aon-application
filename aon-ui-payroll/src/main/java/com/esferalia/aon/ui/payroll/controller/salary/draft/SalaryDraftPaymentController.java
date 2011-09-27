package com.esferalia.aon.ui.payroll.controller.salary.draft;

import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractDetailVariableController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractPaymentVariableHandler;

public class SalaryDraftPaymentController extends ContractDetailVariableController{
	
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
		onReloadExpression(event);
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
	public void initializeVariables(ActionEvent event) {
		getHandler().initializeVariables(event);
	}

	private ContractPaymentVariableHandler handler;
	
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
	public String getExpression(){
		return ((ContractPayment)getTo()).getExpression()!=null?((ContractPayment)getTo()).getExpression():((ContractPayment)getTo()).getPaymentConcept().getExpression();
	}
		
}
