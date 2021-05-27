package com.esferalia.aon.dex.servlet.nav;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.esferalia.aon.dex.IDexConstants;
import com.esferalia.aon.dex.process.nav.Parameters;
import com.esferalia.aon.dex.process.nav.ProductionUnloadManager;

public class ProductionUnloadServlet extends HttpServlet implements IDexConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	private void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Parameters params = new Parameters();
		try {
			params.setDomainId(Integer.parseInt(request.getParameter(DOMAIN_ID)));
			params.setDomainName(request.getParameter(DOMAIN_NAME));
			params.setFromDate(DateUtils.parseDate(request.getParameter(DATE_FROM), new String[]{"dd/MM/yyyy"}));
			params.setToDate(DateUtils.parseDate(request.getParameter(DATE_TO), new String[]{"dd/MM/yyyy"}));
			params.setHotelId(request.getParameter(HOTEL) != null ? Integer.parseInt(request.getParameter(HOTEL)) : 0);

			ProductionUnloadManager manager = new ProductionUnloadManager();
			manager.processProductionList(params);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
