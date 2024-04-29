package net.aonsolutions.db.up2date.registry;

import static com.esferalia.aon.jooq.tables.Ritem.RITEM;

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


public class AlterRitemEdiSalesCode implements Update {

	public static final AlterRitemEdiSalesCode ALTER_RITEM_EDI_SALES_CODE = new AlterRitemEdiSalesCode();

	private AlterRitemEdiSalesCode() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		//	`edi_sales_code` varchar(512) COLLATE latin1_spanish_ci DEFAULT NULL,

		boolean updateEdiSalesCode = false;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from ritem limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("edi_sales_code".equals(name)) {
					if (rsmd.getPrecision(i) != 32) {
						updateEdiSalesCode = true;
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
		if (updateEdiSalesCode) {
			try {
				Date now = new Date();
				System.out.println("\tAlterRitemEdiSalesCode. edi_sales_code must be updated.");
				dslContext.alterTable(RITEM).alterColumn("edi_sales_code")
					.set(SQLDataType.VARCHAR.length(32))
					.execute();
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAlterRitemEdiSalesCode. edi_sales_code UPDATED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAlterRitemEdiSalesCode. edi_sales_code NOT UPDATED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAlterRitemEdiSalesCode. edi_sales_code has right length.");
		}

	}

}
