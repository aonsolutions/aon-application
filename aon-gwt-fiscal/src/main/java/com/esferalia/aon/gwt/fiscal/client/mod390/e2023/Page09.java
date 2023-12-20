package com.esferalia.aon.gwt.fiscal.client.mod390.e2023;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2023.Model3902023.Model3902023Callback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page09 extends PageAbs {

	private AonDoubleBox box230 = new AonDoubleBox();
	private AonDoubleBox box109 = new AonDoubleBox();
	private AonDoubleBox box231 = new AonDoubleBox();
	private AonDoubleBox box232 = new AonDoubleBox();
	private AonDoubleBox box111 = new AonDoubleBox();
	private AonDoubleBox box113 = new AonDoubleBox();
	private AonDoubleBox box523 = new AonDoubleBox();
	private AonDoubleBox box654 = new AonDoubleBox();
	private AonDoubleBox box655 = new AonDoubleBox();
	private AonDoubleBox box656 = new AonDoubleBox();
	private AonDoubleBox box657 = new AonDoubleBox();

	Page09(Model3902023Callback callback) {
		super(callback);
		paint();
		setValue();
	}

	@Override
	protected void setValue() {
		box230.setValue(getModel().getBox230());
		box109.setValue(getModel().getBox109());
		box231.setValue(getModel().getBox231());
		box232.setValue(getModel().getBox232());
		box111.setValue(getModel().getBox111());
		box113.setValue(getModel().getBox113());
		box523.setValue(getModel().getBox523());
		box654.setValue(getModel().getBox654());
		box655.setValue(getModel().getBox655());
		box656.setValue(getModel().getBox656());
		box657.setValue(getModel().getBox657());
	}

	private void paint() {
		
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);

		basePanel.add(getTitle(AON.MSG.specificOperations()));

		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);
		
		box230.addValueChangeHandler(event -> {
			if (box230.getValue() == null) box230.setValue(0.0, false);
			getModel().setBox230(box230.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box230Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(230),AON.CSS.aonWidth40())
			.addCell(box230,AON.CSS.aonWidth120());
		
		box109.addValueChangeHandler(event -> {
			if (box109.getValue() == null) box109.setValue(0.0, false);
			getModel().setBox109(box109.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box109Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(109),AON.CSS.aonWidth40())
			.addCell(box109,AON.CSS.aonWidth120());
		
		box231.addValueChangeHandler(event -> {
			if (box231.getValue() == null) box231.setValue(0.0, false);
			getModel().setBox231(box231.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box231Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(231),AON.CSS.aonWidth40())
			.addCell(box231,AON.CSS.aonWidth120());
		
		box232.addValueChangeHandler(event -> {
			if (box232.getValue() == null) box232.setValue(0.0, false);
			getModel().setBox232(box232.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box232Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(232),AON.CSS.aonWidth40())
			.addCell(box232,AON.CSS.aonWidth120());

		box111.addValueChangeHandler(event -> {
			if (box111.getValue() == null) box111.setValue(0.0, false);
			getModel().setBox111(box111.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box111Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(111),AON.CSS.aonWidth40())
			.addCell(box111,AON.CSS.aonWidth120());

		box113.addValueChangeHandler(event -> {
			if (box113.getValue() == null) box113.setValue(0.0, false);
			getModel().setBox113(box113.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box113Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(113),AON.CSS.aonWidth40())
			.addCell(box113,AON.CSS.aonWidth120());

		box523.addValueChangeHandler(event -> {
			if (box523.getValue() == null) box523.setValue(0.0, false);
			getModel().setBox523(box523.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box523Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(523),AON.CSS.aonWidth40())
			.addCell(box523,AON.CSS.aonWidth120());

		
		basePanel.add(getTitle(AON.MSG.accrualRegimeOperations()));
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);

		box654.addValueChangeHandler(event -> {
			if (box654.getValue() == null) box654.setValue(0.0, false);
			getModel().setBox654(box654.getValue());
			markAsDirty();
		});
		box655.addValueChangeHandler(event -> {
			if (box655.getValue() == null) box655.setValue(0.0, false);
			getModel().setBox655(box655.getValue());
			markAsDirty();
		});
		tab1.addRow()
			.addCell(new Label(AON.MSG.accrualRegimeOutputMsg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(654),AON.CSS.aonWidth40())
			.addCell(box654,AON.CSS.aonWidth120())
			.addCell(new AonBoxLabel(655),AON.CSS.aonWidth40())
			.addCell(box655,AON.CSS.aonWidth120())
			;

		box656.addValueChangeHandler(event -> {
			if (box656.getValue() == null) box656.setValue(0.0, false);
			getModel().setBox656(box656.getValue());
			markAsDirty();
		});
		box657.addValueChangeHandler(event -> {
			if (box657.getValue() == null) box657.setValue(0.0, false);
			getModel().setBox657(box657.getValue());
			markAsDirty();
		});
		tab1.addRow()
			.addCell(new Label(AON.MSG.accrualRegimeInputMsg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(656),AON.CSS.aonWidth40())
			.addCell(box656,AON.CSS.aonWidth120())
			.addCell(new AonBoxLabel(657),AON.CSS.aonWidth40())
			.addCell(box657,AON.CSS.aonWidth120())
			;
	}

}
