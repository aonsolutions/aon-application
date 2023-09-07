package com.esferalia.aon.ingenet.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.aonsolutions.tests.request.IServlet;

public class IngenetDeliveryServletTest implements IServlet {

	IngenetDeliveryServlet servlet = new IngenetDeliveryServlet();
	
	@Override
	public void get(HttpServletRequest req, HttpServletResponse resp) {
		try {
			servlet.doGet(req, resp);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void post(HttpServletRequest req, HttpServletResponse resp) {
		try {
			servlet.doPost(req, resp);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
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
