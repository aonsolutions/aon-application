package com.esferalia.aon.gwt.fiscal.server.fiscal.mod425.e2025;

import java.io.IOException;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL4252025;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.occam.impl.jooq.dao.mod425_2025.Mod425ToDEC;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod425 2025 File download", urlPatterns = { "/aon_gwt_fiscal/ms/Model4252025File" })
public class Mod4252025File extends HttpServlet {
	
	private static final long serialVersionUID = 1113672513474523748L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			System.out.println("Mod4252025File");
			System.out.println("PASO 0");
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod4252025 mod425 = MODEL4252025.get(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			
			System.out.println("PASO 1");
			
			// Obtener el XML
			String xml = Mod425ToDEC.getDeclaration(mod425);
			
			System.out.println("PASO 2. xml="+xml);
			
			// FALTA - TENGO QUE MODIFICAR LA FUNCION AWS PARA QUE ACEPTE EL MOD4252025
			// Pasarlo al modulo de impresión para obtener el fichero para la presentación 
//			ModelAdmonUtils.callAtcAwsFunction(xml, mod425, false, resp);
			
			System.out.println("PASO 3. OK");
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

}
