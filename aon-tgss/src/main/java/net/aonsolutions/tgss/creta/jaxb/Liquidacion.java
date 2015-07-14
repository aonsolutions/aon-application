package net.aonsolutions.tgss.creta.jaxb;

import java.util.List;

public interface Liquidacion<L extends LiquidacionMes> {
	
	CtaCot getCcc();
	
	String getTipo();
	
	Periodo getFechaControl();

	CtaCot getCccConcertado();

	Periodo getPeriodoDesde();
	Periodo getPeriodoHasta();
	
	List<L> getLiquidacionMes();

}
