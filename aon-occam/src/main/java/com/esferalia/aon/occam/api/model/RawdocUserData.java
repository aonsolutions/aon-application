package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.EnumMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;

public class RawdocUserData implements Serializable {

	private static final long serialVersionUID = -1902490494107861971L;
	
	private EnumMap<RawdocNature,EnumMap<RawdocStatus, RawdocNotice>> natureMap = new EnumMap<RawdocNature,EnumMap<RawdocStatus, RawdocNotice>>(RawdocNature.class);
	
	public EnumMap<RawdocNature, EnumMap<RawdocStatus, RawdocNotice>> getNatureMap() {
		return natureMap;
	}
	
	public RawdocUserData setNatureMap(EnumMap<RawdocNature, EnumMap<RawdocStatus, RawdocNotice>> natureMap) {
		this.natureMap = natureMap;
		return this;
	}
	
	public EnumMap<RawdocStatus, RawdocNotice> getInvoiceNotice() {
		if ( natureMap.get(RawdocNature.INVOICE) == null) {
			natureMap.put(RawdocNature.INVOICE, new EnumMap<RawdocStatus, RawdocNotice>(RawdocStatus.class));
		}
		return natureMap.get(RawdocNature.INVOICE);
	}
	
	public void append(RawdocUserData rud) {
		for(RawdocNature nature : rud.getNatureMap().keySet()) {
			for(RawdocStatus status : rud.getNatureMap().get(nature).keySet()) {
				if(this.getInvoiceNotice().containsKey(status)) {
					RawdocNotice notice = this.getInvoiceNotice().get(status);
					notice.setCount(notice.getCount() + rud.getNatureMap().get(nature).get(status).getCount()); 
					notice.getDomains().addAll(rud.getNatureMap().get(nature).get(status).getDomains());
					this.getInvoiceNotice().put(status, notice);
				} else {
					this.getInvoiceNotice()
						.put(status,rud.getNatureMap().get(nature).get(status));
				}
			}
		}
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		for(RawdocNature nature : natureMap.keySet()) {
			JSONObject natureJson = new JSONObject();
			for(RawdocStatus status : natureMap.get(nature).keySet()) {
				JSONObject noticeJson = new JSONObject();
				noticeJson.put("count", natureMap.get(nature).get(status).getCount());
				JSONArray domainsArray = new JSONArray();
				natureMap.get(nature).get(status).getDomains()
					.stream().forEach(r -> domainsArray.put(r));
				noticeJson.put("domains", domainsArray);
				natureJson.put(status.name().toLowerCase(), noticeJson);
			}
			json.put(nature.name().toLowerCase(), natureJson);
		}
		return json;
	}
	
}
