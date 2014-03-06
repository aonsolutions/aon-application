package com.code.aon.ui.audit;

import static com.code.aon.ui.audit.controller.MenuParser.A4J_COMMAND_LINK;
import static com.code.aon.ui.audit.controller.MenuParser.AON_COMMAND_LINK;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.ui.util.AonUtil;

public class BasicOption implements Serializable, IOption {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	/** The id. */
	private String id;
		
	/** The action. */
	private String action;
	
	/** The description. */
	private String description;	
	
	/** The rendered. */
	private String rendered;	
	
	private String viewId;	

	private List<ActionSource> actionSources;
	
	private String xml;		
	
	public BasicOption() {
		this.actionSources = new LinkedList<ActionSource>();
	}
	
	public List<ActionSource> getActionSources() {
		return actionSources;
	}
	
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
		return (String) AonUtil.getValue(description);
	}
	
	public String getRawDescription() {
		return this.description;
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
	
	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}	

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getXml( String prefix ) {
		String newId = prefix + getId();
		String newXml = StringUtils.replace(getXml(), VALUE_PATTERN, getRawDescription());
		return StringUtils.replace(newXml, ID_PATTERN, newId);
	}

	public String getInitActionXml( String prefix ) {
		String xml = StringUtils.replace(getXml(prefix), AON_COMMAND_LINK, A4J_COMMAND_LINK);
		return StringUtils.replace(xml, "<"+A4J_COMMAND_LINK, "<"+A4J_COMMAND_LINK+" reRender='aonContent'");
	}
	
}
