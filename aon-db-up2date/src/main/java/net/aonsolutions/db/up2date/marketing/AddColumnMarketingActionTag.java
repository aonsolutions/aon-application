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

public class AddColumnMarketingActionTag implements Update {

	public static final AddColumnMarketingActionTag ADD_COLUMN_MARKETING_ACTION_TAG = new AddColumnMarketingActionTag();

	private AddColumnMarketingActionTag() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean addTagColumn = true;

		// Check if its need to be added
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from mk_action limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("tag".equals(name)) {
					addTagColumn = false;	
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
		if (addTagColumn) {
			try {
				Date now = new Date();
				System.out.println("\tAddColumnMarketingActionTag. tag column must be added.");
				
				String sql = "ALTER TABLE `mk_action` ADD COLUMN `tag` int(11) DEFAULT NULL Comment 'Etiqueta para definir el tipo de accion' AFTER `media_type`, "
						+ "ADD INDEX `IDX_MK_ACTION_TAG` (`tag`), "
						+ "ADD CONSTRAINT `FK_MK_ACTION_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`)";
				dslContext.execute(sql);
				
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAddColumnMarketingActionTag. tag column ADDED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAddColumnMarketingActionTag. tag column NOT ADDED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAddColumnMarketingActionTag. tag column already exists.");
		}

	}

}
