package net.aonsolutions.core.tgss.creta.jaxb.dba;

import java.util.LinkedList;
import java.util.List;


public class AccionDatosBancariosBuilder {
	
	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
	
	public interface  Tipo {
		String getString();
	}
	
	public static enum TipoAccion implements Tipo {
		ALTA("1"),
		ELIMINACION("2");

		private String string;
		
		private TipoAccion(String string) {
			this.string = string;
		}

		@Override
		public String getString() {
			return string;
		}
	}

	public static enum TipoMoviento implements Tipo {
		DEUDORAS("C"),
		ACREEDORAS("S"),
		DEUDORAS_Y_ACREEDORAS("A");
		
		private String string;
		
		private TipoMoviento(String string) {
			this.string = string;
		}
		
		
		public String getString() {
			return string;
		}
	}
	
	public static enum TipoDocumento implements Tipo {
		DNI("1"),
		NIE("6"),
		CIF("9"),
		;

		private String string;
		
		private TipoDocumento(String string) {
			this.string = string;
		}
		
		public String getString() {
			return string;
		}
	
	}

	private String ccc;
	private String iban;
	private String nombreTitular;
	private String numeroDocumento;
	private TipoAccion tipoAccion;
	private TipoMoviento tipoMoviento;
	private TipoDocumento tipoDocumento;
	
	

	private List<DatosBancarios> datosBancarios;
	
	public AccionDatosBancariosBuilder() {
		this.datosBancarios = new LinkedList<DatosBancarios>();
	}
	
	public AccionDatosBancariosBuilder setCCC(String ccc) {
		this.ccc = ccc;
		return this;
	}
	
	public AccionDatosBancariosBuilder setIban(String iban) {
		this.iban = iban;
		return this;
	}
	
	public AccionDatosBancariosBuilder setNombreTitular(String nombreTitular) {
		this.nombreTitular = nombreTitular;
		return this;
	}
	
	public AccionDatosBancariosBuilder setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
		return this;
	}
	
	public AccionDatosBancariosBuilder setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
		return this;
	}
	
	public AccionDatosBancariosBuilder setTipoAccion(TipoAccion tipoAccion) {
		this.tipoAccion = tipoAccion;
		return this;
	}
	
	public AccionDatosBancariosBuilder setTipoMoviento(TipoMoviento tipoMoviento) {
		this.tipoMoviento = tipoMoviento;
		return this;
	}
	
	
	public AccionDatosBancarios createAccionDatosBancarios() {
		AccionDatosBancarios accionDatosBancarios = OBJECT_FACTORY.createAccionDatosBancarios();
		
		
		CtaCot ctaCot = OBJECT_FACTORY.createCtaCot();
		ctaCot.setRegimen(ccc.substring(0,4));
		ctaCot.setProvincia(ccc.substring(4,6));
		ctaCot.setNumero(ccc.substring(6));
		accionDatosBancarios.setCcc(ctaCot);
		
		accionDatosBancarios.setTipoAccion(tipoAccion.string);

		accionDatosBancarios.setTipoMovimiento(tipoMoviento.string);
		
		DatosBancarios datosBancarios = OBJECT_FACTORY.createDatosBancarios();
		datosBancarios.setIBAN(iban);
		
		Titular titular = OBJECT_FACTORY.createTitular();
		titular.setNombreTitular(nombreTitular);
		
		Ipf ipf = OBJECT_FACTORY.createIpf();
		ipf.setTipoIpf(tipoDocumento.string);
		ipf.setNumeroIpf(numeroDocumento);
		titular.setIpfTitular(ipf);

		datosBancarios.setTitular(titular);
		
		accionDatosBancarios.setDatosBancarios(datosBancarios);
		
		return accionDatosBancarios;
	}
	
	// ------------------------------------------------------------------------
	
	public static <T extends Tipo> T  valueOfTipo(Class<T> clazz, String string) {
		for ( T tipo : clazz.getEnumConstants()) {
			if ( tipo.getString().equals(string) ){
				return tipo;
			}
		}
		return null;
	}
	


	
}
