package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.esferalia.aon.payroll.enumeration.ContractCode;

public class ContractCodeController {

	private DataModel model;

	public DataModel getModel() {
		if (model == null) {
			List<ContractCode> list = new LinkedList<ContractCode>();
			ContractCode[] codes = ContractCode.values();
			for (ContractCode cc : codes) {
				list.add(cc);
			}
			model = new ListDataModel( list ); 
		}
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public void onReset(ActionEvent event) {
		setModel(null);
	}
	
	
}
