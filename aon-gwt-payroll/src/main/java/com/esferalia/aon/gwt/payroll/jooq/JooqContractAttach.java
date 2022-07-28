package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record10;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractAttach;

public class JooqContractAttach {
	
	private JooqContractAttach() {
		super();
	}
	
	private static Settings settings = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	public static List<ContractAttach> getContractAttachments(Connection conn, Integer domainId, Integer contractId) {
		return getContractAttachmentsDB(DSL.using(conn, getDefaultSettings()), domainId, contractId);
	}
	
	private static List<ContractAttach> getContractAttachmentsDB(DSLContext dslContext, Integer domainId, Integer contractId) {
		List<ContractAttach> contractAttachs = new ArrayList<>();
		
		 Result<Record10<Integer, Integer, Integer, Byte, String, Byte, Integer, Byte, Timestamp, String>> contractAttachRecords = dslContext.select(
				CONTRACT_ATTACH.ID, CONTRACT_ATTACH.DOMAIN, CONTRACT_ATTACH.CONTRACT, CONTRACT_ATTACH.MIMETYPE,
				CONTRACT_ATTACH.DESCRIPTION, CONTRACT_ATTACH.TYPE, CONTRACT_ATTACH.SCOPE, CONTRACT_ATTACH.SECURITY_LEVEL,
				CONTRACT_ATTACH.ATTACH_DATE, CONTRACT_ATTACH.DRIVEID).from(CONTRACT_ATTACH)
			.where(CONTRACT_ATTACH.CONTRACT.eq(contractId)
					.or(CONTRACT_ATTACH.CONTRACT.isNull()))
			.and(CONTRACT_ATTACH.DOMAIN.eq(domainId))
			.fetch();
		
		for(Record contractAttachRecord : contractAttachRecords) {
			if(null != contractAttachRecord.get(CONTRACT_ATTACH.TYPE) && contractAttachRecord.get(CONTRACT_ATTACH.TYPE) == (byte)4)
				continue;
			
			ContractAttach contractAttach = new ContractAttach();
			contractAttach.setId(contractAttachRecord.get(CONTRACT_ATTACH.ID));
			contractAttach.setDomain(contractAttachRecord.get(CONTRACT_ATTACH.DOMAIN));
			contractAttach.setContract(contractAttachRecord.get(CONTRACT_ATTACH.CONTRACT));
			contractAttach.setMimeType(contractAttachRecord.get(CONTRACT_ATTACH.MIMETYPE));
			contractAttach.setDescription(contractAttachRecord.get(CONTRACT_ATTACH.DESCRIPTION));
			contractAttach.setData("".getBytes()); //contractAttachRecord.get(CONTRACT_ATTACH.DATA));
			contractAttach.setType(contractAttachRecord.get(CONTRACT_ATTACH.TYPE));
			contractAttach.setScope(contractAttachRecord.get(CONTRACT_ATTACH.SCOPE));
			contractAttach.setSecurityLevel(contractAttachRecord.get(CONTRACT_ATTACH.SECURITY_LEVEL));
			contractAttach.setAttachDate(contractAttachRecord.get(CONTRACT_ATTACH.ATTACH_DATE));
			contractAttach.setDriveId(contractAttachRecord.get(CONTRACT_ATTACH.DRIVEID));
			
			contractAttachs.add(contractAttach);
		}
		
		return contractAttachs;
	}

	public static byte[] getContractAttachAttachment(String domainName, Integer attachId) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			Record contractAttachRecord = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.ID.eq(attachId))
				.fetchOne();
			
			return contractAttachRecord.get(CONTRACT_ATTACH.DATA);
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}
	
	// ------------------------------------------ TA (Alta)
	
	public static String getContractTA(Connection connection, Integer contractId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record> idcRecords = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)98))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.orderBy(CONTRACT_ATTACH.ID.desc())
				.fetch();
		
		return idcRecords.isEmpty() ? null : Base64.getEncoder().encodeToString(idcRecords.get(0).get(CONTRACT_ATTACH.DATA));
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
				.set(CONTRACT_ATTACH.DESCRIPTION, "TA (Alta)")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)98)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
	}
	
	// ------------------------------------------ TA (Baja)
	
	public static String getContractTAEnd(Connection connection, Integer contractId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record> idcRecords = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)99))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.orderBy(CONTRACT_ATTACH.ID.desc())
				.fetch();
		
		return idcRecords.isEmpty() ? null : Base64.getEncoder().encodeToString(idcRecords.get(0).get(CONTRACT_ATTACH.DATA));
	}
	
	public static void setContractTAEnd(Connection connection, Integer domainId, Integer contractId, byte[] data) {
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
				.set(CONTRACT_ATTACH.DESCRIPTION, "TA (Baja)")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)99)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
	}
	
	// ------------------------------------------ CopyContract
	
	public static byte[] getCopyContract(Connection connection, Integer contractId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record> copyContractRecords = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)101))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.fetch();
		
		return copyContractRecords.isEmpty() ? null : copyContractRecords.get(0).get(CONTRACT_ATTACH.DATA);
	}
	
	public static void setCopyContract(Connection connection, Integer domainId, Integer contractId, byte[] data) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Integer contractAttachId = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)101))
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
				.set(CONTRACT_ATTACH.DESCRIPTION, "Copia Contrato (SEPE)")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)101)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
	}
	
	// ------------------------------------------ CopyBasic

	public static byte[] getCopyBasic(Connection connection, Integer contractId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record> copyBasicRecords = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)102))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.fetch();
		
		return copyBasicRecords.isEmpty() ? null : copyBasicRecords.get(0).get(CONTRACT_ATTACH.DATA);
	}
	
	public static void setCopyBasic(Connection connection, Integer domainId, Integer contractId, byte[] data) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Integer contractAttachId = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)102))
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
				.set(CONTRACT_ATTACH.DESCRIPTION, "Copia Basica (SEPE)")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)102)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
	}
	
	// ------------------------------------------ CopyContractTransform
	
	public static byte[] getCopyContractTransform(Connection connection, Integer contractId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record> copyContractRecords = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)108))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.fetch();
		
		return copyContractRecords.isEmpty() ? null : copyContractRecords.get(0).get(CONTRACT_ATTACH.DATA);
	}
	
	public static void setCopyContractTransform(Connection connection, Integer domainId, Integer contractId, byte[] data) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Integer contractAttachId = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)108))
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
				.set(CONTRACT_ATTACH.DESCRIPTION, "Copia Contrato Transformacion (SEPE)")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)108)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
	}
	
	// ------------------------------------------ Certific@2 PDF
	
	public static byte[] getCertifica2PDF(Connection connection, Integer contractId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record> copyBasicRecords = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)103))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.fetch();
		
		return copyBasicRecords.isEmpty() ? null : copyBasicRecords.get(0).get(CONTRACT_ATTACH.DATA);
	}
	
	public static void setCertifica2PDF(Connection connection, Integer domainId, Integer contractId, byte[] data) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Integer contractAttachId = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)103))
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
				.set(CONTRACT_ATTACH.DESCRIPTION, "Certific@2 (SEPE)")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)103)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
	}
	
}
