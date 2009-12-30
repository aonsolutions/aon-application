package com.code.aon.finance.invoicing.engine;

import java.util.HashMap;
import java.util.Map;

import com.code.aon.finance.invoicing.InvoicingException;

public class InvoicingEngineFactory {

	public static final String CUSTOMER_FEE_ENGINE_KEY = "customerFeeEngine";
	
	private static InvoicingEngineFactory instance;
	
	private Map<String,IInvoicingEngine> engines;
	
	private InvoicingEngineFactory() {
		engines = new HashMap<String,IInvoicingEngine> ();
	}
	
	public static InvoicingEngineFactory getInstance() {
		if (instance == null) {
			instance = new InvoicingEngineFactory();
		}
		return instance;
	}

	public static IInvoicingEngine getInvoicingEngine( String key) throws InvoicingException{
		if (!getInstance().engines.containsKey(key)) {
			throw new InvoicingException( "Engine '" + key + "' not registered.");  
		}
		return getInstance().engines.get(key);
	}

	public static void register(String key,IInvoicingEngine engine) {
		getInstance().engines.put(key, engine);
	}
}