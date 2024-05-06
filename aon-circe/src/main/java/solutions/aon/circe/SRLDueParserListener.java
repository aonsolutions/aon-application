package solutions.aon.circe;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SRLDueParserListener {

	default void onRegistroEntradaYPAE(String registroEntrada, String pae) {

	}

	default void onEmpresaActividad(LocalDate inicioActividad, LocalDate cierreEjercicio) {

	}

	default void onEmpresaDatosJuridicos(String duracionPersonaJuridica, String denominacionSocial, float capitalSocial,
			String acreditaCapitalSocial, String estatutosTipo) {

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
	
	default void onComunicaciones(String recibirInformacion) {
		
	}
	
	default void onSocio(String docIdentidad, String nombre, String apellido, String nacionalidad, String sexo, LocalDate fechaNacimineto, String nss, String estadoCivil, String domicilioResidencia, String domicilioPersonaAdministradora, String socioTrabajador, String presentadorITP, String socioAdministrador, String funcionesDireccionGerencia) {
		
	}
	
	default void onAportacion(float aportacionDineraria, float aportacionNoDineraria, String descripcion) {
		
	}
	
	default void onConyuge(String docIdenidad, String nombre, String apellidos, String sexo, String tipoRegimen) {
		
	}
	
	default void onCentroActividad(String nombreCentroActividad, float superficieTotal, String domicilio, String localPrincipal, String domicilioActividadTGSS) {
		
	}
	
	default void onActividadCNAE(String actividadPrincipal, String otrasActividades) {
		
	}
	
	default void onActividadIAE(String actividadPrincipal) {
		
	}
	
	default void onLugaresFueraLocal(String epigrafeAE, String tipoActividad, String provincia, String municipio, LocalDate fechaInicio) {
		
	}
	
	default void onDeclaracionCensal(int codigo, String respuesta, LocalDate fecha) {

	}
	
	default void onRepresentante(String docIdentidad, String nombre, String apellidos, String domicilioResidencia, String causaRepresentacion, String clave, String tipoRepresentacion, String tituloRepresentacion) {
		
	}
	
	default void onTrabajadorCuentaPropia(String docIdentidad, String nombre, String apellido, String nacionalidad, String sexo, LocalDate fechaNacimineto, String nss, String estadoCivil, String domicilioResidencia) {
		
	}
	
	default void onAltaPersonaTrabajadora(LocalDate fechaAlta, String funcionesDireccionGerencia) {
		
	}
	
	default void onCentroTrabajo(String centroTrabajo) {
		
	}
	
	default void onRegimenEncuadramiento(String regimen, String trl, String subgrupo, String grupo) {
		
	}
	
	default void onCuentaPropia(String cnae, String mutuaIt, String domicilioNotificacion, String altaReta, String contingencias, String ceseActividad, LocalDate fechaRealAlta, String opcionCAFP, String observacionTGSS) {
		
	}
	
	default void onBaseCotizacion(float baseCotizacion, float rendimientosNetos) {
		
	}
	
	default void onDatosDomiciliacionPago(String cuenta) {
		
	}
	
	default void onCitaNotarial(String personaCita, String telefono, LocalDateTime fechaHora) {
		
	}
	
	default void onDatosNotaria(String nombre, String apellidos, String direccion, String telefono, String fax) {
		
	}
}
