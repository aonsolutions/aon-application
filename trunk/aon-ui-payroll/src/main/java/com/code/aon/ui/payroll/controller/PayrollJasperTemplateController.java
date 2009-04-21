package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ITransferObject;



public class PayrollJasperTemplateController {
	
	/**
	 * Lista para la impresion en las plantillas de unicamente el elemento seleccionado
	 */
	private static List<ITransferObject> selectedToList;

	public List<ITransferObject> getSelectedToList() {
		return selectedToList;
	}

	public void setSelectedToList(List<ITransferObject> selectedToList) {
		PayrollJasperTemplateController.selectedToList = selectedToList;
	}
	
	public static void addSelectedToList(ITransferObject to) {
		selectedToList = null;
		selectedToList = new LinkedList<ITransferObject>();
		
		selectedToList.add(to);
	}
	
}
