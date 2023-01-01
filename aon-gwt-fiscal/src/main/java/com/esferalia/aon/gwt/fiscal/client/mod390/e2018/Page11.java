package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018.Model3902018Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod390.DeductionRegime;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page11 extends PageAbs {

	private AonDoubleBox box139 = new AonDoubleBox();
	private AonDoubleBox box140 = new AonDoubleBox();
	private AonDoubleBox box141 = new AonDoubleBox();
	private AonDoubleBox box142 = new AonDoubleBox();
	private AonDoubleBox box143 = new AonDoubleBox();
	private AonDoubleBox box144 = new AonDoubleBox();
	private AonDoubleBox box145 = new AonDoubleBox();
	private AonDoubleBox box146 = new AonDoubleBox();
	private AonDoubleBox box147 = new AonDoubleBox();
	private AonDoubleBox box148 = new AonDoubleBox();
	private AonDoubleBox box149 = new AonDoubleBox();
	private AonDoubleBox box150 = new AonDoubleBox();
	private AonDoubleBox box151 = new AonDoubleBox();
	private AonDoubleBox box152 = new AonDoubleBox();
	private AonDoubleBox box640 = new AonDoubleBox();
	private AonDoubleBox box153 = new AonDoubleBox();
	private AonDoubleBox box154 = new AonDoubleBox();
	private AonDoubleBox box155 = new AonDoubleBox();

	private AonDoubleBox box156 = new AonDoubleBox();
	private AonDoubleBox box157 = new AonDoubleBox();
	private AonDoubleBox box158 = new AonDoubleBox();
	private AonDoubleBox box159 = new AonDoubleBox();
	private AonDoubleBox box160 = new AonDoubleBox();
	private AonDoubleBox box161 = new AonDoubleBox();
	private AonDoubleBox box162 = new AonDoubleBox();
	private AonDoubleBox box163 = new AonDoubleBox();
	private AonDoubleBox box164 = new AonDoubleBox();
	private AonDoubleBox box165 = new AonDoubleBox();
	private AonDoubleBox box166 = new AonDoubleBox();
	private AonDoubleBox box167 = new AonDoubleBox();
	private AonDoubleBox box168 = new AonDoubleBox();
	private AonDoubleBox box169 = new AonDoubleBox();
	private AonDoubleBox box641 = new AonDoubleBox();
	private AonDoubleBox box170 = new AonDoubleBox();
	private AonDoubleBox box171 = new AonDoubleBox();
	private AonDoubleBox box172 = new AonDoubleBox();

	private AonDoubleBox box173 = new AonDoubleBox();
	private AonDoubleBox box174 = new AonDoubleBox();
	private AonDoubleBox box175 = new AonDoubleBox();
	private AonDoubleBox box176 = new AonDoubleBox();
	private AonDoubleBox box177 = new AonDoubleBox();
	private AonDoubleBox box178 = new AonDoubleBox();
	private AonDoubleBox box179 = new AonDoubleBox();
	private AonDoubleBox box180 = new AonDoubleBox();
	private AonDoubleBox box181 = new AonDoubleBox();
	private AonDoubleBox box182 = new AonDoubleBox();
	private AonDoubleBox box183 = new AonDoubleBox();
	private AonDoubleBox box184 = new AonDoubleBox();
	private AonDoubleBox box185 = new AonDoubleBox();
	private AonDoubleBox box186 = new AonDoubleBox();
	private AonDoubleBox box642 = new AonDoubleBox();
	private AonDoubleBox box187 = new AonDoubleBox();
	private AonDoubleBox box188 = new AonDoubleBox();
	private AonDoubleBox box189 = new AonDoubleBox();

	public Page11(Model3902018Callback callback) {
		super(callback);
		setValue();
		paint();
	}
	
	@Override
	protected void setValue() {
		if (getModel().getRegime1() == null) getModel().setRegime1(new DeductionRegime());
		box139.setValue(getModel().getRegime1().getBase1());
		box140.setValue(getModel().getRegime1().getQuota1());
		box141.setValue(getModel().getRegime1().getBase2());
		box142.setValue(getModel().getRegime1().getQuota2());
		box143.setValue(getModel().getRegime1().getBase3());
		box144.setValue(getModel().getRegime1().getQuota3());
		box145.setValue(getModel().getRegime1().getBase4());
		box146.setValue(getModel().getRegime1().getQuota4());
		box147.setValue(getModel().getRegime1().getBase5());
		box148.setValue(getModel().getRegime1().getQuota5());
		box149.setValue(getModel().getRegime1().getBase6());
		box150.setValue(getModel().getRegime1().getQuota6());
		box151.setValue(getModel().getRegime1().getBase7());
		box152.setValue(getModel().getRegime1().getQuota7());
		box640.setValue(getModel().getRegime1().getBase8());
		box153.setValue(getModel().getRegime1().getQuota8());
		box154.setValue(getModel().getRegime1().getQuota9());
		box155.setValue(getModel().getRegime1().getQuota10());
		
		if (getModel().getRegime2() == null) getModel().setRegime2(new DeductionRegime());
		box156.setValue(getModel().getRegime2().getBase1());
		box157.setValue(getModel().getRegime2().getQuota1());
		box158.setValue(getModel().getRegime2().getBase2());
		box159.setValue(getModel().getRegime2().getQuota2());
		box160.setValue(getModel().getRegime2().getBase3());
		box161.setValue(getModel().getRegime2().getQuota3());
		box162.setValue(getModel().getRegime2().getBase4());
		box163.setValue(getModel().getRegime2().getQuota4());
		box164.setValue(getModel().getRegime2().getBase5());
		box165.setValue(getModel().getRegime2().getQuota5());
		box166.setValue(getModel().getRegime2().getBase6());
		box167.setValue(getModel().getRegime2().getQuota6());
		box168.setValue(getModel().getRegime2().getBase7());
		box169.setValue(getModel().getRegime2().getQuota7());
		box641.setValue(getModel().getRegime2().getBase8());
		box170.setValue(getModel().getRegime2().getQuota8());
		box171.setValue(getModel().getRegime2().getQuota9());
		box172.setValue(getModel().getRegime2().getQuota10());

		if (getModel().getRegime3() == null) getModel().setRegime3(new DeductionRegime());
		box173.setValue(getModel().getRegime3().getBase1());
		box174.setValue(getModel().getRegime3().getQuota1());
		box175.setValue(getModel().getRegime3().getBase2());
		box176.setValue(getModel().getRegime3().getQuota2());
		box177.setValue(getModel().getRegime3().getBase3());
		box178.setValue(getModel().getRegime3().getQuota3());
		box179.setValue(getModel().getRegime3().getBase4());
		box180.setValue(getModel().getRegime3().getQuota4());
		box181.setValue(getModel().getRegime3().getBase5());
		box182.setValue(getModel().getRegime3().getQuota5());
		box183.setValue(getModel().getRegime3().getBase6());
		box184.setValue(getModel().getRegime3().getQuota6());
		box185.setValue(getModel().getRegime3().getBase7());
		box186.setValue(getModel().getRegime3().getQuota7());
		box642.setValue(getModel().getRegime3().getBase8());
		box187.setValue(getModel().getRegime3().getQuota8());
		box188.setValue(getModel().getRegime3().getQuota9());
		box189.setValue(getModel().getRegime3().getQuota10());
	}
	
	private void paint() {
		
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);

		basePanel.add(getTitle(AON.MSG.difActivitiesRegime()));
		
		basePanel.add(getSubtitle(AON.MSG.inputVat() + ". " + AON.MSG.group() + " 1."));
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);
		
		tab.addRow()
			.addCell( getSubsubtitle(AON.MSG.internOpVatDeduction()),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120());

		box139.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setBase1(box139.getValue());
			markAsDirty();
		});
		box140.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setQuota1(box140.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell( getSubsubtitle(AON.MSG.commonAsset()))
			.addCell(new AonBoxLabel(139),AON.CSS.aonWidth40())
			.addCell(box139)
			.addCell(new AonBoxLabel(140),AON.CSS.aonWidth40())
			.addCell(box140,AON.CSS.aonWidth120());

		box141.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setBase2(box141.getValue());
			markAsDirty();
		});
		box142.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setQuota2(box142.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.investAsset()))
			.addCell(new AonBoxLabel(141),AON.CSS.aonWidth40())
			.addCell(box141)
			.addCell(new AonBoxLabel(142),AON.CSS.aonWidth40())
			.addCell(box142,AON.CSS.aonWidth120());
		
		tab.addRow()
			.addCell( getSubsubtitle(AON.MSG.importVatDeduction()),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120());
			
		box143.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setBase3(box143.getValue());
			markAsDirty();
		});
		box144.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setQuota3(box144.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell( getSubsubtitle(AON.MSG.commonAsset()))
			.addCell(new AonBoxLabel(143),AON.CSS.aonWidth40())
			.addCell(box143)
			.addCell(new AonBoxLabel(144),AON.CSS.aonWidth40())
			.addCell(box144,AON.CSS.aonWidth120());
	
		box145.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setBase4(box145.getValue());
			markAsDirty();
		});
		box146.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setQuota4(box146.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.investAsset()))
			.addCell(new AonBoxLabel(145),AON.CSS.aonWidth40())
			.addCell(box145)
			.addCell(new AonBoxLabel(146),AON.CSS.aonWidth40())
			.addCell(box146,AON.CSS.aonWidth120());
		
		tab.addRow()
			.addCell( getSubsubtitle(AON.MSG.intracommunityAdqVatDeduction()),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120());
		
		box147.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setBase5(box147.getValue());
			markAsDirty();
		});
		box148.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setQuota5(box148.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell( getSubsubtitle(AON.MSG.commonAsset()))
			.addCell(new AonBoxLabel(147),AON.CSS.aonWidth40())
			.addCell(box147)
			.addCell(new AonBoxLabel(148),AON.CSS.aonWidth40())
			.addCell(box148,AON.CSS.aonWidth120());
	
		box149.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setBase6(box149.getValue());
			markAsDirty();
		});
		box150.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setQuota6(box150.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.investAsset()))
			.addCell(new AonBoxLabel(149),AON.CSS.aonWidth40())
			.addCell(box149)
			.addCell(new AonBoxLabel(150),AON.CSS.aonWidth40())
			.addCell(box150,AON.CSS.aonWidth120());
		
		box151.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setBase7(box151.getValue());
			markAsDirty();
		});
		box152.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setQuota7(box152.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.agricultureCompensation()))
			.addCell(new AonBoxLabel(151),AON.CSS.aonWidth40())
			.addCell(box151)
			.addCell(new AonBoxLabel(152),AON.CSS.aonWidth40())
			.addCell(box152,AON.CSS.aonWidth120());
		
		box640.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setBase8(box640.getValue());
			markAsDirty();
		});
		box153.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setQuota8(box153.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.deductionRectification()))
			.addCell(new AonBoxLabel(640),AON.CSS.aonWidth40())
			.addCell(box640)
			.addCell(new AonBoxLabel(153),AON.CSS.aonWidth40())
			.addCell(box153,AON.CSS.aonWidth120());
		
		box154.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setQuota9(box154.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.investAssetRegularization()))
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label())
			.addCell(new AonBoxLabel(154),AON.CSS.aonWidth40())
			.addCell(box154,AON.CSS.aonWidth120());

		box155.addValueChangeHandler(event -> {
			getModel().ensureRegime1().setQuota10(box155.getValue());
			markAsDirty();
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.deductionSum()))
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label())
			.addCell(new AonBoxLabel(155),AON.CSS.aonWidth40())
			.addCell(box155,AON.CSS.aonWidth120());
		
		
		basePanel.add(getSubtitle(AON.MSG.inputVat() + ". " + AON.MSG.group() + " 2."));
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);

		tab1.addRow()
			.addCell( getSubsubtitle(AON.MSG.internOpVatDeduction()),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120());
	

		box156.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setBase1(box156.getValue());
			markAsDirty();
		});
		box157.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setQuota1(box157.getValue());
			markAsDirty();
		});
		tab1.addRow()
			.addCell( getSubsubtitle(AON.MSG.commonAsset()))
			.addCell(new AonBoxLabel(156),AON.CSS.aonWidth40())
			.addCell(box156)
			.addCell(new AonBoxLabel(157),AON.CSS.aonWidth40())
			.addCell(box157,AON.CSS.aonWidth120());
	
		box158.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setBase2(box158.getValue());
			markAsDirty();
		});
		box159.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setQuota2(box159.getValue());
			markAsDirty();
		});
		tab1.addRow()
			.addCell( new Label(AON.MSG.investAsset()))
			.addCell(new AonBoxLabel(158),AON.CSS.aonWidth40())
			.addCell(box158)
			.addCell(new AonBoxLabel(159),AON.CSS.aonWidth40())
			.addCell(box159,AON.CSS.aonWidth120());
		
		tab1.addRow()
			.addCell( getSubsubtitle(AON.MSG.importVatDeduction()),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120());
			
		box160.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setBase3(box160.getValue());
			markAsDirty();
		});
		box161.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setQuota3(box161.getValue());
			markAsDirty();
		});
		tab1.addRow()
			.addCell( getSubsubtitle(AON.MSG.commonAsset()))
			.addCell(new AonBoxLabel(160),AON.CSS.aonWidth40())
			.addCell(box160)
			.addCell(new AonBoxLabel(161),AON.CSS.aonWidth40())
			.addCell(box161,AON.CSS.aonWidth120());
	
		box162.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setBase4(box162.getValue());
			markAsDirty();
		});
		box163.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setQuota4(box163.getValue());
			markAsDirty();
		});
		tab1.addRow()
			.addCell( new Label(AON.MSG.investAsset()))
			.addCell(new AonBoxLabel(162),AON.CSS.aonWidth40())
			.addCell(box162)
			.addCell(new AonBoxLabel(163),AON.CSS.aonWidth40())
			.addCell(box163,AON.CSS.aonWidth120());
		
		tab1.addRow()
			.addCell( getSubsubtitle(AON.MSG.intracommunityAdqVatDeduction()),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120());
		
		box164.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setBase5(box164.getValue());
			markAsDirty();
		});
		box165.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setQuota5(box165.getValue());
			markAsDirty();
		});
		tab1.addRow()
			.addCell( getSubsubtitle(AON.MSG.commonAsset()))
			.addCell(new AonBoxLabel(164),AON.CSS.aonWidth40())
			.addCell(box164)
			.addCell(new AonBoxLabel(165),AON.CSS.aonWidth40())
			.addCell(box165,AON.CSS.aonWidth120());
	
		box166.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setBase6(box166.getValue());
			markAsDirty();
		});
		box167.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setQuota6(box167.getValue());
			markAsDirty();
		});
		tab1.addRow()
			.addCell( new Label(AON.MSG.investAsset()))
			.addCell(new AonBoxLabel(166),AON.CSS.aonWidth40())
			.addCell(box166)
			.addCell(new AonBoxLabel(167),AON.CSS.aonWidth40())
			.addCell(box167,AON.CSS.aonWidth120());
		
		box168.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setBase7(box168.getValue());
			markAsDirty();
		});
		box169.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setQuota7(box169.getValue());
			markAsDirty();
		});
		tab1.addRow()
			.addCell( new Label(AON.MSG.agricultureCompensation()))
			.addCell(new AonBoxLabel(168),AON.CSS.aonWidth40())
			.addCell(box168)
			.addCell(new AonBoxLabel(169),AON.CSS.aonWidth40())
			.addCell(box169,AON.CSS.aonWidth120());
		
		box641.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setBase8(box641.getValue());
			markAsDirty();
		});
		box170.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setQuota8(box170.getValue());
			markAsDirty();
		});
		tab1.addRow()
			.addCell( new Label(AON.MSG.deductionRectification()))
			.addCell(new AonBoxLabel(641),AON.CSS.aonWidth40())
			.addCell(box641)
			.addCell(new AonBoxLabel(170),AON.CSS.aonWidth40())
			.addCell(box170,AON.CSS.aonWidth120());
		
		box171.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setQuota9(box171.getValue());
			markAsDirty();
		});
		tab1.addRow()
			.addCell( new Label(AON.MSG.investAssetRegularization()))
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label())
			.addCell(new AonBoxLabel(171),AON.CSS.aonWidth40())
			.addCell(box171,AON.CSS.aonWidth120());
	
		box172.addValueChangeHandler(event -> {
			getModel().ensureRegime2().setQuota10(box172.getValue());
			markAsDirty();
		});
		tab1.addRow()
			.addCell( new Label(AON.MSG.deductionSum()))
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label())
			.addCell(new AonBoxLabel(172),AON.CSS.aonWidth40())
			.addCell(box172,AON.CSS.aonWidth120());
		
		basePanel.add(getSubtitle(AON.MSG.inputVat() + ". " + AON.MSG.group() + " 3."));
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab2);

		tab2.addRow()
			.addCell( getSubsubtitle(AON.MSG.internOpVatDeduction()),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120());


		box173.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setBase1(box173.getValue());
			markAsDirty();
		});
		box174.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setQuota1(box174.getValue());
			markAsDirty();
		});
		tab2.addRow()
			.addCell( getSubsubtitle(AON.MSG.commonAsset()))
			.addCell(new AonBoxLabel(173),AON.CSS.aonWidth40())
			.addCell(box173)
			.addCell(new AonBoxLabel(174),AON.CSS.aonWidth40())
			.addCell(box174,AON.CSS.aonWidth120());
	
		box175.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setBase2(box175.getValue());
			markAsDirty();
		});
		box176.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setQuota2(box176.getValue());
			markAsDirty();
		});
		tab2.addRow()
			.addCell( new Label(AON.MSG.investAsset()))
			.addCell(new AonBoxLabel(175),AON.CSS.aonWidth40())
			.addCell(box175)
			.addCell(new AonBoxLabel(176),AON.CSS.aonWidth40())
			.addCell(box176,AON.CSS.aonWidth120());

		tab2.addRow()
			.addCell( getSubsubtitle(AON.MSG.importVatDeduction()),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120());
			
		box177.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setBase3(box177.getValue());
			markAsDirty();
		});
		box178.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setQuota3(box178.getValue());
			markAsDirty();
		});
		tab2.addRow()
			.addCell( getSubsubtitle(AON.MSG.commonAsset()))
			.addCell(new AonBoxLabel(177),AON.CSS.aonWidth40())
			.addCell(box177)
			.addCell(new AonBoxLabel(178),AON.CSS.aonWidth40())
			.addCell(box178,AON.CSS.aonWidth120());
	
		box179.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setBase4(box179.getValue());
			markAsDirty();
		});
		box180.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setQuota4(box180.getValue());
			markAsDirty();
		});
		tab2.addRow()
			.addCell( new Label(AON.MSG.investAsset()))
			.addCell(new AonBoxLabel(179),AON.CSS.aonWidth40())
			.addCell(box179)
			.addCell(new AonBoxLabel(180),AON.CSS.aonWidth40())
			.addCell(box180,AON.CSS.aonWidth120());
		
		tab2.addRow()
			.addCell( getSubsubtitle(AON.MSG.intracommunityAdqVatDeduction()),AON.CSS.aonWidthAuto())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120())
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label(),AON.CSS.aonWidth120());
		
		box181.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setBase5(box181.getValue());
			markAsDirty();
		});
		box182.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setQuota5(box182.getValue());
			markAsDirty();
		});
		tab2.addRow()
			.addCell( getSubsubtitle(AON.MSG.commonAsset()))
			.addCell(new AonBoxLabel(181),AON.CSS.aonWidth40())
			.addCell(box181)
			.addCell(new AonBoxLabel(182),AON.CSS.aonWidth40())
			.addCell(box182,AON.CSS.aonWidth120());
	
		box183.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setBase6(box183.getValue());
			markAsDirty();
		});
		box184.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setQuota6(box184.getValue());
			markAsDirty();
		});
		tab2.addRow()
			.addCell( new Label(AON.MSG.investAsset()))
			.addCell(new AonBoxLabel(183),AON.CSS.aonWidth40())
			.addCell(box183)
			.addCell(new AonBoxLabel(184),AON.CSS.aonWidth40())
			.addCell(box184,AON.CSS.aonWidth120());
		
		box185.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setBase7(box185.getValue());
			markAsDirty();
		});
		box186.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setQuota7(box186.getValue());
			markAsDirty();
		});
		tab2.addRow()
			.addCell( new Label(AON.MSG.agricultureCompensation()))
			.addCell(new AonBoxLabel(185),AON.CSS.aonWidth40())
			.addCell(box185)
			.addCell(new AonBoxLabel(186),AON.CSS.aonWidth40())
			.addCell(box186,AON.CSS.aonWidth120());
		
		box642.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setBase8(box642.getValue());
			markAsDirty();
		});
		box187.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setQuota8(box187.getValue());
			markAsDirty();
		});
		tab2.addRow()
			.addCell( new Label(AON.MSG.deductionRectification()))
			.addCell(new AonBoxLabel(642),AON.CSS.aonWidth40())
			.addCell(box642)
			.addCell(new AonBoxLabel(187),AON.CSS.aonWidth40())
			.addCell(box187,AON.CSS.aonWidth120());
		
		box188.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setQuota9(box188.getValue());
			markAsDirty();
		});
		tab2.addRow()
			.addCell( new Label(AON.MSG.investAssetRegularization()))
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label())
			.addCell(new AonBoxLabel(189),AON.CSS.aonWidth40())
			.addCell(box189,AON.CSS.aonWidth120());
	
		box189.addValueChangeHandler(event -> {
			getModel().ensureRegime3().setQuota10(box189.getValue());
			markAsDirty();
		});
		tab2.addRow()
			.addCell( new Label(AON.MSG.deductionSum()))
			.addCell(new Label(),AON.CSS.aonWidth40())
			.addCell(new Label())
			.addCell(new AonBoxLabel(189),AON.CSS.aonWidth40())
			.addCell(box189,AON.CSS.aonWidth120());
		

	}
	
}
