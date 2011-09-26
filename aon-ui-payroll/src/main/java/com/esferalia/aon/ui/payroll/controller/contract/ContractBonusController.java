package com.esferalia.aon.ui.payroll.controller.contract;

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
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;

public class ContractBonusController extends ContractDetailVariableController{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractBonusController.class.getName());
	

	@Override
	public void onSave(ActionEvent event) {
		IController master = FormUtil.getController("contract");
		Contract contract = (Contract) master.getTo();
		ContractBonus cd  = (ContractBonus) getTo();
		cd.setContract(contract);
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
	public void onEdit(ActionEvent event) {
		super.onEdit(event);
		initializeVariables(event);
	}
	
	@Override
	protected void completeCiteria() {
//		if(isSearchCurrent()){
			try {
				if(isSearchCurrent()){
					Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE), new Date());
					Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE));
					getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
				} else {
					if(getInactiveDate()!=null){
						Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE), getInactiveDate());
						Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IPayrollAlias.CONTRACT_BONUS_END_DATE));
						getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
					}
				}
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}
	}
	
	
	//**********************************************
	// VARIABLES
	//**********************************************
	
	@Override
	public void initializeVariables(ActionEvent event) {
		ContractBonus bonus = (ContractBonus)this.getTo();
		Contract contract = bonus.getContract();
		List<ContractData> dataList;
		try {
			getHandler().setVariablesModel(null);
			getHandler().setUndefinedVariablesModel(null);
			if(bonus.getExpression()!=null || bonus.getBonusConcept().getExpression()!=null){
				dataList = new LinkedList<ContractData>();
				Set<String> vl = ExpressionContext.getVariables(bonus.getExpression()==null?bonus.getBonusConcept().getExpression():bonus.getExpression());
				List<ContractData> undefined = new LinkedList<ContractData>();
				if(!vl.isEmpty()){
					for(String s: vl){
						List<ITransferObject> list = getHandler().existingContractData(s, contract);
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
				getHandler().setVariablesModel(new ListDataModel(dataList));
				if(!undefined.isEmpty()){
					getHandler().setUndefinedVariablesModel(new ListDataModel(undefined));
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

	private ContractBonusVariableHandler handler;
	
	@Override
	public ContractBonusVariableHandler getHandler() {
		if(handler==null){
			handler = new ContractBonusVariableHandler(this);
		}
		return handler;
	}
	@Override
	public SalaryType getSalaryType() {
		return SalaryType.SALARY;
	}
	
}
