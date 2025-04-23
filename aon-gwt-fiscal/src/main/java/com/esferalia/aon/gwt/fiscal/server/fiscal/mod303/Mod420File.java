package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Base64;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.mod420_2025.DEC;
import com.esferalia.aon.occam.impl.jooq.dao.mod420_2025.Mod420ToDEC;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod420 File download", urlPatterns = { "/aon_gwt_fiscal/ms/Model420File" })
public class Mod420File extends HttpServlet {

	private static final long serialVersionUID = 5298054660785878368L;
	
	private static final String ERROR_TEMPLATE_START = "<html>"
			+"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>"
			+"<body>";
	
	private static final String ERROR_TEMPLATE_AEAT = "<div style=\""
			+"font-family: arial, 'lucida Grande', 'Trebuchet MS', sans-serif;"
			+"font-weight: bold;"
			+"margin-top: 20px;"
		+"\">"
		+ "El módulo de impresión de la Agencia Tributaria Canaria devolvió el siguiente mensaje:"
		+"</div>";

	private static final String ERROR_TEMPLATE_BEFORE = "<html>"
			+"<ul style=\""
				 +"background-attachment: scroll;"
				 +"background-clip: border-box;"
				 +"background-position: 3px 2px;"
				 +"background-repeat: no-repeat;"
				 +"background-size: auto auto;"
				 +"background-color: #f5e3e3;"
				 +"border: solid black 1px;"
				 +"font-size: small;"
				 +"font-family: arial, 'lucida Grande', 'Trebuchet MS', sans-serif;"
				 +"font-weight: bold;"
				 +"border: solid black 1px;"
				 +"padding-top: 20px;"
				 +"padding-bottom: 20px;"
			+"\">";
	private static final String ERROR_TEMPLATE_BODY = "<li>{0}</li>";
	private static final String ERROR_TEMPLATE_AFTER = "</ul>";
	private static final String ERROR_TEMPLATE_END = "</body></html>";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			int id = Integer.parseInt(req.getParameter("modelID"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod303 mod303 = MODEL303.get(occam,id);
			
//			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
//			Occam occam = new Occam()
//					.setDomainName(aeatParams.getDomainName())
//					.setDomain(aeatParams.getDomainId())
//					.setUser(aeatParams.getUser());
//			Mod303 mod303 = MODEL303.get(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			
			System.out.println("PASO 1");
			
			// Obtener el XML
			String xml = obtenerXML(mod303);
			
			System.out.println("PASO 2. xml="+xml);
			
			// Pasarlo al modulo de impresión para obtener el fichero para la presentación telemática o carga en programa de ayuda
//			String resultado = null;
//			if (mod303.isMonthPeriod()) {
//				resultado = obtenerPresentacion417(xml);
//			} else {
//				resultado = obtenerPresentacion420(xml);	
//			}
//			
//			if (resultado == null) {
//				throw new AonCoreException("RESULTADO ES NULO");				
//			}
			llamarAWS(xml, mod303, resp);
			
			System.out.println("PASO 3. OK");
			
			// PRUEBA LLAMAR SERVICIO APARTE (CON FORM-URLENDODED)
//			String parameters = MessageFormat.format("MOD={0}&EJF={1}&FIC={2}"
//					,mod303.isMonthPeriod()?"417":"420"
//					,AonNumberUtils.toString(mod303.getYear())
//					,ModelAdmonUtils.getEncodedFile(xml.getBytes(), StandardCharsets.ISO_8859_1));
//			HttpRequest request = HttpRequest.newBuilder()
//					.uri(URI.create( "http://localhost:8080/canarias/ModuloImpresion" ))
//					.POST(HttpRequest.BodyPublishers.ofString(parameters))
//					.setHeader(AonHttpUtils.USER_AGENT, "Java 11 HttpClient Bot")
//					.setHeader(AonHttpUtils.CONTENT_TYPE, "application/x-www-form-urlencoded")
//					.build();
//				HttpClient httpClient = HttpClient.newBuilder()
//		            .version(HttpClient.Version.HTTP_2)
//		            .connectTimeout(Duration.ofSeconds(10))
//		            .build();
//				HttpResponse<byte[]> response = httpClient
//					.send(request, HttpResponse.BodyHandlers.ofByteArray());
//			ByteArrayInputStream in = new ByteArrayInputStream(response.body());
			
			// PRUEBA LLAMAR SERVICIO APARTE (CON JSON)
//			JSONObject params = new JSONObject();
//			params.put("MODELO", mod303.isMonthPeriod()?"417":"420");
//			params.put("EJERCICIO", AonNumberUtils.toString(mod303.getYear()));
//			params.put("FIC", ModelAdmonUtils.getUnencodedFile(xml.getBytes(),StandardCharsets.UTF_8));
//			
//			String url = "http://localhost:8080/canarias/ModuloImpresion";
//			HttpClient httpClient = HttpClient.newBuilder()
//		            .version(HttpClient.Version.HTTP_2)
//		            .connectTimeout(Duration.ofSeconds(120))
//		            .build();
//			HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create( url ))
//				.setHeader( AonHttpUtils.CONTENT_TYPE, "application/json;charset=UTF-8")
//				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")
//				.POST(HttpRequest.BodyPublishers.ofString(params.toString()))
//				.build();
//			HttpResponse<byte[]> response = httpClient
//				.send(request, HttpResponse.BodyHandlers.ofByteArray());
//			
//			String ct = ModelAdmonUtils.getContentTypeHeader(response);
//			if (AonStringUtils.contains(ct, MimeType.TXT.getName())) {
//				ByteArrayInputStream in = new ByteArrayInputStream(response.body());
//			    String fileName = AonFiscalFileUtils.getFileName(mod303);
//			    MimeType mime = MimeType.TXT; 
//			    resp.setCharacterEncoding("ISO-8859-1");
//				resp.setContentType(mime.getName());
//				resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".atc" + "\";");
//				AonIOUtils.copy(in, resp.getOutputStream());
//				resp.flushBuffer();
//			}
//			else if (AonStringUtils.contains(ct, MimeType.JSON.getName())) {
//				JSONObject json = new JSONObject(new String(response.body()));
//				AEATResponse aeatResponse = new AEATResponse();
//				if (json.has("errores")) {
//					// Respuesta con errores, obtenemos los mensajes de error
//					JSONArray jsonErrores = json.optJSONArray("errores");
//					if (jsonErrores != null) {
//						for (int i = 0; i < jsonErrores.length(); i++ ) {
//							aeatResponse.addError(jsonErrores.getString(i));
//						}
//					}
//					manageWrongResponse(resp, aeatResponse);
//				} else {
//					giveExceptionBack(resp, "No se ha encontrado una respuesta válida por parte del módulo de impresión de la Agencia Tributaria Canaria. (JSON)");
//				}					
//			} else if (AonStringUtils.contains(ct, MimeType.HTML.getName())) {
//				giveExceptionBack(resp, response.body(), MimeType.HTML);					  
//			} else {					 					
//				giveExceptionBack(resp, "No se ha encontrado una respuesta válida por parte del módulo de impresión de la Agencia Tributaria Canaria.");
//			}
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

	private String obtenerXML(Mod303 mod303) throws JAXBException {
		DEC dec = Mod420ToDEC.getDEC(mod303);
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(DEC.class);
		Marshaller um = context.createMarshaller();
		um.setProperty("jaxb.encoding", "ISO-8859-1");
		um.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		um.marshal(dec, writer);
		return writer.toString();		
	}
	
	// FALTA - PRUEBA LLAMAR FUNCION LAMBDA AWS
	private void llamarAWS(String xml, Mod303 mod303, HttpServletResponse resp) throws IOException {

		// FALTA - POR AHORA LE VAMOS A PASAR UNICAMENTE EL FICHERO XML DE UN MODELO 420/417, LA IDEA ES QUE LA FUNCION SIRVA PARA VARIOS MODELOS
		// ENTONCES SE LE PASARA UN JSON CON EL MODELO, EJERCICIO Y EL XML
//		JSONObject params = new JSONObject();
//		params.put("modelo", mod303.isMonthPeriod()?"417":"420");
//		params.put("ejercicio", AonNumberUtils.toString(mod303.getYear()));
//		params.put("declaracion", ModelAdmonUtils.getUnencodedFile(xml.getBytes(),StandardCharsets.UTF_8));
		
		String payload =
		String.format("{"
		+ "\"declaracion\":\"%s\" "
		+ "}"
		, Base64.getEncoder().encodeToString(xml.getBytes()));
		
		System.out.println("payload = " + payload);
		
		String functionName = "aon-aws-atc";
		InvokeRequest invokeRequest = 
				new InvokeRequest()
				.withFunctionName(functionName )
				.withPayload(payload);
		
		InvokeResult invokeResult = null;

		try {
			AWSLambda awsLambda = AWSLambdaClientBuilder.defaultClient();

			invokeResult = awsLambda.invoke(invokeRequest);

			String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);
			
//			String ans = MIModelo420Client.call("aon-aws-atc" , xml.getBytes());
//			String ans = MIModelo420Client.prueba(xml);
			
			// write out the return value
			System.out.println("ans = " + ans);
			
			// Interpretar el resultado, será un JSON con resultado o errores
			JSONObject json = new JSONObject(ans);
			
			if (json.has("resultado")) {
				// Validación correcta, viene el fichero a presentar en resultado
				ByteArrayInputStream in = new ByteArrayInputStream(json.getString("resultado").getBytes());
			    String fileName = AonFiscalFileUtils.getFileName(mod303);
			    MimeType mime = MimeType.TXT; 
			    resp.setCharacterEncoding("ISO-8859-1");
				resp.setContentType(mime.getName());
				resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".atc" + "\";");
				AonIOUtils.copy(in, resp.getOutputStream());
				resp.flushBuffer();
			}
			else if (json.has("errores")) {
					// Validación con errores, obtenemos los mensajes de error
					AEATResponse aeatResponse = new AEATResponse();
					JSONArray jsonErrores = json.optJSONArray("errores");
					if (jsonErrores != null) {
						for (int i = 0; i < jsonErrores.length(); i++ ) {
							aeatResponse.addError(jsonErrores.getString(i));
						}
					}
					manageWrongResponse(resp, aeatResponse);
			} else {
					giveExceptionBack(resp, "ERROR INDEFINIDO (Mod420File)");
			}		
		
		} catch (Exception e) {
			System.out.println(e);
			giveExceptionBack(resp, "EXCEPTION ERROR (Mod420File): " + e.getMessage());
		}

//		System.out.println(invokeResult.getStatusCode());
		
	}
	
	// PARA PROBAR FICHERO SIMPLE DE PRUEBA
//	private static String obtenerXML() throws JAXBException {
//		String fileXML =
//			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
//			"<DEC PER=\"1T\" ANY=\"2025\" MOD=\"420\">" +
//			"<IDE>" +
//			"<OTP PAI=\"ES\" CP=\"35002\" CMU=\"35016\" POP=\"35\" NVP=\"AGENCIA TRIBUTARIA CANARIA\" SVP=\"CL\" NRS=\"DECLARACION SIN ACTIVIDAD\" NIF=\"B50111111\"/>" +
//			"</IDE>" +
//			"<RES TIP=\"S\"/>" +
//			"</DEC>";
//		return fileXML;
//	}
	
//	private static String obtenerPresentacion420(String declaracion) {
//		if (declaracion==null)
//			return null;
//		String resultado = null;
//		try {
//			System.out.println("PASO 2.1");
// 			Path prueba = Files.createTempDirectory("M420");
// 			String path = prueba.toString();
//			System.out.println("PASO 2.2. path="+path);
//			MIModelo420 miModelo420 = new MIModelo420(path);
//			System.out.println("PASO 2.3");
//			resultado = miModelo420.getFicheroPresentacion(declaracion);
//			System.out.println("PASO 2.4. resultado="+resultado);
//			if (resultado == null)
//				mostrarMensajes(miModelo420.getMensajes());
//		} catch (Exception e) {
//			e.printStackTrace();			
//			throw new AonCoreException(e);
//		}
//		return resultado;
//	}
//	
//	private static String obtenerPresentacion417(String declaracion) {
//		if (declaracion==null)
//			return null;
//		String resultado = null;
//		try {
//			System.out.println("PASO 2.1");
// 			Path prueba = Files.createTempDirectory("M417");
// 			String path = prueba.toString();
//			System.out.println("PASO 2.2. path="+path);
//			MIModelo417 miModelo417 = new MIModelo417(path);
//			System.out.println("PASO 2.3");
//			resultado = miModelo417.getFicheroPresentacion(declaracion);
//			System.out.println("PASO 2.4. resultado="+resultado);
//			if (resultado == null)
//				mostrarMensajes(miModelo417.getMensajes());
//		} catch (Exception e) {
//			e.printStackTrace();			
//			throw new AonCoreException(e);
//		}
//		return resultado;
//	}
//	
//	private static void mostrarMensajes(List<String> mensajes) {
//		if (mensajes != null && mensajes.size() > 0) {
//			System.out.println("Nº mensajes error: " + mensajes.size());
//			for (String m : mensajes)
//				System.out.println("" + m);
//		}
//	}
	
//	public static void main(String[] args) {
//		
//		String xml = obtenerXML();
//		String resultado = obtenerPresentacion(xml);
//		
//		if (resultado != null) {
//			System.out.println("Resultado:");
//			System.out.println(resultado);
//
//			try {
//				File archivo = new File("c:\\tmp\\prueba.atc");
//	            FileWriter escribir = new FileWriter(archivo, true);
//	            escribir.write(resultado);
//	            escribir.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	private AEATResponse manageWrongResponse(HttpServletResponse resp, AEATResponse response) {
		if (response.getErrores() == null || response.getErrores().isEmpty()) {
			giveExceptionBack(resp, "La Agencia Tributaria Canaria ha devuelto un error, pero no se han encontrado mensajes del mismo.");
		} else {
			String[] array = response.getErrores().toArray(new String[0]);
			giveExceptionBack(resp, array);				
		}
		return response;
	}
	
	private void giveExceptionBack( HttpServletResponse resp, String ... msgs)  {
		StringBuilder buff = new StringBuilder();
		buff.append(ERROR_TEMPLATE_START);
		buff.append(ERROR_TEMPLATE_AEAT);	
		buff.append(ERROR_TEMPLATE_BEFORE);
		for (String msg : msgs) {
			buff.append(MessageFormat.format(ERROR_TEMPLATE_BODY, msg));
		}
		buff.append(ERROR_TEMPLATE_AFTER);
		buff.append(ERROR_TEMPLATE_END);
		giveExceptionBack(resp, buff.toString().getBytes(StandardCharsets.UTF_8), MimeType.HTML);
	}
	
	private void giveExceptionBack( HttpServletResponse resp, byte[] data, MimeType mimeType )  {
		try {
			resp.setHeader(AonHttpUtils.CONTENT_TYPE, mimeType.getName());
			resp.setHeader(AonHttpUtils.CONTENT_ENCODING, StandardCharsets.UTF_8.displayName());				
			AonIOUtils.write( data, resp.getOutputStream() );
			resp.flushBuffer();
		} catch (IOException e) {
			throw new AonCoreException(MessageFormat.format("Unexpected exception [{0}] ", e.getMessage()));	
		}
	}
	
}

 