package net.aonsolutions.db.up2date.management;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterRitem implements Update {

	public static final AlterRitem ALTER_RITEM = new AlterRitem();

	private AlterRitem() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		//	`quantity` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Cantidad',
		//	`start_date` date DEFAULT NULL COMMENT 'Fecha de inicio',
		//	`end_date` date DEFAULT NULL COMMENT 'Fecha de fin',
		//	`creation_date` datetime DEFAULT NULL COMMENT 'Momento de creación',
		//	`creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario creador',
		//	`modification_date` datetime DEFAULT NULL COMMENT 'Momento de modificación',
		//	`modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario modificador'
		

		boolean createdQuantity = false;
		boolean createdStartDate = false;
		boolean createdEndDate = false;
		boolean createdCreationDate = false;
		boolean createdCreationUser = false;
		boolean createdModificationDate = false;
		boolean createdModificationUser = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from ritem limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("quantity".equals(name)) {
					createdQuantity = true;
				} else if ("start_date".equals(name)) {
					createdStartDate = true;
				} else if ("end_date".equals(name)) {
					createdEndDate = true;
				} else if ("creation_date".equals(name)) {
					createdCreationDate = true;
				} else if ("creation_user".equals(name)) {
					createdCreationUser = true;
				} else if ("modification_date".equals(name)) {
					createdModificationDate = true;
				} else if ("modification_user".equals(name)) {
					createdModificationUser = true;
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
		
		final boolean mustBeUpdated =
				!createdQuantity &&
				!createdStartDate &&
				!createdEndDate &&
				!createdCreationDate &&
				!createdCreationUser &&
				!createdModificationDate &&
				!createdModificationUser;
		
		
		if (mustBeUpdated) {
			try {
				Date now = new Date();
				System.out.println("\tAlterRitem. 'quantity', 'start_date', 'end_date', 'creation_date', 'creation_user', 'modification_date' and 'modification_user' must be inserted.");
				StringBuilder querySB = new StringBuilder("ALTER TABLE `ritem`");
				querySB.append(" ADD `quantity` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Cantidad',");
				querySB.append(" ADD `start_date` date DEFAULT NULL COMMENT 'Fecha de inicio',");
				querySB.append(" ADD `end_date` date DEFAULT NULL COMMENT 'Fecha de fin',");
				querySB.append(" ADD `creation_date` datetime DEFAULT NULL COMMENT 'Momento de creación',");
				querySB.append(" ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario creador',");
				querySB.append(" ADD `modification_date` datetime DEFAULT NULL COMMENT 'Momento de modificación',");
				querySB.append(" ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario modificador'");
				
				dslContext
				.execute(querySB.toString());
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAlterRitem. 'quantity', 'start_date', 'end_date', 'creation_date', 'creation_user', 'modification_date' and 'modification_user' INSERTED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAlterRitem. 'quantity', 'start_date', 'end_date', 'creation_date', 'creation_user', 'modification_date' and 'modification_user' NOT INSERTED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterRitem. 'quantity', 'start_date', 'end_date', 'creation_date', 'creation_user', 'modification_date' and 'modification_user' already exist.");
		}

	}

}
