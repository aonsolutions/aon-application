package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Mod390CallBack;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
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

	Mod390CallBack callback;
	
	@UiField
	DoubleBox box230;
	
	@UiField
	DoubleBox box109;
	
	@UiField
	DoubleBox box231;
	
	@UiField
	DoubleBox box232;
	
	@UiField
	DoubleBox box111;
	
	@UiField
	DoubleBox box113;
	
	@UiField
	DoubleBox box523;

	@UiField
	DoubleBox box654;
	
	@UiField
	DoubleBox box655;
	
	@UiField
	DoubleBox box656;
	
	@UiField
	DoubleBox box657;

	public Page11() {
		Widget ui = page7Binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void setValue(Mod3902014 m390) {
		box230.setValue(m390.getBox230());
		box109.setValue(m390.getBox109());
		box231.setValue(m390.getBox231());
		box232.setValue(m390.getBox232());
		box111.setValue(m390.getBox111());
		box113.setValue(m390.getBox113());
		box523.setValue(m390.getBox523());
		box654.setValue(m390.getBox654());
		box655.setValue(m390.getBox655());
		box656.setValue(m390.getBox656());
		box657.setValue(m390.getBox657());
	}

	public void populate(Mod3902014 mod390) {
		mod390.setBox230(box230.getValue());
		mod390.setBox109(box109.getValue());
		mod390.setBox231(box231.getValue());
		mod390.setBox232(box232.getValue());
		mod390.setBox111(box111.getValue());
		mod390.setBox113(box113.getValue());
		mod390.setBox523(box523.getValue());
		mod390.setBox654(box654.getValue());
		mod390.setBox655(box655.getValue());
		mod390.setBox656(box656.getValue());
		mod390.setBox657(box657.getValue());
	}
	
	public void setCallback(Mod390CallBack callback) {
		this.callback = callback;
	}

}
