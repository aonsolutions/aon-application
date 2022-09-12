package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Arrays;

import com.esferalia.aon.occam.api.model.type.DomainType;
import com.google.gwt.user.client.ui.ListBox;

public class AonDomainTypeBox extends ListBox {
	
	public AonDomainTypeBox() {
		this.addItem( "" );
		Arrays.stream(DomainType.values())
			.forEach(t -> this.addItem( t.getName() ));
	}
	
	public DomainType getValue() {
		int i = getSelectedIndex();
		return (i==0)? null : DomainType.values()[i-1];
	}
	
	public void setValue(DomainType t) {
		setSelectedIndex(0);
		for (int i = 0; i < DomainType.values().length; i++) {
			if ( t == DomainType.values()[i]) {
				setSelectedIndex(i);
				break;
			}
		}
	}
	
}
