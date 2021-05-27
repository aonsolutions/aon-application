package net.aonsolutions.core.tgss.creta.jaxb;

public interface Trabajador<D extends Dato> {
	
	String getNaf();

	default Tramos<Tramo<D>> getTramos() {
		throw new UnsupportedOperationException();
	}
}
