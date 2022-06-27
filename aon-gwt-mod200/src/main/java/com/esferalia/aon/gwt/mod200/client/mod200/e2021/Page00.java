// IDENTIFICACION, TIPO DE DECLARACION, CARACTERES 
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import static com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Character.CHARACTERS_KEYS;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Character.CHARACTER_ALSO_CHECK_MAP;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Character.CHARACTER_INCOMPATIBILITY_MAP;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Mod2002021Object.IMod200ChangeListener;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.EcpnType;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;

public class Page00 extends PageAbs {
	
	public static final HashSet<Mod2002021Key> NOT_SUPPORTED_CHARACTERS = new HashSet<Mod2002021Key>();
	static {
		NOT_SUPPORTED_CHARACTERS.add(Mod2002021Key.C0003);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002021Key.C0004);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002021Key.C0007);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002021Key.C0024);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002021Key.C0025);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002021Key.C0035);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002021Key.C0036);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002021Key.C0058);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002021Key.C0061);
	};

	public static final Mod2002021Key[] DECLARATION_CHARACTERS_BLOCK1 = new Mod2002021Key[] {
		Mod2002021Key.C0001,
		Mod2002021Key.C0002,
		Mod2002021Key.C0080,
		Mod2002021Key.C0003,
		Mod2002021Key.C0004,
		Mod2002021Key.C0005,		
		Mod2002021Key.C0011,
		Mod2002021Key.C0013,
		Mod2002021Key.C0014,
		Mod2002021Key.C0017,
		Mod2002021Key.C0018,		
		Mod2002021Key.C0019,
		Mod2002021Key.C0021,
		Mod2002021Key.C0023,
		Mod2002021Key.C0024,
		Mod2002021Key.C0025,
		Mod2002021Key.C0031,
		Mod2002021Key.C0032,
		Mod2002021Key.C0036,
		Mod2002021Key.C0048,
		Mod2002021Key.C0058,
		Mod2002021Key.C0060,
		Mod2002021Key.C0066,
		Mod2002021Key.C0078
	};

	public static final Mod2002021Key[] DECLARATION_CHARACTERS_BLOCK2 = new Mod2002021Key[] {
		Mod2002021Key.C0006,
		Mod2002021Key.C0015,
		Mod2002021Key.C0079,
		Mod2002021Key.C0022,
		Mod2002021Key.C0028,
		Mod2002021Key.C0047,
		Mod2002021Key.C0049,
		Mod2002021Key.C0035,		
		Mod2002021Key.C0029,
		Mod2002021Key.C0033,
		Mod2002021Key.C0034,
		Mod2002021Key.C0038,
		Mod2002021Key.C0046,
		Mod2002021Key.C0012,
		Mod2002021Key.C0064,
		Mod2002021Key.C0057,
		Mod2002021Key.C0012R,		
		Mod2002021Key.C0062,
		Mod2002021Key.C0020
	};
	
	public static final Mod2002021Key[] DECLARATION_CHARACTERS_BLOCK3 = new Mod2002021Key[] {
		Mod2002021Key.C0007,
		Mod2002021Key.C0009,
		Mod2002021Key.C0010,
		Mod2002021Key.C0081,
		Mod2002021Key.C0082,
		Mod2002021Key.C0016,
		Mod2002021Key.C0026,
		Mod2002021Key.C0027,
		Mod2002021Key.C0030,
		Mod2002021Key.C0039,
		Mod2002021Key.C0043,
		Mod2002021Key.C0045,
		Mod2002021Key.C0063,
		Mod2002021Key.C0071,
		Mod2002021Key.C0070,
		Mod2002021Key.C0059,
		Mod2002021Key.C0065,
		Mod2002021Key.C0067,
		Mod2002021Key.C0072,
		Mod2002021Key.C0073,
		Mod2002021Key.C0037,
		Mod2002021Key.C0044,
		Mod2002021Key.C0074
	};
	
	private Map<Mod2002021Key, CheckBox> inputs = new HashMap<Mod2002021Key, CheckBox>();
	
	private AonDocumentTextBox nif = new AonDocumentTextBox();
	private AonTextBox companyName = new AonTextBox();
	private AonTextBox phone1 = new AonTextBox();
	private AonTextBox phone2 = new AonTextBox();
	private AonTextBox cnae = new AonTextBox();
	private InlineLabel cnaeLabel = new InlineLabel();
	private CheckBox complementary = new CheckBox();
	private AonTextBox complementaryReceipt = new AonTextBox();
	private ListBox periodType = new ListBox();
	private FlowPanel periodPanel = new FlowPanel();
	private AonDateBox periodStart = new AonDateBox();
	private AonDateBox periodEnd = new AonDateBox();
	private ListBox balanceSheetType = new ListBox();
	private ListBox ecpnType = new ListBox();
	private ListBox profitAndLossType = new ListBox();
	private AonDoubleBox c041 = new AonDoubleBox();
	private AonDoubleBox c042 = new AonDoubleBox();	
	
	public Page00( Model200PageCallback callback ) {
		super(callback);
		addBasePanel();
		initializeTable();
		
		callback.getMod200Object().register( new IMod200ChangeListener() {
			
			@Override
			public void mod200Changed(Mod2002021 mod200) {
				DoubleVariableEx sv = mod200.getVariable(Mod2002021Key.C0027);
				if (sv != null && inputs.containsKey( Mod2002021Key.C0027 )) {
					inputs.get( Mod2002021Key.C0027 ).setValue( AonMathUtils.equals(sv.getValue() , 1.0) );
				}
			}
		});
	}
	
	@Override
	public void dump() {
		super.dump();
		nif.setValue(callback.getMod200Object().getMod200().getDocument());
		companyName.setValue(callback.getMod200Object().getMod200().getName());
		phone1.setValue(callback.getMod200Object().getMod200().getEnterprisePhone1());
		phone2.setValue(callback.getMod200Object().getMod200().getEnterprisePhone2());
		complementary.setValue(callback.getMod200Object().getMod200().isComplementary());
		complementaryReceipt.setValue(callback.getMod200Object().getMod200().getReplacedNumber());
		complementaryReceipt.setEnabled(complementary.getValue());
		periodType.setSelectedIndex(callback.getMod200Object().getMod200().getPeriodType() - 1 );
		periodPanel.setVisible((periodType.getSelectedIndex() != 0));
		periodStart.setValue(callback.getMod200Object().getMod200().getPeriodStart() );
		periodEnd.setValue(callback.getMod200Object().getMod200().getPeriodEnd() );
		int index = callback.getMod200Object().getMod200().getBalanceType().ordinal();
		balanceSheetType.setSelectedIndex(index);
		index = callback.getMod200Object().getMod200().getEcpnType().ordinal();
		ecpnType.setSelectedIndex(index);
		index = callback.getMod200Object().getMod200().getPygType().ordinal();
		profitAndLossType.setSelectedIndex(index);
		
		DoubleVariableEx dv = callback.getMod200Object().getMod200().getKeysMap().get(Mod2002021Key.C0041);
		Double value = 0.0;
		if (dv != null) {
			value = dv.getValue();
		}
		c041.setValue(value);
		
		dv = callback.getMod200Object().getMod200().getKeysMap().get(Mod2002021Key.C0042);
		value = 0.0;
		if (dv != null) {
			value = dv.getValue();
		}
		c042.setValue(value);
		
		for (CheckBox check : inputs.values()) {
			check.setValue(false);
		}
		
		for (Mod2002021Key key : CHARACTERS_KEYS) {
			DoubleVariableEx sv = callback.getMod200Object().getMod200().getKeysMap().get(key);
			if (sv != null && inputs.containsKey( key )) {
				boolean checked = AonMathUtils.equals(sv.getValue() , 1.0);
				inputs.get( key ).setValue( checked);
			}
		}
		
		cnaeLabel.setText(null);
		cnae.setValue(callback.getMod200Object().getMod200().getCnae());
		if (!AonStringUtils.isEmpty(callback.getMod200Object().getMod200().getCnae())) {
			CNAE2009 cnae = CNAE2009.valueOfCode(callback.getMod200Object().getMod200().getCnae());
			cnaeLabel.setText(cnae==null?null:cnae.getDescription());	
		}
		
	}

	@Override
	protected void populate() {
		callback.getMod200Object().getMod200().setDocument(nif.getValue());
		callback.getMod200Object().getMod200().setName(companyName.getValue());
		callback.getMod200Object().getMod200().setEnterprisePhone1(phone1.getValue());
		callback.getMod200Object().getMod200().setEnterprisePhone2(phone2.getValue());
		callback.getMod200Object().getMod200().setComplementary(complementary.getValue());
		callback.getMod200Object().getMod200().setReplacedNumber(complementaryReceipt.getValue());
		callback.getMod200Object().getMod200().setPeriodType(periodType.getSelectedIndex() + 1 );
		callback.getMod200Object().getMod200().setPeriodStart( periodStart.getValue() );
		callback.getMod200Object().getMod200().setPeriodEnd( periodEnd.getValue() );
		callback.getMod200Object().getMod200().setBalanceType( balanceSheetType.getSelectedIndex() );
		callback.getMod200Object().getMod200().setEcpnType( ecpnType.getSelectedIndex() );
		callback.getMod200Object().getMod200().setPygType( profitAndLossType.getSelectedIndex() );
		callback.getMod200Object().getMod200().setCnae(cnae.getValue());
		
		DoubleVariableEx bv = null;
		for (Mod2002021Key key : inputs.keySet()) {
			bv = new DoubleVariableEx( key );
			bv.setValue(inputs.get(key).getValue());
			callback.getMod200Object().getMod200().addVariable(bv);
		}
		
		bv = new DoubleVariableEx( Mod2002021Key.C0050 );
		bv.setValue((callback.getMod200Object().getMod200().getBalanceType() == BalanceType.NORMAL));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariableEx( Mod2002021Key.C0051 );
		bv.setValue((callback.getMod200Object().getMod200().getBalanceType() == BalanceType.ABREVIADO));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariableEx( Mod2002021Key.C0052 );
		bv.setValue((callback.getMod200Object().getMod200().getBalanceType() == BalanceType.PYMES));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariableEx( Mod2002021Key.C0075 );
		bv.setValue((callback.getMod200Object().getMod200().getEcpnType() == EcpnType.NORMAL));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariableEx( Mod2002021Key.C0076 );
		bv.setValue((callback.getMod200Object().getMod200().getEcpnType() == EcpnType.ABREVIADO));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariableEx( Mod2002021Key.C0077 );
		bv.setValue((callback.getMod200Object().getMod200().getEcpnType() == EcpnType.PYMES));
		callback.getMod200Object().getMod200().addVariable(bv);
					
		bv = new DoubleVariableEx( Mod2002021Key.C0053 );
		bv.setValue((callback.getMod200Object().getMod200().getPygType() == BalanceType.NORMAL));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariableEx( Mod2002021Key.C0054 );
		bv.setValue((callback.getMod200Object().getMod200().getPygType() == BalanceType.ABREVIADO));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariableEx( Mod2002021Key.C0055 );
		bv.setValue((callback.getMod200Object().getMod200().getPygType() == BalanceType.PYMES));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariableEx( Mod2002021Key.C0041 );
		bv.setValue( c041.getValue() );
		callback.getMod200Object().getMod200().addVariable(bv);

		bv = new DoubleVariableEx( Mod2002021Key.C0042 );
		bv.setValue( c042.getValue() );
		callback.getMod200Object().getMod200().addVariable(bv);
		
	}

	@Override
	protected void initializeTable() {
		paint();
	}

	private void changeAvailability(Mod2002021Key key) {
		boolean enabled = inputs.get(key).getValue();
		if (CHARACTER_INCOMPATIBILITY_MAP.get(key) != null) {
			for (Mod2002021Key incompatible : CHARACTER_INCOMPATIBILITY_MAP.get(key)) {
				CheckBox check = inputs.get(incompatible);
				if (check != null) {
					check.setEnabled(!enabled);
					if (enabled) {
						check.setValue(!enabled);
					}
				}
			}
		}
		if (enabled && CHARACTER_ALSO_CHECK_MAP.get(key) != null) {
			for (Mod2002021Key alsoCheck : CHARACTER_ALSO_CHECK_MAP.get(key)) {
				CheckBox check = inputs.get(alsoCheck);
				if (check != null) {
					check.setValue(true);	
				}
			}
		}
	}

	protected void enableCharacters( boolean enabled) {
		periodType.setEnabled(enabled);
		balanceSheetType.setEnabled(enabled);
		ecpnType.setEnabled(enabled);
		profitAndLossType.setEnabled(enabled);
		for (CheckBox check : inputs.values()) {
			check.setEnabled(enabled);
		}		
		for (Mod2002021Key key : CHARACTERS_KEYS) {
			if (inputs.containsKey( key ) && inputs.get( key ).getValue()) {
				changeAvailability(key);
			}
		}
	}
	
	private void paint() {
		
		// IDENTIFICACION 
		
		basePanel.add(getTitle(AON.MSG.identification()));
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);
		
		nif.setVisibleLength(9);
		nif.setMaxLength(9);
		nif.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setDocument(nif.getValue());
			callback.markAsDirty();
		});		
		
		companyName.setVisibleLength(45);
		companyName.setMaxLength(45);
		companyName.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setName(companyName.getValue());
			callback.markAsDirty();
		});
		
		phone1.setVisibleLength(9);
		phone1.setMaxLength(9);
		phone2.setVisibleLength(9);
		phone2.setMaxLength(9);
		
		FlowPanel phones = new FlowPanel(); 
		phones.add(phone1);
		phones.add(phone2);
		
		phone1.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setEnterprisePhone1(phone1.getValue());
			callback.markAsDirty();
		});
		phone2.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setEnterprisePhone2(phone2.getValue());
			callback.markAsDirty();
		});
		
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
		
		cnaeLabel.setStyleName(AON.CSS.aonMarginLeft());

		FlowPanel cnaePanel = new FlowPanel();
		cnaePanel.add(cnae);
		cnaePanel.add(cnaeButton);
		cnaePanel.add(cnaeLabel);

		periodType.addItem(AON.MSG.periodType1());
		periodType.addItem(AON.MSG.periodType2());
		periodType.addItem(AON.MSG.periodType3());
		periodType.addChangeHandler( event -> {
			periodPanel.setVisible(periodType.getSelectedIndex() != 0);
			callback.markAsDirty();
		});
		
		periodStart.addStyleName(AON.CSS.aonMarginLeft());
		periodStart.addValueChangeHandler( event -> {
			callback.getMod200Object().getMod200().setPeriodStart(periodStart.getValue());			
			callback.markAsDirty();
		});
		
		periodEnd.addStyleName(AON.CSS.aonMarginLeft());
		periodEnd.addValueChangeHandler( event -> {
			callback.getMod200Object().getMod200().setPeriodEnd(periodEnd.getValue());
			callback.markAsDirty();
		});
		
		InlineLabel fromLabel = new InlineLabel(AON.MSG.periodLabel());
		fromLabel.setStyleName(AON.CSS.aonMarginLeft());
		
		InlineLabel toLabel = new InlineLabel(AON.MSG.to());
		toLabel.setStyleName(AON.CSS.aonMarginLeft());
		
		periodPanel.add(fromLabel);
		periodPanel.add(periodStart);
		periodPanel.add(toLabel);
		periodPanel.add(periodEnd);
	
		FlowPanel complementaryPanel = new FlowPanel();
		complementaryReceipt.addStyleName(AON.CSS.aonMarginLeft());
		complementaryReceipt.setVisibleLength(13);
		complementaryReceipt.setMaxLength(13);
		
		complementary.addClickHandler(event -> {
			complementaryReceipt.setEnabled(complementary.getValue());
			if (!complementary.getValue()) {
				complementaryReceipt.setValue("", true);
			}
			callback.getMod200Object().getMod200().setComplementary(complementary.getValue());
			callback.markAsDirty();
		});
		complementaryReceipt.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setReplacedNumber(complementaryReceipt.getValue());			
			callback.markAsDirty();
		});

		InlineLabel complementaryLabel = new InlineLabel(AON.MSG.complementaryReceipt());
		complementaryLabel.setStyleName(AON.CSS.aonMarginLeft());
		
		complementaryPanel.add(complementary);
		complementaryPanel.add(complementaryLabel);
		complementaryPanel.add(complementaryReceipt);
		
		tab.addLabelWidgetRow(AON.MSG.document(), nif)
		   .addLabelWidgetRow("Apellidos y nombre o raz\u00F3n social", companyName)
		   .addLabelWidgetRow(AON.MSG.phone(), phones)
		   .addLabelWidgetRow(AON.MSG.mainActivityCNAE(), cnaePanel)
		   .addLabelWidgetRow(AON.MSG.periodType(), periodType)
		   .addLabelWidgetRow("", periodPanel)
		   .addLabelWidgetRow(AON.MSG.complementary(), complementaryPanel);
		
		// ESTADOS DE CUENTAS
		
		basePanel.add(getSubtitle("Estados de Cuentas"));
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab2);
		
		balanceSheetType.addItem("Modalidad Normal");
		balanceSheetType.addItem("Modalidad Abreviada");
		balanceSheetType.addItem("Modalidad PYMES");		
		balanceSheetType.addChangeHandler( event -> {
			callback.markAsDirty();
		});		
		
		ecpnType.addItem("Modalidad Normal");
		ecpnType.addItem("Modalidad Abreviado (voluntario)");
		ecpnType.addItem("Modalidad PYMES (voluntario)");
		ecpnType.addItem("No consta");
		ecpnType.addChangeHandler( event -> {
			callback.markAsDirty();
		});
		
		profitAndLossType.addItem("Modalidad Normal");
		profitAndLossType.addItem("Modalidad Abreviada");
		profitAndLossType.addItem("Modalidad PYMES");
		profitAndLossType.addChangeHandler( event -> {
			callback.markAsDirty();
		});		
				
		tab2.addLabelWidgetRow(AON.MSG.balanceSheet(), balanceSheetType)
		    .addLabelWidgetRow(AON.MSG.ecpn(), ecpnType)
		    .addLabelWidgetRow(AON.MSG.profitAndLoss(), profitAndLossType);
		
		// PERSONAL ASALARIADO
		
		basePanel.add(getSubtitle("Personal asalariado"));
		
		AonDisplayTable tab3 = new AonDisplayTable();
		tab3.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab3.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab3);
		
		c041.setMaxLength(8);
		c041.setVisibleLength(8);		
		c041.addValueChangeHandler(event -> {			
			DoubleVariableEx dv = new DoubleVariableEx(Mod2002021Key.C0041);
			dv.setValue(c041.getValue());
			callback.getMod200Object().getMod200().addVariable(dv);
			callback.markAsDirty();
		});
		
		c042.setMaxLength(8);
		c042.setVisibleLength(8);		
		c042.addValueChangeHandler(event -> {
			DoubleVariableEx dv = new DoubleVariableEx(Mod2002021Key.C0042);
			dv.setValue(c042.getValue());
			callback.getMod200Object().getMod200().addVariable(dv);
			callback.markAsDirty();
		});
		
		tab3.addLabelWidgetRow(AON.MSG.fixedPersonal(), c041)
	    	.addLabelWidgetRow(AON.MSG.nonFixedPersonal(), c042);
		
		// CARACTERES DE LA DECLARACION
		
		FlexTable charactersTable1 = new FlexTable();
		FlexTable charactersTable2 = new FlexTable();
		FlexTable charactersTable3 = new FlexTable();
		
		initializeTable(charactersTable1, DECLARATION_CHARACTERS_BLOCK1);
		initializeTable(charactersTable2, DECLARATION_CHARACTERS_BLOCK2);
		initializeTable(charactersTable3, DECLARATION_CHARACTERS_BLOCK3);
		
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
	
	private void initializeTable(FlexTable table, Mod2002021Key[] declarationCharatersBlock) {
		table.setWidth("100%");
		table.setCellSpacing(0);
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setStyleName(0, AON.AON_CSS.aonWidth40());
		cf.setStyleName(1, AON.AON_CSS.aonWidthAuto());
		
		int row = 0;
		
		for (final Mod2002021Key key : declarationCharatersBlock ) {
			if (key != Mod2002021Key.C0012R) {
			   AonBoxLabel l = new AonBoxLabel( key.getCode() , BOX_LENGTH );
			   table.setWidget(row, 0, l);
			}
			
			final CheckBox check = new CheckBox(key.getDescription() + (NOT_SUPPORTED_CHARACTERS.contains(key)?" (NO)":""));
			check.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (NOT_SUPPORTED_CHARACTERS.contains(key)) {
						AonMessageDialog.error(AON.MSG.unsupportedCharacter(key.getDescription()));
						check.setValue(false);
					} else {
						changeAvailability(key);
						callback.markAsDirty();
					}
				}				
			});
			inputs.put(key, check);
			check.setStyleName(AON.AON_CSS.aonFiscalCheckbox());
			table.setWidget(row, 1, check);
			row++;
		}
	}
	
}
