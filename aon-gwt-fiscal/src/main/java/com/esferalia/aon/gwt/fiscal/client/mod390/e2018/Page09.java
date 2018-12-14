package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018.IMod3902018CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018.IMod3902018Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page09 extends ResizeComposite implements RequiresResize , IMod3902018Page {

	interface PageBinder extends UiBinder<Widget, Page09> {}

	private static final PageBinder BINDER = GWT.create(PageBinder.class);

	IMod3902018CallBack callback;
	
	@UiField
	DoubleBox box230;
	
	@UiField
	DoubleBox box109;
	
	@UiField
	DoubleBox box231;
	
	@UiField
	DoubleBox box232;
	
	@UiField
	DoubleBox box111;
	
	@UiField
	DoubleBox box113;
	
	@UiField
	DoubleBox box523;

	@UiField
	DoubleBox box654;
	
	@UiField
	DoubleBox box655;
	
	@UiField
	DoubleBox box656;
	
	@UiField
	DoubleBox box657;

	public Page09(Mod3902018 m390) {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		setValue(m390);
	}

	private void setValue(Mod3902018 m390) {
		box230.setValue(m390.getBox230());
		box109.setValue(m390.getBox109());
		box231.setValue(m390.getBox231());
		box232.setValue(m390.getBox232());
		box111.setValue(m390.getBox111());
		box113.setValue(m390.getBox113());
		box523.setValue(m390.getBox523());
		box654.setValue(m390.getBox654());
		box655.setValue(m390.getBox655());
		box656.setValue(m390.getBox656());
		box657.setValue(m390.getBox657());
	}

	@Override
	public void populate(Mod3902018 mod390) {
		mod390.setBox230(box230.getValue());
		mod390.setBox109(box109.getValue());
		mod390.setBox231(box231.getValue());
		mod390.setBox232(box232.getValue());
		mod390.setBox111(box111.getValue());
		mod390.setBox113(box113.getValue());
		mod390.setBox523(box523.getValue());
		mod390.setBox654(box654.getValue());
		mod390.setBox655(box655.getValue());
		mod390.setBox656(box656.getValue());
		mod390.setBox657(box657.getValue());
	}
	
	@Override
	public void setCallback(IMod3902018CallBack callback) {
		this.callback = callback;
	}

	@Override
	public void refresh(Mod3902018 m390) {
		setValue(m390);
	}
	
	@UiHandler("box230") void onChangeBox230(ChangeEvent event) {	if (box230.getValue() == null) box230.setValue(0.0,false);}
	@UiHandler("box109") void onChangeBox109(ChangeEvent event) {	if (box109.getValue() == null) box109.setValue(0.0,false);}
	@UiHandler("box231") void onChangeBox231(ChangeEvent event) {	if (box231.getValue() == null) box231.setValue(0.0,false);}
	@UiHandler("box232") void onChangeBox232(ChangeEvent event) {	if (box232.getValue() == null) box232.setValue(0.0,false);}
	@UiHandler("box111") void onChangeBox111(ChangeEvent event) {	if (box111.getValue() == null) box111.setValue(0.0,false);}
	@UiHandler("box113") void onChangeBox113(ChangeEvent event) {	if (box113.getValue() == null) box113.setValue(0.0,false);}
	@UiHandler("box523") void onChangeBox523(ChangeEvent event) {	if (box523.getValue() == null) box523.setValue(0.0,false);}
	@UiHandler("box654") void onChangeBox654(ChangeEvent event) {	if (box654.getValue() == null) box654.setValue(0.0,false);}
	@UiHandler("box655") void onChangeBox655(ChangeEvent event) {	if (box655.getValue() == null) box655.setValue(0.0,false);}
	@UiHandler("box656") void onChangeBox656(ChangeEvent event) {	if (box656.getValue() == null) box656.setValue(0.0,false);}
	@UiHandler("box657") void onChangeBox657(ChangeEvent event) {	if (box657.getValue() == null) box657.setValue(0.0,false);}
	

}
