package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class DomainCompanyJSON {
	
	private DomainCompanyJSON() {
		
	}
	
	public static List<DomainCompany> fromJSONArray(String jsonArray) {
		JSONArray array = new JSONArray( jsonArray );
		return fromJSON( array );
	}
	
	public static List<DomainCompany> fromJSON(JSONArray json) {
		LinkedList<DomainCompany> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static DomainCompany fromJSON(JSONObject json) {
		if(json == null) return new DomainCompany();
		return new DomainCompany()
				.setDomain(DomainJSON.fromJSON(json.optJSONObject(IJsonNames.DOMAIN)))
				.setCompany(CompanyJSON.fromJSON(json.optJSONObject(IJsonNames.COMPANY)))
		;
	}

	public static JSONArray toJSON(List<DomainCompany> domains) {
		return toJSON(domains.stream());
	}
	
	public static JSONArray toJSON(Stream<DomainCompany> domains) {
		JSONArray array = new JSONArray();
		domains.forEach(task -> array.put(toJSON(task)));
		return array;
	}
	
	public static JSONObject toJSON(DomainCompany domainCompany) {
		if(domainCompany == null) return new JSONObject();
		return new JSONObject()
			.putOpt(IJsonNames.DOMAIN, DomainJSON.toJSON(domainCompany.getDomain()))
			.putOpt(IJsonNames.COMPANY, CompanyJSON.toJSON(domainCompany.getCompany()))
			;		
	}

		
}
