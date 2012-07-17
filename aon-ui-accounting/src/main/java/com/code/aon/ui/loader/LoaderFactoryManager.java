package com.code.aon.ui.loader;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.ui.loader.factory.CreditorLoaderFactory;
import com.code.aon.ui.loader.factory.CustomerLoaderFactory;
import com.code.aon.ui.loader.factory.FinanceLoaderFactory;
import com.code.aon.ui.loader.factory.InvoiceDetailLoaderFactory;
import com.code.aon.ui.loader.factory.InvoiceLoaderFactory;
import com.code.aon.ui.loader.factory.SupplierLoaderFactory;
import com.code.aon.ui.loader.pojo.ILoadedPojo;

public class LoaderFactoryManager {
	
	private List<ILoaderFactory<ILoadedPojo>> factories;
	
	public LoaderFactoryManager(ILoaderIdCache cache) {
		factories = new LinkedList<ILoaderFactory<ILoadedPojo>>();
		
		factories.add( new CustomerLoaderFactory(cache));
		factories.add( new CreditorLoaderFactory(cache));
		factories.add( new SupplierLoaderFactory(cache));
		factories.add( new FinanceLoaderFactory(cache));
		factories.add( new InvoiceLoaderFactory(cache));
		factories.add( new InvoiceDetailLoaderFactory(cache));
	}
	
	public List<ILoaderFactory<ILoadedPojo>> getFactories() {
		return factories;
	}
	
	public boolean accept( String key ) {
		for (ILoaderFactory<ILoadedPojo> f : getFactories()) {
			if (f.accept(key)) {
				return true;
			}
		}
		return false;
	}

	public ILoaderFactory<ILoadedPojo > getFactory( String key ) {
		for (ILoaderFactory<ILoadedPojo> f : getFactories()) {
			if (f.accept(key)) {
				return f;
			}
		}
		return null;
	}
	
}
