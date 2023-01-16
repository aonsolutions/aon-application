package net.aonsolutions.aon.invoice.communication.sii;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;

import net.aonsolutions.aon.invoice.communication.CommunicationInvoice;
import net.aonsolutions.aon.invoice.communication.ICommunicationInvoiceVisitor;
import net.aonsolutions.aon.tbai.TbaiMain;

public class SiiGipuzkoaCommunicationInvoiceVisitor implements ICommunicationInvoiceVisitor {

	@Override
	public void alta(CommunicationInvoice invoice) throws Exception {
		TbaiConfiguration config = AON.getTbaiConfiguration(invoice.getCompany().getDomain(), LOGIN);
		TbaiMain tb = new TbaiMain();
		tb.createEmisionTBAI(invoice.getCompany(), invoice.getInvoice(), config);
		
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
