package com.esferalia.aon.gwt.fiscal.server.fiscal.mod390;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL3902022;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902022;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod3902022 Send AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Mod3902022SendAEAT" })
public class Mod3902022SendAEAT extends HttpServlet {

	private static final long serialVersionUID = -1886808421915141367L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod3902022 mod3902022 = MODEL3902022.get(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			if (mod3902022 == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			ModelAdmonUtils.send(resp, aeatParams, mod3902022);
		} catch (Exception e) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
