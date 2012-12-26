package com.code.aon.file.tax.model.MOD123;

public class MOD123FactoryManager {

	private static MOD123FactoryManager instance;
	
	public static MOD123FactoryManager getInstance() {
		if (instance == null ) {
			instance = new MOD123FactoryManager();
		}
		return instance;
	}
	
	private MOD123FactoryManager() {
		
	}
	
	public IMOD123Factory getFactory( MOD123Format format) throws InstantiationException, IllegalAccessException {
		return format.getFactory().newInstance();
	}

}
