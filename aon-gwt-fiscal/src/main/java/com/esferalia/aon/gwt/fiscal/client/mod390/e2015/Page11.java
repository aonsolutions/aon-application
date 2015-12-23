package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page11 extends ResizeComposite implements RequiresResize , IMod3902015Page {

	interface PageBinder extends UiBinder<Widget, Page11> {}

	private static final PageBinder BINDER = GWT.create(PageBinder.class);

	IMod3902015CallBack callback;

	public Page11() {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
	}

	public void setValue(Mod3902015 m390) {
		// TODO Auto-generated method stub
		
	}

	public void populate(Mod3902015 mod390) {
		// TODO Auto-generated method stub
	}
	
	public void setCallback(IMod3902015CallBack callback) {
		this.callback = callback;
	}
	
}
