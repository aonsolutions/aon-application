package com.esferalia.aon.occam.impl.jooq.dao.offer;

import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Offer.OFFER;
import static com.esferalia.aon.jooq.tables.OfferDetail.OFFER_DETAIL;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.OfferDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.OfferDetailProperties;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.type.OfferDetailStatus;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.OfferDAO.OfferFiller;

public class OfferDetailDAO {
	
	private OfferDetailDAO() {
	
	}
	
	private static final OfferDetailPropertiesDAO OFFER_DETAIL_PROPERTIES = new OfferDetailPropertiesDAO();
	public static class OfferDetailPropertiesDAO implements OfferDetailProperties {
		
		public Condition[] getConditions(OfferDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.DOMAIN);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.MODIFICATION_DATE);}
		@Override public Property<Integer> getOfferProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.OFFER);}
		@Override public Property<Short> getLineProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.LINE);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.ITEM);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.DESCRIPTION);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.QUANTITY);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.PRICE);}
		@Override public Property<String> getDiscountProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.DISCOUNT_EXPR);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL.STATUS);}
	}
	
	
	public static SelectConditionStep<Record> select(AONContext ctx, OfferDetailFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(OFFER_DETAIL)
				.where(OFFER_DETAIL_PROPERTIES.getConditions(filter));
	}
	
	public static SelectConditionStep<Record> selectWithOffer(AONContext ctx, OfferDetailFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(OFFER_DETAIL)
				.join(OFFER).on(OFFER_DETAIL.OFFER.eq(OFFER.ID))
				.leftOuterJoin(PROJECT).on(PROJECT.ID.eq(OFFER.PROJECT))
				.where(OFFER_DETAIL_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<OfferDetail> getStream(AONContext ctx, OfferDetailFilter filter){	
		return select(ctx, filter).fetch().stream().map(new OfferDetailFiller());
	}
	
	public static Stream<OfferDetail> getStream(AONContext ctx, OfferDetailFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter)
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch().stream().map(new OfferDetailFiller());
	}
	
	public static List<OfferDetail> getList(AONContext ctx, OfferDetailFilter filter){	
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static List<OfferDetail> getList(AONContext ctx, OfferDetailFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static OfferDetail get(AONContext ctx, Integer id) {
        return get(ctx, f -> f.getIdProperty().eq(id));
    }
	
	public static OfferDetail get(AONContext ctx, OfferDetailFilter filter) {
		return selectWithOffer(ctx, filter).limit(1)
			.fetch().stream().map(new OfferDetailFiller())
			.findFirst().orElse(new OfferDetail());
	}
	
	
	public static List<OfferDetail> save(AONContext ctx, List<OfferDetail> details) {
		LinkedList<OfferDetail> list = new LinkedList<>();
		details.stream().forEach(detail -> 
			list.add(save(ctx, detail))
		);
		return list;
	}
	
	public static OfferDetail save(AONContext ctx, OfferDetail detail) {
		OfferDetailValidation.validate(ctx, detail);
		detail = detail.getId() != null 
			? update(ctx, detail)
			: insert(ctx, detail);	
		return detail;
	}
	
	public static List<OfferDetail> update(AONContext ctx, List<OfferDetail> details) {
		details.stream().forEach(detail -> update(ctx, detail));
		return details;
	}
	
	public static OfferDetail update(AONContext ctx, OfferDetail detail) {
		ctx.getDslContext().update(OFFER_DETAIL)
		.set(OFFER_DETAIL.DOMAIN, detail.getDomain())
		.set(OFFER_DETAIL.OFFER, detail.getOffer().getId())
		.set(OFFER_DETAIL.LINE, detail.getLine())
		.set(OFFER_DETAIL.ITEM, detail.getItem().getId())
		.set(OFFER_DETAIL.DESCRIPTION, detail.getDescription())
		.set(OFFER_DETAIL.QUANTITY, detail.getQuantity())
		.set(OFFER_DETAIL.PRICE, detail.getPrice())
		.set(OFFER_DETAIL.DISCOUNT_EXPR, detail.getDiscountExpression())
		.set(OFFER_DETAIL.STATUS, detail.getStatus().value())
		.set(OFFER_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
		.set(OFFER_DETAIL.MODIFICATION_USER, ctx.getUser())
		.where(OFFER_DETAIL.ID.eq(detail.getId()))
		.execute();
		return detail;
	}
	
	public static OfferDetail insert(AONContext ctx, OfferDetail detail) {
		Integer id = ctx.getDslContext().insertInto(OFFER_DETAIL)
			.set(OFFER_DETAIL.DOMAIN, detail.getDomain())
			.set(OFFER_DETAIL.OFFER, detail.getOffer().getId())
			.set(OFFER_DETAIL.LINE, detail.getLine())
			.set(OFFER_DETAIL.ITEM, detail.getItem().getId())
			.set(OFFER_DETAIL.DESCRIPTION, detail.getDescription())
			.set(OFFER_DETAIL.QUANTITY, detail.getQuantity())
			.set(OFFER_DETAIL.PRICE, detail.getPrice())
			.set(OFFER_DETAIL.DISCOUNT_EXPR, detail.getDiscountExpression())
			.set(OFFER_DETAIL.STATUS, detail.getStatus().value())
			.set(OFFER_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.set(OFFER_DETAIL.MODIFICATION_USER, ctx.getUser())
			.set(OFFER_DETAIL.CREATION_USER ,ctx.getUser())
			.set(OFFER_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
			.set(OFFER_DETAIL.MODIFICATION_USER ,ctx.getUser())
			.set(OFFER_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(OFFER_DETAIL.ID).fetchOne().getId();
		return detail.setId(id);
	}	

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getIdProperty().eq(id)));
	}
	
	public static void delete(AONContext ctx, OfferDetailFilter filter){
		ctx.getDslContext().delete(OFFER_DETAIL)
		.where(OFFER_DETAIL_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class OfferDetailFiller extends Filler implements Function<Record, OfferDetail> {

		@Override
		public OfferDetail apply(Record r) {
			return build(r);
		}
		
		public static OfferDetail build(Record r) {
			return new OfferDetail()
				.setId(getValue(r, OFFER_DETAIL.ID))
				.setDomain(getValue(r, OFFER_DETAIL.DOMAIN))
				.setOffer(checkField(r, OFFER.ID)
					? OfferFiller.build(r)
					: new Offer().setId(getValue(r, OFFER_DETAIL.OFFER)))
				.setLine(getValue(r, OFFER_DETAIL.LINE))
				.setDescription(getValue(r, OFFER_DETAIL.DESCRIPTION))
				.setQuantity(getValue(r, OFFER_DETAIL.QUANTITY))
				.setPrice(getValue(r, OFFER_DETAIL.PRICE))
				.setDiscountExpression(getValue(r, OFFER_DETAIL.DISCOUNT_EXPR))
				.setItem(checkField(r, ITEM.ID)
					? ItemFiller.build(r)
					: new Item().setId(getValue(r, OFFER_DETAIL.ITEM)))
				.setStatus(OfferDetailStatus.safeValueOf(getValue(r, OFFER_DETAIL.STATUS)));
		}
		
	}
}
