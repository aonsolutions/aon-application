package com.code.aon.master.test;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import junit.framework.TestCase;

import com.code.aon.master.VersionManager;

public class VersionTest extends TestCase{
	

	public void testUpdateVersions() throws Exception {
		System.out.println("[BEGIN] testUpdateVersions");
		VersionManager vm = new VersionManager();
		String[] versions = vm.getVersions();
		for (String version: versions ) {
			URL url = vm.getUpdateScript(version);
			if (url == null) {
				System.out.println("Version " + version + " has no update!");
			}
		}
		System.out.println("[END] testUpdateVersions");
	}
	
	public void testAvailableVersions() throws Exception {
		System.out.println("[BEGIN] testAvailableVersions");
		VersionManager vm = new VersionManager();
		String version = vm.getDatabaseVersion(getConnection());
		URL[] urls = vm.getAvailableUpdateScripts(version);
		if (urls != null) {
			for (URL url : urls) {
				System.out.println("Available update file --> " + url);
			}
		} else {
			System.out.println("No available update files!");
		}
		getConnection().close();
		System.out.println("[END] testAvailableVersions");
	}
	
	public void testDatabaseVersion() {
		System.out.println("[BEGIN] testDatabaseVersion");
		try {
			VersionManager vm = new VersionManager();
			String version = vm.getDatabaseVersion(getConnection());
			System.out.println( "Current Version ..: " + version);
			testAvailableVersions();
			getConnection().close();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		System.out.println("[END] testDatabaseVersion");
	}
	
	public void testUptodateDatabase() {
		System.out.println("[BEGIN] testUptodateDatabase");
		try {
			VersionManager vm = new VersionManager();
			Connection c = getConnection();
			System.out.println( "Current Version ..: " + vm.getDatabaseVersion(c));
			vm.uptodateDatabase(c);
			System.out.println( "Current Version ..: " + vm.getDatabaseVersion(c));
			testAvailableVersions();
			getConnection().close();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		System.out.println("[END] testUptodateDatabase");
	}

	private Connection getConnection() throws ClassNotFoundException, SQLException {
		String url = "jdbc:mysql://volga:3306/aon_test_master?autoReconnect=true";
		String usr = "dbuser"; 
		String psw = "serubd2000";
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection(url,usr ,psw );
		return c;
	}
}
