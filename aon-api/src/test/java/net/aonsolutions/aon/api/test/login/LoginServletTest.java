package net.aonsolutions.aon.api.test.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.aonsolutions.aon.api.servlet.LoginServlet;
import net.aonsolutions.tests.request.IServlet;

public class LoginServletTest implements IServlet {

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
