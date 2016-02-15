package com.esferalia.aon.gwt.viewer.server;

import java.util.LinkedHashMap;

@SuppressWarnings("serial")
public class ViewerCache<K,V> extends LinkedHashMap<K,V> {

	private final int capacity;

	public ViewerCache(int capacity){
		super(capacity + 1, 1.1f, true);
		this.capacity = capacity;
	}
	
	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > capacity;
	}
}
