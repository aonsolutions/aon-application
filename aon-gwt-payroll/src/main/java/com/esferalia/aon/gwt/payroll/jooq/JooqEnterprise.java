package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.EnterpriseInfo;
import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;

public class JooqEnterprise {

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	// --------------------------------- GETTER / SETTER ---------------------------------------------
	
	public static EnterpriseInfo getEnterpriseInfo(Connection conn, Integer enterpriseId) {
		return getEnterpriseInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseId);
	}
	
	public static EnterpriseInfo setEnterpriseInfo(Connection conn, EnterpriseInfo enterpriseInfo) {
		return setEnterpriseInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseInfo);
	}
	
	// --------------------------------- GET ENTERPRISES INFO ----------------------------------------
	
	public static Map<Integer, String> getEnterpriseAddresses(Connection conn, Integer enterpriseId) {
		return getEnterpriseAddressesInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseId);
	}
	
	public static Map<Integer, String> getEnterpriseScopes(Connection conn, Integer enterpriseId) {
		return getEnterpriseScopesInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseId);
	}

	public static Map<Integer, String> getEnterpriseCalendars(Connection conn, Integer enterpriseId) {
		return getEnterpriseCalendarsInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseId);
	}
	
	public static Map<Integer, String> getEnterpriseActivities(Connection conn, Integer enterpriseId) {
		return getEnterpriseActivitiesInfoDB(DSL.using(conn, getDefaultSettings()), enterpriseId);
	}
	
	
	// --------------------------------- GETTER / SETTER ---------------------------------------------
	
	private static EnterpriseInfo getEnterpriseInfoDB(DSLContext dslContext, Integer enterpriseId) {
		
		EnterpriseInfo enterpriseInfo = new EnterpriseInfo();
		
		// --------- ENTERPRISE TABLE
		
		Record enterpriseRecord = dslContext.select().from(ENTERPRISE)
				.where(ENTERPRISE.REGISTRY.eq(enterpriseId))
				.fetchOne();
		
		Integer domainId = enterpriseRecord.get(ENTERPRISE.DOMAIN);
		Integer scopeId = enterpriseRecord.get(ENTERPRISE.SCOPE);
		Integer calendarId = enterpriseRecord.get(ENTERPRISE.CALENDAR);
		
		// --------- REGISTRY TABLE
		
		Record registryRecord = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(enterpriseId))
				.fetchOne();
		
		String document = registryRecord.get(REGISTRY.DOCUMENT);
		Byte documentType = registryRecord.get(REGISTRY.DOCUMENT_TYPE);
		String documentCountry = registryRecord.get(REGISTRY.DOCUMENT_COUNTRY);
		String name = registryRecord.get(REGISTRY.NAME);
		String alias = registryRecord.get(REGISTRY.ALIAS);
		
		// --------- RADDRESS TABLE
		
		Result<Record> raddressRecords = dslContext.select().from(RADDRESS)
				.where(RADDRESS.REGISTRY.eq(enterpriseId))
					.and(RADDRESS.TYPE.eq((byte)0))
				.fetch();
		
		Integer raddressId = null;
		String streetType = null;
		String address = null;
		String addressNum = null;
		String addressZip = null;
		String addressCity = null;
		Integer geozoneId = null;
		String addressProvince = null;
		
		if(null != raddressRecords && !raddressRecords.isEmpty()) {
			
			raddressId = raddressRecords.get(0).get(RADDRESS.ID);
			streetType = raddressRecords.get(0).get(RADDRESS.STREET_TYPE);
			address = raddressRecords.get(0).get(RADDRESS.ADDRESS);
			addressNum = raddressRecords.get(0).get(RADDRESS.NUMBER);
			addressZip = raddressRecords.get(0).get(RADDRESS.ZIP);
			addressCity = raddressRecords.get(0).get(RADDRESS.CITY);
			geozoneId = raddressRecords.get(0).get(RADDRESS.GEOZONE);
			
			if(null != geozoneId) {
				Record geozoneRecord = dslContext.select().from(GEOZONE)
						.where(GEOZONE.ID.eq(geozoneId))
						.fetchOne();
				
				addressProvince = geozoneRecord.get(GEOZONE.NAME);
			}
		}
		
		// --------- RMEDIA TABLE
		
		Result<Record> rmediaRecords = dslContext.select().from(RMEDIA)
				.where(RMEDIA.REGISTRY.eq(enterpriseId))
				.fetch();
		
		Integer phoneId = null;
		String phone = null;
		Integer mobileId = null;
		String mobile = null;
		Integer emailId = null;
		String email = null;
		Integer webId = null;
		String web = null;
		
		for(Record r : rmediaRecords) {
			if(r.get(RMEDIA.MEDIA).equals((byte)1)) {		//Phone
				phoneId = r.get(RMEDIA.ID);
				phone = r.get(RMEDIA.VALUE);
			}else if(r.get(RMEDIA.MEDIA).equals((byte)3)){	//Mobile
				mobileId = r.get(RMEDIA.ID);
				mobile = r.get(RMEDIA.VALUE);
			}else if(r.get(RMEDIA.MEDIA).equals((byte)4)){	//Email
				emailId = r.get(RMEDIA.ID);
				email = r.get(RMEDIA.VALUE);
			}else if(r.get(RMEDIA.MEDIA).equals((byte)5)){	//Web
				webId = r.get(RMEDIA.ID);
				web = r.get(RMEDIA.VALUE);
			}
		}
		
		// --------- ENTERPRISE DATA TABLE
		
		Result<Record> enterpriseDataRecords = dslContext.select().from(ENTERPRISE_DATA)
				.where(ENTERPRISE_DATA.ENTERPRISE.eq(enterpriseId))
				.fetch();
		
		Integer paysheetModelId = null;
		Byte paysheetModel = null;
		Integer paysheetModelDraftId = null;
		Byte paysheetModelDraft = null;
		Integer costsModelId = null;
		Byte costsModel = null;
		Integer paysheetSendTypeId = null;
		Byte paysheetSendType = null;
		Integer paysheetEmailId = null;
		String paysheetEmail = null;
		Integer enterpriseAgreementId = null;
		
		for(Record r : enterpriseDataRecords) {
			if(r.get(ENTERPRISE_DATA.NAME).equals("PAY_REPORT_salary_PAY")) {
				paysheetModelId = r.get(ENTERPRISE_DATA.ID);
				paysheetModel = getModelIndexPaysheet(r.get(ENTERPRISE_DATA.EXPRESSION));
			}else if(r.get(ENTERPRISE_DATA.NAME).equals("PAY_REPORT_salaryDraft_PAY")) {
				paysheetModelDraftId = r.get(ENTERPRISE_DATA.ID);
				paysheetModelDraft = getModelIndexPaysheetDraft(r.get(ENTERPRISE_DATA.EXPRESSION));
			}else if(r.get(ENTERPRISE_DATA.NAME).equals("PAY_REPORT_enterpriseSalary_PAY")) {
				costsModelId = r.get(ENTERPRISE_DATA.ID);
				costsModel = getModelIndexCosts(r.get(ENTERPRISE_DATA.EXPRESSION));
			}else if(r.get(ENTERPRISE_DATA.NAME).equals("PAY_salarySendingMethod_PAY")) {
				paysheetSendTypeId = r.get(ENTERPRISE_DATA.ID);
				paysheetSendType = getIndexSendType(r.get(ENTERPRISE_DATA.EXPRESSION));
			}else if(r.get(ENTERPRISE_DATA.NAME).equals("PAY_salarySending_email_PAY")) {
				paysheetEmailId = r.get(ENTERPRISE_DATA.ID);
				paysheetEmail = r.get(ENTERPRISE_DATA.EXPRESSION);
			}else if(r.get(ENTERPRISE_DATA.NAME).equals("agreement")) {
				enterpriseAgreementId = r.get(ENTERPRISE_DATA.ID);	
			}			
		}
		
		// --------- SET ENTERPRISE INFO
		
		enterpriseInfo.setDomainId(domainId);
		enterpriseInfo.setScopeId(scopeId);
		enterpriseInfo.setCalendarId(calendarId);
		enterpriseInfo.setEnterpriseId(enterpriseId);
		enterpriseInfo.setName(name);
		enterpriseInfo.setAlias(alias);
		enterpriseInfo.setDocumentType(documentType);
		enterpriseInfo.setDocumentCountry(documentCountry);
		enterpriseInfo.setDocument(document);
		enterpriseInfo.setRaddressId(raddressId);
		enterpriseInfo.setStreetType(streetType);
		enterpriseInfo.setAddress(address);
		enterpriseInfo.setAddressNum(addressNum);
		enterpriseInfo.setAddressZip(addressZip);
		enterpriseInfo.setAddressCity(addressCity);
		enterpriseInfo.setGeozoneId(geozoneId);
		enterpriseInfo.setAddressProvince(addressProvince);
		enterpriseInfo.setMobileId(mobileId);
		enterpriseInfo.setMobile(mobile);
		enterpriseInfo.setPhoneId(phoneId);
		enterpriseInfo.setPhone(phone);
		enterpriseInfo.setEmailId(emailId);
		enterpriseInfo.setEmail(email);
		enterpriseInfo.setWebId(webId);
		enterpriseInfo.setWeb(web);
		enterpriseInfo.setPaysheetModelId(paysheetModelId);
		enterpriseInfo.setPaysheetModeDraftlId(paysheetModelDraftId);
		enterpriseInfo.setPaysheetModelDraft(paysheetModelDraft);
		enterpriseInfo.setPaysheetModel(paysheetModel);
		enterpriseInfo.setCostsModelId(costsModelId);
		enterpriseInfo.setCostsModel(costsModel);
		enterpriseInfo.setPaysheetSendTypeId(paysheetSendTypeId);
		enterpriseInfo.setPaysheetSendType(paysheetSendType);
		enterpriseInfo.setPaysheetEmailId(paysheetEmailId);
		enterpriseInfo.setPaysheetEmail(paysheetEmail);
		enterpriseInfo.setEnterpriseAgreementId(enterpriseAgreementId);
		
		return enterpriseInfo;
	}
	
	private static EnterpriseInfo setEnterpriseInfoDB(DSLContext dslContext, EnterpriseInfo enterpriseInfo) {
		
		// --------- ENTERPRISE TABLE
		
		dslContext.update(ENTERPRISE)
			.set(ENTERPRISE.SCOPE, enterpriseInfo.getScopeId())
			.set(ENTERPRISE.CALENDAR, enterpriseInfo.getCalendarId())
			.where(ENTERPRISE.REGISTRY.eq(enterpriseInfo.getEnterpriseId()))
			.execute();
		
		// --------- REGISTRY TABLE
		
		dslContext.update(REGISTRY)
			.set(REGISTRY.DOCUMENT, enterpriseInfo.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE, enterpriseInfo.getDocumentType())
			.set(REGISTRY.DOCUMENT_COUNTRY, enterpriseInfo.getDocumentCountry())
			.set(REGISTRY.NAME, enterpriseInfo.getName())
			.set(REGISTRY.ALIAS, enterpriseInfo.getAlias())
			.set(REGISTRY.NATIONALITY, enterpriseInfo.getDocumentCountry())
			.where(REGISTRY.ID.eq(enterpriseInfo.getEnterpriseId()))
			.execute();
		
		// --------- RADDRESS TABLE
		
		Record1<Integer> geozone = dslContext.select(GEOZONE.ID)
				.from(GEOZONE)
				.where(GEOZONE.CODE.eq(enterpriseInfo.getAddressProvince()))
					.and(GEOZONE.DOMAIN.eq(enterpriseInfo.getDomainId()))
				.fetchOne();
		
		Integer geozoneId = null;
		
		if(geozone == null){
			Result<Record1<String>> names = dslContext.select(GEOZONE.NAME)
				.from(GEOZONE)
				.where(GEOZONE.CODE.eq(enterpriseInfo.getAddressProvince()))
				.fetch();
			
			if(!names.isEmpty()){
				GeozoneRecord geozoneRecord  = dslContext.insertInto(GEOZONE)
						.set(GEOZONE.DOMAIN, enterpriseInfo.getDomainId())
						.set(GEOZONE.NAME, names.get(0).value1())
						.set(GEOZONE.CODE, enterpriseInfo.getAddressProvince())
						.returning(GEOZONE.ID)
						.fetchOne();
					
				geozoneId = geozoneRecord.getId();
			}
		}else
			geozoneId = geozone.value1();

		Integer rAddressId = enterpriseInfo.getRaddressId();
		
		if(null == rAddressId){
			RaddressRecord rAddressRecord = dslContext.insertInto(RADDRESS)
					.set(RADDRESS.DOMAIN, enterpriseInfo.getDomainId())
					.set(RADDRESS.REGISTRY,  enterpriseInfo.getEnterpriseId())
					.set(RADDRESS.STREET_TYPE, enterpriseInfo.getStreetType())
					.set(RADDRESS.ADDRESS, enterpriseInfo.getAddress())
					.set(RADDRESS.NUMBER, enterpriseInfo.getAddressNum())
					.set(RADDRESS.ZIP, enterpriseInfo.getAddressZip())
					.set(RADDRESS.CITY, enterpriseInfo.getAddressCity())
					.set(RADDRESS.GEOZONE, geozoneId)
					.returning(RADDRESS.ID, RADDRESS.GEOZONE)
					.fetchOne();
			
			rAddressId = rAddressRecord.getId();
		}else{
			dslContext.update(RADDRESS)
					.set(RADDRESS.STREET_TYPE, enterpriseInfo.getStreetType())
					.set(RADDRESS.ADDRESS, enterpriseInfo.getAddress())
					.set(RADDRESS.NUMBER, enterpriseInfo.getAddressNum())
					.set(RADDRESS.ZIP, enterpriseInfo.getAddressZip())
					.set(RADDRESS.CITY, enterpriseInfo.getAddressCity())
					.set(RADDRESS.GEOZONE, geozoneId)
					.where(RADDRESS.ID.eq(rAddressId))
					.execute();
		}
		
		if(geozone == null && null != geozoneId){
			Integer rAddressGeozone = geozoneId;
			
			GeozoneRecord geozoneParentRecord  = dslContext.insertInto(GEOZONE)
					.set(GEOZONE.DOMAIN, enterpriseInfo.getDomainId())
					.set(GEOZONE.NAME, "ESPAÑA")
					.set(GEOZONE.CODE, "ES")
					.set(GEOZONE.SYSTEM, (byte) 1)
					.returning(GEOZONE.ID)
					.fetchOne();
			
			Integer geozoneParentId = geozoneParentRecord.getId();
			
			dslContext.insertInto(GEOTREE)
			.set(GEOTREE.DOMAIN, enterpriseInfo.getDomainId())
			.set(GEOTREE.PARENT, geozoneParentId)
			.set(GEOTREE.CHILD, rAddressGeozone)
			.execute();
			
			dslContext.insertInto(GEOTREE)
			.set(GEOTREE.DOMAIN, enterpriseInfo.getDomainId())
			.set(GEOTREE.PARENT, (Integer) null)
			.set(GEOTREE.CHILD, geozoneParentId)
			.execute();
		}
		
		// --------- RMEDIA TABLE
		
		dslContext.insertInto(RMEDIA, RMEDIA.ID, RMEDIA.DOMAIN, RMEDIA.REGISTRY, RMEDIA.MEDIA, RMEDIA.VALUE, RMEDIA.COMMENT, 
				  RMEDIA.ADMINISTRATIVE, RMEDIA.COMMERCIAL, RMEDIA.TECHNICAL, RMEDIA.RADDRESS)
			.values(enterpriseInfo.getPhoneId(), enterpriseInfo.getDomainId(), enterpriseInfo.getEnterpriseId(), (byte) 1, enterpriseInfo.getPhone(), (String) null, 
					(byte) 1, (byte) 1, (byte) 1, rAddressId)
			.onDuplicateKeyUpdate()
			.set(RMEDIA.VALUE, enterpriseInfo.getPhone())
			.set(RMEDIA.RADDRESS, rAddressId)
			.execute();
		
		dslContext.insertInto(RMEDIA, RMEDIA.ID, RMEDIA.DOMAIN, RMEDIA.REGISTRY, RMEDIA.MEDIA, RMEDIA.VALUE, RMEDIA.COMMENT, 
				  RMEDIA.ADMINISTRATIVE, RMEDIA.COMMERCIAL, RMEDIA.TECHNICAL, RMEDIA.RADDRESS)
			.values(enterpriseInfo.getMobileId(), enterpriseInfo.getDomainId(),  enterpriseInfo.getEnterpriseId(), (byte) 3, enterpriseInfo.getMobile(), (String) null, 
					(byte) 1, (byte) 1, (byte) 1, rAddressId)
			.onDuplicateKeyUpdate()
			.set(RMEDIA.VALUE, enterpriseInfo.getMobile())
			.set(RMEDIA.RADDRESS, rAddressId)
			.execute();
		
		dslContext.insertInto(RMEDIA, RMEDIA.ID, RMEDIA.DOMAIN, RMEDIA.REGISTRY, RMEDIA.MEDIA, RMEDIA.VALUE, RMEDIA.COMMENT, 
				  RMEDIA.ADMINISTRATIVE, RMEDIA.COMMERCIAL, RMEDIA.TECHNICAL, RMEDIA.RADDRESS)
			.values(enterpriseInfo.getEmailId(), enterpriseInfo.getDomainId(),  enterpriseInfo.getEnterpriseId(), (byte) 4, enterpriseInfo.getEmail(), (String) null, 
					(byte) 1, (byte) 1, (byte) 1, rAddressId)
			.onDuplicateKeyUpdate()
			.set(RMEDIA.VALUE, enterpriseInfo.getEmail())
			.set(RMEDIA.RADDRESS, rAddressId)
			.execute();
		
		dslContext.insertInto(RMEDIA, RMEDIA.ID, RMEDIA.DOMAIN, RMEDIA.REGISTRY, RMEDIA.MEDIA, RMEDIA.VALUE, RMEDIA.COMMENT, 
				  RMEDIA.ADMINISTRATIVE, RMEDIA.COMMERCIAL, RMEDIA.TECHNICAL, RMEDIA.RADDRESS)
			.values(enterpriseInfo.getWebId(), enterpriseInfo.getDomainId(),  enterpriseInfo.getEnterpriseId(), (byte) 5, enterpriseInfo.getWeb(), (String) null, 
					(byte) 1, (byte) 1, (byte) 1, rAddressId)
			.onDuplicateKeyUpdate()
			.set(RMEDIA.VALUE, enterpriseInfo.getWeb())
			.set(RMEDIA.RADDRESS, rAddressId)
			.execute();
		
		// --------- ENTERPRISE DATA TABLE
		
		dslContext.update(ENTERPRISE_DATA)
			.set(ENTERPRISE_DATA.EXPRESSION, getCostsModel(enterpriseInfo.getCostsModel()))
			.where(ENTERPRISE_DATA.ID.eq(enterpriseInfo.getCostsModelId()))
			.execute();
		
		dslContext.update(ENTERPRISE_DATA)
			.set(ENTERPRISE_DATA.EXPRESSION, getPaysheetModel(enterpriseInfo.getPaysheetModel()))
			.where(ENTERPRISE_DATA.ID.eq(enterpriseInfo.getPaysheetModelId()))
			.execute();
		
		dslContext.update(ENTERPRISE_DATA)
			.set(ENTERPRISE_DATA.EXPRESSION, getPaysheetModelDraft(enterpriseInfo.getPaysheetModelDraft()))
			.where(ENTERPRISE_DATA.ID.eq(enterpriseInfo.getPaysheetModeDraftlId()))
			.execute();
		
		dslContext.update(ENTERPRISE_DATA)
			.set(ENTERPRISE_DATA.EXPRESSION, getPaysheetSendType(enterpriseInfo.getPaysheetSendType()))
			.where(ENTERPRISE_DATA.ID.eq(enterpriseInfo.getPaysheetSendTypeId()))
			.execute();
		
		if(0 == enterpriseInfo.getPaysheetSendType())
			dslContext.update(ENTERPRISE_DATA)
				.set(ENTERPRISE_DATA.EXPRESSION, enterpriseInfo.getPaysheetEmail())
				.where(ENTERPRISE_DATA.ID.eq(enterpriseInfo.getPaysheetEmailId()))
				.execute();

		if(null != enterpriseInfo.getEnterpriseAgreementId() && -1 != enterpriseInfo.getEnterpriseAgreementId()) {
			String agreementDescription = dslContext.select().from(AGREEMENT)
					.where(AGREEMENT.ID.eq(enterpriseInfo.getEnterpriseAgreementId()))
					.fetchOptional(AGREEMENT.DESCRIPTION)
					.orElse(null)
					;
			if ( agreementDescription != null )	{	 
				dslContext.insertInto(ENTERPRISE_DATA, ENTERPRISE_DATA.ID, ENTERPRISE_DATA.DOMAIN, ENTERPRISE_DATA.ENTERPRISE, ENTERPRISE_DATA.NAME,
						ENTERPRISE_DATA.EXPRESSION, ENTERPRISE_DATA.START_DATE, ENTERPRISE_DATA.END_DATE)
					.values(enterpriseInfo.getEnterpriseAgreementId(), enterpriseInfo.getDomainId(),  enterpriseInfo.getEnterpriseId(), "agreement", 
							agreementDescription, null, null)
					.onDuplicateKeyUpdate()
					.set(ENTERPRISE_DATA.EXPRESSION, agreementDescription)
					.execute();
				return enterpriseInfo;
			} 
		}
		
		if(null != enterpriseInfo.getEnterpriseAgreementId())
			dslContext.delete(ENTERPRISE_DATA)
				.where(ENTERPRISE_DATA.ID.eq(enterpriseInfo.getEnterpriseAgreementId()))
				.execute();
		
		return enterpriseInfo;
	}
	
	// --------------------------------- GET ENTERPRISES INFO ----------------------------------------
	
	private static Map<Integer, String> getEnterpriseAddressesInfoDB(DSLContext dslContext, Integer enterpriseId) {
		Map<Integer, String> addresses = new HashMap<Integer, String>();
		
		Result<Record> raddressRecords = dslContext.select().from(RADDRESS)
				.where(RADDRESS.REGISTRY.eq(enterpriseId))
				.fetch();
		
		for(Record r : raddressRecords){
			Record1<String> geozoneName = dslContext.select(GEOZONE.NAME).from(GEOZONE)
					.where(GEOZONE.ID.eq(r.get(RADDRESS.GEOZONE)))
					.fetchOne();
			
			String addressStr = "";
			
			if(r.get(RADDRESS.STREET_TYPE) != "")
				addressStr += r.get(RADDRESS.STREET_TYPE) + " ";
			if(null == r.get(RADDRESS.ADDRESS))
				continue;
			if(!r.get(RADDRESS.ADDRESS).isEmpty())
				addressStr += r.get(RADDRESS.ADDRESS) + " ";
			if(null !=r.get(RADDRESS.NUMBER))
				if(!r.get(RADDRESS.NUMBER).isEmpty())
					addressStr += r.get(RADDRESS.NUMBER) + " ";
			
			if(null != geozoneName)
				addressStr += geozoneName.get(0);
			
			addresses.put(r.get(RADDRESS.ID), addressStr);
		}
		
		return addresses;
	}
	
	private static Map<Integer, String> getEnterpriseScopesInfoDB(DSLContext dslContext, Integer enterpriseId) {
		Map<Integer, String> scopes = new HashMap<Integer, String>();
		
		Record enterpriseRecord = dslContext.select().from(ENTERPRISE)
					.where(ENTERPRISE.REGISTRY.eq(enterpriseId))
					.fetchOne();
		
		Integer domainId = enterpriseRecord.get(ENTERPRISE.DOMAIN);
		
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.eq(domainId))
				.fetchOne();
		
		Integer parentDomianId = domainRecord.get(DOMAIN.PARENT);
		String domainDescription = domainRecord.get(DOMAIN.DESCRIPTION);
		
		if(null == parentDomianId) {
			Result<Record> scopeRecords = dslContext.select().from(SCOPE)
					.where(SCOPE.DOMAIN.eq(domainId))
					.fetch();
			
			if(null != scopeRecords && !scopeRecords.isEmpty())
				for(Record r : scopeRecords)
					scopes.put(r.get(SCOPE.ID), r.get(SCOPE.DESCRIPTION) + " (" + domainDescription +")");
			
		}else {
			Result<Record> scopeRecords = dslContext.select().from(SCOPE)
					.where(SCOPE.DOMAIN.eq(domainId)
							.or(SCOPE.DOMAIN.eq(parentDomianId))
					)
					.fetch();
			
			if(null != scopeRecords && !scopeRecords.isEmpty()) {
				Record parentDomainRecord = dslContext.select().from(DOMAIN)
					.where(DOMAIN.ID.eq(parentDomianId))
					.fetchOne();
			
				String parentDescription = parentDomainRecord.get(DOMAIN.DESCRIPTION);
			
				for(Record r : scopeRecords)			
					scopes.put(r.get(SCOPE.ID), r.get(SCOPE.DESCRIPTION) + " (" + parentDescription +")");
		
			}
		}
		
		return scopes;
	}
	
	private static Map<Integer, String> getEnterpriseCalendarsInfoDB(DSLContext dslContext, Integer enterpriseId) {
		Map<Integer, String> calendars = new HashMap<Integer, String>();
		
		Record enterpriseRecord = dslContext.select().from(ENTERPRISE)
				.where(ENTERPRISE.REGISTRY.eq(enterpriseId))
				.fetchOne();
		
		Integer domain = enterpriseRecord.get(ENTERPRISE.DOMAIN);
		
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.eq(domain))
				.fetchOne();
		
		Integer parentDomain = domainRecord.get(DOMAIN.PARENT);
		
		Result<Record> caledarRecords = null;
		if(null == parentDomain)
			caledarRecords = dslContext.select().from(CALENDAR)
					.where(CALENDAR.DOMAIN.eq(domain))
					.or(CALENDAR.DOMAIN.eq(0))
					.fetch();
		else
			caledarRecords = dslContext.select().from(CALENDAR)
				.where(CALENDAR.DOMAIN.eq(domain))
				.or(CALENDAR.DOMAIN.eq(parentDomain))
				.or(CALENDAR.DOMAIN.eq(0))
				.fetch();
				
		for(Record r : caledarRecords){
			if(null != r.get(CALENDAR.DESCRIPTION))
				calendars.put(r.get(CALENDAR.ID), r.get(CALENDAR.DESCRIPTION));
		}
		
		return calendars;	
	}
	
	private static Map<Integer, String> getEnterpriseActivitiesInfoDB(DSLContext dslContext, Integer enterpriseId) {
		Map<Integer, String> activities = new HashMap<Integer, String>();
		
		Result<Record> enterpriseActivityRecords = dslContext.select().from(ENTERPRISE_ACTIVITY)
				.where(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(enterpriseId))
				.fetch();
		
		for(Record r : enterpriseActivityRecords){
			activities.put(r.get(ENTERPRISE_ACTIVITY.ID), r.get(ENTERPRISE_ACTIVITY.DESCRIPTION));
		}
		
		return activities;
	}
	
	// ------------------------------------ AUX METHODS ----------------------------------------

	private static Byte getIndexSendType(String sendType) {
		switch (sendType) {
		case "EMAIL":
			return (byte)0;
		case "PAPER":
			return (byte)1;
		default:
			return (byte)2;
		}
	}

	private static Byte getModelIndexCosts(String costsModel) {
		switch (costsModel) {
		case "salaryExpense":
			return (byte)0;
		default:
			return (byte)1;
		}
	}

	private static Byte getModelIndexPaysheet(String paysheetModel) {
		switch (paysheetModel) {
		case "salary":
			return (byte)0;
		case "salary_dualColumn":
			return (byte)1;
		case "salary_invoiceSimple":
			return (byte)2;
		default:
			return (byte)3;
		}
	}
	
	private static Byte getModelIndexPaysheetDraft(String paysheetModel) {
		switch (paysheetModel) {
		case "salaryDraft":
			return (byte)0;
		case "salaryDraft_dualColumn":
			return (byte)1;
		case "salaryDraft_invoiceSimple":
			return (byte)2;
		default:
			return (byte)3;
		}
	}
	
	private static String getPaysheetSendType(Byte paysheetSendType) {
		switch (paysheetSendType) {
		case (byte)0 :
			return "EMAIL";
		case (byte)1:
			return "PAPER";
		default:
			return "OTHERS";
		}
	}

	private static String getPaysheetModelDraft(Byte paysheetModelDraft) {
		switch (paysheetModelDraft) {
		case (byte)0:
			return "salaryDraft";
		case (byte)1:
			return "salaryDraft_dualColumn";
		case (byte)2:
			return "salaryDraft_invoiceSimple";
		default:
			return "salaryDraft_invoiceCraGroup";
		}
	}

	private static String getPaysheetModel(Byte paysheetModel) {
		switch (paysheetModel) {
		case (byte)0:
			return "salary";
		case (byte)1:
			return "salary_dualColumn";
		case (byte)2:
			return "salary_invoiceSimple";
		default:
			return "salary_invoiceCraGroup";
		}
	}

	private static String getCostsModel(Byte costsModel) {
		switch (costsModel) {
		case (byte)0 :
			return "salaryExpense";
		default:
			return "salaryExpenseExtended";
		}
	}
	
}
