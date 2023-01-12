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

public class AlterFsMod184Detail2022 implements Update {

	public static final AlterFsMod184Detail2022 ALTER_FS_MODEL_184_DETAIL_2022  = new AlterFsMod184Detail2022();

	private AlterFsMod184Detail2022() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean titConvivencia = false;
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model184_detail");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);

				if ("asset_days".equals(name)) 	{titConvivencia = true;}
				
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
		
		
		if (!titConvivencia) {
			try { 
				System.out.println("\tAlterFsMod184Detail2022. NUMERO DIAS INMUEBLE NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model184_detail` ADD COLUMN `asset_days` int DEFAULT '0' COMMENT 'Numero dias inmueble'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod184Detail2022. NUMERO DIAS INMUEBLE CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod184Detail2022. NUMERO DIAS INMUEBLE NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod184Detail2022. NUMERO DIAS INMUEBLE EXISTS!");
		}
	}
}
