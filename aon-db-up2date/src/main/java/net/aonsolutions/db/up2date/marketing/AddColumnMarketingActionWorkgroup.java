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

public class AddColumnMarketingActionWorkgroup implements Update {

	public static final AddColumnMarketingActionWorkgroup ADD_COLUMN_MARKETING_ACTION_WORKGROUP = new AddColumnMarketingActionWorkgroup();

	private AddColumnMarketingActionWorkgroup() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean addWorkgroupColumn = true;

		// Check if its need to be added
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from mk_action limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("workgroup".equals(name)) {
					addWorkgroupColumn = false;	
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
		if (addWorkgroupColumn) {
			try {
				Date now = new Date();
				System.out.println("\tAddColumnMarketingActionWorkgroup. workgroup column must be added.");
				
				String sql = "ALTER TABLE `mk_action` ADD COLUMN `workgroup` int(11) DEFAULT NULL Comment 'Grupo de trabajo de la accion' AFTER `expense`, "
						+ "ADD INDEX `IDX_MK_ACTION_WORKGROUP` (`workgroup`), "
						+ "ADD CONSTRAINT `FK_MK_ACTION_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)";
				dslContext.execute(sql);
				
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAddColumnMarketingActionWorkgroup. workgroup column ADDED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAddColumnMarketingActionWorkgroup. workgroup column NOT ADDED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAddColumnMarketingActionWorkgroup. workgroup column already exists.");
		}

	}

}
