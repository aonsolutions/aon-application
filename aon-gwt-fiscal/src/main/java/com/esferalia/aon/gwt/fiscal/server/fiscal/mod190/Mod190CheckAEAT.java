package com.esferalia.aon.gwt.fiscal.server.fiscal.mod190;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL190;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod190 Check AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Mod190CheckAEAT" })
public class Mod190CheckAEAT extends HttpServlet {
	
	private static final long serialVersionUID = 5522101454237873381L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod190 mod190 = MODEL190.get(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			if (mod190 == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			ModelAdmonUtils.checkAEAT(resp, aeatParams, mod190);
		} catch (Exception e) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
