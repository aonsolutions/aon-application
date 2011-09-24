package com.esferalia.aon.ui.payroll.controller.contract;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.form.IController;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractData;

public class ContractBonusVariableHandler extends ContractDetailVariableHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractBonusVariableHandler.class.getName());
	
	public ContractBonusVariableHandler(IController controller) {
		super(controller);
	}

	@Override
	public void resetVariable() {
		setData(new ContractData());
		((ContractData)getData()).setContract(((ContractBonus) getController().getTo()).getContract());
		
	}
	@Override
	public void initializeVariables(ActionEvent event) {
		// TODO
		
	}
	
}
