package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RegistryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.validation.RegistryAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.RegistryValidation;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class RegistryDAO {
	private static final RegistryPropertiesDAO REGISTRY_PROPERTIES = new RegistryPropertiesDAO();
	
	public static class RegistryFiller  implements Function<Record,Registry> {
		@Override
		public Registry apply(Record record) {
			return new Registry()
				.setId(record.getValue(REGISTRY.ID))
				.setDomain(new Domain().setId(record.getValue(REGISTRY.DOMAIN)))
				.setAlias(record.getValue(REGISTRY.ALIAS))
				.setDocument(record.getValue(REGISTRY.DOCUMENT))
				.setDocumentType(DocumentType.safeValueOf(record.getValue(REGISTRY.DOCUMENT_TYPE)))
				.setDocumentCountry(Country.safeValueOf(record.getValue(REGISTRY.DOCUMENT_COUNTRY)))
				.setName(record.getValue(REGISTRY.NAME))
				.setNationality(Country.safeValueOf(record.getValue(REGISTRY.NATIONALITY)))
				.setSecurityLevel(SecurityLevel.safeValueOf( record.getValue(REGISTRY.SECURITY_LEVEL)))
				.setLegalPerson( AonEnumUtils.getBoolean( record.getValue(REGISTRY.TYPE) ))
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
	
	public static Registry get(AONContext ctx, Integer id){
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}

	public static <R extends Registry> R save(AONContext ctx, R registry) {
		return (registry.getId() == null)
			?insert(ctx, registry)
			:update(ctx, registry);
	}
	
	private static <R extends Registry> R insert(AONContext ctx, R registry) {
		ctx.checkWrite();
		RegistryAutoComplete.autoComplete(ctx, registry);
		RegistryValidation.validate(ctx, registry); 
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
		ctx.log().info("INSERT REGISTRY id: " + registry.getId());
		return registry; 
	}

	private static <R extends Registry> R update(AONContext ctx, R registry) {
		ctx.checkWrite();
		RegistryAutoComplete.autoComplete(ctx, registry);
		RegistryValidation.validate(ctx, registry); 
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
		ctx.log().info("UPDATE REGISTRY id: " + registry.getId() + ". (" + count + " rows)");
		return registry; 
	}
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		RegistryValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(REGISTRY)
			.where(REGISTRY.ID.eq(id))
			.execute();
		ctx.log().info("DELETE REGISTRY id:" + id + " ("+count+" rows)");
	}
	
	// *************************************************
	// ********** FULL REGISTRY *****************
	// *************************************************
	public static RegistryFull getFull(AONContext ctx, Integer id){
		Registry registry = getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
		RegistryFull full = null;
		if (registry != null) {
			full = new RegistryFull()
				.setRegistry(registry)
				.setAddresses( RegistryAddressDAO.getStreamByRegistry(ctx, registry.getId()).collect(Collectors.toCollection(LinkedList::new)))
				.setMedias( RegistryMediaDAO.getStreamByRegistry(ctx, registry.getId()).collect(Collectors.toCollection(LinkedList::new)))
			;
		}
		return full;
	}

	public static RegistryFull save(AONContext ctx, RegistryFull registryFull) {
		if (registryFull == null) throw new IllegalArgumentException("registryFull is null");
		if (registryFull.getRegistry() == null) throw new IllegalArgumentException("registryFull.registry is null");
		if (registryFull.isNew()) {
			ctx.log().info("INSERT REGISTRY FULL");
		} else {
			ctx.log().info("UPDATE REGISTRY FULL");
		}
		// Registry 
		registryFull.setRegistry( registryFull.isNew()
			?insert(ctx, registryFull.getRegistry())
			:update(ctx, registryFull.getRegistry())
		);
		// Registry Medias
		if (registryFull.hasMedias()) {
			for (RegistryMedia media : registryFull.getMedias()) {
				if (media.isDirty()) {
					media.setRegistry(registryFull.getId());
					RegistryMediaDAO.save(ctx, media);
				}
			}
		}
		RegistryFull ret = getFull(ctx, registryFull.getId()); 
		return ret;
	}
	
	public static void delete(AONContext ctx, RegistryFull registryFull) {
		// Registry Medias
		if (registryFull.hasMedias()) {
			for (RegistryMedia media : registryFull.getMedias()) {
				RegistryMediaDAO.delete(ctx, media.getId());
			}
		}
		// Registry 
		delete(ctx, registryFull.getId()); 
		
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
