package com.code.aon.ui.accounting.check;

import com.code.aon.common.ITransferObject;

public interface ICheckEntry {

	public ITransferObject getTo();
	public void setTo(ITransferObject to);

	public String getMessage();
	public void setMessage(String message);

	public boolean isFixAvailable();
	public boolean isFixed();
	public String getFixActionLabel();
	public void fix() throws AccountingCheckException;

	
}
