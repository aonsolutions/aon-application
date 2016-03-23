package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.stat.SelectableEnum;

public class DataColumType implements Serializable{
	
	private static final long serialVersionUID = -6125865721763488810L;
	
	public static String benefit;
	public static SelectableEnum<InvoiceType>[] invoiceType;
	
	public String getBenefit() {
		return benefit;
	}
	public DataColumType setBenefit(String benefit) {
		DataColumType.benefit = benefit;
		return this;
	}
	public static SelectableEnum<InvoiceType>[] getInvoiceType() {
		return invoiceType;
	}
	public DataColumType setInvoiceType(SelectableEnum<InvoiceType>[] invoiceType) {
		DataColumType.invoiceType = invoiceType;
		return this;
	}


}
