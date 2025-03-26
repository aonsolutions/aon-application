package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Category.CATEGORY;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.Category;
import com.esferalia.aon.jooq.tables.CategoryTree;
import com.esferalia.aon.jooq.tables.Rattach;
import com.esferalia.aon.jooq.tables.Rdoc;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.S3CategoryFilter;
import com.esferalia.aon.occam.api.model.S3Category;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.S3CategoryPropertiesDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class S3CategoryDAO {
	
	private static final S3CategoryPropertiesDAO S3CATEGORY_PROPERTIES = new S3CategoryPropertiesDAO();
	
	private S3CategoryDAO() {
		  throw new IllegalStateException("Utility class");
	}
	
	public static Stream<S3Category> getStream(AONContext ctx, S3CategoryFilter filter, Optional<Integer> page, Optional<Integer> perPage) {
		ctx.checkRead();
		SelectConditionStep<Record> query = ctx.getDslContext()
				.select()
				.from(Category.CATEGORY)
				.leftJoin(CategoryTree.CATEGORY_TREE)
				.on(Category.CATEGORY.ID.eq(CategoryTree.CATEGORY_TREE.ID_CATEGORY))
				.where(S3CATEGORY_PROPERTIES.getConditions(filter));
		if(page.isPresent() && perPage.isPresent())
			return query.limit(perPage.get()).offset(perPage.get() * (page.get() - 1))
				.fetch()
				.stream()
				.map(new S3CategoryFiller());
		else
			return query
				.fetch()
				.stream()
				.map(new S3CategoryFiller());
	}
	
	public static long count(AONContext ctx, S3CategoryFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select(Category.CATEGORY.ID)
				.from(Category.CATEGORY)
				.join(CategoryTree.CATEGORY_TREE)
				.on(Category.CATEGORY.ID.eq(CategoryTree.CATEGORY_TREE.ID_CATEGORY))
				.where(S3CATEGORY_PROPERTIES.getConditions(filter)).fetch().stream().count();
	}
	
	public static S3Category insert(AONContext ctx, S3Category category){
		ctx.checkWrite();
		validate(ctx,category);
		Integer id = ctx.getDslContext()
				.insertInto(CATEGORY)
				.set(CATEGORY.DOMAIN, category.getDomain())
				.set(CATEGORY.NAME, category.getName())
				.set(CATEGORY.DESCRIPTION, category.getDescription())
				.set(CATEGORY.SCOPE, category.getScope())
				.set(CATEGORY.URL, category.getUrl())
				.set(CATEGORY.TYPE, (byte) 0)
				.set(CATEGORY.RATTACH, category.getRattach())
				.returning(CATEGORY.ID).fetchOne().getId();
		ctx.getDslContext()
				.insertInto(CategoryTree.CATEGORY_TREE)
				.set(CategoryTree.CATEGORY_TREE.ID_CATEGORY, id)
				.set(CategoryTree.CATEGORY_TREE.ID_PARENT, category.getParent())
				.set(CategoryTree.CATEGORY_TREE.IS_DELETABLE, (byte) 1)
				.set(CategoryTree.CATEGORY_TREE.IS_VISIBLE, (byte) 1)
				.execute()
				;
		return category.setId(id);
	}
	
	public static S3Category update(AONContext ctx, S3Category category){
		ctx.checkWrite();
		validate(ctx, category);
		ctx.getDslContext().update(CATEGORY)
			.set(CATEGORY.NAME, category.getName())
			.set(CATEGORY.DESCRIPTION, category.getDescription())
			.set(CATEGORY.SCOPE, category.getScope())
			.set(CATEGORY.URL, category.getUrl())
			.set(CATEGORY.RATTACH, category.getRattach())
			.where(CATEGORY.ID.eq(category.getId()))
			.execute();
		ctx.getDslContext().update(CategoryTree.CATEGORY_TREE)
			.set(CategoryTree.CATEGORY_TREE.IS_VISIBLE, category.getIsVisible())
			.where(CategoryTree.CATEGORY_TREE.ID_CATEGORY.eq(category.getId()))
			.execute();
		return category;
	}
	
	public static void delete(AONContext ctx, S3CategoryFilter filter){
		ctx.checkWrite();
		Optional<S3Category> cat = ctx.getDslContext().select()
				.from(CATEGORY)
				.leftJoin(CategoryTree.CATEGORY_TREE)
				.on(Category.CATEGORY.ID.eq(CategoryTree.CATEGORY_TREE.ID_CATEGORY))
				.where(S3CATEGORY_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new S3CategoryFiller()).findFirst();
		if(cat.isPresent() && cat.get().getIsDeletable() == 1) {
			ctx.getDslContext().delete(Category.CATEGORY)
					.where(S3CATEGORY_PROPERTIES.getConditions(filter))
					.execute();
			ctx.getDslContext().delete(CategoryTree.CATEGORY_TREE)
				.where(CategoryTree.CATEGORY_TREE.ID_CATEGORY.eq(cat.get().getId()))
				.execute();
			Integer nullvalue = null;
			ctx.getDslContext().update(Rattach.RATTACH)
				.set(Rattach.RATTACH.CATEGORY, nullvalue)
				.where(Rattach.RATTACH.CATEGORY.eq(cat.get().getId()))
				.execute();
			ctx.getDslContext().update(Rdoc.RDOC)
				.set(Rdoc.RDOC.CATEGORY, nullvalue)
				.where(Rdoc.RDOC.CATEGORY.eq(cat.get().getId()))
				.execute();	
		} else {
			throw new AonCoreException("No se encontro el elemento");
		}
	}
	
public static class S3CategoryFiller extends Filler implements Function<Record, S3Category> {
		
		public S3Category apply(Record r) {
			return build(r);
		}
	
		public static S3Category build(Record r) {
			return new S3Category()
					.setId(r.getValue(CATEGORY.ID))
					.setDescription(r.getValue(CATEGORY.DESCRIPTION))
					.setDomain(r.getValue(CATEGORY.DOMAIN))
					.setName(r.getValue(CATEGORY.NAME))
					.setRattach(r.getValue(CATEGORY.RATTACH))
					.setScope(r.getValue(CATEGORY.SCOPE))
					.setUrl(r.getValue(CATEGORY.URL))
					.setRattach(r.getValue(CATEGORY.RATTACH))
					.setParent(r.getValue(CategoryTree.CATEGORY_TREE.ID_PARENT))
					.setIsDeletable(r.getValue(CategoryTree.CATEGORY_TREE.IS_DELETABLE))
					.setIsVisible(r.getValue(CategoryTree.CATEGORY_TREE.IS_VISIBLE));
		}
	}
	
	public static final BiConsumer<AONContext, S3Category> IS_PARENT = (ctx, category) -> {
//		if (category.getDomain() != ctx.getDomainId())
//			throw new AonCoreException(AonError.WRITE_FORBIDDEN.format("operation"));
	};
	
	public static void validate(AONContext ctx, S3Category category) throws AonCoreException {
		IS_PARENT.accept(ctx, category);
	}
}
