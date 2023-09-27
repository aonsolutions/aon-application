package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractDoc.CONTRACT_DOC;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Base64;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.storage.s3.S3;

public class JooqContractAttach {
	
	private JooqContractAttach() {
		super();
	}
	
	private static Settings settings = null;
	
	// ------------------------------------------ TA (Alta)
	
	public static String getContractTA(Connection connection, Integer contractId) {
	    return getContractDataURL(connection, contractId, (byte)98);
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
	    return getContractDataURL(connection, contractId, (byte)99);
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
	
	public static String getCopyContract(Connection connection, Integer contractId) {
	    return getContractDataURL(connection, contractId, (byte)101);
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
	
	public static void removeCopyContract(Connection connection, Integer contractId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		dslContext.delete(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)101))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.execute();
	}
	
	// ------------------------------------------ CopyBasic

	public static String getCopyBasic(Connection connection, Integer contractId) {
	    	return getContractDataURL(connection, contractId, (byte)102);
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
	
	public static void removeCopyBasic(Connection connection, Integer contractId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		dslContext.delete(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.TYPE.eq((byte)102))
				.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.execute();
	}
	
	// ------------------------------------------ CopyContractTransform
	
	public static String getCopyContractTransform(Connection connection, Integer contractId) {
	    	return getContractDataURL(connection, contractId, (byte)108);
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
	
	public static String getCertifica2PDF(Connection connection, Integer contractId) {
	    	return getContractDataURL(connection, contractId, (byte)103);
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
	
	// --------------------------------------------------------------------
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	

	// --------------------------------------------------------------------

	private static String getContractDataURL(Connection connection, Integer contractId, byte type) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Record contractDataRecord =
		dslContext.select()
		.from(CONTRACT_ATTACH)
		.leftJoin(CONTRACT_DOC)
		.on(CONTRACT_ATTACH.ID.eq(CONTRACT_DOC.ID))
		.where(CONTRACT_ATTACH.TYPE.eq(type))
		.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
		.orderBy(CONTRACT_ATTACH.ID.desc())
		.limit(1)
		.fetchOne();
		
		if ( contractDataRecord == null )
		    return null;
		
		String s3Key = contractDataRecord.get(CONTRACT_DOC.S3_KEY);		
		if ( s3Key != null ) 
		    return S3.getContractDocDownloadURL(s3Key).toString();
		
		byte [] data = contractDataRecord.get(CONTRACT_ATTACH.DATA);
		return  Base64.getEncoder().encodeToString(data);
		
		
		
	}
	
	
}
