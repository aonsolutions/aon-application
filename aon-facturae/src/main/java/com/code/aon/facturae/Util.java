package com.code.aon.facturae;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.company.Enterprise;
import com.code.aon.finance.Invoice;

import es.mityc.facturae32.AmountType;

public class Util {

	private static final Logger LOGGER = LoggerFactory.getLogger(Util.class.getName());

	public static String getBatchIdentifier( Invoice invoice, Enterprise enterprise ) {
		StringBuffer id = new StringBuffer( enterprise.getRegistry().getDocument() );
		id.append( String.valueOf(invoice.getNumber()) );
		id.append( invoice.getSeries() );
		return id.toString();
	}
	
	public static XMLGregorianCalendar toXMLCalendar( Date date ) {
		XMLGregorianCalendar xgc = null;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime( date );
		try {
			xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar( calendar );
			xgc.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			xgc.setTime(
					DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
					DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
		} catch (DatatypeConfigurationException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return xgc;
	}
	
	public static AmountType getAmount( double value ) {
		AmountType amount = new AmountType();
		amount.setTotalAmount( value );
		return amount;
	}
	
	public static String toPostCodeType( String value ) {
		StringBuffer sb = new StringBuffer(5);
		if (! StringUtils.isEmpty(value) ) {
			for( int i = 0; i < value.length(); i++ ) {
				char c = value.charAt(i);
				if ( Character.isDigit(c) ) {
					sb.append(c);
				}
			}			
		}
		return StringUtils.leftPad( sb.toString(), 5, '0' );
	}

	public static String toTextMax10Type( String value ) {
		return StringUtils.substring(value, 0, 10);
	}
	
	public static String toTextMax20Type( String value ) {
		return StringUtils.substring(value, 0, 20);
	}
	
	public static String toTextMax40Type( String value ) {
		return StringUtils.substring(value, 0, 40);
	}

	public static String toTextMax50Type( String value ) {
		return StringUtils.substring(value, 0, 50);
	}
	
	public static String toTextMax80Type( String value ) {
		return StringUtils.substring(value, 0, 80);
	}

	public static String toTextMax2500Type( String value ) {
		return StringUtils.substring(value, 0, 2500);
	}
	
}
