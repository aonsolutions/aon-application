package net.aonsolutions.tests.request;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IServlet {
	void get(HttpServletRequest request, HttpServletResponse response);
	void post(HttpServletRequest request, HttpServletResponse response);
	void put(HttpServletRequest request, HttpServletResponse response);
	void delete(HttpServletRequest request, HttpServletResponse response);
	void patch(HttpServletRequest request, HttpServletResponse response);
}
