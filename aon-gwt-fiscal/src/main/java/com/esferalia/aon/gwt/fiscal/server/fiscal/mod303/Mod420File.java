package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.mod420_2025.DEC;
import com.esferalia.aon.occam.impl.jooq.dao.mod420_2025.Mod420ToDEC;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.io.AonIOUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod420 File download", urlPatterns = { "/aon_gwt_fiscal/ms/Model420File" })
public class Mod420File extends HttpServlet {

	private static final long serialVersionUID = 5298054660785878368L;

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
			
			System.out.println("PASO 1");
			
			// Obtener el XML
			String xml = obtenerXML(mod303);
			
			System.out.println("PASO 2. xml="+xml);
			
			// Pasarlo al modulo de impresión para obtener el fichero para la presentación telemática o carga en programa de ayuda
			String resultado = null;
//			if (mod303.isMonthPeriod()) {
//				resultado = obtenerPresentacion417(xml);
//			} else {
//				resultado = obtenerPresentacion420(xml);	
//			}
			
			if (resultado == null) {
				throw new AonCoreException("RESULTADO ES NULO");				
			}
			
			System.out.println("PASO 3. OK");
			
			// PRUEBA LLAMAR SERVICIO APARTE
//			HttpRequest request = HttpRequest.newBuilder()
//					.uri(URI.create( "http://localhost:8080/PruebaDos/PruebaCanarias" ))
//					.POST(HttpRequest.BodyPublishers.ofString("MOD=modelo"))
//					.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")
//					.setHeader( AonHttpUtils.CONTENT_TYPE, "application/x-www-form-urlencoded")
//					.build();
//				HttpClient httpClient = HttpClient.newBuilder()
//		            .version(HttpClient.Version.HTTP_2)
//		            .connectTimeout(Duration.ofSeconds(10))
//		            .build();
//				HttpResponse<byte[]> response = httpClient
//					.send(request, HttpResponse.BodyHandlers.ofByteArray());
//			ByteArrayInputStream in = new ByteArrayInputStream(response.body());				
			
			ByteArrayInputStream in = new ByteArrayInputStream(resultado.getBytes());
		    String fileName = AonFiscalFileUtils.getFileName(mod303);
		    MimeType mime = MimeType.TXT; 
		    resp.setCharacterEncoding("ISO-8859-1");
			resp.setContentType(mime.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".atc" + "\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

	private static String obtenerXML(Mod303 mod303) throws JAXBException {
		DEC dec = Mod420ToDEC.getDEC(mod303);
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(DEC.class);
		Marshaller um = context.createMarshaller();
		um.setProperty("jaxb.encoding", "ISO-8859-1");
		um.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		um.marshal(dec, writer);
		return writer.toString();		
	}
	
	// FALTA - POR AHORA PARA PROBAR, AQUI SE HARA LA CONVERSION DEL MODELO A XML SEGUN EL ESQUEMA
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
	
}

 