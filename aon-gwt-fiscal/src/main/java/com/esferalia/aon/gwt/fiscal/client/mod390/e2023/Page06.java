package com.esferalia.aon.gwt.fiscal.client.mod390.e2023;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2023.Model3902023.Model3902023Callback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page06 extends PageAbs {

	private AonDoubleBox box658 = new AonDoubleBox();
	private AonDoubleBox box84 = new AonDoubleBox();
	private AonDoubleBox box87 = new AonDoubleBox();
	private AonDoubleBox box88 = new AonDoubleBox();
	private AonDoubleBox box89 = new AonDoubleBox();
	private AonDoubleBox box90 = new AonDoubleBox();
	private AonDoubleBox box91 = new AonDoubleBox();
	private AonDoubleBox box92 = new AonDoubleBox();
	private AonDoubleBox box659 = new AonDoubleBox();
	private AonDoubleBox box93 = new AonDoubleBox();
	private AonDoubleBox box94 = new AonDoubleBox();

	public Page06(Model3902023Callback callback) {
		super(callback);
		paint();
		setValue();
	}

	@Override
	protected void setValue() {
		box658.setValue(getModel().getBox658(), false);
		box84.setValue(getModel().getBox84(), false);
		box87.setValue(getModel().getBox87(), false);
		box88.setValue(getModel().getBox88(), false);
		box89.setValue(getModel().getBox89(), false);
		box90.setValue(getModel().getBox90(), false);
		box91.setValue(getModel().getBox91(), false);
		box92.setValue(getModel().getBox92(), false);
		box659.setValue(getModel().getBox659(), false);
		box93.setValue(getModel().getBox93(), false);
		box94.setValue(getModel().getBox94(), false);
	}

	private void paint() {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);

		Label msg = getTitle(AON.MSG.page8HelpText());
		msg.addStyleName(AON.CSS.aonFontLarger());
		msg.addStyleName(AON.CSS.aonBackgroundLigthBlue());
		msg.addStyleName(AON.CSS.aonTextCenter());
		msg.addStyleName(AON.CSS.aonBorder());
		basePanel.add(msg);
		
		basePanel.add(getTitle(AON.MSG.taxByTerritory()));

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
			.addCell(new AonBoxLabel(64),AON.CSS.aonWidth40())
			.addCell(box84,AON.CSS.aonWidth120());
		
		box87.setMaxLength(6);
		box87.setVisibleLength(6);
		box87.addValueChangeHandler(event -> {
			if (box87.getValue() == null) box87.setValue(0.0,false);
			getModel().setBox87(box87.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		FlowPanel pp1 = new FlowPanel();
		pp1.add(box87);
		pp1.add(new InlineLabel("%"));
		tab.addRow()
			.addCell(new Label(AON.MSG.commonTerritory()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto(), AON.CSS.aonTextRight())
			.addCell(new AonBoxLabel(87),AON.CSS.aonWidth40())
			.addCell(pp1,AON.CSS.aonWidth120());
	
		
		box88.setMaxLength(6);
		box88.setVisibleLength(6);
		box88.addValueChangeHandler(event -> {
			if (box88.getValue() == null) box88.setValue(0.0,false);
			getModel().setBox88(box88.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		FlowPanel pp2 = new FlowPanel();
		pp2.add(box88);
		pp2.add(new InlineLabel("%"));
		tab.addRow()
			.addCell(new Label(AON.MSG.alava()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto(), AON.CSS.aonTextRight())
			.addCell(new AonBoxLabel(88),AON.CSS.aonWidth40())
			.addCell(pp2,AON.CSS.aonWidth120());
		
		box89.setMaxLength(6);
		box89.setVisibleLength(6);
		box89.addValueChangeHandler(event -> {
			if (box89.getValue() == null) box89.setValue(0.0,false);
			getModel().setBox89(box89.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		FlowPanel pp3 = new FlowPanel();
		pp3.add(box89);
		pp3.add(new InlineLabel("%"));
		tab.addRow()
			.addCell(new Label(AON.MSG.gipuzkoa()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto(), AON.CSS.aonTextRight())
			.addCell(new AonBoxLabel(89),AON.CSS.aonWidth40())
			.addCell(pp3,AON.CSS.aonWidth120());
		
		box90.setMaxLength(6);
		box90.setVisibleLength(6);
		box90.addValueChangeHandler(event -> {
			if (box90.getValue() == null) box90.setValue(0.0,false);
			getModel().setBox90(box90.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		FlowPanel pp4 = new FlowPanel();
		pp4.add(box90);
		pp4.add(new InlineLabel("%"));
		tab.addRow()
			.addCell(new Label(AON.MSG.bizkaia()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto(), AON.CSS.aonTextRight())
			.addCell(new AonBoxLabel(90),AON.CSS.aonWidth40())
			.addCell(pp4,AON.CSS.aonWidth120());
		
		box91.setMaxLength(6);
		box91.setVisibleLength(6);
		box91.addValueChangeHandler(event -> {
			if (box91.getValue() == null) box91.setValue(0.0,false);
			getModel().setBox91(box91.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		FlowPanel pp5 = new FlowPanel();
		pp5.add(box91);
		pp5.add(new InlineLabel("%"));
		tab.addRow()
			.addCell(new Label(AON.MSG.navarra()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto(), AON.CSS.aonTextRight())
			.addCell(new AonBoxLabel(91),AON.CSS.aonWidth40())
			.addCell(pp5,AON.CSS.aonWidth120());
		
		box92.setEnabled(false);
		tab.addRow()
			.addCell(new Label(AON.MSG.commonTerritoryResult()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(92),AON.CSS.aonWidth40())
			.addCell(box92,AON.CSS.aonWidth120());
		
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

		box93.addValueChangeHandler(event -> {
			if (box93.getValue() == null) box93.setValue(0.0,false);
			getModel().setBox93(box93.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.commonTerritoryQuotaCompensation()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(93),AON.CSS.aonWidth40())
			.addCell(box93,AON.CSS.aonWidth120());
		
		box94.setEnabled(false);
		tab.addRow()
			.addCell(new Label(AON.MSG.commonTerritoryDeclarationResult()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(94),AON.CSS.aonWidth40())
			.addCell(box94,AON.CSS.aonWidth120());
	}

}
