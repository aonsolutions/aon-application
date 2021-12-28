package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

class Page06 extends ResizeComposite {

	interface PageBinder extends UiBinder<Widget, Page06> {
	}

	private static final PageBinder BINDER = GWT
			.create(PageBinder.class);

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
	
		box658.addValueChangeHandler(event -> {
			if (box658.getValue() == null) box658.setValue(0.0,false);
			m390.setBox658(box658.getValue());
			m390.calculate();
		});

		box87.addValueChangeHandler(event -> {
			if (box87.getValue() == null) box87.setValue(0.0,false);
			m390.setBox87(box87.getValue());
			m390.calculate();
		});
		
		box88.addValueChangeHandler(event -> {
			if (box88.getValue() == null) box88.setValue(0.0,false);
			m390.setBox88(box88.getValue());
			m390.calculate();
		});
		
		box89.addValueChangeHandler(event -> {
			if (box89.getValue() == null) box89.setValue(0.0,false);
			m390.setBox89(box89.getValue());
			m390.calculate();
		});

		box90.addValueChangeHandler(event -> {
			if (box90.getValue() == null) box90.setValue(0.0,false);
			m390.setBox90(box90.getValue());
			m390.calculate();
		});
		
		box91.addValueChangeHandler(event -> {
			if (box91.getValue() == null) box91.setValue(0.0,false);
			m390.setBox91(box91.getValue());
			m390.calculate();
		});

		box659.addValueChangeHandler(event -> {
			if (box659.getValue() == null) box659.setValue(0.0,false);
			m390.setBox659(box659.getValue());
			m390.calculate();
		});

		box93.addValueChangeHandler(event -> {
			if (box93.getValue() == null) box93.setValue(0.0,false);
			m390.setBox93(box93.getValue());
			m390.calculate();
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
