package com.code.aon.finance.util;

import org.apache.commons.lang.StringUtils;

import com.code.aon.finance.enumeration.InvoiceType;

public class FinanceUtil {
	/**
	 * Genera un Numero de Documento para Facturas.
	 * 
	 * @param type
	 *            Tipo de Factura 
	 * 
	 * @param series
	 *            Serie de Factura
	 * 
	 * @param number
	 *            Numero de Factura
	 * 
	 * @return String El Numero de Documento generado
	 */
	public static String getDocumentNumber(InvoiceType type, String series, int number) {
		String documentNumber = ((InvoiceType.SALES == type) ? "E" : (InvoiceType.UNDEDUCTIBLE == type) ? "G" : "R") + "-";
		if (!StringUtils.isEmpty(series)) {
			documentNumber += series + "/";
		}
		documentNumber += StringUtils.leftPad(Integer.toString(number), 6, "0");
		return documentNumber;
	}

}
