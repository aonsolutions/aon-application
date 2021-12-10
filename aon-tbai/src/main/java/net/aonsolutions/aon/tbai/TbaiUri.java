package net.aonsolutions.aon.tbai;

import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;

public class TbaiUri {
		
	private TbaiUri() {
	
	}
	
	private static final String URL_ARABA_EMISION = "";
	private static final String URL_ARABA_EMISION_TEST = "https://pruebas-ticketbai.araba.eus/TicketBAI/v1/facturas/";
	private static final String URL_ARABA_ANULACION = "";
	private static final String URL_ARABA_ANULACION_TEST = "https://pruebas-ticketbai.araba.eus/TicketBAI/v1/anulaciones/";
	private static final String URL_ARABA_QR = "https://ticketbai.araba.eus/TBAI/QRTBAI";
	private static final String URL_ARABA_QR_TEST = "https://pruebas-ticketbai.araba.eus/tbai/qrtbai/";
	
	private static final String URL_BIZKAIA_EMISION = "https://sarrerak.bizkaia.eus/N3B4000M/aurkezpena";
	private static final String URL_BIZKAIA_EMISION_TEST = "https://pruesarrerak.bizkaia.eus/N3B4000M/aurkezpena";
	private static final String URL_BIZKAIA_ANULACION = "https://sarrerak.bizkaia.eus/N3B4000M/aurkezpena";
	private static final String URL_BIZKAIA_ANULACION_TEST = "https://pruesarrerak.bizkaia.eus/N3B4000M/aurkezpena";
	private static final String URL_BIZKAIA_QR = "https://batuz.eus/QRTBAI/";
	private static final String URL_BIZKAIA_QR_TEST = "https://batuz.eus/QRTBAI/";
	
	private static final String URL_GIPUZKOA_EMISION = "https://tbai-z.egoitza.gipuzkoa.eus/sarrerak/alta";
	private static final String URL_GIPUZKOA_EMISION_TEST = "https://tbai-prep.egoitza.gipuzkoa.eus/WAS/HACI/HTBRecepcionFacturasWEB/rest/recepcionFacturas/alta";
	private static final String URL_GIPUZKOA_ANULACION = "https://tbai-z.egoitza.gipuzkoa.eus/sarrerak/baja";
	private static final String URL_GIPUZKOA_ANULACION_TEST = "https://tbai-prep.egoitza.gipuzkoa.eus/WAS/HACI/HTBRecepcionFacturasWEB/rest/recepcionFacturas/anulacion";
	private static final String URL_GIPUZKOA_QR = "https://tbai.egoitza.gipuzkoa.eus/qr/";
	private static final String URL_GIPUZKOA_QR_TEST = "https://tbai.prep.gipuzkoa.eus/qr/";
	
	
	public static String getUrlEmision(TbaiConfiguration  tbai) {
		if(Administration.ALAVA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_ARABA_EMISION_TEST : URL_ARABA_EMISION;
		else if(Administration.BIZKAIA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_BIZKAIA_EMISION_TEST : URL_BIZKAIA_EMISION;
		else if(Administration.GIPUZKOA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_GIPUZKOA_EMISION_TEST : URL_GIPUZKOA_EMISION;
		return "";
	}
	
	public static String getUrlAnulacion(TbaiConfiguration  tbai) {
		if(Administration.ALAVA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_ARABA_ANULACION_TEST : URL_ARABA_ANULACION;
		else if(Administration.BIZKAIA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_BIZKAIA_ANULACION_TEST : URL_BIZKAIA_ANULACION;
		else if(Administration.GIPUZKOA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_GIPUZKOA_ANULACION_TEST : URL_GIPUZKOA_ANULACION;
		return "";
	}
	
	public static String getUrlQr(TbaiConfiguration  tbai) {
		if(Administration.ALAVA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_ARABA_QR_TEST : URL_ARABA_QR;
		else if(Administration.BIZKAIA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_BIZKAIA_QR_TEST : URL_BIZKAIA_QR;
		else if(Administration.GIPUZKOA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_GIPUZKOA_QR_TEST : URL_GIPUZKOA_QR;
		return "";
	}
	
}
