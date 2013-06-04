package com.code.aon.aio.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.pool.AonConnectionException;

public class TestServlet extends HttpServlet {
	
	private static final long serialVersionUID = 2880941215591419695L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String host = req.getParameter("aon.domain");
			if (host == null || "".equals(host)){
				host = req.getServerName();
			}
			CompanyDisplay cd = new CompanyDisplay();
			cd.init( host );
			resp.sendError(HttpServletResponse.SC_OK);
		} catch (AonConnectionException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (Throwable e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
	}
	

}
