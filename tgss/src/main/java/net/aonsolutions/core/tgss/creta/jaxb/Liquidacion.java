package net.aonsolutions.core.tgss.creta.jaxb;

import java.util.List;

public interface Liquidacion<L extends LiquidacionMes, C extends CtaCot, P extends Periodo, D extends DatosLiquidacion, F extends FechaHoraRecaudacion> {
	
	C getCcc();
	void setCcc(C ccc);
	
	String getTipo();
	void setTipo(String tipo);
	
	Periodo getFechaControl();
	void setFechaControl(P fechaControl);

	C getCccConcertado();
	void setCccConcertado(C ccc);

	P getPeriodoDesde();
	void setPeriodoDesde(P periodoDesde);
	P getPeriodoHasta();
	void setPeriodoHasta(P periodoHasta);
	
	default F getFechaHoraRecaudacion(){
		throw new UnsupportedOperationException();
	}
	default void setFechaHoraRecaudacion(F fechaHoraRecaudacion){
		throw new UnsupportedOperationException();
	}
	
	default D getDatosLiquidacion(){
		throw new UnsupportedOperationException();
	}
	default List<L> getLiquidacionMes() {
		throw new UnsupportedOperationException();
	}

}
