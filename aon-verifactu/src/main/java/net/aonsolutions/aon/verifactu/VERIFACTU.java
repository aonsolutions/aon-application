package net.aonsolutions.aon.verifactu;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.VerifactuConfiguration;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaRegFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import net.aonsolutions.aon.verifactu.utils.XMLUtils;

public class VERIFACTU {

	public static VERIFACTU getInstance() {
		return new VERIFACTU();
	}
	
	public void accept(VerifactuConfiguration verifactuConfiguration, Company company, List<Invoice> invoices, VerifactuBlockchain blockchain) throws Exception {
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(verifactuConfiguration, company, invoices, blockchain);
		String requestStr = XMLUtils.soapMarshal(request, RegFactuSistemaFacturacion.class);
		String responseStr = XMLUtils.post(verifactuConfiguration.getCertificate(), VerifactuUri.getUrlEmision(), requestStr);
		RespuestaRegFactuSistemaFacturacionType response = (RespuestaRegFactuSistemaFacturacionType) XMLUtils.soapUnmarshal(RespuestaRegFactuSistemaFacturacionType.class, responseStr);
		save(company, request, response);
	}

	protected void save(Company company, RegFactuSistemaFacturacion request, RespuestaRegFactuSistemaFacturacionType response) throws ParserConfigurationException, SAXException, IOException, JAXBException {
		byte[] requestData = XMLUtils.marshal(request, RegFactuSistemaFacturacion.class);
		byte[] responseData = XMLUtils.marshal(request, RespuestaRegFactuSistemaFacturacionType.class);
		// save DATA REQUEST
		DataRequest dataRequest = saveRequest(company.getDomain(), requestData);
		// save DATA RESPONSE
		DataResponse dataResponse = saveResponse(company.getDomain(), dataRequest, responseData);
		
		request.getRegistroFactura().stream().forEach(req -> {
			Integer invoiceId = AonNumberUtils.toInteger(req.getRegistroAlta().getRefExterna());
			// TODO save INVOICE_DATA (HUELLA)
		});
		
		String lastHuella = request.getRegistroFactura().getLast().getRegistroAlta().getHuella();
		// TODO save la huella en app_param last_huella o algo asi..	
		
		// TODO save INVOICE BATCH
//		InvoiceBatch invoiceBatch = saveInvoiceBatch(company.getDomain(), dataResponse);
		if(!response.getRespuestaLinea().isEmpty()) {
			RespuestaExpedidaType r = response.getRespuestaLinea().get(0);
			Integer invoiceId = AonNumberUtils.toInteger(r.getRefExterna());
			// TODO save InvoiceInfo
			saveInvoiceInfo(null, invoiceId);
			// TODO save InvoiceBatchDetail
//			saveInvoiceBatchDetail(invoiceBatch, invoiceId);
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
				.setSource(DataResponseSource.VERIFACTU)
				
				;
		
		// TODO save dataresponse
		
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
	
	private static InvoiceInfo saveInvoiceInfo(Domain domain, Integer invoice) {
		return new InvoiceInfo();
	}
	
	private static InvoiceBatch saveInvoiceBatch(Domain domain, Integer invoice) {
		return new InvoiceBatch();
	}
	
	
	private static InvoiceBatchDetail saveInvoiceBAtchdetail(Domain domain, Integer invoice) {
		InvoiceBatchDetail ibd = new InvoiceBatchDetail();
		return ibd;		
	}

	
}
