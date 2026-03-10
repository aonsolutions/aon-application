package com.esferalia.aon.gwt.fiscal.server.fiscal.mod421;

import java.io.IOException;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.impl.jooq.dao.mod421_2026.Mod421ToDEC2026;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod421 Validate Print ATC", urlPatterns = { "/aon_gwt_fiscal/ms/Mod421ValidatePrintATC" })
public class Mod421ValidatePrintATC extends HttpServlet {

	private static final long serialVersionUID = 5288088897821507984L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod421 mod421 = MODEL421.get(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			
			System.out.println("PASO 1");
			
			// Obtener el XML
			String xml = Mod421ToDEC2026.getDeclaration(mod421); 
			
			System.out.println("PASO 2. xml="+xml);
			
			// Pasarlo al modulo de impresión para obtener el borrador pdf
			ModelAdmonUtils.callAtcAwsFunction(xml, mod421, true, resp);
			
			System.out.println("PASO 3. OK");
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
}

 