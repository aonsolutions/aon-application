package net.aonsolutions.aon.verifactu;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.VerifactuConfiguration;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.EstadoRegistroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaRegFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import net.aonsolutions.aon.verifactu.utils.XMLUtils;

public class VERIFACTU {

	public static VERIFACTU getInstance() {
		return new VERIFACTU();
	}
	
	public void accept(VerifactuConfiguration verifactuConfiguration, Company company, List<Invoice> invoices, VerifactuBlockchain blockchain) throws Exception {
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(verifactuConfiguration, company, invoices, blockchain);
		String requestStr = XMLUtils.soapMarshal(request, RegFactuSistemaFacturacion.class);
		VerifactuResponse response = XMLUtils.post(verifactuConfiguration.getCertificate(), VerifactuUri.getUrlEmision(true), requestStr);

		save(company, invoices, request, response);
	}

	protected void save(Company company, List<Invoice> invoices, RegFactuSistemaFacturacion request, VerifactuResponse response) throws ParserConfigurationException, SAXException, IOException, JAXBException {
		byte[] requestData = XMLUtils.marshal(request, RegFactuSistemaFacturacion.class);
		byte[] responseData = response.getResponse().getBytes();
		// save DATA REQUEST
		DataRequest dataRequest = saveRequest(company.getDomain(), requestData);
		// save DATA RESPONSE
		DataResponse dataResponse = saveResponse(company.getDomain(), dataRequest, responseData);
		
		User user = new User().setLogin("");
		request.getRegistroFactura().stream().forEach(req -> {
			Integer invoiceId = AonNumberUtils.toInteger(req.getRegistroAlta().getRefExterna());
			
//			VerifactuBlockchain invoiceBlockchain = new VerifactuBlockchain()
//					.setDate(req.getRegistroAlta().getIDFactura().getFechaExpedicionFactura())
//					.setDocument(req.getRegistroAlta().getIDFactura().getIDEmisorFactura())	
//					.setHuella(req.getRegistroAlta().getHuella())
//					.setReference(req.getRegistroAlta().getIDFactura().getNumSerieFactura());

			InvoiceData invoiceData = new InvoiceData()
					.setDomain(company.getDomain().getId())
					.setInvoice(invoiceId)
					.setName("VERIFACTU_HUELLA")
					.setValue(req.getRegistroAlta().getHuella());
			AON.saveInvoiceData(company.getDomain(), user, invoiceData);
			
			InvoiceData invoiceData2 = new InvoiceData()
					.setDomain(company.getDomain().getId())
					.setInvoice(invoiceId)
					.setName("VERIFACTU_QR")
					.setValue(getQrUrl(req.getRegistroAlta()));
			AON.saveInvoiceData(company.getDomain(), user, invoiceData2);
		});
		
		RegistroFacturacionAltaType last = request.getRegistroFactura().getLast().getRegistroAlta();
		VerifactuBlockchain blockchain = new VerifactuBlockchain()
				.setDate(last.getIDFactura().getFechaExpedicionFactura())
				.setDocument(last.getIDFactura().getIDEmisorFactura())
				.setHuella(last.getHuella())
				.setReference(last.getIDFactura().getNumSerieFactura());
		saveVerifactuBlockchain(company.getDomain(), user, blockchain);
		
		InvoiceBatch invoiceBatch = saveInvoiceBatch(company.getDomain(), user, dataResponse);
		if(response.isError()) {
			invoices.stream().forEach(r -> {
				saveInvoiceInfo(company.getDomain(), user, r.getId(), InvoiceCommunicationStatus.WRONG);
				saveInvoiceBatchdetail(company.getDomain(), user, invoiceBatch, r.getId(), InvoiceCommunicationStatus.WRONG);					
			});
		} else {
			RespuestaRegFactuSistemaFacturacionType respuesta = (RespuestaRegFactuSistemaFacturacionType) XMLUtils.soapUnmarshal(RespuestaRegFactuSistemaFacturacionType.class, response.getResponse());
			if(!respuesta.getRespuestaLinea().isEmpty()) {
				respuesta.getRespuestaLinea().stream().forEach(r -> {
					Integer invoiceId = AonNumberUtils.toInteger(r.getRefExterna());
					InvoiceCommunicationStatus status = getInvoiceCommunicationStatus(r.getEstadoRegistro());
					saveInvoiceInfo(company.getDomain(), user, invoiceId, status); 
					saveInvoiceBatchdetail(company.getDomain(),user, invoiceBatch, invoiceId, status);				
				});
			}
		}		
	}
	
	private InvoiceCommunicationStatus getInvoiceCommunicationStatus(EstadoRegistroType status) {
		if(status == null) return null;
		if(EstadoRegistroType.CORRECTO.equals(status)) return InvoiceCommunicationStatus.ACCEPTED;
		else if(EstadoRegistroType.ACEPTADO_CON_ERRORES.equals(status)) return InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS;
		else if(EstadoRegistroType.INCORRECTO.equals(status)) return InvoiceCommunicationStatus.WRONG;
		return null;
	}
	
	private String getQrUrl(RegistroFacturacionAltaType alta) {
		return new StringBuilder(VerifactuUri.getUrlQr())
				.append("?")
				.append("nif=").append(encodeParam(alta.getIDFactura().getIDEmisorFactura()))
				.append("numserie=").append(encodeParam(alta.getIDFactura().getNumSerieFactura()))
				.append("fecha=").append(encodeParam(alta.getIDFactura().getFechaExpedicionFactura()))
				.append("importe=").append(encodeParam(alta.getImporteTotal()))
				.toString();
	}
	
	private String encodeParam(String param) {
		try {
			return URLEncoder.encode(param, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return param;
		}
	}
	private DataRequest saveRequest(Domain domain, byte[] request) {
		DataRequest dataRequest = new DataRequest()
				.setDomain(domain.getId())
				.setDate(new Date())
				.setBlackBox("")
				.setType(DataRequestType.VERIFACTU);
		
		dataRequest = AON.saveDataRequest(domain.getName(), domain.getId(), "", dataRequest);
		
		Attach attach = new Attach()
				.setDomain(domain)
				.setAttachType(AttachType.DATA)
				.setType(DataAttachType.REQUEST.value())
				.setSource(DataAttachSource.VERIFACTU.value())
				.setSourceId(dataRequest.getId())
				.setMimeType(MimeType.XML)
				.setData(request);
		
		AON.insertAttach(domain.getName(), domain.getId(), "", attach);
		
		return dataRequest;		
	}
	
	private DataResponse saveResponse(Domain domain, DataRequest dataRequest, byte[] response) {
		DataResponse dataResponse = new DataResponse()
				.setDomain(dataRequest.getDomain())
				.setDataRequest(dataRequest.getId())
				.setCode("")
				.setSource(DataResponseSource.VERIFACTU);
		
		dataResponse = AON.insertDataResponse(domain.getName(), domain.getId(), "", dataResponse);
		
		Attach attach = new Attach()
				.setDomain(domain)
				.setAttachType(AttachType.DATA)
				.setType(DataAttachType.RESPONSE_OK.value())
				.setSource(DataAttachSource.VERIFACTU.value())
				.setSourceId(dataResponse.getId())
				.setMimeType(MimeType.XML)
				.setData(response);
		
		AON.insertAttach(domain.getName(), domain.getId(), "", attach);
		
		return dataResponse;		
	}
	
	private static InvoiceInfo saveInvoiceInfo(Domain domain, User user, Integer invoice, InvoiceCommunicationStatus status) {
		InvoiceInfo info = new InvoiceInfo()
				.setDomain(domain.getId())
				.setInvoice(invoice)
				.setType(InvoiceCommunicationType.VERIFACTU)
				.setStatus(status);
		
		return AON.saveInvoiceInfo(domain, user, info);
	}
	
	private static InvoiceBatch saveInvoiceBatch(Domain domain, User user, DataResponse dataResponse) {
		InvoiceBatch invoiceBatch = new InvoiceBatch()
				.setDomain(domain.getId())
				.setType(InvoiceCommunicationType.VERIFACTU)
				.setDate(new Date())
				.setOperation(InvoiceCommunicationOperation.REGISTER)
				.setDataResponse(dataResponse.getId());
		return AON.saveInvoiceBatch(domain, user, invoiceBatch);
	}
	
	private static InvoiceBatchDetail saveInvoiceBatchdetail(Domain domain, User user, InvoiceBatch invoiceBatch, Integer invoice, InvoiceCommunicationStatus status) {
		InvoiceBatchDetail ibd = new InvoiceBatchDetail()
				.setDomain(invoiceBatch.getDomain())
				.setInvoiceBatch(invoiceBatch.getId())
				.setInvoice(invoice)
				.setStatus(status);
		
		return AON.saveInvoiceBatchDetail(domain, user, ibd);
	}
	
	private static void saveVerifactuBlockchain(Domain domain, User user, VerifactuBlockchain blockchain) {
		Occam occam = new Occam().setDomain(domain.getId()).setDomainName(domain.getName()).setUser(user.getLogin());
		AON.saveApplicationParameter(occam, new ApplicationParameter().setDomain(domain.getId()).setName(AppParam.VERIFACTU_BLOCKCHAIN_DOCUMENT.name()).setValue(blockchain.getDocument()));
		AON.saveApplicationParameter(occam, new ApplicationParameter().setDomain(domain.getId()).setName(AppParam.VERIFACTU_BLOCKCHAIN_REFERENCE.name()).setValue(blockchain.getReference()));
		AON.saveApplicationParameter(occam, new ApplicationParameter().setDomain(domain.getId()).setName(AppParam.VERIFACTU_BLOCKCHAIN_DATE.name()).setValue(blockchain.getDate()));
		AON.saveApplicationParameter(occam, new ApplicationParameter().setDomain(domain.getId()).setName(AppParam.VERIFACTU_BLOCKCHAIN_HUELLA.name()).setValue(blockchain.getHuella()));
	}

}
