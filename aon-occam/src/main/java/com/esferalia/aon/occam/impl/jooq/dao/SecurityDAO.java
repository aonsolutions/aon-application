package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ApplicationRole.APPLICATION_ROLE;
import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.Auth.AUTH;
import static com.esferalia.aon.jooq.tables.Contact.CONTACT;
import static com.esferalia.aon.jooq.tables.ContactData.CONTACT_DATA;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Profile.PROFILE;
import static com.esferalia.aon.jooq.tables.ProfileRole.PROFILE_ROLE;
import static com.esferalia.aon.jooq.tables.Role.ROLE;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Signature.SIGNATURE;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.UserWorkgroup.USER_WORKGROUP;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.extension.DSLExtensions;
import com.esferalia.aon.jooq.tables.records.ContactRecord;
import com.esferalia.aon.jooq.tables.records.MailAccountRecord;
import com.esferalia.aon.jooq.tables.records.SignatureRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Contact;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.ContactFilter;
import com.esferalia.aon.occam.api.model.Filter.MailAccountFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.ScopeFilter;
import com.esferalia.aon.occam.api.model.Filter.SignatureFilter;
import com.esferalia.aon.occam.api.model.Filter.UserFilter;
import com.esferalia.aon.occam.api.model.Filter.UserScopeFilter;
import com.esferalia.aon.occam.api.model.Filter.UserWorkgroupFilter;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Properties.ContactProperties;
import com.esferalia.aon.occam.api.model.Properties.MailAccountProperties;
import com.esferalia.aon.occam.api.model.Properties.SignatureProperties;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserWorkgroup;
import com.esferalia.aon.occam.api.model.type.AonRole;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ScopePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.UserPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.UserScopePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.UserWorkgroupPropertiesDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SecurityDAO {
	private static final UserPropertiesDAO USER_PROPERTIES = new UserPropertiesDAO();
	private static final UserScopePropertiesDAO USER_SCOPE_PROPERTIES = new UserScopePropertiesDAO();
	private static final UserWorkgroupPropertiesDAO USER_WORKGROUP_PROPERTIES = new UserWorkgroupPropertiesDAO();
	private static final ScopePropertiesDAO SCOPE_PROPERTIES = new ScopePropertiesDAO();
	private static final SignaturePropertiesDAO SIGNATURE_PROPERTIES = new SignaturePropertiesDAO();
	protected static class SignaturePropertiesDAO implements SignatureProperties {
		protected Condition[] getConditions(SignatureFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(SIGNATURE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(SIGNATURE.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(SIGNATURE.NAME);}
		@Override public Property<String> getSignatureProperty() {return new FilterDAO.PropertyDAO<String>(SIGNATURE.SIGNATURE_);}
		@Override public Property<Integer> getUserIdProperty() {return new FilterDAO.PropertyDAO<Integer>(SIGNATURE.USER_ID);}
	}

	public static Auth getAuth(AONContext ctx, String email) {
		return ctx.getDslContext()
			.select(DSLExtensions.hex(AUTH.ID), AUTH.EMAIL, AUTH.PASSWORD)
			.from(AUTH)
			.where(AUTH.EMAIL.eq(email))
			.fetch().stream().map(new AuthFiller()).findFirst().orElse(new Auth());
	}
	
	public static Integer[] getAuthDomains (AONContext ctx, byte[] auth) {
		return ctx.getDslContext()
			.select(USER.DOMAIN)
			.from(USER)
			.where(USER.AUTH.eq(auth))
			.fetch().stream().map(r -> r.getValue(USER.DOMAIN)).toArray(Integer[]::new);
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

	public static byte[] unHexUuid(AONContext ctx, String uuid) {
		return ctx.getDslContext().select(DSLExtensions.unhex(uuid)).stream().map(r -> r.value1()).findFirst().get();
	}
	
	public static Auth insertAuth(AONContext ctx, Auth auth) {
		String uuid = ctx.getDslContext().fetch("select uuid();").stream().map(r -> r.getValue(0).toString()).findFirst().get().replace("-", "");
		ctx.getDslContext().insertInto(AUTH)
			.set(AUTH.ID, unHexUuid(ctx, uuid))
			.set(AUTH.EMAIL, auth.getEmail())
			.set(AUTH.PASSWORD, auth.getPassword())
			.execute();
		
		return getAuth(ctx, auth.getEmail());
	}
	
	public static User getUser(AONContext ctx, Integer userId) {
		ctx.checkRead();
		Record6<Integer, Integer, String, String, Byte, Integer> record = 
			ctx.getDslContext()
				.select(USER.ID, 
						USER.DOMAIN, 
						USER.NAME, 
						USER.LOGIN,
						USER.ACTIVE,
						USER.REGISTRY)
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
			user.setRegistry(record.getValue(USER.REGISTRY));
			user.setRoles( SecurityDAO.getUserRoles(ctx, user.getId()));
		}
		return user;
	}
	
	public static String getUserPassword(AONContext ctx, Integer userId) {
		return ctx.getDslContext().select(USER.PASSWORD)
				.from(USER).where(USER.ID.eq(userId)).fetchOne().getValue(USER.PASSWORD);
	}

	public static User getUser(AONContext ctx) {
		return getUser(ctx,ctx.getUser());
	}
	
	public static LinkedList<User> getUsersByEmail(AONContext ctx, String email){
		return ctx.getDslContext().select()
		.from(USER).join(MAIL_ACCOUNT).on(USER.ID.eq(MAIL_ACCOUNT.USER_ID))
		.where(MAIL_ACCOUNT.EMAIL.eq(email))
		.fetch().stream().map(new UserFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<User> getUsersByScope(AONContext ctx, Integer scope){
		return ctx.getDslContext().select()
		.from(USER).join(USER_SCOPE).on(USER.ID.eq(USER_SCOPE.USER_ID))
		.where(USER.DOMAIN.eq(ctx.getDomainId()))
		.and(USER_SCOPE.SCOPE.eq(scope))
		.fetch().stream().map(new UserFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	
	public static LinkedList<Domain> getCompaniesByScope(AONContext ctx, Integer scope){
		
		return ctx.getDslContext().select()
		.from(DOMAIN).join(SCOPE).on(DOMAIN.SCOPE.eq(SCOPE.ID))
		.where(DOMAIN.PARENT.eq(ctx.getDomainId()))
		.and(SCOPE.ID.eq(scope))
		.fetch().stream().map(new DomainFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	

	public static class DomainFiller  implements Function<Record, Domain> {

		@Override
		public Domain apply(Record record) {
			return new Domain()
				.setId(record.getValue(DOMAIN.ID))
				.setName(record.getValue(DOMAIN.NAME))
				.setDescription(record.getValue(DOMAIN.DESCRIPTION));
		}
		
	}
	
	public static class AuthFiller  implements Function<Record3<String, String, String>,Auth> {

		@Override
		public Auth apply(Record3<String, String, String> record) {
			return new Auth()
				.setUuid(record.value1())
				.setEmail(record.getValue(AUTH.EMAIL))
				.setPassword(record.getValue(AUTH.PASSWORD));
		}
		
	}
	
	public static class UserFiller  implements Function<Record,User> {

		@Override
		public User apply(Record record) {
			return new User()
				.setId(record.getValue(USER.ID))
				.setDomain(record.getValue(USER.DOMAIN))
				.setLogin(record.getValue(USER.LOGIN))
				.setActive(AonEnumUtils.getBoolean(record.getValue(USER.ACTIVE)))
				.setRegistry(record.getValue(USER.REGISTRY));
				//.setRoles( SecurityDAO.getUserRoles(ctx, user.getId()));
		}
		
	}
	
	public static Stream<User> getUserStream(AONContext ctx, UserFilter filter) {
		return USER_PROPERTIES.build(ctx.getDslContext().select().from(USER), filter)
				.fetch().stream().map(new UserFiller());
	}
	
	public static User getUser(AONContext ctx, UserFilter filter) {
		return getUserStream(ctx, filter).findFirst().orElse(new User());
	}
	
	public static User getUser(AONContext ctx, String login) {
		ctx.checkRead();
		Record6<Integer, Integer, String, String, Byte, Integer> record = 
			ctx.getDslContext()
				.select(USER.ID, 
						USER.DOMAIN, 
						USER.NAME, 
						USER.LOGIN,
						USER.ACTIVE,
						USER.REGISTRY)
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
								USER.ACTIVE,
								USER.REGISTRY)
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
			user.setRegistry(record.getValue(USER.REGISTRY));
			user.setRoles( SecurityDAO.getUserRoles(ctx, user.getId()));
		}
		return user;
	}
	
	public static void assignAuthToUser(AONContext ctx, User user, byte[] auth) {
		ctx.getDslContext().update(USER)
			.set(USER.AUTH, auth)
			.where(USER.ID.eq(user.getId()))
			.execute();
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
	
	public static Condition getSecurityLevelCondition(AONContext ctx, Field<Byte> field) {
		if (AonStringUtils.isBlank(ctx.getUser())) return DSL.trueCondition();
		return getSecurityLevelCondition(ctx,ctx.getUser(), field);
	}

	public static Condition getSecurityLevelCondition (AONContext ctx, String userLogin, Field<Byte> field) {
		ctx.checkRead();
		User user = getUser(ctx, userLogin);
		if (user == null) {
			throw new IllegalAccessError("Usuario no encontrado.");
		}
		if (user.hasConfidentialityRole()) {
			// Tiene el rol de confidencialidad por lo no hay que filtrar.
			return DSL.trueCondition();
		} else {
			// No tiene el rol de confidencialidad, solo puede ver lo oficial.
			return field.equal(SecurityLevel.OFFICIAL.value()); 
		}
		
	}
	public static Condition getUserScopesCondition (AONContext ctx, Field<Integer> field) {
		if (AonStringUtils.isBlank(ctx.getUser())) return DSL.trueCondition();
		return getUserScopesCondition(ctx,ctx.getUser(), field);
	}
	
	public static Condition getUserScopesCondition (AONContext ctx, String userLogin, Field<Integer> field) {
		Integer[] scopes = getUserScopes(ctx,userLogin);
		// No tiene scopes o tiene acceso a todo.
		if (scopes == null) return DSL.trueCondition();
		Condition c = null;
		for (Integer scope : scopes) {
			c = c == null?field.eq(scope):c.or(field.eq(scope));
		}
		return c;
	}

	public static Integer[] getUserScopes (AONContext ctx) {
		return getUserScopes(ctx, ctx.getUser());
	}
	public static Integer[] getUserScopes (AONContext ctx, String userLogin) {
		ctx.checkRead();
		User user = getUser(ctx, userLogin);
		if (user == null) {
			throw new IllegalAccessError("Usuario no encontrado.");
		}
		return getUserScopes(ctx, user.getId());
	}
	
	public static UserScope getUserScope(AONContext ctx, Integer userId, Integer scope) {
		return ctx.getDslContext().select()
				.from(USER_SCOPE)
				.where(USER_SCOPE.USER_ID.eq(userId))
				.and(USER_SCOPE.SCOPE.eq(scope))
				.fetch().stream().map(r -> new UserScope()
						.setDomain(r.getValue(USER_SCOPE.DOMAIN))
						.setId(r.getValue(USER_SCOPE.ID))
						.setScope(r.getValue(USER_SCOPE.SCOPE))
						.setUserId(r.getValue(USER_SCOPE.USER_ID)))
				.findFirst().orElse(null);
	}
	
	public static Integer[] getUserScopes (AONContext ctx, Integer userId) {
		ctx.checkRead();
		User user = getUser(ctx, userId);
		if (user == null) {
			throw new IllegalAccessError("Usuario no encontrado.");
		}
		// Es un usuario del dominio, por lo que hay que consultar los scopes del dominio
		int dom = user.getDomain();
		if ( user.getDomain() == ctx.getDomainId()) {
			final List<Integer> list = new ArrayList<Integer>();
			ctx.getDslContext()
				.select(USER_SCOPE.SCOPE)
					.from(USER_SCOPE)
					.where(USER_SCOPE.USER_ID.equal(userId))
					.fetch()
				.stream()
				.forEach(rec -> list.add( rec.getValue(USER_SCOPE.SCOPE) ) );
			if (list.size() == 0) {
				throw new AonCoreException("Usuario sin \u00E1mbitos definidos.");
			}
			Integer[] scopes = new Integer[list.size()];
			list.toArray(scopes);
			return scopes;
		} 
		
		// Comprabamos si es un usuario del dominio padre.
		Domain domain = DomainDAO.getDomain(ctx, ctx.getDomainId());
		int par = domain.getParentId();
		if ( dom == par ) {
			// Se trata de un usuario del dominio padre, por 
			// lo que tiene acceso a todos los scopes, se devuelve 
			// NULL, por lo que no hay que cruzar la tabla user_scope.
			return null;
		}
		
		// NO DEBE PASAR. 
		// Es un usuario que no pertenece al dominio en curso ni al dominio padre. 
		// Si ha llegado aqui es un error.
		throw new IllegalAccessError("Usuario sin permisos.");
	}
	public static LinkedList<Scope> getAvailableScopes (AONContext ctx) {
		return ctx.getDslContext()
			.select()
			.from(SCOPE)
			.where(getUserScopesCondition(ctx,SCOPE.ID))
			.and(SCOPE.DOMAIN.in(getInheritanceDomainIds(ctx)))
			.fetchInto(SCOPE)
			.stream()
			.map(new ScopeFiller())
			.collect(Collectors.toCollection(LinkedList::new))
			;
	}
	public static LinkedList<Scope> getDomainScopes(AONContext ctx) {
		return ctx.getDslContext()
			.select()
			.from(SCOPE)
			.where(SCOPE.DOMAIN.eq(ctx.getDomainId()))
			.fetchInto(SCOPE)
			.stream()
			.map(new ScopeFiller())
			.collect(Collectors.toCollection(LinkedList::new))
			;
	}

	public static Stream<Scope> getScopeStream(AONContext ctx, ScopeFilter filter){
		return ctx.getDslContext().select().from(SCOPE)
				.where(SCOPE_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new ScopeFiller());
	}
	
	public static Stream<Scope> getUserScopeStream(AONContext ctx,  Integer userId, ScopeFilter filter){
		return ctx.getDslContext().select().from(SCOPE)
				.join(USER_SCOPE).on(USER_SCOPE.SCOPE.eq(SCOPE.ID))
				.where(SCOPE_PROPERTIES.getConditions(filter))
				.and(USER_SCOPE.USER_ID.eq(userId))
				.fetch().stream().map(new ScopeFiller());
	}
	
	public static Stream<UserWorkgroup> getUserWorkgroupStream(AONContext ctx, UserWorkgroupFilter filter){
		return ctx.getDslContext().select().from(USER_WORKGROUP)
				.where(USER_WORKGROUP_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new UserWorkgroupFiller());
	}
	
	public static Scope insertScope(AONContext ctx, Scope scope){
		return ctx.getDslContext().insertInto(SCOPE, SCOPE.DOMAIN, SCOPE.DESCRIPTION)
			.values(scope.getDomain(), scope.getDescription())
			.returning().fetch().stream().map(new ScopeFiller())
			.findFirst().orElse(new Scope());
	}
	
	public static Integer deleteScope(AONContext ctx, Integer scopeId){
		ctx.getDslContext().delete(SCOPE).where(SCOPE.ID.eq(scopeId)).execute();
		return scopeId;
	}
	
	public static void insertUserScope(AONContext ctx, UserScope userScope){
		ctx.getDslContext().insertInto(USER_SCOPE, USER_SCOPE.DOMAIN, USER_SCOPE.SCOPE, USER_SCOPE.USER_ID)
			.values(userScope.getDomain(), userScope.getScope(), userScope.getUserId()).execute();
	}
	
	public static void deleteUserScope(AONContext ctx, UserScopeFilter filter){
		ctx.getDslContext()
			.delete(USER_SCOPE)
			.where(USER_SCOPE_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	private static class ScopeFiller implements Function<Record, Scope> {
		@Override
		public Scope apply(Record r) {
			return new Scope()
				.setId(r.getValue(SCOPE.ID))
				.setDomain(r.getValue(SCOPE.DOMAIN))
				.setDescription(r.getValue(SCOPE.DESCRIPTION));
		}
	}
	
	private static class UserWorkgroupFiller implements Function<Record, UserWorkgroup> {
		@Override
		public UserWorkgroup apply(Record r) {
			return new UserWorkgroup()
				.setId(r.getValue(USER_WORKGROUP.ID))
				.setDomain(r.getValue(USER_WORKGROUP.DOMAIN))
				.setUserId(r.getValue(USER_WORKGROUP.USER_ID))
				.setWorkgroup(r.getValue(USER_WORKGROUP.WORKGROUP));
		}
	}

	public static Signature getSignature(AONContext ctx, Integer signatureId){
		return ctx.getDslContext().select().from(SIGNATURE)
				.where(SIGNATURE.ID.eq(signatureId)).limit(1).fetchInto(SIGNATURE)
				.stream().map(new FullSignatureFiller()).findFirst().orElse(new Signature());
	}
	
	public static Signature getSignature(AONContext ctx, SignatureFilter filter){
		return ctx.getDslContext().select().from(SIGNATURE)
				.where(SIGNATURE_PROPERTIES.getConditions(filter)).limit(1).fetchInto(SIGNATURE)
				.stream().map(new FullSignatureFiller()).findFirst().orElse(new Signature());
	}
	
	public static LinkedList<Signature> getSignatureList(AONContext ctx, SignatureFilter filter){
		return ctx.getDslContext().select().from(SIGNATURE)
				.where(SIGNATURE_PROPERTIES.getConditions(filter)).fetchInto(SIGNATURE)
				.stream().map(new FullSignatureFiller()).collect(Collectors.toCollection(LinkedList::new));
	}

	
	private static class FullSignatureFiller implements Function<SignatureRecord, Signature> {
		@Override
		public Signature apply(SignatureRecord r) {
			return new Signature()
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setName(r.getName())
					.setSignature(r.getSignature())
					.setUserId(r.getUserId())
					;
		}
	}
	
	public static MailAccount getMailAccount(AONContext ctx, MailAccountFilter filter){
		return ctx.getDslContext().select().from(MAIL_ACCOUNT).where(MAIL_ACCOUNT_PROPERTIES.getConditions(filter))
		.limit(1).fetchInto(MAIL_ACCOUNT).stream().map(new FullMailAccountFiller()).findFirst().orElse(new MailAccount());
	}
	
	public static LinkedList<MailAccount> getMailAccountList(AONContext ctx, MailAccountFilter filter){
		return ctx.getDslContext().select().from(MAIL_ACCOUNT).where(MAIL_ACCOUNT_PROPERTIES.getConditions(filter))
		.fetchInto(MAIL_ACCOUNT).stream().map(new FullMailAccountFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	private static final MailAccountPropertiesDAO MAIL_ACCOUNT_PROPERTIES = new MailAccountPropertiesDAO();

	protected static class MailAccountPropertiesDAO implements MailAccountProperties {
		protected Condition[] getConditions(MailAccountFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(MAIL_ACCOUNT.ID);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.NAME);}
		@Override public Property<String> getEmailProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.EMAIL);}
		@Override public Property<Integer> getSignatureProperty() {return new FilterDAO.PropertyDAO<Integer>(MAIL_ACCOUNT.SIGNATURE);}
		@Override public Property<Byte> getDefaultAccountProperty() {return new FilterDAO.PropertyDAO<Byte>(MAIL_ACCOUNT.DEFAULT_ACCOUNT);}
		@Override public Property<String> getDisplayNameProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.DISPLAY_NAME);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(MAIL_ACCOUNT.DOMAIN);}
		@Override public Property<String> getDraftFolderProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.DRAFT_FOLDER);}
		@Override public Property<String> getIncomingHostProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.INCOMING_HOST);}
		@Override public Property<Integer> getIncomingPortProperty() {return new FilterDAO.PropertyDAO<Integer>(MAIL_ACCOUNT.INCOMING_PORT);}
		@Override public Property<Byte> getIncomingSecurityProperty() {return new FilterDAO.PropertyDAO<Byte>(MAIL_ACCOUNT.INCOMING_SECURITY);}
		@Override public Property<String> getMailUsernameProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.MAIL_USERNAME);}
		@Override public Property<String> getOutgoingHostProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.OUTGOING_HOST);}
		@Override public Property<Integer> getOutgoingPortProperty() {return new FilterDAO.PropertyDAO<Integer>(MAIL_ACCOUNT.OUTGOING_PORT);}
		@Override public Property<Byte> getOutgoingSecurityProperty() {return new FilterDAO.PropertyDAO<Byte>(MAIL_ACCOUNT.OUTGOING_SECURITY);}
		@Override public Property<Byte> getOutgoingVerificationProperty() {return new FilterDAO.PropertyDAO<Byte>(MAIL_ACCOUNT.OUTGOING_VERIFICATION);}
		@Override public Property<String> getPasswordProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.PASSWORD);}
		@Override public Property<String> getProtocolProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.PROTOCOL);}
		@Override public Property<String> getReplytoMailProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.REPLYTO_MAIL);}
		@Override public Property<String> getSentFolderProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.SENT_FOLDER);}
		@Override public Property<String> getSpamFolderProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.SPAM_FOLDER);}
		@Override public Property<String> getTrashFolderProperty() {return new FilterDAO.PropertyDAO<String>(MAIL_ACCOUNT.TRASH_FOLDER);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(MAIL_ACCOUNT.TYPE);}
		@Override public Property<Integer> getUserIdProperty() {return new FilterDAO.PropertyDAO<Integer>(MAIL_ACCOUNT.USER_ID);}
	}
	
	private static class FullMailAccountFiller implements Function<MailAccountRecord, MailAccount> {
		@Override
		public MailAccount apply(MailAccountRecord r) {
			return new MailAccount()
					.setDefaultAccount(r.getDefaultAccount())
					.setDisplayName(r.getDisplayName())
					.setDomain(r.getDomain())
					.setDraftFolder(r.getDraftFolder())
					.setEmail(r.getEmail())
					.setId(r.getId())
					.setIncomingHost(r.getIncomingHost())
					.setIncomingPort(r.getIncomingPort())
					.setIncomingSecurity(r.getIncomingSecurity())
					.setMailUsername(r.getMailUsername())
					.setName(r.getName())
					.setOutgoingHost(r.getOutgoingHost())
					.setOutgoingPort(r.getOutgoingPort())
					.setOutgoingSecurity(r.getOutgoingSecurity())
					.setOutgoingVerification(r.getOutgoingVerification())
					.setPassword(r.getPassword())
					.setProtocol(r.getProtocol())
					.setReplytoMail(r.getReplytoMail())
					.setSentFolder(r.getSentFolder())
					.setSignatureId(r.getSignature())
					.setSpamFolder(r.getSpamFolder())
					.setTrashFolder(r.getTrashFolder())
					.setType(r.getType())
					.setUserId(r.getUserId());
		}
	}
	
	public static Contact getContact(AONContext ctx, ContactFilter filter){
		return ctx.getDslContext().select().from(CONTACT).where(CONTACT_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(CONTACT).stream().map(new FullContactFiller()).findFirst().orElse(new Contact());
	}
	
	public static LinkedList<Contact> getContactList(AONContext ctx, ContactFilter filter){
		return ctx.getDslContext().select().from(CONTACT).where(CONTACT_PROPERTIES.getConditions(filter))
				.fetchInto(CONTACT).stream().map(new FullContactFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static String getContactEmail(AONContext ctx, Integer contactDataId){
		return ctx.getDslContext().select(CONTACT_DATA.EMAIL).from(CONTACT_DATA).where(CONTACT_DATA.ID.eq(contactDataId))
				.limit(1).fetchAny().getValue(CONTACT_DATA.EMAIL);
	}
	
	private static final ContactPropertiesDAO CONTACT_PROPERTIES = new ContactPropertiesDAO();

	protected static class ContactPropertiesDAO implements ContactProperties {
		protected Condition[] getConditions(ContactFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(CONTACT.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(CONTACT.DOMAIN);}
		@Override public Property<Integer> getUserIdProperty() {return new FilterDAO.PropertyDAO<Integer>(CONTACT.USER_ID);}
		@Override public Property<String> getDisplayNameProperty() {return new FilterDAO.PropertyDAO<String>(CONTACT.DISPLAYNAME);}
		@Override public Property<Integer> getContactDataProperty() {return new FilterDAO.PropertyDAO<Integer>(CONTACT.CONTACT_DATA);}
	}
	
	private static class FullContactFiller implements Function<ContactRecord, Contact> {
		@Override
		public Contact apply(ContactRecord r) {
			return new Contact()
					.setId(r.getId())
					.setDomain(r.getDomain())
					.setUserId(r.getUserId())
					.setDisplayName(r.getDisplayname())
					.setContactData(r.getContactData())
					;
		}
	}
	
//	public static Integer[] getUserScopes(String domainName, int domainId, Integer id) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	public static Scope getScopeFromRegistry(AONContext ctx, boolean payment, Integer registry) {
		Scope scope = null;
		if (payment) {
			scope = getScopeFromCreditor(ctx, registry);
			if (scope == null) {
				scope = getScopeFromSupplier(ctx, registry);	
			}
		} else {
			scope = getScopeFromCustomer(ctx, registry);
		}
		return scope;
	}
	
	public static Scope getScopeFromCustomer(AONContext ctx, Integer id) {
		Record record = ctx.getDslContext()
			.select(SCOPE.fields())
				.from(CUSTOMER)
				.join(SCOPE).on(CUSTOMER.SCOPE.equal(SCOPE.ID))
				.where(CUSTOMER.REGISTRY.equal(id))
				.fetch()
				.stream()
				.findFirst()
				.orElse(null);
		return (record == null)
			? null
			: new Scope()
				.setId(record.getValue(SCOPE.ID))
				.setDomain(record.getValue(SCOPE.DOMAIN))
				.setDescription(record.getValue(SCOPE.DESCRIPTION));
	}
	public static Scope getScopeFromSupplier(AONContext ctx, Integer id) {
		Record record = ctx.getDslContext()
			.select(SCOPE.fields())
				.from(SUPPLIER)
				.join(SCOPE).on(SUPPLIER.SCOPE.equal(SCOPE.ID))
				.where(SUPPLIER.REGISTRY.equal(id))
				.fetch()
				.stream()
				.findFirst()
				.orElse(null);
		return (record == null)
			? null
			: new Scope()
				.setId(record.getValue(SCOPE.ID))
				.setDomain(record.getValue(SCOPE.DOMAIN))
				.setDescription(record.getValue(SCOPE.DESCRIPTION));
	}
	public static Scope getScopeFromCreditor(AONContext ctx, Integer id) {
		Record record = ctx.getDslContext()
			.select(SCOPE.fields())
			   .from(CREDITOR)
			   .join(SCOPE).on(CREDITOR.SCOPE.equal(SCOPE.ID))
				.where(CREDITOR.REGISTRY.equal(id))
			   .fetch()
			   .stream()
			   .findFirst()
			   .orElse(null);
		return (record == null)
			? null
			: new Scope()
				.setId(record.getValue(SCOPE.ID))
				.setDomain(record.getValue(SCOPE.DOMAIN))
				.setDescription(record.getValue(SCOPE.DESCRIPTION));
	}
	
	public static Scope getScopeFromContract(AONContext ctx, Date dueDate, Integer registry) {
		Record record = ctx.getDslContext()
				.select(SCOPE.fields())
				   .from(CONTRACT)
				   .join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
				   .join(SCOPE).on(WORKPLACE.SCOPE.equal(SCOPE.ID))
				   .where(CONTRACT.PERSON.equal(registry))
				   .and(CONTRACT.END_DATE.isNull())
				   .orderBy(CONTRACT.START_DATE.desc())
				   .fetch()
				   .stream()
				   .findFirst()
				   .orElse(null);
		if (record == null) {
			record = ctx.getDslContext()
					.select(SCOPE.fields())
					   .from(CONTRACT)
					   .join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
					   .join(SCOPE).on(WORKPLACE.SCOPE.equal(SCOPE.ID))
					   .where(CONTRACT.PERSON.equal(registry))
					   .orderBy(CONTRACT.START_DATE.desc(),CONTRACT.END_DATE.desc())
					   .fetch()
					   .stream()
					   .findFirst()
					   .orElse(null);			
		}
		return (record == null)
				? null
				: new Scope()
					.setId(record.getValue(SCOPE.ID))
					.setDomain(record.getValue(SCOPE.DOMAIN))
					.setDescription(record.getValue(SCOPE.DESCRIPTION));
		
	}

	public static Condition getDomainInheritanceCondition(AONContext ctx, Field<Integer> field) {
		return (field.in(getInheritanceDomainIds(ctx))); 
	}
	
	public static Integer[] getInheritanceDomainIds(AONContext ctx) {
		return getInheritanceDomainIds(ctx, ctx.getDomainId());
	}
	
	public static Integer[] getInheritanceDomainIds(AONContext ctx, int domain) {
		Integer parentDomain = ctx.getDslContext()
				.select(DOMAIN.PARENT)
				.from(DOMAIN)
				.where(DOMAIN.ID.eq(domain))
				.and(DOMAIN.ENABLEHEREDITY.eq((byte) 1))
				.and(DOMAIN.PARENT.isNotNull())
				.fetch()
				.stream()
				.map(rec -> rec.getValue(DOMAIN.PARENT))
				.findFirst()
				.orElse( null )
				;
		return parentDomain == null 
				? new Integer[]{domain}
				: new Integer[]{domain,parentDomain};
	}

}

