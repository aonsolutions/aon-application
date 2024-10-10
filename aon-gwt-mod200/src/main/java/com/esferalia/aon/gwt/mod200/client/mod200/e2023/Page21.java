// DOCUMENTO DE INGRESO O DEVOLUCION
package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import java.util.Iterator;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIbanTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIbanTextBox.IbanSuggestion;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Model2002023.Model2002023PageCallback;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;

public class Page21 extends PageAbs {

	FlowPanel devPanel;
	FlowPanel payPanel;
	FlowPanel zeroPanel;
	RadioButton devTypeR;
	RadioButton devTypeD;
	RadioButton devTypeV;
	RadioButton payTypeH;
	RadioButton payTypeU;
	RadioButton payTypeI;
	RadioButton payTypeG;
	CheckBox zeroQuota;
	AonDoubleBox amountD;
	AonDoubleBox amountP;
	AonIbanTextBox ibanD;
	AonIbanTextBox ibanP;
	private AonTextBox nrc;
	
	public Page21( Model2002023PageCallback callback ) {
		super(callback);
	}

	@Override
	protected void initializeTable() {
		paint();
	}
	
	private void paint() {
		
		basePanel.clear();
		
		// Liquidación
		
		FlexTable table = addTable(AON.MSG.liquidacion());
		
		int row = 0;		
		row = paintKey(table, Mod2002023Key.LQ552, row, false, true);
		row = paintKey(table, Mod2002023Key.LQ562, row, false, true);
		paintDescription(table, Mod2002023Key.BN621.getDescription() + ": Estado", row, 0, false);
		paintKeyField(table, Mod2002023Key.BN621, row++, 1, false);
		paintDescription(table, "Opci\u00F3n de fraccionamiento art. 19.1 LIS", row++, 0, true);
		paintDescription(table, Mod2002023Key.LQ2489.getDescription() + ": Estado", row, 0, false);
		paintKeyField(table, Mod2002023Key.LQ2489, row, 1, false);
		
		// Panel Devolución
		
		devPanel = new FlowPanel();		
		devPanel.add(getTitle(AON.MSG.payBack()));
		
		FlowPanel devPanel1 = new FlowPanel();		
		devPanel1.addStyleName(AON.CSS.aonMarginLeft());
		devPanel1.addStyleName(AON.CSS.aonMarginTop());
		
		devTypeR = new RadioButton("devTypeButton");		
		devTypeR.setText(AON.MSG.payBackRefuse());
		devTypeR.addClickHandler(event -> {
			callback.getMod200Object().getMod200().setDevType("R");
			callback.markAsDirty();				
		});
		otherInputs.add(devTypeR);

		devTypeD = new RadioButton("devTypeButton");
		devTypeD.addStyleName(AON.CSS.aonMarginLeft());
		devTypeD.setText(AON.MSG.payBackTransfer());
		devTypeD.addClickHandler(event -> {
			callback.getMod200Object().getMod200().setDevType("D");
			callback.markAsDirty();			
		});
		otherInputs.add(devTypeD);

		devTypeV = new RadioButton("devTypeButton");
		devTypeV.addStyleName(AON.CSS.aonMarginLeft());
		devTypeV.setText(AON.MSG.payBackCCT());
		devTypeV.addClickHandler(event -> {
			callback.getMod200Object().getMod200().setDevType("V");
			callback.markAsDirty();				
		});
		
		otherInputs.add(devTypeV);
		
		devPanel1.add(devTypeR);
		devPanel1.add(devTypeD);
		devPanel1.add(devTypeV);
		devPanel.add(devPanel1);
		
		FlowPanel devPanel2 = new FlowPanel();
		devPanel2.addStyleName(AON.CSS.aonMarginLeft());
		devPanel2.addStyleName(AON.CSS.aonMarginTop());
		devPanel2.add(new InlineLabel(AON.MSG.amount()));		
		amountD = new AonDoubleBox();
		amountD.setEnabled(false);
		amountD.addStyleName(AON.CSS.aonMarginLeft());
		devPanel2.add(amountD);		
		devPanel.add(devPanel2);

		FlowPanel devPanel3 = new FlowPanel();
		devPanel3.addStyleName(AON.CSS.aonMarginLeft());
		devPanel3.addStyleName(AON.CSS.aonMarginTop());
		devPanel3.add(new InlineLabel(AON.MSG.iban()));
		
		ibanD = new AonIbanTextBox(getSuggestOracle(),true);
		ibanD.addStyleName(AON.CSS.aonMarginLeft());
		ibanD.addSelectionHandler(event -> {
			Suggestion suggestion = event.getSelectedItem();
			if (suggestion instanceof IbanSuggestion) {
				IbanSuggestion is = (IbanSuggestion) suggestion;
				ibanD.setValue(is.getIbanContainer().getIBan(), is.getIbanContainer().getBic());
				callback.getMod200Object().getMod200().setIban(ibanD.getValue());
				callback.getMod200Object().getMod200().setBic(ibanD.getBic());
			} else {
				ibanD.setValue(suggestion.getReplacementString());	
			}
			callback.markAsDirty();			
		});		
		otherInputs.add(ibanD);
		addValueChangeHandlerIban(ibanD);
		
		devPanel3.add(ibanD);
		devPanel.add(devPanel3);
		
		basePanel.add(devPanel);

		//	Panel Ingreso
		
		payPanel = new FlowPanel(); 
		payPanel.add(getTitle(AON.MSG.deposit()));
		
		FlowPanel payPanel1 = new FlowPanel();		
		payPanel1.addStyleName(AON.CSS.aonMarginLeft());
		payPanel1.addStyleName(AON.CSS.aonMarginTop());		
		
		payTypeI = new RadioButton("devTypeButton");
//		payTypeI.setText(AON.MSG.payInAccount());
		payTypeI.setText("Adeudo en cuenta (Ingreso)");		
		payTypeI.addClickHandler(event -> {
			callback.getMod200Object().getMod200().setPayType("I");
			callback.markAsDirty();				
		});
		otherInputs.add(payTypeI);
		
		payTypeH = new RadioButton("devTypeButton");
		payTypeH.addStyleName(AON.CSS.aonMarginLeft());
//		payTypeH.setText(AON.MSG.cash());
		payTypeH.setText("En efectivo (obsoleto)");
		payTypeH.addClickHandler(event -> {
			callback.getMod200Object().getMod200().setPayType("H");
			callback.markAsDirty();				
		});
		otherInputs.add(payTypeH);
		
		payTypeU = new RadioButton("devTypeButton");
		payTypeU.addStyleName(AON.CSS.aonMarginLeft());
		payTypeU.setText(AON.MSG.directDebit());
		payTypeU.addClickHandler(event -> {
			callback.getMod200Object().getMod200().setPayType("U");
			callback.markAsDirty();				
		});
		otherInputs.add(payTypeU);
		
		payTypeG = new RadioButton("devTypeButton");
		payTypeG.addStyleName(AON.CSS.aonMarginLeft());
		payTypeG.setText(AON.MSG.payCCT());
		payTypeG.addClickHandler(event -> {
			callback.getMod200Object().getMod200().setPayType("G");
			callback.markAsDirty();				
		});
		otherInputs.add(payTypeG);
		
		payPanel1.add(payTypeI);
		payPanel1.add(payTypeH);
		payPanel1.add(payTypeU);
		payPanel1.add(payTypeG);
		payPanel.add(payPanel1);
		
		FlowPanel payPanel2 = new FlowPanel();
		payPanel2.addStyleName(AON.CSS.aonMarginLeft());
		payPanel2.addStyleName(AON.CSS.aonMarginTop());
		payPanel2.add(new InlineLabel(AON.MSG.amount()));		
		amountP = new AonDoubleBox();  
		amountP.setEnabled(false);
		amountP.addStyleName(AON.CSS.aonMarginLeft());
		payPanel2.add(amountP);
		payPanel.add(payPanel2);
		
		FlowPanel payPanel3 = new FlowPanel();
		payPanel3.addStyleName(AON.CSS.aonMarginLeft());
		payPanel3.addStyleName(AON.CSS.aonMarginTop());
		payPanel3.add(new InlineLabel(AON.MSG.iban()));
		
		ibanP = new AonIbanTextBox(getSuggestOracle(),true);
		ibanP.addStyleName(AON.CSS.aonMarginLeft());
		ibanP.addSelectionHandler(event -> {
			Suggestion suggestion = event.getSelectedItem();
			if (suggestion instanceof IbanSuggestion) {
				IbanSuggestion is = (IbanSuggestion) suggestion;
				ibanP.setValue(is.getIbanContainer().getIBan(), is.getIbanContainer().getBic());
				callback.getMod200Object().getMod200().setIban(ibanP.getValue());
				callback.getMod200Object().getMod200().setBic(ibanP.getBic());
			} else {
				ibanP.setValue(suggestion.getReplacementString());
			}
			callback.markAsDirty();
		});		
		otherInputs.add(ibanP);
		addValueChangeHandlerIban(ibanP);		
	
		payPanel3.add(ibanP);
		payPanel.add(payPanel3);
				
		FlowPanel payPanel4 = new FlowPanel();
		payPanel4.addStyleName(AON.CSS.aonMarginLeft());
		payPanel4.addStyleName(AON.CSS.aonMarginTop());
		payPanel4.add(new InlineLabel("NRC"));
		
		nrc = new AonTextBox();
		nrc.addStyleName(AON.CSS.aonMarginLeft());
		nrc.setVisibleLength(22);
		nrc.setMaxLength(22);
		nrc.setValue(callback.getMod200Object().getMod200().getNrc());
		nrc.addValueChangeHandler(event -> {
					callback.getMod200Object().getMod200().setNrc(nrc.getValue());
					callback.markAsDirty();
		});
		otherInputs.add(nrc);
		
		payPanel4.add(nrc);
		payPanel.add(payPanel4);
		
		basePanel.add(payPanel);
		
		// Abono / Compensación
		FlexTable table2 = addTable("Abono / Compensaci\u00F3n");
		paintDescription(table2, "Abono por conversi\u00F3n de activos por impuesto diferido (art. 130 LIS)", 0, 0, true);
		paintKeyField(table2, Mod2002023Key.BN1020, 0, 1, true, "A", false);
		paintDescription(table2, "Compensaci\u00F3n por conversi\u00F3n de activos por impuesto diferido (art. 130 LIS)", 1, 0, true);
		paintKeyField(table2, Mod2002023Key.BN1021, 1, 1, true, "C", false);		
		paintDescription(table2, Mod2002023Key.LQ3318.getDescription(), 2, 0, false);
		paintKeyField(table2, Mod2002023Key.LQ3318, 2, 1, true, "3318", false);
		paintDescription(table2, Mod2002023Key.LQ2490.getDescription(), 3, 0, false);
		paintKeyField(table2, Mod2002023Key.LQ2490, 3, 1, true, "2490", false);
		paintDescription(table2, Mod2002023Key.LQ2493.getDescription(), 4, 0, false);
		paintKeyField(table2, Mod2002023Key.LQ2493, 4, 1, true, "2493", false);
		
		// Cuota Cero
		
		zeroPanel = new FlowPanel();
		zeroPanel.add(getTitle(AON.MSG.zeroQuota()));
		
		zeroQuota = new CheckBox(AON.MSG.zeroQuota());
		zeroQuota.addStyleName(AON.CSS.aonMarginLeft());
		
		zeroPanel.add(zeroQuota);
		basePanel.add(zeroPanel);
				
	}
	
	private void addValueChangeHandlerIban(AonIbanTextBox iban) {
		
		// Controlar posible modificación manual de los campos del IBAN (excepto el primero que no veo forma de modificarlo manualmente) y el BIC 
		Iterator<Widget> arrayOfWidgets = iban.iterator();
		while (arrayOfWidgets.hasNext()){
		  Widget ch = arrayOfWidgets.next();
		  if (ch instanceof FlowPanel) {
			  Iterator<Widget> iterator = ((FlowPanel) ch).iterator();
			  while (iterator.hasNext()){
				  Widget w = iterator.next();
				  if (w instanceof AonTextBox) {					  
					  ((AonTextBox) w).addValueChangeHandler(event -> {
							callback.getMod200Object().getMod200().setIban(iban.getValue());
							callback.getMod200Object().getMod200().setBic(iban.getBic());
							callback.markAsDirty();
						});
				  }
			  }
		  }
		}		
		
	}

	@Override
	public void dump() {
		super.dump();
		dumpPay(callback.getMod200Object().getMod200());
	}
	
	private void dumpPay(Mod2002023 mod200) {
		devTypeR.setValue(false);
		devTypeD.setValue(false);
		devTypeV.setValue(false);
		amountD.setValue(0.0);
		ibanD.setValue(null);
		payTypeH.setValue(false);
		payTypeU.setValue(false);
		payTypeI.setValue(false);
		payTypeG.setValue(false);
		amountP.setValue(0.0);
		ibanP.setValue(null);
		zeroQuota.setValue(false);
		zeroQuota.setEnabled(false);
		if (AonStringUtils.isEmpty(mod200.getResultType())) {
			payPanel.setVisible(true);
			zeroPanel.setVisible(true);
			devPanel.setVisible(true);
		} else if ("D".equals( mod200.getResultType()) ) {
			payPanel.setVisible(false);
			zeroPanel.setVisible(false);
			devPanel.setVisible(true);
			devTypeR.setValue("R".equals(mod200.getDevType()));
			devTypeD.setValue("D".equals(mod200.getDevType()));
			devTypeV.setValue("V".equals(mod200.getDevType()));
			ibanD.setValue(mod200.getIban(),mod200.getBic());
			amountD.setValue( mod200.getAmount() );	
		} else if ("I".equals( mod200.getResultType()) ) {
			payPanel.setVisible(true);
			zeroPanel.setVisible(false);
			devPanel.setVisible(false);
			payTypeU.setValue("U".equals(mod200.getPayType()));
			payTypeH.setValue("H".equals(mod200.getPayType()));
			payTypeI.setValue("I".equals(mod200.getPayType()));
			payTypeG.setValue("G".equals(mod200.getPayType()));
			ibanP.setValue(mod200.getIban(),mod200.getBic());
			amountP.setValue( mod200.getAmount() );	
		} else if ("N".equals( mod200.getResultType()) ) {
			payPanel.setVisible(false);
			zeroPanel.setVisible(true);
			devPanel.setVisible(false);
			zeroQuota.setValue(true);
		}
	}

	class EnterpriseSuggestOracle extends MultiWordSuggestOracle {
		@Override
		public void requestSuggestions(final Request request,
				final Callback callback) {
			
			Page21.this.callback.getMod200Object().getCompanyBanks ( 
					new AsyncCallback<LinkedList<CompanyBank>>() {

						public void onFailure(Throwable caught) {
							Window.alert("Error while getting suggestions.");
						}

						public void onSuccess(LinkedList<CompanyBank> result) {
							LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
							if (result != null) {
								for (final CompanyBank cb : result) {
									suggestions.add(new AonIbanTextBox.IbanSuggestion(cb));
								}
							}
							Response resp = new Response(suggestions);
							callback.onSuggestionsReady(request, resp);
						}
					});
		}
	}
	
	private SuggestOracle getSuggestOracle() {
		return new EnterpriseSuggestOracle();
	}
	
	@Override
	protected boolean isEditable(IMod200Key k) {
		// Caso especial de algunas casillas que siempre van deshabilitadas
		if (k == Mod2002023Key.BN1020 || k == Mod2002023Key.BN1021	|| k == Mod2002023Key.LQ3318 || k == Mod2002023Key.LQ2490 || k ==  Mod2002023Key.LQ2493)
			return false;
		else
			return super.isEditable(k);
	}

}
