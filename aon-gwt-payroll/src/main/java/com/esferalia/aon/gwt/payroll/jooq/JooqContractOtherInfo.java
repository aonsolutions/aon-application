package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqContractOtherInfo {

	// ---------------------------------------------------- Constructor
	
	private JooqContractOtherInfo() {
		super();
	}
	
	// ---------------------------------------------------- Settings

	private static Settings settings = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	// ---------------------------------------------------- DataBase
	
	public static  Map<String, String> getContractOtherInfo(Connection conn, Integer domainId, Integer parentDomainId, Integer contractId, Integer contractType) {
		return getContractOtherInfoDB(DSL.using(conn, getDefaultSettings()), domainId, parentDomainId, contractId, contractType);
	}
	
	private static Map<String, String> getContractOtherInfoDB(DSLContext dslContext, Integer domainId, Integer parentDomainId, Integer contractId, Integer contractType) {
		Map<String, String> contractOtherInfoMap = new HashMap<>();
		List<String> contractOtherDataNames = getContractOtherDataListNames(contractType);
		
		// Variables globales
		
		Result<Record> contractOtherDataRecords = dslContext.select().from(CONTRACT_INFO)
				.where(CONTRACT_INFO.NAME.in(contractOtherDataNames))
				.and(CONTRACT_INFO.CONTRACT.isNull())
				.and(CONTRACT_INFO.DOMAIN.eq(domainId).or(CONTRACT_INFO.DOMAIN.eq(parentDomainId)))
				.orderBy(CONTRACT_INFO.START_DATE.desc())
				.fetch();
		
		for(Record contractOtherDataRecord : contractOtherDataRecords) {
			String name = contractOtherDataRecord.get(CONTRACT_INFO.NAME);
			String value = contractOtherDataRecord.get(CONTRACT_INFO.EXPRESSION);
			contractOtherInfoMap.put(name, value);
		}
		
		// Variables contrato
		
		contractOtherDataRecords = dslContext.select().from(CONTRACT_INFO)
				.where(CONTRACT_INFO.NAME.in(contractOtherDataNames))
				.and(CONTRACT_INFO.CONTRACT.eq(contractId))
				.orderBy(CONTRACT_INFO.START_DATE.desc())
				.fetch();
		
		for(Record contractOtherDataRecord : contractOtherDataRecords) {
			String name = contractOtherDataRecord.get(CONTRACT_INFO.NAME);
			String value = contractOtherDataRecord.get(CONTRACT_INFO.EXPRESSION);
			contractOtherInfoMap.put(name, value);
		}
		
		return contractOtherInfoMap;
	}
	
	public static  Map<String, String> setContractOtherInfo(Connection conn, Integer domainId, Integer parentDomainId, Integer contractId, Map<String, String> contractOtherInfo) {
		return setContractOtherInfoDB(DSL.using(conn, getDefaultSettings()), domainId, parentDomainId, contractId, contractOtherInfo);
	}
	
	private static Map<String, String> setContractOtherInfoDB(DSLContext dslContext, Integer domainId, Integer parentDomainId, Integer contractId, Map<String, String> contractOtherInfo) {
		Record2<Date, Date> contractRecord = dslContext.select(CONTRACT.START_DATE, CONTRACT.END_DATE).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		
		List<String> contractOtherDataNames = getContractOtherDataListNames();
		
		dslContext.delete(CONTRACT_INFO)
			.where(CONTRACT_INFO.NAME.in(contractOtherDataNames))
			.and(CONTRACT_INFO.CONTRACT.eq(contractId))
			.execute();
		
		for(Entry<String, String> entry : contractOtherInfo.entrySet()) {
			if(	AonStringUtils.isNotBlank(entry.getValue())) {
				dslContext.insertInto(CONTRACT_INFO)
					.set(CONTRACT_INFO.DOMAIN, domainId)
					.set(CONTRACT_INFO.CONTRACT, contractId)
					.set(CONTRACT_INFO.NAME, entry.getKey())
					.set(CONTRACT_INFO.EXPRESSION, entry.getValue())
					.set(CONTRACT_INFO.START_DATE, startDate)
					.set(CONTRACT_INFO.END_DATE, endDate)
					.execute();
			}
		}
		
		return contractOtherInfo;
	}
	
	private static List<String> getContractOtherDataListNames(){
		List<String> contractOtherDataNames = new ArrayList<>();
		
		// ------------------------------------------------------- Indefinite Table
		
		contractOtherDataNames.add("I_ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("I_ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("I_ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("I_LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("I_LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("I_LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("I_FUNCTIONS");
		contractOtherDataNames.add("I_EMPLOYEE_CONTRACT_DISTANCE");
		contractOtherDataNames.add("I_EMPLOYEE_CONTRACT_DIST_ADDR");
		contractOtherDataNames.add("I_DISC_WORK_DESCRIPTION");
		contractOtherDataNames.add("I_DISC_WORK_ACTIVITY");
		contractOtherDataNames.add("I_DISC_WORK_DURATION");
		contractOtherDataNames.add("I_DISC_WORK_ESTIMATED_DURATION");
		contractOtherDataNames.add("I_DISC_WORK_ESTIM_JOURNAL_HOURS");
		contractOtherDataNames.add("I_DISC_WORK_ESTIM_JOURNAL_PERIOD");
		contractOtherDataNames.add("I_DISC_WORK_ESTIM_SCHEDULE");
		contractOtherDataNames.add("I_DISC_AGREEMENT_COLLECTIVE");
		contractOtherDataNames.add("I_FULL_TIME_WEEK_HOURS");
		contractOtherDataNames.add("I_FULL_TIME_START_TIME");
		contractOtherDataNames.add("I_FULL_TIME_END_TIME");
		contractOtherDataNames.add("I_PARTIALLY_TIME_HOURS");
		contractOtherDataNames.add("I_DEFAULT_JOURNAL_HOURS");
		contractOtherDataNames.add("I_COMPLEMENTARY_HOURS");
		contractOtherDataNames.add("I_TRIAL_DURATION");
		contractOtherDataNames.add("I_SALARY_AMOUNT");
		contractOtherDataNames.add("I_SALARY_PERIOD");
		contractOtherDataNames.add("I_SALARY_CONCEPT");
		contractOtherDataNames.add("I_HOLIDAYS");
		contractOtherDataNames.add("I_SEPE_MUNICIPALITY");
		contractOtherDataNames.add("I_OPT2_SEPE_MUNICIPALITY");
		contractOtherDataNames.add("I_OPT2_DISABILITY_NO_SEVERE");
		contractOtherDataNames.add("I_OPT2_DISABILITY_SEVERE");
		contractOtherDataNames.add("I_OPT2_REDUCTION");
		contractOtherDataNames.add("I_OPT5_BONUS_ART4_RDL3_2012");
		contractOtherDataNames.add("I_OPT5_UNEMPLOYED_BT_16_30");
		contractOtherDataNames.add("I_OPT5_UNEMPLOYED_GT_45");
		contractOtherDataNames.add("I_OPT5_UNEMPL_3_MONTH_BENEFIT");
		contractOtherDataNames.add("I_OPT5_FIRST_EMPLOYEE_AND_LT_30");
		contractOtherDataNames.add("I_OPT6_AGE");
		contractOtherDataNames.add("I_OPT6_AGREEMENT_COLLECTIVE1");
		contractOtherDataNames.add("I_OPT6_AGREEMENT_COLLECTIVE2");
		contractOtherDataNames.add("I_OPT15_ONSITE_HOURS");
		contractOtherDataNames.add("I_OPT15_ONSITE_WEEK_HOURS");
		contractOtherDataNames.add("I_OPT15_SALARY");
		contractOtherDataNames.add("I_OPT15_OVERNIGHT");
		contractOtherDataNames.add("I_OPT15_OVERNIGHT_WEEK_DAYS");
		contractOtherDataNames.add("I_OPT17_FULL_TIME_QUOTE_BONUS");
		contractOtherDataNames.add("I_OPT17_DISCONT_TIME_QUOTE_BONUS");
		contractOtherDataNames.add("I_OPT17_SRC_CONTRACT_SEPE_MUNIC");
		
		// ------------------------------------------------------- Temporal Table
		
		contractOtherDataNames.add("T_ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("T_ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("T_ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("T_LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("T_LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("T_LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("T_FUNCTIONS");
		contractOtherDataNames.add("T_EMPLOYEE_CONTRACT_DISTANCE");
		contractOtherDataNames.add("T_EMPLOYEE_CONTRACT_DIST_ADDR");
		contractOtherDataNames.add("T_FULL_TIME_WEEK_HOURS");
		contractOtherDataNames.add("T_FULL_TIME_START_TIME");
		contractOtherDataNames.add("T_FULL_TIME_END_TIME");
		contractOtherDataNames.add("T_PARTIALLY_TIME_JOB_LOWER_THAN");
		contractOtherDataNames.add("T_PARTIALLY_TIME_JOB_DISTRIB");
		contractOtherDataNames.add("T_END_DATE_TEXT");
		contractOtherDataNames.add("T_TRIAL_DURATION");
		contractOtherDataNames.add("T_GREATER_DURATION_AGREEMENT_COL");
		contractOtherDataNames.add("T_SALARY_AMOUNT");
		contractOtherDataNames.add("T_SALARY_PERIOD");
		contractOtherDataNames.add("T_SALARY_CONCEPT");
		contractOtherDataNames.add("T_HOLIDAYS");
		contractOtherDataNames.add("T_SEPE_MUNICIPALITY");
		contractOtherDataNames.add("T_OPT1_WORK_DESCRIPTION1");
		contractOtherDataNames.add("T_OPT1_WORK_DESCRIPTION2");
		contractOtherDataNames.add("T_OPT2_WORK_DESCRIPTION1");
		contractOtherDataNames.add("T_OPT2_WORK_DESCRIPTION2");
		contractOtherDataNames.add("T_OPT3_REPLACED_WORKER_NAME");
		contractOtherDataNames.add("T_OPT10_REQUIREMENTS_OPT");
		contractOtherDataNames.add("T_OPT10_FORMATION_OPT");
		contractOtherDataNames.add("T_OPT10_FORMATION_TYPE_OPT");
		contractOtherDataNames.add("T_OPT10_FORMATION_TYPE_OPT1_TEXT");
		contractOtherDataNames.add("T_OPT10_FORMATION_TYPE_OPT2_TEXT");
		contractOtherDataNames.add("T_OPT12_ONSITE_HOURS");
		contractOtherDataNames.add("T_OPT12_ONSITE_WEEK_HOURS");
		contractOtherDataNames.add("T_OPT12_ONSITE_HOURS_DISTRIB");
		contractOtherDataNames.add("T_OPT12_SALARY_OPT");
		contractOtherDataNames.add("T_OPT12_OVERNIGHT");
		contractOtherDataNames.add("T_OPT12_OVERNIGHT_WEEK_DAYS");
		contractOtherDataNames.add("T_OPT13_DISABILITY_ISSUED_BY");
		contractOtherDataNames.add("T_OPT13_DISABILITY");
		contractOtherDataNames.add("T_OPT13_SEVERE_DISABILITY");
		contractOtherDataNames.add("T_OPT14_TRIAL_PERIOD");
		contractOtherDataNames.add("T_OPT14_TRIAL_TERMS");
		contractOtherDataNames.add("T_OPT14_PROFESSION");
		contractOtherDataNames.add("T_OPT14_DISTANCE_ADJUSTMENT");
		contractOtherDataNames.add("T_OPT14_DISTANCE_ADJUSTMENT_MORE");
		contractOtherDataNames.add("T_OPT14_COLLECTIVE_AGREEMENT");
		
		// ------------------------------------------------------- Formation Table
		
		contractOtherDataNames.add("L_ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("L_ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("L_ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("L_LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("L_LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("L_LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("L_QUOTE_BONUS");
		contractOtherDataNames.add("L_EMPLOYEE_OPT");
		contractOtherDataNames.add("L_CONTRACT_WORKPLACE_ADDRESS");
		contractOtherDataNames.add("L_FORMATION_TEACHER");
		contractOtherDataNames.add("L_HORARIO_LABORAL");
		contractOtherDataNames.add("L_HORARIO_LECTIVO");
		contractOtherDataNames.add("L_TRIAL_DURATION");
		contractOtherDataNames.add("L_TRIAL_DURATION_INCREASE");
		contractOtherDataNames.add("L_SALARY_AMOUNT");
		contractOtherDataNames.add("L_SALARY_PERIOD");
		contractOtherDataNames.add("L_HOLIDAYS");
		contractOtherDataNames.add("L_ANNEX_I_CHECK");
		contractOtherDataNames.add("L_ANNEX_II_CHECK");
		
		// ------------------------------------------------------- Practice Table
		
		contractOtherDataNames.add("P_ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("P_ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("P_ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("P_LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("P_LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("P_LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("P_PROFESSIONAL_CERT");
		contractOtherDataNames.add("P_PROFESSIONAL_CERT_OBTAIN_DATE");
		contractOtherDataNames.add("P_DISABILITY_ISSUE_ENTITY");
		contractOtherDataNames.add("P_DISABILITY_ISSUE_ENTITY_MORE");
		contractOtherDataNames.add("P_FIRST_CONTRACT");
		contractOtherDataNames.add("P_FULL_TIME_WEEK_HOURS");
		contractOtherDataNames.add("P_FULL_TIME_START_TIME");
		contractOtherDataNames.add("P_FULL_TIME_END_TIME");
		contractOtherDataNames.add("P_JOB_TIME_DISTRIBUTION2");
		contractOtherDataNames.add("P_TRIAL_DURATION");
		contractOtherDataNames.add("P_SALARY_AMOUNT");
		contractOtherDataNames.add("P_SALARY_PERIOD");
		contractOtherDataNames.add("P_SALARY_CONCEPT");
		contractOtherDataNames.add("P_HOLIDAYS");
		contractOtherDataNames.add("P_SEPE_START_COMMUNICATION");
		contractOtherDataNames.add("P_SEPE_END_COMMUNICATION");
		contractOtherDataNames.add("P_OPT3_UNEMPLOYMENT");
		contractOtherDataNames.add("P_OPT4_TRIAL_DURATION");
		contractOtherDataNames.add("P_OPT4_TRIAL_DURATION_CONDITIONS");
		contractOtherDataNames.add("P_OPT4_WORK_PLACE_ADAPTATIONS");
		contractOtherDataNames.add("P_OPT4_STAFF_ADJUSTMENT");
		contractOtherDataNames.add("P_OPT4_STAFF_ADJUSTMENT_MORE");
		contractOtherDataNames.add("P_OPT5_MOTIVATION");
		contractOtherDataNames.add("P_OPT5_EMPLOYER");
		
		return contractOtherDataNames;
	}
	
	private static List<String> getContractOtherDataListNames(Integer contractType) {
		if(contractType >= 100 && contractType <= 400) {
			return getContractOtherDataIndefiniteListNames();
		} else if (contractType == 421) {
			return getContractOtherDataFormationListNames();
		} else if (contractType == 420 || contractType == 520) {
			return getContractOtherDataPracticeListNames();
		} else
			return getContractOtherDataTempListNames();
	}
	
	private static List<String> getContractOtherDataIndefiniteListNames(){
		List<String> contractOtherDataNames = new ArrayList<>();
		
		contractOtherDataNames.add("I_ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("I_ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("I_ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("I_LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("I_LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("I_LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("I_FUNCTIONS");
		contractOtherDataNames.add("I_EMPLOYEE_CONTRACT_DISTANCE");
		contractOtherDataNames.add("I_EMPLOYEE_CONTRACT_DIST_ADDR");
		contractOtherDataNames.add("I_DISC_WORK_DESCRIPTION");
		contractOtherDataNames.add("I_DISC_WORK_ACTIVITY");
		contractOtherDataNames.add("I_DISC_WORK_DURATION");
		contractOtherDataNames.add("I_DISC_WORK_ESTIMATED_DURATION");
		contractOtherDataNames.add("I_DISC_WORK_ESTIM_JOURNAL_HOURS");
		contractOtherDataNames.add("I_DISC_WORK_ESTIM_JOURNAL_PERIOD");
		contractOtherDataNames.add("I_DISC_WORK_ESTIM_SCHEDULE");
		contractOtherDataNames.add("I_DISC_AGREEMENT_COLLECTIVE");
		contractOtherDataNames.add("I_FULL_TIME_WEEK_HOURS");
		contractOtherDataNames.add("I_FULL_TIME_START_TIME");
		contractOtherDataNames.add("I_FULL_TIME_END_TIME");
		contractOtherDataNames.add("I_PARTIALLY_TIME_HOURS");
		contractOtherDataNames.add("I_DEFAULT_JOURNAL_HOURS");
		contractOtherDataNames.add("I_COMPLEMENTARY_HOURS");
		contractOtherDataNames.add("I_TRIAL_DURATION");
		contractOtherDataNames.add("I_SALARY_AMOUNT");
		contractOtherDataNames.add("I_SALARY_PERIOD");
		contractOtherDataNames.add("I_SALARY_CONCEPT");
		contractOtherDataNames.add("I_HOLIDAYS");
		contractOtherDataNames.add("I_SEPE_MUNICIPALITY");
		contractOtherDataNames.add("I_OPT2_SEPE_MUNICIPALITY");
		contractOtherDataNames.add("I_OPT2_DISABILITY_NO_SEVERE");
		contractOtherDataNames.add("I_OPT2_DISABILITY_SEVERE");
		contractOtherDataNames.add("I_OPT2_REDUCTION");
		contractOtherDataNames.add("I_OPT5_BONUS_ART4_RDL3_2012");
		contractOtherDataNames.add("I_OPT5_UNEMPLOYED_BT_16_30");
		contractOtherDataNames.add("I_OPT5_UNEMPLOYED_GT_45");
		contractOtherDataNames.add("I_OPT5_UNEMPL_3_MONTH_BENEFIT");
		contractOtherDataNames.add("I_OPT5_FIRST_EMPLOYEE_AND_LT_30");
		contractOtherDataNames.add("I_OPT6_AGE");
		contractOtherDataNames.add("I_OPT6_AGREEMENT_COLLECTIVE1");
		contractOtherDataNames.add("I_OPT6_AGREEMENT_COLLECTIVE2");
		contractOtherDataNames.add("I_OPT15_ONSITE_HOURS");
		contractOtherDataNames.add("I_OPT15_ONSITE_WEEK_HOURS");
		contractOtherDataNames.add("I_OPT15_SALARY");
		contractOtherDataNames.add("I_OPT15_OVERNIGHT");
		contractOtherDataNames.add("I_OPT15_OVERNIGHT_WEEK_DAYS");
		contractOtherDataNames.add("I_OPT17_FULL_TIME_QUOTE_BONUS");
		contractOtherDataNames.add("I_OPT17_DISCONT_TIME_QUOTE_BONUS");
		contractOtherDataNames.add("I_OPT17_SRC_CONTRACT_SEPE_MUNIC");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE");
		
		return contractOtherDataNames;
	}
	
	private static List<String> getContractOtherDataTempListNames(){
		List<String> contractOtherDataNames = new ArrayList<>();
		
		contractOtherDataNames.add("T_ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("T_ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("T_ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("T_LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("T_LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("T_LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("T_FUNCTIONS");
		contractOtherDataNames.add("T_EMPLOYEE_CONTRACT_DISTANCE");
		contractOtherDataNames.add("T_EMPLOYEE_CONTRACT_DIST_ADDR");
		contractOtherDataNames.add("T_FULL_TIME_WEEK_HOURS");
		contractOtherDataNames.add("T_FULL_TIME_START_TIME");
		contractOtherDataNames.add("T_FULL_TIME_END_TIME");
		contractOtherDataNames.add("T_PARTIALLY_TIME_JOB_LOWER_THAN");
		contractOtherDataNames.add("T_PARTIALLY_TIME_JOB_DISTRIB");
		contractOtherDataNames.add("T_END_DATE_TEXT");
		contractOtherDataNames.add("T_TRIAL_DURATION");
		contractOtherDataNames.add("T_GREATER_DURATION_AGREEMENT_COL");
		contractOtherDataNames.add("T_SALARY_AMOUNT");
		contractOtherDataNames.add("T_SALARY_PERIOD");
		contractOtherDataNames.add("T_SALARY_CONCEPT");
		contractOtherDataNames.add("T_HOLIDAYS");
		contractOtherDataNames.add("T_SEPE_MUNICIPALITY");
		contractOtherDataNames.add("T_OPT1_WORK_DESCRIPTION1");
		contractOtherDataNames.add("T_OPT1_WORK_DESCRIPTION2");
		contractOtherDataNames.add("T_OPT2_WORK_DESCRIPTION1");
		contractOtherDataNames.add("T_OPT2_WORK_DESCRIPTION2");
		contractOtherDataNames.add("T_OPT3_REPLACED_WORKER_NAME");
		contractOtherDataNames.add("T_OPT10_REQUIREMENTS_OPT");
		contractOtherDataNames.add("T_OPT10_FORMATION_OPT");
		contractOtherDataNames.add("T_OPT10_FORMATION_TYPE_OPT");
		contractOtherDataNames.add("T_OPT10_FORMATION_TYPE_OPT1_TEXT");
		contractOtherDataNames.add("T_OPT10_FORMATION_TYPE_OPT2_TEXT");
		contractOtherDataNames.add("T_OPT12_ONSITE_HOURS");
		contractOtherDataNames.add("T_OPT12_ONSITE_WEEK_HOURS");
		contractOtherDataNames.add("T_OPT12_ONSITE_HOURS_DISTRIB");
		contractOtherDataNames.add("T_OPT12_SALARY_OPT");
		contractOtherDataNames.add("T_OPT12_OVERNIGHT");
		contractOtherDataNames.add("T_OPT12_OVERNIGHT_WEEK_DAYS");
		contractOtherDataNames.add("T_OPT13_DISABILITY_ISSUED_BY");
		contractOtherDataNames.add("T_OPT13_DISABILITY");
		contractOtherDataNames.add("T_OPT13_SEVERE_DISABILITY");
		contractOtherDataNames.add("T_OPT14_TRIAL_PERIOD");
		contractOtherDataNames.add("T_OPT14_TRIAL_TERMS");
		contractOtherDataNames.add("T_OPT14_PROFESSION");
		contractOtherDataNames.add("T_OPT14_DISTANCE_ADJUSTMENT");
		contractOtherDataNames.add("T_OPT14_DISTANCE_ADJUSTMENT_MORE");
		contractOtherDataNames.add("T_OPT14_COLLECTIVE_AGREEMENT");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE");
		
		return contractOtherDataNames;
	}
	
	private static List<String> getContractOtherDataFormationListNames(){
		List<String> contractOtherDataNames = new ArrayList<>();
		
		contractOtherDataNames.add("L_ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("L_ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("L_ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("L_LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("L_LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("L_LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("L_QUOTE_BONUS");
		contractOtherDataNames.add("L_EMPLOYEE_OPT");
		contractOtherDataNames.add("L_CONTRACT_WORKPLACE_ADDRESS");
		contractOtherDataNames.add("L_FORMATION_TEACHER");
		contractOtherDataNames.add("L_HORARIO_LABORAL");
		contractOtherDataNames.add("L_HORARIO_LECTIVO");
		contractOtherDataNames.add("L_TRIAL_DURATION");
		contractOtherDataNames.add("L_TRIAL_DURATION_INCREASE");
		contractOtherDataNames.add("L_SALARY_AMOUNT");
		contractOtherDataNames.add("L_SALARY_PERIOD");
		contractOtherDataNames.add("L_HOLIDAYS");
		contractOtherDataNames.add("L_ANNEX_I_CHECK");
		contractOtherDataNames.add("L_ANNEX_II_CHECK");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE");
		
		return contractOtherDataNames;
	}
	
	private static List<String> getContractOtherDataPracticeListNames(){
		List<String> contractOtherDataNames = new ArrayList<>();
		
		contractOtherDataNames.add("P_ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("P_ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("P_ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("P_LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("P_LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("P_LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("P_PROFESSIONAL_CERT");
		contractOtherDataNames.add("P_PROFESSIONAL_CERT_OBTAIN_DATE");
		contractOtherDataNames.add("P_DISABILITY_ISSUE_ENTITY");
		contractOtherDataNames.add("P_DISABILITY_ISSUE_ENTITY_MORE");
		contractOtherDataNames.add("P_FIRST_CONTRACT");
		contractOtherDataNames.add("P_FULL_TIME_WEEK_HOURS");
		contractOtherDataNames.add("P_FULL_TIME_START_TIME");
		contractOtherDataNames.add("P_FULL_TIME_END_TIME");
		contractOtherDataNames.add("P_JOB_TIME_DISTRIBUTION2");
		contractOtherDataNames.add("P_TRIAL_DURATION");
		contractOtherDataNames.add("P_SALARY_AMOUNT");
		contractOtherDataNames.add("P_SALARY_PERIOD");
		contractOtherDataNames.add("P_SALARY_CONCEPT");
		contractOtherDataNames.add("P_HOLIDAYS");
		contractOtherDataNames.add("P_SEPE_START_COMMUNICATION");
		contractOtherDataNames.add("P_SEPE_END_COMMUNICATION");
		contractOtherDataNames.add("P_OPT3_UNEMPLOYMENT");
		contractOtherDataNames.add("P_OPT4_TRIAL_DURATION");
		contractOtherDataNames.add("P_OPT4_TRIAL_DURATION_CONDITIONS");
		contractOtherDataNames.add("P_OPT4_WORK_PLACE_ADAPTATIONS");
		contractOtherDataNames.add("P_OPT4_STAFF_ADJUSTMENT");
		contractOtherDataNames.add("P_OPT4_STAFF_ADJUSTMENT_MORE");
		contractOtherDataNames.add("P_OPT5_MOTIVATION");
		contractOtherDataNames.add("P_OPT5_EMPLOYER");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE");
		
		return contractOtherDataNames;
	}
	
}
