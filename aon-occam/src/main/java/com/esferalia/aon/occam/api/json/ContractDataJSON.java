package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ContractDataJSON {	
	
	public static LinkedList<ContractData> fromJSON(JSONArray json) {
		LinkedList<ContractData> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static ContractData fromJSON(JSONObject json) {
		return new ContractData()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setName(JsonUtils.getString(json, IJsonNames.NAME))
			.setContract(JsonUtils.getInteger(json, "contract"))
			.setExpression(JsonUtils.getString(json, "expression"))
			.setStartDate(AonDateUtils.parse(JsonUtils.getString(json, "startDate"), "yyyy-MM-dd"))
			.setEndDate(AonDateUtils.parse(JsonUtils.getString(json, "endDate"), "yyyy-MM-dd"))
			;
	}
	
	public static JSONArray toJSON(LinkedList<ContractData> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<ContractData> cts) {
		JSONArray array = new JSONArray();
		cts.forEach(ct -> array.put(toJSON(ct)));
		return array;
	}
	
	public static JSONObject toJSON(ContractData cData) {
		return new JSONObject()
			.put(IJsonNames.ID, cData.getId())
			.put(IJsonNames.DOMAIN, cData.getDomain())
			.put("contract", cData.getName())
			.put("expression", cData.getExpression() )
			.put(IJsonNames.START_DATE, cData.getStartDate())
			.put(IJsonNames.END_DATE, cData.getEndDate());
	}

}
