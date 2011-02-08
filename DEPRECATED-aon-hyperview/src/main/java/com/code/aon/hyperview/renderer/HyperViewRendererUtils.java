package com.code.aon.hyperview.renderer;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import com.code.aon.hyperview.model.HyperViewAttribute;

public class HyperViewRendererUtils {
	private static final String AON_DATE_PATTERN_KEY = "aon_date_pattern";

	private static final String AON_TIME_PATTERN_KEY = "aon_time_pattern";

	private static final String AON_TIMESTAMP_PATTERN_KEY = "aon_timestamp_pattern";

	private DateFormat dateFormatter;

	private DateFormat timeFormatter;

	private DateFormat timestampFormatter;

	private NumberFormat integerFormatter;

	private NumberFormat decimalFormatter;

	private DateFormat sixFormatter = new SimpleDateFormat("yyMMdd");

	private DateFormat eightFormatter = new SimpleDateFormat("yyyyMMdd");

	public HyperViewRendererUtils(ResourceBundle bundle) {
		if (bundle != null) {
			integerFormatter = NumberFormat.getIntegerInstance(bundle
					.getLocale());
			decimalFormatter = NumberFormat.getNumberInstance(bundle
					.getLocale());
			String datePattern = bundle.getString(AON_DATE_PATTERN_KEY);
			dateFormatter = new SimpleDateFormat(datePattern);
			String timePattern = bundle.getString(AON_TIME_PATTERN_KEY);
			timeFormatter = new SimpleDateFormat(timePattern);
			String timestampPattern = bundle
					.getString(AON_TIMESTAMP_PATTERN_KEY);
			timestampFormatter = new SimpleDateFormat(timestampPattern);
		} else {
			integerFormatter = NumberFormat.getIntegerInstance();
			decimalFormatter = NumberFormat.getNumberInstance();
			dateFormatter = DateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
			timeFormatter = DateFormat.getTimeInstance(SimpleDateFormat.MEDIUM);
			timestampFormatter = DateFormat
					.getTimeInstance(SimpleDateFormat.FULL);
		}
	}

	public String getValue(HyperViewAttribute attr, Object value)
			throws HyperViewRendererException {
		if (value != null) {
			if (attr.isBoolean()) {
				if (!(value instanceof Boolean)) {
					throw new HyperViewRendererException("Value of '"
							+ attr.getLabel()
							+ "' is defined as Boolean, but is '"
							+ value.getClass().getName() + "'");
				}
				Boolean bool = (Boolean) value;
				if (bool != null) {
					return bool.toString();
				}
			} else if (attr.isDate()) {
				Date date = null;
				if (value instanceof Date) {
					date = (Date) value;
				} else {
					if ((value instanceof Number)) {
						date = getDate((Number) value);
					} else {
						throw new HyperViewRendererException("Value of '"
								+ attr.getLabel()
								+ "' is defined as Date, but is '"
								+ value.getClass().getName() + "'");
					}
				}
				if (date != null) {
					return dateFormatter.format(date);
				}
			} else if (attr.isTime()) {
				if (!(value instanceof Date)) {
					throw new HyperViewRendererException("Value of '"
							+ attr.getLabel()
							+ "' is defined as Time, but is '"
							+ value.getClass().getName() + "'");
				}
				Date date = (Date) value;
				if (date != null) {
					return timeFormatter.format(date);
				}
			} else if (attr.isTimestamp()) {
				if (!(value instanceof Date)) {
					throw new HyperViewRendererException("Value of '"
							+ attr.getLabel()
							+ "' is defined as Timestamp, but is '"
							+ value.getClass().getName() + "'");
				}
				Date date = (Date) value;
				if (date != null) {
					return timestampFormatter.format(date);
				}
			} else if (attr.isInteger()) {
				if (!(value instanceof Number)) {
					throw new HyperViewRendererException("Value of '"
							+ attr.getLabel()
							+ "' is defined as Integer, but is '"
							+ value.getClass().getName() + "'");
				}
				Number number = (Number) value;
				if (number != null) {
					return integerFormatter.format(number);
				}
			} else if (attr.isDouble()) {
				if (!(value instanceof Number)) {
					throw new HyperViewRendererException("Value of '"
							+ attr.getLabel()
							+ "' is defined as Double, but is '"
							+ value.getClass().getName() + "'");
				}
				Number number = (Number) value;
				if (number != null) {
					return decimalFormatter.format(number);
				}
			} else {
				if (!(value instanceof String)) {
					throw new HyperViewRendererException("Value of '"
							+ attr.getLabel()
							+ "' is defined as String, but is '"
							+ value.getClass().getName() + "'");
				}
				String lit = (String) value;
				return lit == null ? "" : lit;
			}
		}
		return "";
	}
	
	public Date getDate(Number number) throws HyperViewRendererException {
		String n = Integer.toString( number.intValue() );
		DateFormat formatter = null;
		if (n.length() < 6) { // YYMMDD
			StringBuffer buf = new StringBuffer( n );
			while (buf.length() < 6) {
				buf.insert(0, '0');	
			}
			n = buf.toString();
			formatter = sixFormatter;
		} else if (n.length() == 6) { // YYMMDD
			formatter = sixFormatter;
		} else if (n.length() < 8) { // YYYYMMDD
			n = '0' + n;
			formatter = eightFormatter;
		} else if (n.length() == 8) { // YYYYMMDD
			formatter = eightFormatter;
		} else if (n.length() > 8) {
			throw new HyperViewRendererException("Unknown Date.. '" + number + "'");
		}
		try {
			Date date = formatter.parse( n );
			return date; 
		} catch (Exception e) {
			throw new HyperViewRendererException("Unknown Date.. '" + number + "'. " + e.getMessage());
		}
				
	}
	
	
	public DateFormat getDateFormatter() {
		return dateFormatter;
	}

	public NumberFormat getDecimalFormatter() {
		return decimalFormatter;
	}

	public NumberFormat getIntegerFormatter() {
		return integerFormatter;
	}

	public DateFormat getTimeFormatter() {
		return timeFormatter;
	}

	public DateFormat getTimestampFormatter() {
		return timestampFormatter;
	}

}
