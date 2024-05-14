package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ElaborationDetail.ELABORATION_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailProperties;
import com.esferalia.aon.occam.api.model.ElaborationDetailType;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO.WarehouseFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ElaborationDetailDAO {
	
	private ElaborationDetailDAO() {
	
	}
	
	private static final ElaborationDetailPropertiesDAO ELABORATION_DETAIL_PROPERTIES = new ElaborationDetailPropertiesDAO();

	protected static class ElaborationDetailPropertiesDAO implements ElaborationDetailProperties {
		protected Condition[] getConditions(ElaborationDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.DOMAIN);}
		@Override public Property<Integer> getElaborationProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.ELABORATION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.TYPE);}
		@Override public Property<Timestamp> getDateProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.DATE);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.ITEM);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.QUANTITY);}
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.WAREHOUSE);}
		@Override public Property<String> getAddInfoProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.ADD_INFO);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL.MODIFICATION_DATE);}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, ElaborationDetailFilter filter) {
		 return ctx.getDslContext().select()
			.from(ELABORATION_DETAIL)
			.join(ITEM).on(ITEM.ID.eq(ELABORATION_DETAIL.ITEM))
			.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
			.join(WAREHOUSE).on(WAREHOUSE.ID.eq(ELABORATION_DETAIL.WAREHOUSE))
			.where(ELABORATION_DETAIL_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<ElaborationDetail> getStream(AONContext ctx, ElaborationDetailFilter filter, Options... options){
		if(options.length > 0) 
			return getStream(ctx, filter, options[0]);
		return select(ctx, filter).fetch().stream().map(new ElaborationDetailFiller());
	}
	
	private static Stream<ElaborationDetail> getStream(AONContext ctx, ElaborationDetailFilter filter, Options options){
		if(options.isFull() && options.isPagination())
			return getFullStream(ctx, filter, options.getPage(), options.getPerPage());
		else if(options.isFull())
			return getFullStream(ctx, filter);
		else if(options.isPagination())
			return getStream(ctx, filter, options.getPage(), options.getPerPage());
		else return getStream(ctx, filter);
	}
	
	public static Stream<ElaborationDetail> getStream(AONContext ctx, ElaborationDetailFilter filter, Integer page, Integer perPage){
		return select(ctx, filter)
			.limit(perPage).offset(perPage * (page -1))
			.fetch().stream().map(new ElaborationDetailFiller());
	}

	public static Stream<ElaborationDetail> getFullStream(AONContext ctx, ElaborationDetailFilter filter, Integer page, Integer perPage){
		// TODO Cambiar el método de ElaborationDetailCompositionDAO
		return getStream(ctx, filter, page, perPage)
			.map(r -> r.setComposition(ElaborationDetailCompositionDAO
					.getElaborationDetailCompositionList(ctx, f-> f.getElaborationDetailProperty().eq(r.getId()))));
	}
	
	public static Stream<ElaborationDetail> getFullStream(AONContext ctx, ElaborationDetailFilter filter) {
		// TODO Cambiar el método de ElaborationDetailCompositionDAO
		return getStream(ctx, filter)
			.map(r -> r.setComposition(ElaborationDetailCompositionDAO
					.getElaborationDetailCompositionList(ctx, f-> f.getElaborationDetailProperty().eq(r.getId()))));
	}
	
	public static List<ElaborationDetail> getList(AONContext ctx, ElaborationDetailFilter filter, Options options){
		return getStream(ctx, filter, options).toList();
	}
	
	public static ElaborationDetail get(AONContext ctx, Integer id, Options... options){
		return get(ctx, f -> f.getIdProperty().eq(id), options);
	}
	
	public static ElaborationDetail get(AONContext ctx, ElaborationDetailFilter filter, Options... options){
		if(options.length > 0 && options[0].isFull())
			return getFull(ctx, filter);
		return select(ctx, filter).limit(1).fetch().stream().map(new ElaborationDetailFiller())
			.findFirst().orElse(new ElaborationDetail());
	}

	public static ElaborationDetail getFull(AONContext ctx, ElaborationDetailFilter filter){
		ElaborationDetail elaborationDetail = get(ctx, filter);
		// TODO Cambiar el método de ElaborationDetailCompositionDAO
		if(!elaborationDetail.isEmpty()) {
			elaborationDetail.setComposition(ElaborationDetailCompositionDAO
					.getElaborationDetailCompositionList(ctx, f-> f.getElaborationDetailProperty().eq(elaborationDetail.getId())));
		}
		return elaborationDetail;
	}

	public static ElaborationDetail save(AONContext ctx, ElaborationDetail elaborationDetail) {
		return elaborationDetail.getId() != null
			? update(ctx, elaborationDetail)
			: insert(ctx, elaborationDetail);
	}
	
	public static ElaborationDetail insert(AONContext ctx, ElaborationDetail elaborationDetail) {
		ctx.checkWrite();
		Timestamp now = AonDateUtils.toTimestamp(new Date());
		Integer id = ctx
				.getDslContext()
				.insertInto(ELABORATION_DETAIL, ELABORATION_DETAIL.DOMAIN,
						ELABORATION_DETAIL.ELABORATION,
						ELABORATION_DETAIL.DATE, ELABORATION_DETAIL.ITEM,
						ELABORATION_DETAIL.QUANTITY,
						ELABORATION_DETAIL.WAREHOUSE,
						ELABORATION_DETAIL.ADD_INFO,
						ELABORATION_DETAIL.TYPE,
						ELABORATION_DETAIL.CREATION_USER,
						ELABORATION_DETAIL.CREATION_DATE,
						ELABORATION_DETAIL.MODIFICATION_USER,
						ELABORATION_DETAIL.MODIFICATION_DATE)
				.values(ctx.getDomainId(),
						elaborationDetail.getElaboration().getId(),
						new Timestamp(elaborationDetail.getDate().getTime()),
						elaborationDetail.getItem().getId(),
						elaborationDetail.getQuantity(),
						elaborationDetail.getWarehouse()!=null?elaborationDetail.getWarehouse().getId():null,
						elaborationDetail.getAddInfo(), elaborationDetail.getType().value(), 
						ctx.getUser(), now, ctx.getUser(), now)
				.returning(ELABORATION_DETAIL.ID).fetchOne().getId();
		return elaborationDetail.setId(id);
	}
	
	public static ElaborationDetail update(AONContext ctx, ElaborationDetail elaborationDetail) {
		ctx.checkWrite();
		Timestamp modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		ctx.getDslContext()
				.update(ELABORATION_DETAIL)
				.set(ELABORATION_DETAIL.DOMAIN, ctx.getDomainId())
				.set(ELABORATION_DETAIL.ELABORATION,
						elaborationDetail.getElaboration().getId())
				.set(ELABORATION_DETAIL.TYPE, elaborationDetail.getType().value())
				.set(ELABORATION_DETAIL.DATE,
						new Timestamp(elaborationDetail.getDate().getTime()))
				.set(ELABORATION_DETAIL.ITEM,
						elaborationDetail.getItem().getId())
				.set(ELABORATION_DETAIL.QUANTITY,
						elaborationDetail.getQuantity())
				.set(ELABORATION_DETAIL.WAREHOUSE,
						elaborationDetail.getWarehouse()!=null?elaborationDetail.getWarehouse().getId():null)
				.set(ELABORATION_DETAIL.ADD_INFO,
						elaborationDetail.getAddInfo())
				.set(ELABORATION_DETAIL.MODIFICATION_USER, ctx.getUser())
				.set(ELABORATION_DETAIL.MODIFICATION_DATE, modificationDate)
				.where(ELABORATION_DETAIL.ID.eq(elaborationDetail.getId()))
				.execute();
		return elaborationDetail;
	}

	public static ElaborationDetail delete(AONContext ctx, ElaborationDetailFilter filter) {
		ctx.checkWrite();
		return ctx.getDslContext().delete(ELABORATION_DETAIL)
				.where(ELABORATION_DETAIL_PROPERTIES.getConditions(filter))
				.and(ELABORATION_DETAIL.DOMAIN.eq(ctx.getDomainId()))
				.returning().fetch().stream().map(new ElaborationDetailFiller()).findFirst().orElse(null);
	}

	public static ElaborationDetail delete(AONContext ctx, ElaborationDetail detail) {
		return delete(ctx, f -> f.getIdProperty().eq(detail.getId()));
	}
	
	public static ElaborationDetail delete(AONContext ctx, Integer id) {
		return delete(ctx, f -> f.getIdProperty().eq(id));
	}

	private static class ElaborationDetailFiller extends Filler implements Function<Record, ElaborationDetail> {
		@Override
		public ElaborationDetail apply(Record r) {
			return new ElaborationDetail()
				.setId(getValue(r, ELABORATION_DETAIL.ID))
				.setDomain(getValue(r, ELABORATION_DETAIL.DOMAIN))
				.setElaboration(new Elaboration().setId(getValue(r, ELABORATION_DETAIL.ELABORATION)))
				.setType(ElaborationDetailType.safeValueOf(getValue(r, ELABORATION_DETAIL.TYPE)))
				.setDate(getValue(r, ELABORATION_DETAIL.DATE))
				.setQuantity(getDouble(r, ELABORATION_DETAIL.QUANTITY))
				.setItem(checkField(r, ITEM.ID)
					? ItemFiller.build(r)
					: new Item().setId(getValue(r, ELABORATION_DETAIL.ITEM)))
				.setWarehouse(checkField(r, WAREHOUSE.ID)
					? WarehouseFiller.build(r)
					: new Warehouse().setId(getValue(r, ELABORATION_DETAIL.WAREHOUSE)))
				.setAddInfo(r.getValue(ELABORATION_DETAIL.ADD_INFO))
				.setCreationDate(getValue(r, ELABORATION_DETAIL.CREATION_DATE))
				.setCreationUser(getValue(r, ELABORATION_DETAIL.CREATION_USER))
				.setModificationDate(getValue(r, ELABORATION_DETAIL.MODIFICATION_DATE))
				.setModificationUser(getValue(r, ELABORATION_DETAIL.MODIFICATION_USER));
		}
	}

}
