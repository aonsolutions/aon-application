package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Base.Model347BaseCallback;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Declared2014.IModel347DeclaredCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Mod347Key;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class Model3472014DeclaredPanel extends SimpleLayoutPanel implements Focusable {

	private static class MediumLabel extends InlineLabel {
		private MediumLabel(String label) {
			super();
			if (AonStringUtils.length(label) > 35) {
				setText(AonStringUtils.abbreviate(label, 35));
				setTitle(label);
			} else {
				setText(label);
			}
			setStyleName(AON.AON_CSS.aonFontMedium());
		}
	}
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
	
	private Model347BaseCallback callbackMod347;
	  
	public Model3472014DeclaredPanel(Mod347Declared declared, IModel347DeclaredCallback callback, Model347BaseCallback cbk) {
		
		callbackMod347 = cbk;
		
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		// NIF Declarado / NIF-IVA Declarado / NIF Representante / Nombre
		
		FlexTable tab1 = new FlexTable();
		tab1.getColumnFormatter().setWidth(0, "100px");
		tab1.getColumnFormatter().setWidth(1, "120px");		
		tab1.getColumnFormatter().setWidth(2, "100px");
		tab1.getColumnFormatter().setWidth(3, "auto");
		
		tab1.setStyleName(AON.AON_CSS.aonWidthAll());
		tab1.addStyleName(AON.AON_CSS.aonNowrap());

		tab1.setWidget(0, 0, new MediumLabel(AON.MSG.document()));
		tab1.setWidget(0, 1, new MediumLabel("NIF-IVA declarado"));
		tab1.setWidget(0, 2, new MediumLabel(AON.MSG.representativeDocument()));		
		tab1.setWidget(0, 3, new MediumLabel(AON.MSG.fullName()));
		
		document.setValue(declared.getDocument());
		document.setMaxLength(9);
		document.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				declared.setDocument(document.getValue());				
				callback.onValueChanged(declared);
			}
		});
		
		// Controlar si se introduce algo en document se deshabilita vatDocument (ambos son incompatibles)
		document.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				vatDocument.setEnabled(AonStringUtils.isBlank(document.getText()));				
			}
			
		});
		
		tab1.setWidget(1, 0, document);		
				
		vatDocument.setVisibleLength(17);		
		vatDocument.setMaxLength(17);
		vatDocument.setStyleName(AON.AON_CSS.aonInputText());
		vatDocument.setValue(declared.getOperatorNif());
		vatDocument.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				declared.setOperatorNif(vatDocument.getValue());
				callback.onValueChanged(declared);
			}
		});
		
		// Controlar si se introduce algo en vatDocument se deshabilita document (ambos son incompatibles)
		vatDocument.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				document.setEnabled(AonStringUtils.isBlank(vatDocument.getText()));				
			}
			
		});		
		tab1.setWidget(1, 1, vatDocument);
		
		DocumentTextBox representativeDocument = new DocumentTextBox();
		representativeDocument.setValue(declared.getRepresentativeDocument());
		representativeDocument.setMaxLength(9);
		representativeDocument.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				declared.setRepresentativeDocument(representativeDocument.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab1.setWidget(1, 2, representativeDocument);
				
		TextBox name = new TextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setStyleName(AON.AON_CSS.aonInputText());		
		name.setValue(declared.getName());
		name.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				declared.setName(name.getValue());
				callback.onTableChanged(declared);
			}
		});
		tab1.setWidget(1, 3, name);
		
		panel.add(tab1);
		
		// Provincia / Pais / Clave
		
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, "110px");
		tab2.getColumnFormatter().setWidth(1, "110px");
		tab2.getColumnFormatter().setWidth(2, "auto");
		
		tab2.setStyleName(AON.AON_CSS.aonWidthAll());
		tab2.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab2.setWidget(0, 0, new MediumLabel(AON.MSG.province()));
		tab2.setWidget(0, 1, new MediumLabel(AON.MSG.country()));
		tab2.setWidget(0, 2, new MediumLabel(AON.MSG.key()));

		ProvinceListBox province = new ProvinceListBox();
		province.setValue(declared.getProvince());
		province.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				declared.setProvince(Province.getByName(province.getSelectedValue()));
				callback.onValueChanged(declared);
			}
		});
		tab2.setWidget(1, 0, province);
				
		CountryListBox country = new CountryListBox();
		country.setWidth("120px");
		country.setValue(declared.getCountry());
		country.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				declared.setCountry(Country.safeValueOf(country.getSelectedValue()));
				callback.onValueChanged(declared);
			}
		});
		tab2.setWidget(1, 1, country);
		
 		key.setValue(declared.getType());
 		key.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				declared.setType(key.getValue());
				setEnabledFields();
				callback.onTableChanged(declared);
			}
		});
		tab2.setWidget(1, 2, key);
		
		panel.add(tab2);

		// Operación de seguro / arrendamiento / iva de caja / isp / deposito aduanero
		
		FlexTable tab3 = new FlexTable();
		tab3.getColumnFormatter().setWidth(0, "90px");
		tab3.getColumnFormatter().setWidth(1, "120px");
		tab3.getColumnFormatter().setWidth(2, "120px");
		tab3.getColumnFormatter().setWidth(3, "80px");		
		tab3.getColumnFormatter().setWidth(4, "auto");
		
		tab3.setStyleName(AON.AON_CSS.aonWidthAll());
		tab3.addStyleName(AON.AON_CSS.aonNowrap());
		
		CheckBox insurance = new CheckBox("Op. seguro");
		insurance.setTitle("Operaci\u00F3n de seguro");
		insurance.setValue(declared.isInsuranceOperation());
		insurance.addClickHandler(new ClickHandler() {			
			
			@Override
			public void onClick(ClickEvent event) {
				declared.setInsuranceOperation(insurance.getValue());
				callback.onValueChanged(declared);				
			}
		});   
		tab3.setWidget(0, 0, insurance);
		
		CheckBox rental = new CheckBox("Arr. local negocio");
		rental.setTitle("Arrendamiento de local de negocio");
		rental.setValue(declared.isBusinessPremiseRental());
		rental.addClickHandler(new ClickHandler() {			
			
			@Override
			public void onClick(ClickEvent event) {
				declared.setBusinessPremiseRental(rental.getValue());
				callback.onValueChanged(declared);				
			}
		});   
		tab3.setWidget(0, 1, rental);
		
		accrual.setText("Op. IVA de caja");
		accrual.setTitle("Operaci\u00F3n IVA criterio de caja");		
		accrual.setValue(declared.isVatAccrual());
		accrual.addClickHandler(new ClickHandler() {			
			
			@Override
			public void onClick(ClickEvent event) {
				declared.setVatAccrual(accrual.getValue());
				setEnabledFields();
				callback.onValueChanged(declared);				
			}
		});   
		tab3.setWidget(0, 2, accrual);
		
		CheckBox isp = new CheckBox("Op. ISP");
		isp.setTitle("Operaci\u00F3n con inversi\u00F3n del sujeto pasivo (solo destinatario de la operaci\u00F3n)");
		isp.setValue(declared.isIsp());
		isp.addClickHandler(new ClickHandler() {			
			
			@Override
			public void onClick(ClickEvent event) {
				declared.setIsp(isp.getValue());
				callback.onValueChanged(declared);				
			}
		});   
		tab3.setWidget(0, 3, isp);
		
		CheckBox deposit = new CheckBox("Op. reg. dep. distinto aduanero");
		deposit.setTitle("Operaci\u00F3n con bienes vinculados o destinados a vincularse al r\u00E9gimen de dep\u00F3sito distinto del aduanero");
		deposit.setValue(declared.isDepositRegime());
		deposit.addClickHandler(new ClickHandler() {			
			
			@Override
			public void onClick(ClickEvent event) {
				declared.setDepositRegime(deposit.getValue());
				callback.onValueChanged(declared);				
			}
		});   
		tab3.setWidget(0, 4, deposit);
		
		panel.add(tab3);
		
		// Importe percibido en metalico / Ejercicio / Importe operaciones criterio de caja
		
		FlexTable tab4 = new FlexTable();
		tab4.getColumnFormatter().setWidth(0, "130px");
		tab4.getColumnFormatter().setWidth(1, "80px");
		tab4.getColumnFormatter().setWidth(2, "auto");
		
		tab4.setStyleName(AON.AON_CSS.aonWidthAll());
		tab4.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab4.setWidget(0, 0, new MediumLabel("Importe perc. en met\u00E1lico"));
		tab4.setWidget(0, 1, new MediumLabel(AON.MSG.fiscalYear()));
		
		MediumLabel label6 = new MediumLabel("Imp. anual op. dev. criterio de caja");
		label6.setTitle("Importe anual de las operaciones devengadas conforme al criterio de caja del IVA");		
		tab4.setWidget(0, 2, label6);
		
		cashAmount.setValue(declared.getCashAmount());
		cashAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				declared.setCashAmount(cashAmount.getValue());
				// Si el importe metalico es cero o esta vacio, dejamos el año vacio
				if (cashAmount.getValue()==null || cashAmount.getValue()==0.0) {			
					cashYear.setValue((Integer)null, true);
				}
				callback.onValueChanged(declared);
			}
		});
		
		// Deshabilitar ejercicio si importe metalico es cero o no tiene contenido
		cashAmount.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				cashYear.setEnabled(cashAmount.getValue() != null && cashAmount.getValue() != 0.0);
			}
			
		});
		
		tab4.setWidget(1, 0, cashAmount);
		
		cashYear.setMaxLength(4);
		cashYear.setVisibleLength(4);
		cashYear.setValue(declared.getCashYear());
		cashYear.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				declared.setCashYear(cashYear.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab4.setWidget(1, 1, cashYear);
				
		DoubleBox accrualAmount = new DoubleBox();		
		accrualAmount.setValue(declared.getVatAccrualAmount());
		accrualAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				declared.setVatAccrualAmount(accrualAmount.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab4.setWidget(1, 2, accrualAmount);
		
		panel.add(tab4);
		
		// Importe de las operaciones (1T, 2T, 3T, 4T, Total Anual)
		
		FlexTable tab5 = new FlexTable();
		tab5.getColumnFormatter().setWidth(0, "120px");
		tab5.getColumnFormatter().setWidth(1, "120px");
		tab5.getColumnFormatter().setWidth(2, "120px");
		tab5.getColumnFormatter().setWidth(3, "120px");		
		tab5.getColumnFormatter().setWidth(4, "auto");
		
		tab5.setStyleName(AON.AON_CSS.aonWidthAll());
		tab5.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab5.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab5.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab5.getFlexCellFormatter().setColSpan(0, 0, 5);
		tab5.setWidget(0, 0, new InlineLabel("Importe de las operaciones"));
		
		tab5.setWidget(1, 0, new MediumLabel("Trimestre 1"));
		tab5.setWidget(1, 1, new MediumLabel("Trimestre 2"));
		tab5.setWidget(1, 2, new MediumLabel("Trimestre 3"));
		tab5.setWidget(1, 3, new MediumLabel("Trimestre 4"));
		tab5.setWidget(1, 4, new MediumLabel("Importe anual"));
		
		firstAmount.setValue(declared.getFirstQuarterAmount());
		firstAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				// Recalcular amount con la suma de los trimestres
				if (!amount.isEnabled()) {					
					double total = firstAmount.getValue()+secondAmount.getValue()+thirdAmount.getValue()+fourthAmount.getValue();
					amount.setValue(total,false);					
					declared.setAmount(total);				
				}
				declared.setFirstQuarterAmount(firstAmount.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab5.setWidget(2, 0, firstAmount);
	
		secondAmount.setValue(declared.getSecondQuarterAmount());
		secondAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				// Recalcular amount con la suma de los trimestres
				if (!amount.isEnabled()) {					
					double total = firstAmount.getValue()+secondAmount.getValue()+thirdAmount.getValue()+fourthAmount.getValue();
					amount.setValue(total,false);
					declared.setAmount(total);				
				}
				declared.setSecondQuarterAmount(secondAmount.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab5.setWidget(2, 1, secondAmount);	
		
		thirdAmount.setValue(declared.getThirdQuarterAmount());
		thirdAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				// Recalcular amount con la suma de los trimestres
				if (!amount.isEnabled()) {					
					double total = firstAmount.getValue()+secondAmount.getValue()+thirdAmount.getValue()+fourthAmount.getValue();
					amount.setValue(total,false);
					declared.setAmount(total);				
				}
				declared.setThirdQuarterAmount(thirdAmount.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab5.setWidget(2, 2, thirdAmount);		
		
		fourthAmount.setValue(declared.getFourthQuarterAmount());
		fourthAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				// Recalcular amount con la suma de los trimestres
				if (!amount.isEnabled()) {					
					double total = firstAmount.getValue()+secondAmount.getValue()+thirdAmount.getValue()+fourthAmount.getValue();
					amount.setValue(total,false);
					declared.setAmount(total);				
				}
				declared.setFourthQuarterAmount(fourthAmount.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab5.setWidget(2, 3, fourthAmount);		
				
		amount.setValue(declared.getAmount());
		amount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				declared.setAmount(amount.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab5.setWidget(2, 4, amount);
		
		panel.add(tab5);
		
		// Importe percibido por transmisiones de inmuebles sujetas a IVA (1T, 2T, 3T, 4T, Total Anual)
		
		FlexTable tab6 = new FlexTable();
		tab6.getColumnFormatter().setWidth(0, "120px");
		tab6.getColumnFormatter().setWidth(1, "120px");
		tab6.getColumnFormatter().setWidth(2, "120px");
		tab6.getColumnFormatter().setWidth(3, "120px");		
		tab6.getColumnFormatter().setWidth(4, "auto");
		
		tab6.setStyleName(AON.AON_CSS.aonWidthAll());
		tab6.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab6.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab6.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab6.getFlexCellFormatter().setColSpan(0, 0, 5);
		tab6.setWidget(0, 0, new InlineLabel("Importe percibido por transmisiones de inmuebles sujetas a IVA"));
		
		tab6.setWidget(1, 0, new MediumLabel("Trimestre 1"));
		tab6.setWidget(1, 1, new MediumLabel("Trimestre 2"));
		tab6.setWidget(1, 2, new MediumLabel("Trimestre 3"));
		tab6.setWidget(1, 3, new MediumLabel("Trimestre 4"));
		tab6.setWidget(1, 4, new MediumLabel("Importe anual"));

		firstAssetAmount.setValue(declared.getAssetFirstQuarterAmount());
		firstAssetAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				// Recalcular assetAmount con la suma de los trimestres
				if (!assetAmount.isEnabled()) {					
					double total = firstAssetAmount.getValue()+secondAssetAmount.getValue()+thirdAssetAmount.getValue()+fourthAssetAmount.getValue();
					assetAmount.setValue(total,false);
					declared.setAssetAmount(total);				
				}
				declared.setAssetFirstQuarterAmount(firstAssetAmount.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab6.setWidget(2, 0, firstAssetAmount);
		
		secondAssetAmount.setValue(declared.getAssetSecondQuarterAmount());
		secondAssetAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				// Recalcular assetAmount con la suma de los trimestres
				if (!assetAmount.isEnabled()) {					
					double total = firstAssetAmount.getValue()+secondAssetAmount.getValue()+thirdAssetAmount.getValue()+fourthAssetAmount.getValue();
					assetAmount.setValue(total,false);
					declared.setAssetAmount(total);				
				}
				declared.setAssetSecondQuarterAmount(secondAssetAmount.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab6.setWidget(2, 1, secondAssetAmount);	
		
		thirdAssetAmount.setValue(declared.getAssetThirdQuarterAmount());
		thirdAssetAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				// Recalcular assetAmount con la suma de los trimestres
				if (!assetAmount.isEnabled()) {					
					double total = firstAssetAmount.getValue()+secondAssetAmount.getValue()+thirdAssetAmount.getValue()+fourthAssetAmount.getValue();
					assetAmount.setValue(total,false);
					declared.setAssetAmount(total);				
				}
				declared.setAssetThirdQuarterAmount(thirdAssetAmount.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab6.setWidget(2, 2, thirdAssetAmount);		
		
		fourthAssetAmount.setValue(declared.getAssetFourthQuarterAmount());
		fourthAssetAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				// Recalcular assetAmount con la suma de los trimestres
				if (!assetAmount.isEnabled()) {					
					double total = firstAssetAmount.getValue()+secondAssetAmount.getValue()+thirdAssetAmount.getValue()+fourthAssetAmount.getValue();
					assetAmount.setValue(total,false);
					declared.setAssetAmount(total);				
				}
				declared.setAssetFourthQuarterAmount(fourthAssetAmount.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab6.setWidget(2, 3, fourthAssetAmount);		
				
		assetAmount.setValue(declared.getAssetAmount());				
		assetAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				declared.setAssetAmount(assetAmount.getValue());
				callback.onValueChanged(declared);
			}
		});
		tab6.setWidget(2, 4, assetAmount);
		
		// Comprobar cuales son los campos que deben dejarse editar, en determinadas ocasiones se cumplimenta solo el importe anual
		setEnabledFields();
		
		panel.add(tab6);

		scroll.setWidget(panel);
		setWidget(scroll);
		
	}
	
	private void setEnabledFields() {
		
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
		String doc = callbackMod347.getMod347().getDocument();
		boolean enabled = (accrual.getValue() == null ? false : accrual.getValue()) || (doc == null ? false : doc.startsWith("H"));		
		
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
	}

	@Override
	public int getTabIndex() {
		return tabIndex;
	}

	@Override
	public void setAccessKey(char key) {
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
