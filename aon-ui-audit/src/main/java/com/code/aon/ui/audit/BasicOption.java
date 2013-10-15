package com.code.aon.ui.audit;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.ui.util.AonUtil;

public class BasicOption implements Serializable, IOption {

	private static final long serialVersionUID = 1L;

	/** The action. */
	private String action;
	
	/** The rendered. */
	private String rendered;	
	
	private String viewId;	

	private List<ActionSource> actionSources;
	
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
	
}
