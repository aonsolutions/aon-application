package net.aonsolutions.tgss.creta.jaxb;

public interface Tramo<D extends Dato> {
	
	Fecha getFechaDesde();
	Fecha getFechaHasta();
	
	DatosTramo<D> getDatosTramo();
	
	default InformacionAfiliacion<?> getInformacionAfiliacion(){
		throw new UnsupportedOperationException();
	};
	

}
