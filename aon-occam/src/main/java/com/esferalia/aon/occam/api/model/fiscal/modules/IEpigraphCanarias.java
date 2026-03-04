package com.esferalia.aon.occam.api.model.fiscal.modules;

public interface IEpigraphCanarias {
	
	public String getEpigraph();
	public String getDescription();
	public double getPorcIng();         // Porcentaje de ingreso a cuenta
	public double getPorcMin();         // Porcentaje de cuota mínima
	public Module[] getIgicModules();
	public default int getSpecialEpigraph() {  // Indicador auxiliar de actividad para determinados epígrafes 
		return 0;
	};
	
}
