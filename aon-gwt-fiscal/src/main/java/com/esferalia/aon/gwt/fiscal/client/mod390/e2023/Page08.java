package com.esferalia.aon.gwt.fiscal.client.mod390.e2023;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2023.Model3902023.Model3902023Callback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page08 extends PageAbs {

	private AonDoubleBox box99 = new AonDoubleBox();
	private AonDoubleBox box653 = new AonDoubleBox();
	private AonDoubleBox box103 = new AonDoubleBox();
	private AonDoubleBox box104 = new AonDoubleBox();
	private AonDoubleBox box105 = new AonDoubleBox();
	private AonDoubleBox box110 = new AonDoubleBox();
	private AonDoubleBox box125 = new AonDoubleBox();
	private AonDoubleBox box126 = new AonDoubleBox();
	private AonDoubleBox box127 = new AonDoubleBox();
	private AonDoubleBox box128 = new AonDoubleBox();
	private AonDoubleBox box100 = new AonDoubleBox();
	private AonDoubleBox box101 = new AonDoubleBox();
	private AonDoubleBox box102 = new AonDoubleBox();
	private AonDoubleBox box227 = new AonDoubleBox();
	private AonDoubleBox box228 = new AonDoubleBox();
	private AonDoubleBox box106 = new AonDoubleBox();
	private AonDoubleBox box107 = new AonDoubleBox();
	private AonDoubleBox box108 = new AonDoubleBox();
	
	public Page08(Model3902023Callback callback) {
		super(callback);
		paint();
		setValue();
	}
	
	@Override
	protected void setValue() {
		box99.setValue(getModel().getBox99());
		box653.setValue(getModel().getBox653());
		box103.setValue(getModel().getBox103());
		box104.setValue(getModel().getBox104());
		box105.setValue(getModel().getBox105());
		box110.setValue(getModel().getBox110());
		box125.setValue(getModel().getBox125());
		box126.setValue(getModel().getBox126());
		box127.setValue(getModel().getBox127());
		box128.setValue(getModel().getBox128());
		box100.setValue(getModel().getBox100());
		box101.setValue(getModel().getBox101());
		box102.setValue(getModel().getBox102());
		box227.setValue(getModel().getBox227());
		box228.setValue(getModel().getBox228());
		box106.setValue(getModel().getBox106());
		box107.setValue(getModel().getBox107());
		box108.setValue(getModel().getBox108());
	}

	private void paint() {
		
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);

		basePanel.add(getTitle(AON.MSG.operationsVolume()));

		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);
		
		box99.addValueChangeHandler(event -> {
			if (box99.getValue() == null) box99.setValue(0.0,false);
			getModel().setBox99(box99.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box99Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(99),AON.CSS.aonWidth40())
			.addCell(box99,AON.CSS.aonWidth120());


		box653.addValueChangeHandler(event -> {
			if (box653.getValue() == null) box653.setValue(0.0,false);
			getModel().setBox653(box653.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box653Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(653),AON.CSS.aonWidth40())
			.addCell(box653,AON.CSS.aonWidth120());
		

		box103.addValueChangeHandler(event -> {
			if (box103.getValue() == null) box103.setValue(0.0,false);
			getModel().setBox103(box103.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box103Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(103),AON.CSS.aonWidth40())
			.addCell(box103,AON.CSS.aonWidth120());
		
		
		box104.addValueChangeHandler(event -> {
			if (box104.getValue() == null) box104.setValue(0.0,false);
			getModel().setBox104(box104.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box104Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(104),AON.CSS.aonWidth40())
			.addCell(box104,AON.CSS.aonWidth120());
		
		
		box105.addValueChangeHandler(event -> {
			if (box105.getValue() == null) box105.setValue(0.0,false);
			getModel().setBox105(box105.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box105Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(105),AON.CSS.aonWidth40())
			.addCell(box105,AON.CSS.aonWidth120());
		
		
		box110.addValueChangeHandler(event -> {
			if (box110.getValue() == null) box110.setValue(0.0,false);
			getModel().setBox110(box110.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box110Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(110),AON.CSS.aonWidth40())
			.addCell(box110,AON.CSS.aonWidth120());
		
		
		box125.addValueChangeHandler(event -> {
			if (box125.getValue() == null) box125.setValue(0.0,false);
			getModel().setBox125(box125.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label("Operaciones sujetas con inversi\u00F3n del sujeto pasivo"),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(125),AON.CSS.aonWidth40())
			.addCell(box125,AON.CSS.aonWidth120());

		box126.addValueChangeHandler(event -> {
			if (box126.getValue() == null) box126.setValue(0.0,false);
			getModel().setBox126(box126.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label("OSS. Operaciones no sujetas por reglas de localizaci\u00F3n acogidas a la OSS"),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(126),AON.CSS.aonWidth40())
			.addCell(box126,AON.CSS.aonWidth120());
		
		box127.addValueChangeHandler(event -> {
			if (box127.getValue() == null) box127.setValue(0.0,false);
			getModel().setBox127(box127.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label("OSS. Operaciones sujetas y acogidas a la OSS"),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(127),AON.CSS.aonWidth40())
			.addCell(box127,AON.CSS.aonWidth120());

		box128.addValueChangeHandler(event -> {
			if (box128.getValue() == null) box128.setValue(0.0,false);
			getModel().setBox128(box128.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label("Operaciones intragrupo valoradas conforme a lo dispuesto en los arts. 78 y 79 LIVA"),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(128),AON.CSS.aonWidth40())
			.addCell(box128,AON.CSS.aonWidth120());
		
		box100.addValueChangeHandler(event -> {
			if (box100.getValue() == null) box100.setValue(0.0,false);
			getModel().setBox100(box100.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box100Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(100),AON.CSS.aonWidth40())
			.addCell(box100,AON.CSS.aonWidth120());
		

		box101.addValueChangeHandler(event -> {
			if (box101.getValue() == null) box101.setValue(0.0,false);
			getModel().setBox101(box101.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box101Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(101),AON.CSS.aonWidth40())
			.addCell(box101,AON.CSS.aonWidth120());
		
		
		box102.addValueChangeHandler(event -> {
			if (box102.getValue() == null) box102.setValue(0.0,false);
			getModel().setBox102(box102.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box102Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(102),AON.CSS.aonWidth40())
			.addCell(box102,AON.CSS.aonWidth120());
		
		
		box227.addValueChangeHandler(event -> {
			if (box227.getValue() == null) box227.setValue(0.0,false);
			getModel().setBox227(box227.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box227Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(227),AON.CSS.aonWidth40())
			.addCell(box227,AON.CSS.aonWidth120());
		
		
		box228.addValueChangeHandler(event -> {
			if (box228.getValue() == null) box228.setValue(0.0,false);
			getModel().setBox228(box228.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box228Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(228),AON.CSS.aonWidth40())
			.addCell(box228,AON.CSS.aonWidth120());
		
		
		box106.addValueChangeHandler(event -> {
			if (box106.getValue() == null) box106.setValue(0.0,false);
			getModel().setBox106(box106.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box106Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(106),AON.CSS.aonWidth40())
			.addCell(box106,AON.CSS.aonWidth120());
		
		box107.addValueChangeHandler(event -> {
			if (box107.getValue() == null) box107.setValue(0.0,false);
			getModel().setBox107(box107.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.box107Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(107),AON.CSS.aonWidth40())
			.addCell(box107,AON.CSS.aonWidth120());
		
		box108.setEnabled(false);
		tab.addRow()
			.addCell(new Label(AON.MSG.box108Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(108),AON.CSS.aonWidth40())
			.addCell(box108,AON.CSS.aonWidth120());
	}
	
}
