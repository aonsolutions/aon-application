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

public class AlterFsMod193Detail2024 implements Update {

	public static final AlterFsMod193Detail2024 ALTER_FS_MODEL_193_DETAIL_2024  = new AlterFsMod193Detail2024();

	private AlterFsMod193Detail2024() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean previousPayerDocument = false;
		boolean accrualDate = false;
		boolean marketKey = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model193_detail");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				if ("previous_payer_document".equals(name)) {
					previousPayerDocument = true;
				}
				if ("accrual_date".equals(name)) {
					accrualDate = true;
				}
				if ("market_key".equals(name)) {
					marketKey = true;
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
		
		if (!previousPayerDocument) {
			try { 
				System.out.println("\tAlterFsMod193Detail2024. \"previous_payer_document\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model193_detail` ADD COLUMN `previous_payer_document` varchar(9) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF del pagador anterior'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod193Detail2024. \"previous_payer_document\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod193Detail2024. \"previous_payer_document\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod193Detail2024. \"previous_payer_document\" EXISTS!");
		}
		
		if (!accrualDate) {
			try { 
				System.out.println("\tAlterFsMod193Detail2024. \"accrual_date\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model193_detail` ADD COLUMN `accrual_date` date DEFAULT NULL COMMENT 'Fecha de devengo'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod193Detail2024. \"accrual_date\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod193Detail2024. \"accrual_date\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod193Detail2024. \"accrual_date\" EXISTS!");
		}
		
		if (!marketKey) {
			try { 
				System.out.println("\tAlterFsMod193Detail2024. \"market_key\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model193_detail` ADD COLUMN `market_key` varchar(1) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Clave de mercado'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod193Detail2024. \"market_key\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod193Detail2024. \"market_key\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod193Detail2024. \"market_key\" EXISTS!");
		}
		
	}

}
