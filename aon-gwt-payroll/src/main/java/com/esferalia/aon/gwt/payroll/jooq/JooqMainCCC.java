package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.MainCCCInfo;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;

public class JooqMainCCC {

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static MainCCCInfo getMainCCCInfo(Connection conn, Integer domainId, Integer userId) {
		return getMainCCCInfoDB(DSL.using(conn, getDefaultSettings()), domainId, userId);
	}

	private static MainCCCInfo getMainCCCInfoDB(DSLContext dslContext, Integer domainId, Integer userId) {
		MainCCCInfo mainCCCInfo = new MainCCCInfo();
		Map<Integer, String> domainActivities = new HashMap<Integer, String>();
		Map<Integer, CCCInfo> cccs = new HashMap<Integer, CCCInfo>();
		
		//ENTERPRISE_ACTIVITY
		List<Record> enterpriseActivitiesRecords = dslContext.select().from(ENTERPRISE_ACTIVITY)
				.where(ENTERPRISE_ACTIVITY.DOMAIN.eq(domainId))
				.fetch();
		
		for(Record enterpriseActivityRecord : enterpriseActivitiesRecords) {
			Integer enterpriseActivityId = enterpriseActivityRecord.get(ENTERPRISE_ACTIVITY.ID);
			String enterpriseActivityDescription = enterpriseActivityRecord.get(ENTERPRISE_ACTIVITY.DESCRIPTION);
			
			domainActivities.put(enterpriseActivityId, enterpriseActivityDescription);
			
			//ENTERPRISE_CCC
			Result<Record> enterpriseCCCRecords = dslContext.select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(enterpriseActivityId))
					.fetch();
			
			for(Record enterpriseCCCRecord : enterpriseCCCRecords) {
				Integer cccId = enterpriseCCCRecord.get(ENTERPRISE_CCC.ID);
				String ccc = enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC);
				Byte cccRegime = enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE);
				String cccRegimeCode = getCCCRegimeCode(cccRegime);
				Integer geozoneId = enterpriseCCCRecord.get(ENTERPRISE_CCC.GEOZONE);
				
				Boolean useByContracts = false;
				
				Result<Record> contractRecord = dslContext.select().from(CONTRACT)
						.where(CONTRACT.ENTERPRISE_CCC.eq(cccId))
						.and(CONTRACT.ID.greaterThan(0))
						.fetch();
				
				if(null != contractRecord && !contractRecord.isEmpty())
					useByContracts = true;
				
				String geozone = null;
				String geozoneCode = null;
				if(null != geozoneId) {
					Result<Record> geozoneName = dslContext.select()
							.from(GEOZONE)
							.where(GEOZONE.ID.eq(geozoneId))
							.fetch();
					
					geozone = geozoneName.get(0).get(GEOZONE.NAME);
					geozoneCode = geozoneName.get(0).get(GEOZONE.CODE);
				}
				
				CCCInfo cccInfo = new CCCInfo(ccc, cccRegimeCode, ccc, cccRegime, geozone, geozoneCode, enterpriseActivityId, cccId, useByContracts);
				cccs.put(cccId, cccInfo);	
			}
		}
		
		mainCCCInfo.setActivities(domainActivities);
		mainCCCInfo.setCccs(cccs);
		
		return mainCCCInfo;
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

	public static void setMainCCCInfo(Connection conn, Integer domainId, Integer userId, MainCCCInfo mainCCCInfo) {
		setMainCCCInfoDB(DSL.using(conn, getDefaultSettings()), domainId, userId, mainCCCInfo);
	}

	private static void setMainCCCInfoDB(DSLContext dslContext, Integer domainId, Integer userId, MainCCCInfo mainCCCInfo) {
		for(Integer deleteId : mainCCCInfo.getDeletedCCCs().keySet())
			dslContext.delete(ENTERPRISE_CCC).where(ENTERPRISE_CCC.ID.eq(deleteId)).execute();
		
		for(Entry<Integer, CCCInfo> entry : mainCCCInfo.getCccs().entrySet()) {
			Integer cccId = entry.getKey();
			CCCInfo cccInfo = entry.getValue();
			
			Record domainRecord = dslContext.select().from(DOMAIN)
					.where(DOMAIN.ID.eq(domainId))
					.fetchOne();
			
			Integer parentDomain = domainRecord.get(DOMAIN.PARENT);
			Boolean hasHerefity = domainRecord.get(DOMAIN.ENABLEHEREDITY) == (byte)0 ? false : true;
			
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
							.and(GEOZONE.DOMAIN.eq(domainId))
							.fetch();
				}
				
				
				if(null == geozoneRecords || geozoneRecords.isEmpty()) {
					//TODO: No existe este geozone
//					Result<Record> gezoneRecordsInfo = dslContext.select().from(GEOZONE)
//							.where(GEOZONE.NAME.eq(cccInfo.getGeozone()))
//							.fetch();
					if(cccInfo.getGeozoneCode() != null) {
						geozoneId = dslContext.insertInto(GEOZONE)
							.set(GEOZONE.DOMAIN, domainId)
							.set(GEOZONE.NAME, cccInfo.getGeozone()) //gezoneRecordsInfo.get(0).get(GEOZONE.NAME))
							.set(GEOZONE.CODE, cccInfo.getGeozoneCode()) //gezoneRecordsInfo.get(0).get(GEOZONE.CODE))
							.returning(GEOZONE.ID)
							.fetchOne().get(GEOZONE.ID);
					}
				}else {
					geozoneId = geozoneRecords.get(0).get(GEOZONE.ID);
					
				}
				
			}
			
			if(cccId >= 0) {
				dslContext.update(ENTERPRISE_CCC)
					.set(ENTERPRISE_CCC.CCC, cccInfo.getCcc())
					.set(ENTERPRISE_CCC.TYPE, cccInfo.getType())
					.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, cccInfo.getActivityId())
					.set(ENTERPRISE_CCC.GEOZONE, geozoneId)
					.where(ENTERPRISE_CCC.ID.eq(cccId))
					.execute();
			}else {
				if(null == cccInfo.getActivityId()  || cccInfo.getActivityId() == -1)
					insertCompleteComunicaCCC(dslContext, domainId, geozoneId, cccInfo);
				else
					dslContext.insertInto(ENTERPRISE_CCC, ENTERPRISE_CCC.DOMAIN, ENTERPRISE_CCC.CCC, ENTERPRISE_CCC.TYPE, ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, ENTERPRISE_CCC.GEOZONE)
						.values(domainId, cccInfo.getCcc(), cccInfo.getType(), cccInfo.getActivityId(), geozoneId)
						.execute();
			}
		}

	}

	private static void insertCompleteComunicaCCC(DSLContext dslContext, Integer domainId, Integer geozoneId, CCCInfo cccInfo) {
		EnterpriseRecord enterpriseRecord = dslContext.select().from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId))
				.fetchOptionalInto(ENTERPRISE)
				.orElseThrow(IllegalArgumentException::new);
		
		Integer enterpriseId = enterpriseRecord.getRegistry();
		
		Record enterpriseActivityRecord = dslContext.insertInto(ENTERPRISE_ACTIVITY)
			.set(ENTERPRISE_ACTIVITY.DOMAIN, domainId)
			.set(ENTERPRISE_ACTIVITY.ENTERPRISE, enterpriseId)
			.set(ENTERPRISE_ACTIVITY.DESCRIPTION, "Actividad " + cccInfo.getGeozone())
			.set(ENTERPRISE_ACTIVITY.TYPE, (byte)0)
			.returning(ENTERPRISE_ACTIVITY.ID)
			.fetchOne();
		
		Integer enterpriseActivityId = enterpriseActivityRecord.get(ENTERPRISE_ACTIVITY.ID);
		
		dslContext.insertInto(ENTERPRISE_CCC, ENTERPRISE_CCC.DOMAIN, ENTERPRISE_CCC.CCC, ENTERPRISE_CCC.TYPE, ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, ENTERPRISE_CCC.GEOZONE)
			.values(domainId, cccInfo.getCcc(), cccInfo.getType(), enterpriseActivityId, geozoneId)
			.execute();
		
		setWorkplace(dslContext, domainId, enterpriseRecord, enterpriseActivityId, cccInfo.getCcc(), geozoneId);
			
	}
	
	private static void setWorkplace(DSLContext dslContext, Integer domainId, EnterpriseRecord enterpriseRecord, Integer enterpriseActivityId, String ccc, Integer geozoneId){
		Integer registry = enterpriseRecord.getRegistry();
		
		///---RADDRESS
		RaddressRecord raddress = dslContext
		.select()
		.from(RADDRESS)
		.where(RADDRESS.DOMAIN.eq(domainId))
		.and(RADDRESS.GEOZONE.eq(geozoneId))
		.and(RADDRESS.REGISTRY.eq(registry))
		.fetchStreamInto(RADDRESS).findFirst()
		.orElseGet(() ->
			dslContext
			.insertInto(RADDRESS)
			.set(RADDRESS.DOMAIN, domainId)
			.set(RADDRESS.GEOZONE, geozoneId )
			.set(RADDRESS.TYPE, (byte)0)
			.set(RADDRESS.REGISTRY, registry)
			.returning()
			.fetchOne()
		);
		//---------WORKPLACE
		WorkplaceRecord workplaceRecord = dslContext
		.select()
		.from(WORKPLACE)
		.innerJoin(RADDRESS).onKey()
		.innerJoin(GEOZONE).onKey()
		.where(WORKPLACE.DOMAIN.eq(domainId))
		.and(GEOZONE.CODE.eq(ccc.substring(0,2)))
		.fetchStreamInto(WORKPLACE).findFirst()
		.orElseGet(() ->
			dslContext
			.insertInto(WORKPLACE)
			.set(WORKPLACE.DOMAIN, domainId)
			.set(WORKPLACE.ENTERPRISE, registry)
			.set(WORKPLACE.DESCRIPTION, "CT AUTOGENERADO")
			.set(WORKPLACE.ADDRESS, raddress.getId())
			.set(WORKPLACE.SCOPE, enterpriseRecord.getScope())
			.returning()
			.fetchOne()
		);
		
		//---------PAYROLL_WORKPLACE
		PayrollWorkplaceRecord payrollRecord = dslContext
		.select()
		.from(PAYROLL_WORKPLACE)
		.innerJoin(WORKPLACE).onKey()
		.where(WORKPLACE.DOMAIN.eq(domainId))
		.fetchStreamInto(PAYROLL_WORKPLACE).findFirst()
		.orElseGet(() ->
				dslContext
				.insertInto(PAYROLL_WORKPLACE)
				.set(PAYROLL_WORKPLACE.DOMAIN, domainId)
				.set(PAYROLL_WORKPLACE.WORKPLACE, workplaceRecord.getId())
				.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, enterpriseActivityId)
				.returning()
				.fetchOne()
		);

		if(null == payrollRecord.getEnterpriseActivity()){
			dslContext
			.update(PAYROLL_WORKPLACE)
			.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, enterpriseActivityId)
			.where(PAYROLL_WORKPLACE.ID.in(payrollRecord.getId()))
			.execute();
		}


	}
	

}
