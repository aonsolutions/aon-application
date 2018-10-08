package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;

public class JooqWorkplace {

	private static Settings SETTINGS = null;
	
	public static WorkplaceInfo getWorkplaceInfo(Connection conn, Integer workplaceId) {
		return getWorkplaceInfoDB(DSL.using(conn, getDefaultSettings()), workplaceId);
	}
	
	public static WorkplaceInfo setWorkplaceInfo(Connection conn, WorkplaceInfo workplaceInfo) {
		return setWorkplaceInfoDB(DSL.using(conn, getDefaultSettings()), workplaceInfo);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static WorkplaceInfo getWorkplaceInfoDB(DSLContext dslContext, Integer workplaceId) {
		WorkplaceInfo workplaceInfo = new WorkplaceInfo();
		
		//General Data
		Record workplaceRecord = dslContext.select().from(WORKPLACE)
				.where(WORKPLACE.ID.eq(workplaceId))
				.fetchOne();
		
		Integer workplaceEnterprise = workplaceRecord.get(WORKPLACE.ENTERPRISE);
		String workplaceDescription = workplaceRecord.get(WORKPLACE.DESCRIPTION);
		Integer workplaceAddress = workplaceRecord.get(WORKPLACE.ADDRESS);
		Byte workplaceEconomicConcert = workplaceRecord.get(WORKPLACE.ECONOMICAGREEMENT) == null ? (byte) -1 : workplaceRecord.get(WORKPLACE.ECONOMICAGREEMENT);
		Byte workplaceActive = workplaceRecord.get(WORKPLACE.ACTIVE);
		
		Result<Record> raddressRecords = dslContext.select().from(RADDRESS)
				.where(RADDRESS.REGISTRY.eq(workplaceEnterprise))
				.fetch();
		
		Map<Integer, String> addresses = new HashMap<Integer, String>();
		
		for(Record r : raddressRecords){
			Record1<String> geozoneName = dslContext.select(GEOZONE.NAME).from(GEOZONE)
					.where(GEOZONE.ID.eq(r.get(RADDRESS.GEOZONE)))
					.fetchOne();
			
			String addressStr = "";
			
			if(r.get(RADDRESS.STREET_TYPE) != "")
				addressStr += r.get(RADDRESS.STREET_TYPE) + " ";
			if(r.get(RADDRESS.ADDRESS) != "")
				addressStr += r.get(RADDRESS.ADDRESS) + " ";
			if(r.get(RADDRESS.NUMBER) != "")
				addressStr += r.get(RADDRESS.NUMBER) + " ";
			
			addressStr += geozoneName.get(0);
			
			addresses.put(r.get(RADDRESS.ID), addressStr);
		}
		
		//Payroll Data
		Record payrollWorkplaceRecord = dslContext.select().from(PAYROLL_WORKPLACE)
				.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId))
				.fetchOne();
		
		Integer payrollWorkplaceId = payrollWorkplaceRecord.get(PAYROLL_WORKPLACE.ID);
		Integer workplaceCalendar = payrollWorkplaceRecord.get(PAYROLL_WORKPLACE.CALENDAR);
		Integer workplaceAgreement = payrollWorkplaceRecord.get(PAYROLL_WORKPLACE.AGREEMENT);
		Integer workplaceActivity = payrollWorkplaceRecord.get(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY);
		
		Result<Record> caledarRecords = dslContext.select().from(CALENDAR).fetch();
		Map<Integer, String> calendars = new HashMap<Integer, String>();
		
		for(Record r : caledarRecords){
			calendars.put(r.get(CALENDAR.ID), r.get(CALENDAR.DESCRIPTION));
		}
		
		Result<Record> enterpriseActivityRecords = dslContext.select().from(ENTERPRISE_ACTIVITY)
				.where(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(workplaceEnterprise))
				.fetch();
		
		Map<Integer, String> activities = new HashMap<Integer, String>();
		
		for(Record r : enterpriseActivityRecords){
			activities.put(r.get(ENTERPRISE_ACTIVITY.ID), r.get(ENTERPRISE_ACTIVITY.DESCRIPTION));
		}
		
		Record agreementRecord = dslContext.select().from(AGREEMENT)
				.where(AGREEMENT.ID.eq(workplaceAgreement))
				.fetchOne();
		
		String agreementDescription = (null == agreementRecord) ? null : agreementRecord.get(AGREEMENT.DESCRIPTION);
		
		//SET General Data
		workplaceInfo.setDescription(workplaceDescription);
		workplaceInfo.setAddresses(addresses);
		workplaceInfo.setAddressId(workplaceAddress);
		workplaceInfo.setEconomicConcert(workplaceEconomicConcert);
		workplaceInfo.setActive(workplaceActive);
		
		//SET Payroll Data
		workplaceInfo.setCalendar(null);
		workplaceInfo.setCalendar(calendars);
		workplaceInfo.setCalendarId(workplaceCalendar);
		workplaceInfo.setAgreementId(workplaceAgreement);
		workplaceInfo.setAgreementDescription(agreementDescription);
		workplaceInfo.setActivities(activities);
		workplaceInfo.setActivityId(workplaceActivity);
		
		//TablesID
		workplaceInfo.setWorkplaceId(workplaceId);
		workplaceInfo.setPayrollWorkplaceId(payrollWorkplaceId);
		
		
		return workplaceInfo;
	}
	
	private static WorkplaceInfo setWorkplaceInfoDB(DSLContext dslContext, WorkplaceInfo workplaceInfo) {
		
		dslContext.update(PAYROLL_WORKPLACE)
			.set(PAYROLL_WORKPLACE.AGREEMENT, workplaceInfo.getAgreementId())
			.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, workplaceInfo.getActivityId())
			.set(PAYROLL_WORKPLACE.CALENDAR, (null == workplaceInfo.getCalendarId()) ? null : workplaceInfo.getCalendarId())
			.where(PAYROLL_WORKPLACE.ID.eq(workplaceInfo.getPayrollWorkplaceId()))
			.execute();
		
		dslContext.update(WORKPLACE)
			.set(WORKPLACE.DESCRIPTION, workplaceInfo.getDescription())
			.set(WORKPLACE.ADDRESS, workplaceInfo.getAddressId())
			.set(WORKPLACE.ECONOMICAGREEMENT, workplaceInfo.getEconomicConcert() == -1 ? null : workplaceInfo.getEconomicConcert())
			.set(WORKPLACE.ACTIVE, workplaceInfo.isActive())
			.where(WORKPLACE.ID.eq(workplaceInfo.getWorkplaceId()))
			.execute();
		
		return workplaceInfo;
	}

}
