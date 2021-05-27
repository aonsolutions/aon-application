package com.esferalia.aon.gwt.fiscal.client.mod390.e2014;

import com.esferalia.aon.gwt.fiscal.client.mod390.e2014.Model3902014.IMod3902014CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2014.Model3902014.IMod3902014Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page11 extends ResizeComposite implements RequiresResize , IMod3902014Page {

	interface PageBinder extends UiBinder<Widget, Page11> {}

	private static final PageBinder BINDER = GWT.create(PageBinder.class);

	IMod3902014CallBack callback;

	public Page11() {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
	}

	public void setValue(Mod3902014 m390) {
		// TODO Auto-generated method stub
		
	}

	public void populate(Mod3902014 mod390) {
		// TODO Auto-generated method stub
	}
	
	public void setCallback(IMod3902014CallBack callback) {
		this.callback = callback;
	}
	
}
