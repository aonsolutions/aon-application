package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.type.RawdocStatus;

public class RawdocInvoiceCounter implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Map<RawdocStatus, RawdocInvoiceCounterDetail> map = new EnumMap<>(RawdocStatus.class);
	
	public Map<RawdocStatus, RawdocInvoiceCounterDetail> getMap() {
		return map;
	}
	
	public RawdocInvoiceCounter setMap(Map<RawdocStatus, RawdocInvoiceCounterDetail> map) {
		this.map = map;
		return this;
	}
}
