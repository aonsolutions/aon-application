package net.aonsolutions.db.up2date.marketing;

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

import net.aonsolutions.db.up2date.Update;

public class AddColumnMarketingActionProduct implements Update {

	public static final AddColumnMarketingActionProduct ADD_COLUMN_MARKETING_ACTION_PRODUCT = new AddColumnMarketingActionProduct();

	private AddColumnMarketingActionProduct() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean addProductColumn = true;

		// Check if its need to be added
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from mk_action limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("product".equals(name)) {
					addProductColumn = false;	
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
		if (addProductColumn) {
			try {
				Date now = new Date();
				System.out.println("\tAddColumnMarketingActionProduct. Product column must be added.");
				
				String sql = "ALTER TABLE `mk_action`"
						+ "ADD COLUMN `product` INT(11) DEFAULT NULL COMMENT 'Producto base contratacion de la accion',"
						+ "ADD CONSTRAINT `FK_MK_ACTION_PRODUCT`"
						+ "FOREIGN KEY (`product`)"
						+ "REFERENCES `product` (`id`)"
						;
				
				dslContext.execute(sql);
				
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAddColumnMarketingActionProduct. Product column ADDED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAddColumnMarketingActionProduct. Product column NOT ADDED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAddColumnMarketingActionProduct. Product column already exists.");
		}

	}

}
