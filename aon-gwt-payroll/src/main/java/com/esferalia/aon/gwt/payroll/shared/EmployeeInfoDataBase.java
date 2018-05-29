package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EmployeeInfoDataBase implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// ------------------------------------------------ EMPLOYEE INFO ---------------------------------------------------------
	
	//PERSON TABLE
	private Integer person_table_id;		//ID Person Table
	private Date birth_date;				//Person Birth Date
	private Byte gender;					//Person Gender
	private String social_security_num;		//Person S.S.
	private String name;					//Person Name
	private String first_surname;			//Person First Surname
	private String second_surname;			//Person Second Surname
	private Integer domain;					//Person Domain
	
	//REGISTRY TABLE
	private Integer registry_table_id; 		//ID Registry Table
	private String document;				//Person Document
	private Byte document_type;				//Person Document Type
	private String nationality;				//Person Nationality
	
	//RADDRESS TABLE
	private Integer raddress_table_id;		//ID Raddress Table
	private String address;					//Person Address
	private String address_number;			//Person Address Number
	private String zip_code;				//Person Addrezz Zip
	private String province;				//Person Address Province
	private String locality;				//Person Address Locality

	//GEOZONE TABLE
	private Integer geozone_table_id;		//ID Geozone Table
	private String geozone_name;			//Person Address Province(2)
	
	//RMEDIA TABLE
	private Integer rmedia_table_phone_id = null;	//ID Rmedia Table Phone
	private String phone = null;					//Person Phone
	private Integer rmedia_table_mobile_id = null;	//ID Rmedia Table Mobile
	private String mobile = null;					//Person Mobile
	private Integer rmedia_table_email_id = null;	//ID Rmedia Table Email
	private String email = null;					//Person Email
	
	// ------------------------------------------------ CONTRACT INFO ---------------------------------------------------------
	
	//CONTRACT TABLE
	private Integer contract_table_id;		//ID Contract Table
	private Date start_date;				//Contract StartDate
	private Date end_date;					//Contract EndDate
	private Date seniority_date;			//Contract SeniorityDate
	private String category_description;	//Contract CategoryDescription
	private Byte regime;					//Contract SS Regime
	
	//WORKPLACE TABLE
	private Integer workplace_table_id;		//ID Workplace Table
	private String workplace;				//Workplace Description
	
	//ENTERPRISE ACTIVITY TABLE Y CNAME2009
	private Integer 
		enterprise_activity_table_id;		//ID Enterprise Activity Table
	private String enterprise_activity;		//Contract Enterprise Activity
	private Integer cname2009_table_id;		//ID Cname2009 Table
	private String cname2009;				//Enterprise Activity Cname2009
	
	//CONTRACT INFO TABLE
	private Integer contract_info_table_id;	//ID Contract Info Table
	private Integer contract_model;			//Contract Model
	
	//ENTERPRISE CCC TABLE
	private Integer enterprise_ccc_table_id;//ID Enterprise CCC Table
	private String quote_account;			//Contract QuoteAccount
	
	//CONTRACT DATA TABLE
	private Map<String, String> 
		contract_data = new HashMap<>();	//ContractData Map (All Variables)
	private Integer 
		contract_data_table_type_id = null;		//ID Contract Data Table Type
	private String contract_type;			//Contract Type
	private Integer 
		contract_data_table_quote_group_id = null;	//ID Contract Data Table Quote Group
	private String quote_group;				//Contract QuoteGroup
	private Integer 
		contract_data_table_ocupation_id = null;	//ID Contract Data Table Ocupation
	private String ocupation;				//Contract Ocupation
	private Integer contract_data_table_journey_type_id = null; //ID Contract Journey Type
	private Boolean journeyType;			//Contract Journey Type
	
	//AGREEMENT TABLE
	private Integer agreement_table_id;		//ID Agreement Table
	private String agreement;				//Contract Agreement
	
	//AGREEMENT LEVEL TABLE
	private Integer 
		agreement_level_table_id;			//ID Agreement Level Table
	private String agreement_level;			//Contract AgreementLevel
	
	public EmployeeInfoDataBase() {
		super();
	}

	public EmployeeInfoDataBase(EmployeeInfoDataBase employeeInfo) {
		
		// -------------------------------------------------------------------
		// ------------------------ EMPLOYEE INFO ----------------------------
		// -------------------------------------------------------------------
		
		this.person_table_id = employeeInfo.getPerson_table_id();
		this.birth_date = employeeInfo.getBirth_date();
		this.gender = employeeInfo.getGender();
		this.social_security_num = employeeInfo.getSocial_security_num();
		this.name = employeeInfo.getName();
		this.first_surname = employeeInfo.getFirst_surname();
		this.second_surname = employeeInfo.getSecond_surname();
		this.domain = employeeInfo.getDomain();
	
		this.registry_table_id = employeeInfo.getRegistry_table_id();
		this.document = employeeInfo.getDocument();
		this.document_type = employeeInfo.getDocument_type();
		this.nationality = employeeInfo.getNationality();
		
		this.raddress_table_id = employeeInfo.getRaddress_table_id();
		this.address = employeeInfo.getAddress();
		this.address_number = employeeInfo.getAddress_number();
		this.zip_code = employeeInfo.getZip_code();
		this.province = employeeInfo.getProvince();
		this.locality = employeeInfo.getLocality();
		
		this.geozone_table_id = employeeInfo.getGeozone_table_id();
		this.geozone_name = employeeInfo.getGeozone_name();
		
		this.rmedia_table_phone_id = employeeInfo.getRmedia_table_phone_id();
		this.phone = employeeInfo.getPhone();
		this.rmedia_table_mobile_id = employeeInfo.getRmedia_table_mobile_id();
		this.mobile = employeeInfo.getMobile();
		this.rmedia_table_email_id = employeeInfo.getRmedia_table_email_id();
		this.email = employeeInfo.getEmail();
		
	
		// -------------------------------------------------------------------
		// ------------------------ CONTRACT INFO ----------------------------
		// -------------------------------------------------------------------
		
		this.contract_table_id = employeeInfo.getContract_table_id();
		this.start_date = employeeInfo.getStart_date();
		this.end_date = employeeInfo.getEnd_date();
		this.seniority_date = employeeInfo.getSeniority_date();
		this.category_description = employeeInfo.getCategory_description();
		this.regime = employeeInfo.getSSRegime();
		
		this.workplace_table_id = employeeInfo.getWorkplace_table_id();
		this.workplace = employeeInfo.getWorkplace();
		
		this.enterprise_activity_table_id = employeeInfo.getEnterprise_activity_table_id();
		this.enterprise_activity = employeeInfo.getEnterprise_activity();
		this.cname2009_table_id = employeeInfo.getCname2009_table_id();
		this.cname2009 = employeeInfo.getCname2009();
		
		this.contract_info_table_id = employeeInfo.getContract_info_table_id();
		this.contract_model = employeeInfo.getContract_model();
		
		this.enterprise_ccc_table_id = employeeInfo.getEnterprise_ccc_table_id();
		this.quote_account = employeeInfo.getQuote_account();
		
		this.contract_data = employeeInfo.getContract_data();
		this.contract_data_table_type_id = employeeInfo.getContract_data_table_type_id();
		this.contract_type = employeeInfo.getContract_type();
		this.contract_data_table_quote_group_id = employeeInfo.getContract_data_table_quote_group_id();
		this.quote_group = employeeInfo.getQuote_group();
		this.contract_data_table_ocupation_id = employeeInfo.getContract_data_table_ocupation_id();
		this.ocupation = employeeInfo.getOcupation();
		this.contract_data_table_journey_type_id = employeeInfo.getContract_data_table_journey_type_id();
		this.journeyType = employeeInfo.getJourneyType();
		
		this.agreement_table_id = employeeInfo.getAgreement_table_id();
		this.agreement = employeeInfo.getAgreement();
		
		this.agreement_level_table_id = employeeInfo.getAgreement_level_table_id();
		this.agreement_level = employeeInfo.getAgreement_level();
		
	}

	public Integer getPerson_table_id() {
		return person_table_id;
	}

	public void setPerson_table_id(Integer person_table_id) {
		this.person_table_id = person_table_id;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public Byte getDocument_type() {
		return document_type;
	}

	public void setDocument_type(Byte document_type) {
		this.document_type = document_type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirst_surname() {
		return first_surname;
	}

	public void setFirst_surname(String first_surname) {
		this.first_surname = first_surname;
	}

	public String getSecond_surname() {
		return second_surname;
	}

	public void setSecond_surname(String second_surname) {
		this.second_surname = second_surname;
	}

	public Date getBirth_date() {
		return birth_date;
	}

	public void setBirth_date(Date birth_date) {
		this.birth_date = birth_date;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getSocial_security_num() {
		return social_security_num;
	}

	public void setSocial_security_num(String social_security_num) {
		this.social_security_num = social_security_num;
	}

	public Byte getGender() {
		return gender;
	}

	public void setGender(Byte gender) {
		this.gender = gender;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress_number() {
		return address_number;
	}

	public void setAddress_number(String address_number) {
		this.address_number = address_number;
	}
	
	public String getLocality() {
		return locality;
	}

	public void setLocality(String location) {
		this.locality = location;
	}

	public String getZip_code() {
		return zip_code;
	}

	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}

	public String getGeozone_name() {
		return geozone_name;
	}

	public void setGeozone_name(String geozone_name) {
		this.geozone_name = geozone_name;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public Date getSeniority_date() {
		return seniority_date;
	}

	public void setSeniority_date(Date seniority_date) {
		this.seniority_date = seniority_date;
	}

	public String getEnterprise_activity() {
		return enterprise_activity;
	}

	public void setEnterprise_activity(String enterprise_activity) {
		this.enterprise_activity = enterprise_activity;
	}

	public Integer getContract_model() {
		return contract_model;
	}

	public void setContract_model(Integer ordinal) {
		this.contract_model = ordinal;
	}

	public String getQuote_account() {
		return quote_account;
	}

	public void setQuote_account(String quote_account) {
		this.quote_account = quote_account;
	}

	public String getContract_type() {
		return contract_type;
	}

	public void setContract_type(String contract_type) {
		this.contract_type = contract_type;
	}

	public String getQuote_group() {
		return quote_group;
	}

	public void setQuote_group(String quote_group) {
		this.quote_group = quote_group;
	}

	public String getOcupation() {
		return ocupation;
	}

	public void setOcupation(String ocupation) {
		this.ocupation = ocupation;
	}
	
	public Boolean getJourneyType() {
		return journeyType;
	}

	public void setJourneyType(Boolean journeyType) {
		this.journeyType = journeyType;
	}

	public String getAgreement() {
		return agreement;
	}

	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}

	public String getAgreement_level() {
		return agreement_level;
	}

	public void setAgreement_level(String agreement_level) {
		this.agreement_level = agreement_level;
	}

	public String getCategory_description() {
		return category_description;
	}

	public void setCategory_description(String category_description) {
		this.category_description = category_description;
	}

	public Map<String, String> getContract_data() {
		return contract_data;
	}

	public void setContract_data(Map<String, String> contract_data) {
		this.contract_data = contract_data;
		this.contract_type = (this.contract_data.get("TC2") == null) ? null : this.contract_data.get("TC2").split("\"")[1];
		this.quote_group = (this.contract_data.get("GRUPO_COTIZACION") == null) ? null : this.contract_data.get("GRUPO_COTIZACION").split("\"")[1];
		this.ocupation = (this.contract_data.get("OCUPACION") == null) ? null : this.contract_data.get("OCUPACION").split("\"")[1];
		this.journeyType = (this.contract_data.get("TIEMPO_COMPLETO") == null) ? null : Boolean.parseBoolean(this.contract_data.get("TIEMPO_COMPLETO"));
	}

	public Integer getRegistry_table_id() {
		return registry_table_id;
	}

	public void setRegistry_table_id(Integer registry_table_id) {
		this.registry_table_id = registry_table_id;
	}

	public Integer getRaddress_table_id() {
		return raddress_table_id;
	}

	public void setRaddress_table_id(Integer raddress_table_id) {
		this.raddress_table_id = raddress_table_id;
	}

	public Integer getGeozone_table_id() {
		return geozone_table_id;
	}

	public void setGeozone_table_id(Integer geozone_table_id) {
		this.geozone_table_id = geozone_table_id;
	}

	public Integer getRmedia_table_phone_id() {
		return rmedia_table_phone_id;
	}

	public void setRmedia_table_phone_id(Integer rmedia_table_phone_id) {
		this.rmedia_table_phone_id = rmedia_table_phone_id;
	}

	public Integer getRmedia_table_mobile_id() {
		return rmedia_table_mobile_id;
	}

	public void setRmedia_table_mobile_id(Integer rmedia_table_mobile_id) {
		this.rmedia_table_mobile_id = rmedia_table_mobile_id;
	}

	public Integer getRmedia_table_email_id() {
		return rmedia_table_email_id;
	}

	public void setRmedia_table_email_id(Integer rmedia_table_email_id) {
		this.rmedia_table_email_id = rmedia_table_email_id;
	}

	public Integer getContract_table_id() {
		return contract_table_id;
	}

	public void setContract_table_id(Integer contract_table_id) {
		this.contract_table_id = contract_table_id;
	}

	public Integer getContract_info_table_id() {
		return contract_info_table_id;
	}

	public void setContract_info_table_id(Integer contract_info_table_id) {
		this.contract_info_table_id = contract_info_table_id;
	}

	public Integer getEnterprise_ccc_table_id() {
		return enterprise_ccc_table_id;
	}

	public void setEnterprise_ccc_table_id(Integer enterprise_ccc_table_id) {
		this.enterprise_ccc_table_id = enterprise_ccc_table_id;
	}

	public Integer getContract_data_table_type_id() {
		return contract_data_table_type_id;
	}

	public void setContract_data_table_type_id(Integer contract_data_table_type_id) {
		this.contract_data_table_type_id = contract_data_table_type_id;
	}

	public Integer getContract_data_table_quote_group_id() {
		return contract_data_table_quote_group_id;
	}

	public void setContract_data_table_quote_group_id(Integer contract_data_table_quote_group_id) {
		this.contract_data_table_quote_group_id = contract_data_table_quote_group_id;
	}

	public Integer getContract_data_table_ocupation_id() {
		return contract_data_table_ocupation_id;
	}

	public void setContract_data_table_ocupation_id(Integer contract_data_table_ocupation_id) {
		this.contract_data_table_ocupation_id = contract_data_table_ocupation_id;
	}
	
	public Integer getContract_data_table_journey_type_id() {
		return contract_data_table_journey_type_id;
	}

	public void setContract_data_table_journey_type_id(Integer contract_data_table_journey_type_id) {
		this.contract_data_table_journey_type_id = contract_data_table_journey_type_id;
	}

	public Integer getAgreement_table_id() {
		return agreement_table_id;
	}

	public void setAgreement_table_id(Integer agreement_table_id) {
		this.agreement_table_id = agreement_table_id;
	}

	public Integer getAgreement_level_table_id() {
		return agreement_level_table_id;
	}

	public void setAgreement_level_table_id(Integer agreement_level_table_id) {
		this.agreement_level_table_id = agreement_level_table_id;
	}

	public Integer getEnterprise_activity_table_id() {
		return enterprise_activity_table_id;
	}

	public void setEnterprise_activity_table_id(Integer enterprise_activity_table_id) {
		this.enterprise_activity_table_id = enterprise_activity_table_id;
	}

	public Integer getCname2009_table_id() {
		return cname2009_table_id;
	}

	public void setCname2009_table_id(Integer cname2009_table_id) {
		this.cname2009_table_id = cname2009_table_id;
	}

	public String getCname2009() {
		return cname2009;
	}

	public void setCname2009(String cname2009) {
		this.cname2009 = cname2009;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;	
	}
	
	public Integer getDomain() {
		return this.domain;	
	}

	public void setWorkplace_table_id(Integer employe_workplace_table_id) {
		this.workplace_table_id = employe_workplace_table_id;
	}

	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}
	
	public Integer getWorkplace_table_id() {
		return this.workplace_table_id;
	}

	public String getWorkplace() {
		return this.workplace;
	}

	public void setSSRegime(Byte regime) {
		this.regime = regime;
	}
	
	public Byte getSSRegime(){
		return this.regime;
	}
	
}
