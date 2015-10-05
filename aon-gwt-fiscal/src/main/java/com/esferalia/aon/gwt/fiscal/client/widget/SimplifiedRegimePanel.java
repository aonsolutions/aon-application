package com.esferalia.aon.gwt.fiscal.client.widget;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.Mod311Results;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.SimpliedRegimeActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SimplifiedRegimePanel extends ResizeComposite {

	interface SimplifiedRegimePanelBinder extends
			UiBinder<Widget, SimplifiedRegimePanel> {
	}

	private static final SimplifiedRegimePanelBinder panelBinder = GWT
			.create(SimplifiedRegimePanelBinder.class);

	@UiField
	Label title;
	
	@UiField
    TextBox epigrafe;
	@UiField
    DoubleTextBox unit1;
	@UiField
    DoubleTextBox amount1;
	@UiField
    DoubleTextBox unit2;
	@UiField
    DoubleTextBox amount2;
	@UiField
    DoubleTextBox unit3;
	@UiField
    DoubleTextBox amount3;
	@UiField
    DoubleTextBox unit4;
	@UiField
    DoubleTextBox amount4;
	@UiField
    DoubleTextBox unit5;
	@UiField
    DoubleTextBox amount5;
	@UiField
    DoubleTextBox unit6;
	@UiField
    DoubleTextBox amount6;
	@UiField
    DoubleTextBox unit7;
	@UiField
    DoubleTextBox amount7;
	
	@UiField
    DoubleTextBox boxC;
	@UiField
    DoubleTextBox boxD;
	@UiField
	DoubleTextBox boxE;
	@UiField
	DoubleTextBox boxF;
	@UiField
	DoubleTextBox boxG;
	@UiField
	DoubleTextBox boxH;
	@UiField
	DoubleTextBox boxI;
	@UiField
	DoubleTextBox boxJ;
	
	
	public SimplifiedRegimePanel() {
		AON.ensureInjected();
		
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
	}


	public void setValue(Mod311Results mod311Results) {
	    epigrafe.setValue(mod311Results.getEpigrafe());
	    unit1.setValue(mod311Results.getUnit1());
	    amount1.setValue(mod311Results.getAmount1());
	    unit2.setValue(mod311Results.getUnit2());
	    amount2.setValue(mod311Results.getAmount2());
	    unit3.setValue(mod311Results.getUnit3());
	    amount3.setValue(mod311Results.getAmount3());
	    unit4.setValue(mod311Results.getUnit4());
	    amount4.setValue(mod311Results.getAmount4());
	    unit5.setValue(mod311Results.getUnit5());
	    amount5.setValue(mod311Results.getAmount5());
	    unit6.setValue(mod311Results.getUnit6());
	    amount6.setValue(mod311Results.getAmount6());
	    unit7.setValue(mod311Results.getUnit7());
	    amount7.setValue(mod311Results.getAmount7());
	    boxC.setValue(mod311Results.getBoxC());
	    boxD.setValue(mod311Results.getBoxD());
		boxE.setValue(mod311Results.getBoxE());
		boxF.setValue(mod311Results.getBoxF());
		boxG.setValue(mod311Results.getBoxG());
		boxH.setValue(mod311Results.getBoxH());
		boxI.setValue(mod311Results.getBoxI());
		boxJ.setValue(mod311Results.getBoxJ());
	}


	public void setValue(SimpliedRegimeActivity regime) {
	    epigrafe.setValue(regime.getEpigrafe());
	    unit1.setValue(regime.getUnit1());
	    amount1.setValue(regime.getAmount1());
	    unit2.setValue(regime.getUnit2());
	    amount2.setValue(regime.getAmount2());
	    unit3.setValue(regime.getUnit3());
	    amount3.setValue(regime.getAmount3());
	    unit4.setValue(regime.getUnit4());
	    amount4.setValue(regime.getAmount4());
	    unit5.setValue(regime.getUnit5());
	    amount5.setValue(regime.getAmount5());
	    unit6.setValue(regime.getUnit6());
	    amount6.setValue(regime.getAmount6());
	    unit7.setValue(regime.getUnit7());
	    amount7.setValue(regime.getAmount7());
	    boxC.setValue(regime.getBoxC());
	    boxD.setValue(regime.getBoxD());
		boxE.setValue(regime.getBoxE());
		boxF.setValue(regime.getBoxF());
		boxG.setValue(regime.getBoxG());
		boxH.setValue(regime.getBoxH());
		boxI.setValue(regime.getBoxI());
		boxJ.setValue(regime.getBoxJ());
	}


	public SimpliedRegimeActivity populate() {
		SimpliedRegimeActivity reg = null;
		if (!AonUtil.isEmpty(epigrafe.getValue())) {
			reg = new SimpliedRegimeActivity();
		    reg.setEpigrafe(epigrafe.getValue());
		    reg.setUnit1(unit1.getDoubleValue());
		    reg.setAmount1(amount1.getDoubleValue());
		    reg.setUnit2(unit2.getDoubleValue());
		    reg.setAmount2(amount2.getDoubleValue());
		    reg.setUnit3(unit3.getDoubleValue());
		    reg.setAmount3(amount3.getDoubleValue());
		    reg.setUnit4(unit4.getDoubleValue());
		    reg.setAmount4(amount4.getDoubleValue());
		    reg.setUnit5(unit5.getDoubleValue());
		    reg.setAmount5(amount5.getDoubleValue());
		    reg.setUnit6(unit6.getDoubleValue());
		    reg.setAmount6(amount6.getDoubleValue());
		    reg.setUnit7(unit7.getDoubleValue());
		    reg.setAmount7(amount7.getDoubleValue());
		    reg.setBoxC(boxC.getDoubleValue());
		    reg.setBoxD(boxD.getDoubleValue());
			reg.setBoxE(boxE.getDoubleValue());
			reg.setBoxF(boxF.getDoubleValue());
			reg.setBoxG(boxG.getDoubleValue());
			reg.setBoxH(boxH.getDoubleValue());
			reg.setBoxI(boxI.getDoubleValue());
			reg.setBoxJ(boxJ.getDoubleValue());
		}
		return reg;
	}


	public void empty() {
	    epigrafe.setValue(null);
	    unit1.setValue(null);
	    amount1.setValue(null);
	    unit2.setValue(null);
	    amount2.setValue(null);
	    unit3.setValue(null);
	    amount3.setValue(null);
	    unit4.setValue(null);
	    amount4.setValue(null);
	    unit5.setValue(null);
	    amount5.setValue(null);
	    unit6.setValue(null);
	    amount6.setValue(null);
	    unit7.setValue(null);
	    amount7.setValue(null);
	    boxC.setValue(null);
	    boxD.setValue(null);
		boxE.setValue(null);
		boxF.setValue(null);
		boxG.setValue(null);
		boxH.setValue(null);
		boxI.setValue(null);
		boxJ.setValue(null);
	}
	
	public double getBoxJ() {
		return boxJ.getDoubleValue();
	}
}
