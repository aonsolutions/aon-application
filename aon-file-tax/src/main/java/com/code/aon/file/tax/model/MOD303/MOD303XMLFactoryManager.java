package com.code.aon.file.tax.model.MOD303;

public class MOD303XMLFactoryManager {

	private static MOD303XMLFactoryManager instance;
	
	public static MOD303XMLFactoryManager getInstance() {
		if (instance == null ) {
			instance = new MOD303XMLFactoryManager();
		}
		return instance;
	}
	
	private MOD303XMLFactoryManager() {
		
	}
	
	public IMOD303XMLFactory getFactory( MOD303Format format) {
		if (format == MOD303Format.ALAVA_2010) {
			return new Alava2010MOD303XMLFactory();
		}
		return null;
	}

}
