package net.aonsolutions.core.tgss.creta.jaxb;

import java.util.List;


import net.aonsolutions.core.tgss.creta.jaxb.Dato;

public interface DatosLiquidacion<D extends Dato> {
	
	default List<D> getDato() {
		return getDatoSolicitado();
	}

	default List<D> getDatoSolicitado() {
		return getDato();
	}
}
