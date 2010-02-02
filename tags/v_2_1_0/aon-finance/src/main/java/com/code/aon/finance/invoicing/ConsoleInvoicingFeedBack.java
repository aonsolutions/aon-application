package com.code.aon.finance.invoicing;

import java.util.Collections;
import java.util.List;

public class ConsoleInvoicingFeedBack implements IInvoicingFeedBack {

	public void addMessage(String message) {
		System.out.println(message);
	}

	public List getMessages() {
		return Collections.EMPTY_LIST;
	}

}
