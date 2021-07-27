package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

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

	public static void setContractTA(Connection connection, Integer domainId, Integer contractId, byte[] data) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Integer contractAttachId = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)98))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.fetchOne(CONTRACT_ATTACH.ID);
		
		if(null != contractAttachId)
			dslContext.update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.where(CONTRACT_ATTACH.ID.eq(contractAttachId))
				.execute();
		else
			dslContext.insertInto(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DOMAIN, domainId)
				.set(CONTRACT_ATTACH.CONTRACT, contractId)
				.set(CONTRACT_ATTACH.MIMETYPE, (byte)22)
				.set(CONTRACT_ATTACH.DESCRIPTION, "TA")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)98)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
	}

	public static void setContractIDC(Connection connection, Integer domainId, Integer contractId, byte[] data) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Integer contractAttachId = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)99))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.fetchOne(CONTRACT_ATTACH.ID);
		
		if(null != contractAttachId)
			dslContext.update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.where(CONTRACT_ATTACH.ID.eq(contractAttachId))
				.execute();
		else
			dslContext.insertInto(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DOMAIN, domainId)
				.set(CONTRACT_ATTACH.CONTRACT, contractId)
				.set(CONTRACT_ATTACH.MIMETYPE, (byte)22)
				.set(CONTRACT_ATTACH.DESCRIPTION, "IDC")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)99)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
	}	
	
}
