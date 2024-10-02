package net.aonsolutions.aon.bank.nordigen.utils;

import java.util.Date;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;

import net.aonsolutions.aon.bank.nordigen.NordigenAPI;

public class NordigenTokenUtils {
	
	//**************************************//
	//**Funciones para el token de nordigen*//
	//**************************************//
	//**************************************//
	
	public static NordigenAccessToken getNewAccessToken() {
		NordigenAccessToken token = NordigenAPI.newAccessToken();
		Date tokenCreationDate = new Date();
		return token
			.setCreationDate(tokenCreationDate)
			.setRefreshDate(tokenCreationDate);
	}
	//ESTE DE MOMENTO SOLO LO USAN EN TESTS
	public static NordigenAccessToken refreshToken(NordigenAccessToken token)  {
		return token.refreshAccessToken( NordigenAPI.refreshAccessToken(token.getRefresh()) )
			.setRefreshDate(new Date());
	}

}
