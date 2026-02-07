package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

import java.io.IOException;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.impl.jooq.dao.mod417_2025.Mod417ToDEC;
import com.esferalia.aon.occam.impl.jooq.dao.mod417_2026.Mod417ToDEC2026;
import com.esferalia.aon.occam.impl.jooq.dao.mod420_2025.Mod420ToDEC;

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
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod303 mod303 = MODEL303.get(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			
			System.out.println("PASO 1");
			
			// Obtener el XML
			String xml = mod303.isMonthPeriod() ? 
							mod303.getYear() >= 2026 ?  Mod417ToDEC2026.getDeclaration(mod303) : Mod417ToDEC.getDeclaration(mod303) // Modelo 417
							: Mod420ToDEC.getDeclaration(mod303); // Modelo 420
			
			System.out.println("PASO 2. xml="+xml);
			
			// Pasarlo al modulo de impresión para obtener el fichero para la presentación 
			ModelAdmonUtils.callAtcAwsFunction(xml, mod303, false, resp);
			
			System.out.println("PASO 3. OK");
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
//	private void pruebaFichero(String xml, Mod303 mod303, HttpServletResponse resp) throws IOException {
//
//		try {
//			MIModelo420Request input = new MIModelo420Request();
//			input.setDeclaracion(xml);
//			input.setBorrador(false);
//			MIModelo420Result result = new MIModelo420RequestHandler().handleRequest(input, null);
//
//			JSONObject json = new JSONObject(result);
//			
//			if (json.has("resultado")) {
//				// Validación correcta, viene el fichero a presentar en resultado
//			    String fileName = AonFiscalFileUtils.getFileName(mod303);
//			    resp.setCharacterEncoding("ISO-8859-1");
//				resp.setContentType(MimeType.TXT.getName());
//				resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".atc" + "\";");
//				AonIOUtils.write(json.getString("resultado").getBytes(),resp.getOutputStream());
//				resp.flushBuffer();
//			}
//			else if (json.has("errores")) {
//				// Validación con errores, obtenemos los mensajes de error
//				ModelAdmonUtils.manageWrongResponseCanarias(resp, json.optJSONArray("errores"));
//			} else {
//				ModelAdmonUtils.giveExceptionBackCanarias(resp, "ERROR INDEFINIDO (Mod420File)");
//			}		
//		
//		} catch (Exception e) {
//			System.out.println(e);
//			ModelAdmonUtils.giveExceptionBackCanarias(resp, "EXCEPTION ERROR (Mod420File): " + e.getMessage());
//		}
//		
//	}
	
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

 