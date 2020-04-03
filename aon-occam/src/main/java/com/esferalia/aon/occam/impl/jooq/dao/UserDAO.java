package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.User.USER;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.UserRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.UserFilter;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.UserPropertiesDAO;

public class UserDAO {
	
	private static final UserPropertiesDAO USER_PROPERTIES = new UserPropertiesDAO();
	
	public static User getUser(AONContext ctx, Integer id) {

		UserRecord userRecord = ctx.getDslContext().selectFrom(USER)
				.where(USER.ID.eq(id)).fetchOne();

		User user = new User();
		user.setId(userRecord.getValue(USER.ID));
		user.setLogin(userRecord.getValue(USER.LOGIN));
		user.setName(userRecord.getValue(USER.NAME));
		user.setDomain(userRecord.getValue(USER.DOMAIN));

		return user;
	}
	
	public static User getUser(AONContext ctx, UserFilter filter) {
		return getUserStream(ctx, filter).findFirst().orElse(new User());
	}
	
	public static Stream<User> getUserStream(AONContext ctx, UserFilter filter) {
		return ctx.getDslContext().select()
		.from(USER)
		.where(USER_PROPERTIES.getConditions(filter))
		.fetch().stream().map(new UserFiller());
	}
	
	private static class UserFiller implements Function<Record, User> {
		@Override
		public User apply(Record r) {
			return new User()
					.setId(r.getValue(USER.ID))
					.setLogin(r.getValue(USER.LOGIN))
					.setName(r.getValue(USER.NAME))
					.setDomain(r.getValue(USER.DOMAIN))
					.setActive(r.getValue(USER.ACTIVE) == 1)
					.setRegistry(r.getValue(USER.REGISTRY))
//					.setRoles(¿?)
					;
		}
	}
}
