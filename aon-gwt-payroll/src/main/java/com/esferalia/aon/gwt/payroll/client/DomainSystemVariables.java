package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.SystemVariable;
import com.esferalia.aon.gwt.payroll.shared.SystemVariableType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.ListBox;

public class DomainSystemVariables extends Variables {

	@Override
	protected Variable<?> newVariable() {
		return new SystemVariable();
	}

	@Override
	protected <T extends Enum<?>> String getVariableType(String shortType) {
		if (AonStringUtils.isBlank(shortType))
			return "N/D";

		switch (shortType) {
		case "D":
			return "System Data";
		default:
			return "N/D";
		}
	}

	@Override
	protected <T extends Enum<?>> String getVariableTypeShort(T variableType) {
		if ( variableType == SystemVariableType.SYSTEM_DATA)
			return "D";
		else
			return "N/D";
	}
	
	@Override
	protected <T extends Enum<?>> T valueOf(String type) {
		return (T) SystemVariableType.valueOf(type);
	}
	

	@Override
	protected void initializeVariableTypeLB(ListBox variableTypeListBox) {
		variableTypeListBox.clear();
		variableTypeListBox.addItem("System Data", "0");

		variableTypeListBox.addChangeHandler(e -> changeYear());

		setSelectedValueLB(variableTypeListBox, "0");
	}

	@Override
	protected void initializeDialogVariableTypeLB(ListBox variableTypeListBox) {
		variableTypeListBox.clear();
		variableTypeListBox.addItem("System Data", "SYSTEM_DATA");
	}

}
