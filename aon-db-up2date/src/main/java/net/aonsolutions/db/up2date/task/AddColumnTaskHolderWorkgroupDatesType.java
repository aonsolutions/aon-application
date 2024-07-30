package net.aonsolutions.db.up2date.task;

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

public class AddColumnTaskHolderWorkgroupDatesType implements Update {

	public static final AddColumnTaskHolderWorkgroupDatesType ADD_COLUMN_TASK_HOLDER_WORKGROUP_DATES_TYPE = new AddColumnTaskHolderWorkgroupDatesType();

	private AddColumnTaskHolderWorkgroupDatesType() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean addColumns = true;

		// Check if its need to be added
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from task_holder_workgroup limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("task_holder_workgroup_type".equals(name)) {
					addColumns = false;	
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
		
		if (addColumns) {
			try {
				Date now = new Date();
				System.out.println("\tAddColumnTaskHolderWorkgroupDatesType. columns must be added.");
				
				String sql = "ALTER TABLE `task_holder_workgroup` "
					+ "ADD COLUMN `task_holder_workgroup_type` tinyint(4) DEFAULT 0 COMMENT 'Tipo operario del grupo de trabajo'";
				
				String sql2 = "ALTER TABLE `task_holder_workgroup` "
						+ "ADD COLUMN `start_date` datetime DEFAULT NULL COMMENT 'Fecha inicio del operario en el grupo de trabajo'";
				
				String sql3 = "ALTER TABLE `task_holder_workgroup` "
						+ "ADD COLUMN `end_date` datetime DEFAULT NULL COMMENT 'Fecha fin del operario en el grupo de trabajo'";
				
				dslContext.execute(sql);
				dslContext.execute(sql2);
				dslContext.execute(sql3);
				
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAddColumnTaskHolderWorkgroupDatesType. columns ADDED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAddColumnTaskHolderWorkgroupDatesType. columns NOT ADDED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAddColumnTaskHolderWorkgroupDatesType. columns already exists.");
		}

	}

}
