package net.aonsolutions.aon.tbai;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;

import net.aonsolutions.aon.sign.TbaiSigner;
import net.aonsolutions.aon.tbai.utils.XMLUtils;
import ticketbai.anulacion.AnulaTicketBai;
import ticketbai.emision.TicketBai;
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
}
