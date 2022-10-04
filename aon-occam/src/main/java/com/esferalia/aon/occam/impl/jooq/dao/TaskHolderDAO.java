package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.TaskHolderWorkgroup.TASK_HOLDER_WORKGROUP;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaskHolderFilter;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderProperties;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.validation.TaskHolderAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.TaskHolderValidation;

public class TaskHolderDAO {
	
	private TaskHolderDAO() {
	
	}
	
	private static final TaskHolderPropertiesDAO TASK_HOLDER_PROPERTIES = new TaskHolderPropertiesDAO();
	public static class TaskHolderPropertiesDAO extends RegistryPropertiesDAO implements TaskHolderProperties {
		
		
		protected Condition[] getConditions(TaskHolderFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.DOMAIN);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.TYPE);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.ACTIVE);}
		@Override public Property<Integer> getUserIdProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.USER_ID);}
		@Override public Property<Integer> getCostProfileProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.COST_PROFILE);}
	}

	
	public static class TaskHolderFiller extends Filler implements Function<Record, TaskHolder> {

		public TaskHolder apply(Record r) {
			return build(r, REGISTRY);
		}
		
		public static TaskHolder build(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			if(registry == null) registry = REGISTRY;
			return new TaskHolder()
					.copy(RegistryFiller.build(r, registry)
						.setDomain(new Domain().setId(r.getValue(TASK_HOLDER.DOMAIN))))
					.setActive(getBoolean(r, TASK_HOLDER.ACTIVE))
					.setCostProfile(r.getValue(TASK_HOLDER.COST_PROFILE))
					.setType(TaskHolderType.safeValueOf(r.getValue(TASK_HOLDER.TYPE)))
					.setUserId(r.getValue(TASK_HOLDER.USER_ID));
		}
		
		public static TaskHolder build(Record r, com.esferalia.aon.jooq.tables.TaskHolder th, com.esferalia.aon.jooq.tables.Registry registry) {
			if(th == null) th = TASK_HOLDER;
			if(registry == null) registry = REGISTRY;
			return new TaskHolder()
					.copy(RegistryFiller.build(r, registry))
					.setActive(getBoolean(r, th.ACTIVE))
					.setCostProfile(r.getValue(th.COST_PROFILE))
					.setType(TaskHolderType.safeValueOf(r.getValue(th.TYPE)))
					.setUserId(r.getValue(th.USER_ID));
		}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, TaskHolderFilter filter) {
		return ctx.getDslContext().select()
				.from(TASK_HOLDER)
				.join(REGISTRY).on(REGISTRY.ID.eq(TASK_HOLDER.REGISTRY))
				.where(TASK_HOLDER_PROPERTIES.getConditions(filter));
		
	}

	public static Stream<TaskHolder> getStream(AONContext ctx, TaskHolderFilter filter){
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new TaskHolderFiller());
	}

	public static List<TaskHolder> getList(AONContext ctx, TaskHolderFilter filter){
		return getStream(ctx, filter)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Stream<TaskHolder> getStream(AONContext ctx, TaskHolderFilter filter, int offset, int limit){
		return select(ctx,filter)
				.orderBy(REGISTRY.NAME)
				.offset(offset)
				.limit(limit)
				.fetch()
				.stream()
				.map(new TaskHolderFiller());
	}

	public static TaskHolder get(AONContext ctx, Integer id){
		return get(ctx, p -> p.getIdProperty().eq(id));
	}
	
	public static TaskHolder get(AONContext ctx, TaskHolderFilter filter){
		return select(ctx,filter)
			.limit(1)
			.fetch()
			.stream()
			.map(new TaskHolderFiller())
			.findFirst()
			.orElse(new TaskHolder());
	}
	
	public static TaskHolder save(AONContext ctx, TaskHolder taskHolder) {
		ctx.checkWrite();
		TaskHolderAutoComplete.autoComplete(ctx, taskHolder);
		TaskHolderValidation.validate(ctx, taskHolder);
		
		boolean nullId = (taskHolder.getId() == null); 
//		if(nullId) 
		taskHolder = RegistryDAO.save(ctx, taskHolder);
		return nullId || get(ctx, taskHolder.getId()).isEmpty() 
			? insert(ctx, taskHolder) : update(ctx, taskHolder);
	}

	private static TaskHolder insert(AONContext ctx, TaskHolder taskHolder){
		ctx.getDslContext().insertInto(TASK_HOLDER)
			.set(TASK_HOLDER.REGISTRY, taskHolder.getId())
			.set(TASK_HOLDER.DOMAIN, taskHolder.getDomain().getId())
			.set(TASK_HOLDER.TYPE, taskHolder.getType().value())
			.set(TASK_HOLDER.USER_ID, taskHolder.getUserId())
			.set(TASK_HOLDER.COST_PROFILE, taskHolder.getCostProfile())
			.set(TASK_HOLDER.ACTIVE, taskHolder.getActiveValue())
			.execute();
		ctx.log().debug("INSERT TASK HOLDER id: {0}",taskHolder.getId());		
		return taskHolder;
	}
	
	private static TaskHolder update(AONContext ctx, TaskHolder taskHolder){
		ctx.checkWrite();
		int count = ctx.getDslContext().update(TASK_HOLDER)
			.set(TASK_HOLDER.DOMAIN, taskHolder.getDomain().getId())
			.set(TASK_HOLDER.TYPE, taskHolder.getType().value())
			.set(TASK_HOLDER.USER_ID, taskHolder.getUserId())
			.set(TASK_HOLDER.COST_PROFILE, taskHolder.getCostProfile())
			.set(TASK_HOLDER.ACTIVE, taskHolder.getActiveValue())
			.where(TASK_HOLDER.REGISTRY.eq(taskHolder.getId()))
			.execute();
		ctx.log().debug("UPDATE TASK HOLDER id: {0}. ({1} rows)",taskHolder.getId(),count);		
		return taskHolder;
	}

	public static TaskHolder delete(AONContext ctx, TaskHolder taskHolder) {
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(TASK_HOLDER)
			.where(TASK_HOLDER.REGISTRY.eq(taskHolder.getId()))
			.execute();
		ctx.log().debug("DELETE TASK HOLDER id: {0} ({1} rows)",taskHolder.getId(),count);
		return taskHolder;
	}
	
	public static Stream<TaskHolder> getTaskHolderWorkgroup(AONContext ctx, TaskHolderFilter filter, Integer workgroupId){
		SelectConditionStep<Record> r = ctx.getDslContext().select()
				.from(TASK_HOLDER)
				.join(REGISTRY).on(REGISTRY.ID.eq(TASK_HOLDER.REGISTRY))
				.leftOuterJoin(TASK_HOLDER_WORKGROUP).on(TASK_HOLDER.REGISTRY.eq(TASK_HOLDER_WORKGROUP.TASK_HOLDER))
				.where(TASK_HOLDER_PROPERTIES.getConditions(filter));
		if(workgroupId != null && workgroupId>0)
			r.and(TASK_HOLDER_WORKGROUP.WORKGROUP.eq(workgroupId));
	
		return r.orderBy(REGISTRY.NAME).fetch().stream().map(new TaskHolderFiller());
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	
	public static TaskHolder getRandom(AONContext ctx, TaskHolderFilter filter) {
		return select(ctx, filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new TaskHolderFiller())
			.findFirst()
			.orElse(null);
	}
	
}

