package com.esferalia.aon.salary.expression;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

class SnapshotMap<K,V> implements Map<K, V> {
	
	private Map<K, V> snapshot;
	private Map<K, V> original;
	
	
	public SnapshotMap(Set<K> keys, Map<K, V> original) {
		snapshot = new HashMap<K, V>();
		for (K key : keys) {
			if ( original.containsKey(key) ) {
				V value = original.get(key);
				snapshot.put(key, getSnapShot(value));
			}
		}
		this.original = original;
	}
	
	protected V getSnapShot(V value) {
		return value;
	}
	@Override
	public int size() {
		return snapshot.size() + original.size();
	}

	@Override
	public boolean isEmpty() {
		return snapshot.isEmpty() && original.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return snapshot.containsKey(key) || original.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return snapshot.containsValue(value) || original.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return snapshot.containsKey(key) ? snapshot.get(key) : original.get(key);
	}

	@Override
	public V put(K key, V value) {
		return original.put(key, value );
	}

	@Override
	public V remove(Object key) {
		return original.remove(key );
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		original.putAll(m);
	}

	@Override
	public void clear() {
		snapshot.clear();
		original.clear();
	}

	@Override
	public Set<K> keySet() {
		Set<K> keySet = new HashSet<K>();
		keySet.addAll(snapshot.keySet());
		keySet.addAll(original.keySet());
		return keySet;
	}

	@Override
	public Collection<V> values() {
		Collection<V> values = 
			new LinkedList<V>();
		values.addAll(snapshot.values());
		Set<Entry<K, V>> originalEntries = 
			original.entrySet();
		for (Entry<K, V> originalEntry : originalEntries) {
			if ( !snapshot.containsKey(originalEntry.getKey()) ){
				values.add(originalEntry.getValue());
			}
		}
		return values;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		Set<java.util.Map.Entry<K, V>> entrySet = 
			new HashSet<Map.Entry<K,V>>();
		
		entrySet.addAll(snapshot.entrySet());
		entrySet.addAll(original.entrySet());
		return entrySet;
	}
	
}
