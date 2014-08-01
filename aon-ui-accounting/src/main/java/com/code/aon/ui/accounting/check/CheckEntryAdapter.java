package com.code.aon.ui.accounting.check;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;

public abstract class CheckEntryAdapter implements ICheckEntry, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Integer id;
	private String message;
	private ITransferObject to;
	
	@Override
	public String getMessage() {
		return message;
	}
	@Override
	public void setMessage(String message) {
		this.message = message; 
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	public ITransferObject getTo() {
		return to;
	}

	@Override
	public void setTo(ITransferObject to) {
		this.to = to;
	}
	
	@Override
	public void onFix(ActionEvent event) throws AonCheckException {
	}
	
	@Override
	public String fixAction() throws AonCheckException {
		return null;
	}

}
