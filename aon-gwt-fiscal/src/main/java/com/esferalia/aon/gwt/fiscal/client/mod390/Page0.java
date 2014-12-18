package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.occam.api.model.Mod390;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Page0 extends ResizeComposite {

	@UiField
	DocumentTextBox document;
	
	@UiField
	TextBox name;
	
	@UiField
	TextBox firstSurname;
	
	@UiField
	TextBox secondSurname;
	
	@UiField
	TextBox phone;
	
	interface Page1Binder extends
			UiBinder<Widget, Page0> {
	}

	private static final Page1Binder page1Binder = GWT
			.create(Page1Binder.class);

	private final static AonResources RESOURCES = GWT.create(AonResources.class);
	
	public Page0() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		
		Widget ui = page1Binder.createAndBindUi(this);
		initWidget(ui);
		document.setEnabled(false);
	}

	public void setValue(Mod390 m390) {
		document.setValue(m390.getDocument());
		name.setValue(m390.getName());
		if (m390.isLegalEntity()) {
			name.setMaxLength(37);
			firstSurname.setValue(null);
			secondSurname.setValue(null);
			firstSurname.setEnabled(false);
			secondSurname.setEnabled(false);
		} else {
			name.setMaxLength(15);
			firstSurname.setValue(m390.getFirstSurname());
			secondSurname.setValue(m390.getSecondSurname());
			firstSurname.setEnabled(true);
			secondSurname.setEnabled(true);
		}
		phone.setValue(m390.getContactPhone());
	}

	public void populate(Mod390 mod390) {
		mod390.setName(name.getValue());
		mod390.setFirstSurname(firstSurname.getValue());
		mod390.setSecondSurname(secondSurname.getValue());
		mod390.setContactPhone(phone.getValue());
	}

}
