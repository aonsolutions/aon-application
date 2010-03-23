package com.code.aon.ui.audit;

import org.apache.commons.lang.StringUtils;


/**
 * The Class ApplicationCategory.
 */
public class ApplicationCategory {
	
	/** The name. */
	private String name;
	
	/** The alias. */
	private String alias;

	/** The styleClass. */
	private String styleClass;

	public ApplicationCategory(String name, String alias) {
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

}