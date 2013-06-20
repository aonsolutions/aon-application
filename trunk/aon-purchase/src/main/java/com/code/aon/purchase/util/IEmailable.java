package com.code.aon.purchase.util;

import java.util.List;

public interface IEmailable {
	
	/**
	 * Returns the Recipient Email list
	 * 
	 * @return the Recipient Email list
	 */
	public List<String> getMoreRecipients();
	
	public void setMoreRecipients(List<String> list);
	
	
}
