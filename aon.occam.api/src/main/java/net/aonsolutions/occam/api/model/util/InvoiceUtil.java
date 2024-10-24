package net.aonsolutions.occam.api.model.util;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.InvoiceType.InvoiceTypeVisitor;

public class InvoiceUtil {
	
	private static final String PROFORMA = "PROFORMA";
	private static final int TIMES = 6;
	
	private InvoiceUtil() {
		
	}
	
	public static String getDocumentNumber(InvoiceType type, String series, Integer number) {
		String documentNumber = type == null
			?AonStringUtils.EMPTY
			:type.visit(new InvoiceTypeVisitor<String>() {
				@Override public String visitPurchase() {return "R-";}
				@Override public String visitExpenses() {return "R-";}
				@Override public String visitSales() 	{return "E-";}
				@Override public String visitUndeductible() 	{return "G-";}
			});
		documentNumber += getSeriesNumber(series,number);
		return documentNumber;
	}
	
	public static String getSeriesNumber(String series, Integer number) {
		String seriesNumber = AonStringUtils.defaultIfBlank(series);
		if (AonStringUtils.isNotBlank(series)) seriesNumber += "/";
		seriesNumber += getNumber(number);
		return seriesNumber;
	}

	public static String getNumber(Integer number) {
		if (number == null) return AonStringUtils.repeat('?', TIMES);
		if (AonMathUtils.isLessThanZero(number)) return PROFORMA;
		return AonStringUtils.leftPad(AonNumberUtils.toString(number), 6, "0");
	}
}
