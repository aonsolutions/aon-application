package com.esferalia.aon.gwt.mod200.server.e2023;

import java.io.IOException;

import com.esferalia.aon.gwt.mod200.server.Model200AdmonUtils;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.mod200.api.MODEL2002023;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod2002023 Check Data Reponse Data", urlPatterns = { "/aon_gwt_mod200/ms/Mod2002023CheckDataResponseData" })
public class Mod2002023CheckDataResponseData extends HttpServlet {

	private static final long serialVersionUID = 8962165983359245524L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams params = Model200AdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(params.getDomainName())
					.setDomain(params.getDomainId())
					.setUser(params.getUser());
			Mod2002023 mod2002023 = MODEL2002023.getMod2002023ById(occam, params.getMod());
			if (mod2002023 == null) {
				Model200AdmonUtils.giveExceptionBack(resp, "Declaración no encontrada" );
			}
			Model200AdmonUtils.giveDataResponseDataBack(resp, params, mod2002023);			
			
		} catch (AonCoreException e ) {
			Model200AdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}
}
