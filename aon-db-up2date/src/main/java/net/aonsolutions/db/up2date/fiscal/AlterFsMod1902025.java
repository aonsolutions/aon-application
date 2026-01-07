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

public class AlterFsMod1902025 implements Update {

	public static final AlterFsMod1902025 ALTER_FS_MODEL_190_2025  = new AlterFsMod1902025();

	private AlterFsMod1902025() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		boolean preferredContributions = false;
		boolean preferredContributionsUnder36 = false;
		boolean preferredContributionsOver36 = false;
		boolean preferredGrossAnnualSalary = false;
		boolean otherContributionsUnder36 = false;
		boolean otherContributionsOver36 = false;
		boolean otherGrossAnnualSalary = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_model190");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				
				if ("preferred_contributions".equals(name)) {
					preferredContributions = true;
				}
				if ("preferred_contributions_under36".equals(name)) {
					preferredContributionsUnder36 = true;
				}
				if ("preferred_contributions_over36".equals(name)) {
					preferredContributionsOver36 = true;
				}
				if ("preferred_gross_annual_salary".equals(name)) {
					preferredGrossAnnualSalary = true;
				}
				if ("other_contributions_under36".equals(name)) {
					otherContributionsUnder36 = true;
				}
				if ("other_contributions_over36".equals(name)) {
					otherContributionsOver36 = true;
				}
				if ("other_gross_annual_salary".equals(name)) {
					otherGrossAnnualSalary = true;
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
		
		executeSQL(dslContext, preferredContributions, "preferred_contributions", "ALTER TABLE `fs_model190` ADD COLUMN `preferred_contributions` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Preferentes. Aportaciones'");
		executeSQL(dslContext, preferredContributionsUnder36, "preferred_contributions_under36", "ALTER TABLE `fs_model190` ADD COLUMN `preferred_contributions_under36` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Preferentes. Contribuciones empresariales a favor de personas menores de 36 años'");
		executeSQL(dslContext, preferredContributionsOver36, "preferred_contributions_over36", "ALTER TABLE `fs_model190` ADD COLUMN `preferred_contributions_over36` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Preferentes. Contribuciones empresariales a favor de personas de 36 años o mas'");
		executeSQL(dslContext, preferredGrossAnnualSalary, "preferred_gross_annual_salary", "ALTER TABLE `fs_model190` ADD COLUMN `preferred_gross_annual_salary` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Preferentes. Salario bruto anual de la entidad'");
		executeSQL(dslContext, otherContributionsUnder36, "other_contributions_under36", "ALTER TABLE `fs_model190` ADD COLUMN `other_contributions_under36` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Resto sistemas de empleo. Contribuciones empresariales a favor de personas menores de 36 años'");
		executeSQL(dslContext, otherContributionsOver36, "other_contributions_over36", "ALTER TABLE `fs_model190` ADD COLUMN `other_contributions_over36` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Resto sistemas de empleo. Contribuciones empresariales a favor de personas de 36 años o mas'");
		executeSQL(dslContext, otherGrossAnnualSalary, "other_gross_annual_salary", "ALTER TABLE `fs_model190` ADD COLUMN `other_gross_annual_salary` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Resto sistemas de empleo. Salario bruto anual de la entidad'");
		
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
	

}
