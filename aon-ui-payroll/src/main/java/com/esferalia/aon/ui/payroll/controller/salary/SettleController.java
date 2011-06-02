package com.esferalia.aon.ui.payroll.controller.salary;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;

public class SettleController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SettleController.class);
	
	private SettleParams params;
	
	public SettleParams getParams() {
		return params;
	}
	public void setParams(SettleParams params) {
		this.params = params;
	}
	
	public void onSelectContract(ActionEvent event){
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		controller.onSelect(event);
		validateEndDate((Contract) controller.getTo());
		setParams(new SettleParams());
		getParams().setContract((Contract) controller.getTo());
		getParams().setSeniorityDate(((Contract) controller.getTo()).getSeniorityDate());
	}
	
	private void validateEndDate(Contract contract) {
		if(contract.getEndDate()==null){
			String msg = "No se puede generar el finiquito de un contrato no finalizado.";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onGenerate(ActionEvent event){
		AonUtil.addErrorMessage("sin implementar");
		LOGGER.error("sin implementar");
	}
}
