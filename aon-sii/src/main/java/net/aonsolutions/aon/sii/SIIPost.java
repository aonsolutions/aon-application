package net.aonsolutions.aon.sii;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.LinkedList;

import javax.net.ssl.KeyManagerFactory;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;

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
import com.esferalia.aon.occam.api.model.fiscal.VatContext;

import net.aonsolutions.aeat.sii.EstadoRegistroType;
import net.aonsolutions.aeat.sii.RespuestaLRAgenciasViajesType;
import net.aonsolutions.aeat.sii.RespuestaLRBienesInversionType;
import net.aonsolutions.aeat.sii.RespuestaLRCobrosEmitidasType;
import net.aonsolutions.aeat.sii.RespuestaLRFEmitidasType;
import net.aonsolutions.aeat.sii.RespuestaLRFRecibidasType;
import net.aonsolutions.aeat.sii.RespuestaLRIMetalicoType;
import net.aonsolutions.aeat.sii.RespuestaLROComunitariasType;
import net.aonsolutions.aeat.sii.RespuestaLROperacionesSegurosType;
import net.aonsolutions.aeat.sii.RespuestaLRPagosRecibidasType;
import net.aonsolutions.aeat.sii.BajaLRFacturasEmitidas;
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
    protected JSONObject suministroFacturasEmitidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {


		
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS);
    	SuministroLRFacturasEmitidas suministro = SIIBuilt.getInstance().suministroFacturasEmitidas(domain, login, company, invoiceList, contextList, cert, pass);     	

    	JAXBElement<RespuestaLRFEmitidasType> response = (JAXBElement<RespuestaLRFEmitidasType>) post(uri, suministro);
    	RespuestaLRFEmitidasType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasEmitidas(suministro);
        byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);
        SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);

        return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    			correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor());
	}
	
	@SuppressWarnings("unchecked")
    protected JSONObject bajaFacturasEmitidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
		String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS);
    	BajaLRFacturasEmitidas suministro = SIIBuilt.getInstance().bajaFacturasEmitidas(company, invoiceList, contextList);     	

    	JAXBElement<RespuestaLRFEmitidasType> response = (JAXBElement<RespuestaLRFEmitidasType>) post(uri, suministro);
    	RespuestaLRFEmitidasType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    
    //	byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasEmitidas(suministro);
    //    byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasEmitidas(respuesta);
    //   SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);

        return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    			correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor());
	}
   
    // -------------------- FACTURAS EMITIDAS COBROS
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_EMITIDAS_COBROS) : SIIUri.getInstance().getURI(SIIType.FACTURAS_EMITIDAS_COBROS);
    	SuministroLRCobrosEmitidas suministro = SIIBuilt.getInstance().suministroFacturasEmitidasCobros(company);     	
    
    	JAXBElement<RespuestaLRCobrosEmitidasType> response = (JAXBElement<RespuestaLRCobrosEmitidasType>) post(uri, suministro);
    	RespuestaLRCobrosEmitidasType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    	
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasEmitidasCobros(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasEmitidasCobros(respuesta);
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    	
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor());
    }
    
    @SuppressWarnings("unchecked")
    protected JSONObject bajaFacturasEmitidasCobros(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	return new JSONObject();
    }
    
    // -------------------- FACTURAS RECIBIDAS
	
    @SuppressWarnings("unchecked")
	protected JSONObject suministroFacturasRecibidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_RECIBIDAS) : SIIUri.getInstance().getURI(SIIType.FACTURAS_RECIBIDAS);
    	SuministroLRFacturasRecibidas suministro = SIIBuilt.getInstance().suministroFacturasRecibidas(domain, login, company, invoiceList, contextList, cert, pass);       	
   
    	JAXBElement<RespuestaLRFRecibidasType> response = (JAXBElement<RespuestaLRFRecibidasType>) post(uri, suministro);
    	RespuestaLRFRecibidasType respuesta = response.getValue();	
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
    
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasRecibidas(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasRecibidas(respuesta);
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    	    	respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor());
    }
    
    @SuppressWarnings("unchecked")
   	protected JSONObject bajaFacturasRecibidas(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	return new JSONObject();
    }
    
    // -------------------- FACTURAS RECIBIDAS PAGOS
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroFacturasRecibidasPagos(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.FACTURAS_RECIBIDAS_PAGOS) : SIIUri.getInstance().getURI(SIIType.FACTURAS_RECIBIDAS_PAGOS);
    	SuministroLRPagosRecibidas suministro = SIIBuilt.getInstance().suministroFacturasRecibidasPagos(company);     	
    
    	JAXBElement<RespuestaLRPagosRecibidasType> response = (JAXBElement<RespuestaLRPagosRecibidasType>) post(uri, suministro);
    	RespuestaLRPagosRecibidasType respuesta = response.getValue();
    	
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);
   
    	byte[] requestXml = SIIBuilt.getInstance().getSuministroFacturasRecibidasPagos(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroFacturasRecibidasPagos(respuesta);
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    	
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    			correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor());
    }
    
    @SuppressWarnings("unchecked")
    protected JSONObject bajaFacturasRecibidasPagos(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	return new JSONObject();
    }
    
    // -------------------- BIENES INVERSION
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroBienesInversion(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.BIENES_INVERSION) : SIIUri.getInstance().getURI(SIIType.BIENES_INVERSION);
    	SuministroLRBienesInversion suministro = SIIBuilt.getInstance().suministroBienesInversion(domain, login, company, invoiceList, contextList);     	
    	
    	JAXBElement<RespuestaLRBienesInversionType> response = (JAXBElement<RespuestaLRBienesInversionType>) post(uri, suministro);
    	RespuestaLRBienesInversionType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);

    	byte[] requestXml = SIIBuilt.getInstance().getSuministroBienesInversion(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroBienesInversion(respuesta);
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    	
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor());
    }
    
    @SuppressWarnings("unchecked")
    protected JSONObject bajaBienesInversion(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	return new JSONObject();
    }
    
    // -------------------- OPERACIONES INTRACOMUNITARIAS
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroOperacionesIntracomunitarias(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.OPERACIONES_INTRACOMUNITARIAS) : SIIUri.getInstance().getURI(SIIType.OPERACIONES_INTRACOMUNITARIAS);
    	SuministroLRDetOperacionIntracomunitaria suministro = SIIBuilt.getInstance().suministroOperacionesIntracomunitarias(domain, login, company, invoiceList, contextList);     	
    	
    	JAXBElement<RespuestaLROComunitariasType> response = (JAXBElement<RespuestaLROComunitariasType>) post(uri, suministro);
    	RespuestaLROComunitariasType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);

    	byte[] requestXml = SIIBuilt.getInstance().getSuministroOperacionesIntracomunitarias(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroOperacionesIntracomunitarias(respuesta);
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			respuesta.getRespuestaLinea().get(0).getIDFactura().getNumSerieFacturaEmisor());
    }
    
    @SuppressWarnings("unchecked")
    protected JSONObject bajaOperacionesIntracomunitarias(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	return new JSONObject();
    }
    
    
    // -------------------- COBROS METALICO
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroCobrosMetalico(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.COBROS_METALICO) : SIIUri.getInstance().getURI(SIIType.COBROS_METALICO);
    	SuministroLRCobrosMetalico suministro = SIIBuilt.getInstance().suministroCobrosMetalico(domain, login, company, invoiceList, contextList);     	
    	
    	JAXBElement<RespuestaLRIMetalicoType> response = (JAXBElement<RespuestaLRIMetalicoType>) post(uri, suministro);
    	RespuestaLRIMetalicoType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);

    	byte[] requestXml = SIIBuilt.getInstance().getSuministroCobrosMetalico(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroCobrosMetalico(respuesta);
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    	
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    }
    
    @SuppressWarnings("unchecked")
    protected JSONObject bajaCobrosMetalico(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	return new JSONObject();
    }
    
    // -------------------- OPERACIONES SEGUROS
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroOperacionesSeguros(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.OPERACIONES_SEGUROS) : SIIUri.getInstance().getURI(SIIType.OPERACIONES_SEGUROS);
    	SuministroLROperacionesSeguros suministro = SIIBuilt.getInstance().suministroOperacionesSeguros(domain, login, company, invoiceList, contextList);     	
    	
    	JAXBElement<RespuestaLROperacionesSegurosType> response = (JAXBElement<RespuestaLROperacionesSegurosType>) post(uri, suministro);
    	RespuestaLROperacionesSegurosType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);

    	byte[] requestXml = SIIBuilt.getInstance().getSuministroOperacionesSeguros(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroOperacionesSeguros(respuesta);
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
    	
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    }
    
    @SuppressWarnings("unchecked")
    protected JSONObject bajaOperacionesSeguros(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	return new JSONObject();
    }
    
    // -------------------- AGENCIAS VIAJES
	
    @SuppressWarnings("unchecked")
    protected JSONObject suministroAgenciasViajes(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
    	String uri = pruebas ? SIIUri.getInstance().getURIPruebas(SIIType.AGENCIAS_VIAJES) : SIIUri.getInstance().getURI(SIIType.AGENCIAS_VIAJES);
    	SuministroLRAgenciasViajes suministro = SIIBuilt.getInstance().suministroAgenciasViajes(domain, login, company, invoiceList, contextList);     	
    	
    	JAXBElement<RespuestaLRAgenciasViajesType> response = (JAXBElement<RespuestaLRAgenciasViajesType>) post(uri, suministro);
    	RespuestaLRAgenciasViajesType respuesta = response.getValue();
    	Boolean correcto = respuesta.getRespuestaLinea().get(0).getEstadoRegistro().equals(EstadoRegistroType.CORRECTO);

    	byte[] requestXml = SIIBuilt.getInstance().getSuministroAgenciasViajes(suministro);
    	byte[] responseXml = SIIBuilt.getInstance().getRespuestaSuministroAgenciasViajes(respuesta);
    	SIIDB.getInstance().insertSuministro(domain, login, invoiceList, requestXml, responseXml);
   
    	return json(correcto ? 200 : respuesta.getRespuestaLinea().get(0).getCodigoErrorRegistro().intValue(), 
    	    	correcto ? "Envio realizado correctamente" : respuesta.getRespuestaLinea().get(0).getDescripcionErrorRegistro(),
    			"");
    }
    
    @SuppressWarnings("unchecked")
    protected JSONObject bajaAgenciasViajes(Domain domain, String login, Company company, LinkedList<Integer> invoiceList, LinkedList<VatContext> contextList) {
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