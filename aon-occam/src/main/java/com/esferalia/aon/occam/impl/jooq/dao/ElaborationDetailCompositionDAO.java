package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ElaborationDetailComposition.ELABORATION_DETAIL_COMPOSITION;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.ElaborationDetailCompositionProperties;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailCompositionFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ElaborationDetailCompositionDAO {
	
	private ElaborationDetailCompositionDAO() {
	
	}
	
	private static final ElaborationDetailCompositionPropertiesDAO ELABORATION_DETAIL_COMPOSITION_PROPERTIES = new ElaborationDetailCompositionPropertiesDAO();
	
	protected static class ElaborationDetailCompositionPropertiesDAO implements ElaborationDetailCompositionProperties {
		protected Condition[] getConditions(ElaborationDetailCompositionFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL_COMPOSITION.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL_COMPOSITION.DOMAIN);}
		@Override public Property<Integer> getElaborationDetailProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL_COMPOSITION.ELABORATION_DETAIL);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL_COMPOSITION.ITEM);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL_COMPOSITION.QUANTITY);}
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL_COMPOSITION.WAREHOUSE);}
		@Override public Property<String> getAddInfoProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL_COMPOSITION.ADD_INFO);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL_COMPOSITION.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL_COMPOSITION.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL_COMPOSITION.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION_DETAIL_COMPOSITION.MODIFICATION_DATE);}
	}

	public static List<ElaborationDetailComposition> getElaborationDetailCompositionList(
			AONContext ctx, Integer elaborationDetailId) {
		ctx.checkRead();
		return ctx
				.getDslContext()
				.select()
				.from(ELABORATION_DETAIL_COMPOSITION)
				.join(ITEM).on(ITEM.ID.eq(ELABORATION_DETAIL_COMPOSITION.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.where(ELABORATION_DETAIL_COMPOSITION.ELABORATION_DETAIL
						.eq(elaborationDetailId))
				.fetchInto(ELABORATION_DETAIL_COMPOSITION).stream()
				.map(new ElaborationDetailCompositionFiller())
				.collect(Collectors.toList());
	}
	
	public static List<ElaborationDetailComposition> getElaborationDetailCompositionList(
			AONContext ctx, ElaborationDetailCompositionFilter filter) {
		ctx.checkRead();
		return ctx
				.getDslContext()
				.select()
				.from(ELABORATION_DETAIL_COMPOSITION)
				.join(ITEM).on(ITEM.ID.eq(ELABORATION_DETAIL_COMPOSITION.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.where(ELABORATION_DETAIL_COMPOSITION_PROPERTIES.getConditions(filter))
				.and(ELABORATION_DETAIL_COMPOSITION.DOMAIN.eq(ctx.getDomainId()))
				.fetch().stream()
				.map(new ElaborationDetailCompositionFiller())
				.collect(Collectors.toList());
	}
	
	public static ElaborationDetailComposition getElaborationDetailComposition(
			AONContext ctx, Integer elaborationDetailCompositionId) {
		ctx.checkRead();
		return ctx
				.getDslContext()
				.select()
				.from(ELABORATION_DETAIL_COMPOSITION)
				.join(ITEM).on(ITEM.ID.eq(ELABORATION_DETAIL_COMPOSITION.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.where(ELABORATION_DETAIL_COMPOSITION.DOMAIN.eq(
						ctx.getDomainId()).and(
						ELABORATION_DETAIL_COMPOSITION.ID
								.eq(elaborationDetailCompositionId))).limit(1)
				.fetchInto(ELABORATION_DETAIL_COMPOSITION).stream()
				.map(new ElaborationDetailCompositionFiller()).findFirst()
				.orElse(new ElaborationDetailComposition());
	}
	
	public static int insertElaborationDetailComposition(AONContext ctx, ElaborationDetailComposition elaborationDetailComposition) {
		ctx.checkWrite();
		Timestamp now = AonDateUtils.toTimestamp(new Date());
		return ctx
				.getDslContext()
				.insertInto(ELABORATION_DETAIL_COMPOSITION,
						ELABORATION_DETAIL_COMPOSITION.DOMAIN,
						ELABORATION_DETAIL_COMPOSITION.ELABORATION_DETAIL,
						ELABORATION_DETAIL_COMPOSITION.ITEM,
						ELABORATION_DETAIL_COMPOSITION.QUANTITY,
						ELABORATION_DETAIL_COMPOSITION.WAREHOUSE,
						ELABORATION_DETAIL_COMPOSITION.ADD_INFO,
						ELABORATION_DETAIL_COMPOSITION.CREATION_USER,
						ELABORATION_DETAIL_COMPOSITION.CREATION_DATE,
						ELABORATION_DETAIL_COMPOSITION.MODIFICATION_USER,
						ELABORATION_DETAIL_COMPOSITION.MODIFICATION_DATE)
				.values(ctx.getDomainId(),
						elaborationDetailComposition.getElaborationDetail()
								.getId(),
						elaborationDetailComposition.getItem().getId(),
						elaborationDetailComposition.getQuantity(),
						elaborationDetailComposition.getWarehouse()!=null?
								elaborationDetailComposition.getWarehouse().getId():null,
						elaborationDetailComposition.getAddInfo(),
						ctx.getUser(), now, ctx.getUser(), now)
				.returning(ELABORATION_DETAIL_COMPOSITION.ID).fetchOne()
				.getId();
	}
	
	public static ElaborationDetailComposition updateElaborationDetailComposition(AONContext ctx,
			ElaborationDetailComposition elaborationDetailComposition) {
		ctx.checkWrite();
		Timestamp modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		return ctx
				.getDslContext()
				.update(ELABORATION_DETAIL_COMPOSITION)
				.set(ELABORATION_DETAIL_COMPOSITION.DOMAIN,
						ctx.getDomainId())
				.set(ELABORATION_DETAIL_COMPOSITION.ELABORATION_DETAIL,
						elaborationDetailComposition.getElaborationDetail()
								.getId())
				.set(ELABORATION_DETAIL_COMPOSITION.ITEM,
						elaborationDetailComposition.getItem().getId())
				.set(ELABORATION_DETAIL_COMPOSITION.QUANTITY,
						elaborationDetailComposition.getQuantity())
				.set(ELABORATION_DETAIL_COMPOSITION.WAREHOUSE,
						elaborationDetailComposition.getWarehouse()!=null?
								elaborationDetailComposition.getWarehouse().getId():null)
				.set(ELABORATION_DETAIL_COMPOSITION.ADD_INFO,
						elaborationDetailComposition.getAddInfo())
				.set(ELABORATION_DETAIL_COMPOSITION.MODIFICATION_USER,
						ctx.getUser())
				.set(ELABORATION_DETAIL_COMPOSITION.MODIFICATION_DATE,
						modificationDate)
				.where(ELABORATION_DETAIL_COMPOSITION.ID
						.eq(elaborationDetailComposition.getId()))
				.returning()
				.fetch().stream().map(new ElaborationDetailCompositionFiller()).findFirst()
				.orElse(null);
	}
	
	public static ElaborationDetailComposition deleteElaborationDetailComposition(AONContext ctx,
			ElaborationDetailCompositionFilter filter) {
		ctx.checkWrite();
		return ctx
				.getDslContext()
				.delete(ELABORATION_DETAIL_COMPOSITION)
				.where(ELABORATION_DETAIL_COMPOSITION_PROPERTIES
						.getConditions(filter))
				.and(ELABORATION_DETAIL_COMPOSITION.DOMAIN.eq(ctx.getDomainId()))
				.returning().fetch().stream().map(new ElaborationDetailCompositionFiller()).findFirst().orElse(null);
	}
	
	public static ElaborationDetailComposition deleteElaborationDetailComposition(AONContext ctx,
			ElaborationDetailComposition composition) {
		ctx.checkWrite();
		return ctx
				.getDslContext()
				.delete(ELABORATION_DETAIL_COMPOSITION)
				.where(ELABORATION_DETAIL_COMPOSITION.ID.eq(composition.getId()))
				.returning().fetch().stream().map(new ElaborationDetailCompositionFiller()).findFirst().orElse(null);
	}
		
	private static class ElaborationDetailCompositionFiller extends Filler implements Function<Record, ElaborationDetailComposition> {
		@Override
		public ElaborationDetailComposition apply(Record r) {
			return new ElaborationDetailComposition()
				.setId(r.getValue(ELABORATION_DETAIL_COMPOSITION.ID))
				.setDomain(r.getValue(ELABORATION_DETAIL_COMPOSITION.DOMAIN))
				.setElaborationDetail(new ElaborationDetail().setId(r.getValue(ELABORATION_DETAIL_COMPOSITION.ELABORATION_DETAIL)))
				.setItem(checkField(r, ITEM.ID)
					? ItemFiller.build(r)
					: new Item().setId(getValue(r, ELABORATION_DETAIL_COMPOSITION.ITEM)))
				.setQuantity(r.getValue(ELABORATION_DETAIL_COMPOSITION.QUANTITY))
				.setWarehouse(new Warehouse()
					.setId(r.getValue(ELABORATION_DETAIL_COMPOSITION.WAREHOUSE)))
					.setAddInfo(r.getValue(ELABORATION_DETAIL_COMPOSITION.ADD_INFO))
					.setCreationDate(r.getValue(ELABORATION_DETAIL_COMPOSITION.CREATION_DATE))
					.setCreationUser(r.getValue(ELABORATION_DETAIL_COMPOSITION.CREATION_USER))
					.setModificationDate(r.getValue(ELABORATION_DETAIL_COMPOSITION.MODIFICATION_DATE))
					.setModificationUser(r.getValue(ELABORATION_DETAIL_COMPOSITION.MODIFICATION_USER));
		}
	}

	
}
