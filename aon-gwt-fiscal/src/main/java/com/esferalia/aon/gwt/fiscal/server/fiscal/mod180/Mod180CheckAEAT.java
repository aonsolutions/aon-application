package com.esferalia.aon.gwt.fiscal.server.fiscal.mod180;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL180;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod180 Check AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Mod180CheckAEAT" })
public class Mod180CheckAEAT extends HttpServlet {
	
	private static final long serialVersionUID = 6444960088063581350L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod180 mod180 = MODEL180.get(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			if (mod180 == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			ModelAdmonUtils.checkAEAT(resp, aeatParams, mod180);
		} catch (Exception e) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
