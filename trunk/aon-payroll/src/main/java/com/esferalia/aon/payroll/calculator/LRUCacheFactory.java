package com.esferalia.aon.payroll.calculator;

public interface LRUCacheFactory<K, V> {
	V create(K key);
}
