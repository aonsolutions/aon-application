package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;

import java.sql.Connection;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;

public class JooqContractAttach {
	
	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static void setContractAttachAttachment(String domainName, Integer attachId, String fileName, byte[] data, byte mimeType) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			dslContext.update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DESCRIPTION, fileName)
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.MIMETYPE, mimeType)
				.where(CONTRACT_ATTACH.ID.eq(attachId))
				.execute();
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}

	public static byte[] getContractAttachAttachment(String domainName, Integer attachId) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			Record contractAttachRecord = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.ID.eq(attachId))
				.fetchOne();
			
			byte[] data = contractAttachRecord.get(CONTRACT_ATTACH.DATA);
			
			return data;
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}	
	
}
