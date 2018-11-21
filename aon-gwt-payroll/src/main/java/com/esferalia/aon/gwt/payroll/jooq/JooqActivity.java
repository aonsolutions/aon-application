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
		Integer domain = enterpriseActivity.get(ENTERPRISE_ACTIVITY.DOMAIN);
		Integer cnae2009Id = enterpriseActivity.get(ENTERPRISE_ACTIVITY.CNAE2009);
		Byte regime = enterpriseActivity.get(ENTERPRISE_ACTIVITY.VAT_REGIME);
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
		activityInfo.setDomain(domain);
		activityInfo.setCnae2009Code(cnae2009Code);
		activityInfo.setCnae2009Title(cnae2009Title);
		activityInfo.setRegime(getRegimeNameByType(regime));
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
			
			Result<Record> geozoneName = dslContext.select()
					.from(GEOZONE)
					.where(GEOZONE.ID.eq(geozoneId))
						//.and(GEOZONE.DOMAIN.eq(domain))
					.fetch();
			
			String geozone = geozoneName.get(0).get(GEOZONE.NAME);
			
			activityInfo.insertCCC(cccId, ccc, cccRegimeCode, ccc, cccRegime, geozone);
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
