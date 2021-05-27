package solutions.aon.in.invoice.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;

public class BlankPDFException extends InvoicePDFException {
	
	private static final long serialVersionUID = -2718519223847597960L;
	
	private PDDocument pdDocument;

	public BlankPDFException(PDDocument pdDocument) {
		super();
		this.pdDocument = pdDocument;
	}
	
	public PDDocument getPdDocument() {
		return pdDocument;
	}

}
