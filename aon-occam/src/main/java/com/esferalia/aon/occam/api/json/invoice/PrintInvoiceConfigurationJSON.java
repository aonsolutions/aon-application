package com.esferalia.aon.occam.api.json.invoice;

import java.util.Base64;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.type.MimeType;

public class PrintInvoiceConfigurationJSON {

	private PrintInvoiceConfigurationJSON() {

	}
	
	public static PrintInvoiceConfiguration fromJSON(JSONObject json) {
		Attach attach = null;
		if(json.opt(IJsonNames.BACKGROUND_ATTACH) != null) {
			JSONObject file = json.optJSONObject(IJsonNames.BACKGROUND_ATTACH);
			String base64 = file.optString("content");
			String contentType = file.optString("contentType");
			byte[] fileData = Base64.getDecoder().decode(base64);
			attach = new Attach()
					.setAttachType(AttachType.DATA)
					.setData(fileData)
					.setType(DataAttachType.REQUEST.value())
					.setMimeType(MimeType.safeValueFromContenType(contentType))
					.setSource(DataAttachSource.INVOICE_PRINT_CONFIGURATION.value());
		}
		return new PrintInvoiceConfiguration()
				.setDetailed(json.optBoolean(IJsonNames.DETAILED))
				.setAdjustImage(json.optBoolean(IJsonNames.ADJUST))
				.setFooter(json.opt(IJsonNames.FOOTER) != null ? json.optInt(IJsonNames.FOOTER) : 100)
				.setHeader(json.opt(IJsonNames.HEADER) != null ? json.optInt(IJsonNames.HEADER) : 100)
				.setLogo(json.optBoolean(IJsonNames.LOGO))
				.setCompany(json.optBoolean(IJsonNames.COMPANY))
				.setBorder(JsonUtils.optInteger(json, IJsonNames.BORDER))
				.setBackground(attach)
				.setRecordData(JsonUtils.getboolean(json, IJsonNames.RECORD_DATA))
				.setContactData(JsonUtils.getboolean(json, IJsonNames.CONTACT_DATA))
				.setLanguage(AonLanguage.safeValueOf(JsonUtils.getString(json, IJsonNames.LANGUAGE)))
				.setTheme(PrintInvoiceThemeConfigurationJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.THEME)));
	}
	
	public static JSONObject toJSON(PrintInvoiceConfiguration pic) {
		return new JSONObject()
				.put(IJsonNames.DETAILED, pic.isDetailed())
				.put(IJsonNames.ADJUST, pic.getAdjustImage())
				.put(IJsonNames.FOOTER, pic.getFooter())
				.put(IJsonNames.HEADER, pic.getHeader())
				.put(IJsonNames.LOGO, pic.isLogo())
				.put(IJsonNames.COMPANY, pic.isCompany())
				.put(IJsonNames.BORDER, pic.getBorder())
				.put(IJsonNames.BACKGROUND, pic.isBackground())
				.put(IJsonNames.RECORD_DATA, pic.isRecordData())
				.put(IJsonNames.CONTACT_DATA, pic.isContactData())
				.put(IJsonNames.LANGUAGE, pic.getLanguage().getLanguage())
				.put(IJsonNames.THEME, PrintInvoiceThemeConfigurationJSON.toJSON(pic.getTheme()));
	}
	
}
