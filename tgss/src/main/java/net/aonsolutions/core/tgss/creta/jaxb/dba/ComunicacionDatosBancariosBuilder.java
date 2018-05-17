package net.aonsolutions.core.tgss.creta.jaxb.dba;

import java.util.LinkedList;
import java.util.List;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class ComunicacionDatosBancariosBuilder {

	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

	private int autorizado;
	private List<AccionDatosBancarios> accionDatosBancariosList;
	
	public ComunicacionDatosBancariosBuilder() {
		this.accionDatosBancariosList= new LinkedList<AccionDatosBancarios>();
	}

	public ComunicacionDatosBancarios createComunicacionDatosBancarios() {
		ComunicacionDatosBancarios comunicacionDatosBancarios = OBJECT_FACTORY
				.createComunicacionDatosBancarios();
		
		comunicacionDatosBancarios.setAutorizado(String.format("%08d",autorizado));
		comunicacionDatosBancarios.setReferenciaExterna(Utils.createReferenciaExterna());
		
		comunicacionDatosBancarios.getAccionDatosBancarios().addAll(accionDatosBancariosList);
		
		return comunicacionDatosBancarios;
	}

	public ComunicacionDatosBancariosBuilder setAutorizado(int autorizado) {
		this.autorizado = autorizado;
		return this;
	}
	
	public ComunicacionDatosBancariosBuilder addAccionDatosBancarios(AccionDatosBancarios accionDatosBancarios){
		accionDatosBancariosList.add(accionDatosBancarios);
		return this;
	}
	
	// ------------------------------------------------------------------------

	

}
