package com.code.aon.webservice.sii;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.ws.Holder;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.sii.respuestasuministro.EstadoEnvioType;
import net.aonsolutions.aon.sii.respuestasuministro.RespuestaAgenciasViajesType;
import net.aonsolutions.aon.sii.respuestasuministro.RespuestaBienType;
import net.aonsolutions.aon.sii.respuestasuministro.RespuestaComunitariaType;
import net.aonsolutions.aon.sii.respuestasuministro.RespuestaExpedidaType;
import net.aonsolutions.aon.sii.respuestasuministro.RespuestaLRBajaFEmitidasType;
import net.aonsolutions.aon.sii.respuestasuministro.RespuestaMetalicoType;
import net.aonsolutions.aon.sii.respuestasuministro.RespuestaOperacionesSegurosType;
import net.aonsolutions.aon.sii.respuestasuministro.RespuestaRecibidaType;
import net.aonsolutions.aon.sii.suministroinformacion.CabeceraSii;
import net.aonsolutions.aon.sii.suministroinformacion.DatosPresentacionType;
import net.aonsolutions.aon.sii.suministrolr.BajaLRFacturasRecibidas;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRAgenciasViajes;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRBienesInversion;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRCobrosEmitidas;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRCobrosMetalico;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRDetOperacionIntracomunitaria;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRFacturasEmitidas;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRFacturasRecibidas;
import net.aonsolutions.aon.sii.suministrolr.SuministroLROperacionesSeguros;
import net.aonsolutions.aon.sii.suministrolr.SuministroLRPagosRecibidas;

public class SIIPost {
	
	private Boolean pruebas = true;
	
	public static SIIPost getInstance() {
		return new SIIPost();
	}
	
	public SIIPost() {
		
	}
	
    public static void main(String args[]) {

	
		SIIPost sii = SIIPost.getInstance();

//		sii.suministroFacturasEmitidas(company, vatList);
//		sii.suministroFacturasEmitidasCobros(company, vatList);
//		sii.suministroFacturasRecibidas(company, vatList);
//		sii.suministroFacturasRecibidasPagos(company, vatList);
//		sii.suministroBienesInversion(company, vatList);
//		sii.suministroOperacionesIntracomunitarias(company, vatList);
//		sii.suministroOperacionesMetalico(company, vatList);
//		sii.suministroOperacionesSeguros(company, vatList);
//		sii.suministroAgenciasViajes(company, vatList);
	}

    // -------------------- FACTURAS EMITIDAS
    
    protected void consultaFacturasEmitidas(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrofactemitidas.SiiService service = new net.aonsolutions.aon.sii.suministrofactemitidas.SiiService();
    	net.aonsolutions.aon.sii.suministrofactemitidas.SiiSOAP soap = service.getSuministroFactEmitidas();    	
    	if(pruebas) soap =  service.getSuministroFactEmitidasPruebas();
    	//TODO 
    	
    	//soap.consultaLRFacturasEmitidas(cabecera, filtroConsulta, periodoImpositivo, indicadorPaginacion, resultadoConsulta, registroRespuestaConsultaLRFacturasEmitidas);	
    }
    
    protected void suministroFacturasEmitidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList){
    	net.aonsolutions.aon.sii.suministrofactemitidas.SiiService service = new net.aonsolutions.aon.sii.suministrofactemitidas.SiiService();
    	net.aonsolutions.aon.sii.suministrofactemitidas.SiiSOAP soap = service.getSuministroFactEmitidasSello();
    	if(pruebas) soap = service.getSuministroFactEmitidasPruebas();
    	//BindingProvider bp = (BindingProvider) soap;
    	//configurarSeguridad(soap, wsaTo, wsaAction, timeout);
		Holder<String> csv = new Holder<>("");
    	Holder<DatosPresentacionType> datosPresentacion = new Holder<>();
    	Holder<EstadoEnvioType> estadoEnvio = new Holder<EstadoEnvioType>();
    	Holder<List<RespuestaExpedidaType>> respuestaLinea = new Holder<List<RespuestaExpedidaType>>(new LinkedList<>());
    	
    	SuministroLRFacturasEmitidas suministro = SIIBuilt.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceList, contextList);
     	Holder<CabeceraSii> holder = new Holder<CabeceraSii>(suministro.getCabecera());
       	soap.suministroLRFacturasEmitidas(holder, suministro.getRegistroLRFacturasEmitidas(), csv, datosPresentacion, estadoEnvio, respuestaLinea);
   
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasEmitidas(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getSuministroFacturasEmitidas(suministro);
    	
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    }

    
    private void configurarSeguridad(Object port, String wsaTo, String wsaAction, int timeout) throws Exception {
    	
    }
    
    protected void anulacionFacturasEmitidas(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrofactemitidas.SiiService service = new net.aonsolutions.aon.sii.suministrofactemitidas.SiiService();
    	net.aonsolutions.aon.sii.suministrofactemitidas.SiiSOAP soap = service.getSuministroFactEmitidas();
    	if(pruebas) soap = service.getSuministroFactEmitidasPruebas();
    	
    	RespuestaLRBajaFEmitidasType respuesta = soap.anulacionLRFacturasEmitidas(SIIBuilt.getInstance().bajaFacturasEmitidas(company, vatList));
    }

    protected void suministroFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList){
    	net.aonsolutions.aon.sii.suministrocobrosemitidas.SiiService service = new net.aonsolutions.aon.sii.suministrocobrosemitidas.SiiService();
    	net.aonsolutions.aon.sii.suministrocobrosemitidas.SiiSOAP soap = service.getSuministroCobrosEmitidas();
    	if(pruebas) soap = service.getSuministroCobrosEmitidasPruebas();
    	
    	SuministroLRCobrosEmitidas suministro = SIIBuilt.getInstance().suministroFacturasEmitidasCobros(company);

    	// TODO
    	//soap.suministroLRCobrosEmitidas(cabecera, registroLRCobros, csv, datosPresentacion, cabecera0, estadoEnvio, respuestaLinea);
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasEmitidasCobros(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getSuministroFacturasEmitidasCobros(suministro);
    	
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    }

    // -------------------- FACTURAS RECIBIDAS
    
    protected void consultaFacturasRecibidas(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrofactrecibidas.SiiService service = new net.aonsolutions.aon.sii.suministrofactrecibidas.SiiService();
    	net.aonsolutions.aon.sii.suministrofactrecibidas.SiiSOAP soap = service.getSuministroFactRecibidas();
    	if(pruebas) soap = service.getSuministroFactRecibidasPruebas();   
     	
    	//TODO
    	
    	//soap.consultaLRFacturasRecibidas(cabecera, filtroConsulta, periodoImpositivo, indicadorPaginacion, resultadoConsulta, registroRespuestaConsultaLRFacturasRecibidas);
    }
    
    protected void suministroFacturasRecibidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrofactrecibidas.SiiService service = new net.aonsolutions.aon.sii.suministrofactrecibidas.SiiService();
    	net.aonsolutions.aon.sii.suministrofactrecibidas.SiiSOAP soap = service.getSuministroFactRecibidas();
    	if(pruebas) soap = service.getSuministroFactRecibidasPruebas();
    	
    	Holder<String> csv = null;
    	Holder<DatosPresentacionType> datosPresentacion = null;
    	Holder<EstadoEnvioType> estadoEnvio = null;
    	Holder<List<RespuestaRecibidaType>> respuestaLinea = null;
    	
    	SuministroLRFacturasRecibidas suministro = SIIBuilt.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceList, vatList);
    	Holder<CabeceraSii> holder = new Holder<CabeceraSii>(suministro.getCabecera());
    	soap.suministroLRFacturasRecibidas(holder, suministro.getRegistroLRFacturasRecibidas(), csv, datosPresentacion, estadoEnvio, respuestaLinea);
    	
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasRecibidas(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getSuministroFacturasRecibidas(suministro);
    	
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    }
    
    protected void anulacionFacturasRecibidas(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrofactrecibidas.SiiService service = new net.aonsolutions.aon.sii.suministrofactrecibidas.SiiService();
    	net.aonsolutions.aon.sii.suministrofactrecibidas.SiiSOAP soap = service.getSuministroFactRecibidas();
    	if(pruebas) soap = service.getSuministroFactRecibidasPruebas();
    	//TODO 
    	
    	BajaLRFacturasRecibidas baja = SIIBuilt.getInstance().bajaFacturasRecibidas(company, vatList);
    }
    
    protected void suministroFacturasRecibidasPagos(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministropagosrecibidas.SiiService service = new net.aonsolutions.aon.sii.suministropagosrecibidas.SiiService();
    	net.aonsolutions.aon.sii.suministropagosrecibidas.SiiSOAP soap = service.getSuministroPagosRecibidas();
    	if(pruebas) soap = service.getSuministroPagosRecibidasPruebas();
    	
    	 SuministroLRPagosRecibidas suministro = SIIBuilt.getInstance().suministroFacturasRecibidasPagos(company);

    	// TODO 
    	
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasRecibidasPagos(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getSuministroFacturasRecibidasPagos(suministro);
    	
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    }
    
    // -------------------- BIENES DE INVERSION
    
    protected void consultaBienesInversion(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrobienesinversion.SiiService service = new net.aonsolutions.aon.sii.suministrobienesinversion.SiiService();
    	net.aonsolutions.aon.sii.suministrobienesinversion.SiiSOAP soap = service.getSuministroBienesInversion();
    	if(pruebas) soap = service.getSuministroBienesInversionPruebas();
    	
    	//TODO
    	
    	//soap.consultaLRBienesInversion(cabecera, filtroConsulta, periodoImpositivo, indicadorPaginacion, resultadoConsulta, registroRespuestaConsultaLRBienesInversion);
    }
    
    protected void suministroBienesInversion(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList){
    	net.aonsolutions.aon.sii.suministrobienesinversion.SiiService service = new net.aonsolutions.aon.sii.suministrobienesinversion.SiiService();
    	net.aonsolutions.aon.sii.suministrobienesinversion.SiiSOAP soap = service.getSuministroBienesInversion();
    	if(pruebas) soap = service.getSuministroBienesInversionPruebas();
    	
    	Holder<String> csv = null;
    	Holder<DatosPresentacionType> datosPresentacion = null;
    	Holder<EstadoEnvioType> estadoEnvio = null;
    	Holder<List<RespuestaBienType>> respuestaLinea = null;
    	
    	SuministroLRBienesInversion suministro = SIIBuilt.getInstance().suministroBienesInversion(domain, login, company, invoiceList, contextList);
    	Holder<CabeceraSii> holder = new Holder<CabeceraSii>(suministro.getCabecera());
    	soap.suministroLRBienesInversion(holder, suministro.getRegistroLRBienesInversion(), csv, datosPresentacion, estadoEnvio, respuestaLinea);

    	byte[] requestXml = SIIBuilt.getInstance().getSuministroBienesInversion(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getSuministroBienesInversion(suministro);
    	
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    }
    
    protected void anulacionBienesInversion(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrobienesinversion.SiiService service = new net.aonsolutions.aon.sii.suministrobienesinversion.SiiService();
    	net.aonsolutions.aon.sii.suministrobienesinversion.SiiSOAP soap = service.getSuministroBienesInversion();
    	if(pruebas) soap = service.getSuministroBienesInversionPruebas();
    	
    	//TODO 
    	
    	//soap.anulacionLRBienesInversion(anulacionLRBienesInversion);
    }
    
    // -------------------- OPERACIONES INTRACOMUNITARIAS

    protected void consultaOperacionesIntracomunitarias(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministroopintracomunitarias.SiiService service = new net.aonsolutions.aon.sii.suministroopintracomunitarias.SiiService();
    	net.aonsolutions.aon.sii.suministroopintracomunitarias.SiiSOAP soap = service.getSuministroOpIntracomunitarias();
    	if(pruebas) soap = service.getSuministroOpIntracomunitariasPruebas();
    	
    	//TODO
    	
    	//soap.consultaLRDetOperacionIntracomunitaria(consultaLRDetOperIntracomunitarias);
    }
    
    protected void suministroOperacionesIntracomunitarias(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList){
    	net.aonsolutions.aon.sii.suministroopintracomunitarias.SiiService service = new net.aonsolutions.aon.sii.suministroopintracomunitarias.SiiService();
    	net.aonsolutions.aon.sii.suministroopintracomunitarias.SiiSOAP soap = service.getSuministroOpIntracomunitarias();
    	if(pruebas) soap = service.getSuministroOpIntracomunitariasPruebas();
    	
    	Holder<String> csv = null;
    	Holder<DatosPresentacionType> datosPresentacion = null;
    	Holder<EstadoEnvioType> estadoEnvio = null;
    	Holder<List<RespuestaComunitariaType>> respuestaLinea = null;
    	
    	SuministroLRDetOperacionIntracomunitaria suministro = SIIBuilt.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceList, contextList);
    	Holder<CabeceraSii> holder = new Holder<CabeceraSii>(suministro.getCabecera());
    	soap.suministroLRDetOperacionIntracomunitaria(holder, suministro.getRegistroLRDetOperacionIntracomunitaria(), csv, datosPresentacion, estadoEnvio, respuestaLinea);
    	
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    	
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    }
    
    protected void anulacionOperacionesIntracomunitarias(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministroopintracomunitarias.SiiService service = new net.aonsolutions.aon.sii.suministroopintracomunitarias.SiiService();
    	net.aonsolutions.aon.sii.suministroopintracomunitarias.SiiSOAP soap = service.getSuministroOpIntracomunitarias();
    	if(pruebas) soap = service.getSuministroOpIntracomunitariasPruebas();
    	
    	//TODO 
    	
    	//soap.anulacionLRDetOperacionIntracomunitaria(anulacionLRDetOperacionIntracomunitaria);
    }
    
    // -------------------- OPERACIONES EN METALICO

    protected void consultaOperacionesMetalico(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService service = new net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService();
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiSOAP soap = service.getSuministroOpTrascendTribu();
    	if(pruebas) soap = service.getSuministroOpTrascendTribuPruebas();   

    	//TODO
    	
    	//soap.consultaLRCobrosMetalico(cabecera, filtroConsulta, periodoImpositivo, indicadorPaginacion, resultadoConsulta, registroRespuestaConsultaLRCobrosMetalico);
    }
    
    protected void suministroOperacionesMetalico(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList){
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService service = new net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService();
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiSOAP soap = service.getSuministroOpTrascendTribu();
    	if(pruebas) soap = service.getSuministroOpTrascendTribuPruebas();

    	Holder<String> csv = null;
    	Holder<DatosPresentacionType> datosPresentacion = null;
    	Holder<EstadoEnvioType> estadoEnvio = null;
    	Holder<List<RespuestaMetalicoType>> respuestaLinea = null;
    	
    	SuministroLRCobrosMetalico suministro = SIIBuilt.getInstance().suministroCobrosMetalico(domain, login, company, invoiceList, contextList);
    	Holder<CabeceraSii> holder = new Holder<CabeceraSii>(suministro.getCabecera());
    	soap.suministroLRCobrosMetalico(holder, suministro.getRegistroLRCobrosMetalico(), csv, datosPresentacion, estadoEnvio, respuestaLinea);
    	
    	
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroCobrosMetalico(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getSuministroCobrosMetalico(suministro);
    	
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    }
    
    protected void anulacionOperacionesMetalico(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService service = new net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService();
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiSOAP soap = service.getSuministroOpTrascendTribu();
    	if(pruebas) soap = service.getSuministroOpTrascendTribuPruebas();

    	//TODO 
    	
    	//soap.anulacionLRCobrosMetalico(anulacionLRCobrosMetalico);
    }
    
    // -------------------- OPERACIONES DE SEGUROS
    
    protected void consultaOperacionesSeguros(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService service = new net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService();
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiSOAP soap = service.getSuministroOpTrascendTribu();
    	if(pruebas) soap = service.getSuministroOpTrascendTribuPruebas();
    	//TODO
    	
    	//soap.consultaLROperacionesSeguros(cabecera, filtroConsulta, periodoImpositivo, indicadorPaginacion, resultadoConsulta, registroRespuestaConsultaLROperacionesSeguros);
    }
    
    protected void suministroOperacionesSeguros(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList){
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService service = new net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService();
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiSOAP soap = service.getSuministroOpTrascendTribu();
    	if(pruebas) soap = service.getSuministroOpTrascendTribuPruebas();
    	Holder<String> csv = null;
    	Holder<DatosPresentacionType> datosPresentacion = null;
    	Holder<EstadoEnvioType> estadoEnvio = null;
    	Holder<List<RespuestaOperacionesSegurosType>> respuestaLinea = null;
    	
    	SuministroLROperacionesSeguros suministro = SIIBuilt.getInstance().suministroOperacionesSeguros(domain, login, company, invoiceList, contextList);
    	Holder<CabeceraSii> holder = new Holder<CabeceraSii>(suministro.getCabecera());
    	soap.suministroLROperacionesSeguros(holder, suministro.getRegistroLROperacionesSeguros(), csv, datosPresentacion, estadoEnvio, respuestaLinea);
    	
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroOperacionesSeguros(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getSuministroOperacionesSeguros(suministro);
    	
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    }
    
    protected void anulacionOperacionesSeguros(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService service = new net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService();
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiSOAP soap = service.getSuministroOpTrascendTribu();
    	if(pruebas) soap = service.getSuministroOpTrascendTribuPruebas();

    	//TODO 
    
    	//soap.anulacionLROperacionesSeguros(anulacionLROperacionesSeguros);
    }
    
    // -------------------- AGENCIAS DE VIAJES
    
    protected void consultaAgenciasViajes(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService service = new net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService();
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiSOAP soap = service.getSuministroOpTrascendTribu();
    	if(pruebas) soap = service.getSuministroOpTrascendTribuPruebas();
    	
    	//TODO
    
    	//soap.consultaLRAgenciasViajes(cabecera, filtroConsulta, periodoImpositivo, indicadorPaginacion, resultadoConsulta, registroRespuestaConsultaLRAgenciasViajes);
    }
    
    protected void suministroAgenciasViajes(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList){
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService service = new net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService();
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiSOAP soap = service.getSuministroOpTrascendTribu();
    	if(pruebas) soap = service.getSuministroOpTrascendTribuPruebas();

    	Holder<String> csv = null;
    	Holder<DatosPresentacionType> datosPresentacion = null;
    	Holder<EstadoEnvioType> estadoEnvio = null;
    	Holder<List<RespuestaAgenciasViajesType>> respuestaLinea = null;
    	
    	SuministroLRAgenciasViajes suministro = SIIBuilt.getInstance().suministroAgenciasViajes(domain, login, company, invoiceList, contextList);
    	Holder<CabeceraSii> holder = new Holder<CabeceraSii>(suministro.getCabecera());
    	soap.suministroLRAgenciasViajes(holder, suministro.getRegistroLRAgenciasViajes(), csv, datosPresentacion, estadoEnvio, respuestaLinea);

    	byte[] requestXml = SIIBuilt.getInstance().getSuministroAgenciasViajes(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getSuministroAgenciasViajes(suministro);
    	
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    }

    protected void anulacionAgenciasViajes(Company company, LinkedList<VatContext> vatList){
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService service = new net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiService();
    	net.aonsolutions.aon.sii.suministrooptrascendtribu.SiiSOAP soap = service.getSuministroOpTrascendTribu();
    	if(pruebas) soap = service.getSuministroOpTrascendTribuPruebas();

    	//TODO 
    	
    	//soap.anulacionLRAgenciasViajes(anulacionLRAgenciasViajes);
    }      
}