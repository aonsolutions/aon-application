package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@MultipartConfig(location = "/tmp", fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class CretaBasesServletTestImpl extends CretaServlet {

	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(
			System.getProperty("java.io.tmpdir"));
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
//		req.setAttribute(org.eclipse.jetty.server.Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
		super.doPost(req, resp);
	}
	
	@Override
	protected Connection getConnection(HttpServletRequest req) throws SQLException {

		// first of all load JDBC driver
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String dbHost = "127.0.0.1";
		String dbPort = "3306";
		String dbName = "sig-grupo-esferalia";
		String dbUser = "aonsolutions";
		String dbPasswd = "40ns0lut10ns";

		String url = String
				.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbName);
		Connection connection = DriverManager.getConnection(url, dbUser,
				dbPasswd);

		return connection;
	}

}
