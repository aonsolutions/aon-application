package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.occam.api.model.type.Mod347Key;
import com.google.gwt.user.client.ui.ListBox;

public class Mod347KeyListBox extends ListBox {
	
	public Mod347KeyListBox() {		
		setWidth("700px");		
		this.addItem( "-", "" );
		for (Mod347Key k : Mod347Key.values()) {
			this.addItem(k.getValue()+" - "+k.getDescription(), k.getValue());			
		}
	}

	public Mod347Key getValue() {		
		return Mod347Key.safeValueOf(getValue(getSelectedIndex()));
	}

	public void setValue(Mod347Key key) {
		setSelectedIndex(key==null?0:key.ordinal()+1);		
	}

}
