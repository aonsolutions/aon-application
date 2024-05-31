package net.aonsolutions.aon.tedi.invofox;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;

import net.aonsolutions.invofox.model.OCRDocument;
import net.aonsolutions.invofox.model.OCRInvoice;

public class OCRResult implements Serializable {

	private static final long serialVersionUID = -1880851370528653257L;

	private OCRDocument ocrDocument;
	private Invoice invoice;
	private LinkedList<InvoiceError> messages = new LinkedList<>();
	
	boolean selected;

	public OCRResult() {

	}

	public OCRResult(OCRDocument ocrDocument, Invoice invoice) {
		this.ocrDocument = ocrDocument;
		this.invoice = invoice;
	}
	
	public String getUuid() {
		return getOCRDocument() != null ? getOCRDocument().getId().orElse(null) : null;
	}

	public boolean isSelected() {
		return selected;
	}
	public OCRResult setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	public OCRDocument getOCRDocument() {
		return ocrDocument;
	}
	public OCRInvoice getOCRInvoice() {
		return ocrDocument.getData().orElse(null);
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	
	public void add(InvoiceError error) {
		messages.add(error);
	}

	public void clearMessages() {
		messages.clear();
	}

	public LinkedList<InvoiceError> getMessages() {
		return messages;
	}

	public boolean isImportable() {
		InvoiceErrorLevel level = getMoreSeriousLevel();
		return ( level == null || level.ordinal() < InvoiceErrorLevel.ERR.ordinal() );
	}

	public InvoiceErrorLevel getMoreSeriousLevel() {
		InvoiceErrorLevel level  = null;
		if (getMessages() != null) {
			for (InvoiceError error : getMessages()) {
				if (level == null || error.getLevel().ordinal() >  level.ordinal()) {
					level = error.getLevel();
				}
			}
		}
		return level;
	}
}
