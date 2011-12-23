package com.esferalia.aon.ui.payroll.controller.salary.draft;


import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

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
import com.esferalia.aon.payroll.BonusConcept;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractBonusVariableHandler;
import com.esferalia.aon.ui.payroll.controller.contract.ContractDetailVariableController;

public class SalaryDraftBonusController extends ContractDetailVariableController{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryDraftBonusController.class.getName());
	
	private boolean variableFound;
	
	public boolean isVariableFound() {
		return variableFound;
	}
	public void setVariableFound(boolean variableFound) {
		this.variableFound = variableFound;
	}
	
	@Override
	public void onSave(ActionEvent event) {
		IController master = FormUtil.getController("contract");
		Contract contract = (Contract) master.getTo();
		ContractBonus cb  = (ContractBonus) getTo();
		cb.setContract(contract);
		super.onSave(event);
	}
	@Override
	protected void initialiceConcepts() {
		setConcepts(new LinkedList<SelectItem>());
		try {
			IManagerBean bean = BeanManager.getManagerBean(BonusConcept.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(bean.getFieldName(IEntityAlias.BONUS_CONCEPT_DESCRIPTION));
			List<ITransferObject> list = bean.getList(criteria);
			for (ITransferObject to: list) {
				BonusConcept bc = (BonusConcept) to;
				getConcepts().add(new SelectItem(bc, bc.getDescription()));
			}
		} catch (ManagerBeanException e) {
			// Se devuelve la lista vacia.
		} 
	}
	
	@Override
	protected void completeCiteria() {
		try {
			SalaryDraftController master = (SalaryDraftController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
			Contract contract = (Contract) master.getTo();
			getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_BONUS_CONTRACT_ID), contract.getId());
//			if(isSearchCurrent()){
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_BONUS_END_DATE), master.getStartDate());
				Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IEntityAlias.CONTRACT_BONUS_END_DATE));
				getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
//			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar las bonificaciones del contrato (" + e.getMessage() +")";
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

	private ContractBonusVariableHandler handler;
	
	@Override
	public ContractBonusVariableHandler getHandler() {
		if(handler==null){
			handler = new ContractBonusVariableHandler(this);
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
		return SalaryType.SALARY;
	}
	@Override
	public String getExpression(){
		return ((ContractBonus)getTo()).getExpression()!=null?((ContractBonus)getTo()).getExpression():((ContractBonus)getTo()).getBonusConcept().getExpression();
	}
	
}
