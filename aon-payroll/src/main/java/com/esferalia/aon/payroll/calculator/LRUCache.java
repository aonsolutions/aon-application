package com.esferalia.aon.payroll.calculator;

import java.util.LinkedHashMap;

@SuppressWarnings("serial")
public class LRUCache<K,V> extends LinkedHashMap<K,V> {

	private final int 				capacity;
	private LRUCacheFactory<K, V> 	factory;

	public LRUCache(int capacity, LRUCacheFactory<K, V> factory)
	{
		super(capacity + 1, 1.1f, true);
		this.capacity = capacity;
		this.factory = factory;
	}
	
	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > capacity;
	}

	@SuppressWarnings("unchecked")
	public V get(Object key) {
		if ( ! containsKey(key)) {
			super.put((K)key, factory.create((K)key));
		}
		return super.get(key) ;
	}


	
}
