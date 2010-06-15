package com.code.aon.ui.webmail.bean;

import org.apache.commons.lang.ArrayUtils;
import org.richfaces.model.ModifiableModel;
import org.richfaces.model.SequenceDataModel;

import com.code.aon.webmail.bean.AonMessage;

public class MessageDataModel extends ModifiableModel {

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
