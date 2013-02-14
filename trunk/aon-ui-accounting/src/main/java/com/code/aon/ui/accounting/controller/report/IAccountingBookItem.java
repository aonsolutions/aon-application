package com.code.aon.ui.accounting.controller.report;

public interface IAccountingBookItem {

	public boolean isCoverVisible();
	public void setCoverVisible(boolean coverVisible);

	public boolean isCounterVisible();
	public void setCounterVisible(boolean counterVisible);

	public int getPageCounter();
	public void setPageCounter(int pageCounter);
	
}
