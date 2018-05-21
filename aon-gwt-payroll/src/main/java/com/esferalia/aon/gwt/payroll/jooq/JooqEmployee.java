package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;

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
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfoDataBase;
import com.esferalia.aon.jooq.tables.records.GeozoneRecord;

public class JooqEmployee {

	private static Settings SETTINGS = null;

	public static EmployeeInfoDataBase getEmployeeInfo(Connection conn, Integer contract) {
		return getEmployeeInfoDB(DSL.using(conn, getDefaultSettings()), contract);
	}
	
	public static EmployeeInfoDataBase setEmployeeInfo(Connection conn, EmployeeInfoDataBase newEmployeeInfo) {
		return setEmployeeInfoDB(DSL.using(conn, getDefaultSettings()), newEmployeeInfo);
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
		Record personTable = dslContext.select().from(PERSON).where(PERSON.REGISTRY.eq(
				dslContext.select(CONTRACT.PERSON).from(CONTRACT).where(CONTRACT.ID.eq(contract)))).fetchOne();
	
		employee.setPerson_table_id(personTable.get(PERSON.REGISTRY));
		employee.setBirth_date(personTable.get(PERSON.BIRTH_DATE));
		employee.setGender(personTable.get(PERSON.GENDER));
		employee.setSocial_security_num(personTable.get(PERSON.SOCIAL_SECURITY_NUM));
		employee.setName(personTable.get(PERSON.NAME));
		employee.setFirst_surname(personTable.get(PERSON.FIRST_SURNAME));
		employee.setSecond_surname(personTable.get(PERSON.SECOND_SURNAME));
		employee.setDomain(personTable.get(PERSON.DOMAIN));
		
		Integer employee_registry = personTable.get(PERSON.REGISTRY);
		
		//REGISTRY TABLE
		Record registryTable = dslContext.select().from(REGISTRY).where(REGISTRY.ID.eq(employee_registry)).fetchOne();
		
		employee.setRegistry_table_id(employee_registry);
		employee.setDocument(registryTable.get(REGISTRY.DOCUMENT));
		employee.setDocument_type(registryTable.get(REGISTRY.DOCUMENT_TYPE));
		employee.setNationality(registryTable.get(REGISTRY.NATIONALITY));
		
		//RADDRESS AND GEOZONE TABLE
		Record raddressTable = dslContext.select().from(RADDRESS).where(RADDRESS.REGISTRY.eq(employee_registry)).fetchOne();
		
		employee.setRaddress_table_id(raddressTable.get(RADDRESS.ID));
		employee.setAddress(raddressTable.get(RADDRESS.ADDRESS));
		employee.setAddress_number(raddressTable.get(RADDRESS.NUMBER));
		employee.setZip_code(raddressTable.get(RADDRESS.ZIP));
		employee.setLocality(raddressTable.get(RADDRESS.CITY));
		
		Integer raddress_geozone = raddressTable.get(RADDRESS.GEOZONE);
		
		//raddress_geozone can be null
		Record geozoneTable = dslContext.select().from(GEOZONE).where(GEOZONE.ID.eq(raddress_geozone)).fetchOne();
		
		employee.setGeozone_table_id((geozoneTable == null) ? null : geozoneTable.get(GEOZONE.ID));
		employee.setProvince((geozoneTable == null) ? null : geozoneTable.get(GEOZONE.NAME));
		employee.setGeozone_name((geozoneTable == null) ? null : geozoneTable.get(GEOZONE.NAME));
		
		//RMEDIA TABLE
		Result<Record> rmediaTable = dslContext.select().from(RMEDIA).where(RMEDIA.REGISTRY.eq(employee_registry)).fetch();
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
		
		
		// ------------------------------------------------ CONTRACT INFO ---------------------------------------------------------
		System.out.println("******************************* CONTRACT = "+contract+" *******************************");
		Record contractTable = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contract)).fetchOne();
		
		employee.setContract_table_id(contract);
		employee.setStart_date(contractTable.get(CONTRACT.START_DATE));
		employee.setEnd_date(contractTable.get(CONTRACT.END_DATE));
		employee.setSeniority_date(contractTable.get(CONTRACT.SENIORITY_DATE));
		employee.setCategory_description(contractTable.get(CONTRACT.CATEGORY_DESCRIPTION));
		
		Integer employe_workplace_table_id = contractTable.get(CONTRACT.WORKPLACE);
		Record workplaceTable = dslContext.select().from(WORKPLACE).where(WORKPLACE.ID.eq(employe_workplace_table_id)).fetchOne();
		
		employee.setWorkplace_table_id(employe_workplace_table_id);
		employee.setWorkplace(workplaceTable.get(WORKPLACE.DESCRIPTION));
		
		Integer enterpriseActivity = contractTable.get(CONTRACT.ENTERPRISE_ACTIVITY);
		
		//enterpriseActivity can be null
		Record enterpriseActivityTable = dslContext.select().from(ENTERPRISE_ACTIVITY).where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseActivity)).fetchOne();
		Integer cnae2009 = enterpriseActivityTable.get(ENTERPRISE_ACTIVITY.CNAE2009);
		//cnae2009 can be null
		Record cnae2009Table = dslContext.select().from(CNAE2009).where(CNAE2009.ID.eq(cnae2009)).fetchOne();
		
		employee.setEnterprise_activity_table_id((enterpriseActivityTable == null) ? null : enterpriseActivityTable.get(ENTERPRISE_ACTIVITY.ID));
		//TODO: MIRAR EL ACCESO A CNAE2009TABLE
		employee.setEnterprise_activity(enterpriseActivityTable.get(ENTERPRISE_ACTIVITY.DESCRIPTION) + " - " + cnae2009Table.get(CNAE2009.TITLE));
		employee.setCname2009_table_id((cnae2009Table == null) ? null : cnae2009Table.get(CNAE2009.ID));
		employee.setCname2009((cnae2009Table == null) ? null : cnae2009Table.get(CNAE2009.TITLE));
		
		Integer agreementLevel = contractTable.get(CONTRACT.AGREEMENT_LEVEL);
		//agreementLevel can be null
		Record agreementLevelTable = dslContext.select().from(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.ID.eq(agreementLevel)).fetchOne();
		
		employee.setAgreement_level_table_id((agreementLevelTable == null) ? null : agreementLevelTable.get(AGREEMENT_LEVEL.ID));
		employee.setAgreement_level((agreementLevelTable == null) ? null : agreementLevelTable.get(AGREEMENT_LEVEL.DESCRIPTION));
		
		Integer agreement = (agreementLevelTable == null) ? null : agreementLevelTable.get(AGREEMENT_LEVEL.AGREEMENT);
		//agreement can be null
		Record agreementTable = dslContext.select().from(AGREEMENT).where(AGREEMENT.ID.eq(agreement)).fetchOne();
		
		employee.setAgreement((agreementTable == null) ? null : agreementTable.get(AGREEMENT.DESCRIPTION));	
		employee.setAgreement_table_id((agreementTable == null) ? null : agreementTable.get(AGREEMENT.ID));
		
		Integer enterpriseCCC = contractTable.get(CONTRACT.ENTERPRISE_CCC);
		//enterpriseCCC can be null
		Record enterpriseCCCTable = dslContext.select().from(ENTERPRISE_CCC).where(ENTERPRISE_CCC.ID.eq(enterpriseCCC)).fetchOne();
		
		employee.setQuote_account((enterpriseCCCTable == null) ? null : enterpriseCCCTable.get(ENTERPRISE_CCC.CCC));
		employee.setEnterprise_ccc_table_id((enterpriseCCCTable == null) ? null : enterpriseCCCTable.get(ENTERPRISE_CCC.ID));
		
		java.util.Date actualJavaDate = new java.util.Date();
		Date actualSQLDate = new Date(actualJavaDate.getTime());
		
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
		}
		
		employee.setContract_data(contractDataMap);
		
		Record contractInfoTable = dslContext.select().from(CONTRACT_INFO)
			.where(CONTRACT_INFO.CONTRACT.eq(contract))
			.and(CONTRACT_INFO.NAME.eq("OPCION_CONTRATO"))
			.fetchOne();
		
		String contractType = (contractInfoTable == null) ? "" : contractInfoTable.get(CONTRACT_INFO.EXPRESSION).split("\"")[1];
		Integer ordinal;
		if(contractType == "")
			ordinal = -1;
		else
			ordinal = ModelOption.valueOf(contractType).ordinal();
		
		employee.setContract_model(ordinal);
		employee.setContract_info_table_id((contractInfoTable == null) ? null : contractInfoTable.get(CONTRACT_INFO.ID));
		
		System.out.println(
				"------------------- CONTRACT INFO ------------------- \n" +
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
			.set(PERSON.NAME, newEmployeeInfo.getName())
			.set(PERSON.FIRST_SURNAME, newEmployeeInfo.getFirst_surname())
			.set(PERSON.SECOND_SURNAME, newEmployeeInfo.getSecond_surname())
			.set(PERSON.GENDER, newEmployeeInfo.getGender())
			.set(PERSON.BIRTH_DATE, (newEmployeeInfo.getBirth_date() == null) ? null : new Date(newEmployeeInfo.getBirth_date().getTime()))
			.set(PERSON.SOCIAL_SECURITY_NUM, newEmployeeInfo.getSocial_security_num())
			.where(PERSON.REGISTRY.eq(newEmployeeInfo.getPerson_table_id()))
			.execute();
		
		dslContext.update(REGISTRY)
			.set(REGISTRY.DOCUMENT, newEmployeeInfo.getDocument())
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
			
			GeozoneRecord geozoneRecord  = dslContext.insertInto(GEOZONE)
				.set(GEOZONE.DOMAIN, newEmployeeInfo.getDomain())
				.set(GEOZONE.NAME, newEmployeeInfo.getGeozone_name())
				.set(GEOZONE.CODE, codes.get(0).value1())
				.returning(GEOZONE.ID)
				.fetchOne();
			
			geozoneId = geozoneRecord.getId();
		}else
			geozoneId = geozone.value1();
		
		dslContext.update(RADDRESS)
			.set(RADDRESS.ADDRESS, newEmployeeInfo.getAddress())
			.set(RADDRESS.NUMBER, newEmployeeInfo.getAddress_number())
			.set(RADDRESS.ZIP, newEmployeeInfo.getZip_code())
			.set(RADDRESS.CITY, newEmployeeInfo.getLocality())
			.set(RADDRESS.GEOZONE, geozoneId)
			.where(RADDRESS.ID.eq(newEmployeeInfo.getRaddress_table_id()))
			.execute();
		
		if(newEmployeeInfo.getPhone() != null){
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
		
		if(newEmployeeInfo.getMobile() != null){
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
		
		if(newEmployeeInfo.getEmail() != null){
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
		
		// ------------------------------------------------------------------------------------------------------------------------
		// ------------------------------------------------ CONTRACT INFO ---------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------------------------
		
		Record contractTable = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(newEmployeeInfo.getContract_table_id())).fetchOne();
		
		Date oldStartDate = contractTable.get(CONTRACT.START_DATE);
		Date oldEndDate = contractTable.get(CONTRACT.END_DATE);
		
		dslContext.update(CONTRACT)
			.set(CONTRACT.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
			.set(CONTRACT.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.set(CONTRACT.SENIORITY_DATE, new Date(newEmployeeInfo.getSeniority_date().getTime()))
			.set(CONTRACT.CATEGORY_DESCRIPTION, newEmployeeInfo.getCategory_description())
			.set(CONTRACT.AGREEMENT_LEVEL, newEmployeeInfo.getAgreement_level_table_id())
			.where(CONTRACT.ID.eq(newEmployeeInfo.getContract_table_id()))
			.execute();
		
		if(newEmployeeInfo.getEnterprise_ccc_table_id() != null)
			dslContext.update(ENTERPRISE_CCC)
				.set(ENTERPRISE_CCC.CCC, newEmployeeInfo.getQuote_account())
				.where(ENTERPRISE_CCC.ID.eq(newEmployeeInfo.getEnterprise_ccc_table_id()))
				.execute();
		else if(newEmployeeInfo.getQuote_account() != null)
			dslContext.insertInto(ENTERPRISE_CCC)
			.set(ENTERPRISE_CCC.DOMAIN, newEmployeeInfo.getDomain())
			.set(ENTERPRISE_CCC.CCC, newEmployeeInfo.getQuote_account())
			.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, newEmployeeInfo.getEnterprise_activity_table_id())
			.execute();
		
		if(newEmployeeInfo.getContract_data_table_type_id() != null){
			if(newEmployeeInfo.getContract_type().contains("\""))
				dslContext.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.EXPRESSION, newEmployeeInfo.getContract_type())
					.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
					.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
					.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_type_id()))
					.execute();
			else
				dslContext.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.EXPRESSION, "\""+newEmployeeInfo.getContract_type()+"\"")
					.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
					.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
					.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_type_id()))
					.execute();
		}else
			dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, newEmployeeInfo.getDomain())
			.set(CONTRACT_DATA.NAME, "TC2")
			.set(CONTRACT_DATA.CONTRACT, newEmployeeInfo.getContract_table_id())
			.set(CONTRACT_DATA.EXPRESSION, newEmployeeInfo.getContract_type())
			.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
			.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.execute();
		
		if(newEmployeeInfo.getContract_info_table_id() != null){
			if(newEmployeeInfo.getContract_model() != null)
				dslContext.update(CONTRACT_INFO)
					.set(CONTRACT_INFO.EXPRESSION,  "\""+ ModelOption.values()[newEmployeeInfo.getContract_model()].toString()  +"\"")
					.set(CONTRACT_INFO.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
					.set(CONTRACT_INFO.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
					.where(CONTRACT_INFO.ID.eq(newEmployeeInfo.getContract_info_table_id()))
					.execute();
			else
				dslContext.update(CONTRACT_INFO)
				.set(CONTRACT_INFO.EXPRESSION, (String) null)
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
			if(newEmployeeInfo.getQuote_group() != null){
				if(newEmployeeInfo.getQuote_group().contains("\""))
					dslContext.update(CONTRACT_DATA)
						.set(CONTRACT_DATA.EXPRESSION, newEmployeeInfo.getQuote_group())
						.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
						.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
						.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_quote_group_id()))
						.execute();
				else
					dslContext.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.EXPRESSION, "\""+newEmployeeInfo.getQuote_group()+"\"")
					.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
					.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
					.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_quote_group_id()))
					.execute();
			}else{
				dslContext.update(CONTRACT_DATA)
				.set(CONTRACT_DATA.EXPRESSION, (String) null)
				.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
				.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
				.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_quote_group_id()))
				.execute();
			}
		}else
			dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, newEmployeeInfo.getDomain())
			.set(CONTRACT_DATA.NAME, "GRUPO_COTIZACION")
			.set(CONTRACT_DATA.CONTRACT, newEmployeeInfo.getContract_table_id())
			.set(CONTRACT_DATA.EXPRESSION, newEmployeeInfo.getQuote_group())
			.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
			.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.execute();
		
		if(newEmployeeInfo.getContract_data_table_ocupation_id() != null){
			if(newEmployeeInfo.getOcupation() != null){
				if (newEmployeeInfo.getOcupation().contains("\""))
					dslContext.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.EXPRESSION, newEmployeeInfo.getOcupation())
					.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
					.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
					.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_ocupation_id()))
					.execute();
				else
					dslContext.update(CONTRACT_DATA)
						.set(CONTRACT_DATA.EXPRESSION, "\""+newEmployeeInfo.getOcupation()+"\"")
						.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
						.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
						.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_ocupation_id()))
						.execute();
			}else{
				dslContext.update(CONTRACT_DATA)
				.set(CONTRACT_DATA.EXPRESSION, (String) null)
				.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
				.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
				.where(CONTRACT_DATA.ID.eq(newEmployeeInfo.getContract_data_table_ocupation_id()))
				.execute();
			} 
		}else
			dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, newEmployeeInfo.getDomain())
			.set(CONTRACT_DATA.NAME, "OCUPACION")
			.set(CONTRACT_DATA.CONTRACT, newEmployeeInfo.getContract_table_id())
			.set(CONTRACT_DATA.EXPRESSION, newEmployeeInfo.getOcupation())
			.set(CONTRACT_DATA.START_DATE, new Date(newEmployeeInfo.getStart_date().getTime()))
			.set(CONTRACT_DATA.END_DATE, (newEmployeeInfo.getEnd_date() == null) ? null : new Date(newEmployeeInfo.getEnd_date().getTime()))
			.execute();
		
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
		else
			updateInfo += "Modalidad = \"" + ModelOption.values()[newEmployeeInfo.getContract_model()].toString() + "\"\n";
				
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

}
