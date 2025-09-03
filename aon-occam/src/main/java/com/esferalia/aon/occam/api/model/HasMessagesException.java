package com.esferalia.aon.occam.api.model;

import java.util.List;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.GwtIncompatible;

public interface HasMessagesException<T> {
	
	public List<T> getMessages();
	public String getUniqueMessage();
	
	@GwtIncompatible
	public JSONArray toJSON();

	
}
