package com.esferalia.aon.gwt.fiscal.server.fiscal.mod193;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL193;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod193 Send AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Mod193SendAEAT" })
public class Mod193SendAEAT extends HttpServlet {

	private static final long serialVersionUID = 2205093258725601539L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod193 mod193 = MODEL193.get(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			if (mod193 == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			ModelAdmonUtils.sendOnlineTGVI(resp, aeatParams, mod193);
		} catch (Exception e) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
