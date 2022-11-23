package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ActivityType.ACTIVITY_TYPE;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ActivityType;
import com.esferalia.aon.occam.api.model.Filter.ActivityTypeFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.ActivityTypeProperties;
import com.esferalia.aon.occam.impl.jooq.validation.ActivityTypeValidation;

public class ActivityTypeDAO {
	
	private ActivityTypeDAO() {

	}
	
	private static final ActivityTypePropertiesDAO ACTIVITY_TYPE_PROPERTIES = new ActivityTypePropertiesDAO();
	protected static class ActivityTypePropertiesDAO implements ActivityTypeProperties {
		protected Condition[] getConditions(ActivityTypeFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(ACTIVITY_TYPE.ID);} 
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(ACTIVITY_TYPE.ACTIVE);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(ACTIVITY_TYPE.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(ACTIVITY_TYPE.DESCRIPTION);}
		@Override public Property<Integer> getProjectTypeProperty() {return new FilterDAO.PropertyDAO<>(ACTIVITY_TYPE.PROJECT_TYPE);}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, ActivityTypeFilter filter) {
		return ctx.getDslContext()
			.select(ACTIVITY_TYPE.fields())
			.from(ACTIVITY_TYPE)
			.where(ACTIVITY_TYPE_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<ActivityType> getStream(AONContext ctx, ActivityTypeFilter filter){
		return select(ctx, filter).fetch().stream().map(new ActivityTypeFiller());
	}
	
	public static ActivityType get(AONContext ctx, ActivityTypeFilter filter){
		return select(ctx, filter).limit(1).fetch().stream().map(new ActivityTypeFiller()).findFirst().orElse(new ActivityType());
	}
	
	public static ActivityType save(AONContext ctx, ActivityType activityType){
		if(!activityType.isDirty()) return activityType;
		ActivityTypeValidation.validate(ctx, activityType);
		return activityType.getId() != null 
			? update(ctx, activityType) 
			: insert(ctx, activityType);
	}
	
	public static ActivityType insert(AONContext ctx, ActivityType activityType){
		Integer id = ctx.getDslContext()
			.insertInto(ACTIVITY_TYPE)
			.set(ACTIVITY_TYPE.DOMAIN, activityType.getDomain())
			.set(ACTIVITY_TYPE.DESCRIPTION, activityType.getDescription())
			.set(ACTIVITY_TYPE.ACTIVE, activityType.isActive() ? (byte) 1 : (byte) 0)
			.set(ACTIVITY_TYPE.PROJECT_TYPE, activityType.getProjectType())
			.returning(ACTIVITY_TYPE.ID).fetchOne().getValue(ACTIVITY_TYPE.ID);
		ctx.log().debug("INSERT ACTIVITY TYPE id: " +id);	
		return activityType.setId(id).setDirty(false);
	}
	
	public static ActivityType update(AONContext ctx, ActivityType activityType){
		ctx.getDslContext()
			.update(ACTIVITY_TYPE)
			.set(ACTIVITY_TYPE.DOMAIN, activityType.getDomain())
			.set(ACTIVITY_TYPE.DESCRIPTION, activityType.getDescription())
			.set(ACTIVITY_TYPE.ACTIVE, activityType.isActive() ? (byte) 1 : (byte) 0)
			.set(ACTIVITY_TYPE.PROJECT_TYPE, activityType.getProjectType())
			.where(ACTIVITY_TYPE.ID.eq(activityType.getId())).execute();
		ctx.log().debug("UPDATE ACTIVITY TYPE id:" + activityType.getId());
		return activityType.setDirty(false);
	}

	public static void delete(AONContext ctx, Integer id) {
		ProjectActivityDAO.delete(ctx, f-> f.getActivityTypeProperty().eq(id));
		delete(ctx, f -> f.getIdProperty().eq(id));
		ctx.log().debug("DELETE ACTIVITY TYPE id: " + id);		
	}
	
	public static void delete(AONContext ctx, ActivityTypeFilter filter) {
		ctx.checkWrite();
		
		ctx.getDslContext()
		.delete(ACTIVITY_TYPE)
		.where(ACTIVITY_TYPE_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class ActivityTypeFiller extends Filler implements Function<Record, ActivityType> {
		
		@Override
		public ActivityType apply(Record r) {
			return build(r);
		}
		
		public static ActivityType build(Record r) {
			return new ActivityType()
				.setId(r.getValue(ACTIVITY_TYPE.ID))
				.setDomain(r.getValue(ACTIVITY_TYPE.DOMAIN))
				.setDescription(r.getValue(ACTIVITY_TYPE.DESCRIPTION))
				.setActive(getBoolean(r, ACTIVITY_TYPE.ACTIVE))
				.setProjectType(r.getValue(ACTIVITY_TYPE.PROJECT_TYPE))
				.setDirty(false);
		}
	}
	
}
