package com.esferalia.aon.occam.impl.jooq.dao;

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
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Certificate.CertificateOwner;
import com.esferalia.aon.occam.api.model.Certificate.CertificateSecurity;
import com.esferalia.aon.occam.api.model.Certificate.CertificateType;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.Filter.AttachFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddInfoFilter;
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddInfoProperties;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CertificateDAO {
	
	// -------------------------- AttachPropertiesDAO
	
	protected static class AttachPropertiesDAO implements AttachProperties {
		
		protected Condition[] getConditions(AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.TYPE);}
		@Override public Property<Date> getAttachDateProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.ATTACH_DATE);}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.CATEGORY);}
		@Override public Property<Integer> getTagProperty() {return new FilterDAO.PropertyDAO<>(RATTACH_TAG.TAG);}
		@Override public Property<Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.CREATION_USER);}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.DATA);}
		@Override public Property<String> getDparentIdProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.DPARENT_ID);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.DRIVE_ID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.MIMETYPE);}
		@Override public Property<Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.MODIFICATION_USER);}
		@Override public Property<Integer> getAttachModuleProperty() {return null;}
		@Override public Property<Integer> getScopeProperty() {return null;}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.SECURITY_LEVEL);}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
		@Override public Property<Integer> getContractProperty() {return null;}
		
	}

	// -------------------------- RegistryAddInfoPropertiesDAO
	
	protected static class RegistryAddInfoPropertiesDAO implements RegistryAddInfoProperties {
		
		protected Condition[] getConditions(RegistryAddInfoFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
	
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RADDINFO.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RADDINFO.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RADDINFO.REGISTRY);}
		@Override public Property<String> getAttributeProperty() {return new FilterDAO.PropertyDAO<>(RADDINFO.ATTRIBUTE);}
		@Override public Property<String> getValueProperty() {return new FilterDAO.PropertyDAO<>(RADDINFO.VALUE);}
		@Override public Property<Date> getValueDate() {return new FilterDAO.PropertyDAO<>(RADDINFO.VALUE_DATE);}
		
	}
	
	// -------------------------- Constructor
	
	protected CertificateDAO() {
		super();
	}
	
	// -------------------------- Variables
	
	private static final AttachPropertiesDAO ATTACH_PROPERTIES = new AttachPropertiesDAO();
	private static final RegistryAddInfoPropertiesDAO RADDINFO_PROPERTIES = new RegistryAddInfoPropertiesDAO();
	
	// -------------------------- Methods
	
	public static List<Certificate> getList(AONContext ctx, Integer domainId, Integer userId) throws IllegalArgumentException {
		try {
			List<Certificate> certificateList = new ArrayList<>();
			getUserCertificates(ctx, userId, certificateList);
			getEnterpriseCertificates(ctx, domainId, certificateList);
			return certificateList;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		
	}
	
	public static List<Certificate> getListWithParent(AONContext ctx, Integer domainId, Integer parentDomainId, Integer userId) throws IllegalArgumentException {
		try {
			List<Certificate> certificateList = new ArrayList<>();
			getUserCertificates(ctx, userId, certificateList);
			getEnterpriseCertificates(ctx, domainId, certificateList);
			getEnterprisParentCertificates(ctx, parentDomainId, certificateList);
			return certificateList;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public static Certificate get(AONContext ctx, AttachFilter attachFilter) {
		Record rattachRecord = ctx.getDslContext().select().from(RATTACH).where(ATTACH_PROPERTIES.getConditions(attachFilter)).fetchOne();
		return new Certificate()
			.setData(rattachRecord.get(RATTACH.DATA))
			.setType(MimeType.PKCS12.name())
			.setPassword(getCertificatePassword(ctx, rattachRecord));
	}

	public static CertificateInfo getInfo(AONContext ctx, AttachFilter attachFilter) throws IllegalArgumentException {
		Certificate certificate = get(ctx, attachFilter);
		return null != certificate ? verifyCertificate(certificate.getData(), certificate.getPassword()) : new CertificateInfo();
	}

	public static void delete(AONContext ctx, Integer attachId, AttachFilter attachFilter, RegistryAddInfoFilter raddinfoFilter) {
		ctx.getDslContext().delete(RATTACH_TAG).where(RATTACH_TAG.RATTACH.eq(attachId)).execute();
		ctx.getDslContext().delete(RATTACH).where(ATTACH_PROPERTIES.getConditions(attachFilter)).execute();
		ctx.getDslContext().delete(RADDINFO).where(RADDINFO_PROPERTIES.getConditions(raddinfoFilter)).execute();
	}
	
	public static void save(AONContext ctx, Integer domainId, Integer userId, Certificate certificate) {
		ctx.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0;");
		
		// TGSS CERTIFICATE
		if(certificate.getOwner() == CertificateOwner.USER)
			saveUserCertificate(ctx, userId, certificate);
		
		// SEPE CERTIFICATE
		if(certificate.getOwner() == CertificateOwner.ENTERPRISE)
			saveEnterpriseCertificate(ctx, domainId, certificate);
		
		ctx.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1;");
	}

	// -------------------------- Methods auxiliar methods (getList)
	
	private static void getUserCertificates(AONContext ctx, Integer userId, List<Certificate> certificateList) throws IllegalArgumentException {
		try {
			Integer registryUserId = ctx.getDslContext().select(USER.REGISTRY).from(USER).where(USER.ID.eq(userId)).fetchOne(USER.REGISTRY);
			
			if(null == registryUserId) return;
			
			Result<Record> certificateRecords = ctx.getDslContext().select().from(RATTACH)
					.where(RATTACH.REGISTRY.eq(registryUserId))
					.and(RATTACH.TYPE.eq((byte)4))
					.fetch();
			
			for(Record certificateRecord : certificateRecords) {
				
				java.util.Date updateDate = null == certificateRecord.get(RATTACH.MODIFICATION_DATE) ? null : new java.util.Date(certificateRecord.get(RATTACH.MODIFICATION_DATE).getTime());
				String description = certificateRecord.get(RATTACH.DESCRIPTION);
				
				Certificate certificate = new Certificate();
				certificate.setId(certificateRecord.get(RATTACH.ID));
				certificate.setOwner(CertificateOwner.USER);
				certificate.setDescription(parseDescription(description));
				certificate.setConfidential(certificateRecord.get(RATTACH.SECURITY_LEVEL) == 0 ? CertificateSecurity.PUBLIC : CertificateSecurity.PRIVATE);
				certificate.setHasCertificate(null != certificateRecord.get(RATTACH.DATA));
				certificate.setUpdateDate(updateDate);
				parsePassword(ctx, registryUserId, description, certificate);
				getCertificateTags(ctx, certificate);
				getCertificateInfo(ctx, certificate);
				
				certificateList.add(certificate);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private static void getEnterpriseCertificates(AONContext ctx, Integer domainId, List<Certificate> certificateList) throws IllegalArgumentException {
		try {
			Integer registryEnterpriseId = ctx.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY);
			
			if(null == registryEnterpriseId)
				return;
			
			Result<Record> certificateRecords = ctx.getDslContext().select().from(RATTACH)
					.where(RATTACH.REGISTRY.eq(registryEnterpriseId))
					.and(RATTACH.TYPE.eq((byte)4))
					.and(RATTACH.DOMAIN.eq(domainId))
					.fetch();
			
			for(Record certificateRecord : certificateRecords) {
				
				java.util.Date updateDate = null == certificateRecord.get(RATTACH.MODIFICATION_DATE) ? null : new java.util.Date(certificateRecord.get(RATTACH.MODIFICATION_DATE).getTime());
				String description = certificateRecord.get(RATTACH.DESCRIPTION);
				
				Certificate certificate = new Certificate();
				certificate.setId(certificateRecord.get(RATTACH.ID));
				certificate.setOwner(CertificateOwner.ENTERPRISE);
				certificate.setDescription(parseDescription(description));
				certificate.setConfidential(certificateRecord.get(RATTACH.SECURITY_LEVEL) == 0 ? CertificateSecurity.PUBLIC : CertificateSecurity.PRIVATE);
				certificate.setHasCertificate(null != certificateRecord.get(RATTACH.DATA));
				certificate.setUpdateDate(updateDate);
				parsePassword(ctx, registryEnterpriseId, description, certificate);
				getCertificateTags(ctx, certificate);
				getCertificateInfo(ctx, certificate);
				
				certificateList.add(certificate);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static void getEnterprisParentCertificates(AONContext ctx, Integer parentDomainId, List<Certificate> certificateList) throws IllegalArgumentException {
		try {
			Integer registryEnterpriseId = ctx.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(parentDomainId)).fetchOne(ENTERPRISE.REGISTRY);
			
			if(null == registryEnterpriseId)
				return;
			
			Result<Record> certificateRecords = ctx.getDslContext().select().from(RATTACH)
					.where(RATTACH.REGISTRY.eq(registryEnterpriseId))
					.and(RATTACH.TYPE.eq((byte)4))
					.and(RATTACH.SECURITY_LEVEL.eq((byte)0))
					.and(RATTACH.DOMAIN.eq(parentDomainId))
					.fetch();
			
			for(Record certificateRecord : certificateRecords) {
				
				java.util.Date updateDate = null == certificateRecord.get(RATTACH.MODIFICATION_DATE) ? null : new java.util.Date(certificateRecord.get(RATTACH.MODIFICATION_DATE).getTime());
				String description = certificateRecord.get(RATTACH.DESCRIPTION);
				
				Certificate certificate = new Certificate();
				certificate.setId(certificateRecord.get(RATTACH.ID));
				certificate.setDomain(certificateRecord.get(RATTACH.DOMAIN));
				certificate.setOwner(CertificateOwner.ENTERPRISE);
				certificate.setDescription(parseDescription(description));
				certificate.setConfidential(certificateRecord.get(RATTACH.SECURITY_LEVEL) == 0 ? CertificateSecurity.PUBLIC : CertificateSecurity.PRIVATE);
				certificate.setHasCertificate(null != certificateRecord.get(RATTACH.DATA));
				certificate.setUpdateDate(updateDate);
				parsePassword(ctx, registryEnterpriseId, description, certificate);
				getCertificateTags(ctx, certificate);
				getCertificateInfo(ctx, certificate);
				
				certificateList.add(certificate);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	// -------------------------- Methods auxiliar methods (getInfo)
	
	public static CertificateInfo verifyCertificate(byte[] data, String password) throws IllegalArgumentException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(data)) {	
			
			KeyStore keyStore = KeyStore.getInstance(MimeType.PKCS12.name());
	        keyStore.load(certificateInputStream, password.toCharArray());
	        Enumeration<String> enums = keyStore.aliases();
	        
	        while (enums.hasMoreElements()) {
	        	
	        	try {
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
		            	cif = subjectDN.split("=VATES-")[1].split(",")[0];
			            type = subjectDN.split("T=")[1].split(",")[0];
			            ocupation = subjectDN.split("OU=")[1].split(",")[0];
		            } catch (Exception e) {
		            	e.printStackTrace();
		            }
		            
		            String surname = "";
	            	String name = "";
		            try {
			            surname = subjectDN.split("SURNAME=")[1].split(",")[0];
			            name = subjectDN.split("GIVENNAME=")[1].split(",")[0];
		            } catch (Exception e) {
		            	e.printStackTrace();
					}
		            
		            if(AonStringUtils.isBlank(name))
		            	try {
		            		name = subjectDN.split("O=")[1].split(",")[0];
		            	}catch (Exception e) {
		            		e.printStackTrace();
						}
		            
		            if(AonStringUtils.isBlank(enterprise))
		            	try {
		            		enterprise = subjectDN.split("O=")[1].split(",")[0];
		            	}catch (Exception e) {
		            		e.printStackTrace();
						}
		            
		            String document = "";
		           
		            try {
		            	document = subjectDN.split("SERIALNUMBER=IDCES-")[1].split(",")[0];
		            } catch (Exception e) {
						try {
							document = subjectDN.split("SERIALNUMBER=")[1].split(",")[0];
						} catch (Exception e1) {
							document = "";
						}
					}
		            
		            java.util.Date fromDate = c.getNotBefore();
		            java.util.Date toDate = c.getNotAfter();
		            
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
		            
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        		throw new IllegalArgumentException("Certificado validado correctamente");
				}
	        }
		} catch (Exception e) {
			if(AonStringUtils.equalsIgnoreCase(e.getMessage(), "keystore password was incorrect"))
				throw new IllegalArgumentException("Contrase\u00F1a incorrecta");
			throw new IllegalArgumentException(e.getMessage());
		}
		return null;
	}

	// -------------------------- Methods auxiliar methods (save)
	
	private static void saveUserCertificate(AONContext ctx, Integer userId, Certificate certificate) {
		Record userRecord = ctx.getDslContext().select().from(USER).where(USER.ID.eq(userId)).fetchOne();
		Integer registryUserId = userRecord.get(USER.REGISTRY);
		Integer userDomain = userRecord.get(USER.DOMAIN);
		
		// User Registry
		if(null == registryUserId) registryUserId = createRegistryForUser(ctx, userRecord);
		
		// Description	
		String description = parseDescriptionLength(certificate.getDescription(), certificate.getPassword());
		certificate.setDescription(description);
		
		if(null == certificate.getId()) certificate.setId(insert(ctx, userDomain, registryUserId, certificate));
		else update(ctx, certificate);
		
		// Update Password
		if(null != certificate.getPasswordId()) updatePassword(ctx, certificate);
			
		// Rattach Tags
		updateCertificateTags(ctx, userDomain, certificate);
	}
	
	private static void saveEnterpriseCertificate(AONContext ctx, Integer domainId, Certificate certificate) {
		Integer registryEntepriseId = ctx.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domainId))
				.fetchOne(ENTERPRISE.REGISTRY);
		
		// Description	
		String description = parseDescriptionLength(certificate.getDescription(), certificate.getPassword());
		certificate.setDescription(description);
		
		if(null == certificate.getId()) certificate.setId(insert(ctx, domainId, registryEntepriseId, certificate));
		else update(ctx, certificate);
		
		// Update Password
		if(null != certificate.getPasswordId()) updatePassword(ctx, certificate);
			
		// Rattach Tags
		updateCertificateTags(ctx, domainId, certificate);
	}
	
	private static Integer insert(AONContext ctx, Integer domainId, Integer registryId, Certificate certificate) {
		return ctx.getDslContext().insertInto(RATTACH)
			.set(RATTACH.DOMAIN, domainId)
			.set(RATTACH.REGISTRY, registryId)
			.set(RATTACH.MIMETYPE, (byte)36)
			.set(RATTACH.TYPE, (byte)4)
			.set(RATTACH.DATA, certificate.getData())
			.set(RATTACH.SECURITY_LEVEL, parseCertificateSecurity(certificate.getConfidential()))
			.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
			.set(RATTACH.DESCRIPTION, certificate.getDescription())
			.returning(RATTACH.ID)
			.fetchOne()
			.getId();
	}
	
	private static void update(AONContext ctx, Certificate certificate) {
		if(null != certificate.getData() && certificate.getData().length > 0)
			ctx.getDslContext().update(RATTACH)
				.set(RATTACH.DATA, certificate.getData())
				.set(RATTACH.SECURITY_LEVEL, parseCertificateSecurity(certificate.getConfidential()))
				.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
				.set(RATTACH.DESCRIPTION, certificate.getDescription())
				.where(RATTACH.ID.eq(certificate.getId()))
				.execute();
		else
			ctx.getDslContext().update(RATTACH)
				.set(RATTACH.SECURITY_LEVEL, parseCertificateSecurity(certificate.getConfidential()))
				.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
				.set(RATTACH.DESCRIPTION, certificate.getDescription())
				.where(RATTACH.ID.eq(certificate.getId()))
				.execute();
	}
	
	private static void updatePassword(AONContext ctx, Certificate certificate) {
		ctx.getDslContext().update(RADDINFO)
			.set(RADDINFO.VALUE, certificate.getPassword())
			.where(RADDINFO.ID.eq(certificate.getPasswordId()))
			.execute();
	}
	
	private static void updateCertificateTags(AONContext ctx, Integer domainId, Certificate certificate) {
		ctx.getDslContext().delete(RATTACH_TAG).where(RATTACH_TAG.RATTACH.eq(certificate.getId())).execute();
		
		for(CertificateType tagType : certificate.getTags()) {

			Integer tagId = ctx.getDslContext().select()
					.from(TAG)
					.where(TAG.NAME.eq(tagType.name()))
					.and(TAG.TYPE.eq(TagType.CERTIFICATE.value()))
					.and(TAG.DOMAIN.eq(0))
					.limit(1)
					.fetch().stream().map(r -> r.getValue(TAG.ID)).findFirst().orElse(null);
			
			if(tagId == null) {
				tagId = ctx.getDslContext().insertInto(TAG)
							.set(TAG.DOMAIN, 0)
							.set(TAG.NAME, tagType.name())
							.set(TAG.TYPE, TagType.CERTIFICATE.value())
							.returning(TAG.ID)
							.fetchOne()
							.getId();
			}
			
			ctx.getDslContext().insertInto(RATTACH_TAG)
				.set(RATTACH_TAG.DOMAIN, domainId)
				.set(RATTACH_TAG.RATTACH, certificate.getId())
				.set(RATTACH_TAG.TAG, tagId)
				.execute();
		}
	}
	
	// -------------------------- Methods auxiliar methods
	
	private static String parseDescription(String description) {
		if( AonStringUtils.isNotBlank(description) && 
			AonStringUtils.containsIgnoreCase(description, "HIDE(")) {
			try {
				return description.split("HIDE\\(")[0];
			} catch (Exception e) {
				return description;
			}
		} else 
			return description;
	}
	
	private static void parsePassword(AONContext ctx, Integer registryId, String description, Certificate certificate) {
		Record certificatePasswordRecord = ctx.getDslContext().select().from(RADDINFO)
				.where(RADDINFO.REGISTRY.eq(registryId))
				.and(RADDINFO.ATTRIBUTE.eq("PASSWORD_CERTIFICATE_" + certificate.getId()))
				.fetchOne();
		
		if( AonStringUtils.isNotBlank(description) && 
			AonStringUtils.containsIgnoreCase(description, "HIDE(")) {
			try {
				certificate.setPassword(description.split("HIDE\\(")[1].split("\\)")[0]);
			} catch (Exception e) {
				certificate.setPassword("");
			}
		} else {
			if(null != certificatePasswordRecord) {
				certificate.setPasswordId(certificatePasswordRecord.get(RADDINFO.ID));
				certificate.setPassword(certificatePasswordRecord.get(RADDINFO.VALUE));
			}
		}
	}
	
	private static void getCertificateTags(AONContext ctx, Certificate certificate) {
		List<Record> certificateTags = ctx.getDslContext().select().from(RATTACH_TAG)
				.where(RATTACH_TAG.RATTACH.eq(certificate.getId())).fetch();
		
		List<CertificateType> tags = new ArrayList<>();
		for(Record certificateTag : certificateTags) {
			Record tagRecord = ctx.getDslContext().select().from(TAG).where(TAG.ID.eq(certificateTag.get(RATTACH_TAG.TAG))).fetchOne();
			tags.add(CertificateType.valueOf(tagRecord.get(TAG.NAME)));
		}
		certificate.setTags(tags);
		
	}
	
	private static void getCertificateInfo(AONContext ctx, Certificate certificate) {
		try {
			certificate.setCertificateInfo(getInfo(ctx, f -> f.getIdProperty().eq(certificate.getId())));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Integer createRegistryForUser(AONContext ctx, Record userRecord) {
		String userName = userRecord.get(USER.NAME);
		Integer userDomain = userRecord.get(USER.DOMAIN);
		Integer userId = userRecord.get(USER.ID);
		
		RegistryRecord registryUserRecord = ctx.getDslContext().insertInto(REGISTRY)
				.set(REGISTRY.DOMAIN, userDomain)
				.set(REGISTRY.NAME, userName)
				.returning(REGISTRY.ID)
				.fetchOne();
		
		Integer registryUserId = registryUserRecord.get(REGISTRY.ID);
		
		ctx.getDslContext().update(USER)
			.set(USER.REGISTRY, registryUserId)
			.where(USER.ID.eq(userId))
			.execute();
		
		return registryUserId;
	}
	
	private static String getCertificatePassword(AONContext ctx, Record rattachRecord) {
		String description = rattachRecord.get(RATTACH.DESCRIPTION);
		
		if(AonStringUtils.containsIgnoreCase(description, "HIDE")) {
			try {
				return description.split("HIDE\\(")[1].split("\\)")[0];
			} catch (Exception e) {
				return "";
			}
		} else
			return ctx.getDslContext().select(RADDINFO.VALUE).from(RADDINFO)
				.where(RADDINFO.ATTRIBUTE.eq("PASSWORD_CERTIFICATE_" + rattachRecord.get(RATTACH.ID)))
				.fetchOne(RADDINFO.VALUE);
	}
		
	private static String parseDescriptionLength(String fileName, String password) {
		String description = fileName + "HIDE(" + password + ")";
		if(description.length() > 64) {
			Integer diff = description.length() - 62; // Asi nos aseguramos no apurar los 64 varchar
			description = AonStringUtils.substring(fileName, 0, fileName.length() - diff) + "HIDE(" + password + ")";
		}
			
		return description;
	}
	
	private static byte parseCertificateSecurity(CertificateSecurity certificateSecurity) {
		return certificateSecurity == CertificateSecurity.PUBLIC ? (byte)0 : (byte)1;
	}
	
}
