package com.esferalia.aon.seres.ftp;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.seres.SeresInfo;
import com.esferalia.aon.seres.Seres;
import com.jcraft.jsch.JSchException;

public class SftpTest {
	
	private final static String SERVER = "s-67c038cdfb944c9c8.server.transfer.eu-west-1.amazonaws.com";
	private final static String USERNAME = "seres";
	private final static String PASSWORD = "Seres123";
	private final static Integer PORT = 22;
	
	@Test
	public void testConnect() {
		SeresInfo info = new SeresInfo()
				.setServer(SERVER)
				.setUser(USERNAME)
				.setPassword(PASSWORD)
				.setPort(PORT);
		try {
			connect(info);
		} catch (JSchException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testConnectHostNull() {
		SeresInfo info = new SeresInfo()
				.setUser(USERNAME)
				.setPassword(PASSWORD)
				.setPort(PORT);
		try {
			connect(info);
			fail("Host Nulo, no se debería de haber conectado");
		} catch (JSchException e) {
			if(!e.getMessage().contains("host must not be null")) {
				fail(e.getMessage());
			}
		}
	}

	@Test
	public void testConnectHostIncorrect() {
		SeresInfo info = new SeresInfo()
			.setServer(SERVER + "a")
			.setUser(USERNAME)
			.setPassword(PASSWORD)
			.setPort(PORT);
		try {
			connect(info);
			fail("Host Erroneo, no se debería de haber conectado.");
		} catch (JSchException e) {
			if(!e.getMessage().contains("UnknownHostException")) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testConnectUserNull() {
		SeresInfo info = new SeresInfo()
			.setServer(SERVER)
			.setPassword(PASSWORD)
			.setPort(PORT);
		try {
			connect(info);
			fail("Usuario Nulo, no se debería de haber conectado.");
		} catch (JSchException e) {
//			if(!e.getMessage().contains("Auth fail")) {
//				fail(e.getMessage());
//			}
		}
	}
	
	@Test
	public void testConnectUserIncorrect() {
		SeresInfo info = new SeresInfo()
			.setServer(SERVER)
			.setUser(USERNAME + "a")
			.setPassword(PASSWORD)
			.setPort(PORT);
		try {
			connect(info);
			fail("Usuario Erroneo, no se debería de haber conectado.");
		} catch (JSchException e) {
			if(!e.getMessage().contains("Auth fail")) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testConnectPasswordNull() {
		SeresInfo info = new SeresInfo()
			.setServer(SERVER)
			.setUser(USERNAME)
			.setPort(PORT);
		try {
			connect(info);
			fail("Contraseña Nula, no se debería de haber conectado.");
		} catch (JSchException e) {
			if(!e.getMessage().contains("Auth fail")) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testConnectPasswordIncorrect() {
		SeresInfo info = new SeresInfo()
			.setServer(SERVER)
			.setUser(USERNAME)
			.setPassword(PASSWORD + "a")
			.setPort(PORT);
		try {
			connect(info);
			fail("Contraseña Erroneo, no se debería de haber conectado.");
		} catch (JSchException e) {
			if(!e.getMessage().contains("Auth fail")) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testConnectPortNull() {
		SeresInfo info = new SeresInfo()
			.setServer(SERVER)
			.setUser(USERNAME)
			.setPassword(PASSWORD);
		try {
			connect(info);
		} catch (JSchException e) {
			fail(e.getMessage());
		}
	}
	
	private void connect(SeresInfo info) throws JSchException {
		Seres seres = new Seres(info);
		seres.checkLogin();
	}
}
