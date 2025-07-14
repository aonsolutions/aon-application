package com.esferalia.aon.gwt.mod200.server.e2024;

import java.io.IOException;

import com.esferalia.aon.gwt.mod200.server.Model200AdmonUtils;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.mod200.api.MODEL2002024;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod2002024 Send AEAT", urlPatterns = { "/aon_gwt_mod200/ms/Mod2002024SendAEAT" })
public class Mod2002024SendAEAT extends HttpServlet {

	private static final long serialVersionUID = -7769111734580984482L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = Model200AdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod2002024 mod2002024 = MODEL2002024.getMod2002024ById(occam, Model200AdmonUtils.getFiscalModelId(aeatParams));
			if (mod2002024 == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			Model200AdmonUtils.send(resp, aeatParams, mod2002024);
		} catch (Exception e) {
			Model200AdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}