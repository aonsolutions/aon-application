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
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;

import net.aonsolutions.aeat.sii.BajaLRBienesInversion;
import net.aonsolutions.aeat.sii.BajaLRDetOperacionIntracomunitaria;
import net.aonsolutions.aeat.sii.BajaLRFacturasEmitidas;
import net.aonsolutions.aeat.sii.BajaLRFacturasRecibidas;
import net.aonsolutions.aeat.sii.EstadoRegistroType;
import net.aonsolutions.aeat.sii.RespuestaExpedidaType;
import net.aonsolutions.aeat.sii.RespuestaLRAgenciasViajesType;
import net.aonsolutions.aeat.sii.RespuestaLRBajaBienesInversionType;
import net.aonsolutions.aeat.sii.RespuestaLRBajaFEmitidasType;
import net.aonsolutions.aeat.sii.RespuestaLRBajaFRecibidasType;
import net.aonsolutions.aeat.sii.RespuestaLRBajaOComunitariasType;
import net.aonsolutions.aeat.sii.RespuestaLRBienesInversionType;
import net.aonsolutions.aeat.sii.RespuestaLRCobrosEmitidasType;
import net.aonsolutions.aeat.sii.RespuestaLRFEmitidasType;
import net.aonsolutions.aeat.sii.RespuestaLRFRecibidasType;
import net.aonsolutions.aeat.sii.RespuestaLRIMetalicoType;
import net.aonsolutions.aeat.sii.RespuestaLROComunitariasType;
import net.aonsolutions.aeat.sii.RespuestaLROperacionesSegurosType;
import net.aonsolutions.aeat.sii.RespuestaLRPagosRecibidasType;
import net.aonsolutions.aeat.sii.RespuestaRecibidaType;
import net.aonsolutions.aeat.sii.SuministroLRAgenciasViajes;
import net.aonsolutions.aeat.sii.SuministroLRBienesInversion;
import net.aonsolutions.aeat.sii.SuministroLRCobrosEmitidas;
import net.aonsolutions.aeat.sii.SuministroLRCobrosMetalico;
import net.aonsolutions.aeat.sii.SuministroLRDetOperacionIntracomunitaria;
import net.aonsolutions.aeat.sii.SuministroLRFacturasEmitidas;
import net.aonsolutions.aeat.sii.SuministroLRFacturasRecibidas;
import net.aonsolutions.aeat.sii.SuministroLROperacionesSeguros;
import net.aonsolutions.aeat.sii.SuministroLRPagosRecibidas;

public class SIIPost extends WebServiceGatewaySupport{
	
	private Boolean pruebas = true;
	
	public static SIIPost getInstance(byte[] cert, String pass) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		return new SIIPost(cert, pass);
	}
	byte[] cert;
	String pass;
	
	public SIIPost(byte[] cert, String pass) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		this.cert = cert;
		this.pass = pass;
		ByteArrayInputStream key = new ByteArrayInputStream(cert);
	    KeyStore keyStore = KeyStore.getInstance("PKCS12");

	    keyStore.load(key, pass.toCharArray());
    	
    	KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   		kmf.init(keyStore, pass.toCharArray());

   		HttpsUrlConnectionMessageSender messageSender = new HttpsUrlConnectionMessageSender();
    	messageSender.setKeyManagers(kmf.getKeyManagers());

    	setMessageSender(messageSender);
    	
    	CustJaxbUnMarshaller marshaller = new CustJaxbUnMarshaller();
        marshaller.setContextPath("net.aonsolutions.aeat.sii");
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
	}

	private Object post(String uri, Object object) {
		return getWebServiceTemplate().marshalSendAndReceive(uri, object);
	}
	
	private JSONObject json(Integer id, String name, String referenceCode){
		JSONObject json = new JSONObject();
    	json.put("id", id);
    	json.put("name",  referenceCode + " - " + name);
    	return json;
	}
	
    // -------------------- FACTURAS EMITIDAS
	
	@SuppressWarnings("unchecked")
    protected JSONArray suministroFacturasEmitidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS);
    	
    	LinkedList<VatContext> modList = contextList.stream().filter(v->  "Correcto".equals(v.getSiiStatus())
    			|| "AceptadoConErrores".equals(v.getSiiStatus())
    			|| "Anulada".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));
    	
    	LinkedList<VatContext> newList = contextList.stream().filter(v-> "Pendiente".equals(v.getSiiStatus())
    			|| "Incorrecto".equals(v.getSiiStatus())).collect(Collectors.toCollection(LinkedList::new));
    	
    	    	
    	JSONArray array = new JSONArray();
		
    	if(modList.size() > 0){
    		SuministroLRFacturasEmitidas suministroMod = SIIBuilt.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceList, modList, cert, pass, true, terceros);     	

    		JAXBElement<RespuestaLRFEmitidasType> response = (JAXBElement<RespuestaLRFEmitidasType>) post(uri, suministroMod);
    		RespuestaLRFEmitidasType respuesta = response.getValue();
    		HashMap<Integer, SIIDataVariable> map = new HashMap<>();
    		Integer index = 0;
    	
    		for (RespuestaExpedidaType r : respuesta.getRespuestaLinea()) {
    			Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    			Boolean aceptadoConErrores = r.getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);
    			map.put(invoiceList.get(index), correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_OK : SIIDataVariable.INVOICE_SUMINISTRO_ERROR);
    			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    							r.getIDFactura().getNumSerieFacturaEmisor()));
    			index++;
    		}

    		byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasEmitidas(suministroMod);
    		byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);
       
    		LinkedList<String> status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status);
    	}
    	if(newList.size() > 0){
    		SuministroLRFacturasEmitidas suministroNew = SIIBuilt.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceList, newList, cert, pass, false, terceros);     	

    		JAXBElement<RespuestaLRFEmitidasType> response = (JAXBElement<RespuestaLRFEmitidasType>) post(uri, suministroNew);
    		RespuestaLRFEmitidasType respuesta = response.getValue();
    		HashMap<Integer, SIIDataVariable> map = new HashMap<>();
    		Integer index = 0;
    	
    		for (RespuestaExpedidaType r : respuesta.getRespuestaLinea()) {
    			Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    			Boolean aceptadoConErrores = r.getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);
    			map.put(invoiceList.get(index), correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_OK : SIIDataVariable.INVOICE_SUMINISTRO_ERROR);
    			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
    					correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
    							r.getIDFactura().getNumSerieFacturaEmisor()));
    			index++;
    		}

    		byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasEmitidas(suministroNew);
    		byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);
       
    		LinkedList<String> status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status);
    	}
        return array;
	}
	
	@SuppressWarnings("unchecked")
    protected JSONArray bajaFacturasEmitidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS);
    	BajaLRFacturasEmitidas suministro = SIIBuilt.getInstance().bajaFacturasEmitidas(company, invoiceList, contextList);     	

    	JAXBElement<RespuestaLRBajaFEmitidasType> response = (JAXBElement<RespuestaLRBajaFEmitidasType>) post(uri, suministro);
    	RespuestaLRBajaFEmitidasType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	Boolean aceptadoErrores = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);
    	byte[] requestXml = SIIBuilt.getInstance().getBajaFacturasEmitidas(suministro);
   		byte[] responseXml = SIIBuilt.getInstance().getRespuestaBajaFacturasEmitidas(respuesta);
   		
    	//LinkedList<String> status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
   		//SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status);

    	JSONArray array = new JSONArray();
    	array.put(json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    			correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor()));
    	return array;
	}

    // -------------------- FACTURAS EMITIDAS COBROS
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Invoice> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS_COBROS) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS_COBROS);
    	SuministroLRCobrosEmitidas suministro = SIIBuilt.getInstance().suministroFacturasEmitidasCobros(domain, login, company, invoiceList);     	
    
    	JAXBElement<RespuestaLRCobrosEmitidasType> response = (JAXBElement<RespuestaLRCobrosEmitidasType>) post(uri, suministro);
    	RespuestaLRCobrosEmitidasType respuesta = response.getValue();
   
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	Boolean aceptadoConErrores = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasEmitidasCobros(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasEmitidasCobros(respuesta);
    
    	//LinkedList<String> status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    	//SIIDB.getInstance().insertSuministro(domain, login, invoiceList.stream().map(a -> a.getId()).collect(Collectors.toCollection(LinkedList::new)) , requestXml, responseXml, status);
   		//TODO  parcial ¿?
    	
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor());
    }
    
    // -------------------- FACTURAS RECIBIDAS
	
    @SuppressWarnings("unchecked")
	protected JSONArray suministroFacturasRecibidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_RECIBIDAS) : SIIUri.getInstance().getURI(SIIType.FACTURAS_RECIBIDAS);
    	LinkedList<VatContext> modList = contextList.stream().filter(v-> v.getSiiStatus().equals("Correcto")
    			|| v.getSiiStatus().equals("AceptadoConErrores")
    			|| v.getSiiStatus().equals("Anulada")).collect(Collectors.toCollection(LinkedList::new));
    	
    	LinkedList<VatContext> newList = contextList.stream().filter(v-> v.getSiiStatus().equals("Pendiente")
    			|| v.getSiiStatus().equals("Incorrecto")).collect(Collectors.toCollection(LinkedList::new));   	
  
    	JSONArray array = new JSONArray();
    	
    	if(modList.size() > 0){
        	SuministroLRFacturasRecibidas suministroMod = SIIBuilt.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceList, modList, cert, pass, true, terceros);
    		JAXBElement<RespuestaLRFRecibidasType> response = (JAXBElement<RespuestaLRFRecibidasType>) post(uri, suministroMod);
    		RespuestaLRFRecibidasType respuesta = response.getValue();	
    
    		HashMap<Integer, SIIDataVariable> map = new HashMap<>();
    		Integer index = 0;
    	
    	
    		for (RespuestaRecibidaType r : respuesta.getRespuestaLinea()) {
    			Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    			Boolean aceptadoConErrores = r.getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);
    			map.put(invoiceList.get(index), correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_OK : SIIDataVariable.INVOICE_SUMINISTRO_ERROR);
    			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
        			correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
        					r.getIDFactura().getNumSerieFacturaEmisor()));
    			index++;
    		}
    		
    		byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasRecibidas(suministroMod);
    		byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
    		LinkedList<String> status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status);
    	}
    	
    	if(newList.size()>0){
    	  	SuministroLRFacturasRecibidas suministroNew = SIIBuilt.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceList, newList, cert, pass, false, terceros);     	
    	    
    		JAXBElement<RespuestaLRFRecibidasType> response = (JAXBElement<RespuestaLRFRecibidasType>) post(uri, suministroNew);
    		RespuestaLRFRecibidasType respuesta = response.getValue();	
    
    		HashMap<Integer, SIIDataVariable> map = new HashMap<>();
    		Integer index = 0;
    	
    	
    		for (RespuestaRecibidaType r : respuesta.getRespuestaLinea()) {
    			Boolean correcto = r.getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    			Boolean aceptadoConErrores = r.getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);
    			map.put(invoiceList.get(index), correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_OK : SIIDataVariable.INVOICE_SUMINISTRO_ERROR);
    			array.put(json(correcto ? 200 : r.getCodigoErrorRegistro().intValue(), 
        			correcto ? "Envio realizado correctamente" : r.getDescripcionErrorRegistro(),
        					r.getIDFactura().getNumSerieFacturaEmisor()));
    			index++;
    		}
    		
    		byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasRecibidas(suministroNew);
    		byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
    		LinkedList<String> status = respuesta.getRespuestaLinea().stream().map(m -> m.getEstadoRegistro().value()).collect(Collectors.toCollection(LinkedList::new));
    		SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, status);
    	}
        return array;
    }
    
    @SuppressWarnings("unchecked")
   	protected JSONArray bajaFacturasRecibidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_RECIBIDAS) : SIIUri.getInstance().getURI(SIIType.FACTURAS_RECIBIDAS);
    	BajaLRFacturasRecibidas suministro = SIIBuilt.getInstance().bajaFacturasRecibidas(company, invoiceList, contextList);     	

    	JAXBElement<RespuestaLRBajaFRecibidasType> response = (JAXBElement<RespuestaLRBajaFRecibidasType>) post(uri, suministro);
    	RespuestaLRBajaFRecibidasType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
     	Boolean aceptadoErrores = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);
         
    //	byte[] requestXml = SIIBuilt.getInstance().getBajaFacturasRecibidas(suministro);
    //	byte[] responseXml = SIIBuilt.getInstance().getRespuestaBajaFacturasRecibidas(respuesta);

    //	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoErrores ? SIIDataVariable.INVOICE_BAJA_OK: SIIDataVariable.INVOICE_BAJA_ERROR);

    	JSONArray array = new JSONArray();
    	array.put(json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    			correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor()));
    	return array;
    }
    
    // -------------------- FACTURAS RECIBIDAS PAGOS
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroFacturasRecibidasPagos(Domain domain, String login, Company company, LinkedList<Invoice> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_RECIBIDAS_PAGOS) : SIIUri.getInstance().getURI(SIIType.FACTURAS_RECIBIDAS_PAGOS);
    	SuministroLRPagosRecibidas suministro = SIIBuilt.getInstance().suministroFacturasRecibidasPagos(domain, login, company, invoiceList);     	
    
    	JAXBElement<RespuestaLRPagosRecibidasType> response = (JAXBElement<RespuestaLRPagosRecibidasType>) post(uri, suministro);
    	RespuestaLRPagosRecibidasType respuesta = response.getValue();
    	
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	Boolean aceptadoConErrores = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);

    	byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasRecibidasPagos(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasRecibidasPagos(respuesta);

    	//LinkedList<Integer> il = invoiceList.stream().map(m -> m.getId()).collect(Collectors.toCollection(LinkedList::new));
    	//SIIDB.getInstance().insertSuministro(domain, login, il, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_COBROS_PAGOS_OK : SIIDataVariable.INVOICE_COBROS_PAGOS_ERROR);
    	// TODO partial ¿?
    	
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    			correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor());
    }

    // -------------------- BIENES INVERSION
	
    @SuppressWarnings("unchecked")
    protected JSONArray suministroBienesInversion(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.BIENES_INVERSION) : SIIUri.getInstance().getURI(SIIType.BIENES_INVERSION);
    	SuministroLRBienesInversion suministro = SIIBuilt.getInstance().suministroBienesInversion(domain, login, company, invoiceList, contextList);     	
    	
    	JAXBElement<RespuestaLRBienesInversionType> response = (JAXBElement<RespuestaLRBienesInversionType>) post(uri, suministro);
    	RespuestaLRBienesInversionType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	Boolean aceptadoConErrores = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);

    	byte[] requestXml = SIIBuilt.getInstance().getSuministroBienesInversion(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroBienesInversion(respuesta);
    //	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_BIENES_INVERSION_OK : SIIDataVariable.INVOICE_SUMINISTRO_BIENES_INVERSION_ERROR);
    	
    	JSONArray array = new JSONArray();
    	array.put(json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor()));
    	return array;
    }
    
    @SuppressWarnings("unchecked")
    protected JSONArray bajaBienesInversion(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.BIENES_INVERSION) : SIIUri.getInstance().getURI(SIIType.BIENES_INVERSION);
    	BajaLRBienesInversion suministro = SIIBuilt.getInstance().bajaBienesInversion(company, invoiceList, contextList);     	

    	JAXBElement<RespuestaLRBajaBienesInversionType> response = (JAXBElement<RespuestaLRBajaBienesInversionType>) post(uri, suministro);
    	RespuestaLRBajaBienesInversionType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	Boolean aceptadoErrores = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);
    
    	byte[] requestXml = SIIBuilt.getInstance().getBajaBienesInversion(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaBajaBienesInversion(respuesta);
    //	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoErrores ? SIIDataVariable.INVOICE_BAJA_BIENES_OK: SIIDataVariable.INVOICE_BAJA_BIENES_ERROR);

    	JSONArray array = new JSONArray();
    	array.put(json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    			correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor()));
    	return array;
    }
    
    // -------------------- OPERACIONES INTRACOMUNITARIAS
	
    @SuppressWarnings("unchecked")
    protected JSONArray suministroOperacionesIntracomunitarias(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String tipoOp, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.OPERACIONES_INTRACOMUNITARIAS) : SIIUri.getInstance().getURI(SIIType.OPERACIONES_INTRACOMUNITARIAS);
    	SuministroLRDetOperacionIntracomunitaria suministro = SIIBuilt.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceList, contextList, tipoOp);     	
    	
    	JAXBElement<RespuestaLROComunitariasType> response = (JAXBElement<RespuestaLROComunitariasType>) post(uri, suministro);
    	RespuestaLROComunitariasType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	Boolean aceptadoConErrores = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);

    	byte[] requestXml = SIIBuilt.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroOperacionesIntracomunitarias(respuesta);
    //	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_OPERACIONES_INTRACOMUNITARIAS_OK : SIIDataVariable.INVOICE_SUMINISTRO_OPERACIONES_INTRACOMUNITARIAS_ERROR);
    	
    	JSONArray array = new JSONArray();
    	array.put(json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor()));
    	return array;
    }
    
    @SuppressWarnings("unchecked")
    protected JSONArray bajaOperacionesIntracomunitarias(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.OPERACIONES_INTRACOMUNITARIAS) : SIIUri.getInstance().getURI(SIIType.OPERACIONES_INTRACOMUNITARIAS);
    	BajaLRDetOperacionIntracomunitaria suministro = SIIBuilt.getInstance().bajaOperacionesIntracomunitarias(company, invoiceList, contextList);     	

    	JAXBElement<RespuestaLRBajaOComunitariasType> response = (JAXBElement<RespuestaLRBajaOComunitariasType>) post(uri, suministro);
    	RespuestaLRBajaOComunitariasType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	Boolean aceptadoErrores = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);
    
    	byte[] requestXml = SIIBuilt.getInstance().getBajaOperacionesIntracomunitarias(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaBajaOperacionesIntracomunitarias(respuesta);
    //	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoErrores ? SIIDataVariable.INVOICE_BAJA_OPERACIONES_INTRACOMUNITARIAS_OK: SIIDataVariable.INVOICE_BAJA_OPERACIONES_INTRACOMUNITARIAS_ERROR);

    	JSONArray array = new JSONArray();
    	array.put(json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    			correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor()));
    	return array;
    }
    
    
    // -------------------- COBROS METALICO
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroCobrosMetalico(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.COBROS_METALICO) : SIIUri.getInstance().getURI(SIIType.COBROS_METALICO);
    	SuministroLRCobrosMetalico suministro = SIIBuilt.getInstance().suministroCobrosMetalico(domain, login, company, invoiceList, contextList);     	
    	
    	JAXBElement<RespuestaLRIMetalicoType> response = (JAXBElement<RespuestaLRIMetalicoType>) post(uri, suministro);
    	RespuestaLRIMetalicoType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	Boolean aceptadoConErrores = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);
  
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroCobrosMetalico(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroCobrosMetalico(respuesta);
   // 	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_METALICO_OK : SIIDataVariable.INVOICE_SUMINISTRO_METALICO_ERROR);
    	
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    }
    
    @SuppressWarnings("unchecked")
    protected JSONObject bajaCobrosMetalico(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	//RespuestaLRBajaIMetalicoType
    	return new JSONObject();
    }
    
    // -------------------- OPERACIONES SEGUROS
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroOperacionesSeguros(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.OPERACIONES_SEGUROS) : SIIUri.getInstance().getURI(SIIType.OPERACIONES_SEGUROS);
    	SuministroLROperacionesSeguros suministro = SIIBuilt.getInstance().suministroOperacionesSeguros(domain, login, company, invoiceList, contextList);     	
    	
    	JAXBElement<RespuestaLROperacionesSegurosType> response = (JAXBElement<RespuestaLROperacionesSegurosType>) post(uri, suministro);
    	RespuestaLROperacionesSegurosType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	Boolean aceptadoConErrores = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);
    	
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroOperacionesSeguros(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroOperacionesSeguros(respuesta);
    //	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_SEGUROS_OK: SIIDataVariable.INVOICE_SUMINISTRO_SEGUROS_ERROR);
    	
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    }
    
    @SuppressWarnings("unchecked")
    protected JSONObject bajaOperacionesSeguros(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	//RespuestaLRBajaOperacionesSegurosType
    	return new JSONObject();
    }
    
    // -------------------- AGENCIAS VIAJES
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroAgenciasViajes(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList, String terceros) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.AGENCIAS_VIAJES) : SIIUri.getInstance().getURI(SIIType.AGENCIAS_VIAJES);
    	SuministroLRAgenciasViajes suministro = SIIBuilt.getInstance().suministroAgenciasViajes(domain, login, company, invoiceList, contextList);     	
    	
    	JAXBElement<RespuestaLRAgenciasViajesType> response = (JAXBElement<RespuestaLRAgenciasViajesType>) post(uri, suministro);
    	RespuestaLRAgenciasViajesType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	Boolean aceptadoConErrores = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.ACEPTADO_CON_ERRORES);
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroAgenciasViajes(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroAgenciasViajes(respuesta);
  //  	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml, correcto || aceptadoConErrores ? SIIDataVariable.INVOICE_SUMINISTRO_AGENCIAS_OK : SIIDataVariable.INVOICE_SUMINISTRO_AGENCIAS_ERROR);
   
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    }
    
    @SuppressWarnings("unchecked")
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