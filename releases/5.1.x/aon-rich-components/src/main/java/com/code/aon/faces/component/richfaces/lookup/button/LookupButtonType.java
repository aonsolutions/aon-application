package com.code.aon.faces.component.richfaces.lookup.button;

public enum LookupButtonType {
	
	LIST( "list" ),
	
	SEARCH( "search" ),
	
	NEW( "new" );
	
	private String name;

	LookupButtonType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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
