package solutions.aon.circe;

import java.util.Date;

public interface DueParserListener {
	default void onPeriod(Date date) {
	}

	default void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
			String economicActivityDescription, String regime, String fullCCC) {
	}

	default void onAgreement(String code) {
	}

	default void onEmployee(String nss, String name) {
	}

	default void onEmployeeOtherInfo(String documentType, String document, String gender, Date birthDate) {
	}

	default void onEmployeePerido(String ssNum, String ccc, String gc, Date startDate, Date endDate) {
		onEmployeePerido(ssNum, ccc, startDate, endDate);
		onEmployeeQuoteGroup(gc);
	}

	default void onEmployeePerido(String ssNum, String ccc, Date startDate, Date endDate) {
	}

	default void onEmployeeQuoteGroup(String group) {
	}

	default void onEmployeeQuoteGroup(String group, boolean monthly ) {
	}

	default void onEmployeeQuoteTypes(Double it, Double ims, Double unemployment) {
	}

	default void onNoEmployeeQuotePEC(String ssNum, String ccc, Date start, Date end) {
	}
	
	default void onEmployeeQuoteTRL(String ssNum, String ccc, String description, Date start, Date end) {
	}
	
	default void onEmployeeQuotePEC(String ssNum, String ccc, String code, String description, String portTipo,
			String quota, String colective, Date start, Date end) {
		onEmployeeQuotePEC(ssNum, ccc, code, description, portTipo, quota, start, end);
	}

	default void onEmployeeQuotePEC(String ssNum, String ccc, String code, String description, String portTipo,
			String quota, Date start, Date end) {
	}

	default void onEmployeeBenefitsLoss(String ssNum, String ccc, String cause, Date start, Date end) {
	}

	
	// --------------------------------------------------------------- Contract
	
	default void onContractType(String contractType) {
	}
	
	default void onRlce(String rlce) {
	}

	default void onContractStart(Date start) {
	}

	default void onContractEnd(Date end) {
	}

	default void onContractPartialCoeficient(String coeficient) {
	}

	default void onContractQuoteGroup(String quoteGroup) {
	}

	default void onContractInactivityType(String inactivityType) {
	}

	default void onContractOcupation(String ocupation) {
	}

	default void onContractAgrarianQuoteModality(String quoteModality) {
	}

	default void onContractAgrarianRealJourney(String realJourney) {
	}

	default void onContractAgrarianRealJourneyProvided(String realJourneyProvided) {
	}

	// ------------------------------------------------------------------- TGSS
	
	default void onAuthorized(Integer number, String name) {
	}

}