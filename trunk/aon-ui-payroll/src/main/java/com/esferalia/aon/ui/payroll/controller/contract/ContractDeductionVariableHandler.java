package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionContext;

public class ContractDeductionVariableHandler extends ContractDetailVariableHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractDeductionVariableHandler.class.getName());
	
	public ContractDeductionVariableHandler(IController controller) {
		super(controller);
	}

	@Override
	public void resetVariable() {
		setData(new ContractData());
		((ContractData)getData()).setContract(((ContractDeduction) getController().getTo()).getContract());
		
	}
	@Override
	public void initializeVariables(ActionEvent event) {
		ContractDeduction deduction = (ContractDeduction)getController().getTo();
		Contract contract = deduction.getContract();
		List<ContractData> dataList;
		try {
			setVariablesModel(null);
			setUndefinedVariablesModel(null);
			if(deduction.getExpression()!=null || deduction.getDeductionConcept().getExpression()!=null){
				dataList = new LinkedList<ContractData>();
				Set<String> vl = ExpressionContext.getVariables(deduction.getExpression()==null?deduction.getDeductionConcept().getExpression():deduction.getExpression());
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
				setVariablesModel( new ListDataModel(dataList) );
				if(!undefined.isEmpty()){
					setUndefinedVariablesModel( new ListDataModel(undefined) );
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
