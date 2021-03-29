package net.aonsolutions.aon.api.test.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.aonsolutions.aon.api.servlet.LoginServlet;
import net.aonsolutions.aon.api.test.request.ServletVisitor;

public class LoginVisit implements ServletVisitor{

	LoginServlet ls = new LoginServlet();
	
	@Override
	public void get(HttpServletRequest req, HttpServletResponse resp) {

	}

	@Override
	public void post(HttpServletRequest req, HttpServletResponse resp) {
		ls.doPost(req, resp);
	}

	@Override
	public void put(HttpServletRequest req, HttpServletResponse resp) {
	
	}

	@Override
	public void delete(HttpServletRequest req, HttpServletResponse resp) {
	
	}

	@Override
	public void patch(HttpServletRequest req, HttpServletResponse resp) {
	
	}
}
