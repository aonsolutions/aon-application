package com.code.aon.file.tax.model.MOD303;

public class MOD303FactoryManager {

	private static MOD303FactoryManager instance;
	
	public static MOD303FactoryManager getInstance() {
		if (instance == null ) {
			instance = new MOD303FactoryManager();
		}
		return instance;
	}
	
	private MOD303FactoryManager() {
		
	}
	
	public IMOD303Factory getFactory( MOD303Format format) throws InstantiationException, IllegalAccessException {
		return format.getFactory().newInstance();
	}

}
