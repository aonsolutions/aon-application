package com.esferalia.aon.ui.payroll.controller.salary.draft;

import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.TaxationType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.ui.payroll.controller.IPaymentHandler;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractDetailVariableController;

public class SalaryDraftPaymentController extends ContractDetailVariableController implements IPaymentHandler{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryDraftPaymentController.class.getName());
	
	public void onPaymentConceptChange(ActionEvent event) {
		ContractPayment cp = (ContractPayment) getTo();
		if (cp.getType() != null && StringUtils.isEmpty(cp.getDescription())) {
			cp.setDescription( cp.getPaymentConcept().getDescription() );
		}
	}
	
	@Override
	public void onEdit(ActionEvent event) {
		setSelectedPayment(event);
		onReloadExpression(event);
		reset(true);
	}

	public void onSave(ActionEvent event) {
		ContractPayment cp = (ContractPayment) this.getTo();
		if(cp.getDescription().isEmpty()){
			cp.setDescription(null);
		}
		SalaryDraftController master = (SalaryDraftController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
		cp.setContract((Contract) master.getTo());
		super.accept(event);
		reset(true);
	}

	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		reset(false);
	}
	
	public void onReset(ActionEvent event) {
		super.onReset(event);
		reset(true);
	}
	
	public void reset(boolean panelVisible) {
		setModalPanelVisible(panelVisible);
		setMonth(Month.getMonthByValue(CommonUtil.getMonth(new Date())));
		setYear( CommonUtil.getYear(new Date()));
		SalaryDraftController master = (SalaryDraftController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
		master.reset();
	}
	
	private void setSelectedPayment(ActionEvent event){
		SalaryDraftController controller = (SalaryDraftController) AonUtil.getRegisteredBean(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
		IContractPayment payment = (IContractPayment) controller.getPaymentsModel().getRowData();
		try {
			if(payment.getScope()==ExpressionScope.CONTRACT){
				this.select(event, (ITransferObject) payment);
				onReloadExpression(event);
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
				
				onReloadExpression(event);
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible seleccionar la percepcion";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
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
			getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_PAYMENT_CONTRACT_ID), contract.getId());
			if(isSearchCurrent()){
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_PAYMENT_END_DATE), new Date());
				Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IEntityAlias.CONTRACT_PAYMENT_END_DATE));
				getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar las percepciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	@Override
	public String getQuoteExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIrpfExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExpression(String expression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setQuoteExpression(String expression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIrpfExpression(String expression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TaxationType getTaxation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuoteType getQuote() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SalaryType getSalaryType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpression() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
		
}
