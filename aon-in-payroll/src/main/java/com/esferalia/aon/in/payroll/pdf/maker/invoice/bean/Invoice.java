package com.esferalia.aon.in.payroll.pdf.maker.invoice.bean;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class Invoice {
	private InputStream	background;
	private boolean		detailed;

	private String reference;
	private Date   date;

	private String document;
	private String name;

	private String address;
	private String zipCityProvince;

	private List<InvoiceEntry>	 entries;
	private List<InvoiceTax>	 taxes;
	private List<InvoiceFinance> finances;

	private double		bottomPx;
	private double		topPx;
	private InputStream	qrCode;

	public Invoice(
			InputStream background, boolean detailed, String reference, Date date, String document, String name,
			String address, String zipCityProvince, List<InvoiceEntry> entries, List<InvoiceTax> taxes,
			List<InvoiceFinance> finances, double bottomPx, double topPx, InputStream qrCode
	) {

		this.background		 = background;
		this.detailed		 = detailed;
		this.reference		 = reference;
		this.date			 = date;
		this.document		 = document;
		this.name			 = name;
		this.address		 = address;
		this.zipCityProvince = zipCityProvince;
		this.entries		 = entries;
		this.taxes			 = taxes;
		this.finances		 = finances;
		this.bottomPx		 = bottomPx;
		this.topPx			 = topPx;
		this.qrCode			 = qrCode;
	}

	public InputStream getBackground() {
		return background;
	}

	public boolean isDetailed() {
		return detailed;
	}

	public String getReference() {
		return reference;
	}

	public Date getDate() {
		return date;
	}

	public String getDocument() {
		return document;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getZipCityProvince() {
		return zipCityProvince;
	}

	public List<InvoiceEntry> getEntries() {
		return entries;
	}

	public List<InvoiceTax> getTaxes() {
		return taxes;
	}

	public List<InvoiceFinance> getFinances() {
		return finances;
	}

	public double getBottomPx() {
		return bottomPx;
	}

	public double getTopPx() {
		return topPx;
	}

	public InputStream getQrCode() {
		return qrCode;
	}

}
