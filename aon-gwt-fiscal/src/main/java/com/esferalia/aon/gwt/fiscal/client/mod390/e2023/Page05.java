package com.esferalia.aon.gwt.fiscal.client.mod390.e2023;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2023.Model3902023.Model3902023Callback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page05 extends PageAbs {

	private AonDoubleBox box658 = new AonDoubleBox();
	private AonDoubleBox box84 = new AonDoubleBox();
	private AonDoubleBox box659 = new AonDoubleBox();
	private AonDoubleBox box85 = new AonDoubleBox();
	private AonDoubleBox box86 = new AonDoubleBox();
	
	public Page05(Model3902023Callback callback) {
		super(callback);
		paint();
		setValue();
	}

	protected void setValue() {
		box658.setValue(getModel().getBox658(), false);
		box84.setValue(getModel().getBox84(), false);
		box659.setValue(getModel().getBox659(), false);
		box85.setValue(getModel().getBox85(), false);
		box86.setValue(getModel().getBox86(), false);
	}
	
	private void paint() {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);

		Label msg = getTitle(AON.MSG.page7HelpText());
		msg.addStyleName(AON.CSS.aonFontLarger());
		msg.addStyleName(AON.CSS.aonBackgroundLigthBlue());
		msg.addStyleName(AON.CSS.aonTextCenter());
		msg.addStyleName(AON.CSS.aonBorder());
		basePanel.add(msg);
		
		basePanel.add(getTitle(AON.MSG.annualLiquidationResult()));

		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);
		
		box658.addValueChangeHandler(event -> {
			if (box658.getValue() == null) box658.setValue(0.0,false);
			getModel().setBox658(box658.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.regQuotaArt80()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(658),AON.CSS.aonWidth40())
			.addCell(box658,AON.CSS.aonWidth120());

		
		box84.setEnabled(false);
		tab.addRow()
			.addCell(new Label(AON.MSG.resultSum()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(84),AON.CSS.aonWidth40())
			.addCell(box84,AON.CSS.aonWidth120());

		box659.addValueChangeHandler(event -> {
			if (box659.getValue() == null) box659.setValue(0.0,false);
			getModel().setBox659(box659.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.importIVACustoms()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(659),AON.CSS.aonWidth40())
			.addCell(box659,AON.CSS.aonWidth120());
		
		box85.addValueChangeHandler(event -> {
			if (box85.getValue() == null) box85.setValue(0.0,false);
			getModel().setBox85(box85.getValue());
			calculateAndRefresh();
			markAsDirty();
		});	
		tab.addRow()
			.addCell(new Label(AON.MSG.previousYearCompensation()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(85),AON.CSS.aonWidth40())
			.addCell(box85,AON.CSS.aonWidth120());

		box86.setEnabled(false);
		tab.addRow()
			.addCell(new Label(AON.MSG.liquidationResult()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(86),AON.CSS.aonWidth40())
			.addCell(box86,AON.CSS.aonWidth120());
		
		
	}
}
