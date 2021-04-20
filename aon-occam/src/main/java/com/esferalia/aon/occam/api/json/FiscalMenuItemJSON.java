package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;

public enum FiscalMenuItemJSON {

	ID {
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.ID, model.getId());
		}
	},
	DOMAIN{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.DOMAIN, model.getDomain());
		}
	},
	DOMAIN_NAME{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.DOMAIN_NAME, model.getDomainName());
		}
	},
	ADMINISTRATION{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.ADMINISTRATION, model.getAdministration().toString());
		}
	},
	MODEL{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.MODEL, model.getModel().getValue());
		}
	},
	YEAR{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.YEAR, model.getYear());
		}
	},
	PERIOD{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.PERIOD, model.getPeriod() == null?"":model.getPeriod().toString());
		}
	},
	STATUS{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.STATUS, model.getStatus().toString());
		}
	},
	COMPLEMENTARY{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.COMPLEMENTARY, model.isComplementary());
		}
	},
	REPLACEMENT{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.REPLACEMENT, model.isReplacement());
		}
	},
	DOCUMENT{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.DOCUMENT, model.getDocument());
		}
	},
	NAME{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.NAME, model.getName());
		}
	},
	SURNAME{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.SURNAME, model.getSurname());
		}
	},
	IBAN{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.IBAN, (model.getFinance() != null && model.getFinance().getBankAccount() != null)?model.getFinance().getBankAccount().getIban():"");
		}
	},
	BANNK_ALIAS{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.BANK_ALIAS, (model.getFinance() != null)?model.getFinance().getBankAlias():"");
		}
	},
	RESULT{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.RESULT, model.getResult());
		}
	},
	DECLARATION_TYPE{
		@Override
		public JSONObject to(IFiscalModel model, JSONObject json) {
			return json.put(IJsonNames.TYPE, model.getDeclarationType());
		}
	},
	;

	public abstract JSONObject to(IFiscalModel model, JSONObject json);
	
	public static JSONObject toJSON(IFiscalModel model) {
		if (model != null) {
			JSONObject json = new JSONObject();
			for (FiscalMenuItemJSON p : FiscalMenuItemJSON.values()) {
				p.to(model, json);
			}
			return json;
		}
		return null;
	}

}
