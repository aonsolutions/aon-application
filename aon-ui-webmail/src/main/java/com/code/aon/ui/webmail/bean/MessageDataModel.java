package com.code.aon.ui.webmail.bean;

import java.io.Serializable;

import org.apache.commons.lang.ArrayUtils;
import org.richfaces.model.ModifiableModel;
import org.richfaces.model.SequenceDataModel;

import com.code.aon.AonVersion;
import com.code.aon.webmail.bean.AonMessage;

public class MessageDataModel extends ModifiableModel implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private int rowCount;
	
	public MessageDataModel(AonMessage[] messageList) {
		super(new SequenceDataModel(), "to");
		setWrappedData( messageList );
		this.rowCount = ArrayUtils.getLength(messageList);
	}

	@Override
	public int getRowCount() {
		return this.rowCount;
	}

}
