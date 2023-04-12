package com.esferalia.aon.in.payroll.ivl;

import java.io.IOException;
import java.util.Date;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
public interface IvlParserListener {
	
	
	


	default void onPeriod(Date date) {

	};

	default void onEnterprise(String socialReason, String ccc, String regime, String nif, String economicActivityCode,
			String economicActivityDescription, String fullCCC) {
 
	};

	default void onEnterpriseAddress(String city, String address, String cp, String economicActivityCode) {

	};

	default void onEnterprisePeriod(String startDate, String endDate) {

	};

	default void onAtTypes(String it, String ims, String total) {

	};
	// LISTENER PRIMERA PARTE PDF

	default void onEmployee(String naf, String docType, String docNum, String fullName) {

	};

	default void onEmployeeIdent(String nss, String ident) {

	};

	default void onEmployeeSituation(String situation, String start, String effect, String startSit, String effectSit,
			String gc, String tc, String it, String ep, String ims, String total, String cotDays, String clv) {

	};

}


