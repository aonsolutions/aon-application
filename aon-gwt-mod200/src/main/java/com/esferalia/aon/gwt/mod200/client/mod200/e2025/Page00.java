// IDENTIFICACION, TIPO DE DECLARACION, RECTIFICATIVA, ESTADOS DE CUENTAS, CARACTERES 
package com.esferalia.aon.gwt.mod200.client.mod200.e2025;

import static com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Character.CHARACTERS_KEYS;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Character.CHARACTER_ALSO_CHECK_MAP;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Character.CHARACTER_INCOMPATIBILITY_MAP;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Character.NOT_SUPPORTED_CHARACTERS;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2025.Model2002025.Model2002025PageCallback;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.EcpnType;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;

public class Page00 extends PageAbs {
	
	private Map<Mod2002025Key, CheckBox> inputsCheckBox;
	
	private AonDocumentTextBox nif;
	private AonTextBox companyName;
	private AonTextBox phone1;
	private AonTextBox phone2;
	private AonTextBox cnae;
	private InlineLabel cnaeLabel;
	
	private CheckBox rectification;
	private AonTextBox rectificationNumber;
	private CheckBox rectificationMotive1;
	private CheckBox rectificationMotive2;
	private CheckBox rectificationMotive3;
	private ListBox periodType;
	private FlowPanel periodPanel;
	private AonDateBox periodStart;
	private AonDateBox periodEnd;
	private ListBox balanceSheetType;
	private ListBox ecpnType;
	private ListBox profitAndLossType;
	private CheckBox agriculturalActivities;
	private InlineLabel receiptNumberLabel;
	private AonTextBox receiptNumber;
	
	public Page00( Model2002025PageCallback callback ) {
		super(callback);		
	}
	
	@Override
	public void dump() {
		
		nif.setValue(callback.getMod200Object().getMod200().getDocument());
		companyName.setValue(callback.getMod200Object().getMod200().getName());
		phone1.setValue(callback.getMod200Object().getMod200().getEnterprisePhone1());
		phone2.setValue(callback.getMod200Object().getMod200().getEnterprisePhone2());
		rectification.setValue(callback.getMod200Object().getMod200().isComplementary());
		rectificationNumber.setValue(callback.getMod200Object().getMod200().getReplacedNumber());
		rectificationMotive1.setValue(callback.getMod200Object().getMod200().getBooleanValue(Mod2002025Key.R0001));
		rectificationMotive2.setValue(callback.getMod200Object().getMod200().getBooleanValue(Mod2002025Key.R0002));
		rectificationMotive3.setValue(callback.getMod200Object().getMod200().getBooleanValue(Mod2002025Key.R0003));
		agriculturalActivities.setValue(callback.getMod200Object().getMod200().getBooleanValue(Mod2002025Key.X0001));
		receiptNumber.setValue(callback.getMod200Object().getMod200().getNumber());
		
		periodType.setSelectedIndex(callback.getMod200Object().getMod200().getPeriodType() - 1 );
		periodPanel.setVisible((periodType.getSelectedIndex() != 0));
		periodStart.setValue(callback.getMod200Object().getMod200().getPeriodStart() );
		periodEnd.setValue(callback.getMod200Object().getMod200().getPeriodEnd() );
		
		if (callback.getMod200Object().getMod200().getBalanceType() == null) {
			callback.getMod200Object().getMod200().setBalanceType(BalanceType.ABREVIADO);
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.C0051, true);
		}
		int index = callback.getMod200Object().getMod200().getBalanceType().ordinal();
		balanceSheetType.setSelectedIndex(index);

		index = callback.getMod200Object().getMod200().getEcpnType().ordinal();
		ecpnType.setSelectedIndex(index);
		
		if (callback.getMod200Object().getMod200().getPygType() == null) {
			callback.getMod200Object().getMod200().setPygType(BalanceType.ABREVIADO);
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.C0054, true);
		}		
		index = callback.getMod200Object().getMod200().getPygType().ordinal();
		profitAndLossType.setSelectedIndex(index);
		
		cnaeLabel.setText(null);
		cnae.setValue(callback.getMod200Object().getMod200().getCnae());
		if (!AonStringUtils.isEmpty(callback.getMod200Object().getMod200().getCnae())) {
			CNAE2009 cnae2009 = CNAE2009.valueOfCode(callback.getMod200Object().getMod200().getCnae());
			cnaeLabel.setText(cnae2009 == null ? null : cnae2009.getDescription());	
		}
		
		for (Mod2002025Key key : inputsCheckBox.keySet()) {
			inputsCheckBox.get(key).setValue(callback.getMod200Object().getMod200().getBooleanValue(key));			
		}	
		
		// El valor del caracter [00027] puede estar en draftMap, pues se modifica durante el cálculo del modelo
		DoubleVariableEx sv = callback.getMod200Object().getMod200().getVariable(Mod2002025Key.C0027);
		if (sv != null && inputsCheckBox.containsKey(Mod2002025Key.C0027)) {
			inputsCheckBox.get(Mod2002025Key.C0027).setValue(AonMathUtils.equals(sv.getValue(), 1.0));
		}
		
		// Hago la llamada despues, porque para setEnabled(), necesito que algunos campos ya contengan el valor
		super.dump();		
				
	}
	
    @Override
    protected void setEnabled() {
    	
    	super.setEnabled();
    	
    	// El número de justificante no se deja modificar y solo se muestra cuando esta presentado y contiene algun valor, es decir se ha presentado de forma directa
        receiptNumberLabel.setVisible((callback.getMod200Object().getMod200().isSent() && AonStringUtils.isNotEmpty(callback.getMod200Object().getMod200().getNumber())));
		receiptNumber.setVisible(receiptNumberLabel.isVisible());
        receiptNumber.setEnabled(false);
    	
    	// Autoliquidación rectificativa
        rectificationNumber.setEnabled(isEditable() && rectification.getValue());
        rectificationMotive1.setEnabled(isEditable() && rectification.getValue());
        rectificationMotive2.setEnabled(isEditable() && rectification.getValue());
        rectificationMotive3.setEnabled(isEditable() && rectification.getValue());
		
		// Determinados campos y los caracteres, se desabilitan si ya está inicializado el modelo
		boolean enabled = !callback.getMod200Object().isInitialized();
		
		periodType.setEnabled(enabled);
		balanceSheetType.setEnabled(enabled);
		ecpnType.setEnabled(enabled);
		profitAndLossType.setEnabled(enabled);
		for (CheckBox check : inputsCheckBox.values()) {
			check.setEnabled(enabled);
		}	
		if (enabled)
			for (Mod2002025Key[] block : CHARACTERS_KEYS)
			  for (Mod2002025Key key : block) {
				if (inputsCheckBox.containsKey(key) && inputsCheckBox.get(key).getValue()) {
					changeAvailability(key);
				}
			  }
		
		// El caracter [00027] siempre está deshabilitado
		if (inputsCheckBox.containsKey(Mod2002025Key.C0027)) {
			inputsCheckBox.get(Mod2002025Key.C0027).setEnabled(false);
		}
		
    }

	@Override
	protected void initializeTable() {
		paint();
	}

	private void paint() {
  		
		otherInputs.clear();
		basePanel.clear();
		
		// IDENTIFICACION

		basePanel.add(getTitle(AON.MSG.identification()));
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);
		
		nif = new AonDocumentTextBox();
		nif.setVisibleLength(9);
		nif.setMaxLength(9);
		nif.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setDocument(nif.getValue());
			callback.markAsDirty();
		});		
		otherInputs.add(nif);
		
		companyName = new AonTextBox();
		companyName.setVisibleLength(45);
		companyName.setMaxLength(45);
		companyName.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setName(companyName.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(companyName);
		
		phone1 = new AonTextBox();
		phone1.setVisibleLength(9);
		phone1.setMaxLength(9);
		phone1.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setEnterprisePhone1(phone1.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(phone1);
		
		phone2 = new AonTextBox();
		phone2.setVisibleLength(9);
		phone2.setMaxLength(9);
		phone2.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setEnterprisePhone2(phone2.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(phone2);
		
		FlowPanel phones = new FlowPanel(); 
		phones.add(phone1);
		phones.add(phone2);
				
		cnae = new AonTextBox();
		cnaeLabel = new InlineLabel();
		
		final AonCnae2009Panel cnae2009Panel = new AonCnae2009Panel();		
		cnae2009Panel.addSelectionHandler(event -> {
			CNAE2009 selected = event.getSelectedItem();
			cnae.setValue(selected.getCode());
			cnaeLabel.setText(selected.getDescription());
			callback.getMod200Object().getMod200().setCnae(cnae.getValue());
			callback.markAsDirty();
		});
		
		cnae.setVisibleLength(5);
		cnae.setMaxLength(5);
		cnae.setReadOnly(true);
		
		AonTableButton cnaeButton = new AonTableButton(AON.MSG.mainActivityCNAE(),AON.CSS.aonIconSearch());
		cnaeButton.addStyleName(AON.CSS.aonMarginLeft());
		cnaeButton.setTitle(AON.MSG.mainActivityCNAE());
		cnaeButton.addClickHandler(event -> cnae2009Panel.onShow());
		otherInputs.add(cnaeButton);
		
		cnaeLabel.setStyleName(AON.CSS.aonMarginLeft());

		FlowPanel cnaePanel = new FlowPanel();
		cnaePanel.add(cnae);
		cnaePanel.add(cnaeButton);
		cnaePanel.add(cnaeLabel);

		periodType = new ListBox();
		periodType.addItem(AON.MSG.periodType1());
		periodType.addItem(AON.MSG.periodType2());
		periodType.addItem(AON.MSG.periodType3());
		periodType.addChangeHandler( event -> {
			periodPanel.setVisible(periodType.getSelectedIndex() != 0);
			callback.getMod200Object().getMod200().setPeriodType(periodType.getSelectedIndex() + 1 );
			callback.markAsDirty();
		});
		otherInputs.add(periodType);

		periodStart = new AonDateBox();
		periodStart.addStyleName(AON.CSS.aonMarginLeft());
		periodStart.addValueChangeHandler( event -> {
			callback.getMod200Object().getMod200().setPeriodStart(periodStart.getValue());			
			callback.markAsDirty();
		});
		otherInputs.add(periodStart);
		
		periodEnd = new AonDateBox();
		periodEnd.addStyleName(AON.CSS.aonMarginLeft());
		periodEnd.addValueChangeHandler( event -> {
			callback.getMod200Object().getMod200().setPeriodEnd(periodEnd.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(periodEnd);
		
		InlineLabel fromLabel = new InlineLabel(AON.MSG.periodLabel());
		fromLabel.setStyleName(AON.CSS.aonMarginLeft());
		
		InlineLabel toLabel = new InlineLabel(AON.MSG.to());
		toLabel.setStyleName(AON.CSS.aonMarginLeft());

		periodPanel = new FlowPanel();
		periodPanel.add(fromLabel);
		periodPanel.add(periodStart);
		periodPanel.add(toLabel);
		periodPanel.add(periodEnd);
		
		agriculturalActivities = new CheckBox();
		agriculturalActivities.addClickHandler(event -> {			
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.X0001, agriculturalActivities.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(agriculturalActivities);
		
		receiptNumberLabel = new InlineLabel("N\u00FAmero de justificante");
		receiptNumber = new AonTextBox();		
		receiptNumber.setVisibleLength(13);
		receiptNumber.setMaxLength(13);
		receiptNumber.setEnabled(false);
		
		tab.addLabelWidgetRow(AON.MSG.document(), nif)
		   .addLabelWidgetRow("Apellidos y nombre o raz\u00F3n social", companyName)
		   .addLabelWidgetRow(AON.MSG.phone(), phones)
		   .addLabelWidgetRow(AON.MSG.mainActivityCNAE(), cnaePanel)
		   .addLabelWidgetRow(AON.MSG.periodType(), periodType)
		   .addLabelWidgetRow("", periodPanel)
		   .addLabelWidgetRow(Mod2002025Key.X0001.getDescription(), agriculturalActivities)
		   .addLabelWidgetRow(receiptNumberLabel, receiptNumber);
		
		// AUTOLIQUIDACION RECTIFICATIVA
		
		basePanel.add(getTitle("Autoliquidaci\u00F3n Rectificativa"));
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		rectification = new CheckBox();
		rectification.addClickHandler(event -> {
			rectificationNumber.setEnabled(rectification.getValue());
			rectificationMotive1.setEnabled(rectification.getValue());
			rectificationMotive2.setEnabled(rectification.getValue());
			rectificationMotive3.setEnabled(rectification.getValue());
			if (!rectification.getValue()) {
				callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.R0001, false);
				callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.R0002, false);
				callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.R0003, false);
				rectificationMotive1.setValue(false, false);
				rectificationMotive2.setValue(false, false);
				rectificationMotive3.setValue(false, false);
				rectificationNumber.setValue("", true);
			}
			callback.getMod200Object().getMod200().setComplementary(rectification.getValue());
			callback.getMod200Object().calculate(); // Marcar o desmarcar este check influye en el cálculo del modelo (Casilla 866)
			callback.markAsDirty();
		});
		otherInputs.add(rectification);
		
		rectificationNumber = new AonTextBox();
		rectificationNumber.addStyleName(AON.CSS.aonMarginLeft());
		rectificationNumber.setVisibleLength(13);
		rectificationNumber.setMaxLength(13);
		rectificationNumber.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setReplacedNumber(rectificationNumber.getValue());			
			callback.markAsDirty();
		});
		
		rectificationMotive1 = new CheckBox();
		rectificationMotive1.addClickHandler(event -> {			
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.R0001, rectificationMotive1.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(rectificationMotive1);
		
		rectificationMotive2 = new CheckBox();
		rectificationMotive2.addClickHandler(event -> {			
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.R0002, rectificationMotive2.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(rectificationMotive2);
		
		rectificationMotive3 = new CheckBox();
		rectificationMotive3.addClickHandler(event -> {			
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.R0003, rectificationMotive3.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(rectificationMotive3);
		
		addLabelWidgetRow600(tab1,"Autoliquidaci\u00F3n rectificativa de otra anterior correspondiente al mismo concepto, ejercicio y per\u00EDodo", rectification);
	    addLabelWidgetRow600(tab1,AON.MSG.previousReceipt(), rectificationNumber);
	    addLabelWidgetRow600(tab1,Mod2002025Key.R0001.getDescription(), rectificationMotive1);
	    addLabelWidgetRow600(tab1,Mod2002025Key.R0002.getDescription(), rectificationMotive2);
	    addLabelWidgetRow600(tab1,Mod2002025Key.R0003.getDescription(), rectificationMotive3);
		
		// ESTADOS DE CUENTAS
		
		basePanel.add(getTitle("Estados de Cuentas"));
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab2);
		
		balanceSheetType = new ListBox();
		balanceSheetType.addItem("Modalidad Normal");
		balanceSheetType.addItem("Modalidad Abreviada");
		balanceSheetType.addItem("Modalidad PYMES");
		balanceSheetType.addChangeHandler( event -> {
			callback.getMod200Object().getMod200().setBalanceType(balanceSheetType.getSelectedIndex());
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.C0050, (callback.getMod200Object().getMod200().getBalanceType() == BalanceType.NORMAL));
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.C0051, (callback.getMod200Object().getMod200().getBalanceType() == BalanceType.ABREVIADO));
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.C0052, (callback.getMod200Object().getMod200().getBalanceType() == BalanceType.PYMES));
			callback.markAsDirty();
		});
		otherInputs.add(balanceSheetType);
		
		ecpnType = new ListBox();
		ecpnType.addItem("Modalidad Normal");
		ecpnType.addItem("Modalidad Abreviado (voluntario)");
		ecpnType.addItem("Modalidad PYMES (voluntario)");
		ecpnType.addItem("No consta");
		ecpnType.addChangeHandler( event -> {
			callback.getMod200Object().getMod200().setEcpnType(ecpnType.getSelectedIndex());
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.C0075, (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.NORMAL));
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.C0076, (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.ABREVIADO));
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.C0077, (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.PYMES));
			callback.markAsDirty();
		});
		otherInputs.add(ecpnType);
		
		profitAndLossType = new ListBox();
		profitAndLossType.addItem("Modalidad Normal");
		profitAndLossType.addItem("Modalidad Abreviada");
		profitAndLossType.addItem("Modalidad PYMES");
		profitAndLossType.addChangeHandler( event -> {
			callback.getMod200Object().getMod200().setPygType(profitAndLossType.getSelectedIndex());
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.C0053, (callback.getMod200Object().getMod200().getPygType() == BalanceType.NORMAL));
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.C0054, (callback.getMod200Object().getMod200().getPygType() == BalanceType.ABREVIADO));
			callback.getMod200Object().getMod200().setBooleanValue(Mod2002025Key.C0055, (callback.getMod200Object().getMod200().getPygType() == BalanceType.PYMES));
			callback.markAsDirty();
		});	
		otherInputs.add(profitAndLossType);
				
		tab2.addLabelWidgetRow(AON.MSG.balanceSheet(), balanceSheetType)
		    .addLabelWidgetRow(AON.MSG.ecpn(), ecpnType)
		    .addLabelWidgetRow(AON.MSG.profitAndLoss(), profitAndLossType);
		
		// CARACTERES DE LA DECLARACION
		
		inputsCheckBox = new HashMap<>();
		
		FlexTable charactersTable1 = new FlexTable();
		FlexTable charactersTable2 = new FlexTable();
		FlexTable charactersTable3 = new FlexTable();
		initializeCharactersTable(charactersTable1, CHARACTERS_KEYS[0]);
		initializeCharactersTable(charactersTable2, CHARACTERS_KEYS[1]);
		initializeCharactersTable(charactersTable3, CHARACTERS_KEYS[2]);
		
		FlexTable tab4 = new FlexTable();
		tab4.addStyleName(AON.CSS.aonWidthAll());
		tab4.getColumnFormatter().setWidth(0, "33%");
		tab4.getColumnFormatter().setWidth(1, "33%");
		tab4.getColumnFormatter().setWidth(2, "33%");
		tab4.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
		tab4.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
		tab4.getCellFormatter().setVerticalAlignment(1, 2, HasVerticalAlignment.ALIGN_TOP);
		
		tab4.setWidget(0, 0, getSubtitle("Tipo de Entidad"));
		tab4.setWidget(1, 0, charactersTable1);
		tab4.setWidget(0, 1, getSubtitle("Reg\u00EDmenes Aplicables"));
		tab4.setWidget(1, 1, charactersTable2);
		tab4.setWidget(0, 2, getSubtitle("Otros caracteres"));
		tab4.setWidget(1, 2, charactersTable3);
		
		basePanel.add(getTitle("CARACTERES DE LA DECLARACION"));
		basePanel.add(tab4);
				
	}
	
	private void initializeCharactersTable(FlexTable table, Mod2002025Key[] declarationCharactersBlock) {
		table.setWidth("100%");
		table.setCellSpacing(0);
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setStyleName(0, AON.AON_CSS.aonWidth40());
		cf.setStyleName(1, AON.AON_CSS.aonWidthAuto());
		
		int row = 0;
		
		for (final Mod2002025Key key : declarationCharactersBlock ) {
			if (key != Mod2002025Key.C0012R) {
			   AonBoxLabel l = new AonBoxLabel( key.getCode() , BOX_LENGTH );
			   table.setWidget(row, 0, l);
			}
			
			final CheckBox check = new CheckBox(key.getDescription() + (NOT_SUPPORTED_CHARACTERS.contains(key)?" (NO)":""));
			check.addClickHandler( event -> {
				if (NOT_SUPPORTED_CHARACTERS.contains(key)) {
					AonMessageDialog.error(AON.MSG.unsupportedCharacter(key.getDescription()));
					check.setValue(false);
				} else {
					callback.getMod200Object().getMod200().setBooleanValue(key, check.getValue());
					changeAvailability(key);
					callback.markAsDirty();
				}
			});
			
			inputsCheckBox.put(key, check);
			check.setStyleName(AON.AON_CSS.aonFiscalCheckbox());
			table.setWidget(row, 1, check);
			row++;
		}
	}
	
	private void changeAvailability(Mod2002025Key key) {
		
		boolean enabled = inputsCheckBox.get(key).getValue();
		
		// Comprobar incompatibilidad de otros caracteres, si este está marcado
		if (CHARACTER_INCOMPATIBILITY_MAP.get(key) != null) {
			for (Mod2002025Key incompatible : CHARACTER_INCOMPATIBILITY_MAP.get(key)) {
				CheckBox check = inputsCheckBox.get(incompatible);
				if (check != null) {
					check.setEnabled(!enabled);
					if (enabled) {
						check.setValue(!enabled);
						callback.getMod200Object().getMod200().setBooleanValue(incompatible, check.getValue());
					}
				}
			}
		}
		
		// Comprobar caracteres que se marcan automaticamente, si este está marcado
		if (enabled && CHARACTER_ALSO_CHECK_MAP.get(key) != null) {
			for (Mod2002025Key alsoCheck : CHARACTER_ALSO_CHECK_MAP.get(key)) {
				CheckBox check = inputsCheckBox.get(alsoCheck);
				if (check != null) {
					check.setValue(true);
					callback.getMod200Object().getMod200().setBooleanValue(alsoCheck, check.getValue());
				}
			}
		}
	}
	
}
