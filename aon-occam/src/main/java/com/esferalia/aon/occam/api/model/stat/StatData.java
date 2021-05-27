package com.esferalia.aon.occam.api.model.stat;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class StatData<R,C,D> implements Serializable {

	private static final long serialVersionUID = -2437329320797925654L;
	
	private LinkedHashMap<R, LinkedHashMap<C,D>> map = 
			new LinkedHashMap<R, LinkedHashMap<C,D>>();

	public LinkedHashMap<R, LinkedHashMap<C, D>> getMap() {
		return map;
	}

	public void setMap(LinkedHashMap<R, LinkedHashMap<C, D>> map) {
		this.map = map;
	} 

	public D get(R row, C col) {
		if (map.containsKey(row)) {
			return map.get(row).get(col);
		}
		return null;
	}

	public StatData<R,C,D> put(R row, C col, D e) {
		LinkedHashMap<C,D> rowMap = map.get(row);
		if (rowMap == null) {
			rowMap = new LinkedHashMap<C, D>();
			map.put(row, rowMap);
		}
		rowMap.put(col, e);
		return this;
	}
	
	public boolean isEmpty() {
		return map.size() == 0;
	}
}
