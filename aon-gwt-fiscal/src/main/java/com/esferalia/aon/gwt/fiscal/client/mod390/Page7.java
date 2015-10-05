package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Mod390CallBack;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
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

public class Page7 extends ResizeComposite implements RequiresResize {

	interface Page7Binder extends UiBinder<Widget, Page7> {
	}

	private static final Page7Binder page7Binder = GWT
			.create(Page7Binder.class);

	private Mod390 mod390;
	
	Mod390CallBack callback;

	@UiField
	DoubleTextBox box84;
	
	@UiField
	DoubleTextBox box85;
	
	@UiField
	DoubleTextBox box86;
	
	public Page7() {
		Widget ui = page7Binder.createAndBindUi(this);
		initWidget(ui);
		box84.setEnabled(false);
		box86.setEnabled(false);
	}
	
	@UiHandler("box85")
	void onChangeBox85 (ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				if (box85.isValidValue()) {
					mod390.setBox85(box85.getDoubleValue());
					callback.calculateAndRefresh();	
				}
			}
		});
	}
	
	public void setValue(Mod390 m390) {
		this.mod390 = m390;
		box84.setValue(this.mod390.getBox84());
		box85.setValue(this.mod390.getBox85());
		box86.setValue(this.mod390.getBox86());
	}
	
	public void populate(Mod390 mod390) {
		mod390.setBox84(box84.getDoubleValue());
		mod390.setBox85(box85.getDoubleValue());
		mod390.setBox86(box86.getDoubleValue());
	}

	public void setCallback(Mod390CallBack callback) {
		this.callback = callback;
	}
}
