package com.code.aon.ui.audit;

import static com.code.aon.ui.audit.controller.MenuParser.FISCAL_STYLE_CLASS;
import static com.code.aon.ui.audit.controller.MenuParser.STYLE_ATTRIBUTE;
import static com.code.aon.ui.audit.controller.MenuParser.STYLE_CLASS_ATTRIBUTE;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.AonVersion;
import com.code.aon.ui.audit.controller.MenuParser;

/**
 * The Class ApplicationOption.
 */
public class ApplicationOption extends BasicOption implements Comparable<ApplicationOption> {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
	
	private String getAttribute( String attribute, String value ) {
		return attribute + "=\"" + value + "\"";
	}

	public String getMenuItemXml( String prefix ) {
		String xml = StringUtils.replace(getXml(prefix), MenuParser.AON_COMMAND_LINK, MenuParser.AON_MENU_ITEM);
		xml = xml.replaceAll( getAttribute(STYLE_ATTRIBUTE, "([^\"])+"), "");
		xml = xml.replace(getAttribute(STYLE_CLASS_ATTRIBUTE, FISCAL_STYLE_CLASS), getAttribute(STYLE_ATTRIBUTE, "margin-right: 1em;") );
		xml = xml.replaceAll( getAttribute(STYLE_CLASS_ATTRIBUTE, "([^\"])+"), "");
		return xml;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		ApplicationOption ao = (ApplicationOption) obj;
		return new EqualsBuilder().append(getId(), ao.getId()).isEquals();		
	}
	
	@Override
	public int compareTo(ApplicationOption o) {
		return getDescription().compareToIgnoreCase( o.getDescription() );
	}
	
	public boolean isAddParentDiv() {
		return ! StringUtils.contains(getXml(), FISCAL_STYLE_CLASS);
	}
	
	
}