package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.type.CategoryType;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CategoryPropertiesDAO;

public class CategoryDAO {

	private CategoryDAO() {
		  throw new IllegalStateException("Utility class");
    }
	
	private static final CategoryPropertiesDAO CATEGORY_PROPERTIES = new CategoryPropertiesDAO();
	
	public static Stream<Category> getStream(AONContext ctx, CategoryFilter filter) {
		return getStream(ctx, filter, Optional.empty(), Optional.empty());
	}
	
	public static Stream<Category> getStream(AONContext ctx, CategoryFilter filter, Integer page, Integer perPage) {
		return getStream(ctx, filter, Optional.of(page), Optional.of(perPage));
	}
	
	public static Category get(AONContext ctx, CategoryFilter filter) {
		return getStream(ctx, filter).findFirst().orElse(null);
	}

	public static Category save(AONContext ctx, Category category) {
		return category.getId()!=null && category.getId() !=0 ? update(ctx, category) : insert(ctx, category);
	}
	
	public static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		
		Integer nullvalue = null;
		
		ctx.getDslContext()
		.update(RATTACH)
		.set(RATTACH.CATEGORY, nullvalue)
		.where(RATTACH.CATEGORY.eq(id))
		.execute();

		delete(ctx, f -> f.getIdProperty().eq(id));
		
		ctx.log().debug("DELETE CATEGORY id: " + id);	
	}
	
	private static Stream<Category> getStream(AONContext ctx, CategoryFilter filter, Optional<Integer> page, Optional<Integer> perPage){
		ctx.checkRead();
		SelectConditionStep<Record> condition = ctx.getDslContext()
		.select().from(CATEGORY)
		.where(CATEGORY_PROPERTIES.getConditions(filter));

		if(page.isPresent() && perPage.isPresent()) {
			Integer per = perPage.get();
			Integer p = page.get();
			condition.limit(per).offset(per * (p -1));
		}
		
		return condition.fetchInto(CATEGORY).stream().map(new CategoryFiller());
	}
	
	private static Category insert(AONContext ctx, Category category){
		ctx.checkWrite();
		
		Integer id = ctx.getDslContext()
				.insertInto(CATEGORY)
				.set(CATEGORY.DOMAIN, category.getDomain())
				.set(CATEGORY.NAME, category.getName())
				.set(CATEGORY.DESCRIPTION, category.getDescription())
				.set(CATEGORY.SCOPE, category.getScope())
				.set(CATEGORY.URL, category.getUrl())
				.set(CATEGORY.TYPE, category.getType())
				.returning(CATEGORY.ID).fetchOne().getId();
		
		ctx.log().debug("INSERT CATEGORY id: " + id);		
		
		return category.setId(id);
	}
	
	private static Category update(AONContext ctx, Category category) {
		ctx.checkWrite();
		
		ctx.getDslContext().update(CATEGORY)
		.set(CATEGORY.NAME, category.getName())
		.set(CATEGORY.DESCRIPTION, category.getDescription())
		.set(CATEGORY.SCOPE, category.getScope())
		.set(CATEGORY.URL, category.getUrl())
		.where(CATEGORY.ID.eq(category.getId()))
		.execute();
		
		ctx.log().debug("UPDATE CATEGORY id: " + category.getId());		
		return category;
	}
	
	private static void delete(AONContext ctx, CategoryFilter filter) {
		ctx.checkWrite();
		
		ctx.getDslContext().delete(CATEGORY)
		.where(CATEGORY_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class CategoryFiller extends Filler implements Function<Record, Category> {
		
		public Category apply(Record r) {
			return build(r);
		}
	
		public static Category build(Record r) {
			return new Category()
					.setId(r.getValue(CATEGORY.ID))
					.setDescription(r.getValue(CATEGORY.DESCRIPTION))
					.setDomain(r.getValue(CATEGORY.DOMAIN))
					.setName(r.getValue(CATEGORY.NAME) ) // (String) r.getValue(DSL.field("title"))
					.setRattach(r.getValue(CATEGORY.RATTACH))
					.setScope(r.getValue(CATEGORY.SCOPE))
					.setCategoryType(CategoryType.safeValueOf(r.getValue(CATEGORY.TYPE)))
					.setUrl(r.getValue(CATEGORY.URL));
		}
	}	
}
