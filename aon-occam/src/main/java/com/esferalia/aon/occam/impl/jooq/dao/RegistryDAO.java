package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Category.CATEGORY;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.esferalia.aon.jooq.tables.records.CategoryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Category;

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
}
