package com.esferalia.aon.in.payroll.tgss.idc;

import java.util.Date;

public interface Listener {
	void onPeriod(Date date);

	void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode, String economicActivityDescription, String regime, String fullCCC);
	
	void onAgreement(String code);
	
	void onEmployee(String nss, String name);
	void onEmployeeOtherInfo(String documentType, String document, String gender, Date birthDate);
	void onEmployeePerido(Date startDate, Date endDate);
	void onEmployeeQuoteGroup(String group);
	void onEmployeeQuoteTypes(double it, double ims, double unemployment);
	void onEmployeeQuotePEC(String code, String description, String portTipo, String quota, String colectivo, String legislacion );
	void onEmployeeQuotePEC(String ssNum, String ccc, String code, String description, String portTipo, String quota, Date start, Date end );
	
	void onContractType(String contractType);
	void onContractStart(Date start);
	void onContractEnd(Date end);
	void onContractPartialCoeficient(String coeficient);
	void onContractQuoteGroup(String quoteGroup);
	void onContractInactivityType(String inactivityType);
	void onContractOcupation(String ocupation);
	void onContractAgrarianQuoteModality(String quoteModality);
	void onContractAgrarianRealJourney(String realJourney);
	void onContractAgrarianRealJourneyProvided(String realJourneyProvided);
}
