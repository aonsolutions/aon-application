package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounter;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounterDetail;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.ewok.TediInvoiceType;

public class RawdocInvoiceCounterJSON {
	
	private RawdocInvoiceCounterJSON() {

	}
	
	public static JSONObject toJSON(RawdocInvoiceCounter counter) {
		JSONObject json = new JSONObject();
		counter.getMap().keySet().stream().forEach(key -> {
			RawdocInvoiceCounterDetail counterDetail = counter.getMap().get(key);
			JSONObject detail = new JSONObject();
			detail.put(IJsonNames.COUNT, counterDetail.getCount());
			counterDetail.getMap().keySet()
				.stream()
				.forEach(i -> {
					String k = null;
					if (i == TediInvoiceType.EMITIDA) {
						k = RawdocType.OUTPUT.name();
					} else if (i == TediInvoiceType.RECIBIDA) {
						k = RawdocType.INPUT.name();
					} else {
						k = i.name(); 
					}
					detail.put( k, counterDetail.getMap().get(i));
				});
			json.put(key.getName(), detail);
		});
		return json;
	}

}
