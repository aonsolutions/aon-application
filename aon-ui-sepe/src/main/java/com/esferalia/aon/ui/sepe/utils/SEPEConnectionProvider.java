package com.esferalia.aon.ui.sepe.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.Classpath;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.enumeration.ContrataFileType;
import com.esferalia.aon.payroll.enumeration.certificados.Terrores;
import com.esferalia.aon.payroll.enumeration.contrata.TERRORES;
import com.esferalia.aon.sepe.api.SWComunicacionDatos.SWComunicacionDatos;
import com.esferalia.aon.sepe.api.SWComunicacionDatos.SWComunicacionDatosService;
import com.esferalia.aon.sepe.api.SWConsultaDatos.SWConsultaDatos;
import com.esferalia.aon.sepe.api.SWConsultaDatos.SWConsultaDatosService;
import com.esferalia.aon.sepe.api.ServicioWebConsulta.ServicioWebConsulta;
import com.esferalia.aon.sepe.api.ServicioWebConsulta.ServicioWebConsultaService;
import com.esferalia.aon.sepe.api.ServicioWebEntrada.ServicioWebEntrada;
import com.esferalia.aon.sepe.api.ServicioWebEntrada.ServicioWebEntradaService;

public final class SEPEConnectionProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SEPEConnectionProvider.class.getName());
	
	private final static String IDIOMA = "14";
	
	private final static String COMUNIDAD = "99";
	
	
	/**
	 * CONTRAT@: COMMUNICATION
	 * ----------------------
		Control Horario:
			- Actividad no disponible en el siguiente horario: de 1:00 a 3:00
		Comunidad e idioma:
			- Parmetros comunidad o idioma incorrectos.
		Validacin usuario-clave:
			- Errores resultantes en la comprobacin de que el usuario y la clave enviados tienen 
			formato correcto, se corresponden entre si y estn autorizados para realizar la comunicacin.
		Registro de comunicaciones:
			- No se pudo obtener el nmero identificador del envo.
			- Errores de parseo de XML. (Validacin de esquema, ausencia de etiquetas, etc.).
		Error general:
			- El sistema actualmente no est disponible 
	 * @param isTestEnv
	 * @param data
	 * @param connectedUser
	 * @param mainUser
	 * @param passwd
	 * @return
	 */
	public static String processContrataCommunication(boolean isSslEnv, boolean isTestEnv, byte[] data, String connectedUser, String mainUser, String passwd) {
		return processContrataCommunication(isSslEnv, isTestEnv, new String(data), connectedUser, mainUser, passwd, ContrataFileType.CONTRACT);
	}
	
	public static String processContrataCommunication(boolean isSslEnv, boolean isTestEnv, String data, String connectedUser, String mainUser, String passwd, ContrataFileType fileType) {
		assignSslSystemProperies();
		try {
			SWComunicacionDatosService service = new SWComunicacionDatosService();
			SWComunicacionDatos datos = null;
			if(isTestEnv){
				datos = isSslEnv?service.getSWComunicacionDatosTestingSsl():service.getSWComunicacionDatosTesting();
			} else {
				datos = isSslEnv?service.getSWComunicacionDatosSsl():service.getSWComunicacionDatos();
			}
			String result = null;
			switch (fileType) {
			case CONTRACT:
				result = datos.servicioContratos(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			case EXTENSION:
				result = datos.servicioProrrogas(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			case TRANSFORMATION:
				result = datos.servicioTransformacionIndefinido(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			case INDEFINITE_CALL:
				result = datos.servicioLlamamientoFijoDiscontinuo(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			case BASIC_COPY:
				result = datos.servicioCopiaBasica(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			case GROUP_CONTRACT:
				result = datos.servicioContratoGrupo(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			case ADDITIONAL_HOURS:
				result = datos.servicioHorasComplementarias(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			case OFFICE_CONTRACT:
				result = datos.servicioInclusionContratoOE(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			case LEARNING_ANNEX:
				// XXX: service not implemented
				break;
			case CORRECTION_CONTRACT:
				result = datos.servicioCorreccionContrato(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			case CORRECTION_EXTENSION:
				result = datos.servicioCorreccionProrroga(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			case CORRECTION_TRANSFORMATION:
				result = datos.servicioCorreccionTransformacion(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			case CORRECTION_INDEFINITE_CALL:
				result = datos.servicioCorreccionLlamamiento(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			case CORRECTION_ADDITIONAL_HOURS:
				result = datos.servicioCorreccionHorasComplementarias(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
				break;
			default:
				break;
			}			
			LOGGER.debug("INFO SEPE (Contrat@) - RESPUESTA RESULTANTE DE LA COMUNICACION CON EL S.E.P.E. :  \n" + result);
			return result;
		} finally{
			restoreSslSystemProperies();
		}
	}

	/**
	 * CONTRAT@: STATUS QUERY
	 * ---------------------
		Comunidad e idioma:
			- Parmetros comunidad o idioma incorrectos.
		Validacin usuario-clave:
			- Errores resultantes en la comprobacin de que el usuario y la clave enviados tienen formato correcto, 
			se corresponden entre si y estn autorizados para realizar la comunicacin.
		Realizar consulta:
			- No existe el fichero o no lo encuentra.
		Errores incluidos en la respuesta:
			- Errores incluidos en el fichero de respuesta, que son resultantes de la validacin de las comunicaciones 
			realizadas. Estos cdigos de error se pueden encontrar en el documento "terrores.txt" que está publicado en Contrat@.
		Error general
			- El sistema actualmente no est disponible
	 * @param isTestEnv
	 * @param communicationId
	 * @param connectedUser
	 * @param mainUser
	 * @param passwd
	 * @return
	 */
	public static String processContrataQuery(boolean isSslEnv, boolean isTestEnv, String communicationId, String connectedUser, String mainUser, String passwd) {
		assignSslSystemProperies();
		try {
			SWConsultaDatosService service = new SWConsultaDatosService();
			SWConsultaDatos datos = null;
			if(isTestEnv){
				datos = isSslEnv?service.getSWConsultaDatosTestingSsl():service.getSWConsultaDatosTesting();
			} else {
				datos = isSslEnv?service.getSWConsultaDatosSsl():service.getSWConsultaDatos();
			}
			String result = datos.servicioConsulta(communicationId, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
			LOGGER.debug("INFO SEPE (Contrat@) - RESPUESTA RESULTANTE DE LA CONSULTA AL S.E.P.E. :  \n" + result);
			return result;
		} finally{
			restoreSslSystemProperies();
		}
	}
	
	
	/**
	 * CERTIFIC@2: COMMUNICATION
	 * -------------------------
		Parametros de salida
		Identificador de la comunicacin (Int): Nmero asignado a cada comunicacin.
		Cdigo del error (String): Clave del error, si existiese ste. 
		Descripcin del error (String): Breve explicacin del error ocurrido. 
		
		DEX0023
			Error del sistema.
		DEX0204
			El fichero no est completado de forma correcta. Se acompaña de una descripcin tcnica del error que se ha producido al validar el fichero.
		DEX0207
			Alguno de los datos de la autenticacin Contrat@ es errneo o est incompleto.
		DEX0210
			El usuario conectado no est registrado en la aplicacin. Debe acceder a Certific@2  y proporcionar sus datos de contacto.
		DEX0211
			El acceso a la aplicacin se encuentra deshabilitado temporalmente. 
	 * 
	 * @param _UsuarioConectado (String): usuario que realiza la comunicacin (cif/nif/nie de un usuario principal o de un usuario asociado). Debe estar registrado en la aplicacin para facilitar sus datos de contacto.
	 * @param _UsuarioPrincipal (String): usuario principal correspondiente al usuario conectado.
	 * @param _Password (String): clave del usuario conectado.
	 * @param _Xml (String): documento en formato XML con la informacin de la comunicacin.
	 * @param _Idioma (String): para usos futuros. Debe ir informado con el valor 14.
	 * @param _Comunidad (String): para usos futuros. Debe ir informado con el valor 99.
	 * @param isTestEnv
	 * @return
	 */
	public static String processCertificadosCommunication(boolean isSslEnv, boolean isTestEnv, String _Xml, String _UsuarioConectado, String _UsuarioPrincipal, String _Password) {
		assignSslSystemProperies();
		try {
			ServicioWebEntradaService service = new ServicioWebEntradaService(); 
			ServicioWebEntrada datos = null;
			if(isTestEnv){
				datos = isSslEnv?service.getServicioWebEntradaTestingSsl():service.getServicioWebEntradaTesting();
			} else {
				datos = isSslEnv?service.getServicioWebEntradaSsl():service.getServicioWebEntrada();
			}
			String result = datos.ejecuta(_UsuarioConectado, _UsuarioPrincipal, _Password, _Xml, IDIOMA, COMUNIDAD);
			LOGGER.debug("INFO SEPE (Certific@2) - RESPUESTA RESULTANTE DE LA COMUNICACION CON EL S.E.P.E. :  \n" + result);
			return result;
		} finally{
			restoreSslSystemProperies();
		}
	}

	public static String processCertificadosCommunication(boolean isSslEnv, boolean isTestEnv, byte[] _Xml, String _UsuarioConectado, String _UsuarioPrincipal, String _Password) {
		return processCertificadosCommunication(isSslEnv, isTestEnv, new String(_Xml), _UsuarioConectado, _UsuarioPrincipal, _Password);
	}
	
	/**
	 * CERTIFIC@2: STATUS QUERY
	 * ------------------------
		Parametros de salida
		Cdigo del error (String): Clave del error si existiese ste.
		Estado de la comunicacin (String): Recibida, En proceso o Procesada.
		Contenido del fichero XML  enviado y en cuyo final se añade la descripcin del resultado de su procesamiento.
		
		DEX0023
			Error del sistema.
		DEX0206
			El identificador del fichero no existe.
		DEX0207
			Alguno de los datos de la autenticación Contrat@ es erróneo o est incompleto.
		DEX0208
			No se puede recuperar el fichero de respuesta.
		DEX0209
			El identificador del fichero corresponde a un envío no realizado a travs del servicio web.
		DEX0211
			El acceso a la aplicacin se encuentra deshabilitado temporalmente. 
		
		La descripción de los errores correspondientes a las validaciones del contenido del fichero xml estn disponibles en la aplicacin Certific@2.
	 * 
	 * @param _UsuarioConectado (String): usuario que realiza la consulta (cif/nif/nie de un usuario principal o de un usuario asociado).
	 * @param _UsuarioPrincipal (String): cif/nif/nie de un usuario principal o de un usuario asociado 
	 * @param _Password (String): clave del usuario conectado.
	 * @param _idComunicacion (String): identificador de la comunicacin otorgado en el momento de hacer la comunicacin.
	 * @param _Idioma (String): para usos futuros. Debe ir informado con el valor 14.
	 * @param _Comunidad (String): para usos futuros. Debe ir informado con el valor 99.
	 * @param isTestEnv
	 * @return
	 */
	private static String processCertificadosQuery(boolean isSslEnv, boolean isTestEnv, String _idComunicacion, String _UsuarioConectado, String _UsuarioPrincipal, String _Password, String _Idioma, String _Comunidad) {
		assignSslSystemProperies();
		try {
			ServicioWebConsultaService service = new ServicioWebConsultaService();  
			ServicioWebConsulta datos = null;
			if(isTestEnv){
				datos = isSslEnv?service.getServicioWebConsultaTestingSsl():service.getServicioWebConsultaTesting();
			} else {
				datos = isSslEnv?service.getServicioWebConsultaSsl():service.getServicioWebConsulta();
			}
			String result = datos.ejecuta(_UsuarioConectado, _UsuarioPrincipal, _Password, _idComunicacion, _Idioma, _Comunidad);
			LOGGER.debug("INFO SEPE (Certific@2) - RESPUESTA RESULTANTE DE LA CONSULTA AL S.E.P.E. :  \n" + result);
			return result;
		} finally{
			restoreSslSystemProperies();
		}
	}
	
	public static String processCertificadosQuery(boolean isSslEnv, boolean isTestEnv, String _idComunicacion, String _UsuarioConectado, String _UsuarioPrincipal, String _Password) {
		return processCertificadosQuery(isSslEnv, isTestEnv, _idComunicacion, _UsuarioConectado, _UsuarioPrincipal, _Password, IDIOMA, COMUNIDAD);
	}
	
	public enum SEPECommunicationError {
		/**
		 * Error del sistema.
		 */
		DEX0023("Error del sistema."),
		/**
		 * El fichero no está completado de forma correcta. Se acompaña de una
		 * descripción técnica del error que se ha producido al validar el
		 * fichero.
		 */
		DEX0204(
				"El fichero no está completado de forma correcta. Se acompaña de una descripción técnica del error que se ha producido al validar el fichero."),
		/**
		 * El identificador del fichero no existe.
		 */
		DEX0206("El identificador del fichero no existe."),
		/**
		 * Alguno de los datos de la autenticación Contrat@ es erróneo o está
		 * incompleto.
		 */
		DEX0207(
				"Alguno de los datos de la autenticación Contrat@ es erróneo o está incompleto."),
		/**
		 * No se puede recuperar el fichero de respuesta.
		 */
		DEX0208("No se puede recuperar el fichero de respuesta."),
		/**
		 * El identificador del fichero corresponde a un envío no realizado a
		 * través del servicio web.
		 */
		DEX0209(
				"El identificador del fichero corresponde a un envío no realizado a través del servicio web."),
		/**
		 * El usuario conectado no está registrado en la aplicación. Debe
		 * acceder a Certific@2 y proporcionar sus datos de contacto.
		 */
		DEX0210(
				"El usuario conectado no está registrado en la aplicación. Debe acceder a Certific@2 y proporcionar sus datos de contacto."),
		/**
		 * El acceso a la aplicación se encuentra deshabilitado temporalmente.
		 */
		DEX0211(
				"El acceso a la aplicación se encuentra deshabilitado temporalmente.");
		private String description;

		public String getDescription() {
			return description;
		}

		private SEPECommunicationError(String description) {
			this.description = description;
		}
	}
	
	// ******************************
	// VALIDATION
	// ******************************
	
	public static boolean validateContrataLogin(boolean ssl, boolean test, String contrataUser, String mainUser, String contrataPassword) {
		try {
			String result = processContrataCommunication(ssl, test, "<?xml>", contrataUser, mainUser, contrataPassword, ContrataFileType.CONTRACT);
			result = result.replaceAll("\n", "");
			result = StringUtils.substringBetween(result, "<ERROR>", "</ERROR>");
			return TERRORES.getEnumByValue(result)!=null;
		} catch (Throwable th) {
			LOGGER.error(th.getMessage());
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage());
		}
	}
	
	public static boolean validateCertifica2Login(boolean ssl, boolean test, String certifica2User, String mainUser, String certifica2Password) {
		try {
			String result = processCertificadosCommunication(ssl, test, "<?xml>", certifica2User, mainUser, certifica2Password);
			result = result.replaceAll("\n", "");
			if(StringUtils.contains(result, SEPECommunicationError.DEX0023.toString())
					|| StringUtils.contains(result, SEPECommunicationError.DEX0207.toString())
					|| StringUtils.contains(result, SEPECommunicationError.DEX0210.toString())
					|| StringUtils.contains(result, SEPECommunicationError.DEX0211.toString())){
				return false;
			}
			return StringUtils.contains(result, SEPECommunicationError.DEX0204.toString()) || Terrores.getEnumByValue(result)!=null;
		} catch (Throwable th) {
			LOGGER.error(th.getMessage());
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage());
		}
	}
	
	
	/*
	 * ***********************************************************************************
	 * these parameters must be defined in the server configuration as java vm parameters
	 * ***********************************************************************************
	 */
	private static String keyStore = null;
	private static String keyStorePassword = null;
	private static String trustStore = null;
	private static String trustStorePassword = null;

	private static final String KEY_STORE_NAME		= "cacerts";
	private static final String KEY_STORE_PASSWD 	= "changeit";
	private static File caFile = null;
	
	private static void assignSslSystemProperies() {
		createCAFile();
		
		keyStore = System.getProperty("javax.net.ssl.keyStore");
		keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
		trustStore = System.getProperty("javax.net.ssl.trustStore");
		trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
		
		System.setProperty("javax.net.ssl.keyStore", caFile.getPath());
		System.setProperty("javax.net.ssl.keyStorePassword", KEY_STORE_PASSWD);
		System.setProperty("javax.net.ssl.trustStore", caFile.getPath());
		System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWD);
	}

	private static void restoreSslSystemProperies() {
		try {
			if(StringUtils.isBlank(keyStore)){
				System.clearProperty("javax.net.ssl.keyStore");
			} else {
				System.setProperty("javax.net.ssl.keyStore",keyStore);
			}
			if(StringUtils.isBlank(keyStorePassword)){
				System.clearProperty("javax.net.ssl.keyStorePassword");
			} else {
				System.setProperty("javax.net.ssl.keyStorePassword",keyStorePassword);
			}
			if(StringUtils.isBlank(trustStore)){
				System.clearProperty("javax.net.ssl.trustStore");
			} else {
				System.setProperty("javax.net.ssl.trustStore",trustStore);
			}
			if(StringUtils.isBlank(trustStorePassword)){
				System.clearProperty("javax.net.ssl.trustStorePassword");
			} else {
				System.setProperty("javax.net.ssl.trustStorePassword",trustStorePassword);
			}
		} finally {
			if(caFile.exists()){
				caFile.delete();
			}
		}
	}
	
	private static void createCAFile() {
		InputStream keystoreStream = null;
		OutputStream outputStream = null;
		try {
			caFile = File.createTempFile(KEY_STORE_NAME, "");
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, "META-INF", KEY_STORE_NAME);
			keystoreStream = urls[0].openStream();
			outputStream = new FileOutputStream(caFile);
	 		int read = 0;
			byte[] bytes = new byte[1024];
	 
			while ((read = keystoreStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		} catch (IOException e) {
			String msg = "No se ha podido generar el certificado de autoridad para la comunicacion a traves de ssl";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} finally {
			if (keystoreStream != null) {
				try {
					keystoreStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	 
			}
		}
	}
	
	
	
	public static void main(String[] args) throws Exception {
		String DOCUMENTO = "";
		String USUARIO_CONECTADO = "A01306190";
		String USUARIO_PRINCIPAL = "A01306190";
		String PASSWORD = "945121010";

		try {
			
			String result = processContrataCommunication(false, true, DOCUMENTO, USUARIO_CONECTADO, USUARIO_PRINCIPAL, PASSWORD, ContrataFileType.CONTRACT);
//			String result = processContrataQuery(true, false, DOCUMENTO, USUARIO_CONECTADO, USUARIO_PRINCIPAL, PASSWORD);
			
//			String DOCUMENTO = "C7534747";
//			String result = processDataQuery(DOCUMENTO, USUARIO_CONECTADO, USUARIO_PRINCIPAL, PASSWORD);
			
//			String result = processCertificadosCommunication(true, false, DOCUMENTO, USUARIO_CONECTADO, USUARIO_PRINCIPAL, PASSWORD);
			
			System.out.println("RESULTADO = " );
			System.out.println(result);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
}