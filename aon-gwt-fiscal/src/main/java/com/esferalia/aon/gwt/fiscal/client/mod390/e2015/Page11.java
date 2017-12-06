package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015.DeductionRegime;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page11 extends ResizeComposite implements RequiresResize , IMod3902015Page {

	interface PageBinder extends UiBinder<Widget, Page11> {}

	private static final PageBinder BINDER = GWT.create(PageBinder.class);

	IMod3902015CallBack callback;

	@UiField
	DoubleBox box139;
	@UiField
	DoubleBox box140;
	@UiField
	DoubleBox box141;
	@UiField
	DoubleBox box142;
	@UiField
	DoubleBox box143;
	@UiField
	DoubleBox box144;
	@UiField
	DoubleBox box145;
	@UiField
	DoubleBox box146;
	@UiField
	DoubleBox box147;
	@UiField
	DoubleBox box148;
	@UiField
	DoubleBox box149;
	@UiField
	DoubleBox box150;
	@UiField
	DoubleBox box151;
	@UiField
	DoubleBox box152;
	@UiField
	DoubleBox box640;
	@UiField
	DoubleBox box153;
	@UiField
	DoubleBox box154;
	@UiField
	DoubleBox box155;

	@UiField
	DoubleBox box156;
	@UiField
	DoubleBox box157;
	@UiField
	DoubleBox box158;
	@UiField
	DoubleBox box159;
	@UiField
	DoubleBox box160;
	@UiField
	DoubleBox box161;
	@UiField
	DoubleBox box162;
	@UiField
	DoubleBox box163;
	@UiField
	DoubleBox box164;
	@UiField
	DoubleBox box165;
	@UiField
	DoubleBox box166;
	@UiField
	DoubleBox box167;
	@UiField
	DoubleBox box168;
	@UiField
	DoubleBox box169;
	@UiField
	DoubleBox box641;
	@UiField
	DoubleBox box170;
	@UiField
	DoubleBox box171;
	@UiField
	DoubleBox box172;

	@UiField
	DoubleBox box173;
	@UiField
	DoubleBox box174;
	@UiField
	DoubleBox box175;
	@UiField
	DoubleBox box176;
	@UiField
	DoubleBox box177;
	@UiField
	DoubleBox box178;
	@UiField
	DoubleBox box179;
	@UiField
	DoubleBox box180;
	@UiField
	DoubleBox box181;
	@UiField
	DoubleBox box182;
	@UiField
	DoubleBox box183;
	@UiField
	DoubleBox box184;
	@UiField
	DoubleBox box185;
	@UiField
	DoubleBox box186;
	@UiField
	DoubleBox box642;
	@UiField
	DoubleBox box187;
	@UiField
	DoubleBox box188;
	@UiField
	DoubleBox box189;

	
		

	public Page11(Mod3902015 m390) {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		setValue(m390);
	}

	private void setValue(Mod3902015 m390) {
		if (m390.getRegime1() == null) m390.setRegime1(new DeductionRegime());
		box139.setValue(m390.getRegime1().getBase1());
		box140.setValue(m390.getRegime1().getQuota1());
		box141.setValue(m390.getRegime1().getBase2());
		box142.setValue(m390.getRegime1().getQuota2());
		box143.setValue(m390.getRegime1().getBase3());
		box144.setValue(m390.getRegime1().getQuota3());
		box145.setValue(m390.getRegime1().getBase4());
		box146.setValue(m390.getRegime1().getQuota4());
		box147.setValue(m390.getRegime1().getBase5());
		box148.setValue(m390.getRegime1().getQuota5());
		box149.setValue(m390.getRegime1().getBase6());
		box150.setValue(m390.getRegime1().getQuota6());
		box151.setValue(m390.getRegime1().getBase7());
		box152.setValue(m390.getRegime1().getQuota7());
		box640.setValue(m390.getRegime1().getBase8());
		box153.setValue(m390.getRegime1().getQuota8());
		box154.setValue(m390.getRegime1().getQuota9());
		box155.setValue(m390.getRegime1().getQuota10());
		
		if (m390.getRegime2() == null) m390.setRegime2(new DeductionRegime());
		box156.setValue(m390.getRegime2().getBase1());
		box157.setValue(m390.getRegime2().getQuota1());
		box158.setValue(m390.getRegime2().getBase2());
		box159.setValue(m390.getRegime2().getQuota2());
		box160.setValue(m390.getRegime2().getBase3());
		box161.setValue(m390.getRegime2().getQuota3());
		box162.setValue(m390.getRegime2().getBase4());
		box163.setValue(m390.getRegime2().getQuota4());
		box164.setValue(m390.getRegime2().getBase5());
		box165.setValue(m390.getRegime2().getQuota5());
		box166.setValue(m390.getRegime2().getBase6());
		box167.setValue(m390.getRegime2().getQuota6());
		box168.setValue(m390.getRegime2().getBase7());
		box169.setValue(m390.getRegime2().getQuota7());
		box641.setValue(m390.getRegime2().getBase8());
		box170.setValue(m390.getRegime2().getQuota8());
		box171.setValue(m390.getRegime2().getQuota9());
		box172.setValue(m390.getRegime2().getQuota10());

		if (m390.getRegime3() == null) m390.setRegime3(new DeductionRegime());
		box173.setValue(m390.getRegime3().getBase1());
		box174.setValue(m390.getRegime3().getQuota1());
		box175.setValue(m390.getRegime3().getBase2());
		box176.setValue(m390.getRegime3().getQuota2());
		box177.setValue(m390.getRegime3().getBase3());
		box178.setValue(m390.getRegime3().getQuota3());
		box179.setValue(m390.getRegime3().getBase4());
		box180.setValue(m390.getRegime3().getQuota4());
		box181.setValue(m390.getRegime3().getBase5());
		box182.setValue(m390.getRegime3().getQuota5());
		box183.setValue(m390.getRegime3().getBase6());
		box184.setValue(m390.getRegime3().getQuota6());
		box185.setValue(m390.getRegime3().getBase7());
		box186.setValue(m390.getRegime3().getQuota7());
		box642.setValue(m390.getRegime3().getBase8());
		box187.setValue(m390.getRegime3().getQuota8());
		box188.setValue(m390.getRegime3().getQuota9());
		box189.setValue(m390.getRegime3().getQuota10());
	}
	
	@Override
	public void refresh(Mod3902015 m390) {
		// TODO Auto-generated method stub
		
	}

	public void populate(Mod3902015 m390) {
		if (m390.getRegime1() == null) m390.setRegime1(new DeductionRegime());
		m390.getRegime1().setBase1(box139.getValue());
		m390.getRegime1().setQuota1(box140.getValue());
		m390.getRegime1().setBase2(box141.getValue());
		m390.getRegime1().setQuota2(box142.getValue());
		m390.getRegime1().setBase3(box143.getValue());
		m390.getRegime1().setQuota3(box144.getValue());
		m390.getRegime1().setBase4(box145.getValue());
		m390.getRegime1().setQuota4(box146.getValue());
		m390.getRegime1().setBase5(box147.getValue());
		m390.getRegime1().setQuota5(box148.getValue());
		m390.getRegime1().setBase6(box149.getValue());
		m390.getRegime1().setQuota6(box150.getValue());
		m390.getRegime1().setBase7(box151.getValue());
		m390.getRegime1().setQuota7(box152.getValue());
		m390.getRegime1().setBase8(box640.getValue());
		m390.getRegime1().setQuota8(box153.getValue());
		m390.getRegime1().setQuota9(box154.getValue());
		m390.getRegime1().setQuota10(box155.getValue());

		if (m390.getRegime2() == null) m390.setRegime2(new DeductionRegime());
		m390.getRegime2().setBase1(box156.getValue());
		m390.getRegime2().setQuota1(box157.getValue());
		m390.getRegime2().setBase2(box158.getValue());
		m390.getRegime2().setQuota2(box159.getValue());
		m390.getRegime2().setBase3(box160.getValue());
		m390.getRegime2().setQuota3(box161.getValue());
		m390.getRegime2().setBase4(box162.getValue());
		m390.getRegime2().setQuota4(box163.getValue());
		m390.getRegime2().setBase5(box164.getValue());
		m390.getRegime2().setQuota5(box165.getValue());
		m390.getRegime2().setBase6(box166.getValue());
		m390.getRegime2().setQuota6(box167.getValue());
		m390.getRegime2().setBase7(box168.getValue());
		m390.getRegime2().setQuota7(box169.getValue());
		m390.getRegime2().setBase8(box641.getValue());
		m390.getRegime2().setQuota8(box170.getValue());
		m390.getRegime2().setQuota9(box171.getValue());
		m390.getRegime2().setQuota10(box172.getValue());

		if (m390.getRegime3() == null) m390.setRegime3(new DeductionRegime());
		m390.getRegime3().setBase1(box173.getValue());
		m390.getRegime3().setQuota1(box174.getValue());
		m390.getRegime3().setBase2(box175.getValue());
		m390.getRegime3().setQuota2(box176.getValue());
		m390.getRegime3().setBase3(box177.getValue());
		m390.getRegime3().setQuota3(box178.getValue());
		m390.getRegime3().setBase4(box179.getValue());
		m390.getRegime3().setQuota4(box180.getValue());
		m390.getRegime3().setBase5(box181.getValue());
		m390.getRegime3().setQuota5(box182.getValue());
		m390.getRegime3().setBase6(box183.getValue());
		m390.getRegime3().setQuota6(box184.getValue());
		m390.getRegime3().setBase7(box185.getValue());
		m390.getRegime3().setQuota7(box186.getValue());
		m390.getRegime3().setBase8(box642.getValue());
		m390.getRegime3().setQuota8(box187.getValue());
		m390.getRegime3().setQuota9(box188.getValue());
		m390.getRegime3().setQuota10(box189.getValue());
	}
	
	public void setCallback(IMod3902015CallBack callback) {
		this.callback = callback;
	}

	
}
