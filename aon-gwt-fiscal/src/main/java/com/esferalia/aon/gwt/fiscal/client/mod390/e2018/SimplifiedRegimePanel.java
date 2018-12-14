package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.shared.Mod311Results;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018.SimpliedRegimeActivity;
import com.esferalia.aon.watson.util.AonStringUtils;
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
    DoubleBox unit1;
	@UiField
    DoubleBox amount1;
	@UiField
    DoubleBox unit2;
	@UiField
    DoubleBox amount2;
	@UiField
    DoubleBox unit3;
	@UiField
    DoubleBox amount3;
	@UiField
    DoubleBox unit4;
	@UiField
    DoubleBox amount4;
	@UiField
    DoubleBox unit5;
	@UiField
    DoubleBox amount5;
	@UiField
    DoubleBox unit6;
	@UiField
    DoubleBox amount6;
	@UiField
    DoubleBox unit7;
	@UiField
    DoubleBox amount7;
	
	@UiField
    DoubleBox boxC;
	@UiField
    DoubleBox boxD;
	@UiField
	DoubleBox boxE;
	@UiField
	DoubleBox boxF;
	@UiField
	DoubleBox boxG;
	@UiField
	DoubleBox boxH;
	@UiField
	DoubleBox boxI;
	@UiField
	DoubleBox boxJ;
	
	
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
		if (!AonStringUtils.isEmpty(epigrafe.getValue())) {
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
		return reg;
	}


	public void empty() {
	    epigrafe.setValue(null);
	    unit1.setValue(0.0);
	    amount1.setValue(0.0);
	    unit2.setValue(0.0);
	    amount2.setValue(0.0);
	    unit3.setValue(0.0);
	    amount3.setValue(0.0);
	    unit4.setValue(0.0);
	    amount4.setValue(0.0);
	    unit5.setValue(0.0);
	    amount5.setValue(0.0);
	    unit6.setValue(0.0);
	    amount6.setValue(0.0);
	    unit7.setValue(0.0);
	    amount7.setValue(0.0);
	    boxC.setValue(0.0);
	    boxD.setValue(0.0);
		boxE.setValue(0.0);
		boxF.setValue(0.0);
		boxG.setValue(0.0);
		boxH.setValue(0.0);
		boxI.setValue(0.0);
		boxJ.setValue(0.0);
	}
	
	public double getBoxJ() {
		return boxJ.getValue();
	}
}
