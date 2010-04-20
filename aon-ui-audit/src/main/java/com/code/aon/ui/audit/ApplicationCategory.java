package com.code.aon.ui.audit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.ui.util.AonUtil;

/**
 * The Class ApplicationCategory.
 */
public class ApplicationCategory implements Comparable<ApplicationCategory> {
	
	/** The name. */
	private String name;
	
	/** The alias. */
	private String alias;

	/** The styleClass. */
	private String styleClass;
	
	/** The rendered. */
	private String rendered;
	
	private List<OptionGroup> groups;

	public ApplicationCategory(String name, String alias) {
		this.groups = new ArrayList<OptionGroup>();		
		this.name = name;
		this.alias = alias;
	}

	public String getIconClass() {
		for( String style : StringUtils.split(styleClass) ) {
			if ( StringUtils.startsWith(style, "aon-icon-") ) {
				return style;
			}
		}
		return null;
	}
	
	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
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
	 * Adds the group.
	 * 
	 * @param group the group
	 */
	public void addGroup( OptionGroup group ) {
		this.groups.add(group);
	}
	
	/**
	 * Gets the groups.
	 * 
	 * @return the groups
	 */
	public List<OptionGroup> getGroups() {
		return this.groups;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		ApplicationCategory rhs = (ApplicationCategory) obj;
		return new EqualsBuilder().append(name, rhs.name).isEquals();		
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public int compareTo(ApplicationCategory o) {
		return name.compareTo( o.getName() );
	}

	@Override
	public String toString() {
	     return new ToStringBuilder(this).
	       append("name", name).
	       append("alias", alias).
	       append("rendered", rendered).
	       append("styleClass", styleClass).
	       toString();
	}	
	
}