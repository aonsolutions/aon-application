package net.aonsolutions.aon.tbai;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceDataName;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.sign.TbaiSigner;
import net.aonsolutions.aon.sign.exception.AonSignerException;
import net.aonsolutions.aon.tbai.sign.TbaiSign;
import net.aonsolutions.aon.tbai.utils.XMLUtils;
import ticketbai.anulacion.AnulaTicketBai;
import ticketbai.emision.Factura;
import ticketbai.emision.TicketBai;
import ticketbai.respuesta.Salida;
import ticketbai.respuesta.TicketBaiResponse;
import ticketbai.zuzendu_alta.SubsanacionModificacionTicketBAI;

public class TBAI {

	/** Estado de la respuesta cuando el fichero ha sido recibido; el 01 es rechazo. */
	private static final String ESTADO_RECIBIDO = "00";
	
	private TBAI() {
		
	}
	
	@Deprecated
	public static void accept(AONContext ctx, InvoiceCommunicationConfiguration icc, Company company, Invoice invoice) throws Exception {
		TbaiMain tbai = new TbaiMain();
		tbai.createEmisionTBAI(ctx, company, invoice, icc);
	}

	@Deprecated
	public static void modify(InvoiceCommunicationConfiguration icc, Company company, Invoice invoice) throws Exception {
		byte[] data = generateModifyXMl(icc, company, invoice);
		byte[] xml = TbaiSigner.getInstance().sign(icc, data);
		String uri = TbaiUri.getUrlZuzendu(icc);
		byte[] response = XMLUtils.send(icc.getCertificate(), uri, xml);
		TicketBaiResponse tbaiResponse = (TicketBaiResponse) XMLUtils.unmarshal(response, TicketBaiResponse.class);
	}
	
	@Deprecated
	public static byte[] generateAcceptXMl(InvoiceCommunicationConfiguration icc, Company company, Invoice invoice, TbaiBlockchain blockchain) throws Exception{
		TicketBai tbai = Invoice2tbai.build(company, invoice, icc, blockchain);
		return XMLUtils.marshal(tbai, TicketBai.class);
	}
	
	@Deprecated
	public static byte[] generateModifyXMl(InvoiceCommunicationConfiguration icc, Company company, Invoice invoice) throws Exception{
		SubsanacionModificacionTicketBAI tbai = Invoice2tbai.buildZuzendu(company, invoice, icc, null, null, false);
		return XMLUtils.marshal(tbai, SubsanacionModificacionTicketBAI.class);
	}

	@Deprecated
	protected static void save(Company company, Invoice invoice, byte[] request, byte[] response, InvoiceCommunicationConfiguration icc) throws ParserConfigurationException, SAXException, IOException, JAXBException {
		DataRequest datRequest = saveRequest(company.getDomain(), request);
		String sign = TbaiSign.getSign(request);
		TicketBaiResponse tbaiResponse = (TicketBaiResponse) XMLUtils.unmarshal(response, TicketBaiResponse.class);
		TbaiBlockchain bc = new TbaiBlockchain().setDate(AonDateUtils.format(new Date(), "dd-MM-yyyy"))
			.setNumber(Integer.toString(invoice.getNumber())).setSerie(invoice.getSeries())
			.setSignature(sign.substring(0, 100));
	}

	@Deprecated
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
		SubsanacionModificacionTicketBAI modifyRequest;
		AnulaTicketBai cancelRequest;
		
		public TbaiContext(Invoice invoice, AnulaTicketBai request) {
			this.invoice = invoice;
			this.cancelRequest = request;
		}
		
		public TbaiContext(Invoice invoice, TicketBai request) {
			this.invoice = invoice;
			this.acceptRequest = request;
		}
		
		public TbaiContext(Invoice invoice, SubsanacionModificacionTicketBAI request) {
			this.invoice = invoice;
			this.modifyRequest = request;
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
		
		public SubsanacionModificacionTicketBAI getModifyRequest() {
			return modifyRequest;
		}
		
		/** Factura del fichero que se comunica, sea de alta o de zuzendu. */
		public Factura getFactura() {
			if(acceptRequest != null) return acceptRequest.getFactura();
			return modifyRequest != null ? modifyRequest.getFactura() : null;
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
				TbaiContext accept = new TbaiContext(invoice, Invoice2tbai.build(context.getCompany(), invoice, context.getConfig(), blockchain));
				TbaiValidation.validateEmision(accept.getAcceptRequest());
				blockchain = getBlockchain(accept.getAcceptRequest(), invoice);
				accept.setBlockchain(blockchain);
				list.add(accept);
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
		InvoiceBatch invoiceBatch = InvoiceCommunicationDAO.saveAccept(ctx, context.getDomain(), InvoiceCommunicationType.TBAI, requestBytes, responseBytes);
		TicketBaiResponse response = (TicketBaiResponse) XMLUtils.unmarshal(responseBytes, TicketBaiResponse.class);
		saveTbaiInfo(ctx, context, tc, invoiceBatch.getDataResponse(), response.getSalida().getIdentificadorTBAI());
		InvoiceCommunicationDAO.saveInvoice(ctx, context.getDomain(), invoiceBatch, tc.invoice.getId(), 
				isRecibido(response) ? InvoiceCommunicationStatus.ACCEPTED : InvoiceCommunicationStatus.WRONG);
		return context;
	}
	
	private static void saveTbaiInfo(AONContext ctx, InvoiceCommunicatorContext context, TbaiContext tc, Integer dataResponseId, String tbaiId) {
		saveTbaiId(ctx, tc, dataResponseId, tbaiId);
		saveTbaiBlockchain(ctx, tc, dataResponseId);
		String tbaiUrl = buildTbaiUrl(ctx, context, tc, tbaiId);
		saveTbaiUrl(ctx, tc, dataResponseId, tbaiUrl);
	}
	
	private static void saveTbaiId(AONContext ctx, TbaiContext tc, Integer dataResponseId, String tbaiId) {
		InvoiceDataDAO.save(ctx, new InvoiceData()
				.setDomain(tc.getInvoice().getDomain())
				.setInvoice(tc.getInvoice().getId())
				.setName(InvoiceDataName.TBAI_ID)
				.setValue(tbaiId));
		DataResponseDetail drd1 = new DataResponseDetail()
				.setDomain(tc.getInvoice().getDomain())
				.setDataResponse(dataResponseId)
				.setDataVariable("tbaiId")
				.setDataValue(tbaiId);
		DataResponseDAO.insertDataResponseDetail(ctx, drd1);
	}
	
	private static void saveTbaiBlockchain(AONContext ctx, TbaiContext tc, Integer dataResponseId) {
		String blockchain = tc.getBlockchain().toJSON().toString();
		
		InvoiceDataDAO.save(ctx, new InvoiceData()
				.setDomain(tc.getInvoice().getDomain())
				.setInvoice(tc.getInvoice().getId())
				.setName(InvoiceDataName.TBAI_BLOCKCHAIN)
				.setValue(blockchain));
		
		DataResponseDetail drd2 = new DataResponseDetail()
				.setDomain(tc.getInvoice().getDomain())
				.setDataResponse(dataResponseId)
				.setDataVariable("blockchain")
				.setDataValue(blockchain);
		DataResponseDAO.insertDataResponseDetail(ctx, drd2);
	}
	
	private static void saveTbaiUrl(AONContext ctx, TbaiContext tc, Integer dataResponseId, String url) {
		InvoiceDataDAO.save(ctx, new InvoiceData()
				.setDomain(tc.getInvoice().getDomain())
				.setInvoice(tc.getInvoice().getId())
				.setName(InvoiceDataName.TBAI_URL)
				.setValue(url));
		
		DataResponseDetail drd3 = new DataResponseDetail()
				.setDomain(tc.getInvoice().getDomain())
				.setDataResponse(dataResponseId)
				.setDataVariable("tbaiUrl")
				.setDataValue(url);
		DataResponseDAO.insertDataResponseDetail(ctx, drd3);
	}
	
	private static String buildTbaiUrl(AONContext ctx, InvoiceCommunicatorContext context, TbaiContext tc, String tbaiId) {
		String qrUrl = TbaiUri.getUrlQr(context.getConfig()) + "?id=" + tbaiId + "&s="
				+ (tc.getInvoice().getSeries() != null ? tc.getInvoice().getSeries() : "") + "&nf=" + tc.getInvoice().getNumber() + "&i="
				+ tc.getFactura().getDatosFactura().getImporteTotalFactura();
				
		try {
			String crc = CRC8.calculate(qrUrl);
			qrUrl = qrUrl + "&cr=" + crc;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return qrUrl;
	}
	
	// **************************************************************
	// ************************************************ [MODIFY] ****
	// **************************************************************
	
	/**
	 * Comunica la subsanacion o la modificacion (zuzendu) de las facturas del
	 * contexto. El fichero de zuzendu repite el fichero de alta con los datos
	 * corregidos, mantiene su huella -el encadenamiento no cambia- e identifica la
	 * factura que se corrige con la firma de ese alta.
	 *
	 * El fichero no se firma -su esquema, a diferencia de los de alta y anulacion,
	 * no incluye el bloque Signature-: el remitente se identifica con el
	 * certificado del envio.
	 */
	public static InvoiceCommunicatorContext modify(AONContext ctx, InvoiceCommunicatorContext context) throws InvoiceCommunicationException {
		try {
			List<TbaiContext> list = new LinkedList<>();
			for(Invoice invoice : context.invoiceStream().toList()) {
				TicketBai acceptRequest = getAcceptRequest(ctx, invoice);
				TbaiBlockchain blockchain = getInvoiceBlockchain(ctx, invoice);
				TbaiContext modify = new TbaiContext(invoice, Invoice2tbai.buildZuzendu(context.getCompany(), invoice, context.getConfig(), 
						acceptRequest, blockchain, isSubsanar(ctx, invoice)));
				TbaiValidation.validateZuzendu(modify.getModifyRequest());
				modify.setBlockchain(blockchain);
				list.add(modify);
			}
		
			for(TbaiContext tc : list) {
				byte[] requestBytes = XMLUtils.marshal(tc.getModifyRequest(), SubsanacionModificacionTicketBAI.class);
				String uri = TbaiUri.getUrlZuzendu(context.getConfig());
				byte[] responseBytes = XMLUtils.send(context.getConfig().getCertificate(), uri, requestBytes);
				saveModify(ctx, context, tc, requestBytes, responseBytes);
			}
		
			return context;
		} catch (JAXBException | IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) {
			throw new InvoiceCommunicationException(e);
		}
	}
	
	/**
	 * Fichero de alta comunicado previamente, del que el zuzendu mantiene la
	 * huella.
	 */
	private static TicketBai getAcceptRequest(AONContext ctx, Invoice invoice) throws InvoiceCommunicationException, JAXBException {
		byte[] requestBytes = InvoiceCommunicationDAO.getLastRequest(ctx, invoice.getId(), InvoiceCommunicationType.TBAI, InvoiceCommunicationOperation.REGISTER)
				.orElseThrow(() -> new InvoiceCommunicationException(InvoiceCommunicationError.AON_0038));
		return (TicketBai) XMLUtils.unmarshal(requestBytes, TicketBai.class);
	}
	
	/**
	 * Encadenamiento del alta de la factura, cuya firma identifica en el zuzendu a
	 * la factura que se corrige.
	 */
	private static TbaiBlockchain getInvoiceBlockchain(AONContext ctx, Invoice invoice) {
		return TbaiBlockchain.fromJSON(InvoiceDataDAO.getValue(ctx, invoice.getDomain(), invoice.getId(), InvoiceDataName.TBAI_BLOCKCHAIN)
				.orElse(null));
	}
	
	/**
	 * El fichero de subsanacion corrige un alta rechazada; si el alta se registro,
	 * la accion es la de modificacion.
	 */
	private static boolean isSubsanar(AONContext ctx, Invoice invoice) {
		return InvoiceInfoDAO.get(ctx, invoice.getId(), InvoiceCommunicationType.TBAI)
				.map(InvoiceInfo::isWrong)
				.orElse(false);
	}
	
	private static InvoiceCommunicatorContext saveModify(AONContext ctx, InvoiceCommunicatorContext context, TbaiContext tc, byte[] requestBytes, byte[] responseBytes) throws JAXBException {
		InvoiceBatch invoiceBatch = InvoiceCommunicationDAO.saveModify(ctx, context.getDomain(), InvoiceCommunicationType.TBAI, requestBytes, responseBytes);
		TicketBaiResponse response = (TicketBaiResponse) XMLUtils.unmarshal(responseBytes, TicketBaiResponse.class);
		boolean recibido = isRecibido(response);
		if(recibido) saveTbaiModifyInfo(ctx, context, tc, invoiceBatch.getDataResponse(), response.getSalida().getIdentificadorTBAI());
		InvoiceCommunicationDAO.saveInvoice(ctx, context.getDomain(), invoiceBatch, tc.getInvoice().getId(), 
				recibido ? InvoiceCommunicationStatus.ACCEPTED : InvoiceCommunicationStatus.WRONG);
		return context;
	}
	
	/**
	 * El zuzendu genera un identificador TBAI nuevo y, con el, una nueva URL de
	 * verificacion. El encadenamiento no cambia, por lo que se mantiene el que se
	 * guardo con el alta.
	 */
	private static void saveTbaiModifyInfo(AONContext ctx, InvoiceCommunicatorContext context, TbaiContext tc, Integer dataResponseId, String tbaiId) {
		saveTbaiId(ctx, tc, dataResponseId, tbaiId);
		String tbaiUrl = buildTbaiUrl(ctx, context, tc, tbaiId);
		saveTbaiUrl(ctx, tc, dataResponseId, tbaiUrl);
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

		saveCancelInvoice(ctx, context, invoiceBatch, invoice, isRecibido(response));
		return context;
	}
	
	private static void saveCancelInvoice(AONContext ctx, InvoiceCommunicatorContext context, InvoiceBatch invoiceBatch, Invoice invoice, boolean correcto) {
		if(correcto) {
			InvoiceCommunicationDAO.saveInvoice(ctx, context.getDomain(), invoiceBatch, invoice.getId(), InvoiceCommunicationStatus.CANCELLED);
			InvoiceDAO.annul(ctx, invoice.getId());
		} else InvoiceCommunicationDAO.saveInvoiceBatchdetail(ctx, invoiceBatch, invoice.getId(), InvoiceCommunicationStatus.WRONG);
	}
	
	/**
	 * Determina si el servicio ha recibido el fichero. Un fichero recibido puede
	 * llevar avisos, que no suponen rechazo, mientras que el estado 01 es el rechazo
	 * del fichero y deja la factura sin comunicar.
	 */
	private static boolean isRecibido(TicketBaiResponse response) {
		Salida salida = response != null ? response.getSalida() : null;
		return salida != null && ESTADO_RECIBIDO.equals(salida.getEstado());
	}
	
}
