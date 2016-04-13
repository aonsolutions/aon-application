package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.CategoryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class RegistryDAO {
	
	public static Category getCategory(AONContext ctx, Integer categoryId){
		return ctx.getDslContext()
				.select().from(CATEGORY).where(CATEGORY.ID.eq(categoryId)).limit(1)
				.fetchInto(CATEGORY).stream().map(new FullCategoryFiller()).findFirst().orElse(new Category());
	}
	
	public static LinkedList<Category> getCategoryList(AONContext ctx){
		return ctx.getDslContext()
				.select().from(CATEGORY).where(CATEGORY.DOMAIN.eq(ctx.getDomainId())).limit(1)
				.fetchInto(CATEGORY).stream().map(new FullCategoryFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Registry getRegistry(AONContext ctx, String name){
		return ctx.getDslContext().select().from(REGISTRY).where(REGISTRY.NAME.eq(name)).fetchInto(REGISTRY)
			.stream().map(new RegistryFiller()).findFirst().orElse(new Registry());
	}
	
	private static class FullCategoryFiller implements Function<CategoryRecord, Category> {
		@Override
		public Category apply(CategoryRecord r) {
			return new Category()
					.setDescription(r.getDescription())
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setName(r.getName())
					.setRattach(r.getRattach())
					.setScope(r.getScope())
					.setType(r.getType())
					.setUrl(r.getUrl());
		}
	}
	
	public static class RegistryFiller  implements Function<Record,Registry> {

		@Override
		public Registry apply(Record record) {
			return new Registry()
					.setId(record.getValue(REGISTRY.ID))
					.setDomain(record.getValue(REGISTRY.DOMAIN))
					.setAlias(record.getValue(REGISTRY.ALIAS))
					.setDocument(record.getValue(REGISTRY.DOCUMENT))
					.setDocumentType(DocumentType.safeValueOf(record.getValue(REGISTRY.DOCUMENT_TYPE)))
					.setDocumentCountry(Country.safeValueOf(record.getValue(REGISTRY.DOCUMENT_COUNTRY)))
					.setName(record.getValue(REGISTRY.NAME))
					.setNationality(Country.safeValueOf(record.getValue(REGISTRY.NATIONALITY)))
					.setSecurityLevel(SecurityLevel.safeValueOf( record.getValue(REGISTRY.SECURITY_LEVEL)))
					.setType(record.getValue(REGISTRY.TYPE))
				;
		}
		
	}
	
}
