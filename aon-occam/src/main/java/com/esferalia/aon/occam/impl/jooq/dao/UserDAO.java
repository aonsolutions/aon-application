package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Auth.AUTH;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.Options;
import com.esferalia.aon.occam.api.model.Filter.UserFilter;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.security.UserType;
import com.esferalia.aon.occam.api.model.security.UserWorkgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.UserPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.AuthFiller;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class UserDAO {

	private UserDAO() {
		throw new IllegalStateException("Utility class");
	}
	
	private static final UserPropertiesDAO USER_PROPERTIES = new UserPropertiesDAO();

	private static SelectConditionStep<Record> select(AONContext ctx, UserFilter filter) {
		return ctx.getDslContext().select()
			.from(USER)
			.leftOuterJoin(AUTH).on(AUTH.ID.eq(USER.AUTH))
			.leftOuterJoin(REGISTRY).on(REGISTRY.ID.eq(USER.REGISTRY))
			.where(USER_PROPERTIES.getConditions(filter));
	}
	
	public static User get(AONContext ctx, UserFilter filter, Options...options) {
		User user = select(ctx, filter).limit(1).fetch()
			.stream().map(new UserFiller()).findFirst().orElse(new User());
		user.setTaskHolders(TaskHolderDAO.getList(ctx, f -> f.getUserIdProperty().eq(user.getId())));
		return user;
	}
	
	public static Stream<User> getStream(AONContext ctx, UserFilter filter, Options...options) {
		return options.length > 0 
		    ? getStream(ctx, filter, options[0])
		    : select(ctx, filter).fetch().stream().map(new UserFiller());
	}
	   
    public static Stream<User> getStream(AONContext ctx, UserFilter filter, Integer page, Integer perPage){
        return select(ctx, filter)
            .limit(perPage).offset(perPage * (page -1))
            .fetch().stream().map(new UserFiller());
    }
    
    private static Stream<User> getStream(AONContext ctx, UserFilter filter, Options options){
        if(options.isPagination())
            return getStream(ctx, filter, options.getPage(), options.getPerPage());
        else return getStream(ctx, filter);
    }
	
	public static User save(AONContext ctx, User user) {
		user = user.getId() != null 
				? update(ctx, user) 
				: insert(ctx, user);
				
		saveUserWorkgroups(ctx, user);
		return user;
	}
	
	public static User insert(AONContext ctx, User user) {
		Integer id = ctx.getDslContext().insertInto(USER)
			.set(USER.TYPE, user.getTypeValue())
			.set(USER.NAME, user.getName())
			.set(USER.LOGIN, user.getLogin())
			.set(USER.ACTIVE, user.isActive() ? (byte) 1 : (byte) 0)
			.set(USER.DOMAIN, user.getDomain())
			.set(USER.AUTH, user.getAuth().getAuth())
			.set(USER.SHARED, user.isShared() ? (byte) 1 : (byte) 0)
			.set(USER.ENTERPRISE, user.getEnterprise())
			.set(USER.TOOLBAR, user.getToolbar().value())
			.returning(USER.ID).fetchOne().getId();
		
		return user.setId(id);
	}
	
	public static User update(AONContext ctx, User user) {
		ctx.getDslContext().update(USER)
			.set(USER.TYPE, user.getTypeValue())
			.set(USER.NAME, user.getName())
			.set(USER.LOGIN, user.getLogin())
			.set(USER.ACTIVE, user.isActive() ? (byte) 1 : (byte) 0)
			.set(USER.DOMAIN, user.getDomain())
			.set(USER.AUTH, user.getAuth().getAuth())
			.set(USER.SHARED, user.isShared() ? (byte) 1 : (byte) 0)
			.set(USER.ENTERPRISE, user.getEnterprise())
			.set(USER.TOOLBAR, user.getToolbar().value())
			.where(USER.ID.eq(user.getId()))
			.execute();	
		return user;
	}
	
	public static void saveUserWorkgroups(AONContext ctx, User user){
		user.getWorkgroups().stream()
		.forEach(workgroup -> {
			if(workgroup.isRemoved()) {
				deleteUserWorkgroup(ctx, user, workgroup);
			} else {
				UserWorkgroupDAO.save(ctx, new UserWorkgroup()
						.setDomain(workgroup.getDomain())
						.setUserId(user.getId())
						.setWorkgroup(workgroup));

				TaskHolder th = TaskHolderDAO.get(ctx, f -> f.getUserIdProperty().eq(user.getId()));
				if(!th.isEmpty()) {
					TaskHolderWorkgroupDAO.save(ctx, new TaskHolderWorkgroup()
						.setDomain(workgroup.getDomain())
						.setTaskHolder(th.getId())
						.setWorkgroup(workgroup));
				}
			}
		});
	}
	
	public static void deleteUserWorkgroup(AONContext ctx, User user, Workgroup workgroup) {
		UserWorkgroupDAO.delete(ctx, f -> f.getUserIdProperty().eq(user.getId())
			.and(f.getWorkgroupProperty().eq(workgroup.getId())));
		TaskHolder th = TaskHolderDAO.get(ctx, f -> f.getUserIdProperty().eq(user.getId()));
		if(!th.isEmpty()) {
			TaskHolderWorkgroupDAO.delete(ctx, f -> f.getTaskHolderProperty().eq(th.getId())
				.and(f.getWorkgroupProperty().eq(workgroup.getId())));
		}
	}
	
	public static class UserFiller extends Filler implements Function<Record,User> {
		
		@Override
		public User apply(Record r) {
			return build(r);
		}
		
		public static User build(Record r) {
			return new User()
				.setId(r.getValue(USER.ID))
				.setDomain(r.getValue(USER.DOMAIN))
				.setType(UserType.safeValueOf(getValue(r, USER.TYPE)))
				.setName(r.getValue(USER.NAME))
				.setLogin(r.getValue(USER.LOGIN))
				.setActive(AonEnumUtils.getBoolean(r.getValue(USER.ACTIVE)))
				.setRegistry(checkField(r, REGISTRY.ID)
				        ? RegistryFiller.build(r)
				        : new Registry().setId(r.getValue(USER.REGISTRY)))
				.setAuth(checkField(r, AUTH.ID) && getValue(r, AUTH.ID) != null
						? AuthFiller.build(r)
						: new Auth().setAuth(r.getValue(USER.AUTH)))
				.setShared(AonEnumUtils.getBoolean(r.getValue(USER.SHARED)))
				.setToolbar(UserToolbar.safeValueOf(r.getValue(USER.TOOLBAR)))
				.setEnterprise(r.getValue(USER.ENTERPRISE))
				.setExpirationDate(getValue(r, USER.PASSWORDEXPIRATION));
		}		
	}

	
}
