package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.ProductCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.ProductCategoryProperties;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.impl.jooq.validation.ProductCategoryValidation;

public class ProductCategoryDAO {
	private static final ProductCategoryPropertiesDAO PRODUCT_CATEGORY_PROPERTIES = new ProductCategoryPropertiesDAO();

	protected static class ProductCategoryPropertiesDAO implements ProductCategoryProperties {
		protected Condition[] getConditions(ProductCategoryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PCATEGORY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PCATEGORY.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(PCATEGORY.NAME);}
		@Override public Property<String> getDetailProperty() {return new FilterDAO.PropertyDAO<String>(PCATEGORY.DETAIL);}
		@Override public Property<String> getDetail2Property() {return new FilterDAO.PropertyDAO<String>(PCATEGORY.DETAIL2);}
		@Override public Property<String> getDetail3Property() {return new FilterDAO.PropertyDAO<String>(PCATEGORY.DETAIL3);}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, ProductCategoryFilter filter) {
		return ctx.getDslContext()
				.select()
				.from(PCATEGORY)
				.where(PRODUCT_CATEGORY_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<ProductCategory> getStream(AONContext ctx, ProductCategoryFilter filter) {
		return select(ctx, filter)
		.fetch().stream().map(new ProductCategoryFiller());
	}

	public static ProductCategory get(AONContext ctx, ProductCategoryFilter filter) {
		return select(ctx, filter).limit(1)
		.fetch().stream().map(new ProductCategoryFiller())
		.findFirst().orElse(new ProductCategory());
	}
	
	public static ProductCategory save(AONContext ctx, ProductCategory productCategory) {
		ProductCategoryValidation.validate(ctx, productCategory);
		ProductCategory existProductCategory = get(ctx, f -> 
			f.getDomainProperty().eq(productCategory.getDomain())
			.and(f.getNameProperty().eq(productCategory.getName()))
			.and(f.getDetailProperty().eq(productCategory.getDetail()))
			.and(f.getDetail2Property().eq(productCategory.getDetail2()))
			.and(f.getDetail3Property().eq(productCategory.getDetail3())));
		if(productCategory.getId() == null && existProductCategory.getId() != null) productCategory.setId(existProductCategory.getId());
		return productCategory.getId() != null
			? update(ctx, productCategory)
			: insert(ctx, productCategory);
	}
	
	public static ProductCategory insert(AONContext ctx, ProductCategory productCategory) {
		ctx.checkWrite();
		
		Integer id = ctx.getDslContext().insertInto(PCATEGORY)
			.set(PCATEGORY.DOMAIN, productCategory.getDomain())
			.set(PCATEGORY.NAME, productCategory.getName())
			.set(PCATEGORY.DETAIL, productCategory.getDetail())
			.set(PCATEGORY.DETAIL2, productCategory.getDetail2())
			.set(PCATEGORY.DETAIL3, productCategory.getDetail3())
			.returning(PCATEGORY.ID).fetchOne().getValue(PCATEGORY.ID);
	
		return productCategory.setId(id);	
	}
	
	public static ProductCategory update(AONContext ctx, ProductCategory productCategory) {
		ctx.checkWrite();
		ctx.getDslContext().update(PCATEGORY)
		.set(PCATEGORY.DOMAIN, productCategory.getDomain())
		.set(PCATEGORY.NAME, productCategory.getName())
		.set(PCATEGORY.DETAIL, productCategory.getDetail())
		.set(PCATEGORY.DETAIL2, productCategory.getDetail2())
		.set(PCATEGORY.DETAIL3, productCategory.getDetail3())
		.where(PCATEGORY.ID.eq(productCategory.getId()))
		.execute();
	
		return productCategory;	
	}
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(PCATEGORY)
			.where(PCATEGORY.ID.equal(id))
			//.and(PCATEGORY.DOMAIN.eq(ctx.getDomainId()))
			.execute();
	}
	
	
	protected static class ProductCategoryFiller implements Function<Record, ProductCategory> {
		@Override
		public ProductCategory apply(Record r) {
			return build(r);			
		}
		
		public static ProductCategory build(Record r) {
			return new ProductCategory()
					.setId(r.getValue(PCATEGORY.ID))
					.setDomain(r.getValue(PCATEGORY.DOMAIN))
					.setName(r.getValue(PCATEGORY.NAME))
					.setDetail(r.getValue(PCATEGORY.DETAIL))
					.setDetail2(r.getValue(PCATEGORY.DETAIL2))
					.setDetail3(r.getValue(PCATEGORY.DETAIL3));
		}
		
	}
	
}
