package com.code.aon.webservice.util;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;

public class ToJSON {
	
	public ToJSON() {
	
	}
	
	public static JSONObject rmediaToJSON(RegistryMedia rmedia) {
		JSONObject json = new JSONObject();
		json.put("id", rmedia.getId());
		json.put("domain", rmedia.getDomain());
		json.put("registry", rmedia.getRegistry().getId());
		json.put("media", rmedia.getMedia());
		json.put("value", rmedia.getValue());
		json.put("comment", rmedia.getComment());
		json.put("administrative", rmedia.getAdministrative() == 1);
		json.put("commercial", rmedia.getCommercial() == 1);
		json.put("technical", rmedia.getTechnical() == 1);
		json.put("comment", rmedia.getRaddress());
		json.put("icon", Icon.rmediaIcon(rmedia.getMedia()));
		return json;
	}
	
	public static JSONObject rnoteToJSON(RegistryNote rnote) {
		JSONObject json = new JSONObject();
		json.put("id", rnote.getId());
		json.put("domain", rnote.getDomain());
		json.put("registry", rnote.getRegistry());
		json.put("description", rnote.getDescription());
		json.put("note_date", rnote.getNoteDate());
		json.put("comments", rnote.getComments());
		json.put("note_type", rnote.getNoteType());
		json.put("confidential", rnote.getSecurityLevel() == 1);
		return json;
	}
	
	public static JSONObject invoiceToJSON(Invoice invoice) {
		JSONObject json = new JSONObject();
		json.put("id", invoice.getId());
		json.put("domain", invoice.getDomain());
		json.put("registry", invoice.getRegistry());
		json.put("reference_code", invoice.getReferenceCode());
		return json;
	}
	
	public static JSONObject feeToJSON(Fee fee) {
		JSONObject json = new JSONObject();
		json.put("id", fee.getId());
		json.put("domain", fee.getDomain());
		json.put("customer", fee.getCustomer());
		json.put("description", fee.getDescription());
		return json;
	}
	

}
