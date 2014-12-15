package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.Mod303Results;
import com.esferalia.aon.occam.api.model.Mod390;
import com.esferalia.aon.occam.api.model.Mod390.Mod390Detail;
import com.esferalia.aon.occam.api.model.Mod390.Mod390DetailKey;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page10 extends ResizeComposite implements RequiresResize {

	interface Page7Binder extends UiBinder<Widget, Page10> {
	}

	private static final Page7Binder page7Binder = GWT
			.create(Page7Binder.class);

	private static final AonResources RESOURCES = GWT
			.create(AonResources.class);
	
	@UiField
	DoubleTextBox box99;
	
	@UiField
	DoubleTextBox box103;
	
	@UiField
	DoubleTextBox box104;
	
	@UiField
	DoubleTextBox box105;
	
	@UiField
	DoubleTextBox box110;
	
	@UiField
	DoubleTextBox box112;
	
	@UiField
	DoubleTextBox box100;
	
	@UiField
	DoubleTextBox box101;
	
	@UiField
	DoubleTextBox box102;
	
	@UiField
	DoubleTextBox box227;
	
	@UiField
	DoubleTextBox box228;
	
	@UiField
	DoubleTextBox box106;
	
	@UiField
	DoubleTextBox box107;
	
	@UiField
	DoubleTextBox box108;
	
	public Page10() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = page7Binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void fillBox(Mod390Detail detail) {
		if (detail.getKey() == Mod390DetailKey.B099) {
			box99.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B100) {
			box100.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B101) {
			box101.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B102) {
			box102.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B103) {
			box103.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B104) {
			box104.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B105) {
			box105.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B106) {
			box106.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B107) {
			box107.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B108) {
			box108.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B110) {
			box110.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B112) {
			box112.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B227) {
			box227.setValue(detail.getTaxableBase()); 
		} else if (detail.getKey() == Mod390DetailKey.B228) {
			box228.setValue(detail.getTaxableBase()); 
		}
		
	}

	public void setValue(Mod390 m390) {
		box99.setValue(m390.getBox99());
		box103.setValue(m390.getBox103());
		box104.setValue(m390.getBox104());
		box105.setValue(m390.getBox105());
		box110.setValue(m390.getBox110());
		box112.setValue(m390.getBox112());
		box100.setValue(m390.getBox100());
		box101.setValue(m390.getBox101());
		box102.setValue(m390.getBox102());
		box227.setValue(m390.getBox227());
		box228.setValue(m390.getBox228());
		box106.setValue(m390.getBox106());
		box107.setValue(m390.getBox107());
		box108.setValue(m390.getBox108());
		
	}

	public void populate(Mod390 mod390) {
		mod390.setBox99(box99.getDoubleValue());
		mod390.setBox103(box103.getDoubleValue());
		mod390.setBox104(box104.getDoubleValue());
		mod390.setBox105(box105.getDoubleValue());
		mod390.setBox110(box110.getDoubleValue());
		mod390.setBox112(box112.getDoubleValue());
		mod390.setBox100(box100.getDoubleValue());
		mod390.setBox101(box101.getDoubleValue());
		mod390.setBox102(box102.getDoubleValue());
		mod390.setBox227(box227.getDoubleValue());
		mod390.setBox228(box228.getDoubleValue());
		mod390.setBox106(box106.getDoubleValue());
		mod390.setBox107(box107.getDoubleValue());
		mod390.setBox108(box108.getDoubleValue());
	}
	
	@UiHandler("box99")
	void onChangeBox99(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("box103")
	void onChangeBox103(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("box104")
	void onChangeBox104(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("box105")
	void onChangeBox105(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("box110")
	void onChangeBox110(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("box112")
	void onChangeBox112(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("box100")
	void onChangeBox100(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("box101")
	void onChangeBox101(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("box102")
	void onChangeBox102(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("box227")
	void onChangeBox227(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("box228")
	void onChangeBox228(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("box106")
	void onChangeBox106(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("box107")
	void onChangeBox107(ChangeEvent event) {
		refresh();
	}

	private void refresh() {
		box108.setValue( 
				AonUtil.round(box99.getDoubleValue()
					+box103.getDoubleValue()
					+box104.getDoubleValue()
					+box105.getDoubleValue()
					+box110.getDoubleValue()
					+box112.getDoubleValue()
					+box100.getDoubleValue()
					+box101.getDoubleValue()
					+box102.getDoubleValue()
					+box227.getDoubleValue()
					+box228.getDoubleValue()
					-box106.getDoubleValue()
					-box107.getDoubleValue()));
	}

	public void setValue(Mod303Results result) {
		box99.setValue(result.getNationalSales());
		box102.setValue(result.getReSales());
		box103.setValue(result.getIntracommunitarySales());
		box104.setValue(result.getExtracommunitarySales());
		box105.setValue(result.getWithoutRightSales());
		box107.setValue(result.getInvestmentSales());
		box110.setValue(result.getISPSales());
		refresh();
	}
	
}
