package net.aonsolutions.infovox;

import java.text.MessageFormat;

import com.esferalia.aon.watson.util.AonStringUtils;

public class OCRParams  {
	private static final String PARAM_TEMPLATE = "{0}={1}";
	private StringBuilder buf = new StringBuilder();
	
	protected OCRParams append(String name, String value) {
		if (AonStringUtils.isBlank(value)) return this;
		if (buf.length() > 0) {
			buf.append('&');	
		}
		buf.append(MessageFormat.format(PARAM_TEMPLATE,name,value));
		return this;
	}
	public String build() {
		return buf.insert(0,'?').toString();
	}
}
