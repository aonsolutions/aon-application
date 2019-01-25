package es.translogia.tedi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TediDateUtils {
	
	public static Date parse(String date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			return date == null ? null : format.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
}
