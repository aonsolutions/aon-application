package net.aonsolutions.occam.api.json;

import org.json.JSONObject;

import net.aonsolutions.occam.api.model.Company;

public class CompanyJSON {
	
	private CompanyJSON() {
	}
	
	public static Company fromJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return null;
		Company company  = 
			RegistryJSON.fromJSON(json, Company::new )
			.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
			.setSurcharge(JsonUtils.getboolean(json, IJsonNames.SURCHARGE))
			.setWithholding(JsonUtils.getboolean(json, IJsonNames.WITHHOLDING))
			.setVatAccrualPayment(JsonUtils.getboolean(json, IJsonNames.VAT_ACCRUAL_PAYMENT))
			.seteInvoice(JsonUtils.getboolean(json, IJsonNames.E_INVOICE));
		;
		return company;
	}

	public static JSONObject toJSON(Company company) {
		if (company == null) return null;
		return RegistryJSON.toJSON(company)
			.put(IJsonNames.ACTIVE, company.isActive())
			.put(IJsonNames.SURCHARGE, company.isSurcharge())
			.put(IJsonNames.WITHHOLDING, company.isWithholding())
			.put(IJsonNames.VAT_ACCRUAL_PAYMENT, company.isVatAccrualPayment())
			.put(IJsonNames.E_INVOICE, company.iseInvoice());
	}
}
