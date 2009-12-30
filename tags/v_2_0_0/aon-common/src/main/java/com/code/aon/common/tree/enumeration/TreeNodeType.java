package com.code.aon.common.tree.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enumeration of Tree node types.
 * 
 * @author Consulting & Development. ecastellano - 28-feb-2006
 * @version 1.0
 * 
 * @since 1.0
 */
public enum TreeNodeType implements IResourceable {

	/**
	 * Root
	 */
	ROOT,

	/**
	 * Group.
	 */
	GROUP,

	/**
	 * Leaf.
	 */
	LEAF;

	/**
     * Messages file base path.
	 */
	private static final String BASE_NAME = "com.code.aon.console.workstation.i18n.enum";

	/**
     * Messages key prefix. 
	 */
	private static final String MSG_KEY_PREFIX = "aon_enum_treenodetype_";

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
	 */
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

}