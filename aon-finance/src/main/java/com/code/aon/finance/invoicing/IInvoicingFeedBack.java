package com.code.aon.finance.invoicing;

import java.util.List;

public interface IInvoicingFeedBack {

	public void addMessage(String message);
	
	public List<String> getMessages();

	public int getCurrentRow();
	public void setCurrentRow(int currentValue);

	public int getRowCount();
	public void setRowCount(int rowCount);
}
