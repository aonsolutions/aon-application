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
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.sign.TbaiSigner;
import net.aonsolutions.aon.tbai.sign.TbaiSign;
import net.aonsolutions.aon.tbai.utils.XMLUtils;
import ticketbai.anulacion.AnulaTicketBai;
import ticketbai.emision.TicketBai;
import ticketbai.emision.TicketBaiResponse;
import ticketbai.zuzendu_alta.SubsanacionModificacionTicketBAI;

public class TBAI {

	public static TBAI getInstance() {
		return new TBAI();
	}
	
	public void accept(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice, TbaiBlockchain blockchain) throws Exception {
		byte[] data = generateAcceptXMl(tbaiConfiguration, company, invoice, blockchain);
		byte[] xml = TbaiSigner.getInstance().sign(tbaiConfiguration, data);
		String uri = TbaiUri.getUrlEmision(tbaiConfiguration);
		byte[] response = XMLUtils.send(tbaiConfiguration.getCertificate(), uri, xml);

		
	}
	
	public void modify(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) throws Exception {
		byte[] data = generateModifyXMl(tbaiConfiguration, company, invoice);
		byte[] xml = TbaiSigner.getInstance().sign(tbaiConfiguration, data);
		String uri = TbaiUri.getUrlZuzendu(tbaiConfiguration);
		byte[] response = XMLUtils.send(tbaiConfiguration.getCertificate(), uri, xml);
		TicketBaiResponse tbaiResponse = (TicketBaiResponse) XMLUtils.unmarshal(response, TicketBaiResponse.class);
	}
	
	public void cancel(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) throws Exception {
		byte[] data = generateCancelXMl(tbaiConfiguration, company, invoice);
		byte[] xml = TbaiSigner.getInstance().sign(tbaiConfiguration, data);
		String uri = TbaiUri.getUrlAnulacion(tbaiConfiguration);
		byte[] response = XMLUtils.send(tbaiConfiguration.getCertificate(), uri, xml);
	}

	public byte[] generateAcceptXMl(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice, TbaiBlockchain blockchain) throws Exception{
		TicketBai tbai = Invoice2tbai.build(company, invoice, tbaiConfiguration, blockchain);
		return XMLUtils.marshal(tbai, TicketBai.class);
	}
	
	public byte[] generateModifyXMl(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) throws Exception{
		SubsanacionModificacionTicketBAI tbai = Invoice2tbai.buildZuzendu(company, invoice, tbaiConfiguration, null, null, false);
		return XMLUtils.marshal(tbai, SubsanacionModificacionTicketBAI.class);
	}
	
	public byte[] generateCancelXMl(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) throws Exception{
		final AnulaTicketBai tbai = Invoice2tbai.buildBaja(company, invoice, tbaiConfiguration);
		return XMLUtils.marshal(tbai, AnulaTicketBai.class);
	}
	
	
	protected void save(Company company, Invoice invoice, byte[] request, byte[] response, TbaiConfiguration tbaiConfiguration) throws ParserConfigurationException, SAXException, IOException, JAXBException {
		DataRequest datRequest = saveRequest(company.getDomain(), request);
		
		String sign = TbaiSign.getSign(request);
		TicketBaiResponse tbaiResponse = (TicketBaiResponse) XMLUtils.unmarshal(response, TicketBaiResponse.class);

		TbaiBlockchain bc = new TbaiBlockchain().setDate(AonDateUtils.format(new Date(), "dd-MM-yyyy"))
			.setNumber(Integer.toString(invoice.getNumber())).setSerie(invoice.getSeries())
			.setSignature(sign.substring(0, 100));

		
	}
	
	private DataRequest saveRequest(Domain domain, byte[] request) {
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
