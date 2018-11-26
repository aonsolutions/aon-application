package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
import com.esferalia.aon.jooq.tables.records.PayMethodRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RbankRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;

public class JooqActivity {

	private static Settings SETTINGS = null;
	
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
			
			Result<Record> geozoneName = dslContext.select()
					.from(GEOZONE)
					.where(GEOZONE.ID.eq(geozoneId))
						//.and(GEOZONE.DOMAIN.eq(domain))
					.fetch();
			
			String geozone = geozoneName.get(0).get(GEOZONE.NAME);
			
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
		
		//ENTERPRISE_CCC
		for(Integer cccId : activityInfo.getDeleteCccs().keySet())
			dslContext.delete(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.ID.eq(cccId))
				.execute();
		
		for(Entry<Integer, CCCInfo> entry : activityInfo.getCccs().entrySet()) {
			Integer cccId = entry.getKey();
			CCCInfo cccInfo = entry.getValue();
			
			Result<Record> geozoneRecords = dslContext.select().from(GEOZONE)
					.where(GEOZONE.NAME.eq(cccInfo.getGeozone()))
					.fetch();
			
			Integer geozoneId = 0;
			if(null == geozoneRecords || geozoneRecords.isEmpty()) {
				//TODO: No existe este geozone
			}else {
				geozoneId = geozoneRecords.get(0).get(GEOZONE.ID);
			}
			
			if(cccId >= 0) {
				dslContext.update(ENTERPRISE_CCC)
					.set(ENTERPRISE_CCC.CCC, cccInfo.getCcc())
					.set(ENTERPRISE_CCC.TYPE, cccInfo.getType())
					.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, activityInfo.getId())
					.set(ENTERPRISE_CCC.GEOZONE, geozoneId)
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
		
		dslContext.insertInto(ENTERPRISE_ACTIVITY)
			.set(ENTERPRISE_ACTIVITY.DOMAIN, activityInfo.getDomain())
			.set(ENTERPRISE_ACTIVITY.DESCRIPTION, activityInfo.getDescription())
			.set(ENTERPRISE_ACTIVITY.ENTERPRISE, activityInfo.getEnterprise())
			.set(ENTERPRISE_ACTIVITY.TYPE, (byte)0)
			.set(ENTERPRISE_ACTIVITY.CNAE2009, cnae2009Id)
			.set(ENTERPRISE_ACTIVITY.START_DATE, (null == activityInfo.getStartDate()) ? null : new Date(activityInfo.getStartDate().getTime()))
			.set(ENTERPRISE_ACTIVITY.END_DATE, (null == activityInfo.getEndDate()) ? null : new Date(activityInfo.getEndDate().getTime()))
			.set(ENTERPRISE_ACTIVITY.PRINCIPAL, (activityInfo.getActive() == false) ? (byte)0 : (byte)1)
			.execute();
		
		//ENTERPRISE_CCC
		for(Entry<Integer, CCCInfo> entry : activityInfo.getCccs().entrySet()) {
			CCCInfo cccInfo = entry.getValue();
			
			Result<Record> geozoneRecords = dslContext.select().from(GEOZONE)
					.where(GEOZONE.NAME.eq(cccInfo.getGeozone()))
					.fetch();
			
			Integer geozoneId = 0;
			if(null == geozoneRecords || geozoneRecords.isEmpty()) {
				//TODO: No existe este geozone
			}else {
				geozoneId = geozoneRecords.get(0).get(GEOZONE.ID);
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
