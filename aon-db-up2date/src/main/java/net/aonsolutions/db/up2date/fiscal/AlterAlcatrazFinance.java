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

public class AlterAlcatrazFinance implements Update {
	
	public static final AlterAlcatrazFinance ALTER_ALCATRAZ_FINANCE = new AlterAlcatrazFinance();

	private AlterAlcatrazFinance() {
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
		
		boolean financeExists = false;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from alcatraz");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("finance".equals(name)) {
					financeExists = true;
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
		
		if (!financeExists) {
			try {
				System.out.println("\tAlterAlcatrazFinance. finance column must be created.");
				String sql1 = "ALTER TABLE `alcatraz` ADD COLUMN `finance` int(4) DEFAULT NULL Comment 'ID Vto'";
				dslContext.execute(sql1);
				System.out.println("\t\tdone!. [OK]");
			} catch (Throwable e) {
				System.out.println("\t[ERROR!!] AlterAlcatrazFinance. finance NOT CREATED!");
				e.printStackTrace();
			}

			try {
				System.out.println("\tAlterAlcatrazFinance. IDX_ALCATRAZ_FINANCE index must be created.");
				String sql2 = "ALTER TABLE `alcatraz` ADD KEY `IDX_ALCATRAZ_FINANCE` (`finance`)";
				dslContext.execute(sql2);
				System.out.println("\t\tdone!. [OK]");
			} catch (Throwable e) {
				System.out.println("\t[ERROR!!] AlterAlcatrazFinance. IDX_ALCATRAZ_FINANCE index NOT CREATED!");
				e.printStackTrace();
			}
				
			try {
				System.out.println("\tAlterAlcatrazFinance. FK_ALCATRAZ_FINANCE constraint must be created.");
				String sql3 = "ALTER TABLE `alcatraz` ADD CONSTRAINT `FK_ALCATRAZ_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`)";
				dslContext.execute(sql3);
				System.out.println("\t\tdone!. [OK]");
			} catch (Throwable e) {
				System.out.println("\t[ERROR!!] AlterAlcatrazFinance. FK_ALCATRAZ_FINANCE constraint NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterAlcatrazFinance. finance already exists. [OK]");
		}
	}
}
