package net.aonsolutions.db.up2date.marketing;

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

public class AddColumnMarketingActionTargetAudit implements Update {

	public static final AddColumnMarketingActionTargetAudit ADD_COLUMN_MARKETING_ACTION_TARGET_AUDIT = new AddColumnMarketingActionTargetAudit();

	private AddColumnMarketingActionTargetAudit() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean addAuditColumns = true;

		// Check if its need to be added
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from mk_action_target limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("creation_user".equals(name)) {
					addAuditColumns = false;	
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
		if (addAuditColumns) {
			try {
				Date now = new Date();
				System.out.println("\tAddColumnMarketingActionTargetAudit. audit columns must be added.");
				
				String sql = "ALTER TABLE `mk_action_target` "
					+ "ADD COLUMN `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion'";
				String sql2 = "ALTER TABLE `mk_action_target` "
					+ "ADD COLUMN `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion'";
				String sql3 = "ALTER TABLE `mk_action_target` "
					+ "ADD COLUMN `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion'";
				String sql4 = "ALTER TABLE `mk_action_target` "
					+ "ADD COLUMN `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion'";

				dslContext.execute(sql);
				dslContext.execute(sql2);
				dslContext.execute(sql3);
				dslContext.execute(sql4);
				
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAddColumnMarketingActionTargetAudit. audit columns ADDED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAddColumnMarketingActionTargetAudit. audit columns NOT ADDED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAddColumnMarketingActionTargetAudit. audit columns already exists.");
		}

	}

}
