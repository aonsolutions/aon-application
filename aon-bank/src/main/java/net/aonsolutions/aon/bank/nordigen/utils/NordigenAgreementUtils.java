package net.aonsolutions.aon.bank.nordigen.utils;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;

import net.aonsolutions.aon.bank.nordigen.NordigenAPI;

public class NordigenAgreementUtils {

	//**************************************//
		//**Funciones acuerdo de Nordigen*******//
		//**************************************//
		//**************************************//
		//Igual cambiarlo para segun dias que permita el banco y el cliente quiera (esto indica el limite de dias de acceso a la cuenta bancaria desde nordigen)
		private static final Integer MAX_DAYS = 90;

		public static NordigenAgreement createAgreement(NordigenAccessToken token, String institutionId)  {
			return NordigenAPI.createEndUserAgreement(token.getAccess(), MAX_DAYS, MAX_DAYS, null, institutionId);
		}
		
		//ESTE DE MOMENTO SOLO SE USABA EN TESTS
		public static NordigenAgreement getAgreement(NordigenAccessToken token, String agreementId) {
			return NordigenAPI.getEndUserAgreement(token.getAccess(), agreementId);
		}
}
