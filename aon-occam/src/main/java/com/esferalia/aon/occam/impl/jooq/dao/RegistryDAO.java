package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rsegment.RSEGMENT;
import static com.esferalia.aon.jooq.tables.Rseller.RSELLER;
import static com.esferalia.aon.jooq.tables.Segment.SEGMENT;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.validation.RegistryAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.RegistryValidation;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class RegistryDAO {

	private RegistryDAO() {
	}
	
	protected static final RegistryPropertiesDAO REGISTRY_PROPERTIES = new RegistryPropertiesDAO();
	protected static class RegistryPropertiesDAO implements RegistryProperties {
		
		protected Condition[] getConditions(RegistryFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOMAIN);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.SECURITY_LEVEL);}
	}
	
	public static class RegistryFiller extends Filler implements Function<Record,Registry> {
		@Override
		public Registry apply(Record r) {
			return build(r, null);
		}

	    public static Registry build(Record r) {
	        return build(r, REGISTRY);
	    }
		
	    public static Registry build(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			if(registry == null) 
				registry = REGISTRY;
			return new Registry() 
					.setId(getValue(r, registry.ID))
					.setDomain(checkField(r, DOMAIN.ID) 
						? DomainFiller.build(r)
						: new Domain().setId(getValue(r, registry.DOMAIN)))
					.setDocument(getValue(r, registry.DOCUMENT))
					.setDocumentType(DocumentType.safeValueOf(getValue(r, registry.DOCUMENT_TYPE)))
					.setDocumentCountry(Country.safeValueOf(getValue(r, registry.DOCUMENT_COUNTRY)) )
					.setName(getValue(r, registry.NAME))
					.setAlias(getValue(r, registry.ALIAS))
					.setLegalPerson(AonEnumUtils.getBoolean(getValue(r, registry.TYPE)))
					.setNationality(Country.safeValueOf(getValue(r, registry.NATIONALITY)) )
					.setSecurityLevel(SecurityLevel.safeValueOf(getValue(r, registry.SECURITY_LEVEL)))
					.setDirty(false)
					;
		}
	}
	private static SelectConditionStep<Record> select(AONContext ctx, RegistryFilter filter) {
		return ctx.getDslContext()
				.select()
				.from(REGISTRY)
				.where(REGISTRY_PROPERTIES.getConditions(filter));
	}

	public static Stream<Registry> getStream(AONContext ctx, RegistryFilter filter){
		ctx.checkRead();
		return select(ctx, filter)
			.fetch()
			.stream()
			.map(new RegistryFiller());
	}
	
	public static Registry get(AONContext ctx, RegistryFilter filter){
		return getStream(ctx, filter).limit(1)
			.findFirst().orElse(new Registry());
	}
	
	public static Registry get(AONContext ctx, Integer id){
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}

	public static <R extends Registry> R save(AONContext ctx, R registry) {
		ctx.checkWrite();
		RegistryAutoComplete.autoComplete(ctx, registry);
		RegistryValidation.validate(ctx, registry);
		if (registry.isDirty()) {
			registry = (registry.getId() == null)?insert(ctx, registry):update(ctx, registry);
		} else {
			ctx.log().debug("NOT SAVED REGISTRY (not dirty) id: {0}",registry.getId());
		}
		return registry;
	}
	
	private static <R extends Registry> R insert(AONContext ctx, R registry) {
		Integer id = ctx.getDslContext().insertInto(REGISTRY)
			.set(REGISTRY.DOMAIN, registry.getDomain().getId())
			.set(REGISTRY.DOCUMENT,registry.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE,registry.getDocumentType()==null?null:registry.getDocumentType().value())
			.set(REGISTRY.DOCUMENT_COUNTRY, Country.safeIso2( registry.getDocumentCountry()))
			.set(REGISTRY.NAME,registry.getName())
			.set(REGISTRY.ALIAS,registry.getAlias())
			.set(REGISTRY.TYPE,AonEnumUtils.getByte(registry.isLegalPerson()))
			.set(REGISTRY.NATIONALITY, Country.safeIso2( registry.getNationality()))
			.set(REGISTRY.SECURITY_LEVEL,(registry.isConfidential()
					?SecurityLevel.CONFIDENTIAL.value()
					:SecurityLevel.OFFICIAL.value()))
			.returning(REGISTRY.ID)
			.fetchOne()
			.getValue(REGISTRY.ID);
		registry.setId(id);
		ctx.log().debug("INSERT REGISTRY id: {0}",registry.getId());
		return registry; 
	}

	private static <R extends Registry> R update(AONContext ctx, R registry) {
		int count = ctx.getDslContext().update(REGISTRY)
			.set(REGISTRY.DOCUMENT,registry.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE,registry.getDocumentType()==null?null:registry.getDocumentType().value())
			.set(REGISTRY.DOCUMENT_COUNTRY,Country.safeIso2( registry.getDocumentCountry()))
			.set(REGISTRY.NAME,registry.getName())
			.set(REGISTRY.ALIAS,registry.getAlias())
			.set(REGISTRY.TYPE,AonEnumUtils.getByte(registry.isLegalPerson()))
			.set(REGISTRY.NATIONALITY,Country.safeIso2( registry.getNationality()))
			.set(REGISTRY.SECURITY_LEVEL,(registry.isConfidential()
					?SecurityLevel.CONFIDENTIAL.value()
					:SecurityLevel.OFFICIAL.value()))
			.where(REGISTRY.ID.eq(registry.getId()))
			.execute();
		ctx.log().debug("UPDATE REGISTRY id: {0}. ({1} rows)", registry.getId(),count);
		return registry; 
	}
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		RegistryValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(REGISTRY)
			.where(REGISTRY.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE REGISTRY id: {0} ({1} rows)",id,count);
	}
	
	// *************************************************
	// ********** FULL REGISTRY *****************
	// *************************************************

	public static <R extends RegistryFull<?>> R fillChilds(AONContext ctx, R full){
		full.setBanks(RegistryBankDAO.getStream(ctx, f -> f.getRegistryProperty().eq(full.getRegistry().getId())).collect(Collectors.toCollection(LinkedList::new)))
			.setAddresses( RegistryAddressDAO.getStreamByRegistry(ctx, full.getId()).collect(Collectors.toCollection(LinkedList::new)))
			.setMedias( RegistryMediaDAO.getStreamByRegistry(ctx, full.getId()).collect(Collectors.toCollection(LinkedList::new)))
			.setRecordDatas(RegistryOldDAO.getRecordDataStream(ctx, f-> f.getRegistryProperty().eq(full.getId())).collect(Collectors.toCollection(LinkedList::new)));
		return full;
	}
	public static <R extends RegistryFull<?>> R saveChilds(AONContext ctx, R registryFull) {
		// Registry Bank
		if (registryFull.hasBanks()) {
			for (RegistryBank bank : registryFull.getBanks()) {
				if (bank.isRemoved()) {
					RegistryBankDAO.delete(ctx, bank.getId());
				} else if (bank.isDirty()) {
					bank.setRegistry(registryFull.getId());
					bank.setDomain(registryFull.getDomain());
					RegistryBankDAO.save(ctx, bank);
				}
			}
		}
		
		// Registry Addresses
		if (registryFull.hasAddresses()) {
			for (RegistryAddress address : registryFull.getAddresses()) {
				if (address.isRemoved()) {
					RegistryAddressDAO.delete(ctx, address.getId());
				} else if (address.isDirty()) {
					address.setRegistry(registryFull.getId());
					address.setDomain(registryFull.getDomain());
					RegistryAddressDAO.save(ctx, address);
				}
			}
		}
		
		// Registry Medias
		if (registryFull.hasMedias()) {
			for (RegistryMedia media : registryFull.getMedias()) {
				if (media.isRemoved()) {
					RegistryMediaDAO.delete(ctx, media.getId());
				} else if (media.isDirty()) {
					media.setRegistry(registryFull.getId());
					media.setDomain(registryFull.getDomain());
					RegistryMediaDAO.save(ctx, media);
				}
			}
		}
		return registryFull;
	}
	
	public static <R extends RegistryFull<?>> void delete(AONContext ctx, R registryFull) {
		// Registry Bank
		if (registryFull.hasBanks()) {
			for (RegistryBank bank : registryFull.getBanks()) {
				RegistryBankDAO.delete(ctx, bank.getId());
			}
		}
		
		// RegistryAddress ??
		
		// Registry Medias
		if (registryFull.hasMedias()) {
			for (RegistryMedia media : registryFull.getMedias()) {
				RegistryMediaDAO.delete(ctx, media.getId());
			}
		}
		// Registry 
		delete(ctx, registryFull.getId()); 
		
	}

	public static Stream<String> getRegistrySegmentNames(AONContext ctx, Integer registry) {
		return ctx.getDslContext()
			.select(SEGMENT.NAME)
			.from(SEGMENT)
			.join(RSEGMENT).on(RSEGMENT.SEGMENT.eq(SEGMENT.ID))
			.where(RSEGMENT.REGISTRY.eq(registry))
			.fetch(SEGMENT.NAME)
			.stream();
	}
	
	public static Stream<String> getRegistrySellerNames(AONContext ctx, Integer registry, java.util.Date date) {
		return ctx.getDslContext()
			.select(REGISTRY.NAME)
			.from(RSELLER)
			.leftOuterJoin(REGISTRY).on(RSELLER.SELLER.eq(REGISTRY.ID))
			.where( RSELLER.REGISTRY.eq(registry))
			.and( RSELLER.START_DATE.isNull().or( RSELLER.START_DATE.le( AonDateUtils.toSql (date) ) ) )
			.and( RSELLER.END_DATE.isNull().or( RSELLER.END_DATE.ge( AonDateUtils.toSql(date) ) ) )
			.and(RSELLER.STATUS.eq( (byte) 0))
			.fetch(REGISTRY.NAME)
			.stream()
		;
	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static Registry getRandom(AONContext ctx, RegistryFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new RegistryFiller())
			.findFirst()
			.orElse(null);
	}
	
}