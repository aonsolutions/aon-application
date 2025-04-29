package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

import java.io.IOException;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.impl.jooq.dao.mod420_2025.Mod420ToDEC;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod420 Validate Print ATC", urlPatterns = { "/aon_gwt_fiscal/ms/Mod420ValidatePrintATC" })
public class Mod420ValidatePrintATC extends HttpServlet {

	private static final long serialVersionUID = 208024560660799390L;

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
			String xml = Mod420ToDEC.getDeclaration(mod303);
			
			System.out.println("PASO 2. xml="+xml);
			
			// Pasarlo al modulo de impresión para obtener el borrador pdf
			ModelAdmonUtils.callAtcAwsFunction(xml, mod303, true, resp);
//			pruebaBorrador(xml, resp);
			
			System.out.println("PASO 3. OK");
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
//	private void pruebaBorrador(String xml, HttpServletResponse resp) throws IOException {
//
//		try {
//			MIModelo420Request input = new MIModelo420Request();
//			input.setDeclaracion(Base64.getEncoder().encodeToString(xml.getBytes()));
//			input.setBorrador(true);
//			MIModelo420Result result = new MIModelo420RequestHandler().handleRequest(input, null);
//			
//			JSONObject json = new JSONObject(result);
//			System.out.println("result = " + result);
//			
//			if (json.has("resultado")) {
//				ModelAdmonUtils.giveBase64Back(resp, json.getString("resultado").getBytes(StandardCharsets.ISO_8859_1), MimeType.PDF);
//			}
//			else if (json.has("errores")) {
//				ModelAdmonUtils.manageWrongResponseCanarias(resp, json.optJSONArray("errores"));
//			} else {
//				ModelAdmonUtils.giveExceptionBackCanarias(resp, "ERROR INDEFINIDO (Mod420ValidatePrintATC)");
//			}		
//		
//		} catch (Exception e) {
//			System.out.println(e);
//			ModelAdmonUtils.giveExceptionBackCanarias(resp, "EXCEPTION ERROR (Mod420ValidatePrintATC): " + e.getMessage());
//		}
//		
//	}
	
}

 