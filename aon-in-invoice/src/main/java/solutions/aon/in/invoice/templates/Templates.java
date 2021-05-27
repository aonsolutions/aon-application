package solutions.aon.in.invoice.templates;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.InvoiceTemplate;
import solutions.aon.in.invoice.pdf.InvoicePDFException;

public class Templates {

	public static final InvoiceTemplate PDF_TEMPLATES [] = {AutoMLTemplate.AUTO_ML_TEMPLATE,};
	
	public static InvoiceTemplate parse ( String text, InvoiceBuilder<?> handler) throws InvoicePDFException {
		return parse(null, text, handler);
	}

	public static InvoiceTemplate parse ( InvoiceTemplate invoiceTemplate, String text, InvoiceBuilder<?> handler) throws InvoicePDFException {
		if ( invoiceTemplate == null ) {
			for (InvoiceTemplate pdfTemplate : PDF_TEMPLATES ) {
				try {
					
//System.out.println( text );					
					
					return pdfTemplate.parse(text, handler);
				} catch ( Throwable e ) {
					throw new InvoicePDFException(e);
				}
			}
			throw new InvoicePDFException("Formato de factura desconocido");
		
		} else {
			try {
				return invoiceTemplate.parse(text, handler);
			} catch ( InvoicePDFException e ) {
				throw new InvoicePDFException(e);
			}
		}
	}

}
