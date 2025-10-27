package net.aonsolutions.aon.verifactu;

import java.math.BigDecimal;
import java.util.Date;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class VerifactuUtils {

	static final String DATE_FORMAT = "dd-MM-yyyy";
	
	private VerifactuUtils() {
		
	}
	
	// ******************************************* [DATE]
	public static Date toDate(String d) {
		return AonDateUtils.parse(d, DATE_FORMAT );
	}
	public static String toString(Date d) {
		return AonDateUtils.format(d, DATE_FORMAT );
	}

	// ******************************************* [double]
	public static String toString(double d) {
		String ds = AonNumberUtils.toString( AonMathUtils.round(d) );
		return new BigDecimal(ds).stripTrailingZeros().toPlainString();
	}
	public static double todouble(String d) {
		return AonNumberUtils.todouble( d );
	}
	

}
