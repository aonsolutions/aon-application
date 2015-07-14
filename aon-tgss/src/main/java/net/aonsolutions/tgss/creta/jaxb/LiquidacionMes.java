package net.aonsolutions.tgss.creta.jaxb;


public interface LiquidacionMes<D extends DatosMes<Dato>> {
	
	Periodo getMesLiquidativo();
	
	D getDatosMes();

	Trabajadores<Trabajador> getTrabajadores();
	

}
