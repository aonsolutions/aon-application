package com.esferalia.aon.gwt.common.client.json;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.BookingResume;
import com.esferalia.aon.occam.api.model.security.DomainTypeInfo;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;

public class DomainCompanyJSON {
	
	private static DateTimeFormat formatFullDate = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public static List<DomainCompany> parseDomainCompanyJSONArr(String json) {
		JSONArray arr = JSONParser.parseStrict(json).isArray();
		LinkedList<DomainCompany> list = new LinkedList<>();
		
		for(Integer i = 0; i < arr.size(); i++) {
			list.add(parseDomainCompanyJSON(arr.get(i).isObject()));
		}
 		
		return list;
	}
	
	private static DomainCompany parseDomainCompanyJSON(JSONObject json) {
		Window.alert("parseDomainCompanyJSON:\n" + json);
		if(json == null) return new DomainCompany();
		return new DomainCompany()
				.setSchema(JsonGWTUtils.getString(json, IJsonNames.SCHEMA))
				.setDomain(parseDomainJSON(json.get(IJsonNames.DOMAIN)))
				.setCompany(parseCompanyJSON(json.get(IJsonNames.COMPANY)))
		;
	}
	
	public static JSONObject domainCompanyToJSON(DomainCompany domainCompany) {
		if(domainCompany == null) return new JSONObject();
		JSONObject jsonObject = new JSONObject();
		
		if(null != domainCompany.getSchema())
			jsonObject.put(IJsonNames.SCHEMA, new JSONString(domainCompany.getSchema()));
			
		jsonObject
			.put(IJsonNames.DOMAIN, domainToJSON(domainCompany.getDomain()))
			.isObject()
			.put(IJsonNames.COMPANY, companyToJSON(domainCompany.getCompany()))
			.isObject()
			;	
		
		return jsonObject;
	}

	private static Domain parseDomainJSON(JSONValue json) {
		if(json == null) return new Domain();
		
		JSONObject jsonObj = json.isObject();
		
		return new Domain()
			.setId(JsonGWTUtils.getInteger(jsonObj,IJsonNames.ID))
			.setName(JsonGWTUtils.getString(jsonObj, IJsonNames.NAME))
			.setDescription(JsonGWTUtils.getString(jsonObj, IJsonNames.DESCRIPTION))
			.setOwner(JsonGWTUtils.getString(jsonObj, IJsonNames.OWNER))
			.setParentId(JsonGWTUtils.getInteger(jsonObj, IJsonNames.PARENT_ID))
			.setDomainType( DomainType.safeValueOf( JsonGWTUtils.getString(jsonObj,IJsonNames.DOMAIN_TYPE) ))
			.setEnableHeredity(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.ENABLE_HEREDITY))
			.setDomainManagement(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.DOMAIN_MANAGEMENT))
			.setDisableDomainManagement(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.DISABLE_DOMAIN_MANAGEMENT))
			.setActive(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.ACTIVE))
			.setScope(JsonGWTUtils.getInteger(jsonObj,IJsonNames.SCOPE))
			.setMaxDefinedUsers( JsonGWTUtils.getInteger(jsonObj,IJsonNames.MAX_DEFINED_USERS))
			.setDefinedUsers( JsonGWTUtils.getInteger(jsonObj,IJsonNames.DEFINED_USERS))
			.setMaxDocumentSize( JsonGWTUtils.getInteger(jsonObj,IJsonNames.MAX_DOCUMENT_SIZE))
			.setMaxTotalDocumentSize( JsonGWTUtils.getInteger(jsonObj,IJsonNames.MAX_TOTAL_DOCUMENT_SIZE))
			.setLastAccessUser(JsonGWTUtils.getString(jsonObj, IJsonNames.LAST_ACCESS_USER))
			.setLastAccessDate(JsonGWTUtils.getDate(jsonObj, IJsonNames.LAST_ACCESS_DATE))
			.setExpirationDate(JsonGWTUtils.getDate(jsonObj, IJsonNames.EXPIRATION_DATE))
			.setCreationUser(JsonGWTUtils.getString(jsonObj, IJsonNames.CREATION_USER))
			.setCreationDate(JsonGWTUtils.getDate(jsonObj, IJsonNames.CREATION_DATE))
			.setModificationUser(JsonGWTUtils.getString(jsonObj, IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonGWTUtils.getDate(jsonObj, IJsonNames.MODIFICATION_DATE))
			.setAonCustomer(JsonGWTUtils.getInteger(jsonObj, IJsonNames.AON_CUSTOMER))
			.setAonStatus(AonStatus.safeValueOf(JsonGWTUtils.getString(jsonObj,IJsonNames.AON_STATUS)))
		;
	}
	
	public static JSONObject domainToJSON(Domain domain) {
		if(domain == null) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, new JSONString(domain.getId().toString()))
			.isObject()
			.put(IJsonNames.NAME,new JSONString(domain.getName()))
			.isObject()
			.put(IJsonNames.DESCRIPTION, new JSONString(domain.getDescription()))
			.isObject()
			.put(IJsonNames.OWNER, new JSONString(domain.getOwner()))
			.isObject()
			.put(IJsonNames.PARENT_ID, new JSONString(domain.getParentId().toString()))
			.isObject()
			.put(IJsonNames.DOMAIN_TYPE, new JSONString(domain.getDomainType() == null?null:domain.getDomainType().toString()))
			.isObject()
			.put(IJsonNames.ENABLE_HEREDITY, new JSONString(domain.isEnableHeredity() + ""))
			.isObject()
			.put(IJsonNames.DOMAIN_MANAGEMENT, new JSONString(domain.isDomainManagement() + ""))
			.isObject()
			.put(IJsonNames.DISABLE_DOMAIN_MANAGEMENT, new JSONString(domain.isDisableDomainManagement() + ""))
			.isObject()
			.put(IJsonNames.ACTIVE, new JSONString(domain.isActive() + ""))
			.isObject()
			.put(IJsonNames.SCOPE, new JSONString(domain.getScope()+ ""))
			.isObject()
			.put(IJsonNames.MAX_DEFINED_USERS, new JSONString(domain.getMaxDefinedUsers() + ""))
			.isObject()
			.put(IJsonNames.DEFINED_USERS, new JSONString(domain.getDefinedUsers()+ ""))
			.isObject()
			.put(IJsonNames.MAX_DOCUMENT_SIZE, new JSONString(domain.getMaxDocumentSize() + ""))
			.isObject()
			.put(IJsonNames.MAX_TOTAL_DOCUMENT_SIZE,new JSONString( domain.getMaxTotalDocumentSize() + ""))
			.isObject()
			.put(IJsonNames.LAST_ACCESS_USER, new JSONString(domain.getLastAccessUser()))
			.isObject()
			.put(IJsonNames.LAST_ACCESS_DATE, new JSONString(formatFullDate.format(domain.getLastAccessDate())))
			.isObject()
			.put(IJsonNames.EXPIRATION_DATE, new JSONString(formatFullDate.format(domain.getExpirationDate())))
			.isObject()
			.put(IJsonNames.CREATION_USER, new JSONString(domain.getCreationUser()))
			.isObject()
			.put(IJsonNames.CREATION_DATE, new JSONString(formatFullDate.format(domain.getCreationDate())))
			.isObject()
			.put(IJsonNames.MODIFICATION_USER, new JSONString(domain.getModificationUser()))
			.isObject()
			.put(IJsonNames.MODIFICATION_DATE, new JSONString(formatFullDate.format(domain.getModificationDate())))
			.isObject()
			.put(IJsonNames.AON_CUSTOMER, new JSONString(domain.getAonCustomer() + ""))
			.isObject()
			.put(IJsonNames.AON_STATUS,new JSONString(domain.getAonStatus() == null?null:domain.getAonStatus().toString()))
			.isObject()
			;
	}

	private static Company parseCompanyJSON(JSONValue json) {
		if(json == null) return new Company();
		
		JSONObject jsonObj = json.isObject();
		
		return new Company()
				.copy(parseRegistryJSON(jsonObj))
				.setActive(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.ACTIVE))
				.setSurcharge(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.SURCHARGE))
				.setWithholding(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.WITHHOLDING))
				.setVatAccrualPayment(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.VAT_ACCRUAL_PAYMENT))
				.seteInvoice(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.E_INVOICE));
	}
	
	private static JSONObject companyToJSON(Company company) {
		return toJSONRegistry(company)
				.put(IJsonNames.ACTIVE, new JSONString(company.isActive() + ""))
				.isObject()
				.put(IJsonNames.SURCHARGE, new JSONString(company.isSurcharge() + ""))
				.isObject()
				.put(IJsonNames.WITHHOLDING, new JSONString(company.isWithholding() + ""))
				.isObject()
				.put(IJsonNames.VAT_ACCRUAL_PAYMENT, new JSONString(company.isVatAccrualPayment() + ""))
				.isObject()
				.put(IJsonNames.E_INVOICE,new JSONString( company.iseInvoice() + ""))
				.isObject();
	}
	
	private static Registry parseRegistryJSON(JSONObject jsonObj) {
		if(jsonObj == null) {
			return new Registry();
		}
		
		return new Registry() 
			.setId(JsonGWTUtils.getInteger(jsonObj, IJsonNames.ID))
			.setDomain(parseDomainJSON(JsonGWTUtils.getJSONObject(jsonObj, IJsonNames.DOMAIN)))
			.setDocument(JsonGWTUtils.getString(jsonObj,IJsonNames.DOCUMENT))
			.setDocumentCountry(Country.safeValueOf(JsonGWTUtils.getString(jsonObj,IJsonNames.DOCUMENT_COUNTRY)))
			.setDocumentType(DocumentType.safeValueOf(JsonGWTUtils.getString(jsonObj,IJsonNames.DOCUMENT_TYPE)))
			.setName(JsonGWTUtils.getString(jsonObj,IJsonNames.NAME))
			.setAlias(JsonGWTUtils.getString(jsonObj,IJsonNames.ALIAS))
			.setLegalPerson(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.LEGAL_PERSON))
			.setNationality(Country.safeValueOf(JsonGWTUtils.getString(jsonObj,IJsonNames.NATIONALITY)))
			.setConfidential(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.CONFIDENTIAL))
			.setGlobal(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.GLOBAL) == null ? false : JsonGWTUtils.getBoolean(jsonObj, IJsonNames.GLOBAL))
			.setDirty(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.DIRTY) == null ? false : JsonGWTUtils.getBoolean(jsonObj, IJsonNames.DIRTY));
	}
	
	private static JSONObject toJSONRegistry(Company registry) {
		if(registry == null || registry.isEmpty()) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, new JSONString(registry.getId().toString()))
			.isObject()
			.put(IJsonNames.DOMAIN, domainToJSON(registry.getDomain()))
			.isObject()
			.put(IJsonNames.DOCUMENT, new JSONString(registry.getDocument()))
			.isObject()
			.put(IJsonNames.DOCUMENT_COUNTRY, new JSONString(registry.getDocumentCountry() != null ? registry.getDocumentCountry().getIso2(): null))
			.isObject()
			.put(IJsonNames.DOCUMENT_TYPE, new JSONString(registry.getDocumentType() != null ? registry.getDocumentType().name(): null))
			.isObject()
			.put(IJsonNames.NAME, new JSONString(registry.getName()))
			.isObject()
			.put(IJsonNames.ALIAS, new JSONString(registry.getAlias()))
			.isObject()
			.put(IJsonNames.LEGAL_PERSON, new JSONString(registry.isLegalPerson() + ""))
			.isObject()
			.put(IJsonNames.NATIONALITY, new JSONString(registry.getNationality() != null ? registry.getNationality().getIso2() : null))
			.isObject()
			.put(IJsonNames.CONFIDENTIAL, new JSONString(registry.isConfidential() + ""))
			.isObject();
	}
	
	public static Booking parseBookingJSON(String json) {
		JSONObject jsonObj = JSONParser.parseStrict(json).isObject();
		
		List<AonApp> apps = new LinkedList<>();
		List<AonApp> parentApps = new LinkedList<>();
		
		JSONArray appsArr = JsonGWTUtils.getJSONArray(jsonObj, IJsonNames.APPS);
		for(int i=0; i<appsArr.size(); i++)
			apps.add(AonApp.safeValueOf(appsArr.get(i).toString()));
		
		JSONArray parentAppsArr = JsonGWTUtils.getJSONArray(jsonObj, IJsonNames.PARENT_APPS);
		for(int i=0; i<parentAppsArr.size(); i++)
			parentApps.add(AonApp.safeValueOf(parentAppsArr.get(i).toString()));
		
		return new Booking()
			.setDomain(parseDomainJSON(jsonObj.get(IJsonNames.DOMAIN)))
			.setCompany(parseCompanyJSON(jsonObj.get(IJsonNames.COMPANY)))
			.setType(DomainType.safeValueOf(JsonGWTUtils.getString(jsonObj, IJsonNames.TYPE)))
			.setApps(apps)
			.setParentApps(parentApps)
			.setNumberOfUsers(JsonGWTUtils.getInteger(jsonObj, IJsonNames.NUMBER_OF_USERS))
			.setPayer(JsonGWTUtils.getString(jsonObj, IJsonNames.PAYER));
	}
	
	public static JSONObject bookingToJson(Booking booking) {
		JSONArray apps = new JSONArray();
		JSONArray parentApps = new JSONArray();
		
		for(int i=0; i<booking.getApps().size(); i++)
			apps.set(i, new JSONString(booking.getApps().get(i).name()));
		
		for(int i=0; i<booking.getParentApps().size(); i++)
			parentApps.set(i, new JSONString(booking.getParentApps().get(i).name()));

		return new JSONObject()
			.put(IJsonNames.DOMAIN, domainToJSON(booking.getDomain()))
			.isObject()
			.put(IJsonNames.COMPANY, companyToJSON(booking.getCompany()))
			.isObject()
			.put(IJsonNames.TYPE, new JSONString(booking.getType() != null ? booking.getType().name() : null))
			.isObject()
			.put(IJsonNames.APPS, apps) 
			.isObject()
			.put(IJsonNames.PARENT_APPS, parentApps) 
			.isObject()
			.put(IJsonNames.NUMBER_OF_USERS, new JSONString(booking.getNumberOfUsers() + ""))
			.isObject()
			.put(IJsonNames.PAYER, new JSONString(booking.getPayer()))
			.isObject()
			.put(IJsonNames.RESUME, booking.getResume() != null ? bookingResume(booking.getResume()): null)
			.isObject();
	}
	
	private static JSONObject bookingResume(BookingResume resume) {
		JSONObject o = new JSONObject();
		if(resume != null) { 
			JSONObject domain = new JSONObject();
			resume.getDomainTypes().keySet().forEach(r ->{
				DomainTypeInfo dti = resume.getDomainTypes().get(r);
				JSONObject oa = new JSONObject();
				oa.put("number", new JSONString(dti.getNumber() + ""));
				
				JSONObject apps = new JSONObject();
				dti.getChildApps().keySet().forEach(r2 -> apps.put(r2.name(), new JSONString(dti.getChildApps().get(r2) + "")));
				oa.put(IJsonNames.APPS, apps);

				oa.put("childs", childJson(dti.getChilds()));

				domain.put(r.name(), oa);	
			});
			o.put(IJsonNames.DOMAIN, domain);

			JSONObject user = new JSONObject();
			resume.getUserTypes().keySet().forEach(r -> user.put(r.name(), new JSONString(resume.getUserTypes().get(r) + "")));
			user.put("childDefinedUsers", new JSONString(resume.getChildDefinedUsers() + ""));
			user.put("childBillingUsers", new JSONString(resume.getChildBillingUsers() + ""));
			
			o.put(IJsonNames.USER, user);
		}
		return o;
	}
	
	private static JSONArray childJson(List<Domain> childs) {
		JSONArray array = new JSONArray();
		
		for(int i=0; i<childs.size(); i++) {
			JSONArray apps = new JSONArray();
			Domain d = childs.get(i);
			
			for(int j=0; j<d.getApps().size(); j++)
				apps.set(j, new JSONString(d.getApps().get(j).getApp().name()));
			
			JSONObject json = new JSONObject();
			json.put(IJsonNames.ID, new JSONString(d.getId().toString()));
			json.put(IJsonNames.NAME, new JSONString(d.getName()));
			json.put(IJsonNames.DESCRIPTION, new JSONString(d.getDescription()));
			json.put(IJsonNames.APPS, apps);
			json.put(IJsonNames.MAX_DEFINED_USERS, new JSONString(d.getMaxDefinedUsers() + ""));
			
			array.set(i, json);
		}
		
		return array;
	}
}
