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

public class AlterFsMod190Detail2022 implements Update {

	public static final AlterFsMod190Detail2022 ALTER_FS_MODEL_190_DETAIL_2022  = new AlterFsMod190Detail2022();

	private AlterFsMod190Detail2022() {
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
		boolean compInfancia = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model190_detail");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);

				if ("tit_convivencia".equals(name)) 	{titConvivencia = true;}
				if ("comp_infancia".equals(name)) 	{compInfancia = true;}
				
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
		if (!titConvivencia) {
			try { 
				System.out.println("\tAlterFsMod190Detail2022. TITULAR UNIDAD DE CONVIVENCIA NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `tit_convivencia` tinyint(1) DEFAULT '0' Comment 'Tit. unidad convivencia'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2022. TITULAR UNIDAD DE CONVIVENCIA CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2022. TITULAR UNIDAD DE CONVIVENCIA NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2022. TITULAR UNIDAD DE CONVIVENCIA EXISTS!");
		}
		
		if (!compInfancia) {
			try { 
				System.out.println("\tAlterFsMod190Detail2022. COMPLEMENTO AYUDA PARA LA INFANCIA NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `comp_infancia` tinyint(1) DEFAULT '0' Comment 'Compl. ayuda infancia'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2022. COMPLEMENTO AYUDA PARA LA INFANCIA CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2022. COMPLEMENTO AYUDA PARA LA INFANCIA NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2022. COMPLEMENTO AYUDA PARA LA INFANCIA EXISTS!");
		}
	}

}
