package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CUSTOMER_ALIAS;

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
import com.esferalia.aon.occam.api.model.Filter.DeliveryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.DeliveryDetailProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO.DeliveryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.validation.DeliveryDetailValidation;
import com.esferalia.aon.occam.impl.jooq.validation.DeliveryValidation;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DeliveryDetailDAO {
	
	private static final DeliveryDetailPropertiesDAO DELIVERY_DETAIL_PROPERTIES = new DeliveryDetailPropertiesDAO();

	private DeliveryDetailDAO() {
	
	}
	
	protected static class DeliveryDetailPropertiesDAO implements DeliveryDetailProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,DeliveryDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(DeliveryDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.DOMAIN);}
		@Override public Property<Integer> getDelivery() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.DELIVERY);}
		@Override public Property<Short> getLine() {return new FilterDAO.PropertyDAO<Short>(DELIVERY_DETAIL.LINE);}
		@Override public Property<Integer> getItem() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.ITEM);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.DESCRIPTION);}
		@Override public Property<Integer> getWarehouse() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.WAREHOUSE);}
		@Override public Property<Double> getQuantity() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.QUANTITY);}
		@Override public Property<Double> getPrice() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.PRICE);}
		@Override public Property<String> getDiscountExpressionProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.DISCOUNT_EXPR);}
		@Override public Property<Integer> getSalesDetail() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.SALES_DETAIL);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.MODIFICATION_USER);}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, DeliveryDetailFilter filter) {
		 return ctx.getDslContext().select().from(DELIVERY_DETAIL)
			.join(ITEM).on(DELIVERY_DETAIL.ITEM.eq(ITEM.ID))
			.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.join(DELIVERY).on(DELIVERY_DETAIL.DELIVERY.eq(DELIVERY.ID))
			.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(DELIVERY.CUSTOMER))
			.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
			.leftOuterJoin(PROJECT).on(PROJECT.ID.eq(DELIVERY.PROJECT))
			.where(DELIVERY_DETAIL_PROPERTIES.getConditions(filter));
	}

	public static DeliveryDetail getFull(AONContext ctx, Integer id){
		DeliveryDetail dd = get(ctx, f -> f.getIdProperty().eq(id));
		if(dd.getSalesDetail() != null) {
			dd.setSalesDetailData(SalesDetailDAO.get(ctx, dd.getSalesDetail()));
		}
		return dd;
	}
	
	public static DeliveryDetail get(AONContext ctx, Integer id){
		return get(ctx, f -> f.getIdProperty().eq(id));
	}
	
	public static DeliveryDetail get(AONContext ctx, DeliveryDetailFilter filter){
		return select(ctx, filter).limit(1).fetch().stream().map(new DeliveryDetailFiller())
			.findFirst().orElse(new DeliveryDetail());
	}
	
	public static Stream<DeliveryDetail> getStream(AONContext ctx, DeliveryDetailFilter filter){
		return select(ctx, filter).fetch().stream().map(new DeliveryDetailFiller());
	}
	
	public static List<DeliveryDetail> getList(AONContext ctx, DeliveryDetailFilter filter){
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}

	public static List<DeliveryDetail> save(AONContext ctx, Delivery delivery, List<DeliveryDetail> details) {
		LinkedList<DeliveryDetail> list = new LinkedList<>();
		details.stream().forEach(detail -> list.add(save(ctx, detail.setDelivery(delivery))));
		return list;
	}
	
	public static DeliveryDetail save(AONContext ctx, DeliveryDetail detail) {
		// TODO AUTOCOMPLETE & VALIDATE
		DeliveryDetailValidation.validate(ctx, detail);
		
		return detail.getId() != null
			? update(ctx, detail)
			: insert(ctx, detail);
	}
	
	public static DeliveryDetail insert(AONContext ctx, DeliveryDetail detail) {
		ctx.checkWrite();

		Integer id = ctx.getDslContext()
				.insertInto(DELIVERY_DETAIL)
				.set(DELIVERY_DETAIL.DOMAIN, detail.getDomain())
				.set(DELIVERY_DETAIL.DELIVERY, detail.getDelivery().getId())
				.set(DELIVERY_DETAIL.LINE, detail.getLine())
				.set(DELIVERY_DETAIL.ITEM, detail.getItem().getId())
				.set(DELIVERY_DETAIL.DESCRIPTION, detail.getDescription())
				.set(DELIVERY_DETAIL.WAREHOUSE, detail.getWarehouse())
				.set(DELIVERY_DETAIL.QUANTITY, detail.getQuantity())
				.set(DELIVERY_DETAIL.PRICE, detail.getPrice())
				.set(DELIVERY_DETAIL.DISCOUNT_EXPR, detail.getDiscountExpression())
				.set(DELIVERY_DETAIL.SALES_DETAIL, detail.getSalesDetail())
				.set(DELIVERY_DETAIL.CREATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(DELIVERY_DETAIL.CREATION_USER, ctx.getUser())
				.set(DELIVERY_DETAIL.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(DELIVERY_DETAIL.MODIFICATION_USER, ctx.getUser())
				.returning(DELIVERY_DETAIL.ID).fetchOne().getId();
			
		return detail.setId(id);
	}

	public static DeliveryDetail update(AONContext ctx, DeliveryDetail detail) {
		ctx.checkWrite();
		ctx.getDslContext()
				.update(DELIVERY_DETAIL)
				.set(DELIVERY_DETAIL.DOMAIN, detail.getDomain())
				.set(DELIVERY_DETAIL.DELIVERY, detail.getDelivery().getId())
				.set(DELIVERY_DETAIL.LINE, detail.getLine())
				.set(DELIVERY_DETAIL.ITEM, detail.getItem().getId())
				.set(DELIVERY_DETAIL.DESCRIPTION, detail.getDescription())
				.set(DELIVERY_DETAIL.WAREHOUSE, detail.getWarehouse())
				.set(DELIVERY_DETAIL.QUANTITY, detail.getQuantity())
				.set(DELIVERY_DETAIL.PRICE, detail.getPrice())
				.set(DELIVERY_DETAIL.DISCOUNT_EXPR, detail.getDiscountExpression())
				.set(DELIVERY_DETAIL.SALES_DETAIL, detail.getSalesDetail())
				.set(DELIVERY_DETAIL.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(DELIVERY_DETAIL.MODIFICATION_USER, ctx.getUser())
				.where(DELIVERY_DETAIL.ID.eq(detail.getId()))
				.execute();
		return detail;
	}
	
	public static void delete(AONContext ctx, Integer id) {
		delete(ctx, f -> f.getIdProperty().eq(id));
	}
	
	public static void delete(AONContext ctx, DeliveryDetailFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(DELIVERY_DETAIL)
			.where(DELIVERY_DETAIL_PROPERTIES.getConditions(filter))
			.execute();
	}

	public static class DeliveryDetailFiller extends Filler implements Function<Record, DeliveryDetail> {

		@Override
		public DeliveryDetail apply(Record r) {
			return build(r);
		}
		
		public static DeliveryDetail build(Record r) {
			return new DeliveryDetail()
				.setId(getValue(r, DELIVERY_DETAIL.ID))
				.setDomain(getInteger(r, DELIVERY_DETAIL.DOMAIN))
				.setDelivery(checkField(r, DELIVERY.ID)
					? DeliveryFiller.build(r)
					: new Delivery().setId(getValue(r, DELIVERY_DETAIL.DELIVERY)))
				.setLine(getShort(r, DELIVERY_DETAIL.LINE))
				.setItem(checkField(r, ITEM.ID)
					? ItemFiller.build(r)
					: new Item().setId(getValue(r, DELIVERY_DETAIL.ITEM)))
				.setDescription(getValue(r, DELIVERY_DETAIL.DESCRIPTION))
				.setWarehouse(getValue(r, DELIVERY_DETAIL.WAREHOUSE))
				.setQuantity(getDouble(r, DELIVERY_DETAIL.QUANTITY))
				.setPrice(getDouble(r, DELIVERY_DETAIL.PRICE))
				.setDiscountExpression(getValue(r, DELIVERY_DETAIL.DISCOUNT_EXPR))
				.setSalesDetail(getValue(r, DELIVERY_DETAIL.SALES_DETAIL))
				.setPurchaseReference(getValue(r, SALES.PURCHASE_REFERENCE))
				.setCreationDate(getValue(r, DELIVERY_DETAIL.CREATION_DATE))
				.setCreationUser(getValue(r, DELIVERY_DETAIL.CREATION_USER))
				.setModificationDate(getValue(r, DELIVERY_DETAIL.MODIFICATION_DATE))
				.setModificationUser(getValue(r, DELIVERY_DETAIL.MODIFICATION_USER));
		}
	}
}
