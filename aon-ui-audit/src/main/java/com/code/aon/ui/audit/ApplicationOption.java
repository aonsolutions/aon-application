package com.code.aon.ui.audit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.util.AonUtil;

/**
 * The Class ApplicationOption.
 */
public class ApplicationOption {
	
	public static final String ID_PATTERN = "(id)";
	
	public static final String VALUE_PATTERN = "(value)";
	
	public static final String AON_MENU_ITEM = "aon:menuItem";

	private static final DateFormat RECENT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy - HH:mm");
		
	private static final String ID_ATTRIBUTE_PATTERN = "id=\"" + ID_PATTERN + "\"";

	/** The action. */
	private String action;

	/** The id. */
	private String id;
	
	/** The description. */
	private String description;
	
	/** The rendered. */
	private String rendered;
	
	/** The category. */
	private ApplicationCategory category;
	
	private String xml;	
	
	/**
	 * Gets the action.
	 * 
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 * 
	 * @param action the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the rendered.
	 * 
	 * @return the rendered
	 */
	public String getRendered() {
		return rendered;
	}

	/**
	 * Sets the rendered.
	 * 
	 * @param rendered the new rendered
	 */
	public void setRendered(String rendered) {
		this.rendered = rendered;
	}

	/**
	 * Gets the value of the expression.
	 * 
	 * @param expression
	 *            the expression
	 * 
	 * @return the expression value
	 */
	public boolean isRendered() {
		if ( this.rendered != null ) {
			return (Boolean) AonUtil.getValue(this.rendered);			
		}
		return true;
	}	
	
	/**
	 * Gets the category.
	 * 
	 * @return the category
	 */
	public ApplicationCategory getCategory() {
		return category;
	}

	/**
	 * Sets the category.
	 * 
	 * @param category the new category
	 */
	public void setCategory(ApplicationCategory category) {
		this.category = category;
	}
	
	public String getXml( String prefix ) {
		String newId = prefix + this.id;
		String newXml = StringUtils.replace(this.xml, VALUE_PATTERN, this.description);
		return StringUtils.replace(newXml, ID_PATTERN, newId);
	}

	public String getMenuItemXml( String prefix ) {
		return StringUtils.replace(getXml(prefix), ApplicationOptionController.AON_COMMAND_LINK, AON_MENU_ITEM);
	}
	
	public String getRecentXml( Date date ) {
		String newValue = this.description;
		if ( date != null ) {
			newValue = RECENT_DATE_FORMAT.format(date) + "&#160;&#160;&#160;" + newValue;
		}
		String xmlWithoutId = StringUtils.remove(this.xml, ID_ATTRIBUTE_PATTERN);
		return StringUtils.replace(xmlWithoutId, VALUE_PATTERN, newValue);
	}
	
	/**
	 * Sets the xml.
	 * 
	 * @param xml the new xml
	 */
	public void setXml(String xml) {
		this.xml = xml;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}