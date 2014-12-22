package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page11 extends ResizeComposite implements RequiresResize {

	interface Page7Binder extends UiBinder<Widget, Page11> {
	}

	private static final Page7Binder page7Binder = GWT
			.create(Page7Binder.class);

	@UiField
	DoubleTextBox box230;
	
	@UiField
	DoubleTextBox box109;
	
	@UiField
	DoubleTextBox box231;
	
	@UiField
	DoubleTextBox box232;
	
	@UiField
	DoubleTextBox box111;
	
	@UiField
	DoubleTextBox box113;
	
	@UiField
	DoubleTextBox box523;

	public Page11() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		Model390.RESOURCES.css().ensureInjected();

		Widget ui = page7Binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void setValue(Mod390 m390) {
		box230.setValue(m390.getBox230());
		box109.setValue(m390.getBox109());
		box231.setValue(m390.getBox231());
		box232.setValue(m390.getBox232());
		box111.setValue(m390.getBox111());
		box113.setValue(m390.getBox113());
		box523.setValue(m390.getBox523());
	}

	public void populate(Mod390 mod390) {
		mod390.setBox230(box230.getDoubleValue());
		mod390.setBox109(box109.getDoubleValue());
		mod390.setBox231(box231.getDoubleValue());
		mod390.setBox232(box232.getDoubleValue());
		mod390.setBox111(box111.getDoubleValue());
		mod390.setBox113(box113.getDoubleValue());
		mod390.setBox523(box523.getDoubleValue());
	}
	
}
