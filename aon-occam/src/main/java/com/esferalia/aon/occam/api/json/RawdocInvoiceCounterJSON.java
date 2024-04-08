package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounter;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounterDetail;

public class RawdocInvoiceCounterJSON {
	
	private RawdocInvoiceCounterJSON() {

	}
	
	public static JSONObject toJSON(RawdocInvoiceCounter counter) {
		JSONObject json = new JSONObject();
		counter.getMap().keySet().stream().forEach(key -> {
			RawdocInvoiceCounterDetail counterDetail = counter.getMap().get(key);
			JSONObject detail = new JSONObject();
			detail.put(IJsonNames.COUNT, counterDetail.getCount());
			counterDetail.getMap().keySet().stream().forEach(i -> 
				detail.put(i.name(), counterDetail.getMap().get(i)));
			json.put(key.getName(), detail);
		});
		return json;
	}

}
