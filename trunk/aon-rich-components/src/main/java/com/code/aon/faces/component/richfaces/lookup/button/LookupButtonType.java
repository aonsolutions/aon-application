package com.code.aon.faces.component.richfaces.lookup.button;

import com.code.aon.faces.component.richfaces.lookup.ILookupConstants;
import com.code.aon.faces.component.util.FaceletUtil;

public enum LookupButtonType implements ILookupConstants {
	
	LIST( "list", LIST_TITLE, LIST_ACTION_LISTENER, LIST_STYLE_CLASS, LIST_DISABLED_STYLE_CLASS ),
	
	SEARCH( "search", LIST_TITLE, SEARCH_ACTION_LISTENER, LIST_STYLE_CLASS, LIST_DISABLED_STYLE_CLASS ),
	
	NEW( "new", NEW_TITLE, NEW_ACTION_LISTENER, NEW_STYLE_CLASS, NEW_DISABLED_STYLE_CLASS ),
	
	CLEAR( "clear", CLEAR_TITLE, CLEAR_ACTION_LISTENER, CLEAR_STYLE_CLASS );
	
	private String name;
	
	private String title;
	
	private String styleClass;
	
	private String disabledStyleClass;
	
	private String actionListener;

	LookupButtonType(String name, String title, String actionListener, String styleClass ) {
		this( name, title, actionListener, styleClass, null );
	}
	
	LookupButtonType(String name, String title, String actionListener, String styleClass, String disabledStyleClass ) {
		this.name = name;
		this.title = title;
		this.actionListener = actionListener;
		this.styleClass = styleClass;
		this.disabledStyleClass = disabledStyleClass;
	}

	public String getName() {
		return name;
	}
	
	public String getTitle() {
		return title;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public String getDisabledStyleClass() {
		return (disabledStyleClass != null) ? disabledStyleClass : styleClass;
	}

	public String getActionListener( String lookup ) {
		return FaceletUtil.appendExpression( lookup, actionListener );
	}
	
	public static LookupButtonType get( String value ) {
		for( LookupButtonType type : values() ) {
			if ( type.getName().equals(value) ) {
				return type;
			}
		}
		return null;
	}

}
