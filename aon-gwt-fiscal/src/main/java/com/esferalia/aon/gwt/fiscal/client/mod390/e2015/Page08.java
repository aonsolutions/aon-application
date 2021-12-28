package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

class Page08 extends ResizeComposite {

	interface PageBinder extends UiBinder<Widget, Page08> {
	}

	private static final PageBinder BINDER = GWT.create(PageBinder.class);

	@UiField
	DoubleBox box99;
	
	@UiField
	DoubleBox box653;

	@UiField
	DoubleBox box103;
	
	@UiField
	DoubleBox box104;
	
	@UiField
	DoubleBox box105;
	
	@UiField
	DoubleBox box110;
	
	@UiField
	DoubleBox box112;
	
	@UiField
	DoubleBox box100;
	
	@UiField
	DoubleBox box101;
	
	@UiField
	DoubleBox box102;
	
	@UiField
	DoubleBox box227;
	
	@UiField
	DoubleBox box228;
	
	@UiField
	DoubleBox box106;
	
	@UiField
	DoubleBox box107;
	
	@UiField
	DoubleBox box108;
	
	public Page08(Mod3902015 m390) {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		box108.setEnabled(false);
		setValue(m390);
		box99.addValueChangeHandler(event -> {
			if (box99.getValue() == null) box99.setValue(0.0,false);
			m390.setBox99(box99.getValue());
			m390.calculate();
		});

		box653.addValueChangeHandler(event -> {
			if (box653.getValue() == null) box653.setValue(0.0,false);
			m390.setBox653(box653.getValue());
			m390.calculate();
		});

		box103.addValueChangeHandler(event -> {
			if (box103.getValue() == null) box103.setValue(0.0,false);
			m390.setBox103(box103.getValue());
			m390.calculate();
		});
		
		box104.addValueChangeHandler(event -> {
			if (box104.getValue() == null) box104.setValue(0.0,false);
			m390.setBox104(box104.getValue());
			m390.calculate();
		});
		
		box105.addValueChangeHandler(event -> {
			if (box105.getValue() == null) box105.setValue(0.0,false);
			m390.setBox105(box105.getValue());
			m390.calculate();
		});
		
		box110.addValueChangeHandler(event -> {
			if (box110.getValue() == null) box110.setValue(0.0,false);
			m390.setBox110(box110.getValue());
			m390.calculate();
		});
		
		box112.addValueChangeHandler(event -> {
			if (box112.getValue() == null) box112.setValue(0.0,false);
			m390.setBox112(box112.getValue());
			m390.calculate();
		});
		
		box100.addValueChangeHandler(event -> {
			if (box100.getValue() == null) box100.setValue(0.0,false);
			m390.setBox100(box100.getValue());
			m390.calculate();
		});

		box101.addValueChangeHandler(event -> {
			if (box101.getValue() == null) box101.setValue(0.0,false);
			m390.setBox101(box101.getValue());
			m390.calculate();
		});
		
		box102.addValueChangeHandler(event -> {
			if (box102.getValue() == null) box102.setValue(0.0,false);
			m390.setBox102(box102.getValue());
			m390.calculate();
		});
		
		box227.addValueChangeHandler(event -> {
			if (box227.getValue() == null) box227.setValue(0.0,false);
			m390.setBox227(box227.getValue());
			m390.calculate();
		});
		
		box228.addValueChangeHandler(event -> {
			if (box228.getValue() == null) box228.setValue(0.0,false);
			m390.setBox228(box228.getValue());
			m390.calculate();
		});
		
		box106.addValueChangeHandler(event -> {
			if (box106.getValue() == null) box106.setValue(0.0,false);
			m390.setBox106(box106.getValue());
			m390.calculate();
		});
		
		box107.addValueChangeHandler(event -> {
			if (box107.getValue() == null) box107.setValue(0.0,false);
			m390.setBox107(box107.getValue());
			m390.calculate();
		});
		
	}

	private void setValue(Mod3902015 m390) {
		box99.setValue(m390.getBox99());
		box653.setValue(m390.getBox653());
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

	public void populate(Mod3902015 mod390) {
		mod390.setBox99(box99.getValue());
		mod390.setBox653(box653.getValue());
		mod390.setBox103(box103.getValue());
		mod390.setBox104(box104.getValue());
		mod390.setBox105(box105.getValue());
		mod390.setBox110(box110.getValue());
		mod390.setBox112(box112.getValue());
		mod390.setBox100(box100.getValue());
		mod390.setBox101(box101.getValue());
		mod390.setBox102(box102.getValue());
		mod390.setBox227(box227.getValue());
		mod390.setBox228(box228.getValue());
		mod390.setBox106(box106.getValue());
		mod390.setBox107(box107.getValue());
		mod390.setBox108(box108.getValue());
	}
	
	public void refresh(Mod3902015 m390) {
		if (!AonMathUtils.equals(box108.getValue(), m390.getBox108())) 
			box108.setValue(m390.getBox108(),true,true);
	}
}

