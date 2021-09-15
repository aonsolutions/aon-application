package net.aonsolutions.db.up2date.accounting;

import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class AlterAccountEntryDetailConcept implements Update {
	
	private static final Logger LOGGER  = Logger.getLogger(AlterAccountEntryDetailConcept.class.getName());

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
				if ("concept".equals(name) && rsmd.getPrecision(i) != 64) {
					updateConcept = true;
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		
		if (updateConcept) {
			try {
				Date now = new Date();
				LOGGER.info("\tAlterAccountEntryDetailConcept. concept must be updated.");
				dslContext.alterTable(ACCOUNT_ENTRY_DETAIL).alterColumn("concept")
					.set(SQLDataType.VARCHAR.length(64))
					.execute();
				long millis = (new Date()).getTime() - now.getTime(); 
				LOGGER.log(Level.INFO, "\tAlterAccountEntryDetailConcept. concept UPDATED! [ {0} sec.]", (millis / 1000));
			} catch (Exception e) {
				LOGGER.warning("\tAlterAccountEntryDetailConcept. concept NOT UPDATED!");
				e.printStackTrace();
			}
		} else {
			LOGGER.info("\tAlterAccountEntryDetailConcept. concept has right length.");
		}

	}

}
