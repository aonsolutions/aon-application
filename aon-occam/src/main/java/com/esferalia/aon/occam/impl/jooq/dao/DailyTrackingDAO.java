package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DailyTracking.DAILY_TRACKING;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.JobType.JOB_TYPE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.DailyTrackingFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.DailyTrackingProperties;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.task.DailyTracking;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TaskHolderFiller;

public class DailyTrackingDAO {

	private DailyTrackingDAO() {
		throw new IllegalStateException("Utility Class");
	}
	
	private static final DailyTrackingPropertiesDAO DAILY_TRACKING_PROPERTIES = new DailyTrackingPropertiesDAO();

	protected static class DailyTrackingPropertiesDAO implements DailyTrackingProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, DailyTrackingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(DailyTrackingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DAILY_TRACKING.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(DAILY_TRACKING.DOMAIN);}
		@Override public Property<Integer> getTaskHolderProperty() {return new FilterDAO.PropertyDAO<>(DAILY_TRACKING.TASK_HOLDER);}
		@Override public Property<Integer> getJobTypeProperty() {return new FilterDAO.PropertyDAO<>(DAILY_TRACKING.JOB_TYPE);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(DAILY_TRACKING.REGISTRY);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(DAILY_TRACKING.PROJECT);}
		@Override public Property<Integer> getActivityTypeProperty() {return new FilterDAO.PropertyDAO<>(DAILY_TRACKING.ACTIVITY_TYPE);}
		@Override public Property<Integer> getTaskProperty() {return new FilterDAO.PropertyDAO<>(DAILY_TRACKING.TASK);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(DAILY_TRACKING.COMMENTS);}
	}
	
	private static Stream<DailyTracking> getStream(AONContext ctx, DailyTrackingFilter filter, Optional<Integer> page, Optional<Integer> perPage){	
		SelectConditionStep<Record> condition = ctx.getDslContext()
			.select()
			.from(DAILY_TRACKING)
			.innerJoin(DOMAIN).on(DOMAIN.ID.eq(DAILY_TRACKING.DOMAIN))
			.innerJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(DAILY_TRACKING.TASK_HOLDER))
			.innerJoin(JOB_TYPE).on(JOB_TYPE.ID.eq(DAILY_TRACKING.JOB_TYPE))
			.leftOuterJoin(REGISTRY).on(REGISTRY.ID.eq(DAILY_TRACKING.REGISTRY))
			.where(DAILY_TRACKING_PROPERTIES.getConditions(filter));
	
		if(page.isPresent() && perPage.isPresent()) {
			Integer per = perPage.get();
			Integer p = page.get();
			condition.limit(per).offset(per * (p -1));
		}
		
		return condition.orderBy(DAILY_TRACKING.ID.desc()).fetch().stream().map(new DailyTrackingFiller());
	}
	
	public static Stream<DailyTracking> getStream(AONContext ctx, DailyTrackingFilter filter){	
		return getStream(ctx, filter, Optional.empty(), Optional.empty());
	}
	
	public static Stream<DailyTracking> getStream(AONContext ctx, DailyTrackingFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, Optional.of(page), Optional.of(perPage));
	}

	public static DailyTracking get(AONContext ctx, DailyTrackingFilter filter) {
		ctx.checkRead();

		return getStream(ctx, filter).findFirst().orElse(new DailyTracking());
	}
	
	public static DailyTracking save(AONContext ctx, DailyTracking dailyTracking) {
//		DailyTrackingAutoComplete.autoComplete(ctx, dailyTracking);
//		DailyTrackingValidation.validate(ctx, dailyTracking);
		if(dailyTracking.getId() != null && dailyTracking.getId()>0) {
			update(ctx, dailyTracking);
		} else {
			insert(ctx, dailyTracking);
		}

		return dailyTracking;
	}
	
	public static DailyTracking update(AONContext ctx, DailyTracking dailyTracking) {
		ctx.getDslContext()
			.update(DAILY_TRACKING)
			.set(DAILY_TRACKING.TRACKING_DATE, toSql(dailyTracking.getTrackingDate()))
			.set(DAILY_TRACKING.TRACKING_DURATION, dailyTracking.getTrackingDuration())
			.set(DAILY_TRACKING.JOB_TYPE, dailyTracking.getJobType())
			.set(DAILY_TRACKING.REGISTRY, dailyTracking.getRegistry().getId())
			.set(DAILY_TRACKING.PROJECT, dailyTracking.getProject().getId())
			.set(DAILY_TRACKING.ACTIVITY_TYPE, dailyTracking.getActivityType())
			.set(DAILY_TRACKING.COMMENTS, dailyTracking.getComments())
			.set(DAILY_TRACKING.TASK, dailyTracking.getTask())
			.set(DAILY_TRACKING.COST, dailyTracking.getCost())
			.where(DAILY_TRACKING.ID.eq(dailyTracking.getId())).execute();
		
		ctx.log().debug("UPDATE DAILY_TRACKING id: " + dailyTracking.getId());		
		return dailyTracking;
	}
	
	public static DailyTracking insert(AONContext ctx, DailyTracking dailyTracking) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext()
				.insertInto(DAILY_TRACKING)
				.set(DAILY_TRACKING.DOMAIN, dailyTracking.getDomain().getId())
				.set(DAILY_TRACKING.TASK_HOLDER, dailyTracking.getTaskHolder().getId())
				.set(DAILY_TRACKING.TRACKING_DATE, toSql(dailyTracking.getTrackingDate()))
				.set(DAILY_TRACKING.TRACKING_DURATION, dailyTracking.getTrackingDuration())
				.set(DAILY_TRACKING.JOB_TYPE, dailyTracking.getJobType())
				.set(DAILY_TRACKING.REGISTRY, dailyTracking.getRegistry()!=null ? dailyTracking.getRegistry().getId() : null)
				.set(DAILY_TRACKING.PROJECT, dailyTracking.getProject()!=null ? dailyTracking.getProject().getId() : null)
				.set(DAILY_TRACKING.ACTIVITY_TYPE, dailyTracking.getActivityType())
				.set(DAILY_TRACKING.COMMENTS, dailyTracking.getComments())
				.set(DAILY_TRACKING.TASK, dailyTracking.getTask())
				.set(DAILY_TRACKING.COST, dailyTracking.getCost())
				.returning(DAILY_TRACKING.ID).fetchOne().getId();
		ctx.log().debug("INSERT DAILY_TRACKING id: " + id);		
		return dailyTracking.setId(id);
	}	

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getIdProperty().eq(id));
		ctx.log().debug("DELETE DAILY_TRACKING id:" + id);
	}
	
	private static void delete(AONContext ctx, DailyTrackingFilter filter) {
		ctx.getDslContext()
			.delete(DAILY_TRACKING)
			.where(DAILY_TRACKING_PROPERTIES.getConditions(filter))
			.execute();
	}

	public static class DailyTrackingFiller extends Filler implements Function<Record, DailyTracking> {

		@Override
		public DailyTracking apply(Record r) {
			return new DailyTracking()
				.setId(r.getValue(DAILY_TRACKING.ID))
				.setDomain(DomainFiller.build(r))
				.setTaskHolder(TaskHolderFiller.build(r, TASK_HOLDER, null))
				.setTrackingDate(new Date(r.getValue(DAILY_TRACKING.TRACKING_DATE).getTime()))
				.setTrackingDuration(r.getValue(DAILY_TRACKING.TRACKING_DURATION))
				.setJobType(r.getValue(DAILY_TRACKING.JOB_TYPE))
				.setRegistry(checkField(r, DAILY_TRACKING.REGISTRY) ? RegistryFiller.build(r, REGISTRY) : null )
				.setProject(checkField(r, DAILY_TRACKING.PROJECT) ? new Project().setId(r.getValue(DAILY_TRACKING.PROJECT)) : null)
				.setActivityType(r.getValue(DAILY_TRACKING.ACTIVITY_TYPE))
				.setComments(r.getValue(DAILY_TRACKING.COMMENTS))
				.setTask(r.getValue(DAILY_TRACKING.TASK))
				.setCost(r.getValue(DAILY_TRACKING.COST));
		}
	}
	
	private static java.sql.Date toSql(Date date) {
		return null!= date ? new java.sql.Date(date.getTime()) : null;
	}
	
}
