package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.type.InvoiceType;

public class InvoiceCounter implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Map<InvoiceType, Integer> map = new EnumMap<>(InvoiceType.class);
	
	public Map<InvoiceType, Integer> getMap() {
		return map;
	}
	
	public InvoiceCounter setMap(Map<InvoiceType, Integer> map) {
		this.map = map;
		return this;
	}
}
