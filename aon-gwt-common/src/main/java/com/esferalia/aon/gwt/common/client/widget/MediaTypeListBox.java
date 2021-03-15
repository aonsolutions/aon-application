package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.MediaType;
import com.google.gwt.user.client.ui.ListBox;

public class MediaTypeListBox extends ListBox {

	public MediaTypeListBox() {
		setWidth("80px");
		for (MediaType d : MediaType.values()) {
			addItem(d.getDescription());	
		}
	}

	public void setValue(MediaType media) {
		setSelectedIndex(media==null?0:media.ordinal());
	}

	public MediaType getValue() {
		return MediaType.values()[getSelectedIndex()];
	}
	
}

