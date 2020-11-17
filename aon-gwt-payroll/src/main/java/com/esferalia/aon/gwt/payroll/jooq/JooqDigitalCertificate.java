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
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate;
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
	
	public static List<DigitalCertificate> getDigitalCertificates(Connection conn, Integer domainId, Integer userId) {
		return getDigitalCertificatesDB(DSL.using(conn, getDefaultSettings()), domainId, userId);
	}

	private static List<DigitalCertificate> getDigitalCertificatesDB(DSLContext dslContext, Integer domainId, Integer userId) {
		System.out.println("getDigitalCertificatesDB() -> Domain ID : " + domainId + " User ID : " + userId);
		List<DigitalCertificate> digitalCertificates = new ArrayList<DigitalCertificate>();
		
		// TGSS CERTIFICATE
		
		Integer registryUserId = dslContext.select(USER.REGISTRY).from(USER).where(USER.ID.eq(userId)).fetchOne(USER.REGISTRY);
		
		if(null != registryUserId) {
			Record employeeCertificateRecord = dslContext.select().from(RATTACH).where(RATTACH.REGISTRY.eq(registryUserId)).and(RATTACH.TYPE.eq((byte)4)).fetchOne();
			if(null != employeeCertificateRecord) {
				DigitalCertificate tgssDigitalCertiticate = new DigitalCertificate();
				tgssDigitalCertiticate.setType((byte)1);
				tgssDigitalCertiticate.setDescription(employeeCertificateRecord.get(RATTACH.DESCRIPTION));
				tgssDigitalCertiticate.setConfidential(employeeCertificateRecord.get(RATTACH.SECURITY_LEVEL) == 0 ? false : true);
				tgssDigitalCertiticate.setHasCertificate(null == employeeCertificateRecord.get(RATTACH.DATA) ? false : true);
				tgssDigitalCertiticate.setCreationDate(null == employeeCertificateRecord.get(RATTACH.CREATION_DATE) ? null : new java.util.Date(employeeCertificateRecord.get(RATTACH.CREATION_DATE).getTime()));
			
				String tgssDigitalCertificatePassword = dslContext.select(RADDINFO.VALUE).from(RADDINFO).where(RADDINFO.REGISTRY.eq(registryUserId)).and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD")).fetchOne(RADDINFO.VALUE);
				
				if(null != tgssDigitalCertificatePassword)
					tgssDigitalCertiticate.setPassword(tgssDigitalCertificatePassword);
			
				// Add certificate to list
				digitalCertificates.add(tgssDigitalCertiticate);
			}		
		}
		
		// SEPE CERTIFICATE
		
		Integer registryEntepriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY);
		
		Record employeeCertificateRecord = dslContext.select().from(RATTACH).where(RATTACH.REGISTRY.eq(registryEntepriseId)).and(RATTACH.TYPE.eq((byte)4)).fetchOne();
		if(null != employeeCertificateRecord) {
			DigitalCertificate tgssDigitalCertiticate = new DigitalCertificate();
			tgssDigitalCertiticate.setType((byte)0);
			tgssDigitalCertiticate.setDescription(employeeCertificateRecord.get(RATTACH.DESCRIPTION));
			tgssDigitalCertiticate.setConfidential(employeeCertificateRecord.get(RATTACH.SECURITY_LEVEL) == 0 ? false : true);
			tgssDigitalCertiticate.setHasCertificate(null == employeeCertificateRecord.get(RATTACH.DATA) ? false : true);
			tgssDigitalCertiticate.setCreationDate(null == employeeCertificateRecord.get(RATTACH.CREATION_DATE) ? null : new java.util.Date(employeeCertificateRecord.get(RATTACH.CREATION_DATE).getTime()));
		
			String tgssDigitalCertificatePassword = dslContext.select(RADDINFO.VALUE).from(RADDINFO).where(RADDINFO.REGISTRY.eq(registryEntepriseId)).and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD")).fetchOne(RADDINFO.VALUE);
			
			if(null != tgssDigitalCertificatePassword)
				tgssDigitalCertiticate.setPassword(tgssDigitalCertificatePassword);
		
			// Add certificate to list
			digitalCertificates.add(tgssDigitalCertiticate);		
		}
		
		return digitalCertificates;
	}

	public static void setDigitalCertificates(Connection conn, Integer domainId, Integer userId, List<DigitalCertificate> digitalCertificateList) {
		setDigitalCertificatesDB(DSL.using(conn, getDefaultSettings()), domainId, userId, digitalCertificateList);
	}

	private static void setDigitalCertificatesDB(DSLContext dslContext, Integer domainId, Integer userId, List<DigitalCertificate> digitalCertificateList) {
		System.out.println("setDigitalCertificatesDB() -> Domain ID : " + domainId + " User ID : " + userId);
		
		for(DigitalCertificate digitalCertificate : digitalCertificateList) {
			
			if(digitalCertificate.getType() == (byte)1) {
				// TGSS CERTIFICATE
				
				Record userRecord = dslContext.select().from(USER).where(USER.ID.eq(userId)).fetchOne();
				
				Integer registryUserId = userRecord.get(USER.REGISTRY);
				
				// If null create registry
				if(null == registryUserId) {
					String userName = userRecord.get(USER.NAME);
					
					RegistryRecord registryUserRecord = dslContext.insertInto(REGISTRY)
							.set(REGISTRY.DOMAIN, domainId)
							.set(REGISTRY.NAME, userName)
							.returning(REGISTRY.ID)
							.fetchOne();
					
					registryUserId = registryUserRecord.get(REGISTRY.ID);
					
					dslContext.update(USER)
					.set(USER.REGISTRY, registryUserId)
					.where(USER.ID.eq(userId))
					.execute();
				}
				
				Record employeeCertificateRecord = dslContext.select().from(RATTACH).where(RATTACH.REGISTRY.eq(registryUserId)).and(RATTACH.TYPE.eq((byte)4)).fetchOne();
				
				if(null != employeeCertificateRecord) {
					dslContext.update(RATTACH)
						.set(RATTACH.SECURITY_LEVEL, digitalCertificate.getConfidential() ? (byte)1 : (byte)0)
						.set(RATTACH.DESCRIPTION, digitalCertificate.getDescription())
						.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
						.where(RATTACH.ID.eq(employeeCertificateRecord.get(RATTACH.ID)))
						.execute();
				} else {
					dslContext.insertInto(RATTACH)
						.set(RATTACH.DOMAIN, domainId)
						.set(RATTACH.REGISTRY, registryUserId)
						.set(RATTACH.MIMETYPE, (byte)36)
						.set(RATTACH.TYPE, (byte)4)
						.set(RATTACH.SECURITY_LEVEL, digitalCertificate.getConfidential() ? (byte)1 : (byte)0)
						.set(RATTACH.DESCRIPTION, digitalCertificate.getDescription())
						.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
						.execute();
				}
				
				Record tgssDigitalCertificatePasswordRecord = dslContext.select().from(RADDINFO).where(RADDINFO.REGISTRY.eq(registryUserId)).and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD")).fetchOne();
				
				if(null != tgssDigitalCertificatePasswordRecord) {
					dslContext.update(RADDINFO)
						.set(RADDINFO.VALUE, digitalCertificate.getPassword())
						.where(RADDINFO.ID.eq(tgssDigitalCertificatePasswordRecord.get(RADDINFO.ID)))
						.execute();
				} else {
					if(null != digitalCertificate.getPassword())
						dslContext.insertInto(RADDINFO)
							.set(RADDINFO.DOMAIN, domainId)
							.set(RADDINFO.REGISTRY, registryUserId)
							.set(RADDINFO.ATTRIBUTE, "DIGITAL_CERTIFICATE_PASSWORD")
							.set(RADDINFO.VALUE, digitalCertificate.getPassword())
							.set(RADDINFO.VALUE_DATE,  new Date(new java.util.Date().getTime()))
							.execute();
				}
			
			}
			
			if(digitalCertificate.getType() == (byte)0) {
				
				// SEPE CERTIFICATE
				
				Integer registryEntepriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY);
				
				Record enterpriseCertificateRecord = dslContext.select().from(RATTACH).where(RATTACH.REGISTRY.eq(registryEntepriseId)).and(RATTACH.TYPE.eq((byte)4)).fetchOne();
				
				if(null != enterpriseCertificateRecord) {
					dslContext.update(RATTACH)
						.set(RATTACH.SECURITY_LEVEL, digitalCertificate.getConfidential() ? (byte)1 : (byte)0)
						.set(RATTACH.DESCRIPTION, digitalCertificate.getDescription())
						.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
						.where(RATTACH.ID.eq(enterpriseCertificateRecord.get(RATTACH.ID)))
						.execute();
				} else {
					dslContext.insertInto(RATTACH)
						.set(RATTACH.DOMAIN, domainId)
						.set(RATTACH.REGISTRY, registryEntepriseId)
						.set(RATTACH.MIMETYPE, (byte)36)
						.set(RATTACH.TYPE, (byte)4)
						.set(RATTACH.SECURITY_LEVEL, digitalCertificate.getConfidential() ? (byte)1 : (byte)0)
						.set(RATTACH.DESCRIPTION, digitalCertificate.getDescription())
						.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
						.execute();
				}
				
				Record sepeDigitalCertificatePasswordRecord = dslContext.select().from(RADDINFO).where(RADDINFO.REGISTRY.eq(registryEntepriseId)).and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD")).fetchOne();
				
				if(null != sepeDigitalCertificatePasswordRecord) {
					dslContext.update(RADDINFO)
						.set(RADDINFO.VALUE, digitalCertificate.getPassword())
						.where(RADDINFO.ID.eq(sepeDigitalCertificatePasswordRecord.get(RADDINFO.ID)))
						.execute();
				} else {
					if(null != digitalCertificate.getPassword())
						dslContext.insertInto(RADDINFO)
							.set(RADDINFO.DOMAIN, domainId)
							.set(RADDINFO.REGISTRY, registryEntepriseId)
							.set(RADDINFO.ATTRIBUTE, "DIGITAL_CERTIFICATE_PASSWORD")
							.set(RADDINFO.VALUE, digitalCertificate.getPassword())
							.set(RADDINFO.VALUE_DATE,  new Date(new java.util.Date().getTime()))
							.execute();
				}
			}
		}
	}
	
	public static void deleteDigitalCertificate(Connection conn, Integer domainId, Integer userId, Byte type) {
		deleteDigitalCertificateDB(DSL.using(conn, getDefaultSettings()), domainId, userId, type);
	}

	private static void deleteDigitalCertificateDB(DSLContext dslContext, Integer domainId, Integer userId, Byte type) {
		if(type == (byte)1) {
			// TGSS CERTIFICATE
			
			Record userRecord = dslContext.select().from(USER).where(USER.ID.eq(userId)).fetchOne();
			Integer registryUserId = userRecord.get(USER.REGISTRY);
			
			if(null != registryUserId) {
				Record employeeCertificateRecord = dslContext.select().from(RATTACH).where(RATTACH.REGISTRY.eq(registryUserId)).and(RATTACH.TYPE.eq((byte)4)).fetchOne();
				
				if(null != employeeCertificateRecord)
					dslContext.delete(RATTACH).where(RATTACH.ID.eq(employeeCertificateRecord.get(RATTACH.ID))).execute();
				
				Record tgssDigitalCertificatePasswordRecord = dslContext.select().from(RADDINFO).where(RADDINFO.REGISTRY.eq(registryUserId)).and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD")).fetchOne();
				
				if(null != tgssDigitalCertificatePasswordRecord)
					dslContext.delete(RADDINFO).where(RADDINFO.ID.eq(tgssDigitalCertificatePasswordRecord.get(RADDINFO.ID))).execute();
					
			}
		} else {
			// SEPE CERTIFICATE
			
			Integer registryEntepriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY);
	
			Record enterpriseCertificateRecord = dslContext.select().from(RATTACH).where(RATTACH.REGISTRY.eq(registryEntepriseId)).and(RATTACH.TYPE.eq((byte)4)).fetchOne();
			
			if(null != enterpriseCertificateRecord)
				dslContext.delete(RATTACH).where(RATTACH.ID.eq(enterpriseCertificateRecord.get(RATTACH.ID))).execute();
				
			Record sepeDigitalCertificatePasswordRecord = dslContext.select().from(RADDINFO).where(RADDINFO.REGISTRY.eq(registryEntepriseId)).and(RADDINFO.ATTRIBUTE.eq("DIGITAL_CERTIFICATE_PASSWORD")).fetchOne();
			
			if(null != sepeDigitalCertificatePasswordRecord)
				dslContext.delete(RADDINFO).where(RADDINFO.ID.eq(sepeDigitalCertificatePasswordRecord.get(RADDINFO.ID))).execute();
			
		}
	}	

	public static void setDigitalCertificateData(String domainName, String userLogin, byte mimeType, String fileName, Byte certificateType, byte[] data) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			
			System.out.println("setDigitalCertificateData() -> Domain ID : " + domainId + " User ID : " + userId);
			
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			// SEPE CERTIFICATE
			
			if(certificateType == (byte)0) {
				Integer registryEntepriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY);
				
				Record enterpriseCertificateRecord = dslContext.select().from(RATTACH).where(RATTACH.REGISTRY.eq(registryEntepriseId)).and(RATTACH.TYPE.eq((byte)4)).fetchOne();
				
				if(null != enterpriseCertificateRecord) {
					dslContext.update(RATTACH)
						.set(RATTACH.DATA, data)
						.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
						.set(RATTACH.DESCRIPTION, fileName)
						.where(RATTACH.ID.eq(enterpriseCertificateRecord.get(RATTACH.ID)))
						.execute();
				} else {
					dslContext.insertInto(RATTACH)
						.set(RATTACH.DOMAIN, domainId)
						.set(RATTACH.REGISTRY, registryEntepriseId)
						.set(RATTACH.MIMETYPE, (byte)36)
						.set(RATTACH.DESCRIPTION, fileName)
						.set(RATTACH.DATA, data)
						.set(RATTACH.TYPE, (byte)4)
						.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
						.execute();
				}
				
			}
			
			// TGSS CERTIFICATE
			
			if(certificateType == (byte)1) {
				Record userRecord = dslContext.select().from(USER).where(USER.ID.eq(userId)).fetchOne();
				
				Integer registryUserId = userRecord.get(USER.REGISTRY);
				
				// If null create registry
				if(null == registryUserId) {
					String userName = userRecord.get(USER.NAME);
					
					RegistryRecord registryUserRecord = dslContext.insertInto(REGISTRY)
							.set(REGISTRY.DOMAIN, domainId)
							.set(REGISTRY.NAME, userName)
							.returning(REGISTRY.ID)
							.fetchOne();
					
					registryUserId = registryUserRecord.get(REGISTRY.ID);
					
					dslContext.update(USER)
						.set(USER.REGISTRY, registryUserId)
						.where(USER.ID.eq(userId))
						.execute();
				}
				
				Record employeeCertificateRecord = dslContext.select().from(RATTACH).where(RATTACH.REGISTRY.eq(registryUserId)).and(RATTACH.TYPE.eq((byte)4)).fetchOne();
				
				if(null != employeeCertificateRecord) {
					dslContext.update(RATTACH)
						.set(RATTACH.DATA, data)
						.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
						.set(RATTACH.DESCRIPTION, fileName)
						.where(RATTACH.ID.eq(employeeCertificateRecord.get(RATTACH.ID)))
						.execute();
				} else {
					dslContext.insertInto(RATTACH)
						.set(RATTACH.DOMAIN, domainId)
						.set(RATTACH.REGISTRY, registryUserId)
						.set(RATTACH.MIMETYPE, (byte)36)
						.set(RATTACH.DESCRIPTION, fileName)
						.set(RATTACH.DATA, data)
						.set(RATTACH.TYPE, (byte)4)
						.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
						.execute();
				}
			}
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}
	
}
