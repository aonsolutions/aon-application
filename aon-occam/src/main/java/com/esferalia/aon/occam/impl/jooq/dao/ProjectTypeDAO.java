package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ProjectType.PROJECT_TYPE;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.ProjectTypeFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.ProjectTypeProperties;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.impl.jooq.validation.ProjectTypeValidation;

public class ProjectTypeDAO {
	
	private ProjectTypeDAO() {

	}
	
	private static final ProjectTypePropertiesDAO PROJECT_TYPE_PROPERTIES = new ProjectTypePropertiesDAO();
	protected static class ProjectTypePropertiesDAO implements ProjectTypeProperties {
		protected Condition[] getConditions(ProjectTypeFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_TYPE.ID);} 
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_TYPE.ACTIVE);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_TYPE.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_TYPE.DESCRIPTION);}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, ProjectTypeFilter filter) {
		return ctx.getDslContext().select()
			.from(PROJECT_TYPE)
			.where(PROJECT_TYPE_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<ProjectType> getStream(AONContext ctx, ProjectTypeFilter filter){
		return select(ctx, filter).fetch().stream().map(new ProjectTypeFiller());
	}
	
	public static ProjectType get(AONContext ctx, ProjectTypeFilter filter){
		return select(ctx, filter).limit(1).fetch().stream().map(new ProjectTypeFiller()).findFirst().orElse(new ProjectType());
	}
	
	public static ProjectType save(AONContext ctx, ProjectType projectType){
		if(!projectType.isDirty())return projectType;
		ProjectTypeValidation.validate(ctx, projectType);
		return projectType.getId() != null 
			? update(ctx, projectType) 
			: insert(ctx, projectType);
	}
	
	public static ProjectType insert(AONContext ctx, ProjectType projectType){
		Integer id = ctx.getDslContext().insertInto(PROJECT_TYPE)
			.set(PROJECT_TYPE.DOMAIN, projectType.getDomain())
			.set(PROJECT_TYPE.DESCRIPTION, projectType.getDescription())
			.set(PROJECT_TYPE.ACTIVE, projectType.isActive() ? (byte) 1 : (byte) 0)
			.returning(PROJECT_TYPE.ID).fetchOne().getValue(PROJECT_TYPE.ID);
		ctx.log().debug("INSERT PROJECT TYPE id: " +id);	
		return projectType.setId(id).setDirty(false);
	}
	
	public static ProjectType update(AONContext ctx, ProjectType projectType){
		ctx.getDslContext().update(PROJECT_TYPE)
			.set(PROJECT_TYPE.DOMAIN, projectType.getDomain())
			.set(PROJECT_TYPE.DESCRIPTION, projectType.getDescription())
			.set(PROJECT_TYPE.ACTIVE, projectType.isActive() ? (byte) 1 : (byte) 0)
			.where(PROJECT_TYPE.ID.eq(projectType.getId())).execute();
		ctx.log().debug("UPDATE PROJECT TYPE id:" + projectType.getId());
		return projectType.setDirty(false);
	}

	public static void delete(AONContext ctx, Integer id) {
		delete(ctx, f -> f.getIdProperty().eq(id));
	}
	
	public static void delete(AONContext ctx, ProjectTypeFilter filter) {
		ctx.getDslContext().delete(PROJECT_TYPE)
		.where(PROJECT_TYPE_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class ProjectTypeFiller extends Filler implements Function<Record, ProjectType> {
		
		@Override
		public ProjectType apply(Record r) {
			return build(r);
		}
		
		public static ProjectType build(Record r) {
			return new ProjectType()
				.setId(r.getValue(PROJECT_TYPE.ID))
				.setDomain(r.getValue(PROJECT_TYPE.DOMAIN))
				.setDescription(r.getValue(PROJECT_TYPE.DESCRIPTION))
				.setActive(getBoolean(r, PROJECT_TYPE.ACTIVE))
				.setDirty(false);
		}
	}
	
}
