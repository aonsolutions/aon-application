package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ApplicationRole.APPLICATION_ROLE;
import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Profile.PROFILE;
import static com.esferalia.aon.jooq.tables.ProfileRole.PROFILE_ROLE;
import static com.esferalia.aon.jooq.tables.Role.ROLE;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jooq.Record5;

import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonRole;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class SecurityDAO {
	
	public static Domain getDomain(AONContext ctx, int domain) {
		final Domain dom = new Domain();
		ctx.getDslContext()
			.select(DOMAIN.ID
					,DOMAIN.NAME
					,DOMAIN.PARENT
					,DOMAIN.TYPE
					,DOMAIN.DOMAINMANAGEMENT
					,DOMAIN.ACTIVE)
			.from(DOMAIN)
			.where(DOMAIN.ID.equal(domain))
			.fetch()
			.stream()
			.forEach( record -> {
				dom.setId(record.getValue(DOMAIN.ID));
				dom.setName(record.getValue(DOMAIN.NAME));
				dom.setActive(AonEnumUtils.getBoolean(record.getValue(DOMAIN.ACTIVE)));
				dom.setParentId(record.getValue(DOMAIN.PARENT));
				boolean parent = (record.getValue(DOMAIN.PARENT) == null);
				boolean domainManagement = AonEnumUtils.getBoolean(record.getValue(DOMAIN.DOMAINMANAGEMENT));
				if (!parent) {
					dom.setChild(true);
					dom.setStandalone(false);
					dom.setParent(false);
				} else {
					if (domainManagement) {
						dom.setParent(true);
						dom.setStandalone(false);
						dom.setChild(false);
					} else {
						dom.setParent(false);
						dom.setStandalone(true);
						dom.setChild(false);
					}
				}
			});
		return dom;
	}
	
	public static User getUser(AONContext ctx, Integer userId) {
		ctx.checkRead();
		Record5<Integer, Integer, String, String, Byte> record = 
			ctx.getDslContext()
				.select(USER.ID, 
						USER.DOMAIN, 
						USER.NAME, 
						USER.LOGIN,
						USER.ACTIVE)
				.from(USER)
				.where(USER.ID.equal(userId))
				.fetchOne();
		User user = null;
		if (record != null) {
			user = new User();
			user.setId(record.getValue(USER.ID));
			user.setDomain(record.getValue(USER.DOMAIN));
			user.setName(record.getValue(USER.NAME));
			user.setLogin(record.getValue(USER.LOGIN)); 
			user.setActive(AonEnumUtils.getBoolean(record.getValue(USER.ACTIVE)));
			user.setRoles( SecurityDAO.getUserRoles(ctx, user.getId()));
		}
		return user;
	}

	public static User getUser(AONContext ctx, String login) {
		ctx.checkRead();
		Record5<Integer, Integer, String, String, Byte> record = 
			ctx.getDslContext()
				.select(USER.ID, 
						USER.DOMAIN, 
						USER.NAME, 
						USER.LOGIN,
						USER.ACTIVE)
				.from(USER)
				.where(USER.DOMAIN.equal(ctx.getDomainId()))
				.and(USER.LOGIN.equal(login))
				.fetchOne();
		if (record == null) {
			com.esferalia.aon.jooq.tables.Domain PARENT_DOMAIN = DOMAIN.as("PARENT_DOMAIN");
			record = ctx.getDslContext()
						.select(USER.ID, 
								USER.DOMAIN, 
								USER.NAME, 
								USER.LOGIN,
								USER.ACTIVE)
						.from(DOMAIN)
						.join(PARENT_DOMAIN).on(DOMAIN.PARENT.equal(PARENT_DOMAIN.ID))
						.join(USER).on(USER.DOMAIN.equal(PARENT_DOMAIN.ID))
						.where(DOMAIN.ID.equal(ctx.getDomainId()))
						.and(USER.LOGIN.equal(login))
						.fetchOne();			
		}
		User user = new User();
		if (record != null) {
			user.setId(record.getValue(USER.ID));
			user.setDomain(record.getValue(USER.DOMAIN));
			user.setName(record.getValue(USER.NAME));
			user.setLogin(record.getValue(USER.LOGIN)); 
			user.setActive(AonEnumUtils.getBoolean(record.getValue(USER.ACTIVE)));
			user.setRoles( SecurityDAO.getUserRoles(ctx, user.getId()));
		}
		return user;
	}
	
	public static AonRole[] getUserRoles(AONContext ctx, Integer userId) {
		final List<AonRole> list = new ArrayList<AonRole>();
		ctx.getDslContext()
			.selectDistinct(ROLE.NAME)
			.from(USER)
			.join(APPLICATION_USER).on(APPLICATION_USER.USER_ID.equal(USER.ID))
			.join(APPLICATION_USER_PROFILE).on(APPLICATION_USER_PROFILE.APPLICATION_USER.equal(APPLICATION_USER.ID))
			.join(PROFILE).on(APPLICATION_USER_PROFILE.PROFILE.equal(PROFILE.ID))
			.join(PROFILE_ROLE).on(PROFILE_ROLE.PROFILE.equal(PROFILE.ID))
			.join(APPLICATION_ROLE).on(APPLICATION_ROLE.ID.equal(PROFILE_ROLE.APPLICATION_ROLE))
			.join(ROLE).on(APPLICATION_ROLE.ROLE.equal(ROLE.ID))
			.join(DOMAIN).on(DOMAIN.ID.equal(USER.DOMAIN).or(DOMAIN.PARENT.equal(USER.DOMAIN)))
			.and(USER.ID.equal(userId))
			.fetch()
			.stream()
			.forEach(rec -> {
				String role = rec.getValue(ROLE.NAME);
				list.add( AonRole.valueOfBDValue( role) );
				} );
		AonRole[] roles = new AonRole[list.size()];
		list.toArray(roles);
		return roles;
	}

	public static Integer[] getUserScopes (AONContext ctx, Integer userId) {
		ctx.checkRead();
		User user = getUser(ctx, userId);
		if (user == null) {
			// ??
			throw new IllegalAccessError("Usario no encontrado.");
		}
		// Es un usuario del dominio, por lo que es 
		// consultar los scopes del dominio
		if ( user.getDomain() == ctx.getDomainId() ) {
			final List<Integer> list = new ArrayList<Integer>();
			ctx.getDslContext()
				.select(USER_SCOPE.SCOPE)
					.from(USER_SCOPE)
					.where(USER_SCOPE.USER_ID.equal(userId))
					.fetch()
				.stream()
				.forEach(rec -> list.add( rec.getValue(USER_SCOPE.SCOPE) ) );
			Integer[] scopes = new Integer[list.size()];
			list.toArray(scopes);
			return scopes;
		} 
		
		// Comprabamos si es un usuario del dominio padre.
		Domain domain = getDomain(ctx, ctx.getDomainId());
		if ( user.getDomain() == domain.getParentId() ) {
			// Se trata de un usuario del cominio padre, por 
			// lo que tiene acceso a todos los scopes, se devuelve 
			// NULL, por lo que no hay que cruzar la tabla user_scope.
			return null;
		} else {
			// NO DEBE PASAR. Es un usuario que no pertenece 
			// al dominio en curso ni al dominio padre. 
			// Si ha llegado aqui es un error.
			throw new IllegalAccessError("Usario sin permisos.");
		}
	}
	
	public static Scope getScope(AONContext ctx, Integer scopeId){
		return ctx.getDslContext().select().from(SCOPE)
				.where(SCOPE.ID.eq(scopeId)).limit(1).fetchInto(SCOPE)
				.stream().map(new FullScopeFiller()).findFirst().orElse(new Scope());
	}
	
	private static class FullScopeFiller implements Function<ScopeRecord, Scope> {
		@Override
		public Scope apply(ScopeRecord r) {
			return new Scope()
					.setDescription(r.getDescription())
					.setDomain(r.getDomain())
					.setId(r.getId());
		}
	}

	public static Integer[] getUserScopes(String domainName, int domainId,
			Integer id) {
		// TODO Auto-generated method stub
		return null;
	}
}

