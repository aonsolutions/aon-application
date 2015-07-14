package net.aonsolutions.tgss.creta.jaxb;

public interface Trabajador {
	
	String getNaf();

	default TIpf getIpf(){
		throw new UnsupportedOperationException();
	}
	
	Tramos<Tramo> getTramos();
}
