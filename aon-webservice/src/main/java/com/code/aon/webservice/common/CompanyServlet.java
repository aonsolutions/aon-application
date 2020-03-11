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
import com.esferalia.aon.occam.api.model.Domain;

@SuppressWarnings("serial")
@WebServlet(name = "AonCompanyServlet", urlPatterns = {"/ms/company/*"})
public class CompanyServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CompanyServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON COMPANY SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		JSONArray jsArray = new JSONArray();
		AON_SOLUTIONS.getDomainStream(token).forEach(
			domain -> jsArray.put(domain2json(domain)));
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, jsArray, new JSONObject());	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("COMPANY SERVLET - POST METHOD");
	}
	
	private JSONObject domain2json(Domain domain) {
		return new JSONObject()
				.put("id", domain.getId())
				.put("name", domain.getName())
				.put("description", domain.getDescription())
				.put("type", domain.getDomainType().ordinal())
				.put("parent", domain.getParentId())
				.put("scope", domain.getScope());
	}
	
}
