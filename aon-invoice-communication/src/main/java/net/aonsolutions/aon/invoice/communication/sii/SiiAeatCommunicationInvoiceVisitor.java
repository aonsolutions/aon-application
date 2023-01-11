package net.aonsolutions.aon.invoice.communication.sii;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

import net.aonsolutions.aon.invoice.communication.CommunicationInvoice;
import net.aonsolutions.aon.invoice.communication.ICommunicationInvoiceVisitor;
import net.aonsolutions.aon.sii.SendType;
import net.aonsolutions.aon.sii.aeat.SIIAeatPost;

public class SiiAeatCommunicationInvoiceVisitor implements ICommunicationInvoiceVisitor {

	@Override
	public void alta(CommunicationInvoice invoice) throws Exception {
		SiiConfiguration config = AON.getSiiConfiguration(invoice.getCompany().getDomain(), LOGIN);
		if(invoice.getInvoice().isSales()) {
//			return SIIAeatPost.getInstance(config).suministroFacturasEmitidas(
//					invoice.getCompany().getDomain(), LOGIN, invoice.getCompany(), , contextList, terceros, uri, modList, SendType.MOD_EMITIDAS);
		}	
	}
	
	@Override
	public void modificacion(CommunicationInvoice invoice) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void baja(CommunicationInvoice invoice) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
}
