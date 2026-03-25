package com.esferalia.aon.gwt.mod200.server.e2025;

import java.io.IOException;

import com.esferalia.aon.gwt.mod200.server.Model200AdmonUtils;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.mod200.api.MODEL2002025;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod2002025 Check Data Reponse Data", urlPatterns = { "/aon_gwt_mod200/ms/Mod2002025CheckDataResponseData" })
public class Mod2002025CheckDataResponseData extends HttpServlet {

	private static final long serialVersionUID = -5517522173106574527L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams params = Model200AdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(params.getDomainName())
					.setDomain(params.getDomainId())
					.setUser(params.getUser());
			Mod2002025 mod2002025 = MODEL2002025.getMod2002025ById(occam, params.getMod());
			if (mod2002025 == null) {
				Model200AdmonUtils.giveExceptionBack(resp, "Declaraci�n no encontrada" );
			}
			Model200AdmonUtils.giveDataResponseDataBack(resp, params, mod2002025);			
			
		} catch (AonCoreException e ) {
			Model200AdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}
}