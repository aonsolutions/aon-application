package com.esferalia.aon.gwt.office.client;

import java.util.Date;

import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;

public class DefaultAonTagIssueSelected implements LabelSelected {
	
	protected JsLabel label;
	private String name;

	private DateTimeFormat timeFormat;
	private DefaultAonUserIssueSelected user;
	
	public DefaultAonTagIssueSelected(JsLabel label) {
		this.label = label;
		this.name = URL.decode(label.getName());
		this.timeFormat = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.S");
		if (label.getUser() != null)
			this.user = new DefaultAonUserIssueSelected(label.getUser());
	}
	
	@Override
	public Integer getId() {		
		return label.getId();
	}
	
	@Override
	public Integer getDomain() {		
		return label.getDomain();
	}
	
	@Override
	public byte getType() {	
		return label.getType();
	}
	
	@Override
	public String getName() {	
		return this.name;
	}
	
	@Override
	public String getColor() {		
		return label.getColor();
	}
	
	@Override
	public DefaultAonUserIssueSelected getUser() {
		return this.user;
	}
	
	@Override
	public Date getCreateAt() {		
		Date date = timeFormat.parse(label.getCreatedAt());
		String dateAsString = DateTimeFormat.getFormat("dd-MM-yyyy HH:mm")
				.format(date);
		return DateTimeFormat.getFormat("dd-MM-yyyy HH:mm").parse(dateAsString);
		
	}
	
	@Override
	public Date getDeletedAt() {
		if (label.getDeletedAt() != null) {
			Date date = timeFormat.parse(label.getDeletedAt());
			String dateAsString = DateTimeFormat.getFormat("dd-MM-yyyy HH:mm")
					.format(date);
			return DateTimeFormat.getFormat("dd-MM-yyyy HH:mm").parse(dateAsString);
		}
		return null;
	}
}
