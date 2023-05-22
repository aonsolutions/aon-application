package com.esferalia.aon.gwt.fiscal.server.fiscal.mod390;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL3902022;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902022;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod3902022 Check Data Reponse Data", urlPatterns = { "/aon_gwt_fiscal/ms/Mod3902022CheckDataResponseData" })
public class Mod3902022CheckDataResponseData extends HttpServlet {

	private static final long serialVersionUID = -8391437522744646639L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams params = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(params.getDomainName())
					.setDomain(params.getDomainId())
					.setUser(params.getUser());
			Mod3902022 mod3902022 = MODEL3902022.get(occam, params.getMod());
			if (mod3902022 == null) {
				ModelAdmonUtils.giveExceptionBack(resp, "Declaración no encontrada" );
			}
			ModelAdmonUtils.giveDataResponseDataBack(resp, params, mod3902022);			
			
		} catch (AonCoreException e ) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}
}
