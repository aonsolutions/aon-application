package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

public class JooqEnterprise {

	private static Settings SETTINGS = null;
	
	public static Map<Integer, String> getEnterpriseAddresses(Connection conn, Integer enterpriseId) {
		return getEnterpriseAddressesInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseId);
	}

	public static Map<Integer, String> getEnterpriseCalendars(Connection conn, Integer enterpriseId) {
		return getEnterpriseCalendarsInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseId);
	}
	
	public static Map<Integer, String> getEnterpriseActivities(Connection conn, Integer enterpriseId) {
		return getEnterpriseActivitiesInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseId);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
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
	
	private static Map<Integer, String> getEnterpriseCalendarsInfoDB(DSLContext dslContext, Integer enterpriseId) {
		Map<Integer, String> calendars = new HashMap<Integer, String>();
		
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
		Map<Integer, String> activities = new HashMap<Integer, String>();
		
		Result<Record> enterpriseActivityRecords = dslContext.select().from(ENTERPRISE_ACTIVITY)
				.where(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(enterpriseId))
				.fetch();
		
		for(Record r : enterpriseActivityRecords){
			activities.put(r.get(ENTERPRISE_ACTIVITY.ID), r.get(ENTERPRISE_ACTIVITY.DESCRIPTION));
		}
		
		return activities;
	}
	
}
