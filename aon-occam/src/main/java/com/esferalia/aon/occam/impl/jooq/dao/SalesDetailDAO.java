package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;
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
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DiscountExpression;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SalesDetailFilter;
import com.esferalia.aon.occam.api.model.Properties.SalesDetailProperties;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO.SalesFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class SalesDetailDAO {
	
	private static final SalesDetailPropertiesDAO SALES_DETAIL_PROPERTIES = new SalesDetailPropertiesDAO();

	private SalesDetailDAO() {
	
	}
	
	protected static class SalesDetailPropertiesDAO implements SalesDetailProperties {
		protected Condition[] getConditions(SalesDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.DOMAIN);}
		@Override public Property<Integer> getSalesProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.SALES);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.ITEM);}
		@Override public Property<Short> getLineProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.LINE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.DESCRIPTION);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.QUANTITY);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.PRICE);}
		@Override public Property<String> getDiscountExpressionProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.DISCOUNT_EXPR);}
		@Override public Property<Double> getTaxesProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.TAXES);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.STATUS);}
		@Override public Property<Integer> getOfferDetailProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.OFFER_DETAIL);}
		@Override public Property<Double> getDeliveredProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.DELIVERED);}
		@Override public Property<java.sql.Date> getDeliveryDateProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.DELIVERY_DATE);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.CARRIER);}
		@Override public Property<Integer> getCarrierPackingProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.CARRIER_PACKING);}
		@Override public Property<Integer> getDeliveryProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.DELIVERY);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(SALES_DETAIL.MODIFICATION_DATE);}
		
		@Override public Property<Integer> getProductProperty() {return new FilterDAO.PropertyDAO<>(ITEM.PRODUCT);}
	}
	
	
	private static SelectConditionStep<Record> select(AONContext ctx, SalesDetailFilter filter) {
		 return ctx.getDslContext().select().from(SALES_DETAIL)
			.join(ITEM).on(SALES_DETAIL.ITEM.eq(ITEM.ID))
			.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.join(SALES).on(SALES_DETAIL.SALES.eq(SALES.ID))
			.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(SALES.CUSTOMER))
			.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
			.where(SALES_DETAIL_PROPERTIES.getConditions(filter));
	}

	public static SalesDetail get(AONContext ctx, Integer salesDetailId){
		return get(ctx, f -> f.getIdProperty().eq(salesDetailId));
	}
	
	public static SalesDetail get(AONContext ctx, SalesDetailFilter filter){
		return select(ctx, filter).limit(1).fetch().stream().map(new SalesDetailFiller())
			.findFirst().orElse(new SalesDetail());
	}
	
	public static Stream<SalesDetail> getStream(AONContext ctx, SalesDetailFilter filter){
		return select(ctx, filter).fetch().stream().map(new SalesDetailFiller());
	}
	
	public static List<SalesDetail> getList(AONContext ctx, SalesDetailFilter filter){
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}

	public static List<SalesDetail> save(AONContext ctx, List<SalesDetail> details) {
		LinkedList<SalesDetail> list = new LinkedList<>();
		details.stream().forEach(salesDetail -> list.add(save(ctx, salesDetail)));
		return list;
	}
	
	public static SalesDetail save(AONContext ctx, SalesDetail salesDetail) {
		// TODO AUTOCOMPLETE & VALIDATE
		return salesDetail.getId() != null
			? update(ctx, salesDetail)
			: insert(ctx, salesDetail);
	}
	
	public static SalesDetail insert(AONContext ctx, SalesDetail salesDetail) {
		ctx.checkWrite();

		Integer id = ctx.getDslContext()
				.insertInto(SALES_DETAIL)
				.set(SALES_DETAIL.DOMAIN, salesDetail.getDomain())
				.set(SALES_DETAIL.SALES, salesDetail.getSales().getId())
				.set(SALES_DETAIL.LINE, salesDetail.getLine())
				.set(SALES_DETAIL.ITEM, salesDetail.getItem().getId())
				.set(SALES_DETAIL.DESCRIPTION, salesDetail.getDescription())
				.set(SALES_DETAIL.QUANTITY, salesDetail.getQuantity())
				.set(SALES_DETAIL.PRICE, salesDetail.getPrice())
				.set(SALES_DETAIL.DISCOUNT_EXPR, salesDetail.getDiscountExpression().getDiscountExpr())
				.set(SALES_DETAIL.TAXES, salesDetail.getTaxes())
				.set(SALES_DETAIL.STATUS, salesDetail.getStatus().value())
				.set(SALES_DETAIL.OFFER_DETAIL, salesDetail.getOfferDetail())
				.set(SALES_DETAIL.DELIVERED, salesDetail.getDelivered())
				.set(SALES_DETAIL.DELIVERY_DATE, AonDateUtils.toSql(salesDetail.getDeliveryDate()))
				.set(SALES_DETAIL.CARRIER, salesDetail.getCarrier().getId())
				.set(SALES_DETAIL.CARRIER_PACKING, salesDetail.getCarrierPacking())
				.set(SALES_DETAIL.DELIVERY, salesDetail.getDelivery())
				.set(SALES_DETAIL.CREATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(SALES_DETAIL.CREATION_USER, ctx.getUser())
				.set(SALES_DETAIL.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(SALES_DETAIL.MODIFICATION_USER, ctx.getUser())
				.returning(SALES_DETAIL.ID).fetchOne().getId();
			
		return salesDetail.setId(id);
	}

	public static SalesDetail update(AONContext ctx, SalesDetail salesDetail) {
		ctx.checkWrite();
		
		ctx.getDslContext()
				.update(SALES_DETAIL)
				.set(SALES_DETAIL.DOMAIN, salesDetail.getDomain())
				.set(SALES_DETAIL.SALES, salesDetail.getSales().getId())
				.set(SALES_DETAIL.LINE, salesDetail.getLine())
				.set(SALES_DETAIL.ITEM, salesDetail.getItem().getId())
				.set(SALES_DETAIL.DESCRIPTION, salesDetail.getDescription())
				.set(SALES_DETAIL.QUANTITY, salesDetail.getQuantity())
				.set(SALES_DETAIL.PRICE, salesDetail.getPrice())
				.set(SALES_DETAIL.DISCOUNT_EXPR, salesDetail.getDiscountExpression().getDiscountExpr())
				.set(SALES_DETAIL.TAXES, salesDetail.getTaxes())
				.set(SALES_DETAIL.STATUS, salesDetail.getStatus().value())
				.set(SALES_DETAIL.OFFER_DETAIL, salesDetail.getOfferDetail())
				.set(SALES_DETAIL.DELIVERED, salesDetail.getDelivered())
				.set(SALES_DETAIL.DELIVERY_DATE, AonDateUtils.toSql(salesDetail.getDeliveryDate()))
				.set(SALES_DETAIL.CARRIER, salesDetail.getCarrier().getId())
				.set(SALES_DETAIL.CARRIER_PACKING, salesDetail.getCarrierPacking())
				.set(SALES_DETAIL.DELIVERY, salesDetail.getDelivery())
				.set(SALES_DETAIL.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(SALES_DETAIL.MODIFICATION_USER, ctx.getUser())
				.where(SALES_DETAIL.ID.eq(salesDetail.getId()))
				.execute();
		return salesDetail;
	}
	
	public static void delete(AONContext ctx, Integer salesDetailId) {
		delete(ctx, f -> f.getIdProperty().eq(salesDetailId));
	}
	
	public static void delete(AONContext ctx, SalesDetailFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(SALES_DETAIL)
			.where(SALES_DETAIL_PROPERTIES.getConditions(filter))
			.execute();
	}

	public static class SalesDetailFiller extends Filler implements Function<Record, SalesDetail> {

		@Override
		public SalesDetail apply(Record r) {
			return build(r);
		}
	
		public static SalesDetail build(Record r) {
			return new SalesDetail()
				.setId(getValue(r, SALES_DETAIL.ID))
				.setDomain(getValue(r, SALES_DETAIL.DOMAIN))
				.setSales(checkField(r, SALES.ID) 
					? SalesFiller.build(r) 
					: new Sales().setId(getValue(r, SALES_DETAIL.SALES)))
				.setItem(checkField(r, ITEM.ID)
					? ItemFiller.build(r)
					: new Item().setId(getValue(r, SALES_DETAIL.ITEM)))
				.setLine(getValue(r, SALES_DETAIL.LINE))
				.setDescription(getValue(r, SALES_DETAIL.DESCRIPTION))
				.setQuantity(getValue(r, SALES_DETAIL.QUANTITY))
				.setPrice(getValue(r, SALES_DETAIL.PRICE))
				.setDiscountExpression(new DiscountExpression(getValue(r, SALES_DETAIL.DISCOUNT_EXPR)))
				.setTaxes(getValue(r, SALES_DETAIL.TAXES))
				.setStatus(SalesDetailStatus.safeValueOf(getValue(r, SALES_DETAIL.STATUS)))
				.setOfferDetail(getValue(r, SALES_DETAIL.OFFER_DETAIL))
				.setDelivered(getValue(r, SALES_DETAIL.DELIVERED))
				.setDeliveryDate(getValue(r, SALES_DETAIL.DELIVERY_DATE))
				.setCarrier(new Carrier().setId(getValue(r, SALES_DETAIL.CARRIER)))
				.setCarrierPacking(getValue(r, SALES_DETAIL.CARRIER_PACKING))
				.setDelivery(getValue(r, SALES_DETAIL.DELIVERY))
				
				;
			
		}
	}
}
