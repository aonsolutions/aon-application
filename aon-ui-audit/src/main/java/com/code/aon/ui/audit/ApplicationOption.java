package com.code.aon.ui.audit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The Class ApplicationOption.
 */
public class ApplicationOption {
	
	private static final DateFormat RECENT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy - H:m");
	
	public static final String ID_PATTERN = "(id)";
	
	public static final String VALUE_PATTERN = "(value)";
	
	private static final String ID_ATTRIBUTE_PATTERN = "id=\"" + ID_PATTERN + "\"";

	/** The action. */
	private String action;

	/** The id. */
	private String id;
	
	/** The description. */
	private String description;
	
	/** The category. */
	private String category;
	
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
	 * Gets the category.
	 * 
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the category.
	 * 
	 * @param category the new category
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getXml( String prefix ) {
		String newId = prefix + this.id;
		String newXml = StringUtils.replace(this.xml, VALUE_PATTERN, this.description);
		return StringUtils.replace(newXml, ID_PATTERN, newId);
	}

	public String getRecentXml( Date date ) {
		String newValue = this.description;
		if ( date != null ) {
			newValue = RECENT_DATE_FORMAT.format(date) + "  " + newValue;
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