package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page05 extends ResizeComposite implements RequiresResize, IMod3902015Page {

	interface PageBinder extends UiBinder<Widget, Page05> {
	}

	private static final PageBinder BINDER = GWT
			.create(PageBinder.class);

	private Mod3902015 mod390;
	
	IMod3902015CallBack callback;

	@UiField
	DoubleBox box84;
	
	@UiField
	DoubleBox box85;
	
	@UiField
	DoubleBox box86;
	
	public Page05() {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		box84.setEnabled(false);
		box86.setEnabled(false);
	}
	
	@UiHandler("box85")
	void onChangeBox85 (ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox85(box85.getValue());
				callback.calculateAndRefresh();	
			}
		});
	}
	
	public void setValue(Mod3902015 m390) {
		this.mod390 = m390;
		box84.setValue(this.mod390.getBox84());
		box85.setValue(this.mod390.getBox85());
		box86.setValue(this.mod390.getBox86());
	}
	
	public void populate(Mod3902015 mod390) {
		mod390.setBox84(box84.getValue());
		mod390.setBox85(box85.getValue());
		mod390.setBox86(box86.getValue());
	}

	public void setCallback(IMod3902015CallBack callback) {
		this.callback = callback;
	}
}
