package com.code.aon.ui.audit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.ui.util.AonUtil;


/**
 * The Class OptionGroup.
 */
public class OptionGroup {
	
	/** The id. */
	private String id;
	
	/** The description. */
	private String description;
	
	/** The styleClass. */
	private String styleClass;
	
	/** The rendered. */
	private String rendered;
	
	/** The category. */
	private ApplicationCategory category;
	
	private List<ApplicationOption> options;

	public OptionGroup(ApplicationCategory category, String description) {
		this.options = new ArrayList<ApplicationOption>();		
		this.description = description;
		this.category = category;
		this.category.addGroup(this);
	}
	
	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public ApplicationCategory getCategory() {
		return category;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
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
	 * Adds the option.
	 * 
	 * @param option the option
	 */
	public void addOption( ApplicationOption option) {
		this.options.add(option);
	}
	
	/**
	 * Gets the options.
	 * 
	 * @return the options
	 */
	public List<ApplicationOption> getOptions() {
		return this.options;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}		
	
}