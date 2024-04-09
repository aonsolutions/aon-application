package solutions.aon.circe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface DueParserListener{

	default void onRegistroEntradaYPAE(String registroEntrada, String pae) {

	}

	default void onDatosPersonales(String docIdentidad, String nombre, String apellidos, String nacionalidad,
			String sexo, Date fechaNacimiento, String nss, String estadoCivil) {

	}
	
	default void onDomicilios(String domicilioResidencia, String domicilioFiscal, String domicilioNotificaciones) {
		
	}
	
	default void onNotificacionTGSS(String telefono, String email) {
		
	}
	
	default void onNotificacionAEAT(int prefijo, String telefono, String email) {
		
	}
	
	default void onComunicaciones(String comunicacion) {
		
	}
	
	default void onActividades(Date inicioActividad, int numTrabajadoras) {
		
	}
	
	default void onCentroActividad(float superficie, String domicilio) {
		
	}
	
	default void onCNAE(String cnae) {
		
	}
	
	default void onIAE(String iae) {
		
	}
	
	default void onLugarFueraDelLocal(String epigrafeAE, String tipoActividad, String provincia, String municipio, Date fechaInicio) {
		
	}
	
	default void onDeclaracionCensal(int codigo, String respuesta, Date fecha) {
		List<String> declaracionCensalList = new ArrayList<>();
		String declaracionCensal = codigo + " " + respuesta + " " + fecha;
		declaracionCensalList.add(declaracionCensal);
	}
	
	default void onSeguridadSocial(String tipo, Date fecha) {
		
	}
	
	default void onRegimenDeEncuadramiento(String regimen, String trl, String subgrupo, String grupo) {
		
	}
	
	default void onBaseCotizacion(float baseCotizacion, float rendimiento) {
		
	}
	
	default void onIncapacidadTemporal(String mutuaIT) {
		
	}
	
	default void onCobertura(String contingenciaProfesional, String ceseActividad) {
		
	}
	
	default void onReduccion(String reduccion) {
		
	}
	
	default void onOpcionCAFP(String opcion) {
		
	}
	
	default void onCuenta(String cuenta) {
		
	}
	
}