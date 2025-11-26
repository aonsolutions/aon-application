package com.esferalia.aon.gwt.fiscal.server.fiscal.mod425.e2025;

import java.io.IOException;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL4252025;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.occam.impl.jooq.dao.mod425_2025.Mod425ToDEC;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod425 2025 Validate Print ATC", urlPatterns = { "/aon_gwt_fiscal/ms/Mod4252025ValidatePrintATC" })
public class Mod4252025ValidatePrintATC extends HttpServlet {

	private static final long serialVersionUID = -6450099054253219939L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod4252025 mod425 = MODEL4252025.get(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			
			if (mod425 == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			
			System.out.println("PASO 1");
			
			// Obtener el XML
			String xml = Mod425ToDEC.getDeclaration(mod425);
			
			System.out.println("PASO 2. xml="+xml);
			
			// Pasarlo al modulo de impresión para obtener el borrador pdf
			ModelAdmonUtils.callAtcAwsFunction(xml, mod425, true, resp);
			
			System.out.println("PASO 3. OK");
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
}
