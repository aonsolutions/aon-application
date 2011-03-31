package com.code.aon.accounting.annualReport;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

public class AnnualReportFormatterEventHandler implements ReferenceInsertionEventHandler{

	private DateFormat dateFormatter;
	private NumberFormat numberFormatter;
	
	private NumberFormat getNumberFormatter() {
		if (numberFormatter == null) {
			numberFormatter = DecimalFormat.getInstance();
		}
		return numberFormatter;
	}
	
	private DateFormat getDateFormatter() {
		if (dateFormatter == null) {
			dateFormatter = new SimpleDateFormat("dd/MM/yyyy" );
		}
		return dateFormatter;
	}

	@Override
	public Object referenceInsert(String reference, Object value) {
		if (value != null && value instanceof Number) {
			return getNumberFormatter().format((Number) value);
		}
		if (value != null && value instanceof Date) {
			return getDateFormatter().format((Date) value);
		}
		return value;
	}

}
