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

public class AddColumnCustomerFiscalStatus implements Update {

	public static final AddColumnCustomerFiscalStatus ADD_COLUMN_CUSTOMER_FISCAL_STATUS = new AddColumnCustomerFiscalStatus();

	private AddColumnCustomerFiscalStatus() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean addFiscalStatusColumn = true;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from customer limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				if ("fiscal_status".equals(name)) {
					addFiscalStatusColumn = false;
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

		System.out.println();
		if (addFiscalStatusColumn) {
			try {
				Date now = new Date();
				System.out.println("\tAddColumnCustomerFiscalStatus. fiscal_status column must be added.");

				// Values: 0=REGISTERED, 1=NOT_REGISTERED, 2=NOT_IDENTIFIED
				String sql = "ALTER TABLE `customer` ADD COLUMN `fiscal_status` tinyint DEFAULT 0 COMMENT 'Estado Fiscal: 0=REGISTERED, 1=NOT_REGISTERED, 2=NOT_IDENTIFIED' AFTER `status`";
				dslContext.execute(sql);

				long millis = (new Date()).getTime() - now.getTime();
				System.out.println("\tAddColumnCustomerFiscalStatus. fiscal_status column ADDED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAddColumnCustomerFiscalStatus. fiscal_status column NOT ADDED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAddColumnCustomerFiscalStatus. fiscal_status column already exists.");
		}

	}

}
