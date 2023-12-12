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

public class AlterFsMod190Detail2023 implements Update {

	public static final AlterFsMod190Detail2023 ALTER_FS_MODEL_190_DETAIL_2023  = new AlterFsMod190Detail2023();

	private AlterFsMod190Detail2023() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean commonRetention = false;
		boolean navarraRetention = false;
		boolean arabaRetention = false;
		boolean bizkaiaRetention = false;
		boolean gipuzkoaRetention = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model190_detail");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);

				if ("common_retention".equals(name)) 	{commonRetention = true;}
				if ("navarra_retention".equals(name)) 	{navarraRetention = true;}
				if ("araba_retention".equals(name)) 	{arabaRetention = true;}
				if ("bizkaia_retention".equals(name)) 	{bizkaiaRetention = true;}
				if ("gipuzkoa_retention".equals(name)) 	{gipuzkoaRetention = true;}
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
		if (!commonRetention) {
			try { 
				System.out.println("\tAlterFsMod190Detail2023. \"common_retention\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `common_retention` decimal(15,3) NOT NULL DEFAULT '0.000'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2023. \"common_retention\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2023. \"common_retention\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2023. \"common_retention\" EXISTS!");
		}
		
		if (!navarraRetention) {
			try { 
				System.out.println("\tAlterFsMod190Detail2023. \"navarra_retention\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `navarra_retention` decimal(15,3) NOT NULL DEFAULT '0.000'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2023. \"navarra_retention\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2023. \"navarra_retention\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2023. \"navarra_retention\" EXISTS!");
		}
		
		if (!arabaRetention) {
			try { 
				System.out.println("\tAlterFsMod190Detail2023. \"araba_retention\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `araba_retention` decimal(15,3) NOT NULL DEFAULT '0.000'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2023. \"araba_retention\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2023. \"araba_retention\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2023. \"araba_retention\" EXISTS!");
		}
		
		if (!bizkaiaRetention) {
			try { 
				System.out.println("\tAlterFsMod190Detail2023. \"bizkaia_retention\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `bizkaia_retention` decimal(15,3) NOT NULL DEFAULT '0.000'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2023. \"bizkaia_retention\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2023. \"bizkaia_retention\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2023. \"bizkaia_retention\" EXISTS!");
		}
		
		if (!gipuzkoaRetention) {
			try { 
				System.out.println("\tAlterFsMod190Detail2023. \"gipuzkoa_retention\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `gipuzkoa_retention` decimal(15,3) NOT NULL DEFAULT '0.000'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2023. \"gipuzkoa_retention\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2023. \"gipuzkoa_retention\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2023. \"gipuzkoa_retention\" EXISTS!");
		}
		
	}
}
