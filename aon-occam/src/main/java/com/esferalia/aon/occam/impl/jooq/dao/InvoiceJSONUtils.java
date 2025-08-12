package com.esferalia.aon.occam.impl.jooq.dao;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.MimeType;

public class InvoiceJSONUtils {
	
	private static final String CONTENT_TYPE = "content_type";
	private static final String DOMAIN_NAME = "domain_name";
	private static final String DOMAIN_ID = "domain_id";
	private static final String ATTACH_TYPE = "attach_type";
	private static final String SOURCE = "source";
	
	private InvoiceJSONUtils() {
	}

	public static JSONObject buildInvoiceFileJSON(AONContext ctx, Domain domain, String user, Invoice invoice) {
		JSONObject json = new JSONObject();
		InvoiceDoc invoiceDoc = invoice.getDoc().orElse(null);
		if(invoiceDoc == null) invoiceDoc = InvoiceDocDAO.get(ctx, invoice.getDomain(), invoice.getId()).orElse(null);
		
		if(invoiceDoc != null) {
		    json.put(IJsonNames.URL, invoiceDoc.getUrl());
		    json.put(CONTENT_TYPE, invoiceDoc.getMimeType().getName());
			return json;
		} else {
			Attach invoiceAttach = AttachmentDAO.getInvoiceAttachStream(ctx
					, f -> f.getAttachModuleProperty().eq(invoice.getId()), true)
				.findFirst()
				.orElse(null);
			if (invoiceAttach != null && invoiceAttach.getId() != null) {
				JSONObject data = new JSONObject();
				data.put(DOMAIN_NAME, domain.getName());
				data.put(DOMAIN_ID, domain.getId());
				data.put(IJsonNames.ID, invoiceAttach.getId());
				data.put(ATTACH_TYPE, AttachType.INVOICE.getName());
				String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
				String path = "/ms/api/file/" +  result;	
				String url = "https://" + domain.getName() + path; 
			    json.put(IJsonNames.URL, url);
			    json.put(IJsonNames.PATH, path);
			    json.put(CONTENT_TYPE, invoiceAttach.getMimeType().getName());
				return json;
			} else if(invoice.isSales()){
				JSONObject data = new JSONObject();
				data.put(DOMAIN_NAME, domain.getName());
				data.put(DOMAIN_ID, domain.getId());
				data.put(IJsonNames.ID, invoice.getId());
				data.put(SOURCE, "invoice");
				data.put(IJsonNames.LOGIN, user);
			
				String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
				String path = "/ms/api/download_invoice_pdf?json=" +  result;
				String url = "https://" + domain.getName() + path;
			    json.put(IJsonNames.URL, url);
			    json.put(IJsonNames.PATH, path);
			    json.put(CONTENT_TYPE, MimeType.PDF.getName());
				return json;
			}	
		}
		return null;
	}

}
