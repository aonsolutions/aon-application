package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Elaboration.ELABORATION;
import static com.esferalia.aon.jooq.tables.ElaborationDetail.ELABORATION_DETAIL;
import static com.esferalia.aon.jooq.tables.ElaborationDetailComposition.ELABORATION_DETAIL_COMPOSITION;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;

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
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.ElaborationDetailCompositionProperties;
import com.esferalia.aon.occam.api.model.ElaborationDetailProperties;
import com.esferalia.aon.occam.api.model.ElaborationDetailType;
import com.esferalia.aon.occam.api.model.ElaborationProperties;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailCompositionFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.type.ElaborationSource;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO.WarehouseFiller;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ElaborationDAO {
	
	private ElaborationDAO() {
	
	}
	
	private static final ElaborationPropertiesDAO ELABORATION_PROPERTIES = new ElaborationPropertiesDAO();
	private static final ElaborationDetailPropertiesDAO ELABORATION_DETAIL_PROPERTIES = new ElaborationDetailPropertiesDAO();
	private static final ElaborationDetailCompositionPropertiesDAO ELABORATION_DETAIL_COMPOSITION_PROPERTIES = new ElaborationDetailCompositionPropertiesDAO();
	
	
	protected static class ElaborationPropertiesDAO implements ElaborationProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, ElaborationFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		protected Condition[] getConditions(ElaborationFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.DOMAIN);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.NUMBER);}
		@Override public Property<Timestamp> getDateProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.DATE);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.ITEM);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.DESCRIPTION);}
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.WAREHOUSE);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.QUANTITY);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.STATUS);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.COMMENTS);}
		@Override public Property<String> getRemarksProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.REMARKS);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.SOURCE);}
		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.SOURCE_ID);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(ELABORATION.MODIFICATION_DATE);}
	}

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

	/*
	 * ELABORATION 
	 */
	
	private static SelectConditionStep<Record> select(AONContext ctx, ElaborationFilter filter) {
		 return ctx.getDslContext().select()
			.from(ELABORATION)
			.leftOuterJoin(WAREHOUSE).on(WAREHOUSE.ID.eq(ELABORATION.WAREHOUSE))
			.where(ELABORATION_PROPERTIES.getConditions(filter));
	}

	public static Elaboration get(AONContext ctx, ElaborationFilter filter, Options... options){
		if(options.length > 0 && options[0].isFull())
			return getFull(ctx, filter);
		return select(ctx, filter).limit(1).fetch().stream().map(new ElaborationFiller())
			.findFirst().orElse(new Elaboration());
	}

	private static Elaboration getFull(AONContext ctx, ElaborationFilter filter){
		Elaboration elaboration = select(ctx, filter).limit(1).fetch().stream().map(new ElaborationFiller())
			.findFirst().orElse(new Elaboration());
		if(!elaboration.isEmpty()) {
			List<ElaborationDetail> details = getDetailFullStream(ctx, f -> f.getElaborationProperty().eq(elaboration.getId()))
					.collect(Collectors.toCollection(LinkedList::new));
			elaboration.setDetail(details.stream().filter(f-> ElaborationDetailType.ELABORATION.equals(f.getType()))
					.findFirst().orElse(new ElaborationDetail()));
			elaboration.setPackaging(details.stream().filter(f-> ElaborationDetailType.PACKAGING.equals(f.getType()))
					.collect(Collectors.toCollection(LinkedList::new)));
		}
		return elaboration;
	}
	
	public static Stream<Elaboration> getStream(AONContext ctx, ElaborationFilter filter, Options... options){
		if(options.length > 0) 
			return getStream(ctx, filter, options[0]);
		return select(ctx, filter).fetch().stream().map(new ElaborationFiller());
	}
	
	private static Stream<Elaboration> getStream(AONContext ctx, ElaborationFilter filter, Options options){
		if(options.isPagination())
			return getStream(ctx, filter, options.getPage(), options.getPerPage());
		else return getStream(ctx, filter);
	}
	
	public static Stream<Elaboration> getStream(AONContext ctx, ElaborationFilter filter, Integer page, Integer perPage) {
		return select(ctx, filter)
			.orderBy(ELABORATION.DATE.desc())
			.limit(perPage).offset(perPage * (page -1))
			.fetch().stream().map(new ElaborationFiller()); 
	}
	
	public static Stream<Elaboration> getElaborationStream(AONContext ctx, ElaborationFilter filter) {
		ctx.checkRead();
		SelectConditionStep<Record> select = (SelectConditionStep<Record>) (ELABORATION_PROPERTIES
				.build(ctx.getDslContext().select().from(ELABORATION)
						.leftJoin(WAREHOUSE)
						.on(WAREHOUSE.ID.eq(ELABORATION.WAREHOUSE)), filter));
		return select.orderBy(ELABORATION.DATE.desc()).fetch().stream()
				.map(new ElaborationFiller());
	}
	
	public static List<Elaboration> getList(AONContext ctx, ElaborationFilter filter, Options... options) {
		return getStream(ctx, filter, options)
			.collect(Collectors.toList());
	}
	
	public static List<Elaboration> getElaborationList(AONContext ctx, ElaborationFilter filter) {
		return getElaborationStream(ctx, filter)
				.collect(Collectors.toList());
	}

	public static Elaboration getElaboration(AONContext ctx, Integer elaborationId) {
		ctx.checkRead();
		return ctx.getDslContext().select().from(ELABORATION)
				.where(ELABORATION.ID.eq(elaborationId)).limit(1)
				.fetchInto(ELABORATION).stream()
				.map(new ElaborationFiller()).findFirst()
				.orElse(new Elaboration());
	}
	
	public static Elaboration getElaboration(AONContext ctx, String series, Integer number) {
		ctx.checkRead();
		return ctx.getDslContext().select().from(ELABORATION)
				.where(ELABORATION.SERIES.eq(series))
				.and(ELABORATION.NUMBER.eq(number))
				.and(ELABORATION.DOMAIN.eq(ctx.getDomainId())).limit(1)
				.fetchInto(ELABORATION).stream()
				.map(new ElaborationFiller()).findFirst()
				.orElse(new Elaboration());
	}
	
	public static Elaboration save(AONContext ctx, Elaboration elaboration) {
		return elaboration.getId() != null
			? updateElaboration(ctx, elaboration)
			: elaboration.setId(insertElaboration(ctx, elaboration));
	}

	
	public static int insertElaboration(AONContext ctx, Elaboration elaboration) {
		ctx.checkWrite();
		Timestamp now = AonDateUtils.toTimestamp(new Date());
		String series = elaboration.getSeries();
		int number = elaboration.getNumber() > 0 
				? elaboration.getNumber()
				: getNextNumber(ctx, series);
		return ctx
				.getDslContext()
				.insertInto(ELABORATION, ELABORATION.DOMAIN,
						ELABORATION.SERIES, ELABORATION.NUMBER,
						ELABORATION.DATE, ELABORATION.ITEM,
						ELABORATION.DESCRIPTION, ELABORATION.WAREHOUSE,
						ELABORATION.QUANTITY, ELABORATION.STATUS,
						ELABORATION.COMMENTS, ELABORATION.REMARKS,
						ELABORATION.SOURCE, ELABORATION.SOURCE_ID,
						ELABORATION.CREATION_USER, ELABORATION.CREATION_DATE,
						ELABORATION.MODIFICATION_USER,
						ELABORATION.MODIFICATION_DATE)
				.values(ctx.getDomainId(),
						series,
						number,
						new Timestamp(elaboration.getDate().getTime()),
						elaboration.getItem().getId(),
						elaboration.getDescription(),
						elaboration.getWarehouse() != null ? elaboration
								.getWarehouse().getId() : null,
						elaboration.getQuantity(), elaboration.getStatusValue(),
						elaboration.getComments(), elaboration.getRemarks(),
						elaboration.getSourceValue(), elaboration.getSourceId(),
						ctx.getUser(), now, ctx.getUser(), now)
				.returning(ELABORATION.ID).fetchOne()
				.getId();
	}

	public static Elaboration updateElaboration(AONContext ctx, Elaboration elaboration) {
		ctx.checkWrite();
		return ctx
				.getDslContext()
				.update(ELABORATION)
				.set(ELABORATION.DOMAIN, ctx.getDomainId())
				.set(ELABORATION.SERIES, elaboration.getSeries())
				.set(ELABORATION.NUMBER, elaboration.getNumber())
				.set(ELABORATION.DATE,
						new Timestamp(elaboration.getDate().getTime()))
				.set(ELABORATION.ITEM, elaboration.getItem().getId())
				.set(ELABORATION.DESCRIPTION, elaboration.getDescription())
				.set(ELABORATION.WAREHOUSE,
						elaboration.getWarehouse() != null ? elaboration
								.getWarehouse().getId() : null)
				.set(ELABORATION.QUANTITY, elaboration.getQuantity())
				.set(ELABORATION.STATUS, elaboration.getStatusValue())
				.set(ELABORATION.COMMENTS, elaboration.getComments())
				.set(ELABORATION.REMARKS, elaboration.getRemarks())
				.set(ELABORATION.SOURCE, elaboration.getSourceValue())
				.set(ELABORATION.SOURCE_ID, elaboration.getSourceId())
				.set(ELABORATION.MODIFICATION_USER, ctx.getUser())
				.set(ELABORATION.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.where(ELABORATION.ID.eq(elaboration.getId())).returning()
				.fetch().stream().map(new ElaborationFiller()).findFirst()
				.orElse(null);
	}

	public static Elaboration deleteElaboration(AONContext ctx, ElaborationFilter filter) {
		ctx.checkWrite();
		return ctx.getDslContext().delete(ELABORATION)
				.where(ELABORATION_PROPERTIES.getConditions(filter))
				.and(ELABORATION.DOMAIN.eq(ctx.getDomainId()))
				.returning().fetch().stream().map(new ElaborationFiller()).findFirst().orElse(null);
	}
	
	public static Elaboration deleteElaboration(AONContext ctx, Integer elaborationId) {
		return deleteElaboration(ctx, f -> f.getIdProperty().eq(elaborationId));
	}
	
	
	/*
	 * ELABORATION DETAIL
	 */
	
	private static SelectConditionStep<Record> selectDetail(AONContext ctx, ElaborationDetailFilter filter) {
		 return ctx.getDslContext().select()
			.from(ELABORATION_DETAIL)
			.join(ITEM).on(ITEM.ID.eq(ELABORATION_DETAIL.ITEM))
			.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
			.join(WAREHOUSE).on(WAREHOUSE.ID.eq(ELABORATION_DETAIL.WAREHOUSE))
			.where(ELABORATION_DETAIL_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<ElaborationDetail> getDetailStream(AONContext ctx, ElaborationDetailFilter filter, Options... options){
		if(options.length > 0) 
			return getDetailStream(ctx, filter, options[0]);
		return selectDetail(ctx, filter).fetch().stream().map(new ElaborationDetailFiller());
	}
	
	private static Stream<ElaborationDetail> getDetailStream(AONContext ctx, ElaborationDetailFilter filter, Options options){
		if(options.isFull() && options.isPagination())
			return getDetailFullStream(ctx, filter, options.getPage(), options.getPerPage());
		else if(options.isFull())
			return getDetailFullStream(ctx, filter);
		else if(options.isPagination())
			return getDetailStream(ctx, filter, options.getPage(), options.getPerPage());
		else return getDetailStream(ctx, filter);
	}
	
	public static Stream<ElaborationDetail> getDetailStream(AONContext ctx, ElaborationDetailFilter filter, Integer page, Integer perPage){
		return selectDetail(ctx, filter)
			.limit(perPage).offset(perPage * (page -1))
			.fetch().stream().map(new ElaborationDetailFiller());
	}

	public static Stream<ElaborationDetail> getDetailFullStream(AONContext ctx, ElaborationDetailFilter filter, Integer page, Integer perPage){
		return getDetailStream(ctx, filter, page, perPage)
			.map(r -> r.setComposition(getElaborationDetailCompositionList(ctx, f-> f.getElaborationDetailProperty().eq(r.getId()))));
	}
	
	public static Stream<ElaborationDetail> getDetailFullStream(AONContext ctx, ElaborationDetailFilter filter) {
		return getDetailStream(ctx, filter)
			.map(r -> r.setComposition(getElaborationDetailCompositionList(ctx, f-> f.getElaborationDetailProperty().eq(r.getId()))));
	}
	
	public static List<ElaborationDetail> getElaborationDetailList(
			AONContext ctx, Integer elaborationId) {
		ctx.checkRead();
		return ctx.getDslContext().select().from(ELABORATION_DETAIL)
				.where(ELABORATION_DETAIL.ELABORATION.eq(elaborationId)
					.and(ELABORATION_DETAIL.TYPE.eq(ElaborationDetailType.ELABORATION.value())))
				.orderBy(ELABORATION_DETAIL.DATE.desc())
				.fetchInto(ELABORATION_DETAIL).stream()
				.map(new ElaborationDetailFiller())
				.collect(Collectors.toList());
	}

	public static List<ElaborationDetail> getElaborationDetailList(
			AONContext ctx, ElaborationDetailFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext().select().from(ELABORATION_DETAIL)
				.where(ELABORATION_DETAIL_PROPERTIES.getConditions(filter))
				.and(ELABORATION_DETAIL.DOMAIN.eq(ctx.getDomainId()))
				.orderBy(ELABORATION_DETAIL.DATE.desc())
				.fetchInto(ELABORATION_DETAIL).stream()
				.map(new ElaborationDetailFiller())
				.collect(Collectors.toList());
	}

	public static ElaborationDetail getElaborationDetail(AONContext ctx, ElaborationDetailFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext().select().from(ELABORATION_DETAIL)
				.where(ELABORATION_DETAIL_PROPERTIES.getConditions(filter)).limit(1)
				.fetchInto(ELABORATION_DETAIL).stream()
				.map(new ElaborationDetailFiller()).findFirst()
				.orElse(new ElaborationDetail());
	}
	
	public static ElaborationDetail getElaborationDetail(AONContext ctx, Integer elaborationDetailId) {
		return getElaborationDetail(ctx, f -> f.getIdProperty().eq(elaborationDetailId));
	}
	
	public static int insertElaborationDetail(AONContext ctx,
			ElaborationDetail elaborationDetail) {
		ctx.checkWrite();
		Timestamp now = AonDateUtils.toTimestamp(new Date());
		return ctx
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
	}
	
	public static ElaborationDetail updateElaborationDetail(AONContext ctx,
			ElaborationDetail elaborationDetail) {
		ctx.checkWrite();
		Timestamp modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		return ctx
				.getDslContext()
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
				.returning()
				.fetch().stream().map(new ElaborationDetailFiller()).findFirst()
				.orElse(null);
	}

	public static ElaborationDetail deleteElaborationDetail(AONContext ctx,
			ElaborationDetailFilter filter) {
		ctx.checkWrite();
		return ctx.getDslContext().delete(ELABORATION_DETAIL)
				.where(ELABORATION_DETAIL_PROPERTIES.getConditions(filter))
				.and(ELABORATION_DETAIL.DOMAIN.eq(ctx.getDomainId()))
				.returning().fetch().stream().map(new ElaborationDetailFiller()).findFirst().orElse(null);
	}

	public static ElaborationDetail deleteElaborationDetail(AONContext ctx,
			ElaborationDetail detail) {
		return deleteElaborationDetail(ctx, f -> f.getIdProperty().eq(detail.getId()));
	}
	
	public static ElaborationDetail deleteElaborationDetail(AONContext ctx, Integer id) {
		return deleteElaborationDetail(ctx, f -> f.getIdProperty().eq(id));
	}
	
	
	/*
	 * ELABORATION DETAIL COMPOSITION
	 */
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
		
	public static int getNextNumber(AONContext ctx, String series ) {
		Integer next = ctx.getDslContext()
			.select( DSL.max(ELABORATION.NUMBER))
			.from(ELABORATION)
			.where(ELABORATION.DOMAIN.eq(ctx.getDomainId()))
			.and(AonStringUtils.isBlank(series)
					? ELABORATION.SERIES.isNull().or(DSL.trim(ELABORATION.SERIES).eq(""))
					: ELABORATION.SERIES.eq(series))
			.fetch()
			.stream()
			.mapToInt(rec -> (rec != null && rec.getValue(DSL.max(ELABORATION.NUMBER)) != null) 
					? rec.getValue(DSL.max(ELABORATION.NUMBER)) : 0)
			.findFirst()
			.orElse(0);
		if(next < 0) next = 0;
		return ++next;
	}
	
	/*
	 * FILLERS 
	 */
	
	public static class ElaborationFiller extends Filler implements Function<Record, Elaboration> {

		@Override
		public Elaboration apply(Record r) {
			return build(r);
		}
		
		public static Elaboration build(Record r) {
			return new Elaboration()
					.setId(getValue(r, ELABORATION.ID))
					.setDomain(getValue(r, ELABORATION.DOMAIN))
					.setSeries(getValue(r, ELABORATION.SERIES))
					.setNumber(getValue(r, ELABORATION.NUMBER))
					.setDate(getValue(r, ELABORATION.DATE))
					.setItem(checkField(r, ITEM.ID)
							? ItemFiller.build(r)
							: new Item().setId(getValue(r, ELABORATION.ITEM)))
					.setDescription(getValue(r, ELABORATION.DESCRIPTION))
					.setWarehouse(checkField(r, WAREHOUSE.ID)
							? WarehouseFiller.build(r)
							: new Warehouse().setId(getValue(r, ELABORATION.WAREHOUSE)))
					.setQuantity(getValue(r, ELABORATION.QUANTITY))
					.setStatus(ElaborationStatus.safeValueOf(getValue(r, ELABORATION.STATUS)))
					.setComments(getValue(r, ELABORATION.COMMENTS))
					.setRemarks(getValue(r, ELABORATION.REMARKS))
					.setSource(ElaborationSource.safeValueOf(getValue(r, ELABORATION.SOURCE)))
					.setSourceId(getValue(r, ELABORATION.SOURCE_ID))
					.setCreationDate(getValue(r, ELABORATION.CREATION_DATE))
					.setCreationUser(getValue(r, ELABORATION.CREATION_USER))
					.setModificationDate(getValue(r, ELABORATION.MODIFICATION_DATE))
					.setModificationUser(getValue(r, ELABORATION.MODIFICATION_USER));
		}
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
				.setWarehouse(
							new Warehouse().setId(r
									.getValue(ELABORATION_DETAIL_COMPOSITION.WAREHOUSE)))
					.setAddInfo(
							r.getValue(ELABORATION_DETAIL_COMPOSITION.ADD_INFO))
					.setCreationDate(
							r.getValue(ELABORATION_DETAIL_COMPOSITION.CREATION_DATE))
					.setCreationUser(
							r.getValue(ELABORATION_DETAIL_COMPOSITION.CREATION_USER))
					.setModificationDate(
							r.getValue(ELABORATION_DETAIL_COMPOSITION.MODIFICATION_DATE))
					.setModificationUser(
							r.getValue(ELABORATION_DETAIL_COMPOSITION.MODIFICATION_USER));
		}
	}

	
}
