package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ProjectHolder.PROJECT_HOLDER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import static com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TASK_HOLDER_ALIAS;

import java.sql.Timestamp;
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
import com.esferalia.aon.occam.api.model.Filter.ProjectHolderFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.ProjectHolderProperties;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TaskHolderFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkgroupDAO.WorkgroupFiller;
import com.esferalia.aon.occam.impl.jooq.validation.ProjectHolderAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.ProjectHolderValidation;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ProjectHolderDAO {
	
	private ProjectHolderDAO() {
		
	}

	private static final ProjectHolderPropertiesDAO PROJECT_HOLDER_PROPERTIES = new ProjectHolderPropertiesDAO();
	protected static class ProjectHolderPropertiesDAO implements ProjectHolderProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, ProjectHolderFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(ProjectHolderFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_HOLDER.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_HOLDER.DOMAIN);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_HOLDER.PROJECT);}
		@Override public Property<Timestamp> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_HOLDER.START_DATE);}
		@Override public Property<Timestamp> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_HOLDER.END_DATE);}
		@Override public Property<Integer> getWorkgroupProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_HOLDER.WORKGROUP);}
		@Override public Property<Integer> getTaskHolderProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_HOLDER.TASK_HOLDER);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, ProjectHolderFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(PROJECT_HOLDER)
				.leftOuterJoin(WORKGROUP).on(PROJECT_HOLDER.WORKGROUP.eq(WORKGROUP.ID))
				.leftOuterJoin(TASK_HOLDER).on(PROJECT_HOLDER.TASK_HOLDER.eq(TASK_HOLDER.REGISTRY))
				.leftOuterJoin(TASK_HOLDER_ALIAS).on(PROJECT_HOLDER.TASK_HOLDER.eq(TASK_HOLDER_ALIAS.ID))
				.where(PROJECT_HOLDER_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<ProjectHolder> getStream(AONContext ctx, ProjectHolderFilter filter){	
		return select(ctx, filter).fetch().stream().map(new ProjectHolderFiller());
	}
	
	public static Stream<ProjectHolder> getStream(AONContext ctx, ProjectHolderFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter)
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch().stream().map(new ProjectHolderFiller());
	}
	
	public static List<ProjectHolder> getList(AONContext ctx, ProjectHolderFilter filter){	
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static List<ProjectHolder> getList(AONContext ctx, ProjectHolderFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static ProjectHolder get(AONContext ctx, ProjectHolderFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new ProjectHolderFiller())
			.findFirst().orElse(new ProjectHolder());
	}
	
	public static ProjectHolder save(AONContext ctx, ProjectHolder projectHolder) {
		if(!projectHolder.isDirty()) return projectHolder;
		ProjectHolderAutoComplete.autoComplete(ctx, projectHolder);
		ProjectHolderValidation.validate(ctx, projectHolder);
		return projectHolder.getId() != null 
			? update(ctx, projectHolder)
			: insert(ctx, projectHolder);
	}
	
	public static ProjectHolder update(AONContext ctx, ProjectHolder projectHolder) {
		ctx.getDslContext().update(PROJECT_HOLDER)
			.set(PROJECT_HOLDER.DOMAIN, projectHolder.getDomain())
			.set(PROJECT_HOLDER.PROJECT, projectHolder.getProject())
			.set(PROJECT_HOLDER.TASK_HOLDER, projectHolder.getTaskHolder().getId())
			.set(PROJECT_HOLDER.START_DATE, AonDateUtils.toTimestamp(projectHolder.getStartDate()))	
			.set(PROJECT_HOLDER.END_DATE, AonDateUtils.toTimestamp(projectHolder.getEndDate()))
			.set(PROJECT_HOLDER.WORKGROUP, projectHolder.getWorkgroup().getId())
			.where(PROJECT_HOLDER.ID.eq(projectHolder.getId()))
			.execute();
		return projectHolder.setDirty(false);
	}
	
	public static ProjectHolder insert(AONContext ctx, ProjectHolder projectHolder) {
		Integer id = ctx.getDslContext().insertInto(PROJECT_HOLDER)
				.set(PROJECT_HOLDER.DOMAIN, projectHolder.getDomain())
				.set(PROJECT_HOLDER.PROJECT, projectHolder.getProject())
				.set(PROJECT_HOLDER.TASK_HOLDER, projectHolder.getTaskHolder().getId())
				.set(PROJECT_HOLDER.START_DATE, AonDateUtils.toTimestamp(projectHolder.getStartDate()))	
				.set(PROJECT_HOLDER.END_DATE, AonDateUtils.toTimestamp(projectHolder.getEndDate()))
				.set(PROJECT_HOLDER.WORKGROUP, projectHolder.getWorkgroup().getId())
			.returning(PROJECT_HOLDER.ID).fetchOne().getId();
		return projectHolder.setId(id).setDirty(false);
	}	
	
	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getIdProperty().eq(id));
	}
	
	public static void delete(AONContext ctx, ProjectHolderFilter filter) {
		ctx.getDslContext().delete(PROJECT_HOLDER)
		.where(PROJECT_HOLDER_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class ProjectHolderFiller extends Filler implements Function<Record, ProjectHolder> {
		
		@Override
		public ProjectHolder apply(Record r) {
			return build(r);
		}
		
		public static ProjectHolder build(Record r) {
			return new ProjectHolder()
					.setId(r.getValue(PROJECT_HOLDER.ID))
					.setDomain(r.getValue(PROJECT_HOLDER.DOMAIN))
					.setProject(r.getValue(PROJECT_HOLDER.PROJECT))
					.setStartDate(r.getValue(PROJECT_HOLDER.START_DATE))
					.setEndDate(r.getValue(PROJECT_HOLDER.END_DATE))
					.setWorkgroup(checkField(r, WORKGROUP.ID)
						? WorkgroupFiller.build(r)
						: new Workgroup().setId(r.getValue(PROJECT_HOLDER.WORKGROUP)))
					.setTaskHolder(checkField(r, TASK_HOLDER.REGISTRY)
						? TaskHolderFiller.build(r)
						: new TaskHolder().setRegistry(r.getValue(PROJECT_HOLDER.TASK_HOLDER)))
					.setDirty(false);
		}
	}
}
