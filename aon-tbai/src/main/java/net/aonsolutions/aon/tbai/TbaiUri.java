package net.aonsolutions.aon.tbai;

import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;

public class TbaiUri {
		
	private TbaiUri() {
	
	}
	
	private static final String URL_ARABA_EMISION = "https://ticketbai.araba.eus/TicketBAI/v1/facturas/";
	private static final String URL_ARABA_EMISION_TEST = "https://pruebas-ticketbai.araba.eus/TicketBAI/v1/facturas/";
	private static final String URL_ARABA_ANULACION = "https://ticketbai.araba.eus/TicketBAI/v1/anulaciones/";
	private static final String URL_ARABA_ANULACION_TEST = "https://pruebas-ticketbai.araba.eus/TicketBAI/v1/anulaciones/";
	private static final String URL_ARABA_ZUZENDU = "https://ticketbai.araba.eus/TicketBAI/v1/facturas/subsanarmodificar";
	private static final String URL_ARABA_ZUZENDU_TEST = "https://pruebas-ticketbai.araba.eus/TicketBAI/v1/facturas/subsanarmodificar";
	private static final String URL_ARABA_ZUZENDU_BAJA = "https://ticketbai.araba.eus/TicketBAI/v1/anulaciones/baja";
	private static final String URL_ARABA_ZUZENDU_BAJA_TEST = "https://pruebas-ticketbai.araba.eus/TicketBAI/v1/anulaciones/baja";
	private static final String URL_ARABA_OSATU = "https://ticketbai.araba.eus/TicketBAI/v1/osatu/";
	private static final String URL_ARABA_OSATU_TEST = "https://pruebas-ticketbai.araba.eus/TicketBAI/v1/osatu/";
	private static final String URL_ARABA_QR = "https://ticketbai.araba.eus/tbai/qrtbai/";
	private static final String URL_ARABA_QR_TEST = "https://pruebas-ticketbai.araba.eus/tbai/qrtbai/";
	
	private static final String URL_BIZKAIA = "https://sarrerak.bizkaia.eus/N3B4000M/aurkezpena";
	private static final String URL_BIZKAIA_TEST = "https://pruesarrerak.bizkaia.eus/N3B4000M/aurkezpena";
	private static final String URL_BIZKAIA_CONSULTA = "https://sarrerak.bizkaia.eus/N3B4001M/kontsulta";
	private static final String URL_BIZKAIA_CONSULTA_TEST = "https://pruesarrerak.bizkaia.eus/N3B4001M/kontsulta";
	private static final String URL_BIZKAIA_QR = "https://batuz.eus/QRTBAI/";
	private static final String URL_BIZKAIA_QR_TEST = "https://batuz.eus/QRTBAI/";
	
	private static final String URL_GIPUZKOA_EMISION = "https://tbai-z.egoitza.gipuzkoa.eus/sarrerak/alta";
	private static final String URL_GIPUZKOA_EMISION_TEST = "https://tbai-z.prep.gipuzkoa.eus/sarrerak/alta";
	private static final String URL_GIPUZKOA_ANULACION = "https://tbai-z.egoitza.gipuzkoa.eus/sarrerak/baja";
	private static final String URL_GIPUZKOA_ANULACION_TEST = "https://tbai-z.prep.gipuzkoa.eus/sarrerak/baja";
	private static final String URL_GIPUZKOA_ZUZENDU = "https://tbai-z.egoitza.gipuzkoa.eus/sarrerak/zuzendu-alta";
	private static final String URL_GIPUZKOA_ZUZENDU_TEST = "https://tbai-z.prep.gipuzkoa.eus/sarrerak/zuzendu-alta";
	private static final String URL_GIPUZKOA_ZUZENDU_BAJA = "https://tbai-z.egoitza.gipuzkoa.eus/sarrerak/zuzendu-baja";
	private static final String URL_GIPUZKOA_ZUZENDU_BAJA_TEST = "https://tbai-z.prep.gipuzkoa.eus/sarrerak/zuzendu-baja";
	private static final String URL_GIPUZKOA_OSATU = "https://tbai-z.egoitza.gipuzkoa.eus/sarrerak/osatu";
	private static final String URL_GIPUZKOA_OSATU_TEST = "https://tbai-z.prep.gipuzkoa.eus/sarrerak/osatu";
	private static final String URL_GIPUZKOA_KONTSULTA = "";
	private static final String URL_GIPUZKOA_KONTSULTA_TEST = "https://tbai-k.prep.gipuzkoa.eus/kontsulta/fakturak";
	private static final String URL_GIPUZKOA_KONTSULTA_LAST = "";
	private static final String URL_GIPUZKOA_KONTSULTA_LAST_TEST = "https://tbai-k.prep.gipuzkoa.eus/kontsulta/azkena";
	private static final String URL_GIPUZKOA_QR = "https://tbai.egoitza.gipuzkoa.eus/qr/";
	private static final String URL_GIPUZKOA_QR_TEST = "https://tbai.prep.gipuzkoa.eus/qr/";
	
	
	public static String getUrlEmision(TbaiConfiguration  tbai) {
		if(Administration.ALAVA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_ARABA_EMISION_TEST : URL_ARABA_EMISION;
		else if(Administration.BIZKAIA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_BIZKAIA_TEST : URL_BIZKAIA;
		else if(Administration.GIPUZKOA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_GIPUZKOA_EMISION_TEST : URL_GIPUZKOA_EMISION;
		return "";
	}
	
	public static String getUrlAnulacion(TbaiConfiguration  tbai) {
		if(Administration.ALAVA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_ARABA_ANULACION_TEST : URL_ARABA_ANULACION;
		else if(Administration.BIZKAIA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_BIZKAIA_TEST : URL_BIZKAIA;
		else if(Administration.GIPUZKOA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_GIPUZKOA_ANULACION_TEST : URL_GIPUZKOA_ANULACION;
		return "";
	}
	
	public static String getUrlZuzendu(TbaiConfiguration  tbai) {
		if(Administration.ALAVA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_ARABA_ZUZENDU_TEST : URL_ARABA_ZUZENDU;
		else if(Administration.BIZKAIA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_BIZKAIA_TEST : URL_BIZKAIA;
		else if(Administration.GIPUZKOA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_GIPUZKOA_ZUZENDU_TEST : URL_GIPUZKOA_ZUZENDU;
		return "";
	}
	
	public static String getUrlZuzenduBaja(TbaiConfiguration  tbai) {
		if(Administration.ALAVA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_ARABA_ZUZENDU_BAJA_TEST : URL_ARABA_ZUZENDU_BAJA;
		else if(Administration.BIZKAIA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_BIZKAIA_TEST : URL_BIZKAIA;
		else if(Administration.GIPUZKOA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_GIPUZKOA_ZUZENDU_BAJA_TEST : URL_GIPUZKOA_ZUZENDU_BAJA;
		return "";
	}
	
	public static String getUrlOsatu(TbaiConfiguration  tbai) {
		if(Administration.ALAVA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_ARABA_OSATU_TEST : URL_ARABA_OSATU;
		else if(Administration.BIZKAIA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_BIZKAIA_TEST : URL_BIZKAIA;
		else if(Administration.GIPUZKOA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_GIPUZKOA_OSATU_TEST : URL_GIPUZKOA_OSATU;
		return "";
	}
	
	public static String getUrlConsulta(TbaiConfiguration  tbai) {
		if(Administration.BIZKAIA.equals(tbai.getAdministration()))
			return tbai.isTest() ? URL_BIZKAIA_CONSULTA_TEST : URL_BIZKAIA_CONSULTA;
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
