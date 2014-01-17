package com.esferalia.aon.gwt.fiscal.client.widget;

import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.user.cellview.client.Column;

public abstract class TextInputColumn<T> extends Column<T, String> {
	
	public TextInputColumn() {
		super(new TextInputCell());
	}
}
