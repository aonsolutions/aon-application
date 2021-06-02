package com.esferalia.aon.occam.api.json.invoice;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;

public class PrintInvoiceConfigurationJSON {

	public static PrintInvoiceConfiguration fromJSON(JSONObject json) {
		return new PrintInvoiceConfiguration()
				.setDetailed(json.optBoolean(IJsonNames.DETAILED))
				.setAdjustImage(json.optBoolean(IJsonNames.ADJUST))
				.setFooter(json.opt(IJsonNames.FOOTER) != null ? json.optInt(IJsonNames.FOOTER) : 100)
				.setHeader(json.opt(IJsonNames.HEADER) != null ? json.optInt(IJsonNames.HEADER) : 100);
	}
	
	public static JSONObject toJSON(PrintInvoiceConfiguration pic) {
		return new JSONObject()
				.put(IJsonNames.DETAILED, pic.getDetailed())
				.put(IJsonNames.ADJUST, pic.getAdjustImage())
				.put(IJsonNames.FOOTER, pic.getFooter())
				.put(IJsonNames.HEADER, pic.getHeader());
	}
	
}
