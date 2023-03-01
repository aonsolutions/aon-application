package net.aonsolutions.db.up2date.security;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.AppParam;
import com.esferalia.aon.jooq.tables.DomainApp;
import com.esferalia.aon.jooq.tables.User;
import com.esferalia.aon.jooq.tables.UserAppRole;

import net.aonsolutions.db.up2date.Update;

public class UdpatePortalConectaUsers implements Update {


	public static final UdpatePortalConectaUsers UPDATE_PORTAL_CONECTA_USERS = new UdpatePortalConectaUsers();

	private UdpatePortalConectaUsers() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		List<AonPortal> list = getAonPortalList(dslContext);
		
		for (AonPortal aonPortal : list) {
			if(aonPortal.isActive()) {
				List<Integer> users = getPortalUsers(dslContext, aonPortal);
				for (Integer user : users) {
					if(!hasUserRole(dslContext, aonPortal, user)) {
						createUserRole(dslContext, aonPortal, user);
					}
				}
			}
//			deleteAonPortal(dslContext, aonPortal);			
		}
	}

	private List<AonPortal> getAonPortalList(DSLContext dslContext) {
		return  dslContext
			.select(AppParam.APP_PARAM.ID, AppParam.APP_PARAM.DOMAIN, AppParam.APP_PARAM.VALUE)
			.from(AppParam.APP_PARAM)
			.where(AppParam.APP_PARAM.NAME.eq("AON_PORTAL"))
			.fetch().stream().map(r -> new AonPortal()
					.setId(r.getValue(AppParam.APP_PARAM.ID))
					.setDomain(r.getValue(AppParam.APP_PARAM.DOMAIN))
					.setValue(r.getValue(AppParam.APP_PARAM.VALUE))
					).toList();
	}
	
	public static void deleteAonPortal(DSLContext dslContext, AonPortal ap) {
		dslContext
		.delete(AppParam.APP_PARAM)
		.where(AppParam.APP_PARAM.ID.eq(ap.getId()))
		.execute();
	}
	
	private List<Integer> getPortalUsers(DSLContext dslContext, AonPortal aonPortal) {
		return dslContext.select(User.USER.ID)
		.from(User.USER)
		.where(User.USER.DOMAIN.eq(aonPortal.getDomain())
			.and(User.USER.TYPE.eq((byte) 1).or(User.USER.ENTERPRISE.isNotNull())))
		.fetch().stream().map(r -> r.getValue(User.USER.ID)).toList();
	}
	
	private boolean hasUserRole(DSLContext dslContext, AonPortal aonPortal, Integer id) {
		long a = dslContext.select(UserAppRole.USER_APP_ROLE.ID)
		.from(UserAppRole.USER_APP_ROLE)
		.where(UserAppRole.USER_APP_ROLE.DOMAIN.eq(aonPortal.getDomain())
				.and(UserAppRole.USER_APP_ROLE.USER_ID.eq(id)))
		.fetch().stream().count();
		return a > 0;
	}
	
	private void createUserRole(DSLContext dslContext, AonPortal aonPortal, Integer id) {
		createUserRole(dslContext, aonPortal.getDomain(), id, (byte) 30);
		if(aonPortal.isAccountingInfo()) {	
			createUserRole(dslContext, aonPortal.getDomain(), id, (byte) 1);
		}
		if(aonPortal.isDocumental() || aonPortal.isDocumentalInfo()) {
			createUserRole(dslContext, aonPortal.getDomain(), id, (byte) 8);
			createUserRole(dslContext, aonPortal.getDomain(), id, (byte) 31);
			if(!hasDomainApp(dslContext, aonPortal.getDomain(), (byte) 1))
				createDomainApp(dslContext, aonPortal.getDomain(), (byte) 1);
		}
		
		if(aonPortal.isFinance()) {
			createUserRole(dslContext, aonPortal.getDomain(), id, (byte) 18);
			createUserRole(dslContext, aonPortal.getDomain(), id, (byte) 20);
		}
		
		if(aonPortal.isFiscalInfo()) {
			createUserRole(dslContext, aonPortal.getDomain(), id, (byte) 3);
		}
		
		if(aonPortal.isPayroll() || aonPortal.isPayrollInfo()) {
			createUserRole(dslContext, aonPortal.getDomain(), id, (byte) 5);
			createUserRole(dslContext, aonPortal.getDomain(), id, (byte) 7);
		}
	}
	
	private void createUserRole(DSLContext dslContext, Integer domain, Integer user, byte role) {
		dslContext.insertInto(UserAppRole.USER_APP_ROLE)
		.set(UserAppRole.USER_APP_ROLE.DOMAIN, domain)
		.set(UserAppRole.USER_APP_ROLE.APP, (byte) -1)
		.set(UserAppRole.USER_APP_ROLE.USER_ID, user)
		.set(UserAppRole.USER_APP_ROLE.ROLE, role)
		.execute();	
	}
	
	private boolean hasDomainApp(DSLContext dslContext, Integer domain, byte app) {
		long a = dslContext.select(DomainApp.DOMAIN_APP.ID)
		.from(DomainApp.DOMAIN_APP)
		.where(DomainApp.DOMAIN_APP.DOMAIN.eq(domain)
			.and(DomainApp.DOMAIN_APP.APP.eq(app)))
		.fetch().stream().count();
		return a > 0;
	}
	
	private void createDomainApp(DSLContext dslContext, Integer domain,  byte app) {
		dslContext.insertInto(DomainApp.DOMAIN_APP)
		.set(DomainApp.DOMAIN_APP.DOMAIN, domain)
		.set(DomainApp.DOMAIN_APP.APP, app)
		.set(DomainApp.DOMAIN_APP.ACTIVE, (byte) 1)
		.execute();	
	}
	
	public static class AonPortal {
		
		private static final int PAYROLL_INFO_PORTAL = 1;
		private static final int FISCAL_INFO_PORTAL = 2;
		private static final int DOCUMENTAL_INFO_PORTAL = 4;
		private static final int PAYROLL_PORTAL = 8;
		private static final int ACCOUNTING_PORTAL = 16;
		private static final int ACTIVE_PORTAL = 32;
		private static final int INACTIVE_PORTAL = 64;
		private static final int DOCUMENTAL_MANAGEMENT_PORTAL = 128;
		private static final int FINANCE_MANAGEMENT_PORTAL = 256;
		
		Integer id;
		Integer domain;
		Integer value;
		
		public AonPortal() {
			
		}
		
		public AonPortal(Integer id, Integer domain, String value) {
			this.id = id;
			this.domain = domain;
			this.value = value != null && value.length() > 0
				? Integer.valueOf(value) : 0;
		}
		
		public Integer getId() {
			return id;
		}
		
		public AonPortal setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public Integer getDomain() {
			return domain;
		}

		public AonPortal setDomain(Integer domain) {
			this.domain = domain;
			return this;
		}
		
		public Integer getValue() {
			return value;
		}
		
		public AonPortal setValue(Integer value) {
			this.value = value;
			return this;
		}
		
		public AonPortal setValue(String value) {
			this.value =  value != null && value.length() > 0
					? Integer.valueOf(value) : 0;
			return this;
		}
		
		public boolean isPayrollInfo() {
			return getPortalValue(PAYROLL_INFO_PORTAL);
		}
		
		public boolean isFiscalInfo() {
			return getPortalValue(FISCAL_INFO_PORTAL);
		}
		
		public boolean isDocumentalInfo() {
			return getPortalValue(DOCUMENTAL_INFO_PORTAL);
		}
		
		public boolean isPayroll() {
			return getPortalValue(PAYROLL_PORTAL);
		}
		
		public boolean isAccountingInfo() {
			return getPortalValue(ACCOUNTING_PORTAL);
		}
		
		public boolean isActive() {
			return getPortalValue(ACTIVE_PORTAL);
		}
		
		public boolean isInactive() {
			return getPortalValue(INACTIVE_PORTAL);
		}
		
		public boolean isDocumental() {
			return getPortalValue(DOCUMENTAL_MANAGEMENT_PORTAL);
		}
		
		public boolean isFinance() {
			return getPortalValue(FINANCE_MANAGEMENT_PORTAL);
		}
		
		private boolean getPortalValue(int bitwise) {
			return (this.value & bitwise) != 0;
		}
		
		
	}
}
