package com.code.aon.webservice.common;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Company;

@SuppressWarnings("serial")
@WebServlet(name = "AonCompanyServlet", urlPatterns = {"/ms/company/*"})
public class CompanyServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CompanyServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON COMPANY SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		JSONArray jsArray = new JSONArray();
		AON_SOLUTIONS.getCompanyStream(token).forEach(
			domain -> jsArray.put(company2json(domain)));
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, jsArray, new JSONObject());	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("COMPANY SERVLET - POST METHOD");
	}
	
	private JSONObject company2json(Company company) {
		return new JSONObject()
				.put("id", company.getDomain())
				.put("domain", company.getDomainName())
				.put("name", company.getName())
				.put("document", company.getDocument());
	}
	
}
