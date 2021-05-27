package com.esferalia.aon.gwt.fiscal.client.mod349;

import com.esferalia.aon.occam.api.model.type.Mod349Key;
import com.google.gwt.user.client.ui.ListBox;

public class Mod349KeyListBox extends ListBox {
	
	public Mod349KeyListBox() {		
		setWidth("700px");		
		this.addItem( "-", "" );
		for (Mod349Key k : Mod349Key.values()) {
			this.addItem(k.getValue()+" - "+k.getDescription(), k.getValue());			
		}
	}

	public Mod349Key getValue() {		
		return Mod349Key.safeValueOf(getValue(getSelectedIndex()));
	}

	public void setValue(Mod349Key key) {
		setSelectedIndex(key==null?0:key.ordinal()+1);		
	}

}
