package com.code.aon.file.tax.model.MOD111;

public class MOD111FactoryManager {

	private static MOD111FactoryManager instance;
	
	public static MOD111FactoryManager getInstance() {
		if (instance == null ) {
			instance = new MOD111FactoryManager();
		}
		return instance;
	}
	
	private MOD111FactoryManager() {
		
	}
	
	public IMOD111Factory getFactory( MOD111Format format) throws InstantiationException, IllegalAccessException {
		return format.getFactory().newInstance();
	}

}
