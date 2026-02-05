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

public class AlterFsMod347Detail2025Bis implements Update {

	public static final AlterFsMod347Detail2025Bis ALTER_FS_MOD_347_DETAIL_2025_BIS = new AlterFsMod347Detail2025Bis();

	private AlterFsMod347Detail2025Bis() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		// Nuevos campos para el modelo 347 (Se usarán para el modelo 415 de Canarias)
		boolean rentalAmount = false;
		boolean firstQuarterRentalAmount = false;
		boolean secondQuarterRentalAmount = false;
		boolean thirdQuarterRentalAmount = false;
		boolean fourthQuarterRentalAmount = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from fs_mod347_detail");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);
				
				if ("rental_amount".equals(name)) {
					rentalAmount = true;
				}
				if ("first_quarter_rental_amount".equals(name)) {
					firstQuarterRentalAmount = true;
				}
				if ("second_quarter_rental_amount".equals(name)) {
					secondQuarterRentalAmount = true;
				}
				if ("third_quarter_rental_amount".equals(name)) {
					thirdQuarterRentalAmount = true;
				}
				if ("fourth_quarter_rental_amount".equals(name)) {
					fourthQuarterRentalAmount = true;
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
		
		executeSQL(dslContext, rentalAmount             , "rental_amount"               , "ALTER TABLE `fs_mod347_detail` ADD COLUMN `rental_amount`                decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe arrendamiento de locales de negocios'");
		executeSQL(dslContext, firstQuarterRentalAmount , "first_quarter_rental_amount" , "ALTER TABLE `fs_mod347_detail` ADD COLUMN `first_quarter_rental_amount`  decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe arrendamiento de locales de negocios primer trimestre'");
		executeSQL(dslContext, secondQuarterRentalAmount, "second_quarter_rental_amount", "ALTER TABLE `fs_mod347_detail` ADD COLUMN `second_quarter_rental_amount` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe arrendamiento de locales de negocios segundo trimestre'");
		executeSQL(dslContext, thirdQuarterRentalAmount , "third_quarter_rental_amount" , "ALTER TABLE `fs_mod347_detail` ADD COLUMN `third_quarter_rental_amount`  decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe arrendamiento de locales de negocios tercer trimestre'");
		executeSQL(dslContext, fourthQuarterRentalAmount, "fourth_quarter_rental_amount", "ALTER TABLE `fs_mod347_detail` ADD COLUMN `fourth_quarter_rental_amount` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe arrendamiento de locales de negocios cuarto trimestre'");
		
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
