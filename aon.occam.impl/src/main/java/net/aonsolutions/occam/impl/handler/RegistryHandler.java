package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.Filter.RegistryFilter;
import net.aonsolutions.occam.api.model.Properties.RegistryProperties;
import net.aonsolutions.occam.api.model.Registry;
import net.aonsolutions.occam.api.model.RegistryFull;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.SecurityLevel;
import net.aonsolutions.occam.impl.AONContext;

class RegistryHandler {
	
	private RegistryHandler() {
	}

	protected static final RegistryPropertiesHandler REGISTRY_PROPERTIES = new RegistryPropertiesHandler();
	protected static class RegistryPropertiesHandler implements RegistryProperties {
		
		protected Condition[] getConditions(RegistryFilter filter) {
			if (filter == null) return new Condition[0];
			FilterImpl filterDAO = (FilterImpl) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterImpl.PropertyDAO<>(REGISTRY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterImpl.PropertyDAO<>(REGISTRY.DOMAIN);}
		@Override public Property<String> getDocumentProperty() {return new FilterImpl.PropertyDAO<>(REGISTRY.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterImpl.PropertyDAO<>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterImpl.PropertyDAO<>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterImpl.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterImpl.PropertyDAO<>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getTypeProperty() {return new FilterImpl.PropertyDAO<>(REGISTRY.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterImpl.PropertyDAO<>(REGISTRY.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterImpl.PropertyDAO<>(REGISTRY.SECURITY_LEVEL);}
	}

	protected static class RegistryFiller extends Filler<Registry> {
		@Override
		public Registry apply(Record r) {
			return build(r);
		}

		static Registry build(Record r) {
	        return build(r, REGISTRY, Registry::new);
	    }
		
		static <T extends Registry> T build(Record r, com.esferalia.aon.jooq.tables.Registry registry, Supplier<T> sup ) {
    		T t = sup.get();
    		t.setId(getValue(r, registry.ID));
    		t.setDomain(getValue(r, registry.DOMAIN));
    		t.setDocument(getValue(r, registry.DOCUMENT));
    		t.setDocumentType(DocumentType.value(getValue(r, registry.DOCUMENT_TYPE)).orElse(null));
    		t.setDocumentCountry(Country.value(getValue(r, registry.DOCUMENT_COUNTRY)).orElse(null));
    		t.setName(getValue(r, registry.NAME));
    		t.setAlias(getValue(r, registry.ALIAS));
    		t.setLegalPerson(AonEnumUtils.getBoolean(getValue(r, registry.TYPE)));
    		t.setNationality(Country.value(getValue(r, registry.NATIONALITY)).orElse(null));
    		t.setConfidential(getBoolean(r, registry.SECURITY_LEVEL));
			return t;  
		}
	}
	
	protected static <R extends Registry> R save(AONContext ctx, R registry) {
		ctx.checkWrite();
		RegistryAutoComplete.autoComplete(ctx, registry);
		RegistryValidation.validate(ctx, registry);
		if (registry.getId() == null) {
			insert(ctx, registry);
		} else {
			update(ctx, registry);
		}
		return registry;
	}
	
	private static <R extends Registry> R insert(AONContext ctx, R registry) {
		Integer id = ctx.getDslContext().insertInto(REGISTRY)
			.set(REGISTRY.DOMAIN, registry.getDomain())
			.set(REGISTRY.DOCUMENT,registry.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE, DocumentType.value( registry.getDocumentType()))
			.set(REGISTRY.DOCUMENT_COUNTRY, Country.value( registry.getDocumentCountry()))
			.set(REGISTRY.NAME,registry.getName())
			.set(REGISTRY.ALIAS,registry.getAlias())
			.set(REGISTRY.TYPE,AonEnumUtils.getByte(registry.isLegalPerson()))
			.set(REGISTRY.NATIONALITY, Country.value( registry.getNationality()))
			.set(REGISTRY.SECURITY_LEVEL, SecurityLevel.value( registry.isConfidential()))
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
			.set(REGISTRY.DOCUMENT_TYPE, DocumentType.value( registry.getDocumentType()))
			.set(REGISTRY.DOCUMENT_COUNTRY,Country.value( registry.getDocumentCountry()))
			.set(REGISTRY.NAME,registry.getName())
			.set(REGISTRY.ALIAS,registry.getAlias())
			.set(REGISTRY.TYPE,AonEnumUtils.getByte(registry.isLegalPerson()))
			.set(REGISTRY.NATIONALITY,Country.value( registry.getNationality()))
			.set(REGISTRY.SECURITY_LEVEL, SecurityLevel.value( registry.isConfidential()))
			.where(REGISTRY.ID.eq(registry.getId()))
			.execute();
		ctx.log().debug("UPDATE REGISTRY id: {0}. ({1} rows)", registry.getId(),count);
		return registry; 
	}

	private class RegistryAutoComplete {
		
		private static final String AUTOCOMPLETE_DOCUMENT_TYPE = "\t saving registry: autocomplete document type: {0}";

		private RegistryAutoComplete() {
			
		}
		
		private static final BiConsumer<AONContext,Registry> COMPLETE_DOCUMENT_COUNTRY = (ctx,reg) -> {
			if (reg.getDocumentCountry() == null) {
				ctx.log().debug("\t saving registry: autocomplete document country: {0}",Country.ES.getIso2());
				reg.setDocumentCountry(Country.ES);
			}
		};

		private static final BiConsumer<AONContext,Registry> COMPLETE_DOCUMENT_TYPE = (ctx,reg) -> {
			if (reg.getDocumentCountry() == Country.ES 
				&& AonStringUtils.isNotBlank( reg.getDocument()) 
				&& (reg.getDocumentType() == null 
				 || reg.getDocumentType() == DocumentType.NIF 
				 || reg.getDocumentType() == DocumentType.CIF 
				 || reg.getDocumentType() == DocumentType.NIE)) {
					
				if ( AonDocumentUtil.isValidDNI( reg.getDocument() ) && reg.getDocumentType() != DocumentType.NIF) {
					reg.setDocumentType(DocumentType.NIF);
					ctx.log().debug(AUTOCOMPLETE_DOCUMENT_TYPE,DocumentType.NIF.getShortName());
				} else if ( AonDocumentUtil.isValidCIF( reg.getDocument() ) && reg.getDocumentType() != DocumentType.CIF) {
					reg.setDocumentType(DocumentType.CIF);
					ctx.log().debug(AUTOCOMPLETE_DOCUMENT_TYPE,DocumentType.CIF.getShortName());
				} else if ( AonDocumentUtil.isValidNIE( reg.getDocument() ) && reg.getDocumentType() != DocumentType.NIE) {
					reg.setDocumentType(DocumentType.NIE);
					ctx.log().debug(AUTOCOMPLETE_DOCUMENT_TYPE,DocumentType.NIE.getShortName());
				} 
			}
		};

		private static final BiConsumer<AONContext,Registry> COMPLETE_NATIONALITY = (ctx,reg) -> {
			if (reg.getNationality() == null) {
				ctx.log().debug("\t saving registry: autocomplete nationality: {0}",Country.ES.getIso2());
				reg.setNationality(Country.ES);
			}
		};

		private static final BiConsumer<AONContext,Registry> COMPLETE_LEGAL_ENTITY = (ctx,reg) -> {
			if (reg.getDocumentCountry()  == Country.ES) {
				boolean legalPerson = AonDocumentUtil.isEntity(reg.getDocument());
				if (legalPerson != reg.isLegalPerson()) {
					ctx.log().debug("\t saving registry: autocomplete legal person: {0} [{1}]",legalPerson,reg.getDocument());
					reg.setLegalPerson( legalPerson );
				}
			}
		};

		private static void autoComplete(AONContext ctx, Registry registry) throws AonCoreException {
			
			COMPLETE_DOCUMENT_COUNTRY
				.andThen(COMPLETE_DOCUMENT_TYPE)
				.andThen(COMPLETE_NATIONALITY)
				.andThen(COMPLETE_LEGAL_ENTITY)
				.accept(ctx, registry);

		}

	}
	
	private class RegistryValidation {
		private RegistryValidation() {
			
		}
		
		private static final BiConsumer<Registry,AONContext> EMPTY_DOMAIN = (reg,ctx) -> {
			if (reg.getDomain() == null ) throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		private static final BiConsumer<Registry,AONContext> OVERFLOW_DOCUMENT = (reg,ctx) -> {
			if (HandlerUtils.overflows(REGISTRY.DOCUMENT, reg.getDocument())) 
				throw new AonCoreException(AonError.REGISTRY_OVERFLOW_DOCUMENT.getMessage());
		};
		
		private static final BiConsumer<Registry,AONContext> OVERFLOW_NAME = (reg,ctx) -> {
			if (HandlerUtils.overflows(REGISTRY.NAME, reg.getName()))
				throw new AonCoreException(AonError.INVALID_LENGTH.format( "Nombre o raz\u00F3n social", REGISTRY.NAME.getDataType().length() ));
		};
		
		private static final BiConsumer<Registry,AONContext> OVERFLOW_ALIAS = (reg,ctx) -> {
			if (HandlerUtils.overflows(REGISTRY.ALIAS, reg.getAlias()))
				throw new AonCoreException(AonError.INVALID_LENGTH.format( "Alias", REGISTRY.ALIAS.getDataType().length() ));
		};
		
		private static void validate(AONContext ctx, Registry reg) throws AonCoreException{
			EMPTY_DOMAIN
			.andThen(OVERFLOW_DOCUMENT)
			.andThen(OVERFLOW_NAME)
			.andThen(OVERFLOW_ALIAS)
			.accept(reg, ctx);
		}

	}
	
	// *************************************************
	// ********** FULL REGISTRY *****************
	// *************************************************
	protected static <R extends RegistryFull<?>> R fillChilds(AONContext ctx, R full){
		RegistryBankHandler.streamByRegistry(ctx, full.getRegistry().getId()).forEach(full::addBank);
		RegistryAddressHandler.streamByRegistry(ctx, full.getId()).forEach(full::addAddress);
		RegistryMediaHandler.streamByRegistry(ctx, full.getId()).forEach(full::addMedia);
		return full;
	}
	
	protected static <R extends RegistryFull<?>> R saveChilds(AONContext ctx, R registryFull) {
		// Registry Banks
		registryFull.bankStream()
			.map(b ->  b.setRegistry(registryFull.getId()).setDomain(registryFull.getDomain()))
			.forEach(b -> RegistryBankHandler.save(ctx, b))
		;

		// Registry Addresses
		registryFull.addressStream()
			.map(b ->  b.setRegistry(registryFull.getId()).setDomain(registryFull.getDomain()))
			.forEach(b -> RegistryAddressHandler.save(ctx, b))
		;
		
		// Registry Medias
		registryFull.mediaStream()
			.map(b ->  b.setRegistry(registryFull.getId()).setDomain(registryFull.getDomain()))
			.forEach(b -> RegistryMediaHandler.save(ctx, b))
		;
		
		return registryFull;
	}
	
	// ***********************************************************************
	// ***********************************************************************
	// ***********************************************************************
	// ***********************************************************************
	// ***********************************************************************
/*	
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
	
	public static <R extends RegistryFull<?>> void delete(AONContext ctx, R registryFull) {
		// Registry Bank
		if (registryFull.hasBanks()) {
			for (RegistryBank bank : registryFull.getBanks()) {
				RegistryBankHandler.delete(ctx, bank.getId());
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
*/	

}
