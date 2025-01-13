package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.type.RawdocNature;

public class InvoiceUserData implements Serializable {

	private static final long serialVersionUID = -1902490494107861971L;
	
	private EnumMap<InvoiceStatus, InvoiceNotice> invoiceNoticeMap = new EnumMap<>(InvoiceStatus.class);
	
	
	public Map<InvoiceStatus, InvoiceNotice> getInvoiceNotice() {
		return invoiceNoticeMap;
	}
	
	public void append(InvoiceUserData invoiceUserData) {
		for(InvoiceStatus status : invoiceUserData.getInvoiceNotice().keySet()) {
			this.getInvoiceNotice().merge(status, invoiceUserData.getInvoiceNotice().get(status), ( n1, n2 ) -> {
				n1.getDomains().addAll(n2.getDomains());
				n1.setCount( n1.getCount() + n2.getCount());
				n1.getDomainCount().putAll(n2.getDomainCount());
				return n1;
			});
		}
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		for(InvoiceStatus status : invoiceNoticeMap.keySet()) {
			JSONObject noticeJson = new JSONObject();
			noticeJson.put("count", invoiceNoticeMap.get(status).getCount());
			JSONArray domainsArray = new JSONArray();
			invoiceNoticeMap.get(status).getDomains()
				.stream().forEach(r -> domainsArray.put(r));
			noticeJson.put("domains", domainsArray);

			JSONObject domainCount = new JSONObject();
			invoiceNoticeMap.get(status).getDomainCount()
			.forEach((name,count) -> domainCount.put(name, count));
			noticeJson.put("domainCount", domainCount);
			
			json.put(status.name().toLowerCase(), noticeJson);
		}
		return json;
	}
	
}
