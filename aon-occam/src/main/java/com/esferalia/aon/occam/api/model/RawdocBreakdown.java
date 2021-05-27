package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.EnumMap;

import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;

public class RawdocBreakdown implements Serializable {

	private static final long serialVersionUID = 320474275458042903L;
	
	private EnumMap<RawdocType,EnumMap<RawdocStatus,Integer>> map = new EnumMap<RawdocType,EnumMap<RawdocStatus,Integer>>(RawdocType.class);
	
	
	public EnumMap<RawdocType, EnumMap<RawdocStatus, Integer>> getTypeMap() {
		return map;
	}
	public EnumMap<RawdocStatus, Integer> getStatusMap( RawdocType type) {
		return map.get(type);
	}
	
	public RawdocBreakdown putBreakdown(RawdocType type,RawdocStatus status, int count) {
		EnumMap<RawdocStatus,Integer> statuses = map.get(type);
		if (statuses == null) {
			statuses = new EnumMap<RawdocStatus,Integer>(RawdocStatus.class);
			map.put(type, statuses);
		}
		statuses.put(status, count);
		return this;
	}
	
}
