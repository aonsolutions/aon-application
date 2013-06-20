package com.esferalia.aon.ui.payroll.sepe;


import java.net.MalformedURLException;
import java.net.URL;

import com.esferalia.aon.sepe.api.SWComunicacionDatos.SWComunicacionDatos;
import com.esferalia.aon.sepe.api.SWComunicacionDatos.SWComunicacionDatosService;
import com.esferalia.aon.sepe.api.SWConsultaDatos.SWConsultaDatos;
import com.esferalia.aon.sepe.api.SWConsultaDatos.SWConsultaDatosService;

public class ContrataManager {
	
	private final static String IDIOMA = "14";
	
	private final static String COMUNIDAD = "99";
	
	// COMMUNICATION ADDRESS LOCATION
//	FIXME: secure http ?? 
//	private final static String COMMUNICATION_PRODUCTION_ENVIRONMENT = 	"https://www.sepe.es/ccomunicacto/services/SWComunicacionDatos";
	private final static String COMMUNICATION_PRODUCTION_ENVIRONMENT = 	"http://www.sepe.es/ccomunicacto/services/SWComunicacionDatos";
	
	private final static String COMMUNICATION_TEST_ENVIRONMENT = 		"http://www.sepe.es/ecomunicacto/services/SWComunicacionDatos";

	// QUIERY ADDRESS LOCATION
//	FIXME: secure http ?? 
//	private final static String QUERY_PRODUCTION_ENVIRONMENT = 			"https://www.sepe.es/ccomunicacto/services/SWConsultaDatos";
	private final static String QUERY_PRODUCTION_ENVIRONMENT = 			"http://www.sepe.es/ccomunicacto/services/SWConsultaDatos";
	
	private final static String QUERY_TEST_ENVIRONMENT = 				"http://www.sepe.es/ecomunicacto/services/SWConsultaDatos";

	
	public static String processDataComunication(boolean isTestEnv, byte[] data, String connectedUser, String mainUser, String passwd){
		return processDataComunication(isTestEnv, new String(data), connectedUser, mainUser, passwd);
	}
	
	public static String processDataComunication(boolean isTestEnv, String document, String connectedUser, String mainUser, String passwd){
//		SWComunicacionDatosService service = new SWComunicacionDatosService(obtainContrataEnviroment(true));
		SWComunicacionDatosService service = new SWComunicacionDatosService();
		SWComunicacionDatos datos = service.getSWComunicacionDatos();
		
		String result = datos.servicioContratos(document, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
		
		if(result==null || result.isEmpty()){
			try {
				datos.wait(1000000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
		
		
//		Control Horario:
//		- Actividad no disponible en el siguiente horario: de 1:00 a 3:00
//		Comunidad e idioma:
//		- Parámetros comunidad o idioma incorrectos.
//		Validación usuario-clave:
//		- Errores resultantes en la comprobación de que el usuario y la clave enviados tienen 
//		  formato correcto, se corresponden entre sí y están autorizados para realizar la comunicación.
//		Registro de comunicaciones:
//		- No se pudo obtener el número identificador del envío.
//		- Errores de parseo de XML. (Validación de esquema, ausencia de etiquetas, etc.).
//		Error general:
//		- El sistema actualmente no está disponible
		
		
	}

	public static String processDataQuery(boolean isTestEnv, String document, String connectedUser, String mainUser, String passwd){
		try {
			SWConsultaDatosService service = new SWConsultaDatosService(new URL(obtainContrataEnvironment(false, isTestEnv)));
//		SWConsultaDatosService service = new SWConsultaDatosService();
			SWConsultaDatos datos = service.getSWConsultaDatos();
			String result = datos.servicioConsulta(document, connectedUser, mainUser, passwd, IDIOMA, COMUNIDAD);
			return result;
		} catch (MalformedURLException e) {
			// nada, no se comunica
		}
		
		return null;
		
		
//		Comunidad e idioma:
//		- Parámetros comunidad o idioma incorrectos.
//		Validación usuario-clave:
//		- Errores resultantes en la comprobación de que el usuario y la clave enviados tienen formato correcto, 
//		  se corresponden entre sí y están autorizados para realizar la comunicación.
//		Realizar consulta:
//		- No existe el fichero o no lo encuentra.
//		Errores incluidos en la respuesta:
//		- Errores incluidos en el fichero de respuesta, que son resultantes de la validación de las comunicaciones 
//		  realizadas. Estos códigos de error se pueden encontrar en el documento "terrores.txt" que está publicado en Contrat@.
//		Error general
//		- El sistema actualmente no está disponible
		
	}
	
	private static String obtainContrataEnvironment(boolean isCommunication, boolean isTestEnv) {
		if(isCommunication){
				return isTestEnv ? COMMUNICATION_TEST_ENVIRONMENT : COMMUNICATION_PRODUCTION_ENVIRONMENT;
		} else {
				return isTestEnv ? QUERY_TEST_ENVIRONMENT : QUERY_PRODUCTION_ENVIRONMENT;
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		String DOCUMENTO = "";
		String USUARIO_CONECTADO = "A01306190";
		String USUARIO_PRINCIPAL = "A01306190";
		String PASSWORD = "945121010";
		String result = processDataComunication(true, DOCUMENTO, USUARIO_CONECTADO, USUARIO_PRINCIPAL, PASSWORD);
		
//		String DOCUMENTO = "C7534747";
//		String result = processDataQuery(DOCUMENTO, USUARIO_CONECTADO, USUARIO_PRINCIPAL, PASSWORD);
		
		
		System.out.println("RESULTADO=  " );
		System.out.println(result);
	}
	
}