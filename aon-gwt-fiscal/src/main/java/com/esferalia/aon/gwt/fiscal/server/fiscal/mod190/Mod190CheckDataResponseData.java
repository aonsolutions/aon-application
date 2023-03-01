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

@WebServlet(name = "Mod190 Check Data Reponse Data", urlPatterns = { "/aon_gwt_fiscal/ms/Mod190CheckDataResponseData" })
public class Mod190CheckDataResponseData extends HttpServlet {

	private static final long serialVersionUID = -3278276769178948397L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams params = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(params.getDomainName())
					.setDomain(params.getDomainId())
					.setUser(params.getUser());
			Mod190 mod190 = MODEL190.get(occam, params.getMod());
			if (mod190 == null) {
				ModelAdmonUtils.giveExceptionBack(resp, "Declaración no encontrada" );
			}
			ModelAdmonUtils.giveDataResponseDataBack(resp, params, mod190);			
			
		} catch (AonCoreException e ) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}
}
