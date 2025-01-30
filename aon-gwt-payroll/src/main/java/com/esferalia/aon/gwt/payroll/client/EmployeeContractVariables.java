package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
import com.esferalia.aon.gwt.payroll.shared.ContractVariableType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.ListBox;

public class EmployeeContractVariables extends Variables {

	@Override
	protected Variable<?> newVariable() {
		return new ContractVariable();
	}

	@Override
	protected <T extends Enum<?>> String getVariableType(String shortType) {
		if (AonStringUtils.isBlank(shortType))
			return "N/D";

		switch (shortType) {
		case "D":
			return "Contract Data";
		case "I":
			return "Contract Info";
		default:
			return "N/D";
		}
	}

	@Override
	protected <T extends Enum<?>> String getVariableTypeShort(T variableType) {
		if ( variableType == ContractVariableType.CONTRACT_DATA)
			return "D";
		else if (variableType == ContractVariableType.CONTRACT_INFO)
			return "I";
		else
			return "N/D";
	}
	
	@Override
	protected <T extends Enum<?>> T valueOf(String type) {
		return (T) ContractVariableType.valueOf(type);
	}
	
	@Override
	protected void initializeVariableTypeLB(ListBox variableTypeListBox) {
		variableTypeListBox.clear();
		variableTypeListBox.addItem("Contract Data", "0");
		variableTypeListBox.addItem("Contract Info", "1");
		variableTypeListBox.addItem("Todas", "2");

		variableTypeListBox.addChangeHandler(e -> changeYear());

		setSelectedValueLB(variableTypeListBox, "0");
	}
	
	@Override
	protected void initializeDialogVariableTypeLB(ListBox variableTypeListBox) {
		variableTypeListBox.clear();
		variableTypeListBox.addItem("Contract Data", "CONTRACT_DATA");
		variableTypeListBox.addItem("Contract Info", "CONTRACT_INFO");
	}

}
