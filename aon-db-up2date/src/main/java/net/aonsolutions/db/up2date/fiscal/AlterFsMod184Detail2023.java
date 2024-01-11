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

public class AlterFsMod184Detail2023 implements Update {

	public static final AlterFsMod184Detail2023 ALTER_FS_MODEL_184_DETAIL_2023  = new AlterFsMod184Detail2023();

	private AlterFsMod184Detail2023() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean rendNetoPrevio = false;
		boolean rendNetoMinorado = false;
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model184_detail");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);

				if ("rend_neto_previo".equals(name)) 	{rendNetoPrevio = true;}
				if ("rend_neto_minora".equals(name)) 	{rendNetoMinorado = true;}
				
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
		}
		
		if (!rendNetoPrevio) {
			try { 
				System.out.println("\tAlterFsMod184Detail2023. \"rend_neto_previo\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model184_detail` ADD COLUMN `rend_neto_previo` decimal(15,3) NOT NULL DEFAULT '0.000'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod184Detail2023. \"rend_neto_previo\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2023. \"rend_neto_previo\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2023. \"rend_neto_previo\" EXISTS!");
		}
		
		if (!rendNetoMinorado) {
			try { 
				System.out.println("\tAlterFsMod184Detail2023. \"rend_neto_minora\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model184_detail` ADD COLUMN `rend_neto_minora` decimal(15,3) NOT NULL DEFAULT '0.000'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod184Detail2023. \"rend_neto_minora\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2023. \"rend_neto_minora\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2023. \"rend_neto_minora\" EXISTS!");
		}
		
	}
}
