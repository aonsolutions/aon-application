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

public class AlterFsMod369Detail2025 implements Update {

	public static final AlterFsMod369Detail2025 ALTER_FS_MODEL_369_DETAIL_2025  = new AlterFsMod369Detail2025();

	private AlterFsMod369Detail2025() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean originalCountryExists = false;
		boolean originalVatPercentExists = false;
		boolean originalBaseExists = false;
		boolean originalQuotaExists = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model369_detail");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				if ("original_country".equals(name)) {
					originalCountryExists = true;
				}
				if ("original_vat_percent".equals(name)) {
					originalVatPercentExists = true;
				}
				if ("original_base".equals(name)) {
					originalBaseExists = true;
				}
				if ("original_quota".equals(name)) {
					originalQuotaExists = true;
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
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
		}
		
		executeSQL(dslContext, originalCountryExists, "original_country", "ALTER TABLE `fs_model369_detail` ADD COLUMN `original_country` varchar(2) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Valor Original Codigo pais de consumo'");
		executeSQL(dslContext, originalVatPercentExists, "original_vat_percent", "ALTER TABLE `fs_model369_detail` ADD COLUMN `original_vat_percent` decimal(5,2) DEFAULT NULL COMMENT 'Valor Original Porcentaje de IVA'"); 
		executeSQL(dslContext, originalBaseExists, "original_base", "ALTER TABLE `fs_model369_detail` ADD COLUMN `original_base` decimal(17,2) DEFAULT NULL COMMENT 'Valor Original Base Imponible'");  
		executeSQL(dslContext, originalQuotaExists, "original_quota", "ALTER TABLE `fs_model369_detail` ADD COLUMN `original_quota` decimal(17,2) DEFAULT NULL COMMENT 'Valor Original Cuota IVA'"); 

	}
	
	private void executeSQL(DSLContext dslContext, boolean exists, String fieldName, String sql) {
		
		if (!exists) {
			try { 
				System.out.println("\t" + this.getClass().getSimpleName() + " \"" + fieldName + "\" NOT EXISTS!");
				dslContext.execute(sql);
				System.out.println("\t" + this.getClass().getSimpleName() + " \"" + fieldName + "\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\t" + this.getClass().getSimpleName() + " \"" + fieldName + "\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\t" + this.getClass().getSimpleName() + " \"" + fieldName + "\" EXISTS!");
		}
		
	}

}
