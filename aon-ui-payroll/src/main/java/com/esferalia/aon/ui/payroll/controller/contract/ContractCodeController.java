package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.code.aon.common.AonVersion;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.esferalia.aon.payroll.enumeration.ContractCode;

public class ContractCodeController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private DataModel model;

	public DataModel getModel() {
		if (model == null) {
			List<ContractCode> list = new LinkedList<ContractCode>();
			ContractCode[] codes = ContractCode.values();
			for (ContractCode cc : codes) {
				list.add(cc);
			}
			model = new SerializableListDataModel( list ); 
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
