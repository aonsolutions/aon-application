package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.CertificateInfo;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateOwner;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateSecurity;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateType;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.TagType;

public class JooqDigitalCertificateNew {
	
	private static Settings settings = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	private JooqDigitalCertificateNew() {}
	
	// ----------------------------------------------------------- GET DIGITAL CERTIFICATES
	
	public static List<DigitalCertificateNew> getDigitalCertificates(Connection connection, Integer domainId, Integer userId){
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		List<DigitalCertificateNew> certificateList = new ArrayList<>();
		
		getUserCertificates(dslContext, userId, certificateList);
		getEnterpriseCertificates(dslContext, domainId, certificateList);
		
//		certificateList.forEach(certitficate -> System.out.println(certitficate.toString()));
		
		return certificateList;
	}

	private static void getUserCertificates(DSLContext dslContext, Integer userId, List<DigitalCertificateNew> certificateList) {
		Integer registryUserId = dslContext.select(USER.REGISTRY).from(USER).where(USER.ID.eq(userId)).fetchOne(USER.REGISTRY);
		
		if(null == registryUserId)
			return;
		
		Result<Record> certificateRecords = dslContext.select().from(RATTACH)
				.where(RATTACH.REGISTRY.eq(registryUserId))
				.and(RATTACH.TYPE.eq((byte)4))
				.fetch();
		
		for(Record certificateRecord : certificateRecords) {
			
			Record certificatePasswordRecord = dslContext.select().from(RADDINFO)
					.where(RADDINFO.REGISTRY.eq(registryUserId))
					.and(RADDINFO.ATTRIBUTE.eq("PASSWORD_CERTIFICATE_" + certificateRecord.get(RATTACH.ID)))
					.fetchOne();
			
			List<Record> certificateTags = dslContext.select().from(RATTACH_TAG).where(RATTACH_TAG.RATTACH.eq(certificateRecord.get(RATTACH.ID))).fetch();
			
			if(certificateTags.isEmpty())
				continue;
			
			java.util.Date updateDate = null == certificateRecord.get(RATTACH.MODIFICATION_DATE) ? null : new java.util.Date(certificateRecord.get(RATTACH.MODIFICATION_DATE).getTime());
			
			DigitalCertificateNew digitalCertitficate = new DigitalCertificateNew();
			digitalCertitficate.setRattachId(certificateRecord.get(RATTACH.ID));
			digitalCertitficate.setOwner(CertificateOwner.USER);
			digitalCertitficate.setDescription(certificateRecord.get(RATTACH.DESCRIPTION));
			digitalCertitficate.setConfidential(certificateRecord.get(RATTACH.SECURITY_LEVEL) == 0 ? CertificateSecurity.PUBLIC : CertificateSecurity.PRIVATE);
			digitalCertitficate.setHasCertificate(null != certificateRecord.get(RATTACH.DATA));
			digitalCertitficate.setUpdateDate(updateDate);
			
			if(null != certificatePasswordRecord) {
				digitalCertitficate.setRaddinfoId(certificatePasswordRecord.get(RADDINFO.ID));
				digitalCertitficate.setPassword(certificatePasswordRecord.get(RADDINFO.VALUE));
			}
			
			if(!certificateTags.isEmpty()) {
				List<CertificateType> tags = new ArrayList<>();
				for(Record certificateTag : certificateTags) {
					Record tagRecord = dslContext.select().from(TAG).where(TAG.ID.eq(certificateTag.get(RATTACH_TAG.TAG))).fetchOne();
					tags.add(CertificateType.valueOf(tagRecord.get(TAG.NAME)));
				}
				digitalCertitficate.setTags(tags);
			}
			
			certificateList.add(digitalCertitficate);
		}
		
	}
	
	private static void getEnterpriseCertificates(DSLContext dslContext, Integer domainId, List<DigitalCertificateNew> certificateList) {
		
		Integer registryEnterpriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY);
		
		if(null == registryEnterpriseId)
			return;
		
		Result<Record> certificateRecords = dslContext.select().from(RATTACH)
				.where(RATTACH.REGISTRY.eq(registryEnterpriseId))
				.and(RATTACH.TYPE.eq((byte)4))
				.fetch();
		
		for(Record certificateRecord : certificateRecords) {
			
			Record certificatePasswordRecord = dslContext.select().from(RADDINFO)
					.where(RADDINFO.REGISTRY.eq(registryEnterpriseId))
					.and(RADDINFO.ATTRIBUTE.eq("PASSWORD_CERTIFICATE_" + certificateRecord.get(RATTACH.ID)))
					.fetchOne();
			
			List<Record> certificateTags = dslContext.select().from(RATTACH_TAG).where(RATTACH_TAG.RATTACH.eq(certificateRecord.get(RATTACH.ID))).fetch();
			
			if(certificateTags.isEmpty())
				continue;
			
			java.util.Date updateDate = null == certificateRecord.get(RATTACH.MODIFICATION_DATE) ? null : new java.util.Date(certificateRecord.get(RATTACH.MODIFICATION_DATE).getTime());
			
			DigitalCertificateNew digitalCertitficate = new DigitalCertificateNew();
			digitalCertitficate.setRattachId(certificateRecord.get(RATTACH.ID));
			digitalCertitficate.setOwner(CertificateOwner.ENTERPRISE);
			digitalCertitficate.setDescription(certificateRecord.get(RATTACH.DESCRIPTION));
			digitalCertitficate.setConfidential(certificateRecord.get(RATTACH.SECURITY_LEVEL) == 0 ? CertificateSecurity.PUBLIC : CertificateSecurity.PRIVATE);
			digitalCertitficate.setHasCertificate(null != certificateRecord.get(RATTACH.DATA));
			digitalCertitficate.setUpdateDate(updateDate);
			
			if(null != certificatePasswordRecord) {
				digitalCertitficate.setRaddinfoId(certificatePasswordRecord.get(RADDINFO.ID));
				digitalCertitficate.setPassword(certificatePasswordRecord.get(RADDINFO.VALUE));
			}
			
			if(!certificateTags.isEmpty()) {
				List<CertificateType> tags = new ArrayList<>();
				for(Record certificateTag : certificateTags) {
					Record tagRecord = dslContext.select().from(TAG).where(TAG.ID.eq(certificateTag.get(RATTACH_TAG.TAG))).fetchOne();
					tags.add(CertificateType.valueOf(tagRecord.get(TAG.NAME)));
				}
				digitalCertitficate.setTags(tags);
			}
			
			certificateList.add(digitalCertitficate);
		}
	}
	
	// ----------------------------------------------------------- DELETE DIGITAL CERTIFICATES
	
	public static void deleteDigitalCertificate(Connection conn, DigitalCertificateNew digitalCertificate) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		dslContext.delete(RATTACH_TAG)
			.where(RATTACH_TAG.RATTACH.eq(digitalCertificate.getRattachId()))
			.execute();
		
		dslContext.delete(RATTACH)
			.where(RATTACH.ID.eq(digitalCertificate.getRattachId()))
			.execute();
	
		dslContext.delete(RADDINFO)
			.where(RADDINFO.ID.eq(digitalCertificate.getRaddinfoId()))
			.execute();
	}

	// ----------------------------------------------------------- SET DIGITAL CERTIFICATES (CERTIFICATE SERVLET)
	
	public static void setDigitalCertificateData(String domainName, String userLogin, String fileName, List<CertificateType> tagTypes, CertificateOwner owner, byte[] data, String password, byte security, Integer rattachId, Integer raddinfoId) {
		
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			// TGSS CERTIFICATE
			if(owner == CertificateOwner.USER)
				setUserCertificate(dslContext, domainId, userId, tagTypes, fileName, data, password, security, rattachId, raddinfoId);
			
			// SEPE CERTIFICATE
			if(owner == CertificateOwner.ENTERPRISE)
				setEnterpriseCertificate(dslContext, domainId, tagTypes, fileName, data, password, security, rattachId, raddinfoId);
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}

	private static void setUserCertificate(DSLContext dslContext, Integer domainId, Integer userId, List<CertificateType> tagTypes, String fileName, byte[] data, String password, byte security, Integer rattachId, Integer raddinfoId) {
		Record userRecord = dslContext.select().from(USER).where(USER.ID.eq(userId)).fetchOne();
		Integer registryUserId = userRecord.get(USER.REGISTRY);
		
		// User Registry
		if(null == registryUserId)
			registryUserId = createRegistryForUser(dslContext, userRecord, domainId);
		
		// Rattach Data	
		if(null == rattachId)
			rattachId = dslContext.insertInto(RATTACH)
				.set(RATTACH.DOMAIN, domainId)
				.set(RATTACH.REGISTRY, registryUserId)
				.set(RATTACH.MIMETYPE, (byte)36)
				.set(RATTACH.TYPE, (byte)4)
				.set(RATTACH.DATA, data)
				.set(RATTACH.SECURITY_LEVEL, security)
				.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
				.set(RATTACH.DESCRIPTION, fileName + "HIDE(" + password + ")")
				.returning(RATTACH.ID)
				.fetchOne()
				.getId();
		else
			if(null != data && data.length > 0)
				dslContext.update(RATTACH)
					.set(RATTACH.DATA, data)
					.set(RATTACH.SECURITY_LEVEL, security)
					.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
					.set(RATTACH.DESCRIPTION, fileName + "HIDE(" + password + ")")
					.where(RATTACH.ID.eq(rattachId))
					.execute();
			else
				dslContext.update(RATTACH)
					.set(RATTACH.SECURITY_LEVEL, security)
					.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
					.set(RATTACH.DESCRIPTION, fileName + "HIDE(" + password + ")")
					.where(RATTACH.ID.eq(rattachId))
					.execute();
		
		// RaddInfo Password
		if(null == raddinfoId)
			dslContext.insertInto(RADDINFO)
					.set(RADDINFO.DOMAIN, domainId)
					.set(RADDINFO.REGISTRY, registryUserId)
					.set(RADDINFO.ATTRIBUTE, "PASSWORD_CERTIFICATE_" + rattachId)
					.set(RADDINFO.VALUE, password)
					.set(RADDINFO.VALUE_DATE, new Date(new java.util.Date().getTime()))
					.execute();
		else
			dslContext.update(RADDINFO)
				.set(RADDINFO.VALUE, password)
				.where(RADDINFO.ID.eq(raddinfoId))
				.execute();
		
		// Rattach Tags
		
		dslContext.delete(RATTACH_TAG).where(RATTACH_TAG.RATTACH.eq(rattachId)).execute();
		
		for(CertificateType tagType : tagTypes) {
			Record tagRecord = dslContext.select().from(TAG).where(TAG.NAME.eq(tagType.name())).fetchOne();
			Integer tagId = null;
			if(null == tagRecord) {
				tagId = dslContext.insertInto(TAG)
							.set(TAG.DOMAIN, 0)
							.set(TAG.NAME, tagType.name())
							.set(TAG.TYPE, TagType.CERTIFICATE.value())
							.returning(TAG.ID)
							.fetchOne()
							.getId();
			} else
				tagId = tagRecord.get(TAG.ID);
				
			
			dslContext.insertInto(RATTACH_TAG)
				.set(RATTACH_TAG.DOMAIN, domainId)
				.set(RATTACH_TAG.RATTACH, rattachId)
				.set(RATTACH_TAG.TAG, tagId)
				.execute();
		}
		
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

	private static void setEnterpriseCertificate(DSLContext dslContext, Integer domainId, List<CertificateType> tagTypes, String fileName, byte[] data, String password, byte security, Integer rattachId, Integer raddinfoId) {
		Integer registryEntepriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domainId))
				.fetchOne(ENTERPRISE.REGISTRY);
		
		// Rattach Data	
		if(null == rattachId)
			rattachId = dslContext.insertInto(RATTACH)
				.set(RATTACH.DOMAIN, domainId)
				.set(RATTACH.REGISTRY, registryEntepriseId)
				.set(RATTACH.MIMETYPE, (byte)32)
				.set(RATTACH.TYPE, (byte)4)
				.set(RATTACH.DATA, data)
				.set(RATTACH.SECURITY_LEVEL, security)
				.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
				.set(RATTACH.DESCRIPTION, fileName + "HIDE(" + password + ")")
				.returning(RATTACH.ID)
				.fetchOne()
				.getId();
		else
			if(null != data)
				dslContext.update(RATTACH)
					.set(RATTACH.DATA, data)
					.set(RATTACH.SECURITY_LEVEL, security)
					.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
					.set(RATTACH.DESCRIPTION, fileName + "HIDE(" + password + ")")
					.where(RATTACH.ID.eq(rattachId))
					.execute();
			else
				dslContext.update(RATTACH)
					.set(RATTACH.SECURITY_LEVEL, security)
					.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
					.set(RATTACH.DESCRIPTION, fileName + "HIDE(" + password + ")")
					.where(RATTACH.ID.eq(rattachId))
					.execute();
		
		// RaddInfo Password
		if(null == raddinfoId)
			dslContext.insertInto(RADDINFO)
					.set(RADDINFO.DOMAIN, domainId)
					.set(RADDINFO.REGISTRY, registryEntepriseId)
					.set(RADDINFO.ATTRIBUTE, "PASSWORD_CERTIFICATE_" + rattachId)
					.set(RADDINFO.VALUE, password)
					.set(RADDINFO.VALUE_DATE, new Date(new java.util.Date().getTime()))
					.execute();
		else
			dslContext.update(RADDINFO)
				.set(RADDINFO.VALUE, password)
				.where(RADDINFO.ID.eq(raddinfoId))
				.execute();
		
		// Rattach Tags
		
		dslContext.delete(RATTACH_TAG).where(RATTACH_TAG.RATTACH.eq(rattachId)).execute();
		
		for(CertificateType tagType : tagTypes) {
			Record tagRecord = dslContext.select().from(TAG).where(TAG.NAME.eq(tagType.name())).fetchOne();
			Integer tagId = null;
			if(null == tagRecord) {
				tagId = dslContext.insertInto(TAG)
							.set(TAG.DOMAIN, 0)
							.set(TAG.NAME, tagType.name())
							.set(TAG.TYPE, TagType.CERTIFICATE.value())
							.returning(TAG.ID)
							.fetchOne()
							.getId();
			} else
				tagId = tagRecord.get(TAG.ID);
				
			
			dslContext.insertInto(RATTACH_TAG)
				.set(RATTACH_TAG.DOMAIN, domainId)
				.set(RATTACH_TAG.RATTACH, rattachId)
				.set(RATTACH_TAG.TAG, tagId)
				.execute();
		}
	}
	
	// ----------------------------------------------------------- GET CERTIFICATE

	public static Certificate getCertificate(Connection conn, Integer rattachId) {
		Certificate certificate = new Certificate();
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		byte[] data = dslContext.select(RATTACH.DATA).from(RATTACH).where(RATTACH.ID.eq(rattachId)).fetchOne(RATTACH.DATA);
		String password = dslContext.select(RADDINFO.VALUE).from(RADDINFO).where(RADDINFO.ATTRIBUTE.eq("PASSWORD_CERTIFICATE_" + rattachId)).fetchOne(RADDINFO.VALUE);
		
		certificate.setType(MimeType.PKCS12.name());
		certificate.setCertificate(data);
		certificate.setPassword(password);
		
		return certificate;
	}
	
	public static CertificateInfo validateCertJava(Connection conn, Integer rattachId) {
		Certificate certificate = getCertificate(conn, rattachId);
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate())) {	
			KeyStore keyStore = KeyStore.getInstance(MimeType.PKCS12.name());
	        keyStore.load(certificateInputStream, certificate.getPassword().toCharArray());
	        Enumeration<String> enums = keyStore.aliases();
	        while (enums.hasMoreElements()) {
	            String alias = enums.nextElement();
	            X509Certificate c = (X509Certificate) keyStore.getCertificate(alias);
	            
	            System.out.println(c.getSubjectDN());
	            
	            String subjectDN = c.getSubjectDN().getName();
	            String enterprise = null;
	            String ocupation = null;
	            String cif = null;
	            String type = null;
	            try {
	            	enterprise = subjectDN.split("O=\"")[1].split("\"")[0];
		            ocupation = subjectDN.split("OU=")[1].split(",")[0];
		            cif = subjectDN.split("=VATES-")[1].split(",")[0];
		            type = subjectDN.split("T=")[1].split(",")[0];
	            } catch (Exception e) {}
	            
	            String surname = subjectDN.split("SURNAME=")[1].split(",")[0];
	            String name = subjectDN.split("GIVENNAME=")[1].split(",")[0];
	            String document = subjectDN.split("SERIALNUMBER=IDCES-")[1].split(" ")[0];
	            java.util.Date fromDate = c.getNotBefore();
	            java.util.Date toDate = c.getNotAfter();
	            
//	            System.out.println(c.getSubjectDN());
//	            System.out.println(c.getIssuerDN());
//	            System.out.println(c.getNotAfter());
//	            System.out.println(c.getNotBefore());
	            
	            return new CertificateInfo()
	            		.setEnterprise(enterprise)
	            		.setOcupation(ocupation)
	            		.setCif(cif)
	            		.setType(type)
	            		.setSurname(surname)
	            		.setName(name)
	            		.setDocument(document)
	            		.setFromDate(fromDate)
	            		.setToDate(toDate);
	        }
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
}
