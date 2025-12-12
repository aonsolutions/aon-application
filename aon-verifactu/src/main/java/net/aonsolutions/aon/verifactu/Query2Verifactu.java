package net.aonsolutions.aon.verifactu;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationQuery;
import com.esferalia.aon.occam.api.model.type.Month;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.consultalr.ConsultaFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.consultalr.LRFiltroRegFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CabeceraConsultaSf;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ContraparteConsultaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.FechaExpedicionConsultaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ObligadoEmisionConsultaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PeriodoImputacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RangoFechaExpedicionType;

class Query2Verifactu {
	
	static final String VERSION  = "1.0";
	static final String DATE_PATERN = "dd-MM-yyyy";
	
	private Query2Verifactu() {
	
	}
	
	static ConsultaFactuSistemaFacturacionType build(VerifactuContext vc) throws InvoiceCommunicationException {
		ConsultaFactuSistemaFacturacionType  query = new ConsultaFactuSistemaFacturacionType();
		query.setCabecera( getQueryCabecera( vc ) );
		VerifactuValidation.validateHeader(query);
		query.setFiltroConsulta( getFiltroConsulta( vc ) );
		return query;
	}

	private static CabeceraConsultaSf getQueryCabecera(VerifactuContext vc) {
		CabeceraConsultaSf cabecera = new CabeceraConsultaSf();
		cabecera.setIDVersion(VERSION);
		ObligadoEmisionConsultaType obligadoEmision = new ObligadoEmisionConsultaType();
		obligadoEmision.setNIF(vc.getCompany().getDocument());
		obligadoEmision.setNombreRazon(vc.getCompany().getName());
		cabecera.setObligadoEmision(obligadoEmision);
		return cabecera;
	}
	
	private static LRFiltroRegFacturacionType getFiltroConsulta(VerifactuContext vc) {
		InvoiceCommunicationQuery icq = vc.getInvoiceCommunicationQuery();
		LRFiltroRegFacturacionType filtro = new LRFiltroRegFacturacionType();
		
		PeriodoImputacionType periodoImputacion = new PeriodoImputacionType();
		filtro.setPeriodoImputacion( periodoImputacion );
		
		
		icq.getYear().ifPresent(y -> periodoImputacion.setEjercicio( AonNumberUtils.toString( y ) ));
		icq.getMonth().ifPresent( m -> 
			periodoImputacion.setPeriodo(
				Month.value( m ) 
					.map( AonNumberUtils::toString )
					.map( i -> AonStringUtils.leftPad( i , 2 , '0') )
					.orElse(null) 
			)
		);
		icq.getReferenceCode().ifPresent( rc -> filtro.setNumSerieFactura( rc ) );
		
		if (icq.hasRegistryData()) {
			ContraparteConsultaType contraparte = new ContraparteConsultaType();
			icq.getRegistryDocument()
				.ifPresent( rd -> contraparte.setNIF( rd ) );
			icq.getRegistryName()
				.ifPresent( rn -> contraparte.setNombreRazon( rn ) );
			filtro.setContraparte( contraparte );
		}
		
		if (icq.getFromDate().isPresent() || icq.getToDate().isPresent()) {
			FechaExpedicionConsultaType fect = new FechaExpedicionConsultaType(); 	
			RangoFechaExpedicionType rangoFechaExpedicion = new RangoFechaExpedicionType();
			icq.getFromDate().ifPresent( fd -> rangoFechaExpedicion.setDesde( AonDateUtils.format( fd , DATE_PATERN )));
			icq.getToDate().ifPresent( td -> rangoFechaExpedicion.setHasta( AonDateUtils.format( td , DATE_PATERN )));
			fect.setRangoFechaExpedicion( rangoFechaExpedicion );
			filtro.setFechaExpedicionFactura( fect );
		}
		icq.getId()
			.ifPresent( id -> filtro.setRefExterna( AonNumberUtils.toString( id ) ) );
		return filtro;
	}
	
}

