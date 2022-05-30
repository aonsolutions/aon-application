package net.aonsolutions.aon.api.test.delivery;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.aonsolutions.aon.api.servlet.DeliveryServlet;
import net.aonsolutions.tests.request.IServlet;

public class DeliveryServletTest implements IServlet {

	DeliveryServlet ls = new DeliveryServlet();
	
	@Override
	public void get(HttpServletRequest req, HttpServletResponse resp) {
		ls.doGet(req, resp);
	}

	@Override
	public void post(HttpServletRequest req, HttpServletResponse resp) {
		ls.doPost(req, resp);
	}

	@Override
	public void put(HttpServletRequest req, HttpServletResponse resp) {
		ls.doPut(req, resp);
	}

	@Override
	public void delete(HttpServletRequest req, HttpServletResponse resp) {
		ls.doDelete(req, resp);
	}

	@Override
	public void patch(HttpServletRequest req, HttpServletResponse resp) {
		
	}
}
