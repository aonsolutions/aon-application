package com.code.aon.ui.accounting.check;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ITransferObject;

public interface ICheckEntry {

	public ITransferObject getTo();
	public void setTo(ITransferObject to);

	public String getMessage();
	public void setMessage(String message);

	public boolean isFixAvailable();
	public boolean isFixed();
	public String getFixActionLabel();
	
	public void onFix(ActionEvent event) throws AonCheckException;
	public String fixAction() throws AonCheckException;

	
}
