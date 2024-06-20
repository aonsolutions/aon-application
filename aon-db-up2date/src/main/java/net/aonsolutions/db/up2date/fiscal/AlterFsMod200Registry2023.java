package net.aonsolutions.db.up2date.fiscal;

import static com.esferalia.aon.jooq.tables.FsModel200Registry.FS_MODEL200_REGISTRY;

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
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class AlterFsMod200Registry2023 implements Update {

	public static final AlterFsMod200Registry2023 ALTER_FS_MODEL200_REGISTRY_2023 = new AlterFsMod200Registry2023();

	private AlterFsMod200Registry2023() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean uValueExists = false;
		boolean vValueExists = false;
		boolean wValueExists = false;
		boolean xValueExists = false;
		boolean yValueExists = false;
		boolean zValueExists = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model200_registry");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				if ("u_value".equals(name)) {
					uValueExists = true;
				}
				if ("v_value".equals(name)) {
					vValueExists = true;
				}
				if ("w_value".equals(name)) {
					wValueExists = true;
				}
				if ("x_value".equals(name)) {
					xValueExists = true;
				}
				if ("y_value".equals(name)) {
					yValueExists = true;
				}
				if ("z_value".equals(name)) {
					zValueExists = true;
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
		
		doAlter(dslContext, uValueExists, "u_value");
		doAlter(dslContext, vValueExists, "v_value");
		doAlter(dslContext, wValueExists, "w_value");
		doAlter(dslContext, xValueExists, "x_value");
		doAlter(dslContext, yValueExists, "y_value");
		doAlter(dslContext, zValueExists, "z_value");

	}
	
	private void doAlter(DSLContext dslContext, boolean valueExists, String name) {
		
		if (!valueExists) {
			try {
				System.out.println("\tAlterFsMod200Registry2023: " + name + " must be created.");
				dslContext.alterTable(FS_MODEL200_REGISTRY).addColumn(name, SQLDataType.DECIMAL(15,3).nullable(true)).execute();
				System.out.println("\tAlterFsMod200Registry2023: " + name + " CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod200Registry2023: " + name + " NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod200Registry2023: " + name + " already exists.");
		}
		
	}

}
