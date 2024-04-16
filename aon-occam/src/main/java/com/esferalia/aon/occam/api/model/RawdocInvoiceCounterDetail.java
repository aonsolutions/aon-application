package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.type.RawdocType;

public class RawdocInvoiceCounterDetail implements Serializable {

	private static final long serialVersionUID = 320474275458042903L;
		
	private Integer count;
	private Map<RawdocType,Integer> map = new EnumMap<>(RawdocType.class);
	
	public RawdocInvoiceCounterDetail() {
		count = 0;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public RawdocInvoiceCounterDetail setCount(Integer count) {
		this.count = count;
		return this;
	}
	
	public RawdocInvoiceCounterDetail addCount(Integer count) {
		setCount(getCount() + count);
		return this;
	}
	
	public Map<RawdocType, Integer> getMap() {
		return map;
	}
	
	public RawdocInvoiceCounterDetail setMap(Map<RawdocType, Integer> map) {
		this.map = map;
		return this;
	}
}