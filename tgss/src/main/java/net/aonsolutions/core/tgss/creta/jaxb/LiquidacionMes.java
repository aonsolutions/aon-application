package net.aonsolutions.core.tgss.creta.jaxb;


public interface LiquidacionMes<D extends DatosMes<Dato>> {
	
	Periodo getMesLiquidativo();
	
	default D getDatosMes() {
		throw new UnsupportedOperationException();
	}

	Trabajadores<Trabajador> getTrabajadores();
	

}
