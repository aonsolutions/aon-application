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

public class AlterFsMod2002023 implements Update {

	public static final AlterFsMod2002023 ALTER_FS_MODEL200_2023 = new AlterFsMod2002023();

	private AlterFsMod2002023() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		// Nuevo campo Modelo 200 para el 2023
		//	nrs_anexoVI, varchar(22), Documentación presentada por el Anexo VI (RIIB: Inversiones anticipadas)

		boolean nrsExists = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model200");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("nrs_anexoVI".equals(name)) {
					nrsExists = true;
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
		
		if (!nrsExists) {
			try {
				System.out.println("\tAlterFsMod2002023: nrs_anexoVI must be created.");
				dslContext.alterTable(FS_MODEL200).addColumn("nrs_anexoVI", SQLDataType.VARCHAR.length(22)).execute();
				System.out.println("\tAlterFsMod2002023: nrs_anexoVI CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod2002023: nrs_anexoVI NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod2002023: nrs_anexoVI already exists.");
		}
	}

}
