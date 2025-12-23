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

public class AlterFsMod190Detail2025 implements Update {

	public static final AlterFsMod190Detail2025 ALTER_FS_MODEL_190_DETAIL_2025  = new AlterFsMod190Detail2025();

	private AlterFsMod190Detail2025() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean entrepreneurship = false;           // Rendimientos del trabajo obtenidos por la gestión de fondos vinculados al emprendimiento
		boolean retirementPension = false; 			// Tipos de prestaciones de la Clave B.01 percibidas en el ejercicio: 01. Jubilación.
		boolean widowhoodPension = false;			// Tipos de prestaciones de la Clave B.01 percibidas en el ejercicio: 02. Viudedad.
		boolean permanentDisabilityPension = false; // Tipos de prestaciones de la Clave B.01 percibidas en el ejercicio: 03. Pensión por incapacidad permanente, total o parcial.
		boolean nonContributoryPension = false;	 	// Tipos de prestaciones de la Clave B.01 percibidas en el ejercicio: 04. Pensión NO contributiva por invalidez o jubilación.
		boolean otherNonExemptPensions = false; 	// Tipos de prestaciones de la Clave B.01 percibidas en el ejercicio: 05. Resto de prestaciones del artículo 17.2.a).1ª de la Ley IRPF, no exentas, distintas de las anteriores.

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model190_detail");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);

				if ("entrepreneurship".equals(name)) {
					entrepreneurship = true;
				}
				if ("retirement_pension".equals(name)) {
					retirementPension = true;
				}
				if ("widowhood_pension".equals(name)) {
					widowhoodPension = true;
				}
				if ("permanent_disability_pension".equals(name)) {
					permanentDisabilityPension = true;
				}
				if ("non_contributory_pension".equals(name)) {
					nonContributoryPension = true;
				}
				if ("other_non_exempt_pensions".equals(name)) {
					otherNonExemptPensions = true;
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
		
		if (!entrepreneurship) {
			try { 
				System.out.println("\tAlterFsMod190Detail2025. \"entrepreneurship\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `entrepreneurship` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Rendimientos del trabajo obtenidos por la gestion de fondos vinculados al emprendimiento'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2025. \"entrepreneurship\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2025. \"entrepreneurship\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2025. \"entrepreneurship\" EXISTS!");
		}
		if (!retirementPension) {
			try { 
				System.out.println("\tAlterFsMod190Detail2025. \"retirement_pension\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `retirement_pension` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipos de prestaciones Clave B01. Jubilacion'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2025. \"retirement_pension\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2025. \"retirement_pension\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2025. \"retirement_pension\" EXISTS!");
		}
		if (!widowhoodPension) {
			try { 
				System.out.println("\tAlterFsMod190Detail2025. \"widowhood_pension\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `widowhood_pension` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipos de prestaciones Clave B01. Viudedad'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2025. \"widowhood_pension\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2025. \"widowhood_pension\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2025. \"widowhood_pension\" EXISTS!");
		}
		if (!permanentDisabilityPension) {
			try { 
				System.out.println("\tAlterFsMod190Detail2025. \"permanent_disability_pension\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `permanent_disability_pension` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipos de prestaciones Clave B01. Pension por incapacidad permanente, total o parcial'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2025. \"permanent_disability_pension\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2025. \"permanent_disability_pension\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2025. \"permanent_disability_pension\" EXISTS!");
		}
		if (!nonContributoryPension) {
			try { 
				System.out.println("\tAlterFsMod190Detail2025. \"non_contributory_pension\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `non_contributory_pension` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipos de prestaciones Clave B01. Pension no contributiva por invalidez o jubilacion'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2025. \"non_contributory_pension\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2025. \"non_contributory_pension\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2025. \"non_contributory_pension\" EXISTS!");
		}
		if (!otherNonExemptPensions) {
			try { 
				System.out.println("\tAlterFsMod190Detail2025. \"other_non_exempt_pensions\" NOT EXISTS!");
				String sql1 = "ALTER TABLE `fs_model190_detail` ADD COLUMN `other_non_exempt_pensions` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Tipos de prestaciones Clave B01. Resto de prestaciones del art.17.2.a.1 de la Ley IRPF, no exentas, distintas de las anteriores'";
				dslContext.execute(sql1);
				System.out.println("\tAlterFsMod190Detail2025. \"other_non_exempt_pensions\" CREATED!");
			} catch (Throwable e) {
				System.out.println("\tAlterFsMod190Detail2025. \"other_non_exempt_pensions\" NOT CREATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterFsMod190Detail2025. \"other_non_exempt_pensions\" EXISTS!");
		}
		
	}
}
