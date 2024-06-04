package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.ContractExtendedData;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class ContractExtendedDataJSON {
	
	public static JSONObject toJSON(ContractExtendedData data) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.ID, data.getId());
		json.put(IJsonNames.START_DATE, data.getStartDate());
		json.put(IJsonNames.END_DATE, data.getEndDate());
		json.put(IJsonNames.WORKPLACE_IDS, data.getWorkplace());
		json.put(IJsonNames.WORKPLACE, data.getWorkplaceName());
		json.put(IJsonNames.NAME, data.getPersonName());
		json.put(IJsonNames.TYPE, data.getContractType() != null ? data.getContractType().replace("\"", "") : ""); 
		json.put(IJsonNames.AMOUNT, data.getGrossSalaryLastMonth());
		json.put(IJsonNames.DOCUMENT, data.getPersonDocument());
		json.put("time", data.getTotalMarksLastMonth() != null ? data.getTotalMarksLastMonth() : 0);
		return json;
	}
	
	public static JSONObject toJSONSimple(ContractExtendedData data) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.ID, data.getId());
		json.put(IJsonNames.NAME, data.getPersonName());
		json.put(IJsonNames.DOCUMENT, data.getPersonDocument());
		return json;
	}
	
}
