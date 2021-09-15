package net.aonsolutions.db.up2date.fiscal;

import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class AlterFsMod2002020 implements Update {
	
	private static final Logger LOGGER  = Logger.getLogger(AlterFsMod2002020.class.getName());

	public static final AlterFsMod2002020 ALTER_FS_MODEL_200_2020 = new AlterFsMod2002020();

	private AlterFsMod2002020() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		// Nuevo campo Modelo 200 para el 2020
		//	nrs_anexoV_ric, varchar(22), Documentación presentada por el Anexo V RIC Inversiones anticipadas

		boolean nrsExists = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model200");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("nrs_anexoV_ric".equals(name)) {
					nrsExists = true;
				}				
			}
		} catch (Exception t) {
			t.printStackTrace();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		if (!nrsExists) {
			try {
				LOGGER.info("\tAlterFsMod2002020. nrs_anexoV_ric must be created.");
				dslContext.alterTable(FS_MODEL200).addColumn("nrs_anexoV_ric", SQLDataType.VARCHAR.length(22)).execute();
				LOGGER.info("\tAlterFsMod2002020. nrs_anexoV_ric CREATED!");
			} catch (Exception e) {
				LOGGER.info("\tAlterFsMod2002020. nrs_anexoV_ric NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterFsMod2002020. nrs_anexoV_ric already exists.");
		}
	}

}
