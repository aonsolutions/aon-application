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
				.setTheme(PrintInvoiceTheme.safeValueOf(JsonUtils.getString(json, IJsonNames.THEME)))
				.setBoxBodyBackgroundColor(JsonUtils.getString(json, IJsonNames.BOX_BODY_BACKGROUND_COLOR))
				.setTitleTextColor(JsonUtils.getString(json, IJsonNames.TITLE_TEXT_COLOR))
				.setBoxTitleBackgroundColor(JsonUtils.getString(json, IJsonNames.BOX_TITLE_BACKGROUND_COLOR))
				.setBoxTitleTextColor(JsonUtils.getString(json, IJsonNames.BOX_TITLE_TEXT_COLOR))
				.setBorderColor(JsonUtils.getString(json, IJsonNames.BOX_BORDER_COLOR))
				.setCustomerBackgroundColor(JsonUtils.getString(json, IJsonNames.CUSTOMER_BACKGROUND_COLOR))
				.setTextColor(JsonUtils.getString(json, IJsonNames.TEXT_COLOR));
	}
	
	public static JSONObject toJSON(PrintInvoiceThemeConfiguration pic) {
		return new JSONObject()
				.put(IJsonNames.THEME, pic.getTheme().name())
				.put(IJsonNames.BOX_BODY_BACKGROUND_COLOR, pic.getBoxBodyBackgroundColorHTML())
				.put(IJsonNames.TITLE_TEXT_COLOR, pic.getTitleTextColorHTML())
				.put(IJsonNames.BOX_TITLE_BACKGROUND_COLOR, pic.getBoxTitleBackgroundColorHTML())
				.put(IJsonNames.BOX_TITLE_TEXT_COLOR, pic.getBoxTitleTextColorHTML())
				.put(IJsonNames.BOX_BORDER_COLOR, pic.getBorderColor())
				.put(IJsonNames.CUSTOMER_BACKGROUND_COLOR, pic.getCustomerBackgroundColorHTML())
				.put(IJsonNames.TEXT_COLOR, pic.getTextColorHTML());
	}
	
}
