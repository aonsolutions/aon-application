package es.translogia.tedi.json;

import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediCompany;
import es.translogia.tedi.json.FunctionalInterfaces.ITediCompanyFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediCompanyToJSON;

public enum TediCompanyJSON {
	DOCUMENT((company, json) -> company.setDocument(json.getString(IConstants.DOCUMENT)),
			(company, json) -> json.put(IConstants.DOCUMENT, company.getDocument())),
	COMPANY((company, json) -> company.setCompany(json.optString(IConstants.COMPANY)),
			(company, json) -> json.put(IConstants.COMPANY, company.getCompany())),
	NAME((company, json) -> company.setName(json.getString(IConstants.NAME)),
			(company, json) -> json.put(IConstants.NAME, company.getName())),
	ALIAS((company, json) -> company.setAlias(json.optString(IConstants.ALIAS)),
			(company, json) -> json.put(IConstants.ALIAS, company.getAlias())),
	ACTIVE((company, json) -> company.setActive(json.optBoolean(IConstants.ACTIVE)),
			(company, json) -> json.put(IConstants.ACTIVE, company.getActive())),
	PLAN((company, json) -> {
			JSONObject jsonPlan = json.optJSONObject(IConstants.PLAN);
			return (jsonPlan != null) ? company.setPlan(TediPlanJSON.fromJSON(jsonPlan)) : company;
		}
		,(company, json) -> (company.getPlan() != null)
			? json.put(IConstants.PLAN, TediPlanJSON.toJSON(company.getPlan()))
			: json),
	IBAN((company, json) -> company.setIban(json.optString(IConstants.IBAN)),
			(company, json) -> json.put(IConstants.IBAN, company.getIban())),
	BIC((company, json) -> company.setBic(json.optString(IConstants.BIC)),
			(company, json) -> json.put(IConstants.BIC, company.getBic())),
	ADDRESS((company, json) -> {
		JSONObject jsonAddress = json.optJSONObject(IConstants.ADDRESS);
		return (jsonAddress != null) ? company.setAddress(TediAddressJSON.fromJSON(jsonAddress)) : company;
		}
		,(company, json) -> (company.getAddress() != null)
			? json.put(IConstants.ADDRESS, TediAddressJSON.toJSON(company.getAddress()))
			: json)
	;

	private ITediCompanyFromJSON fromJSON;
	private ITediCompanyToJSON toJSON;

	private TediCompanyJSON(ITediCompanyFromJSON fromJSON, ITediCompanyToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(TediCompany t) {
		JSONObject json = new JSONObject();
		for (TediCompanyJSON p : TediCompanyJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static TediCompany fromJSON(JSONObject json) {
		TediCompany emailInfo = new TediCompany();
		if (json != null && !json.isEmpty()) {
			for (TediCompanyJSON p : TediCompanyJSON.values()) {
				p.fromJSON.from(emailInfo, json);
			}
		}
		return emailInfo;
	}
}
