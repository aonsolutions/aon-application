package com.esferalia.aon.gwt.fiscal.server.fiscal;

import java.io.IOException;

import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Matrix Send AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/MatrixSendAEAT" })
public class MatrixSendAEAT extends HttpServlet {

	private static final long serialVersionUID = 6152551748473230508L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			ModelAdmonUtils.sendFromMatrix(resp, aeatParams);
		} catch (Exception e) {
			ModelAdmonUtils.giveExceptionBack(resp, e.getMessage());
		}
	}
	
}
