package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Company;

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
	
	public static JSONObject toJSON(Company company) {
		return RegistryJSON.toJSON(company)
			.put(IJsonNames.ACTIVE, company.isActive())
			.put(IJsonNames.SURCHARGE, company.isSurcharge())
			.put(IJsonNames.WITHHOLDING, company.isWithholding())
			.put(IJsonNames.VAT_ACCRUAL_PAYMENT, company.isVatAccrualPayment())
			.put(IJsonNames.E_INVOICE, company.iseInvoice());
	}
}
