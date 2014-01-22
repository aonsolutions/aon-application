package com.code.aon.file.tax.model.MOD311;

public class MOD311FactoryManager {

	private static MOD311FactoryManager instance;
	
	public static MOD311FactoryManager getInstance() {
		if (instance == null ) {
			instance = new MOD311FactoryManager();
		}
		return instance;
	}
	
	private MOD311FactoryManager() {
		
	}
	
	public IMOD311Factory getFactory( MOD311Format format) throws InstantiationException, IllegalAccessException {
		return format.getFactory().newInstance();
	}

}
