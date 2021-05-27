package net.aonsolutions.core.tgss.jaxb.trabajadorestramos;

import java.util.ArrayList;
import java.util.List;

import net.aonsolutions.core.tgss.creta.jaxb.dba.AccionDatosBancariosBuilder.Tipo;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.ObjectFactory;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TIpf;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramo;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramos;

public class TrabajadorBuilder {

	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

	public static enum TipoIpf {
		DNI("1"),
		NIE("6"),
		CIF("9"),
		;

		private String string;
		
		private TipoIpf(String string) {
			this.string = string;
		}
		
		public String getString() {
			return string;
		}
	
	}

	private String naf;
	private TipoIpf tipoIpf;
	private String numeroIpf;
	private List<Tramo> tramos;

	private String name = "";
	private String firstSurname = "";
	private String secondSurname = "";

	public TrabajadorBuilder() {
		tramos = new ArrayList<Tramo>();
	}

	public Trabajador create() {
		
		Trabajador trabajador = OBJECT_FACTORY.createTrabajador();

		trabajador.setNaf(naf);
		
		TIpf tIpf = OBJECT_FACTORY.createTIpf();
		tIpf.setTipoIpf(tipoIpf.getString());
		tIpf.setNumeroIpf(String.format("%s",numeroIpf)); // TODO: 9 posiciones
		trabajador.setIpf(tIpf);

		Tramos tram0s = OBJECT_FACTORY.createTramos();
		tram0s.getTramo().addAll(tramos);
		trabajador.setTramos(tram0s);
		
		StringBuffer nafBuffer = new StringBuffer();
		nafBuffer.append(firstSurname.substring(0, Math.min(firstSurname.length(),2)));
		nafBuffer.append(secondSurname.substring(0, Math.min(secondSurname.length(),2)));
		nafBuffer.append(name.substring(0, Math.min(name.length(),1)));
		trabajador.setCaf(nafBuffer.toString());

		return trabajador;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	

	public void setTipoIpf(TipoIpf tipoIpf) {
		this.tipoIpf = tipoIpf;
	}
	
	public void setNumeroIpf(String numeroIpf) {
		this.numeroIpf = numeroIpf;
	}

	public TrabajadorBuilder setNaf(String naf) {
		this.naf = naf;
		return this;
	}
	
	public void setFirstSurname(String firstSurname) {
		this.firstSurname = firstSurname;
	}
	
	
	public void setSecondSurname(String secondSurname) {
		this.secondSurname = secondSurname;
	}
	

	public TrabajadorBuilder addTramo(Tramo tramo) {
		tramos.add(tramo);
		return this;
	}
	
	
	

}
