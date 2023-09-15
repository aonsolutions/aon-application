package com.esferalia.aon.gwt.fiscal.server.fiscal.mod131;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL131;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod131 Check AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Mod131CheckAEAT" })
public class Mod131CheckAEAT extends HttpServlet {
	
	private static final long serialVersionUID = -8391437522744646639L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod131 mod131 = MODEL131.getMod131(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			if (mod131 == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			ModelAdmonUtils.checkAEAT(resp, aeatParams, mod131);
		} catch (Exception e) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
