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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;

public class TirantConnectionServlet extends HttpServlet {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final String URL = "http://www.tirantasesores.com/ticket/tolTicketProvider.do";
	
	public static final int TIRANT_FISCAL_PAYROLL = 1;
	public static final int TIRANT_FULL = 2;
	public static final int TIRANT_FISCAL = 3;
	public static final int TIRANT_PAYROLL = 4;
	
	private int getType( HttpServletRequest request ) { 
		String uri = request.getRequestURI();
		String value = StringUtils.right(uri, 1);
		if ( NumberUtils.isDigits(value) ) {
			return NumberUtils.toInt(value);
		}
		return 0;
	}

	private String getPassword( int type ) {
		switch (type) {
			case TIRANT_FULL:
				return "ASESOR";
			case TIRANT_FISCAL:
				return "FISCAL";
			case TIRANT_PAYROLL:
				return "LABORAL";
			case TIRANT_FISCAL_PAYROLL:
				return "ASESOR";
		}
		return null;
	}
	
	private String getURL( HttpServletRequest request ) {
		StringBuffer sb = new StringBuffer();
		sb.append(URL);
		int type = getType(request);
		if ( type != 0 ) {
			sb.append("?user=AON").append(type);
			sb.append("&password=").append(getPassword(type));
		}
		return sb.toString();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		BufferedReader in = null;
		try {
			URL dir = new URL(getURL(request));
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
