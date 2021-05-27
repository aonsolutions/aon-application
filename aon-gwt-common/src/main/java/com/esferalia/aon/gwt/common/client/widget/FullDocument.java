package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;

public class FullDocument extends HorizontalPanel implements Focusable {

	
	private DocumentTypeListBox type;
	private CountryListBox country;
	private DocumentTextBox document;
	private InlineLabel unknownIcon;
	private InlineLabel europeIcon;
	private InlineLabel okIcon; 
	private InlineLabel koIcon;
	
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
		add(type);
		
		country = new CountryListBox();
		country.addStyleName(AON.AON_CSS.aonMarginLeft5());
		add(country);
		
		document = new DocumentTextBox( false );
		document.addStyleName(AON.AON_CSS.aonMarginLeft5());
		add(document);
		
		FlowPanel iconPanel = new FlowPanel();
		iconPanel.setStyleName(AON.AON_CSS.aonNowrap());
		
		europeIcon = new InlineLabel();
		europeIcon.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		europeIcon.addStyleName(AON.AON_CSS.aonIconEurope());
		europeIcon.addStyleName(AON.AON_CSS.aonMarginLeft5());
		europeIcon.setVisible(false);
		iconPanel.add(europeIcon);
		
		okIcon = new InlineLabel();
		okIcon.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		okIcon.addStyleName(AON.AON_CSS.aonIconValidate());
		okIcon.addStyleName(AON.AON_CSS.aonMarginLeft5());
		okIcon.setVisible(false);
		iconPanel.add(okIcon);

		koIcon = new InlineLabel();
		koIcon.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		koIcon.addStyleName(AON.AON_CSS.aonIconIncorrect());
		koIcon.addStyleName(AON.AON_CSS.aonMarginLeft5());
		koIcon.setVisible(false);
		iconPanel.add(koIcon);

		unknownIcon = new InlineLabel();
		unknownIcon.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		unknownIcon.addStyleName(AON.AON_CSS.aonIconQuestion());
		unknownIcon.addStyleName(AON.AON_CSS.aonMarginLeft5());
		unknownIcon.setVisible(false);
		iconPanel.add(unknownIcon);
		add(iconPanel);

		type.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				check();
			}
		});
		country.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				check();
			}
		});
		document.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				check();
			}
		});
	}
	
	public void setValue(DocumentType documentType, Country documentCountry,String doc) {
		type.setValue(documentType);
		country.setValue(documentCountry);
		document.setValue(doc);	
		check();
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
	
	private void check() {
		Byte t = type.getValue() == null ? null : type.getValue().value();
		String c = country.getValue() == null ? null : country.getValue().getIso2();
		if (AonDocumentUtil.isValidable(t, c, document.getValue())) {
			boolean valid = AonDocumentUtil.isValid(t, c, document.getValue());	
			europeIcon.setVisible(country.getValue() != null && country.getValue().isIntracommunityCountry());
			okIcon.setVisible(valid);
			koIcon.setVisible(!valid);
			unknownIcon.setVisible(false);
		} else {
			unknownIcon.setVisible(true);
			europeIcon.setVisible(false);
			okIcon.setVisible(false);
			koIcon.setVisible(false);
		}
		
	}
	
	public void addTypeChangeHandler(ChangeHandler handler) {
		type.addChangeHandler(handler);
	}
	public void addCountryChangeHandler(ChangeHandler handler) {
		country.addChangeHandler(handler);
	}
	public void addDocumentChangeHandler(ValueChangeHandler<String> handler) {
		document.addValueChangeHandler(handler);
	}
	
	@Override
	public int getTabIndex() {
		return document.getTabIndex();
	}
	@Override
	public void setAccessKey(char key) {
		document.setAccessKey(key);
		
	}
	@Override
	public void setFocus(boolean focused) {
		document.setFocus(focused);
	}
	@Override
	public void setTabIndex(int index) {
		type.setTabIndex(index);
		country.setTabIndex(index+1);
		document.setTabIndex(index+2);
	}
	
}
