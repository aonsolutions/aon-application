package com.code.aon.ui.accounting.check;

import java.util.List;



public interface IAccountCheck {

	public void onExecute(AccountingCheckParams params) throws AccountingCheckException;
	public List<ICheckEntry> getCheckList();

	public boolean isEnabled();
	public void setEnabled(boolean enabled);
	
	public String getLabel();
	
}	
	
