package com.code.aon.ui.audit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.AonVersion;
import com.code.aon.ui.audit.controller.MenuParser;

/**
 * The Class ApplicationOption.
 */
public class ApplicationOption extends BasicOption {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final DateFormat RECENT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy - HH:mm");
		
	private static final String ID_ATTRIBUTE_PATTERN = "id=\"" + ID_PATTERN + "\"";

	/** The group. */
	private OptionGroup group;
		
	/**
	 * Gets the group.
	 * 
	 * @return the group
	 */
	public OptionGroup getGroup() {
		return group;
	}

	/**
	 * Sets the group.
	 * 
	 * @param group the new group
	 */
	public void setGroup(OptionGroup group) {
		this.group = group;
	}

	public String getMenuItemXml( String prefix ) {
		return StringUtils.replace(getXml(prefix), MenuParser.AON_COMMAND_LINK, MenuParser.AON_MENU_ITEM);
	}
	
	public String getRecentXml( Date date ) {
		String newValue = getRawDescription();
		if ( date != null ) {
			newValue = RECENT_DATE_FORMAT.format(date) + "&#160;&#160;&#160;" + newValue;
		}
		String xmlWithoutId = StringUtils.remove(getXml(), ID_ATTRIBUTE_PATTERN);
		return StringUtils.replace(xmlWithoutId, VALUE_PATTERN, newValue);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}