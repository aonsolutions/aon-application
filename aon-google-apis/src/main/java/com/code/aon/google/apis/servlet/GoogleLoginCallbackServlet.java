package com.code.aon.google.apis.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GoogleLoginCallbackServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		super.doGet(req, resp);

		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/login/popupclose.jsp");

		dispatcher.forward(req, resp);

	}

}
