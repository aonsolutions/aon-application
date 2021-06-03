package com.esferalia.aon.occam.api.json.invoice;

import java.util.Base64;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.type.MimeType;

public class PrintInvoiceConfigurationJSON {

	public static PrintInvoiceConfiguration fromJSON(JSONObject json) {
		Attach attach = null;
		if(json.opt(IJsonNames.BACKGROUND) != null) {
			JSONObject file = json.optJSONObject(IJsonNames.BACKGROUND);
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
				.setBackground(attach);
	}
	
	public static JSONObject toJSON(PrintInvoiceConfiguration pic) {
		return new JSONObject()
				.put(IJsonNames.DETAILED, pic.getDetailed())
				.put(IJsonNames.ADJUST, pic.getAdjustImage())
				.put(IJsonNames.FOOTER, pic.getFooter())
				.put(IJsonNames.HEADER, pic.getHeader());
	}
	
}
