package net.aonsolutions.aon.tbai;

import java.io.IOException;
import java.util.Date;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.sign.TbaiSigner;
import net.aonsolutions.aon.tbai.sign.TbaiSign;
import net.aonsolutions.aon.tbai.utils.XMLUtils;
import ticketbai.anulacion.AnulaTicketBai;
import ticketbai.emision.TicketBai;
import ticketbai.respuesta.TicketBaiResponse;
import ticketbai.zuzendu_alta.SubsanacionModificacionTicketBAI;

public class TBAI {
	
	private TBAI() {
		
	}
	
	public static void accept(InvoiceCommunicationConfiguration icc, Company company, Invoice invoice, TbaiBlockchain blockchain) throws Exception {
		TbaiMain tbai = new TbaiMain();
		tbai.createEmisionTBAI(company, invoice, icc);
	}
	
	public static void modify(InvoiceCommunicationConfiguration icc, Company company, Invoice invoice) throws Exception {
		byte[] data = generateModifyXMl(icc, company, invoice);
		byte[] xml = TbaiSigner.getInstance().sign(icc, data);
		String uri = TbaiUri.getUrlZuzendu(icc);
		byte[] response = XMLUtils.send(icc.getCertificate(), uri, xml);
		TicketBaiResponse tbaiResponse = (TicketBaiResponse) XMLUtils.unmarshal(response, TicketBaiResponse.class);
	}
	
	public static void cancel(InvoiceCommunicationConfiguration icc, Company company, Invoice invoice) throws Exception {
		byte[] data = generateCancelXMl(icc, company, invoice);
		byte[] xml = TbaiSigner.getInstance().sign(icc, data);
		String uri = TbaiUri.getUrlAnulacion(icc);
		byte[] response = XMLUtils.send(icc.getCertificate(), uri, xml);
	}

	public static byte[] generateAcceptXMl(InvoiceCommunicationConfiguration icc, Company company, Invoice invoice, TbaiBlockchain blockchain) throws Exception{
		TicketBai tbai = Invoice2tbai.build(company, invoice, icc, blockchain);
		return XMLUtils.marshal(tbai, TicketBai.class);
	}
	
	public static byte[] generateModifyXMl(InvoiceCommunicationConfiguration icc, Company company, Invoice invoice) throws Exception{
		SubsanacionModificacionTicketBAI tbai = Invoice2tbai.buildZuzendu(company, invoice, icc, null, null, false);
		return XMLUtils.marshal(tbai, SubsanacionModificacionTicketBAI.class);
	}
	
	public static byte[] generateCancelXMl(InvoiceCommunicationConfiguration icc, Company company, Invoice invoice) throws Exception{
		final AnulaTicketBai tbai = Invoice2tbai.buildBaja(company, invoice, icc);
		return XMLUtils.marshal(tbai, AnulaTicketBai.class);
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

	
}
