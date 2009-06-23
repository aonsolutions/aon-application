package com.code.aon.finance.invoicing;

import java.util.HashMap;
import java.util.Map;

public class InvoicingEngineFactory {

	private static InvoicingEngineFactory instance;
	
	private Map<String,IInvoicingEngine> engines;
	
	private InvoicingEngineFactory() {
		engines = new HashMap<String,IInvoicingEngine> ();
	}
	
	public static IInvoicingEngine getInvoicingEngine( String key) throws InvoicingException{
		if (!getInstance().engines.containsKey(key)) {
			throw new InvoicingException( "Engine '" + key + "' not registered.");  
		}
		return getInstance().engines.get(key);
	}

	public static InvoicingEngineFactory getInstance() {
		if (instance == null) {
			instance = new InvoicingEngineFactory();
		}
		return instance;
	}

	public static void register(String key,IInvoicingEngine engine) {
		getInstance().engines.put(key, engine);
	}
}