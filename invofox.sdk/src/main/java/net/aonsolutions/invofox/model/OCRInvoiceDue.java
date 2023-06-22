package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRInvoiceDue implements Serializable {

	private static final long serialVersionUID = 4460168378961329416L;
	
	private OCRString date;
	private OCRNumber amount;

	public Optional<OCRString> getDate() {
		return Optional.ofNullable(date);
	}
	public OCRInvoiceDue setDate(OCRString date) {
		this.date = date;
		return this;
	}

	public Optional<OCRNumber> getAmount() {
		return Optional.ofNullable(amount);
	}
	public OCRInvoiceDue setAmount(OCRNumber amount) {
		this.amount = amount;
		return this;
	}

}
