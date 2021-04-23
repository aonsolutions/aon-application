package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonNumberUtils;

public enum FiscalMatrixParamsJSON {

	YEAR{
		@Override
		public FiscalMatrixParams from(FiscalMatrixParams params, JSONObject json) {
			return params.setYear( AonNumberUtils.toint(  json.optNumber(IJsonNames.YEAR, null) ));
		}
		@Override
		public JSONObject to(FiscalMatrixParams params, JSONObject json) {
			return json.put(IJsonNames.YEAR, params.getYear());
		}
	},
	
	MODEL{
		@Override
		public FiscalMatrixParams from(FiscalMatrixParams params, JSONObject json) {
			return params.setModel(json.optString(IJsonNames.MODEL,null));
		}
		@Override
		public JSONObject to(FiscalMatrixParams params, JSONObject json) {
			return json.put(IJsonNames.MODEL, params.getModel());
		}
	},
	ADMINISTRATION{
		@Override
		public FiscalMatrixParams from(FiscalMatrixParams params, JSONObject json) {
			return params.setAdministration(Administration.safeValueOf( json.optString(IJsonNames.ADMINISTRATION,null) ));
		}
		@Override
		public JSONObject to(FiscalMatrixParams params, JSONObject json) {
			return json.put(IJsonNames.ADMINISTRATION, params.getAdministration().toString());
		}
	},
	CONFIGURED_VISIBLE{
		@Override
		public FiscalMatrixParams from(FiscalMatrixParams params, JSONObject json) {
			return params.setConfiguredVisible(json.optBoolean(IJsonNames.CONFIGURED_VISIBLE));
		}
		@Override
		public JSONObject to(FiscalMatrixParams params, JSONObject json) {
			return json.put(IJsonNames.CONFIGURED_VISIBLE, params.isConfiguredVisible());
		}
	},
//	private boolean madeModelsVisible;
	MADE_MODELS_VISIBLE{
		@Override
		public FiscalMatrixParams from(FiscalMatrixParams params, JSONObject json) {
			return params.setMadeModelsVisible(json.optBoolean(IJsonNames.MADE_MODELS_VISIBLE));
		}
		@Override
		public JSONObject to(FiscalMatrixParams params, JSONObject json) {
			return json.put(IJsonNames.MADE_MODELS_VISIBLE, params.isMadeModelsVisible());
		}
	},
	;

	public abstract FiscalMatrixParams from(FiscalMatrixParams params, JSONObject json);
	public abstract JSONObject to(FiscalMatrixParams model, JSONObject json);
	
	public static JSONObject toJSON(FiscalMatrixParams params) {
		if (params != null) {
			JSONObject json = new JSONObject();
			for (FiscalMatrixParamsJSON p : FiscalMatrixParamsJSON.values()) {
				p.to(params, json);
			}
			return json;
		}
		return null;
	}
	
	public static FiscalMatrixParams fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}

	public static FiscalMatrixParams fromJSON(JSONObject json) {
		if (json != null) {
			FiscalMatrixParams params = new FiscalMatrixParams();
			for (FiscalMatrixParamsJSON p : FiscalMatrixParamsJSON.values()) {
				p.from(params, json);
			}
			return params;
		}
		return null;
	}

}
