package net.aonsolutions.db.up2date.registry;

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

public class AddColumnSellerTaskHolder implements Update {

	public static final AddColumnSellerTaskHolder ADD_COLUMN_SELLER_TASK_HOLDER = new AddColumnSellerTaskHolder();

	private AddColumnSellerTaskHolder() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean addTaskHolderColumn = true;

		// Check if its need to be added
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from seller limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("task_holder".equals(name)) {
					addTaskHolderColumn = false;	
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
		if (addTaskHolderColumn) {
			try {
				Date now = new Date();
				System.out.println("\tAddColumnSellerTaskHolder. task_holder column must be added.");
				
				String sql = "ALTER TABLE `seller` ADD COLUMN `task_holder` int(11) DEFAULT NULL Comment 'Operario asociado al agente comercial' AFTER `scope`, "
						+ "ADD INDEX `IDX_SELLER_TASK_HOLDER` (`task_holder`), "
						+ "ADD CONSTRAINT `FK_SELLER_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)";
				dslContext.execute(sql);
				
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAddColumnSellerTaskHolder. task_holder column ADDED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAddColumnSellerTaskHolder. task_holder column NOT ADDED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAddColumnSellerTaskHolder. task_holder column already exists.");
		}

	}

}
