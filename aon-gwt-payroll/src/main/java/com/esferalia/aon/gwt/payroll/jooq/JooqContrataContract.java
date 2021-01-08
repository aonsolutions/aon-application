package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Cno.CNO;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractClause.CONTRACT_CLAUSE;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record10;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.ContractAttach;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractSpecificData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.FormativeLevel;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.payroll.contract.ContractFill;
import com.esferalia.aon.payroll.sepe.contrata.Contrata;
import com.esferalia.aon.sepe.api.contract.model.IContratoType;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATOS;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.ibm.icu.util.Calendar;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class JooqContrataContract {

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static void saveDraftContract(String domainName, Integer contractId, byte[] pdfBytes) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			Integer domainId = AonServletUtils.getDomainID(domainName);
			
			dslContext.insertInto(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DOMAIN, domainId)
				.set(CONTRACT_ATTACH.CONTRACT, contractId)
				.set(CONTRACT_ATTACH.MIMETYPE, (byte)22)
				.set(CONTRACT_ATTACH.DESCRIPTION, "BORRADOR CONTRATO")
				.set(CONTRACT_ATTACH.DATA, pdfBytes)
				.set(CONTRACT_ATTACH.TYPE, (byte)0)
				.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
				.execute();
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}
	
	public static byte[] contractFill(Connection connection, Integer domainId, Integer contractId, Integer contractType, String formativeLevelCode) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
				
			Map<String, String> contractOtherInfo = getContractOtherInfoDB(dslContext, domainId, contractId, contractType+"");
			Map<String, String> contractFillInfo = getContractFillInfoDB(dslContext, domainId, contractId);
			
			FormativeLevel formativeLevel = new FormativeLevel();
			contractFillInfo.put("E_FORMATIVE_LVL", AonStringUtils.abbreviate(formativeLevel.getFormativeLevelDescription(formativeLevelCode), 32));
			contractFillInfo.put("E_FORMATIVE_LVL_CODE", formativeLevelCode);
			
			byte[] data = ContractFill.fillContract(contractType, contractOtherInfo, contractFillInfo);
			return data;
	}

	public static byte[] contractFill(String domainName, Integer contractId, String contractTypeStr, String formativeLevelCode) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer contractType = Integer.parseInt(contractTypeStr);
			
			Map<String, String> contractOtherInfo = getContractOtherInfoDB(dslContext, domainId, contractId, contractTypeStr);
			Map<String, String> contractFillInfo = getContractFillInfoDB(dslContext, domainId, contractId);
			
			FormativeLevel formativeLevel = new FormativeLevel();
			contractFillInfo.put("E_FORMATIVE_LVL", AonStringUtils.abbreviate(formativeLevel.getFormativeLevelDescription(formativeLevelCode), 32));
			contractFillInfo.put("E_FORMATIVE_LVL_CODE", formativeLevelCode);
			
			return ContractFill.fillContract(contractType, contractOtherInfo, contractFillInfo);
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}
	
	private static Map<String, String> getContractFillInfoDB(DSLContext dslContext, Integer domainId, Integer contractId) {
		Map<String, String> contractFillData = new HashMap<String, String>();
		
		Municipalities municipalities = new Municipalities();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		// ENTRPRISE
		
		Result<Record> entepriseRecords = dslContext.select().from(ENTERPRISE)
				.leftJoin(RADDRESS)
				.on(ENTERPRISE.REGISTRY.eq(RADDRESS.REGISTRY))
				.leftJoin(REGISTRY)
				.on(ENTERPRISE.REGISTRY.eq(REGISTRY.ID))
				.where(ENTERPRISE.REGISTRY.eq(
						dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY).where(ENTERPRISE_ACTIVITY.ID.eq(
								dslContext.select(CONTRACT.ENTERPRISE_ACTIVITY).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne(CONTRACT.ENTERPRISE_ACTIVITY)
						)).fetchOne(ENTERPRISE_ACTIVITY.ENTERPRISE)
				)).fetch();
		
		if(!entepriseRecords.isEmpty()) {
			Record enterpriseRecord = entepriseRecords.get(0);
			
			contractFillData.put("ET_NIF", enterpriseRecord.get(REGISTRY.DOCUMENT));
			contractFillData.put("ENTERPRISE_NAME", enterpriseRecord.get(REGISTRY.NAME));
			String address = enterpriseRecord.get(RADDRESS.STREET_TYPE) + " " + enterpriseRecord.get(RADDRESS.ADDRESS) + ", " + enterpriseRecord.get(RADDRESS.NUMBER);
			contractFillData.put("ENTERPRISE_ADDR", address);
			
			String municipalityCode = enterpriseRecord.get(RADDRESS.MUNICIPALITY_CODE);
			if(null != municipalityCode) {
				String municipality = municipalities.getMunicipalityByZip(municipalityCode);
				
				contractFillData.put("ENTERPRISE_MUNICIPALITY", municipality);
				contractFillData.put("ENTERPRISE_MUNICIPALITY_CODE", municipalityCode);
			}
			
			
			contractFillData.put("ENTERPRISE_COUNTRY", "ESPA" + String.valueOf("\u00D1") + "a");
			contractFillData.put("ENTERPRISE_COUNTRY_CODE", "724");
			
			contractFillData.put("ENTERPRISE_ZIP", enterpriseRecord.get(RADDRESS.ZIP));
		}
		
		// ENTRPRISE CCC AND ACTIVITY
		
		Result<Record> entepriseCCCRecords = dslContext.select().from(CONTRACT)
				.leftJoin(ENTERPRISE_CCC)
				.on(CONTRACT.ENTERPRISE_CCC.eq(ENTERPRISE_CCC.ID))
				.leftJoin(ENTERPRISE_ACTIVITY)
				.on(CONTRACT.ENTERPRISE_ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
				.where(CONTRACT.ID.eq(contractId))
				.fetch();
		
		if(!entepriseCCCRecords.isEmpty()) {
			Record entepriseCCCRecord = entepriseCCCRecords.get(0);
		
			contractFillData.put("ENTERPRISE_CCC_REG", getCCCRegimeCode(entepriseCCCRecord.get(ENTERPRISE_CCC.TYPE)));
			
			String cccAcount = entepriseCCCRecord.get(ENTERPRISE_CCC.CCC);
			contractFillData.put("ENTERPRISE_CCC_PRV", cccAcount.substring(0, 2));
			contractFillData.put("ENTERPRISE_CCC_NUM", cccAcount.substring(2, 9));
			contractFillData.put("ENTERPRISE_CCC_DC", cccAcount.substring(9, 11));
			
			contractFillData.put("ENTERPRISE_ACTIVITY", entepriseCCCRecord.get(ENTERPRISE_ACTIVITY.DESCRIPTION));
			contractFillData.put("ENTERPRISE_ACTIVITY_CODE", "");
		}
		
		// Workplace
		
		Record raddressRecord = dslContext.select().from(RADDRESS).where(RADDRESS.ID.in(
					dslContext.select(WORKPLACE.ADDRESS).from(WORKPLACE).where(WORKPLACE.ID.in(
								dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT).where(CONTRACT.ID.eq(contractId))
									.fetchOne(CONTRACT.WORKPLACE)
							)).fetchOne(WORKPLACE.ADDRESS)
				)).fetchOne();
		
		if(null != raddressRecord) {
			String municipalityCode = raddressRecord.get(RADDRESS.MUNICIPALITY_CODE);
			if(null != municipalityCode) {
				String municipality = municipalities.getMunicipalityByZip(municipalityCode);
				
				contractFillData.put("WORKPLC_MUNICIPALITY", municipality);
				contractFillData.put("WORKPLC_MUNICIPALITY_CODE", municipalityCode);
			}
		}
		
		contractFillData.put("WORKPLC_COUNTRY", "ESPA" + String.valueOf("\u00D1") + "a");
		contractFillData.put("WORKPLC_COUNTRY_CODE", "724");
		
		// Employee
		
		Result<Record> contractRecords = dslContext.select().from(CONTRACT)
			.leftJoin(PERSON)
			.on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
			.leftJoin(REGISTRY)
			.on(CONTRACT.PERSON.eq(REGISTRY.ID))
			.leftJoin(RADDRESS)
			.on(CONTRACT.PERSON.eq(RADDRESS.REGISTRY))
			.where(CONTRACT.ID.eq(contractId))
			.fetch();
		
		if(!contractRecords.isEmpty()) {
			Record contractRecord = contractRecords.get(0);
			contractFillData.put("E_NAME", contractRecord.get(PERSON.NAME));
			contractFillData.put("E_SURNAME", contractRecord.get(PERSON.FIRST_SURNAME));
			contractFillData.put("E_SURNAME2", contractRecord.get(PERSON.SECOND_SURNAME));
			contractFillData.put("E_NIF", contractRecord.get(REGISTRY.DOCUMENT));
			
			Date birthDate = contractRecord.get(PERSON.BIRTH_DATE);
			if(null != birthDate) {
				contractFillData.put("E_BDAT", simpleDateFormat.format(birthDate));
			}
			
			String ssNum = contractRecord.get(PERSON.SOCIAL_SECURITY_NUM);
			contractFillData.put("E_SS1", ssNum.substring(0, 2));
			contractFillData.put("E_SS2", ssNum.substring(2, 10));
			contractFillData.put("E_SS3", ssNum.substring(10, 12));
			
			String nationality = contractRecord.get(REGISTRY.NATIONALITY);
			contractFillData.put("E_NATIONALITY", Country.valueOf(nationality).getName());
			contractFillData.put("E_NATIONALITY_CODE", Country.valueOf(nationality).getIsoCode()+"");
			
			String municipalityCode = contractRecord.get(RADDRESS.MUNICIPALITY_CODE);
			if(null != municipalityCode) {
				String municipality = municipalities.getMunicipalityByZip(municipalityCode);
				
				contractFillData.put("E_MUNICIPALITY_ADDR", municipality);
				contractFillData.put("E_MUNICIPALITY_ADDR_CODE", municipalityCode);
			}
			
			contractFillData.put("E_COUNTRY_ADDR", "ESPA" + String.valueOf("\u00D1") + "a");
			contractFillData.put("E_COUNTRY_ADDR_CODE", "724");
			
		}
		
		return contractFillData;
	}

	// ------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------ CONTRACT BONUS -------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------
	
	public static List<SSBonusData> getContractBonus(Connection conn, Integer domainId, Integer contractId) {
		return getContractBonusDB(DSL.using(conn, getDefaultSettings()), domainId, contractId);
	}

	private static List<SSBonusData> getContractBonusDB(DSLContext dslContext, Integer domainId, Integer contractId) {
		List<SSBonusData> contractBonusList = new ArrayList<SSBonusData>();
		
		Result<Record> contractBonusRecords = dslContext.select().from(CONTRACT_BONUS)
				.where(CONTRACT_BONUS.CONTRACT.eq(contractId))
				.and(CONTRACT_BONUS.EXPRESSION.isNotNull().and(CONTRACT_BONUS.EXPRESSION.ne("")))
				.orderBy(CONTRACT_BONUS.START_DATE.desc())
				.fetch();
		
		for(Record r : contractBonusRecords) {
			SSBonusData ssBonusData = new SSBonusData();
			ssBonusData.setStartDate(r.get(CONTRACT_BONUS.START_DATE));
			ssBonusData.setEndDate(r.get(CONTRACT_BONUS.END_DATE));
			ssBonusData.setFormula(r.get(CONTRACT_BONUS.EXPRESSION));
			ssBonusData.setDescription(r.get(CONTRACT_BONUS.DESCRIPTION));
			
			contractBonusList.add(ssBonusData);
		}
		
		return contractBonusList;
	}

	public static void setContractBonus(Connection conn, Integer domainId, EmployeeContractInfo employeeContractData) {
		setContractBonusDB(DSL.using(conn, getDefaultSettings()), domainId, employeeContractData);
	}
	
	private static void setContractBonusDB(DSLContext dslContext, Integer domainId, EmployeeContractInfo employeeContractData) {
		// TODO Auto-generated method stub
	}

	// ------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------ CONTRACT ATTACH -------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------
	
	public static List<ContractAttach> getContractAttachments(Connection conn, Integer domainId, Integer contractId) {
		return getContractAttachmentsDB(DSL.using(conn, getDefaultSettings()), domainId, contractId);
	}
	
	private static List<ContractAttach> getContractAttachmentsDB(DSLContext dslContext, Integer domainId, Integer contractId) {
		List<ContractAttach> contractAttachs = new ArrayList<ContractAttach>();
		
		 Result<Record10<Integer, Integer, Integer, Byte, String, Byte, Integer, Byte, Timestamp, String>> contractAttachRecords = dslContext.select(
				CONTRACT_ATTACH.ID, CONTRACT_ATTACH.DOMAIN, CONTRACT_ATTACH.CONTRACT, CONTRACT_ATTACH.MIMETYPE,
				CONTRACT_ATTACH.DESCRIPTION, CONTRACT_ATTACH.TYPE, CONTRACT_ATTACH.SCOPE, CONTRACT_ATTACH.SECURITY_LEVEL,
				CONTRACT_ATTACH.ATTACH_DATE, CONTRACT_ATTACH.DRIVEID).from(CONTRACT_ATTACH)
			.where(CONTRACT_ATTACH.CONTRACT.eq(contractId)
					.or(CONTRACT_ATTACH.CONTRACT.isNull()))
			.and(CONTRACT_ATTACH.DOMAIN.eq(domainId))
			.fetch();
		
		for(Record record : contractAttachRecords) {
			if(null != record.get(CONTRACT_ATTACH.TYPE) && record.get(CONTRACT_ATTACH.TYPE) == (byte)4)
				continue;
			
			ContractAttach contractAttach = new ContractAttach();
			contractAttach.setId(record.get(CONTRACT_ATTACH.ID));
			contractAttach.setDomain(record.get(CONTRACT_ATTACH.DOMAIN));
			contractAttach.setContract(record.get(CONTRACT_ATTACH.CONTRACT));
			contractAttach.setMimeType(record.get(CONTRACT_ATTACH.MIMETYPE));
			contractAttach.setDescription(record.get(CONTRACT_ATTACH.DESCRIPTION));
			contractAttach.setData("".getBytes()); //record.get(CONTRACT_ATTACH.DATA));
			contractAttach.setType(record.get(CONTRACT_ATTACH.TYPE));
			contractAttach.setScope(record.get(CONTRACT_ATTACH.SCOPE));
			contractAttach.setSecurityLevel(record.get(CONTRACT_ATTACH.SECURITY_LEVEL));
			contractAttach.setAttachDate(record.get(CONTRACT_ATTACH.ATTACH_DATE));
			contractAttach.setDriveId(record.get(CONTRACT_ATTACH.DRIVEID));
			
			contractAttachs.add(contractAttach);
		}
		
		return contractAttachs;
	}

	public static List<ContractAttach> createContractAttach(Connection conn, Integer domainId,ContractAttach contractAttach) {
		return createContractAttachDB(DSL.using(conn, getDefaultSettings()), domainId, contractAttach);
	}

	private static List<ContractAttach> createContractAttachDB(DSLContext dslContext, Integer domainId, ContractAttach contractAttach) {
		 dslContext.insertInto(CONTRACT_ATTACH)
			.set(CONTRACT_ATTACH.DOMAIN, contractAttach.getDomain())
			.set(CONTRACT_ATTACH.CONTRACT, contractAttach.getContract())
			.execute();
		 
		 return getContractAttachmentsDB(dslContext, domainId, contractAttach.getContract());
	}
	
	public static List<ContractAttach> deleteContractAttach(Connection conn, Integer domainId, ContractAttach contractAttach) {
		return deleteContractAttachDB(DSL.using(conn, getDefaultSettings()), domainId, contractAttach);
	}
	
	private static List<ContractAttach> deleteContractAttachDB(DSLContext dslContext, Integer domainId, ContractAttach contractAttach) {
		
		// Delete contract_attach
		dslContext.delete(CONTRACT_ATTACH).where(CONTRACT_ATTACH.ID.eq(contractAttach.getId())).execute();
		
		return getContractAttachmentsDB(dslContext, domainId, contractAttach.getContract());
	}
	
	public static List<ContractAttach> setContractAttachments(Connection conn, Integer domainId, Integer contractId, List<ContractAttach> contractAttachments) {
		return setContractAttachmentsDB(DSL.using(conn, getDefaultSettings()), domainId, contractId, contractAttachments);
	}
	
	private static List<ContractAttach> setContractAttachmentsDB(DSLContext dslContext, Integer domainId, Integer contractId, List<ContractAttach> contractAttachments) {
		
		dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
		
		for(ContractAttach contractAttach : contractAttachments) {
			if(null == contractAttach.getContract())
				continue;
			
			dslContext.update(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DESCRIPTION, contractAttach.getDescription())
				.set(CONTRACT_ATTACH.TYPE, contractAttach.getType() == (byte) -1 ? null : contractAttach.getType())
				.set(CONTRACT_ATTACH.SCOPE, contractAttach.getScope() == (byte) -1 ? null : contractAttach.getScope())
				.set(CONTRACT_ATTACH.SECURITY_LEVEL, contractAttach.getSecurityLevel())
				.set(CONTRACT_ATTACH.ATTACH_DATE, contractAttach.getAttachDate() == null ? null : new Timestamp(contractAttach.getAttachDate().getTime()))
				.where(CONTRACT_ATTACH.ID.eq(contractAttach.getId()))
				.execute();
			
		}
		
		dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		
		return contractAttachments;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------ CONTRACT CLAUSE -------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------

	public static List<ContractClause> getContractClauses(Connection conn, Integer domainId, Integer contractId) {
		return getContractClausesDB(DSL.using(conn, getDefaultSettings()), domainId, contractId);
	}
	
	private static List<ContractClause> getContractClausesDB(DSLContext dslContext, Integer domainId, Integer contractId) {
		List<ContractClause> contractClauses = new ArrayList<ContractClause>();
		
		Result<Record> contractClauseRecords = dslContext.select().from(CONTRACT_CLAUSE)
			.where(CONTRACT_CLAUSE.CONTRACT.eq(contractId)
					.or(CONTRACT_CLAUSE.CONTRACT.isNull()))
			.and(CONTRACT_CLAUSE.DOMAIN.eq(domainId))
			.fetch();
		
		for(Record record : contractClauseRecords) {
			if(record.get(CONTRACT_CLAUSE.GENERAL) == (byte) 1)
				continue;
			
			ContractClause contractClause =  new ContractClause();
			contractClause.setId(record.get(CONTRACT_CLAUSE.ID));
			contractClause.setDomain(record.get(CONTRACT_CLAUSE.DOMAIN));
			contractClause.setContract(record.get(CONTRACT_CLAUSE.CONTRACT));
			contractClause.setLineNumber(record.get(CONTRACT_CLAUSE.LINE));
			contractClause.setName(record.get(CONTRACT_CLAUSE.NAME));
			contractClause.setDescription(record.get(CONTRACT_CLAUSE.DESCRIPTION));
			contractClause.setGeneral(record.get(CONTRACT_CLAUSE.GENERAL));
			
			contractClauses.add(contractClause);
		}
		
		return contractClauses;
	}

	public static List<ContractClause> createContractClause(Connection conn, Integer domainId, ContractClause contractClause) {
		return createContractClauseDB(DSL.using(conn, getDefaultSettings()), domainId, contractClause);
	}
	
	private static List<ContractClause> createContractClauseDB(DSLContext dslContext, Integer domainId, ContractClause contractClause) {
		 dslContext.insertInto(CONTRACT_CLAUSE)
			.set(CONTRACT_CLAUSE.DOMAIN, contractClause.getDomain())
			.set(CONTRACT_CLAUSE.CONTRACT, contractClause.getContract())
			.set(CONTRACT_CLAUSE.LINE, contractClause.getLineNumber())
			.set(CONTRACT_CLAUSE.DESCRIPTION, contractClause.getDescription())
			.execute();
		 
		return getContractClausesDB(dslContext, domainId, contractClause.getContract());
	}

	public static List<ContractClause> deleteContractClause(Connection conn, Integer domainId, ContractClause contractClause) {
		return deleteContractClauseDB(DSL.using(conn, getDefaultSettings()), domainId, contractClause);
	}
	
	private static List<ContractClause> deleteContractClauseDB(DSLContext dslContext, Integer domainId, ContractClause contractClause) {
		// Delete contract_attach
		dslContext.delete(CONTRACT_CLAUSE).where(CONTRACT_CLAUSE.ID.eq(contractClause.getId())).execute();
		
		return getContractClausesDB(dslContext, domainId, contractClause.getContract());
	}
	
	public static List<ContractClause> setContractClauses(Connection conn, Integer domainId, Integer contractId, List<ContractClause> contractClauses) {
		return setContractClausesDB(DSL.using(conn, getDefaultSettings()), domainId, contractId, contractClauses);
	}
	
	private static List<ContractClause> setContractClausesDB(DSLContext dslContext, Integer domainId, Integer contractId, List<ContractClause> contractClauses) {
		
		for(ContractClause contractClause : contractClauses) {
			if(null == contractClause.getContract())
				continue;
			
			dslContext.update(CONTRACT_CLAUSE)
				.set(CONTRACT_CLAUSE.LINE, contractClause.getLineNumber())
				.set(CONTRACT_CLAUSE.NAME, contractClause.getName())
				.set(CONTRACT_CLAUSE.DESCRIPTION, contractClause.getDescription())
				.where(CONTRACT_CLAUSE.ID.eq(contractClause.getId()))
				.execute();
			
		}

		return contractClauses;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------- CONTRACT OTHER INFO -----------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------

	public static  Map<String, String> getContractOtherInfo(Connection conn, Integer domainId, Integer contractId, String contractType) {
		return getContractOtherInfoDB(DSL.using(conn, getDefaultSettings()), domainId, contractId, contractType);
	}
	
	private static Map<String, String> getContractOtherInfoDB(DSLContext dslContext, Integer domainId, Integer contractId, String contractType) {
		Map<String, String> contractOtherInfoMap = new HashMap<String, String>();
		List<String> contractOtherDataNames = getContractOtherDataListNames(contractType);
		
		// Variables globales
		
		Result<Record> contractOtherDataRecords = dslContext.select().from(CONTRACT_INFO)
				.where(CONTRACT_INFO.NAME.in(contractOtherDataNames))
				.and(CONTRACT_INFO.CONTRACT.isNull())
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
	
	public static  Map<String, String> setContractOtherInfo(Connection conn, Integer domainId, Integer contractId, String contractType, Map<String, String> contractOtherInfo) {
		return setContractOtherInfoDB(DSL.using(conn, getDefaultSettings()), domainId, contractId, contractType, contractOtherInfo);
	}
	
	private static Map<String, String> setContractOtherInfoDB(DSLContext dslContext, Integer domainId, Integer contractId, String contractType, Map<String, String> contractOtherInfo) {
		Record2<Date, Date> contractRecord = dslContext.select(CONTRACT.START_DATE, CONTRACT.END_DATE).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		
		List<String> contractOtherDataNames = getContractOtherDataListNames();
		
		dslContext.delete(CONTRACT_INFO)
			.where(CONTRACT_INFO.NAME.in(contractOtherDataNames))
			.and(CONTRACT_INFO.CONTRACT.eq(contractId))
			.execute();
		
		for(Entry<String, String> entry : contractOtherInfo.entrySet()) {
			if(AonStringUtils.isNotBlank(entry.getValue())) {
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
	
	// ------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------- CONTRACT SPECIFIC DATA -----------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------

	public static ContractSpecificData getContractSpecificData(Connection conn, Integer domainId, Integer contractId) {
		return getContractSpecificDataDB(DSL.using(conn, getDefaultSettings()), domainId, contractId);
	}
	
	private static ContractSpecificData getContractSpecificDataDB(DSLContext dslContext, Integer domainId, Integer contractId) {
		ContractSpecificData contractSpecificData = new ContractSpecificData();
		
		Result<Record> contractAttachRecords = dslContext.select().from(CONTRACT_ATTACH)
			.where(CONTRACT_ATTACH.CONTRACT.eq(contractId))
			.and(CONTRACT_ATTACH.TYPE.eq((byte)4))
			.fetch();
//			.fetchOne();
		
		if(null == contractAttachRecords || contractAttachRecords.isEmpty())
			return contractSpecificData;	

		Record contractAttachRecord = contractAttachRecords.get(0);
		
		contractSpecificData.setId(contractAttachRecord.get(CONTRACT_ATTACH.ID));

		Contrata contrata = new Contrata();
		CONTRATOS contratos = contrata.getCONTRATOS(contractAttachRecord.get(CONTRACT_ATTACH.DATA));
		if(null == contratos)
			return contractSpecificData;
		
		try {
			Object obj = contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().get(0);
			JooqContrata.completeContratosParams(obj, contractSpecificData);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		contractSpecificData.setId(contractAttachRecord.get(CONTRACT_ATTACH.ID));
		
		return contractSpecificData;
	}
	
	public static void setContractSpecificData(Connection conn, EmployeeContractInfo employeeContractInfo) {
		setContractSpecificDataDB(DSL.using(conn, getDefaultSettings()), employeeContractInfo);
	}
	
	private static void setContractSpecificDataDB(DSLContext dslContext, EmployeeContractInfo employeeContractInfo) {
		ContractSpecificData contractSpecificData = employeeContractInfo.getContractSpecificData();
		Integer contractId = employeeContractInfo.getContractInfo().getContractId();
		Integer domainId = employeeContractInfo.getEmployeeInfo().getDomain();
		Date startDate = new Date(employeeContractInfo.getContractInfo().getStartDate().getTime());
		Date endDate = null == employeeContractInfo.getContractInfo().getEndDate() ? null : new Date(employeeContractInfo.getContractInfo().getEndDate().getTime());
		
		String cno = contractSpecificData.getCno();
		if(!AonStringUtils.isBlank(cno)) {
			dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.NAME.eq("CNO")).and(CONTRACT_DATA.CONTRACT.eq(contractId)).execute();
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.NAME, "CNO")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, "\"" + cno + "\"")
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
		}
		
		IContratoType contrato = (IContratoType) JooqContrata.createCONTRATOS(employeeContractInfo);
		CONTRATOS contratos = new CONTRATOS();
		contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().add(contrato);
		
		// ByteArrayOutputStream
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			Utils.marshal(contratos, System.out);
			Utils.marshal(contratos, out);
			
			Integer attachId = employeeContractInfo.getContractSpecificData().getId();
			if(null == attachId) {
				dslContext.insertInto(CONTRACT_ATTACH)
					.set(CONTRACT_ATTACH.DOMAIN, employeeContractInfo.getEmployeeInfo().getDomain())
					.set(CONTRACT_ATTACH.CONTRACT, employeeContractInfo.getContractInfo().getContractId())
					.set(CONTRACT_ATTACH.MIMETYPE, (byte)5)
					.set(CONTRACT_ATTACH.DESCRIPTION, "CONTRACT - Contrat@")
					.set(CONTRACT_ATTACH.DATA, out.toByteArray())
					.set(CONTRACT_ATTACH.TYPE, (byte)4)
					.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
					.execute();
			} else {
				dslContext.update(CONTRACT_ATTACH)
					.set(CONTRACT_ATTACH.DATA, out.toByteArray())
					.where(CONTRACT_ATTACH.ID.eq(attachId))
					.execute();
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// ------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------- CONTRACT -----------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------
	
	public static List<EmployeeContractInfo> getEmployeesInfo(Connection conn, Integer domainId, Boolean allEmployees) {
		return getEmployeesInfoDB(DSL.using(conn, getDefaultSettings()), domainId, allEmployees);
	}

	private static List<EmployeeContractInfo> getEmployeesInfoDB(DSLContext dslContext, Integer domainId, Boolean allEmployees) {
		List<EmployeeContractInfo> employeesInfo = new ArrayList<EmployeeContractInfo>();
		
		List<Integer> allContractIds = null;
		
		if(allEmployees) {
			// ------------------------------------------------ Get all contracts from domainId
			allContractIds = dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.DOMAIN.eq(domainId))
					.and(CONTRACT.ID.gt(0))
					.fetch(CONTRACT.ID);
		} else {
			// ------------------------------------------------ Get active contracts from domainId or ends in the last two months
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.MONTH, -1);
			
			Date contract_endDate = new Date(cal.getTimeInMillis());
			
			allContractIds = dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.DOMAIN.eq(domainId))
					.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(contract_endDate)))
					.and(CONTRACT.ID.gt(0))
					.fetch(CONTRACT.ID);
		}
		
		for(Integer contractId : allContractIds) {
			
			// --------------------------------------------- Init
			
			EmployeeContractInfo employeeContractInfo = new EmployeeContractInfo();
			ContractInfo contractData = new ContractInfo();
			EmployeeInfo employeeData = new EmployeeInfo();
			
			// --------------------------------------------- Employee Info
			
			// PERSON TABLE
			Record personTable = dslContext.select().from(PERSON)
					.where(PERSON.REGISTRY.eq(
							dslContext.select(CONTRACT.PERSON).from(CONTRACT)
								.where(CONTRACT.ID.eq(contractId))))
					.fetchOne();
		
			employeeData.setEmployeeId(personTable.get(PERSON.REGISTRY));
			employeeData.setDomain(personTable.get(PERSON.DOMAIN));
			employeeData.setBirthdate(personTable.get(PERSON.BIRTH_DATE));
			employeeData.setGender(personTable.get(PERSON.GENDER));
			employeeData.setCivilStatus(personTable.get(PERSON.MARITAL_STATUS));
			employeeData.setSsNumber(personTable.get(PERSON.SOCIAL_SECURITY_NUM));
			employeeData.setName(personTable.get(PERSON.NAME));
			employeeData.setSurName(personTable.get(PERSON.FIRST_SURNAME));
			employeeData.setSecondSurName(personTable.get(PERSON.SECOND_SURNAME));
			
			Integer employee_registry = personTable.get(PERSON.REGISTRY);
			
			// REGISTRY TABLE
			Record registryTable = dslContext.select().from(REGISTRY)
					.where(REGISTRY.ID.eq(employee_registry))
					.fetchOne();
			
			employeeData.setDocument(registryTable.get(REGISTRY.DOCUMENT));
			employeeData.setDocumentType(registryTable.get(REGISTRY.DOCUMENT_TYPE)); //Esto lo saco con el formato del documento, se podria obviar?
			employeeData.setNationality(registryTable.get(REGISTRY.NATIONALITY));
			
			// RADDRESS AND GEOZONE TABLE
			Record raddressTable = dslContext.select().from(RADDRESS)
					.where(RADDRESS.REGISTRY.eq(employee_registry))
					.fetchOne();
			
			if(null != raddressTable) {
			
				employeeData.setRaddressId(raddressTable.get(RADDRESS.ID));
				employeeData.setStreetType(raddressTable.get(RADDRESS.STREET_TYPE));
				employeeData.setAddress(raddressTable.get(RADDRESS.ADDRESS));
				employeeData.setAddressInfo(raddressTable.get(RADDRESS.ADDRESS2));
				employeeData.setAddresNum(raddressTable.get(RADDRESS.NUMBER));
				employeeData.setAddressZip(raddressTable.get(RADDRESS.ZIP));
				employeeData.setAddressCity(raddressTable.get(RADDRESS.MUNICIPALITY_CODE));
				
				Integer raddress_geozone = raddressTable.get(RADDRESS.GEOZONE);
				if(null == raddress_geozone) {
					employeeData.setGeozoneId(null);
					employeeData.setAddressProvinces(null);
				}else {
					Record geozoneTable = dslContext.select().from(GEOZONE)
							.where(GEOZONE.ID.eq(raddress_geozone))
							.fetchOne();
					
					employeeData.setGeozoneId(geozoneTable.get(GEOZONE.ID));
					employeeData.setAddressProvinces(geozoneTable.get(GEOZONE.CODE));
				}
			}
			
			// RMEDIA TABLE
			Result<Record> rmediaTable = dslContext.select().from(RMEDIA)
					.where(RMEDIA.REGISTRY.eq(employee_registry))
					.fetch();
			
			for(Record record : rmediaTable){
				if(record.get(RMEDIA.MEDIA) == 1){
					employeeData.setPhoneId(record.get(RMEDIA.ID));
					employeeData.setPhone(record.get(RMEDIA.VALUE));
				}else if(record.get(RMEDIA.MEDIA) == 2){
					employeeData.setMobileId(record.get(RMEDIA.ID));
					employeeData.setMobile(record.get(RMEDIA.VALUE));
				}else if(record.get(RMEDIA.MEDIA) == 4){
					employeeData.setEmailId(record.get(RMEDIA.ID));
					employeeData.setEmail(record.get(RMEDIA.VALUE));
				}
			}
			
			// FIND RPAYMETHOD
			Record rPayMethodRecord = dslContext.select().from(RPAYMETHOD)
				.where(RPAYMETHOD.REGISTRY.eq(employee_registry))
				.fetchOne();
			
			if(null == rPayMethodRecord) {
				employeeData.setRpaymethodId(null);
				
				employeeData.setPaymethodId(null);
				employeeData.setPayMethodType(null);
				
				employeeData.setRbankId(null);
				employeeData.setAccount(null);
				employeeData.setBic(null);
			}else {
				employeeData.setRpaymethodId(rPayMethodRecord.get(RPAYMETHOD.ID));
				
				Record payMethodTable = dslContext.select().from(PAY_METHOD)
						.where(PAY_METHOD.ID.eq(rPayMethodRecord.get(RPAYMETHOD.PAY_METHOD)))
						.fetchOne();
				
				if(null != payMethodTable) {
					employeeData.setPaymethodId(payMethodTable.get(PAY_METHOD.ID));
					employeeData.setPayMethodType(payMethodTable.get(PAY_METHOD.NAME));
					employeeData.setPayMethodTypeB(payMethodTable.get(PAY_METHOD.TYPE));
				}
				
				Integer rbank = rPayMethodRecord.get(RPAYMETHOD.RBANK);
				
				employeeData.setRbankId(null);
				employeeData.setAccount(null);
				employeeData.setBic(null);
				
				if(null != rbank) {
					Record rBankTable = dslContext.select().from(RBANK)
							.where(RBANK.ID.eq(rbank))
							.fetchOne();
					
					if(null != rBankTable) {
						employeeData.setRbankId(rBankTable.get(RBANK.ID));
						employeeData.setAccount(rBankTable.get(RBANK.BANK_ACCOUNT));
						employeeData.setBic(rBankTable.get(RBANK.BIC));
					}
				}
				
			}
			
			// GET RBANKS
			Result<Record> rbankRecords = dslContext.select().from(RBANK)
					.where(RBANK.REGISTRY.eq(personTable.get(PERSON.REGISTRY)))
					.fetch();
			
			for(Record r: rbankRecords) {
				employeeData.addRbank(r.get(RBANK.ID), r.get(RBANK.BANK_ACCOUNT), r.get(RBANK.BIC));
			}
			
			// --------------------------------------------- Contract Info
			
			// HAS PAYROLL
			Result<Record> salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.CONTRACT.eq(contractId))
						.and(SALARY.TYPE.eq((byte)0))
						.orderBy(SALARY.END_DATE.desc())
						.fetch();
			
			if(salaryRecords.isEmpty()){
				contractData.setHasPayroll(false);
				contractData.setPayrollDate(null);
			}else{
				contractData.setHasPayroll(true);
				contractData.setPayrollDate(salaryRecords.get(0).get(SALARY.END_DATE));
			}
			
			// CONTRACT TABLE
			Record contractTable = dslContext.select().from(CONTRACT)
					.where(CONTRACT.ID.eq(contractId))
					.fetchOne();
			
			contractData.setContractId(contractId);
			contractData.setStartDate(contractTable.get(CONTRACT.START_DATE));
			contractData.setEndDate(contractTable.get(CONTRACT.END_DATE));
			contractData.setSeniorityDate(contractTable.get(CONTRACT.SENIORITY_DATE));
			contractData.setAgreementCategory(contractTable.get(CONTRACT.CATEGORY_DESCRIPTION));
			contractData.setSsRegimen(contractTable.get(CONTRACT.SS_REGIME));
			
			contractData.setOldStartDate(contractTable.get(CONTRACT.START_DATE));
			contractData.setOldEndDate(contractTable.get(CONTRACT.END_DATE));
			
			Integer employe_workplace_table_id = contractTable.get(CONTRACT.WORKPLACE);
			
			// WORKPLACE TABLE		
			contractData.setWorkplaceId(employe_workplace_table_id);
			
			Record raddressRecord = dslContext.select().from(RADDRESS)
					.where(RADDRESS.ID.eq(
							dslContext.select(WORKPLACE.ADDRESS).from(WORKPLACE)
								.where(WORKPLACE.ID.eq(employe_workplace_table_id))
								.fetchOne(WORKPLACE.ADDRESS)
					)).fetchOne();
			
			contractData.setWorkplaceZIP(raddressRecord.get(RADDRESS.MUNICIPALITY_CODE));
			contractData.setWorkplaceFullAddress(raddressRecord.get(RADDRESS.STREET_TYPE)+". "+raddressRecord.get(RADDRESS.ADDRESS)+" "+raddressRecord.get(RADDRESS.NUMBER));
			
			if(contractData.getSsRegimen() != 3){ // NO ES RETA
				// ENTERPRISE ACTIVITY TABLE
				Integer enterpriseActivity = contractTable.get(CONTRACT.ENTERPRISE_ACTIVITY);
				
				if(null == enterpriseActivity) {
					contractData.setActivityId(null);
				}else {
					Record enterpriseActivityTable = dslContext.select().from(ENTERPRISE_ACTIVITY)
							.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseActivity))
							.fetchOne();
					
					contractData.setActivityId(enterpriseActivityTable.get(ENTERPRISE_ACTIVITY.ID));
					
					String enterpriseDocument = dslContext.select(REGISTRY.DOCUMENT).from(REGISTRY)
							.where(REGISTRY.ID.eq(enterpriseActivityTable.get(ENTERPRISE_ACTIVITY.ENTERPRISE)))
							.fetchOne(REGISTRY.DOCUMENT);
					
					contractData.setEnterpriseCIF(enterpriseDocument);
				}
				
				//ENTERPRISE CCC TABLE
				Integer enterpriseCCC = contractTable.get(CONTRACT.ENTERPRISE_CCC);
				
				if(null == enterpriseCCC) {
					contractData.setCccId(null);
					contractData.setCccType(null);
					
				}else {
					Record enterpriseCCCTable = dslContext.select().from(ENTERPRISE_CCC)
							.where(ENTERPRISE_CCC.ID.eq(enterpriseCCC))
							.fetchOne();
					
					contractData.setCccId(enterpriseCCCTable.get(ENTERPRISE_CCC.ID));
					contractData.setCccType(enterpriseCCCTable.get(ENTERPRISE_CCC.TYPE));
					
					contractData.setCompleteCCC(getCCCRegimeCode(enterpriseCCCTable.get(ENTERPRISE_CCC.TYPE))+enterpriseCCCTable.get(ENTERPRISE_CCC.CCC));
				}
			}
			
			// AGREEMENT LEVEL TABLE
			Integer agreementLevel = contractTable.get(CONTRACT.AGREEMENT_LEVEL);
			
			if(null == agreementLevel) {
				contractData.setAgreementLevelId(null);
				contractData.setAgreementId(null);
			}else {
				Record agreementLevelTable = dslContext.select().from(AGREEMENT_LEVEL)
						.where(AGREEMENT_LEVEL.ID.eq(agreementLevel))
						.fetchOne();
				try {
					contractData.setAgreementLevelId(agreementLevelTable.get(AGREEMENT_LEVEL.ID));
					
					//AGREEMENT TABLE
					Integer agreement = agreementLevelTable.get(AGREEMENT_LEVEL.AGREEMENT);
					
					Record agreementTable = dslContext.select().from(AGREEMENT)
							.where(AGREEMENT.ID.eq(agreement))
							.fetchOne();
					
					contractData.setAgreementId(agreementTable.get(AGREEMENT.ID));
				} catch ( Throwable t ) {
					contractData.setAgreementLevelId(null);
					contractData.setAgreementId(null);
				}
				
			}
			
			// FECHA ACTUAL
			java.util.Date actualJavaDate = new java.util.Date();
			Date actualSQLDate = new Date(actualJavaDate.getTime());
			
			contractData.setContracttypeId(null);
			contractData.setContractType(null);
			contractData.setQuotegroupId(null);
			contractData.setQuoteGroup(null);
			contractData.setOcupationId(null);
			contractData.setOcupation(null);
			contractData.setJourneytypeId(null);
			contractData.setJourneyType(null);
			contractData.setPartialityCoef(null);
			
			// CONTRACT DATA TABLE
			Date currentDate = new Date(new java.util.Date().getTime());
			Result<Record> contractDataTable = null;
			
			if(null != contractData.getEndDate()) { //Para contratos finalizados
				if(currentDate.after( contractData.getEndDate())) {
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.orderBy(CONTRACT_DATA.START_DATE)
					.fetch();
				}else {
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
							.where(CONTRACT_DATA.CONTRACT.eq(contractId))
							.and(CONTRACT_DATA.START_DATE.le(currentDate))
							.and(CONTRACT_DATA.END_DATE.ge(currentDate).or(CONTRACT_DATA.END_DATE.isNull()))
							.fetch();
					
					if(contractDataTable.isEmpty())
						contractDataTable = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(contractId))
						.orderBy(CONTRACT_DATA.ID)
						.fetch();
				}
				
			} else {
				
				if(contractData.getStartDate().after(currentDate)) {
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
							.where(CONTRACT_DATA.CONTRACT.eq(contractId))
							.and(CONTRACT_DATA.START_DATE.le(new Date(contractData.getStartDate().getTime())))
							.and(CONTRACT_DATA.END_DATE.ge(new Date(contractData.getStartDate().getTime())).or(CONTRACT_DATA.END_DATE.isNull()))
							.fetch();
				}else
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
							.where(CONTRACT_DATA.CONTRACT.eq(contractId))
							.orderBy(CONTRACT_DATA.START_DATE.asc())
							.fetch();
				
				if(contractDataTable.isEmpty())
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.orderBy(CONTRACT_DATA.ID)
					.fetch();
			}
				
			Map<String, String> contractDataMap = new HashMap<>();
			
			for(Record r : contractDataTable){
				contractDataMap.put(r.get(CONTRACT_DATA.NAME), r.get(CONTRACT_DATA.EXPRESSION));
				
				if(r.get(CONTRACT_DATA.NAME).equals("TC2")) {
					contractData.setContracttypeId(r.get(CONTRACT_DATA.ID));
					contractData.setContractType(r.get(CONTRACT_DATA.EXPRESSION));
				}else if(r.get(CONTRACT_DATA.NAME).equals("GRUPO_COTIZACION")) {
					contractData.setQuotegroupId(r.get(CONTRACT_DATA.ID));
					contractData.setQuoteGroup(r.get(CONTRACT_DATA.EXPRESSION));
				}else if(r.get(CONTRACT_DATA.NAME).equals("OCUPACION")) {
					contractData.setOcupationId(r.get(CONTRACT_DATA.ID));
					contractData.setOcupation(r.get(CONTRACT_DATA.EXPRESSION));
				}else if(r.get(CONTRACT_DATA.NAME).equals("TIEMPO_COMPLETO")) {
					contractData.setJourneytypeId(r.get(CONTRACT_DATA.ID));
					contractData.setJourneyType(r.get(CONTRACT_DATA.EXPRESSION).equalsIgnoreCase("TRUE") ? (byte) 0 : (byte) 1);
				}else if(r.get(CONTRACT_DATA.NAME).equals("COEFICIENTE_PARCIALIDAD")) {
					String expression = r.get(CONTRACT_DATA.EXPRESSION);
					if(null != expression && expression.contains("\""))
						expression = expression.split("\"")[1];
					
					Double partialityCoef = Double.parseDouble(expression);
					contractData.setPartialityCoefId(r.get(CONTRACT_DATA.ID));
					contractData.setPartialityCoef(partialityCoef);
				}
				
			}
			
			// CONTRACT INFO TABLE
			Result<Record> contractInfoTableRecords = dslContext.select().from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("OPCION_CONTRATO"))
				.fetch();
			
			Record contractInfoTable = null;
			
			if(contractInfoTableRecords.isEmpty()) {
				contractData.setContractmodelId(null);
				contractData.setContractModel(null);
			}else {
				contractInfoTable = contractInfoTableRecords.get(0);
				
				if(null == contractInfoTable.get(CONTRACT_INFO.EXPRESSION)){
					contractData.setContractmodelId(contractInfoTable.get(CONTRACT_INFO.ID));
					contractData.setContractModel(null);
				}else {
					String contractType = contractInfoTable.get(CONTRACT_INFO.EXPRESSION);
					
					if(contractType.contains("\""))
						contractType = contractType.split("\"")[1];
					
					Integer ordinal = ModelOption.valueOf(contractType).ordinal();
					
					contractData.setContractmodelId(contractInfoTable.get(CONTRACT_INFO.ID));
					contractData.setContractModel(ordinal);	
				}
			}
			
			contractInfoTable = dslContext.select().from(CONTRACT_INFO)
					.where(CONTRACT_INFO.CONTRACT.eq(contractId))
					.and(CONTRACT_INFO.NAME.eq("RETA"))
					.fetchOne();
			
			if(null == contractInfoTable)
				contractData.setRetaId(null);
			else
				contractData.setRetaId(contractInfoTable.get(CONTRACT_INFO.ID));
			
			Result<Record> journiesDB = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.NAME.like("HORAS%"))
					.and(CONTRACT_DATA.CONTRACT.eq(contractId))
					.orderBy(CONTRACT_DATA.START_DATE)
					.fetch();
			
			Map<java.util.Date, ArrayList<JourneyDuration>> journies = new HashMap<>();
			
			if(null != journiesDB && !journiesDB.isEmpty()) {
				Date iterableDate = journiesDB.get(0).get(CONTRACT_DATA.START_DATE);
				ArrayList<JourneyDuration> journeyList = new ArrayList<>();
				for(Record r : journiesDB) {
					if(r.get(CONTRACT_DATA.START_DATE).equals(iterableDate)) {
						JourneyDuration journey = new JourneyDuration();
						journey.setStartDate(r.get(CONTRACT_DATA.START_DATE));
						journey.setEndDate(r.get(CONTRACT_DATA.END_DATE));
						journey.setName(r.get(CONTRACT_DATA.NAME));
						journey.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
						
						journeyList.add(journey);
					}else {
						journies.put(iterableDate, journeyList);
						journeyList = new ArrayList<>();
						
						iterableDate = r.get(CONTRACT_DATA.START_DATE);
						
						JourneyDuration journey = new JourneyDuration();
						journey.setStartDate(r.get(CONTRACT_DATA.START_DATE));
						journey.setEndDate(r.get(CONTRACT_DATA.END_DATE));
						journey.setName(r.get(CONTRACT_DATA.NAME));
						journey.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
						
						journeyList.add(journey);
					}
				}
				journies.put(iterableDate, orderByWeekDay(journeyList));
			}
			
			contractData.setContractJourneyDuration(journies);
			
			employeeContractInfo.setEmployeeInfo(employeeData);
			employeeContractInfo.setContractInfo(contractData);
			
			// ------------------------------------------- CONTRACT SPECIFIC DATA -----------------------------------------------------
			
			ContractSpecificData contractSpecificData = new ContractSpecificData();
			
			// Get CNO
			Result<Record> cnoRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("CNO"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractData.getContractId()))
				.fetch();
			
			if(cnoRecords.isNotEmpty())
				contractSpecificData.setCno(parseContractTable(cnoRecords.get(0).get(CONTRACT_DATA.EXPRESSION)));
			
			Result<Record> contractSpecificDataRecords = dslContext.select().from(CONTRACT_ATTACH)
					.where(CONTRACT_ATTACH.CONTRACT.eq(contractData.getContractId())
							.or(CONTRACT_ATTACH.CONTRACT.isNull()))
					.and(CONTRACT_ATTACH.DOMAIN.eq(employeeData.getDomain()))
					.and(CONTRACT_ATTACH.TYPE.eq((byte)4))
					.fetch();
			
			if(contractSpecificDataRecords.isNotEmpty()) {
				byte[] data = contractSpecificDataRecords.get(0).get(CONTRACT_ATTACH.DATA);
				
				//TODO: Read XML?¿?¿
			}
			
			employeeContractInfo.setContractSpecificData(contractSpecificData);
			
			// ---------------------------------------------- CONTRACT OTHER INFO -----------------------------------------------------
			
			Map<String, String> contractOtherInfoMap = new HashMap<String, String>();
			employeeContractInfo.setContractOtherData(contractOtherInfoMap);
			
			// ------------------------------------------------ CONTRACT CLAUSE -------------------------------------------------------
			
			List<ContractClause> contractClauses = new ArrayList<ContractClause>();
			employeeContractInfo.setContractClauses(contractClauses);

			// ------------------------------------------------ CONTRACT ATTACH -------------------------------------------------------
			
			List<ContractAttach> contractAttachs = new ArrayList<ContractAttach>();
			employeeContractInfo.setContractAttachments(contractAttachs);
			
			// ------------------------------------------------------------------------------------------------------------------------
			// ------------------------------------------------ SCOPES -------------------------------------------------------
			// ------------------------------------------------------------------------------------------------------------------------

			Map<String, String> scopeMap = new HashMap<String, String>();
			
			Result<Record> scopeRecords = dslContext.select().from(SCOPE)
				.where(SCOPE.DOMAIN.eq(employeeData.getDomain()))
				.fetch();
			
			for(Record record : scopeRecords) {
				scopeMap.put(record.get(SCOPE.DESCRIPTION), record.get(SCOPE.ID).toString());
			}
			
			employeeContractInfo.setScopeMap(scopeMap);
			
			employeesInfo.add(employeeContractInfo);
			
		}
		
		return employeesInfo;
	}
	
	// --------------------------------------- AUX METHODS -----------------------------

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
	
	public static Date parseDate(java.util.Date date) {
		if(null == date)
			return null;
		
		DateUtils.resetTime(date);
		return new Date(date.getTime());
	}
	
	public static String parseContractTableStr(String exp) {
		if(null == exp)
			return null;
		
		return "\""+ exp +"\"";
	}
	
	public static String parseContractTable(String exp) {
		if(null == exp)
			return null;
		
		return exp.split("\"")[1];
	}
	
	public static String getPaymentTypeName(byte type) {
		switch (type) {
		case (byte) 0:
			return "EFECTIVO";
		case (byte) 4:
			return "CHEQUE";
		case (byte) 5:
			return "TRANSFERENCIA";
		default:
			return "";
		}
	}
	
	private static ArrayList<JourneyDuration> orderByWeekDay(ArrayList<JourneyDuration> journeyList) {
		ArrayList<JourneyDuration> result = new ArrayList<>();
		
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_LUNES"))
				result.add(journey);
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_MARTES"))
				result.add(journey);
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_MIERCOLES"))
				result.add(journey);
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_JUEVES"))
				result.add(journey);
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_VIERNES"))
				result.add(journey);
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_SABADO"))
				result.add(journey);
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_DOMINGO"))
				result.add(journey);
		
		return result;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------    AUXLIAR METHODS   --------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------------	
	
	private static List<String> getContractOtherDataListNames(String contractTypeStr) {
		Integer contractType = 100;
		try {
			contractType = Integer.parseInt(contractTypeStr);
		}catch (Exception e) {
			System.out.println("ContractType :" + contractTypeStr);
			return new ArrayList<String>();
		}
		
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
		List<String> contractOtherDataNames = new ArrayList<String>();
		
		contractOtherDataNames.add("ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("FUNCTIONS");
		contractOtherDataNames.add("EMPLOYEE_CONTRACT_DISTANCE");
		contractOtherDataNames.add("EMPLOYEE_CONTRACT_DIST_ADDR");
		contractOtherDataNames.add("DISC_WORK_DESCRIPTION");
		contractOtherDataNames.add("DISC_WORK_ACTIVITY");
		contractOtherDataNames.add("DISC_WORK_DURATION");
		contractOtherDataNames.add("DISC_WORK_ESTIMATED_DURATION");
		contractOtherDataNames.add("DISC_WORK_ESTIM_JOURNAL_HOURS");
		contractOtherDataNames.add("DISC_WORK_ESTIM_JOURNAL_PERIOD");
		contractOtherDataNames.add("DISC_WORK_ESTIM_SCHEDULE");
		contractOtherDataNames.add("DISC_AGREEMENT_COLLECTIVE");
		contractOtherDataNames.add("FULL_TIME_WEEK_HOURS");
		contractOtherDataNames.add("FULL_TIME_START_TIME");
		contractOtherDataNames.add("FULL_TIME_END_TIME");
		contractOtherDataNames.add("PARTIALLY_TIME_HOURS");
		contractOtherDataNames.add("DEFAULT_JOURNAL_HOURS");
		contractOtherDataNames.add("COMPLEMENTARY_HOURS");
		contractOtherDataNames.add("TRIAL_DURATION");
		contractOtherDataNames.add("SALARY_AMOUNT");
		contractOtherDataNames.add("SALARY_PERIOD");
		contractOtherDataNames.add("SALARY_CONCEPT");
		contractOtherDataNames.add("HOLIDAYS");
		contractOtherDataNames.add("SEPE_MUNICIPALITY");
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
		
		return contractOtherDataNames;
	}
	
	private static List<String> getContractOtherDataTempListNames(){
		List<String> contractOtherDataNames = new ArrayList<String>();
		
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
		
		return contractOtherDataNames;
	}
	
	private static List<String> getContractOtherDataFormationListNames(){
		List<String> contractOtherDataNames = new ArrayList<String>();
		
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
		
		return contractOtherDataNames;
	}
	
	private static List<String> getContractOtherDataPracticeListNames(){
		List<String> contractOtherDataNames = new ArrayList<String>();
		
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

	public static Map<String, CNO> getCNOs(Connection conn) {
		return getCNOsDB(DSL.using(conn, getDefaultSettings()));
	}

	private static Map<String, CNO> getCNOsDB(DSLContext dslContext) {
		Map<String, CNO> cnoMap = new HashMap<String, CNO>();
		Result<Record> cnoRecords = dslContext.select().from(CNO).fetch();
		for(Record cnoRecord : cnoRecords) {
			CNO cno = new CNO();
			cno.setId(cnoRecord.get(CNO.ID));
			cno.setCode(cnoRecord.get(CNO.CODE));
			cno.setTitle(cnoRecord.get(CNO.TITLE));
					
			cnoMap.put(cnoRecord.get(CNO.CODE), cno);
		}
		return cnoMap;
	}
	
	private static List<String> getContractOtherDataListNames(){
		List<String> contractOtherDataNames = new ArrayList<String>();
		
		// ------------------------------------------------------- Indefinite Table
		
		contractOtherDataNames.add("ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("FUNCTIONS");
		contractOtherDataNames.add("EMPLOYEE_CONTRACT_DISTANCE");
		contractOtherDataNames.add("EMPLOYEE_CONTRACT_DIST_ADDR");
		contractOtherDataNames.add("DISC_WORK_DESCRIPTION");
		contractOtherDataNames.add("DISC_WORK_ACTIVITY");
		contractOtherDataNames.add("DISC_WORK_DURATION");
		contractOtherDataNames.add("DISC_WORK_ESTIMATED_DURATION");
		contractOtherDataNames.add("DISC_WORK_ESTIM_JOURNAL_HOURS");
		contractOtherDataNames.add("DISC_WORK_ESTIM_JOURNAL_PERIOD");
		contractOtherDataNames.add("DISC_WORK_ESTIM_SCHEDULE");
		contractOtherDataNames.add("DISC_AGREEMENT_COLLECTIVE");
		contractOtherDataNames.add("FULL_TIME_WEEK_HOURS");
		contractOtherDataNames.add("FULL_TIME_START_TIME");
		contractOtherDataNames.add("FULL_TIME_END_TIME");
		contractOtherDataNames.add("PARTIALLY_TIME_HOURS");
		contractOtherDataNames.add("DEFAULT_JOURNAL_HOURS");
		contractOtherDataNames.add("COMPLEMENTARY_HOURS");
		contractOtherDataNames.add("TRIAL_DURATION");
		contractOtherDataNames.add("SALARY_AMOUNT");
		contractOtherDataNames.add("SALARY_PERIOD");
		contractOtherDataNames.add("SALARY_CONCEPT");
		contractOtherDataNames.add("HOLIDAYS");
		contractOtherDataNames.add("SEPE_MUNICIPALITY");
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

}
