package com.esferalia.aon.occam.api.model.accounting.utilities;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.AccUtilitiesItemType;

public class AccUtilitiesResult implements Serializable {

	private static final long serialVersionUID = -2021913435565238739L;
	
	private LinkedList<IAccUtilitiesItem> messages = new LinkedList<IAccUtilitiesItem>();
	
	public LinkedList<IAccUtilitiesItem> getItems() {
		return messages;
	}
	public AccUtilitiesResult add(IAccUtilitiesItem item) {
		messages.add(item);
		return this;
	}
	public AccUtilitiesResult addMessage(String message) {
		messages.add(new AccUtilitiesErrorItem()
				.setMessage(message));
		return this;
	}
	public AccUtilitiesResult addInfoMessage(String message) {
		messages.add(new AccUtilitiesInfoItem()
				.setMessage(message));
		return this;
	}
	public boolean hasErrorMessages() {
		for (IAccUtilitiesItem item : getItems()) {
			if (item.getType() == AccUtilitiesItemType.ERROR_MESSAGE) {
				return true;
			}
		}
		return false;
	}
	public boolean isEmpty() {
		return messages == null || messages.isEmpty();
	}
	
}

