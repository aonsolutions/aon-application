package com.code.aon.file.tax.model.MOD131;

public class MOD131FactoryManager {

	private static MOD131FactoryManager instance;
	
	public static MOD131FactoryManager getInstance() {
		if (instance == null ) {
			instance = new MOD131FactoryManager();
		}
		return instance;
	}
	
	private MOD131FactoryManager() {
		
	}
	
	public IMOD131Factory getFactory( MOD131Format format) throws InstantiationException, IllegalAccessException {
		return format.getFactory().newInstance();
	}

}
