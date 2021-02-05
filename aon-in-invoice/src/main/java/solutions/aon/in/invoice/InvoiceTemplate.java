package solutions.aon.in.invoice;

import solutions.aon.in.invoice.pdf.InvoicePDFException;

public interface InvoiceTemplate {
	
	public InvoiceTemplate parse( String text, InvoiceBuilder<?> handler) throws InvoicePDFException;

}
