package net.aonsolutions.core.tgss.creta.jaxb;

import java.util.List;

public interface Trabajadores<T extends Trabajador> {
	
	List<T> getTrabajador();
}
