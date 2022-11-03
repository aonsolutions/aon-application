package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.TaskHolderWorkgroup.TASK_HOLDER_WORKGROUP;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

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
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaskHolderWorkgroupFilter;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderWorkgroupProperties;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.impl.jooq.dao.TaskOldDAO.WorkgroupFiller;
import com.esferalia.aon.occam.impl.jooq.validation.TaskHolderWorkgroupValidation;

public class TaskHolderWorkgroupDAO {

	private TaskHolderWorkgroupDAO() {
		throw new IllegalStateException("Utility class");
	}
	
	private static final TaskHolderWorkgroupPropertiesDAO TASK_HOLDER_WORKGROUP_PROPERTIES = new TaskHolderWorkgroupPropertiesDAO();

	protected static class TaskHolderWorkgroupPropertiesDAO implements TaskHolderWorkgroupProperties {

		protected Select<Record> build(SelectJoinStep<Record> select, TaskHolderWorkgroupFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(TaskHolderWorkgroupFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER_WORKGROUP.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER_WORKGROUP.DOMAIN);}
		@Override public Property<Integer> getTaskHolderProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER_WORKGROUP.TASK_HOLDER);}
		@Override public Property<Integer> getWorkgroupProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER_WORKGROUP.WORKGROUP);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, TaskHolderWorkgroupFilter filter) {
		return ctx.getDslContext().select()
				.from(TASK_HOLDER_WORKGROUP).join(WORKGROUP).on(WORKGROUP.ID.eq(TASK_HOLDER_WORKGROUP.WORKGROUP))
				.where(TASK_HOLDER_WORKGROUP_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<TaskHolderWorkgroup> getStream(AONContext ctx, TaskHolderWorkgroupFilter filter){
		return select(ctx, filter).fetch().stream().map(new TaskHolderWorkgroupFiller());
	}
	
	public static List<TaskHolderWorkgroup> getList(AONContext ctx, TaskHolderWorkgroupFilter filter) {
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static TaskHolderWorkgroup get(AONContext ctx, TaskHolderWorkgroupFilter filter) {
		return select(ctx, filter).limit(1).fetch().stream().map(new TaskHolderWorkgroupFiller())
				.findFirst().orElse(new TaskHolderWorkgroup());
	}
	
	public static TaskHolderWorkgroup save(AONContext ctx, TaskHolderWorkgroup taskHolderWorkgroup) {
		TaskHolderWorkgroupValidation.validate(ctx, taskHolderWorkgroup);
		
		TaskHolderWorkgroup uw = TaskHolderWorkgroupDAO.get(ctx, f -> f.getTaskHolderProperty().eq(taskHolderWorkgroup.getTaskHolder())
				.and(f.getWorkgroupProperty().eq(taskHolderWorkgroup.getWorkgroup().getId())));

		return uw.isEmpty() ? insert(ctx, taskHolderWorkgroup): uw;
	}
	
	public static TaskHolderWorkgroup insert(AONContext ctx, TaskHolderWorkgroup taskHolderWorkgroup) {
		Integer id =  ctx.getDslContext().insertInto(TASK_HOLDER_WORKGROUP)
				.set(TASK_HOLDER_WORKGROUP.DOMAIN, taskHolderWorkgroup.getDomain())
				.set(TASK_HOLDER_WORKGROUP.TASK_HOLDER, taskHolderWorkgroup.getTaskHolder())
				.set(TASK_HOLDER_WORKGROUP.WORKGROUP, taskHolderWorkgroup.getWorkgroup().getId())
				.returning(TASK_HOLDER_WORKGROUP.ID).fetchOne().getValue(TASK_HOLDER_WORKGROUP.ID);
		return taskHolderWorkgroup.setId(id);
	}
	
	protected static void delete(AONContext ctx, TaskHolder taskHolder) {
		delete(ctx, f -> f.getTaskHolderProperty().eq(taskHolder.getId()));
	}
	
	protected static void delete(AONContext ctx, TaskHolderWorkgroupFilter filter) {
		ctx.getDslContext().delete(TASK_HOLDER_WORKGROUP)
			.where(TASK_HOLDER_WORKGROUP_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	public static class TaskHolderWorkgroupFiller extends Filler implements Function<Record, TaskHolderWorkgroup> {
		@Override
		public TaskHolderWorkgroup apply(Record r) {
			return build(r);
		}
		
		public static TaskHolderWorkgroup build(Record r) {
			return new TaskHolderWorkgroup()
					.setId(r.getValue(TASK_HOLDER_WORKGROUP.ID))
					.setDomain(r.getValue(TASK_HOLDER_WORKGROUP.DOMAIN))
					.setTaskHolder(r.getValue(TASK_HOLDER_WORKGROUP.TASK_HOLDER))
					.setWorkgroup(WorkgroupFiller.buildWorkgroup(r));
		}
	}

}
