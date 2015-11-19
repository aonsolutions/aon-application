package com.code.aon.ui.loader.custom;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.AonVersion;

public class CustomLoaderFactoryManager implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static List<ICustomLoaderFactory> factories;
	
	static {
		factories = new LinkedList<ICustomLoaderFactory>();
		factories.add( new OppidumSalesLoader());
		factories.add( new OppidumPurchasesLoader());
	}
	
	public CustomLoaderFactoryManager() {
//		factories = new LinkedList<ICustomLoaderFactory>();
//		factories.add( new OppidumSalesLoader());
//		factories.add( new OppidumPurchasesLoader());
	}
	
	public static List<ICustomLoaderFactory> getFactories() {
		return factories;
	}
	

	public static ICustomLoaderFactory getFactory( byte[] data ) {
		for (ICustomLoaderFactory f : getFactories()) {
			if (f.accept(data)) {
				return f;
			}
		}
		return null;
	}
	
}
