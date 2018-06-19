package net.aonsolutions.aon.sii.bizkaia;

import java.util.LinkedList;

import javax.xml.bind.JAXBElement;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;

import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.EstadoRegistroType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaBienBajaType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaBienType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaComunitariaBajaType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaComunitariaType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaExpedidaBajaType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaExpedidaCobroType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaExpedidaType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRAgenciasViajesType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRBajaBienesInversionType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRBajaFEmitidasType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRBajaFRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRBajaOComunitariasType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRBienesInversionType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRCobrosEmitidasType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRFEmitidasType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRFRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRIMetalicoType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLROComunitariasType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLROperacionesSegurosType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaLRPagosRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaRecibidaBajaType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaRecibidaPagoType;
import eus.bizkaia.ogasuna.sii.documentos.respuestasuministro.RespuestaRecibidaType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.BajaLRBienesInversion;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.BajaLRDetOperacionIntracomunitaria;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.BajaLRFacturasEmitidas;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.BajaLRFacturasRecibidas;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRAgenciasViajes;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRBienesInversion;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRCobrosEmitidas;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRCobrosMetalico;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRDetOperacionIntracomunitaria;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRFacturasEmitidas;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRFacturasRecibidas;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLROperacionesSeguros;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRPagosRecibidas;
import net.aonsolutions.aon.sii.SIIDB;
import net.aonsolutions.aon.sii.SIIPost2;
import net.aonsolutions.aon.sii.SendType;

public class SIIBizkaiaPost extends SIIPost2{
	
	private static final String CONTEXT_PATH = "eus.bizkaia.ogasuna.sii.documentos";
	
	public static SIIBizkaiaPost getInstance(byte[] cert, String pass) {
		return new SIIBizkaiaPost(cert, pass);
	}
	

	public SIIBizkaiaPost(byte[] cert, String pass) {
		super(cert, pass, CONTEXT_PATH);
	}
	
	// -------------------- FACTURAS EMITIDAS
	
    @SuppressWarnings("unchecked")
	public JSONArray suministroFacturasEmitidas(Domain domain, String login, Company company, Integer invoiceId,
    		LinkedList<VatContext> contextList, String terceros, String uri, LinkedList<VatContext> list, SendType type) {
		JSONArray array = new JSONArray();
		
   		byte[] requestXml = null;
		byte[] responseXml = null;
		
		SuministroLRFacturasEmitidas suministro = FacturasEmitidas.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceId, list, false, terceros);
    	JAXBElement<Object> response = (JAXBElement<Object>) post(uri, suministro);
    		
    	RespuestaLRFEmitidasType respuesta = (RespuestaLRFEmitidasType) response.getValue();
    	for (RespuestaExpedidaType r : respuesta.getRespuestaLinea()) {
    		Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    		array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    				correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    				r.getIDFactura().getNumSerieFacturaEmisor()));
        }
    			
    	requestXml = FacturasEmitidas.getInstance().getSuministroFacturasEmitidas((SuministroLRFacturasEmitidas) suministro);
    	responseXml = FacturasEmitidas.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);

    	String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst().orElse("Incorrecto");

    	SIIDB.getInstance().insertSuministro(domain, login, invoiceId, requestXml, responseXml, status, contextList, type);
    	
    	return array;
	}
    
    @SuppressWarnings("unchecked")
	public JSONArray bajaFacturasEmitidas(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) {
    	JSONArray array = new JSONArray();
    	
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	
    	BajaLRFacturasEmitidas suministro = FacturasEmitidas.getInstance().bajaFacturasEmitidas(company, invoiceId, contextList, terceros);     	
        JAXBElement<Object> response = (JAXBElement<Object>) post(uri, suministro);
        RespuestaLRBajaFEmitidasType respuesta = (RespuestaLRBajaFEmitidasType) response.getValue();
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
    
    @SuppressWarnings("unchecked")
    public JSONArray suministroFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Finance> financeList, Integer invoiceId, String uri) {    	
    	JSONArray array = new JSONArray();

    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	
    	SuministroLRCobrosEmitidas suministro = FacturasEmitidas.getInstance().suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceId);     	
    	JAXBElement<RespuestaLRCobrosEmitidasType> response = (JAXBElement<RespuestaLRCobrosEmitidasType>) post(uri, suministro);
    	RespuestaLRCobrosEmitidasType respuesta = response.getValue();
    	
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
    
    @SuppressWarnings("unchecked")
	public JSONArray suministroFacturasRecibidas(Domain domain, String login, Company company, Integer invoiceId,
    		LinkedList<VatContext> contextList, String terceros, String uri, LinkedList<VatContext> list, SendType type) {
		JSONArray array = new JSONArray();
		
   		byte[] requestXml = null;
		byte[] responseXml = null;
		
		SuministroLRFacturasRecibidas suministro = FacturasRecibidas.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceId, list, false, terceros);
    	JAXBElement<Object> response = (JAXBElement<Object>) post(uri, suministro);
    		
    	RespuestaLRFRecibidasType respuesta = (RespuestaLRFRecibidasType) response.getValue();
    	for (RespuestaRecibidaType r : respuesta.getRespuestaLinea()) {
    		Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    		array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    				correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    				r.getIDFactura().getNumSerieFacturaEmisor()));
        }
    			
    	requestXml = FacturasRecibidas.getInstance().getSuministroFacturasRecibidas((SuministroLRFacturasRecibidas) suministro);
    	responseXml = FacturasRecibidas.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
    	String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst().orElse("Incorrecto");

    	SIIDB.getInstance().insertSuministro(domain, login, invoiceId, requestXml, responseXml, status, contextList, type);
    	
    	return array;
	}

	@SuppressWarnings("unchecked")
	public JSONArray bajaFacturasRecibidas(Domain domain, String login, Company company, Integer invoiceId,
			LinkedList<VatContext> contextList, String terceros, String uri) {
		JSONArray array = new JSONArray();

		byte[] requestXml = null;
		byte[] responseXml = null;

		BajaLRFacturasRecibidas suministro = FacturasRecibidas.getInstance().bajaFacturasRecibidas(company, invoiceId,
				contextList, terceros);

		JAXBElement<RespuestaLRBajaFRecibidasType> response = (JAXBElement<RespuestaLRBajaFRecibidasType>) post(uri,
				suministro);
		RespuestaLRBajaFRecibidasType respuesta = response.getValue();

		for (RespuestaRecibidaBajaType rect : respuesta.getRespuestaLinea()) {
			Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(),
					correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
					rect.getIDFactura().getNumSerieFacturaEmisor()));
		}

		requestXml = FacturasRecibidas.getInstance().getBajaFacturasRecibidas(suministro);
		responseXml = FacturasRecibidas.getInstance().getRespuestaBajaFacturasRecibidas(respuesta);
		String status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).findFirst()
				.orElse("Incorrecto");
		SIIDB.getInstance().insertSuministroBajas(domain, login, invoiceId, requestXml, responseXml, status,
				SendType.BAJA_RECIBIDAS);

		return array;
	}
    	
    @SuppressWarnings("unchecked")
    public JSONArray suministroFacturasRecibidasPagos(Domain domain, String login, Company company, LinkedList<Finance> financeList, Integer invoiceId, String uri) {    	
    	JSONArray array = new JSONArray();
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	
   		SuministroLRPagosRecibidas suministro = FacturasRecibidas.getInstance().suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceId);     	
    
   		JAXBElement<RespuestaLRPagosRecibidasType> response = (JAXBElement<RespuestaLRPagosRecibidasType>) post(uri, suministro);
   		RespuestaLRPagosRecibidasType respuesta = response.getValue();
    	
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
	
    @SuppressWarnings("unchecked")
    public JSONArray suministroBienesInversion(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri, SendType type) {
    	JSONArray array = new JSONArray();

    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	
    	SuministroLRBienesInversion suministro = BienesInversion.getInstance().suministroBienesInversion(domain, login, company, invoiceId, contextList, false, terceros);     	
    	JAXBElement<RespuestaLRBienesInversionType> response = (JAXBElement<RespuestaLRBienesInversionType>) post(uri, suministro);
    	RespuestaLRBienesInversionType respuesta = response.getValue();
    	
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
    
    @SuppressWarnings("unchecked")
    public JSONArray bajaBienesInversion(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) {
    	JSONArray array = new JSONArray();
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	
    	BajaLRBienesInversion suministro = BienesInversion.getInstance().bajaBienesInversion(company, invoiceId, contextList, terceros);     	

    	JAXBElement<RespuestaLRBajaBienesInversionType> response = (JAXBElement<RespuestaLRBajaBienesInversionType>) post(uri, suministro);
    	RespuestaLRBajaBienesInversionType respuesta = response.getValue();
    		
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
	
    @SuppressWarnings("unchecked")
    public JSONArray suministroOperacionesIntracomunitarias(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String tipoOp, String terceros, String uri, SendType type) {

		JSONArray array = new JSONArray();
		byte[] requestXml = null;
    	byte[] responseXml = null;
    	SuministroLRDetOperacionIntracomunitaria suministro = OperacionesIntracomunitarias.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceId, contextList, tipoOp, false, terceros);     	
    	
    	JAXBElement<RespuestaLROComunitariasType> response = (JAXBElement<RespuestaLROComunitariasType>) post(uri, suministro);
    	RespuestaLROComunitariasType respuesta = response.getValue();
    		
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
    
    @SuppressWarnings("unchecked")
    public JSONArray bajaOperacionesIntracomunitarias(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) {    	
		JSONArray array = new JSONArray();

		byte[] requestXml = null;
    	byte[] responseXml = null;
    	
    	BajaLRDetOperacionIntracomunitaria suministro = OperacionesIntracomunitarias.getInstance().bajaOperacionesIntracomunitarias(company, invoiceId, contextList, terceros);     	
    	
    	JAXBElement<RespuestaLRBajaOComunitariasType> response = (JAXBElement<RespuestaLRBajaOComunitariasType>) post(uri, suministro);
    	RespuestaLRBajaOComunitariasType respuesta = response.getValue();
    	
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
	
    @SuppressWarnings("unchecked")
    public JSONObject suministroCobrosMetalico(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) {
    	SuministroLRCobrosMetalico suministro = OperacionesTrascendenciaTributaria.getInstance().suministroCobrosMetalico(domain, login, company, invoiceId, contextList);     	
    	
   		JAXBElement<RespuestaLRIMetalicoType> response = (JAXBElement<RespuestaLRIMetalicoType>) post(uri, suministro);
   		RespuestaLRIMetalicoType respuesta = response.getValue();
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
	
    @SuppressWarnings("unchecked")
    public JSONObject suministroOperacionesSeguros(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) {
    	SuministroLROperacionesSeguros suministro = OperacionesTrascendenciaTributaria.getInstance().suministroOperacionesSeguros(domain, login, company, invoiceId, contextList);     	
    	
    	JAXBElement<RespuestaLROperacionesSegurosType> response = (JAXBElement<RespuestaLROperacionesSegurosType>) post(uri, suministro);
    	RespuestaLROperacionesSegurosType respuesta = response.getValue();
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
	
    @SuppressWarnings("unchecked")
    public JSONObject suministroAgenciasViajes(Domain domain, String login, Company company, Integer invoiceId, LinkedList<VatContext> contextList, String terceros, String uri) {
   		SuministroLRAgenciasViajes suministro = OperacionesTrascendenciaTributaria.getInstance().suministroAgenciasViajes(domain, login, company, invoiceId, contextList);     	
   		JAXBElement<RespuestaLRAgenciasViajesType> response = (JAXBElement<RespuestaLRAgenciasViajesType>) post(uri, suministro);
   		RespuestaLRAgenciasViajesType respuesta = response.getValue();
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