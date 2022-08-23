package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class CompanyJSON {
	
	public static Company fromJSON(JSONObject json) {
		return new Company()
			.copy(RegistryJSON.fromJSON(json))
			.setActive(json.optBoolean(IJsonNames.ACTIVE))
			.setSurcharge(json.optBoolean(IJsonNames.SURCHARGE))
			.setWithholding(json.optBoolean(IJsonNames.WITHHOLDING))
			.setVatAccrualPayment(json.optBoolean(IJsonNames.VAT_ACCRUAL_PAYMENT))
			.seteInvoice(json.optBoolean(IJsonNames.E_INVOICE));
	}

	public static List<Company> fromJSON(JSONArray json) {
		LinkedList<Company> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static JSONObject toJSON(Company company) {
		return RegistryJSON.toJSON(company)
			.put(IJsonNames.ACTIVE, company.isActive())
			.put(IJsonNames.SURCHARGE, company.isSurcharge())
			.put(IJsonNames.WITHHOLDING, company.isWithholding())
			.put(IJsonNames.VAT_ACCRUAL_PAYMENT, company.isVatAccrualPayment())
			.put(IJsonNames.E_INVOICE, company.iseInvoice());
	}
}
