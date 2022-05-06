package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.TaskAttach.TASK_ATTACH;

import java.util.LinkedList;
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
import com.esferalia.aon.occam.api.model.Filter.TaskAttachFilter;
import com.esferalia.aon.occam.api.model.Properties.TaskAttachProperties;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.type.MimeType;

public class TaskAttachDAO {

	private static final TaskAttachPropertiesDAO TASK_ATTACH_PROPERTIES = new TaskAttachPropertiesDAO();
	
	protected static class TaskAttachPropertiesDAO implements TaskAttachProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, TaskAttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(TaskAttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(TASK_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(TASK_ATTACH.DOMAIN);}
		@Override public Property<Integer> getTaskProperty() {return new FilterDAO.PropertyDAO<>(TASK_ATTACH.TASK);}
		@Override public Property<Integer> getTaskWorkflowProperty() {return new FilterDAO.PropertyDAO<>(TASK_ATTACH.TASK_WORKFLOW);}
		@Override public Property<Byte> getMimetypeProperty()  {return new FilterDAO.PropertyDAO<>(TASK_ATTACH.MIMETYPE);}
		@Override public Property<byte[]> getDataProperty()  {return new FilterDAO.PropertyDAO<>(TASK_ATTACH.DATA);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, TaskAttachFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(TASK_ATTACH)
				.where(TASK_ATTACH_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<TaskAttach> getStream(AONContext ctx, TaskAttachFilter filter){	
		return select(ctx, filter).fetch().stream().map(new TaskAttachFiller());
	}
	
	public static LinkedList<TaskAttach> getList(AONContext ctx, TaskAttachFilter filter){	
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static TaskAttach get(AONContext ctx, TaskAttachFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new TaskAttachFiller())
			.findFirst().orElse(new TaskAttach());
	}
	
	public static TaskAttach save(AONContext ctx, TaskAttach taskAttach) {
		return taskAttach.getId() != null 
			? update(ctx, taskAttach)
			: insert(ctx, taskAttach);
	}
	
	public static TaskAttach update(AONContext ctx, TaskAttach taskAttach) {
		ctx.getDslContext().update(TASK_ATTACH)
			.set(TASK_ATTACH.DOMAIN, taskAttach.getDomain())
			.set(TASK_ATTACH.TASK, taskAttach.getTask())
			.set(TASK_ATTACH.TASK_WORKFLOW, taskAttach.getTaskWorkflow())
			.set(TASK_ATTACH.MIMETYPE, taskAttach.getMimetype().value())	
			.set(TASK_ATTACH.DATA, taskAttach.getData())
			.execute();
		ctx.log().debug("UPDATE TASK_ATTACH id: " + taskAttach.getId());		
		return taskAttach;
	}
	
	public static TaskAttach insert(AONContext ctx, TaskAttach taskAttach) {
		Integer id = ctx.getDslContext().insertInto(TASK_ATTACH)
				.set(TASK_ATTACH.DOMAIN, taskAttach.getDomain())
				.set(TASK_ATTACH.TASK, taskAttach.getTask())
				.set(TASK_ATTACH.TASK_WORKFLOW, taskAttach.getTaskWorkflow())
				.set(TASK_ATTACH.MIMETYPE, taskAttach.getMimetype().value())	
				.set(TASK_ATTACH.DATA, taskAttach.getData())
			.returning(TASK_ATTACH.ID).fetchOne().getId();
		ctx.log().debug("INSERT TASK_ATTACH id: " + id);			
		return taskAttach.setId(id);
	}	

	public static void delete(AONContext ctx, Integer id){
		ctx.getDslContext().delete(TASK_ATTACH)
		.where(TASK_ATTACH.ID.eq(id))
		.execute();
		ctx.log().debug("DELETE TASK_ATTACH id:" + id);
	}
	
	public static void deleteByTask(AONContext ctx, Integer id){
		ctx.getDslContext().delete(TASK_ATTACH)
		.where(TASK_ATTACH.TASK.eq(id))
		.execute();
		ctx.log().debug("DELETE TASK_ATTACH task:" + id);
	}
	
	public static class TaskAttachFiller implements Function<Record, TaskAttach> {
		@Override
		public TaskAttach apply(Record r) {
			return new TaskAttach()
				.setId(r.getValue(TASK_ATTACH.ID))
				.setDomain(r.getValue(TASK_ATTACH.DOMAIN))
				.setTask(r.getValue(TASK_ATTACH.TASK))
				.setTaskWorkflow(r.getValue(TASK_ATTACH.TASK_WORKFLOW))
				.setMimetype( MimeType.safeValueOf(r.getValue(TASK_ATTACH.MIMETYPE)) )
				.setData(r.getValue(TASK_ATTACH.DATA));
		}
	}
}
