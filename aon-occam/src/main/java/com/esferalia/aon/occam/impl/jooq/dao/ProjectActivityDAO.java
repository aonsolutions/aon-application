package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ActivityType.ACTIVITY_TYPE;
import static com.esferalia.aon.jooq.tables.ProjectActivity.PROJECT_ACTIVITY;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.ProjectActivityFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.ProjectActivityProperties;
import com.esferalia.aon.occam.api.model.project.ProjectActivity;
import com.esferalia.aon.occam.impl.jooq.dao.ActivityTypeDAO.ActivityTypeFiller;
import com.esferalia.aon.occam.impl.jooq.validation.ProjectActivityAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.ProjectActivityValidation;

public class ProjectActivityDAO {
	
	private ProjectActivityDAO() {
		
	}

	private static final ProjectActivityPropertiesDAO PROJECT_ACTIVITY_PROPERTIES = new ProjectActivityPropertiesDAO();
	protected static class ProjectActivityPropertiesDAO implements ProjectActivityProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, ProjectActivityFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(ProjectActivityFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ACTIVITY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ACTIVITY.DOMAIN);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ACTIVITY.PROJECT);}
		@Override public Property<Integer> getActivityTypeProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ACTIVITY.ACTIVITY_TYPE);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ACTIVITY.ACTIVE);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, ProjectActivityFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(PROJECT_ACTIVITY)
				.join(ACTIVITY_TYPE).on(ACTIVITY_TYPE.ID.eq(PROJECT_ACTIVITY.ACTIVITY_TYPE))
				.where(PROJECT_ACTIVITY_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<ProjectActivity> getStream(AONContext ctx, ProjectActivityFilter filter){	
		return select(ctx, filter).fetch().stream().map(new ProjectActivityFiller());
	}
	
	public static Stream<ProjectActivity> getStream(AONContext ctx, ProjectActivityFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter)
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch().stream().map(new ProjectActivityFiller());
	}
	
	public static Optional<ProjectActivity> get(AONContext ctx, ProjectActivityFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new ProjectActivityFiller())
			.findFirst();
	}
	
	public static ProjectActivity save(AONContext ctx, ProjectActivity projectActivity) {
		if(!projectActivity.isDirty()) return projectActivity;
		ProjectActivityAutoComplete.autoComplete(ctx, projectActivity);
		ProjectActivityValidation.validate(ctx, projectActivity);
		return projectActivity.getId() != null 
			? update(ctx, projectActivity)
			: insert(ctx, projectActivity);
	}
	
	public static ProjectActivity update(AONContext ctx, ProjectActivity projectActivity) {
		ctx.getDslContext().update(PROJECT_ACTIVITY)
			.set(PROJECT_ACTIVITY.PROJECT, projectActivity.getProject())
			.set(PROJECT_ACTIVITY.ACTIVITY_TYPE, projectActivity.getActivityType().getId())
			.set(PROJECT_ACTIVITY.ACTIVE, (byte) (projectActivity.isActive() ? 1 : 0))	
			.where(PROJECT_ACTIVITY.ID.eq(projectActivity.getId()))
			.execute();
		return projectActivity.setDirty(false);
	}
	
	public static ProjectActivity insert(AONContext ctx, ProjectActivity projectActivity) {
		Integer id = ctx.getDslContext().insertInto(PROJECT_ACTIVITY)
				.set(PROJECT_ACTIVITY.DOMAIN, projectActivity.getDomain())
				.set(PROJECT_ACTIVITY.PROJECT, projectActivity.getProject())
				.set(PROJECT_ACTIVITY.ACTIVITY_TYPE, projectActivity.getActivityType().getId())
				.set(PROJECT_ACTIVITY.ACTIVE, (byte) (projectActivity.isActive() ? 1 : 0))	
			.returning(PROJECT_ACTIVITY.ID).fetchOne().getId();
		return projectActivity.setId(id).setDirty(false);
	}	
	
	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getIdProperty().eq(id));
	}
	
	public static void delete(AONContext ctx, ProjectActivityFilter filter) {
		ctx.getDslContext().delete(PROJECT_ACTIVITY)
		.where(PROJECT_ACTIVITY_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class ProjectActivityFiller extends Filler implements Function<Record, ProjectActivity> {
		
		@Override
		public ProjectActivity apply(Record r) {
			return build(r);
		}
		
		public static ProjectActivity build(Record r) {
			return new ProjectActivity()
					.setId(r.getValue(PROJECT_ACTIVITY.ID))
					.setDomain(r.getValue(PROJECT_ACTIVITY.DOMAIN))
					.setProject(r.getValue(PROJECT_ACTIVITY.PROJECT))
					.setActivityType(ActivityTypeFiller.build(r))
					.setActive(r.getValue(PROJECT_ACTIVITY.ACTIVE) == 1)
					.setDirty(false);
		}
	}
}
