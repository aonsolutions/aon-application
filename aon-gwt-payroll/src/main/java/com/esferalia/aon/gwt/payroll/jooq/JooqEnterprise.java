package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.CraBatch.CRA_BATCH;
import static com.esferalia.aon.jooq.tables.CraBatchDetail.CRA_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.payroll.Pair;
import com.esferalia.aon.payroll.Trio;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqEnterprise {

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	// --------------------------------- GETTER / SETTER ---------------------------------------------
	
	public static Integer getEnterpriseId(Connection conn, Integer domainId) {
		return getEnterpriseIdDB(DSL.using(conn, getDefaultSettings()), domainId);
	}
	
	// --------------------------------- GET ENTERPRISES INFO ----------------------------------------
	
	public static Map<Integer, String> getEnterpriseAddresses(Connection conn, Integer enterpriseId) {
		return getEnterpriseAddressesInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseId);
	}
	
	public static Map<Integer, String> getEnterpriseScopes(Connection conn, Integer enterpriseId) {
		return getEnterpriseScopesInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseId);
	}

	public static Map<Integer, String> getEnterpriseCalendars(Connection conn, Integer enterpriseId) {
		return getEnterpriseCalendarsInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseId);
	}
	
	public static Map<Integer, String> getEnterpriseActivities(Connection conn, Integer enterpriseId) {
		return getEnterpriseActivitiesInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseId);
	}
	
	// --------------------------------- GETTER / SETTER ---------------------------------------------
	
	private static Integer getEnterpriseIdDB(DSLContext dslContext, Integer domainId) {
		Result<Record> enterpriseRecords = dslContext.select().from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetch();
		if(enterpriseRecords.isEmpty())
			return null;
		
		return enterpriseRecords.get(0).get(ENTERPRISE.REGISTRY);
	}
	
	private static Map<Integer, String> getEnterpriseAddressesInfoDB(DSLContext dslContext, Integer enterpriseId) {
		Map<Integer, String> addresses = new HashMap<Integer, String>();
		
		Result<Record> raddressRecords = dslContext.select().from(RADDRESS)
				.where(RADDRESS.REGISTRY.eq(enterpriseId))
				.fetch();
		
		for(Record r : raddressRecords){
			Record1<String> geozoneName = dslContext.select(GEOZONE.NAME).from(GEOZONE)
					.where(GEOZONE.ID.eq(r.get(RADDRESS.GEOZONE)))
					.fetchOne();
			
			String addressStr = "";
			
			if(r.get(RADDRESS.STREET_TYPE) != "")
				addressStr += r.get(RADDRESS.STREET_TYPE) + " ";
			if(null == r.get(RADDRESS.ADDRESS))
				continue;
			if(!r.get(RADDRESS.ADDRESS).isEmpty())
				addressStr += r.get(RADDRESS.ADDRESS) + " ";
			if(null !=r.get(RADDRESS.NUMBER))
				if(!r.get(RADDRESS.NUMBER).isEmpty())
					addressStr += r.get(RADDRESS.NUMBER) + " ";
			
			if(null != geozoneName)
				addressStr += geozoneName.get(0);
			
			addresses.put(r.get(RADDRESS.ID), addressStr);
		}
		
		return addresses;
	}
	
	private static Map<Integer, String> getEnterpriseScopesInfoDB(DSLContext dslContext, Integer enterpriseId) {
		Map<Integer, String> scopes = new HashMap<>();
		
		Record enterpriseRecord = dslContext.select().from(ENTERPRISE)
					.where(ENTERPRISE.REGISTRY.eq(enterpriseId))
					.fetchOne();
		
		Integer domainId = enterpriseRecord.get(ENTERPRISE.DOMAIN);
		
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.eq(domainId))
				.fetchOne();
		
		Integer parentDomianId = domainRecord.get(DOMAIN.PARENT);
		String domainDescription = domainRecord.get(DOMAIN.DESCRIPTION);
		
		if(null == parentDomianId) {
			Result<Record> scopeRecords = dslContext.select().from(SCOPE)
					.where(SCOPE.DOMAIN.eq(domainId))
					.fetch();
			
			if(null != scopeRecords && !scopeRecords.isEmpty())
				for(Record r : scopeRecords)
					scopes.put(r.get(SCOPE.ID), r.get(SCOPE.DESCRIPTION) + " (" + domainDescription +")");
			
		}else {
			Result<Record> scopeRecords = dslContext.select().from(SCOPE)
					.where(SCOPE.DOMAIN.eq(domainId)
							.or(SCOPE.DOMAIN.eq(parentDomianId))
					)
					.fetch();
			
			if(null != scopeRecords && !scopeRecords.isEmpty()) {
				Record parentDomainRecord = dslContext.select().from(DOMAIN)
					.where(DOMAIN.ID.eq(parentDomianId))
					.fetchOne();
			
				String parentDescription = parentDomainRecord.get(DOMAIN.DESCRIPTION);
			
				for(Record r : scopeRecords)			
					scopes.put(r.get(SCOPE.ID), r.get(SCOPE.DESCRIPTION) + " (" + parentDescription +")");
		
			}
		}
		
		return scopes;
	}
	
	private static Map<Integer, String> getEnterpriseCalendarsInfoDB(DSLContext dslContext, Integer enterpriseId) {
		Map<Integer, String> calendars = new HashMap<>();
		
		Record enterpriseRecord = dslContext.select().from(ENTERPRISE)
				.where(ENTERPRISE.REGISTRY.eq(enterpriseId))
				.fetchOne();
		
		Integer domain = enterpriseRecord.get(ENTERPRISE.DOMAIN);
		
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.eq(domain))
				.fetchOne();
		
		Integer parentDomain = domainRecord.get(DOMAIN.PARENT);
		
		Result<Record> caledarRecords = null;
		if(null == parentDomain)
			caledarRecords = dslContext.select().from(CALENDAR)
					.where(CALENDAR.DOMAIN.eq(domain))
					.or(CALENDAR.DOMAIN.eq(0))
					.fetch();
		else
			caledarRecords = dslContext.select().from(CALENDAR)
				.where(CALENDAR.DOMAIN.eq(domain))
				.or(CALENDAR.DOMAIN.eq(parentDomain))
				.or(CALENDAR.DOMAIN.eq(0))
				.fetch();
				
		for(Record r : caledarRecords){
			if(null != r.get(CALENDAR.DESCRIPTION))
				calendars.put(r.get(CALENDAR.ID), r.get(CALENDAR.DESCRIPTION));
		}
		
		return calendars;	
	}
	
	private static Map<Integer, String> getEnterpriseActivitiesInfoDB(DSLContext dslContext, Integer enterpriseId) {
		Map<Integer, String> activities = new HashMap<>();
		
		Result<Record> enterpriseActivityRecords = dslContext.select().from(ENTERPRISE_ACTIVITY)
				.where(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(enterpriseId))
				.fetch();
		
		for(Record r : enterpriseActivityRecords){
			activities.put(r.get(ENTERPRISE_ACTIVITY.ID), r.get(ENTERPRISE_ACTIVITY.DESCRIPTION));
		}
		
		return activities;
	}
	
	// --------------------------------------------------------------------------------------------------
	//										MAIN CRA GET CCC INFO
	// --------------------------------------------------------------------------------------------------
	
	public static List<CCC> getCCCs(Connection conn, Integer domainId) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		return getCCCs(dslContext, domainId);
	}

	private static List<CCC> getCCCs(DSLContext ctx, Integer domainId) {
		return 
		ctx.select()
		.from(ENTERPRISE_CCC)
		.where(ENTERPRISE_CCC.DOMAIN.eq(domainId))
		.fetchStreamInto(ENTERPRISE_CCC)
		.map( r -> {
			CCC ccc = new CCC();
			ccc.setCode(r.getCcc());
			ccc.setGeozone(r.getCcc());
			ccc.setRegime(getSSRegime(r.getType()).getCode());
			return ccc;
		})
		.collect(Collectors.toList())
		;
	}

	public static List<CCCInfo> getEnterprisesCCCInfo(Connection conn, Integer userId, Integer domainId, long findPeriodTime) {
		return getEnterprisesCCCInfoDB(DSL.using(conn, getDefaultSettings()), userId, domainId, findPeriodTime);
	}
	
	private static List<CCCInfo> getEnterprisesCCCInfoDB(DSLContext dslContext, Integer userId, Integer domainId, long findPeriodTime) {
		
		List<CCCInfo> enterprisesCCCInfo = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(findPeriodTime);
		
		Date findPeriod = new Date(calendar.getTimeInMillis());
		
		Date findEndPeriod = getFindEndDate(findPeriodTime);
		
		// Domain Childs
		List<Integer> domainChilds = getDomainChilds(dslContext, domainId, userId);
		
		List<Record> enterpriseCCCActivities = 
			dslContext.select().from(ENTERPRISE_CCC)
			.join(DOMAIN).on(DOMAIN.ID.eq(ENTERPRISE_CCC.DOMAIN))
			.leftJoin(ENTERPRISE_ACTIVITY)
			.on(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
			.leftJoin(CONTRACT)
			.on(ENTERPRISE_CCC.ID.eq(CONTRACT.ENTERPRISE_CCC))
			.leftJoin(SALARY)
			.on(CONTRACT.ID.eq(SALARY.CONTRACT))
			.leftJoin(GEOZONE)
			.on(ENTERPRISE_CCC.GEOZONE.eq(GEOZONE.ID))
			.leftJoin(REGISTRY)
			.on(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(REGISTRY.ID))
			.where(ENTERPRISE_ACTIVITY.ID.in(
					dslContext.select(ENTERPRISE_ACTIVITY.ID).from(ENTERPRISE_ACTIVITY)
					.where(ENTERPRISE_ACTIVITY.ENTERPRISE.in(
							dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
								.where(ENTERPRISE.DOMAIN.in(domainChilds))
							.fetch(ENTERPRISE.REGISTRY)))
				.fetch(ENTERPRISE_ACTIVITY.ID)))
			.and(ENTERPRISE_CCC.TYPE.ne((byte)6))
//			.and(CONTRACT.END_DATE.ge(findPeriod).or(CONTRACT.END_DATE.isNull()))
			.and(CONTRACT.SS_REGIME.ne((byte) 3))
			.and(
				((SALARY.START_DATE.ge(findPeriod).and(SALARY.END_DATE.le(findEndPeriod)))
				.or(SALARY.END_DATE.between(findPeriod, findEndPeriod)))
				.or(SALARY.CHARGE_DATE.between(findPeriod, findEndPeriod).and(SALARY.TYPE.eq(SalaryType.DELAY.value() )))
				
			)
			//.and(SALARY.TOTAL_PAYMENT.gt(0.00))
			.fetch();
		
		
		List<Trio<Integer, Byte, String>> visitedCCCs = new ArrayList<>();
		
		for(Record enterprise : enterpriseCCCActivities) {
			Integer enterpriseActivityId = enterprise.get(ENTERPRISE_ACTIVITY.ID);
			String enterpriseActivityDescription = enterprise.get(ENTERPRISE_ACTIVITY.DESCRIPTION);

			Integer cccId = enterprise.get(ENTERPRISE_CCC.ID);
			String cccCode = enterprise.get(ENTERPRISE_CCC.CCC);
			String regime = getSSRegime(enterprise.get(ENTERPRISE_CCC.TYPE)).getCode();
			Byte type = enterprise.get(ENTERPRISE_CCC.TYPE);
			
			String completeCCCAccount = regime + cccCode;
			
			if(containsCCCDomain(visitedCCCs, enterprise.get(ENTERPRISE_ACTIVITY.DOMAIN), type, completeCCCAccount)) continue;
			visitedCCCs.add(new Trio<Integer, Byte, String>(enterprise.get(ENTERPRISE_ACTIVITY.DOMAIN), type, completeCCCAccount));
			
			// ---------------------------------- Has CRA emited
			
			List<Timestamp> craDates = dslContext.select(CRA_BATCH.OUTCOME_FILE_DATE) .from(CRA_BATCH)
				.where(CRA_BATCH.ID.in(
						dslContext.select(CRA_BATCH_DETAIL.CRA_BATCH).from(CRA_BATCH_DETAIL)
							.where(CRA_BATCH_DETAIL.ENTERPRISE_CCC.eq(cccId))
							.fetch(CRA_BATCH_DETAIL.CRA_BATCH)
				)).fetch(CRA_BATCH.OUTCOME_FILE_DATE);
			
			List<java.util.Date> craDatesList = new ArrayList<>();
			
			craDates.forEach(craDate -> {
				if(null != craDate) {
					java.util.Date date = new java.util.Date(craDate.getTime());
					DateUtils.resetTime(date);
					craDatesList.add(date);
				}
			});
						
			
			// ENTERPRISE REGISTRY
			
			String geozoneCode = enterprise.get(GEOZONE.CODE);
			String geozone = enterprise.get(GEOZONE.NAME);
			Integer enterpriseId = enterprise.get(REGISTRY.ID);
			String enterpriseName =  enterprise.get(DOMAIN.DESCRIPTION);
			
			CCCInfo cccInfo = new CCCInfo();
			cccInfo.setCccId(cccId);
			cccInfo.setCcc(cccCode);
			cccInfo.setCccAccount(cccCode);
			cccInfo.setCccRegimeCode(regime);
			cccInfo.setTypeStr(regime);
			cccInfo.setGeozoneCode(geozoneCode);
			cccInfo.setGeozone(geozone);
			cccInfo.setType(type);
			cccInfo.setActivityId(enterpriseActivityId);
			cccInfo.setActivityDescription(enterpriseActivityDescription);
			cccInfo.setUseByContracts(true);
			cccInfo.setEnterpriseDesciption(enterpriseName);
			cccInfo.setEnterpriseId(enterpriseId);
			cccInfo.setCRADates(craDatesList);
			
			enterprisesCCCInfo.add(cccInfo);
		}
		
		System.err.println("enterpriseCCCActivities size : " + enterprisesCCCInfo.size());
		
		return enterprisesCCCInfo;
	}

	private static boolean containsCCCDomain(List<Trio<Integer, Byte, String>> visitedCCCs, Integer domain, Byte type, String completeCCCAccount) {
		for(Trio<Integer, Byte, String> pair : visitedCCCs){
			if(pair.getFirst().equals(domain) && pair.getSecond() == type && AonStringUtils.equalsIgnoreCase(completeCCCAccount, pair.getThird()))
				return true;
		}
		return false;
	}

	private static List<Integer> getDomainChilds(DSLContext dslContext, Integer domainId, Integer userId) {
		return dslContext.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.ID.eq(domainId).or(DOMAIN.PARENT.eq(domainId)))
				.and(DOMAIN.SCOPE.in(
						dslContext.select(USER_SCOPE.SCOPE).from(USER_SCOPE)
							.where(USER_SCOPE.USER_ID.eq(userId))
							.fetch(USER_SCOPE.SCOPE))
				.or(DOMAIN.SCOPE.isNull()))
				.fetch(DOMAIN.ID);
	}

	private static Date getFindEndDate(long findPeriodTime) {
		Calendar endPeriod = Calendar.getInstance();
		endPeriod.setTimeInMillis(findPeriodTime);
		endPeriod.set(Calendar.DATE, endPeriod.getActualMaximum(Calendar.DATE));
		
		return new Date(endPeriod.getTimeInMillis());
	}

	public static SSRegimeType getSSRegime( int cccType ) {
		Map<CCCType, SSRegimeType> regimes = new HashMap<>();
		regimes.put(CCCType.AGRICULTURAL, SSRegimeType.AGRICULTURAL);
		regimes.put(CCCType.ARTIST, SSRegimeType.ARTIST);
		regimes.put(CCCType.HOME_EMPLOYEES, SSRegimeType.DOMESTIC_EMPLOYEES);
		
		try {
			return regimes.getOrDefault(CCCType.values()[cccType], SSRegimeType.GENERAL);
		} catch ( Exception t){
			return SSRegimeType.GENERAL;
		}
	}

	public static List<Integer> getEnterpriseActiveContracts(Connection connection, Integer domainId, java.util.Date date) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		return dslContext.select(CONTRACT.ID)
			.from(CONTRACT)
			.where(CONTRACT.DOMAIN.eq(domainId))
			.and(CONTRACT.END_DATE.ge(parseDateSql(date)).or(CONTRACT.END_DATE.isNull()))
			.fetch(CONTRACT.ID);
	}

	private static Date parseDateSql(java.util.Date date) {
		return null == date ? null : new Date(AonDateUtils.getMonthFirstDay(date).getTime());
	}
	
}
