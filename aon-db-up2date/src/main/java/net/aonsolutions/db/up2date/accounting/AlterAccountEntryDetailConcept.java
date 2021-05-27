package net.aonsolutions.db.up2date.accounting;

import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;

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

public class AlterAccountEntryDetailConcept implements Update {

	public static final AlterAccountEntryDetailConcept ALTER_ACCOUNT_ENTRY_DETAIL_CONCEPT = new AlterAccountEntryDetailConcept();

	private AlterAccountEntryDetailConcept() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		//	concept` varchar(32) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Concepto del Apunte',

		boolean updateConcept = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from account_entry_detail limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("concept".equals(name)) {
					if (rsmd.getPrecision(i) != 64) {
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
				System.out.println("\tAlterAccountEntryDetailConcept. concept must be updated.");
				dslContext.alterTable(ACCOUNT_ENTRY_DETAIL).alterColumn("concept")
					.set(SQLDataType.VARCHAR.length(64))
					.execute();
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAlterAccountEntryDetailConcept. concept UPDATED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAlterAccountEntryDetailConcept. concept NOT UPDATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterAccountEntryDetailConcept. concept has right length.");
		}

	}

}
