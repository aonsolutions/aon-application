package com.esferalia.aon.ui.payroll.sepe;


import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.payroll.contrata.enumeration.TERRORES;
import com.esferalia.aon.sepe.api.SWComunicacionDatos.SWComunicacionDatos;
import com.esferalia.aon.sepe.api.SWComunicacionDatos.SWComunicacionDatosService;
import com.esferalia.aon.sepe.api.SWConsultaDatos.SWConsultaDatos;
import com.esferalia.aon.sepe.api.SWConsultaDatos.SWConsultaDatosService;
import com.esferalia.aon.sepe.api.ServicioWebConsulta.ServicioWebConsulta;
import com.esferalia.aon.sepe.api.ServicioWebConsulta.ServicioWebConsultaService;
import com.esferalia.aon.sepe.api.ServicioWebEntrada.ServicioWebEntrada;
import com.esferalia.aon.sepe.api.ServicioWebEntrada.ServicioWebEntradaService;

public final class SEPEConnectionProvider {
	
	private final static String IDIOMA = "14";
	
	private final static String COMUNIDAD = "99";
	
	/**
	 * CONTRAT@ COMMUNICATION ADDRESS LOCATION
	 */
//	FIXME: secure http ?? 
//	private final static String COMMUNICATION_PRODUCTION_ENVIRONMENT = 	"https://www.sepe.es/ccomunicacto/services/SWComunicacionDatos";
	private final static String COMMUNICATION_PRODUCTION_ENVIRONMENT = 	"http://www.sepe.es/ccomunicacto/services/SWComunicacionDatos";
	private final static String COMMUNICATION_TEST_ENVIRONMENT = 		"http://www.sepe.es/ecomunicacto/services/SWComunicacionDatos";

	/**
	 * CONTRAT@ QUERY ADDRESS LOCATION
	 */
//	FIXME: secure http ?? 
//	private final static String QUERY_PRODUCTION_ENVIRONMENT = 			"https://www.sepe.es/ccomunicacto/services/SWConsultaDatos";
	private final static String QUERY_PRODUCTION_ENVIRONMENT = 			"http://www.sepe.es/ccomunicacto/services/SWConsultaDatos";
	private final static String QUERY_TEST_ENVIRONMENT = 				"http://www.sepe.es/ecomunicacto/services/SWConsultaDatos";

	/**
	 * CERTIFIC@2 COMMUNICATION ADDRESS LOCATION
	 */
//	FIXME: secure http ?? 
//	private final static String CERTIFICADOS_COMMUNICATION_PRODUCTION_ENVIRONMENT = 	"https://sede.sepe.gob.es/DCertificadosWeb/services/ServicioWebConsulta";
//	private final static String CERTIFICADOS_COMMUNICATION_TEST_ENVIRONMENT =			"https://formacion.sepe.gob.es/DCertificadosWeb/services/ServicioWebConsulta"/>
	private final static String CERTIFICADOS_COMMUNICATION_PRODUCTION_ENVIRONMENT = 	"http://sede.sepe.gob.es/DCertificadosWeb/services/ServicioWebConsulta";
	private final static String CERTIFICADOS_COMMUNICATION_TEST_ENVIRONMENT =			"http://formacion.sepe.gob.es/DCertificadosWeb/services/ServicioWebConsulta";
	
	/**
	 * CERTIFIC@2 QUERY ADDRESS LOCATION
	 */
//	FIXME: secure http ?? 
//	private final static String CERTIFICADOS_QUERY_PRODUCTION_ENVIRONMENT = 			"https://sede.sepe.gob.es/DCertificadosWeb/services/ServicioWebEntrada";
//	private final static String CERTIFICADOS_QUERY_TEST_ENVIRONMENT =					"https://formacion.sepe.gob.es/DCertificadosWeb/services/ServicioWebEntrada";
	private final static String CERTIFICADOS_QUERY_PRODUCTION_ENVIRONMENT = 			"http://sede.sepe.gob.es/DCertificadosWeb/services/ServicioWebEntrada";
	private final static String CERTIFICADOS_QUERY_TEST_ENVIRONMENT =					"http://formacion.sepe.gob.es/DCertificadosWeb/services/ServicioWebEntrada";
	
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
	public static String processContrataComunication(boolean isTestEnv, byte[] data, String connectedUser, String mainUser, String passwd){
		return processContrataComunication(isTestEnv, new String(data), connectedUser, mainUser, passwd);
	}
	
	public static String processContrataComunication(boolean isTestEnv, String data, String connectedUser, String mainUser, String passwd){
		try {
			SWComunicacionDatosService service = new SWComunicacionDatosService(new URL(isTestEnv ? COMMUNICATION_TEST_ENVIRONMENT : COMMUNICATION_PRODUCTION_ENVIRONMENT));
			SWComunicacionDatos datos = service.getSWComunicacionDatos();
			String result = datos.servicioContratos(data, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
			return result;
		} catch (MalformedURLException e) {
			// nada, no se comunica
		}
		return null;
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
	public static String processContrataQuery(boolean isTestEnv, String communicationId, String connectedUser, String mainUser, String passwd){
		try {
			SWConsultaDatosService service = new SWConsultaDatosService(new URL(isTestEnv ? QUERY_TEST_ENVIRONMENT : QUERY_PRODUCTION_ENVIRONMENT));
			SWConsultaDatos datos = service.getSWConsultaDatos();
			String result = datos.servicioConsulta(communicationId, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
			return result;
		} catch (MalformedURLException e) {
			// nada, no se comunica
		}
		return null;
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
	private static String processCertificadosCommunication(boolean isTestEnv, String _Xml, String _UsuarioConectado, String _UsuarioPrincipal, String _Password, String _Idioma, String _Comunidad){
		try {
			ServicioWebEntradaService service = new ServicioWebEntradaService(new URL(isTestEnv ? CERTIFICADOS_COMMUNICATION_TEST_ENVIRONMENT : CERTIFICADOS_COMMUNICATION_PRODUCTION_ENVIRONMENT));  
			ServicioWebEntrada datos = service.getServicioWebEntrada();
			String result = datos.ejecuta(_UsuarioConectado, _UsuarioPrincipal, _Password, _Xml, IDIOMA, COMUNIDAD);
			return result;
		} catch (MalformedURLException e) {
			// nada, no se comunica
		}
		return null;
	}

	public static String processCertificadosCommunication(boolean isTestEnv, byte[] _Xml, String _UsuarioConectado, String _UsuarioPrincipal, String _Password){
		return processCertificadosCommunication(isTestEnv, new String(_Xml), _UsuarioConectado, _UsuarioPrincipal, _Password, IDIOMA, COMUNIDAD);
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
	private static String processCertificadosQuery(boolean isTestEnv, String _UsuarioConectado, String _UsuarioPrincipal, String _Password, String _idComunicacion, String _Idioma, String _Comunidad){
		try {
			ServicioWebConsultaService service = new ServicioWebConsultaService(new URL(isTestEnv ? CERTIFICADOS_QUERY_TEST_ENVIRONMENT : CERTIFICADOS_QUERY_PRODUCTION_ENVIRONMENT));  
			ServicioWebConsulta datos = service.getServicioWebConsulta();
			String result = datos.ejecuta(_UsuarioConectado, _UsuarioPrincipal, _Password, _idComunicacion, _Idioma, _Comunidad);
			return result;
		} catch (MalformedURLException e) {
			// nada, no se comunica
		}
		return null;
	}
	
	public static String processCertificadosQuery(boolean isTestEnv, String _UsuarioConectado, String _UsuarioPrincipal, String _Password, String _idComunicacion){
		return processCertificadosQuery(isTestEnv, _UsuarioConectado, _UsuarioPrincipal, _Password, _idComunicacion, IDIOMA, COMUNIDAD);
	}
	
	public static boolean validateLogin(boolean b, String string, String contrataUser, String mainUser, String contrataPassword) {
		String result = processContrataComunication(true, "<?xml>", contrataUser, mainUser, contrataPassword);
		result = result.replaceAll("\n", "");
		result = StringUtils.removeStart(result, "<?xml version='1.0' encoding='ISO-8859-1'?>");
		result = StringUtils.removeStart(result, "<COMUNICACION>");
		result = StringUtils.removeStart(result, "<NUM_ENVIO>");
		result = StringUtils.removeEnd(result, "</COMUNICACION>");
		result = StringUtils.removeEnd(result, "</NUM_ENVIO>");
		result = StringUtils.replace(result, "<ERROR>", "");
		result = StringUtils.replace(result, "</ERROR>", "");
		return TERRORES.getEnumByValue(result)!=null;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		String DOCUMENTO = "";
		String USUARIO_CONECTADO = "A01306190";
		String USUARIO_PRINCIPAL = "A01306190";
		String PASSWORD = "945121010";
		String result = processContrataComunication(true, DOCUMENTO, USUARIO_CONECTADO, USUARIO_PRINCIPAL, PASSWORD);
		
//		String DOCUMENTO = "C7534747";
//		String result = processDataQuery(DOCUMENTO, USUARIO_CONECTADO, USUARIO_PRINCIPAL, PASSWORD);
		
		System.out.println("RESULTADO=  " );
		System.out.println(result);
	}
	
}