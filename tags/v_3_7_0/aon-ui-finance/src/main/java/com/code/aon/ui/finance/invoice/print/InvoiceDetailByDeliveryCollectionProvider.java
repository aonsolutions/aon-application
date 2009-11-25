package com.code.aon.ui.finance.invoice.print;

import java.util.Date;

import com.code.aon.finance.InvoiceDetail;

public class InvoiceDetailByDeliveryCollectionProvider implements Comparable {
	
	private InvoiceDetail invoiceDetail;
	
	private String serieNumber;
	
	private Date date;

	public InvoiceDetailByDeliveryCollectionProvider(InvoiceDetail invoiceDetail, String series, int number, Date date) {
		this.invoiceDetail = invoiceDetail;
		String formattedNum = formatNumber(number);
		this.serieNumber = series + "/" + formattedNum;
		this.date = date;
	}

	public InvoiceDetail getInvoiceDetail() {
		return invoiceDetail;
	}

	public String getSerieNumber() {
		return serieNumber;
	}

	public void setSerieNumber(String serieNumber) {
		this.serieNumber = serieNumber;
	}

	public void setInvoiceDetail(InvoiceDetail invoiceDetail) {
		this.invoiceDetail = invoiceDetail;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int compareTo(Object o) {
		InvoiceDetailByDeliveryCollectionProvider invoiceDetailPrinter = (InvoiceDetailByDeliveryCollectionProvider)o;
		return (this.getSerieNumber().compareTo(invoiceDetailPrinter.getSerieNumber())==0?1:this.getSerieNumber().compareTo(invoiceDetailPrinter.getSerieNumber()));
	}
	
	private String formatNumber(int number) {
		String formattedNumber = "" + number;
		while(formattedNumber.length() < 7){
			formattedNumber = "0" + formattedNumber;
		}
		return formattedNumber;
	}
}
