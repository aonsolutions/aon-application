package com.esferalia.aon.occam.api.model.fiscal.modules;

public interface IEpigraph {
	
	public String getEpigraph();
	public String getDescription();
	public double getVatPorc();
	public double getPorcMin();
	public double getLimExceso();
	public Module[] getIRPFModules();

}
