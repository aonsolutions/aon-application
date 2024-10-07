package net.aonsolutions.aon.bank.nordigen.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;

import net.aonsolutions.aon.bank.nordigen.NordigenAPI;

public class NordigenAgreementUtils {

	// Igual cambiarlo para segun dias que permita el banco y el cliente quiera
	// (esto indica el limite de dias de acceso a la cuenta bancaria desde nordigen)
	private static final Integer MAX_DAYS = 90;

	// **************************************//
	// **Funciones acuerdo de Nordigen*******//
	// **************************************//
	// **************************************//

	public static NordigenAgreement createAgreement(NordigenAccessToken token, String institutionId) {
		return NordigenAPI.createEndUserAgreement(token.getAccess(), MAX_DAYS, MAX_DAYS, null, institutionId);
	}

	// ESTE DE MOMENTO SOLO SE USABA EN TESTS
	public static NordigenAgreement getAgreement(NordigenAccessToken token, String agreementId) {
		return NordigenAPI.getEndUserAgreement(token.getAccess(), agreementId);
	}

	public static boolean handleAgreement(NordigenAgreement agreement) {
		Date today = new Date();
		Date created = agreement.getCreated();
		long diffInMillies = today.getTime() - created.getTime();
		int daysBetween = (int) (TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS));
		if (daysBetween <= 5) {
			return true;
		}
		return false;
	}
}
