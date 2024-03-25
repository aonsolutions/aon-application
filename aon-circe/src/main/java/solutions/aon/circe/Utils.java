package solutions.aon.circe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

	public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd MM yyyy");

	public static Date parse(String text) {
		try {
			return Utils.FORMATTER.parse(text);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
