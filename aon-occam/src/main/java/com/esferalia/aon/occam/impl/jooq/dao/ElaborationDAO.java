package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Elaboration.ELABORATION;
import static com.esferalia.aon.jooq.tables.ElaborationDetail.ELABORATION_DETAIL;
import static com.esferalia.aon.jooq.tables.ElaborationDetailComposition.ELABORATION_DETAIL_COMPOSITION;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.ElaborationDetailCompositionRecord;
import com.esferalia.aon.jooq.tables.records.ElaborationDetailRecord;
import com.esferalia.aon.jooq.tables.records.ElaborationRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.ElaborationDetailCompositionProperties;
import com.esferalia.aon.occam.api.model.ElaborationDetailProperties;
import com.esferalia.aon.occam.api.model.ElaborationProperties;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailCompositionFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.product.Item;

public class ElaborationDAO {
	
	private static final ElaborationPropertiesDAO ELABORATION_PROPERTIES = new ElaborationPropertiesDAO();
	private static final ElaborationDetailPropertiesDAO ELABORATION_DETAIL_PROPERTIES = new ElaborationDetailPropertiesDAO();
	private static final ElaborationDetailCompositionPropertiesDAO ELABORATION_DETAIL_COMPOSITION_PROPERTIES = new ElaborationDetailCompositionPropertiesDAO();
	
	
	protected static class ElaborationPropertiesDAO implements ElaborationProperties {
		protected Condition[] getConditions(ElaborationFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION.DOMAIN);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<String>(ELABORATION.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION.NUMBER);}
		@Override public Property<Timestamp> getDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(ELABORATION.DATE);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION.ITEM);}
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION.WAREHOUSE);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<Double>(ELABORATION.QUANTITY);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(ELABORATION.STATUS);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<String>(ELABORATION.COMMENTS);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<Byte>(ELABORATION.SOURCE);}
		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION.SOURCE_ID);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(ELABORATION.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(ELABORATION.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(ELABORATION.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(ELABORATION.MODIFICATION_DATE);}
	}

	protected static class ElaborationDetailPropertiesDAO implements ElaborationDetailProperties {
		protected Condition[] getConditions(ElaborationDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION_DETAIL.DOMAIN);}
		@Override public Property<Integer> getElaborationProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION_DETAIL.ELABORATION);}
		@Override public Property<Timestamp> getDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(ELABORATION_DETAIL.DATE);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION_DETAIL.ITEM);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<Double>(ELABORATION_DETAIL.QUANTITY);}
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION_DETAIL.WAREHOUSE);}
		@Override public Property<String> getAddInfoProperty() {return new FilterDAO.PropertyDAO<String>(ELABORATION_DETAIL.ADD_INFO);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(ELABORATION_DETAIL.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(ELABORATION_DETAIL.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(ELABORATION_DETAIL.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(ELABORATION_DETAIL.MODIFICATION_DATE);}
	}

	protected static class ElaborationDetailCompositionPropertiesDAO implements ElaborationDetailCompositionProperties {
		protected Condition[] getConditions(ElaborationDetailCompositionFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION_DETAIL_COMPOSITION.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION_DETAIL_COMPOSITION.DOMAIN);}
		@Override public Property<Integer> getElaborationDetailProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION_DETAIL_COMPOSITION.ELABORATION_DETAIL);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION_DETAIL_COMPOSITION.ITEM);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<Double>(ELABORATION_DETAIL_COMPOSITION.QUANTITY);}
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<Integer>(ELABORATION_DETAIL_COMPOSITION.WAREHOUSE);}
		@Override public Property<String> getAddInfoProperty() {return new FilterDAO.PropertyDAO<String>(ELABORATION_DETAIL_COMPOSITION.ADD_INFO);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(ELABORATION_DETAIL_COMPOSITION.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(ELABORATION_DETAIL_COMPOSITION.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(ELABORATION_DETAIL_COMPOSITION.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(ELABORATION_DETAIL_COMPOSITION.MODIFICATION_DATE);}
	}
	


	
	/*
	 * ELABORATION 
	 */
	
	public static List<Elaboration> getElaborationList(AONContext ctx,
			ElaborationFilter filter) {
		return ctx.getDslContext().select().from(ELABORATION)
				.where(ELABORATION_PROPERTIES.getConditions(filter))
				.fetchInto(ELABORATION).stream()
				.map(new FullElaborationFiller()).collect(Collectors.toList());
	}

	public static Elaboration getElaboration(AONContext ctx,
			Integer elaborationId) {
		return ctx.getDslContext().select().from(ELABORATION)
				.where(ELABORATION.ID.eq(elaborationId)).limit(1)
				.fetchInto(ELABORATION).stream()
				.map(new FullElaborationFiller()).findFirst()
				.orElse(new Elaboration());
	}
	
	public static int insertElaboration(AONContext ctx, Elaboration elaboration) {
		ctx.checkWrite();
		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		return ctx
				.getDslContext()
				.insertInto(ELABORATION, ELABORATION.DOMAIN,
						ELABORATION.SERIES, ELABORATION.NUMBER,
						ELABORATION.DATE, ELABORATION.ITEM,
						ELABORATION.WAREHOUSE, ELABORATION.QUANTITY,
						ELABORATION.STATUS, ELABORATION.COMMENTS,
						ELABORATION.SOURCE, ELABORATION.SOURCE_ID,
						ELABORATION.CREATION_USER, ELABORATION.CREATION_DATE,
						ELABORATION.MODIFICATION_USER,
						ELABORATION.MODIFICATION_DATE)
				.values(elaboration.getDomain(), elaboration.getSeries(),
						elaboration.getNumber(),
						new Timestamp(elaboration.getDate().getTime()),
						elaboration.getItem().getId(),
						elaboration.getWarehouse(), elaboration.getQuantity(),
						elaboration.getStatus(), elaboration.getComments(),
						elaboration.getSource(), elaboration.getSourceId(),
						ctx.getUser(), creationDate, ctx.getUser(),
						modificationDate).returning(ELABORATION.ID).fetchOne()
				.getId();
	}

	public static int updateElaboration(AONContext ctx, Elaboration elaboration) {
		ctx.checkWrite();
		Timestamp modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		return ctx.getDslContext()
				.update(ELABORATION)
				.set(ELABORATION.DOMAIN, elaboration.getDomain())
				.set(ELABORATION.SERIES, elaboration.getSeries())
				.set(ELABORATION.NUMBER, elaboration.getNumber())
				.set(ELABORATION.DATE,
						new Timestamp(elaboration.getDate().getTime()))
				.set(ELABORATION.ITEM, elaboration.getItem().getId())
				.set(ELABORATION.WAREHOUSE, elaboration.getWarehouse())
				.set(ELABORATION.QUANTITY, elaboration.getQuantity())
				.set(ELABORATION.STATUS, elaboration.getStatus())
				.set(ELABORATION.COMMENTS, elaboration.getComments())
				.set(ELABORATION.SOURCE, elaboration.getSource())
				.set(ELABORATION.SOURCE_ID, elaboration.getSourceId())
				.set(ELABORATION.MODIFICATION_USER, ctx.getUser())
				.set(ELABORATION.MODIFICATION_DATE, modificationDate)
				.where(ELABORATION.ID.eq(elaboration.getId())).execute();
	}

	public static int deleteElaboration(AONContext ctx,
			ElaborationFilter filter) {
		return ctx.getDslContext().delete(ELABORATION)
				.where(ELABORATION_PROPERTIES.getConditions(filter)).execute();
	}
	
	
	/*
	 * ELABORATION DETAIL
	 */
	
	public static List<ElaborationDetail> getElaborationDetailList(
			AONContext ctx, Integer elaborationId) {
		return ctx.getDslContext().select().from(ELABORATION_DETAIL)
				.where(ELABORATION_DETAIL.ELABORATION.eq(elaborationId))
				.fetchInto(ELABORATION_DETAIL).stream()
				.map(new FullElaborationDetailFiller())
				.collect(Collectors.toList());
	}

	public static List<ElaborationDetail> getElaborationDetailList(
			AONContext ctx, ElaborationDetailFilter filter) {
		return ctx.getDslContext().select().from(ELABORATION_DETAIL)
				.where(ELABORATION_DETAIL_PROPERTIES.getConditions(filter))
				.fetchInto(ELABORATION_DETAIL).stream()
				.map(new FullElaborationDetailFiller())
				.collect(Collectors.toList());
	}
			
	public static ElaborationDetail getElaborationDetail(AONContext ctx,
			Integer elaborationDetailId) {
		return ctx.getDslContext().select().from(ELABORATION_DETAIL)
				.where(ELABORATION_DETAIL.ID.eq(elaborationDetailId))
				.limit(1).fetchInto(ELABORATION_DETAIL).stream()
				.map(new FullElaborationDetailFiller()).findFirst()
				.orElse(new ElaborationDetail());
	}
	
	public static int insertElaborationDetail(AONContext ctx,
			ElaborationDetail elaborationDetail) {
		ctx.checkWrite();
		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		return ctx
				.getDslContext()
				.insertInto(ELABORATION_DETAIL, ELABORATION_DETAIL.DOMAIN,
						ELABORATION_DETAIL.ELABORATION,
						ELABORATION_DETAIL.DATE, ELABORATION_DETAIL.ITEM,
						ELABORATION_DETAIL.QUANTITY,
						ELABORATION_DETAIL.WAREHOUSE,
						ELABORATION_DETAIL.ADD_INFO,
						ELABORATION_DETAIL.CREATION_USER,
						ELABORATION_DETAIL.CREATION_DATE,
						ELABORATION_DETAIL.MODIFICATION_USER,
						ELABORATION_DETAIL.MODIFICATION_DATE)
				.values(elaborationDetail.getDomain(),
						elaborationDetail.getElaboration().getId(),
						new Timestamp(elaborationDetail.getDate().getTime()),
						elaborationDetail.getItem().getId(),
						elaborationDetail.getQuantity(),
						elaborationDetail.getWarehouse(),
						elaborationDetail.getAddInfo(), ctx.getUser(),
						creationDate, ctx.getUser(), modificationDate)
				.returning(ELABORATION_DETAIL.ID).fetchOne().getId();
	}
	
	public static int updateElaborationDetail(AONContext ctx,
			ElaborationDetail elaborationDetail) {
		ctx.checkWrite();
		Timestamp modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		return ctx.getDslContext()
				.update(ELABORATION_DETAIL)
				.set(ELABORATION_DETAIL.DOMAIN, elaborationDetail.getDomain())
				.set(ELABORATION_DETAIL.ELABORATION,
						elaborationDetail.getElaboration().getId())
				.set(ELABORATION_DETAIL.DATE,
						new Timestamp(elaborationDetail.getDate().getTime()))
				.set(ELABORATION_DETAIL.ITEM,
						elaborationDetail.getItem().getId())
				.set(ELABORATION_DETAIL.QUANTITY,
						elaborationDetail.getQuantity())
				.set(ELABORATION_DETAIL.WAREHOUSE,
						elaborationDetail.getWarehouse())
				.set(ELABORATION_DETAIL.ADD_INFO,
						elaborationDetail.getAddInfo())
				.set(ELABORATION_DETAIL.MODIFICATION_USER, ctx.getUser())
				.set(ELABORATION_DETAIL.MODIFICATION_DATE, modificationDate)
				.where(ELABORATION_DETAIL.ID.eq(elaborationDetail.getId()))
				.execute();
	}

	public static int deleteElaborationDetail(AONContext ctx,
			ElaborationDetailFilter filter) {
		return ctx.getDslContext().delete(ELABORATION_DETAIL)
				.where(ELABORATION_DETAIL_PROPERTIES.getConditions(filter))
				.execute();
	}
	
	
	/*
	 * ELABORATION DETAIL COMPOSITION
	 */
	public static List<ElaborationDetailComposition> getElaborationDetailCompositionList(
			AONContext ctx, Integer elaborationDetailId) {
		return ctx
				.getDslContext()
				.select()
				.from(ELABORATION_DETAIL_COMPOSITION)
				.where(ELABORATION_DETAIL_COMPOSITION.ELABORATION_DETAIL
						.eq(elaborationDetailId))
				.fetchInto(ELABORATION_DETAIL_COMPOSITION).stream()
				.map(new FullElaborationDetailCompositionFiller())
				.collect(Collectors.toList());
	}
	
	public static List<ElaborationDetailComposition> getElaborationDetailCompositionList(
			AONContext ctx, ElaborationDetailCompositionFilter filter) {
		return ctx
				.getDslContext()
				.select()
				.from(ELABORATION_DETAIL_COMPOSITION)
				.where(ELABORATION_DETAIL_COMPOSITION_PROPERTIES
						.getConditions(filter))
				.fetchInto(ELABORATION_DETAIL_COMPOSITION).stream()
				.map(new FullElaborationDetailCompositionFiller())
				.collect(Collectors.toList());
	}
	
	public static ElaborationDetailComposition getElaborationDetailComposition(
			AONContext ctx, Integer elaborationDetailCompositionId) {
		return ctx
				.getDslContext()
				.select()
				.from(ELABORATION_DETAIL_COMPOSITION)
				.where(ELABORATION_DETAIL_COMPOSITION.ID
						.eq(elaborationDetailCompositionId)).limit(1)
				.fetchInto(ELABORATION_DETAIL_COMPOSITION).stream()
				.map(new FullElaborationDetailCompositionFiller()).findFirst()
				.orElse(new ElaborationDetailComposition());
	}
	
	public static int insertElaborationDetailComposition(AONContext ctx,
			ElaborationDetailComposition elaborationDetailComposition) {
		ctx.checkWrite();
		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
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
				.values(elaborationDetailComposition.getDomain(),
						elaborationDetailComposition.getElaborationDetail()
								.getId(),
						elaborationDetailComposition.getItem().getId(),
						elaborationDetailComposition.getQuantity(),
						elaborationDetailComposition.getWarehouse(),
						elaborationDetailComposition.getAddInfo(),
						ctx.getUser(), creationDate, ctx.getUser(),
						modificationDate)
				.returning(ELABORATION_DETAIL_COMPOSITION.ID).fetchOne()
				.getId();
	}
	
	public static int updateElaborationDetailComposition(AONContext ctx,
			ElaborationDetailComposition elaborationDetailComposition) {
		ctx.checkWrite();
		Timestamp modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		return ctx
				.getDslContext()
				.update(ELABORATION_DETAIL_COMPOSITION)
				.set(ELABORATION_DETAIL_COMPOSITION.DOMAIN,
						elaborationDetailComposition.getDomain())
				.set(ELABORATION_DETAIL_COMPOSITION.ELABORATION_DETAIL,
						elaborationDetailComposition.getElaborationDetail()
								.getId())
				.set(ELABORATION_DETAIL_COMPOSITION.ITEM,
						elaborationDetailComposition.getItem().getId())
				.set(ELABORATION_DETAIL_COMPOSITION.QUANTITY,
						elaborationDetailComposition.getQuantity())
				.set(ELABORATION_DETAIL_COMPOSITION.WAREHOUSE,
						elaborationDetailComposition.getWarehouse())
				.set(ELABORATION_DETAIL_COMPOSITION.ADD_INFO,
						elaborationDetailComposition.getAddInfo())
				.set(ELABORATION_DETAIL_COMPOSITION.MODIFICATION_USER,
						ctx.getUser())
				.set(ELABORATION_DETAIL_COMPOSITION.MODIFICATION_DATE,
						modificationDate)
				.where(ELABORATION_DETAIL_COMPOSITION.ID
						.eq(elaborationDetailComposition.getId())).execute();
	}
	
	public static int deleteElaborationDetailComposition(AONContext ctx,
			ElaborationDetailCompositionFilter filter) {
		return ctx
				.getDslContext()
				.delete(ELABORATION_DETAIL_COMPOSITION)
				.where(ELABORATION_DETAIL_COMPOSITION_PROPERTIES
						.getConditions(filter)).execute();
	}
	

	
	public static int getSerieMaxNumber(AONContext ctx, String series) {
		try {
			return ctx.getDslContext().select(DSL.max(ELABORATION.NUMBER))
					.from(ELABORATION).fetchOne().value1();
		} catch (NullPointerException e) {
			return 0;
		}
	}
	
	
	/*
	 * FILLERS 
	 */
	
	private static class FullElaborationFiller implements
			Function<ElaborationRecord, Elaboration> {
		@Override
		public Elaboration apply(ElaborationRecord r) {
			return new Elaboration().setId(r.getId()).setDomain(r.getDomain())
					.setSeries(r.getSeries()).setNumber(r.getNumber())
					.setDate(r.getDate())
					.setItem(new Item().setId(r.getItem()))
					.setWarehouse(r.getWarehouse())
					.setQuantity(r.getQuantity()).setStatus(r.getStatus())
					.setComments(r.getComments()).setSource(r.getSource())
					.setSourceId(r.getSourceId())
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser());
		}
	}

	private static class FullElaborationDetailFiller implements
			Function<ElaborationDetailRecord, ElaborationDetail> {
		@Override
		public ElaborationDetail apply(ElaborationDetailRecord r) {
			return new ElaborationDetail()
					.setId(r.getId())
					.setDomain(r.getDomain())
					.setElaboration(new Elaboration().setId(r.getElaboration()))
					.setDate(r.getDate())
					.setItem(new Item().setId(r.getItem()))
					.setQuantity(r.getQuantity())
					.setWarehouse(r.getWarehouse()).setAddInfo(r.getAddInfo())
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser());
		}
	}

	private static class FullElaborationDetailCompositionFiller
			implements
			Function<ElaborationDetailCompositionRecord, ElaborationDetailComposition> {
		@Override
		public ElaborationDetailComposition apply(
				ElaborationDetailCompositionRecord r) {
			return new ElaborationDetailComposition()
					.setId(r.getId())
					.setDomain(r.getDomain())
					.setElaborationDetail(
							new ElaborationDetail().setId(r
									.getElaborationDetail()))
					.setItem(new Item().setId(r.getItem()))
					.setQuantity(r.getQuantity())
					.setWarehouse(r.getWarehouse()).setAddInfo(r.getAddInfo())
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser());
		}
	}

	
}
