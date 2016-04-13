package com.esferalia.aon.gwt.office.client;

import java.util.Date;

import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;

public class AonTagIssueSelected implements LabelSelected {
	
	protected JsLabel label;
	private String name;

	private DateTimeFormat timeFormat;
	private AonUserIssueSelected user;
	
	public AonTagIssueSelected(JsLabel label) {
		this.label = label;
		this.name = URL.decode(label.getName());
		this.timeFormat = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.S");
		if (label.getUser() != null)
			this.user = new AonUserIssueSelected(label.getUser());
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
	public AonUserIssueSelected getUser() {
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
		
		if (label.getDeletedAt() == null)
			return null;

		Date date = timeFormat.parse(label.getDeletedAt());
		String dateAsString = DateTimeFormat.getFormat("dd-MM-yyyy HH:mm")
				.format(date);
		return DateTimeFormat.getFormat("dd-MM-yyyy HH:mm").parse(dateAsString);
	}
	
	@Override
	public boolean endDateIsNull() {	
		return label.getDeletedAt() == null;
	}
	
	@Override
	public boolean isOfficeNotice() {		
		return label.getType() == TagType.OFFICE_NOTICE.value();
	}
	
	@Override
	public boolean isOfficePriority() {
		return label.getType() == TagType.OFFICE_PRIORITY.value();
	}
	
	@Override
	public boolean isOfficeStatus() {		
		return label.getType() == TagType.OFFICE_STATUS.value();
	}
	
	@Override
	public boolean isOfficeType() {		
		return label.getType() == TagType.OFFICE_TYPE.value();
	}
}
