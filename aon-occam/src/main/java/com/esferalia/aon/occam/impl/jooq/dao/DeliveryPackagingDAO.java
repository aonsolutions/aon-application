package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryPackaging.DELIVERY_PACKAGING;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Filter.DeliveryPackagingFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.DeliveryPackagingProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO.DeliveryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DeliveryPackagingDAO {
	
	private DeliveryPackagingDAO() {
	
	}
	
	private static final DeliveryPackagingPropertiesDAO DELIVERY_PACKAGING_PROPERTIES = new DeliveryPackagingPropertiesDAO();
	
	public static class DeliveryPackagingPropertiesDAO implements DeliveryPackagingProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, DeliveryPackagingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(DeliveryPackagingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_PACKAGING.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_PACKAGING.DOMAIN);}
		@Override public Property<Integer> getDeliveryProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_PACKAGING.DELIVERY);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_PACKAGING.ITEM);}

		@Override public Property<String> getCreationUserProperty() { return new FilterDAO.PropertyDAO<>(DELIVERY_PACKAGING.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() { return new FilterDAO.PropertyDAO<>(DELIVERY_PACKAGING.CREATION_DATE);}
 		@Override public Property<String> getModificationUserProperty() { return new FilterDAO.PropertyDAO<>(DELIVERY_PACKAGING.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() { return new FilterDAO.PropertyDAO<>(DELIVERY_PACKAGING.MODIFICATION_DATE); }
	}

	/*
	 * DELIVERY PACKAGING GET
	 */
	
	private static SelectConditionStep<Record> select(AONContext ctx, DeliveryPackagingFilter filter) {
		 return ctx.getDslContext().select()
			.from(DELIVERY_PACKAGING)
			.join(ITEM).on(ITEM.ID.eq(DELIVERY_PACKAGING.ITEM))
			.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
			.where(DELIVERY_PACKAGING_PROPERTIES.getConditions(filter));
	}

	public static DeliveryPackaging get(AONContext ctx, DeliveryPackagingFilter filter, Options... options){
		return select(ctx, filter).limit(1).fetch().stream().map(new DeliveryPackagingFiller())
			.findFirst().orElse(new DeliveryPackaging());
	}
	
	public static Stream<DeliveryPackaging> getStream(AONContext ctx, DeliveryPackagingFilter filter, Options... options){
		if(options.length > 0) 
			return getStream(ctx, filter, options[0]);
		return select(ctx, filter).fetch().stream().map(new DeliveryPackagingFiller());
	}
	
	private static Stream<DeliveryPackaging> getStream(AONContext ctx, DeliveryPackagingFilter filter, Options options){
		if(options.isPagination())
			return getStream(ctx, filter, options.getPage(), options.getPerPage());
		else return getStream(ctx, filter);
	}
	
	public static Stream<DeliveryPackaging> getStream(AONContext ctx, DeliveryPackagingFilter filter, Integer page, Integer perPage) {
		return select(ctx, filter)
			.limit(perPage).offset(perPage * (page -1))
			.fetch().stream().map(new DeliveryPackagingFiller()); 
	}
	
	public static List<DeliveryPackaging> getList(AONContext ctx, DeliveryPackagingFilter filter, Options... options) {
		return getStream(ctx, filter, options)
			.collect(Collectors.toList());
	}

	/*
	 * DELIVERY PACKAGING SET
	 */

	public static List<DeliveryPackaging> save(AONContext ctx, Delivery delivery, List<DeliveryPackaging> packagings) {
		LinkedList<DeliveryPackaging> list = new LinkedList<>();
		packagings.stream().forEach(packaging -> list.add(save(ctx, packaging.setDelivery(delivery))));
		return list;
	}
	
	public static DeliveryPackaging save(AONContext ctx, DeliveryPackaging packaging) {
		return packaging.getId() != null
			? update(ctx, packaging)
			: insert(ctx, packaging);
	}
	
	public static DeliveryPackaging insert(AONContext ctx, DeliveryPackaging packaging) {
		ctx.checkWrite();

		Integer id = ctx.getDslContext()
				.insertInto(DELIVERY_PACKAGING)
				.set(DELIVERY_PACKAGING.DOMAIN, packaging.getDomain())
				.set(DELIVERY_PACKAGING.DELIVERY, packaging.getDelivery().getId())
				.set(DELIVERY_PACKAGING.ITEM, packaging.getItem().getId())
				.set(DELIVERY_PACKAGING.CREATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(DELIVERY_PACKAGING.CREATION_USER, ctx.getUser())
				.set(DELIVERY_PACKAGING.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(DELIVERY_PACKAGING.MODIFICATION_USER, ctx.getUser())
				.returning(DELIVERY_PACKAGING.ID).fetchOne().getId();
			
		return packaging.setId(id);
	}

	public static DeliveryPackaging update(AONContext ctx, DeliveryPackaging packaging) {
		ctx.checkWrite();
		ctx.getDslContext()
				.update(DELIVERY_PACKAGING)
				.set(DELIVERY_PACKAGING.DOMAIN, packaging.getDomain())
				.set(DELIVERY_PACKAGING.DELIVERY, packaging.getDelivery().getId())
				.set(DELIVERY_PACKAGING.ITEM, packaging.getItem().getId())
				.set(DELIVERY_PACKAGING.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(DELIVERY_PACKAGING.MODIFICATION_USER, ctx.getUser())
				.where(DELIVERY_PACKAGING.ID.eq(packaging.getId()))
				.execute();
		return packaging;
	}
	
	/*
	 * DELIVERY PACKAGING DELETE
	 */
	
	public static void delete(AONContext ctx, Integer id) {
		delete(ctx, f -> f.getIdProperty().eq(id));
	}
	
	public static void delete(AONContext ctx, DeliveryPackagingFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(DELIVERY_PACKAGING)
			.where(DELIVERY_PACKAGING_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	
	/*
	 * FILLERS 
	 */

	public static class DeliveryPackagingFiller extends Filler implements Function<Record, DeliveryPackaging> {
		@Override
		public DeliveryPackaging apply(Record r) {
			return new DeliveryPackaging()
					.setId(getValue(r, DELIVERY_PACKAGING.ID))
					.setDomain(getValue(r, DELIVERY_PACKAGING.DOMAIN))
					.setDelivery(checkField(r, DELIVERY.ID)
							? DeliveryFiller.build(r)
							: new Delivery().setId(getValue(r, DELIVERY_PACKAGING.DELIVERY)))
					.setItem(checkField(r, ITEM.ID)
							? ItemFiller.build(r)
							: new Item().setId(getValue(r, DELIVERY_PACKAGING.ITEM)))
					;
		}
	}

	
}
