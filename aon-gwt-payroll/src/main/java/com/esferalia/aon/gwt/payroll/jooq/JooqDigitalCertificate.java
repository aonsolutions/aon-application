package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate.CertificateType;
import com.esferalia.aon.jooq.tables.records.RaddinfoRecord;
import com.esferalia.aon.jooq.tables.records.RattachRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;

public class JooqDigitalCertificate {
	
	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------
	// 													GET DIGITAL CERTIFICATES
	// --------------------------------------------------------------------------------------------------------------------------------------
	
	public static List<DigitalCertificate> getDigitalCertificates(Connection conn, Integer domainId, Integer userId) {
		return getDigitalCertificatesDB(DSL.using(conn, getDefaultSettings()), domainId, userId);
	}

	private static List<DigitalCertificate> getDigitalCertificatesDB(DSLContext dslContext, Integer domainId, Integer userId) {
		System.out.println("getDigitalCertificatesDB() -> Domain ID : " + domainId + " User ID : " + userId);
		
		List<DigitalCertificate> digitalCertificates = new ArrayList<DigitalCertificate>();
		
		// TGSS CERTIFICATE
		checkOrCreateTGSS(dslContext, digitalCertificates, domainId, userId);
		
		// SEPE CERTIFICATE
		checkOrCreateSEPE(dslContext, digitalCertificates, domainId, userId);
		
		return digitalCertificates;
	}

	private static void checkOrCreateTGSS(DSLContext dslContext, List<DigitalCertificate> digitalCertificates, Integer domainId, Integer userId) {
		Record userRecord = dslContext.select().from(USER).where(USER.ID.eq(userId)).fetchOne();
		Integer registryUserId = userRecord.get(USER.REGISTRY);
		
		// If null create registry
		if(null == registryUserId)
			registryUserId = createRegistryForUser(dslContext, userRecord, domainId);
		
		Result<Record> employeeCertificateRecords = dslContext.select().from(RATTACH)
				.where(RATTACH.REGISTRY.eq(registryUserId))
				.and(RATTACH.TYPE.eq((byte)4))
				.fetch();
		
		Record tgssDigitalCertificatePasswordRecord = dslContext.select().from(RADDINFO)
				.where(RADDINFO.REGISTRY.eq(registryUserId))
				.and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD"))
				.fetchOne();
		
		if(employeeCertificateRecords.isNotEmpty()) {
			Record employeeCertificateRecord = employeeCertificateRecords.get(0);
			
			insertTgssDigitalCertiticate(dslContext, registryUserId, employeeCertificateRecord, tgssDigitalCertificatePasswordRecord, digitalCertificates);
				
		} else {
			 RattachRecord employeeCertificateRecord = dslContext.insertInto(RATTACH)
				.set(RATTACH.DOMAIN, domainId)
				.set(RATTACH.REGISTRY, registryUserId)
				.set(RATTACH.MIMETYPE, (byte)36)
				.set(RATTACH.DATA, DSL.castNull(RATTACH.DATA))
				.set(RATTACH.TYPE, (byte)4)
				.returning()
				.fetchOne();
			 
			   RaddinfoRecord employeeCertificatePasswordRecord = dslContext.insertInto(RADDINFO)
					.set(RADDINFO.DOMAIN, domainId)
					.set(RADDINFO.REGISTRY, registryUserId)
					.set(RADDINFO.ATTRIBUTE, "DIGITAL_CERTIFICATE_PASSWORD")
					.set(RADDINFO.VALUE, "")
					.set(RADDINFO.VALUE_DATE, new Date(new java.util.Date().getTime()))
					.returning()
					.fetchOne();
			 
			 insertTgssDigitalCertiticate(dslContext, registryUserId, employeeCertificateRecord, employeeCertificatePasswordRecord, digitalCertificates);
			
		}		
		
	}
	
	private static void insertTgssDigitalCertiticate(DSLContext dslContext, Integer registryUserId, Record employeeCertificateRecord, Record employeeCertificatePasswordRecord, List<DigitalCertificate> digitalCertificates) {
		java.util.Date updateDate = null == employeeCertificateRecord.get(RATTACH.MODIFICATION_DATE) ? null : new java.util.Date(employeeCertificateRecord.get(RATTACH.MODIFICATION_DATE).getTime());
		
		DigitalCertificate tgssDigitalCertiticate = new DigitalCertificate();
		tgssDigitalCertiticate.setRattachId(employeeCertificateRecord.get(RATTACH.ID));
		tgssDigitalCertiticate.setType(CertificateType.TGSS);
		tgssDigitalCertiticate.setDescription(employeeCertificateRecord.get(RATTACH.DESCRIPTION));
		tgssDigitalCertiticate.setConfidential(employeeCertificateRecord.get(RATTACH.SECURITY_LEVEL) == 0 ? false : true);
		tgssDigitalCertiticate.setHasCertificate(null == employeeCertificateRecord.get(RATTACH.DATA) ? false : true);
		tgssDigitalCertiticate.setUpdateDate(updateDate);
	
		
		
		if(null != employeeCertificatePasswordRecord) {
			tgssDigitalCertiticate.setPassword(employeeCertificatePasswordRecord.get(RADDINFO.VALUE));
			tgssDigitalCertiticate.setRaddinfoId(employeeCertificatePasswordRecord.get(RADDINFO.ID));
		}
		
	
		// Add certificate to list
		digitalCertificates.add(tgssDigitalCertiticate);
	}

	private static void checkOrCreateSEPE(DSLContext dslContext, List<DigitalCertificate> digitalCertificates, Integer domainId, Integer userId) {
		Integer registryEntepriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domainId))
				.fetchOne(ENTERPRISE.REGISTRY);
		
		Result<Record> enterpriseCertificateRecords = dslContext.select().from(RATTACH)
				.where(RATTACH.REGISTRY.eq(registryEntepriseId))
				.and(RATTACH.TYPE.eq((byte)4))
				.fetch();
		
		if(enterpriseCertificateRecords.isNotEmpty()) {
			
			Record enterpriseCertificatePasswordRecord = dslContext.select().from(RADDINFO)
					.where(RADDINFO.REGISTRY.eq(registryEntepriseId))
					.and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD"))
					.fetchOne();
			
			for(Record enterpriseCertificateRecord : enterpriseCertificateRecords) {
				insertSepeDigitalCertiticate(dslContext, registryEntepriseId, enterpriseCertificateRecord, enterpriseCertificatePasswordRecord, digitalCertificates);
			}
			
		} else {
			 RattachRecord enterpriseCertificateRecord = dslContext.insertInto(RATTACH)
				.set(RATTACH.DOMAIN, domainId)
				.set(RATTACH.REGISTRY, registryEntepriseId)
				.set(RATTACH.MIMETYPE, (byte)32)
				.set(RATTACH.DATA, DSL.castNull(RATTACH.DATA))
				.set(RATTACH.TYPE, (byte)4)
				.returning()
				.fetchOne();
			 
			 RaddinfoRecord enterpriseCertificatePasswordRecord = dslContext.insertInto(RADDINFO)
				.set(RADDINFO.DOMAIN, domainId)
				.set(RADDINFO.REGISTRY, registryEntepriseId)
				.set(RADDINFO.ATTRIBUTE, "DIGITAL_CERTIFICATE_PASSWORD")
				.set(RADDINFO.VALUE, "")
				.set(RADDINFO.VALUE_DATE, new Date(new java.util.Date().getTime()))
				.returning()
				.fetchOne();
			 
			 insertSepeDigitalCertiticate(dslContext, registryEntepriseId, enterpriseCertificateRecord, enterpriseCertificatePasswordRecord, digitalCertificates);
			 
		}
		
	}
	
	private static void insertSepeDigitalCertiticate(DSLContext dslContext, Integer registryEnterpriseId, Record enterpriseCertificateRecord, Record enterpriseCertificatePasswordRecord, List<DigitalCertificate> digitalCertificates) {
		java.util.Date updateDate = null == enterpriseCertificateRecord.get(RATTACH.MODIFICATION_DATE) ? null : new java.util.Date(enterpriseCertificateRecord.get(RATTACH.MODIFICATION_DATE).getTime());
		
		
		DigitalCertificate tgssDigitalCertiticate = new DigitalCertificate();
		tgssDigitalCertiticate.setRattachId(enterpriseCertificateRecord.get(RATTACH.ID));
		tgssDigitalCertiticate.setType(CertificateType.SEPE);
		tgssDigitalCertiticate.setDescription(enterpriseCertificateRecord.get(RATTACH.DESCRIPTION));
		tgssDigitalCertiticate.setConfidential(enterpriseCertificateRecord.get(RATTACH.SECURITY_LEVEL) == 0 ? false : true);
		tgssDigitalCertiticate.setHasCertificate(null == enterpriseCertificateRecord.get(RATTACH.DATA) ? false : true);
		tgssDigitalCertiticate.setUpdateDate(updateDate);
		
		if(null != enterpriseCertificatePasswordRecord) {
			tgssDigitalCertiticate.setPassword(enterpriseCertificatePasswordRecord.get(RADDINFO.VALUE));
			tgssDigitalCertiticate.setRaddinfoId(enterpriseCertificatePasswordRecord.get(RADDINFO.ID));
		}
	
		// Add certificate to list
		digitalCertificates.add(tgssDigitalCertiticate);
	}
	
	private static Integer createRegistryForUser(DSLContext dslContext, Record userRecord, Integer domainId) {
		String userName = userRecord.get(USER.NAME);
		Integer userId = userRecord.get(USER.ID);
		
		RegistryRecord registryUserRecord = dslContext.insertInto(REGISTRY)
				.set(REGISTRY.DOMAIN, domainId)
				.set(REGISTRY.NAME, userName)
				.returning(REGISTRY.ID)
				.fetchOne();
		
		Integer registryUserId = registryUserRecord.get(REGISTRY.ID);
		
		dslContext.update(USER)
			.set(USER.REGISTRY, registryUserId)
			.where(USER.ID.eq(userId))
			.execute();
		
		return registryUserId;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------
	// 													SET DIGITAL CERTIFICATES
	// --------------------------------------------------------------------------------------------------------------------------------------

	public static void setDigitalCertificates(Connection conn, Integer domainId, Integer userId, List<DigitalCertificate> digitalCertificateList) {
		setDigitalCertificatesDB(DSL.using(conn, getDefaultSettings()), domainId, userId, digitalCertificateList);
	}

	private static void setDigitalCertificatesDB(DSLContext dslContext, Integer domainId, Integer userId, List<DigitalCertificate> digitalCertificateList) {
		System.out.println("setDigitalCertificatesDB() -> Domain ID : " + domainId + " User ID : " + userId);
		
		for(DigitalCertificate digitalCertificate : digitalCertificateList) {
			// TGSS CERTIFICATE
			if(digitalCertificate.getType() == CertificateType.TGSS)
				updateTGSSCertificate(dslContext, domainId, userId, digitalCertificate);
			
			// SEPE CERTIFICATE
			
			if(digitalCertificate.getType() == CertificateType.SEPE)
				updateSEPECertificate(dslContext, domainId, userId, digitalCertificate);
				
		}
	}

	private static void updateTGSSCertificate(DSLContext dslContext, Integer domainId, Integer userId, DigitalCertificate digitalCertificate) {
		Timestamp modificationDate = new Timestamp(new java.util.Date().getTime());
		
		dslContext.update(RATTACH)
			.set(RATTACH.SECURITY_LEVEL, digitalCertificate.getConfidential() ? (byte)1 : (byte)0)
			.set(RATTACH.DESCRIPTION, digitalCertificate.getDescription())
			.set(RATTACH.MODIFICATION_DATE, modificationDate)
			.where(RATTACH.ID.eq(digitalCertificate.getRattachId()))
			.execute();
		
		dslContext.update(RADDINFO)
		.set(RADDINFO.VALUE, digitalCertificate.getPassword())
		.where(RADDINFO.ID.eq(digitalCertificate.getRaddinfoId()))
		.execute();
	}
	
	private static void updateSEPECertificate(DSLContext dslContext, Integer domainId, Integer userId, DigitalCertificate digitalCertificate) {
		Timestamp modificationDate = new Timestamp(new java.util.Date().getTime());
		
		dslContext.update(RATTACH)
			.set(RATTACH.SECURITY_LEVEL, digitalCertificate.getConfidential() ? (byte)1 : (byte)0)
			.set(RATTACH.DESCRIPTION, digitalCertificate.getDescription())
			.set(RATTACH.MODIFICATION_DATE, modificationDate)
			.where(RATTACH.ID.eq(digitalCertificate.getRattachId()))
			.execute();
		
		dslContext.update(RADDINFO)
			.set(RADDINFO.VALUE, digitalCertificate.getPassword())
			.where(RADDINFO.ID.eq(digitalCertificate.getRaddinfoId()))
			.execute();
		
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------
	// 													DELETE DIGITAL CERTIFICATES
	// --------------------------------------------------------------------------------------------------------------------------------------

	public static void deleteDigitalCertificate(Connection conn, Integer domainId, Integer userId, CertificateType type) {
		deleteDigitalCertificateDB(DSL.using(conn, getDefaultSettings()), domainId, userId, type);
	}

	private static void deleteDigitalCertificateDB(DSLContext dslContext, Integer domainId, Integer userId, CertificateType type) {
		// TGSS CERTIFICATE
		if(type == CertificateType.TGSS)
			deleteTGSSCertificate(dslContext, userId);
			
		// SEPE CERTIFICATE
		if(type == CertificateType.SEPE)
			deleteSEPECertificate(dslContext, domainId);
	
	}
	
	private static void deleteTGSSCertificate(DSLContext dslContext, Integer userId) {
		Record userRecord = dslContext.select().from(USER).where(USER.ID.eq(userId)).fetchOne();
		Integer registryUserId = userRecord.get(USER.REGISTRY);
		
		dslContext.delete(RATTACH)
			.where(RATTACH.ID.eq(
					dslContext.select(RATTACH.ID).from(RATTACH)
						.where(RATTACH.REGISTRY.eq(registryUserId))
						.and(RATTACH.TYPE.eq((byte)4))
						.fetchOne(RATTACH.ID)))
			.execute();
		
		dslContext.delete(RADDINFO)
		.where(RADDINFO.ID.eq(
				dslContext.select(RADDINFO.ID).from(RADDINFO)
					.where(RADDINFO.REGISTRY.eq(registryUserId))
					.and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD"))
					.fetchOne(RADDINFO.ID)))
		.execute();
		
	}
	
	private static void deleteSEPECertificate(DSLContext dslContext, Integer domainId) {
		Integer registryEntepriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domainId))
				.fetchOne(ENTERPRISE.REGISTRY);
		
		dslContext.delete(RATTACH)
			.where(RATTACH.ID.eq(
					dslContext.select(RATTACH.ID).from(RATTACH)
						.where(RATTACH.REGISTRY.eq(registryEntepriseId))
						.and(RATTACH.TYPE.eq((byte)4))
						.fetchOne(RATTACH.ID)))
			.execute();
		
		dslContext.delete(RADDINFO)
			.where(RADDINFO.ID.eq(
					dslContext.select(RADDINFO.ID).from(RADDINFO)
						.where(RADDINFO.REGISTRY.eq(registryEntepriseId))
						.and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD"))
						.fetchOne(RADDINFO.ID)))
			.execute();
			
	}

	// --------------------------------------------------------------------------------------------------------------------------------------
	// 													SET DIGITAL CERTIFICATES (DATA [])
	// --------------------------------------------------------------------------------------------------------------------------------------


	public static void setDigitalCertificateData(String domainName, String userLogin, byte mimeType, String fileName, CertificateType certificateType, byte[] data, Integer rattachId, Integer raddinfoId) {
		
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			
			System.out.println("setDigitalCertificateData() -> Domain ID : " + domainId + " User ID : " + userId);
			
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			// TGSS CERTIFICATE
			if(certificateType == CertificateType.TGSS)
				updateTGSSCertificateData(dslContext, domainId, userId, mimeType, fileName, data, rattachId);
			
			// SEPE CERTIFICATE
			if(certificateType == CertificateType.SEPE)
				updateSEPECertificateData(dslContext, domainId, mimeType, fileName, data,  rattachId);
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}

	private static void updateTGSSCertificateData(DSLContext dslContext, Integer domainId, Integer userId, byte mimeType, String fileName, byte[] data, Integer rattachId) {
		dslContext.update(RATTACH)
			.set(RATTACH.DATA, data)
			.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
			.set(RATTACH.DESCRIPTION, fileName)
			.where(RATTACH.ID.eq(rattachId))
			.execute();
	}

	private static void updateSEPECertificateData(DSLContext dslContext, Integer domainId, byte mimeType, String fileName, byte[] data, Integer rattachId) {
		Timestamp modificationDate = new Timestamp(new java.util.Date().getTime());
		
		dslContext.update(RATTACH)
			.set(RATTACH.DATA, data)
			.set(RATTACH.MODIFICATION_DATE, modificationDate)
			.set(RATTACH.DESCRIPTION, fileName)
			.where(RATTACH.ID.eq(rattachId))
			.execute();
	}
	
}
