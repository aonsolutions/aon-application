package net.aonsolutions.aon.sii;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.net.ssl.KeyManagerFactory;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.mime.MimeContainer;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Administration;

import net.aonsolutions.core.aeat.sii.BajaLRBienesInversion;
import net.aonsolutions.core.aeat.sii.BajaLRDetOperacionIntracomunitaria;
import net.aonsolutions.core.aeat.sii.BajaLRFacturasEmitidas;
import net.aonsolutions.core.aeat.sii.BajaLRFacturasRecibidas;
import net.aonsolutions.core.aeat.sii.EstadoRegistroType;
import net.aonsolutions.core.aeat.sii.RespuestaBienBajaType;
import net.aonsolutions.core.aeat.sii.RespuestaBienType;
import net.aonsolutions.core.aeat.sii.RespuestaComunitariaBajaType;
import net.aonsolutions.core.aeat.sii.RespuestaComunitariaType;
import net.aonsolutions.core.aeat.sii.RespuestaExpedidaBajaType;
import net.aonsolutions.core.aeat.sii.RespuestaExpedidaCobroType;
import net.aonsolutions.core.aeat.sii.RespuestaExpedidaType;
import net.aonsolutions.core.aeat.sii.RespuestaLRAgenciasViajesType;
import net.aonsolutions.core.aeat.sii.RespuestaLRBajaBienesInversionType;
import net.aonsolutions.core.aeat.sii.RespuestaLRBajaFEmitidasType;
import net.aonsolutions.core.aeat.sii.RespuestaLRBajaFRecibidasType;
import net.aonsolutions.core.aeat.sii.RespuestaLRBajaOComunitariasType;
import net.aonsolutions.core.aeat.sii.RespuestaLRBienesInversionType;
import net.aonsolutions.core.aeat.sii.RespuestaLRCobrosEmitidasType;
import net.aonsolutions.core.aeat.sii.RespuestaLRFEmitidasType;
import net.aonsolutions.core.aeat.sii.RespuestaLRFRecibidasType;
import net.aonsolutions.core.aeat.sii.RespuestaLRIMetalicoType;
import net.aonsolutions.core.aeat.sii.RespuestaLROComunitariasType;
import net.aonsolutions.core.aeat.sii.RespuestaLROperacionesSegurosType;
import net.aonsolutions.core.aeat.sii.RespuestaLRPagosRecibidasType;
import net.aonsolutions.core.aeat.sii.RespuestaRecibidaBajaType;
import net.aonsolutions.core.aeat.sii.RespuestaRecibidaPagoType;
import net.aonsolutions.core.aeat.sii.RespuestaRecibidaType;
import net.aonsolutions.core.aeat.sii.SuministroLRAgenciasViajes;
import net.aonsolutions.core.aeat.sii.SuministroLRBienesInversion;
import net.aonsolutions.core.aeat.sii.SuministroLRCobrosEmitidas;
import net.aonsolutions.core.aeat.sii.SuministroLRCobrosMetalico;
import net.aonsolutions.core.aeat.sii.SuministroLRDetOperacionIntracomunitaria;
import net.aonsolutions.core.aeat.sii.SuministroLRFacturasEmitidas;
import net.aonsolutions.core.aeat.sii.SuministroLRFacturasRecibidas;
import net.aonsolutions.core.aeat.sii.SuministroLROperacionesSeguros;
import net.aonsolutions.core.aeat.sii.SuministroLRPagosRecibidas;

public class SIIPost extends WebServiceGatewaySupport{
	
	private Boolean pruebas = false;
	
	public static SIIPost getInstance(byte[] cert, String pass, Administration administration) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		return new SIIPost(cert, pass, administration);
	}
	
	byte[] cert;
	String pass;
	private Administration administration;

	public SIIPost(byte[] cert, String pass, Administration administration) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		this.cert = cert;
		this.pass = pass;
		this.administration = administration;
		ByteArrayInputStream key = new ByteArrayInputStream(cert);
	
		KeyStore keyStore = KeyStore.getInstance("PKCS12");

	    keyStore.load(key, pass.toCharArray());
    	
	    System.out.println(KeyManagerFactory.getDefaultAlgorithm());
    	KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   		kmf.init(keyStore, pass.toCharArray());

   		HttpsUrlConnectionMessageSender messageSender = new HttpsUrlConnectionMessageSender();
    	messageSender.setKeyManagers(kmf.getKeyManagers());
    	
    	messageSender.setSslProtocol("SSLv3");
  
    	setMessageSender(messageSender);
    	
    	CustJaxbUnMarshaller marshaller = new CustJaxbUnMarshaller();
    	
    	if(isAeat() || isNavarra()) marshaller.setContextPath("net.aonsolutions.core.aeat.sii");
    	else if(isAraba()) marshaller.setContextPath("net.aonsolutions.core.araba.sii");
    	else if(isGipuzkoa()) marshaller.setContextPath("net.aonsolutions.core.gipuzkoa.sii");
    	else if(isBizkaia()) marshaller.setContextPath("net.aonsolutions.core.bizkaia.sii");

    	setMarshaller(marshaller);
        setUnmarshaller(marshaller);
	}

	private Boolean isAeat() {
		return Administration.COMMON_TERRITORY.equals(administration) || Administration.UNKNOWN.equals(administration);
 	}
	
	private Boolean isAraba() {
		return Administration.ALAVA.equals(administration);
 	}
	
	private Boolean isGipuzkoa() {
		return Administration.GIPUZKOA.equals(administration);
 	}
	
	private Boolean isBizkaia() {
		return Administration.BIZKAIA.equals(administration);
 	}
	
	private Boolean isNavarra() {
		return Administration.NAVARRA.equals(administration);
 	}
	
	private Object post(String uri, Object object) {
		return getWebServiceTemplate().marshalSendAndReceive(uri, object);
	}
	
	private JSONObject json(Integer id, String name, String referenceCode){
		JSONObject json = new JSONObject();
    	json.put("id", id);
    	json.put("name", "Factura " + referenceCode + (!id.equals(200) ?  " - Error " + id + ": " : " - ") + name);
    	return json;
	}
	
	// -------------------- FACTURAS EMITIDAS
	
	@SuppressWarnings("unchecked")
    protected JSONArray suministroFacturasEmitidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros, Administration administration) {
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS, administration) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS, administration);
    	
    	LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
    			|| "AceptadoConErrores".equals(v.getSiiStatus())
    			|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));
    	
    	LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
    			|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));
    	
    	    	
    	JSONArray array = new JSONArray();
		
   		byte[] requestXml = null;
		byte[] responseXml = null;
		LinkedList<String> status = new LinkedList<>();
		
    	// ALTA
    	if(newList.size() > 0){
    		Object suministroNew = null;
    		if(isAeat() || isNavarra()) suministroNew = SIIBuilt.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceList, newList, cert, pass, false, terceros);
    		else if(isAraba()) suministroNew = SIIArabaBuilt.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceList, newList, cert, pass, false, terceros);
    		else if(isGipuzkoa()) suministroNew = SIIGipuzkoaBuilt.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceList, newList, cert, pass, false, terceros);
    		else if(isBizkaia()) suministroNew = SIIBizkaiaBuilt.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceList, newList, cert, pass, false, terceros);
    		
			JAXBElement<Object> response = (JAXBElement<Object>) post(uri, suministroNew);
    		
    		if(isAeat() || isNavarra()) {
    			RespuestaLRFEmitidasType respuesta = (RespuestaLRFEmitidasType) response.getValue();
    			for (RespuestaExpedidaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
        			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
        					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
        					r.getIDFactura().getNumSerieFacturaEmisor()));
        		}
    			
    			requestXml = SIIBuilt.getInstance().getSuministroFacturasEmitidas((SuministroLRFacturasEmitidas) suministroNew);
    			responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));

    		} else if(isAraba()) {
    			net.aonsolutions.core.araba.sii.RespuestaLRFEmitidasType respuesta = (net.aonsolutions.core.araba.sii.RespuestaLRFEmitidasType) response.getValue();
    			for (net.aonsolutions.core.araba.sii.RespuestaExpedidaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
        			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
        					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
        					r.getIDFactura().getNumSerieFacturaEmisor()));
        		}
    			
    			requestXml = SIIArabaBuilt.getInstance().getSuministroFacturasEmitidas((net.aonsolutions.core.araba.sii.SuministroLRFacturasEmitidas) suministroNew);
    			responseXml = SIIArabaBuilt.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		} else if(isGipuzkoa()) {
    			net.aonsolutions.core.gipuzkoa.sii.RespuestaLRFEmitidasType respuesta = (net.aonsolutions.core.gipuzkoa.sii.RespuestaLRFEmitidasType) response.getValue();
    			for (net.aonsolutions.core.gipuzkoa.sii.RespuestaExpedidaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
        			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
        					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
        					r.getIDFactura().getNumSerieFacturaEmisor()));
        		}
    			
    			requestXml = SIIGipuzkoaBuilt.getInstance().getSuministroFacturasEmitidas((net.aonsolutions.core.gipuzkoa.sii.SuministroLRFacturasEmitidas) suministroNew);
    			responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		} else if(isBizkaia()) {
    			net.aonsolutions.core.bizkaia.sii.RespuestaLRFEmitidasType respuesta = (net.aonsolutions.core.bizkaia.sii.RespuestaLRFEmitidasType) response.getValue();
    			for (net.aonsolutions.core.bizkaia.sii.RespuestaExpedidaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
        			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
        					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
        					r.getIDFactura().getNumSerieFacturaEmisor()));
        		}
    			
    			requestXml = SIIBizkaiaBuilt.getInstance().getSuministroFacturasEmitidas((net.aonsolutions.core.bizkaia.sii.SuministroLRFacturasEmitidas) suministroNew);
    			responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		}
    		
    		SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.ALTA_EMITIDAS);
    	}

    	// MODIFICACI�N
    	if(modList.size() > 0){
    		Object suministroMod = null;
    		
    		if(isAeat() || isNavarra()) suministroMod = (SuministroLRFacturasEmitidas) SIIBuilt.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceList, modList, cert, pass, true, terceros);
    		if(isAraba()) suministroMod = (net.aonsolutions.core.araba.sii.SuministroLRFacturasEmitidas) SIIArabaBuilt.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceList, modList, cert, pass, true, terceros);
    		if(isGipuzkoa()) suministroMod = (net.aonsolutions.core.gipuzkoa.sii.SuministroLRFacturasEmitidas) SIIGipuzkoaBuilt.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceList, modList, cert, pass, true, terceros);
    		if(isBizkaia()) suministroMod = (net.aonsolutions.core.bizkaia.sii.SuministroLRFacturasEmitidas) SIIBizkaiaBuilt.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceList, modList, cert, pass, true, terceros);
    		
    		JAXBElement<Object> response = (JAXBElement<Object>) post(uri, suministroMod);
    		
    		if(isAeat() || isNavarra()) {
    			RespuestaLRFEmitidasType respuesta = (RespuestaLRFEmitidasType) response.getValue();
    			for (RespuestaExpedidaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    					r.getIDFactura().getNumSerieFacturaEmisor()));
    			}

    			requestXml = SIIBuilt.getInstance().getSuministroFacturasEmitidas((SuministroLRFacturasEmitidas) suministroMod);
    			responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		} else if(isAraba()) {
    			net.aonsolutions.core.araba.sii.RespuestaLRFEmitidasType respuesta = (net.aonsolutions.core.araba.sii.RespuestaLRFEmitidasType) response.getValue();
    			for (net.aonsolutions.core.araba.sii.RespuestaExpedidaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    					r.getIDFactura().getNumSerieFacturaEmisor()));
    			}

    			requestXml = SIIArabaBuilt.getInstance().getSuministroFacturasEmitidas((net.aonsolutions.core.araba.sii.SuministroLRFacturasEmitidas) suministroMod);
    			responseXml = SIIArabaBuilt.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		} else if(isGipuzkoa()) {
    			net.aonsolutions.core.gipuzkoa.sii.RespuestaLRFEmitidasType respuesta = (net.aonsolutions.core.gipuzkoa.sii.RespuestaLRFEmitidasType) response.getValue();
    			for (net.aonsolutions.core.gipuzkoa.sii.RespuestaExpedidaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    					r.getIDFactura().getNumSerieFacturaEmisor()));
    			}

    			requestXml = SIIGipuzkoaBuilt.getInstance().getSuministroFacturasEmitidas((net.aonsolutions.core.gipuzkoa.sii.SuministroLRFacturasEmitidas) suministroMod);
    			responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		} else if(isBizkaia()) {
    			net.aonsolutions.core.bizkaia.sii.RespuestaLRFEmitidasType respuesta = (net.aonsolutions.core.bizkaia.sii.RespuestaLRFEmitidasType) response.getValue();
    			for (net.aonsolutions.core.bizkaia.sii.RespuestaExpedidaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    					r.getIDFactura().getNumSerieFacturaEmisor()));
    			}

    			requestXml = SIIBizkaiaBuilt.getInstance().getSuministroFacturasEmitidas((net.aonsolutions.core.bizkaia.sii.SuministroLRFacturasEmitidas) suministroMod);
    			responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		}
    		SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.MOD_EMITIDAS);
    	}
    	
        return array;
	}
	
	@SuppressWarnings("unchecked")
    protected JSONArray bajaFacturasEmitidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros, Administration administration) {
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS, administration) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS, administration);
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	LinkedList<String> statusList = new LinkedList<>();
    	JSONArray array = new JSONArray();

    	if(isAeat() || isNavarra()) {
    		BajaLRFacturasEmitidas suministro = SIIBuilt.getInstance().bajaFacturasEmitidas(company, invoiceList, contextList, terceros);     	
        	JAXBElement<Object> response = (JAXBElement<Object>) post(uri, suministro);
        	RespuestaLRBajaFEmitidasType respuesta = (RespuestaLRBajaFEmitidasType) response.getValue();
        	for(RespuestaExpedidaBajaType rect : respuesta.getRespuestaLinea()){
            	Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
        		array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
            	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
            	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
        	}
        	requestXml = SIIBuilt.getInstance().getBajaFacturasEmitidas(suministro);
       		responseXml = SIIBuilt.getInstance().getRespuestaBajaFacturasEmitidas(respuesta);
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	}else if(isAraba()) {
    		net.aonsolutions.core.araba.sii.BajaLRFacturasEmitidas suministro = SIIArabaBuilt.getInstance().bajaFacturasEmitidas(company, invoiceList, contextList, terceros);     	
        	JAXBElement<Object> response = (JAXBElement<Object>) post(uri, suministro);
        	net.aonsolutions.core.araba.sii.RespuestaLRBajaFEmitidasType respuesta = (net.aonsolutions.core.araba.sii.RespuestaLRBajaFEmitidasType) response.getValue();
        	for(net.aonsolutions.core.araba.sii.RespuestaExpedidaBajaType rect : respuesta.getRespuestaLinea()){
            	Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
        		array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
            	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
            	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
        	}
        	requestXml = SIIArabaBuilt.getInstance().getBajaFacturasEmitidas(suministro);
       		responseXml = SIIArabaBuilt.getInstance().getRespuestaBajaFacturasEmitidas(respuesta);
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isGipuzkoa()) {
    		net.aonsolutions.core.gipuzkoa.sii.BajaLRFacturasEmitidas suministro = SIIGipuzkoaBuilt.getInstance().bajaFacturasEmitidas(company, invoiceList, contextList, terceros);     	
        	JAXBElement<Object> response = (JAXBElement<Object>) post(uri, suministro);
        	net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBajaFEmitidasType respuesta = (net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBajaFEmitidasType) response.getValue();
        	for(net.aonsolutions.core.gipuzkoa.sii.RespuestaExpedidaBajaType rect : respuesta.getRespuestaLinea()){
            	Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
        		array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
            	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
            	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
        	}
        	requestXml = SIIGipuzkoaBuilt.getInstance().getBajaFacturasEmitidas(suministro);
       		responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaBajaFacturasEmitidas(respuesta);
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isBizkaia()) {
    		net.aonsolutions.core.bizkaia.sii.BajaLRFacturasEmitidas suministro = SIIBizkaiaBuilt.getInstance().bajaFacturasEmitidas(company, invoiceList, contextList, terceros);     	
        	JAXBElement<Object> response = (JAXBElement<Object>) post(uri, suministro);
        	net.aonsolutions.core.bizkaia.sii.RespuestaLRBajaFEmitidasType respuesta = (net.aonsolutions.core.bizkaia.sii.RespuestaLRBajaFEmitidasType) response.getValue();
        	for(net.aonsolutions.core.bizkaia.sii.RespuestaExpedidaBajaType rect : respuesta.getRespuestaLinea()){
            	Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
        		array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
            	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
            	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
        	}
        	requestXml = SIIBizkaiaBuilt.getInstance().getBajaFacturasEmitidas(suministro);
       		responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaBajaFacturasEmitidas(respuesta);
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	}
	
		HashMap<Integer, String> status = new HashMap<>();
    	for(Integer i = 0; i < invoiceList.size(); i ++){
    		status.put(invoiceList.get(i), statusList.get(i));
    	}
    	SIIDB.getInstance().insertSuministroBajas(domain, login, invoiceList, requestXml, responseXml, status, SendType.BAJA_EMITIDAS);
    	
    	return array;
	}
	
    // -------------------- FACTURAS EMITIDAS COBROS
	
    @SuppressWarnings("unchecked")
    protected JSONArray suministroFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Finance> financeList, LinkedList<Integer> invoiceList, String terceros, Administration administration) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS_COBROS, administration) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS_COBROS, administration);
    	
    	JSONArray array = new JSONArray();
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	LinkedList<String> statusList = new LinkedList<>();
    	
    	if(isAeat() || isNavarra()) {
    		// AEAT || NAVARRA
    		SuministroLRCobrosEmitidas suministro = SIIBuilt.getInstance().suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceList);     	
    		JAXBElement<RespuestaLRCobrosEmitidasType> response = (JAXBElement<RespuestaLRCobrosEmitidasType>) post(uri, suministro);
    		RespuestaLRCobrosEmitidasType respuesta = response.getValue();
    	
    		for(RespuestaExpedidaCobroType rect : respuesta.getRespuestaLinea()){
    			Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIBuilt.getInstance().getSuministroFacturasEmitidasCobros(suministro);
    		responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasEmitidasCobros(respuesta);
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isAraba()) {
    		// ARABA
    		net.aonsolutions.core.araba.sii.SuministroLRCobrosEmitidas suministro = SIIArabaBuilt.getInstance().suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceList);     	
    		JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRCobrosEmitidasType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRCobrosEmitidasType>) post(uri, suministro);
    		net.aonsolutions.core.araba.sii.RespuestaLRCobrosEmitidasType respuesta = response.getValue();
    	
    		for(net.aonsolutions.core.araba.sii.RespuestaExpedidaCobroType rect : respuesta.getRespuestaLinea()){
    			Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIArabaBuilt.getInstance().getSuministroFacturasEmitidasCobros(suministro);
    		responseXml = SIIArabaBuilt.getInstance().getRespuestaSuministroFacturasEmitidasCobros(respuesta);
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isGipuzkoa()) {
    		// GIPUZKOA
    		net.aonsolutions.core.gipuzkoa.sii.SuministroLRCobrosEmitidas suministro = SIIGipuzkoaBuilt.getInstance().suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceList);     	
    		JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRCobrosEmitidasType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRCobrosEmitidasType>) post(uri, suministro);
    		net.aonsolutions.core.gipuzkoa.sii.RespuestaLRCobrosEmitidasType respuesta = response.getValue();
    	
    		for(net.aonsolutions.core.gipuzkoa.sii.RespuestaExpedidaCobroType rect : respuesta.getRespuestaLinea()){
    			Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIGipuzkoaBuilt.getInstance().getSuministroFacturasEmitidasCobros(suministro);
    		responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaSuministroFacturasEmitidasCobros(respuesta);
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isBizkaia()) {
    		// BIZKAIA
    		net.aonsolutions.core.bizkaia.sii.SuministroLRCobrosEmitidas suministro = SIIBizkaiaBuilt.getInstance().suministroFacturasEmitidasCobros(domain, login, company, financeList, invoiceList);     	
    		JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRCobrosEmitidasType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRCobrosEmitidasType>) post(uri, suministro);
    		net.aonsolutions.core.bizkaia.sii.RespuestaLRCobrosEmitidasType respuesta = response.getValue();
    	
    		for(net.aonsolutions.core.bizkaia.sii.RespuestaExpedidaCobroType rect : respuesta.getRespuestaLinea()){
    			Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIBizkaiaBuilt.getInstance().getSuministroFacturasEmitidasCobros(suministro);
    		responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaSuministroFacturasEmitidasCobros(respuesta);
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	}
    	
    	HashMap<Integer, String> status = new HashMap<>();
    	for(Integer i = 0; i < invoiceList.size(); i ++){
    		status.put(invoiceList.get(i), statusList.get(i));
    	}
    	SIIDB.getInstance().insertSuministroCobrosPagos(domain, login, invoiceList, requestXml, responseXml, financeList, status);
    	
    	return array;
    }
    
    // -------------------- FACTURAS RECIBIDAS
	
    @SuppressWarnings("unchecked")
	protected JSONArray suministroFacturasRecibidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros, Administration administration) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_RECIBIDAS, administration) : SIIUri.getInstance().getURI(SIIType.FACTURAS_RECIBIDAS, administration);
    	LinkedList<VatContext> modList = contextList.stream().filter(v-> v.getSiiStatus().equals("Correcto")
    			|| v.getSiiStatus().equals("AceptadoConErrores")
    			|| v.getSiiStatus().equals("Anulada")).collect(Collectors.toCollection(LinkedList::new));
    	
    	LinkedList<VatContext> newList = contextList.stream().filter(v-> v.getSiiStatus().equals("Pendiente")
    			|| v.getSiiStatus().equals("Incorrecto")).collect(Collectors.toCollection(LinkedList::new));   	
  
    	JSONArray array = new JSONArray();
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	LinkedList<String> status = new LinkedList<>();
    	// ALTA
    	if(newList.size()>0){
    		if(isAeat() || isNavarra()) {
    			SuministroLRFacturasRecibidas suministroNew = SIIBuilt.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceList, newList, cert, pass, false, terceros);     	
    	    
    			JAXBElement<RespuestaLRFRecibidasType> response = (JAXBElement<RespuestaLRFRecibidasType>) post(uri, suministroNew);
    			RespuestaLRFRecibidasType respuesta = response.getValue();	
    			for (RespuestaRecibidaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    						correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    								r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		
    			requestXml = SIIBuilt.getInstance().getSuministroFacturasRecibidas(suministroNew);
    			responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		} else if(isAraba()) {
    			net.aonsolutions.core.araba.sii.SuministroLRFacturasRecibidas suministroNew = SIIArabaBuilt.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceList, newList, cert, pass, false, terceros);     	
    	    
    			JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRFRecibidasType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRFRecibidasType>) post(uri, suministroNew);
    			net.aonsolutions.core.araba.sii.RespuestaLRFRecibidasType respuesta = response.getValue();	
    			for (net.aonsolutions.core.araba.sii.RespuestaRecibidaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    						correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    								r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		
    			requestXml = SIIArabaBuilt.getInstance().getSuministroFacturasRecibidas(suministroNew);
    			responseXml = SIIArabaBuilt.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		} else if(isGipuzkoa()) {
    			net.aonsolutions.core.gipuzkoa.sii.SuministroLRFacturasRecibidas suministroNew = SIIGipuzkoaBuilt.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceList, newList, cert, pass, false, terceros);     	
    	    
    			JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRFRecibidasType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRFRecibidasType>) post(uri, suministroNew);
    			net.aonsolutions.core.gipuzkoa.sii.RespuestaLRFRecibidasType respuesta = response.getValue();	
    			for (net.aonsolutions.core.gipuzkoa.sii.RespuestaRecibidaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    						correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    								r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		
    			requestXml = SIIGipuzkoaBuilt.getInstance().getSuministroFacturasRecibidas(suministroNew);
    			responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		} else if(isBizkaia()) {
    			net.aonsolutions.core.bizkaia.sii.SuministroLRFacturasRecibidas suministroNew = SIIBizkaiaBuilt.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceList, newList, cert, pass, false, terceros);     	
    	    
    			JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRFRecibidasType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRFRecibidasType>) post(uri, suministroNew);
    			net.aonsolutions.core.bizkaia.sii.RespuestaLRFRecibidasType respuesta = response.getValue();	
    			for (net.aonsolutions.core.bizkaia.sii.RespuestaRecibidaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    						correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    								r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		
    			requestXml = SIIBizkaiaBuilt.getInstance().getSuministroFacturasRecibidas(suministroNew);
    			responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		}
    		
			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.ALTA_RECIBIDAS);

    	}
    	
    	// MODIFICACI�N
    	if(modList.size() > 0){
        	if(isAeat() || isNavarra()) {
        		SuministroLRFacturasRecibidas suministroMod = SIIBuilt.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceList, modList, cert, pass, true, terceros);
        		JAXBElement<RespuestaLRFRecibidasType> response = (JAXBElement<RespuestaLRFRecibidasType>) post(uri, suministroMod);
        		RespuestaLRFRecibidasType respuesta = response.getValue();	
    
        		for (RespuestaRecibidaType r : respuesta.getRespuestaLinea()) {
        			Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
        			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
        					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
        							r.getIDFactura().getNumSerieFacturaEmisor()));
        		}
    		
        		requestXml = SIIBuilt.getInstance().getSuministroFacturasRecibidas(suministroMod);
        		responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
        		status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
        	} else if(isAraba()) {
        		net.aonsolutions.core.araba.sii.SuministroLRFacturasRecibidas suministroMod = SIIArabaBuilt.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceList, modList, cert, pass, true, terceros);
        		JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRFRecibidasType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRFRecibidasType>) post(uri, suministroMod);
        		net.aonsolutions.core.araba.sii.RespuestaLRFRecibidasType respuesta = response.getValue();	
    
        		for (net.aonsolutions.core.araba.sii.RespuestaRecibidaType r : respuesta.getRespuestaLinea()) {
        			Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
        			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
        					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
        							r.getIDFactura().getNumSerieFacturaEmisor()));
        		}
    		
        		requestXml = SIIArabaBuilt.getInstance().getSuministroFacturasRecibidas(suministroMod);
        		responseXml = SIIArabaBuilt.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
        		status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
        	} else if(isGipuzkoa()) {
        		net.aonsolutions.core.gipuzkoa.sii.SuministroLRFacturasRecibidas suministroMod = SIIGipuzkoaBuilt.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceList, modList, cert, pass, true, terceros);
        		JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRFRecibidasType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRFRecibidasType>) post(uri, suministroMod);
        		net.aonsolutions.core.gipuzkoa.sii.RespuestaLRFRecibidasType respuesta = response.getValue();	
    
        		for (net.aonsolutions.core.gipuzkoa.sii.RespuestaRecibidaType r : respuesta.getRespuestaLinea()) {
        			Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
        			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
        					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
        							r.getIDFactura().getNumSerieFacturaEmisor()));
        		}
    		
        		requestXml = SIIGipuzkoaBuilt.getInstance().getSuministroFacturasRecibidas(suministroMod);
        		responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
        		status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
        	} else if(isBizkaia()) {
        		net.aonsolutions.core.bizkaia.sii.SuministroLRFacturasRecibidas suministroMod = SIIBizkaiaBuilt.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceList, modList, cert, pass, true, terceros);
        		JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRFRecibidasType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRFRecibidasType>) post(uri, suministroMod);
        		net.aonsolutions.core.bizkaia.sii.RespuestaLRFRecibidasType respuesta = response.getValue();	
    
        		for (net.aonsolutions.core.bizkaia.sii.RespuestaRecibidaType r : respuesta.getRespuestaLinea()) {
        			Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
        			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
        					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
        							r.getIDFactura().getNumSerieFacturaEmisor()));
        		}
    		
        		requestXml = SIIBizkaiaBuilt.getInstance().getSuministroFacturasRecibidas(suministroMod);
        		responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
        		status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
        	}
        	
        	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.MOD_RECIBIDAS);
    	}
    
        return array;
    }
    
    @SuppressWarnings("unchecked")
   	protected JSONArray bajaFacturasRecibidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros, Administration administration) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_RECIBIDAS, administration) : SIIUri.getInstance().getURI(SIIType.FACTURAS_RECIBIDAS, administration);
    
    	JSONArray array = new JSONArray();
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	LinkedList<String> statusList = new LinkedList<>();
    	
    	if(isAeat() || isNavarra()) {
    		BajaLRFacturasRecibidas suministro = SIIBuilt.getInstance().bajaFacturasRecibidas(company, invoiceList, contextList, terceros);     	

    		JAXBElement<RespuestaLRBajaFRecibidasType> response = (JAXBElement<RespuestaLRBajaFRecibidasType>) post(uri, suministro);
    		RespuestaLRBajaFRecibidasType respuesta = response.getValue();
    	
    		for(RespuestaRecibidaBajaType rect : respuesta.getRespuestaLinea()){
    			Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIBuilt.getInstance().getBajaFacturasRecibidas(suministro);
    		responseXml = SIIBuilt.getInstance().getRespuestaBajaFacturasRecibidas(respuesta);
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isAraba()) {
    		net.aonsolutions.core.araba.sii.BajaLRFacturasRecibidas suministro = SIIArabaBuilt.getInstance().bajaFacturasRecibidas(company, invoiceList, contextList, terceros);     	

    		JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRBajaFRecibidasType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRBajaFRecibidasType>) post(uri, suministro);
    		net.aonsolutions.core.araba.sii.RespuestaLRBajaFRecibidasType respuesta = response.getValue();
    	
    		for(net.aonsolutions.core.araba.sii.RespuestaRecibidaBajaType rect : respuesta.getRespuestaLinea()){
    			Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIArabaBuilt.getInstance().getBajaFacturasRecibidas(suministro);
    		responseXml = SIIArabaBuilt.getInstance().getRespuestaBajaFacturasRecibidas(respuesta);
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isGipuzkoa()) {
    		net.aonsolutions.core.gipuzkoa.sii.BajaLRFacturasRecibidas suministro = SIIGipuzkoaBuilt.getInstance().bajaFacturasRecibidas(company, invoiceList, contextList, terceros);     	

    		JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBajaFRecibidasType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBajaFRecibidasType>) post(uri, suministro);
    		net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBajaFRecibidasType respuesta = response.getValue();
    	
    		for(net.aonsolutions.core.gipuzkoa.sii.RespuestaRecibidaBajaType rect : respuesta.getRespuestaLinea()){
    			Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIGipuzkoaBuilt.getInstance().getBajaFacturasRecibidas(suministro);
    		responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaBajaFacturasRecibidas(respuesta);
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isBizkaia()) {
    		net.aonsolutions.core.bizkaia.sii.BajaLRFacturasRecibidas suministro = SIIBizkaiaBuilt.getInstance().bajaFacturasRecibidas(company, invoiceList, contextList, terceros);     	

    		JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRBajaFRecibidasType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRBajaFRecibidasType>) post(uri, suministro);
    		net.aonsolutions.core.bizkaia.sii.RespuestaLRBajaFRecibidasType respuesta = response.getValue();
    	
    		for(net.aonsolutions.core.bizkaia.sii.RespuestaRecibidaBajaType rect : respuesta.getRespuestaLinea()){
    			Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIBizkaiaBuilt.getInstance().getBajaFacturasRecibidas(suministro);
    		responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaBajaFacturasRecibidas(respuesta);
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	}
    	
    	HashMap<Integer, String> status = new HashMap<>();
    	for(Integer i = 0; i < invoiceList.size(); i ++){
    		status.put(invoiceList.get(i), statusList.get(i));
    	}
    	SIIDB.getInstance().insertSuministroBajas(domain, login, invoiceList, requestXml, responseXml, status, SendType.BAJA_RECIBIDAS);
    	
    	return array;
    }
    
    // -------------------- FACTURAS RECIBIDAS PAGOS
	
    @SuppressWarnings("unchecked")
    protected JSONArray suministroFacturasRecibidasPagos(Domain domain, String login, Company company, LinkedList<Finance> financeList, LinkedList<Integer> invoiceList, String terceros, Administration administration) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_RECIBIDAS_PAGOS, administration) : SIIUri.getInstance().getURI(SIIType.FACTURAS_RECIBIDAS_PAGOS, administration);
    	
    	JSONArray array = new JSONArray();
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	LinkedList<String> statusList = new LinkedList<>();
    	
    	if(isAeat() || isNavarra()) {
    		SuministroLRPagosRecibidas suministro = SIIBuilt.getInstance().suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceList);     	
    
    		JAXBElement<RespuestaLRPagosRecibidasType> response = (JAXBElement<RespuestaLRPagosRecibidasType>) post(uri, suministro);
    		RespuestaLRPagosRecibidasType respuesta = response.getValue();
    	
    		for(RespuestaRecibidaPagoType rrpt : respuesta.getRespuestaLinea()){
    			Boolean correcto = rrpt.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rrpt.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rrpt.getDescripcionErrorRegistro(),
        	    			rrpt.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIBuilt.getInstance().getSuministroFacturasRecibidasPagos(suministro);
    		responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasRecibidasPagos(respuesta);

    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isAraba()) {
    		net.aonsolutions.core.araba.sii.SuministroLRPagosRecibidas suministro = SIIArabaBuilt.getInstance().suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceList);     	
    	    
    		JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRPagosRecibidasType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRPagosRecibidasType>) post(uri, suministro);
    		net.aonsolutions.core.araba.sii.RespuestaLRPagosRecibidasType respuesta = response.getValue();
    	
    		for(net.aonsolutions.core.araba.sii.RespuestaRecibidaPagoType rrpt : respuesta.getRespuestaLinea()){
    			Boolean correcto = rrpt.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rrpt.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rrpt.getDescripcionErrorRegistro(),
        	    			rrpt.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIArabaBuilt.getInstance().getSuministroFacturasRecibidasPagos(suministro);
    		responseXml = SIIArabaBuilt.getInstance().getRespuestaSuministroFacturasRecibidasPagos(respuesta);

    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isGipuzkoa()) {
    		net.aonsolutions.core.gipuzkoa.sii.SuministroLRPagosRecibidas suministro = SIIGipuzkoaBuilt.getInstance().suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceList);     	
    	    
    		JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRPagosRecibidasType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRPagosRecibidasType>) post(uri, suministro);
    		net.aonsolutions.core.gipuzkoa.sii.RespuestaLRPagosRecibidasType respuesta = response.getValue();
    	
    		for(net.aonsolutions.core.gipuzkoa.sii.RespuestaRecibidaPagoType rrpt : respuesta.getRespuestaLinea()){
    			Boolean correcto = rrpt.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rrpt.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rrpt.getDescripcionErrorRegistro(),
        	    			rrpt.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIGipuzkoaBuilt.getInstance().getSuministroFacturasRecibidasPagos(suministro);
    		responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaSuministroFacturasRecibidasPagos(respuesta);

    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isBizkaia()) {
    		net.aonsolutions.core.bizkaia.sii.SuministroLRPagosRecibidas suministro = SIIBizkaiaBuilt.getInstance().suministroFacturasRecibidasPagos(domain, login, company, financeList, invoiceList);     	
    	    
    		JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRPagosRecibidasType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRPagosRecibidasType>) post(uri, suministro);
    		net.aonsolutions.core.bizkaia.sii.RespuestaLRPagosRecibidasType respuesta = response.getValue();
    	
    		for(net.aonsolutions.core.bizkaia.sii.RespuestaRecibidaPagoType rrpt : respuesta.getRespuestaLinea()){
    			Boolean correcto = rrpt.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rrpt.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rrpt.getDescripcionErrorRegistro(),
        	    			rrpt.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIBizkaiaBuilt.getInstance().getSuministroFacturasRecibidasPagos(suministro);
    		responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaSuministroFacturasRecibidasPagos(respuesta);

    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	}
    	
    	HashMap<Integer, String> status = new HashMap<>();
    	for(Integer i = 0; i < invoiceList.size(); i ++){
    		status.put(invoiceList.get(i), statusList.get(i));
    	}
    	SIIDB.getInstance().insertSuministroCobrosPagos(domain, login, invoiceList, requestXml, responseXml, financeList, status);

    	return array;
    }
    
    // -------------------- BIENES INVERSION
	
    @SuppressWarnings("unchecked")
    protected JSONArray suministroBienesInversion(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros, Administration administration) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.BIENES_INVERSION, administration) : SIIUri.getInstance().getURI(SIIType.BIENES_INVERSION, administration);
    	LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
    			|| "AceptadoConErrores".equals(v.getSiiStatus())
    			|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));
    	
    	LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
    			|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));
    	
    	JSONArray array = new JSONArray();
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	LinkedList<String> status = new LinkedList<>();
    	
    	// ALTA
    	if(newList.size() > 0){
    		if(isAeat() || isNavarra()) {
    			SuministroLRBienesInversion suministro = SIIBuilt.getInstance().suministroBienesInversion(domain, login, company, invoiceList, contextList, false, terceros);     	
    			JAXBElement<RespuestaLRBienesInversionType> response = (JAXBElement<RespuestaLRBienesInversionType>) post(uri, suministro);
    			RespuestaLRBienesInversionType respuesta = response.getValue();
    		
    			requestXml = SIIBuilt.getInstance().getSuministroBienesInversion(suministro);
    			responseXml = SIIBuilt.getInstance().getRespuestaSuministroBienesInversion(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.ALTA_INVERSION);
    			
    			for (RespuestaBienType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    						correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		} else if(isAraba()) {
     			net.aonsolutions.core.araba.sii.SuministroLRBienesInversion suministro = SIIArabaBuilt.getInstance().suministroBienesInversion(domain, login, company, invoiceList, contextList, false, terceros);     	
    			JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRBienesInversionType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRBienesInversionType>) post(uri, suministro);
    			net.aonsolutions.core.araba.sii.RespuestaLRBienesInversionType respuesta = response.getValue();
    		
    			requestXml = SIIArabaBuilt.getInstance().getSuministroBienesInversion(suministro);
    			responseXml = SIIArabaBuilt.getInstance().getRespuestaSuministroBienesInversion(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.ALTA_INVERSION);
    			
    			for (net.aonsolutions.core.araba.sii.RespuestaBienType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    						correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		} else if(isGipuzkoa()) {
     			net.aonsolutions.core.gipuzkoa.sii.SuministroLRBienesInversion suministro = SIIGipuzkoaBuilt.getInstance().suministroBienesInversion(domain, login, company, invoiceList, contextList, false, terceros);     	
    			JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBienesInversionType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBienesInversionType>) post(uri, suministro);
    			net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBienesInversionType respuesta = response.getValue();
    		
    			requestXml = SIIGipuzkoaBuilt.getInstance().getSuministroBienesInversion(suministro);
    			responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaSuministroBienesInversion(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.ALTA_INVERSION);
    			
    			for (net.aonsolutions.core.gipuzkoa.sii.RespuestaBienType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    						correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		} else if(isBizkaia()) {
     			net.aonsolutions.core.bizkaia.sii.SuministroLRBienesInversion suministro = SIIBizkaiaBuilt.getInstance().suministroBienesInversion(domain, login, company, invoiceList, contextList, false, terceros);     	
    			JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRBienesInversionType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRBienesInversionType>) post(uri, suministro);
    			net.aonsolutions.core.bizkaia.sii.RespuestaLRBienesInversionType respuesta = response.getValue();
    		
    			requestXml = SIIBizkaiaBuilt.getInstance().getSuministroBienesInversion(suministro);
    			responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaSuministroBienesInversion(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.ALTA_INVERSION);
    			
    			for (net.aonsolutions.core.bizkaia.sii.RespuestaBienType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    						correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		}
    	} 
    	
    	// MODIFICACI�N
    	if(modList.size() > 0){
        	if(isAeat() || isNavarra()) {
        		SuministroLRBienesInversion suministro = SIIBuilt.getInstance().suministroBienesInversion(domain, login, company, invoiceList, contextList, true, terceros);     	
        		JAXBElement<RespuestaLRBienesInversionType> response = (JAXBElement<RespuestaLRBienesInversionType>) post(uri, suministro);
        		RespuestaLRBienesInversionType respuesta = response.getValue();
        	
        		requestXml = SIIBuilt.getInstance().getSuministroBienesInversion(suministro);
        		responseXml = SIIBuilt.getInstance().getRespuestaSuministroBienesInversion(respuesta);
        		status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
        		SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.MOD_INVERSION);
    	
        		for (RespuestaBienType r : respuesta.getRespuestaLinea()) {
        			Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
        			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
        		}
        	} else if(isAraba()) {
        		net.aonsolutions.core.araba.sii.SuministroLRBienesInversion suministro = SIIArabaBuilt.getInstance().suministroBienesInversion(domain, login, company, invoiceList, contextList, true, terceros);     	
        		JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRBienesInversionType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRBienesInversionType>) post(uri, suministro);
        		net.aonsolutions.core.araba.sii.RespuestaLRBienesInversionType respuesta = response.getValue();
        	
        		requestXml = SIIArabaBuilt.getInstance().getSuministroBienesInversion(suministro);
        		responseXml = SIIArabaBuilt.getInstance().getRespuestaSuministroBienesInversion(respuesta);
        		status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
        		SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.MOD_INVERSION);
    	
        		for (net.aonsolutions.core.araba.sii.RespuestaBienType r : respuesta.getRespuestaLinea()) {
        			Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
        			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
        		}
        	} else if(isGipuzkoa()) {
        		net.aonsolutions.core.gipuzkoa.sii.SuministroLRBienesInversion suministro = SIIGipuzkoaBuilt.getInstance().suministroBienesInversion(domain, login, company, invoiceList, contextList, true, terceros);     	
        		JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBienesInversionType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBienesInversionType>) post(uri, suministro);
        		net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBienesInversionType respuesta = response.getValue();
        	
        		requestXml = SIIGipuzkoaBuilt.getInstance().getSuministroBienesInversion(suministro);
        		responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaSuministroBienesInversion(respuesta);
        		status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
        		SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.MOD_INVERSION);
    	
        		for (net.aonsolutions.core.gipuzkoa.sii.RespuestaBienType r : respuesta.getRespuestaLinea()) {
        			Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
        			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
        		}
        	} else if(isBizkaia()) {
        		net.aonsolutions.core.bizkaia.sii.SuministroLRBienesInversion suministro = SIIBizkaiaBuilt.getInstance().suministroBienesInversion(domain, login, company, invoiceList, contextList, true, terceros);     	
        		JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRBienesInversionType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRBienesInversionType>) post(uri, suministro);
        		net.aonsolutions.core.bizkaia.sii.RespuestaLRBienesInversionType respuesta = response.getValue();
        	
        		requestXml = SIIBizkaiaBuilt.getInstance().getSuministroBienesInversion(suministro);
        		responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaSuministroBienesInversion(respuesta);
        		status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
        		SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.MOD_INVERSION);
    	
        		for (net.aonsolutions.core.bizkaia.sii.RespuestaBienType r : respuesta.getRespuestaLinea()) {
        			Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
        			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
        		}
        	}
    	}
    	return array;
    }
    
    @SuppressWarnings("unchecked")
    protected JSONArray bajaBienesInversion(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros, Administration administration) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.BIENES_INVERSION, administration) : SIIUri.getInstance().getURI(SIIType.BIENES_INVERSION, administration);

    	JSONArray array = new JSONArray();
    	byte[] requestXml = null;
    	byte[] responseXml = null;
    	LinkedList<String> statusList = new LinkedList<>();
    	
    	if(isAeat() || isNavarra()) {
    		BajaLRBienesInversion suministro = SIIBuilt.getInstance().bajaBienesInversion(company, invoiceList, contextList, terceros);     	

    		JAXBElement<RespuestaLRBajaBienesInversionType> response = (JAXBElement<RespuestaLRBajaBienesInversionType>) post(uri, suministro);
    		RespuestaLRBajaBienesInversionType respuesta = response.getValue();
    		
	    	for(RespuestaBienBajaType rect : respuesta.getRespuestaLinea()){
	    		Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
	    		array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
	    	}
    	
	    	requestXml = SIIBuilt.getInstance().getBajaBienesInversion(suministro);
	    	responseXml = SIIBuilt.getInstance().getRespuestaBajaBienesInversion(respuesta);
	    	statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isAraba()) {
    		net.aonsolutions.core.araba.sii.BajaLRBienesInversion suministro = SIIArabaBuilt.getInstance().bajaBienesInversion(company, invoiceList, contextList, terceros);     	

    		JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRBajaBienesInversionType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRBajaBienesInversionType>) post(uri, suministro);
    		net.aonsolutions.core.araba.sii.RespuestaLRBajaBienesInversionType respuesta = response.getValue();
    		
	    	for(net.aonsolutions.core.araba.sii.RespuestaBienBajaType rect : respuesta.getRespuestaLinea()){
	    		Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
	    		array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
	    	}
    	
	    	requestXml = SIIArabaBuilt.getInstance().getBajaBienesInversion(suministro);
	    	responseXml = SIIArabaBuilt.getInstance().getRespuestaBajaBienesInversion(respuesta);
	    	statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isGipuzkoa()) {
    		net.aonsolutions.core.gipuzkoa.sii.BajaLRBienesInversion suministro = SIIGipuzkoaBuilt.getInstance().bajaBienesInversion(company, invoiceList, contextList, terceros);     	

    		JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBajaBienesInversionType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBajaBienesInversionType>) post(uri, suministro);
    		net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBajaBienesInversionType respuesta = response.getValue();
    		
	    	for(net.aonsolutions.core.gipuzkoa.sii.RespuestaBienBajaType rect : respuesta.getRespuestaLinea()){
	    		Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
	    		array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
	    	}
    	
	    	requestXml = SIIGipuzkoaBuilt.getInstance().getBajaBienesInversion(suministro);
	    	responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaBajaBienesInversion(respuesta);
	    	statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));    		
    	} else if(isBizkaia()) {
    		net.aonsolutions.core.bizkaia.sii.BajaLRBienesInversion suministro = SIIBizkaiaBuilt.getInstance().bajaBienesInversion(company, invoiceList, contextList, terceros);     	

    		JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRBajaBienesInversionType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRBajaBienesInversionType>) post(uri, suministro);
    		net.aonsolutions.core.bizkaia.sii.RespuestaLRBajaBienesInversionType respuesta = response.getValue();
    		
	    	for(net.aonsolutions.core.bizkaia.sii.RespuestaBienBajaType rect : respuesta.getRespuestaLinea()){
	    		Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
	    		array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
	    	}
    	
	    	requestXml = SIIBizkaiaBuilt.getInstance().getBajaBienesInversion(suministro);
	    	responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaBajaBienesInversion(respuesta);
	    	statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	}
	    	
	    HashMap<Integer, String> status = new HashMap<>();
    	for(Integer i = 0; i < invoiceList.size(); i ++){
    		status.put(invoiceList.get(i), statusList.get(i));
    	}
    	SIIDB.getInstance().insertSuministroBajas(domain, login, invoiceList, requestXml, responseXml, status, SendType.BAJA_INVERSION);
    	
    	return array;
    }
    
    // -------------------- OPERACIONES INTRACOMUNITARIAS
	
    @SuppressWarnings("unchecked")
    protected JSONArray suministroOperacionesIntracomunitarias(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String tipoOp, String terceros, Administration administration) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.OPERACIONES_INTRACOMUNITARIAS, administration) : SIIUri.getInstance().getURI(SIIType.OPERACIONES_INTRACOMUNITARIAS, administration);
    	
    	LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
    			|| "AceptadoConErrores".equals(v.getSiiStatus())
    			|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));
    	
    	LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
    			|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));
    	
		JSONArray array = new JSONArray();
		byte[] requestXml = null;
    	byte[] responseXml = null;
    	LinkedList<String> status = new LinkedList<>();
    	// ALTA
    	if(newList.size() > 0){
    		if(isAeat() || isNavarra()) {
    			SuministroLRDetOperacionIntracomunitaria suministro = SIIBuilt.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceList, contextList, tipoOp, false, terceros);     	
    	
    			JAXBElement<RespuestaLROComunitariasType> response = (JAXBElement<RespuestaLROComunitariasType>) post(uri, suministro);
    			RespuestaLROComunitariasType respuesta = response.getValue();
    	
    			requestXml = SIIBuilt.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    			responseXml = SIIBuilt.getInstance().getRespuestaSuministroOperacionesIntracomunitarias(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.ALTA_INTRACOMUNITARIAS);
    	
    			for (RespuestaComunitariaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		} else if(isAraba()) {
    			net.aonsolutions.core.araba.sii.SuministroLRDetOperacionIntracomunitaria suministro = SIIArabaBuilt.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceList, contextList, tipoOp, false, terceros);     	
    	    	
    			JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLROComunitariasType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLROComunitariasType>) post(uri, suministro);
    			net.aonsolutions.core.araba.sii.RespuestaLROComunitariasType respuesta = response.getValue();
    	
    			requestXml = SIIArabaBuilt.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    			responseXml = SIIArabaBuilt.getInstance().getRespuestaSuministroOperacionesIntracomunitarias(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.ALTA_INTRACOMUNITARIAS);
    	
    			for (net.aonsolutions.core.araba.sii.RespuestaComunitariaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		} else if(isGipuzkoa()) {
    			net.aonsolutions.core.gipuzkoa.sii.SuministroLRDetOperacionIntracomunitaria suministro = SIIGipuzkoaBuilt.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceList, contextList, tipoOp, false, terceros);     	
    	    	
    			JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLROComunitariasType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLROComunitariasType>) post(uri, suministro);
    			net.aonsolutions.core.gipuzkoa.sii.RespuestaLROComunitariasType respuesta = response.getValue();
    	
    			requestXml = SIIGipuzkoaBuilt.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    			responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaSuministroOperacionesIntracomunitarias(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.ALTA_INTRACOMUNITARIAS);
    	
    			for (net.aonsolutions.core.gipuzkoa.sii.RespuestaComunitariaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		} else if(isBizkaia()) {
    			net.aonsolutions.core.bizkaia.sii.SuministroLRDetOperacionIntracomunitaria suministro = SIIBizkaiaBuilt.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceList, contextList, tipoOp, false, terceros);     	
    	    	
    			JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLROComunitariasType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLROComunitariasType>) post(uri, suministro);
    			net.aonsolutions.core.bizkaia.sii.RespuestaLROComunitariasType respuesta = response.getValue();
    	
    			requestXml = SIIBizkaiaBuilt.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    			responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaSuministroOperacionesIntracomunitarias(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.ALTA_INTRACOMUNITARIAS);
    	
    			for (net.aonsolutions.core.bizkaia.sii.RespuestaComunitariaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		}
    	}  
    	
    	// MODIFICACI�N
    	if(modList.size() > 0){
    		if(isAeat() || isNavarra()) {
    			SuministroLRDetOperacionIntracomunitaria suministro = SIIBuilt.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceList, contextList, tipoOp, true, terceros);     	
    	
    			JAXBElement<RespuestaLROComunitariasType> response = (JAXBElement<RespuestaLROComunitariasType>) post(uri, suministro);
    			RespuestaLROComunitariasType respuesta = response.getValue();
    	
    			requestXml = SIIBuilt.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    			responseXml = SIIBuilt.getInstance().getRespuestaSuministroOperacionesIntracomunitarias(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.MOD_INTRACOMUNITARIAS);
    	
    			for (RespuestaComunitariaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		} else if(isAraba()) {
    			net.aonsolutions.core.araba.sii.SuministroLRDetOperacionIntracomunitaria suministro = SIIArabaBuilt.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceList, contextList, tipoOp, true, terceros);     	
    	    	
    			JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLROComunitariasType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLROComunitariasType>) post(uri, suministro);
    			net.aonsolutions.core.araba.sii.RespuestaLROComunitariasType respuesta = response.getValue();
    	
    			requestXml = SIIArabaBuilt.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    			responseXml = SIIArabaBuilt.getInstance().getRespuestaSuministroOperacionesIntracomunitarias(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.MOD_INTRACOMUNITARIAS);
    	
    			for (net.aonsolutions.core.araba.sii.RespuestaComunitariaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		} else if(isGipuzkoa()) {
    			net.aonsolutions.core.gipuzkoa.sii.SuministroLRDetOperacionIntracomunitaria suministro = SIIGipuzkoaBuilt.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceList, contextList, tipoOp, true, terceros);     	
    	    	
    			JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLROComunitariasType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLROComunitariasType>) post(uri, suministro);
    			net.aonsolutions.core.gipuzkoa.sii.RespuestaLROComunitariasType respuesta = response.getValue();
    	
    			requestXml = SIIGipuzkoaBuilt.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    			responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaSuministroOperacionesIntracomunitarias(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.MOD_INTRACOMUNITARIAS);
    	
    			for (net.aonsolutions.core.gipuzkoa.sii.RespuestaComunitariaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		} else if(isBizkaia()) {
    			net.aonsolutions.core.bizkaia.sii.SuministroLRDetOperacionIntracomunitaria suministro = SIIBizkaiaBuilt.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceList, contextList, tipoOp, true, terceros);     	
    	    	
    			JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLROComunitariasType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLROComunitariasType>) post(uri, suministro);
    			net.aonsolutions.core.bizkaia.sii.RespuestaLROComunitariasType respuesta = response.getValue();
    	
    			requestXml = SIIBizkaiaBuilt.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    			responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaSuministroOperacionesIntracomunitarias(respuesta);
    			status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    			SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status, contextList, SendType.MOD_INTRACOMUNITARIAS);
    	
    			for (net.aonsolutions.core.bizkaia.sii.RespuestaComunitariaType r : respuesta.getRespuestaLinea()) {
    				Boolean correcto = r.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
    				array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    						r.getIDFactura().getNumSerieFacturaEmisor()));
    			}
    		}
    	}
    	return array;
    }
    
    @SuppressWarnings("unchecked")
    protected JSONArray bajaOperacionesIntracomunitarias(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros, Administration administration) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.OPERACIONES_INTRACOMUNITARIAS, administration) : SIIUri.getInstance().getURI(SIIType.OPERACIONES_INTRACOMUNITARIAS, administration);
    	
		JSONArray array = new JSONArray();
		byte[] requestXml = null;
    	byte[] responseXml = null;
    	LinkedList<String> statusList = new LinkedList<>();
    	
    	if(isAeat() || isNavarra()) {
    		BajaLRDetOperacionIntracomunitaria suministro = SIIBuilt.getInstance().bajaOperacionesIntracomunitarias(company, invoiceList, contextList, terceros);     	

    		JAXBElement<RespuestaLRBajaOComunitariasType> response = (JAXBElement<RespuestaLRBajaOComunitariasType>) post(uri, suministro);
    		RespuestaLRBajaOComunitariasType respuesta = response.getValue();
    	
    		for(RespuestaComunitariaBajaType rect : respuesta.getRespuestaLinea()){
    			Boolean correcto = rect.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIBuilt.getInstance().getBajaOperacionesIntracomunitarias(suministro);
    		responseXml = SIIBuilt.getInstance().getRespuestaBajaOperacionesIntracomunitarias(respuesta);
    	
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isAraba()) {
    		net.aonsolutions.core.araba.sii.BajaLRDetOperacionIntracomunitaria suministro = SIIArabaBuilt.getInstance().bajaOperacionesIntracomunitarias(company, invoiceList, contextList, terceros);     	

    		JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRBajaOComunitariasType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRBajaOComunitariasType>) post(uri, suministro);
    		net.aonsolutions.core.araba.sii.RespuestaLRBajaOComunitariasType respuesta = response.getValue();
    	
    		for(net.aonsolutions.core.araba.sii.RespuestaComunitariaBajaType rect : respuesta.getRespuestaLinea()){
    			Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIArabaBuilt.getInstance().getBajaOperacionesIntracomunitarias(suministro);
    		responseXml = SIIArabaBuilt.getInstance().getRespuestaBajaOperacionesIntracomunitarias(respuesta);
    	
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isGipuzkoa()) {
    		net.aonsolutions.core.gipuzkoa.sii.BajaLRDetOperacionIntracomunitaria suministro = SIIGipuzkoaBuilt.getInstance().bajaOperacionesIntracomunitarias(company, invoiceList, contextList, terceros);     	

    		JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBajaOComunitariasType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBajaOComunitariasType>) post(uri, suministro);
    		net.aonsolutions.core.gipuzkoa.sii.RespuestaLRBajaOComunitariasType respuesta = response.getValue();
    	
    		for(net.aonsolutions.core.gipuzkoa.sii.RespuestaComunitariaBajaType rect : respuesta.getRespuestaLinea()){
    			Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIGipuzkoaBuilt.getInstance().getBajaOperacionesIntracomunitarias(suministro);
    		responseXml = SIIGipuzkoaBuilt.getInstance().getRespuestaBajaOperacionesIntracomunitarias(respuesta);
    	
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	} else if(isBizkaia()) {
    		net.aonsolutions.core.bizkaia.sii.BajaLRDetOperacionIntracomunitaria suministro = SIIBizkaiaBuilt.getInstance().bajaOperacionesIntracomunitarias(company, invoiceList, contextList, terceros);     	

    		JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRBajaOComunitariasType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRBajaOComunitariasType>) post(uri, suministro);
    		net.aonsolutions.core.bizkaia.sii.RespuestaLRBajaOComunitariasType respuesta = response.getValue();
    	
    		for(net.aonsolutions.core.bizkaia.sii.RespuestaComunitariaBajaType rect : respuesta.getRespuestaLinea()){
    			Boolean correcto = rect.getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
    			array.put(json(correcto ? 200 : rect.getCodigoErrorRegistro().intValue(), 
        	    	correcto ? "Envio realizado correctamente" : rect.getDescripcionErrorRegistro(),
        	    			rect.getIDFactura().getNumSerieFacturaEmisor()));
    		}
    	
    		requestXml = SIIBizkaiaBuilt.getInstance().getBajaOperacionesIntracomunitarias(suministro);
    		responseXml = SIIBizkaiaBuilt.getInstance().getRespuestaBajaOperacionesIntracomunitarias(respuesta);
    	
    		statusList = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	}
   		HashMap<Integer, String> status = new HashMap<>();
    	for(Integer i = 0; i < invoiceList.size(); i ++){
    		status.put(invoiceList.get(i), statusList.get(i));
    	}
    	SIIDB.getInstance().insertSuministroBajas(domain, login, invoiceList, requestXml, responseXml, status, SendType.BAJA_INTRACOMUNITARIAS);
    	
    	return array;
    }
    
    // -------------------- COBROS METALICO
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroCobrosMetalico(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros, Administration administration) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.COBROS_METALICO, administration) : SIIUri.getInstance().getURI(SIIType.COBROS_METALICO, administration);
    	
    	if(isAeat() || isNavarra()) {
    		SuministroLRCobrosMetalico suministro = SIIBuilt.getInstance().suministroCobrosMetalico(domain, login, company, invoiceList, contextList);     	
    	
    		JAXBElement<RespuestaLRIMetalicoType> response = (JAXBElement<RespuestaLRIMetalicoType>) post(uri, suministro);
    		RespuestaLRIMetalicoType respuesta = response.getValue();
    		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    		
    		//	byte[] requestXml = SIIBuilt.getInstance().getSuministroCobrosMetalico(suministro);
    		//	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroCobrosMetalico(respuesta);
    		// 	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_METALICO_OK : SIIDataVariable.INVOICE_SUMINISTRO_METALICO_ERROR);
    	
    		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    	} else if(isAraba()) {
    		net.aonsolutions.core.araba.sii.SuministroLRCobrosMetalico suministro = SIIArabaBuilt.getInstance().suministroCobrosMetalico(domain, login, company, invoiceList, contextList);     	
        	
    		JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRIMetalicoType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRIMetalicoType>) post(uri, suministro);
    		net.aonsolutions.core.araba.sii.RespuestaLRIMetalicoType respuesta = response.getValue();
    		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
    		
    		//	byte[] requestXml = SIIBuilt.getInstance().getSuministroCobrosMetalico(suministro);
    		//	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroCobrosMetalico(respuesta);
    		// 	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_METALICO_OK : SIIDataVariable.INVOICE_SUMINISTRO_METALICO_ERROR);
    	
    		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    	} else if(isGipuzkoa()) {
    		net.aonsolutions.core.gipuzkoa.sii.SuministroLRCobrosMetalico suministro = SIIGipuzkoaBuilt.getInstance().suministroCobrosMetalico(domain, login, company, invoiceList, contextList);     	
        	
    		JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRIMetalicoType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRIMetalicoType>) post(uri, suministro);
    		net.aonsolutions.core.gipuzkoa.sii.RespuestaLRIMetalicoType respuesta = response.getValue();
    		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
    		
    		//	byte[] requestXml = SIIBuilt.getInstance().getSuministroCobrosMetalico(suministro);
    		//	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroCobrosMetalico(respuesta);
    		// 	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_METALICO_OK : SIIDataVariable.INVOICE_SUMINISTRO_METALICO_ERROR);
    	
    		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    	} else if(isBizkaia()) {
    		net.aonsolutions.core.bizkaia.sii.SuministroLRCobrosMetalico suministro = SIIBizkaiaBuilt.getInstance().suministroCobrosMetalico(domain, login, company, invoiceList, contextList);     	
        	
    		JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRIMetalicoType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRIMetalicoType>) post(uri, suministro);
    		net.aonsolutions.core.bizkaia.sii.RespuestaLRIMetalicoType respuesta = response.getValue();
    		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
    		
    		//	byte[] requestXml = SIIBuilt.getInstance().getSuministroCobrosMetalico(suministro);
    		//	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroCobrosMetalico(respuesta);
    		// 	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_METALICO_OK : SIIDataVariable.INVOICE_SUMINISTRO_METALICO_ERROR);
    	
    		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    	} 
    	return new JSONObject();
    }
    
    protected JSONObject bajaCobrosMetalico(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	//RespuestaLRBajaIMetalicoType
    	return new JSONObject();
    }
    
    // -------------------- OPERACIONES SEGUROS
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroOperacionesSeguros(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros, Administration administration) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.OPERACIONES_SEGUROS, administration) : SIIUri.getInstance().getURI(SIIType.OPERACIONES_SEGUROS, administration);
    		
    	if(isAeat() || isNavarra()) {
    		SuministroLROperacionesSeguros suministro = SIIBuilt.getInstance().suministroOperacionesSeguros(domain, login, company, invoiceList, contextList);     	
    	
    		JAXBElement<RespuestaLROperacionesSegurosType> response = (JAXBElement<RespuestaLROperacionesSegurosType>) post(uri, suministro);
    		RespuestaLROperacionesSegurosType respuesta = response.getValue();
    		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	
    		//	byte[] requestXml = SIIBuilt.getInstance().getSuministroOperacionesSeguros(suministro);
    		//	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroOperacionesSeguros(respuesta);
    		//	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_SEGUROS_OK: SIIDataVariable.INVOICE_SUMINISTRO_SEGUROS_ERROR);
    	
    		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    	} else if(isAraba()) {
    		net.aonsolutions.core.araba.sii.SuministroLROperacionesSeguros suministro = SIIArabaBuilt.getInstance().suministroOperacionesSeguros(domain, login, company, invoiceList, contextList);     	
        	
    		JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLROperacionesSegurosType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLROperacionesSegurosType>) post(uri, suministro);
    		net.aonsolutions.core.araba.sii.RespuestaLROperacionesSegurosType respuesta = response.getValue();
    		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);
    		
    		//	byte[] requestXml = SIIBuilt.getInstance().getSuministroOperacionesSeguros(suministro);
    		//	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroOperacionesSeguros(respuesta);
    		//	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_SEGUROS_OK: SIIDataVariable.INVOICE_SUMINISTRO_SEGUROS_ERROR);
    	
    		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    	} else if(isGipuzkoa()) {
    		/* TODO
    		net.aonsolutions.core.gipuzkoa.sii.SuministroLROperacionesSeguros suministro = SIIGipuzkoaBuilt.getInstance().suministroOperacionesSeguros(domain, login, company, invoiceList, contextList);     	
        	
    		JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLROperacionesSegurosType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLROperacionesSegurosType>) post(uri, suministro);
    		net.aonsolutions.core.gipuzkoa.sii.RespuestaLROperacionesSegurosType respuesta = response.getValue();
    		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);
    		
    		//	byte[] requestXml = SIIBuilt.getInstance().getSuministroOperacionesSeguros(suministro);
    		//	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroOperacionesSeguros(respuesta);
    		//	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_SEGUROS_OK: SIIDataVariable.INVOICE_SUMINISTRO_SEGUROS_ERROR);
    	
    		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    		*/
    	} else if(isBizkaia()) {
    		net.aonsolutions.core.bizkaia.sii.SuministroLROperacionesSeguros suministro = SIIBizkaiaBuilt.getInstance().suministroOperacionesSeguros(domain, login, company, invoiceList, contextList);     	
        	
    		JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLROperacionesSegurosType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLROperacionesSegurosType>) post(uri, suministro);
    		net.aonsolutions.core.bizkaia.sii.RespuestaLROperacionesSegurosType respuesta = response.getValue();
    		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);
    	
    		//	byte[] requestXml = SIIBuilt.getInstance().getSuministroOperacionesSeguros(suministro);
    		//	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroOperacionesSeguros(respuesta);
    		//	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_SEGUROS_OK: SIIDataVariable.INVOICE_SUMINISTRO_SEGUROS_ERROR);
    	
    		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    	} 
    	return new JSONObject();
    }
    
    protected JSONObject bajaOperacionesSeguros(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	//RespuestaLRBajaOperacionesSegurosType
    	return new JSONObject();
    }
    
    // -------------------- AGENCIAS VIAJES
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroAgenciasViajes(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros, Administration administration) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.AGENCIAS_VIAJES, administration) : SIIUri.getInstance().getURI(SIIType.AGENCIAS_VIAJES, administration);
    	if(isAeat() || isNavarra()) {
    		SuministroLRAgenciasViajes suministro = SIIBuilt.getInstance().suministroAgenciasViajes(domain, login, company, invoiceList, contextList);     	
    		JAXBElement<RespuestaLRAgenciasViajesType> response = (JAXBElement<RespuestaLRAgenciasViajesType>) post(uri, suministro);
    		RespuestaLRAgenciasViajesType respuesta = response.getValue();
    		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);

    		// 	byte[] requestXml = SIIBuilt.getInstance().getSuministroAgenciasViajes(suministro);
    		// 	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroAgenciasViajes(respuesta);
    		// 	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_AGENCIAS_OK : SIIDataVariable.INVOICE_SUMINISTRO_AGENCIAS_ERROR);
   
    		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    	} else if(isAraba()) {
    		net.aonsolutions.core.araba.sii.SuministroLRAgenciasViajes suministro = SIIArabaBuilt.getInstance().suministroAgenciasViajes(domain, login, company, invoiceList, contextList);     	
    		JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRAgenciasViajesType> response = (JAXBElement<net.aonsolutions.core.araba.sii.RespuestaLRAgenciasViajesType>) post(uri, suministro);
    		net.aonsolutions.core.araba.sii.RespuestaLRAgenciasViajesType respuesta = response.getValue();
    		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(net.aonsolutions.core.araba.sii.EstadoRegistroType.CORRECTO);

    		// 	byte[] requestXml = SIIBuilt.getInstance().getSuministroAgenciasViajes(suministro);
    		// 	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroAgenciasViajes(respuesta);
    		// 	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_AGENCIAS_OK : SIIDataVariable.INVOICE_SUMINISTRO_AGENCIAS_ERROR);
   
    		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    	} else if(isGipuzkoa()) {
    		net.aonsolutions.core.gipuzkoa.sii.SuministroLRAgenciasViajes suministro = SIIGipuzkoaBuilt.getInstance().suministroAgenciasViajes(domain, login, company, invoiceList, contextList);     	
    		JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRAgenciasViajesType> response = (JAXBElement<net.aonsolutions.core.gipuzkoa.sii.RespuestaLRAgenciasViajesType>) post(uri, suministro);
    		net.aonsolutions.core.gipuzkoa.sii.RespuestaLRAgenciasViajesType respuesta = response.getValue();
    		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(net.aonsolutions.core.gipuzkoa.sii.EstadoRegistroType.CORRECTO);

    		// 	byte[] requestXml = SIIBuilt.getInstance().getSuministroAgenciasViajes(suministro);
    		// 	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroAgenciasViajes(respuesta);
    		// 	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_AGENCIAS_OK : SIIDataVariable.INVOICE_SUMINISTRO_AGENCIAS_ERROR);
   
    		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    	} else if(isBizkaia()) {
    		net.aonsolutions.core.bizkaia.sii.SuministroLRAgenciasViajes suministro = SIIBizkaiaBuilt.getInstance().suministroAgenciasViajes(domain, login, company, invoiceList, contextList);     	
    		JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRAgenciasViajesType> response = (JAXBElement<net.aonsolutions.core.bizkaia.sii.RespuestaLRAgenciasViajesType>) post(uri, suministro);
    		net.aonsolutions.core.bizkaia.sii.RespuestaLRAgenciasViajesType respuesta = response.getValue();
    		Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(net.aonsolutions.core.bizkaia.sii.EstadoRegistroType.CORRECTO);

    		// 	byte[] requestXml = SIIBuilt.getInstance().getSuministroAgenciasViajes(suministro);
    		// 	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroAgenciasViajes(respuesta);
    		// 	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_AGENCIAS_OK : SIIDataVariable.INVOICE_SUMINISTRO_AGENCIAS_ERROR);
   
    		return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    	}
    	return new JSONObject();
    }
    
    protected JSONObject bajaAgenciasViajes(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	//RespuestaLRBajaAgenciasViajesType
    	return new JSONObject();
    }
    
    // -------------------- SUB-CLASES
    
    public class CustJaxbUnMarshaller extends Jaxb2Marshaller {
  	  
	    @Override
	    public Object unmarshal(Source source) throws XmlMappingException {
	        return super.unmarshal(source, null);
	    }
	    @Override
	    public Object unmarshal(Source source, MimeContainer mimeContainer)
	            throws XmlMappingException {
	        Object mimeMessage = new DirectFieldAccessor(mimeContainer)
	                .getPropertyValue("mimeMessage");
	        Object unmarshalObject = null;
	        if (mimeMessage instanceof SaajSoapMessage) {
	            SaajSoapMessage soapMessage = (SaajSoapMessage) mimeMessage;
	            String faultReason = soapMessage.getFaultReason();
	            if (faultReason != null) {
	                throw convertJaxbException(new JAXBException(faultReason));
	            } else {
	                unmarshalObject = super.unmarshal(source, mimeContainer);
	            }
	        }
	        return unmarshalObject;
	    }
	}
}