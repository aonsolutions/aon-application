package net.aonsolutions.core.tgss.creta.jaxb;

import java.util.List;

public interface DatosMes<D extends Dato> {

	default List<D> getDato() {
		return getDatoSolicitado();
	}

	default List<D> getDatoSolicitado() {
		return getDato();
	}
	
}
