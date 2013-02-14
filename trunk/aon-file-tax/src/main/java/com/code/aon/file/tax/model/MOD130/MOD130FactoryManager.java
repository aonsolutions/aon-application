package com.code.aon.file.tax.model.MOD130;

public class MOD130FactoryManager {

	private static MOD130FactoryManager instance;
	
	public static MOD130FactoryManager getInstance() {
		if (instance == null ) {
			instance = new MOD130FactoryManager();
		}
		return instance;
	}
	
	private MOD130FactoryManager() {
		
	}
	
	public IMOD130Factory getFactory( MOD130Format format) throws InstantiationException, IllegalAccessException {
		return format.getFactory().newInstance();
	}

}
