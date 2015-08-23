package net.aonsolutions.tgss.creta.jaxb;

public interface Trabajador<D extends Dato> {
	
	String getNaf();

	Tramos<Tramo<D>> getTramos();
}
