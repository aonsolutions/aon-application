package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page07 extends ResizeComposite implements RequiresResize , IMod3902015Page {

	interface PageBinder extends UiBinder<Widget, Page07> {
	}

	private static final PageBinder BINDER = GWT.create(PageBinder.class);

	IMod3902015CallBack callback;
	
	@UiField
	DoubleBox box95;
	@UiField
	DoubleBox box96;
	@UiField
	DoubleBox box524;
	@UiField
	DoubleBox box97;
	@UiField
	DoubleBox box98;
	@UiField
	DoubleBox box525;
	@UiField
	DoubleBox box526;

	int domain;
	int year;

	public Page07(Mod3902015 m390) {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		setValue(m390);
	}

	private void setValue(Mod3902015 m390) {
		box95.setValue(m390.getBox95());
		box96.setValue(m390.getBox96());
		box524.setValue(m390.getBox524());
		box97.setValue(m390.getBox97());
		box98.setValue(m390.getBox98());
		box525.setValue(m390.getBox525());
		box526.setValue(m390.getBox526());
	}

	@Override
	public void populate(Mod3902015 mod390) {
		mod390.setBox95(box95.getValue());
		mod390.setBox96(box96.getValue());
		mod390.setBox524(box524.getValue());
		mod390.setBox97(box97.getValue());
		mod390.setBox98(box98.getValue());
		mod390.setBox525(box525.getValue());
		mod390.setBox526(box526.getValue());
	}

	@Override
	public void setCallback(IMod3902015CallBack callback) {
		this.callback = callback;
	}
	
	@Override
	public void refresh(Mod3902015 m390) {
		setValue(m390);
	}
}
