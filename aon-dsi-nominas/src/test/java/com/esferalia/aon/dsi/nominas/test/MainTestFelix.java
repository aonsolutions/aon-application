package com.esferalia.aon.dsi.nominas.test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TimeZone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.dsi.nominas.Traspaso;

@Disabled
public class MainTestFelix {

	// TEST QUE TRASPASA LOS DATOS, SOBRE UNA BASE DE DATOS YA EXISTENTE 
	// Y UN DOMINIO PADRE YA EXISTENTE
	
	private final String AON_DB = "pro-aon-solutions-net";  // Base de datos ya existente
	private final String OMEGA_DIRECTORIO = "/cometa32/nomin/010";  // Directorio de las tablas paradox de N�minas Omega
	private final int parentDomain = 5207;  // Dominio padre

	private Connection aonConn;

	@BeforeEach
	public void setupAon() throws Exception {
		aonConn = getAonConnection("jdbc:mysql://localhost/" + AON_DB, "dbuser", "serubd2000");		
	}

	@AfterEach
	public void teardownAon() throws Exception {
		aonConn.close();
	}

	@Test
	public void testTraspaso() throws Exception {
//		Traspaso.execute(OMEGA_DIRECTORIO, aonConn, parentDomain, null);
	}
	
	private static Connection getAonConnection(String url, String user,
			String password) throws SQLException {

		Properties info = new Properties();
		if (user != null)
			info.put("user", user);
		if (password != null)
			info.put("password", password);
		
		info.setProperty("serverTimezone", TimeZone.getDefault().getID());

		Driver driver = new com.mysql.jdbc.Driver();

		return driver.connect(url, info);

	}

}
