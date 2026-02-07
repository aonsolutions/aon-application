package net.aonsolutions.aon.tbai;

import com.esferalia.aon.watson.server.AonDateUtils;

import ticketbai.kontsulta.CabeceraConsultaFacturaType;
import ticketbai.kontsulta.CabeceraConsultaType;
import ticketbai.kontsulta.FiltroConsultaType;
import ticketbai.kontsulta.KontsultaTicketBAI;

public class TbaiConsulta {
	private static final String TBAI_VERSION = "1.0";

	public static KontsultaTicketBAI buildQuery() {
		KontsultaTicketBAI query = new KontsultaTicketBAI();
		query.setCabecera(cabecera());
		query.setFiltroConsulta(filtro());
		return query;
		
	}
	
	public static CabeceraConsultaType cabecera() {
		CabeceraConsultaType cabecera = new CabeceraConsultaType();
		cabecera.setEjercicio(AonDateUtils.getYear(new java.util.Date()));
		cabecera.setIDVersion(TBAI_VERSION);
		cabecera.setNifEmisor("B01487271");
		cabecera.setPeriodo("4T");	
		return cabecera;
	}	
	
	public static FiltroConsultaType filtro() {
		FiltroConsultaType filtro = new FiltroConsultaType();
		CabeceraConsultaFacturaType cabeceraFactura = new CabeceraConsultaFacturaType();
		cabeceraFactura.setSerieFactura("2025");
		filtro.setCabeceraFactura(cabeceraFactura);
		return filtro;
	}
	
}
