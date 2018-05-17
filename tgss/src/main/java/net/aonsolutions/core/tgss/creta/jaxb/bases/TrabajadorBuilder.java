package net.aonsolutions.core.tgss.creta.jaxb.bases;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;

public class TrabajadorBuilder {

	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

	private String naf;
	private List<Tramo> tramos;

	public TrabajadorBuilder() {
		tramos = new ArrayList<Tramo>();
	}

	public Trabajador create() {
		
		Trabajador trabajador = OBJECT_FACTORY.createTrabajador();

		trabajador.setNaf(naf);

		Tramos tram0s = OBJECT_FACTORY.createTramos();
		tram0s.getTramo().addAll(tramos);
		trabajador.setTramos(tram0s);

		return trabajador;
	}

	public TrabajadorBuilder setNaf(String naf) {
		this.naf = naf;
		return this;
	}
	

	public TrabajadorBuilder addTramo(Tramo tramo) {
		tramos.add(tramo);
		return this;
	}
	

}
