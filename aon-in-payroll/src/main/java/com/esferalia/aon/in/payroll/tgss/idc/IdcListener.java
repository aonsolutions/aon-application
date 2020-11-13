package com.esferalia.aon.in.payroll.tgss.idc;

import java.util.Date;
import java.util.List;

public interface IdcListener {
	default void onPeriod(Date date) {
	};

	default void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
			String economicActivityDescription, String regime, String fullCCC) {
	};

	default void onAgreement(String code) {
	};

	default void onEmployee(String nss, String name) {
	};

	default void onEmployeeOtherInfo(String documentType, String document, String gender, Date birthDate) {
	};

	default void onEmployeePerido(String ssNum, String ccc, Date startDate, Date endDate) {
	};

	default void onEmployeeQuoteGroup(String group) {
	};

	default void onEmployeeQuoteTypes(double it, double ims, double unemployment) {
	};

//	default void onEmployeeQuotePEC(String code, String description, String portTipo, String quota, String colectivo,
//			String legislacion) {
//	};

	default void onEmployeeQuotePEC(String ssNum, String ccc, String code, String description, String portTipo,
			String quota, Date start, Date end) {
	};

	
	// --------------------------------------------------------------- Contract
	
	default void onContractType(String contractType) {
	};

	default void onContractStart(Date start) {
	};

	default void onContractEnd(Date end) {
	};

	default void onContractPartialCoeficient(String coeficient) {
	};

	default void onContractQuoteGroup(String quoteGroup) {
	};

	default void onContractInactivityType(String inactivityType) {
	};

	default void onContractOcupation(String ocupation) {
	};

	default void onContractAgrarianQuoteModality(String quoteModality) {
	};

	default void onContractAgrarianRealJourney(String realJourney) {
	};

	default void onContractAgrarianRealJourneyProvided(String realJourneyProvided) {
	};
}
