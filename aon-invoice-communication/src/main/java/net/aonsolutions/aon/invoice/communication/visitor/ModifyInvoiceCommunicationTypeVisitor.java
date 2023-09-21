package net.aonsolutions.aon.invoice.communication.visitor;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.security.User;

public class ModifyInvoiceCommunicationTypeVisitor extends BasicCommunicationInvoiceTypeVisitor implements IInvoiceCommunicationTypeVisitor {

	public ModifyInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice) {
		super(domain, user, invoice);
	}
	
	public ModifyInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice, Integer certificateId) {
		super(domain, user, invoice, certificateId);
	}
	
	@Override
	public void visitSII() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTBAI() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitLROE() {
		// TODO Auto-generated method stub
		
	}

}
