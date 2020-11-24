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
	
	
	public static void parse( File file , InvoiceBuilder<?> handler) throws InvoicePDFException {
		try (PDDocument doc = PDDocument.load(file)) {
			parser(doc, handler);
		} catch (IOException e) {
			throw new InvoicePDFException(e);
		} catch (UnknownInvoiceException e) {
			throw new InvoicePDFException(e);
		}
	}

	public static void parse( InputStream is , InvoiceBuilder<?> handler) throws InvoicePDFException {
		try (PDDocument doc = PDDocument.load(is)) {
			parser(doc, handler);
		} catch (IOException e) {
			throw new InvoicePDFException(e);
		} catch (UnknownInvoiceException e) {
			throw new InvoicePDFException(e);
		} 
	}
	
	private static void parser(PDDocument doc, InvoiceBuilder<?> handler) throws InvoicePDFException, IOException, UnknownInvoiceException {
        AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent()) {
			throw new InvoicePDFException("You do not have permission to extract text");
		}
		PDFTextStripper stripper= new PDFTextStripper();
		stripper.setSortByPosition(true);
		InvoiceTemplate template = null;
		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
            // Set the page interval to extract. If we don't, then all pages would be extracted.
			stripper.setStartPage(p);
			stripper.setEndPage(p);
			
			String text = stripper.getText(doc);
			if (text == null || (text.length()) == 0) {
				continue;
			}
//System.out.println(text);
			template = parse(template, text, handler);
		}
		handler.finalizeParse();
	}
	
	private static InvoiceTemplate parse ( InvoiceTemplate invoiceTemplate, String text, InvoiceBuilder<?> handler) throws InvoicePDFException {
		if ( invoiceTemplate == null ) {
			for (InvoiceTemplate pdfTemplate : PDF_TEMPLATES ) {
				try {
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
