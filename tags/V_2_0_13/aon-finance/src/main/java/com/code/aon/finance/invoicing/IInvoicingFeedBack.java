package com.code.aon.finance.invoicing;

import java.util.List;

public interface IInvoicingFeedBack {

	public void addMessage(String message);
	
	public List getMessages();
}
