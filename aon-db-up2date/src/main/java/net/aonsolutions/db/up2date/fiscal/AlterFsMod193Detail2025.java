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

public class AlterFsMod193Detail2025 implements Update {

	public static final AlterFsMod193Detail2025 ALTER_FS_MODEL_193_DETAIL_2025  = new AlterFsMod193Detail2025();

	private AlterFsMod193Detail2025() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean isin = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model193_detail");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				if ("isin".equals(name)) {
					isin = true;
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
		
		if (!isin) {
			try { 
				System.out.println("\tAlterFsMod193Detail2025. \"isin\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model193_detail` ADD COLUMN `isin` varchar(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo ISIN'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod193Detail2025. \"isin\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod193Detail2025. \"isin\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod193Detail2025. \"isin\" EXISTS!");
		}
		
	}

}
