package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018.IMod3902018CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018.IMod3902018Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
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

public class Page07 extends ResizeComposite implements RequiresResize , IMod3902018Page {

	interface PageBinder extends UiBinder<Widget, Page07> {
	}

	private static final PageBinder BINDER = GWT.create(PageBinder.class);

	IMod3902018CallBack cbk;
	
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
	DoubleBox box662;
	@UiField
	DoubleBox box525;
	@UiField
	DoubleBox box526;

	int domain;
	int year;

	public Page07(Mod3902018 m390) {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		setValue(m390);
	}

	private void setValue(Mod3902018 m390) {
		box95.setValue(m390.getBox95());
		box96.setValue(m390.getBox96());
		box524.setValue(m390.getBox524());
		box97.setValue(m390.getBox97());
		box98.setValue(m390.getBox98());
		box662.setValue(m390.getBox662());
		box525.setValue(m390.getBox525());
		box526.setValue(m390.getBox526());
	}

	@Override
	public void populate(Mod3902018 mod390) {
		mod390.setBox95(box95.getValue());
		mod390.setBox96(box96.getValue());
		mod390.setBox524(box524.getValue());
		mod390.setBox97(box97.getValue());
		mod390.setBox98(box98.getValue());
		mod390.setBox662(box662.getValue());
		mod390.setBox525(box525.getValue());
		mod390.setBox526(box526.getValue());
	}

	@Override
	public void setCallback(IMod3902018CallBack callback) {
		this.cbk = callback;
	}
	
	@Override
	public void refresh(Mod3902018 m390) {
		setValue(m390);
	}
	
	@UiHandler("box95")
	void onChangeBox95 (ChangeEvent event) {
		if (box95.getValue() == null) box95.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox95(box95.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box96")
	void onChangeBox96 (ChangeEvent event) {
		if (box96.getValue() == null) box96.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox96(box96.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box524")
	void onChangeBox524 (ChangeEvent event) {
		if (box524.getValue() == null) box524.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox524(box524.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box97")
	void onChangeBox97 (ChangeEvent event) {
		if (box97.getValue() == null) box97.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox97(box97.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box98")
	void onChangeBox98 (ChangeEvent event) {
		if (box98.getValue() == null) box98.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox98(box98.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box662")
	void onChangeBox662 (ChangeEvent event) {
		if (box662.getValue() == null) box662.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox662(box662.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box525")
	void onChangeBox525 (ChangeEvent event) {
		if (box525.getValue() == null) box525.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox525(box525.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box526")
	void onChangeBox526 (ChangeEvent event) {
		if (box526.getValue() == null) box526.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox526(box526.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
}
