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

public class AlterFsMod190Detail2026 implements Update {

	public static final AlterFsMod190Detail2026 ALTER_FS_MODEL_190_DETAIL_2026  = new AlterFsMod190Detail2026();

	private AlterFsMod190Detail2026() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean forecastPlanContributions = false;  // Contribuciones empresariales a planes de pensiones, planes de previsión social empresarial y mutualidades de previsión social...

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model190_detail limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);

				if ("forecast_plan_contributions".equals(name)) {
					forecastPlanContributions = true;
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
		
		if (!forecastPlanContributions) {
			try { 
				System.out.println("\tAlterFsMod190Detail2026. \"forecast_plan_contributions\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `forecast_plan_contributions` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Contribuciones empresariales a planes de pensiones, planes de prevision social empresarial y mutualidades de prevision social'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2026. \"forecast_plan_contributions\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2026. \"forecast_plan_contributions\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2026. \"forecast_plan_contributions\" EXISTS!");
		}
		
	}
}
