package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.shared.HasId;
import com.google.gwt.view.client.ProvidesKey;

public class HasIdKeyProvider<T extends HasId<?>> implements ProvidesKey<T> {

	@Override
	public Object getKey(T item) {
		return item.getId();
	}

	public static <T extends HasId<?>> HasIdKeyProvider<T> getKeyProvider() {
		return new HasIdKeyProvider<T>();
	}
}