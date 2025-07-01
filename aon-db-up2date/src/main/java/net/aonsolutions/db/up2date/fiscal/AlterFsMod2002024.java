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

public class AlterFsMod2002024 implements Update {

	public static final AlterFsMod2002024 ALTER_FS_MODEL_200_2024  = new AlterFsMod2002024();

	private AlterFsMod2002024() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean ultimateGroupNameExists = false;
		boolean ultimateResidenceDocumentExists = false;
		boolean justBalearesExists = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model200");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				if ("ultimate_group_name".equals(name)) {
					ultimateGroupNameExists = true;
				}
				if ("ultimate_residence_document".equals(name)) {
					ultimateResidenceDocumentExists = true;
				}
				if ("just_baleares".equals(name)) {
					justBalearesExists = true;
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
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
		}
		
		if (!ultimateGroupNameExists) {
			modifySQL(dslContext, ultimateGroupNameExists, "type", "ALTER TABLE `fs_model200_registry` MODIFY `type` TINYINT NOT NULL DEFAULT '0' COMMENT 'Tipo de linea'");
			modifySQL(dslContext, ultimateGroupNameExists, "province", "ALTER TABLE `fs_model200_registry` MODIFY `province` TINYINT NOT NULL DEFAULT '0' COMMENT 'Codigo de Provincia'");
			modifySQL(dslContext, ultimateGroupNameExists, "period_type", "ALTER TABLE `fs_model200` MODIFY `period_type` TINYINT NOT NULL DEFAULT '0' COMMENT 'Tipo de periodo'");
		}
		
		executeSQL(dslContext, ultimateGroupNameExists, "ultimate_group_name", "ALTER TABLE `fs_model200` ADD COLUMN `ultimate_group_name` varchar(40) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de grupo mercantil'");
		executeSQL(dslContext, ultimateResidenceDocumentExists, "ultimate_residence_document", "ALTER TABLE `fs_model200` ADD COLUMN `ultimate_residence_document` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF en el pais de residencia'"); 
		executeSQL(dslContext, justBalearesExists, "just_baleares", "ALTER TABLE `fs_model200` ADD COLUMN `just_baleares` varchar(13) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de justificante Baleares'");  

	}
	
	private void executeSQL(DSLContext dslContext, boolean exists, String fieldName, String sql) {
		
		if (!exists) {
			try { 
				System.out.println("\t" + this.getClass().getSimpleName() + " \"" + fieldName + "\" NOT EXISTS!");
				dslContext.execute(sql);
				System.out.println("\t" + this.getClass().getSimpleName() + " \"" + fieldName + "\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\t" + this.getClass().getSimpleName() + " \"" + fieldName + "\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\t" + this.getClass().getSimpleName() + " \"" + fieldName + "\" EXISTS!");
		}
		
	}
	
	private void modifySQL(DSLContext dslContext, boolean exists, String fieldName, String sql) {

		try { 
			System.out.println("\t" + this.getClass().getSimpleName() + " \"" + fieldName + "\"");
			dslContext.execute(sql);
			System.out.println("\t" + this.getClass().getSimpleName() + " \"" + fieldName + "\" MODIFIED!");
		} catch (Throwable e) {
			System.out.println("\t" + this.getClass().getSimpleName() + " \"" + fieldName + "\" NOT MODIFIED!");
			e.printStackTrace();
		}
		
	}

}