package net.aonsolutions.aon.tbai;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.sign.TbaiSigner;
import net.aonsolutions.aon.sign.exception.AonSignerException;
import net.aonsolutions.aon.tbai.sign.TbaiSign;
import net.aonsolutions.aon.tbai.utils.XMLUtils;
import ticketbai.anulacion.AnulaTicketBai;
import ticketbai.emision.TicketBai;
import ticketbai.respuesta.Salida;
import ticketbai.respuesta.TicketBaiResponse;
import ticketbai.zuzendu_alta.SubsanacionModificacionTicketBAI;

public class TBAI {
	
	private TBAI() {
		
	}
	
	public static void accept(AONContext ctx, InvoiceCommunicationConfiguration icc, Company company, Invoice invoice) throws Exception {
		TbaiMain tbai = new TbaiMain();
		tbai.createEmisionTBAI(ctx, company, invoice, icc);
	}
	
	public static void modify(InvoiceCommunicationConfiguration icc, Company company, Invoice invoice) throws Exception {
		byte[] data = generateModifyXMl(icc, company, invoice);
		byte[] xml = TbaiSigner.getInstance().sign(icc, data);
		String uri = TbaiUri.getUrlZuzendu(icc);
		byte[] response = XMLUtils.send(icc.getCertificate(), uri, xml);
		TicketBaiResponse tbaiResponse = (TicketBaiResponse) XMLUtils.unmarshal(response, TicketBaiResponse.class);
	}
	
	public static byte[] generateAcceptXMl(InvoiceCommunicationConfiguration icc, Company company, Invoice invoice, TbaiBlockchain blockchain) throws Exception{
		TicketBai tbai = Invoice2tbai.build(company, invoice, icc, blockchain);
		return XMLUtils.marshal(tbai, TicketBai.class);
	}
	
	public static byte[] generateModifyXMl(InvoiceCommunicationConfiguration icc, Company company, Invoice invoice) throws Exception{
		SubsanacionModificacionTicketBAI tbai = Invoice2tbai.buildZuzendu(company, invoice, icc, null, null, false);
		return XMLUtils.marshal(tbai, SubsanacionModificacionTicketBAI.class);
	}
	
	protected static void save(Company company, Invoice invoice, byte[] request, byte[] response, InvoiceCommunicationConfiguration icc) throws ParserConfigurationException, SAXException, IOException, JAXBException {
		DataRequest datRequest = saveRequest(company.getDomain(), request);
		String sign = TbaiSign.getSign(request);
		TicketBaiResponse tbaiResponse = (TicketBaiResponse) XMLUtils.unmarshal(response, TicketBaiResponse.class);
		TbaiBlockchain bc = new TbaiBlockchain().setDate(AonDateUtils.format(new Date(), "dd-MM-yyyy"))
			.setNumber(Integer.toString(invoice.getNumber())).setSerie(invoice.getSeries())
			.setSignature(sign.substring(0, 100));
	}
	
	private static DataRequest saveRequest(Domain domain, byte[] request) {
		DataRequest dataRequest = new DataRequest()
				.setDomain(domain.getId())
				.setDate(new Date())
				.setBlackBox("")
				.setType(DataRequestType.TBAI);
		
		dataRequest = AON.saveDataRequest(domain.getName(), domain.getId(), "", dataRequest);
		
		Attach attach = new Attach()
			.setDomain(domain)
			.setAttachType(AttachType.DATA)
			.setType(DataAttachType.REQUEST.value())
			.setSource(DataAttachSource.TBAI.value())
			.setSourceId(dataRequest.getId())
			.setMimeType(MimeType.XML)
			.setData(request);
		
		AON.insertAttach(domain.getName(), domain.getId(), "", attach);
		
		return dataRequest;		
	}

	
	// **************************************************************
	// ************************************************ [CANCEL] ****
	// **************************************************************
	
	static class TbaiBajaContext {
		Invoice invoice;
		AnulaTicketBai request;
		
		public TbaiBajaContext(Invoice invoice, AnulaTicketBai request) {
			this.invoice = invoice;
			this.request = request;
		}
		
		public Invoice getInvoice() {
			return invoice;
		}
		
		public AnulaTicketBai getRequest() {
			return request;
		}
	}
	
	public static InvoiceCommunicatorContext cancel(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		try {
			List<TbaiBajaContext> bajaList = context.invoiceStream()
					.map(i -> new TbaiBajaContext(i, Invoice2tbai.buildBaja(context.getCompany(), i, context.getConfig()))).toList();
			
			for(TbaiBajaContext baja : bajaList) {
				TbaiValidation.validateAnulacion(baja.getRequest());
			}
			
			for(TbaiBajaContext baja : bajaList) {
				byte[] requestBytes = XMLUtils.marshal(baja.getRequest(), AnulaTicketBai.class);
				byte[] requestBytesSigned = TbaiSigner.getInstance().sign(context.getConfig(), requestBytes);
				String uri = TbaiUri.getUrlAnulacion(context.getConfig());
				byte[] responseBytes = XMLUtils.send(context.getConfig().getCertificate(), uri, requestBytesSigned);
				saveCancel(ctx, context, baja.getInvoice(), requestBytes, responseBytes);
			}
		} catch (JAXBException | IOException | AonSignerException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) {
			throw new InvoiceCommunicationException(e);
		}
		return context;
	}
	
	private static InvoiceCommunicatorContext saveCancel(AONContext ctx, InvoiceCommunicatorContext context, Invoice invoice, byte[] requestBytes, byte[] responseBytes) throws JAXBException {
		InvoiceBatch invoiceBatch = InvoiceCommunicationDAO.saveCancel(ctx, context.getDomain(), InvoiceCommunicationType.TBAI, requestBytes, responseBytes);
		TicketBaiResponse response = (TicketBaiResponse) XMLUtils.unmarshal(responseBytes, TicketBaiResponse.class);

		Salida salida = response.getSalida();
	    String estado = salida != null ? salida.getEstado() : null;
	    boolean anulada = "00".equals(estado) || "01".equals(estado);
		saveCancelInvoice(ctx, context, invoiceBatch, invoice, anulada);
		return context;
	}
	
	private static void saveCancelInvoice(AONContext ctx, InvoiceCommunicatorContext context, InvoiceBatch invoiceBatch, Invoice invoice, boolean correcto) {
		if(correcto) {
			InvoiceCommunicationDAO.saveInvoice(ctx, context.getDomain(), invoiceBatch, invoice.getId(), InvoiceCommunicationStatus.CANCELLED);
			InvoiceDAO.annul(ctx, invoice.getId());
		} else InvoiceCommunicationDAO.saveInvoiceBatchdetail(ctx, invoiceBatch, invoice.getId(), InvoiceCommunicationStatus.WRONG);
	}
	
}
