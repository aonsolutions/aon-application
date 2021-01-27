package com.esferalia.aon.gwt.payroll.server;


import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqContrataContract;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "CONTRACT-SERVLET", urlPatterns = { "/ms/api/contract/*"})
public class ContractServlet extends HttpServlet {
	private static Logger LOGGER = Logger.getLogger(ContractServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("[GET] CONTRACT SERVLET");
		OutputStream os = resp.getOutputStream();
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");

		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		byte content [] = gjson.toJson(null).getBytes();
		try {
		
			Connection conn = AonServletUtils.getConnection(domainName);
			boolean allEmployees = Boolean.parseBoolean(req.getParameter("allEmployees"));  
			content = gjson.toJson(JooqContrataContract.getAllEmployeesInfo(conn, domainId, allEmployees)).getBytes();
			
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(500);
			content = e.getMessage().getBytes();
		}
		
		resp.setContentType("text/html");
		resp.setContentLength(content.length);
		os.write(content);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
