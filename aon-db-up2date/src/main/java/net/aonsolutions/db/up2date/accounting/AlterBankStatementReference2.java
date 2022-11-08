package net.aonsolutions.db.up2date.accounting;

import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;

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

public class AlterBankStatementReference2 implements Update {

	public static final AlterBankStatementReference2 ALTER_BANK_STATEMENT_REFERENCE_2 = new AlterBankStatementReference2();

	private AlterBankStatementReference2() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		//	`reference2` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia 2',

		boolean updateConcept = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from bank_statement limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("reference2".equals(name)) {
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
				System.out.println("\tAlterBankStatementReference2. reference2 must be updated.");
				dslContext.alterTable(BANK_STATEMENT).alterColumn("reference2")
					.set(SQLDataType.VARCHAR.length(64))
					.execute();
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAlterBankStatementReference2. reference2 UPDATED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAlterBankStatementReference2. reference2 NOT UPDATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterBankStatementReference2. reference2 has right length.");
		}

	}

}
