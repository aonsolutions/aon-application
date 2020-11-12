package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;

public class JooqWorkplace {

	private static Settings SETTINGS = null;
	
	public static WorkplaceInfo getWorkplaceInfo(Connection conn, Integer workplaceId) {
		return getWorkplaceInfoDB(DSL.using(conn, getDefaultSettings()), workplaceId);
	}
	
	public static WorkplaceInfo setWorkplaceInfo(Connection conn, WorkplaceInfo workplaceInfo) {
		return setWorkplaceInfoDB(DSL.using(conn, getDefaultSettings()), workplaceInfo);
	}
	
	public static WorkplaceInfo createWorkplace(Connection conn, WorkplaceInfo workplaceInfo, Integer enterpriseId) {
		return createWorkplaceInfoDB(DSL.using(conn, getDefaultSettings()), workplaceInfo, enterpriseId);
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
		
		Integer workplaceDomain = workplaceRecord.get(WORKPLACE.DOMAIN);
		Integer workplaceEnterprise = workplaceRecord.get(WORKPLACE.ENTERPRISE);
		String workplaceDescription = workplaceRecord.get(WORKPLACE.DESCRIPTION);
		Integer workplaceAddress = workplaceRecord.get(WORKPLACE.ADDRESS);
		Byte workplaceEconomicConcert = workplaceRecord.get(WORKPLACE.ECONOMICAGREEMENT) == null ? (byte) -1 : workplaceRecord.get(WORKPLACE.ECONOMICAGREEMENT);
		
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
			if(null == r.get(RADDRESS.ADDRESS))
				continue;
			if(!r.get(RADDRESS.ADDRESS).isEmpty())
				addressStr += r.get(RADDRESS.ADDRESS) + ", ";
			if(null !=r.get(RADDRESS.NUMBER))
				if(!r.get(RADDRESS.NUMBER).isEmpty())
					addressStr += r.get(RADDRESS.NUMBER) + ", ";
			
			if(null != geozoneName)
				addressStr += geozoneName.get(0);
			
			addresses.put(r.get(RADDRESS.ID), addressStr);
		}
		
		//Payroll Data
		 Result<Record> payrollWorkplaceRecords = dslContext.select().from(PAYROLL_WORKPLACE)
				.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceId))
				.fetch();
		 
		Record payrollWorkplaceRecord = payrollWorkplaceRecords.get(0);
		
		Integer payrollWorkplaceId = null;
		String workplaceCalendar = null;
		Integer workplaceCalendarId = null;
		Integer workplaceAgreement = null;
		Integer workplaceActivity = null;
		
		if(null != payrollWorkplaceRecord) {
			payrollWorkplaceId = payrollWorkplaceRecord.get(PAYROLL_WORKPLACE.ID);
			workplaceCalendarId = payrollWorkplaceRecord.get(PAYROLL_WORKPLACE.CALENDAR);
			workplaceAgreement = payrollWorkplaceRecord.get(PAYROLL_WORKPLACE.AGREEMENT);
			workplaceActivity = payrollWorkplaceRecord.get(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY);
			
			if(null != workplaceCalendarId) {
				Record calendarRecord = dslContext.select().from(CALENDAR).where(CALENDAR.ID.eq(workplaceCalendarId)).fetchOne();
				Record parentHolidayRecord = dslContext.select().from(HOLIDAY).where(HOLIDAY.ID.eq(calendarRecord.get(CALENDAR.HOLIDAY))).fetchOne();
				if(null != parentHolidayRecord.get(HOLIDAY.HOLIDAY_)) {
					Record holidayRecord = dslContext.select().from(HOLIDAY).where(HOLIDAY.ID.eq(parentHolidayRecord.get(HOLIDAY.HOLIDAY_))).fetchOne();
					workplaceCalendar = holidayRecord.get(HOLIDAY.DESCRIPTION);
				}
			}
		}
		
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.eq(workplaceDomain))
				.fetchOne();
		
		Integer parentDomain = domainRecord.get(DOMAIN.PARENT);
		
		Result<Record> enterpriseActivityRecords = dslContext.select().from(ENTERPRISE_ACTIVITY)
				.where(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(workplaceEnterprise))
				.fetch();
		
		Map<Integer, String> activities = new HashMap<Integer, String>();
		
		for(Record r : enterpriseActivityRecords){
			activities.put(r.get(ENTERPRISE_ACTIVITY.ID), r.get(ENTERPRISE_ACTIVITY.DESCRIPTION));
		}
		
//		Record agreementRecord = dslContext.select().from(AGREEMENT)
//				.where(AGREEMENT.ID.eq(workplaceAgreement))
//				.fetchOne();
		
		//SET General Data
		workplaceInfo.setDomain(workplaceDomain);
		workplaceInfo.setDescription(workplaceDescription);
		workplaceInfo.setAddressId(workplaceAddress);
		workplaceInfo.setEconomicConcert(workplaceEconomicConcert);
		
		//SET Payroll Data
		workplaceInfo.setCalendarDescription(workplaceCalendar);
		workplaceInfo.setAgreementId(workplaceAgreement);
		workplaceInfo.setActivityId(workplaceActivity);
		
		//TablesID
		workplaceInfo.setWorkplaceId(workplaceId);
		workplaceInfo.setPayrollWorkplaceId(payrollWorkplaceId);
		
		
		return workplaceInfo;
	}
	
	private static WorkplaceInfo setWorkplaceInfoDB(DSLContext dslContext, WorkplaceInfo workplaceInfo) {
		
		if(null == workplaceInfo.getPayrollWorkplaceId())
			dslContext.insertInto(PAYROLL_WORKPLACE)
				.set(PAYROLL_WORKPLACE.DOMAIN, workplaceInfo.getDomain())
				.set(PAYROLL_WORKPLACE.WORKPLACE, workplaceInfo.getWorkplaceId())
				.set(PAYROLL_WORKPLACE.AGREEMENT, (null == workplaceInfo.getAgreementId() || workplaceInfo.getAgreementId() == -1) ? null : workplaceInfo.getAgreementId())
				.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, (null == workplaceInfo.getActivityId() || workplaceInfo.getActivityId() == -1) ? null : workplaceInfo.getActivityId())
//				.set(PAYROLL_WORKPLACE.CALENDAR, (null == workplaceInfo.getCalendarId()) ? null : workplaceInfo.getCalendarId())
				.execute();
				
		else
			dslContext.update(PAYROLL_WORKPLACE)
				.set(PAYROLL_WORKPLACE.AGREEMENT, (null == workplaceInfo.getAgreementId() || workplaceInfo.getAgreementId() == -1) ? null : workplaceInfo.getAgreementId())
				.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, (null == workplaceInfo.getActivityId() || workplaceInfo.getActivityId() == -1) ? null : workplaceInfo.getActivityId())
//				.set(PAYROLL_WORKPLACE.CALENDAR, (null == workplaceInfo.getCalendarId()) ? null : workplaceInfo.getCalendarId())
				.where(PAYROLL_WORKPLACE.ID.eq(workplaceInfo.getPayrollWorkplaceId()))
				.execute();
		
		dslContext.update(WORKPLACE)
			.set(WORKPLACE.DESCRIPTION, workplaceInfo.getDescription())
			.set(WORKPLACE.ADDRESS, workplaceInfo.getAddressId())
			.set(WORKPLACE.ECONOMICAGREEMENT, workplaceInfo.getEconomicConcert() == -1 ? null : workplaceInfo.getEconomicConcert())
			.where(WORKPLACE.ID.eq(workplaceInfo.getWorkplaceId()))
			.execute();
		
		return workplaceInfo;
	}

	public static List<Workplace> getWorkplaces(Workplace workplace, Integer domainId, Connection connection) {
		return getWorkplacesDB(DSL.using(connection, getDefaultSettings()), workplace, domainId);
	}

	private static List<Workplace> getWorkplacesDB(DSLContext dslContext, Workplace workplace, Integer domainId) {
		List<Workplace> workplaces  = new ArrayList<Workplace>();
		
		List<Integer> workplaceRecord = null;
		
		if(null != workplace) {
			workplaceRecord  = dslContext.select(WORKPLACE.ENTERPRISE).from(WORKPLACE)
					.where(WORKPLACE.ID.eq(workplace.getId()))
					.fetch(WORKPLACE.ENTERPRISE);
		} else {
			workplaceRecord  = dslContext.select(WORKPLACE.ENTERPRISE).from(WORKPLACE)
					.where(WORKPLACE.DOMAIN.eq(domainId))
					.fetch(WORKPLACE.ENTERPRISE);
		}
		
		Result<Record> workplacesRecords = dslContext.select().from(WORKPLACE)
				.where(WORKPLACE.ENTERPRISE.in(workplaceRecord))
				.orderBy(WORKPLACE.DESCRIPTION)
				.fetch();
		
		for(Record record : workplacesRecords) {
			Workplace workplaceAux = new Workplace();
			workplaceAux.setId(record.get(WORKPLACE.ID));
			workplaceAux.setDescription(record.get(WORKPLACE.DESCRIPTION));
			
			workplaces.add(workplaceAux);
		}
		
		return workplaces;
	}

	public static ActivitiesCCC getActivitiesCCC(Workplace workplace, Integer domainId, Connection connection) {
		return getActivitiesCCCDB(DSL.using(connection, getDefaultSettings()), workplace, domainId);
	}

	private static ActivitiesCCC getActivitiesCCCDB(DSLContext dslContext, Workplace workplace, Integer domainId) {
		ActivitiesCCC activitiesCCC = new ActivitiesCCC();
		
		//ENTERPRISE ACTIVITIES-CCC
		List<Integer> workplaceRecord = null;
		if(null != workplace) {
			 workplaceRecord = dslContext.select(WORKPLACE.ENTERPRISE).from(WORKPLACE)
					.where(WORKPLACE.ID.eq(workplace.getId()))
					.fetch(WORKPLACE.ENTERPRISE);
		} else {
			workplaceRecord = dslContext.select(WORKPLACE.ENTERPRISE).from(WORKPLACE)
					.where(WORKPLACE.DOMAIN.eq(domainId))
					.fetch(WORKPLACE.ENTERPRISE);
		}
		
		Result<Record> enterpriseActivityRecords = dslContext.select().from(ENTERPRISE_ACTIVITY)
				.where(ENTERPRISE_ACTIVITY.ENTERPRISE.in(workplaceRecord))
				.fetch();
		
		Map<Integer, String> activities = new HashMap<Integer, String>();
		
		for(Record r : enterpriseActivityRecords){
			activities.put(r.get(ENTERPRISE_ACTIVITY.ID), r.get(ENTERPRISE_ACTIVITY.DESCRIPTION));
			
			Result<Record> enterpriseCCCRecords = dslContext.select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(r.get(ENTERPRISE_ACTIVITY.ID)))
					.fetch();
			
			for(Record d : enterpriseCCCRecords){
				Integer geozoneId = d.get(ENTERPRISE_CCC.GEOZONE);
				String geozoneName = null;
				String geozoneCode = null;
				if(null != geozoneId){
					Record geozoneRecord = dslContext.select().from(GEOZONE)
							.where(GEOZONE.ID.eq(geozoneId))
							.fetchOne();
					
					geozoneName = geozoneRecord.get(GEOZONE.NAME);
					geozoneCode = geozoneRecord.get(GEOZONE.CODE);
				}
				activitiesCCC.addCCC(d.get(ENTERPRISE_CCC.ID), d.get(ENTERPRISE_CCC.CCC), d.get(ENTERPRISE_CCC.TYPE), geozoneName, geozoneCode, r.get(ENTERPRISE_ACTIVITY.ID));
			}
		}
		
		activitiesCCC.setActivities(activities);
		
		return activitiesCCC;
	}

	private static WorkplaceInfo createWorkplaceInfoDB(DSLContext dslContext, WorkplaceInfo workplaceInfo, Integer enterpriseId) {
		Record enterpriseRecord = dslContext.select().from(ENTERPRISE)
				.where(ENTERPRISE.REGISTRY.eq(enterpriseId))
				.fetchOne();
		
		Integer domainId = enterpriseRecord.get(ENTERPRISE.DOMAIN);
		Integer scopeId = enterpriseRecord.get(ENTERPRISE.SCOPE);
		
		//WORKPALCE
		WorkplaceRecord workplaceRecord = dslContext.insertInto(WORKPLACE)
			.set(WORKPLACE.DOMAIN, domainId)
			.set(WORKPLACE.ENTERPRISE, enterpriseId)
			.set(WORKPLACE.DESCRIPTION, workplaceInfo.getDescription())
			.set(WORKPLACE.ADDRESS, workplaceInfo.getAddressId())
			.set(WORKPLACE.SCOPE, scopeId)
			.set(WORKPLACE.ECONOMICAGREEMENT, workplaceInfo.getEconomicConcert() == 0 ? null : workplaceInfo.getEconomicConcert())
			.returning(WORKPLACE.ID)
			.fetchOne();
		
		Integer workplaceId = workplaceRecord.getId();
		
		//PAYROLL_WORKPLACE
		dslContext.insertInto(PAYROLL_WORKPLACE)
			.set(PAYROLL_WORKPLACE.DOMAIN, domainId)
			.set(PAYROLL_WORKPLACE.WORKPLACE, workplaceId)
			.set(PAYROLL_WORKPLACE.AGREEMENT, workplaceInfo.getAgreementId())
			.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, workplaceInfo.getActivityId())
//			.set(PAYROLL_WORKPLACE.CALENDAR, workplaceInfo.getCalendarId())
			.execute();
		
		return workplaceInfo;
	}

	public static Map<String, String> getPayMethods(Connection conn, Integer domainId) {
		return getPayMethodsDB(DSL.using(conn, getDefaultSettings()), domainId);
	}

	private static Map<String, String> getPayMethodsDB(DSLContext dslContext, Integer domainId) {
		Map<String, String> payMethods = new HashMap<String, String>();
		
		Record2<Byte, Integer> domainRecord = dslContext.select(DOMAIN.ENABLEHEREDITY, DOMAIN.PARENT).from(DOMAIN)
				.where(DOMAIN.ID.eq(domainId)).fetchOne();
		
		Result<Record> payMethodRecords = null;
		Byte hasHeredity = domainRecord.get(DOMAIN.ENABLEHEREDITY);
		
		if(hasHeredity == (byte) 1) {
			Integer parentDomainId = domainRecord.get(DOMAIN.PARENT);
			
			payMethodRecords = dslContext.select().from(PAY_METHOD)
					.where(PAY_METHOD.DOMAIN.eq(domainId)
							.or(PAY_METHOD.DOMAIN.eq(parentDomainId)))
					.fetch();
		} else {
			payMethodRecords = dslContext.select().from(PAY_METHOD)
					.where(PAY_METHOD.DOMAIN.eq(domainId))
					.fetch();
		}
		
		for(Record r : payMethodRecords) {
			Integer payMethodId = r.get(PAY_METHOD.ID);
			String payMethodDescription = r.get(PAY_METHOD.NAME);
			
			payMethods.put(payMethodDescription, payMethodId.toString());
		}
		
		return payMethods;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
