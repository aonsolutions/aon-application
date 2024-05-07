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

public class AddColumnMarketingActionExpense implements Update {

	public static final AddColumnMarketingActionExpense ADD_COLUMN_MARKETING_ACTION_EXPENSE = new AddColumnMarketingActionExpense();

	private AddColumnMarketingActionExpense() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean addExpenseColumn = true;

		// Check if its need to be added
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from mk_action limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("expense".equals(name)) {
					addExpenseColumn = false;	
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
		if (addExpenseColumn) {
			try {
				Date now = new Date();
				System.out.println("\tAddColumnMarketingActionExpense. expense column must be added.");
				
				String sql = "ALTER TABLE `mk_action` ADD COLUMN `expense` decimal(15,2) NOT NULL DEFAULT 0.00 Comment 'Inversion/gasto de la accion' AFTER `budget`";
				dslContext.execute(sql);
				
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAddColumnMarketingActionExpense. expense column ADDED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAddColumnMarketingActionExpense. expense column NOT ADDED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAddColumnMarketingActionExpense. expense column already exists.");
		}

	}

}
