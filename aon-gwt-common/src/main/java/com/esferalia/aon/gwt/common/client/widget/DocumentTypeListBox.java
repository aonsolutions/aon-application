package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

public class DocumentTypeListBox extends ListBox {
	private static final CommonMessages MSG = GWT.create(CommonMessages.class);

	public DocumentTypeListBox() {
		setWidth("50px");
		addItem("","------");
		for (DocumentType d : DocumentType.values()) {
			addItem(d.getDescription(),MSG.documentType( d ));	
		}
	}

	public void setValue(DocumentType documentType) {
		setSelectedIndex(documentType==null?0:documentType.ordinal()+1);
	}

	public DocumentType getValue() {
		return getSelectedIndex()==0?null:DocumentType.values()[getSelectedIndex()-1];
	}
	
}

