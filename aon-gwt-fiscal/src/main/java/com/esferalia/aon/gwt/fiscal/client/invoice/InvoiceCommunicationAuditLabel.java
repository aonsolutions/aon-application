package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.InlineLabel;

public class InvoiceCommunicationAuditLabel extends InlineLabel {

	public InvoiceCommunicationAuditLabel(Date date, String user) {
		super();
		StringBuilder sb = new StringBuilder();
		sb.append("Comunicada");
		if (date != null) {
			sb.append(" el ");
			sb.append( format(date) );
		}
		if (AonStringUtils.isNotBlank(user))  {
			sb.append(" por '");
			sb.append( user );
			sb.append("'");
		}
		setText( sb.toString() );
		setStyleName(AON.CSS.aonNowrap() );
	}
	
	private String format(Date time) {
		if (time == null) return "";
		return AON.TIME_FORMAT.format(time);
	}
	
}
