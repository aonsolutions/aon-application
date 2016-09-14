package com.code.aon.webservice.issues;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "UserServlet", urlPatterns = { "/user/*" })
public class UserServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
		String serverName = req.getServerName();
		
		String pathInfo = req.getPathInfo();
		
		Object object = new Object();
				
		String js = req.getParameter("callback");
		if(js != null){
			resp.setContentType("application/javascript; charset=utf-8");     
			PrintWriter out = resp.getWriter();
			out.print(js + "({" +"\"meta\":{}, \"data\":" + object +"});");
			out.flush();
		} else {
			resp.setContentType("application/json");     
			PrintWriter out = resp.getWriter();
			out.print(object);
			out.flush();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST METHOD");
		super.doPost(req, resp);
	}
}
