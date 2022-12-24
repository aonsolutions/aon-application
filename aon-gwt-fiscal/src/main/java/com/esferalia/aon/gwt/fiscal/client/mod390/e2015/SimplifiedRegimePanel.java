package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015.SimpliedRegimeActivity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class SimplifiedRegimePanel extends FlowPanel implements HasValueChangeHandlers<SimpliedRegimeActivity> {

	private AonTextBox epigrafe = new AonTextBox();
	private AonDoubleBox unit1 = new AonDoubleBox();
	private AonDoubleBox amount1 = new AonDoubleBox();
	private AonDoubleBox unit2 = new AonDoubleBox();
	private AonDoubleBox amount2 = new AonDoubleBox();
	private AonDoubleBox unit3 = new AonDoubleBox();
	private AonDoubleBox amount3 = new AonDoubleBox();
	private AonDoubleBox unit4 = new AonDoubleBox();
	private AonDoubleBox amount4 = new AonDoubleBox();
	private AonDoubleBox unit5 = new AonDoubleBox();
	private AonDoubleBox amount5 = new AonDoubleBox();
	private AonDoubleBox unit6 = new AonDoubleBox();
	private AonDoubleBox amount6 = new AonDoubleBox();
	private AonDoubleBox unit7 = new AonDoubleBox();
	private AonDoubleBox amount7 = new AonDoubleBox();
	
	private AonDoubleBox boxC = new AonDoubleBox();
	private AonDoubleBox boxD = new AonDoubleBox();
	private AonDoubleBox boxE = new AonDoubleBox();
	private AonDoubleBox boxF = new AonDoubleBox();
	private AonDoubleBox boxG = new AonDoubleBox();
	private AonDoubleBox boxH = new AonDoubleBox();
	private AonDoubleBox boxI = new AonDoubleBox();
	private AonDoubleBox boxJ = new AonDoubleBox();
	
	public SimplifiedRegimePanel(SimpliedRegimeActivity regime) {
		paint();
		setValue(regime);
	}

	private void setValue(SimpliedRegimeActivity regime) {
		if (regime == null) {
			empty();
			return;
		}
	    epigrafe.setValue(regime.getEpigrafe(),false);
	    unit1.setValue(regime.getUnit1(),false);
	    amount1.setValue(regime.getAmount1(),false);
	    unit2.setValue(regime.getUnit2(),false);
	    amount2.setValue(regime.getAmount2(),false);
	    unit3.setValue(regime.getUnit3(),false);
	    amount3.setValue(regime.getAmount3(),false);
	    unit4.setValue(regime.getUnit4(),false);
	    amount4.setValue(regime.getAmount4(),false);
	    unit5.setValue(regime.getUnit5(),false);
	    amount5.setValue(regime.getAmount5(),false);
	    unit6.setValue(regime.getUnit6(),false);
	    amount6.setValue(regime.getAmount6(),false);
	    unit7.setValue(regime.getUnit7(),false);
	    amount7.setValue(regime.getAmount7(),false);
	    boxC.setValue(regime.getBoxC(),false);
	    boxD.setValue(regime.getBoxD(),false);
		boxE.setValue(regime.getBoxE(),false);
		boxF.setValue(regime.getBoxF(),false);
		boxG.setValue(regime.getBoxG(),false);
		boxH.setValue(regime.getBoxH(),false);
		boxI.setValue(regime.getBoxI(),false);
		boxJ.setValue(regime.getBoxJ(),false);
	}


	public void fire() {
		SimpliedRegimeActivity reg = null;
		if (!AonStringUtils.isBlank(epigrafe.getValue())) {
			reg = new SimpliedRegimeActivity();
		    reg.setEpigrafe(epigrafe.getValue());
		    reg.setUnit1(unit1.getValue());
		    reg.setAmount1(amount1.getValue());
		    reg.setUnit2(unit2.getValue());
		    reg.setAmount2(amount2.getValue());
		    reg.setUnit3(unit3.getValue());
		    reg.setAmount3(amount3.getValue());
		    reg.setUnit4(unit4.getValue());
		    reg.setAmount4(amount4.getValue());
		    reg.setUnit5(unit5.getValue());
		    reg.setAmount5(amount5.getValue());
		    reg.setUnit6(unit6.getValue());
		    reg.setAmount6(amount6.getValue());
		    reg.setUnit7(unit7.getValue());
		    reg.setAmount7(amount7.getValue());
		    reg.setBoxC(boxC.getValue());
		    reg.setBoxD(boxD.getValue());
			reg.setBoxE(boxE.getValue());
			reg.setBoxF(boxF.getValue());
			reg.setBoxG(boxG.getValue());
			reg.setBoxH(boxH.getValue());
			reg.setBoxI(boxI.getValue());
			reg.setBoxJ(boxJ.getValue());
		}
		ValueChangeEvent.fire(SimplifiedRegimePanel.this,reg);
	}

	private void empty() {
	    epigrafe.setValue(null,false);
	    unit1.setValue(0.0,false);
	    amount1.setValue(0.0,false);
	    unit2.setValue(0.0,false);
	    amount2.setValue(0.0,false);
	    unit3.setValue(0.0,false);
	    amount3.setValue(0.0,false);
	    unit4.setValue(0.0,false);
	    amount4.setValue(0.0,false);
	    unit5.setValue(0.0,false);
	    amount5.setValue(0.0,false);
	    unit6.setValue(0.0,false);
	    amount6.setValue(0.0,false);
	    unit7.setValue(0.0,false);
	    amount7.setValue(0.0,false);
	    boxC.setValue(0.0,false);
	    boxD.setValue(0.0,false);
		boxE.setValue(0.0,false);
		boxF.setValue(0.0,false);
		boxG.setValue(0.0,false);
		boxH.setValue(0.0,false);
		boxI.setValue(0.0,false);
		boxJ.setValue(0.0,false);
	}
	
	private void paint() {
		AonDisplayTable tab0 = new AonDisplayTable();
		tab0.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab0.addStyleName(AON.CSS.aonBlockCenter());
		add(tab0);
		
		epigrafe.setVisibleLength(7);
		epigrafe.setMaxLength(7);
		tab0.addRow()
			.addCell(new Label(""), AON.CSS.aonWidth150())
			.addCell(new Label(AON.MSG.epigraph()), AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new AonBoxLabel(66), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(epigrafe, AON.CSS.aonWidth120());
		unit1.setVisibleLength(7);
		unit2.setVisibleLength(7);
		unit3.setVisibleLength(7);
		unit4.setVisibleLength(7);
		unit5.setVisibleLength(7);
		unit6.setVisibleLength(7);
		unit7.setVisibleLength(7);
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "1"), AON.CSS.aonWidth150(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit1, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(amount1, AON.CSS.aonWidth120());
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "2"), AON.CSS.aonWidth150(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit2, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(amount2, AON.CSS.aonWidth120());
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "3"), AON.CSS.aonWidth150(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit3, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(amount3, AON.CSS.aonWidth120());
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "4"), AON.CSS.aonWidth150(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit4, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(amount4, AON.CSS.aonWidth120());
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "5"), AON.CSS.aonWidth150(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit5, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(amount5, AON.CSS.aonWidth120());
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "6"), AON.CSS.aonWidth150(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit6, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(amount6, AON.CSS.aonWidth120());
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "7"), AON.CSS.aonWidth150(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit7, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(amount7, AON.CSS.aonWidth120());

		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		add(tab1);
		tab1.addRow()
			.addCell(new Label(AON.MSG.page6C()), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("C"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxC, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label(AON.MSG.page6D()), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("D"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxD, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label(AON.MSG.page6E()), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("E"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxE, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label(AON.MSG.page6F()), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("F"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxF, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label(AON.MSG.page6G()), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("G"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxG, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label(AON.MSG.page6H()), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("H"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxH, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label(AON.MSG.page6I()), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("I"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxI, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label(AON.MSG.page6J()), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("J"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxJ, AON.CSS.aonWidth120());
		
		
	    epigrafe.addValueChangeHandler(event -> fire());
	    unit1.addValueChangeHandler(event -> fire());
	    amount1.addValueChangeHandler(event -> fire());
	    unit2.addValueChangeHandler(event -> fire());
	    amount2.addValueChangeHandler(event -> fire());
	    unit3.addValueChangeHandler(event -> fire());
	    amount3.addValueChangeHandler(event -> fire());
	    unit4.addValueChangeHandler(event -> fire());
	    amount4.addValueChangeHandler(event -> fire());
	    unit5.addValueChangeHandler(event -> fire());
	    amount5.addValueChangeHandler(event -> fire());
	    unit6.addValueChangeHandler(event -> fire());
	    amount6.addValueChangeHandler(event -> fire());
	    unit7.addValueChangeHandler(event -> fire());
	    amount7.addValueChangeHandler(event -> fire());
	    boxC.addValueChangeHandler(event -> fire());
	    boxD.addValueChangeHandler(event -> fire());
		boxE.addValueChangeHandler(event -> fire());
		boxF.addValueChangeHandler(event -> fire());
		boxG.addValueChangeHandler(event -> fire());
		boxH.addValueChangeHandler(event -> fire());
		boxI.addValueChangeHandler(event -> fire());
		boxJ.addValueChangeHandler(event -> fire());
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<SimpliedRegimeActivity> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
}
