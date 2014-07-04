package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.MSG;
import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.RESOURCES;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200ConstantsCharacter.CHARACTERS_KEYS;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200ConstantsCharacter.CHARACTER_ALSO_CHECK_MAP;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200ConstantsCharacter.CHARACTER_INCOMPATIBILITY_MAP;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.widget.CnaePanel;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.common.shared.CommonEnum.Administration;
import com.esferalia.aon.gwt.common.shared.CommonEnum.CNAE;
import com.esferalia.aon.gwt.fiscal.shared.mod200.DoubleVariable;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200.BalanceType;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
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
	public static final Mod200Key[] DECLARATION_CHARATERS_BLOCK1 = new Mod200Key[] {
		 Mod200Key.C0001,Mod200Key.C0014,Mod200Key.C0025
		,Mod200Key.C0002,Mod200Key.C0017,Mod200Key.C0031
		,Mod200Key.C0003,Mod200Key.C0018,Mod200Key.C0032
		,Mod200Key.C0004,Mod200Key.C0019,Mod200Key.C0036
		,Mod200Key.C0005,Mod200Key.C0021,Mod200Key.C0048
		,Mod200Key.C0011,Mod200Key.C0023,Mod200Key.C0058
		,Mod200Key.C0013,Mod200Key.C0024,Mod200Key.C0060
	};

	public static final Mod200Key[] DECLARATION_CHARATERS_BLOCK2 = new Mod200Key[] {
	 	 Mod200Key.C0006,Mod200Key.C0047,Mod200Key.C0038
		,Mod200Key.C0015,Mod200Key.C0049,Mod200Key.C0046
		,Mod200Key.C0022,Mod200Key.C0029,Mod200Key.C0012
		,Mod200Key.C0028,Mod200Key.C0033,Mod200Key.C0057
		,Mod200Key.C0034,Mod200Key.C0020
	};
	
	public static final Mod200Key[] DECLARATION_CHARATERS_BLOCK3 = new Mod200Key[] {
		 Mod200Key.C0056,Mod200Key.C0026,Mod200Key.C0043
		,Mod200Key.C0007,Mod200Key.C0027,Mod200Key.C0044 
		,Mod200Key.C0008,Mod200Key.C0030,Mod200Key.C0045
		,Mod200Key.C0009,Mod200Key.C0035,Mod200Key.C0062 
		,Mod200Key.C0010,Mod200Key.C0037,Mod200Key.C0063
		,Mod200Key.C0016,Mod200Key.C0039,Mod200Key.C0059		
	};
	
	private Map<Mod200Key, CheckBox> inputs = new HashMap<Mod200Key, CheckBox>();

	
	interface Page1Binder extends
			UiBinder<Widget, Page00> {
	}

	private static final Page1Binder page1Binder = GWT
			.create(Page1Binder.class);

	private Mod200Object mod200Object;
	
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

	@UiField(provided=true)
	CnaePanel cnaePanel;
	
	@UiField
	DocumentTextBox nif;
	@UiField
	TextBox  companyName;
	@UiField
	TextBox  phone1;
	@UiField
	TextBox  phone2;
	
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
	@UiField
	CheckBox c061;
	@UiField
	DoubleTextBox c041;
	@UiField
	DoubleTextBox c042;
	
	public Page00() {
		
		cnaePanel = new CnaePanel( new CnaePanel.SelectionCallBack() {
			@Override
			public void onSelect(CNAE selected) {
				cnae.setText(selected.getCode());
				cnaeLabel.setText(selected.getDescription());
			}
			@Override
			public void onClose() {
				// Nothing
			}
		});
		periodType = new ListBox();
		periodType.addItem(MSG.periodType1());
		periodType.addItem(MSG.periodType2());
		periodType.addItem(MSG.periodType3());
		
		charactersTable1 = new FlexTable();
		charactersTable2 = new FlexTable();
		charactersTable3 = new FlexTable();
		Widget ui = page1Binder.createAndBindUi(this);
		initWidget(ui);
		
		initializeTable();
	}

	public void dump(Mod200Object mod200Object) {
		this.mod200Object = mod200Object;
		nif.setValue(this.mod200Object.getMod200().getEnterpriseDocument());
		companyName.setValue(this.mod200Object.getMod200().getEnterpriseName());
		phone1.setValue(this.mod200Object.getMod200().getEnterprisePhone1());
		phone2.setValue(this.mod200Object.getMod200().getEnterprisePhone2());
		periodType.setSelectedIndex(mod200Object.getMod200().getPeriodType() - 1 );
		periodPanel.setVisible((periodType.getSelectedIndex() != 0));
		periodStart.setValue(mod200Object.getMod200().getPeriodStart() );
		periodEnd.setValue(mod200Object.getMod200().getPeriodEnd() );
		int index = this.mod200Object.getMod200().getBalanceType().ordinal();
		balanceSheetType.setSelectedIndex(index);
		index = this.mod200Object.getMod200().getPygType().ordinal();
		profitAndLossType.setSelectedIndex(index);
		DoubleVariable dv = this.mod200Object.getMod200().getKeysMap().get(Mod200Key.C0041);
		Double value = 0.0;
		if (dv != null) {
			value = dv.getValue();
		}
		c041.setValue(value);
		
		dv = this.mod200Object.getMod200().getKeysMap().get(Mod200Key.C0042);
		value = 0.0;
		if (dv != null) {
			value = dv.getValue();
		}
		c042.setValue(value);
		
		for (CheckBox check : inputs.values()) {
			check.setValue(false);
		}
		for (Mod200Key key : CHARACTERS_KEYS) {
			DoubleVariable sv = this.mod200Object.getMod200().getKeysMap().get(key);
			if (sv != null && inputs.containsKey( key )) {
				inputs.get( key ).setValue( AonUtil.equals(sv.getValue() , 1.0) );
			}
		}
		cnaeLabel.setText(null);
		cnae.setValue(this.mod200Object.getMod200().getCnae());
		if (!AonUtil.isEmpty(this.mod200Object.getMod200().getCnae())) {
			CNAE cnae = CNAE.valueOfCode(this.mod200Object.getMod200().getCnae());
			cnaeLabel.setText(cnae==null?null:cnae.getDescription());	
		}
	}
	
	protected void initializeTable() {
		c061.setText(Mod200Key.C0061.getDescription());
		inputs.put(Mod200Key.C0061, c061);
		
		int row = 0;
		row = initializeBlock(charactersTable1,row, DECLARATION_CHARATERS_BLOCK1);
		row = initializeBlock(charactersTable2,row, DECLARATION_CHARATERS_BLOCK2);
		row = initializeBlock(charactersTable3,row, DECLARATION_CHARATERS_BLOCK3);
	}

	private int initializeBlock(FlexTable table,int row, Mod200Key[] declarationCharatersBlock) {
		initializeTable(table);
		int mustAdd = 0;
		int colOffset = 0;
		Administration adm = mod200Object==null?Administration.COMMON_TERRITORY:mod200Object.getAdministration();
		for (final Mod200Key key : declarationCharatersBlock ) {
			if (mustAdd == 3) {
				++row;
				mustAdd = 0;
				colOffset = 0;
			}
			BoxLabel code = new BoxLabel( key.getCode( adm ) );
			table.setWidget(row, colOffset, code);
			final CheckBox check = new CheckBox(key.getDescription());
			check.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (key == Mod200Key.C0003
						|| key == Mod200Key.C0004
						|| key == Mod200Key.C0024
						|| key == Mod200Key.C0025
						|| key == Mod200Key.C0036
						|| key == Mod200Key.C0058
						|| key == Mod200Key.C0061) {
						Window.alert("La declaraci\u00F3n para el caracter '" +
								key.getDescription()+ "' no se encuentra disponible");
						check.setValue(false);						
					} else {
						changeAvailability(key); 
					}
						
				}

			});
			inputs.put(key, check);
			check.setStyleName(RESOURCES.css().aonMod200Checkbox());
			table.setWidget(row, colOffset + 1, check);
			colOffset = colOffset + 2;				
			++mustAdd;
		}
		return row++;
	}
	
	private void changeAvailability(Mod200Key key) {
		boolean enabled = inputs.get(key).getValue();
		if (CHARACTER_INCOMPATIBILITY_MAP.get(key) != null) {
			for (Mod200Key incompatible : CHARACTER_INCOMPATIBILITY_MAP.get(key)) {
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
			for (Mod200Key alsoCheck : CHARACTER_ALSO_CHECK_MAP.get(key)) {
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
		cf.setWidth(0, "35px");
		cf.setWidth(1, "300px");
		cf.setWidth(2, "35px");
		cf.setWidth(3, "300px");
		cf.setWidth(4, "35px");
		cf.setWidth(5, "auto");
	}
	
	@UiHandler("showCnae")
	void onSelectCnae(ClickEvent event) {
		cnaePanel.onShow();
	}
	
	@UiHandler("periodType")
	void onChangePeriodType( ChangeEvent event) {
		periodPanel.setVisible((periodType.getSelectedIndex() != 0));
	}

	public void populate(Mod200Object obj) {
		obj.getMod200().setEnterpriseDocument(nif.getValue());
		obj.getMod200().setEnterpriseName(companyName.getValue());
		obj.getMod200().setEnterprisePhone1(phone1.getValue());
		obj.getMod200().setEnterprisePhone2(phone2.getValue());
		obj.getMod200().setPeriodType(periodType.getSelectedIndex() + 1 );
		obj.getMod200().setPeriodStart( periodStart.getValue() );
		obj.getMod200().setPeriodStart( periodEnd.getValue() );
		obj.getMod200().setBalanceType( balanceSheetType.getSelectedIndex() );
		obj.getMod200().setPygType( profitAndLossType.getSelectedIndex() );
		obj.getMod200().setCnae(cnae.getValue());
		DoubleVariable bv = null;
		for (Mod200Key key : inputs.keySet()) {
			bv = new DoubleVariable( key );
			bv.setValue(inputs.get(key).getValue());
			obj.getMod200().addVariable(bv);
		}
		
		bv = new DoubleVariable( Mod200Key.C0061 );
		bv.setValue( c061.getValue() );
		obj.getMod200().addVariable(bv);
		
		bv = new DoubleVariable( Mod200Key.C0050 );
		bv.setValue((obj.getMod200().getBalanceType() == BalanceType.NORMAL));
		obj.getMod200().addVariable(bv);
		
		bv = new DoubleVariable( Mod200Key.C0051 );
		bv.setValue((obj.getMod200().getBalanceType() == BalanceType.ABREVIADO));
		obj.getMod200().addVariable(bv);
		
		bv = new DoubleVariable( Mod200Key.C0052 );
		bv.setValue((obj.getMod200().getBalanceType() == BalanceType.PYMES));
		obj.getMod200().addVariable(bv);
			
		bv = new DoubleVariable( Mod200Key.C0053 );
		bv.setValue((obj.getMod200().getBalanceType() == BalanceType.NORMAL));
		obj.getMod200().addVariable(bv);
		
		bv = new DoubleVariable( Mod200Key.C0054 );
		bv.setValue((obj.getMod200().getBalanceType() == BalanceType.ABREVIADO));
		obj.getMod200().addVariable(bv);
		
		bv = new DoubleVariable( Mod200Key.C0055 );
		bv.setValue((obj.getMod200().getBalanceType() == BalanceType.PYMES));
		obj.getMod200().addVariable(bv);
		
		bv = new DoubleVariable( Mod200Key.C0041 );
		bv.setValue( c041.getDoubleValue() );
		obj.getMod200().addVariable(bv);

		bv = new DoubleVariable( Mod200Key.C0042 );
		bv.setValue( c042.getDoubleValue() );
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

}
