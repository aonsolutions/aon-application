package com.esferalia.aon.gwt.fiscal.server.fiscal.mod180;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL180;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod180 Check Data Reponse Data", urlPatterns = { "/aon_gwt_fiscal/ms/Mod180CheckDataResponseData" })
public class Mod180CheckDataResponseData extends HttpServlet {

	private static final long serialVersionUID = 5491823496353511497L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams params = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(params.getDomainName())
					.setDomain(params.getDomainId())
					.setUser(params.getUser());
			Mod180 mod180 = MODEL180.get(occam, params.getMod());
			if (mod180 == null) {
				ModelAdmonUtils.giveExceptionBack(resp, "Declaración no encontrada" );
			}
			ModelAdmonUtils.giveDataResponseDataBack(resp, params, mod180);			
			
		} catch (AonCoreException e ) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}
}
