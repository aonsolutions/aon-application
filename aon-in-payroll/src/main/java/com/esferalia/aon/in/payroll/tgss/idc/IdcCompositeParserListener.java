package com.esferalia.aon.in.payroll.tgss.idc;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class IdcCompositeParserListener implements IdcParserListener {
	
	private Collection<IdcParserListener> idcParserListeners = new LinkedList<IdcParserListener>();

	public IdcCompositeParserListener add(IdcParserListener idcParserListener) {
		idcParserListeners.add(idcParserListener);
		return this;
	}

	@Override
	public void onPeriod(Date date) {
		idcParserListeners.forEach(l -> l.onPeriod(date));
	}

	@Override
	public void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
			String economicActivityDescription, String regime, String fullCCC) {
		idcParserListeners.forEach(l -> l.onEnterprise(socialReason, ccc, nif, economicActivityCode, economicActivityDescription, regime,
				fullCCC));
	}

	@Override
	public void onAgreement(String code) {
		idcParserListeners.forEach(l -> l.onAgreement(code));
	}

	@Override
	public void onEmployee(String nss, String name) {
		idcParserListeners.forEach(l -> l.onEmployee(nss, name));
	}

	@Override
	public void onEmployeeOtherInfo(String documentType, String document, String gender, Date birthDate) {
		idcParserListeners.forEach(l -> l.onEmployeeOtherInfo(documentType, document, gender, birthDate));
	}

	@Override
	public void onEmployeePerido(String ssNum, String ccc, String gc, Date startDate, Date endDate) {
		idcParserListeners.forEach(l -> l.onEmployeePerido(ssNum, ccc, gc, startDate, endDate));
	}

	@Override
	public void onEmployeePerido(String ssNum, String ccc, Date startDate, Date endDate) {
		idcParserListeners.forEach(l -> l.onEmployeePerido(ssNum, ccc, startDate, endDate));
	}

	@Override
	public void onEmployeeQuoteGroup(String group) {
		idcParserListeners.forEach(l -> l.onEmployeeQuoteGroup(group));
	}

	@Override
	public void onEmployeeQuoteTypes(Double it, Double ims, Double unemployment) {
		idcParserListeners.forEach(l -> l.onEmployeeQuoteTypes(it, ims, unemployment));
	}

	@Override
	public void onNoEmployeeQuotePEC(String ssNum, String ccc, Date start, Date end) {
		idcParserListeners.forEach(l -> l.onNoEmployeeQuotePEC(ssNum, ccc, start, end));
	}

	@Override
	public void onEmployeeQuotePEC(String ssNum, String ccc, String code, String description, String portTipo,
			String quota, String colective, Date start, Date end) {
		idcParserListeners.forEach(l -> l.onEmployeeQuotePEC(ssNum, ccc, code, description, portTipo, quota, colective, start, end));
	}

	@Override
	public void onEmployeeQuotePEC(String ssNum, String ccc, String code, String description, String portTipo,
			String quota, Date start, Date end) {
		idcParserListeners.forEach(l -> l.onEmployeeQuotePEC(ssNum, ccc, code, description, portTipo, quota, start, end));
	}

	@Override
	public void onContractType(String contractType) {
		idcParserListeners.forEach(l -> l.onContractType(contractType));
	}
	
	@Override
	public void onRlce(String rlce) {
		idcParserListeners.forEach(l -> l.onRlce(rlce));
	}

	@Override
	public void onContractStart(Date start) {
		idcParserListeners.forEach(l -> l.onContractStart(start));
	}

	@Override
	public void onContractEnd(Date end) {
		idcParserListeners.forEach(l -> l.onContractEnd(end));
	}

	@Override
	public void onContractPartialCoeficient(String coeficient) {
		idcParserListeners.forEach(l -> l.onContractPartialCoeficient(coeficient));
	}

	@Override
	public void onContractQuoteGroup(String quoteGroup) {
		idcParserListeners.forEach(l -> l.onContractQuoteGroup(quoteGroup));
	}

	@Override
	public void onContractInactivityType(String inactivityType) {
		idcParserListeners.forEach(l -> l.onContractInactivityType(inactivityType));
	}

	@Override
	public void onContractOcupation(String ocupation) {
		idcParserListeners.forEach(l -> l.onContractOcupation(ocupation));
	}

	@Override
	public void onContractAgrarianQuoteModality(String quoteModality) {
		idcParserListeners.forEach(l -> l.onContractAgrarianQuoteModality(quoteModality));
	}

	@Override
	public void onContractAgrarianRealJourney(String realJourney) {
		idcParserListeners.forEach(l -> l.onContractAgrarianRealJourney(realJourney));
	}

	@Override
	public void onContractAgrarianRealJourneyProvided(String realJourneyProvided) {
		idcParserListeners.forEach(l -> l.onContractAgrarianRealJourneyProvided(realJourneyProvided));
	}
	
	@Override
	public void onEmployeeBenefitsLoss(String ssNum, String ccc, String cause, Date start, Date end) {
		idcParserListeners.forEach(l -> l.onEmployeeBenefitsLoss(ssNum, ccc, cause, start, end));
	}
	
	
	
	
}
