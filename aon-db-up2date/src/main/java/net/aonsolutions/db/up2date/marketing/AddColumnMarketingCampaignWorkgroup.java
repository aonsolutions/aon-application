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

public class AddColumnMarketingCampaignWorkgroup implements Update {

	public static final AddColumnMarketingCampaignWorkgroup ADD_COLUMN_MARKETING_CAMPAIGN_WORKGROUP = new AddColumnMarketingCampaignWorkgroup();

	private AddColumnMarketingCampaignWorkgroup() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean addWorkgroupColumn = true;

		// Check if its need to be added
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from mk_campaign limit 1");
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String name = rsmd.getColumnName(i);				
				if ("workgroup".equals(name)) {
					addWorkgroupColumn = false;	
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
		if (addWorkgroupColumn) {
			try {
				Date now = new Date();
				System.out.println("\tAddColumnMarketingCampaignWorkgroup. workgroup column must be added.");
				
				String sql = "ALTER TABLE `mk_campaign` ADD COLUMN `workgroup` int(11) DEFAULT NULL Comment 'Grupo de trabajo de la campaña' AFTER `expense`, "
						+ "ADD INDEX `IDX_MK_CAMPAIGN_WORKGROUP` (`workgroup`), "
						+ "ADD CONSTRAINT `FK_MK_CAMPAIGN_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)";
				dslContext.execute(sql);
				
				long millis = (new Date()).getTime() - now.getTime(); 
				System.out.println("\tAddColumnMarketingCampaignWorkgroup. workgroup column ADDED! [" + (millis / 1000) + " sec.]");
			} catch (Throwable e) {
				System.out.println("\tAddColumnMarketingCampaignWorkgroup. workgroup column NOT ADDED!");
				e.printStackTrace();
			}
		} else {
			System.out.println("\tAddColumnMarketingCampaignWorkgroup. workgroup column already exists.");
		}

	}

}
