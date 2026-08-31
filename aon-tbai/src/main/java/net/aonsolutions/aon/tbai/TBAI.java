package net.aonsolutions.aon.tbai;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
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
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
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
	// ************************************************ [MODEL] *****
	// **************************************************************		

	static class TbaiContext {
		Invoice invoice;
		TicketBai acceptRequest;
		TbaiBlockchain blockchain;
		AnulaTicketBai cancelRequest;
		
		public TbaiContext(Invoice invoice, AnulaTicketBai request) {
			this.invoice = invoice;
			this.cancelRequest = request;
		}
		
		public TbaiContext(Invoice invoice, TicketBai request) {
			this.invoice = invoice;
			this.acceptRequest = request;
		}
		
		public Invoice getInvoice() {
			return invoice;
		}
		
		public AnulaTicketBai getCancelRequest() {
			return cancelRequest;
		}
		
		public TicketBai getAcceptRequest() {
			return acceptRequest;
		}

		public TbaiBlockchain getBlockchain() {
			return blockchain;
		}
		
		public void setBlockchain(TbaiBlockchain blockchain) {
			this.blockchain = blockchain;
		}
	}
	
	// **************************************************************
	// ************************************************ [ACCEPT] ****
	// **************************************************************
		
	public static TbaiBlockchain getBlockchain(AONContext ctx, Integer actualInvoice) {
		DataResponse dr = DataResponseDAO.getLastDataResponse(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getSourceProperty().eq(DataResponseSource.TBAI.value()))
			.and(f.getSourceIdProperty().ne(actualInvoice))
			.and(f.getCodeProperty().ne("baja"))).orElse(new DataResponse());
		
		DataResponseDetail drd = dr.getId() != null ? DataResponseDAO.getDataResponseDetailStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getDataResponseProperty().eq(dr.getId()))
			.and(f.getDataVariableProperty().eq("blockchain")))
			.findFirst()	
			.orElse(new DataResponseDetail()) : new DataResponseDetail();
		
		return TbaiBlockchain.fromJSON(drd.getDataValue());
	}
	
	public static TbaiBlockchain getBlockchain(TicketBai tbai, Invoice invoice) throws InvoiceCommunicationException {
		try {
			return new TbaiBlockchain().setDate(AonDateUtils.format(new Date(), "dd-MM-yyyy"))
				.setNumber(Integer.toString(invoice.getNumber())).setSerie(invoice.getSeries())
				.setSignature(TbaiSign.getSign(XMLUtils.marshal(tbai, TicketBai.class)).substring(0, 100));
		} catch (ParserConfigurationException | SAXException | IOException | JAXBException e) {
			e.printStackTrace();
			throw new InvoiceCommunicationException(e);
		}	
	}
	
	public static InvoiceCommunicatorContext accept(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		try {
			TbaiBlockchain blockchain = getBlockchain(ctx, context.invoiceStream().findFirst().map(Invoice::getId).orElse(null));
			List<TbaiContext> list = new LinkedList<>();
			for(Invoice invoice : context.invoiceStream().toList()) {
//				InvoiceDAO.accept(ctx, invoice);
				TbaiContext accept = new TbaiContext(invoice, Invoice2tbai.build(context.getCompany(), invoice, context.getConfig(), blockchain));
				list.add(accept);
//				TbaiValidation.validateEmision(accept.getAcceptRequest());
				blockchain = getBlockchain(accept.getAcceptRequest(), invoice);
			}
		
			for(TbaiContext tc : list) {
				byte[] requestBytes = XMLUtils.marshal(tc.getAcceptRequest(), TicketBai.class);
				byte[] requestBytesSigned = TbaiSigner.getInstance().sign(context.getConfig(), requestBytes);
				String uri = TbaiUri.getUrlEmision(context.getConfig());
				byte[] responseBytes = XMLUtils.send(context.getConfig().getCertificate(), uri, requestBytesSigned);
				saveAccept(ctx, context, tc, requestBytes, responseBytes);
			}
		
			return context;
		} catch (JAXBException | IOException | AonSignerException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) {
			throw new InvoiceCommunicationException(e);
		}
	}
	
	private static InvoiceCommunicatorContext saveAccept(AONContext ctx, InvoiceCommunicatorContext context, TbaiContext tc, byte[] requestBytes, byte[] responseBytes) throws JAXBException {
		// SAVE INVOICE DATA 
		// SAVE BLOCKCHAIN DATA tc.getBlockchain()
 		
		InvoiceBatch invoiceBatch = InvoiceCommunicationDAO.saveAccept(ctx, context.getDomain(), InvoiceCommunicationType.TBAI, requestBytes, responseBytes);
		TicketBaiResponse response = (TicketBaiResponse) XMLUtils.unmarshal(responseBytes, TicketBaiResponse.class);

		Salida salida = response.getSalida();
	    String estado = salida != null ? salida.getEstado() : null;
	    boolean ok = "00".equals(estado) || "01".equals(estado);
		InvoiceCommunicationDAO.saveInvoice(ctx, context.getDomain(), invoiceBatch, tc.invoice.getId(), 
				ok ? InvoiceCommunicationStatus.ACCEPTED : InvoiceCommunicationStatus.WRONG);
		return context;
	}

	// **************************************************************
	// ************************************************ [CANCEL] ****
	// **************************************************************
	
	
	
	public static InvoiceCommunicatorContext cancel(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		try {
			List<TbaiContext> bajaList = context.invoiceStream()
					.map(i -> new TbaiContext(i, Invoice2tbai.buildBaja(context.getCompany(), i, context.getConfig()))).toList();
			
			for(TbaiContext baja : bajaList) {
				TbaiValidation.validateAnulacion(baja.getCancelRequest());
			}
			
			for(TbaiContext baja : bajaList) {
				byte[] requestBytes = XMLUtils.marshal(baja.getCancelRequest(), AnulaTicketBai.class);
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
