package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Character.CHARACTERS_KEYS;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Character.CHARACTER_ALSO_CHECK_MAP;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Character.CHARACTER_INCOMPATIBILITY_MAP;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.Cnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Mod2002016Object.IMod200ChangeListener;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.DoubleVariable2016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Page00 extends PageAbs {
	public static final HashSet<Mod2002016Key> NOT_SUPPORTED_CHARACTERS = new HashSet<Mod2002016Key>();
	static {
		NOT_SUPPORTED_CHARACTERS.add(Mod2002016Key.C0003);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002016Key.C0004);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002016Key.C0024);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002016Key.C0025);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002016Key.C0036);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002016Key.C0058);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002016Key.C0061);
	};

	public static final Mod2002016Key[] DECLARATION_CHARACTERS_BLOCK1 = new Mod2002016Key[] {
		 Mod2002016Key.C0001,Mod2002016Key.C0002,Mod2002016Key.C0003
		,Mod2002016Key.C0004,Mod2002016Key.C0005,Mod2002016Key.C0011
		,Mod2002016Key.C0013,Mod2002016Key.C0014,Mod2002016Key.C0017
		,Mod2002016Key.C0018,Mod2002016Key.C0019,Mod2002016Key.C0021
		,Mod2002016Key.C0023,Mod2002016Key.C0024,Mod2002016Key.C0025
		,Mod2002016Key.C0031,Mod2002016Key.C0032,Mod2002016Key.C0036
		,Mod2002016Key.C0048,Mod2002016Key.C0058,Mod2002016Key.C0060
		,Mod2002016Key.C0066
	};

	public static final Mod2002016Key[] DECLARATION_CHARACTERS_BLOCK2 = new Mod2002016Key[] {
	 	 Mod2002016Key.C0006,Mod2002016Key.C0015,Mod2002016Key.C0022
		,Mod2002016Key.C0028,Mod2002016Key.C0047,Mod2002016Key.C0035
		,Mod2002016Key.C0049,Mod2002016Key.C0029,Mod2002016Key.C0033
		,Mod2002016Key.C0034,Mod2002016Key.C0038,Mod2002016Key.C0046
		,Mod2002016Key.C0012,Mod2002016Key.C0064,Mod2002016Key.C0057
		,Mod2002016Key.C0020,Mod2002016Key.C0062
	};
	
	public static final Mod2002016Key[] DECLARATION_CHARACTERS_BLOCK3 = new Mod2002016Key[] {
		 Mod2002016Key.C0007,Mod2002016Key.C0009,Mod2002016Key.C0010
		,Mod2002016Key.C0016,Mod2002016Key.C0026,Mod2002016Key.C0027
		,Mod2002016Key.C0030,Mod2002016Key.C0039,Mod2002016Key.C0043
		,Mod2002016Key.C0045,Mod2002016Key.C0063,Mod2002016Key.C0071
		,Mod2002016Key.C0059,Mod2002016Key.C0065,Mod2002016Key.C0067
	};
	
	private Map<Mod2002016Key, CheckBox> inputs = new HashMap<Mod2002016Key, CheckBox>();

	
	interface Page1Binder extends
			UiBinder<Widget, Page00> {
	}

	private static final Page1Binder page1Binder = GWT
			.create(Page1Binder.class);

	@UiField
	Panel basePanel;
	
	@UiField
	FlowPanel periodPanel;
	@UiField
	DateBoxEx periodStart;
	@UiField
	DateBoxEx periodEnd;
	@UiField(provided=true)
	ListBox periodType;
	@UiField
	TextBox  cnae;
	@UiField
	InlineLabel cnaeLabel;	
	@UiField
	Button showCnae;

	Cnae2009Panel cnaePanel;
	
	@UiField
	DocumentTextBox nif;
	@UiField
	TextBox  companyName;
	@UiField
	TextBox  phone1;
	@UiField
	TextBox  phone2;
	@UiField
	CheckBox complementary;
	@UiField
	TextBox  complementaryReceipt;
	
	@UiField(provided = true)
	FlexTable charactersTable1;
	@UiField(provided = true)
	FlexTable charactersTable2;
	@UiField(provided = true)
	FlexTable charactersTable3;
	
	@UiField
	ListBox balanceSheetType;
	@UiField
	ListBox profitAndLossType;
	
	CheckBox c061;
	
	@UiField
	DoubleBox c041;
	@UiField
	DoubleBox c042;
	
	public Page00( Model200PageCallback callback ) {
		super(callback);
		cnaePanel = new Cnae2009Panel( new Cnae2009Panel.SelectionCallBack() {
			@Override
			public void onSelect(CNAE2009 selected) {
				cnae.setText(selected.getCode());
				cnaeLabel.setText(selected.getDescription());
			}
			@Override
			public void onClose() {
				// Nothing
			}
		});
		periodType = new ListBox();
		periodType.addItem(AON.MSG.periodType1());
		periodType.addItem(AON.MSG.periodType2());
		periodType.addItem(AON.MSG.periodType3());
		
		charactersTable1 = new FlexTable();
		charactersTable2 = new FlexTable();
		charactersTable3 = new FlexTable();
		
		c061 = new CheckBox();
		Widget ui = page1Binder.createAndBindUi(this);
		initWidget(ui);
		
		initializeTable();
		
		callback.getMod200Object().register( new IMod200ChangeListener() {
			
			@Override
			public void mod200Changed(Mod2002016 mod200) {
				DoubleVariable2016 sv = mod200.getVariable(Mod2002016Key.C0027);
				if (sv != null && inputs.containsKey( Mod2002016Key.C0027 )) {
					inputs.get( Mod2002016Key.C0027 ).setValue( AonMathUtils.equals(sv.getValue() , 1.0) );
				}
			}
		});
	}
	
	
	@Override
	public void dump() {
		super.dump();
		nif.setValue(callback.getMod200Object().getMod200().getEnterpriseDocument());
		companyName.setValue(callback.getMod200Object().getMod200().getEnterpriseName());
		phone1.setValue(callback.getMod200Object().getMod200().getEnterprisePhone1());
		phone2.setValue(callback.getMod200Object().getMod200().getEnterprisePhone2());
		complementary.setValue(callback.getMod200Object().getMod200().isComplementary());
		complementaryReceipt.setValue(callback.getMod200Object().getMod200().getComplementaryReceipt());
		complementaryReceipt.setEnabled(complementary.getValue());
		periodType.setSelectedIndex(callback.getMod200Object().getMod200().getPeriodType() - 1 );
		periodPanel.setVisible((periodType.getSelectedIndex() != 0));
		periodStart.setValue(callback.getMod200Object().getMod200().getPeriodStart() );
		periodEnd.setValue(callback.getMod200Object().getMod200().getPeriodEnd() );
		int index = callback.getMod200Object().getMod200().getBalanceType().ordinal();
		balanceSheetType.setSelectedIndex(index);
		index = callback.getMod200Object().getMod200().getPygType().ordinal();
		profitAndLossType.setSelectedIndex(index);
		DoubleVariable2016 dv = callback.getMod200Object().getMod200().getKeysMap().get(Mod2002016Key.C0041);
		Double value = 0.0;
		if (dv != null) {
			value = dv.getValue();
		}
		c041.setValue(value);
		
		dv = callback.getMod200Object().getMod200().getKeysMap().get(Mod2002016Key.C0042);
		value = 0.0;
		if (dv != null) {
			value = dv.getValue();
		}
		c042.setValue(value);
		
		for (CheckBox check : inputs.values()) {
			check.setValue(false);
		}
		for (Mod2002016Key key : CHARACTERS_KEYS) {
			DoubleVariable2016 sv = callback.getMod200Object().getMod200().getKeysMap().get(key);
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
		callback.getMod200Object().getMod200().setEnterpriseDocument(nif.getValue());
		callback.getMod200Object().getMod200().setEnterpriseName(companyName.getValue());
		callback.getMod200Object().getMod200().setEnterprisePhone1(phone1.getValue());
		callback.getMod200Object().getMod200().setEnterprisePhone2(phone2.getValue());
		callback.getMod200Object().getMod200().setComplementary(complementary.getValue());
		callback.getMod200Object().getMod200().setComplementaryReceipt(complementaryReceipt.getValue());
		callback.getMod200Object().getMod200().setPeriodType(periodType.getSelectedIndex() + 1 );
		callback.getMod200Object().getMod200().setPeriodStart( periodStart.getValue() );
		callback.getMod200Object().getMod200().setPeriodEnd( periodEnd.getValue() );
		callback.getMod200Object().getMod200().setBalanceType( balanceSheetType.getSelectedIndex() );
		callback.getMod200Object().getMod200().setPygType( profitAndLossType.getSelectedIndex() );
		callback.getMod200Object().getMod200().setCnae(cnae.getValue());
		DoubleVariable2016 bv = null;
		for (Mod2002016Key key : inputs.keySet()) {
			bv = new DoubleVariable2016( key );
			bv.setValue(inputs.get(key).getValue());
			callback.getMod200Object().getMod200().addVariable(bv);
		}
		
		bv = new DoubleVariable2016( Mod2002016Key.C0061 );
		bv.setValue( c061.getValue() );
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariable2016( Mod2002016Key.C0050 );
		bv.setValue((callback.getMod200Object().getMod200().getBalanceType() == BalanceType.NORMAL));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariable2016( Mod2002016Key.C0051 );
		bv.setValue((callback.getMod200Object().getMod200().getBalanceType() == BalanceType.ABREVIADO));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariable2016( Mod2002016Key.C0052 );
		bv.setValue((callback.getMod200Object().getMod200().getBalanceType() == BalanceType.PYMES));
		callback.getMod200Object().getMod200().addVariable(bv);
			
		bv = new DoubleVariable2016( Mod2002016Key.C0053 );
		bv.setValue((callback.getMod200Object().getMod200().getPygType() == BalanceType.NORMAL));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariable2016( Mod2002016Key.C0054 );
		bv.setValue((callback.getMod200Object().getMod200().getPygType() == BalanceType.ABREVIADO));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariable2016( Mod2002016Key.C0055 );
		bv.setValue((callback.getMod200Object().getMod200().getPygType() == BalanceType.PYMES));
		callback.getMod200Object().getMod200().addVariable(bv);
		
		bv = new DoubleVariable2016( Mod2002016Key.C0041 );
		bv.setValue( c041.getValue() );
		callback.getMod200Object().getMod200().addVariable(bv);

		bv = new DoubleVariable2016( Mod2002016Key.C0042 );
		bv.setValue( c042.getValue() );
		callback.getMod200Object().getMod200().addVariable(bv);
		
	}

	@Override
	protected void initializeTable() {
		c061.setText(Mod2002016Key.C0061.getDescription());
		inputs.put(Mod2002016Key.C0061, c061);
		
		int row = 0;
		row = initializeBlock(charactersTable1,row, DECLARATION_CHARACTERS_BLOCK1);
		row = initializeBlock(charactersTable2,row, DECLARATION_CHARACTERS_BLOCK2);
		row = initializeBlock(charactersTable3,row, DECLARATION_CHARACTERS_BLOCK3);
	}

	private int initializeBlock(FlexTable table,int row, Mod2002016Key[] declarationCharatersBlock) {
		initializeTable(table);
		
		Administration adm = (callback.getMod200Object()==null)
				?Administration.COMMON_TERRITORY
				:callback.getMod200Object().getAdministration();
		for (final Mod2002016Key key : declarationCharatersBlock ) {
			BoxLabel l = new BoxLabel( key.getCode( adm ) , Model2002016.BOX_LENGTH );
			table.setWidget(row, 0, l);
			
			final CheckBox check = new CheckBox(key.getDescription() 
					+ (NOT_SUPPORTED_CHARACTERS.contains(key)?" (NO)":""));
			check.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (NOT_SUPPORTED_CHARACTERS.contains(key)) {
						MessageDialog.show("ERROR",AON.MSG.unsupportedCharacter(key.getDescription()));
						check.setValue(false);
					} else {
						changeAvailability(key);
						if (key == Mod2002016Key.C0067 && check.getValue()) {
							if (inputs.get(Mod2002016Key.C0009).getValue()
							 || inputs.get(Mod2002016Key.C0010).getValue()
							 || inputs.get(Mod2002016Key.C0021).getValue()
							 || inputs.get(Mod2002016Key.C0039).getValue()) {
								// OK
							} else {
								MessageDialog.show("El caracter [00067] no se puede marcar si no se "
									+ "marca algunos de los siguientes: [00009], [00010], [00021] \u00F3 [00039]");
							}
						}
					}
				}
				
			});
			inputs.put(key, check);
			check.setStyleName(AON.AON_CSS.aonFiscalCheckbox());
			table.setWidget(row, 1, check);
			row++;
		}
		return row++;
	}
	
	private void changeAvailability(Mod2002016Key key) {
		boolean enabled = inputs.get(key).getValue();
		if (CHARACTER_INCOMPATIBILITY_MAP.get(key) != null) {
			for (Mod2002016Key incompatible : CHARACTER_INCOMPATIBILITY_MAP.get(key)) {
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
			for (Mod2002016Key alsoCheck : CHARACTER_ALSO_CHECK_MAP.get(key)) {
				CheckBox check = inputs.get(alsoCheck);
				if (check != null) {
					check.setValue(true);	
				}
			}
		}
	}

	private void initializeTable(FlexTable table) {
		table.setWidth("100%");
		table.setCellSpacing(0);
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setStyleName(0, AON.AON_CSS.aonWidth40());
		cf.setStyleName(1, AON.AON_CSS.aonWidthAuto());
	}
	
	@UiHandler("complementary")
	void onChangeComplementary(ClickEvent event) {
		callback.getMod200Object().getMod200().setComplementary(complementary.getValue());
		complementaryReceipt.setEnabled(complementary.getValue());
	}

	@UiHandler("showCnae")
	void onSelectCnae(ClickEvent event) {
		cnaePanel.onShow();
	}
	
	@UiHandler("periodType")
	void onChangePeriodType( ChangeEvent event) {
		periodPanel.setVisible((periodType.getSelectedIndex() != 0));
	}
	
	@UiHandler("cnae")
	void onChangeCNAE( ChangeEvent event) {
		if (AonStringUtils.isEmpty(cnae.getText())) {
			cnaeLabel.setText(AonStringUtils.EMPTY);
		} else {
			CNAE2009 c = CNAE2009.valueOfCode(cnae.getText()); 
			if (c == null) {
				MessageDialog.show("CNAE no encontrado");
				cnaeLabel.setText(AonStringUtils.EMPTY);
			} else {
				cnaeLabel.setText(c.getDescription());
			}
		}
		
	}
	
	protected void enableCharacters( boolean enabled) {
		Window.alert("enableCharacters 1");
		periodType.setEnabled(enabled);
		balanceSheetType.setEnabled(enabled);
		profitAndLossType.setEnabled(enabled);
		for (CheckBox check : inputs.values()) {
			check.setEnabled(enabled);
		}
		c061.setEnabled(enabled);
		Window.alert("enableCharacters 2");
		
		for (Mod2002016Key key : CHARACTERS_KEYS) {
			if (inputs.containsKey( key ) && inputs.get( key ).getValue()) {
				changeAvailability(key);
			}
		}
		Window.alert("enableCharacters 3");

	}
	
}
