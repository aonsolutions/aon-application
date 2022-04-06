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

public class AlterFsModelResult implements Update {
	
	public static final AlterFsModelResult ALTER_FS_MODEL_RESULT = new AlterFsModelResult();

	private AlterFsModelResult() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		boolean accountEntryExists = false;
		boolean resultExists = false;
		boolean declarationTypeExists = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				if ("account_entry".equals(name)) {
					accountEntryExists = true;
				}
				if ("result".equals(name)) {
					resultExists = true;
				}
				if ("declaration_type".equals(name)) {
					declarationTypeExists = true;
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
		if (!accountEntryExists) {
			try {
				
				String sql1 = "ALTER TABLE `fs_model` ADD COLUMN `account_entry` int(4) DEFAULT NULL Comment 'identificador del apunte'";
				dslContext.execute(sql1);
				String sql2 = "ALTER TABLE `fs_model` ADD KEY `IDX_FS_MODEL_ACCOUNT_ENTRY` (`account_entry`)";
				dslContext.execute(sql2);
				String sql3 = "ALTER TABLE `fs_model` ADD CONSTRAINT `FK_FS_MODEL_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`)";
				dslContext.execute(sql3);
				System.out.println("\tAlterFsModelResult account_entry CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsModelResult account_entry NOT CREATED! (error)");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsModelResult account_entry NOT CREATED! (no need)");
		}

		if (!resultExists) {
			try {
				String sql1 = "ALTER TABLE `fs_model` ADD COLUMN `result` double(15,3) DEFAULT NULL COMMENT 'Resultado'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsModelResult result CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsModelResult result NOT CREATED! (error)");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsModelResult result NOT CREATED! (no need)");
		}

		if (!declarationTypeExists) {
			try {
				String sql1 = "ALTER TABLE `fs_model` ADD COLUMN `declaration_type` tinyint(2) DEFAULT NULL COMMENT 'Tipo de resultado'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsModelResult declarationType CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsModelResult declarationType NOT CREATED! (error)");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsModelResult declarationType NOT CREATED! (no need)");
		}
	}
}
