package com.code.aon.ui.accounting.check;

import java.util.List;

public interface ICheckModule {

	public void onExecute(CheckParams params) throws AonCheckException;

	public List<ICheckEntry> getCheckList();

	public boolean isEnabled();
	public void setEnabled(boolean enabled);
	
	public CheckCategory getCategory();
	
	public String getLabel();
	
	public void mock(); // remove
	
}	
	
