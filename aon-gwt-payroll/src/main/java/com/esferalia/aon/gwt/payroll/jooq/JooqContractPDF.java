package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.FormativeLevel;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.payroll.contract.ContractFill;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqContractPDF {

	// ---------------------------------------------------- Constructor
	
	private JooqContractPDF() {
		super();
	}
	
	// ---------------------------------------------------- Settings

	private static Settings settings = null;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
	private static SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
	private static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	// ---------------------------------------------------- DataBase
	
	// ---------------------------------------------------- Contract PDF (Save)
	
	public static void saveDraftContract(String domainName, Integer domainId, Integer contractId, byte[] pdfBytes) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			// Id borrador contrato
			List<Integer> attachIds = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
					.where(CONTRACT_ATTACH.DOMAIN.eq(domainId))
					.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
					.and(CONTRACT_ATTACH.TYPE.eq((byte)0))
					.orderBy(CONTRACT_ATTACH.ID.desc()).fetch(CONTRACT_ATTACH.ID);
			
			if(attachIds.isEmpty())
				dslContext.insertInto(CONTRACT_ATTACH)
					.set(CONTRACT_ATTACH.DOMAIN, domainId)
					.set(CONTRACT_ATTACH.CONTRACT, contractId)
					.set(CONTRACT_ATTACH.MIMETYPE, (byte)22)
					.set(CONTRACT_ATTACH.DESCRIPTION, "BORRADOR CONTRATO")
					.set(CONTRACT_ATTACH.DATA, pdfBytes)
					.set(CONTRACT_ATTACH.TYPE, (byte)0)
					.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
					.execute();
			else
				dslContext.update(CONTRACT_ATTACH)
					.set(CONTRACT_ATTACH.DATA, pdfBytes)
					.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
					.where(CONTRACT_ATTACH.ID.eq(attachIds.get(0)))
					.execute();
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}
	
	public static void saveDraftContractTransform(String domainName, Integer domainId, Integer contractId, byte[] pdfBytes) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			// Id borrador contrato
			List<Integer> attachIds = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
					.where(CONTRACT_ATTACH.DOMAIN.eq(domainId))
					.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
					.and(CONTRACT_ATTACH.TYPE.eq((byte)109))
					.orderBy(CONTRACT_ATTACH.ID.desc()).fetch(CONTRACT_ATTACH.ID);
			
			if(attachIds.isEmpty())
				dslContext.insertInto(CONTRACT_ATTACH)
					.set(CONTRACT_ATTACH.DOMAIN, domainId)
					.set(CONTRACT_ATTACH.CONTRACT, contractId)
					.set(CONTRACT_ATTACH.MIMETYPE, (byte)22)
					.set(CONTRACT_ATTACH.DESCRIPTION, "BORRADOR TRANSFORMACION CONTRATO")
					.set(CONTRACT_ATTACH.DATA, pdfBytes)
					.set(CONTRACT_ATTACH.TYPE, (byte)109)
					.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
					.execute();
			else
				dslContext.update(CONTRACT_ATTACH)
					.set(CONTRACT_ATTACH.DATA, pdfBytes)
					.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
					.where(CONTRACT_ATTACH.ID.eq(attachIds.get(0)))
					.execute();
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}
	
	public static void saveDraftContractExtension(String domainName, Integer domainId, Integer contractId, byte[] pdfBytes) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			// Id borrador contrato
			List<Integer> attachIds = dslContext.select(CONTRACT_ATTACH.ID).from(CONTRACT_ATTACH)
					.where(CONTRACT_ATTACH.DOMAIN.eq(domainId))
					.and(CONTRACT_ATTACH.CONTRACT.eq(contractId))
					.and(CONTRACT_ATTACH.TYPE.eq((byte)110))
					.orderBy(CONTRACT_ATTACH.ID.desc()).fetch(CONTRACT_ATTACH.ID);
			
			if(attachIds.isEmpty())
				dslContext.insertInto(CONTRACT_ATTACH)
					.set(CONTRACT_ATTACH.DOMAIN, domainId)
					.set(CONTRACT_ATTACH.CONTRACT, contractId)
					.set(CONTRACT_ATTACH.MIMETYPE, (byte)22)
					.set(CONTRACT_ATTACH.DESCRIPTION, "BORRADOR PRORROGA CONTRATO")
					.set(CONTRACT_ATTACH.DATA, pdfBytes)
					.set(CONTRACT_ATTACH.TYPE, (byte)110)
					.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
					.execute();
			else
				dslContext.update(CONTRACT_ATTACH)
					.set(CONTRACT_ATTACH.DATA, pdfBytes)
					.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
					.where(CONTRACT_ATTACH.ID.eq(attachIds.get(0)))
					.execute();
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}
	
	// ---------------------------------------------------- Contract PDF (fill)
	
	public static byte[] contractFill(Connection connection, Integer domainId, Integer parentDomainId, Integer contractId, Integer contractType, String formativeLevelCode, boolean isTransform) throws IllegalArgumentException {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
				
			try {
				Map<String, String> contractOtherInfo = JooqContractOtherInfo.getContractOtherInfo(connection, domainId, parentDomainId, contractId, contractType);
				Map<String, String> contractFillInfo = getContractFillInfoDB(dslContext, contractId);
				
				TreeMap<String, String> contractClauses = parseClausesToMap(JooqContractClauses.getContractClauses(connection, contractId));
				
				FormativeLevel formativeLevel = new FormativeLevel();
				contractFillInfo.put("E_FORMATIVE_LVL", AonStringUtils.abbreviate(formativeLevel.getFormativeLevelDescription(formativeLevelCode), 32));
				contractFillInfo.put("E_FORMATIVE_LVL_CODE", formativeLevelCode);
				
				String sepeIde = isTransform ? getSepeTransformIde(dslContext, contractId) : getSepeIde(dslContext, contractId);
				java.util.Date comunicationDate = isTransform ? getComunicationTransformDate(dslContext, contractId) : getComunicationDate(dslContext, contractId);
				
				return ContractFill.fillContract(contractType, sepeIde, comunicationDate, contractOtherInfo, contractFillInfo, contractClauses);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
	}
	
	public static byte[] contractExtensionFill(Connection connection, EmployeeInfo employeeData, ContractInfo contractData) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		try {
			Map<String, String> contractExtensionFillInfo = getContractExtensionFillInfoDB(dslContext, employeeData, contractData);
			
			return ContractFill.fillContractExtension(contractExtensionFillInfo);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private static Map<String, String> getContractExtensionFillInfoDB(DSLContext dslContext, EmployeeInfo employeeData, ContractInfo contractData) {
		Map<String, String> extensionInfo = new HashMap<>();
		Municipalities municipalities = new Municipalities();
		
		// ENTRPRISE
		Result<Record> entepriseRecords = dslContext.select().from(ENTERPRISE)
				.leftJoin(RADDRESS)
				.on(ENTERPRISE.REGISTRY.eq(RADDRESS.REGISTRY))
				.where(ENTERPRISE.REGISTRY.eq(
						dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY).where(ENTERPRISE_ACTIVITY.ID.eq(
								dslContext.select(CONTRACT.ENTERPRISE_ACTIVITY).from(CONTRACT).where(CONTRACT.ID.eq(contractData.getContractId())).fetchOne(CONTRACT.ENTERPRISE_ACTIVITY)
						)).fetchOne(ENTERPRISE_ACTIVITY.ENTERPRISE)
				)).fetch();
		
		if(!entepriseRecords.isEmpty()) {
			Record enterpriseRecord = entepriseRecords.get(0);
			String address = enterpriseRecord.get(RADDRESS.STREET_TYPE) + " " + enterpriseRecord.get(RADDRESS.ADDRESS) + ", " + enterpriseRecord.get(RADDRESS.NUMBER);
			String municipalityCode = enterpriseRecord.get(RADDRESS.MUNICIPALITY_CODE);
			String zip = enterpriseRecord.get(RADDRESS.ZIP);
			
			extensionInfo.put("Texto1", contractData.getEnterpriseCIF());
			extensionInfo.put("Texto5", contractData.getEnterpriseName());
			extensionInfo.put("Texto6", address);
			extensionInfo.put("Texto7", "ESPA\u00D1A");
			extensionInfo.put("Cifra1", "7");
			extensionInfo.put("Cifra2", "2");
			extensionInfo.put("Cifra3", "4");
			
			if(null != municipalityCode) {
				String municipality = municipalities.getMunicipalityByZip(municipalityCode);
				
				extensionInfo.put("Texto8", municipality);
				extensionInfo.put("Cifra4", municipalityCode.substring(0, 1));
				extensionInfo.put("Cifra5", municipalityCode.substring(1, 2));
				extensionInfo.put("Cifra6", municipalityCode.substring(2, 3));
				extensionInfo.put("Cifra7", municipalityCode.substring(3, 4));
				extensionInfo.put("Cifra8", municipalityCode.substring(4, 5));
			}
			
			extensionInfo.put("Cifra9", zip.substring(0, 1));
			extensionInfo.put("Cifra10", zip.substring(1, 2));
			extensionInfo.put("Cifra11", zip.substring(2, 3));
			extensionInfo.put("Cifra12", zip.substring(3, 4));
			extensionInfo.put("Cifra13", zip.substring(4, 5));
			
		}
		
		// ENTRPRISE CCC AND ACTIVITY
		Result<Record> entepriseCCCRecords = dslContext.select().from(CONTRACT)
				.leftJoin(ENTERPRISE_CCC)
				.on(CONTRACT.ENTERPRISE_CCC.eq(ENTERPRISE_CCC.ID))
				.leftJoin(ENTERPRISE_ACTIVITY)
				.on(CONTRACT.ENTERPRISE_ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
				.where(CONTRACT.ID.eq(contractData.getContractId()))
				.fetch();
		
		if(!entepriseCCCRecords.isEmpty()) {
			Record entepriseCCCRecord = entepriseCCCRecords.get(0);
			String regime = getCCCRegimeCode(entepriseCCCRecord.get(ENTERPRISE_CCC.TYPE));
			String cccAcount = entepriseCCCRecord.get(ENTERPRISE_CCC.CCC);
			String activity = entepriseCCCRecord.get(ENTERPRISE_ACTIVITY.DESCRIPTION);
			
			extensionInfo.put("Cifra14", regime.substring(0, 1));
			extensionInfo.put("Cifra15", regime.substring(1, 2));
			extensionInfo.put("Cifra16", regime.substring(2, 3));
			extensionInfo.put("Cifra17", regime.substring(3, 4));
			
			try {
				extensionInfo.put("Cifra18", cccAcount.substring(0, 1));
				extensionInfo.put("Cifra19", cccAcount.substring(1, 2));
				extensionInfo.put("Texto9", cccAcount.substring(2, 9));
				extensionInfo.put("Cifra20", cccAcount.substring(9, 10));
				extensionInfo.put("Cifra21", cccAcount.substring(10, 11));
			} catch (IndexOutOfBoundsException e) {
				throw new IllegalArgumentException("La cuenta de cotizaci\u00F3n no tiene el formato correcto (recuerde longuitud 11)");
			}
			
			extensionInfo.put("Texto10", activity);
			extensionInfo.put("Cifra22", "");
			extensionInfo.put("Cifra23", "");
		}
		
		// WORKPLACE
		Record raddressRecord = dslContext.select().from(RADDRESS).where(RADDRESS.ID.in(
					dslContext.select(WORKPLACE.ADDRESS).from(WORKPLACE).where(WORKPLACE.ID.in(
								dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT).where(CONTRACT.ID.eq(contractData.getContractId()))
									.fetchOne(CONTRACT.WORKPLACE)
							)).fetchOne(WORKPLACE.ADDRESS)
				)).fetchOne();
		
		if(null != raddressRecord) {
			String municipalityCode = raddressRecord.get(RADDRESS.MUNICIPALITY_CODE);
			String municipality = municipalities.getMunicipalityByZip(municipalityCode);
			
			extensionInfo.put("Texto11", "ESPA\u00D1A");
			extensionInfo.put("Cifra24", "7");
			extensionInfo.put("Cifra25", "2");
			extensionInfo.put("Cifra26", "4");
			
			if(null != municipalityCode) {
				extensionInfo.put("Texto12", municipality);
				extensionInfo.put("Cifra27", municipalityCode.substring(0, 1));
				extensionInfo.put("Cifra28", municipalityCode.substring(1, 2));
				extensionInfo.put("Cifra29", municipalityCode.substring(2, 3));
				extensionInfo.put("Cifra30", municipalityCode.substring(3, 4));
				extensionInfo.put("Cifra31", municipalityCode.substring(4, 5));
			}
		}
		
		// EMPLOYEE
		Record contractRecord = dslContext.select().from(CONTRACT)
				.leftJoin(REGISTRY)
				.on(CONTRACT.PERSON.eq(REGISTRY.ID))
				.leftJoin(PERSON)
				.on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
				.leftJoin(RADDRESS)
				.on(CONTRACT.PERSON.eq(RADDRESS.REGISTRY))
				.where(CONTRACT.ID.eq(contractData.getContractId()))		
				.fetchOne();
		
		Date birthDate = contractRecord.get(PERSON.BIRTH_DATE);
		
		String municipalityCode = contractRecord.get(RADDRESS.MUNICIPALITY_CODE);
		
		extensionInfo.put("Texto13", employeeData.getFullName());
		extensionInfo.put("Texto14", contractRecord.get(REGISTRY.DOCUMENT));
		extensionInfo.put("Texto15", null == birthDate ? "" : fullDateFormat.format(birthDate));
		extensionInfo.put("Texto16", contractRecord.get(PERSON.SOCIAL_SECURITY_NUM));
		extensionInfo.put("Texto18", employeeData.getNationality());
//		extensionInfo.put("Cifra34", "7");
//		extensionInfo.put("Cifra35", "2");
//		extensionInfo.put("Cifra36", "4");
		
		if(null != municipalityCode) {
			String municipality = municipalities.getMunicipalityByZip(municipalityCode);
			
			extensionInfo.put("Texto19", municipality);
			extensionInfo.put("Cifra37", municipalityCode.substring(0, 1));
			extensionInfo.put("Cifra38", municipalityCode.substring(1, 2));
			extensionInfo.put("Cifra39", municipalityCode.substring(2, 3));
			extensionInfo.put("Cifra40", municipalityCode.substring(3, 4));
			extensionInfo.put("Cifra41", municipalityCode.substring(4, 5));
		}
		
		extensionInfo.put("Texto20", "ESPA\u00D1A");
		extensionInfo.put("Cifra42", "7");
		extensionInfo.put("Cifra43", "2");
		extensionInfo.put("Cifra44", "4");
		
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		
		extensionInfo.put("Renglon9", fullDateFormat.format(contractData.getExtensionDate()));
		extensionInfo.put("Renglon10", fullDateFormat.format(endDate));
		extensionInfo.put("Renglon11", fullDateFormat.format(startDate));
		
		return extensionInfo;
	}

	private static String getSepeIde(DSLContext dslContext, Integer contractId) {
		Result<Record> ideRecords = dslContext.select().from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("SEPE_ID"))
				.fetch();
		
		return ideRecords.isEmpty() ? null : ideRecords.get(0).get(CONTRACT_INFO.EXPRESSION);
	}

	private static java.util.Date getComunicationDate(DSLContext dslContext, Integer contractId) {
		Result<Record> comunicationDateRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("COMUNICATION_DATE"))
				.fetch();
		
		try {
			return comunicationDateRecords.isEmpty() ? null : dateFormat.parse(comunicationDateRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
		} catch (IllegalArgumentException | ParseException e) {
			return null;
		}
	}

	private static String getSepeTransformIde(DSLContext dslContext, Integer contractId) {
		Result<Record> ideRecords = dslContext.select().from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("SEPE_TRANSFORM_ID"))
				.fetch();
		
		return ideRecords.isEmpty() ? null : ideRecords.get(0).get(CONTRACT_INFO.EXPRESSION);
	}

	private static java.util.Date getComunicationTransformDate(DSLContext dslContext, Integer contractId) {
		Result<Record> comunicationDateRecords = dslContext.select().from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("COMUNICATION_TRANSFORM_DATE"))
				.fetch();
		
		try {
			return comunicationDateRecords.isEmpty() ? null : dateFormat.parse(comunicationDateRecords.get(0).get(CONTRACT_INFO.EXPRESSION));
		} catch (IllegalArgumentException | ParseException e) {
			return null;
		}
	}
	
	public static byte[] contractFill(String domainName, Integer contractId, String contractTypeStr, String formativeLevelCode) throws IllegalArgumentException {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer contractType = Integer.parseInt(contractTypeStr);
			return contractFill(connection, domainId, parentDomainId, contractId, contractType, formativeLevelCode, false);
		}catch (SQLException  | IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		} 
	}
	
	// ---------------------------------------------------- Contract PDF (fill - clauses)
	
	private static TreeMap<String, String> parseClausesToMap(List<ContractClause> contractClauses) {
		TreeMap<String, String> contractClausesMap = new TreeMap<>();
		contractClauses.forEach(contractClause -> contractClausesMap.put(contractClause.getLineNumber() + " - " + contractClause.getName(), contractClause.getDescription()));
		return contractClausesMap;
	}
	
	// ---------------------------------------------------- Contract PDF (fill - info)

	private static Map<String, String> getContractFillInfoDB(DSLContext dslContext, Integer contractId) throws IllegalArgumentException {
		Map<String, String> contractFillData = new HashMap<>();
		
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
			
			
			contractFillData.put("ENTERPRISE_COUNTRY", "ESPA\u00D1A");
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
			try {
			contractFillData.put("ENTERPRISE_CCC_PRV", cccAcount.substring(0, 2));
			contractFillData.put("ENTERPRISE_CCC_NUM", cccAcount.substring(2, 9));
			contractFillData.put("ENTERPRISE_CCC_DC", cccAcount.substring(9, 11));
			} catch (IndexOutOfBoundsException e) {
				throw new IllegalArgumentException("La cuenta de cotizaci\u00F3n no tiene el formato correcto (recuerde longuitud 11)");
			}
			
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
		
		String workplaceAddress = "";
		if(null != raddressRecord) {
			workplaceAddress = raddressRecord.get(RADDRESS.STREET_TYPE) + " " + raddressRecord.get(RADDRESS.ADDRESS) + ", " + raddressRecord.get(RADDRESS.NUMBER) + "(" + raddressRecord.get(RADDRESS.ZIP) + ")";
			String municipalityCode = raddressRecord.get(RADDRESS.MUNICIPALITY_CODE);
			if(null != municipalityCode) {
				String municipality = municipalities.getMunicipalityByZip(municipalityCode);
				
				contractFillData.put("WORKPLC_MUNICIPALITY", municipality);
				contractFillData.put("WORKPLC_MUNICIPALITY_CODE", municipalityCode);
				contractFillData.put("WORKPLC_CITY", raddressRecord.get(RADDRESS.CITY));
			}
		}
		
		contractFillData.put("WORKPLC_COUNTRY", "ESPA\u00D1A");
		contractFillData.put("WORKPLC_COUNTRY_CODE", "724");
		contractFillData.put("E_WORKPLACE_ADDR", workplaceAddress);
		
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
			if(AonStringUtils.isNotBlank(ssNum) && ssNum.length() == 12) {
				contractFillData.put("E_SS1", ssNum.substring(0, 2));
				contractFillData.put("E_SS2", ssNum.substring(2, 10));
				contractFillData.put("E_SS3", ssNum.substring(10, 12));
			}
			
			String nationality = contractRecord.get(REGISTRY.NATIONALITY);
			contractFillData.put("E_NATIONALITY", Country.valueOf(nationality).getName());
			contractFillData.put("E_NATIONALITY_CODE", Country.valueOf(nationality).getIsoCode()+"");
			
			String municipalityCode = contractRecord.get(RADDRESS.MUNICIPALITY_CODE);
			if(null != municipalityCode) {
				String municipality = municipalities.getMunicipalityByZip(municipalityCode);
				
				contractFillData.put("E_MUNICIPALITY_ADDR", municipality);
				contractFillData.put("E_MUNICIPALITY_ADDR_CODE", municipalityCode);
			}
			
			contractFillData.put("E_COUNTRY_ADDR", "ESPA\u00D1A");
			contractFillData.put("E_COUNTRY_ADDR_CODE", "724");
			
		}
		
		// Employee (Contract)
		
		Record contractRecord = dslContext.select().from(CONTRACT)
				.leftJoin(AGREEMENT_LEVEL)
				.on(CONTRACT.AGREEMENT_LEVEL.eq(AGREEMENT_LEVEL.ID))
				.leftJoin(AGREEMENT)
				.on(AGREEMENT_LEVEL.AGREEMENT.eq(AGREEMENT.ID))
				.where(CONTRACT.ID.eq(contractId))		
				.fetchOne();
		
		String agreementLevelDescription = contractRecord.get(AGREEMENT_LEVEL.DESCRIPTION);
		String agreementLevelCatDescription = contractRecord.get(CONTRACT.CATEGORY_DESCRIPTION);
		
		contractFillData.put("E_AGREEMENT_LEVEL", agreementLevelDescription);
		contractFillData.put("E_AGREEMENT_CAT", agreementLevelCatDescription);
		
		contractFillData.put("C_START", fullDateFormat.format(contractRecord.get(CONTRACT.START_DATE)));
		
		contractFillData.put("DAY", dayFormat.format(contractRecord.get(CONTRACT.START_DATE)));
		contractFillData.put("MONTH", monthFormat.format(contractRecord.get(CONTRACT.START_DATE)));
		contractFillData.put("YEAR", yearFormat.format(contractRecord.get(CONTRACT.START_DATE)).substring(2, 4));
		
		contractFillData.put("C_AGREEMENT_COLECTIVE", contractRecord.get(AGREEMENT.DESCRIPTION));
		
		Result<Record> cnoRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("CNO"))
				.fetch();
		
		if(!cnoRecords.isEmpty()) {
			Map<String, CNO> cnoMap = JooqContrataContract.getCNOsDB(dslContext);
			String cnoCode = parseContractData(cnoRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
			CNO cno = cnoMap.get(cnoCode);
			contractFillData.put("E_CNO", cnoCode + " " + (null == cno ? "" : cno.getTitle()));
		}
		
		// Employee (Contract Info)
		
		Record contractInfo = dslContext.selectFrom(CONTRACT_INFO)
				.where(CONTRACT_INFO.NAME.eq("I_PARTIALLY_TIME_HOURS"))
				.and(CONTRACT_INFO.CONTRACT.eq(contractId))
				.fetchOne();
		
		if(null != contractInfo) {
			contractFillData.put("I_PARTIALLY_TIME_HOURS", contractInfo.get(CONTRACT_INFO.EXPRESSION));
		}
		
		return contractFillData;
	}
	
	// ---------------------------------------------------- Auxiliar methods
	
	private static String parseContractData(String value) {
		String parsedValue = null;
		if(AonStringUtils.isNotBlank(value) && value.contains("\""))
			try {
				parsedValue = value.split("\"")[1];
			} catch (IndexOutOfBoundsException e) {
				parsedValue = value;
			}
		else
			parsedValue = value;
		
		return parsedValue;
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
}
