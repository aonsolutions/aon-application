package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceUtil {

	public static String getDocumentNumber(InvoiceType type, String series, Integer number) {
		String documentNumber = ((InvoiceType.SALES == type) ? "E" : (InvoiceType.UNDEDUCTIBLE == type) ? "G" : "R") + "-";
		if (!AonStringUtils.isEmpty(series)) {
			documentNumber += series + "/";
		}
		documentNumber += AonStringUtils.leftPad(AonNumberUtils.toString(number), 6, "0");
		return documentNumber;
	}
	
	public static String getSeriesNumber(String series, Integer number) {
		String seriesNumber = AonStringUtils.defaultIfBlank(series);
		if (AonStringUtils.isNotBlank(series)) {
			seriesNumber += "/";
		}
		seriesNumber += AonStringUtils.leftPad(AonNumberUtils.toString(number), 6, "0");
		return seriesNumber;
	}


}
