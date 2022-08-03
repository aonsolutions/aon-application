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

public class AlterFsMod200FsModel implements Update {
	
	public static final AlterFsMod200FsModel ALTER_FS_MOD200_FS_MODEL = new AlterFsMod200FsModel();

	private AlterFsMod200FsModel() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		boolean fsModelExists = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model200");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				if ("fs_model".equals(name)) {
					fsModelExists = true;
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
		System.out.println("AlterFsMod200FsModel");
		if (!fsModelExists) {
			try {
				String sql1 = "ALTER TABLE `fs_model200` ADD COLUMN `fs_model` int(4) DEFAULT NULL Comment 'Identificador de fs_model'";
				dslContext.execute(sql1);
				String sql2 = "ALTER TABLE `fs_model200` ADD KEY `IDX_FS_MODEL200_FS_MODEL` (`fs_model`)";
				dslContext.execute(sql2);
				String sql3 = "ALTER TABLE `fs_model200` ADD CONSTRAINT `FK_FS_MODEL200_FS_MODEL` FOREIGN KEY (`fs_model`) REFERENCES `fs_model` (`id`)";
				dslContext.execute(sql3);
				System.out.println("\tAlterFsMod200FsModel fs_model CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod200FsModel fs_model NOT CREATED! (error)");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod200FsModel fs_model NOT CREATED! (no need)");
		}

	}
}
