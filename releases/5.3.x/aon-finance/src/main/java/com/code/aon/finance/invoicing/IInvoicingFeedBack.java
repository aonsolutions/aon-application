package com.code.aon.finance.invoicing;

import java.util.List;

public interface IInvoicingFeedBack {

	public void addMessage(String message);
	
	@SuppressWarnings("unchecked")
	public List getMessages();

	public int getCurrentRow();
	public void setCurrentRow(int currentValue);

	public int getRowCount();
	public void setRowCount(int rowCount);
}
