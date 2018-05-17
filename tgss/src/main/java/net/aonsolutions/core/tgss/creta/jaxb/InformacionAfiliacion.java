package net.aonsolutions.core.tgss.creta.jaxb;

public interface InformacionAfiliacion<P extends Peculiaridad> {
	
	String getOcupacion();
	String getTipoContrato();
	String getGrupoCotizacion();
	String getCoeficienteTiempoParcial();
	
	Peculiaridades<P> getPeculiaridades();
	
}
