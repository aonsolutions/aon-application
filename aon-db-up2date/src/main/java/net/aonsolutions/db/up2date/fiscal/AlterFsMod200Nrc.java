package net.aonsolutions.db.up2date.fiscal;

import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;

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

public class AlterFsMod200Nrc implements Update {

	public static final AlterFsMod200Nrc ALTER_FS_MODEL200_NRC = new AlterFsMod200Nrc();

	private AlterFsMod200Nrc() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		// Nuevo campo Modelo 200 
		//	nrc, varchar(22), NRC

		boolean nrc = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model200");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("nrc".equals(name)) {
					nrc = true;
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
		
		if (!nrc) {
			try {
				System.out.println("\tAlterFsMod200Nrc: nrc must be created.");
				dslContext.alterTable(FS_MODEL200).addColumn("nrc", SQLDataType.VARCHAR.length(22)).execute();
				System.out.println("\tAlterFsMod200Nrc: nrc CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod200Nrc: nrc NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod200Nrc: nrc already exists.");
		}
	}

}
