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

import java.sql.Connection;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfoDataBase;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
import com.esferalia.aon.jooq.tables.records.PayMethodRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RbankRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;

public class JooqEmployee {

	private static Settings SETTINGS = null;

	public static EmployeeInfoDataBase getEmployeeInfo(Connection conn, Integer contract) {
		return getEmployeeInfoDB(DSL.using(conn, getDefaultSettings()), contract);
	}
	
	public static EmployeeInfoDataBase setEmployeeInfo(Connection conn, EmployeeInfoDataBase newEmployeeInfo) {
		return setEmployeeInfoDB(DSL.using(conn, getDefaultSettings()), newEmployeeInfo);
	}
	
	public static EmployeeContractInfo createEmployeeContract(Connection conn, EmployeeContractInfo employeeContractData) {
		return createEmployeeContractDB(DSL.using(conn, getDefaultSettings()), employeeContractData);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	

	private static EmployeeInfoDataBase getEmployeeInfoDB(DSLContext dslContext, Integer contract) {
		
		// ------------------------------------------------ EMPLOYEE INFO ---------------------------------------------------------
		EmployeeInfoDataBase employee = new EmployeeInfoDataBase();
		
		//PERSON TABLE
		Record personTable = dslContext.select().from(PERSON)
				.where(PERSON.REGISTRY.eq(
						dslContext.select(CONTRACT.PERSON).from(CONTRACT)
							.where(CONTRACT.ID.eq(contract))))
				.fetchOne();
	
		employee.setPerson_table_id(personTable.get(PERSON.REGISTRY));
		employee.setDomain(personTable.get(PERSON.DOMAIN));
		employee.setBirth_date(personTable.get(PERSON.BIRTH_DATE));
		employee.setGender(personTable.get(PERSON.GENDER));
		employee.setSocial_security_num(personTable.get(PERSON.SOCIAL_SECURITY_NUM));
		employee.setName(personTable.get(PERSON.NAME));
		employee.setFirst_surname(personTable.get(PERSON.FIRST_SURNAME));
		employee.setSecond_surname(personTable.get(PERSON.SECOND_SURNAME));
		
		
		Integer employee_registry = personTable.get(PERSON.REGISTRY);
		
		//REGISTRY TABLE
		Record registryTable = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(employee_registry))
				.fetchOne();
		
		employee.setRegistry_table_id(employee_registry);
		employee.setDocument(registryTable.get(REGISTRY.DOCUMENT));
		employee.setDocument_type(registryTable.get(REGISTRY.DOCUMENT_TYPE)); //Esto lo saco con el formato del documento, �se podria obviar?
		employee.setNationality(registryTable.get(REGISTRY.NATIONALITY));
		
		//RADDRESS AND GEOZONE TABLE
		Record raddressTable = dslContext.select().from(RADDRESS)
				.where(RADDRESS.REGISTRY.eq(employee_registry))
				.fetchOne();
		
		employee.setRaddress_table_id(raddressTable.get(RADDRESS.ID));
		employee.setStreetType(raddressTable.get(RADDRESS.STREET_TYPE));
		employee.setAddress(raddressTable.get(RADDRESS.ADDRESS));
		employee.setAddress_number(raddressTable.get(RADDRESS.NUMBER));
		employee.setZip_code(raddressTable.get(RADDRESS.ZIP));
		employee.setLocality(raddressTable.get(RADDRESS.CITY));
		
		Integer raddress_geozone = raddressTable.get(RADDRESS.GEOZONE);
		if(null == raddress_geozone) {
			employee.setGeozone_table_id(null);
			employee.setProvince(null);
			employee.setGeozone_name(null);
		}else {
			Record geozoneTable = dslContext.select().from(GEOZONE)
					.where(GEOZONE.ID.eq(raddress_geozone))
					.fetchOne();
			
			employee.setGeozone_table_id(geozoneTable.get(GEOZONE.ID));
			employee.setProvince(geozoneTable.get(GEOZONE.NAME));
			employee.setGeozone_name(geozoneTable.get(GEOZONE.NAME));
		}
		
		//RMEDIA TABLE
		Result<Record> rmediaTable = dslContext.select().from(RMEDIA)
				.where(RMEDIA.REGISTRY.eq(employee_registry))
				.fetch();
		
		for(Record record : rmediaTable){
			if(record.get(RMEDIA.MEDIA) == 1){
				employee.setRmedia_table_phone_id(record.get(RMEDIA.ID));
				employee.setPhone(record.get(RMEDIA.VALUE));
			}else if(record.get(RMEDIA.MEDIA) == 2){
				employee.setRmedia_table_mobile_id(record.get(RMEDIA.ID));
				employee.setMobile(record.get(RMEDIA.VALUE));
			}else if(record.get(RMEDIA.MEDIA) == 4){
				employee.setRmedia_table_email_id(record.get(RMEDIA.ID));
				employee.setEmail(record.get(RMEDIA.VALUE));
			}
		}
		
		//PAYMETHOD TABLE (can be null)
		Record payMethodTable = dslContext.select().from(PAY_METHOD)
			.where(PAY_METHOD.ID.in(
				dslContext.select(RPAYMETHOD.PAY_METHOD)
					.from(RPAYMETHOD)
					.where(RPAYMETHOD.REGISTRY.eq(employee_registry))
			))
			.fetchOne();
		
		if(null == payMethodTable) {
			employee.setTypePayMethod(null);
			employee.setPayMethodTableId(null);
			
			employee.setRBankTableId(null);
			employee.setBankAccount(null);
			employee.setBIC(null);
		}else{
			employee.setTypePayMethod(payMethodTable.get(PAY_METHOD.NAME));
			employee.setPayMethodTableId(payMethodTable.get(PAY_METHOD.ID));
			
			Record rBankTable = dslContext.select().from(RBANK)
					.where(RBANK.ID.in(
						dslContext.select(RPAYMETHOD.RBANK)
							.from(RPAYMETHOD)
							.where(RPAYMETHOD.REGISTRY.eq(employee_registry))
					))
					.fetchOne();
			
			if(null == rBankTable){
				employee.setRBankTableId(null);
				employee.setBankAccount(null);
				employee.setBIC(null);
				
			}else {
				employee.setRBankTableId(rBankTable.get(RBANK.ID));
				employee.setBankAccount(rBankTable.get(RBANK.BANK_ACCOUNT));
				employee.setBIC(rBankTable.get(RBANK.BIC));
			}
			
		}
		
		
		// ------------------------------------------------ CONTRACT INFO ---------------------------------------------------------
		System.out.println("******************************* CONTRACT = "+contract+" *******************************");
		
		//CONTRACT TABLE
		Record contractTable = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contract))
				.fetchOne();
		
		employee.setContract_table_id(contract);
		employee.setStart_date(contractTable.get(CONTRACT.START_DATE));
		employee.setEnd_date(contractTable.get(CONTRACT.END_DATE));
		employee.setSeniority_date(contractTable.get(CONTRACT.SENIORITY_DATE));
		employee.setCategory_description(contractTable.get(CONTRACT.CATEGORY_DESCRIPTION));
		employee.setSSRegime(contractTable.get(CONTRACT.SS_REGIME));
		
		Integer employe_workplace_table_id = contractTable.get(CONTRACT.WORKPLACE);
		
		//WORKPLACE TABLE
		Record workplaceTable = dslContext.select().from(WORKPLACE)
				.where(WORKPLACE.ID.eq(employe_workplace_table_id))
				.fetchOne();
		
		employee.setWorkplace_table_id(employe_workplace_table_id);
		employee.setWorkplace(workplaceTable.get(WORKPLACE.DESCRIPTION));
		
		Integer workplaceEnterprise = workplaceTable.get(WORKPLACE.ENTERPRISE);
		
		Result<Record> workplaceTableRecords = dslContext.select().from(WORKPLACE)
				.where(WORKPLACE.ENTERPRISE.eq(workplaceEnterprise))
				.fetch();
		
		Map<Integer, String> workplaces = new HashMap<Integer, String>();
		
		for(Record r : workplaceTableRecords)
			workplaces.put(r.get(WORKPLACE.ID), r.get(WORKPLACE.DESCRIPTION));
		
		employee.setWorkplaces(workplaces);
		
		if(employee.getSSRegime() != 3){ //NO ES RETA
			//ENTERPRISE ACTIVITY TABLE
			Integer enterpriseActivity = contractTable.get(CONTRACT.ENTERPRISE_ACTIVITY);
			
			if(null == enterpriseActivity) {
				employee.setEnterprise_activity_table_id(null);
				employee.setEnterprise_activity(null);
			}else {
				Record enterpriseActivityTable = dslContext.select().from(ENTERPRISE_ACTIVITY)
						.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseActivity))
						.fetchOne();
				
				employee.setEnterprise_activity_table_id(enterpriseActivityTable.get(ENTERPRISE_ACTIVITY.ID));
				employee.setEnterprise_activity(enterpriseActivityTable.get(ENTERPRISE_ACTIVITY.DESCRIPTION));
			}
			
			//ENTERPRISE CCC TABLE
			Integer enterpriseCCC = contractTable.get(CONTRACT.ENTERPRISE_CCC);
			
			if(null == enterpriseCCC) {
				employee.setQuote_account(null);
				employee.setEnterprise_ccc_table_id(null);
			}else {
				Record enterpriseCCCTable = dslContext.select().from(ENTERPRISE_CCC)
						.where(ENTERPRISE_CCC.ID.eq(enterpriseCCC))
						.fetchOne();
				
				employee.setEnterprise_ccc_table_id(enterpriseCCCTable.get(ENTERPRISE_CCC.ID));
				employee.setQuote_account(enterpriseCCCTable.get(ENTERPRISE_CCC.CCC)); //Can be null
			}
			
			//ENTERPRISE ACTIVITIES-CCC
			Result<Record> enterpriseActivityRecords = dslContext.select().from(ENTERPRISE_ACTIVITY)
					.where(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(workplaceEnterprise))
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
					if(null != geozoneId){
						Record geozoneRecord = dslContext.select().from(GEOZONE)
								.where(GEOZONE.ID.eq(geozoneId))
								.fetchOne();
						
						geozoneName = geozoneRecord.get(GEOZONE.NAME);
					}
					employee.addCCC(d.get(ENTERPRISE_CCC.ID), d.get(ENTERPRISE_CCC.CCC), d.get(ENTERPRISE_CCC.TYPE), geozoneName, r.get(ENTERPRISE_ACTIVITY.ID));
				}
			}
			
			employee.setEnterpriseActivities(activities);
		}
		
		//AGREEMENT LEVEL TABLE
		Integer agreementLevel = contractTable.get(CONTRACT.AGREEMENT_LEVEL);
		
		if(null == agreementLevel) {
			employee.setAgreement_level_table_id(null);
			employee.setAgreement_level(null);
			employee.setAgreement_table_id(null);
			employee.setAgreement(null);
		}else {
			Record agreementLevelTable = dslContext.select().from(AGREEMENT_LEVEL)
					.where(AGREEMENT_LEVEL.ID.eq(agreementLevel))
					.fetchOne();
			
			employee.setAgreement_level_table_id(agreementLevelTable.get(AGREEMENT_LEVEL.ID));
			employee.setAgreement_level(agreementLevelTable.get(AGREEMENT_LEVEL.DESCRIPTION)); //Can be null
			
			//AGREEMENT TABLE
			Integer agreement = agreementLevelTable.get(AGREEMENT_LEVEL.AGREEMENT);
			
			Record agreementTable = dslContext.select().from(AGREEMENT)
					.where(AGREEMENT.ID.eq(agreement))
					.fetchOne();
			
			employee.setAgreement_table_id(agreementTable.get(AGREEMENT.ID));
			employee.setAgreement(agreementTable.get(AGREEMENT.DESCRIPTION)); //Can be null	
			
		}
		
		//FECHA ACTUAL
		java.util.Date actualJavaDate = new java.util.Date();
		Date actualSQLDate = new Date(actualJavaDate.getTime());
		
		//CONTRACT DATA TABLE
		Result<Record> contractDataTable = dslContext.select().from(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contract))
			.and(CONTRACT_DATA.START_DATE.le(actualSQLDate))
			.and(CONTRACT_DATA.END_DATE.ge(actualSQLDate)
					.or(CONTRACT_DATA.END_DATE.isNull()))
			.fetch();
		
		Map<String, String> contractDataMap = new HashMap<>();
		
		for(Record r : contractDataTable){
			contractDataMap.put(r.get(CONTRACT_DATA.NAME), r.get(CONTRACT_DATA.EXPRESSION));
			
			if(r.get(CONTRACT_DATA.NAME).equals("TC2"))
				employee.setContract_data_table_type_id(r.get(CONTRACT_DATA.ID));
			else if(r.get(CONTRACT_DATA.NAME).equals("GRUPO_COTIZACION"))
				employee.setContract_data_table_quote_group_id(r.get(CONTRACT_DATA.ID));
			else if(r.get(CONTRACT_DATA.NAME).equals("OCUPACION"))
				employee.setContract_data_table_ocupation_id(r.get(CONTRACT_DATA.ID));
			else if(r.get(CONTRACT_DATA.NAME).equals("TIEMPO_COMPLETO"))
				employee.setContract_data_table_journey_type_id(r.get(CONTRACT_DATA.ID));
		}
		
		employee.setContract_data(contractDataMap);
		
		//CONTRACT INFO TABLE
		Record contractInfoTable = dslContext.select().from(CONTRACT_INFO)
			.where(CONTRACT_INFO.CONTRACT.eq(contract))
			.and(CONTRACT_INFO.NAME.eq("OPCION_CONTRATO"))
			.fetchOne();
		
		if(null == contractInfoTable) {
			employee.setContract_info_table_id(null);
			employee.setContract_model(null);
		}else if(null == contractInfoTable.get(CONTRACT_INFO.EXPRESSION)){
			employee.setContract_info_table_id(contractInfoTable.get(CONTRACT_INFO.ID));
			employee.setContract_model(null);
		}else {
			String contractType = contractInfoTable.get(CONTRACT_INFO.EXPRESSION);
			
			if(contractType.contains("\""))
				contractType = contractType.split("\"")[1];
			
			Integer ordinal = ModelOption.valueOf(contractType).ordinal();
			
			employee.setContract_info_table_id(contractInfoTable.get(CONTRACT_INFO.ID));
			employee.setContract_model(ordinal);	
		}
		
		
		System.out.println(
				"------------------- CONTRACT INFO ------------------- \n" +
				"Contrato =" + contract + "\n" +
				"Actividad = " + employee.getEnterprise_activity() + "\n" +
				"Cuenta de cotizacion = " + employee.getQuote_account() + "\n" +
				"Tipo de contrato = " + employee.getContract_type() + "\n" +
				"Modalidad = " + employee.getContract_model() + "\n" +
				"Fecha inicio = " + employee.getStart_date() + "\n" +
				"Fecha fin = " + employee.getEnd_date() + "\n" +
				"Fecha antiguedad = " + employee.getSeniority_date() + "\n" +
				"Convenio = " + employee.getAgreement() + "\n" +
				"Nivel/Categoria = " + employee.getAgreement_level() + "\n" +
				"Mostrar como = " + employee.getCategory_description() + "\n" +
				"Grupo de cotizacion = " + employee.getQuote_group() + "\n" +
				"Ocupacion = " + employee.getOcupation() + "\n" +
				"Tiempo completo = " + employee.getJourneyType() + "\n" +
				"\n" +
				"------------------- EMPLOYEE INFO ------------------- \n" +
				"Documento = " + employee.getDocument() + "\n" +
				"Tipo Documento = " + employee.getDocument_type() + "\n" +
				"Nacionalidad = " + employee.getNationality() + "\n" +
				"Nombre = " + employee.getName() + "\n" +
				"1er Apellido = " + employee.getFirst_surname() + "\n" +
				"2� Apellido = " + employee.getSecond_surname() + "\n" +
				"Fecha Nacimiento = " + employee.getBirth_date() + "\n" +
				"Genero = " + employee.getGender() + "\n" +
				"N� SS = " + employee.getSocial_security_num() + "\n" +
				"Direccion = " + employee.getAddress() + "\n" +
				"Numero = " + employee.getAddress_number() + "\n" +
				"Provincia = " + employee.getProvince() + "\n" +
				"Provincia2 = " + employee.getGeozone_name() + "\n" +
				"Localidad = " + employee.getLocality() + "\n" +
				"CP = " + employee.getZip_code() + "\n" +
				"Telefono = " + employee.getPhone() + "\n" +
				"Movil = " + employee.getMobile() + "\n" +
				"Email = " + employee.getEmail() + "\n" 
		);
		
		return employee;
	}
	
	private static EmployeeInfoDataBase setEmployeeInfoDB(DSLContext dslContext, EmployeeInfoDataBase newEmployeeInfo) {
		
		// ------------------------------------------------------------------------------------------------------------------------
		// ------------------------------------------------ EMPLOYEE INFO ---------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------------------------
		
		dslContext.update(PERSON)
			.set(PERSON.BIRTH_DATE, (null == newEmployeeInfo.getBirth_date()) ? null : new Date(newEmployeeInfo.getBirth_date().getTime()))
			.set(PERSON.GENDER, newEmployeeInfo.getGender())
			.set(PERSON.SOCIAL_SECURITY_NUM, ("" == newEmployeeInfo.getSocial_security_num()) ? null : newEmployeeInfo.getSocial_security_num())
			.set(PERSON.NAME, ("" == newEmployeeInfo.getName()) ? null : newEmployeeInfo.getName())
			.set(PERSON.FIRST_SURNAME, ("" == newEmployeeInfo.getFirst_surname()) ? null : newEmployeeInfo.getFirst_surname())
			.set(PERSON.SECOND_SURNAME, ("" == newEmployeeInfo.getSecond_surname()) ? null : newEmployeeInfo.getSecond_surname())
			.where(PERSON.REGISTRY.eq(newEmployeeInfo.getPerson_table_id()))
			.execute();
		
		dslContext.update(REGISTRY)
			.set(REGISTRY.DOCUMENT, ("" == newEmployeeInfo.getDocument()) ? null : newEmployeeInfo.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE, newEmployeeInfo.getDocument_type())
			.set(REGISTRY.DOCUMENT_COUNTRY, newEmployeeInfo.getNationality())
			.set(REGISTRY.NATIONALITY, newEmployeeInfo.getNationality())
			.set(REGISTRY.NAME, newEmployeeInfo.getFirst_surname() + " " + newEmployeeInfo.getSecond_surname() + ", " + newEmployeeInfo.getName())
			.where(REGISTRY.ID.eq(newEmployeeInfo.getPerson_table_id()))
			.execute();
		
		Record1<Integer> geozone = dslContext.select(GEOZONE.ID)
				.from(GEOZONE)
				.where(GEOZONE.NAME.eq(newEmployeeInfo.getProvince()))
					.and(GEOZONE.DOMAIN.eq(newEmployeeInfo.getDomain()))
				.fetchOne();
		
		Integer geozoneId = null;
		
		if(geozone == null){
			Result<Record1<String>> codes = dslContext.select(GEOZONE.CODE)
				.from(GEOZONE)
				.where(GEOZONE.NAME.like(newEmployeeInfo.getProvince()+"%"))
				.fetch();
			
			if(codes.isEmpty()) {
				geozoneId = null;
			}else {
				GeozoneRecord geozoneRecord  = dslContext.insertInto(GEOZONE)
						.set(GEOZONE.DOMAIN, newEmployeeInfo.getDomain())
						.set(GEOZONE.NAME, newEmployeeInfo.getGeozone_name())
						.set(GEOZONE.CODE, codes.get(0).value1())
						.returning(GEOZONE.ID)
						.fetchOne();
					
				geozoneId = geozoneRecord.getId();
			}
			
		}else
			geozoneId = geozone.value1();
		
		dslContext.update(RADDRESS)
			.set(RADDRESS.STREET_TYPE, newEmployeeInfo.getStreetType())
			.set(RADDRESS.ADDRESS, ("" == newEmployeeInfo.getAddress()) ? null : newEmployeeInfo.getAddress())
			.set(RADDRESS.NUMBER, ("" == newEmployeeInfo.getAddress_number()) ? null : newEmployeeInfo.getAddress_number())
			.set(RADDRESS.ZIP, ("" == newEmployeeInfo.getZip_code()) ? null : newEmployeeInfo.getZip_code())
			.set(RADDRESS.CITY, ("" == newEmployeeInfo.getLocality()) ? null : newEmployeeInfo.getLocality())
			.set(RADDRESS.GEOZONE, geozoneId)
			.where(RADDRESS.ID.eq(newEmployeeInfo.getRaddress_table_id()))
			.execute();
		
		if(geozone == null && null != geozoneId){
			Record geotreeParent = dslContext.select()
									.from(GEOTREE)
									.where(GEOTREE.DOMAIN.eq(newEmployeeInfo.getDomain()))
									.and(GEOTREE.PARENT.isNull())
									.fetchOne();
			
			Integer geozoneParentId = 0;
			
			if(geotreeParent == null){
				GeozoneRecord geozoneParentRecord  = dslContext.insertInto(GEOZONE)
						.set(GEOZONE.DOMAIN, newEmployeeInfo.getDomain())
						.set(GEOZONE.NAME, "ESPAÑA")
						.set(GEOZONE.CODE, "ES")
						.set(GEOZONE.SYSTEM, (byte) 1)
						.returning(GEOZONE.ID)
						.fetchOne();
				
				geozoneParentId = geozoneParentRecord.getId();
			}else{
				geozoneParentId = geotreeParent.get(GEOTREE.CHILD);
			}
			
			
			dslContext.insertInto(GEOTREE)
			.set(GEOTREE.DOMAIN, newEmployeeInfo.getDomain())
			.set(GEOTREE.PARENT, geozoneParentId)
			.set(GEOTREE.CHILD, geozoneId)
			.execute();
			
			if(geotreeParent == null){
				dslContext.insertInto(GEOTREE)
				.set(GEOTREE.DOMAIN, newEmployeeInfo.getDomain())
				.set(GEOTREE.PARENT, (Integer) null)
				.set(GEOTREE.CHILD, geozoneParentId)
				.execute();
			}
		}
		
		if(newEmployeeInfo.getPhone() != ""){
			if(newEmployeeInfo.getRmedia_table_phone_id() != null) //UPDATE
				dslContext.update(RMEDIA)
					.set(RMEDIA.VALUE, newEmployeeInfo.getPhone())
					.where(RMEDIA.ID.eq(newEmployeeInfo.getRmedia_table_phone_id()))
					.execute();
			else //INSERT
				dslContext.insertInto(RMEDIA)
					.set(RMEDIA.DOMAIN, newEmployeeInfo.getDomain())
					.set(RMEDIA.REGISTRY, newEmployeeInfo.getPerson_table_id())
					.set(RMEDIA.MEDIA, (byte) 1)
					.set(RMEDIA.VALUE, newEmployeeInfo.getPhone())
					.set(RMEDIA.RADDRESS, newEmployeeInfo.getRaddress_table_id())
					.execute();
		}
		
		if(newEmployeeInfo.getMobile() != ""){
			if(newEmployeeInfo.getRmedia_table_mobile_id() != null) //UPDATE
				dslContext.update(RMEDIA)
					.set(RMEDIA.VALUE, newEmployeeInfo.getMobile())
					.where(RMEDIA.ID.eq(newEmployeeInfo.getRmedia_table_mobile_id()))
					.execute();
			else //INSERT
				dslContext.insertInto(RMEDIA)
					.set(RMEDIA.DOMAIN, newEmployeeInfo.getDomain())
					.set(RMEDIA.REGISTRY, newEmployeeInfo.getPerson_table_id())
					.set(RMEDIA.MEDIA, (byte) 2)
					.set(RMEDIA.VALUE, newEmployeeInfo.getMobile())
					.set(RMEDIA.RADDRESS, newEmployeeInfo.getRaddress_table_id())
					.execute();
		}
		
		if(newEmployeeInfo.getEmail() != ""){
			if(newEmployeeInfo.getRmedia_table_email_id() != null) //UPDATE
				dslContext.update(RMEDIA)
					.set(RMEDIA.VALUE, newEmployeeInfo.getEmail())
					.where(RMEDIA.ID.eq(newEmployeeInfo.getRmedia_table_email_id()))
					.execute();
			else //INSERT
				dslContext.insertInto(RMEDIA)
					.set(RMEDIA.DOMAIN, newEmployeeInfo.getDomain())
					.set(RMEDIA.REGISTRY, newEmployeeInfo.getPerson_table_id())
					.set(RMEDIA.MEDIA, (byte) 4)
					.set(RMEDIA.VALUE, newEmployeeInfo.getEmail())
					.set(RMEDIA.RADDRESS, newEmployeeInfo.getRaddress_table_id())
					.execute();
		}
		
		if(-1 != getType(newEmployeeInfo.getTypePayMethod())) {
			if(newEmployeeInfo.getRBankTableId() == null && newEmployeeInfo.getPayMethodTableId() == null){
				if(newEmployeeInfo.getTypePayMethod() != "" && -1 != getType(newEmployeeInfo.getTypePayMethod())){
					byte typePayMethod = getType(newEmployeeInfo.getTypePayMethod());
					PayMethodRecord payMethodRecord = dslContext.insertInto(PAY_METHOD)
							.set(PAY_METHOD.DOMAIN, newEmployeeInfo.getDomain())
							.set(PAY_METHOD.NAME, newEmployeeInfo.getTypePayMethod())
							.set(PAY_METHOD.TYPE, typePayMethod)
							.returning(PAY_METHOD.ID)
							.fetchOne();
					
					Integer payMethodTableId = payMethodRecord.get(PAY_METHOD.ID);
					Integer rbankTableId = null;
					if(newEmployeeInfo.getBankAccount() != ""){
						RbankRecord rbankRecord = dslContext.insertInto(RBANK)
								.set(RBANK.DOMAIN, newEmployeeInfo.getDomain())
								.set(RBANK.REGISTRY, newEmployeeInfo.getRegistry_table_id())
								.set(RBANK.BANK_ACCOUNT, newEmployeeInfo.getBankAccount())
								.set(RBANK.BIC, ("" == newEmployeeInfo.getBIC()) ? null : newEmployeeInfo.getBIC())
								.set(RBANK.ALIAS, "CUENTA")
								.set(RBANK.ACTIVE, (byte) 1)
								.returning(RBANK.ID)
								.fetchOne();
						 
						 rbankTableId = rbankRecord.get(RBANK.ID); 
					}
					
					dslContext.insertInto(RPAYMETHOD)
						.set(RPAYMETHOD.DOMAIN, newEmployeeInfo.getDomain())
						.set(RPAYMETHOD.REGISTRY, newEmployeeInfo.getRegistry_table_id())
						.set(RPAYMETHOD.PAY_METHOD, payMethodTableId)
						.set(RPAYMETHOD.RBANK, rbankTableId)
						.execute();
				}
			}
			
			if(newEmployeeInfo.getRBankTableId() != null){
				dslContext.update(RBANK)
				.set(RBANK.BANK_ACCOUNT, newEmployeeInfo.getBankAccount())
				.set(RBANK.BIC, ("" == newEmployeeInfo.getBIC()) ? null : newEmployeeInfo.getBIC())
				.where(RBANK.ID.eq(newEmployeeInfo.getRBankTableId()))
				.execute();
			}else{
				if(newEmployeeInfo.getBankAccount() != "" && newEmployeeInfo.getBIC() != ""){
					RbankRecord rbankRecord = dslContext.insertInto(RBANK)
							.set(RBANK.DOMAIN, newEmployeeInfo.getDomain())
							.set(RBANK.REGISTRY, newEmployeeInfo.getRegistry_table_id())
							.set(RBANK.BANK_ACCOUNT, newEmployeeInfo.getBankAccount())
							.set(RBANK.BIC, newEmployeeInfo.getBIC())
							.set(RBANK.ALIAS, "CUENTA")
							.set(RBANK.ACTIVE, (byte) 1)
							.returning(RBANK.ID)
							.fetchOne();
						
						Record rpayMethodTableRecord = dslContext.select()
								.from(RPAYMETHOD)
								.where(RPAYMETHOD.REGISTRY.eq(newEmployeeInfo.getRegistry_table_id()))
								.fetchOne();
						
						dslContext.update(RPAYMETHOD)
						.set(RPAYMETHOD.RBANK, rbankRecord.get(RBANK.ID))
						.where(RPAYMETHOD.ID.eq(rpayMethodTableRecord.get(RPAYMETHOD.ID)))
						.execute();
				}	
			}
			
			if(newEmployeeInfo.getPayMethodTableId() != null && -1 != getType(newEmployeeInfo.getTypePayMethod())){
				byte typePayMethod = getType(newEmployeeInfo.getTypePayMethod());
				dslContext.update(PAY_METHOD)
					.set(PAY_METHOD.NAME, newEmployeeInfo.getTypePayMethod())
					.set(PAY_METHOD.TYPE, typePayMethod)
					.where(PAY_METHOD.ID.eq(newEmployeeInfo.getPayMethodTableId()))
					.execute();
			}
		}
		
		// ------------------------------------------------------------------------------------------------------------------------
		// ------------------------------------------------ CONTRACT INFO ---------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------------------------
		
		Record contractTable = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(newEmployeeInfo.getContract_table_id())).fetchOne();
		
		Date oldStartDate = contractTable.get(CONTRACT.START_DATE);
		Date oldEndDate = contractTable.get(CONTRACT.END_DATE);
		
		dslContext.update(CONTRACT)
			.set(CONTRACT.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
			.set(CONTRACT.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.set(CONTRACT.SENIORITY_DATE, (null == newEmployeeInfo.getSeniority_date()) ? new Date(newEmployeeInfo.getStart_date().getTime()) : new Date(newEmployeeInfo.getSeniority_date().getTime()))
			.set(CONTRACT.CATEGORY_DESCRIPTION, ("" == newEmployeeInfo.getCategory_description()) ? null : newEmployeeInfo.getCategory_description())
			.set(CONTRACT.AGREEMENT_LEVEL, (null == newEmployeeInfo.getAgreement_level_table_id()) ? null : newEmployeeInfo.getAgreement_level_table_id())
			.set(CONTRACT.ENTERPRISE_CCC, newEmployeeInfo.getEnterprise_ccc_table_id())
			.set(CONTRACT.ENTERPRISE_ACTIVITY, newEmployeeInfo.getEnterprise_activity_table_id())
			.set(CONTRACT.WORKPLACE, newEmployeeInfo.getWorkplace_table_id())
			.where(CONTRACT.ID.eq(newEmployeeInfo.getContract_table_id()))
			.execute();
		
		if(newEmployeeInfo.getSSRegime() != 3){ //NO ES RETA
		
			if(newEmployeeInfo.getContract_data_table_type_id() != null){
				dslContext.update(CONTRACT_DATA)
				.set(CONTRACT_DATA.EXPRESSION, (null == newEmployeeInfo.getContract_type()) ? null : "\""+newEmployeeInfo.getContract_type()+"\"")
				.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
				.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
				.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_type_id()))
				.execute();
			}else
				dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, newEmployeeInfo.getDomain())
				.set(CONTRACT_DATA.NAME, "TC2")
				.set(CONTRACT_DATA.CONTRACT, newEmployeeInfo.getContract_table_id())
				.set(CONTRACT_DATA.EXPRESSION, (null == newEmployeeInfo.getContract_type()) ? null : "\""+newEmployeeInfo.getContract_type()+"\"")
				.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
				.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
				.execute();
		
			if(newEmployeeInfo.getContract_info_table_id() != null){
				dslContext.update(CONTRACT_INFO)
					.set(CONTRACT_INFO.EXPRESSION, (null == newEmployeeInfo.getContract_model()) ? null : "\""+ ModelOption.values()[newEmployeeInfo.getContract_model()].toString()  +"\"")
					.set(CONTRACT_INFO.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
					.set(CONTRACT_INFO.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
					.where(CONTRACT_INFO.ID.eq(newEmployeeInfo.getContract_info_table_id()))
					.execute();
			}else if(newEmployeeInfo.getContract_model() != null)
				dslContext.insertInto(CONTRACT_INFO)
				.set(CONTRACT_INFO.DOMAIN, newEmployeeInfo.getDomain())
				.set(CONTRACT_INFO.NAME, "OPCION_CONTRATO")
				.set(CONTRACT_INFO.CONTRACT, newEmployeeInfo.getContract_table_id())
				.set(CONTRACT_INFO.EXPRESSION, ModelOption.values()[newEmployeeInfo.getContract_model()].toString())
				.set(CONTRACT_INFO.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
				.set(CONTRACT_INFO.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
				.execute();
		
			if(newEmployeeInfo.getContract_data_table_quote_group_id() != null){
				if(newEmployeeInfo.getQuote_group().contains("\""))
					dslContext.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.EXPRESSION, ("0" == newEmployeeInfo.getQuote_group()) ? null : newEmployeeInfo.getQuote_group())
					.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
					.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
					.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_quote_group_id()))
					.execute();
				else
					dslContext.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.EXPRESSION, ("0" == newEmployeeInfo.getQuote_group()) ? null : "\""+newEmployeeInfo.getQuote_group()+"\"")
					.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
					.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
					.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_quote_group_id()))
					.execute();
			}else if("0" != newEmployeeInfo.getQuote_group())
				dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, newEmployeeInfo.getDomain())
				.set(CONTRACT_DATA.NAME, "GRUPO_COTIZACION")
				.set(CONTRACT_DATA.CONTRACT, newEmployeeInfo.getContract_table_id())
				.set(CONTRACT_DATA.EXPRESSION, "\""+newEmployeeInfo.getQuote_group()+"\"")
				.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
				.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
				.execute();
		
			if(newEmployeeInfo.getContract_data_table_ocupation_id() != null){
				if(newEmployeeInfo.getOcupation().contains("\""))
					dslContext.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.EXPRESSION, ("z" == newEmployeeInfo.getOcupation()) ? null : newEmployeeInfo.getOcupation())
					.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
					.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
					.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_ocupation_id()))
					.execute();
				else
					dslContext.update(CONTRACT_DATA)
						.set(CONTRACT_DATA.EXPRESSION, ("z" == newEmployeeInfo.getOcupation()) ? null : "\""+newEmployeeInfo.getOcupation()+"\"")
						.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
						.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
						.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_ocupation_id()))
						.execute();
			}else if("z" != newEmployeeInfo.getOcupation())
				dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, newEmployeeInfo.getDomain())
				.set(CONTRACT_DATA.NAME, "OCUPACION")
				.set(CONTRACT_DATA.CONTRACT, newEmployeeInfo.getContract_table_id())
				.set(CONTRACT_DATA.EXPRESSION, "\""+newEmployeeInfo.getOcupation()+"\"")
				.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
				.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
				.execute();	
		}
		
		if(newEmployeeInfo.getSSRegime() == 3){ //ES RETA
		
			if(newEmployeeInfo.getContract_data_table_journey_type_id() != null){
				dslContext.update(CONTRACT_DATA)
				.set(CONTRACT_DATA.EXPRESSION, newEmployeeInfo.getJourneyType().toString())
				.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
				.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
				.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_journey_type_id()))
				.execute();
			}else{
				dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, newEmployeeInfo.getDomain())
				.set(CONTRACT_DATA.NAME, "TIEMPO_COMPLETO")
				.set(CONTRACT_DATA.CONTRACT, newEmployeeInfo.getContract_table_id())
				.set(CONTRACT_DATA.EXPRESSION, (null == newEmployeeInfo.getJourneyType()) ? null : newEmployeeInfo.getJourneyType().toString())
				.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
				.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
				.execute();
			}
		
		}
		
		//ACTUALIZAR FECHA INICIO Y FIN: contract, contract_data, contract_info, contract_bonus, contract_deduction, contract_embargo,
		// contract_leave, contract_payment
		
		dslContext.update(CONTRACT)
			.set(CONTRACT.START_DATE,  new Date(newEmployeeInfo.getStart_date().getTime()))
			.where(CONTRACT.ID.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT.START_DATE.eq(oldStartDate))
			.execute();
		
		dslContext.update(CONTRACT)
			.set(CONTRACT.END_DATE,  (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.where(CONTRACT.ID.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT.END_DATE.eq(oldEndDate))
			.execute();
		
		dslContext.update(CONTRACT_DATA)
			.set(CONTRACT_DATA.START_DATE,  new Date(newEmployeeInfo.getStart_date().getTime()))
			.where(CONTRACT_DATA.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_DATA.START_DATE.eq(oldStartDate))
			.execute();
	
		dslContext.update(CONTRACT_DATA)
			.set(CONTRACT_DATA.END_DATE,  (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.where(CONTRACT_DATA.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_DATA.END_DATE.eq(oldEndDate))
			.execute();
		
		dslContext.update(CONTRACT_INFO)
			.set(CONTRACT_INFO.START_DATE,  new Date(newEmployeeInfo.getStart_date().getTime()))
			.where(CONTRACT_INFO.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_INFO.START_DATE.eq(oldStartDate))
			.execute();

		dslContext.update(CONTRACT_INFO)
			.set(CONTRACT_INFO.END_DATE,  (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.where(CONTRACT_INFO.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_INFO.END_DATE.eq(oldEndDate))
			.execute();

		dslContext.update(CONTRACT_BONUS)
			.set(CONTRACT_BONUS.START_DATE,  new Date(newEmployeeInfo.getStart_date().getTime()))
			.where(CONTRACT_BONUS.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_BONUS.START_DATE.eq(oldStartDate))
			.execute();

		dslContext.update(CONTRACT_BONUS)
			.set(CONTRACT_BONUS.END_DATE,  (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.where(CONTRACT_BONUS.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_BONUS.END_DATE.eq(oldEndDate))
			.execute();

		dslContext.update(CONTRACT_DEDUCTION)
			.set(CONTRACT_DEDUCTION.START_DATE,  new Date(newEmployeeInfo.getStart_date().getTime()))
			.where(CONTRACT_DEDUCTION.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_DEDUCTION.START_DATE.eq(oldStartDate))
			.execute();

		dslContext.update(CONTRACT_DEDUCTION)
			.set(CONTRACT_DEDUCTION.END_DATE,  (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.where(CONTRACT_DEDUCTION.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_DEDUCTION.END_DATE.eq(oldEndDate))
			.execute();

		dslContext.update(CONTRACT_EMBARGO)
			.set(CONTRACT_EMBARGO.START_DATE,  new Date(newEmployeeInfo.getStart_date().getTime()))
			.where(CONTRACT_EMBARGO.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_EMBARGO.START_DATE.eq(oldStartDate))
			.execute();

		dslContext.update(CONTRACT_EMBARGO)
			.set(CONTRACT_EMBARGO.END_DATE,  (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.where(CONTRACT_EMBARGO.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_EMBARGO.END_DATE.eq(oldEndDate))
			.execute();

		dslContext.update(CONTRACT_LEAVE)
			.set(CONTRACT_LEAVE.START_DATE,  new Date(newEmployeeInfo.getStart_date().getTime()))
			.where(CONTRACT_LEAVE.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_LEAVE.START_DATE.eq(oldStartDate))
			.execute();

		dslContext.update(CONTRACT_LEAVE)
			.set(CONTRACT_LEAVE.END_DATE,  (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.where(CONTRACT_LEAVE.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_LEAVE.END_DATE.eq(oldEndDate))
			.execute();

		dslContext.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.START_DATE,  new Date(newEmployeeInfo.getStart_date().getTime()))
			.where(CONTRACT_PAYMENT.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_PAYMENT.START_DATE.eq(oldStartDate))
			.execute();

		dslContext.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.END_DATE,  (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.where(CONTRACT_PAYMENT.CONTRACT.eq(newEmployeeInfo.getContract_table_id()))
			.and(CONTRACT_PAYMENT.END_DATE.eq(oldEndDate))
			.execute();
			
		
		String updateInfo = "";
		updateInfo +=
				"------------------- CONTRACT UPDATE INFO ------------------- \n" +
				"Actividad = " + newEmployeeInfo.getEnterprise_activity() + "\n" +
				"Cuenta de cotizacion = " + newEmployeeInfo.getQuote_account() + "\n" +
				"Tipo de contrato = " + newEmployeeInfo.getContract_type() + "\n";
		if(newEmployeeInfo.getContract_model() == null)
			updateInfo += "Modalidad = null \n";
//		else
//			updateInfo += "Modalidad = \"" + ModelOption.values()[newEmployeeInfo.getContract_model()].toString() + "\"\n";
				
		updateInfo +=
				"Fecha inicio = " + newEmployeeInfo.getStart_date() + "\n" +
				"Fecha fin = " + newEmployeeInfo.getEnd_date() + "\n" +
				"Fecha antiguedad = " + newEmployeeInfo.getSeniority_date() + "\n" +
				"Convenio = " + newEmployeeInfo.getAgreement() + "\n" +
				"Convenio ID = " + newEmployeeInfo.getAgreement_table_id() + "\n" +
				"Nivel/Categoria = " + newEmployeeInfo.getAgreement_level() + "\n" +
				"Nivel/Categoria ID = " + newEmployeeInfo.getAgreement_level_table_id() + "\n" +
				"Mostrar como = " + newEmployeeInfo.getCategory_description() + "\n" +
				"Grupo de cotizacion = " + newEmployeeInfo.getQuote_group() + "\n" +
				"Ocupacion = " + newEmployeeInfo.getOcupation() + "\n" +
				"Tiempo completo = " + newEmployeeInfo.getJourneyType() + "\n" +
				"\n" +
				"------------------- EMPLOYEE UPDATE INFO ------------------- \n" +
				"Documento = " + newEmployeeInfo.getDocument() + "\n" +
				"Tipo Documento = " + newEmployeeInfo.getDocument_type() + "\n" +
				"Nacionalidad = " + newEmployeeInfo.getNationality() + "\n" +
				"Nombre = " + newEmployeeInfo.getName() + "\n" +
				"1er Apellido = " + newEmployeeInfo.getFirst_surname() + "\n" +
				"2� Apellido = " + newEmployeeInfo.getSecond_surname() + "\n" +
				"Fecha Nacimiento = " + newEmployeeInfo.getBirth_date() + "\n" +
				"Genero = " + newEmployeeInfo.getGender() + "\n" +
				"N� SS = " + newEmployeeInfo.getSocial_security_num() + "\n" +
				"Direccion = " + newEmployeeInfo.getAddress() + "\n" +
				"Numero = " + newEmployeeInfo.getAddress_number() + "\n" +
				"Provincia = " + newEmployeeInfo.getProvince() + "\n" +
				"Provincia2 = " + newEmployeeInfo.getGeozone_name() + "\n" +
				"Localidad = " + newEmployeeInfo.getLocality() + "\n" +
				"CP = " + newEmployeeInfo.getZip_code() + "\n" +
				"Telefono = " + newEmployeeInfo.getPhone() + "\n" +
				"Movil = " + newEmployeeInfo.getMobile() + "\n" +
				"Email = " + newEmployeeInfo.getEmail() + "\n"; 
		
		System.out.println(updateInfo);
			
		return newEmployeeInfo;
	}

	private static byte getType(String typePayMethod) {
		switch (typePayMethod) {
		case "EFECTIVO":
			return (byte) 0;
		case "GIRO":
			return (byte) 1;
		case "CHEQUE":
			return (byte) 4;
		case "TRANSFERENCIA":
			return (byte) 5;
		default:
			return (byte) -1;
		}
	}

	private static EmployeeContractInfo createEmployeeContractDB(DSLContext dslContext, EmployeeContractInfo employeeContractData) {
		System.out.println("GUARDAR DB");
		
		// ------------------------------------------------------------------------------------------------------------------------
		// ------------------------------------------------ EMPLOYEE INFO ---------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------------------------		
		
		
		ContractInfo contractData = employeeContractData.getContractInfo();
		EmployeeInfo employeeData = employeeContractData.getEmployeeInfo();
		
		System.out.println(contractData.toString());
		System.out.println(employeeData.toString());
		
		Record workplaceRecord = dslContext.select()
									.from(WORKPLACE)
									.where(WORKPLACE.ID.eq(contractData.getWorkplaceId()))
									.fetchOne();
		
		Integer domain = workplaceRecord.get(WORKPLACE.DOMAIN);
		Integer registryId = 0;
		
//		Integer workplaceAddressId = workplaceRecord.get(WORKPLACE.ADDRESS);
		
//		Record raddressRecord = dslContext.select()
//			.from(RADDRESS)
//			.where(RADDRESS.ID.eq(workplaceAddressId))
//			.fetchOne();
		
		//Integer workplaceGeozoneId = raddressRecord.get(RADDRESS.GEOZONE);
		
		if(employeeData.getEmployeeId() == null){ //NUEVO EMPLEADO Y NUEVO CONTRATO
			
			RegistryRecord registryRecord = dslContext.insertInto(REGISTRY)
				.set(REGISTRY.DOMAIN, domain)
				.set(REGISTRY.DOCUMENT, employeeData.getDocument())
				.set(REGISTRY.DOCUMENT_TYPE, employeeData.getDocumentType())
				.set(REGISTRY.DOCUMENT_COUNTRY, employeeData.getNationality())
				.set(REGISTRY.NATIONALITY, employeeData.getNationality())
				.set(REGISTRY.NAME, employeeData.getSurName() + " " + employeeData.getSecondSurName() + ", " + employeeData.getName())
				.returning(REGISTRY.ID)
				.fetchOne();
			
			registryId = registryRecord.getId();
			
			dslContext.insertInto(PERSON)
				.set(PERSON.REGISTRY, registryId)
				.set(PERSON.DOMAIN, domain)
				.set(PERSON.NAME, employeeData.getName())
				.set(PERSON.FIRST_SURNAME, employeeData.getSurName())
				.set(PERSON.SECOND_SURNAME, employeeData.getSecondSurName())
				.set(PERSON.GENDER, employeeData.getGender())
				.set(PERSON.BIRTH_DATE, (employeeData.getBirthdate() == null) ? null : new Date(employeeData.getBirthdate().getTime()))
				.set(PERSON.SOCIAL_SECURITY_NUM, employeeData.getSsNumber())
				.execute();
			
			Record1<Integer> geozone = dslContext.select(GEOZONE.ID)
					.from(GEOZONE)
					.where(GEOZONE.NAME.eq(employeeData.getAddressProvinces()))
						.and(GEOZONE.DOMAIN.eq(domain))
					.fetchOne();
			
			Integer geozoneId = null;
			
			if(geozone == null){
				Result<Record1<String>> codes = dslContext.select(GEOZONE.CODE)
					.from(GEOZONE)
					.where(GEOZONE.NAME.like(employeeData.getAddressProvinces()+"%"))
					.fetch();
				
				if(!codes.isEmpty()){
					GeozoneRecord geozoneRecord  = dslContext.insertInto(GEOZONE)
							.set(GEOZONE.DOMAIN, domain)
							.set(GEOZONE.NAME, employeeData.getAddressProvinces())
							.set(GEOZONE.CODE, codes.get(0).value1())
							.returning(GEOZONE.ID)
							.fetchOne();
						
						geozoneId = geozoneRecord.getId();
				}
			}else
				geozoneId = geozone.value1();
			
			RaddressRecord rAddressRecord = dslContext.insertInto(RADDRESS)
				.set(RADDRESS.DOMAIN, domain)
				.set(RADDRESS.REGISTRY, registryId)
				.set(RADDRESS.STREET_TYPE, employeeData.getStreetType())
				.set(RADDRESS.ADDRESS, employeeData.getAddress())
				.set(RADDRESS.NUMBER, employeeData.getAddresNum())
				.set(RADDRESS.ZIP, employeeData.getAddressZip())
				.set(RADDRESS.CITY, employeeData.getAddressCity())
				.set(RADDRESS.GEOZONE, geozoneId)
				.returning(RADDRESS.ID, RADDRESS.GEOZONE)
				.fetchOne();
			
			Integer rAddressId = rAddressRecord.getId();
			
			if(geozone == null && null != geozoneId){
				Integer rAddressGeozone = rAddressRecord.getGeozone();
				
				GeozoneRecord geozoneParentRecord  = dslContext.insertInto(GEOZONE)
						.set(GEOZONE.DOMAIN, domain)
						.set(GEOZONE.NAME, "ESPAÑA")
						.set(GEOZONE.CODE, "ES")
						.set(GEOZONE.SYSTEM, (byte) 1)
						.returning(GEOZONE.ID)
						.fetchOne();
				
				Integer geozoneParentId = geozoneParentRecord.getId();
				
				dslContext.insertInto(GEOTREE)
				.set(GEOTREE.DOMAIN, domain)
				.set(GEOTREE.PARENT, geozoneParentId)
				.set(GEOTREE.CHILD, rAddressGeozone)
				.execute();
				
				dslContext.insertInto(GEOTREE)
				.set(GEOTREE.DOMAIN, domain)
				.set(GEOTREE.PARENT, (Integer) null)
				.set(GEOTREE.CHILD, geozoneParentId)
				.execute();
			}
			
			dslContext.insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN, domain)
				.set(RMEDIA.REGISTRY, registryId)
				.set(RMEDIA.MEDIA, (byte) 1)
				.set(RMEDIA.VALUE, employeeData.getPhone())
				.set(RMEDIA.RADDRESS, rAddressId)
				.execute();
			
			dslContext.insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN, domain)
				.set(RMEDIA.REGISTRY, registryId)
				.set(RMEDIA.MEDIA, (byte) 2)
				.set(RMEDIA.VALUE, employeeData.getMobile())
				.set(RMEDIA.RADDRESS, rAddressId)
				.execute();
			
			dslContext.insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN, domain)
				.set(RMEDIA.REGISTRY, registryId)
				.set(RMEDIA.MEDIA, (byte) 4)
				.set(RMEDIA.VALUE, employeeData.getEmail())
				.set(RMEDIA.RADDRESS, rAddressId)
				.execute();
			
			if(employeeData.getPayMethodType() != null && employeeData.getPayMethodType() != ""){
				byte typePayMethod = getType(employeeData.getPayMethodType());
				PayMethodRecord payMethodRecord = dslContext.insertInto(PAY_METHOD)
						.set(PAY_METHOD.DOMAIN, domain)
						.set(PAY_METHOD.NAME, employeeData.getPayMethodType())
						.set(PAY_METHOD.TYPE, typePayMethod)
						.returning(PAY_METHOD.ID)
						.fetchOne();
				
				Integer payMethodTableId = payMethodRecord.get(PAY_METHOD.ID);
				Integer rbankTableId = null;
				if(employeeData.getAccount() != null && employeeData.getAccount() != ""){
					RbankRecord rbankRecord = dslContext.insertInto(RBANK)
							.set(RBANK.DOMAIN, domain)
							.set(RBANK.REGISTRY, registryId)
							.set(RBANK.BANK_ACCOUNT, employeeData.getAccount())
							.set(RBANK.BIC, employeeData.getBic())
							.set(RBANK.ALIAS, "CUENTA")
							.set(RBANK.ACTIVE, (byte) 1)
							.returning(RBANK.ID)
							.fetchOne();
					 
					 rbankTableId = rbankRecord.get(RBANK.ID); 
				}
				
				dslContext.insertInto(RPAYMETHOD)
					.set(RPAYMETHOD.DOMAIN, domain)
					.set(RPAYMETHOD.REGISTRY, registryId)
					.set(RPAYMETHOD.PAY_METHOD, payMethodTableId)
					.set(RPAYMETHOD.RBANK, rbankTableId)
					.execute();
			}
			
		}else{ //EMPLEADO YA EXISTENTE Y NUEVO CONTRATO
			
			registryId = employeeData.getEmployeeId();
			
			dslContext.update(REGISTRY)
				.set(REGISTRY.DOCUMENT, employeeData.getDocument())
				.set(REGISTRY.DOCUMENT_TYPE, employeeData.getDocumentType())
				.set(REGISTRY.DOCUMENT_COUNTRY, employeeData.getNationality())
				.set(REGISTRY.NATIONALITY, employeeData.getNationality())
				.set(REGISTRY.NAME, employeeData.getSurName() + " " + employeeData.getSecondSurName() + ", " + employeeData.getName())
				.where(REGISTRY.ID.eq(registryId))
				.execute();
			
			dslContext.update(PERSON)
				.set(PERSON.NAME, employeeData.getName())
				.set(PERSON.FIRST_SURNAME, employeeData.getSurName())
				.set(PERSON.SECOND_SURNAME, employeeData.getSecondSurName())
				.set(PERSON.GENDER, employeeData.getGender())
				.set(PERSON.BIRTH_DATE, (employeeData.getBirthdate() == null) ? null : new Date(employeeData.getBirthdate().getTime()))
				.set(PERSON.SOCIAL_SECURITY_NUM, employeeData.getSsNumber())
				.where(PERSON.REGISTRY.eq(registryId))
				.execute();
			
			Record1<Integer> geozone = dslContext.select(GEOZONE.ID)
					.from(GEOZONE)
					.where(GEOZONE.NAME.eq(employeeData.getAddressProvinces()))
						.and(GEOZONE.DOMAIN.eq(domain))
					.fetchOne();
			
			Integer geozoneId = null;
			
			if(geozone == null){
				Result<Record1<String>> codes = dslContext.select(GEOZONE.CODE)
					.from(GEOZONE)
					.where(GEOZONE.NAME.like(employeeData.getAddressProvinces()+"%"))
					.fetch();
				
				GeozoneRecord geozoneRecord  = dslContext.insertInto(GEOZONE)
					.set(GEOZONE.DOMAIN, domain)
					.set(GEOZONE.NAME, employeeData.getAddressProvinces())
					.set(GEOZONE.CODE, codes.get(0).value1())
					.returning(GEOZONE.ID)
					.fetchOne();
				
				geozoneId = geozoneRecord.getId();
			}else
				geozoneId = geozone.value1();
			
//			dslContext.insertInto(RADDRESS, RADDRESS.ID, RADDRESS.DOMAIN, RADDRESS.REGISTRY, RADDRESS.TYPE, RADDRESS.RECIPIENT, 
//					 RADDRESS.STREET_TYPE, RADDRESS.ADDRESS, RADDRESS.NUMBER, RADDRESS.ADDRESS2, RADDRESS.ADDRESS3, RADDRESS.ZIP,
//					 RADDRESS.CITY, RADDRESS.GEOZONE, RADDRESS.ALIAS, RADDRESS.MUNICIPALITY_CODE)
//			 	.values(employeeData.getRaddressId(), domain, registryId, (byte) 0, (String) null, employeeData.getStreetType(), 
//			 			employeeData.getAddress(), employeeData.getAddresNum(), (String) null, (String) null, employeeData.getAddressZip(), 
//			 			employeeData.getAddressCity(), geozoneId, (String) null, (String) null)
//			 	.onDuplicateKeyUpdate()
//			 	.set(RADDRESS.DOMAIN, domain)
//				.set(RADDRESS.REGISTRY, registryId)
//				.set(RADDRESS.STREET_TYPE, employeeData.getStreetType())
//				.set(RADDRESS.ADDRESS, employeeData.getAddress())
//				.set(RADDRESS.NUMBER, employeeData.getAddresNum())
//				.set(RADDRESS.ZIP, employeeData.getAddressZip())
//				.set(RADDRESS.CITY, employeeData.getAddressCity())
//				.set(RADDRESS.GEOZONE, geozoneId)
//				.execute();
			
			
			Integer rAddressId = employeeData.getRaddressId();
			
			if(null == rAddressId){
				RaddressRecord rAddressRecord = dslContext.insertInto(RADDRESS)
						.set(RADDRESS.DOMAIN, domain)
						.set(RADDRESS.REGISTRY, registryId)
						.set(RADDRESS.STREET_TYPE, employeeData.getStreetType())
						.set(RADDRESS.ADDRESS, employeeData.getAddress())
						.set(RADDRESS.NUMBER, employeeData.getAddresNum())
						.set(RADDRESS.ZIP, employeeData.getAddressZip())
						.set(RADDRESS.CITY, employeeData.getAddressCity())
						.set(RADDRESS.GEOZONE, geozoneId)
						.returning(RADDRESS.ID, RADDRESS.GEOZONE)
						.fetchOne();
				
				rAddressId = rAddressRecord.getId();
			}else{
				dslContext.update(RADDRESS)
						.set(RADDRESS.STREET_TYPE, employeeData.getStreetType())
						.set(RADDRESS.ADDRESS, employeeData.getAddress())
						.set(RADDRESS.NUMBER, employeeData.getAddresNum())
						.set(RADDRESS.ZIP, employeeData.getAddressZip())
						.set(RADDRESS.CITY, employeeData.getAddressCity())
						.set(RADDRESS.GEOZONE, geozoneId)
						.where(RADDRESS.ID.eq(rAddressId))
						.execute();
			}
			
			if(geozone == null){
				Integer rAddressGeozone = geozoneId;
				
				GeozoneRecord geozoneParentRecord  = dslContext.insertInto(GEOZONE)
						.set(GEOZONE.DOMAIN, domain)
						.set(GEOZONE.NAME, "ESPAÑA")
						.set(GEOZONE.CODE, "ES")
						.set(GEOZONE.SYSTEM, (byte) 1)
						.returning(GEOZONE.ID)
						.fetchOne();
				
				Integer geozoneParentId = geozoneParentRecord.getId();
				
				dslContext.insertInto(GEOTREE)
				.set(GEOTREE.DOMAIN, domain)
				.set(GEOTREE.PARENT, geozoneParentId)
				.set(GEOTREE.CHILD, rAddressGeozone)
				.execute();
				
				dslContext.insertInto(GEOTREE)
				.set(GEOTREE.DOMAIN, domain)
				.set(GEOTREE.PARENT, (Integer) null)
				.set(GEOTREE.CHILD, geozoneParentId)
				.execute();
			}
			
			dslContext.insertInto(RMEDIA, RMEDIA.ID, RMEDIA.DOMAIN, RMEDIA.REGISTRY, RMEDIA.MEDIA, RMEDIA.VALUE, RMEDIA.COMMENT, 
							  RMEDIA.ADMINISTRATIVE, RMEDIA.COMMERCIAL, RMEDIA.TECHNICAL, RMEDIA.RADDRESS)
			 	.values(employeeData.getPhoneId(), domain, registryId, (byte) 1, employeeData.getPhone(), (String) null, 
			 			(byte) 1, (byte) 1, (byte) 1, rAddressId)
			 	.onDuplicateKeyUpdate()
			 	.set(RMEDIA.VALUE, employeeData.getPhone())
				.set(RMEDIA.RADDRESS, rAddressId)
				.execute();
			
//			dslContext.insertInto(RMEDIA)
//				.set(RMEDIA.DOMAIN, domain)
//				.set(RMEDIA.REGISTRY, registryId)
//				.set(RMEDIA.MEDIA, (byte) 1)
//				.set(RMEDIA.VALUE, employeeData.getPhone())
//				.set(RMEDIA.RADDRESS, rAddressId)
//				.execute();
			
			dslContext.insertInto(RMEDIA, RMEDIA.ID, RMEDIA.DOMAIN, RMEDIA.REGISTRY, RMEDIA.MEDIA, RMEDIA.VALUE, RMEDIA.COMMENT, 
					  RMEDIA.ADMINISTRATIVE, RMEDIA.COMMERCIAL, RMEDIA.TECHNICAL, RMEDIA.RADDRESS)
			 	.values(employeeData.getMobileId(), domain, registryId, (byte) 2, employeeData.getMobile(), (String) null, 
			 			(byte) 1, (byte) 1, (byte) 1, rAddressId)
			 	.onDuplicateKeyUpdate()
			 	.set(RMEDIA.VALUE, employeeData.getMobile())
				.set(RMEDIA.RADDRESS, rAddressId)
				.execute();
		
//			dslContext.insertInto(RMEDIA)
//				.set(RMEDIA.DOMAIN, domain)
//				.set(RMEDIA.REGISTRY, registryId)
//				.set(RMEDIA.MEDIA, (byte) 2)
//				.set(RMEDIA.VALUE, employeeData.getMobile())
//				.set(RMEDIA.RADDRESS, rAddressId)
//				.execute();
			
			dslContext.insertInto(RMEDIA, RMEDIA.ID, RMEDIA.DOMAIN, RMEDIA.REGISTRY, RMEDIA.MEDIA, RMEDIA.VALUE, RMEDIA.COMMENT, 
					  RMEDIA.ADMINISTRATIVE, RMEDIA.COMMERCIAL, RMEDIA.TECHNICAL, RMEDIA.RADDRESS)
			 	.values(employeeData.getEmailId(), domain, registryId, (byte) 4, employeeData.getEmail(), (String) null, 
			 			(byte) 1, (byte) 1, (byte) 1, rAddressId)
			 	.onDuplicateKeyUpdate()
			 	.set(RMEDIA.VALUE, employeeData.getEmail())
				.set(RMEDIA.RADDRESS, rAddressId)
				.execute();
		
//			dslContext.insertInto(RMEDIA)
//				.set(RMEDIA.DOMAIN, domain)
//				.set(RMEDIA.REGISTRY, registryId)
//				.set(RMEDIA.MEDIA, (byte) 4)
//				.set(RMEDIA.VALUE, employeeData.getEmail())
//				.set(RMEDIA.RADDRESS, rAddressId)
//				.execute();
			
			
			if(null == employeeData.getRpaymethodId()){
				if(employeeData.getPayMethodType() != null && employeeData.getPayMethodType() != ""){
					byte typePayMethod = getType(employeeData.getPayMethodType());
					PayMethodRecord payMethodRecord = dslContext.insertInto(PAY_METHOD)
							.set(PAY_METHOD.DOMAIN, domain)
							.set(PAY_METHOD.NAME, employeeData.getPayMethodType())
							.set(PAY_METHOD.TYPE, typePayMethod)
							.returning(PAY_METHOD.ID)
							.fetchOne();
					
					Integer payMethodTableId = payMethodRecord.get(PAY_METHOD.ID);
					Integer rbankTableId = null;
					if(employeeData.getAccount() != null && employeeData.getAccount() != ""){
						RbankRecord rbankRecord = dslContext.insertInto(RBANK)
								.set(RBANK.DOMAIN, domain)
								.set(RBANK.REGISTRY, registryId)
								.set(RBANK.BANK_ACCOUNT, employeeData.getAccount())
								.set(RBANK.BIC, employeeData.getBic())
								.set(RBANK.ALIAS, "CUENTA")
								.set(RBANK.ACTIVE, (byte) 1)
								.returning(RBANK.ID)
								.fetchOne();
						 
						 rbankTableId = rbankRecord.get(RBANK.ID); 
					}
					
					dslContext.insertInto(RPAYMETHOD)
						.set(RPAYMETHOD.DOMAIN, domain)
						.set(RPAYMETHOD.REGISTRY, registryId)
						.set(RPAYMETHOD.PAY_METHOD, payMethodTableId)
						.set(RPAYMETHOD.RBANK, rbankTableId)
						.execute();
				}
			}else{
				if("TRANSFERENCIA".equals(employeeData.getPayMethodType())){
					Integer rbankTableId = employeeData.getRbankId();
					if(null == employeeData.getRbankId() && (employeeData.getAccount() != null && employeeData.getAccount() != "")){
						RbankRecord rbankRecord = dslContext.insertInto(RBANK)
								.set(RBANK.DOMAIN, domain)
								.set(RBANK.REGISTRY, registryId)
								.set(RBANK.BANK_ACCOUNT, employeeData.getAccount())
								.set(RBANK.BIC, employeeData.getBic())
								.set(RBANK.ALIAS, "CUENTA")
								.set(RBANK.ACTIVE, (byte) 1)
								.returning(RBANK.ID)
								.fetchOne();
						 
						 rbankTableId = rbankRecord.get(RBANK.ID); 
					}else{
						dslContext.update(RBANK)
								.set(RBANK.BANK_ACCOUNT, employeeData.getAccount())
								.set(RBANK.BIC, employeeData.getBic())
								.where(RBANK.ID.eq(rbankTableId))
								.execute();
					}
					
					byte typePayMethod = getType(employeeData.getPayMethodType());
					dslContext.update(PAY_METHOD)
							.set(PAY_METHOD.NAME, employeeData.getPayMethodType())
							.set(PAY_METHOD.TYPE, typePayMethod)
							.where(PAY_METHOD.ID.eq(employeeData.getPaymethodId()))
							.execute();
				}else{
					byte typePayMethod = getType(employeeData.getPayMethodType());
					dslContext.update(PAY_METHOD)
							.set(PAY_METHOD.NAME, employeeData.getPayMethodType())
							.set(PAY_METHOD.TYPE, typePayMethod)
							.where(PAY_METHOD.ID.eq(employeeData.getPaymethodId()))
							.execute();
					
					if(null != employeeData.getRbankId()){
						dslContext.update(RPAYMETHOD)
							.set(RPAYMETHOD.RBANK, (Integer) null)
							.where(RPAYMETHOD.ID.eq(employeeData.getRpaymethodId()))
							.execute();
						
						dslContext.delete(RBANK)
							.where(RBANK.ID.eq(employeeData.getRbankId()))
							.execute();
					}
				}
			}
				
		}
		
		// ------------------------------------------------------------------------------------------------------------------------
		// ------------------------------------------------ CONTRACT INFO ---------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------------------------
		
		ContractRecord contractRecord = dslContext.insertInto(CONTRACT)
			.set(CONTRACT.DOMAIN, domain)
			.set(CONTRACT.PERSON, registryId)
			.set(CONTRACT.WORKPLACE, contractData.getWorkplaceId())
			.set(CONTRACT.START_DATE, new Date(contractData.getStartDate().getTime()))
			.set(CONTRACT.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
			.set(CONTRACT.SENIORITY_DATE, (contractData.getSeniorityDate() == null) ? new Date(contractData.getStartDate().getTime()) : new Date(contractData.getSeniorityDate().getTime()))
			.set(CONTRACT.CATEGORY_DESCRIPTION, contractData.getAgreementCategory())
			.set(CONTRACT.AGREEMENT_LEVEL, contractData.getAgreementLevelId())
			.returning(CONTRACT.ID)
			.fetchOne();
		
		Integer contractId = contractRecord.getId();
		
		if(contractData.getSsRegimen() != 3){ //NO ES RETA
			
			if(null != contractData.getActivityId())
				dslContext.update(CONTRACT)
				.set(CONTRACT.ENTERPRISE_CCC, contractData.getCccId())
				.set(CONTRACT.ENTERPRISE_ACTIVITY, contractData.getActivityId())
				.where(CONTRACT.ID.eq(contractId))
				.execute();
			
			//TODO: CODIGO PARA CREAR ACTIVIDAD Y CCC
//			Record enterpriseRecord = dslContext.select()
//				.from(ENTERPRISE)
//				.where(ENTERPRISE.DOMAIN.eq(domain))
//				.fetchOne();
//			
//			Integer enterpriseId = enterpriseRecord.get(ENTERPRISE.REGISTRY);
//			
//			Record enterpriseActivity = dslContext.select()
//					.from(ENTERPRISE_ACTIVITY)
//					.where(ENTERPRISE_ACTIVITY.DOMAIN.eq(domain))
//					.and(ENTERPRISE_ACTIVITY.DESCRIPTION.eq(employeeContractData.getEnterprise_activity()))
//					.fetchOne();
//			
//			Integer enterpiseActivityId = 0;
//			if(enterpriseActivity == null){
//				EnterpriseActivityRecord enterpriseActivityRecord = dslContext.insertInto(ENTERPRISE_ACTIVITY)
//						.set(ENTERPRISE_ACTIVITY.DOMAIN, domain)
//						.set(ENTERPRISE_ACTIVITY.DESCRIPTION, employeeContractData.getEnterprise_activity())
//						.set(ENTERPRISE_ACTIVITY.ENTERPRISE, enterpriseId)
//						.set(ENTERPRISE_ACTIVITY.TYPE, new Byte("0"))
//						.returning(ENTERPRISE_ACTIVITY.ID)
//						.fetchOne();
//					
//				enterpiseActivityId = enterpriseActivityRecord.getId();
//			}else{
//				enterpiseActivityId = enterpriseActivity.get(ENTERPRISE_ACTIVITY.ID);
//			}
//			
//			Result<Record> enterpriseCCC = dslContext.select()
//					.from(ENTERPRISE_CCC)
//					.where(ENTERPRISE_CCC.DOMAIN.eq(domain))
//					.and(ENTERPRISE_CCC.CCC.eq(employeeContractData.getQuote_account()))
//					.and(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(enterpiseActivityId))
//					.and(ENTERPRISE_CCC.GEOZONE.eq(workplaceGeozoneId))
//					.fetch();
//			
//			Integer enterpriseCccId = 0;
//			
//			if(enterpriseCCC == null || enterpriseCCC.isEmpty()){
//				EnterpriseCccRecord enterpriseCccRecord = dslContext.insertInto(ENTERPRISE_CCC)
//						.set(ENTERPRISE_CCC.DOMAIN, domain)
//						.set(ENTERPRISE_CCC.CCC, employeeContractData.getQuote_account())
//						.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, enterpiseActivityId)
//						.set(ENTERPRISE_CCC.GEOZONE, workplaceGeozoneId)
//						.returning(ENTERPRISE_CCC.ID)
//						.fetchOne();
//					
//				enterpriseCccId = enterpriseCccRecord.getId();
//			}else{
//				enterpriseCccId = enterpriseCCC.get(0).get(ENTERPRISE_CCC.ID);
//			}
		
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "TC2")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, (contractData.getContractType() == null) ? (String) null : "\""+contractData.getContractType()+"\"")
				.set(CONTRACT_DATA.START_DATE, new Date(contractData.getStartDate().getTime()))
				.set(CONTRACT_DATA.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
				.execute();
		
			/*ModelOption.values()[employeeContractData.getContract_model()].toString()*/
			dslContext.insertInto(CONTRACT_INFO)
				.set(CONTRACT_INFO.DOMAIN, domain)
				.set(CONTRACT_INFO.NAME, "OPCION_CONTRATO")
				.set(CONTRACT_INFO.CONTRACT, contractId)
				.set(CONTRACT_INFO.EXPRESSION, (contractData.getContractModel() == null) ? (String) null : "\""+ ModelOption.values()[contractData.getContractModel()].toString() +"\"")
				.set(CONTRACT_INFO.START_DATE, new Date(contractData.getStartDate().getTime()))
				.set(CONTRACT_INFO.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
				.execute();
		
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "GRUPO_COTIZACION")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, (contractData.getQuoteGroup() == null) ? (String) null : contractData.getQuoteGroup())
				.set(CONTRACT_DATA.START_DATE, new Date(contractData.getStartDate().getTime()))
				.set(CONTRACT_DATA.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
				.execute();
		
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "OCUPACION")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, (contractData.getOcupation() == null) ? (String) null : contractData.getOcupation())
				.set(CONTRACT_DATA.START_DATE, new Date(contractData.getStartDate().getTime()))
				.set(CONTRACT_DATA.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
				.execute();	
			
			
			
		}else{//ES RETA
		
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "TIEMPO_COMPLETO")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, (contractData.getJourneyType() == 0) ? "FALSE" : "TRUE")
				.set(CONTRACT_DATA.START_DATE, new Date(contractData.getStartDate().getTime()))
				.set(CONTRACT_DATA.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
				.execute();
			
			dslContext.insertInto(CONTRACT_INFO)
			.set(CONTRACT_INFO.DOMAIN, domain)
			.set(CONTRACT_INFO.CONTRACT, contractId)
			.set(CONTRACT_INFO.NAME, "RETA")
			.set(CONTRACT_INFO.EXPRESSION, "true")
			.set(CONTRACT_INFO.START_DATE, new Date(contractData.getStartDate().getTime()))
			.set(CONTRACT_INFO.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
			.execute();
			
			dslContext.update(CONTRACT)
				.set(CONTRACT.SS_REGIME, (byte) 3)
				.where(CONTRACT.ID.eq(contractId))
				.execute();
			
		}
		
		return null;
	}

}
