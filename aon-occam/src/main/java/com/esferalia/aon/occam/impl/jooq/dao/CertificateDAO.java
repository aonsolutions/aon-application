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
import java.sql.Timestamp;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Certificate.CertificateOwner;
import com.esferalia.aon.occam.api.model.Certificate.CertificateSecurity;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Filter.AttachFilter;
import com.esferalia.aon.occam.api.model.Filter.CertificateFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddInfoFilter;
import com.esferalia.aon.occam.api.model.Properties.CertificateProperties;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.impl.jooq.dao.AttachPropertiesDAO.RattachPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RegistryAddInfoPropertiesDAO;
import com.esferalia.aon.watson.util.AonCertificateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CertificateDAO {

	protected static class CertificatePropertiesDAO implements CertificateProperties {
		protected Condition[] getConditions(CertificateFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.REGISTRY);}
		@Override public Property<String> getTypeProperty() {return new FilterDAO.PropertyDAO<>(TAG.NAME);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.SECURITY_LEVEL);}
	}
	
	// -------------------------- Constructor
	
	protected CertificateDAO() {
		super();
	}
	
	// -------------------------- Variables
	
	private static final RattachPropertiesDAO ATTACH_PROPERTIES = new RattachPropertiesDAO();
	private static final RegistryAddInfoPropertiesDAO RADDINFO_PROPERTIES = new RegistryAddInfoPropertiesDAO();
	private static final CertificatePropertiesDAO CERTIFICATE_PROPERTIES = new CertificatePropertiesDAO();

	// -------------------------- Methods
	
	public static Stream<Certificate> getStream(AONContext ctx, CertificateFilter filter) {
		return ctx.getDslContext().select().from(RATTACH)
			.leftOuterJoin(RATTACH_TAG).on(RATTACH_TAG.RATTACH.eq(RATTACH.ID))
			.leftOuterJoin(TAG).on(TAG.ID.eq(RATTACH_TAG.TAG))
			.where(CERTIFICATE_PROPERTIES.getConditions(filter))
			.and(RATTACH.TYPE.eq(RegistryAttachmentType.DIGITAL_CERTIFICATE.value()))
			.groupBy(RATTACH.ID)
			.fetch().stream().map(new CertificateFiller());
	}
	
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
			getEnterpriseParentCertificates(ctx, parentDomainId, certificateList);
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
		if(raddinfoFilter != null) ctx.getDslContext().delete(RADDINFO).where(RADDINFO_PROPERTIES.getConditions(raddinfoFilter)).execute();
	}
	
	public static void save(AONContext ctx, Integer domainId, Integer userId, Certificate certificate) {		
		// TGSS CERTIFICATE
		if(certificate.getOwner() == CertificateOwner.USER)
			saveUserCertificate(ctx, userId, certificate);
		
		// SEPE CERTIFICATE
		if(certificate.getOwner() == CertificateOwner.ENTERPRISE)
			saveEnterpriseCertificate(ctx, domainId, certificate);
	}

	// -------------------------- Methods auxiliar methods (getList)
	
	private static void getUserCertificates(AONContext ctx, Integer userId, List<Certificate> certificateList) throws IllegalArgumentException {
		try {
			Integer registryUserId = ctx.getDslContext().select(USER.REGISTRY).from(USER).where(USER.ID.eq(userId)).fetchOne(USER.REGISTRY);
			
			if(null == registryUserId) return;
			
			Stream<Certificate> stream = ctx.getDslContext().select().from(RATTACH)
					.where(RATTACH.REGISTRY.eq(registryUserId))
					.and(RATTACH.TYPE.eq((byte)4))
					.fetch().stream().map(r -> {	
						Certificate cert = CertificateFiller.build(r);	
						cert.setOwner(CertificateOwner.USER);
						getCertificateTags(ctx, cert);
						getCertificateInfo(ctx, cert);
						return cert;
					});
			
			certificateList.addAll(stream.collect(Collectors.toList()));
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private static void getEnterpriseCertificates(AONContext ctx, Integer domainId, List<Certificate> certificateList) throws IllegalArgumentException {
		try {
			Integer registryEnterpriseId = ctx.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY);
			
			if(null == registryEnterpriseId)
				return;
			
			Stream<Certificate> stream = ctx.getDslContext().select().from(RATTACH)
					.where(RATTACH.REGISTRY.eq(registryEnterpriseId))
					.and(RATTACH.TYPE.eq((byte)4))
					.and(RATTACH.DOMAIN.eq(domainId))
					.fetch().stream().map(r -> {
						Certificate cert = CertificateFiller.build(r);	
						cert.setOwner(CertificateOwner.ENTERPRISE);
						getCertificateTags(ctx, cert);
						getCertificateInfo(ctx, cert);
						return cert;
					});
			certificateList.addAll(stream.collect(Collectors.toList()));
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static void getEnterpriseParentCertificates(AONContext ctx, Integer parentDomainId, List<Certificate> certificateList) throws IllegalArgumentException {
		try {
			Integer registryEnterpriseId = ctx.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(parentDomainId)).fetchOne(ENTERPRISE.REGISTRY);
			
			if(null == registryEnterpriseId)
				return;
			
			Stream<Certificate> stream = ctx.getDslContext().select().from(RATTACH)
					.where(RATTACH.REGISTRY.eq(registryEnterpriseId))
					.and(RATTACH.TYPE.eq((byte)4))
					.and(RATTACH.SECURITY_LEVEL.eq((byte)0))
					.and(RATTACH.DOMAIN.eq(parentDomainId))
					.fetch().stream().map(r -> {
						Certificate cert = CertificateFiller.build(r);
						cert.setOwner(CertificateOwner.ENTERPRISE);
						getCertificateTags(ctx, cert);
						getCertificateInfo(ctx, cert);
						return cert;
					});
			certificateList.addAll(stream.collect(Collectors.toList()));
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
		            } catch (Exception e) {
		            	//e.printStackTrace();
		            }
		            
		            try {
		            	cif = subjectDN.split("=VATES-")[1].split(",")[0];
		            } catch (Exception e) {
		            	//e.printStackTrace();
		            }
		           
		            try {
			            type = subjectDN.split("T=")[1].split(",")[0];
		            } catch (Exception e) {
		            	//e.printStackTrace();
		            }

		            try {
			            ocupation = subjectDN.split("OU=")[1].split(",")[0];
		            } catch (Exception e) {
		            	//e.printStackTrace();
		            }
		            
		            String surname = "";
	            	String name = "";
		            try {
			            surname = subjectDN.split("SURNAME=")[1].split(",")[0];
			            name = subjectDN.split("GIVENNAME=")[1].split(",")[0];
		            } catch (Exception e) {
		            	//e.printStackTrace();
					}
		            
		            if(AonStringUtils.isBlank(name))
		            	try {
		            		name = subjectDN.split("O=")[1].split(",")[0];
		            	}catch (Exception e) {
		            		//e.printStackTrace();
						}
		            
		            if(AonStringUtils.isBlank(enterprise))
		            	try {
		            		enterprise = subjectDN.split("O=")[1].split(",")[0];
		            	}catch (Exception e) {
		            		//e.printStackTrace();
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
		
		if(null == certificate.getId()) certificate.setId(insert(ctx, userDomain, registryUserId, certificate));
		else update(ctx, certificate);
	
		// Rattach Tags
		updateCertificateTags(ctx, userDomain, certificate);
	}
	
	private static void saveEnterpriseCertificate(AONContext ctx, Integer domainId, Certificate certificate) {
		Integer registryEntepriseId = ctx.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domainId))
				.fetchOne(ENTERPRISE.REGISTRY);
		
		if(null == certificate.getId()) certificate.setId(insert(ctx, domainId, registryEntepriseId, certificate));
		else update(ctx, certificate);
					
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
			.set(RATTACH.DESCRIPTION, parseDescriptionLength(certificate.getDescription(), certificate.getPassword()))
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
				.set(RATTACH.DESCRIPTION, parseDescriptionLength(certificate.getDescription(), certificate.getPassword()))
				.where(RATTACH.ID.eq(certificate.getId()))
				.execute();
		else
			ctx.getDslContext().update(RATTACH)
				.set(RATTACH.SECURITY_LEVEL, parseCertificateSecurity(certificate.getConfidential()))
				.set(RATTACH.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
				.set(RATTACH.DESCRIPTION, parseDescriptionLength(certificate.getDescription(), certificate.getPassword()))
				.where(RATTACH.ID.eq(certificate.getId()))
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
//			try {
//				return description.split("HIDE\\(")[1].substring(0, description.split("HIDE\\(")[1].length() - 1);
				return AonCertificateUtils.getCertificatePassword(description);
//			} catch (Exception e) {
//				return "";
//			}
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
			
		// Normalize the text to decompose diacritical marks
        String normalized = Normalizer.normalize(description, Normalizer.Form.NFD);
        
        // Remove diacritical marks (e.g., accents, tildes)
        description = normalized.replaceAll("\\p{M}", "");
        
		return description;
	}
	
	private static byte parseCertificateSecurity(CertificateSecurity certificateSecurity) {
		return certificateSecurity == CertificateSecurity.PUBLIC ? (byte)0 : (byte)1;
	}
	
	
	public static class CertificateFiller extends Filler implements Function<Record, Certificate>{

		@Override
		public Certificate apply(Record r) {
			return build(r);
		}
		
		public static Certificate build(Record r) {
			return new Certificate()
				.setId(getValue(r, RATTACH.ID))
				.setDomain(getValue(r, RATTACH.DOMAIN))
				.setType(MimeType.PKCS12.name())
				.setDescription(getValue(r, RATTACH.DESCRIPTION))
				.setData(getValue(r, RATTACH.DATA))
				.setConfidential(getBoolean(r, RATTACH.SECURITY_LEVEL))
				.setUpdateDate(getValue(r, RATTACH.MODIFICATION_DATE));
		}
	}
	
	public static Stream<Certificate> getAEATCertificates(AONContext ctx, int domainId, String userLogin) {
		Company company = CompanyDAO.getByDomain(ctx, domainId);
		if (company == null || company.getId() == null) return Stream.empty();
		Domain domain = company.getDomain();
		if (domain == null || domain.getId() == null) return Stream.empty();
		User user = SecurityDAO.getUser( ctx, userLogin );
		if (user== null || user.getId() == null) return Stream.empty();
		return CertificateDAO.getStream(ctx, f -> getAEATCertificateFilter( ctx, domain, company, user, f));
	}
	
	private static Filter getAEATCertificateFilter(AONContext ctx, Domain domain, Company company, User user, CertificateProperties f) {
		Filter filter;
		if(domain.getParentId() != null) {
			if(!user.getDomain().getId().equals(domain.getParentId())) {
				filter = (f.getDomainProperty().eq(domain.getId()).or(
						f.getDomainProperty().eq(domain.getParentId())
						.and(f.getSecurityLevelProperty().eq(SecurityLevel.OFFICIAL.value())))
					);
			} else {
				Integer[] domains = {domain.getId(), domain.getParentId()};
				filter = f.getDomainProperty().in(domains);
			}
		} else filter = f.getDomainProperty().eq(domain.getId());
    	
		if(!user.getRegistry().isEmpty() && domain.getParentId() != null) {
			// Company parentCompany = AON.getCompanyForDomain(domain.getName(), domain.getParentId(), user.getLogin());
			Company parentCompany = CompanyDAO.getByDomain(ctx, domain.getParentId());
			Integer[] registries = {user.getRegistry().getId(), company.getId(), parentCompany.getId()};
			filter = filter.and(f.getRegistryProperty().in(registries));
		} else if(!user.getRegistry().isEmpty()) {
			Integer[] registries = {user.getRegistry().getId(), company.getId()};
			filter = filter.and(f.getRegistryProperty().in(registries));
		} else if(domain.getParentId() != null) {
			//Company parentCompany = AON.getCompanyForDomain(domain.getName(), domain.getParentId(), user.getLogin());
			Company parentCompany = CompanyDAO.getByDomain(ctx, domain.getParentId());
			Integer[] registries = {company.getId(), parentCompany.getId()};
			filter = filter.and(f.getRegistryProperty().in(registries));
		} else filter = filter.and(f.getRegistryProperty().eq(company.getId()));
		
		filter = filter.and(f.getTypeProperty().eq(CertificateType.AEAT.name()).or(f.getTypeProperty().isNull()));
		
    	return filter;
    }
}
