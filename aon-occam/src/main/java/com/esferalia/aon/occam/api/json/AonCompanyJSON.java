package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.AonCompany;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.DomainType;

public class AonCompanyJSON {
	
	public static AonCompany fromJSON(JSONObject json) {
		AonCompany aonCompany = new AonCompany();
		aonCompany.setDomain(new Domain());
		aonCompany.setCompany(new Company());	
		aonCompany.setAdministration(Administration.UNKNOWN);
		aonCompany.getCompany().setId(json.getInt("registry"));
		aonCompany.getDomain().setId(json.getInt("id"));
		aonCompany.getDomain().setName(json.getString("domain"));
		aonCompany.getCompany().setDocument(json.optString("document"));
		aonCompany.getCompany().setActive(json.getBoolean("active"));
		aonCompany.setAdministration(json.getEnum(Administration.class, "administration"));
		aonCompany.getDomain().setDomainType(json.getEnum(DomainType.class, "type"));
		if(json.optBoolean("parent") != false) aonCompany.getDomain().setParentId(json.optInt("parentId"));
		aonCompany.setShared(json.optBoolean("shared"));
		aonCompany.getDomain().setMaxDefinedUsers(json.optInt("maxDefinedUsers"));
		aonCompany.getCompany().setWithholding(json.optBoolean("withholding"));
		aonCompany.getCompany().setVatAccrualPayment(json.optBoolean(IJsonNames.VAT_ACCRUAL_PAYMENT));
		aonCompany.getCompany().setSurcharge(json.optBoolean(IJsonNames.SURCHARGE));
		aonCompany.setSchema(json.optString(IJsonNames.SCHEMA));
		
		return aonCompany;
			
	}
	
	public static List<AonCompany> fromJSON(JSONArray json) {
		LinkedList<AonCompany> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}

	public static JSONObject toJSON(AonCompany aonCompany) {
		return new JSONObject()
			.put("registry", aonCompany.getCompany().getId())
			.put("id", aonCompany.getDomain().getId())
			.put("domain", aonCompany.getDomain().getName())
			.put("name", aonCompany.getCompany().getName())
			.put("document", aonCompany.getCompany().getDocument())
			.put("active", aonCompany.getCompany().getDomain().isActive())
			.put("administration", aonCompany.getAdministration() != null ? aonCompany.getAdministration().name() : Administration.COMMON_TERRITORY.name())
			.put("type", aonCompany.getDomain().getDomainType().name())
			.put("parent",aonCompany.getDomain().isParent())
			.put("shared", aonCompany.isShared())
			.put("parentId",aonCompany.getDomain().getParentId())
			.put("maxDefinedUsers", aonCompany.getDomain().getMaxDefinedUsers())
			.put("withholding", aonCompany.getCompany().isWithholding())
			.put(IJsonNames.VAT_ACCRUAL_PAYMENT, aonCompany.getCompany().isVatAccrualPayment())
			.put(IJsonNames.SURCHARGE, aonCompany.getCompany().isSurcharge())
			.put(IJsonNames.SCHEMA, aonCompany.getSchema())
			;
	}
}
