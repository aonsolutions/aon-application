package com.esferalia.aon.gwt.fiscal.server.fiscal.mod111;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod111 Send AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Mod111SendAEAT" })
public class Mod111SendAEAT extends HttpServlet {

	private static final long serialVersionUID = -5473378254653264265L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod111 model = MODEL111.getMod111(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			if (model == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			ModelAdmonUtils.send(resp, aeatParams, model);
		} catch (Exception e) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
