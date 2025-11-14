// RESULTADO DE LA LIQUIDACIÓN ANUAL Y DE LAS AUTOLIQUIDACIONES
package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod425.e2025.Model4252025.Model4252025Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025Description;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page05 extends PageAbs {
	
	private AonDoubleBox box112 = new AonDoubleBox();
	private AonDoubleBox box113 = new AonDoubleBox();
	private AonDoubleBox box114 = new AonDoubleBox();
	private AonDoubleBox box115 = new AonDoubleBox();
	private AonDoubleBox box116 = new AonDoubleBox();
	private AonDoubleBox box117 = new AonDoubleBox();
	private AonDoubleBox box118 = new AonDoubleBox();
	private AonDoubleBox box119 = new AonDoubleBox();
	
	public Page05(Model4252025Callback callback) {
		super(callback);
		paint();
		setValue();
	}

	protected void setValue() {
		box112.setValue(getModel().getBox112(), false);
		box113.setValue(getModel().getBox113(), false);
		box114.setValue(getModel().getBox114(), false);
		box115.setValue(getModel().getBox115(), false);
		box116.setValue(getModel().getBox116(), false);
		box117.setValue(getModel().getBox117(), false);
		box118.setValue(getModel().getBox118(), false);
		box119.setValue(getModel().getBox119(), false);
	}
	
	private void paint() {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);
		
		box112.addValueChangeHandler(event -> {
			if (box112.getValue() == null) box112.setValue(0.0,false);
			getModel().setBox112(box112.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		box114.addValueChangeHandler(event -> {
			if (box114.getValue() == null) box114.setValue(0.0,false);
			getModel().setBox114(box114.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		box116.addValueChangeHandler(event -> {
			if (box116.getValue() == null) box116.setValue(0.0,false);
			getModel().setBox116(box116.getValue());
			markAsDirty();
		});
		
		box117.addValueChangeHandler(event -> {
			if (box117.getValue() == null) box117.setValue(0.0,false);
			getModel().setBox117(box117.getValue());
			markAsDirty();
		});
		
		box118.addValueChangeHandler(event -> {
			if (box118.getValue() == null) box118.setValue(0.0,false);
			getModel().setBox118(box118.getValue());
			markAsDirty();
		});
		
		box119.addValueChangeHandler(event -> {
			if (box119.getValue() == null) box119.setValue(0.0,false);
			getModel().setBox119(box119.getValue());
			markAsDirty();
		});
		
		// Casillas deshabilitadas
		box113.setEnabled(false);
		box115.setEnabled(false);
		
		// Resultado de la liquidación anual

		AonDisplayTable tab1 = addTable(basePanel, getTitle(AON.MSG.annualLiquidationResult()));
		addRow(tab1, Mod4252025Description.BOX_112_TEXT, 112, box112);
		addRow(tab1, Mod4252025Description.BOX_113_TEXT, 113, box113);
		addRow(tab1, Mod4252025Description.BOX_114_TEXT, 114, box114);
		addRow(tab1, Mod4252025Description.BOX_115_TEXT, 115, box115);
		
		// Resultado de las autoliquidaciones
		
		AonDisplayTable tab2 = addTable(basePanel, getTitle("Resultado de las autoliquidaciones"));
		addRow(tab2, Mod4252025Description.BOX_116_TEXT, 116, box116);
		addRow(tab2, Mod4252025Description.BOX_117_TEXT, 117, box117);
		addRow(tab2, Mod4252025Description.BOX_118_TEXT, 118, box118);
		addRow(tab2, Mod4252025Description.BOX_119_TEXT, 119, box119);
		
	}
	
}
