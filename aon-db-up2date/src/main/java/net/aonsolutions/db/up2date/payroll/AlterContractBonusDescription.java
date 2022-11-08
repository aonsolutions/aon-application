package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;

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
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class AlterContractBonusDescription implements Update {

	public static final AlterContractBonusDescription ALTER_CONTRACT_BONUS_DESCRIPTION = new AlterContractBonusDescription();

	private AlterContractBonusDescription() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		//	`description` varchar(256) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripcion',

		boolean updateConcept = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from contract_bonus limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("description".equals(name)) {
					if (rsmd.getPrecision(i) != 256) {
						updateConcept = true;
					};	
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
		if (updateConcept) {
			try {
				Date now = new Date();
				System.out.println("\tAlterContractBonusDescription. description must be updated.");
				dslContext.alterTable(CONTRACT_BONUS).alterColumn("description")
					.set(SQLDataType.VARCHAR.length(256))
					.execute();
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAlterContractBonusDescription. description UPDATED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAlterContractBonusDescription. description NOT UPDATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterContractBonusDescription. description has right length.");
		}

	}

}
