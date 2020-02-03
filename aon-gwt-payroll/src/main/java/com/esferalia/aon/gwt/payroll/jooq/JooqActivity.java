package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;

public class JooqActivity {

	private static Settings SETTINGS = null;
	
	public static Map<String, String> getCNAE2009(Connection conn) {
		return getCNAE2009InfoDB(DSL.using(conn, getDefaultSettings()));
	}

	public static ActivityInfo getActivity(Integer activityId, Connection conn) {
		return getActivityInfoDB(activityId, DSL.using(conn, getDefaultSettings()));
	}

	public static ActivityInfo updateActivity(ActivityInfo activityInfo, Connection conn) {
		return updateActivityInfoDB(activityInfo, DSL.using(conn, getDefaultSettings()));
	}
	
	public static ActivityInfo createActivity(ActivityInfo activityInfo, Connection conn) {
		return createActivityInfoDB(activityInfo, DSL.using(conn, getDefaultSettings()));
	}
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static Map<String, String> getCNAE2009InfoDB(DSLContext dslContext) {
		
		Map<String, String> cnae2009Map = new HashMap<>();
		
		Result<Record> cnae2009Records = dslContext.select().from(CNAE2009)
				.orderBy(CNAE2009.CODE)
				.fetch();
		
		for(Record r : cnae2009Records)
			cnae2009Map.put(r.get(CNAE2009.CODE), r.get(CNAE2009.TITLE));
		
		return cnae2009Map;
	}
	
	private static ActivityInfo getActivityInfoDB(Integer activityId, DSLContext dslContext) {
		ActivityInfo activityInfo = new ActivityInfo();
		
		//CNAE2009
		Result<Record> cnae2009Records = dslContext.select().from(CNAE2009)
				.orderBy(CNAE2009.CODE)
				.fetch();
		
		for(Record r : cnae2009Records)
			activityInfo.addCNAE2009(r.get(CNAE2009.CODE), r.get(CNAE2009.TITLE));
		
		//ENTERPRISE_ACTIVITY
		Record enterpriseActivity = dslContext.select().from(ENTERPRISE_ACTIVITY)
				.where(ENTERPRISE_ACTIVITY.ID.eq(activityId))
				.fetchOne();
		
		String description = enterpriseActivity.get(ENTERPRISE_ACTIVITY.DESCRIPTION);
		Integer enterprise = enterpriseActivity.get(ENTERPRISE_ACTIVITY.ENTERPRISE);
		Integer domain = enterpriseActivity.get(ENTERPRISE_ACTIVITY.DOMAIN);
		Integer cnae2009Id = enterpriseActivity.get(ENTERPRISE_ACTIVITY.CNAE2009);
		Date startDate = enterpriseActivity.get(ENTERPRISE_ACTIVITY.START_DATE);
		Date endDate = enterpriseActivity.get(ENTERPRISE_ACTIVITY.END_DATE);
		Byte regime = (byte) 0;
		Byte principal = enterpriseActivity.get(ENTERPRISE_ACTIVITY.PRINCIPAL);
		
		//ENTERPRISE_ACTIVITY_CNAE2009
		Record enterpriseActivityCNAE2009 = dslContext.select().from(CNAE2009)
				.where(CNAE2009.ID.eq(cnae2009Id))
				.fetchOne();
		
		String cnae2009Code = enterpriseActivityCNAE2009.get(CNAE2009.CODE);
		String cnae2009Title = enterpriseActivityCNAE2009.get(CNAE2009.TITLE);
		
		//SET ACTIVITY INFO
		activityInfo.setId(activityId);
		activityInfo.setDescription(description);
		activityInfo.setEnterprise(enterprise);
		activityInfo.setDomain(domain);
		activityInfo.setCnae2009Code(cnae2009Code);
		activityInfo.setCnae2009Title(cnae2009Title);
		activityInfo.setRegime(getRegimeNameByType(regime));
		activityInfo.setStartDate(startDate);
		activityInfo.setEndDate(endDate);
		activityInfo.setActive((principal == 0) ? false : true);
		
		//ENTERPRISE_CCC
		Result<Record> enterpriseActivityRecords = dslContext.select().from(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(activityId))
				.fetch();
		
		for(Record r : enterpriseActivityRecords) {
			Integer cccId = r.get(ENTERPRISE_CCC.ID);
			String ccc = r.get(ENTERPRISE_CCC.CCC);
			Byte cccRegime = r.get(ENTERPRISE_CCC.TYPE);
			String cccRegimeCode = getCCCRegimeCode(cccRegime);
			Integer geozoneId = r.get(ENTERPRISE_CCC.GEOZONE);
			
			Boolean useByContracts = false;
			
			Result<Record> contractRecord = dslContext.select().from(CONTRACT)
					.where(CONTRACT.ENTERPRISE_CCC.eq(cccId))
					.and(CONTRACT.ID.greaterThan(0))
					.fetch();
			
			if(null != contractRecord && !contractRecord.isEmpty())
				useByContracts = true;
			
			String geozone = null;
			if(null != geozoneId) {
				Result<Record> geozoneName = dslContext.select()
						.from(GEOZONE)
						.where(GEOZONE.ID.eq(geozoneId))
						.fetch();
				
				geozone = geozoneName.get(0).get(GEOZONE.NAME);
			}
			
			activityInfo.insertCCC(cccId, ccc, cccRegimeCode, ccc, cccRegime, geozone, useByContracts);
		}
		
		return activityInfo;
	}
	
	private static ActivityInfo updateActivityInfoDB(ActivityInfo activityInfo, DSLContext dslContext) {
		
		//CNAE2009
		Integer cnae2009Id;
		
		Record cnae2009Record = dslContext.select().from(CNAE2009)
				.where(CNAE2009.CODE.eq(activityInfo.getCnae2009Code()))
				.fetchOne();
		
		cnae2009Id = cnae2009Record.get(CNAE2009.ID);
		
		//ENTERPRISE_ACTIVITY
		
		//Check if this activity is principal
		if(activityInfo.getActive())
			dslContext.update(ENTERPRISE_ACTIVITY)
				.set(ENTERPRISE_ACTIVITY.PRINCIPAL, (byte)0)
				.where(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(activityInfo.getEnterprise()))
				.execute();
		
		
		dslContext.update(ENTERPRISE_ACTIVITY)
			.set(ENTERPRISE_ACTIVITY.DESCRIPTION, activityInfo.getDescription())
			.set(ENTERPRISE_ACTIVITY.CNAE2009, cnae2009Id)
//			.set(ENTERPRISE_ACTIVITY.VAT_REGIME, (byte) 0)
			.set(ENTERPRISE_ACTIVITY.START_DATE, (null == activityInfo.getStartDate()) ? null : new Date(activityInfo.getStartDate().getTime()))
			.set(ENTERPRISE_ACTIVITY.END_DATE, (null == activityInfo.getEndDate()) ? null : new Date(activityInfo.getEndDate().getTime()))
			.set(ENTERPRISE_ACTIVITY.PRINCIPAL, activityInfo.getActive() == false ? (byte)0 : (byte)1)
			.where(ENTERPRISE_ACTIVITY.ID.eq(activityInfo.getId()))
			.execute();
		
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.eq(activityInfo.getDomain()))
				.fetchOne();
		
		Integer parentDomain = domainRecord.get(DOMAIN.PARENT);
		Boolean hasHerefity = domainRecord.get(DOMAIN.ENABLEHEREDITY) == (byte)0 ? false : true;
		
		//ENTERPRISE_CCC
		for(Integer cccId : activityInfo.getDeleteCccs().keySet())
			dslContext.delete(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.ID.eq(cccId))
				.execute();
		
		for(Entry<Integer, CCCInfo> entry : activityInfo.getCccs().entrySet()) {
			Integer cccId = entry.getKey();
			CCCInfo cccInfo = entry.getValue();
			
			Result<Record> geozoneRecords = null;
			Integer geozoneId = null;
			
			if(null != cccInfo.getGeozone()) {
			
				if(hasHerefity) {
					geozoneRecords = dslContext.select().from(GEOZONE)
							.where(GEOZONE.NAME.eq(cccInfo.getGeozone()))
							.and(GEOZONE.DOMAIN.eq(parentDomain))
							.fetch();
				}else {
					geozoneRecords = dslContext.select().from(GEOZONE)
							.where(GEOZONE.NAME.eq(cccInfo.getGeozone()))
							.and(GEOZONE.DOMAIN.eq(activityInfo.getDomain()))
							.fetch();
				}
				
				
				if(null == geozoneRecords || geozoneRecords.isEmpty()) {
					//TODO: No existe este geozone
					Result<Record> gezoneRecordsInfo = dslContext.select().from(GEOZONE)
							.where(GEOZONE.NAME.eq(cccInfo.getGeozone()))
							.fetch();
					
					geozoneId = dslContext.insertInto(GEOZONE)
							.set(GEOZONE.DOMAIN, activityInfo.getDomain())
							.set(GEOZONE.NAME, gezoneRecordsInfo.get(0).get(GEOZONE.NAME))
							.set(GEOZONE.CODE, gezoneRecordsInfo.get(0).get(GEOZONE.CODE))
							.returning(GEOZONE.ID)
							.fetchOne().get(GEOZONE.ID);
				}else {
					geozoneId = geozoneRecords.get(0).get(GEOZONE.ID);
					
				}
				
			}
			
			if(cccId >= 0) {
				dslContext.update(ENTERPRISE_CCC)
					.set(ENTERPRISE_CCC.CCC, cccInfo.getCcc())
					.set(ENTERPRISE_CCC.TYPE, cccInfo.getType())
					.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, activityInfo.getId())
					.set(ENTERPRISE_CCC.GEOZONE, geozoneId)
					.where(ENTERPRISE_CCC.ID.eq(cccId))
					.execute();
			}else {
				dslContext.insertInto(ENTERPRISE_CCC, ENTERPRISE_CCC.DOMAIN, ENTERPRISE_CCC.CCC, ENTERPRISE_CCC.TYPE, ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, ENTERPRISE_CCC.GEOZONE)
					.values(activityInfo.getDomain(), cccInfo.getCcc(), cccInfo.getType(), activityInfo.getId(), geozoneId)
					.execute();
			}
			
		}
		
		return activityInfo;
	}
	
	private static ActivityInfo createActivityInfoDB(ActivityInfo activityInfo, DSLContext dslContext) {
		
		//CNAE2009
		Integer cnae2009Id;
		
		Record cnae2009Record = dslContext.select().from(CNAE2009)
				.where(CNAE2009.CODE.eq(activityInfo.getCnae2009Code()))
				.fetchOne();
		
		cnae2009Id = cnae2009Record.get(CNAE2009.ID);
		
		//ENTERPRISE_ACTIVITY
		
			//Check if this activity is principal
			if(activityInfo.getActive())
				dslContext.update(ENTERPRISE_ACTIVITY)
					.set(ENTERPRISE_ACTIVITY.PRINCIPAL, (byte)0)
					.where(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(activityInfo.getEnterprise()))
					.execute();
		
		EnterpriseActivityRecord activityId = dslContext.insertInto(ENTERPRISE_ACTIVITY)
			.set(ENTERPRISE_ACTIVITY.DOMAIN, activityInfo.getDomain())
			.set(ENTERPRISE_ACTIVITY.DESCRIPTION, activityInfo.getDescription())
			.set(ENTERPRISE_ACTIVITY.ENTERPRISE, activityInfo.getEnterprise())
			.set(ENTERPRISE_ACTIVITY.TYPE, (byte)0)
			.set(ENTERPRISE_ACTIVITY.CNAE2009, cnae2009Id)
			.set(ENTERPRISE_ACTIVITY.START_DATE, (null == activityInfo.getStartDate()) ? null : new Date(activityInfo.getStartDate().getTime()))
			.set(ENTERPRISE_ACTIVITY.END_DATE, (null == activityInfo.getEndDate()) ? null : new Date(activityInfo.getEndDate().getTime()))
			.set(ENTERPRISE_ACTIVITY.PRINCIPAL, (activityInfo.getActive() == false) ? (byte)0 : (byte)1)
			.returning(ENTERPRISE_ACTIVITY.ID)
			.fetchOne();
		
		activityInfo.setId(activityId.getId());
		
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.eq(activityInfo.getDomain()))
				.fetchOne();
		
		Integer parentDomain = domainRecord.get(DOMAIN.PARENT);
		Boolean hasHerefity = domainRecord.get(DOMAIN.ENABLEHEREDITY) == (byte)0 ? false : true;
		
		//ENTERPRISE_CCC
		for(Entry<Integer, CCCInfo> entry : activityInfo.getCccs().entrySet()) {
			CCCInfo cccInfo = entry.getValue();
			
			Result<Record> geozoneRecords = null;
			Integer geozoneId = null;
			
			if(null != cccInfo.getGeozone()) {
				if(hasHerefity) {
					geozoneRecords = dslContext.select().from(GEOZONE)
							.where(GEOZONE.NAME.eq(cccInfo.getGeozone()))
							.and(GEOZONE.DOMAIN.eq(parentDomain))
							.fetch();
				}else {
					geozoneRecords = dslContext.select().from(GEOZONE)
							.where(GEOZONE.NAME.eq(cccInfo.getGeozone()))
							.and(GEOZONE.DOMAIN.eq(activityInfo.getDomain()))
							.fetch();
				}
				
				if(null == geozoneRecords || geozoneRecords.isEmpty()) {
					//TODO: No existe este geozone
					Result<Record> gezoneRecordsInfo = dslContext.select().from(GEOZONE)
							.where(GEOZONE.NAME.eq(cccInfo.getGeozone()))
							.fetch();
					
					geozoneId = dslContext.insertInto(GEOZONE)
							.set(GEOZONE.DOMAIN, activityInfo.getDomain())
							.set(GEOZONE.NAME, gezoneRecordsInfo.get(0).get(GEOZONE.NAME))
							.set(GEOZONE.CODE, gezoneRecordsInfo.get(0).get(GEOZONE.CODE))
							.returning(GEOZONE.ID)
							.fetchOne().get(GEOZONE.ID);
				}else {
					geozoneId = geozoneRecords.get(0).get(GEOZONE.ID);
				}
			}
			
			dslContext.insertInto(ENTERPRISE_CCC, ENTERPRISE_CCC.DOMAIN, ENTERPRISE_CCC.CCC, ENTERPRISE_CCC.TYPE, ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, ENTERPRISE_CCC.GEOZONE)
				.values(activityInfo.getDomain(), cccInfo.getCcc(), cccInfo.getType(), activityInfo.getId(), geozoneId)
				.execute();
		}
		
		return activityInfo;
	}

	private static String getCCCRegimeCode(Byte cccRegime) {
		switch (cccRegime) {
		case 0:
			return "0111";
		case 1:
			return "0111";
		case 2:
			return "0111";
		case 3:
			return "0111";
		case 4:
			return "0111";
		case 5:
			return "0111";
		case 6:
			return "0138";
		case 7:
			return "0163";
		case 8:
			return "0112";
		default:
			return "0111";
		}
	}

	private static String getRegimeNameByType(Byte regime) {
		switch (regime) {
		case 0:
			return "General";
		default:
			return "General";
		}
	}
}
