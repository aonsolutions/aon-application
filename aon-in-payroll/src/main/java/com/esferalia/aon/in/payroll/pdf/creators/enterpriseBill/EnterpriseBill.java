package com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class EnterpriseBill {
	private InputStream background;
	private boolean detailed;

	private String bill_number;
	private Date date;
	private String nif;
	private String enterprise_type;
	private String address_ln_1;
	private String getAddress_ln_2;
	private List<EnterpriseBillEntry> entries;

	private double base;
	private int percent;
	private double quote;
	private double total;
	private Date payDate;
	private String payType;
	private String account;
	private double amount;

	private double bottom_px;
	private double top_px;
	private InputStream qr_code;

	public EnterpriseBill(InputStream background, boolean detailed, String bill_number, Date date, String nif,
						  String enterprise_type, String address_ln_1, String getAddress_ln_2,
						  List<EnterpriseBillEntry> entries, double base, int percent, double quote,
						  double total, Date payDate, String payType, String account, double amount,
						  double bottom_px, double top_px, InputStream qr_code) {

		this.background = background;
		this.detailed = detailed;
		this.bill_number = bill_number;
		this.date = date;
		this.nif = nif;
		this.enterprise_type = enterprise_type;
		this.address_ln_1 = address_ln_1;
		this.getAddress_ln_2 = getAddress_ln_2;
		this.entries = entries;
		this.base = base;
		this.percent = percent;
		this.quote = quote;
		this.total = total;
		this.payDate = payDate;
		this.payType = payType;
		this.account = account;
		this.amount = amount;
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

	public String getBill_number() {
		return bill_number;
	}

	public Date getDate() {
		return date;
	}

	public String getNif() {
		return nif;
	}

	public String getEnterprise_type() {
		return enterprise_type;
	}

	public String getAddress_ln_1() {
		return address_ln_1;
	}

	public String getGetAddress_ln_2() {
		return getAddress_ln_2;
	}

	public List<EnterpriseBillEntry> getEntries() {
		return entries;
	}

	public double getBase() {
		return base;
	}

	public int getPercent() {
		return percent;
	}

	public double getQuote() {
		return quote;
	}

	public double getTotal() {
		return total;
	}

	public Date getPayDate() {
		return payDate;
	}

	public String getPayType() {
		return payType;
	}

	public String getAccount() {
		return account;
	}

	public double getAmount() {
		return amount;
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
				"background='" + background + '\'' +
				", detailed=" + detailed +
				", bill_number='" + bill_number + '\'' +
				", date=" + date +
				", nif='" + nif + '\'' +
				", enterprise_type='" + enterprise_type + '\'' +
				", address_ln_1='" + address_ln_1 + '\'' +
				", getAddress_ln_2='" + getAddress_ln_2 + '\'' +
				", entries=" + entries +
				", base=" + base +
				", percent=" + percent +
				", quote=" + quote +
				", total=" + total +
				", payDate=" + payDate +
				", payType='" + payType + '\'' +
				", account='" + account + '\'' +
				", amount=" + amount +
				", bottom_px=" + bottom_px +
				", top_px=" + top_px +
				", qr_code='" + qr_code + '\'' +
				'}';
	}
}
