package net.aonsolutions.aon.bank.nordigen.utils;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;

import net.aonsolutions.aon.bank.nordigen.NordigenAPI;

public class NordigenInstitutionUtils {

	//**************************************//
	//**Funciones instituciones de Nordigen*//
	//**************************************//
	//**************************************//
	
	public static NordigenInstitution getInstitution(NordigenAccessToken token, String institutionId) {
		return NordigenAPI.getInstitution(token.getAccess(), institutionId);
	}
}
