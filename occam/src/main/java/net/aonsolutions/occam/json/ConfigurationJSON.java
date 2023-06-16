package net.aonsolutions.occam.json;

import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.Configuration;

public class ConfigurationJSON {
	
	private ConfigurationJSON() {
	}
	
	public static Configuration from(JSONObject json) {
		if (json == null) return null; 
		return new Configuration()
			.setAccounting( AccountingConfigurationJSON.from(AonJSONUtils.getObject(json, AonNames.ACCOUNTING)))
		;
	}
	
	public static JSONObject to(Configuration config) {
		if (config == null) return null;
		return new JSONObject()
			.putOpt(AonNames.ACCOUNTING, config.accounting().map(AccountingConfigurationJSON::to).orElse(null) )
			;
	}
}
