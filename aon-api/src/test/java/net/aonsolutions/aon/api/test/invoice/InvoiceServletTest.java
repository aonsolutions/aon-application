package net.aonsolutions.aon.api.test.invoice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.aonsolutions.aon.api.servlet.InvoiceServlet;
import net.aonsolutions.tests.request.IServlet;

public class InvoiceServletTest implements IServlet {

	InvoiceServlet is = new InvoiceServlet();
	
	@Override
	public void get(HttpServletRequest req, HttpServletResponse resp) {
		is.doGet(req, resp);
	}

	@Override
	public void post(HttpServletRequest req, HttpServletResponse resp) {
		is.doPost(req, resp);
	}

	@Override
	public void put(HttpServletRequest req, HttpServletResponse resp) {
		is.doPut(req, resp);
	}

	@Override
	public void delete(HttpServletRequest req, HttpServletResponse resp) {
		is.doDelete(req, resp);
	}

	@Override
	public void patch(HttpServletRequest req, HttpServletResponse resp) {

	}
}
