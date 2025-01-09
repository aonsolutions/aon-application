package net.aonsolutions.db.up2date.fiscal;

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

public class AlterFsMod190Detail2024 implements Update {

	public static final AlterFsMod190Detail2024 ALTER_FS_MODEL_190_DETAIL_2024  = new AlterFsMod190Detail2024();

	private AlterFsMod190Detail2024() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean excesses = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model190_detail");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);

				if ("excesses".equals(name)) {
					excesses = true;
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
		if (!excesses) {
			try { 
				System.out.println("\tAlterFsMod190Detail2024. \"excesses\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `excesses` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Excesos entrega acciones empresas emergentes'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2024. \"excesses\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2024. \"excesses\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2024. \"excesses\" EXISTS!");
		}
		
	}
}
