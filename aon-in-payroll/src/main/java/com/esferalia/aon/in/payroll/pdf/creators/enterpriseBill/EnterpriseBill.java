package com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class EnterpriseBill {
	private InputStream background;
	private boolean detailed;

	private String reference;
	private Date date;

	private String document;
	private String name;

	private String address;
	private String zip_city_province;

	private List<EnterpriseBillEntry> entries;
	private List<EnterpriseBillTax> taxes;
	private List<EnterpriseBillFinance> finances;

	private double bottom_px;
	private double top_px;
	private InputStream qr_code;

	public EnterpriseBill(InputStream background, boolean detailed, String reference,
						  Date date, String document, String name, String address,
						  String zip_city_province, List<EnterpriseBillEntry> entries,
						  List<EnterpriseBillTax> taxes, List<EnterpriseBillFinance> finances,
						  double bottom_px, double top_px, InputStream qr_code) {

		this.background = background;
		this.detailed = detailed;
		this.reference = reference;
		this.date = date;
		this.document = document;
		this.name = name;
		this.address = address;
		this.zip_city_province = zip_city_province;
		this.entries = entries;
		this.taxes = taxes;
		this.finances = finances;
		this.bottom_px = bottom_px;
		this.top_px = top_px;
		this.qr_code = qr_code;
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

	public String getZip_city_province() {
		return zip_city_province;
	}

	public List<EnterpriseBillEntry> getEntries() {
		return entries;
	}

	public List<EnterpriseBillTax> getTaxes() {
		return taxes;
	}

	public List<EnterpriseBillFinance> getFinances() {
		return finances;
	}

	public double getBottom_px() {
		return bottom_px;
	}

	public double getTop_px() {
		return top_px;
	}

	public InputStream getQr_code() {
		return qr_code;
	}

	@Override
	public String toString() {
		return "EnterpriseBill{" +
				"background=" + background +
				", detailed=" + detailed +
				", reference='" + reference + '\'' +
				", date=" + date +
				", document='" + document + '\'' +
				", name='" + name + '\'' +
				", address='" + address + '\'' +
				", zip_city_province='" + zip_city_province + '\'' +
				", entries=" + entries +
				", taxes=" + taxes +
				", finances=" + finances +
				", bottom_px=" + bottom_px +
				", top_px=" + top_px +
				", qr_code=" + qr_code +
				'}';
	}
}
