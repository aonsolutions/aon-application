package com.esferalia.aon.occam.api.json.invoice;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceTheme;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceThemeConfiguration;

public class PrintInvoiceThemeConfigurationJSON {

	private PrintInvoiceThemeConfigurationJSON() {

	}
	
	public static PrintInvoiceThemeConfiguration fromJSON(JSONObject json) {
		return new PrintInvoiceThemeConfiguration()
				.setTheme(PrintInvoiceTheme.safeValueOf(JsonUtils.getString(json, IJsonNames.THEME)));
	}
	
	public static JSONObject toJSON(PrintInvoiceThemeConfiguration pic) {
		return new JSONObject()
				.put(IJsonNames.THEME, pic.getTheme().name());
	}
	
}
