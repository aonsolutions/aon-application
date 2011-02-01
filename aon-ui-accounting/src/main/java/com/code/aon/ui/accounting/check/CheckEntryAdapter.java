package com.code.aon.ui.accounting.check;

import com.code.aon.common.ITransferObject;

public abstract class CheckEntryAdapter implements ICheckEntry {

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

	@Override
	public ITransferObject getTo() {
		return to;
	}

	@Override
	public void setTo(ITransferObject to) {
		this.to = to;
	}

}
