package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
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
	
	public static DigitalCertificate getDigitalCertificateTGSS(Connection connection, Integer domainId, Integer userId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Record userRecord = dslContext.select().from(USER).where(USER.ID.eq(userId)).fetchOne();
		Integer registryUserId = userRecord.get(USER.REGISTRY);
		
		if(null != registryUserId) {
			Result<Record> employeeCertificateRecords = dslContext.select().from(RATTACH)
					.leftJoin(RATTACH_TAG).on(RATTACH.ID.eq(RATTACH_TAG.RATTACH))
					.where(RATTACH_TAG.ID.isNull())
					.and(RATTACH.REGISTRY.eq(registryUserId))
					.and(RATTACH.TYPE.eq((byte)4))
					.fetch();
			
			Result<Record> tgssDigitalCertificatePasswordRecords = dslContext.select().from(RADDINFO)
					.where(RADDINFO.REGISTRY.eq(registryUserId))
					.and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD"))
					.fetch();
			
			if(employeeCertificateRecords.isNotEmpty()) {
				
				Record employeeCertificateRecord = employeeCertificateRecords.get(0);
				java.util.Date updateDate = null == employeeCertificateRecord.get(RATTACH.MODIFICATION_DATE) ? null : new java.util.Date(employeeCertificateRecord.get(RATTACH.MODIFICATION_DATE).getTime());
				
				DigitalCertificate tgssDigitalCertiticate = new DigitalCertificate();
				tgssDigitalCertiticate.setRattachId(employeeCertificateRecord.get(RATTACH.ID));
				tgssDigitalCertiticate.setType(CertificateType.TGSS);
				tgssDigitalCertiticate.setDescription(employeeCertificateRecord.get(RATTACH.DESCRIPTION));
				tgssDigitalCertiticate.setConfidential(employeeCertificateRecord.get(RATTACH.SECURITY_LEVEL) == 0 ? false : true);
				tgssDigitalCertiticate.setHasCertificate(null == employeeCertificateRecord.get(RATTACH.DATA) ? false : true);
				tgssDigitalCertiticate.setUpdateDate(updateDate);
			
				if(null != tgssDigitalCertificatePasswordRecords && tgssDigitalCertificatePasswordRecords.isNotEmpty()) {
					Record tgssDigitalCertificatePasswordRecord = tgssDigitalCertificatePasswordRecords.get(0);
					tgssDigitalCertiticate.setPassword(tgssDigitalCertificatePasswordRecord.get(RADDINFO.VALUE));
					tgssDigitalCertiticate.setRaddinfoId(tgssDigitalCertificatePasswordRecord.get(RADDINFO.ID));
				}
				
				return tgssDigitalCertiticate;
			}
		}
		
		return null;
	}

	public static List<DigitalCertificate> getDigitalCertificatesSEPE(Connection connection, Integer domainId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		List<DigitalCertificate> digitalCertificates = new ArrayList<DigitalCertificate>();
		
		Integer registryEntepriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domainId))
				.fetchOne(ENTERPRISE.REGISTRY);
		
		Result<Record> enterpriseCertificateRecords = dslContext.select().from(RATTACH)
				.leftJoin(RATTACH_TAG).on(RATTACH.ID.eq(RATTACH_TAG.RATTACH))
				.where(RATTACH_TAG.ID.isNull())
				.and(RATTACH.REGISTRY.eq(registryEntepriseId))
				.and(RATTACH.TYPE.eq((byte)4))
				.fetch();
		
		if(enterpriseCertificateRecords.isNotEmpty()) {
			
			for(Record enterpriseCertificateRecord : enterpriseCertificateRecords) {
				List<Record> enterpriseCertificatePasswordRecords = dslContext.select().from(RADDINFO)
						.where(RADDINFO.REGISTRY.eq(registryEntepriseId))
						.and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD"))
						.fetch();
			
				Record enterpriseCertificatePasswordRecord = null;
				if(!enterpriseCertificatePasswordRecords.isEmpty())
					enterpriseCertificatePasswordRecord = enterpriseCertificatePasswordRecords.get(0);
			
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
		}
		
		return digitalCertificates;
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------
	// 													DELETE DIGITAL CERTIFICATES
	// --------------------------------------------------------------------------------------------------------------------------------------

	public static void deleteDigitalCertificate(Connection conn, Integer domainId, DigitalCertificate digitalCertificate) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		dslContext.delete(RATTACH)
			.where(RATTACH.ID.eq(digitalCertificate.getRattachId()))
			.execute();
	
		Integer registryId = dslContext.select(RADDINFO.REGISTRY).from(RADDINFO)
				.where(RADDINFO.ID.eq(digitalCertificate.getRaddinfoId()))
				.fetchOne(RADDINFO.REGISTRY);
		
		dslContext.delete(RADDINFO)
			.where(RADDINFO.ID.eq(digitalCertificate.getRaddinfoId()))
			.execute();
		
		if(null != registryId)
			dslContext.delete(RADDINFO)
			.where(RADDINFO.REGISTRY.eq(registryId))
			.and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD"))
			.execute();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------
	// 													SET DIGITAL CERTIFICATES (CERTIFICATE SERVLET)
	// --------------------------------------------------------------------------------------------------------------------------------------

	public static void setDigitalCertificateData(String domainName, String userLogin, byte mimeType, String fileName, CertificateType certificateType, byte[] data, String password, Integer rattachId, Integer raddinfoId) {
		
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			// TGSS CERTIFICATE
			if(certificateType == CertificateType.TGSS)
				updateCreateTGSSCertificate(dslContext, domainId, userId, mimeType, fileName, data, password, rattachId, raddinfoId);
			
			// SEPE CERTIFICATE
			if(certificateType == CertificateType.SEPE)
				updateCreateSEPECertificate(dslContext, domainId, mimeType, fileName, data, password, rattachId, raddinfoId);
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}

	private static void updateCreateTGSSCertificate(DSLContext dslContext, Integer domainId, Integer userId, byte mimeType, String fileName, byte[] data, String password, Integer rattachId, Integer raddinfoId) {
		Record userRecord = dslContext.select().from(USER).where(USER.ID.eq(userId)).fetchOne();
		Integer registryUserId = userRecord.get(USER.REGISTRY);
		
		// User Registry
		if(null == registryUserId)
			registryUserId = createRegistryForUser(dslContext, userRecord, domainId);
		
		dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
		
		// RAddInfo Password
		if(null == raddinfoId) {
			Record raddinfoRecord = dslContext.select().from(RADDINFO).where(RADDINFO.REGISTRY.eq(registryUserId)).and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD")).fetchOne();
			if(null == raddinfoRecord)
				dslContext.insertInto(RADDINFO)
						.set(RADDINFO.DOMAIN, domainId)
						.set(RADDINFO.REGISTRY, registryUserId)
						.set(RADDINFO.ATTRIBUTE, "DIGITAL_CERTIFICATE_PASSWORD")
						.set(RADDINFO.VALUE, password)
						.set(RADDINFO.VALUE_DATE, new Date(new java.util.Date().getTime()))
						.execute();
			else
				dslContext.update(RADDINFO)
					.set(RADDINFO.VALUE, password)
					.set(RADDINFO.VALUE_DATE, new Date(new java.util.Date().getTime()))
					.where(RADDINFO.ID.eq(raddinfoRecord.get(RADDINFO.ID)))
					.execute();
		} else
			dslContext.update(RADDINFO)
				.set(RADDINFO.VALUE, password)
				.set(RADDINFO.VALUE_DATE, new Date(new java.util.Date().getTime()))
				.where(RADDINFO.ID.eq(raddinfoId))
				.execute();
		
		// RAttach Data	
		if(null == rattachId)
			dslContext.insertInto(RATTACH)
				.set(RATTACH.DOMAIN, domainId)
				.set(RATTACH.REGISTRY, registryUserId)
				.set(RATTACH.MIMETYPE, (byte)36)
				.set(RATTACH.TYPE, (byte)4)
				.set(RATTACH.DATA, data)
				.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
				.set(RATTACH.DESCRIPTION, fileName)
				.execute();
		else
			if(null != data && data.length > 0)
				dslContext.update(RATTACH)
					.set(RATTACH.DATA, data)
					.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
					.set(RATTACH.DESCRIPTION, fileName)
					.where(RATTACH.ID.eq(rattachId))
					.execute();
			else
				dslContext.update(RATTACH)
					.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
					.set(RATTACH.DESCRIPTION, fileName)
					.where(RATTACH.ID.eq(rattachId))
					.execute();
		
		dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
	}

	private static void updateCreateSEPECertificate(DSLContext dslContext, Integer domainId, byte mimeType, String fileName, byte[] data, String password, Integer rattachId, Integer raddinfoId) {
		Integer registryEntepriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domainId))
				.fetchOne(ENTERPRISE.REGISTRY);
		
		dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
		
		// RAddInfo Password
		if(null == raddinfoId) {
			Record raddinfoRecord = dslContext.select().from(RADDINFO).where(RADDINFO.REGISTRY.eq(registryEntepriseId)).and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD")).fetchOne();
			if(null == raddinfoRecord)
				dslContext.insertInto(RADDINFO)
						.set(RADDINFO.DOMAIN, domainId)
						.set(RADDINFO.REGISTRY, registryEntepriseId)
						.set(RADDINFO.ATTRIBUTE, "DIGITAL_CERTIFICATE_PASSWORD")
						.set(RADDINFO.VALUE, password)
						.set(RADDINFO.VALUE_DATE, new Date(new java.util.Date().getTime()))
						.execute();
			else
				dslContext.update(RADDINFO)
					.set(RADDINFO.VALUE, password)
					.set(RADDINFO.VALUE_DATE, new Date(new java.util.Date().getTime()))
					.where(RADDINFO.ID.eq(raddinfoRecord.get(RADDINFO.ID)))
					.execute();
		} else
			dslContext.update(RADDINFO)
				.set(RADDINFO.VALUE, password)
				.where(RADDINFO.ID.eq(raddinfoId))
				.execute();
		
		// RAttach Data	
		if(null == rattachId)
			dslContext.insertInto(RATTACH)
				.set(RATTACH.DOMAIN, domainId)
				.set(RATTACH.REGISTRY, registryEntepriseId)
				.set(RATTACH.MIMETYPE, (byte)32)
				.set(RATTACH.TYPE, (byte)4)
				.set(RATTACH.DATA, data)
				.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
				.set(RATTACH.DESCRIPTION, fileName)
				.execute();
		else
			if(null != data)
				dslContext.update(RATTACH)
					.set(RATTACH.DATA, data)
					.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
					.set(RATTACH.DESCRIPTION, fileName)
					.where(RATTACH.ID.eq(rattachId))
					.execute();
			else
				dslContext.update(RATTACH)
					.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
					.set(RATTACH.DESCRIPTION, fileName)
					.where(RATTACH.ID.eq(rattachId))
					.execute();
		
		dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
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
	
}
