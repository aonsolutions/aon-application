package net.aonsolutions.aon.api.test.rawdoc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.servlet.RawdocServlet;
import net.aonsolutions.tests.request.IServlet;

public class RawdocServletTest implements IServlet {

	RawdocServlet rawdocServlet = new RawdocServlet();
	
	@Override
	public void get(HttpServletRequest req, HttpServletResponse resp) {
		rawdocServlet.doGet(req, resp);
	}

	@Override
	public void post(HttpServletRequest req, HttpServletResponse resp) {
		rawdocServlet.doPost(req, resp);
	}

	@Override
	public void put(HttpServletRequest req, HttpServletResponse resp) {
		rawdocServlet.doPut(req, resp);
	}

	@Override
	public void delete(HttpServletRequest req, HttpServletResponse resp) {
		rawdocServlet.doDelete(req, resp);
	}

	@Override
	public void patch(HttpServletRequest req, HttpServletResponse resp) {
	
	}
}
