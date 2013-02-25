package com.code.aon.file.tax.model.MOD310;

public class MOD310FactoryManager {

	private static MOD310FactoryManager instance;
	
	public static MOD310FactoryManager getInstance() {
		if (instance == null ) {
			instance = new MOD310FactoryManager();
		}
		return instance;
	}
	
	private MOD310FactoryManager() {
		
	}
	
	public IMOD310Factory getFactory( MOD310Format format) throws InstantiationException, IllegalAccessException {
		return format.getFactory().newInstance();
	}

}
