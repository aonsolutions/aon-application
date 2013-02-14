package com.code.aon.accounting.annualReport;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

public class AnnualReportFormatterEventHandler implements ReferenceInsertionEventHandler{

	private static final String NUMBER_PATTERN = "#,##0.00"; 
	private static final String DATE_PATTERN = "dd/MM/yyyy"; 
	private static final String EMPTY = ""; 
		
	private DateFormat dateFormatter;
	private NumberFormat numberFormatter;
	
	private NumberFormat getNumberFormatter() {
		if (numberFormatter == null) {
			numberFormatter = new DecimalFormat(NUMBER_PATTERN);
		}
		return numberFormatter;
	}
	
	private DateFormat getDateFormatter() {
		if (dateFormatter == null) {
			dateFormatter = new SimpleDateFormat(DATE_PATTERN);
		}
		return dateFormatter;
	}

	@Override
	public Object referenceInsert(String reference, Object value) {
		if (value != null && value instanceof Number) {
			Number number = (Number) value; 
			if (number.doubleValue() != 0) {
				return getNumberFormatter().format(number);
			}
			return EMPTY;
		}
		if (value != null && value instanceof Date) {
			return getDateFormatter().format((Date) value);
		}
		return value;
	}

}
