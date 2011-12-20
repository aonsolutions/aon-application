package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.IVariableData;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.ui.payroll.controller.contract.AbstractVariableHandler.AbstractVariableData;

public class ContractPaymentVariableHandler extends ContractDetailVariableHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractPaymentVariableHandler.class.getName());
	
	public ContractPaymentVariableHandler(IController controller) {
		super(controller);
	}

	@Override
	public void resetVariable() {
		setData(new AbstractVariableData());
		getData().setVariableData(new ContractData());
		((ContractData)getData().getVariableData()).setContract(((ContractPayment) getController().getTo()).getContract());
		
	}
	@Override
	public void initializeVariables(ActionEvent event) {
		ContractPayment payment = (ContractPayment)getController().getTo();
		Contract contract = payment.getContract();
		Integer year = ((ContractDetailVariableController) getController()).getYear() ;
		Month month = ((ContractDetailVariableController) getController()).getMonth();
		List<IVariableData> dataList;
		try {
			setVariablesModel(null);
			setUndefinedVariablesModel(null);
			if(payment.getExpression()!=null || payment.getPaymentConcept().getExpression()!=null){
				dataList = new LinkedList<IVariableData>();
				Set<String> vl = ExpressionContext.getVariables(payment.getExpression()==null?payment.getPaymentConcept().getExpression():payment.getExpression());
				List<IVariableData> undefined = new LinkedList<IVariableData>();
				if(!vl.isEmpty()){
					ContractSalaryCalculatorContext ctx = (ContractSalaryCalculatorContext) contract.getSalaryCalculatorContext(year, month, payment.getSalaryType());
					for(String s: vl){
						List<ITransferObject> list = existingContractData(s, contract);
						if(!list.isEmpty()){
							for(ITransferObject to: list){
								AbstractVariableData data = new AbstractVariableData();
								data.setVariableData((IVariableData) to);
								dataList.add(data);
							}
						} else {
							Calendar startCal = Calendar.getInstance();
							Calendar endCal = Calendar.getInstance();
							startCal.set(Calendar.HOUR_OF_DAY, 0);
							startCal.set(Calendar.YEAR, year);
							startCal.set(Calendar.MONTH, month.ordinal());
							startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
							endCal.set(Calendar.HOUR_OF_DAY, 0);
							endCal.set(Calendar.YEAR, year);
							endCal.set(Calendar.MONTH, month.ordinal());
							endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
							ContractData data = new ContractData();
							data.setContract(contract);
							data.setName(s);
							data.setStartDate(startCal.getTime());
							data.setEndDate(contract.getEndDate()!=null?contract.getEndDate():endCal.getTime());
							Object o = ctx.getExpressionContext().getVariable(s, startCal.getTime(), endCal.getTime(), Object.class);
							AbstractVariableData d = new AbstractVariableData();
							d.setVariableData((IVariableData) data);
							if(o==null){
								undefined.add(d);
							} else {
								d.setExpression(o.toString());
								dataList.add(d);
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
			String msg = "Imposible cargar las variables del contrato (" + e.getCause()+": "+e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg,e);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar las variables del contrato (" + e.getCause()+": "+e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg,e);
		}
	}
	
}
