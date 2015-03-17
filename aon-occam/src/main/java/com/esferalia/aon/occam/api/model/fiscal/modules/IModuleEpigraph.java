package com.esferalia.aon.occam.api.model.fiscal.modules;


public interface IModuleEpigraph {
	
	String getEpigraph();
	String getDescription();
	Module[] getIrpfModules();
	Module[] getIvaModules();
	double getPorcMin();
	double getLimPers();
	double getLimExceso();

}
