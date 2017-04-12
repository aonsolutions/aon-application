package net.aonsolutions.aon.gwt.udapa.shared.quality;

public enum WidgetType {
	TEXTBOX("textbox"),
	TEXTAREA("textarea"),
	LISTBOX("listbox"),
	DOUBLEBOX("doublebox")
	;
	
	String name;
	
	private WidgetType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
