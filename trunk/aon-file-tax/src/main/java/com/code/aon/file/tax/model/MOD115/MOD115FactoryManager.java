package com.code.aon.file.tax.model.MOD115;

public class MOD115FactoryManager {

	private static MOD115FactoryManager instance;
	
	public static MOD115FactoryManager getInstance() {
		if (instance == null ) {
			instance = new MOD115FactoryManager();
		}
		return instance;
	}
	
	private MOD115FactoryManager() {
		
	}
	
	public IMOD115Factory getFactory( MOD115Format format) throws InstantiationException, IllegalAccessException {
		return format.getFactory().newInstance();
	}

}
