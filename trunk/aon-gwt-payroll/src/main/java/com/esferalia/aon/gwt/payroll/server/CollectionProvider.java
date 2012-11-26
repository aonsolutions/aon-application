package com.esferalia.aon.gwt.payroll.server;

import java.util.Arrays;
import java.util.Collection;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.google.gwt.user.client.rpc.core.java.util.Collections;

public class CollectionProvider implements ICollectionProvider {
	
	private Collection collection;
	
	public CollectionProvider(Object... objects) {
		collection = Arrays.asList(objects);
	}
	
	
	@Override
	public Collection getCollection() {
		return collection;
	}

	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}

}
