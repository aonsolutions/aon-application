package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.BrandFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.BrandProperties;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.impl.jooq.validation.BrandValidation;

public class BrandDAO {

	private static final BrandPropertiesDAO BRAND_PROPERTIES = new BrandPropertiesDAO();
	protected static class BrandPropertiesDAO implements BrandProperties {
		
		protected Select<Record> build(SelectJoinStep<Record> select, BrandFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(BrandFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(BRAND.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(BRAND.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(BRAND.NAME);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, BrandFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(BRAND)
				.where(BRAND_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<Brand> getStream(AONContext ctx, BrandFilter filter){	
		return select(ctx, filter).fetch().stream().map(new BrandFiller());
	}
	
	public static Stream<Brand> getStream(AONContext ctx, BrandFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter)
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch().stream().map(new BrandFiller());
	}
	
	public static LinkedList<Brand> getList(AONContext ctx, BrandFilter filter){	
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Brand> getList(AONContext ctx, BrandFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Brand get(AONContext ctx, BrandFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new BrandFiller())
			.findFirst().orElse(new Brand());
	}
	
	public static Brand save(AONContext ctx, Brand brand) {
		BrandValidation.validate(ctx, brand);
		return brand.getId() != null 
			? update(ctx, brand)
			: insert(ctx, brand);
	}
	
	public static Brand update(AONContext ctx, Brand brand) {
		ctx.getDslContext().update(BRAND)
			.set(BRAND.DOMAIN, brand.getDomain())
			.set(BRAND.NAME, brand.getName())
			.where(BRAND.ID.eq(brand.getId()))	
			.execute();
		return brand;
	}
	
	public static Brand insert(AONContext ctx, Brand brand) {
		Integer id = ctx.getDslContext().insertInto(BRAND)
			.set(BRAND.DOMAIN, brand.getDomain())
			.set(BRAND.NAME, brand.getName())
			.returning(BRAND.ID).fetchOne().getId();
		return brand.setId(id);
	}	

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getIdProperty().eq(id)));
	}
	
	public static void delete(AONContext ctx, BrandFilter filter){
		ctx.getDslContext().delete(BRAND)
		.where(BRAND_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class BrandFiller implements Function<Record, Brand> {

		@Override
		public Brand apply(Record r) {
			return build(r);
		}
		
		public static Brand build(Record r) {
			return new Brand()
					.setId(r.getValue(BRAND.ID))
					.setDomain(r.getValue(BRAND.DOMAIN))
					.setName(r.getValue(BRAND.NAME));
		}
	}
}
