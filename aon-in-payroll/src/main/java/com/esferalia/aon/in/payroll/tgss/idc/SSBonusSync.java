package com.esferalia.aon.in.payroll.tgss.idc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TimeZone;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;

public class SSBonusSync {
	
	private DSLContext dslContext;
	
	public SSBonusSync(DSLContext dslContext) {
		this.dslContext = dslContext;
	}
	
	public DSLContext getDSLContext() {
		return this.dslContext;
	}
	
	private void syncSSBonus(InputStream is) {
		SSBonusListener ssBonusListener = new SSBonusListener();
		ssBonusListener.setDSLContext(getDSLContext());
		try {
			IdcplnssParser.parse(is, ssBonusListener);
		} catch (IOException exc) {
			exc.printStackTrace();
		} catch (UnknownPDFException exc) {
			try {
				IdcplcccParser.parse(is, ssBonusListener);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnknownPDFException e) {
				try {
					IdcParser.parse(is, ssBonusListener);
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (UnknownPDFException ex) {
					
				}
			}
		}
		
	}
	
	private static DSLContext getDSLContext (Connection connection) throws SQLException {
		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
    	return dslContext;
    }
    
    private static Connection getConnection() throws SQLException {
		String port = "3306";
    	String host = System.getenv("DB_HOST");
		String user = System.getenv("DB_USER");
		String password = System.getenv("DB_PASSWD");
		String database = System.getenv("DB_NAME");
    	
		Properties properties = new Properties();
		properties.setProperty("user", user);
		properties.setProperty("password", password);
		properties.setProperty("useSSL", "false");
		properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
		String url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
		
		return DriverManager.getConnection(url, properties);
    }

	public static void main(String[] args) {
		try {
			Connection connection = getConnection();
			DSLContext dslContext = getDSLContext(connection);
			
			FileInputStream is = new FileInputStream(new File("/Users/sergio/Desktop/idcplnss.pdf"));
			
			SSBonusSync ssBonusSync = new SSBonusSync(dslContext);
			ssBonusSync.syncSSBonus(is);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
