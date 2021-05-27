package net.aonsolutions.aon.sii.gipuzkoa;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;

import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.EstadoRegistroType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaBienBajaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaBienType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaComunitariaBajaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaComunitariaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaExpedidaBajaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaExpedidaCobroType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaExpedidaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRAgenciasViajesType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRBajaBienesInversionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRBajaFEmitidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRBajaFRecibidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRBajaOComunitariasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRBienesInversionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRCobrosEmitidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRFEmitidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRFRecibidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRIMetalicoType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLROComunitariasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLROperacionesSegurosType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaLRPagosRecibidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaRecibidaBajaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaRecibidaPagoType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestasuministro.RespuestaRecibidaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.BajaLRBienesInversion;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.BajaLRDetOperacionIntracomunitaria;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.BajaLRFacturasEmitidas;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.BajaLRFacturasRecibidas;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.SuministroLRAgenciasViajes;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.SuministroLRBienesInversion;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.SuministroLRCobrosEmitidas;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.SuministroLRCobrosMetalico;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.SuministroLRDetOperacionIntracomunitaria;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.SuministroLRFacturasEmitidas;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.SuministroLRFacturasRecibidas;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.SuministroLROperacionesSeguros;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.SuministroLRPagosRecibidas;
import net.aonsolutions.aon.sii.SIIDB;
import net.aonsolutions.aon.sii.SIIPost;
import net.aonsolutions.aon.sii.SendType;

public class SIIGipuzkoaPost extends SIIPost{

	public static SIIGipuzkoaPost getInstance(byte[] cert, String pass) {
		return new SIIGipuzkoaPost(cert, pass);
	}

	public SIIGipuzkoaPost(byte[] cert, String pass) {
		super(cert, pass);
	}
	
	// -------------------- FACTURAS EMITIDAS
	
	public JSONArray suministroFacturasEmitidas(Domain domain, String login, Company company, Integer invoiceId,
    		LinkedList<VatContext> contextList, String terceros, String uri, LinkedList<VatContext> list, SendType type) throws JAXBException, SOAPException, ParserConfigurationException, IOException {
		JSONArray array = new JSONArray();
		
   		byte[] requestXml = null;
		byte[] responseXml = null;
		System.out.println("SII - Factura Emitida con Id. " + invoiceId);
		SuministroLRFacturasEmitidas suministro = FacturasEmitidas.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceId, list, type.isModificacion(), terceros);
    	String sumStr = marshal(SuministroLRFacturasEmitidas.class, suministro); 
		String response = post(uri, sumStr);	
    	RespuestaLRFEmitidasType respuesta = (RespuestaLRFEmitidasType) unmarshal(RespuestaLRFEmitidasType.class, response);
    	for (RespuestaExpedidaType r : respuesta.getRespuestaLinea()) {
    		Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    		if(correcto) System.out.println("SII Response - Factura Emitida con Id. " + invoiceId + " se ha enviado correctamente");
			else System.out.println("ERROR SII Response - Factura Emitida con Id. " + invoiceId + " " + r.getDescripcionErrorRegistro());
    		if(!correcto && r.getCodigoErrorRegistro().intValue() == 3000) {
    			return suministroFacturasEmitidas(domain, login, company, invoiceId, contextList, terceros, uri, list, SendType.MOD_EMITIDAS);
    		} else {
    			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    				correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    				r.getIDFactura().getNumSerieFacturaEmisor()));
    		}
        }
    			
    	requestXml = FacturasEmitidas.getInstance().getSuministroFacturasEmitidas((SuministroLRFacturasEmitidas) suministro);
    	responseXml = FacturasEmitidas.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);

    	String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst().orElse("Incorrecto");

    	SIIDB.getInstance().insertSuministro(domain, login, invoiceId, requestXml, responseXml, status, contextList, type);
    	
    	return array;
	}
	
	public JSONArray bajaFacturasEmitidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) throws JAXBException, SOAPException, ParserConfigurationException, IOException {
    	JSONArray array = new JSONArray();
    	
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	
    	BajaLRFacturasEmitidas suministro = FacturasEmitidas.getInstance().bajaFacturasEmitidas(company, invoiceId, contextList, terceros);     	
    	String sumStr = marshal(BajaLRFacturasEmitidas.class, suministro); 
		String response = post(uri, sumStr);	
		RespuestaLRBajaFEmitidasType respuesta = (RespuestaLRBajaFEmitidasType) unmarshal(RespuestaLRBajaFEmitidasType.class, response);
        for(RespuestaExpedidaBajaType rect : respuesta.getRespuestaLinea()){
           	Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
        	array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
           	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
           	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
        }
        requestXml = FacturasEmitidas.getInstance().getBajaFacturasEmitidas(suministro);
       	responseXml = FacturasEmitidas.getInstance().getRespuestaBajaFacturasEmitidas(respuesta);
    	String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst().orElse("Incorrecto");

    	SIIDB.getInstance().insertSuministroBajas(domain, login, invoiceId, requestXml, responseXml, status, SendType.BAJA_EMITIDAS);
    	
    	return array;
	}
    
    public JSONArray suministroFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Finance> financeList, Integer invoiceId, String uri) throws JAXBException, SOAPException, ParserConfigurationException, IOException {    	
    	JSONArray array = new JSONArray();

    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	
    	SuministroLRCobrosEmitidas suministro = FacturasEmitidas.getInstance().suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId);     	
    	String sumStr = marshal(SuministroLRCobrosEmitidas.class, suministro); 
		String response = post(uri, sumStr);	
    	RespuestaLRCobrosEmitidasType respuesta = (RespuestaLRCobrosEmitidasType) unmarshal(RespuestaLRCobrosEmitidasType.class, response);
    	
    	for(RespuestaExpedidaCobroType rect : respuesta.getRespuestaLinea()){
    		Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    		array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
    			correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
    					rect.getIDFactura().getNumSerieFacturaEmisor()));
    	}
    	
    	requestXml = FacturasEmitidas.getInstance().getSuministroFacturasEmitidasCobros(suministro);
    	responseXml = FacturasEmitidas.getInstance().getRespuestaSuministroFacturasEmitidasCobros(respuesta);
    	
    	String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst().orElse("Incorrecto");
    	
    	SIIDB.getInstance().insertSuministroCobrosPagos(domain, login, invoiceId, requestXml, responseXml, financeList, status);
    	
    	return array;
    }
    
	// -------------------- FACTURAS RECIBIDAS
    
	public JSONArray suministroFacturasRecibidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri, LinkedList<VatContext> list, SendType type) throws JAXBException, SOAPException, ParserConfigurationException, IOException {
		JSONArray array = new JSONArray();
		
   		byte[] requestXml = null;
		byte[] responseXml = null;
		
		SuministroLRFacturasRecibidas suministro = FacturasRecibidas.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceId, list, type.isModificacion(), terceros);
		String sumStr = marshal(SuministroLRFacturasRecibidas.class, suministro); 
		String response = post(uri, sumStr);
    	RespuestaLRFRecibidasType respuesta = (RespuestaLRFRecibidasType) unmarshal(RespuestaLRFRecibidasType.class, response);
    	for (RespuestaRecibidaType r : respuesta.getRespuestaLinea()) {
    		Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    		if(!correcto && r.getCodigoErrorRegistro().intValue() == 3000) {
    			return suministroFacturasRecibidas(domain, login, company, invoiceId, contextList, terceros, uri, list, SendType.MOD_RECIBIDAS);
    		} else {
    			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    				correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    				r.getIDFactura().getNumSerieFacturaEmisor()));
    		}
        }
    			
    	requestXml = FacturasRecibidas.getInstance().getSuministroFacturasRecibidas((SuministroLRFacturasRecibidas) suministro);
    	responseXml = FacturasRecibidas.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
    	String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst().orElse("Incorrecto");

    	SIIDB.getInstance().insertSuministro(domain, login, invoiceId, requestXml, responseXml, status, contextList, type);
    	
    	return array;
	}
   	public JSONArray bajaFacturasRecibidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) throws JAXBException, SOAPException, ParserConfigurationException, IOException {    
    	JSONArray array = new JSONArray();
    	
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	
    	BajaLRFacturasRecibidas suministro = FacturasRecibidas.getInstance().bajaFacturasRecibidas(company, invoiceId, contextList, terceros);     	
    	String sumStr = marshal(BajaLRFacturasRecibidas.class, suministro); 
		String response = post(uri, sumStr);
   		RespuestaLRBajaFRecibidasType respuesta = (RespuestaLRBajaFRecibidasType) unmarshal(RespuestaLRBajaFRecibidasType.class, response);
    	
   		for(RespuestaRecibidaBajaType rect : respuesta.getRespuestaLinea()){
   			Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
   			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
       	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
       	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
   		}
    	
   		requestXml = FacturasRecibidas.getInstance().getBajaFacturasRecibidas(suministro);
   		responseXml = FacturasRecibidas.getInstance().getRespuestaBajaFacturasRecibidas(respuesta);
       	String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst().orElse("Incorrecto");

    	SIIDB.getInstance().insertSuministroBajas(domain, login, invoiceId, requestXml, responseXml, status, SendType.BAJA_RECIBIDAS);
    	
    	return array;
    }
    	
    public JSONArray suministroFacturasRecibidasPagos(Domain domain, String login, Company company, LinkedList<Finance> financeList, Integer invoiceId, String uri) throws JAXBException, SOAPException, ParserConfigurationException, IOException {    	
    	JSONArray array = new JSONArray();
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	
   		SuministroLRPagosRecibidas suministro = FacturasRecibidas.getInstance().suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId);     	
   		String sumStr = marshal(SuministroLRPagosRecibidas.class, suministro); 
		String response = post(uri, sumStr);
   		RespuestaLRPagosRecibidasType respuesta = (RespuestaLRPagosRecibidasType) unmarshal(RespuestaLRPagosRecibidasType.class, response);
    	
   		for(RespuestaRecibidaPagoType rrpt : respuesta.getRespuestaLinea()){
   			Boolean correcto = rrpt.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
   			array.put(json(correcto ? 200 : rrpt.getCodigoErrorRegistro().intValue(), 
       	    	correcto ? "Envio realizado correctamente" : rrpt.getDescripcionErrorRegistro(),
       	    			rrpt.getIDFactura().getNumSerieFacturaEmisor()));
   		}
    	
   		requestXml = FacturasRecibidas.getInstance().getSuministroFacturasRecibidasPagos(suministro);
   		responseXml = FacturasRecibidas.getInstance().getRespuestaSuministroFacturasRecibidasPagos(respuesta);
       	String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst().orElse("Incorrecto");

    	SIIDB.getInstance().insertSuministroCobrosPagos(domain, login, invoiceId, requestXml, responseXml, financeList, status);

    	return array;
    }
    
    // -------------------- BIENES INVERSION
	
    public JSONArray suministroBienesInversion(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri, SendType type) throws JAXBException, SOAPException, ParserConfigurationException, IOException {
    	JSONArray array = new JSONArray();

    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	
    	SuministroLRBienesInversion suministro = BienesInversion.getInstance().suministroBienesInversion(domain, login, company, invoiceId, contextList, type.isModificacion(), terceros);     	
    	String sumStr = marshal(SuministroLRBienesInversion.class, suministro); 
		String response = post(uri, sumStr);
    	RespuestaLRBienesInversionType respuesta = (RespuestaLRBienesInversionType) unmarshal(RespuestaLRBienesInversionType.class, response);
    	
    	requestXml = BienesInversion.getInstance().getSuministroBienesInversion(suministro);
    	responseXml = BienesInversion.getInstance().getRespuestaSuministroBienesInversion(respuesta);
       	String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst().orElse("Incorrecto");
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceId, requestXml, responseXml, status, contextList, type);
    	
    	for (RespuestaBienType r : respuesta.getRespuestaLinea()) {
    		Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    		array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    				correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    	}	
    	return array;
    }
    
    public JSONArray bajaBienesInversion(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) throws JAXBException, SOAPException, ParserConfigurationException, IOException {
    	JSONArray array = new JSONArray();
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	
    	BajaLRBienesInversion suministro = BienesInversion.getInstance().bajaBienesInversion(company, invoiceId, contextList, terceros);     	
    	String sumStr = marshal(BajaLRBienesInversion.class, suministro); 
		String response = post(uri, sumStr);
    	RespuestaLRBajaBienesInversionType respuesta = (RespuestaLRBajaBienesInversionType) unmarshal(RespuestaLRBajaBienesInversionType.class, response);
    		
	    for(RespuestaBienBajaType rect : respuesta.getRespuestaLinea()){
	    	Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
	    	array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
            	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
            			rect.getIDFactura().getNumSerieFacturaEmisor()));
	    }
    	
	    requestXml = BienesInversion.getInstance().getBajaBienesInversion(suministro);
	    responseXml = BienesInversion.getInstance().getRespuestaBajaBienesInversion(respuesta);
	    String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst().orElse("Incorrecto");
    	SIIDB.getInstance().insertSuministroBajas(domain, login, invoiceId, requestXml, responseXml, status, SendType.BAJA_INVERSION);
    	
    	return array;
    }
    
  // -------------------- OPERACIONES INTRACOMUNITARIAS
	
    public JSONArray suministroOperacionesIntracomunitarias(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String tipoOp, String terceros, String uri, SendType type) throws JAXBException, SOAPException, ParserConfigurationException, IOException {

		JSONArray array = new JSONArray();
		byte[] requestXml = null;
    	byte[] responseXml = null;
    	SuministroLRDetOperacionIntracomunitaria suministro = OperacionesIntracomunitarias.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, type.isModificacion(), terceros);     	
    	String sumStr = marshal(SuministroLRDetOperacionIntracomunitaria.class, suministro); 
		String response = post(uri, sumStr);
    	RespuestaLROComunitariasType respuesta = (RespuestaLROComunitariasType) unmarshal(RespuestaLROComunitariasType.class, response);
    		
    	requestXml = OperacionesIntracomunitarias.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    	responseXml = OperacionesIntracomunitarias.getInstance().getRespuestaSuministroOperacionesIntracomunitarias(respuesta);
       	String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst().orElse("Incorrecto");
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceId, requestXml, responseXml, status, contextList, type);
    	
    	for (RespuestaComunitariaType r : respuesta.getRespuestaLinea()) {
    		Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    		array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    				correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    	}

    	return array;
    }
    
    public JSONArray bajaOperacionesIntracomunitarias(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) throws JAXBException, SOAPException, ParserConfigurationException, IOException {    	
		JSONArray array = new JSONArray();

		byte[] requestXml = null;
    	byte[] responseXml = null;
    	
    	BajaLRDetOperacionIntracomunitaria suministro = OperacionesIntracomunitarias.getInstance().bajaOperacionesIntracomunitarias(company, invoiceId, contextList, terceros);     	
    	String sumStr = marshal(BajaLRDetOperacionIntracomunitaria.class, suministro); 
		String response = post(uri, sumStr);
    	RespuestaLRBajaOComunitariasType respuesta = (RespuestaLRBajaOComunitariasType) unmarshal(RespuestaLRBajaOComunitariasType.class, response);
    	
    	for(RespuestaComunitariaBajaType rect : respuesta.getRespuestaLinea()){
    		Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    		array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
    				correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
    						rect.getIDFactura().getNumSerieFacturaEmisor()));
    	}
    	
    	requestXml = OperacionesIntracomunitarias.getInstance().getBajaOperacionesIntracomunitarias(suministro);
    	responseXml = OperacionesIntracomunitarias.getInstance().getRespuestaBajaOperacionesIntracomunitarias(respuesta);
       	String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst().orElse("Incorrecto");

    	SIIDB.getInstance().insertSuministroBajas(domain, login, invoiceId, requestXml, responseXml, status, SendType.BAJA_INTRACOMUNITARIAS);
    	
    	return array;
    }
    
    
    // -------------------- COBROS METALICO
	
    public JSONObject suministroCobrosMetalico(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) throws JAXBException, SOAPException, ParserConfigurationException, IOException {
    	SuministroLRCobrosMetalico suministro = OperacionesTrascendenciaTributaria.getInstance().suministroCobrosMetalico(domain, login, company, invoiceId, contextList);     	
    	String sumStr = marshal(SuministroLRCobrosMetalico.class, suministro); 
		String response = post(uri, sumStr);
   		RespuestaLRIMetalicoType respuesta = (RespuestaLRIMetalicoType) unmarshal(RespuestaLRIMetalicoType.class, response);
   		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    		
   		//	byte[] requestXml = SIIBuilt.getInstance().getSuministroCobrosMetalico(suministro);
   		//	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroCobrosMetalico(respuesta);
   		// 	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_METALICO_OK : SIIDataVariable.INVOICE_SUMINISTRO_METALICO_ERROR);
    	
   		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(), "");
    }
    
    public JSONObject bajaCobrosMetalico(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) {
    	//RespuestaLRBajaIMetalicoType
    	return new JSONObject();
    }
    
    // -------------------- OPERACIONES SEGUROS
	
    public JSONObject suministroOperacionesSeguros(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) throws JAXBException, SOAPException, ParserConfigurationException, IOException {
    	SuministroLROperacionesSeguros suministro = OperacionesTrascendenciaTributaria.getInstance().suministroOperacionesSeguros(domain, login, company, invoiceId, contextList);     	
    	String sumStr = marshal(SuministroLROperacionesSeguros.class, suministro); 
		String response = post(uri, sumStr);
    	RespuestaLROperacionesSegurosType respuesta = (RespuestaLROperacionesSegurosType) unmarshal(RespuestaLROperacionesSegurosType.class, response);
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	
    	//	byte[] requestXml = SIIBuilt.getInstance().getSuministroOperacionesSeguros(suministro);
    	//	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroOperacionesSeguros(respuesta);
    	//	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_SEGUROS_OK: SIIDataVariable.INVOICE_SUMINISTRO_SEGUROS_ERROR);
    	
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	   	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(), "");
    }
    
    public JSONObject bajaOperacionesSeguros(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) {
    	//RespuestaLRBajaOperacionesSegurosType
    	return new JSONObject();
    }
    
    // -------------------- AGENCIAS VIAJES
	
    public JSONObject suministroAgenciasViajes(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) throws JAXBException, SOAPException, ParserConfigurationException, IOException {
   		SuministroLRAgenciasViajes suministro = OperacionesTrascendenciaTributaria.getInstance().suministroAgenciasViajes(domain, login, company, invoiceId, contextList);     	
   		String sumStr = marshal(SuministroLRAgenciasViajes.class, suministro); 
		String response = post(uri, sumStr);
   		RespuestaLRAgenciasViajesType respuesta = (RespuestaLRAgenciasViajesType) unmarshal(RespuestaLRAgenciasViajesType.class, response);
   		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);

   		// 	byte[] requestXml = SIIBuilt.getInstance().getSuministroAgenciasViajes(suministro);
   		// 	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroAgenciasViajes(respuesta);
   		// 	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_AGENCIAS_OK : SIIDataVariable.INVOICE_SUMINISTRO_AGENCIAS_ERROR);
  
   		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
   	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(), "");
    }
    
    public JSONObject bajaAgenciasViajes(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) {
    	//RespuestaLRBajaAgenciasViajesType
    	return new JSONObject();
    }
}