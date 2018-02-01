package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.watson.util.AonMathUtils;
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

	IMod3902015CallBack cbk;

	@UiField
	DoubleBox box658;

	@UiField
	DoubleBox box84;
	
	@UiField
	DoubleBox box659;

	@UiField
	DoubleBox box85;
	
	@UiField
	DoubleBox box86;
	
	public Page05(Mod3902015 m390) {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		box84.setEnabled(false);
		box86.setEnabled(false);
		setValue(m390);
	}
	
	@UiHandler("box658")
	void onChangeBox658 (ChangeEvent event) {
		if (box658.getValue() == null) box658.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox658(box658.getValue());
				cbk.calculateAndRefresh();	
			}
		});
	}

	@UiHandler("box659")
	void onChangeBox659 (ChangeEvent event) {
		if (box659.getValue() == null) box659.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox659(box659.getValue());
				cbk.calculateAndRefresh();	
			}
		});
	}

	@UiHandler("box85")
	void onChangeBox85 (ChangeEvent event) {
		if (box85.getValue() == null) box85.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox85(box85.getValue());
				cbk.calculateAndRefresh();	
			}
		});
	}
	
	private void setValue(Mod3902015 m390) {
		box658.setValue(m390.getBox658());
		box84.setValue(m390.getBox84());
		box659.setValue(m390.getBox659());
		box85.setValue(m390.getBox85());
		box86.setValue(m390.getBox86());
	}
	
	public void populate(Mod3902015 mod390) {
		mod390.setBox658(box658.getValue());
		mod390.setBox84(box84.getValue());
		mod390.setBox659(box659.getValue());
		mod390.setBox85(box85.getValue());
		mod390.setBox86(box86.getValue());
	}

	public void setCallback(IMod3902015CallBack callback) {
		this.cbk = callback;
	}

	@Override
	public void refresh(Mod3902015 m390) {
		if (!AonMathUtils.equals(box84.getValue(), m390.getBox84())) 
			box84.setValue(m390.getBox84(),true,true);
		if (!AonMathUtils.equals(box86.getValue(), m390.getBox86())) 
			box86.setValue(m390.getBox86(),true,true);
	}
}
