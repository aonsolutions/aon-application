package net.aonsolutions.aon.bank.nordigen.utils;

import java.util.List;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.type.Country;

import net.aonsolutions.aon.bank.nordigen.NordigenAPI;

public class NordigenInstitutionUtils {

	//**************************************//
	//**Funciones instituciones de Nordigen*//
	//**************************************//
	//**************************************//
	
	public static NordigenInstitution getInstitution(NordigenAccessToken token, String institutionId) {
		return NordigenAPI.getInstitution(token.getAccess(), institutionId);
	}
	
	//Aqui obtenemos todas las instituciones en españa con pagos habilitados
	public static List<NordigenInstitution> getSpanishInstitutionPe(NordigenAccessToken token) {
		return NordigenAPI.getInstitutions(token.getAccess(), Country.ES, true);
	}
	
	//Aqui obtenemos todas las instituciones en españa sin pagos habilitados
		public static List<NordigenInstitution> getSpanishInstitutionPNe(NordigenAccessToken token) {
			return NordigenAPI.getInstitutions(token.getAccess(), Country.ES, false);
	}
		
	
	
	
	
	
}
