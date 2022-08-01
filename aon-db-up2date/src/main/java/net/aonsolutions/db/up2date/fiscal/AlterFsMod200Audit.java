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

public class AlterFsMod200Audit implements Update {
	
	public static final AlterFsMod200Audit ALTER_FS_MOD200_AUDIT = new AlterFsMod200Audit();

	private AlterFsMod200Audit() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		boolean creationUserExists = false;
		boolean creationDateExists = false;
		boolean modificationUserExists = false;
		boolean modificationDateExists = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model200");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				if ("creation_user".equals(name)) {
					creationUserExists = true;
				}
				if ("creation_date".equals(name)) {
					creationDateExists = true;
				}
				if ("modification_user".equals(name)) {
					modificationUserExists = true;
				}
				if ("modification_date".equals(name)) {
					modificationDateExists = true;
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
		System.out.println("AlterFsMod200Audit");
		
		doAlter(dslContext,creationUserExists, "ALTER TABLE `fs_model200` ADD COLUMN `creation_user` VARCHAR(16) NULL DEFAULT NULL COMMENT 'Usuario de creacion' COLLATE 'latin1_spanish_ci'", "creation_user");
		doAlter(dslContext,creationDateExists, "ALTER TABLE `fs_model200` ADD COLUMN `creation_date` DATETIME NULL DEFAULT NULL COMMENT 'Fecha de creacion'", "creation_date");
		doAlter(dslContext,modificationUserExists, "ALTER TABLE `fs_model200` ADD COLUMN `modification_user` VARCHAR(16) NULL DEFAULT NULL COMMENT 'Usuario de modificacion' COLLATE 'latin1_spanish_ci'", "modification_user");
		doAlter(dslContext,modificationDateExists, "ALTER TABLE `fs_model200` ADD COLUMN `modification_date` DATETIME NULL DEFAULT NULL COMMENT 'Fecha de modificacion'", "modification_date");

	}
	
	private void doAlter(DSLContext dslContext, boolean fieldExists, String sql, String fieldName) {

		if (!fieldExists) {
			try {				
				dslContext.execute(sql);
				System.out.println("\tAlterFsMod200Audit " + fieldName + " CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod200Audit " + fieldName + " NOT CREATED! (error)");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod200Audit " + fieldName + " NOT CREATED! (no need)");
		}
		
	}
	
}
