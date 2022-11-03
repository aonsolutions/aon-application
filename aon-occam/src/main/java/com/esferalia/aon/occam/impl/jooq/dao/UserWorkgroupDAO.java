package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.UserWorkgroup.USER_WORKGROUP;
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
import com.esferalia.aon.occam.api.model.Filter.UserWorkgroupFilter;
import com.esferalia.aon.occam.api.model.Properties.UserWorkgroupProperties;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserWorkgroup;
import com.esferalia.aon.occam.impl.jooq.dao.TaskOldDAO.WorkgroupFiller;
import com.esferalia.aon.occam.impl.jooq.validation.UserWorkgroupValidation;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class UserWorkgroupDAO {

	private UserWorkgroupDAO() {
		throw new IllegalStateException("Utility class");
	}
	
	private static final UserWorkgroupPropertiesDAO USER_WORKGROUP_PROPERTIES = new UserWorkgroupPropertiesDAO();

	protected static class UserWorkgroupPropertiesDAO implements UserWorkgroupProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, UserWorkgroupFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(UserWorkgroupFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(USER_WORKGROUP.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(USER_WORKGROUP.DOMAIN);}
		@Override public Property<Integer> getUserIdProperty() {return new FilterDAO.PropertyDAO<>(USER_WORKGROUP.USER_ID);}
		@Override public Property<Integer> getWorkgroupProperty() {return new FilterDAO.PropertyDAO<>(USER_WORKGROUP.WORKGROUP);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, UserWorkgroupFilter filter) {
		return ctx.getDslContext().select()
				.from(USER_WORKGROUP).join(WORKGROUP).on(WORKGROUP.ID.eq(USER_WORKGROUP.WORKGROUP))
				.where(USER_WORKGROUP_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<UserWorkgroup> getStream(AONContext ctx, UserWorkgroupFilter filter){
		return select(ctx, filter).fetch().stream().map(new UserWorkgroupFiller());
	}
	
	public static List<UserWorkgroup> getList(AONContext ctx, UserWorkgroupFilter filter) {
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static UserWorkgroup get(AONContext ctx, UserWorkgroupFilter filter) {
		return select(ctx, filter).limit(1).fetch().stream().map(new UserWorkgroupFiller()).findFirst().orElse(new UserWorkgroup());
	}
	
	public static UserWorkgroup save(AONContext ctx, UserWorkgroup userWorkgroup) {
		
		UserWorkgroupValidation.validate(ctx, userWorkgroup);
		
		UserWorkgroup uw = UserWorkgroupDAO.get(ctx, f -> f.getUserIdProperty().eq(userWorkgroup.getUserId())
				.and(f.getWorkgroupProperty().eq(userWorkgroup.getWorkgroup().getId())));
		
		return uw.isEmpty() ? insert(ctx, userWorkgroup) : uw;
	}
	

	public static UserWorkgroup insert(AONContext ctx, UserWorkgroup userWorkgroup) {
		Integer id =  ctx.getDslContext().insertInto(USER_WORKGROUP)
				.set(USER_WORKGROUP.DOMAIN, userWorkgroup.getDomain())
				.set(USER_WORKGROUP.USER_ID, userWorkgroup.getUserId())
				.set(USER_WORKGROUP.WORKGROUP, userWorkgroup.getWorkgroup().getId())
				.returning(USER_WORKGROUP.ID).fetchOne().getValue(USER_WORKGROUP.ID);
		return userWorkgroup.setId(id);
	}
	
	protected static void delete(AONContext ctx, User user) {
		delete(ctx, f -> f.getUserIdProperty().eq(user.getId()));
	}
	
	protected static void delete(AONContext ctx, UserWorkgroupFilter filter) {
		ctx.getDslContext().delete(USER_WORKGROUP)
			.where(USER_WORKGROUP_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	public static class UserWorkgroupFiller implements Function<Record, UserWorkgroup> {
		@Override
		public UserWorkgroup apply(Record r) {
			return build(r);
		}
		
		public static UserWorkgroup build(Record r) {
			return new UserWorkgroup()
					.setId(r.getValue(USER_WORKGROUP.ID))
					.setDomain(r.getValue(USER_WORKGROUP.DOMAIN))
					.setUserId(r.getValue(USER_WORKGROUP.USER_ID))
					.setWorkgroup(WorkgroupFiller.buildWorkgroup(r));
		}
	}

}
