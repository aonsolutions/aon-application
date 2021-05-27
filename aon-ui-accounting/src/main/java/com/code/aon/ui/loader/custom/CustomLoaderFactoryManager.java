package com.code.aon.ui.loader.custom;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.AonVersion;
import com.code.aon.common.util.AonFile;

public class CustomLoaderFactoryManager implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static String SALES = "SALES";
	private final static String PURCHASE = "PURCHASE";
	
	private static Map<String, ICustomLoaderFactory> oppidumFactories;
	
	static {
		oppidumFactories = new HashMap<String, ICustomLoaderFactory>();
		oppidumFactories.put(SALES, new OppidumSalesLoader());
		oppidumFactories.put(PURCHASE, new OppidumPurchasesLoader());
	}
		
	public static ICustomLoaderFactory getFactory( AonFile aonFile ) {
		ICustomLoaderFactory acceptedFactory = null;
		if(aonFile.getFileName().matches(".*[vV][eE][nN][tT][aA][sS].*")
				&& oppidumFactories.get(SALES).accept(aonFile.getData())){
			acceptedFactory = oppidumFactories.get(SALES);
		} else if(aonFile.getFileName().matches(".*[cC][oO][mM][pP][rR][aA][sS].*")
			&& oppidumFactories.get(PURCHASE).accept(aonFile.getData())){
			acceptedFactory = oppidumFactories.get(PURCHASE);
		}
		if(acceptedFactory==null || !acceptedFactory.accept(aonFile.getData())) {
			for (ICustomLoaderFactory fact : oppidumFactories.values()) {
				if (fact.accept(aonFile.getData())) {
					acceptedFactory = fact;
				}
			}
		}
		return acceptedFactory;
	}
	
}
