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

public class Page7 extends ResizeComposite implements RequiresResize {

	interface Page7Binder extends UiBinder<Widget, Page7> {
	}

	private static final Page7Binder page7Binder = GWT
			.create(Page7Binder.class);

	private static final AonResources RESOURCES = GWT
			.create(AonResources.class);
	
	Page5 page5;
	
	@UiField
	DoubleTextBox box84;
	
	@UiField
	DoubleTextBox box85;
	
	@UiField
	DoubleTextBox box86;
	
	public Page7() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = page7Binder.createAndBindUi(this);
		initWidget(ui);
		box84.setEnabled(false);
		box86.setEnabled(false);
	}
	public void setPage5(Page5 page5) {
		this.page5 = page5;
	}
	
	@UiHandler("box85")
	void onChangeBox85 (ChangeEvent event) {
		double b84 = box84.getDoubleValue();
		double b85 = box85.getDoubleValue();
		box86.setValue(AonUtil.round(b84 + b85));
	}
	
	public void refresh() {
		double d = page5.getQuotaMap().get(Mod390DetailKey.K37).getDoubleValue();
		box84.setValue(d);
		double b85 = box85.getDoubleValue();
		box86.setValue(AonUtil.round(d + b85));
	}
	public void setValue(Mod390 m390) {
		box84.setValue(m390.getBox84());
		box85.setValue(m390.getBox85());
		box86.setValue(m390.getBox86());
	}
	public void populate(Mod390 mod390) {
		mod390.setBox84(box84.getDoubleValue());
		mod390.setBox85(box85.getDoubleValue());
		mod390.setBox86(box86.getDoubleValue());
	}
}
