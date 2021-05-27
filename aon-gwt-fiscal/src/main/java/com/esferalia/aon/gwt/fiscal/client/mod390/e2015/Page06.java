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

public class Page06 extends ResizeComposite implements RequiresResize , IMod3902015Page {

	interface PageBinder extends UiBinder<Widget, Page06> {
	}

	private static final PageBinder BINDER = GWT
			.create(PageBinder.class);

	IMod3902015CallBack cbk;

	@UiField
	DoubleBox box658;
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
	DoubleBox box659;
	@UiField
	DoubleBox box93;
	@UiField
	DoubleBox box94;

	public Page06(Mod3902015 m390) {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		box87.setMaxLength(6);
		box87.setVisibleLength(6);
		
		box88.setMaxLength(6);
		box88.setVisibleLength(6);
		
		box89.setMaxLength(6);
		box89.setVisibleLength(6);
		
		box90.setMaxLength(6);
		box90.setVisibleLength(6);
		
		box91.setMaxLength(6);
		box91.setVisibleLength(6);
		
		box84.setEnabled(false);
		box92.setEnabled(false);
		box94.setEnabled(false);
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

	@UiHandler("box87")
	void onChangeBox87 (ChangeEvent event) {
		if (box87.getValue() == null) box87.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox87(box87.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box88")
	void onChangeBox88 (ChangeEvent event) {
		if (box88.getValue() == null) box88.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox88(box88.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box89")
	void onChangeBox89 (ChangeEvent event) {
		if (box89.getValue() == null) box89.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox89(box89.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box90")
	void onChangeBox90 (ChangeEvent event) {
		if (box90.getValue() == null) box90.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox90(box90.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box91")
	void onChangeBox91 (ChangeEvent event) {
		if (box91.getValue() == null) box91.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox91(box91.getValue());
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

	@UiHandler("box93")
	void onChangeBox93 (ChangeEvent event) {
		if (box93.getValue() == null) box93.setValue(0.0,false);
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox93(box93.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	private void setValue(Mod3902015 m390) {
		box658.setValue(m390.getBox658());
		box84.setValue(m390.getBox84());
		box87.setValue(m390.getBox87());
		box88.setValue(m390.getBox88());
		box89.setValue(m390.getBox89());
		box90.setValue(m390.getBox90());
		box91.setValue(m390.getBox91());
		box92.setValue(m390.getBox92());
		box659.setValue(m390.getBox659());
		box93.setValue(m390.getBox93());
		box94.setValue(m390.getBox94());
	}

	@Override
	public void populate(Mod3902015 mod390) {
		mod390.setBox84(box84.getValue());
		mod390.setBox87(box87.getValue());
		mod390.setBox88(box88.getValue());
		mod390.setBox89(box89.getValue());
		mod390.setBox90(box90.getValue());
		mod390.setBox91(box91.getValue());
		mod390.setBox92(box92.getValue());
		mod390.setBox93(box93.getValue());
		mod390.setBox94(box94.getValue());
		if (box87.getValue() != null && box87.getValue() > 0) {
			mod390.setBox658(box658.getValue());
			mod390.setBox659(box659.getValue());
		}
	}
	
	@Override
	public void setCallback(IMod3902015CallBack callback) {
		this.cbk = callback;
	}
	
	@Override
	public void refresh(Mod3902015 m390) {
		if (!AonMathUtils.equals(box658.getValue(), m390.getBox658())) 
			box658.setValue(m390.getBox658(),true,true);
		if (!AonMathUtils.equals(box659.getValue(), m390.getBox659())) 
			box659.setValue(m390.getBox659(),true,true);
		if (!AonMathUtils.equals(box84.getValue(), m390.getBox84())) 
			box84.setValue(m390.getBox84(),true,true);
		if (!AonMathUtils.equals(box92.getValue(), m390.getBox92())) 
			box92.setValue(m390.getBox92(),true,true);
		if (!AonMathUtils.equals(box94.getValue(), m390.getBox94())) 
			box94.setValue(m390.getBox94(),true,true);
	}
}
