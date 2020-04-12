package net.aonsolutions.aon.tedi.visitors;

import com.esferalia.aon.occam.api.model.type.InvoiceType;

import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceCategory;
import es.translogia.tedi.ewok.TediVisitors.TediInvoiceTypeVisitor;

public class InvoiceTypeVisitor implements TediInvoiceTypeVisitor {
	private TediInvoice invoice;
	private InvoiceType invoiceType;

	public InvoiceTypeVisitor(TediInvoice invoice) {
		this.invoice = invoice;
		invoice.getType().visit(this);
	}

	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	@Override
	public void visitEMITIDA() {
		invoiceType = InvoiceType.SALES;
	}

	@Override
	public void visitRECIBIDA() {
		invoiceType = (invoice.getCategory() == TediInvoiceCategory.C6000|| invoice.getCategory() == TediInvoiceCategory.C6070) 
			? InvoiceType.PURCHASE
			: InvoiceType.EXPENSES;
	}

	@Override
	public void visitTICKET() {
		invoiceType = InvoiceType.UNDEDUCTIBLE;
	}
}