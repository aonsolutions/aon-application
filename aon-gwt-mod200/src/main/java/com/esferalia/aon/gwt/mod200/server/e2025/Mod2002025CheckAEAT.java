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

@WebServlet(name = "Mod2002025 Check AEAT", urlPatterns = { "/aon_gwt_mod200/ms/Mod2002025CheckAEAT" })
public class Mod2002025CheckAEAT extends HttpServlet {
	
	private static final long serialVersionUID = -6194087739090721618L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = Model200AdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod2002025 mod2002025 = MODEL2002025.getMod2002025ById(occam, Model200AdmonUtils.getFiscalModelId(aeatParams));
			if (mod2002025 == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			Model200AdmonUtils.checkAEAT(resp, aeatParams, mod2002025);
		} catch (Exception e) {
			Model200AdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
