package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod303 Check Data Reponse Data", urlPatterns = { "/aon_gwt_fiscal/ms/Mod303CheckDataResponseData" })
public class Mod303CheckDataResponseData extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(Mod303CheckAEAT.class.getName()); 
	private static final long serialVersionUID = -8391437522744646639L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams params = Mod303AeatUtils.getAEATParams(req);
			Mod303AeatUtils.giveDataResponseDataBack(resp, params);			
			
		} catch (AonCoreException e ) {
			Mod303AeatUtils.giveExceptionBack(resp,e.getMessage());
		}
	}
}
