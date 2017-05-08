package com.esferalia.aon.gwt.fiscal.client.mod200.e2014;

import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Character.CHARACTERS_KEYS;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Character.CHARACTER_ALSO_CHECK_MAP;
import static com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Character.CHARACTER_INCOMPATIBILITY_MAP;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.Cnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2014.Mod2002014Object.IMod200ChangeListener;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.DoubleVariable2014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key;
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
	public static final HashSet<Mod2002014Key> NOT_SUPPORTED_CHARACTERS = new HashSet<Mod2002014Key>();
	static {
		NOT_SUPPORTED_CHARACTERS.add(Mod2002014Key.C0003);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002014Key.C0004);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002014Key.C0024);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002014Key.C0025);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002014Key.C0036);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002014Key.C0058);
		NOT_SUPPORTED_CHARACTERS.add(Mod2002014Key.C0061);
	};

	public static final Mod2002014Key[] DECLARATION_CHARACTERS_BLOCK1 = new Mod2002014Key[] {
		 Mod2002014Key.C0001,Mod2002014Key.C0021
		,Mod2002014Key.C0002,Mod2002014Key.C0023
		,Mod2002014Key.C0003,Mod2002014Key.C0024
		,Mod2002014Key.C0004,Mod2002014Key.C0025
		,Mod2002014Key.C0005,Mod2002014Key.C0031
		,Mod2002014Key.C0011,Mod2002014Key.C0032
		,Mod2002014Key.C0013,Mod2002014Key.C0036
		,Mod2002014Key.C0014,Mod2002014Key.C0048
		,Mod2002014Key.C0017,Mod2002014Key.C0058
		,Mod2002014Key.C0018,Mod2002014Key.C0060
		,Mod2002014Key.C0019
	};

	public static final Mod2002014Key[] DECLARATION_CHARACTERS_BLOCK2 = new Mod2002014Key[] {
	 	 Mod2002014Key.C0006,Mod2002014Key.C0034
		,Mod2002014Key.C0015,Mod2002014Key.C0038
		,Mod2002014Key.C0022,Mod2002014Key.C0046
		,Mod2002014Key.C0028,Mod2002014Key.C0012
		,Mod2002014Key.C0047,Mod2002014Key.C0064
		,Mod2002014Key.C0049,Mod2002014Key.C0057
		,Mod2002014Key.C0029,Mod2002014Key.C0020
		,Mod2002014Key.C0033
		
	};
	
	public static final Mod2002014Key[] DECLARATION_CHARACTERS_BLOCK3 = new Mod2002014Key[] {
		 Mod2002014Key.C0056,Mod2002014Key.C0037
		,Mod2002014Key.C0007,Mod2002014Key.C0039 
		,Mod2002014Key.C0008,Mod2002014Key.C0043
		,Mod2002014Key.C0009,Mod2002014Key.C0044
		,Mod2002014Key.C0010,Mod2002014Key.C0045
		,Mod2002014Key.C0016,Mod2002014Key.C0062
		,Mod2002014Key.C0026,Mod2002014Key.C0063
		,Mod2002014Key.C0027,Mod2002014Key.C0059
		,Mod2002014Key.C0030,Mod2002014Key.C0065
		,Mod2002014Key.C0035
	};
	
	private Map<Mod2002014Key, CheckBox> inputs = new HashMap<Mod2002014Key, CheckBox>();

	
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
	
	public Page00() {
		
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
	}

	public void dump(Mod2002014Object mod200Object) {
		this.mod200Object = mod200Object;
		nif.setValue(this.mod200Object.getMod200().getEnterpriseDocument());
		companyName.setValue(this.mod200Object.getMod200().getEnterpriseName());
		phone1.setValue(this.mod200Object.getMod200().getEnterprisePhone1());
		phone2.setValue(this.mod200Object.getMod200().getEnterprisePhone2());
		complementary.setValue(this.mod200Object.getMod200().isComplementary());
		complementaryReceipt.setValue(this.mod200Object.getMod200().getComplementaryReceipt());
		periodType.setSelectedIndex(mod200Object.getMod200().getPeriodType() - 1 );
		periodPanel.setVisible((periodType.getSelectedIndex() != 0));
		periodStart.setValue(mod200Object.getMod200().getPeriodStart() );
		periodEnd.setValue(mod200Object.getMod200().getPeriodEnd() );
		int index = this.mod200Object.getMod200().getBalanceType().ordinal();
		balanceSheetType.setSelectedIndex(index);
		index = this.mod200Object.getMod200().getPygType().ordinal();
		profitAndLossType.setSelectedIndex(index);
		DoubleVariable2014 dv = this.mod200Object.getMod200().getKeysMap().get(Mod2002014Key.C0041);
		Double value = 0.0;
		if (dv != null) {
			value = dv.getValue();
		}
		c041.setValue(value);
		
		dv = this.mod200Object.getMod200().getKeysMap().get(Mod2002014Key.C0042);
		value = 0.0;
		if (dv != null) {
			value = dv.getValue();
		}
		c042.setValue(value);
		
		for (CheckBox check : inputs.values()) {
			check.setValue(false);
		}
		for (Mod2002014Key key : CHARACTERS_KEYS) {
			DoubleVariable2014 sv = this.mod200Object.getMod200().getKeysMap().get(key);
			if (sv != null && inputs.containsKey( key )) {
				inputs.get( key ).setValue( AonMathUtils.equals(sv.getValue() , 1.0) );
			}
		}
		cnaeLabel.setText(null);
		cnae.setValue(this.mod200Object.getMod200().getCnae());
		if (!AonStringUtils.isEmpty(this.mod200Object.getMod200().getCnae())) {
			CNAE2009 cnae = CNAE2009.valueOfCode(this.mod200Object.getMod200().getCnae());
			cnaeLabel.setText(cnae==null?null:cnae.getDescription());	
		}
		this.mod200Object.register( new IMod200ChangeListener() {
			
			@Override
			public void mod200Changed(Mod2002014 mod200) {
				DoubleVariable2014 sv = mod200.getVariable(Mod2002014Key.C0027);
				if (sv != null && inputs.containsKey( Mod2002014Key.C0027 )) {
					inputs.get( Mod2002014Key.C0027 ).setValue( AonMathUtils.equals(sv.getValue() , 1.0) );
				}
			}
		});
	}
	
	protected void initializeTable() {
		c061.setText(Mod2002014Key.C0061.getDescription());
		inputs.put(Mod2002014Key.C0061, c061);
		
		int row = 0;
		row = initializeBlock(charactersTable1,row, DECLARATION_CHARACTERS_BLOCK1);
		row = initializeBlock(charactersTable2,row, DECLARATION_CHARACTERS_BLOCK2);
		row = initializeBlock(charactersTable3,row, DECLARATION_CHARACTERS_BLOCK3);
	}

	private int initializeBlock(FlexTable table,int row, Mod2002014Key[] declarationCharatersBlock) {
		initializeTable(table);
		int col = 0;
		Administration adm = mod200Object==null?Administration.COMMON_TERRITORY:mod200Object.getAdministration();
		for (final Mod2002014Key key : declarationCharatersBlock ) {
			if (col == 4) {
				++row;
				col = 0;
			}
			
			BoxLabel l = new BoxLabel( key.getCode( adm ) );
			table.setWidget(row, col++, l);
			
			final CheckBox check = new CheckBox(key.getDescription() 
				+ (NOT_SUPPORTED_CHARACTERS.contains(key)?" (NO)":""));
			check.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (NOT_SUPPORTED_CHARACTERS.contains(key)) {
						Window.alert(AON.MSG.unsupportedCharacter(key.getDescription()));
						check.setValue(false);
					} else {
						changeAvailability(key);
					}
				}

			});
			inputs.put(key, check);
			check.setStyleName(AON.AON_CSS.aonFiscalCheckbox());
			table.setWidget(row, col++, check);
		}
		return row++;
	}
	
	private void changeAvailability(Mod2002014Key key) {
		boolean enabled = inputs.get(key).getValue();
		if (CHARACTER_INCOMPATIBILITY_MAP.get(key) != null) {
			for (Mod2002014Key incompatible : CHARACTER_INCOMPATIBILITY_MAP.get(key)) {
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
			for (Mod2002014Key alsoCheck : CHARACTER_ALSO_CHECK_MAP.get(key)) {
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
		cf.setStyleName(0, AON.AON_CSS.aonWidth30());
		cf.setStyleName(1, AON.AON_CSS.aonWidthHalf());
		cf.setStyleName(2, AON.AON_CSS.aonWidth30());
		cf.setStyleName(3, AON.AON_CSS.aonWidthHalf());
	}
	
	@UiHandler("complementary")
	void onChangeComplementary(ClickEvent event) {
		mod200Object.getMod200().setComplementary(complementary.getValue());
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
				Window.alert("CNAE no encontrado");
				cnaeLabel.setText(AonStringUtils.EMPTY);
			} else {
				cnaeLabel.setText(c.getDescription());
			}
		}
		
	}

	public void populate(Mod2002014Object obj) {
		obj.getMod200().setEnterpriseDocument(nif.getValue());
		obj.getMod200().setEnterpriseName(companyName.getValue());
		obj.getMod200().setEnterprisePhone1(phone1.getValue());
		obj.getMod200().setEnterprisePhone2(phone2.getValue());
		obj.getMod200().setComplementary(complementary.getValue());
		obj.getMod200().setComplementaryReceipt(complementaryReceipt.getValue());
		obj.getMod200().setPeriodType(periodType.getSelectedIndex() + 1 );
		obj.getMod200().setPeriodStart( periodStart.getValue() );
		obj.getMod200().setPeriodEnd( periodEnd.getValue() );
		obj.getMod200().setBalanceType( balanceSheetType.getSelectedIndex() );
		obj.getMod200().setPygType( profitAndLossType.getSelectedIndex() );
		obj.getMod200().setCnae(cnae.getValue());
		DoubleVariable2014 bv = null;
		for (Mod2002014Key key : inputs.keySet()) {
			bv = new DoubleVariable2014( key );
			bv.setValue(inputs.get(key).getValue());
			obj.getMod200().addVariable(bv);
		}
		
		bv = new DoubleVariable2014( Mod2002014Key.C0061 );
		bv.setValue( c061.getValue() );
		obj.getMod200().addVariable(bv);
		
		bv = new DoubleVariable2014( Mod2002014Key.C0050 );
		bv.setValue((obj.getMod200().getBalanceType() == BalanceType.NORMAL));
		obj.getMod200().addVariable(bv);
		
		bv = new DoubleVariable2014( Mod2002014Key.C0051 );
		bv.setValue((obj.getMod200().getBalanceType() == BalanceType.ABREVIADO));
		obj.getMod200().addVariable(bv);
		
		bv = new DoubleVariable2014( Mod2002014Key.C0052 );
		bv.setValue((obj.getMod200().getBalanceType() == BalanceType.PYMES));
		obj.getMod200().addVariable(bv);
			
		bv = new DoubleVariable2014( Mod2002014Key.C0053 );
		bv.setValue((obj.getMod200().getBalanceType() == BalanceType.NORMAL));
		obj.getMod200().addVariable(bv);
		
		bv = new DoubleVariable2014( Mod2002014Key.C0054 );
		bv.setValue((obj.getMod200().getBalanceType() == BalanceType.ABREVIADO));
		obj.getMod200().addVariable(bv);
		
		bv = new DoubleVariable2014( Mod2002014Key.C0055 );
		bv.setValue((obj.getMod200().getBalanceType() == BalanceType.PYMES));
		obj.getMod200().addVariable(bv);
		
		bv = new DoubleVariable2014( Mod2002014Key.C0041 );
		bv.setValue( c041.getValue() );
		obj.getMod200().addVariable(bv);

		bv = new DoubleVariable2014( Mod2002014Key.C0042 );
		bv.setValue( c042.getValue() );
		obj.getMod200().addVariable(bv);
		
	}

	protected void enableCharacters( boolean enabled) {
		periodType.setEnabled(enabled);
		balanceSheetType.setEnabled(enabled);
		profitAndLossType.setEnabled(enabled);
		for (CheckBox check : inputs.values()) {
			check.setEnabled(enabled);
		}
		c061.setEnabled(enabled);
	}

//	private changeCharacters() {
//		C0050
//		C0051
//		C0052
//		C0055
//		C0053
//		C0053
//		C0054
//		C0022
//		C0017
//		C0018
//		C0019
//		C0013
//		C0015
//		C0012
//		X0000
//		C0047
//		C0028
//		C0024
//		C0003
//		C0004
//		C0009 
//		C0010 
//		C0024 
//		C0025
//	}

	
}
