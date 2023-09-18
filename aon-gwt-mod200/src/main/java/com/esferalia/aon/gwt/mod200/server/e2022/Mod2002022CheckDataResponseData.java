package com.esferalia.aon.gwt.mod200.server.e2022;

import java.io.IOException;

import com.esferalia.aon.gwt.mod200.server.Model200AdmonUtils;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.mod200.api.MODEL2002022;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod2002022 Check Data Reponse Data", urlPatterns = { "/aon_gwt_mod200/ms/Mod2002022CheckDataResponseData" })
public class Mod2002022CheckDataResponseData extends HttpServlet {

	private static final long serialVersionUID = 8962165983359245524L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams params = Model200AdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(params.getDomainName())
					.setDomain(params.getDomainId())
					.setUser(params.getUser());
			Mod2002022 mod2002022 = MODEL2002022.getMod2002022ById(occam, params.getMod());
			if (mod2002022 == null) {
				Model200AdmonUtils.giveExceptionBack(resp, "Declaración no encontrada" );
			}
			Model200AdmonUtils.giveDataResponseDataBack(resp, params, mod2002022);			
			
		} catch (AonCoreException e ) {
			Model200AdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}
}
