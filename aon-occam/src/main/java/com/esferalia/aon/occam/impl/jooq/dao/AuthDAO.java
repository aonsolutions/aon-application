package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Auth.AUTH;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserAppRole.USER_APP_ROLE;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record8;

import com.esferalia.aon.jooq.extension.DSLExtensions;
import com.esferalia.aon.jooq.tables.records.UserAppRoleRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.AuthFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.AuthProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.security.Auth;

public class AuthDAO {

	private static final AuthPropertiesDAO AUTH_PROPERTIES = new AuthPropertiesDAO();
	protected static class AuthPropertiesDAO implements AuthProperties {
		protected Condition[] getConditions(AuthFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<byte[]> getIdProperty() {return new FilterDAO.PropertyDAO<>(AUTH.ID);}
		@Override public Property<String> getEmailProperty() {return new FilterDAO.PropertyDAO<>(AUTH.EMAIL);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(AUTH.NAME);}
		@Override public Property<String> getSurnameProperty() {return new FilterDAO.PropertyDAO<>(AUTH.SURNAME);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(AUTH.DOCUMENT);}
		@Override public Property<String> getPhoneProperty() {return new FilterDAO.PropertyDAO<>(AUTH.PHONE);}
	}

	public static Auth getAuth(AONContext ctx, byte[] auth) {
		return ctx.getDslContext()
			.select(AUTH.ID, DSLExtensions.hex(AUTH.ID), AUTH.EMAIL, AUTH.PASSWORD, AUTH.NAME, AUTH.SURNAME, AUTH.DOCUMENT, AUTH.PHONE)
			.from(AUTH)
			.where(AUTH.ID.eq(auth))
			.fetch().stream().map(new AuthFiller()).findFirst().orElse(new Auth());
	}

	public static Stream<Auth> getAuthStream(AONContext ctx, AuthFilter filter) {
		return ctx.getDslContext()
			.select(AUTH.ID, DSLExtensions.hex(AUTH.ID), AUTH.EMAIL, AUTH.PASSWORD, AUTH.NAME, AUTH.SURNAME, AUTH.DOCUMENT, AUTH.PHONE)
			.from(AUTH)
			.where(AUTH_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new AuthFiller());
	}
	
	public static Auth getAuth(AONContext ctx, String email) {
		return ctx.getDslContext()
			.select(AUTH.ID, DSLExtensions.hex(AUTH.ID), AUTH.EMAIL, AUTH.PASSWORD, AUTH.NAME, AUTH.SURNAME, AUTH.DOCUMENT, AUTH.PHONE)
			.from(AUTH)
			.where(AUTH.EMAIL.eq(email))
			.fetch().stream().map(new AuthFiller()).findFirst().orElse(new Auth());
	}
	
	public static Auth getAuthByDocument(AONContext ctx, String document) {
		return ctx.getDslContext()
			.select(AUTH.ID, DSLExtensions.hex(AUTH.ID), AUTH.EMAIL, AUTH.PASSWORD, AUTH.NAME, AUTH.SURNAME, AUTH.DOCUMENT, AUTH.PHONE)
			.from(AUTH)
			.where(AUTH.DOCUMENT.eq(document))
			.fetch().stream().map(new AuthFiller()).findFirst().orElse(new Auth());
	}
	
	public static Integer[] getAuthScopes (AONContext ctx, byte[] auth) {
		return ctx.getDslContext()
			.select(USER_SCOPE.SCOPE)
			.from(USER_SCOPE)
			.where(USER_SCOPE.USER_ID.in(
					ctx.getDslContext()
						.select(USER.ID)
						.from(USER)
						.where(USER.AUTH.eq(auth))))
			.fetch().stream().map(r -> r.getValue(USER_SCOPE.SCOPE)).toArray(Integer[]::new);
	}

	public static Integer[] getAuthDomains (AONContext ctx, byte[] auth) {
		return ctx.getDslContext()
			.select()
			.from(USER)
			.where(USER.AUTH.eq(auth))
			.fetch(USER.DOMAIN).stream().toArray(Integer[]::new);
	}

	public static DomainUserRoles[] getAuthDomainsUserRoles (AONContext ctx, byte[] auth) {
		return ctx.getDslContext()
			.select()
			.from(USER)
			.join(USER_APP_ROLE)
			.on(USER_APP_ROLE.USER_ID.eq(USER.ID))
			.where(USER.AUTH.eq(auth))
			//.and(USER_APP_ROLE.APP.eq((byte)-1))
			.fetchStreamInto(USER_APP_ROLE)
			.collect(Collectors.groupingBy(UserAppRoleRecord::getDomain))
			.entrySet().stream().map( e -> 
					new DomainUserRoles()
					.setDomain(new Domain().setId(e.getKey()))
					.setDomainUserRoles(e.getValue().stream().map(UserAppRoleRecord::getRole).map(AonRole::safeValueOf).toList())
			)
			.toArray(DomainUserRoles[]::new);
	}

	public static byte[] unHexUuid(AONContext ctx, String uuid) {
		return ctx.getDslContext().select(DSLExtensions.unhex(uuid)).stream().map(r -> r.value1()).findFirst().orElse(new byte[]{});
	}
	
	public static Auth saveAuth(AONContext ctx, Auth auth) {
		Auth existingAuth = getAuth(ctx, auth.getEmail());
		if (existingAuth.getAuth() != null) {
			return updateAuth(ctx, auth);
		} else {
			return insertAuth(ctx,  auth);
		}
	}
	
	public static Auth insertAuth(AONContext ctx, Auth auth) {
		String uuid = ctx.getDslContext().fetch("select uuid();").stream().map(r -> r.getValue(0).toString()).findFirst().get().replace("-", "");
		ctx.getDslContext().insertInto(AUTH)
			.set(AUTH.ID, unHexUuid(ctx, uuid))
			.set(AUTH.EMAIL, auth.getEmail())
			.set(AUTH.PASSWORD, auth.getPassword())
			.set(AUTH.NAME, auth.getName())
			.set(AUTH.SURNAME, auth.getSurname())
			.set(AUTH.DOCUMENT, auth.getDocument())
			.set(AUTH.PHONE, auth.getPhone())
			.execute();
		
		return getAuth(ctx, auth.getEmail());
	}
	
	public static Auth updateAuth(AONContext ctx, Auth auth) {
		ctx.getDslContext().update(AUTH)
			.set(AUTH.NAME, auth.getName())
			.set(AUTH.SURNAME, auth.getSurname())
			.set(AUTH.DOCUMENT, auth.getDocument())
			.set(AUTH.PHONE, auth.getPhone())
			.where(AUTH.ID.eq(auth.getAuth()))
			.execute();
		return getAuth(ctx, auth.getEmail());
	}
	
	public static Auth updateAuthPassword(AONContext ctx, Auth auth) {
		ctx.getDslContext().update(AUTH)
			.set(AUTH.PASSWORD, auth.getPassword())
			.where(AUTH.ID.eq(auth.getAuth()))
			.execute();
		return auth;
	}
	
	public static Auth updateUserPassword(AONContext ctx, Auth auth) {
		ctx.getDslContext().update(USER)
			.set(USER.PASSWORD, auth.getPassword())
			.where(USER.AUTH.eq(auth.getAuth()))
			.execute();
		return auth;
	}
	
	public static class AuthFiller extends Filler implements Function<Record8<byte[], String, String, String, String, String, String, String>,Auth> {

		@Override
		public Auth apply(Record8<byte[], String, String, String, String, String, String, String> r) {
			return build(r)
					.setUuid(r.value2());
		}
		
		public static Auth build(Record r) {
			return new Auth()
				.setAuth(r.getValue(AUTH.ID))
				.setEmail(r.getValue(AUTH.EMAIL))
				.setName(r.getValue(AUTH.NAME))
				.setSurname(r.getValue(AUTH.SURNAME))
				.setDocument(r.getValue(AUTH.DOCUMENT))
				.setPhone(r.getValue(AUTH.PHONE))
				.setPassword(r.getValue(AUTH.PASSWORD));
		}
		
	}
	
}
