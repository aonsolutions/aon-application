package com.code.aon.aio.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.AonVersion;

public class TirantConnectionServlet extends HttpServlet {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final String URL = "http://www.tirantasesores.com/ticket/tolTicketProvider.do?user=AON1&password=ASESOR";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		BufferedReader in = null;
		try {
			URL dir = new URL(URL);
			HttpURLConnection connection = (HttpURLConnection) dir.openConnection();
			StringBuffer buffer = new StringBuffer();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			System.out.println(connection.getResponseCode() + " --- " + connection.getResponseMessage());
			while (in.ready()) {
				buffer.append(in.readLine());
			}
			System.out.println("Redireccionando a " + buffer.toString());
			response.sendRedirect(buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) in.close();
		}

	}
}
