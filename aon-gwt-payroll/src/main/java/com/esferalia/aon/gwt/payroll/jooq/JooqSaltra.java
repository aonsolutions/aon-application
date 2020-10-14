package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.net.URL;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.server.EmployeeAFIGeneration.CHC;
import com.esferalia.aon.gwt.payroll.shared.SaltraCredentialsNotFoundException;
import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.Raddinfo;
import com.esferalia.aon.jooq.tables.records.UserRecord;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.saltra.api.SaltraException;

public class JooqSaltra {
	
	public static class SaltraCredentials  {
		private String certKey;
		private String certSecret;		
		
		public SaltraCredentials(String certKey, String certSecret) {
			checkCertKey(certKey);			
			this.certKey = certKey;

			checkCertScecret(certSecret);
			this.certSecret = certSecret;
		}

		public String getCertKey() {
			return certKey;
		}
		
		public String getCertSecret() {
			return certSecret;
		}
		
		private static void checkCertKey(String certKey) {
			if ( AonStringUtils.isBlank(certKey) )
				throw new SaltraCredentialsNotFoundException();
		}
		
		private static void checkCertScecret(String certSecret) {
			if ( AonStringUtils.isBlank(certSecret) )
				throw new SaltraCredentialsNotFoundException();
		}

	}
	
	private static final String SALTRA_CERT_KEY = "SALTRA_CERT_KEY";
	private static final String SALTRA_CERT_SECRET = "SALTRA_CERT_SECRET";
	

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	
	public static SaltraCredentials getCredentials(Connection connection, String domainName, String userLogin ) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		Map<String, String> saltraMediaMap = 
		dslContext
		.select()
		.from(DOMAIN)
		.innerJoin(USER).on(DOMAIN.ID.eq(USER.DOMAIN).or(DOMAIN.PARENT.eq(USER.DOMAIN)))
		.innerJoin(REGISTRY).on(USER.REGISTRY.eq(REGISTRY.ID))
		.innerJoin(RADDINFO).on(REGISTRY.ID.eq(RADDINFO.REGISTRY))
		.where(USER.LOGIN.eq(userLogin))
		.and(DOMAIN.NAME.eq(domainName))
		.and(RADDINFO.ATTRIBUTE.in(SALTRA_CERT_KEY, SALTRA_CERT_SECRET))
		.fetchMap(RADDINFO.ATTRIBUTE, RADDINFO.VALUE)
		;
		
		String certKey = saltraMediaMap.get(SALTRA_CERT_KEY);
		String certSecret = saltraMediaMap.get(SALTRA_CERT_SECRET);

		return new SaltraCredentials(certKey, certSecret);
	}
	
	public static void setCredentials(Connection connection, String domainName, String userLogin, String certKey, String certSecret) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		UserRecord user = 
		dslContext
		.select()
		.from(DOMAIN)
		.innerJoin(USER).on(DOMAIN.ID.eq(USER.DOMAIN).or(DOMAIN.PARENT.eq(USER.DOMAIN)))
		.where(USER.LOGIN.eq(userLogin))
		.and(DOMAIN.NAME.eq(domainName))
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
			.and(RADDINFO.ATTRIBUTE.in(SALTRA_CERT_KEY, SALTRA_CERT_SECRET))
			.execute()
			;
		}
		
		dslContext
		.insertInto(RADDINFO)
		.set(RADDINFO.DOMAIN, user.getDomain())
		.set(RADDINFO.REGISTRY, user.getRegistry())
		.set(RADDINFO.ATTRIBUTE, SALTRA_CERT_KEY)
		.set(RADDINFO.VALUE, certKey)
		.set(RADDINFO.VALUE_DATE, DSL.currentDate() )
		.newRecord()
		.set(RADDINFO.DOMAIN, user.getDomain())
		.set(RADDINFO.REGISTRY, user.getRegistry())
		.set(RADDINFO.ATTRIBUTE, SALTRA_CERT_SECRET)
		.set(RADDINFO.VALUE, certSecret)
		.set(RADDINFO.VALUE_DATE, DSL.currentDate() )
		.execute()
		;
	}
	
}
