package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.registry.Registry;
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

	public static Stream<Registry> getStream(AONContext ctx, RegistryFilter filter){
		ctx.checkRead();
		return ctx.getDslContext()
			.select()
			.from(REGISTRY)
			.where(REGISTRY_PROPERTIES.getConditions(filter))
			.fetch()
			.stream()
			.map(new RegistryFiller());
	}
	
	public static Registry get(AONContext ctx, Integer id){
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}

	public static Registry save(AONContext ctx, Registry registry) {
		return (registry.getId() == null)
			?insert(ctx, registry)
			:update(ctx, registry);
	}
	
	public static Registry insert(AONContext ctx, Registry registry) {
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

	public static Registry update(AONContext ctx, Registry registry) {
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
	
	
}
