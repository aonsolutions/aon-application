package com.esferalia.aon.dsi;

import static com.esferalia.aon.dsi.util.EnumUtils.enum2Byte;
import static com.esferalia.aon.jooq.tables.Application.APPLICATION;
import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.DomainApplicationModule.DOMAIN_APPLICATION_MODULE;
import static com.esferalia.aon.jooq.tables.Profile.PROFILE;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;

import org.apache.commons.codec.binary.Base64;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record1;

import com.code.aon.audit.enumeration.Module;
import com.esferalia.aon.dsi.util.EnumUtils;
import com.esferalia.aon.jooq.tables.DomainApplicationModule;
import com.esferalia.aon.jooq.tables.records.ApplicationUserProfileRecord;
import com.esferalia.aon.jooq.tables.records.ApplicationUserRecord;
import com.esferalia.aon.jooq.tables.records.DomainApplicationModuleRecord;
import com.esferalia.aon.jooq.tables.records.DomainApplicationRecord;
import com.esferalia.aon.jooq.tables.records.UserRecord;
import com.esferalia.aon.jooq.tables.records.UserScopeRecord;

public class UserLoader extends AbstractLoader {

	private Integer application = null;

	public UserLoader(DSLContext dsiContext, DSLContext aonContext) {
		super(dsiContext, aonContext);
	}

	InsertSetMoreStep<UserRecord> insertSetMoreStepUser;
	InsertSetMoreStep<UserScopeRecord> insertSetMoreStepUserScope;
	InsertSetMoreStep<ApplicationUserRecord> insertSetMoreStepApplicationUser;
	InsertSetMoreStep<ApplicationUserProfileRecord> insertSetMoreStepApplicationUserProfile;
	InsertSetMoreStep<DomainApplicationRecord> insertSetMoreStepDomainApplication;
	InsertSetMoreStep<DomainApplicationModuleRecord> insertSetMoreStepDomainApplicationModule;

	public int loadModules(int domain, Module... modules) {
		InsertSetStep<DomainApplicationRecord> insertSetStepDomainApplication = getDomainApplicationInsertSetStep();

		int application = getApplication();

		Integer domainApplication = getDomainApplication(domain, application);

		if (domainApplication == null) {
			domainApplication = next(DOMAIN_APPLICATION.getIdentity());
			//@formatter:off
			insertSetMoreStepDomainApplication = insertSetStepDomainApplication
					.set(DOMAIN_APPLICATION.ID, domainApplication)
					.set(DOMAIN_APPLICATION.DOMAIN, domain)
					.set(DOMAIN_APPLICATION.APPLICATION, application)
					;
			//@formatter:on
		}

		for (Module module : modules) {

			if (getDomainApplicationModule(domain, domainApplication, module) != null)
				continue;

			InsertSetStep<DomainApplicationModuleRecord> insertSetStepDomainApplicationModule = getDomainApplicationModuleInsertSetStep();
			//@formatter:off
			insertSetMoreStepDomainApplicationModule = insertSetStepDomainApplicationModule
					.set(DOMAIN_APPLICATION_MODULE.DOMAIN, domain )
					.set(DOMAIN_APPLICATION_MODULE.MODULE, EnumUtils.enum2Byte(module))
					.set(DOMAIN_APPLICATION_MODULE.DOMAIN_APPLICATION, domainApplication)
					;
			//@formatter:on
		}
		return domainApplication;
	}

	public void loadUser(int domain, int domainApplication, String login,
			String... profiles) {
		InsertSetStep<UserRecord> insertSetStepUser = getUserInsertSetStep();
		Integer user = getUser(domainApplication, login);
		if (user == null) {
			user = next(USER.getIdentity());

			//@formatter:off
			insertSetMoreStepUser = insertSetStepUser
				.set(USER.ID, user)
				.set(USER.DOMAIN, domain)
				.set(USER.ACTIVE,(byte)1)
				.set(USER.LOGIN, login)
				.set(USER.NAME, login.toUpperCase())
				.set(USER.PASSWORD, digestPasswd(login))
				.set(USER.PASSWORDEXPIRATION, new Date(0))
				;
			//@formatter:on
		}

		//@formatter:off
		Cursor<Record1<Integer>> scopesCursor = 
				aonContext
				.select(SCOPE.ID)
				.from(SCOPE)
				.where(SCOPE.DOMAIN.eq(domain))
				.fetchLazy();
		// @formatter:on

		while (scopesCursor.hasNext()) {

			int scope = scopesCursor.fetchOne().value1();

			if (getUserScope(user, scope) != null)
				continue;

			InsertSetStep<UserScopeRecord> insertSetStepUserScope = getUserScopeInsertSetStep();
			//@formatter:off
			insertSetMoreStepUserScope = insertSetStepUserScope
					.set(USER_SCOPE.DOMAIN, domain)
					.set(USER_SCOPE.USER_ID, user)
					.set(USER_SCOPE.SCOPE, scope )
					;
			//@formatter:on
		}

		Integer applicationUser = getApplicationUser(user, domainApplication);
		if (applicationUser == null) {
			applicationUser = next(APPLICATION_USER.getIdentity());
			InsertSetStep<ApplicationUserRecord> insertSetStepApplicationUser = getApplicationUserInsertSetStep();
			//@formatter:off
			insertSetMoreStepApplicationUser = insertSetStepApplicationUser
					.set(APPLICATION_USER.ID, applicationUser)
					.set(APPLICATION_USER.DOMAIN, domain)
					.set(APPLICATION_USER.USER_ID, user)
					.set(APPLICATION_USER.DOMAIN_APPLICATION, domainApplication)
					;
			//@formatter:on
		}

		InsertSetStep<ApplicationUserProfileRecord> insertSetStepApplicationUserProfile = getApplicationUserProfileInsertSetStep();
		for (String pr0file : profiles) {
			int profile = getProfile(pr0file);
			
			if ( getApplicationUserProfile(applicationUser, profile) != null )
				continue;
			
			//@formatter:off
			insertSetMoreStepApplicationUserProfile = insertSetStepApplicationUserProfile
					.set(APPLICATION_USER_PROFILE.DOMAIN, domain)
					.set(APPLICATION_USER_PROFILE.APPLICATION_USER, applicationUser )
					.set(APPLICATION_USER_PROFILE.PROFILE, profile)
					;
			//@formatter:on
		}
	}

	public void execute() {
		execute(insertSetMoreStepUser);
		execute(insertSetMoreStepUserScope);
		execute(insertSetMoreStepDomainApplication);
		execute(insertSetMoreStepDomainApplicationModule);
		execute(insertSetMoreStepApplicationUser);
		execute(insertSetMoreStepApplicationUserProfile);

		insertSetMoreStepUser = null;
		insertSetMoreStepUserScope = null;
		insertSetMoreStepDomainApplication = null;
		insertSetMoreStepDomainApplicationModule = null;
		insertSetMoreStepApplicationUser = null;
		insertSetMoreStepApplicationUserProfile = null;
	}

	private int getProfile(String name) {
		//@formatter:off
		return aonContext.select(PROFILE.ID)
				.from(PROFILE)
				.where(PROFILE.NAME.eq(name))
				.and(PROFILE.APPLICATION.eq(getApplication()))
				.fetchOne(PROFILE.ID);
		//@formatter:on
	}

	private int getApplication() {
		if (application == null)
			application = aonContext
			//@formatter:off
			.select(APPLICATION.ID)
			.from(APPLICATION)
			.where(APPLICATION.NAME.eq("aon-aio"))
			.fetchOne(APPLICATION.ID);
		//@formatter:on
		return application;
	}

	private Integer getUser(int domain, String login) {
		//@formatter:off
		return aonContext
		.select()
		.from(USER)
		.where(USER.DOMAIN.eq(domain))
		.and(USER.LOGIN.eq(login))
		.fetchOne(USER.ID);
		//@formatter:on
	}

	private Integer getUserScope(int user, int scope) {
		//@formatter:off
		return aonContext
		.select()
		.from(USER_SCOPE)
		.where(USER_SCOPE.USER_ID.eq(user))
		.and(USER_SCOPE.SCOPE.eq(scope))
		.fetchOne(USER_SCOPE.ID);
		//@formatter:on
	}

	private Integer getDomainApplication(int domain, int application) {
		//@formatter:off
		return aonContext
		.select()
		.from(DOMAIN_APPLICATION)
		.where(DOMAIN_APPLICATION.DOMAIN.eq(domain))
		.and(DOMAIN_APPLICATION.APPLICATION.eq(application))
		.fetchOne(DOMAIN_APPLICATION.ID);
		//@formatter:on
	}

	private Integer getDomainApplicationModule(int domain,
			int domainApplication, Module module) {
		//@formatter:off
		return aonContext
		.select()
		.from(DOMAIN_APPLICATION_MODULE)
		.where(DOMAIN_APPLICATION_MODULE.DOMAIN.eq(domain))
		.and(DOMAIN_APPLICATION_MODULE.MODULE.eq(enum2Byte(module)))
		.and(DOMAIN_APPLICATION_MODULE.DOMAIN_APPLICATION.eq(domainApplication))
		.fetchOne(DOMAIN_APPLICATION_MODULE.ID);
		//@formatter:on
	}

	private Integer getApplicationUser(int user, int domainApplication) {
		//@formatter:off
		return aonContext
		.select()
		.from(APPLICATION_USER)
		.where(APPLICATION_USER.USER_ID.eq(user))
		.and(APPLICATION_USER.DOMAIN_APPLICATION.eq(domainApplication))
		.fetchOne(APPLICATION_USER.ID);
		//@formatter:on
	}

	private Integer getApplicationUserProfile(int applicationUser, int profile) {
		//@formatter:off
		return aonContext
		.select()
		.from(APPLICATION_USER_PROFILE)
		.where(APPLICATION_USER_PROFILE.APPLICATION_USER.eq(applicationUser))
		.and(APPLICATION_USER_PROFILE.PROFILE.eq(profile))
		.fetchOne(APPLICATION_USER_PROFILE.ID);
		//@formatter:on
	}

	private InsertSetStep<UserRecord> getUserInsertSetStep() {
		return get(insertSetMoreStepUser, USER);
	}

	private InsertSetStep<UserScopeRecord> getUserScopeInsertSetStep() {
		return get(insertSetMoreStepUserScope, USER_SCOPE);
	}

	private InsertSetStep<ApplicationUserRecord> getApplicationUserInsertSetStep() {
		return get(insertSetMoreStepApplicationUser, APPLICATION_USER);
	}

	private InsertSetStep<ApplicationUserProfileRecord> getApplicationUserProfileInsertSetStep() {
		return get(insertSetMoreStepApplicationUserProfile,
				APPLICATION_USER_PROFILE);
	}

	private InsertSetStep<DomainApplicationRecord> getDomainApplicationInsertSetStep() {
		return get(insertSetMoreStepDomainApplication, DOMAIN_APPLICATION);
	}

	private InsertSetStep<DomainApplicationModuleRecord> getDomainApplicationModuleInsertSetStep() {
		return get(insertSetMoreStepDomainApplicationModule,
				DomainApplicationModule.DOMAIN_APPLICATION_MODULE);
	}

	// ------------------------------------------------------------------------
	private static String digestPasswd(String passwd) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(passwd.getBytes("UTF-8"));
			byte raw[] = digest.digest();
			return new String(Base64.encodeBase64(raw), "UTF-8"); // step 5
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

}
