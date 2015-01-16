package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.common.client.css.GWTResources;
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

public class Page8 extends ResizeComposite implements RequiresResize {

	interface Page7Binder extends UiBinder<Widget, Page8> {
	}

	private static final Page7Binder page7Binder = GWT
			.create(Page7Binder.class);

	private Mod390 mod390;
	
	Mod390CallBack callback;

	@UiField
	DoubleTextBox box84;
	@UiField
	DoubleTextBox box87;
	@UiField
	DoubleTextBox box88;
	@UiField
	DoubleTextBox box89;
	@UiField
	DoubleTextBox box90;
	@UiField
	DoubleTextBox box91;
	@UiField
	DoubleTextBox box92;
	@UiField
	DoubleTextBox box93;
	@UiField
	DoubleTextBox box94;

	public Page8() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		Model390.RESOURCES.css().ensureInjected();

		Widget ui = page7Binder.createAndBindUi(this);
		initWidget(ui);
		box87.setValue(100);
		box87.setMaxLength(6);
		box87.setVisibleLength(6);
		
		box88.setValue(0);
		box88.setMaxLength(6);
		box88.setVisibleLength(6);
		
		box89.setValue(0);
		box89.setMaxLength(6);
		box89.setVisibleLength(6);
		
		box90.setValue(0);
		box90.setMaxLength(6);
		box90.setVisibleLength(6);
		
		box91.setValue(0);
		box91.setMaxLength(6);
		box91.setVisibleLength(6);
		
		box84.setEnabled(false);
		box92.setEnabled(false);
		box94.setEnabled(false);
	}
	
	@UiHandler("box87")
	void onChangeBox87 (ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				if (box87.isValidValue()) {
					if (box87.getDoubleValue() < 0) {
						box84.setValue(0);	
					}
					if (box87.getDoubleValue() > 100) {
						box84.setValue(100);
					}
					callback.calculateAndRefresh();
				}
			}
		});
	}
	
	@UiHandler("box93")
	void onChangeBox93 (ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				if (box93.isValidValue()) {
					callback.calculateAndRefresh();
				}
			}
		});
	}

	public void setValue(Mod390 m390) {
		this.mod390 = m390;
		box84.setValue(this.mod390.getBox84());
		box87.setValue(this.mod390.getBox87());
		box88.setValue(this.mod390.getBox88());
		box89.setValue(this.mod390.getBox89());
		box90.setValue(this.mod390.getBox90());
		box91.setValue(this.mod390.getBox91());
		box92.setValue(this.mod390.getBox92());
		box93.setValue(this.mod390.getBox93());
		box94.setValue(this.mod390.getBox94());
	}

	public void populate(Mod390 mod390) {
		mod390.setBox84(box84.getDoubleValue());
		mod390.setBox87(box87.getDoubleValue());
		mod390.setBox88(box88.getDoubleValue());
		mod390.setBox89(box89.getDoubleValue());
		mod390.setBox90(box90.getDoubleValue());
		mod390.setBox91(box91.getDoubleValue());
		mod390.setBox92(box92.getDoubleValue());
		mod390.setBox93(box93.getDoubleValue());
		mod390.setBox94(box94.getDoubleValue());
	}
	
	public void setCallback(Mod390CallBack callback) {
		this.callback = callback;
	}
}
