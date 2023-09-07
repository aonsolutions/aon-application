package net.aonsolutions.aon.invoice.communication.visitor;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.security.User;

import net.aonsolutions.aon.tbai.TBAI;

public class CancelInvoiceCommunicationTypeVisitor extends BasicCommunicationInvoiceTypeVisitor implements IInvoiceCommunicationTypeVisitor {

	public CancelInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice) {
		super(domain, user, invoice);
	}
	
	public CancelInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice, Integer certificateId) {
		super(domain, user, invoice, certificateId);
	}
	
	@Override
	public void visitSII() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTBAI() {
		try {
			TBAI.getInstance().cancel(getTbaiConfiguration(), getCompany(), getInvoice());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visitLROE() {
		try {
			TBAI.getInstance().cancel(getTbaiConfiguration(), getCompany(), getInvoice());
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

}
