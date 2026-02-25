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
import com.esferalia.aon.occam.impl.jooq.dao.mod420_2026.Mod420ToDEC2026;

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
			String xml = switch (mod303.getYear()) {
				case 2025 -> mod303.isMonthPeriod() ? Mod417ToDEC.getDeclaration(mod303) : Mod420ToDEC.getDeclaration(mod303);
				default -> mod303.isMonthPeriod() ? Mod417ToDEC2026.getDeclaration(mod303) : Mod420ToDEC2026.getDeclaration(mod303);
			};
			
			System.out.println("PASO 2. xml="+xml);
			
			// Pasarlo al modulo de impresión para obtener el borrador pdf
			ModelAdmonUtils.callAtcAwsFunction(xml, mod303, true, resp);
			
			System.out.println("PASO 3. OK");
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
}

 