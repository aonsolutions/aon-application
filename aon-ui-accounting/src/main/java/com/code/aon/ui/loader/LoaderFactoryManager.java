package com.code.aon.ui.loader;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.ui.loader.factory.AccountEntryDetailLoaderFactory;
import com.code.aon.ui.loader.factory.AccountEntryLoaderFactory;
import com.code.aon.ui.loader.factory.CreditorLoaderFactory;
import com.code.aon.ui.loader.factory.CustomerLoaderFactory;
import com.code.aon.ui.loader.factory.FinanceLoaderFactory;
import com.code.aon.ui.loader.factory.InvoiceDetailLoaderFactory;
import com.code.aon.ui.loader.factory.InvoiceLoaderFactory;
import com.code.aon.ui.loader.factory.SupplierLoaderFactory;
import com.code.aon.ui.loader.pojo.ILoadedPojo;

public class LoaderFactoryManager {
	
	private List<ILoaderFactory<ILoadedPojo>> factories;
	
	public LoaderFactoryManager(ILoaderEngine engine) {
		factories = new LinkedList<ILoaderFactory<ILoadedPojo>>();
		
		factories.add( new CustomerLoaderFactory(engine));
		factories.add( new CreditorLoaderFactory(engine));
		factories.add( new SupplierLoaderFactory(engine));
		factories.add( new FinanceLoaderFactory(engine));
		factories.add( new InvoiceLoaderFactory(engine));
		factories.add( new InvoiceDetailLoaderFactory(engine));
		factories.add( new AccountEntryLoaderFactory(engine));
		factories.add( new AccountEntryDetailLoaderFactory(engine));
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

	public ILoaderFactory<ILoadedPojo> getFactory(Class<? extends ILoadedPojo> clazz) {
		for (ILoaderFactory<ILoadedPojo> f : getFactories()) {
			if (f.accept(clazz)) {
				return f;
			}
		}
		return null;
	}
	
}
