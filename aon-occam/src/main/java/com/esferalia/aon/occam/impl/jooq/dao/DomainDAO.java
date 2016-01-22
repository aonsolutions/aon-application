package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.DomainGserviceaccount.DOMAIN_GSERVICEACCOUNT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record10;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.DomainGserviceaccountRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.DomainGserviceaccountFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.DomainGserviceaccountProperties;
import com.esferalia.aon.occam.api.model.Properties.DomainProperties;
import com.esferalia.aon.occam.api.model.type.DomainType;

public class DomainDAO {
	private static final DomainPropertiesDAO DOMAIN_PROPERTIES = new DomainPropertiesDAO();

	protected static class DomainPropertiesDAO implements DomainProperties {
		protected Condition[] getConditions(DomainFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<Byte>(DOMAIN.ACTIVE);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(DOMAIN.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(DOMAIN.CREATION_USER);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(DOMAIN.DESCRIPTION);}
		@Override public Property<Byte> getDisabledomainmanagementProperty() {return new FilterDAO.PropertyDAO<Byte>(DOMAIN.DISABLEDOMAINMANAGEMENT);}
		@Override public Property<Byte> getDomainmanagementProperty() {return new FilterDAO.PropertyDAO<Byte>(DOMAIN.DOMAINMANAGEMENT);}
		@Override public Property<Byte> getEnableheredityProperty() {return new FilterDAO.PropertyDAO<Byte>(DOMAIN.ENABLEHEREDITY);}
		@Override public Property<Date> getExpirationdateProperty() {return new FilterDAO.PropertyDAO<Date>(DOMAIN.EXPIRATIONDATE);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(DOMAIN.ID);}
		@Override public Property<Timestamp> getLastaccessDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(DOMAIN.LASTACCESS_DATE);}
		@Override public Property<String> getLastaccessUserProperty() {return new FilterDAO.PropertyDAO<String>(DOMAIN.LASTACCESS_USER);}
		@Override public Property<Integer> getMaxdefinedusersProperty() {return new FilterDAO.PropertyDAO<Integer>(DOMAIN.MAXDEFINEDUSERS);}
		@Override public Property<Integer> getMaxdocumentsizeProperty() {return new FilterDAO.PropertyDAO<Integer>(DOMAIN.MAXDOCUMENTSIZE);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(DOMAIN.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(DOMAIN.MODIFICATION_USER);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(DOMAIN.NAME);}
		@Override public Property<String> getOwnerProperty() {return new FilterDAO.PropertyDAO<String>(DOMAIN.OWNER);}
		@Override public Property<Integer> getParentProperty() {return new FilterDAO.PropertyDAO<Integer>(DOMAIN.PARENT);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(DOMAIN.SCOPE);}
		@Override public Property<String> getSubdomainsuffixProperty() {return new FilterDAO.PropertyDAO<String>(DOMAIN.SUBDOMAINSUFFIX);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(DOMAIN.TYPE);}
	}
	public static Domain getDomain(AONContext ctx, Integer domainId){
		Domain domain = null;
		Result<DomainRecord> domainResult = ctx.getDslContext().select()
				.from(DOMAIN)
				.where(DOMAIN.ID.eq(domainId))
				.fetchInto(DOMAIN);
		 
		if(domainResult.isNotEmpty()){
			DomainRecord domainRecord = domainResult.get(0);
			domain = new Domain();
			domain.setId(domainId);
			domain.setActive(domainRecord.getActive() == 1);
			domain.setChild(domainRecord.getParent() == null);
			domain.setDescription(domainRecord.getDescription());
			domain.setDomainType(DomainType.values()[domainRecord.getType()]);
			domain.setName(domainRecord.getName());
			domain.setParent(domainRecord.getParent() != null);
			domain.setParentId(domainRecord.getParent());
			domain.setEnableHeredity(domainRecord.getEnableheredity() == 1);
		}
		return domain;
	}
	
	public static LinkedList<Domain> getDomainList(AONContext ctx, DomainFilter filter){
		return ctx.getDslContext().select().from(DOMAIN).where(DOMAIN_PROPERTIES.getConditions(filter))
			.fetchInto(DOMAIN).stream().map(new FullDomainFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Domain insertDomain(AONContext ctx, Integer parentDomain,
			String document, String name, List<String> messages) {

		Domain domain = null;

		Record domainRecord = ctx
				.getDslContext()
				.select()
				.from(DOMAIN.join(REGISTRY).on(DOMAIN.ID.eq(REGISTRY.DOMAIN))
						.join(COMPANY).on(REGISTRY.ID.eq(COMPANY.REGISTRY)))
				.where(REGISTRY.DOCUMENT.eq(document).and(
						DOMAIN.PARENT.eq(parentDomain))).fetchAny();

		if (domainRecord != null) {

			domain = new Domain();

			domain.setId(domainRecord.getValue(DOMAIN.ID));
			domain.setName(domainRecord.getValue(DOMAIN.NAME));
			domain.setParentId(domainRecord.getValue(DOMAIN.PARENT));

			messages.add(name
					+ " ya se encuentra registrada en la base de datos. No se crea el dominio.");

			return domain;

		}

		DomainRecord parent = getParentDomain(ctx, parentDomain);

		String lowerDocument = document.toLowerCase().concat("-")
				.concat(parent.getValue(DOMAIN.SUBDOMAINSUFFIX));

		// DOMAIN

		int newDomainId = ctx
				.getDslContext()
				.insertInto(DOMAIN)
				.set(DOMAIN.CREATION_USER,
						parent.getValue(DOMAIN.CREATION_USER))
				.set(DOMAIN.CREATION_DATE,
						new java.sql.Timestamp(System.currentTimeMillis()))
				.set(DOMAIN.DOMAINMANAGEMENT, (byte) 0)
				.set(DOMAIN.TYPE, (byte) 0)
				.set(DOMAIN.PARENT, parent.getValue(DOMAIN.ID))
				.set(DOMAIN.OWNER, parent.getValue(DOMAIN.OWNER))
				.set(DOMAIN.NAME, lowerDocument).set(DOMAIN.DESCRIPTION, name)
				.set(DOMAIN.ENABLEHEREDITY, (byte) 1)
				.set(DOMAIN.MAXDEFINEDUSERS, 0).set(DOMAIN.MAXDOCUMENTSIZE, 1)
				.set(DOMAIN.MAXTOTALDOCUMENTSIZE, 16).returning(DOMAIN.ID)
				.fetchOne().getId();

		messages.add("Dominio " + lowerDocument + " insertado correctamente");

		ctx.getDslContext().insertInto(DOMAIN_APPLICATION)
				.set(DOMAIN_APPLICATION.DOMAIN, newDomainId)
				.set(DOMAIN_APPLICATION.APPLICATION, 28)
				.set(DOMAIN_APPLICATION.ACTIVE, (byte) 1)
				.set(DOMAIN_APPLICATION.AUDIT_LEVEL, (byte) 0).execute();

		messages.add("Aplicacion de dominio insertada correctamente");

		int newRegistryId = ctx.getDslContext().insertInto(REGISTRY)
				.set(REGISTRY.DOMAIN, newDomainId)
				.set(REGISTRY.DOCUMENT, document)
				.set(REGISTRY.DOCUMENT_TYPE, (byte) 0).set(REGISTRY.NAME, name)
				.set(REGISTRY.TYPE, (byte) 1).returning(REGISTRY.ID).fetchOne()
				.getId();

		messages.add("Registrado insertado correctamente");

		ctx.getDslContext().insertInto(COMPANY)
				.set(COMPANY.REGISTRY, newRegistryId)
				.set(COMPANY.DOMAIN, newDomainId).execute();

		EnterpriseRecord registryRecord = ctx.getDslContext()
				.selectFrom(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(parentDomain)).fetchAny();

		messages.add("Empresa insertada correctamente");

		ctx.getDslContext()
				.insertInto(ENTERPRISE)
				.set(ENTERPRISE.REGISTRY, newRegistryId)
				.set(ENTERPRISE.DOMAIN, newDomainId)
				.set(ENTERPRISE.SCOPE,
						registryRecord.getValue(ENTERPRISE.SCOPE)).execute();

		domain = new Domain();
		domain.setId(newDomainId);
		domain.setName(lowerDocument);
		domain.setParentId(parent.getValue(DOMAIN.ID));

		return domain;
	}

	public static DomainRecord getParentDomain(AONContext ctx, Integer domain) {
		return ctx.getDslContext().selectFrom(DOMAIN)
				.where(DOMAIN.ID.eq(domain)).fetchOne();
	}
	
	//-------------------- DOMAIN G SERVICE ACCOUNT
	
	public static DomainGserviceaccount getDomainGserviceaccount(AONContext ctx, DomainGserviceaccountFilter filter){
		return ctx.getDslContext().select()
		.from(DOMAIN_GSERVICEACCOUNT)
		.where(DOMAIN_GSERVICEACCOUNT_PROPERTIES.getConditions(filter))
		.limit(1).fetchInto(DOMAIN_GSERVICEACCOUNT).stream().map(new FullDomainGserviceaccountFiller())
		.findFirst().orElse(null);
	}
	
	public static LinkedList<DomainGserviceaccount> getDomainGserviceaccountList(AONContext ctx){
		return ctx.getDslContext().select().from(DOMAIN_GSERVICEACCOUNT)
				.fetchInto(DOMAIN_GSERVICEACCOUNT).stream().map(new FullDomainGserviceaccountFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static DomainGserviceaccount getDomainGserviceaccount(AONContext ctx){
		Result<Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String>> data = ctx.getDslContext()
						.select(DOMAIN_GSERVICEACCOUNT.CLIENT_ID, DOMAIN_GSERVICEACCOUNT.CLIENT_SECRET
								, DOMAIN.NAME , DOMAIN_GSERVICEACCOUNT.DOMAIN
								, DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, DOMAIN_GSERVICEACCOUNT.LIMIT
								, DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY, DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY
								, DOMAIN_GSERVICEACCOUNT.SIZE, DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT)
						.from(DOMAIN_GSERVICEACCOUNT).join(DOMAIN).on(DOMAIN.ID.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN))
						.where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(ctx.getDomainId()))
						.fetch();
				
		Result<Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String>> dataParent = ctx.getDslContext()
						.select(DOMAIN_GSERVICEACCOUNT.CLIENT_ID, DOMAIN_GSERVICEACCOUNT.CLIENT_SECRET
								, DOMAIN.NAME , DOMAIN_GSERVICEACCOUNT.DOMAIN
								, DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, DOMAIN_GSERVICEACCOUNT.LIMIT
								, DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY, DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY
								, DOMAIN_GSERVICEACCOUNT.SIZE, DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT)
						.from(DOMAIN_GSERVICEACCOUNT).join(DOMAIN).on(DOMAIN.PARENT.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN))
						.where(DOMAIN.ID.eq(ctx.getDomainId()))
						.fetch();
				
		Result<Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String>> dataDefault = ctx.getDslContext()
						.select(DOMAIN_GSERVICEACCOUNT.CLIENT_ID, DOMAIN_GSERVICEACCOUNT.CLIENT_SECRET
								, DOMAIN.NAME , DOMAIN_GSERVICEACCOUNT.DOMAIN
								, DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, DOMAIN_GSERVICEACCOUNT.LIMIT
								, DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY, DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY
								, DOMAIN_GSERVICEACCOUNT.SIZE, DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT)
						.from(DOMAIN_GSERVICEACCOUNT).join(DOMAIN).on(DOMAIN.ID.eq(DOMAIN_GSERVICEACCOUNT.DOMAIN))
						.where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(0))
						.fetch();
				
				
		Boolean b = false;
		DomainGserviceaccount dgserviceaccount = new DomainGserviceaccount();
		for(Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String> r : data){
			if(!b){
				dgserviceaccount = new DomainGserviceaccount()
					.setClientId(r.getValue(DOMAIN_GSERVICEACCOUNT.CLIENT_ID))
					.setClientSecret(r.getValue(DOMAIN_GSERVICEACCOUNT.CLIENT_SECRET))
					.setDomain(r.getValue(DOMAIN.NAME))
					.setDomainId(r.getValue(DOMAIN_GSERVICEACCOUNT.DOMAIN))
					.setEmailAddress(r.getValue(DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS))
					.setGoogleAccount(r.getValue(DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT))
					.setLimit(r.getValue(DOMAIN_GSERVICEACCOUNT.LIMIT))
					.setPrivateKey(r.getValue(DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY))
					.setPublicKey(r.getValue(DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY))
					.setSize(r.getValue(DOMAIN_GSERVICEACCOUNT.SIZE));
				b= true;
			}
		};
		for(Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String> r : dataParent){
			if(!b){
				dgserviceaccount = new DomainGserviceaccount()
						.setClientId(r.getValue(DOMAIN_GSERVICEACCOUNT.CLIENT_ID))
						.setClientSecret(r.getValue(DOMAIN_GSERVICEACCOUNT.CLIENT_SECRET))
						.setDomain(r.getValue(DOMAIN.NAME))
						.setDomainId(r.getValue(DOMAIN_GSERVICEACCOUNT.DOMAIN))
						.setEmailAddress(r.getValue(DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS))
						.setGoogleAccount(r.getValue(DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT))
						.setLimit(r.getValue(DOMAIN_GSERVICEACCOUNT.LIMIT))
						.setPrivateKey(r.getValue(DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY))
						.setPublicKey(r.getValue(DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY))
						.setSize(r.getValue(DOMAIN_GSERVICEACCOUNT.SIZE));
				b= true;
			}
		};
		for(Record10<String, byte[], String, Integer, String, Double, byte[], String, Double, String> r : dataDefault){
			if(!b){
				dgserviceaccount = new DomainGserviceaccount()
						.setClientId(r.getValue(DOMAIN_GSERVICEACCOUNT.CLIENT_ID))
						.setClientSecret(r.getValue(DOMAIN_GSERVICEACCOUNT.CLIENT_SECRET))
						.setDomain(r.getValue(DOMAIN.NAME))
						.setDomainId(r.getValue(DOMAIN_GSERVICEACCOUNT.DOMAIN))
						.setEmailAddress(r.getValue(DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS))
						.setGoogleAccount(r.getValue(DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT))
						.setLimit(r.getValue(DOMAIN_GSERVICEACCOUNT.LIMIT))
						.setPrivateKey(r.getValue(DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY))
						.setPublicKey(r.getValue(DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY))
						.setSize(r.getValue(DOMAIN_GSERVICEACCOUNT.SIZE));
				b= true;
			}
		};
				
		return dgserviceaccount;		
	}
	
	public static void updateDomainGserviceaccount(AONContext ctx, String googleAccount){
		ctx.getDslContext().update(DOMAIN_GSERVICEACCOUNT)
		.set(DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT, googleAccount)
		.where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(ctx.getDomainId()))
		.execute();
	}
	
	public static void updateDomainGserviceaccount(AONContext ctx, DomainGserviceaccount dgsa){
		if(dgsa.getPrivateKey() != null){
			ctx.getDslContext().update(DOMAIN_GSERVICEACCOUNT)
				.set(DOMAIN_GSERVICEACCOUNT.DOMAIN, dgsa.getDomainId())
				.set(DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, dgsa.getEmailAddress())
				.set(DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT, dgsa.getGoogleAccount())
				.set(DOMAIN_GSERVICEACCOUNT.LIMIT, dgsa.getLimit())
				.set(DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY, dgsa.getPublicKey())
				.set(DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY, dgsa.getPrivateKey())
				.where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(dgsa.getDomainId()))
				.execute();
		}else {
			ctx.getDslContext().update(DOMAIN_GSERVICEACCOUNT)
				.set(DOMAIN_GSERVICEACCOUNT.DOMAIN, dgsa.getDomainId())
				.set(DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, dgsa.getEmailAddress())
				.set(DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT, dgsa.getGoogleAccount())
				.set(DOMAIN_GSERVICEACCOUNT.LIMIT, dgsa.getLimit())
				.set(DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY, dgsa.getPublicKey())
				.where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(dgsa.getDomainId()))
				.execute();
		}
	}
	
	public static void deleteDomainGserviceaccount(AONContext ctx){
		ctx.getDslContext().delete(DOMAIN_GSERVICEACCOUNT)
		.where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(ctx.getDomainId()))
		.execute();
	}
	
	public static void insertDomainGserviceaccount(AONContext ctx, DomainGserviceaccount dgsa){
		ctx.getDslContext().insertInto(DOMAIN_GSERVICEACCOUNT,
				DOMAIN_GSERVICEACCOUNT.CLIENT_ID,
				DOMAIN_GSERVICEACCOUNT.DOMAIN,
				DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS,
				DOMAIN_GSERVICEACCOUNT.LIMIT,
				DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY,
				DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY,
				DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT)
		.values(dgsa.getClientId(), dgsa.getDomainId(), dgsa.getEmailAddress(), dgsa.getLimit(),
				dgsa.getPrivateKey(),dgsa.getPublicKey(), dgsa.getGoogleAccount()).execute();	
		
		if(dgsa.getPrivateKey() != null){
			ctx.getDslContext().update(DOMAIN_GSERVICEACCOUNT)
				.set(DOMAIN_GSERVICEACCOUNT.DOMAIN, dgsa.getDomainId())
				.set(DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, dgsa.getEmailAddress())
				.set(DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT, dgsa.getGoogleAccount())
				.set(DOMAIN_GSERVICEACCOUNT.LIMIT, dgsa.getLimit())
				.set(DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY, dgsa.getPublicKey())
				.set(DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY, dgsa.getPrivateKey())
				.where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(dgsa.getDomainId()))
				.execute();
		}else {
			ctx.getDslContext().update(DOMAIN_GSERVICEACCOUNT)
				.set(DOMAIN_GSERVICEACCOUNT.DOMAIN, dgsa.getDomainId())
				.set(DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS, dgsa.getEmailAddress())
				.set(DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT, dgsa.getGoogleAccount())
				.set(DOMAIN_GSERVICEACCOUNT.LIMIT, dgsa.getLimit())
				.set(DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY, dgsa.getPublicKey())
				.where(DOMAIN_GSERVICEACCOUNT.DOMAIN.eq(dgsa.getDomainId()))
				.execute();
		}
	}
	
	public static Integer[] getSonsDomains(AONContext ctx){
		Object[] oArray = ctx.getDslContext().select(DOMAIN.ID).from(DOMAIN).where(DOMAIN.PARENT.eq(ctx.getDomainId())).fetch().stream().map(d -> d.getValue(DOMAIN.ID)).toArray();
		return Arrays.copyOf(oArray, oArray.length, Integer[].class);
	}
	
	
	private static final DomainGserviceaccountDAO DOMAIN_GSERVICEACCOUNT_PROPERTIES = new DomainGserviceaccountDAO();
	private static class DomainGserviceaccountDAO implements DomainGserviceaccountProperties {
		private Condition[] getConditions(DomainGserviceaccountFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<String> getClientIdProperty() {return new FilterDAO.PropertyDAO<String>(DOMAIN_GSERVICEACCOUNT.CLIENT_ID);}
		@Override public Property<byte[]> getClientSecretProperty() {return new FilterDAO.PropertyDAO<byte[]>(DOMAIN_GSERVICEACCOUNT.CLIENT_SECRET);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(DOMAIN_GSERVICEACCOUNT.DOMAIN);}
		@Override public Property<String> getEmailAddressProperty() {return new FilterDAO.PropertyDAO<String>(DOMAIN_GSERVICEACCOUNT.EMAIL_ADDRESS);}
		@Override public Property<String> getGoogleAccountProperty() {return new FilterDAO.PropertyDAO<String>(DOMAIN_GSERVICEACCOUNT.GOOGLE_ACCOUNT);}
		@Override public Property<Double> getLimitProperty() {return new FilterDAO.PropertyDAO<Double>(DOMAIN_GSERVICEACCOUNT.LIMIT);}
		@Override public Property<byte[]> getPrivateKeyProperty() {return new FilterDAO.PropertyDAO<byte[]>(DOMAIN_GSERVICEACCOUNT.PRIVATE_KEY);}
		@Override public Property<String> getPublicKeyProperty() {return new FilterDAO.PropertyDAO<String>(DOMAIN_GSERVICEACCOUNT.PUBLIC_KEY);}
		@Override public Property<Double> getSizeProperty() {return new FilterDAO.PropertyDAO<Double>(DOMAIN_GSERVICEACCOUNT.SIZE);}
	}	
	
	private static class FullDomainGserviceaccountFiller implements Function<DomainGserviceaccountRecord, DomainGserviceaccount> {
		@Override
		public DomainGserviceaccount apply(DomainGserviceaccountRecord r) {
			return new DomainGserviceaccount()
					.setClientId(r.getClientId())
					.setClientSecret(r.getClientSecret())
					.setDomainId(r.getDomain())
					.setEmailAddress(r.getEmailAddress())
					.setGoogleAccount(r.getGoogleAccount())
					.setLimit(r.getLimit())
					.setPrivateKey(r.getPrivateKey())
					.setPublicKey(r.getPublicKey())
					.setSize(r.getSize());
		}
	}
	
	private static class FullDomainFiller implements Function<DomainRecord, Domain> {
		@Override
		public Domain apply(DomainRecord r) {
			return new Domain()
					.setActive(r.getActive() == 1)
					.setChild(r.getParent() != null)
					.setDescription(r.getDescription())
					.setDomainType(DomainType.values()[r.getType()])
					.setEnableHeredity(r.getEnableheredity() == 1)
					.setId(r.getId())
					.setName(r.getName())
					.setParent(r.getParent() == null)
					.setParentId(r.getParent())
					//.setStandalone(¿standalone?)
					;
		}
	}
}
