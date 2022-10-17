package com.esferalia.aon.occam.api.json.nordigen;

import static com.esferalia.aon.occam.api.json.nordigen.NordigenJSONUtils.countryJSONArrayToSet;
import static com.esferalia.aon.occam.api.json.nordigen.NordigenJSONUtils.countryJSONSetToArray;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenInstitutionFromJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenInstitutionToJSON;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.watson.util.AonNumberUtils;

public enum NordigenInstitutionJSON {
	ID(
			(institution, json) -> institution.setId(json.optString("id", null)),
			(institution, json) -> json.put("id", institution.getId())
	),
	NAME(
			(institution, json) -> institution.setName(json.optString("name", null)),
			(institution, json) -> json.put("name", institution.getName())
	),
	BIC(
			(institution, json) -> institution.setBic(json.optString("bic", null)),
			(institution, json) -> json.put("bic", institution.getBic())
	),
	TRANSACTION_TOTAL_DAYS(
			(institution, json) -> institution.setTransactionTotalDays(AonNumberUtils.toInteger(json.optString("transaction_total_days", null))),
			(institution, json) -> json.put("transaction_total_days", institution.getTransactionTotalDays())
	),
	COUNTRIES(
			(institution, json) -> institution.setCountries(countryJSONArrayToSet(json.optJSONArray("countries"))),
			(institution, json) -> json.put("countries", countryJSONSetToArray(institution.getCountries()))
	),
	LOGO(
			(institution, json) -> institution.setLogo(json.optString("logo", null)),
			(institution, json) -> json.put("logo", institution.getLogo())
	)
	;
	private INordigenInstitutionFromJSON fromJSON;
	private INordigenInstitutionToJSON toJSON;
	
	private NordigenInstitutionJSON(INordigenInstitutionFromJSON fromJSON, INordigenInstitutionToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(NordigenInstitution institution) {
		if (institution != null) {
			JSONObject json = new JSONObject();
			for (NordigenInstitutionJSON n : NordigenInstitutionJSON.values()) {
				n.toJSON.to(institution, json);
			}
			return json;
		}
		return null;
	}
	
	public static NordigenInstitution fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json);
	}

	public static NordigenInstitution fromJSON(JSONObject json) {
		if (json != null) {
			NordigenInstitution institution = new NordigenInstitution();
			for (NordigenInstitutionJSON n : NordigenInstitutionJSON.values()) {
				n.fromJSON.from(institution, json);
			}
			return institution;
		}
		return null;
	}
	
	public static List<NordigenInstitution> fromJSONArray(JSONArray jsonArray) {
		List<NordigenInstitution> instList = new LinkedList<>();
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.optJSONObject(i);
				NordigenInstitution institution = NordigenInstitutionJSON.fromJSON(jsonObj);
				instList.add(institution);
			}
		}
		return instList;
	}
	
	public static JSONArray toJSONArray(List<NordigenInstitution> list) {
		JSONArray instArr = new JSONArray();
		if (list != null) {
			for (NordigenInstitution inst: list) {
				JSONObject instJson = NordigenInstitutionJSON.toJSON(inst);
				instArr.put(instJson);
			}
		}
		return instArr;
	}
}
