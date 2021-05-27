package net.aonsolutions.db.up2date.fiscal;

import static com.esferalia.aon.jooq.tables.FsModel200Registry.FS_MODEL200_REGISTRY;
import static org.jooq.impl.SQLDataType.VARCHAR;

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

public class AlterFsMod200Registry2018 implements Update {

	public static final AlterFsMod200Registry2018 ALTER_FS_MODEL_200_2018 = new AlterFsMod200Registry2018();

	private AlterFsMod200Registry2018() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		boolean mustChangeDocument = false;
		boolean ddValueExists = false;
		boolean eValueExists = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model200_registry");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				if ("document".equals(name)) {
					mustChangeDocument = (rsmd.getPrecision(i) != 20);
				}
				if ("dd_value".equals(name)) {
					ddValueExists = true;
				}
				if ("e_value".equals(name)) {
					eValueExists = true;
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
		if (mustChangeDocument) {
			try {
				System.out.println("\tAlterFsMod200Registry2018. Document length must be changed.");
				dslContext.alterTable(FS_MODEL200_REGISTRY).alterColumn(FS_MODEL200_REGISTRY.DOCUMENT)
						.set(VARCHAR.length(20)).execute();
				System.out.println("\tAlterFsMod200Registry2018. document ALTERED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod200Registry2018. Document lenght NOT CHANGED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod200Registry2018. Document length OK.");
		}

		if (!ddValueExists) {
			try {
				System.out.println("\tAlterFsMod200Registry2018. dd_column must be created.");
				dslContext.alterTable(FS_MODEL200_REGISTRY).addColumn("dd_value", SQLDataType.DOUBLE.nullable(true))
						.execute();
				System.out.println("\tAlterFsMod200Registry2018. dd_value CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod200Registry2018. dd_value NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod200Registry2018. dd_column already exists.");
		}

		if (!eValueExists) {
			try {
				System.out.println("\tAlterFsMod200Registry2018. e_value must be created.");
				dslContext.alterTable(FS_MODEL200_REGISTRY).addColumn("e_value", SQLDataType.DOUBLE.nullable(true))
						.execute();
				System.out.println("\tAlterFsMod200Registry2018. e_value CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod200Registry2018. e_value NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod200Registry2018. e_column already exists.");
		}
	}

}
