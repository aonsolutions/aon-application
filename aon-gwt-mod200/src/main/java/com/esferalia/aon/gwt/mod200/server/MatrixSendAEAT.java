package com.esferalia.aon.gwt.mod200.server;

import java.io.IOException;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Matrix Send AEAT", urlPatterns = { "/aon_gwt_mod200/ms/MatrixSendAEAT" })
public class MatrixSendAEAT extends HttpServlet {

	private static final long serialVersionUID = 6152551748473230508L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Model200AdmonUtils.sendFromMatrix(resp, aeatParams);  
		} catch (Exception e) {
			ModelAdmonUtils.giveExceptionBack(resp, e.getMessage());
		}
	}
	
}
