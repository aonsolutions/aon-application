package net.aonsolutions.infovox.model;

import java.util.Optional;

public class OCRInvoiceDue {

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
