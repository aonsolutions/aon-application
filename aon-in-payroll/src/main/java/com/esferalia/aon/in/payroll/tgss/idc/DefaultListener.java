package com.esferalia.aon.in.payroll.tgss.idc;

import java.util.Date;
import java.util.List;

public class DefaultListener implements Listener{

	@Override
	public void onPeriod(Date date) {}

	@Override
	public void onAgreement(String code) {}

	@Override
	public void onEmployee(String nss, String name) {}

	@Override
	public void onEmployeePerido(String ssNum, String ccc, Date startDate, Date endDate) {}

	@Override
	public void onEmployeeQuoteGroup(String group) {}

	@Override
	public void onEmployeeQuoteTypes(double it, double ims, double unemployment) {}

	@Override
	public void onEmployeeQuotePEC(String code, String description, String portTipo, String quota, String colectivo, String legislacion) {}

	@Override
	public void onContractType(String contractType) {}

	@Override
	public void onContractStart(Date start) {}

	@Override
	public void onContractEnd(Date end) {}

	@Override
	public void onContractPartialCoeficient(String coeficient) {}

	@Override
	public void onContractQuoteGroup(String quoteGroup) {}

	@Override
	public void onContractInactivityType(String inactivityType) {}

	@Override
	public void onContractOcupation(String ocupation) {}

	@Override
	public void onContractAgrarianQuoteModality(String quoteModality) {}

	@Override
	public void onContractAgrarianRealJourney(String realJourney) {}

	@Override
	public void onContractAgrarianRealJourneyProvided(String realJourneyProvided) {}

	@Override
	public void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode, String economicActivityDescription, String regime, String fullCCC) {}

	@Override
	public void onEmployeeOtherInfo(String documentType, String document, String gender, Date birthDate) {}

	@Override
	public void onEmployeeQuotePEC(String ssNum, String ccc, String code, String description, String portTipo, String quota, Date start, Date end) {}

	@Override
	public void onEmployeeQuotePECList(List<EmployeeQuotePEC> employeeQuotePEC) {}

}