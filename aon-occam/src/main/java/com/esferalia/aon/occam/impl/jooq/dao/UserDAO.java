package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.User.USER;

import com.esferalia.aon.jooq.tables.records.UserRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.security.User;

public class UserDAO {

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
}
