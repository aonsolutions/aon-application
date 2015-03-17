package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

public class FullDocument extends HorizontalPanel {

	
	private DocumentTypeListBox type;
	private CountryListBox country;
	private DocumentTextBox document;
	
	public ListBox getTypeWidget() {
		return type;
	}
	public CountryListBox getCountryWidget() {
		return country;
	}
	public DocumentTextBox getDocumentWidget() {
		return document;
	}

	public FullDocument() {
		type = new DocumentTypeListBox();
		country = new CountryListBox();
		document = new DocumentTextBox();
		add(type);
		add(country);
		add(document);
		
	}
	public void setValue(DocumentType documentType, Country documentCountry,String doc) {
		type.setValue(documentType);
		country.setValue(documentCountry);
		document.setValue(doc);	
	}
	public DocumentType getType() {
		return type.getValue();
	}
	public Country getCountry() {
		return country.getValue();
	}
	public String getDocument() {
		return document.getValue();
	}
	
	public void setEnabled(boolean enabled) {
		type.setEnabled(enabled);
		country.setEnabled(enabled);
		document.setEnabled(enabled);	
	}
	
}
