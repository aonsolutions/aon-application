package com.esferalia.aon.ui.payroll.controller.salary.draft;


import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
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
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractDetailAbstractController;

public class SalaryDraftBonusController extends ContractDetailAbstractController {
	
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
			criteria.addOrder(bean.getFieldName(IPayrollAlias.BONUS_CONCEPT_DESCRIPTION));
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
			getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CONTRACT_BONUS_CONTRACT_ID), contract.getId());
//			if(isSearchCurrent()){
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE), master.getStartDate());
				Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE));
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
	protected void initializeVariables(ActionEvent event) {
		ContractBonus bonus = (ContractBonus)this.getTo();
		Contract contract = bonus.getContract();
		List<ContractData> dataList;
		try {
			setVariablesModel(null);
			setUndefinedVariablesModel(null);
			if(bonus.getExpression()!=null || bonus.getBonusConcept().getExpression()!=null){
				dataList = new LinkedList<ContractData>();
				Set<String> vl = ExpressionContext.getVariables(bonus.getExpression()==null?bonus.getBonusConcept().getExpression():bonus.getExpression());
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
