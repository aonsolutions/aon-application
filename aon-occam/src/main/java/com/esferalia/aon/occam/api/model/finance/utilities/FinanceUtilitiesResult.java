package com.esferalia.aon.occam.api.model.finance.utilities;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.finance.utilities.IFinanceUtilitiesItem.FinanceUtilitiesItemType;

public class FinanceUtilitiesResult implements Serializable {

	private static final long serialVersionUID = -2255581012267211573L;
	
	private LinkedList<IFinanceUtilitiesItem> messages = new LinkedList<IFinanceUtilitiesItem>();
	
	public LinkedList<IFinanceUtilitiesItem> getItems() {
		return messages;
	}
	public FinanceUtilitiesResult add(IFinanceUtilitiesItem item) {
		messages.add(item);
		return this;
	}
	public FinanceUtilitiesResult addMessage(String message) {
		messages.add(new FinanceUtilitiesErrorItem()
				.setMessage(message));
		return this;
	}
	public FinanceUtilitiesResult addInfoMessage(String message) {
		messages.add(new FinanceUtilitiesInfoItem()
				.setMessage(message));
		return this;
	}
	public boolean hasErrorMessages() {
		for (IFinanceUtilitiesItem item : getItems()) {
			if (item.getType() == FinanceUtilitiesItemType.ERROR_MESSAGE) {
				return true;
			}
		}
		return false;
	}
	public boolean isEmpty() {
		return messages == null || messages.isEmpty();
	}
	
}

