package solutions.aon.in.invoice.pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.InvoiceTemplate;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.templates.AutoMLTemplate;

public class InvoicePDFParser {
	
	private static final InvoiceTemplate PDF_TEMPLATES [] = {AutoMLTemplate.AUTO_ML_TEMPLATE,};
	
	
	public static void parse( File file , InvoiceBuilder<?> invoiceBuilder) throws IOException, UnknownInvoiceException {
		try (PDDocument doc = PDDocument.load(file))
		{
			parser(doc, invoiceBuilder);
		}
	}

	public static void parse( InputStream is , InvoiceBuilder<?> invoiceBuilder) throws IOException , UnknownInvoiceException {
		try (PDDocument doc = PDDocument.load(is))
		{
			parser(doc, invoiceBuilder);
		}
	}
	
	private static void parser(PDDocument doc, InvoiceBuilder<?> invoiceBuilder) throws IOException, UnknownInvoiceException {
        AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent())
		{
			throw new IOException("You do not have permission to extract text");
		}
		
		PDFTextStripper stripper= new PDFTextStripper();
		
		stripper.setSortByPosition(true);
		
		InvoiceTemplate template = null;
		
		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
            // Set the page interval to extract. 
			// If we don't, then all pages would be extracted.
			stripper.setStartPage(p);
			stripper.setEndPage(p);
			
			String text = stripper.getText(doc);
//System.out.println(text);
			if (text == null || (text.length()) == 0) {
				continue;
			}
			template = parse(template, text, invoiceBuilder);
			
		}
	}
	
	private static InvoiceTemplate parse ( InvoiceTemplate invoiceTemplate, String text, InvoiceBuilder<?> invoiceBuilder) throws UnknownInvoiceException, IOException {
		if ( invoiceTemplate == null ) {
			for (InvoiceTemplate pdfTemplate : PDF_TEMPLATES ) {
				try {
					return pdfTemplate.parse(text, invoiceBuilder);
				} catch ( InvoicePDFException e ) {
					return pdfTemplate;
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			}
			throw new UnknownInvoiceException("Formato de factura desconocido");
		
		} else {
			try {
				return invoiceTemplate.parse(text, invoiceBuilder);
			} catch ( InvoicePDFException e ) {
				return invoiceTemplate;
			}
		}
	}

}
