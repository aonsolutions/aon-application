package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.TaskHolderWorkgroup.TASK_HOLDER_WORKGROUP;
import static com.esferalia.aon.jooq.tables.UserWorkgroup.USER_WORKGROUP;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.WorkgroupFilter;
import com.esferalia.aon.occam.api.model.Properties.WorkgroupProperties;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.type.WorkgroupStatus;

public class WorkgroupDAO {
	
	private WorkgroupDAO() {
		
	}
	
	private static final WorkgroupPropertiesDAO WORKGROUP_PROPERTIES = new WorkgroupPropertiesDAO();

	protected static class WorkgroupPropertiesDAO implements WorkgroupProperties {
		protected Condition[] getConditions(WorkgroupFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(WORKGROUP.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(WORKGROUP.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(WORKGROUP.DESCRIPTION);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(WORKGROUP.STATUS);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, WorkgroupFilter filter) {
		return ctx.getDslContext().select().from(WORKGROUP).where(WORKGROUP_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<Workgroup> getStream(AONContext ctx, WorkgroupFilter filter){
		return select(ctx, filter).fetch().stream().map(new WorkgroupFiller());
	}

	public static List<Workgroup> getList(AONContext ctx, WorkgroupFilter filter) {
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Workgroup get(AONContext ctx, WorkgroupFilter filter) {
		return select(ctx, filter).limit(1).fetch().stream().map(new WorkgroupFiller()).findFirst().orElse(new Workgroup());
	}
	
	public static Workgroup save(AONContext ctx, Workgroup workgroup) {
		workgroup.setStatus(workgroup.getStatus() !=null ? workgroup.getStatus() : WorkgroupStatus.ACTIVE);
		return workgroup.getId() != null
				? update(ctx, workgroup)
				: insert(ctx, workgroup);
	}
	
	
	public static Stream<Workgroup> getWorkgroupByTaskHolderStream(AONContext ctx, WorkgroupFilter filter, Integer taskHolder){
		return ctx.getDslContext().select().from(WORKGROUP).join(TASK_HOLDER_WORKGROUP).on(TASK_HOLDER_WORKGROUP.WORKGROUP.eq(WORKGROUP.ID))
				.where(WORKGROUP_PROPERTIES.getConditions(filter))
				.and(TASK_HOLDER_WORKGROUP.TASK_HOLDER.eq(taskHolder))
				.fetch().stream().map(new WorkgroupFiller());
	}
	
	public static Workgroup insert(AONContext ctx, Workgroup workgroup) {
		Integer id = ctx.getDslContext().insertInto(WORKGROUP)
				.set(WORKGROUP.DOMAIN, workgroup.getDomain())
				.set(WORKGROUP.DESCRIPTION, workgroup.getDescription())
				.set(WORKGROUP.STATUS, workgroup.getStatus().value())
				.returning(WORKGROUP.ID).fetchOne().getValue(WORKGROUP.ID);
		ctx.log().debug("INSERT WORKGROUP id: " +id);	
		return workgroup.setId(id);
	}
	
	public static Workgroup update(AONContext ctx, Workgroup workgroup) {
		ctx.getDslContext().update(WORKGROUP)
				.set(WORKGROUP.DOMAIN, workgroup.getDomain())
				.set(WORKGROUP.DESCRIPTION, workgroup.getDescription())
				.set(WORKGROUP.STATUS, workgroup.getStatus().value())
				.where(WORKGROUP.ID.eq(workgroup.getId())).execute();
		ctx.log().debug("UPDATE WORKGROUP id:" + workgroup.getId());
		return workgroup;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.getDslContext().delete(USER_WORKGROUP).where(USER_WORKGROUP.WORKGROUP.eq(id)).execute();
		ctx.getDslContext().delete(TASK_HOLDER_WORKGROUP).where(TASK_HOLDER_WORKGROUP.WORKGROUP.eq(id)).execute();
		ctx.getDslContext().delete(WORKGROUP).where(WORKGROUP.ID.eq(id)).execute();
		ctx.log().debug("DELETE WORKGROUP id:" + id);
	}

	public static class WorkgroupFiller implements Function<Record, Workgroup> {

		@Override
		public Workgroup apply(Record r) {
			return build(r);
		}

		public static Workgroup build(Record r) {
			return new Workgroup()
				.setId(r.getValue(WORKGROUP.ID))
				.setDomain(r.getValue(WORKGROUP.DOMAIN))
				.setDescription(r.getValue(WORKGROUP.DESCRIPTION))
				.setStatus(WorkgroupStatus.safeValueOf(r.getValue(WORKGROUP.STATUS)))
				.setDirty(false);
		}
	}
	
}
