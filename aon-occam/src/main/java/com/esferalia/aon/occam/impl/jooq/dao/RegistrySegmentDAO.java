package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Rsegment.RSEGMENT;
import static com.esferalia.aon.jooq.tables.Segment.SEGMENT;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistrySegmentFilter;
import com.esferalia.aon.occam.api.model.Properties.RegistrySegmentProperties;
import com.esferalia.aon.occam.api.model.registry.RegistrySegment;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;

public class RegistrySegmentDAO {
	
	private RegistrySegmentDAO() {
		
	}
	
	private static final RegistrySegmentPropertiesDAO RSEGMENT_PROPERTIES = new RegistrySegmentPropertiesDAO();
	private static class RegistrySegmentPropertiesDAO implements RegistrySegmentProperties {
		
		private Condition[] getConditions(RegistrySegmentFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RSEGMENT.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RSEGMENT.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RSEGMENT.REGISTRY);}
		@Override public Property<Integer> getSegmentProperty() {return new FilterDAO.PropertyDAO<>(RSEGMENT.SEGMENT);}
	}


	
	private static SelectConditionStep<Record> select(AONContext ctx, RegistrySegmentFilter filter) {
		return ctx.getDslContext().select()
			.from(RSEGMENT)
			.innerJoin(DOMAIN).on(DOMAIN.ID.eq(RSEGMENT.DOMAIN))
			.innerJoin(SEGMENT).on(SEGMENT.ID.eq(RSEGMENT.SEGMENT))
			.where(RSEGMENT_PROPERTIES.getConditions(filter));
	}

	
	public static RegistrySegment get(AONContext ctx, RegistrySegmentFilter filter){
		return getStream(ctx, filter).findFirst().orElse(null);
	}
	
	public static Stream<RegistrySegment> getStream(AONContext ctx, RegistrySegmentFilter filter) {
		ctx.checkRead();
		return select(ctx,filter)
			.orderBy(RSEGMENT.ID.desc())
			.fetch()
			.stream()
			.map(new RegistrySegmentFiller());
	}
	
	public static RegistrySegment save(AONContext ctx, RegistrySegment rsegment){
		return rsegment.getId() == null
			? insert(ctx, rsegment)
			: update(ctx, rsegment);
	}
	
	private static RegistrySegment insert(AONContext ctx, RegistrySegment rsegment){
		Integer id = ctx.getDslContext().insertInto(RSEGMENT)
			.set(RSEGMENT.DOMAIN, rsegment.getDomain().getId())
			.set(RSEGMENT.SEGMENT, rsegment.getSegment().getId())
			.set(RSEGMENT.REGISTRY, rsegment.getRegistry())
			.returning(RSEGMENT.ID)
			.fetchOne()
			.getValue(RSEGMENT.ID);
		rsegment.setId(id);
		ctx.log().debug("INSERT REGISTRY SEGMENT DATA ( registry: {0}) id: {1}", rsegment.getRegistry(), rsegment.getId());
		return rsegment;
	}
	
	private static RegistrySegment update(AONContext ctx, RegistrySegment rsegment){
		int count = ctx.getDslContext().update(RSEGMENT)
			.set(RSEGMENT.DOMAIN, rsegment.getDomain().getId())
			.set(RSEGMENT.SEGMENT, rsegment.getSegment().getId())
			.set(RSEGMENT.REGISTRY, rsegment.getRegistry())
			.where(RSEGMENT.ID.eq(rsegment.getId()))
			.execute();
		ctx.log().debug("UPDATE REGISTRY SEGMENT ( registry: {0}) id: {1}. ({2} rows)", rsegment.getRegistry(), rsegment.getId(), count);
		return rsegment;
	}
	
	public static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RSEGMENT)
			.where(RSEGMENT.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE REGISTRY SEGMENT id: {0} ({1} rows)",id, count);
	}	
	
	public static void delete(AONContext ctx, RegistrySegmentFilter filter){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RSEGMENT)
			.where(RSEGMENT_PROPERTIES.getConditions(filter))
			.execute();
		ctx.log().debug("DELETE REGISTRY SEGMENT: ({0} rows)", count);
	}	
	
	public static class RegistrySegmentFiller implements Function<Record, RegistrySegment> {
		
		public RegistrySegment apply(Record r) {
			return build(r);
		}
				
		public RegistrySegment build(Record r) {
			return new RegistrySegment()
					.setId(r.getValue(RSEGMENT.ID))
					.setRegistry(r.getValue(RSEGMENT.REGISTRY))
					.setDomain(DomainFiller.build(r))
					.setSegment(buildSegment(r))
					;
		}
		
		private Segment buildSegment(Record r) {
			return new Segment()
				.setId(r.getValue(SEGMENT.ID))
				.setDomain(r.getValue(RSEGMENT.DOMAIN))
				.setName(r.getValue(SEGMENT.NAME))
			;	
		}
	}

}
