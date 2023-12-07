package com.esferalia.aon.gwt.fiscal.client.mod390.e2023;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2023.Model3902023.Model3902023Callback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page07 extends PageAbs {

	private AonDoubleBox box95 = new AonDoubleBox();
	private AonDoubleBox box96 = new AonDoubleBox();
	private AonDoubleBox box524 = new AonDoubleBox();
	private AonDoubleBox box97 = new AonDoubleBox();
	private AonDoubleBox box98 = new AonDoubleBox();
	private AonDoubleBox box662 = new AonDoubleBox();
	private AonDoubleBox box525 = new AonDoubleBox();
	private AonDoubleBox box526 = new AonDoubleBox();

	public Page07(Model3902023Callback callback) {
		super(callback);
		setValue();
		paint();
	}

	@Override
	protected void setValue() {
		box95.setValue(getModel().getBox95());
		box96.setValue(getModel().getBox96());
		box524.setValue(getModel().getBox524());
		box97.setValue(getModel().getBox97());
		box98.setValue(getModel().getBox98());
		box662.setValue(getModel().getBox662());
		box525.setValue(getModel().getBox525());
		box526.setValue(getModel().getBox526());
	}

	private void paint() {
	
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);

		basePanel.add(getTitle(AON.MSG.commonRegimePeriods()));

		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);

		box95.addValueChangeHandler( event -> {
			if (box95.getValue() == null) box95.setValue(0.0,false);
			getModel().setBox95(box95.getValue());
			getModel().calculate();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.depositDeclarationsResult()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new AonBoxLabel(95),AON.CSS.aonWidth40())
			.addCell(box95,AON.CSS.aonWidth120());

		box96.addValueChangeHandler( event -> {
			if (box96.getValue() == null) box96.setValue(0.0,false);
			getModel().setBox96(box96.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.paybacksTotal()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new AonBoxLabel(96),AON.CSS.aonWidth40())
			.addCell(box96,AON.CSS.aonWidth120());
		
		box524.addValueChangeHandler( event -> {
			if (box524.getValue() == null) box524.setValue(0.0,false);
			getModel().setBox524(box524.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.paybackTransportTotal()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new AonBoxLabel(524),AON.CSS.aonWidth40())
			.addCell(box524,AON.CSS.aonWidth120());
		
		box97.addValueChangeHandler( event -> {
			if (box97.getValue() == null) box97.setValue(0.0,false);
			getModel().setBox97(box97.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.lastDeclarationResult()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new Label(AON.MSG.toCompensate()),AON.CSS.aonWidth120())
			.addCell(new AonBoxLabel(97),AON.CSS.aonWidth40())
			.addCell(box97,AON.CSS.aonWidth120());
		
		box98.addValueChangeHandler( event -> {
			if (box98.getValue() == null) box98.setValue(0.0,false);
			getModel().setBox98(box98.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new Label(AON.MSG.toPayback()),AON.CSS.aonWidth120())
			.addCell(new AonBoxLabel(98),AON.CSS.aonWidth40())
			.addCell(box98,AON.CSS.aonWidth120());
		
		box662.addValueChangeHandler( event -> {
			if (box662.getValue() == null) box662.setValue(0.0,false);
			getModel().setBox662(box662.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.pendingQuotes()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new AonBoxLabel(662),AON.CSS.aonWidth40())
			.addCell(box662,AON.CSS.aonWidth120());

		basePanel.add(getTitle(AON.MSG.entityGroupRegimePeriods()));

		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		box525.addValueChangeHandler( event -> {
			if (box525.getValue() == null) box525.setValue(0.0,false);
			getModel().setBox525(box525.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab1.addRow()
			.addCell(new Label(AON.MSG.mod322PositiveResults()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(525),AON.CSS.aonWidth40())
			.addCell(box525,AON.CSS.aonWidth120());

		box526.addValueChangeHandler( event -> {
			if (box526.getValue() == null) box526.setValue(0.0,false);
			getModel().setBox526(box526.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab1.addRow()
			.addCell(new Label(AON.MSG.mod322NegativeResults()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(526),AON.CSS.aonWidth40())
			.addCell(box526,AON.CSS.aonWidth120());
	}
}

