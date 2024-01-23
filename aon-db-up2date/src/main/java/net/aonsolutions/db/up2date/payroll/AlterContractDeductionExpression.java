package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;

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


public class AlterContractDeductionExpression implements Update {

	public static final AlterContractDeductionExpression ALTER_CONTRACT_DEDUCTION_EXPRESSION = new AlterContractDeductionExpression();

	private AlterContractDeductionExpression() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		//	`expression` varchar(512) COLLATE latin1_spanish_ci DEFAULT NULL,

		boolean updateConcept = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from contract_deduction limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("expression".equals(name)) {
					if (rsmd.getPrecision(i) != 512) {
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
				System.out.println("\tAlterContractDeductionExpression. expression must be updated.");
				dslContext.alterTable(CONTRACT_DEDUCTION).alterColumn("expression")
					.set(SQLDataType.VARCHAR.length(512))
					.execute();
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAlterContractDeductionExpression. expression UPDATED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAlterContractDeductionExpression. expression NOT UPDATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterContractDeductionExpression. expression has right length.");
		}

	}

}
