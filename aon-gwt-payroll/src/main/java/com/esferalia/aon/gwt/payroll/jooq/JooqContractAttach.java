package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
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

	public static void createContractAttach(Connection conn, ContractAttach contractAttach) {
		createContractAttachDB(DSL.using(conn, getDefaultSettings()), contractAttach);
	}

	private static void createContractAttachDB(DSLContext dslContext, ContractAttach contractAttach) {
		 dslContext.insertInto(CONTRACT_ATTACH)
			.set(CONTRACT_ATTACH.DOMAIN, contractAttach.getDomain())
			.set(CONTRACT_ATTACH.CONTRACT, contractAttach.getContract())
			.execute();
	}
	
	public static void setContractAttachments(Connection conn, List<ContractAttach> contractAttachments) {
		setContractAttachmentsDB(DSL.using(conn, getDefaultSettings()), contractAttachments);
	}
	
	private static void setContractAttachmentsDB(DSLContext dslContext, List<ContractAttach> contractAttachments) {
		
		dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
		
		for(ContractAttach contractAttach : contractAttachments) {
			if(null == contractAttach.getContract())
				continue;
			
			dslContext.update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DESCRIPTION, contractAttach.getDescription())
				.set(CONTRACT_ATTACH.TYPE, contractAttach.getType() == (byte) -1 ? null : contractAttach.getType())
				.set(CONTRACT_ATTACH.SCOPE, contractAttach.getScope() == (byte) -1 ? null : contractAttach.getScope())
				.set(CONTRACT_ATTACH.SECURITY_LEVEL, contractAttach.getSecurityLevel())
				.set(CONTRACT_ATTACH.ATTACH_DATE, contractAttach.getAttachDate() == null ? null : new Timestamp(contractAttach.getAttachDate().getTime()))
				.where(CONTRACT_ATTACH.ID.eq(contractAttach.getId()))
				.execute();
			
		}
		
		dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
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
			
			return contractAttachRecord.get(CONTRACT_ATTACH.DATA);
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}

	// ------------------------------------------ IDC

	public static String __getContractIdc(Connection connection, Integer contractId, Date date) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record> idcRecords = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)101))
				.and(CONTRACT_ATTACH.ATTACH_DATE.eq(new Timestamp(date.getTime())))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.orderBy(CONTRACT_ATTACH.ID.desc())
				.fetch();
		
		return idcRecords.isEmpty() ? null : Base64.getEncoder().encodeToString(idcRecords.get(0).get(CONTRACT_ATTACH.DATA));
	}
	
	public static void __setContractIDC(Connection connection, Integer domainId, Integer contractId, byte[] data, Date date) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		List<Integer> contractAttachIds = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)101))
				.and(CONTRACT_ATTACH.ATTACH_DATE.eq(new Timestamp(date.getTime())))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.orderBy(CONTRACT_ATTACH.ID.desc())
				.fetch(CONTRACT_ATTACH.ID);
		
		if(!contractAttachIds.isEmpty())
			dslContext.update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DATA, data)
				.where(CONTRACT_ATTACH.ID.eq(contractAttachIds.get(0)))
				.execute();
		else
			dslContext.insertInto(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DOMAIN, domainId)
				.set(CONTRACT_ATTACH.CONTRACT, contractId)
				.set(CONTRACT_ATTACH.MIMETYPE, (byte)22)
				.set(CONTRACT_ATTACH.DESCRIPTION, "IDC")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)101)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(date.getTime()))
				.execute();
	}
	
	// ------------------------------------------ IDCPlNss

	public static String __getContractIdcPlNss(Connection connection, Integer contractId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record> idcRecords = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)102))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.orderBy(CONTRACT_ATTACH.ID.desc())
				.fetch();
		
		return idcRecords.isEmpty() ? null : Base64.getEncoder().encodeToString(idcRecords.get(0).get(CONTRACT_ATTACH.DATA));
	}
	
	public static void __setContractIdcPlNss(Connection connection, Integer domainId, Integer contractId, byte[] data) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		List<Integer> contractAttachIds = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)102))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.orderBy(CONTRACT_ATTACH.ID.desc())
				.fetch(CONTRACT_ATTACH.ID);
		
		if(!contractAttachIds.isEmpty())
			dslContext.update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.where(CONTRACT_ATTACH.ID.eq(contractAttachIds.get(0)))
				.execute();
		else
			dslContext.insertInto(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DOMAIN, domainId)
				.set(CONTRACT_ATTACH.CONTRACT, contractId)
				.set(CONTRACT_ATTACH.MIMETYPE, (byte)22)
				.set(CONTRACT_ATTACH.DESCRIPTION, "IDC PL NSS")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)102)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
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
				.set(CONTRACT_ATTACH.DESCRIPTION, "TA (Baja)")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)99)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
	}
	
	// ------------------------------------------ CopyBasic

	public static byte[] getCopyBasic(Connection connection, Integer contractId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record> copyBasicRecords = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)3))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.fetch();
		
		return copyBasicRecords.isEmpty() ? null : copyBasicRecords.get(0).get(CONTRACT_ATTACH.DATA);
	}
	
	public static void setCopyBasic(Connection connection, Integer domainId, Integer contractId, byte[] data) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Integer contractAttachId = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)3))
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
				.set(CONTRACT_ATTACH.DESCRIPTION, "Copia Basica")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)3)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
	}
	
	// ------------------------------------------ CopyContract
	
	public static byte[] getCopyContract(Connection connection, Integer contractId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record> copyContractRecords = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)1))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.fetch();
		
		return copyContractRecords.isEmpty() ? null : copyContractRecords.get(0).get(CONTRACT_ATTACH.DATA);
	}
	
	public static void setCopyContract(Connection connection, Integer domainId, Integer contractId, byte[] data) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Integer contractAttachId = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)1))
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
				.set(CONTRACT_ATTACH.DESCRIPTION, "Copia Contrato")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)1)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
		
		/**
		 * 		Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
				
				Attach attach = new Attach()
						.setAttachType(AttachType.CONTRACT)
						.setDomain(domain)
						.setAttachModule(contractId)
						.setDescription("Copia Contrato")
						.setData(pdfBytes)
						.setType((byte)1)
						.setConfidential(false)
						.setDate(new Date())
						.setScope(null)
						.setMimeType(MimeType.PDF);
				
				AON.insertAttach(domainName, domain.getId(), userLogin, attach);
		 */
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
				.set(CONTRACT_ATTACH.DESCRIPTION, "Certific@2")
				.set(CONTRACT_ATTACH.DATA, data)
				.set(CONTRACT_ATTACH.TYPE, (byte)103)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
	}
	
}
