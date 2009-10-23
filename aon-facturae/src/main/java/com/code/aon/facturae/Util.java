package com.code.aon.facturae;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;

import com.code.aon.company.Company;
import com.code.aon.finance.Invoice;

import es.mityc.facturae31.AmountType;

public class Util {

	private static final Logger LOGGER = Logger.getLogger(FacturaeWriter.class.getName());

	public static String getBatchIdentifier( Invoice invoice, Company company ) {
		StringBuffer id = new StringBuffer( company.getDocument() );
		id.append( String.valueOf(invoice.getNumber()) );
		id.append( invoice.getSeries() );
		return id.toString();
	}
	
	public static XMLGregorianCalendar toXMLCalendar( Date date ) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime( date );
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar( calendar );
		} catch (DatatypeConfigurationException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
	public static AmountType getAmount( double value ) {
		AmountType amount = new AmountType();
		amount.setTotalAmount( value );
		return amount;
	}
	
	public static String toPostCodeType( String value ) {
		StringBuffer sb = new StringBuffer(5);
		for( int i = 0; i < value.length(); i++ ) {
			char c = value.charAt(i);
			if ( Character.isDigit(c) ) {
				sb.append(c);
			}
		}
		String pc = StringUtils.leftPad( sb.toString(), 5, '0' );
		return pc;
	}
	
}
