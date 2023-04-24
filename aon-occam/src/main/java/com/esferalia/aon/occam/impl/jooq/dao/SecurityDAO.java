package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ActionDenied.ACTION_DENIED;
import static com.esferalia.aon.jooq.tables.ActionFavorite.ACTION_FAVORITE;
import static com.esferalia.aon.jooq.tables.ApplicationRole.APPLICATION_ROLE;
import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.Auth.AUTH;
import static com.esferalia.aon.jooq.tables.Contact.CONTACT;
import static com.esferalia.aon.jooq.tables.ContactData.CONTACT_DATA;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.DataAttach.DATA_ATTACH;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApp.DOMAIN_APP;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.DomainApplicationModule.DOMAIN_APPLICATION_MODULE;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Profile.PROFILE;
import static com.esferalia.aon.jooq.tables.ProfileRole.PROFILE_ROLE;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Role.ROLE;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Session.SESSION;
import static com.esferalia.aon.jooq.tables.Signature.SIGNATURE;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserAppRole.USER_APP_ROLE;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.UserWorkgroup.USER_WORKGROUP;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.api.model.attachment.DataAttachSource.SISTEMA_RED;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.DIGITAL_CERTIFICATE;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.extension.DSLExtensions;
import com.esferalia.aon.jooq.tables.records.ContactRecord;
import com.esferalia.aon.jooq.tables.records.MailAccountRecord;
import com.esferalia.aon.jooq.tables.records.SignatureRecord;
import com.esferalia.aon.jooq.tables.records.UserRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Contact;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.AuthFilter;
import com.esferalia.aon.occam.api.model.Filter.CertificateFilter;
import com.esferalia.aon.occam.api.model.Filter.ContactFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainAppFilter;
import com.esferalia.aon.occam.api.model.Filter.MailAccountFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.ScopeFilter;
import com.esferalia.aon.occam.api.model.Filter.SignatureFilter;
import com.esferalia.aon.occam.api.model.Filter.UserAppRoleFilter;
import com.esferalia.aon.occam.api.model.Filter.UserFilter;
import com.esferalia.aon.occam.api.model.Filter.UserScopeFilter;
import com.esferalia.aon.occam.api.model.Filter.UserWorkgroupFilter;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.MailAccountType;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.Properties.AuthProperties;
import com.esferalia.aon.occam.api.model.Properties.CertificateProperties;
import com.esferalia.aon.occam.api.model.Properties.ContactProperties;
import com.esferalia.aon.occam.api.model.Properties.MailAccountProperties;
import com.esferalia.aon.occam.api.model.Properties.SignatureProperties;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.security.CertificateNotFoundException;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserWorkgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.AonRole;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.impl.jooq.dao.CertificateDAO.CertificatePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.DomainAppPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ScopePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.UserAppRolePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.UserPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.UserScopePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO.UserFiller;
import com.esferalia.aon.occam.impl.jooq.dao.UserWorkgroupDAO.UserWorkgroupFiller;
import com.esferalia.aon.occam.impl.jooq.dao.UserWorkgroupDAO.UserWorkgroupPropertiesDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SecurityDAO {

	private SecurityDAO() {
		throw new IllegalStateException("Utility class");
	}
	
	private static final String DIGITAL_CERTIFICATE_PASSWORD = "DIGITAL_CERTIFICATE_PASSWORD";
	private static final UserPropertiesDAO USER_PROPERTIES = new UserPropertiesDAO();
	private static final UserScopePropertiesDAO USER_SCOPE_PROPERTIES = new UserScopePropertiesDAO();
	private static final UserWorkgroupPropertiesDAO USER_WORKGROUP_PROPERTIES = new UserWorkgroupPropertiesDAO();
	private static final ScopePropertiesDAO SCOPE_PROPERTIES = new ScopePropertiesDAO();
	private static final SignaturePropertiesDAO SIGNATURE_PROPERTIES = new SignaturePropertiesDAO();
	private static final DomainAppPropertiesDAO DOMAIN_APP_PROPERTIES = new DomainAppPropertiesDAO();
	private static final UserAppRolePropertiesDAO USER_APP_ROLE_PROPERTIES = new UserAppRolePropertiesDAO();
	private static final CertificatePropertiesDAO CERTIFICATE_PROPERTIES = new CertificatePropertiesDAO();
	protected static class SignaturePropertiesDAO implements SignatureProperties {
		protected Condition[] getConditions(SignatureFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(SIGNATURE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(SIGNATURE.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(SIGNATURE.NAME);}
		@Override public Property<String> getSignatureProperty() {return new FilterDAO.PropertyDAO<>(SIGNATURE.SIGNATURE_);}
		@Override public Property<Integer> getUserIdProperty() {return new FilterDAO.PropertyDAO<>(SIGNATURE.USER_ID);}
	}
	
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
		return ctx.getDslContext().select(DSLExtensions.unhex(uuid)).stream().map(r -> r.value1()).findFirst().orElse(new byte[]{});
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
		return auth;
	}
	
	public static Auth updateAuthPassword(AONContext ctx, Auth auth) {
		ctx.getDslContext().update(AUTH)
			.set(AUTH.PASSWORD, auth.getPassword())
			.where(AUTH.ID.eq(auth.getAuth()))
			.execute();
		return auth;
	}
	
	public static DomainApp saveDomainApp(AONContext ctx, DomainApp domainApp) {
		return saveDomainApp(ctx, domainApp, true);
	}

	public static DomainApp saveDomainApp(AONContext ctx, DomainApp domainApp, boolean old) {
		if (domainApp == null) throw new AonCoreException("DomainApp can not be null");
		if (domainApp.getDomain() == null) throw new AonCoreException("DomainApp.domain can not be null"); 
		if (domainApp.getApp() == null) throw new AonCoreException("DomainApp.app can not be null");
		// Se chequea que no exista una fila para ese dominio y app
		if(old) saveDomainModule(ctx, domainApp);
		DomainApp exists = getDomainAppStream(ctx, p -> p.getDomainProperty().eq(domainApp.getDomain())
	 			.and(p.getAppProperty().eq( domainApp.getApp().value())))
				.findFirst()
				.orElse(null); 
		if (exists == null) {
			return insertDomainApp(ctx, domainApp);
		} else {
			if (domainApp.getId() == null) {
				domainApp.setId(exists.getId());
			} else {
				if (!AonNumberUtils.equals(domainApp.getId(), exists.getId()) ) {
					throw new AonCoreException("A row for domain+App exists with other ID");			
				}
			}
			return updateDomainApp(ctx, domainApp);
		}
	}
	private static DomainApp insertDomainApp(AONContext ctx, DomainApp domainApp) {
		Integer id = ctx.getDslContext().insertInto(DOMAIN_APP)
			.set(DOMAIN_APP.DOMAIN, domainApp.getDomain())
			.set(DOMAIN_APP.APP, domainApp.getApp().value())
			.set(DOMAIN_APP.ACTIVE, domainApp.isActive() ? (byte) 1 : (byte) 0)
			.execute();
		ctx.log().info("\tINSERT DOMAIN APP id: " + id);
		return domainApp.setId(id);
	}
	
	private static DomainApp updateDomainApp(AONContext ctx, DomainApp domainApp) {
		ctx.getDslContext().update(DOMAIN_APP)
			.set(DOMAIN_APP.ACTIVE, domainApp.isActive() ? (byte) 1 : (byte) 0)
			.where(DOMAIN_APP.DOMAIN.eq(domainApp.getDomain()))
			.and(DOMAIN_APP.APP.eq(domainApp.getApp().value()))
			.execute();
		ctx.log().info("\tUPDATE DOMAIN APP id: " + domainApp.getId());
		return domainApp;
	}
	
	public static UserAppRole insertUserAppRole(AONContext ctx, UserAppRole userAppRole) {
		Integer id = ctx.getDslContext().insertInto(USER_APP_ROLE)
			.set(USER_APP_ROLE.DOMAIN, userAppRole.getDomain())
			.set(USER_APP_ROLE.APP, userAppRole.getApp() != null ? userAppRole.getApp().value() : -1)
			.set(USER_APP_ROLE.USER_ID, userAppRole.getUser())
			.set(USER_APP_ROLE.ROLE, userAppRole.getRole().value())
			.execute();
		
		return userAppRole.setId(id);
	}
	
	public static UserAppRole updateUserAppRole(AONContext ctx, UserAppRole userAppRole) {
		ctx.getDslContext().update(USER_APP_ROLE)
			.set(USER_APP_ROLE.ROLE, userAppRole.getRole().value())
			.where(USER_APP_ROLE.ID.eq(userAppRole.getId()))
			.execute();
		return userAppRole;
	}
	
	public static UserAppRole deleteUserAppRole(AONContext ctx, UserAppRoleFilter filter) {
		ctx.getDslContext()
			.delete(USER_APP_ROLE)
			.where(USER_APP_ROLE_PROPERTIES.getConditions(filter))
			.execute();
		return new UserAppRole();
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
			user.setRegistry(new Registry().setId(record.getValue(USER.REGISTRY)));
			user.setRoles( SecurityDAO.getUserRoles(ctx, user.getId()));
		}
		return user;
	}
	
	public static String getUserPassword(AONContext ctx, Integer userId) {
		return ctx.getDslContext().select(USER.PASSWORD)
				.from(USER).where(USER.ID.eq(userId)).fetchOne().getValue(USER.PASSWORD);
	}

	public static void updateUserPassword(AONContext ctx, Integer userId, String password) {
		ctx.getDslContext().update(USER).set(USER.PASSWORD, password).where(USER.ID.eq(userId)).execute();
	}

	
	public static User getUser(AONContext ctx) {
		return getUser(ctx,ctx.getUser());
	}
	
	public static User save(AONContext ctx, User user) {
		return user.getId() != null ? updateUser(ctx, user) : insertUser(ctx, user);
	}
	
	public static User insertUser(AONContext ctx, User user) {
		Integer id = ctx.getDslContext().insertInto(USER)
			.set(USER.NAME, user.getName())
			.set(USER.TYPE, user.getTypeValue())
			.set(USER.LOGIN, user.getLogin())
			.set(USER.ACTIVE, user.isActive() ? (byte) 1 : (byte) 0)
			.set(USER.DOMAIN, user.getDomain())
			.set(USER.AUTH, user.getAuth().getAuth())
			.set(USER.SHARED, user.isShared() ? (byte) 1 : (byte) 0)
			.set(USER.ENTERPRISE, user.getEnterprise())
			.set(USER.TOOLBAR, user.getToolbar().value())
			.set(USER.PASSWORDEXPIRATION, AonDateUtils.toSql(user.getExpirationDate()))
			.returning(USER.ID).fetchOne().getId();
		
		return user.setId(id);
	}
	
	public static User updateUser(AONContext ctx, User user) {
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
	
	public static User delete(AONContext ctx, User user) {
		ctx.checkWrite();
		deleteSession(ctx, user);
		deleteUserTaskHolder(ctx, user);
		deleteUserAppRoles(ctx, user);
		deleteUserScopes(ctx, user);
		UserWorkgroupDAO.delete(ctx, user);
		deleteApplicationUser(ctx, user);
		deleteMailAccount(ctx, user);
		deleteActionDenied(ctx, user);
		deleteActionFavorite(ctx, user);
		deleteUser(ctx, user);
		return user;
	}
	
	public static void deleteSession(AONContext ctx, User user) {
		ctx.getDslContext().delete(SESSION).where(SESSION.USER_ID.eq(user.getId()));
	}
	
	private static void deleteMailAccount(AONContext ctx, User user) {
		ctx.getDslContext().delete(MAIL_ACCOUNT).where(MAIL_ACCOUNT.USER_ID.eq(user.getId())).execute();
	}
	
	private static void deleteActionDenied(AONContext ctx, User user) {
		ctx.getDslContext().delete(ACTION_DENIED).where(ACTION_DENIED.USER_ID.eq(user.getId())).execute();
	}
	
	private static void deleteActionFavorite(AONContext ctx, User user) {
		ctx.getDslContext().delete(ACTION_FAVORITE).where(ACTION_FAVORITE.USER_ID.eq(user.getId())).execute();
	}
	
	private static void deleteApplicationUser(AONContext ctx, User user) {
		ctx.getDslContext().select()
		.from(APPLICATION_USER).where(APPLICATION_USER.USER_ID.eq(user.getId()))
		.fetchInto(APPLICATION_USER).stream().forEach(au -> {
			ctx.getDslContext().delete(APPLICATION_USER_PROFILE).where(APPLICATION_USER_PROFILE.APPLICATION_USER.eq(au.getId())).execute();
			ctx.getDslContext().delete(APPLICATION_USER).where(APPLICATION_USER.ID.eq(au.getId())).execute();
		});
	}
	
	private static void deleteUser(AONContext ctx, UserFilter filter) {
		ctx.getDslContext().delete(USER)
			.where(USER_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	private static void deleteUser(AONContext ctx, User user) {
		deleteUser(ctx, f -> f.getIdProperty().eq(user.getId()));
	}
	
	private static void deleteUserScopes(AONContext ctx, User user) {
		deleteUserScope(ctx, f -> f.getUserIdProperty().eq(user.getId()));
	}
	
	private static void deleteUserTaskHolder(AONContext ctx, User user) {
		TaskHolder th = TaskHolderDAO.get(ctx, f -> f.getUserIdProperty().eq(user.getId()));
		if(th.getId() != null) {
			th.setActive(false);
			th.setUserId(null);
			TaskHolderDAO.save(ctx, th);
		}
	}
	private static void deleteUserAppRoles(AONContext ctx, User user) {
		deleteUserAppRole(ctx, f -> f.getUserIdProperty().eq(user.getId()));
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
	
	private static Field<?>[] USER_FIELDS = new Field[]{
			USER.ID, USER.DOMAIN, USER.TYPE, USER.NAME, USER.LOGIN, USER.ENTERPRISE, USER.REGISTRY, USER.ACTIVE,
			USER.ALLOWCONCURRENT, USER.PASSWORDEXPIRATION, USER.TOOLBAR, USER.LOCALE, USER.PAGELIMIT,
			USER.LINESPAGELIMIT, USER.INITACTION, USER.LASTACCESS, USER.AUTH, USER.SHARED
		}; 
	
	public static Stream<User> getDomainUserStream(AONContext ctx) {
		return ctx.getDslContext()
				.selectDistinct(USER_FIELDS)
				.from(USER)
				.join(DOMAIN).on(USER.DOMAIN.eq(DOMAIN.ID).or(USER.DOMAIN.eq(DOMAIN.PARENT)))
				.leftOuterJoin(USER_SCOPE).on(USER_SCOPE.USER_ID.eq(USER.ID))
				.where(DOMAIN.ID.eq(ctx.getDomainId()).and( 
							DOMAIN.SCOPE.isNull().or(USER.DOMAIN.eq(ctx.getDomainId())).or( 
									DOMAIN.SCOPE.eq(USER_SCOPE.SCOPE)
							)
						))
				.fetch().stream().map(new UserFiller());
	}
	
	public static Stream<User> getDomainUserStream(AONContext ctx, UserFilter filter) {
		
		return ctx.getDslContext()
				.selectDistinct(USER_FIELDS)
				.from(USER)
				.join(DOMAIN).on(USER.DOMAIN.eq(DOMAIN.ID).or(USER.DOMAIN.eq(DOMAIN.PARENT)))
				.leftOuterJoin(USER_SCOPE).on(USER_SCOPE.USER_ID.eq(USER.ID))
				.leftOuterJoin(USER_WORKGROUP).on(USER_WORKGROUP.USER_ID.eq(USER.ID))
				.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.USER_ID.eq(USER.ID))
				.where(USER_PROPERTIES.getConditions(filter))
				.and(DOMAIN.ID.eq(ctx.getDomainId()).and( 
							DOMAIN.SCOPE.isNull().or(USER.DOMAIN.eq(ctx.getDomainId())).or( 
									DOMAIN.SCOPE.eq(USER_SCOPE.SCOPE)
							)
						))
				.fetch().stream().map(new UserFiller());
	}
	
	public static Stream<User> getDomainUserStream(AONContext ctx, Integer page, Integer perPage, UserFilter filter) {
		return ctx.getDslContext()
				.select()
				.from(USER)
				.leftOuterJoin(AUTH).on(USER.AUTH.eq(AUTH.ID))
				.leftOuterJoin(USER_SCOPE).on(USER_SCOPE.USER_ID.eq(USER.ID))
				.where(USER_PROPERTIES.getConditions(filter))
				.groupBy(USER.ID)
				.orderBy(USER.NAME)
				.limit(perPage)
				.offset(perPage * (page -1))				
				.fetch().stream().map(new UserFiller());
	}
	
	@Deprecated
	public static Stream<User> getUserStream(AONContext ctx, UserFilter filter) {
		return USER_PROPERTIES.build(ctx.getDslContext().select().from(USER), filter)
				.fetch().stream().map(new UserFiller());
	}

	@Deprecated
	public static User getUser(AONContext ctx, UserFilter filter) {
		return getUserStream(ctx, filter).findFirst().orElse(new User());
	}
	
	@Deprecated
	public static User getUser(AONContext ctx, String login) {
		ctx.checkRead();
		Record7<Integer, Integer, String, String, Byte, Integer, byte[]> record = ctx.getDslContext()
				.select(USER.ID, 
						USER.DOMAIN, 
						USER.NAME, 
						USER.LOGIN,
						USER.ACTIVE,
						USER.REGISTRY,
						USER.AUTH)
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
								USER.REGISTRY,
								USER.AUTH)
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
			user.setRegistry(new Registry().setId(record.getValue(USER.REGISTRY)));
			user.setRoles( SecurityDAO.getUserRoles(ctx, user.getId()));
			user.setAuth(new Auth().setAuth(record.getValue(USER.AUTH)));
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
		final List<AonRole> list = new ArrayList<>();
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
	
	public static Stream<DomainApp> getDomainAppStream(AONContext ctx, DomainAppFilter filter){
		return ctx.getDslContext().select()
				.from(DOMAIN_APP)
				.where(DOMAIN_APP_PROPERTIES.getConditions(filter))				
				.fetch().stream().map(new DomainAppFiller());
	}
	
	public static Stream<UserAppRole> getUserAppRoleStream(AONContext ctx, UserAppRoleFilter filter){
		return ctx.getDslContext().select().from(USER_APP_ROLE)
				.where(USER_APP_ROLE_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new UserAppRoleFiller());
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
		if(filter != null)
			return ctx.getDslContext().select().from(SCOPE)
				.join(USER_SCOPE).on(USER_SCOPE.SCOPE.eq(SCOPE.ID))
				.where(SCOPE_PROPERTIES.getConditions(filter))
				.and(USER_SCOPE.USER_ID.eq(userId))
				.fetch().stream().map(new ScopeFiller());

		return ctx.getDslContext().select().from(SCOPE)
				.join(USER_SCOPE).on(USER_SCOPE.SCOPE.eq(SCOPE.ID))
				.where(USER_SCOPE.USER_ID.eq(userId))
				.fetch().stream().map(new ScopeFiller());
	}
	
	/**
	 * @deprecated replaced by UserWorkgroupDAO.getStream
	 */
	public static Stream<UserWorkgroup> getUserWorkgroupStream(AONContext ctx, UserWorkgroupFilter filter){
		return ctx.getDslContext().select()
				.from(USER_WORKGROUP).join(WORKGROUP).on(WORKGROUP.ID.eq(USER_WORKGROUP.WORKGROUP))
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
	
	public static class ScopeFiller implements Function<Record, Scope> {
		@Override
		public Scope apply(Record r) {
			return buildScope(r);
		}
		
		public static Scope buildScope(Record r) {
			return new Scope()
					.setId(r.getValue(SCOPE.ID))
					.setDomain(r.getValue(SCOPE.DOMAIN))
					.setDescription(r.getValue(SCOPE.DESCRIPTION));
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
	
	private static class DomainAppFiller implements Function<Record, DomainApp> {
		@Override
		public DomainApp apply(Record r) {
			return new DomainApp()
				.setId(r.getValue(DOMAIN_APP.ID))
				.setDomain(r.getValue(DOMAIN_APP.DOMAIN))
				.setApp(AonApp.safeValueOf(r.getValue(DOMAIN_APP.APP)))
				.setActive(r.getValue(DOMAIN_APP.ACTIVE) == 1);
		}
	}
	
	private static class UserAppRoleFiller implements Function<Record, UserAppRole> {
		@Override
		public UserAppRole apply(Record r) {
			return new UserAppRole()
				.setId(r.getValue(USER_APP_ROLE.ID))
				.setDomain(r.getValue(USER_APP_ROLE.DOMAIN))
				.setUser(r.getValue(USER_APP_ROLE.USER_ID))
				.setApp(AonApp.safeValueOf(r.getValue(USER_APP_ROLE.APP)))
				.setRole(com.esferalia.aon.occam.api.model.aonsolutions.AonRole.safeValueOf(r.getValue(USER_APP_ROLE.ROLE)));
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
					.setType(MailAccountType.safeValueOf(r.getType()))
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

	public static Stream<Module> getDomainModules(AONContext ctx, Integer domainId){
		return ctx.getDslContext().select().from(DOMAIN_APPLICATION_MODULE)
				.where(DOMAIN_APPLICATION_MODULE.DOMAIN.eq(domainId)).fetchInto(DOMAIN_APPLICATION_MODULE)
				.stream().map(r -> Module.safeValueOf(r.getValue(DOMAIN_APPLICATION_MODULE.MODULE).intValue()));
	}
	
	public static Stream<Module> getDomainModules(AONContext ctx){
		return getDomainModules(ctx, ctx.getDomainId());
	}
	
	public static Integer getDomainApplicationModule(AONContext ctx, Module module){
		return ctx.getDslContext().select().from(DOMAIN_APPLICATION_MODULE)
				.where(DOMAIN_APPLICATION_MODULE.DOMAIN.eq(ctx.getDomainId()))
				.and(DOMAIN_APPLICATION_MODULE.MODULE.eq(module.value()))
				.fetch().stream().map(r -> r.getValue(DOMAIN_APPLICATION_MODULE.ID))
				.findFirst().orElse(null);
	}
	
	public static void saveDomainModule(AONContext ctx, DomainApp domainApp) {
		domainApp.getApp().getModules().stream().forEach(module -> {
			if(domainApp.isActive()) {
				insertDomainModule(ctx, module);
			} else deleteDomainModule(ctx, module);
		});
	}
	
	public static void insertDomainModule(AONContext ctx, Module module) {
		Integer id = getDomainApplicationModule(ctx, module);
		if(id == null) {
			Integer domainApplication = ctx.getDslContext().select(DOMAIN_APPLICATION.ID)
					.from(DOMAIN_APPLICATION)
					.where(DOMAIN_APPLICATION.DOMAIN.eq(ctx.getDomainId()))
					.fetch().stream().map(r -> r.getValue(DOMAIN_APPLICATION.ID)).findFirst().orElse(null);

			ctx.getDslContext().insertInto(DOMAIN_APPLICATION_MODULE)
				.set(DOMAIN_APPLICATION_MODULE.DOMAIN, ctx.getDomainId())
				.set(DOMAIN_APPLICATION_MODULE.DOMAIN_APPLICATION, domainApplication)
				.set(DOMAIN_APPLICATION_MODULE.MODULE, module.value())
				.execute();
		}
	}	
	
	public static void deleteDomainModule(AONContext ctx, Module module) {
		ctx.getDslContext().delete(DOMAIN_APPLICATION_MODULE)
			.where(DOMAIN_APPLICATION_MODULE.DOMAIN.eq(ctx.getDomainId())
			.and(DOMAIN_APPLICATION_MODULE.MODULE.eq(module.value())))
			.execute();
	}
	
	public static Certificate getCertificate(AONContext ctx, Integer userId, String certificateType) {
		Certificate certificate;
		DSLContext dslContext = ctx.getDslContext();
		
		Integer parentDomainId = dslContext.select(DOMAIN.PARENT).from(DOMAIN).where(DOMAIN.ID.eq(ctx.getDomainId())).fetchOne(DOMAIN.PARENT);
		Record userRegistryRecord = dslContext.select().from(USER).where(USER.ID.eq(userId)).fetchOne();
		Integer userRegistryId = userRegistryRecord.get(USER.REGISTRY);
		Integer userRegistryDomain = userRegistryRecord.get(USER.DOMAIN);
		Integer enterpriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(ctx.getDomainId())).fetchOne(ENTERPRISE.REGISTRY);
		Integer enterpriseParentId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(parentDomainId)).fetchOne(ENTERPRISE.REGISTRY);
		
		certificate = getUserCertificateNew(dslContext, userRegistryId, certificateType);
		
		if(null != certificate) return certificate;
		
		certificate = getEnterpriseCertificateNew(dslContext, enterpriseId, userRegistryDomain, certificateType);
		
		if(null != certificate) return certificate;
		
		certificate = getEnterpriseParentCertificateNew(dslContext, enterpriseParentId, userRegistryDomain, certificateType);
		
		if(null != certificate) return certificate;

		if(AonStringUtils.equalsIgnoreCase(certificateType, "TGSS")) {
			certificate = getCertificate(ctx, f -> f.getIdProperty().eq(userId))
					.orElseThrow(CertificateNotFoundException::new);
			if(null != certificate) return certificate;
		} else if(AonStringUtils.equalsIgnoreCase(certificateType, "SEPE")) {
			certificate = getCertificateSEPE(ctx, ctx.getDomainId())
					.orElseThrow(CertificateNotFoundException::new);
			if(null != certificate) return certificate;
		}
		
		throw new CertificateNotFoundException();
	}
	
	private static Certificate getUserCertificateNew(DSLContext dslContext, Integer userRegistryId, String certificateType) {
		Record certificateRecord = dslContext.select().from(RATTACH)
				.innerJoin(RATTACH_TAG).on(RATTACH.ID.eq(RATTACH_TAG.RATTACH))
				.innerJoin(TAG).on(RATTACH_TAG.TAG.eq(TAG.ID), TAG.TYPE.eq(TagType.CERTIFICATE.value()), TAG.NAME.eq(certificateType))
				.where(RATTACH.TYPE.eq(RegistryAttachmentType.DIGITAL_CERTIFICATE.value()))
				.and(RATTACH.REGISTRY.eq(userRegistryId))
				.fetchOne();
		
		if(null == certificateRecord) return null;
		
		String password = certificateRecord.get(RATTACH.DESCRIPTION).split("HIDE\\(")[1].split("\\)")[0];
		
		return new Certificate()
				.setType(MimeType.PKCS12.name())
				.setPassword(password)
				.setData(certificateRecord.get(RATTACH.DATA));
	}
	
	private static Certificate getEnterpriseCertificateNew(DSLContext dslContext, Integer enterpriseId, Integer userRegistryDomain, String certificateType) {
		Record certificateRecord = dslContext.select().from(RATTACH)
				.innerJoin(RATTACH_TAG).on(RATTACH.ID.eq(RATTACH_TAG.RATTACH))
				.innerJoin(TAG).on(RATTACH_TAG.TAG.eq(TAG.ID), TAG.TYPE.eq(TagType.CERTIFICATE.value()), TAG.NAME.eq(certificateType))
				.where(RATTACH.TYPE.eq((byte)4))
				.and(RATTACH.SECURITY_LEVEL.eq((byte)0).or(RATTACH.SECURITY_LEVEL.eq((byte)1).and(RATTACH.DOMAIN.eq(userRegistryDomain))))
				.and(RATTACH.REGISTRY.eq(enterpriseId))
				.fetchOne();
		
//		Record certificateRecord = dslContext.select().from(RATTACH)
//				.innerJoin(RATTACH_TAG).on(RATTACH.ID.eq(RATTACH_TAG.RATTACH))
//				.innerJoin(TAG).on(RATTACH_TAG.TAG.eq(TAG.ID), TAG.TYPE.eq(TagType.CERTIFICATE.value()), TAG.NAME.eq(certificateType))
//				.where(RATTACH.TYPE.eq((byte)4))
//				.and(RATTACH.REGISTRY.eq(enterpriseId))
//				.fetchOne();
		
		if(null == certificateRecord) return null;

		String password = null;
		if(certificateRecord.get(RATTACH.DESCRIPTION).contains("HIDE"))
			password = certificateRecord.get(RATTACH.DESCRIPTION).split("HIDE\\(")[1].split("\\)")[0];
		
		return new Certificate()
				.setType(MimeType.PKCS12.name())
				.setPassword(password)
				.setData(certificateRecord.get(RATTACH.DATA));
	}
	
	private static Certificate getEnterpriseParentCertificateNew(DSLContext dslContext, Integer enterpriseParentId, Integer userRegistryDomain, String certificateType) {
		Record certificateRecord = dslContext.select().from(RATTACH)
				.innerJoin(RATTACH_TAG).on(RATTACH.ID.eq(RATTACH_TAG.RATTACH))
				.innerJoin(TAG).on(RATTACH_TAG.TAG.eq(TAG.ID), TAG.TYPE.eq(TagType.CERTIFICATE.value()), TAG.NAME.eq(certificateType))
				.where(RATTACH.TYPE.eq((byte)4))
				.and(RATTACH.SECURITY_LEVEL.eq((byte)0).or(RATTACH.SECURITY_LEVEL.eq((byte)1).and(RATTACH.DOMAIN.eq(userRegistryDomain))))
				.and(RATTACH.REGISTRY.eq(enterpriseParentId))
				.fetchOne();
		
		if(null == certificateRecord) return null;
		
		String password = certificateRecord.get(RATTACH.DESCRIPTION).split("HIDE\\(")[1].split("\\)")[0];
		
		return new Certificate()
				.setType(MimeType.PKCS12.name())
				.setPassword(password)
				.setData(certificateRecord.get(RATTACH.DATA));
	}
	
	public static Optional<Certificate> getCertificate(AONContext aonContext, UserFilter userFilter) {
		return 
		getUserCertificate(aonContext.getDslContext(), userFilter)
		.or(()->getDomainCertificate(aonContext.getDslContext(), userFilter))
		.or(()-> getParentDomainCertificate(aonContext.getDslContext(), userFilter));
	}
	
	
	public static Optional<Certificate> getUserCertificate(DSLContext dslContext, UserFilter userFilter ) {
		SelectOnConditionStep<Record> select = 
		dslContext
		.select()
		.from(USER)
		.innerJoin(REGISTRY).onKey()
		.innerJoin(RATTACH).on(REGISTRY.ID.eq(RATTACH.REGISTRY), RATTACH.TYPE.eq(DIGITAL_CERTIFICATE.value()) )
		.innerJoin(RADDINFO).on(REGISTRY.ID.eq(RADDINFO.REGISTRY), RADDINFO.ATTRIBUTE.eq(DIGITAL_CERTIFICATE_PASSWORD))
		;
				
		return 
		USER_PROPERTIES
		.build(select, userFilter)
		.fetchOptional()
		.map(r -> new Certificate()
		.setType(MimeType.PKCS12.name())
		.setPassword(r.get(RADDINFO.VALUE))
		.setData(r.get(RATTACH.DATA))
		)
		;
	}
	
	
	public static Optional<Certificate> getDomainCertificate(DSLContext dslContext, UserFilter userFilter ) {
		SelectOnConditionStep<Record> select = 
		dslContext
		.select()
		.from(USER)
		.innerJoin(DATA_ATTACH).on(
		USER.DOMAIN.eq(DATA_ATTACH.DOMAIN)
		,DATA_ATTACH.SOURCE.eq((byte)SISTEMA_RED.ordinal())
		,DATA_ATTACH.TYPE.eq((byte)DataAttachType.DIGITAL_CERTIFICATE.ordinal())
		);
				
		return 
		USER_PROPERTIES
		.build(select, userFilter)
		.fetchOptional()
		.map(r -> new Certificate()
		.setType(MimeType.PKCS12.name())
		.setData(r.get(DATA_ATTACH.DATA))
		.setPassword(r.get(DATA_ATTACH.DESCRIPTION))
		)
		;
	}

	public static Optional<Certificate> getParentDomainCertificate(DSLContext dslContext, UserFilter userFilter ) {
		SelectOnConditionStep<Record> select = 
		dslContext
		.select()
		.from(USER)
		.innerJoin(DOMAIN).on(
		USER.DOMAIN.eq(DOMAIN.ID))
		.innerJoin(DATA_ATTACH).on(
		DOMAIN.PARENT.eq(DATA_ATTACH.DOMAIN)
		,DATA_ATTACH.SOURCE.eq((byte)SISTEMA_RED.ordinal())
		,DATA_ATTACH.TYPE.eq((byte)DataAttachType.DIGITAL_CERTIFICATE.ordinal())
		);
				
		return 
		USER_PROPERTIES
		.build(select, userFilter)
		.fetchOptional()
		.map(r -> new Certificate()
		.setType(MimeType.PKCS12.name())
		.setData(r.get(DATA_ATTACH.DATA))
		.setPassword(r.get(DATA_ATTACH.DESCRIPTION))
		)
		;
	}

	public static Certificate insertCertificate(AONContext aonContext, UserFilter userFilter, Certificate certificate ) {
		return insertCertificate(aonContext.getDslContext(), userFilter, certificate);
	}
	
	public static Certificate insertCertificate(DSLContext dslContext, UserFilter userFilter, Certificate certificate ) {
		UserRecord user = USER_PROPERTIES
		.build(
		dslContext
		.select()
		.from(USER)
		, userFilter )
		.fetchOneInto(USER)
		;	
		
		if ( user.getRegistry() == null ) {
			Integer registryId = 
			dslContext
			.insertInto(REGISTRY)
			.set(REGISTRY.TYPE, (byte) 0)	
			.set(REGISTRY.NAME, user.getName())	
			.set(REGISTRY.DOMAIN, user.getDomain())
			.returning(REGISTRY.ID)
			.fetchOne()
			.getId()
			;
			user.setRegistry(registryId);
			user.update(USER.REGISTRY);
		} else {		
			dslContext
			.delete(RADDINFO)
			.where(RADDINFO.REGISTRY.eq(user.getRegistry()))
			.and(RADDINFO.ATTRIBUTE.eq(DIGITAL_CERTIFICATE_PASSWORD))
			.execute()
			;
			dslContext
			.delete(RATTACH)
			.where(RATTACH.REGISTRY.eq(user.getRegistry()))
			.and(RATTACH.TYPE.eq(DIGITAL_CERTIFICATE.value()))
			.execute()
			;
		}
		
		dslContext
		.insertInto(RADDINFO)
		.set(RADDINFO.DOMAIN, user.getDomain())
		.set(RADDINFO.REGISTRY, user.getRegistry())
		.set(RADDINFO.ATTRIBUTE, DIGITAL_CERTIFICATE_PASSWORD)
		.set(RADDINFO.VALUE, certificate.getPassword())
		.set(RADDINFO.VALUE_DATE, DSL.currentDate() )
		.execute()
		;

		dslContext
		.insertInto(RATTACH)
		.set(RATTACH.DOMAIN, user.getDomain())
		.set(RATTACH.REGISTRY, user.getRegistry())
		.set(RATTACH.TYPE, DIGITAL_CERTIFICATE.value())
		.set(RATTACH.MIMETYPE, MimeType.PKCS12.value())
		.set(RATTACH.ATTACH_DATE, DSL.currentDate())
		.set(RATTACH.DATA, certificate.getData())
		.set(RATTACH.CREATION_USER, user.getLogin())
		.set(RATTACH.CREATION_DATE, DSL.currentTimestamp())
		.set(RATTACH.MODIFICATION_DATE, DSL.currentTimestamp())
		.execute()
		;
		
		return certificate;
	}
	
	public static Optional<Certificate> getCertificateSEPE(AONContext aonContext, Integer domainId) {
		return getCertificateSEPE(aonContext.getDslContext(), domainId);
	}
	
	public static Optional<Certificate> getCertificateSEPE(DSLContext dslContext, Integer domainId ) {
		Certificate certificate = null; 
		Result<Record> rattachRecords = dslContext
				.select()
				.from(ENTERPRISE)
				.innerJoin(REGISTRY).onKey()
				.innerJoin(RATTACH).on(REGISTRY.ID.eq(RATTACH.REGISTRY), RATTACH.TYPE.eq(DIGITAL_CERTIFICATE.value()) )
				.innerJoin(RADDINFO).on(REGISTRY.ID.eq(RADDINFO.REGISTRY), RADDINFO.ATTRIBUTE.eq(DIGITAL_CERTIFICATE_PASSWORD))
				.where(ENTERPRISE.DOMAIN.eq(domainId))
				.fetch();
		
		for(Record r : rattachRecords) {
			Result<Record> tagRecords = dslContext.select().from(RATTACH_TAG).where(RATTACH_TAG.RATTACH.eq(r.get(RATTACH.ID))).fetch();
			if(tagRecords.isEmpty())
				certificate = new Certificate()
						.setType(MimeType.PKCS12.name())
						.setPassword(r.get(RADDINFO.VALUE))
						.setData(r.get(RATTACH.DATA));
		}
		
		return null == certificate ? Optional.empty() : Optional.of(certificate);
		
	}
	
	public static DomainUserRoles getDomainUserRoles(AONContext ctx, Integer userId) {
		Domain domain = DomainDAO.getDomain(ctx, ctx.getDomainId());
		Domain parentDomain = DomainDAO.getDomain(ctx, domain.getParentId());
		ApplicationParameter domainPayer = AppParamDAO.fetchOne(ctx, AppParam.AON_DOMAIN_PAYER);
		
		
		User user = userId != null ? UserDAO.get(ctx, f -> f.getIdProperty().eq(userId)) : new User();
		user.setRoles(getUserRoles(ctx, userId));
		LinkedList<AonApp> domainApps = getDomainAppStream(ctx, f -> f.getDomainProperty().eq(domain.getId()).and(f.getActiveProperty().eq((byte) 1)))
				.map(r -> r.getApp()).collect(Collectors.toCollection(LinkedList::new));
		LinkedList<AonApp> parentDomainApps = domain.getParentId() != null
				? getDomainAppStream(ctx, f -> f.getDomainProperty().eq(domain.getParentId()).and(f.getActiveProperty().eq((byte) 1)))
						.map(r -> r.getApp()).collect(Collectors.toCollection(LinkedList::new))
				: new LinkedList<>();
						
		LinkedList<com.esferalia.aon.occam.api.model.aonsolutions.AonRole> domainUserRoles = 
			userId != null
				? getUserAppRoleStream(ctx, f -> f.getDomainProperty().eq(domain.getId()).and(f.getUserIdProperty().eq(userId)))
						.map(r -> r.getRole()).collect(Collectors.toCollection(LinkedList::new)) 
				: new LinkedList<>();	
		LinkedList<com.esferalia.aon.occam.api.model.aonsolutions.AonRole> parentDomainUserRoles = 
			userId != null  && domain.getParentId() != null && user.getDomain().equals(domain.getParentId())
				? getUserAppRoleStream(ctx, f -> f.getDomainProperty().eq(domain.getParentId()).and(f.getUserIdProperty().eq(userId)))
						.map(r -> r.getRole()).collect(Collectors.toCollection(LinkedList::new))
				: new LinkedList<>();	
		
		Long userNum = getDomainUserStream(ctx, f -> f.getDomainProperty().eq(domain.getId()).and(f.getEnterpriseProperty().isNull()).and(f.getActiveProperty().eq((byte) 1)).and(f.getSharedProperty().eq((byte)0))).count();
		domain.setDefinedUsers(userNum.intValue());
		

		return new DomainUserRoles()
				.setOldDomainModules(getDomainModules(ctx).collect(Collectors.toCollection(LinkedList::new)))
				.setOldParentDomainModules(domain.getParentId() != null
					? getDomainModules(ctx, domain.getParentId()).collect(Collectors.toCollection(LinkedList::new))
					: new LinkedList<>())
				.setDomain(domain)
				.setParentDomain(parentDomain)
				.setUser(user)
				.setDomainApps(domainApps)
				.setParentDomainApps(parentDomainApps)
				.setDomainUserRoles(domainUserRoles)
				.setParentDomainUserRoles(parentDomainUserRoles)
				.setDomainPayer(domainPayer != null);
	}

	public static boolean isOCRActive(AONContext ctx, int domain) {
		User user = getUser(ctx);
		if (user != null) {
			return getDomainAppStream(ctx, p -> 
				p.getDomainProperty().eq(user.getDomain())
					.and(p.getAppProperty().eq( AonApp.OCR.value()))
					.and(p.getActiveProperty().eq( (byte) 1 )))
				.findFirst()
				.isPresent();
		}
		return false;
	}
	
	public static void saveDomainMaxDefinedUser(AONContext ctx, Integer maxDefinedUser) {
		ctx.getDslContext().update(DOMAIN)
			.set(DOMAIN.MAXDEFINEDUSERS, maxDefinedUser)
			.where(DOMAIN.ID.eq(ctx.getDomainId()))
			.execute();
	}
	
	@Deprecated
	public static void saveUserFinancePortal(AONContext ctx, Integer userId) {
		
		Integer profile = ctx.getDslContext().select().from(PROFILE)
				.where(PROFILE.DOMAIN.isNull()
				.and(PROFILE.NAME.eq("Portal Gestion"))).fetch().stream().map(r-> r.getValue(PROFILE.ID)).findFirst().orElse(null);
		
		ctx.getDslContext().select(DOMAIN_APPLICATION.ID)
				.from(DOMAIN_APPLICATION)
				.where(DOMAIN_APPLICATION.DOMAIN.eq(ctx.getDomainId()))
				.fetch().stream().map(r -> r.getValue(DOMAIN_APPLICATION.ID)).forEach(domainApplication -> {
					Integer applicationUser = ctx.getDslContext().insertInto(APPLICATION_USER)
							.set(APPLICATION_USER.DOMAIN, ctx.getDomainId())
							.set(APPLICATION_USER.USER_ID, userId)
							.set(APPLICATION_USER.DOMAIN_APPLICATION, domainApplication)
							.set(APPLICATION_USER.ACTIVE, (byte)1)
							.returning(APPLICATION_USER.ID).fetchOne().getId();


						if(profile != null) {
							ctx.getDslContext().insertInto(APPLICATION_USER_PROFILE)
							.set(APPLICATION_USER_PROFILE.DOMAIN, ctx.getDomainId())
							.set(APPLICATION_USER_PROFILE.APPLICATION_USER, applicationUser)
							.set(APPLICATION_USER_PROFILE.PROFILE, profile)
							.execute();
						}
				});
	}
	
}
