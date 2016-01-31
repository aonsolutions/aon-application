package com.esferalia.aon.gwt.common.client.widget;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateBoxEx extends com.google.gwt.user.datepicker.client.DateBox {

	private static final DefaultFormat DEFAULT_FORMAT = GWT.create(DefaultFormat.class);
	
	public static class DefaultFormat implements Format {

		// TODO i18n
		public static final String MAIN_PATTERN = "dd/MM/yyyy";
		
		private static final DateTimeFormat[] EXTRA_PATTERNS = new DateTimeFormat[3];
		static {
			EXTRA_PATTERNS[0] = DateTimeFormat.getFormat("ddMMyy"); 
			EXTRA_PATTERNS[1] = DateTimeFormat.getFormat("ddMMyyyy"); 
			EXTRA_PATTERNS[2] = DateTimeFormat.getFormat("dd/MM/yy"); 
		}

		private final DateTimeFormat dateFormat;
		

		/**
		 * Creates a new default format instance.
		 */
		public DefaultFormat() {
			dateFormat = DateTimeFormat.getFormat(MAIN_PATTERN);
		}

		/**
		 * Creates a new default format instance.
		 * 
		 * @param dateTimeFormat
		 *            the {@link DateTimeFormat} to use with this {@link Format}
		 *            .
		 */
		public DefaultFormat(DateTimeFormat dateTimeFormat) {
			this.dateFormat = dateTimeFormat;
		}

		public String format(DateBox box, Date date) {
			if (date == null) {
				return "";
			} else {
				return dateFormat.format(date);
			}
		}

		/**
		 * Gets the date time format.
		 * 
		 * @return the date time format
		 */
		public DateTimeFormat getDateTimeFormat() {
			return dateFormat;
		}

		@SuppressWarnings("deprecation")
		public Date parse(DateBox dateBox, String dateText, boolean reportError) {
			Date date = null;
			try {
				if (dateText.length() > 0) {
					// TODO Mirar si el año sólo tiene dos dígitos.
					date = dateFormat.parseStrict(dateText);
				}
			} catch (IllegalArgumentException exception) {
				boolean parsed = false;
				for (DateTimeFormat format : EXTRA_PATTERNS) {
					try {
						date = format.parseStrict(dateText);
						dateBox.setValue(date,true);
						parsed = true;
						break;
					} catch (IllegalArgumentException e) {
						// next.
					}
				}
				if (!parsed) {
					try {
						date = new Date(dateText);
					} catch (IllegalArgumentException e1) {
						if (reportError) {
							dateBox.addStyleName(AON.AON_CSS.aonTextBoxError() );
						}
						return null;
					}
				}
			}
			return date;
		}

		public void reset(DateBox dateBox, boolean abandon) {
			dateBox.removeStyleName(AON.AON_CSS.aonTextBoxError() );
		}
	}

	public DateBoxEx() {
		super(new DatePicker(), null, DEFAULT_FORMAT);
		setStyleName(AON.AON_CSS.aonInputText());
		setWidth("80px");
	}

	public String format() {
		return getFormat().format(this, this.getValue());
	}

	public Date parse(String value, boolean b) {
		return getFormat().parse(this, value, b);
	}

}
