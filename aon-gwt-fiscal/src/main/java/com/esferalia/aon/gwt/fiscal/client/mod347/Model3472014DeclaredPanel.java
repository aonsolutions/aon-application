package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347.Model347Callback;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Declared2014.IModel347DeclaredCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod347Key;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class Model3472014DeclaredPanel extends SimpleLayoutPanel implements Focusable {

	private static final String WIDTH_120PX = "120px";

	private int tabIndex;
	
	private DocumentTextBox document = new DocumentTextBox();
	private TextBox vatDocument = new TextBox();
	private Mod347KeyListBox key = new Mod347KeyListBox();
	private DoubleBox cashAmount = new DoubleBox();
	private IntegerBox cashYear = new IntegerBox();	
	private CheckBox accrual = new CheckBox();	 
	private DoubleBox amount = new DoubleBox(); 		
	private DoubleBox firstAmount = new DoubleBox();
	private DoubleBox secondAmount = new DoubleBox();
	private DoubleBox thirdAmount = new DoubleBox();
	private DoubleBox fourthAmount = new DoubleBox();
	private DoubleBox assetAmount = new DoubleBox(); 		
	private DoubleBox firstAssetAmount = new DoubleBox();
	private DoubleBox secondAssetAmount = new DoubleBox();
	private DoubleBox thirdAssetAmount = new DoubleBox();
	private DoubleBox fourthAssetAmount = new DoubleBox();
	private AonTextBox bdns = new AonTextBox();
	
	Model3472014DeclaredPanel(Model347Callback cbk, Mod347 mod347, Mod347Declared declared, IModel347DeclaredCallback callback) {
		
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		
		// NIF Declarado / NIF-IVA Declarado / NIF Representante / Nombre
		
		FlexTable tab1 = new FlexTable();
		tab1.getColumnFormatter().setWidth(0, "100px");
		tab1.getColumnFormatter().setWidth(1, WIDTH_120PX);		
		tab1.getColumnFormatter().setWidth(2, "100px");
		tab1.getColumnFormatter().setWidth(3, "auto");
		
		tab1.setStyleName(AON.CSS.aonWidthAll());
		tab1.addStyleName(AON.CSS.aonNowrap());

		tab1.setWidget(0, 0, new Model347SmallerLabel(AON.MSG.document()));
		tab1.setWidget(0, 1, new Model347SmallerLabel("NIF-IVA declarado"));
		tab1.setWidget(0, 2, new Model347SmallerLabel(AON.MSG.representativeDocument()));		
		tab1.setWidget(0, 3, new Model347SmallerLabel(AON.MSG.fullName()));
		
		document.setValue(declared.getDocument());
		document.setMaxLength(9);
		document.addValueChangeHandler( event -> {
			declared.setDocument(document.getValue());				
			callback.onValueChanged(declared);
		});
		
		// Controlar si se introduce algo en document se deshabilita vatDocument (ambos son incompatibles)
		document.addKeyUpHandler(event -> vatDocument.setEnabled(AonStringUtils.isBlank(document.getText())));
		
		tab1.setWidget(1, 0, document);		
				
		vatDocument.setVisibleLength(17);		
		vatDocument.setMaxLength(17);
		vatDocument.setStyleName(AON.CSS.aonInputText());
		vatDocument.setValue(declared.getOperatorNif());
		vatDocument.addValueChangeHandler(event -> {
			declared.setOperatorNif(vatDocument.getValue());
			callback.onValueChanged(declared);
		});
		
		// Controlar si se introduce algo en vatDocument se deshabilita document (ambos son incompatibles)
		vatDocument.addKeyUpHandler(event -> document.setEnabled(AonStringUtils.isBlank(vatDocument.getText())));		
		tab1.setWidget(1, 1, vatDocument);
		
		DocumentTextBox representativeDocument = new DocumentTextBox();
		representativeDocument.setValue(declared.getRepresentativeDocument());
		representativeDocument.setMaxLength(9);
		representativeDocument.addValueChangeHandler( event -> {
			declared.setRepresentativeDocument(representativeDocument.getValue());
			callback.onValueChanged(declared);
		});
		tab1.setWidget(1, 2, representativeDocument);
				
		TextBox name = new TextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setStyleName(AON.CSS.aonInputText());		
		name.setValue(declared.getName());
		name.addValueChangeHandler(event -> {
			declared.setName(name.getValue());
			callback.onTableChanged(declared);
		});
		tab1.setWidget(1, 3, name);
		
		panel.add(tab1);
		
		// Provincia / Pais / Clave
		
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, "110px");
		tab2.getColumnFormatter().setWidth(1, "110px");
		tab2.getColumnFormatter().setWidth(2, "auto");
		
		tab2.setStyleName(AON.CSS.aonWidthAll());
		tab2.addStyleName(AON.CSS.aonNowrap());
		
		tab2.setWidget(0, 0, new Model347SmallerLabel(AON.MSG.province()));
		tab2.setWidget(0, 1, new Model347SmallerLabel(AON.MSG.country()));
		tab2.setWidget(0, 2, new Model347SmallerLabel(AON.MSG.key()));

		ProvinceListBox province = new ProvinceListBox();
		province.setValue(declared.getProvince());
		province.addChangeHandler(event -> {
			declared.setProvince( Province.safeValueOf(province.getSelectedIndex()) );				                                    
			callback.onValueChanged(declared);
		});
		tab2.setWidget(1, 0, province);
				
		CountryListBox country = new CountryListBox();
		country.setWidth(WIDTH_120PX);
		country.setValue(declared.getCountry());
		country.addChangeHandler(event -> {
			declared.setCountry(Country.safeValueOf(country.getSelectedValue()));
			callback.onValueChanged(declared);
		});
		tab2.setWidget(1, 1, country);
		
 		key.setValue(declared.getType());
 		key.addChangeHandler( event -> {
			declared.setType(key.getValue());
			setEnabledFields(mod347);
			callback.onTableChanged(declared);
		});
		tab2.setWidget(1, 2, key);
		
		panel.add(tab2);

		// Operación de seguro / arrendamiento / iva de caja / isp / deposito aduanero
		
		FlexTable tab3 = new FlexTable();
		tab3.getColumnFormatter().setWidth(0, "90px");
		tab3.getColumnFormatter().setWidth(1, WIDTH_120PX);
		tab3.getColumnFormatter().setWidth(2, WIDTH_120PX);
		tab3.getColumnFormatter().setWidth(3, "80px");		
		tab3.getColumnFormatter().setWidth(4, "auto");
		
		tab3.setStyleName(AON.CSS.aonWidthAll());
		tab3.addStyleName(AON.CSS.aonNowrap());
		
		CheckBox insurance = new CheckBox("Op. seguro");
		insurance.setTitle("Operaci\u00F3n de seguro");
		insurance.setValue(declared.isInsuranceOperation());
		insurance.addClickHandler(event -> {
			declared.setInsuranceOperation(insurance.getValue());
			callback.onValueChanged(declared);				
		});   
		tab3.setWidget(0, 0, insurance);
		
		CheckBox rental = new CheckBox("Arr. local negocio");
		rental.setTitle("Arrendamiento de local de negocio");
		rental.setValue(declared.isBusinessPremiseRental());
		rental.addClickHandler(event -> {
			declared.setBusinessPremiseRental(rental.getValue());
			callback.onValueChanged(declared);				
		});   
		tab3.setWidget(0, 1, rental);
		
		accrual.setText("Op. IVA de caja");
		accrual.setTitle("Operaci\u00F3n IVA criterio de caja");		
		accrual.setValue(declared.isVatAccrual());
		accrual.addClickHandler(event -> {
			declared.setVatAccrual(accrual.getValue());
			setEnabledFields(mod347);
			callback.onValueChanged(declared);				
		});   
		tab3.setWidget(0, 2, accrual);
		
		CheckBox isp = new CheckBox("Op. ISP");
		isp.setTitle("Operaci\u00F3n con inversi\u00F3n del sujeto pasivo (solo destinatario de la operaci\u00F3n)");
		isp.setValue(declared.isIsp());
		isp.addClickHandler(event -> {
			declared.setIsp(isp.getValue());
			callback.onValueChanged(declared);				
		});   
		tab3.setWidget(0, 3, isp);
		
		CheckBox deposit = new CheckBox("Op. reg. dep. distinto aduanero");
		deposit.setTitle("Operaci\u00F3n con bienes vinculados o destinados a vincularse al r\u00E9gimen de dep\u00F3sito distinto del aduanero");
		deposit.setValue(declared.isDepositRegime());
		deposit.addClickHandler(event -> {
			declared.setDepositRegime(deposit.getValue());
			callback.onValueChanged(declared);				
		});   
		tab3.setWidget(0, 4, deposit);
		
		panel.add(tab3);
		
		// Importe percibido en metalico / Ejercicio / Importe operaciones criterio de caja / BDNS
		
		FlexTable tab4 = new FlexTable();
		tab4.getColumnFormatter().setWidth(0, "130px");
		tab4.getColumnFormatter().setWidth(1, "80px");
		tab4.getColumnFormatter().setWidth(2, "130px");
		tab4.getColumnFormatter().setWidth(3, "auto");
		
		tab4.setStyleName(AON.CSS.aonWidthAll());
		tab4.addStyleName(AON.CSS.aonNowrap());
		
		tab4.setWidget(0, 0, new Model347SmallerLabel("Importe perc. en met\u00E1lico"));
		tab4.setWidget(0, 1, new Model347SmallerLabel(AON.MSG.fiscalYear()));
		
		Model347SmallerLabel label6 = new Model347SmallerLabel("Imp. anual op. crit. caja");
		label6.setTitle("Importe anual de las operaciones devengadas conforme al criterio de caja del IVA");		
		tab4.setWidget(0, 2, label6);
		
		Model347SmallerLabel label7 = new Model347SmallerLabel("N\u00FAm. convocatoria BDNS");
		label7.setVisible(mod347.getYear() >= 2025); // Número de convocatoria BDNS (solo a partir del ejercicio 2025)
		tab4.setWidget(0, 3, label7);
		
		cashAmount.setValue(declared.getCashAmount());
		cashAmount.addValueChangeHandler(event -> {
			declared.setCashAmount(cashAmount.getValue());
			// Si el importe metalico es cero o esta vacio, dejamos el año vacio
			if (cashAmount.getValue()==null || cashAmount.getValue()==0.0) {			
				cashYear.setValue((Integer)null, true);
			}
			callback.onValueChanged(declared);
		});
		
		// Deshabilitar ejercicio si importe metalico es cero o no tiene contenido
		cashAmount.addKeyUpHandler(event -> cashYear.setEnabled(cashAmount.getValue() != null && cashAmount.getValue() != 0.0));
		
		tab4.setWidget(1, 0, cashAmount);
		
		cashYear.setMaxLength(4);
		cashYear.setVisibleLength(4);
		cashYear.setValue(declared.getCashYear());
		cashYear.addValueChangeHandler(event -> {
			declared.setCashYear(cashYear.getValue());
			callback.onValueChanged(declared);
		});
		tab4.setWidget(1, 1, cashYear);
				
		DoubleBox accrualAmount = new DoubleBox();		
		accrualAmount.setValue(declared.getVatAccrualAmount());
		accrualAmount.addValueChangeHandler(event -> {
			declared.setVatAccrualAmount(accrualAmount.getValue());
			callback.onValueChanged(declared);
		});
		tab4.setWidget(1, 2, accrualAmount);
		
		bdns.setVisible(mod347.getYear() >= 2025);
		bdns.setMaxLength(6);
		bdns.setVisibleLength(6);
		bdns.setValue(declared.getBdns());
		bdns.addValueChangeHandler(event -> {
			declared.setBdns(bdns.getValue());
			callback.onValueChanged(declared);
		});
		tab4.setWidget(1, 3, bdns);
		
		panel.add(tab4);
		
		// Importe de las operaciones (1T, 2T, 3T, 4T, Total Anual, Botón info)
		
		FlexTable tab5 = new FlexTable();
		tab5.getColumnFormatter().setWidth(0, WIDTH_120PX);
		tab5.getColumnFormatter().setWidth(1, WIDTH_120PX);
		tab5.getColumnFormatter().setWidth(2, WIDTH_120PX);
		tab5.getColumnFormatter().setWidth(3, WIDTH_120PX);		
		tab5.getColumnFormatter().setWidth(4, WIDTH_120PX);
		tab5.getColumnFormatter().setWidth(5, "auto");
		
		tab5.setStyleName(AON.CSS.aonWidthAll());
		tab5.addStyleName(AON.CSS.aonNowrap());
		
		tab5.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab5.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab5.getFlexCellFormatter().setColSpan(0, 0, 6);
		tab5.setWidget(0, 0, new InlineLabel("Importe de las operaciones"));
		
		tab5.setWidget(1, 0, new Model347SmallerLabel("Trimestre 1"));
		tab5.setWidget(1, 1, new Model347SmallerLabel("Trimestre 2"));
		tab5.setWidget(1, 2, new Model347SmallerLabel("Trimestre 3"));
		tab5.setWidget(1, 3, new Model347SmallerLabel("Trimestre 4"));
		tab5.setWidget(1, 4, new Model347SmallerLabel("Importe anual"));
		
		firstAmount.setValue(declared.getFirstQuarterAmount());
		firstAmount.addValueChangeHandler(event -> {
			// Recalcular amount con la suma de los trimestres
			if (!amount.isEnabled()) {					
				double total = firstAmount.getValue()+secondAmount.getValue()+thirdAmount.getValue()+fourthAmount.getValue();
				amount.setValue(total,false);					
				declared.setAmount(total);				
			}
			declared.setFirstQuarterAmount(firstAmount.getValue());
			callback.onValueChanged(declared);
		});
		tab5.setWidget(2, 0, firstAmount);
	
		secondAmount.setValue(declared.getSecondQuarterAmount());
		secondAmount.addValueChangeHandler(event -> {
			// Recalcular amount con la suma de los trimestres
			if (!amount.isEnabled()) {					
				double total = firstAmount.getValue()+secondAmount.getValue()+thirdAmount.getValue()+fourthAmount.getValue();
				amount.setValue(total,false);
				declared.setAmount(total);				
			}
			declared.setSecondQuarterAmount(secondAmount.getValue());
			callback.onValueChanged(declared);
		});
		tab5.setWidget(2, 1, secondAmount);	
		
		thirdAmount.setValue(declared.getThirdQuarterAmount());
		thirdAmount.addValueChangeHandler(event -> {
			// Recalcular amount con la suma de los trimestres
			if (!amount.isEnabled()) {					
				double total = firstAmount.getValue()+secondAmount.getValue()+thirdAmount.getValue()+fourthAmount.getValue();
				amount.setValue(total,false);
				declared.setAmount(total);				
			}
			declared.setThirdQuarterAmount(thirdAmount.getValue());
			callback.onValueChanged(declared);
		});
		tab5.setWidget(2, 2, thirdAmount);		
		
		fourthAmount.setValue(declared.getFourthQuarterAmount());
		fourthAmount.addValueChangeHandler(event -> {
			// Recalcular amount con la suma de los trimestres
			if (!amount.isEnabled()) {					
				double total = firstAmount.getValue()+secondAmount.getValue()+thirdAmount.getValue()+fourthAmount.getValue();
				amount.setValue(total,false);
				declared.setAmount(total);				
			}
			declared.setFourthQuarterAmount(fourthAmount.getValue());
			callback.onValueChanged(declared);
		});
		tab5.setWidget(2, 3, fourthAmount);		
				
		amount.setValue(declared.getAmount());
		amount.addValueChangeHandler(event -> {
			declared.setAmount(amount.getValue());
			callback.onValueChanged(declared);
		});
		tab5.setWidget(2, 4, amount);
		
		AonTableButton button = new AonTableButton("Ver desglose en facturas", AON.CSS.aonIconData());
		button.setTabIndex(-2); // NO FOCUS
		button.addClickHandler(event -> cbk.showInvoiceVatBreakdownInfo(button, mod347, declared, FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN));		
		tab5.setWidget(2, 5, button);
		
		panel.add(tab5);
		
		// Vaciar y cerrar el panel de informacion de desglose 
		cbk.cleanInfoPanel();
		
		// Importe percibido por transmisiones de inmuebles sujetas a IVA (1T, 2T, 3T, 4T, Total Anual)
		
		FlexTable tab6 = new FlexTable();
		tab6.getColumnFormatter().setWidth(0, WIDTH_120PX);
		tab6.getColumnFormatter().setWidth(1, WIDTH_120PX);
		tab6.getColumnFormatter().setWidth(2, WIDTH_120PX);
		tab6.getColumnFormatter().setWidth(3, WIDTH_120PX);		
		tab6.getColumnFormatter().setWidth(4, "auto");
		
		tab6.setStyleName(AON.CSS.aonWidthAll());
		tab6.addStyleName(AON.CSS.aonNowrap());
		
		tab6.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab6.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab6.getFlexCellFormatter().setColSpan(0, 0, 5);
		tab6.setWidget(0, 0, new InlineLabel("Importe percibido por transmisiones de inmuebles sujetas a IVA"));
		
		tab6.setWidget(1, 0, new Model347SmallerLabel("Trimestre 1"));
		tab6.setWidget(1, 1, new Model347SmallerLabel("Trimestre 2"));
		tab6.setWidget(1, 2, new Model347SmallerLabel("Trimestre 3"));
		tab6.setWidget(1, 3, new Model347SmallerLabel("Trimestre 4"));
		tab6.setWidget(1, 4, new Model347SmallerLabel("Importe anual"));

		firstAssetAmount.setValue(declared.getAssetFirstQuarterAmount());
		firstAssetAmount.addValueChangeHandler(event -> {
			// Recalcular assetAmount con la suma de los trimestres
			if (!assetAmount.isEnabled()) {					
				double total = firstAssetAmount.getValue()+secondAssetAmount.getValue()+thirdAssetAmount.getValue()+fourthAssetAmount.getValue();
				assetAmount.setValue(total,false);
				declared.setAssetAmount(total);				
			}
			declared.setAssetFirstQuarterAmount(firstAssetAmount.getValue());
			callback.onValueChanged(declared);
		});
		tab6.setWidget(2, 0, firstAssetAmount);
		
		secondAssetAmount.setValue(declared.getAssetSecondQuarterAmount());
		secondAssetAmount.addValueChangeHandler(event -> {
			// Recalcular assetAmount con la suma de los trimestres
			if (!assetAmount.isEnabled()) {					
				double total = firstAssetAmount.getValue()+secondAssetAmount.getValue()+thirdAssetAmount.getValue()+fourthAssetAmount.getValue();
				assetAmount.setValue(total,false);
				declared.setAssetAmount(total);				
			}
			declared.setAssetSecondQuarterAmount(secondAssetAmount.getValue());
			callback.onValueChanged(declared);
		});
		tab6.setWidget(2, 1, secondAssetAmount);	
		
		thirdAssetAmount.setValue(declared.getAssetThirdQuarterAmount());
		thirdAssetAmount.addValueChangeHandler(event -> {
			// Recalcular assetAmount con la suma de los trimestres
			if (!assetAmount.isEnabled()) {					
				double total = firstAssetAmount.getValue()+secondAssetAmount.getValue()+thirdAssetAmount.getValue()+fourthAssetAmount.getValue();
				assetAmount.setValue(total,false);
				declared.setAssetAmount(total);				
			}
			declared.setAssetThirdQuarterAmount(thirdAssetAmount.getValue());
			callback.onValueChanged(declared);
		});
		tab6.setWidget(2, 2, thirdAssetAmount);		
		
		fourthAssetAmount.setValue(declared.getAssetFourthQuarterAmount());
		fourthAssetAmount.addValueChangeHandler(event -> {
			// Recalcular assetAmount con la suma de los trimestres
			if (!assetAmount.isEnabled()) {					
				double total = firstAssetAmount.getValue()+secondAssetAmount.getValue()+thirdAssetAmount.getValue()+fourthAssetAmount.getValue();
				assetAmount.setValue(total,false);
				declared.setAssetAmount(total);				
			}
			declared.setAssetFourthQuarterAmount(fourthAssetAmount.getValue());
			callback.onValueChanged(declared);
		});
		tab6.setWidget(2, 3, fourthAssetAmount);		
				
		assetAmount.setValue(declared.getAssetAmount());				
		assetAmount.addValueChangeHandler(event -> {
			declared.setAssetAmount(assetAmount.getValue());
			callback.onValueChanged(declared);
		});
		tab6.setWidget(2, 4, assetAmount);
		
		// Comprobar cuales son los campos que deben dejarse editar, en determinadas ocasiones se cumplimenta solo el importe anual
		setEnabledFields(mod347);
		
		panel.add(tab6);

		scroll.setWidget(panel);
		setWidget(scroll);
		
	}
	
	private void setEnabledFields(Mod347 mod347) {
		
		// Document y vatDocument son mutuamente excluyentes
		document.setEnabled(AonStringUtils.isBlank(vatDocument.getValue()));
		vatDocument.setEnabled(AonStringUtils.isBlank(document.getValue()));		
		
		// Importe percibido en metalico y ejercicio (solo claves B, C y F)
		cashAmount.setEnabled( key.getValue() == Mod347Key.B || key.getValue() == Mod347Key.C || key.getValue() == Mod347Key.F);
		cashYear.setEnabled(cashAmount.isEnabled() && cashAmount.getValue() != null && cashAmount.getValue() != 0.0);
		if (!cashAmount.isEnabled()) {
			cashAmount.setValue(null, true);			
		}
		if (!cashYear.isEnabled()) {			
			cashYear.setValue((Integer)null, true);
		}
		
		// Habilita total importes y deshabilita los trimestres, si es criterio de caja o NIF declarante comienza por H
		boolean enabled = (accrual.getValue() || mod347.getDocument() == null || mod347.getDocument().startsWith("H"));		
		
		amount.setEnabled(enabled); 		
		firstAmount.setEnabled(!amount.isEnabled());
		secondAmount.setEnabled(!amount.isEnabled());
		thirdAmount.setEnabled(!amount.isEnabled());
		fourthAmount.setEnabled(!amount.isEnabled());
		
		if (amount.isEnabled()) {
			// Si está habilitado el total, se dejan a cero los importes de los trimestres
			firstAmount.setValue(0.0,true);
			secondAmount.setValue(0.0,true);
			thirdAmount.setValue(0.0,true);
			fourthAmount.setValue(0.0,true);
		}
		else {
			// Si está deshabilitado y los trimestres están a cero
			// se iguala el primer trimestre a lo que tenga el total
			if (firstAmount.getValue() == 0.0 &&
				secondAmount.getValue() == 0.0 &&
				thirdAmount.getValue() == 0.0 &&
				fourthAmount.getValue() == 0.0)
				firstAmount.setValue(amount.getValue(),true);
		}
		
		// Importe percibido por trasmisiones (solo claves B, C y F)
		if (key.getValue() != Mod347Key.B && key.getValue() != Mod347Key.C && key.getValue() != Mod347Key.F) {
			firstAssetAmount.setEnabled(false);
			secondAssetAmount.setEnabled(false);
			thirdAssetAmount.setEnabled(false);
			fourthAssetAmount.setEnabled(false);
			assetAmount.setEnabled(false);
			
			firstAssetAmount.setValue(0.0,true);
			secondAssetAmount.setValue(0.0,true);
			thirdAssetAmount.setValue(0.0,true);
			fourthAssetAmount.setValue(0.0,true);
			assetAmount.setValue(0.0,true);					
		}
		else {
			// Ver si se habilita el total o los trimestres
			assetAmount.setEnabled(enabled);
			firstAssetAmount.setEnabled(!assetAmount.isEnabled());
			secondAssetAmount.setEnabled(!assetAmount.isEnabled());
			thirdAssetAmount.setEnabled(!assetAmount.isEnabled());
			fourthAssetAmount.setEnabled(!assetAmount.isEnabled());

			if (assetAmount.isEnabled()) {
				// Si está habilitado el total, se dejan a cero los importes de
				// los trimestres
				firstAssetAmount.setValue(0.0, true);
				secondAssetAmount.setValue(0.0, true);
				thirdAssetAmount.setValue(0.0, true);
				fourthAssetAmount.setValue(0.0, true);
			} else {
				// Si está deshabilitado y los trimestres están a cero
				// se iguala el primer trimestre a lo que tenga el total
				if (firstAssetAmount.getValue() == 0.0 && 
					secondAssetAmount.getValue() == 0.0 && 
					thirdAssetAmount.getValue() == 0.0 && 
					fourthAssetAmount.getValue() == 0.0)
					firstAssetAmount.setValue(assetAmount.getValue(), true);
			}
		}
		
		// BDNS solo si esta visible (a partir del ejercicio 2025) y si es clave E, si está deshabilitado se limpia el campo
		bdns.setEnabled( bdns.isVisible() && key.getValue() == Mod347Key.E );
		if (!bdns.isEnabled()) {
			bdns.setValue(null, true);
		}
		
	}

	@Override
	public int getTabIndex() {
		return tabIndex;
	}

	@Override
	public void setAccessKey(char key) {
		// Nothing
	}

	@Override
	public void setFocus(boolean focused) {
		document.setFocus(true);
	}

	@Override
	public void setTabIndex(int index) {
		tabIndex = index;
	}

}
