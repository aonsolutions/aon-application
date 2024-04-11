package solutions.aon.circe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface SRLDueParserListener {

	default void onRegistroEntradaYPAE(String registroEntrada, String pae) {

	}

	default void onEmpresaActividad(Date inicioActividad, Date cierreEjercicio) {

	}

	default void onEmpresaDatosJuridicos(String duracionPersonaJuridica, String denominacionSocial, float capitalSocial,
			boolean acreditaCapitalSocial, boolean estatutosTipo) {

	}
	
	default void onPersonasTrabajadoras(int numPersonasTrabajadoras) {
		
	}
	
	default void onDomicilio(String domicilioSocial, String domicilioFiscal, String domicilioNotificaciones) {
		
	}
	
	default void onNotificacionesTGSS(String telefono, String email) {
		
	}
	
	default void onMedioNotificacion(String medioNotificacion) {
		
	}
	
	default void onNotiicacionAEAT(String prefijo, String telefono, String email) {
		
	}
	
	default void onComunicaciones(boolean recibirInformacion) {
		
	}
	
	default void onSocio(String docIdentidad, String nombre, String apellido, String nacionalidad, String sexo, Date fechaNacimineto, String nss, String estadoCivil, String domicilioResidencia, String domicilioPersonaAdministradora, boolean socioTrabajador, boolean presentadorITP, boolean socioAdministrador, boolean funcionesDireccionGerencia) {
		
	}
	
	default void onAportacion(float aportacionDineraria, float aportacionNoDineraria, String descripcion) {
		
	}
	
	default void onCentroActividad(String nombreCentroActividad, float superficieTotal, String Domicilio, boolean localPrincipal, boolean domicilioActividadTGSS) {
		
	}
	
	default void onActividadCNAE(String actividadPrincipal, String otrasActividades) {
		
	}
	
	default void onActividadIAE(String actividadPrincipal) {
		
	}
	
	default void onLugaresFueraLocal(String epigrafeAE, String tipoActividad, String provincia, String municipio, Date fechaInicio) {
		
	}
	
	default void onDeclaracionCensal(int codigo, String respuesta, Date fecha) {
		List<String> declaracionCensalList = new ArrayList<>();
		String declaracionCensal = codigo + " " + respuesta + " " + fecha;
		declaracionCensalList.add(declaracionCensal);
	}
	
	default void onRepresentante(String docIdentidad, String nombre, String apellidos, String domicilioResidencia, String causaRepresentacion, String clave, String tipoRepresentacion, String tituloRepresentacion) {
		
	}
	
	default void onTrabajadorCuentaPropia(String docIdentidad, String nombre, String apellido, String nacionalidad, String sexo, Date fechaNacimineto, String nss, String estadoCivil, String domicilioResidencia) {
		
	}
	
	default void onAltaPersonatrabajadora(Date fechaAlta, boolean funcionesDireccionGerencia) {
		
	}
	
	default void onCentroTrabajo(String centroTrabajo) {
		
	}
	
	default void onRegimenEncuadramiento(String regimen, boolean trl, String subgrupo, String grupo) {
		
	}
	
	default void onCuentaPropia(String cnae, String mutuaIt, String domicilioNotificacion, boolean altaReta, boolean contingencias, boolean ceseActividad, Date fechaRealAlta, boolean opcionCAFP, String observacionTGSS) {
		
	}
	
	default void onBaseCotizacion(float baseCotizacion, float rendiientosNetos) {
		
	}
	
	default void onDatosDomiciliacionPago(String cuenta) {
		
	}
	
	default void onCitaNotarial(String personaCita, String telefono, Date fecha, Date hora) {
		
	}
	
	default void onDatosNotaria(String nombre, String apellidos, String direccion) {
		
	}
}
