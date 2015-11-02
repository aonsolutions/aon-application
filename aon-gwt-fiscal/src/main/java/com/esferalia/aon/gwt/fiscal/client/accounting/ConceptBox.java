package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

class ConceptBox extends TextBox {

	public ConceptBox(final AccountEntryDetail aed) {
		super();
		setVisibleLength(20);
		setMaxLength(32);
		setStyleName(AON.AON_CSS.aonInputText());
		setValue(aed.getConcept());
		
		addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				aed.setConcept(getValue());
			}
		});
	}
	
}
