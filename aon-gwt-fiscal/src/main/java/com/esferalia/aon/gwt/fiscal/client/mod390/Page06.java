package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model3902014.IMod3902014CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model3902014.IMod3902014Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
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

public class Page06 extends ResizeComposite implements RequiresResize , IMod3902014Page {

	interface PageBinder extends UiBinder<Widget, Page06> {
	}

	private static final PageBinder BINDER = GWT
			.create(PageBinder.class);

	private Mod3902014 mod390;
	
	IMod3902014CallBack callback;

	@UiField
	DoubleBox box84;
	@UiField
	DoubleBox box87;
	@UiField
	DoubleBox box88;
	@UiField
	DoubleBox box89;
	@UiField
	DoubleBox box90;
	@UiField
	DoubleBox box91;
	@UiField
	DoubleBox box92;
	@UiField
	DoubleBox box93;
	@UiField
	DoubleBox box94;

	public Page06() {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		box87.setValue(100.0);
		box87.setMaxLength(6);
		box87.setVisibleLength(6);
		
		box88.setValue(0.0);
		box88.setMaxLength(6);
		box88.setVisibleLength(6);
		
		box89.setValue(0.0);
		box89.setMaxLength(6);
		box89.setVisibleLength(6);
		
		box90.setValue(0.0);
		box90.setMaxLength(6);
		box90.setVisibleLength(6);
		
		box91.setValue(0.0);
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
				mod390.setBox87(box87.getValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box93")
	void onChangeBox93 (ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox93(box93.getValue());
				callback.calculateAndRefresh();
			}
		});
	}

	public void setValue(Mod3902014 m390) {
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

	public void populate(Mod3902014 mod390) {
		mod390.setBox84(box84.getValue());
		mod390.setBox87(box87.getValue());
		mod390.setBox88(box88.getValue());
		mod390.setBox89(box89.getValue());
		mod390.setBox90(box90.getValue());
		mod390.setBox91(box91.getValue());
		mod390.setBox92(box92.getValue());
		mod390.setBox93(box93.getValue());
		mod390.setBox94(box94.getValue());
	}
	
	public void setCallback(IMod3902014CallBack callback) {
		this.callback = callback;
	}
}
