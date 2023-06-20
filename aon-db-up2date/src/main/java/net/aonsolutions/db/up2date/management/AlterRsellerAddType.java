package net.aonsolutions.db.up2date.management;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterRsellerAddType implements Update {

	public static final AlterRsellerAddType ALTER_RSELLER_ADD_TYPE = new AlterRsellerAddType();

	private AlterRsellerAddType() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		//	`type` tinyint DEFAULT 0 NOT NULL COMMENT 'Tipo',
		

		boolean createdType = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from rseller limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("type".equals(name)) {
					createdType = true;
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
		
		System.out.println();
		
		final boolean mustBeUpdated = !createdType;
		
		
		if (mustBeUpdated) {
			try {
				Date now = new Date();
				System.out.println("\tAlterRsellerAddType. 'type' must be inserted.");
				StringBuilder querySB = new StringBuilder("ALTER TABLE `rseller`");
				querySB.append(" ADD `type` tinyint DEFAULT 0 NOT NULL COMMENT 'Tipo'");
				dslContext
				.execute(querySB.toString());
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAlterRsellerAddType. 'type' INSERTED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAlterRsellerAddType. 'type' NOT INSERTED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterRsellerAddType. 'type' already exist.");
		}

	}

}
