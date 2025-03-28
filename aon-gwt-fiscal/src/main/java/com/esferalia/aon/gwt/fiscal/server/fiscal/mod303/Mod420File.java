package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

import java.io.IOException;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;

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
			

//			ByteArrayOutputStream output = new ByteArrayOutputStream();
//			OutputStreamWriter wr = null;
//			try {
//				wr = new OutputStreamWriter(output,"ISO-8859-1");
//			} catch (UnsupportedEncodingException e) {
//				wr = new OutputStreamWriter(output);
//			}
//			PrintWriter writer = new PrintWriter(wr);
//			Mod303Writer.fillWriter(mod303, writer);
//			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
//			
//		    String fileName = AonFiscalFileUtils.getFileName(mod303);
//		    // MimeType mime = mod303.isAraba()?MimeType.XML:MimeType.TXT;
//		    MimeType mime = MimeType.TXT;
//		    resp.setCharacterEncoding("ISO-8859-1");
//			resp.setContentType(mime.getName());
//			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "." + mime.getExtension()+ "\";");
//			AonIOUtils.copy(in, resp.getOutputStream());
//			resp.flushBuffer();
			
			// FALTA - Obtener el XML
			String xml = obtenerXML();
			
			System.out.println("PASO 2. xml="+xml);
			
			// FALTA - Pasarlo al modulo de impresión para obtener el fichero para la presentación telemática o carga en programa de ayuda
//			String resultado = PruebaCanarias.obtenerPresentacion(xml);
//			
//			if (resultado == null) {
//				throw new AonCoreException("RESULTADO ES NULO");
//			}
//			
//			System.out.println("PASO 3. resultado="+resultado);
//			
//			ByteArrayInputStream in = new ByteArrayInputStream(resultado.getBytes());
//		    String fileName = AonFiscalFileUtils.getFileName(mod303);
//		    MimeType mime = MimeType.TXT; 
//		    resp.setCharacterEncoding("ISO-8859-1");
//			resp.setContentType(mime.getName());
//			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "." + mime.getExtension()+ "\";");
//			AonIOUtils.copy(in, resp.getOutputStream());
//			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

	// FALTA - POR AHORA PARA PROBAR, AQUI SE HARA LA CONVERSION DEL MODELO A XML SEGUN EL ESQUEMA
	private static String obtenerXML() {
		String fileXML =
			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
			"<DEC PER=\"1T\" ANY=\"2025\" MOD=\"420\">" +
			"<IDE>" +
			"<OTP PAI=\"ES\" CP=\"35002\" CMU=\"35016\" POP=\"35\" NVP=\"AGENCIA TRIBUTARIA CANARIA\" SVP=\"CL\" NRS=\"DECLARACION SIN ACTIVIDAD\" NIF=\"B50111111\"/>" +
			"</IDE>" +
			"<RES TIP=\"S\"/>" +
			"</DEC>";
		return fileXML;
	}
//	
//	// FALTA - OBTENER FICHERO PARA PRESENTACION PASANDO EL XML AL MODULO DE IMPRESION DE LA ATC
//	private static String obtenerPresentacion(String declaracion) {
//		if (declaracion==null)
//			return null;
//		String resultado = null;
//		try {
//			System.out.println("PASO 2.1");
////			Path prueba = Files.createTempDirectory("Temp");
////			String path = prueba.toString();
//			String path = "C:\\TEMP";
////			File prueba = createTempDirectory("PRUEBA");
////			String path = prueba.toString();
//			System.out.println("PASO 2.2. path="+path);
//			org.grecasa.ext.pa.mod420.MIModelo420 miModelo420 = new org.grecasa.ext.pa.mod420.MIModelo420(path);
//			System.out.println("PASO 2.3");
//			resultado = miModelo420.getFicheroPresentacion(declaracion);
//			System.out.println("PASO 2.4. resultado="+resultado);
//			if (resultado == null)
//				mostrarMensajes(miModelo420.getMensajes());
//		} catch (Exception e) {
//			e.printStackTrace();
//			/*
//			 * TODO: procesar la excepcion ...
//			 */
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
//	
////    public static File createTempDirectory(String prefix) throws IOException {
////    	// TODO Auto-generated method stub
////        File dir = new File(System.getProperty("java.io.tmpdir"), prefix);
////        dir.mkdir();
////        if (System.getProperty("poi.keep.tmp.files") == null)
////            dir.deleteOnExit();
////        return dir;
////    }
//	
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
//		
//	}
//	
	
}
 