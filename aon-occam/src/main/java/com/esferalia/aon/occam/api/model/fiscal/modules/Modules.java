package com.esferalia.aon.occam.api.model.fiscal.modules;


public class Modules {
	
	public IModuleEpigraph[] getModuleEpigraphs(int year) {
		if (year == 2015) {
			return Modules2015.Epigraph.values();
		}
		return null;
	}
	
	
	

}
