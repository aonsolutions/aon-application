package solutions.aon.in.invoice;

import java.io.IOException;

public interface InvoiceTemplate {
	
	public  InvoiceTemplate parse( String text, InvoiceBuilder<?> handler) throws IOException, UnknownInvoiceException ;

}
