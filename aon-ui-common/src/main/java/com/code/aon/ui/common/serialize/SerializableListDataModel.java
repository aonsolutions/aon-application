package com.code.aon.ui.common.serialize;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import com.code.aon.common.AonVersion;

public class SerializableListDataModel extends ListDataModel implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public SerializableListDataModel() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public SerializableListDataModel(List list) {
		super(list);
	}
	
}
