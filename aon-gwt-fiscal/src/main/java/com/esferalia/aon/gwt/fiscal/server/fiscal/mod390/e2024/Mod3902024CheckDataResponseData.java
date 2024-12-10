package com.esferalia.aon.gwt.fiscal.server.fiscal.mod390.e2024;

import java.io.IOException;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL3902024;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902024;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod3902024 Check Data Reponse Data", urlPatterns = { "/aon_gwt_fiscal/ms/Mod3902024CheckDataResponseData" })
public class Mod3902024CheckDataResponseData extends HttpServlet {

	private static final long serialVersionUID = -3117137491107045173L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams params = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(params.getDomainName())
					.setDomain(params.getDomainId())
					.setUser(params.getUser());
			Mod3902024 mod3902024 = MODEL3902024.get(occam, params.getMod());
			if (mod3902024 == null) {
				ModelAdmonUtils.giveExceptionBack(resp, "Declaración no encontrada" );
			}
			ModelAdmonUtils.giveDataResponseDataBack(resp, params, mod3902024);			
			
		} catch (AonCoreException e ) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}
}
