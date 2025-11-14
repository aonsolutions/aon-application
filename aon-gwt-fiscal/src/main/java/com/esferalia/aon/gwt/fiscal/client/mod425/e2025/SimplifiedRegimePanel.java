package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.fiscal.mod425.SimpliedRegimeActivity425;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class SimplifiedRegimePanel extends FlowPanel implements HasValueChangeHandlers<SimpliedRegimeActivity425> {

	private AonTextBox epigrafe = new AonTextBox();
	
	private AonDoubleBox unit1 = new AonDoubleBox();
	private AonDoubleBox unit2 = new AonDoubleBox();
	private AonDoubleBox unit3 = new AonDoubleBox();
	private AonDoubleBox unit4 = new AonDoubleBox();
	private AonDoubleBox unit5 = new AonDoubleBox();
	private AonDoubleBox unit6 = new AonDoubleBox();
	private AonDoubleBox unit7 = new AonDoubleBox();
	
	// FALTA - POR AHORA SOLO LAS UNIDADES, QUE ES LO UNICO QUE VA EN EL XML
//	private AonDoubleBox amount1 = new AonDoubleBox();
//	private AonDoubleBox amount2 = new AonDoubleBox();
//	private AonDoubleBox amount3 = new AonDoubleBox();
//	private AonDoubleBox amount4 = new AonDoubleBox();
//	private AonDoubleBox amount5 = new AonDoubleBox();
//	private AonDoubleBox amount6 = new AonDoubleBox();
//	private AonDoubleBox amount7 = new AonDoubleBox();
	
	private AonDoubleBox boxA = new AonDoubleBox(); // Cuota anual devengada por operaciones corrientes
	private AonDoubleBox boxB = new AonDoubleBox(); // Cuotas soportadas por operaciones corrientes 
	private AonDoubleBox boxC = new AonDoubleBox(); // Indice corrector 
	private AonDoubleBox boxD = new AonDoubleBox(); // Diferencia (A-B)*C
	private AonDoubleBox boxE = new AonDoubleBox(); // Porcentaje cuota mínima operaciones corrientes
	private AonDoubleBox boxF = new AonDoubleBox(); // Cuota mínima
	private AonDoubleBox boxG = new AonDoubleBox(); // Cuota anual derivada de régimen simplificado (el importe mayor de D o F)
	
	public SimplifiedRegimePanel(SimpliedRegimeActivity425 regime) {
		paint();
		setValue(regime);
	}

	private void setValue(SimpliedRegimeActivity425 regime) {
		if (regime == null) {
			empty();
			return;
		}
	    epigrafe.setValue(regime.getEpigrafe(),false);
	    unit1.setValue(regime.getUnit1(),false);
	    unit2.setValue(regime.getUnit2(),false);
	    unit3.setValue(regime.getUnit3(),false);
	    unit4.setValue(regime.getUnit4(),false);
	    unit5.setValue(regime.getUnit5(),false);
	    unit6.setValue(regime.getUnit6(),false);
	    unit7.setValue(regime.getUnit7(),false);
	    // FALTA - POR AHORA SOLO LAS UNIDADES
//	    amount1.setValue(regime.getAmount1(),false);
//	    amount2.setValue(regime.getAmount2(),false);
//	    amount3.setValue(regime.getAmount3(),false);
//	    amount4.setValue(regime.getAmount4(),false);
//	    amount5.setValue(regime.getAmount5(),false);
//	    amount6.setValue(regime.getAmount6(),false);
//	    amount7.setValue(regime.getAmount7(),false);
	    
	    boxA.setValue(regime.getBoxA(),false);
	    boxB.setValue(regime.getBoxB(),false);
	    boxC.setValue(regime.getBoxC(),false);
	    boxD.setValue(regime.getBoxD(),false);
		boxE.setValue(regime.getBoxE(),false);
		boxF.setValue(regime.getBoxF(),false);
		boxG.setValue(regime.getBoxG(),false);
	}

	public void fire() {
		SimpliedRegimeActivity425 reg = null;
		if (!AonStringUtils.isBlank(epigrafe.getValue())) {
			reg = new SimpliedRegimeActivity425();
		    reg.setEpigrafe(epigrafe.getValue());
		    reg.setUnit1(unit1.getValue());
		    reg.setUnit2(unit2.getValue());
		    reg.setUnit3(unit3.getValue());
		    reg.setUnit4(unit4.getValue());
		    reg.setUnit5(unit5.getValue());
		    reg.setUnit6(unit6.getValue());
		    reg.setUnit7(unit7.getValue());
		    // FALTA - POR AHORA SOLO LAS UNIDADES
//		    reg.setAmount1(amount1.getValue());
//		    reg.setAmount2(amount2.getValue());
//		    reg.setAmount3(amount3.getValue());
//		    reg.setAmount4(amount4.getValue());
//		    reg.setAmount5(amount5.getValue());
//		    reg.setAmount6(amount6.getValue());
//		    reg.setAmount7(amount7.getValue());
		    
		    reg.setBoxA(boxA.getValue());
		    reg.setBoxB(boxB.getValue());
		    reg.setBoxC(boxC.getValue());
		    reg.setBoxD(boxD.getValue());
			reg.setBoxE(boxE.getValue());
			reg.setBoxF(boxF.getValue());
			reg.setBoxG(boxG.getValue());
		}
		ValueChangeEvent.fire(SimplifiedRegimePanel.this,reg);
	}

	private void empty() {
	    epigrafe.setValue(null,false);

	    unit1.setValue(0.0,false);
	    unit2.setValue(0.0,false);
	    unit3.setValue(0.0,false);
	    unit4.setValue(0.0,false);
	    unit5.setValue(0.0,false);
	    unit6.setValue(0.0,false);
	    unit7.setValue(0.0,false);
	    // FALTA - POR AHORA SOLO LAS UNIDADES
//	    amount1.setValue(0.0,false);
//	    amount2.setValue(0.0,false);
//	    amount3.setValue(0.0,false);
//	    amount4.setValue(0.0,false);
//	    amount5.setValue(0.0,false);
//	    amount6.setValue(0.0,false);
//	    amount7.setValue(0.0,false);
	    
	    boxA.setValue(0.0,false);
	    boxB.setValue(0.0,false);
	    boxC.setValue(0.0,false);
	    boxD.setValue(0.0,false);
		boxE.setValue(0.0,false);
		boxF.setValue(0.0,false);
		boxG.setValue(0.0,false);
	}
	
	private void paint() {
		
		AonDisplayTable tab0 = new AonDisplayTable();
		tab0.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab0.addStyleName(AON.CSS.aonBlockCenter());
		add(tab0);
		
		epigrafe.setVisibleLength(7);
		epigrafe.setMaxLength(5);
		tab0.addRow()
			// FALTA - LA PARTE DE LA DESCRIPCION ESTABA CON 150, ESO ESTA BIEN SI PONEMOS LA DESCRIPCION COMPLETA, LA CAMBIO A 60
//			.addCell(new Label(""), AON.CSS.aonWidth150())
			.addCell(new Label(AON.MSG.epigraph()), AON.CSS.aonWidth60())
//			.addCell(new AonBoxLabel(66), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(epigrafe, AON.CSS.aonWidth80())
			.addCell(new Label(""), AON.CSS.aonWidthAuto(), AON.CSS.aonTextCenter());
		
		tab0.addRow()
			.addCell(new Label(""), AON.CSS.aonWidth60(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(new Label("Unidades"), AON.CSS.aonWidth80(), AON.CSS.aonTextCenter(), AON.CSS.aonFontSmaller())
			.addCell(new Label(""), AON.CSS.aonWidthAuto(), AON.CSS.aonTextCenter());
		
		unit1.setVisibleLength(7);
		unit2.setVisibleLength(7);
		unit3.setVisibleLength(7);
		unit4.setVisibleLength(7);
		unit5.setVisibleLength(7);
		unit6.setVisibleLength(7);
		unit7.setVisibleLength(7);
		
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "1"), AON.CSS.aonWidth60(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit1, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
//			.addCell(new Label(""), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter());
			.addCell(new Label(""), AON.CSS.aonWidthAuto(), AON.CSS.aonTextCenter());
//			.addCell(amount1, AON.CSS.aonWidth120());
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "2"), AON.CSS.aonWidth60(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit2, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidthAuto(), AON.CSS.aonTextCenter());
//			.addCell(amount2, AON.CSS.aonWidth120());
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "3"), AON.CSS.aonWidth60(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit3, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidthAuto(), AON.CSS.aonTextCenter());
//			.addCell(amount3, AON.CSS.aonWidth120());
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "4"), AON.CSS.aonWidth60(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit4, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidthAuto(), AON.CSS.aonTextCenter());
//			.addCell(amount4, AON.CSS.aonWidth120());
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "5"), AON.CSS.aonWidth60(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit5, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidthAuto(), AON.CSS.aonTextCenter());
//			.addCell(amount5, AON.CSS.aonWidth120());
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "6"), AON.CSS.aonWidth60(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit6, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidthAuto(), AON.CSS.aonTextCenter());
//			.addCell(amount6, AON.CSS.aonWidth120());
		tab0.addRow()
			.addCell(new Label(AON.MSG.module() + "7"), AON.CSS.aonWidth60(), AON.CSS.aonTextRight(), AON.CSS.aonPaddingRight())
			.addCell(unit7, AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell(new Label(""), AON.CSS.aonWidthAuto(), AON.CSS.aonTextCenter());
//			.addCell(amount7, AON.CSS.aonWidth120());
		
		boxD.setEnabled(false);
		boxF.setEnabled(false);
		boxG.setEnabled(false);

		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		add(tab1);
		tab1.addRow()
			.addCell(new Label("Cuota anual devengada por operaciones corrientes"), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("A"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxA, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label("Cuotas soportadas por operaciones corrientes"), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("B"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxB, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label("Indice corrector"), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("C"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxC, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label("Diferencia [(A-B)*C]"), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("D"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxD, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label("Porcentaje cuota m\u00EDnima op. corrientes"), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("E"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxE, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label("Cuota m\u00EDnima"), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("F"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxF, AON.CSS.aonWidth120());
		tab1.addRow()
			.addCell(new Label("Cuota anual derivada de r\u00E9gimen simplificado"), AON.CSS.aonWidthAuto(), AON.CSS.aonBorderBottom())
			.addCell(new AonBoxLabel("G"), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(boxG, AON.CSS.aonWidth120());
		
	    epigrafe.addValueChangeHandler(event -> fire());
	    
	    unit1.addValueChangeHandler(event -> fire());
	    unit2.addValueChangeHandler(event -> fire());
	    unit3.addValueChangeHandler(event -> fire());
	    unit4.addValueChangeHandler(event -> fire());
	    unit5.addValueChangeHandler(event -> fire());
	    unit6.addValueChangeHandler(event -> fire());
	    unit7.addValueChangeHandler(event -> fire());
	    // FALTA - POR AHORA SOLO LAS UNIDADES
//	    amount1.addValueChangeHandler(event -> fire());
//	    amount2.addValueChangeHandler(event -> fire());
//	    amount3.addValueChangeHandler(event -> fire());
//	    amount4.addValueChangeHandler(event -> fire());
//	    amount5.addValueChangeHandler(event -> fire());
//	    amount6.addValueChangeHandler(event -> fire());
//	    amount7.addValueChangeHandler(event -> fire());
	    
	    boxA.addValueChangeHandler(event -> fire());
	    boxB.addValueChangeHandler(event -> fire());
	    boxC.addValueChangeHandler(event -> fire());
//	    boxD.addValueChangeHandler(event -> fire());
		boxE.addValueChangeHandler(event -> fire());
//		boxF.addValueChangeHandler(event -> fire());
//		boxG.addValueChangeHandler(event -> fire());
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<SimpliedRegimeActivity425> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
}
