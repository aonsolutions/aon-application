package com.code.aon.ui.audit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The Class ApplicationCategory.
 */
public class ApplicationCategory extends BasicOption implements Comparable<ApplicationCategory> {

	private static final long serialVersionUID = 1L;
	
	/** The alias. */
	private String alias;

	private List<OptionGroup> groups;

	public ApplicationCategory(String description, String alias) {
		this.groups = new ArrayList<OptionGroup>();		
		setDescription(description);
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
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
		return new EqualsBuilder().append(getRawDescription(), rhs.getRawDescription()).isEquals();		
	}
	
	@Override
	public int hashCode() {
		return getRawDescription().hashCode();
	}
	
	@Override
	public int compareTo(ApplicationCategory o) {
		return getRawDescription().compareTo( o.getRawDescription() );
	}

	@Override
	public String toString() {
	     return new ToStringBuilder(this).
	       append("name", getRawDescription()).
	       append("alias", alias).
	       append("action", getAction()).
	       append("rendered", getRendered()).
	       toString();
	}	
	
}