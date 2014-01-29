package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.fiscal.client.css.AonResources;
import com.esferalia.aon.gwt.fiscal.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.FiscalEnum.Mod390DetailKey;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.google.gwt.core.client.GWT;
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

	private static final AonResources RESOURCES = GWT
			.create(AonResources.class);
	
	Page5 page5;
	Page6 page6;
	
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
		RESOURCES.css().ensureInjected();

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
	
	public void setPage5(Page5 page5) {
		this.page5 = page5;
	}
	public void setPage6(Page6 page6) {
		this.page6 = page6;
	}

	@UiHandler("box87")
	void onChangeBox87 (ChangeEvent event) {
		if (box87.getDoubleValue() < 0) {
			box84.setValue(0);	
		}
		if (box87.getDoubleValue() > 100) {
			box84.setValue(100);
		}
		refresh();
	}
	
	@UiHandler("box93")
	void onChangeBox93 (ChangeEvent event) {
		refresh();
	}

	public void refresh() {
		double k37 = page5.getQuotaMap().get(Mod390DetailKey.K37).getDoubleValue();
		double box83 = page6.getBox83();
		double bx84 = AonUtil.round(k37 + box83);
		box84.setValue(bx84);
		
		box92.setValue(AonUtil.round(bx84 * box87.getDoubleValue() / 100));
		box94.setValue(AonUtil.round(box92.getDoubleValue() - box93.getDoubleValue()));
	}

	public void setValue(Mod390 m390) {
		box84.setValue(m390.getBox84());
		box87.setValue(m390.getBox87());
		box88.setValue(m390.getBox88());
		box89.setValue(m390.getBox89());
		box90.setValue(m390.getBox90());
		box91.setValue(m390.getBox91());
		box92.setValue(m390.getBox92());
		box93.setValue(m390.getBox93());
		box94.setValue(m390.getBox94());
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
	
}
