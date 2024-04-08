package solutions.aon.circe;

import java.util.Date;

public interface DueParserListener {

	default void onRegistroEntradaYPAE(String registroEntrada, String pae) {

	}

	default void onDatosPersonales(String docIdentidad, String nombre, String apellidos, String nacionalidad,
			String sexo, Date fechaNacimiento, int nss, String estadoCivil) {

	}
	
	default void onDomicilios(String domicilioResidencia, String domicilioFiscal, String domicilioNotificaciones) {
		
	}
	
	default void onNotificacionTGSS(int telefono, String email) {
		
	}
	
	default void onNotificacionAEAT(int prefijo, int telefono, String email) {
		
	}
	
	default void onComunicaciones(String comunicacion) {
		
	}
	
	default void onActividades(Date inicioActividad, int numTrabajadoras) {
		
	}
	
	default void onCentroActividad(int superficie, String domicilio) {
		
	}
	
	default void onCNAE(String cnae) {
		
	}
	
	default void onIAE(String iae) {
		
	}
	
	default void onLugarFueraDelLocal(String epigrafeAE, String tipoActividad, String provincia, String municipio, Date fechaInicio) {
		
	}
	
	
}