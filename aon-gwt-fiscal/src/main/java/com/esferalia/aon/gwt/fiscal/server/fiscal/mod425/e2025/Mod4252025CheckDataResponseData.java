package com.esferalia.aon.gwt.fiscal.server.fiscal.mod425.e2025;

import java.io.IOException;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL4252025;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod4252025 Check Data Reponse Data", urlPatterns = { "/aon_gwt_fiscal/ms/Mod4252025CheckDataResponseData" })
public class Mod4252025CheckDataResponseData extends HttpServlet {

	private static final long serialVersionUID = -1358555942392200350L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams params = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(params.getDomainName())
					.setDomain(params.getDomainId())
					.setUser(params.getUser());
			Mod4252025 mod4252025 = MODEL4252025.get(occam, params.getMod());
			if (mod4252025 == null) {
				ModelAdmonUtils.giveExceptionBack(resp, "Declaración no encontrada" );
			}
			ModelAdmonUtils.giveDataResponseDataBack(resp, params, mod4252025);			
			
		} catch (AonCoreException e ) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}
}
