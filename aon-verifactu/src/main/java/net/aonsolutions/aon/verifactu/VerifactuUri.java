package net.aonsolutions.aon.verifactu;

import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;

public class VerifactuUri {
		
	private VerifactuUri() {
	
	}
	

    // https://prewww2.aeat.es equivalente en cuanto a requisitos a https://www2.agenciatributaria.gob.es
  	private static final String URL_1 = "https://www2.agenciatributaria.gob.es";
	private static final String URL_TEST_1 = "https://prewww2.aeat.es";

  	// https://prewww1.aeat.es equivalente en cuanto a requisitos a https://www1.agenciatributaria.gob.es
  	private static final String URL_2 = "https://www1.agenciatributaria.gob.es";
	private static final String URL_TEST_2 = "https://prewww1.aeat.es";

  	// https://prewww10.aeat.es (pruebas de Web Services para Contribuyentes con certificado de sello) equivalente en cuanto a requisitos a https://www10.agenciatributaria.gob.es
	private static final String URL_3 = "https://www10.agenciatributaria.gob.es";
	private static final String URL_TEST_3 = "https://prewww10.aeat.es";
	
	private static final String URL_QR = "https://prewww2.aeat.es/wlpl/TIKE-CONT/ValidarQR";
	private static final String URL_QR_TEST = "https://prewww2.aeat.es/wlpl/TIKE-CONT/ValidarQR";
	
	public static String getUrlEmision() {
		return URL_TEST_1;
	}
	
	public static String getUrlQr() {
		return URL_QR;
	}
	
}
