package net.aonsolutions.db.up2date.domain;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DomainAonCustomer implements Update {

	public static final DomainAonCustomer DOMAIN_AON_CUSTOMER = new DomainAonCustomer();
	
	private DomainAonCustomer() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		boolean columnExists = false;
		
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from domain limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				if ("aonCustomer".equals(name)) {
					columnExists = true;
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			;
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
			;
		}
		
		if (!columnExists) {			
			try {
				dslContext
				.execute("ALTER TABLE `domain` ADD `aonCustomer` int DEFAULT NULL COMMENT 'Referencia al customer en Aon'");
				System.out.println("\tDomainAonCustomer. aonCustomer ADDED!");
			} catch ( Exception e ) {
				System.out.println("\tDomainAonCustomer. aonCustomer NOT ADDED!");
				e.printStackTrace();
			}
		}
	}

}
