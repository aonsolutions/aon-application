package com.esferalia.aon.gwt.fiscal.client.mod390.e2024;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2024.Model3902024.Model3902024Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod390.FarmerRegimeActivity;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page04 extends PageAbs {

	private AonTextBox f01A = new AonTextBox(); 
	private AonDoubleBox f01B = new AonDoubleBox();
	private AonDoubleBox f01C = new AonDoubleBox(AonDoubleBox.VISIBLE_LENGTH, 4);
	private AonDoubleBox f01D = new AonDoubleBox(); // Cuota devengada
	private AonDoubleBox f01D1 = new AonDoubleBox();  // Reducción DANA 2024
	private AonDoubleBox f01E = new AonDoubleBox();
	private AonDoubleBox f01K = new AonDoubleBox();
	
	private AonTextBox f02A = new AonTextBox();
	private AonDoubleBox f02B = new AonDoubleBox();
	private AonDoubleBox f02C = new AonDoubleBox(AonDoubleBox.VISIBLE_LENGTH, 4);
	private AonDoubleBox f02D = new AonDoubleBox();
	private AonDoubleBox f02D1 = new AonDoubleBox();  // Reducción DANA 2024
	private AonDoubleBox f02E = new AonDoubleBox();
	private AonDoubleBox f02K = new AonDoubleBox();

	private AonTextBox f03A = new AonTextBox();
	private AonDoubleBox f03B = new AonDoubleBox();
	private AonDoubleBox f03C = new AonDoubleBox(AonDoubleBox.VISIBLE_LENGTH, 4);
	private AonDoubleBox f03D = new AonDoubleBox();
	private AonDoubleBox f03D1 = new AonDoubleBox();  // Reducción DANA 2024
	private AonDoubleBox f03E = new AonDoubleBox();
	private AonDoubleBox f03K = new AonDoubleBox();

	private AonTextBox f04A = new AonTextBox();
	private AonDoubleBox f04B = new AonDoubleBox();
	private AonDoubleBox f04C = new AonDoubleBox(AonDoubleBox.VISIBLE_LENGTH, 4);
	private AonDoubleBox f04D = new AonDoubleBox();
	private AonDoubleBox f04D1 = new AonDoubleBox();  // Reducción DANA 2024
	private AonDoubleBox f04E = new AonDoubleBox();
	private AonDoubleBox f04K = new AonDoubleBox();

	private AonTextBox f05A = new AonTextBox();
	private AonDoubleBox f05B = new AonDoubleBox();
	private AonDoubleBox f05C = new AonDoubleBox(AonDoubleBox.VISIBLE_LENGTH, 4);
	private AonDoubleBox f05D = new AonDoubleBox();
	private AonDoubleBox f05D1 = new AonDoubleBox();  // Reducción DANA 2024
	private AonDoubleBox f05E = new AonDoubleBox();
	private AonDoubleBox f05K = new AonDoubleBox();

	private AonDoubleBox box74 = new AonDoubleBox();
	private AonDoubleBox box75 = new AonDoubleBox();
	private AonDoubleBox box76 = new AonDoubleBox();
	private AonDoubleBox box77 = new AonDoubleBox();
	private AonDoubleBox box78 = new AonDoubleBox();
	private AonDoubleBox box79 = new AonDoubleBox();
	private AonDoubleBox box80 = new AonDoubleBox();
	private AonDoubleBox box81 = new AonDoubleBox();
	private AonDoubleBox box82 = new AonDoubleBox();
	private AonDoubleBox box83 = new AonDoubleBox();
	
	public Page04(Model3902024Callback callback) {
		super(callback);
		paint();
		setValue();
	}
	
	@Override
	protected void setValue() {
		FarmerRegimeActivity farmer = getModel().getFarmerRegime1();
		if (farmer != null) {
			f01A.setValue(farmer.getCodigo(),false);
			f01B.setValue(farmer.getIncomes(),false);
			f01C.setValue(farmer.getQuotaIndex(),false);
			f01D.setValue(farmer.getAccrualQuota(),false);
			f01D1.setValue(farmer.getDanaReduction(),false);
			f01E.setValue(farmer.getInputQuotas(),false);
			f01K.setValue(farmer.getQuota(),false);
		} else {
			f01K.setValue(0.0,false);
		}
		farmer = getModel().getFarmerRegime2();
		if (farmer != null) {
			f02A.setValue(farmer.getCodigo(),false);
			f02B.setValue(farmer.getIncomes(),false);
			f02C.setValue(farmer.getQuotaIndex(),false);
			f02D.setValue(farmer.getAccrualQuota(),false);
			f02D1.setValue(farmer.getDanaReduction(),false);
			f02E.setValue(farmer.getInputQuotas(),false);
			f02K.setValue(farmer.getQuota(),false);
		} else {
			f02K.setValue(0.0,false);
		}
		farmer = getModel().getFarmerRegime3();
		if (farmer != null) {
			f03A.setValue(farmer.getCodigo(),false);
			f03B.setValue(farmer.getIncomes(),false);
			f03C.setValue(farmer.getQuotaIndex(),false);
			f03D.setValue(farmer.getAccrualQuota(),false);
			f03D1.setValue(farmer.getDanaReduction(),false);
			f03E.setValue(farmer.getInputQuotas(),false);
			f03K.setValue(farmer.getQuota(),false);
		} else {
			f03K.setValue(0.0,false);
		}
		farmer = getModel().getFarmerRegime4();
		if (farmer != null) {
			f04A.setValue(farmer.getCodigo(),false);
			f04B.setValue(farmer.getIncomes(),false);
			f04C.setValue(farmer.getQuotaIndex(),false);
			f04D.setValue(farmer.getAccrualQuota(),false);
			f04D1.setValue(farmer.getDanaReduction(),false);
			f04E.setValue(farmer.getInputQuotas(),false);
			f04K.setValue(farmer.getQuota(),false);
		} else {
			f04K.setValue(0.0,false);
		}
		farmer = getModel().getFarmerRegime5();
		if (farmer != null) {
			f05A.setValue(farmer.getCodigo(),false);
			f05B.setValue(farmer.getIncomes(),false);
			f05C.setValue(farmer.getQuotaIndex(),false);
			f05D.setValue(farmer.getAccrualQuota(),false);
			f05D1.setValue(farmer.getDanaReduction(),false);
			f05E.setValue(farmer.getInputQuotas(),false);
			f05K.setValue(farmer.getQuota(),false);
		} else {
			f05K.setValue(0.0,false);
		}
		
		box74.setValue(getModel().getBox74(),false);
		box75.setValue(getModel().getBox75(),false);
		box76.setValue(getModel().getBox76(),false);
		box77.setValue(getModel().getBox77(),false);
		box78.setValue(getModel().getBox78(),false);
		box79.setValue(getModel().getBox79(),false);
		box80.setValue(getModel().getBox80(),false);
		box81.setValue(getModel().getBox81(),false);
		box82.setValue(getModel().getBox82(),false);
		box83.setValue(getModel().getBox83(),false);
	}

	private void paint() {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);
		
		basePanel.add(getTitle(AON.MSG.simplifiedRegimeOperations()));
		basePanel.add(getSubtitle(AON.MSG.noFarmerActivity()));
		
		SimplifiedRegimePanel activity1 = new SimplifiedRegimePanel(getModel().getSimpRegime1());
		activity1.addValueChangeHandler(event -> {
			getModel().setSimpRegime1(event.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		SimplifiedRegimePanel activity2 = new SimplifiedRegimePanel(getModel().getSimpRegime2());
		activity2.addValueChangeHandler(event -> {
			getModel().setSimpRegime2(event.getValue());
			calculateAndRefresh();
			markAsDirty();
		});

		AonDisplayTable tab0 = new AonDisplayTable();
		tab0.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab0.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab0);
		tab0.addRow()
			.addCell(activity1)
			.addCell(activity2);
		
		basePanel.add(getSubtitle(AON.MSG.farmerActivity()));
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		f01A.setVisibleLength(3);
		f02A.setVisibleLength(3);
		f03A.setVisibleLength(3);
		f04A.setVisibleLength(3);
		f05A.setVisibleLength(3);
		f01C.setVisibleLength(5);
		f02C.setVisibleLength(5);
		f03C.setVisibleLength(5);
		f04C.setVisibleLength(5);
		f05C.setVisibleLength(5);

		tab1.addRow()
			.addCell(new Label(AON.MSG.code()), AON.CSS.aonBold(), AON.CSS.aonWidth80())
			.addCell(new Label (AON.MSG.f02Msg()), AON.CSS.aonBold(), AON.CSS.aonWidth80())
			.addCell(new Label (AON.MSG.f03Msg()), AON.CSS.aonBold(), AON.CSS.aonWidth80())
			.addCell(new Label (AON.MSG.f04Msg()), AON.CSS.aonBold(), AON.CSS.aonWidth80())
			.addCell(new Label ("Reducci\u00F3n DANA"), AON.CSS.aonBold(), AON.CSS.aonWidth80())
			.addCell(new Label (AON.MSG.f05Msg()), AON.CSS.aonBold(), AON.CSS.aonWidth80())
			.addCell(new Label (AON.MSG.page6J()), AON.CSS.aonBold(), AON.CSS.aonWidthAuto());
		tab1.addRow()
			.addCell(f01A)
			.addCell(f01B)
			.addCell(f01C)
			.addCell(f01D)
			.addCell(f01D1)
			.addCell(f01E)
			.addCell(f01K);
		tab1.addRow()
			.addCell(f02A)
			.addCell(f02B)
			.addCell(f02C)
			.addCell(f02D)
			.addCell(f02D1)
			.addCell(f02E)
			.addCell(f02K);
		tab1.addRow()
			.addCell(f03A)
			.addCell(f03B)
			.addCell(f03C)
			.addCell(f03D)
			.addCell(f03D1)
			.addCell(f03E)
			.addCell(f03K);
		tab1.addRow()
			.addCell(f04A)
			.addCell(f04B)
			.addCell(f04C)
			.addCell(f04D)
			.addCell(f04D1)
			.addCell(f04E)
			.addCell(f04K);
		tab1.addRow()
			.addCell(f05A)
			.addCell(f05B)
			.addCell(f05C)
			.addCell(f05D)
			.addCell(f05D1)
			.addCell(f05E)
			.addCell(f05K);

		basePanel.add(getSubtitle(AON.MSG.outputVat()));
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab2);
		
		box74.setEnabled(false);
		box75.setEnabled(false);
		box79.setEnabled(false);
		tab2.addRow()
			.addCell(new Label(AON.MSG.box74Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(74),AON.CSS.aonWidth40())
			.addCell(box74,AON.CSS.aonWidth120());
		tab2.addRow()
			.addCell(new Label(AON.MSG.box75Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(75),AON.CSS.aonWidth40())
			.addCell(box75,AON.CSS.aonWidth120());
		tab2.addRow()
			.addCell(new Label(AON.MSG.box76Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(76),AON.CSS.aonWidth40())
			.addCell(box76,AON.CSS.aonWidth120());
		tab2.addRow()
			.addCell(new Label(AON.MSG.box77Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(77),AON.CSS.aonWidth40())
			.addCell(box77,AON.CSS.aonWidth120());
		tab2.addRow()
			.addCell(new Label(AON.MSG.box78Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(78),AON.CSS.aonWidth40())
			.addCell(box78,AON.CSS.aonWidth120());
		tab2.addRow()
			.addCell(new Label(AON.MSG.box79Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(79),AON.CSS.aonWidth40())
			.addCell(box79,AON.CSS.aonWidth120());

		basePanel.add(getSubtitle(AON.MSG.inputVat()));
		
		AonDisplayTable tab3 = new AonDisplayTable();
		tab3.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab3.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab3);

		box82.setEnabled(false);
		
		tab3.addRow()
			.addCell(new Label(AON.MSG.box80Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(80),AON.CSS.aonWidth40())
			.addCell(box80,AON.CSS.aonWidth120());
		tab3.addRow()
			.addCell(new Label(AON.MSG.investAssetRegularization()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(81),AON.CSS.aonWidth40())
			.addCell(box81,AON.CSS.aonWidth120());
		tab3.addRow()
			.addCell(new Label(AON.MSG.deductionSum()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(82),AON.CSS.aonWidth40())
			.addCell(box82,AON.CSS.aonWidth120());

		AonDisplayTable tab4 = new AonDisplayTable();
		tab4.addStyleName(AON.CSS.aonMarginTop());
		tab4.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab4.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab4);
		box83.setEnabled(false);
		tab4.addRow()
			.addCell(new Label(AON.MSG.box83Msg()),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell(new AonBoxLabel(83),AON.CSS.aonWidth40())
			.addCell(box83,AON.CSS.aonWidth120());

		f01A.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime1().setCodigo(f01A.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f01B.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime1().setIncomes(f01B.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f01C.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime1().setQuotaIndex(f01C.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f01D.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime1().setAccrualQuota(f01D.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f01D1.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime1().setDanaReduction(f01D1.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f01E.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime1().setInputQuotas(f01E.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f01K.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime1().setQuota(f01K.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		f02A.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime2().setCodigo(f02A.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f02B.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime2().setIncomes(f02B.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f02C.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime2().setQuotaIndex(f02C.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f02D.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime2().setAccrualQuota(f02D.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f02D1.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime2().setDanaReduction(f02D1.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f02E.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime2().setInputQuotas(f02E.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f02K.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime2().setQuota(f02K.getValue());
			calculateAndRefresh();
			markAsDirty();
		});

		f03A.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime3().setCodigo(f03A.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f03B.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime3().setIncomes(f03B.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f03C.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime3().setQuotaIndex(f03C.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f03D.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime3().setAccrualQuota(f03D.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f03D1.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime3().setDanaReduction(f03D1.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f03E.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime3().setInputQuotas(f03E.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f03K.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime3().setQuota(f03K.getValue());
			calculateAndRefresh();
			markAsDirty();
		});

		f04A.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime4().setCodigo(f04A.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f04B.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime4().setIncomes(f04B.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f04C.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime4().setQuotaIndex(f04C.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f04D.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime4().setAccrualQuota(f04D.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f04D1.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime4().setDanaReduction(f04D1.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f04E.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime4().setInputQuotas(f04E.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f04K.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime4().setQuota(f04K.getValue());
			calculateAndRefresh();
			markAsDirty();
		});

		f05A.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime5().setCodigo(f05A.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f05B.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime5().setIncomes(f05B.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f05C.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime5().setQuotaIndex(f05C.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f05D.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime5().setAccrualQuota(f05D.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f05D1.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime5().setDanaReduction(f05D1.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f05E.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime5().setInputQuotas(f05E.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		f05K.addValueChangeHandler(event -> {
			getModel().ensureFarmerRegime5().setQuota(f05K.getValue());
			calculateAndRefresh();
			markAsDirty();
		});

		box74.addValueChangeHandler(event -> {
			getModel().setBox74(box74.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box75.addValueChangeHandler(event -> {
			getModel().setBox75(box75.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box76.addValueChangeHandler(event -> {
			getModel().setBox76(box76.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box77.addValueChangeHandler(event -> {
			getModel().setBox77(box77.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box78.addValueChangeHandler(event -> {
			getModel().setBox78(box78.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box79.addValueChangeHandler(event -> {
			getModel().setBox79(box79.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box80.addValueChangeHandler(event -> {
			getModel().setBox80(box80.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box81.addValueChangeHandler(event -> {
			getModel().setBox81(box81.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box82.addValueChangeHandler(event -> {
			getModel().setBox82(box82.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box83.addValueChangeHandler(event -> {
			getModel().setBox83(box83.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
	}
}
