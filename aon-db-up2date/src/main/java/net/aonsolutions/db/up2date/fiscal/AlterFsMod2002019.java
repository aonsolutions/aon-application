package net.aonsolutions.db.up2date.fiscal;

import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
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

public class AlterFsMod2002019 implements Update {

	public static final AlterFsMod2002019 ALTER_FS_MODEL_200_2019 = new AlterFsMod2002019();

	private AlterFsMod2002019() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		// Nuevos campos Modelo 200 para el 2019
		//	ultimate_document, varchar(15), Grupo - Clave 00081 - Datos de la sociedad matriz última: NIF o equivalente.
		//	ultimate_document_country, varchar(2), Grupo - Clave 00081 - Datos de la sociedad matriz última: Código país
		//	ultimate_name, varchar(40), Grupo - Clave 00081 - Datos de la sociedad matriz última: Nombre o razón social
		//	ultimate_country, varchar(2), Grupo - Clave 00081 - Datos de la sociedad matriz última: País o jurisdicción

		boolean ultimateDocumentExists = false;
		boolean ultimateDocumentCountryExists = false;
		boolean ultimateNameExists = false;
		boolean ultimateCountryExists = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model200");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("ultimate_document".equals(name)) {
					ultimateDocumentExists = true;
				}				
				if ("ultimate_document_country".equals(name)) {
					ultimateDocumentCountryExists = true;
				}
				if ("ultimate_name".equals(name)) {
					ultimateNameExists = true;
				}
				if ("ultimate_country".equals(name)) {
					ultimateCountryExists = true;
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
		
		//	ultimate_document, varchar(15), Grupo - Clave 00081 - Datos de la sociedad matriz última: NIF o equivalente.
		if (!ultimateDocumentExists) {
			try {
				System.out.println("\tAlterFsMod2002019. ultimate_document must be created.");
				dslContext.alterTable(FS_MODEL200).addColumn("ultimate_document", SQLDataType.VARCHAR.length(15)).execute();
				System.out.println("\tAlterFsMod2002019. ultimate_document CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod2002019. ultimate_document NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod2002019. ultimate_document already exists.");
		}
		
		//	ultimate_document_country, varchar(2), Grupo - Clave 00081 - Datos de la sociedad matriz última: Código país
		if (!ultimateDocumentCountryExists) {
			try {
				System.out.println("\tAlterFsMod2002019. ultimate_document_country must be created.");
				dslContext.alterTable(FS_MODEL200).addColumn("ultimate_document_country", SQLDataType.VARCHAR.length(2)).execute();
				System.out.println("\tAlterFsMod2002019. ultimate_document_country CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod2002019. ultimate_document_country NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod2002019. ultimate_document_country already exists.");
		}
		
		//	ultimate_name, varchar(40), Grupo - Clave 00081 - Datos de la sociedad matriz última: Nombre o razón social
		if (!ultimateNameExists) {
			try {
				System.out.println("\tAlterFsMod2002019. ultimate_name must be created.");
				dslContext.alterTable(FS_MODEL200).addColumn("ultimate_name", SQLDataType.VARCHAR.length(40)).execute();
				System.out.println("\tAlterFsMod2002019. ultimate_name CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod2002019. ultimate_name NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod2002019. ultimate_name already exists.");
		}

		//	ultimate_country, varchar(2), Grupo - Clave 00081 - Datos de la sociedad matriz última: País o jurisdicción
		if (!ultimateCountryExists) {
			try {
				System.out.println("\tAlterFsMod2002019. ultimate_country must be created.");
				dslContext.alterTable(FS_MODEL200).addColumn("ultimate_country", SQLDataType.VARCHAR.length(2)).execute();
				System.out.println("\tAlterFsMod2002019. ultimate_country CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod2002019. ultimate_country NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod2002019. ultimate_country already exists.");
		}

	}

}
