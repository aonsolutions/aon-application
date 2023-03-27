package com.esferalia.aon.gwt.payroll.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.URL;

public interface FIEService {

	public static enum Parameter {
		FILE, 
		USER,
		DOMAIN
	}
	
	public static class JsITPart extends JavaScriptObject {
		protected JsITPart() {
		}
		
		public final native int getId() /*-{
			return this.id;
		}-*/;
		
		
		public final native int getDomain() /*-{
			return this.domain;
		}-*/;
		

		public final native byte getType() /*-{
			return this.type;
		}-*/;
		
		
		public final native int getIt() /*-{
			return this.it;
		}-*/;
		

		public final native String getCollegeNumber() /*-{
			return this.collegeNumber;
		}-*/;
		
		public final native byte getConfirmOrderNumber() /*-{
			return this.confirmOrderNumber;
		}-*/;
		
		public final native String getCias() /*-{
			return this.cias;
		}-*/;

		public final native String getDate() /*-{
			return this.date;
		}-*/;
		
		public final native byte getStatus() /*-{
			return this.status;
		}-*/;
		
	}
	
	public static class JsIT extends JavaScriptObject {
		protected JsIT() {
		}
		
		public final native int getId() /*-{
			return this.id;
		}-*/;
		
		
		public final native int getDomain() /*-{
			return this.id;
		}-*/;
		

		public final native int getContract() /*-{
			return this.contract;
		}-*/;
		
		
		public final native int getParent() /*-{
			return this.parent;
		}-*/;
		

		public final native boolean isParent() /*-{
			return this.isParent;
		}-*/;
		

		public final native String getStartDate() /*-{
			return this.startDate;
		}-*/;
		

		public final native String getEndDate() /*-{
			return this.endDate;
		}-*/;
		
		
		public final native String getDescription() /*-{
			return this.description;
		}-*/;
		
		
		public final native byte getTypeLowPart() /*-{
			return this.typeLowPart;
		}-*/;
		

		public final native byte getTypeHighPart() /*-{
			return this.typeHighPart;
		}-*/;
		
		
		public final native byte getMaternityType() /*-{
			return this.maternityType;
		}-*/;
		

		public final native byte getMaternityReason() /*-{
			return this.maternityReason;
		}-*/;
		

		public final native String getFullName() /*-{
			return this.fullName;
		}-*/;
		

		public final native JsArray<JsITPart> getITParts() /*-{
			return this.itParts;
		}-*/;
		
		public final native double getDailyCGCBase() /*-{
			return this.dailyCGCBase;
		}-*/;

		public final native double getDailyCGPBase() /*-{
			return this.dailyCGPBase;
		}-*/;
		
		public final native double getDailyREGBase() /*-{
			return this.dailyREGBase;
		}-*/;
		
	}

	public static class JsITEmployee extends JavaScriptObject {
		protected JsITEmployee() {
		}
		
		public final native byte getStatus() /*-{
			return this.status;
		}-*/;
		
		
		public final native JsArray<JsIT> getITs() /*-{
			return this.its;
		}-*/;
		
		
		public final native JsContractInfo getContractInfo() /*-{
			return this.contractInfo;
		}-*/;
		
		
		public final native JsEmployeeInfo getEmployeeInfo() /*-{
			return this.employeeInfo;
		}-*/;
		
	}
	
	public static class JsEmployeeInfo extends JavaScriptObject {

		
		protected JsEmployeeInfo() {		
		}
		
		public final native String getAccount() /*-{
			return this.account;
		}-*/;
		
		
		public final native String getAddresNum() /*-{
			return this.addresNum;
		}-*/;
		
		
		public final native String getAddress() /*-{
			return this.address;
		}-*/;
		
		
		public final native String getAddressCity() /*-{
			return this.addressCity;
		}-*/;
		
		
		public final native String getAddressInfo() /*-{
			return this.addressInfo;
		}-*/;
		
		
		public final native String getAddressProvinces() /*-{
			return this.addressProvinces;
		}-*/;
		
		
		public final native String getAddressZip() /*-{
			return this.addressZip;
		}-*/;
		
		
		public final native String getBic() /*-{
			return this.bic;
		}-*/;
		
		
		public final native String getBirthdate() /*-{
			return this.birthdate;
		}-*/;
		
		
		public final native byte getCivilStatus() /*-{
			return this.civilStatus;
		}-*/;
		
		
		public final native boolean getContractActive() /*-{
			return this.contractActive;
		}-*/;
		
		
		public final native int getContractId() /*-{
			return this.contractId;
		}-*/;
		
		
		public final native String getDocument() /*-{
			return this.document;
		}-*/;
		
		
		public final native byte getDocumentType() /*-{
			return this.documentType;
		}-*/;
		
		
		public final native int getDomain() /*-{
			return this.domain;
		}-*/;
		
		
		public final native String getEmail() /*-{
			return this.email;
		}-*/;
		
		
		public final native int getEmailId() /*-{
			return this.emailId;
		}-*/;
		
		
		public final native int getEmployeeId() /*-{
			return this.employeeId;
		}-*/;
		
		
		public final native byte getGender() /*-{
			return this.gender;
		}-*/;
		
		
		public final native int getGeozoneId() /*-{
			return this.geozoneId;
		}-*/;
		
		
		public final native boolean getIsFullTime() /*-{
			return this.isFullTime;
		}-*/;
		
		
		public final native String getMobile() /*-{
			return this.mobile;
		}-*/;
		
		
		public final native int getMobileId() /*-{
			return this.mobileId;
		}-*/;
		
		
		public final native String getName() /*-{
			return this.name;
		}-*/;
		
		
		public final native String getNationality() /*-{
			return this.nationality;
		}-*/;
		
		
		public final native int getPaymethodId() /*-{
			return this.paymethodId;
		}-*/;
		
		
		public final native String getPayMethodType() /*-{
			return this.payMethodType;
		}-*/;
		
		
		public final native byte getPayMethodTypeB() /*-{
			return this.payMethodTypeB;
		}-*/;
		
		
		public final native String getPhone() /*-{
			return this.phone;
		}-*/;
		
		
		public final native int getPhoneId() /*-{
			return this.phoneId;
		}-*/;
		
		
		public final native int getRaddressId() /*-{
			return this.raddressId;
		}-*/;
		
		
		public final native int getRbankId() /*-{
			return this.rbankId;
		}-*/;
		
		
		public final native int getRpaymethodId() /*-{
			return this.rpaymethodId;
		}-*/;
		
		
		public final native String getSecondSurName() /*-{
			return this.secondSurName;
		}-*/;
		
		
		public final native String getSsNumber() /*-{
			return this.ssNumber;
		}-*/;
		
		
		public final native String getStreetType() /*-{
			return this.streetType;
		}-*/;
		
		
		public final native String getSurName() /*-{
			return this.surName;
		}-*/;
		
		
		

	}
	public static class JsContractInfo extends JavaScriptObject {

		
		protected JsContractInfo() {		
		}
		
		public final native int getActivityId() /*-{
			return this.activityId;
		}-*/;
		
		public final native String getEnterpriseCIF() /*-{
			return this.enterpriseCIF;
		}-*/;
		
		public final native String getEnterpriseName() /*-{
			return this.enterpriseName;
		}-*/;
		
		public final native int getCccId() /*-{
			return this.cccId;
		}-*/;
		
		public final native String getCompleteCCC() /*-{
			return this.completeCCC;
		}-*/;
		
		public final native byte getCccType() /*-{
			return this.cccType;
		}-*/;
		
		public final native int getWorkplaceId() /*-{
			return this.workplaceId;
		}-*/;
		
		public final native String getWorkplaceZIP() /*-{
			return this.workplaceZIP;
		}-*/;
		
		public final native String getWorkplaceFullAddress() /*-{
			return this.workplaceFullAddress;
		}-*/;
		
		public final native String getContractType() /*-{
			return this.contractType;
		}-*/;
		
		public final native int getContractModel() /*-{
			return this.contractModel;
		}-*/;
		
		public final native String getStartDate() /*-{
			return this.startDate;
		}-*/;
		
		public final native String getEndDate() /*-{
			return this.endDate;
		}-*/;
		
		public final native String getSeniorityDate() /*-{
			return this.seniorityDate;
		}-*/;
		
		public final native int getAgreementId() /*-{
			return this.agreementId;
		}-*/;
		
		public final native int getAgreementLevelId() /*-{
			return this.agreementLevelId;
		}-*/;
		
		public final native String getAgreementCategory() /*-{
			return this.agreementCategory;
		}-*/;
		
		public final native String getQuoteGroup() /*-{
			return this.quoteGroup;
		}-*/;
		
		public final native String getOcupation() /*-{
			return this.ocupation;
		}-*/;
		
		public final native byte getJourneyType() /*-{
			return this.journeyType;
		}-*/;
		
		public final native byte getSsRegimen() /*-{
			return this.ssRegimen;
		}-*/;
		
		public final native int getContractId() /*-{
			return this.contractId;
		}-*/;
		
		public final native int getContracttypeId() /*-{
			return this.contracttypeId;
		}-*/;
		
		public final native int getQuotegroupId() /*-{
			return this.quotegroupId;
		}-*/;
		
		public final native int getOcupationId() /*-{
			return this.ocupationId;
		}-*/;
		
		public final native int getJourneytypeId() /*-{
			return this.journeytypeId;
		}-*/;
		
		public final native int getContractmodelId() /*-{
			return this.contractmodelId;
		}-*/;
		
		public final native int getRetaId() /*-{
			return this.retaId;
		}-*/;
		
		public final native int getContractJourneyDuration() /*-{
			return this.contractJourneyDuration;
		}-*/;
		
		public final native String getOldStartDate() /*-{
			return this.oldStartDate;
		}-*/;
		
		public final native String getOldEndDate() /*-{
			return this.oldEndDate;
		}-*/;
		
		public final native boolean getHasPayroll() /*-{
			return this.hasPayroll;
		}-*/;
		
		public final native String getPayrollDate() /*-{
			return this.payrollDate;
		}-*/;
		
	}
	
	public static final String FIE_URL = URL
			.encode(GWT.getModuleBaseURL() + "fie");
	
	
	
	
}
