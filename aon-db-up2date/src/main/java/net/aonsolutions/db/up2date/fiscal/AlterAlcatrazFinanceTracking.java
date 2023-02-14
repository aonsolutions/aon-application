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

public class AlterAlcatrazFinanceTracking implements Update {
	
	public static final AlterAlcatrazFinanceTracking ALTER_ALCATRAZ_FINANCE_TRACKING = new AlterAlcatrazFinanceTracking();

	private AlterAlcatrazFinanceTracking() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		boolean financeTrackingExists = false;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from alcatraz limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("finance_tracking".equals(name)) {
					financeTrackingExists = true;
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
		
		if (!financeTrackingExists) {
			try {
				System.out.println("\tAlterAlcatrazFinanceTracking. finance_tracking column must be created.");
				String sql1 = "ALTER TABLE `alcatraz` ADD COLUMN `finance_tracking` int(4) DEFAULT NULL Comment 'ID Seguimiento Vto'";
				dslContext.execute(sql1);
				System.out.println("\t\tdone!. [OK]");
			} catch (Throwable e) {
				System.out.println("\t[ERROR!!] AlterAlcatrazFinanceTracking. finance_tracking NOT CREATED!");
				e.printStackTrace();
			}

			try {
				System.out.println("\tAlterAlcatrazFinanceTracking. IDX_ALCATRAZ_FINANCE_TRACKING index must be created.");
				String sql2 = "ALTER TABLE `alcatraz` ADD KEY `IDX_ALCATRAZ_FINANCE_TRACKING` (`finance_tracking`)";
				dslContext.execute(sql2);
				System.out.println("\t\tdone!. [OK]");
			} catch (Throwable e) {
				System.out.println("\t[ERROR!!] AlterAlcatrazFinanceTracking. IDX_ALCATRAZ_FINANCE_TRACKING index NOT CREATED!");
				e.printStackTrace();
			}
				
			try {
				System.out.println("\tAlterAlcatrazFinanceTracking. FK_ALCATRAZ_FINANCE_TRACKING constraint must be created.");
				String sql3 = "ALTER TABLE `alcatraz` ADD CONSTRAINT `FK_ALCATRAZ_FINANCE_TRACKING` FOREIGN KEY (`finance_tracking`) REFERENCES `finance_tracking` (`id`)";
				dslContext.execute(sql3);
				System.out.println("\t\tdone!. [OK]");
			} catch (Throwable e) {
				System.out.println("\t[ERROR!!] AlterAlcatrazFinanceTracking. FK_ALCATRAZ_FINANCE_TRACKING constraint NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterAlcatrazFinanceTracking. finance_tracking already exists. [OK]");
		}
	}
}
