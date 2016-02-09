package net.aonsolutions.tgss.creta.jaxb;


public interface FechaHoraRecaudacion<F extends Fecha> {
	
	F getFechaRecaudacion();
	void setFechaRecaudacion(F fecha);
	
	String getHoraRecaudacion();
	void setHoraRecaudacion(String hora);
}
