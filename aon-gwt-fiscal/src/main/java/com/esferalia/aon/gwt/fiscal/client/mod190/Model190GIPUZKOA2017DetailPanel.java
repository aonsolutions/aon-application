package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190GIPUZKOADetail2017.IModel190DetailCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Mod1902016Key;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class Model190GIPUZKOA2017DetailPanel extends SimpleLayoutPanel implements Focusable {
	
	private static class MediumLabel extends InlineLabel {
		private MediumLabel(String label) {
			super(label);
			setStyleName(AON.AON_CSS.aonFontMedium());
		}
	}
	private int tabIndex; 
	private DocumentTextBox document;
	
	public Model190GIPUZKOA2017DetailPanel(Mod190Detail detail, IModel190DetailCallback callback) {
		FlowPanel additionalDataPanel = new FlowPanel();
		FlowPanel ilPanel = new FlowPanel();
		
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		FlexTable tab1 = new FlexTable();
		panel.add(tab1);
		tab1.getColumnFormatter().setWidth(0, "100px");
		tab1.getColumnFormatter().setWidth(1, "100px");
		tab1.getColumnFormatter().setWidth(2, "300px");
		tab1.getColumnFormatter().setWidth(3, "auto");
		tab1.setStyleName(AON.AON_CSS.aonWidthAll());
		tab1.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab1.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab1.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab1.getFlexCellFormatter().setColSpan(0, 0, 4);
		tab1.setWidget(0, 0, new InlineLabel(AON.MSG.perceptionData()));

 		tab1.setWidget(1, 0, new MediumLabel(AON.MSG.receiverDocument()));
		tab1.setWidget(1, 1, new MediumLabel(AON.MSG.representativeDocument()));
		tab1.setWidget(1, 2, new MediumLabel(AON.MSG.fullName()));
		tab1.setWidget(1, 3, new MediumLabel(AON.MSG.province()));
		
		document = new DocumentTextBox();
		document.setValue(detail.getDocument());
		document.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setDocument(document.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab1.setWidget(2, 0, document);
		
		DocumentTextBox representativeDocument = new DocumentTextBox();
		representativeDocument.setValue(detail.getRepresentativeDocument());
		representativeDocument.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setRepresentativeDocument(representativeDocument.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab1.setWidget(2, 1, representativeDocument);
		
		TextBox name = new TextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setStyleName(AON.AON_CSS.aonInputText());
		name.setValue(detail.getName());
		name.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setName(name.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab1.setWidget(2, 2, name);
		
		ProvinceListBox province = new ProvinceListBox();
		province.setSelectedIndex(detail.getProvince());
		province.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				detail.setProvince(province.getSelectedIndex());
				callback.onValueChanged(detail);
			}
		});
		tab1.setWidget(2, 3, province);

		FlexTable tab2 = new FlexTable();
		panel.add(tab2);
		tab2.getColumnFormatter().setWidth(0, "40px");
		tab2.getColumnFormatter().setWidth(1, "40px");
		tab2.getColumnFormatter().setWidth(2, "40px");
		tab2.getColumnFormatter().setWidth(3, "40px");
		tab2.getColumnFormatter().setWidth(4, "90px");
		tab2.getColumnFormatter().setWidth(5, "150px");
		tab2.getColumnFormatter().setWidth(6, "150px");
		tab2.getColumnFormatter().setWidth(8, "auto");
		
		tab2.setStyleName(AON.AON_CSS.aonWidthAll());
		tab2.addStyleName(AON.AON_CSS.aonNowrap());
		
		
		tab2.setWidget(0, 0, new MediumLabel(AON.MSG.key()));
		tab2.getFlexCellFormatter().setRowSpan(0, 0, 4);
		
		final ListBox subkey = new ListBox();
		subkey.setWidth("45px");

		final ListBox key = new ListBox();
		key.setWidth("40px");
		for (Mod1902016Key k : Mod1902016Key.values()) {
			key.addItem(k.getDescription(),k.getValue());
		}
		
		Model190GIPUZKOA2017DetailPanel.setValue(key, subkey, detail);
		
		key.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				subkey.clear();
				Mod1902016Key keyEnum = Mod1902016Key.values()[key.getSelectedIndex()];
				detail.setKey( keyEnum.toString() );
				if (keyEnum.hasSubkeys()) {
					subkey.setEnabled(true);
					for (int i = 0; i < keyEnum.getSubKeys().length; i++) {
						subkey.addItem(keyEnum.getSubKeys()[i]);
					}
					detail.setSubKey(keyEnum.getSubKeys()[0]);
				} else {
					subkey.setEnabled(false);
					detail.setSubKey(null);
				}
				Model190GIPUZKOA2017DetailPanel.enableOrDisableIlPanel(key, subkey, ilPanel);
				Model190GIPUZKOA2017DetailPanel.enableOrDisableAdditionalDataPanel(key, subkey, additionalDataPanel);
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(0, 1, key);
		tab2.getFlexCellFormatter().setRowSpan(0, 1, 4);
		
		subkey.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Mod1902016Key keyEnum = Mod1902016Key.values()[key.getSelectedIndex()];
				if (keyEnum.hasSubkeys()) {
					int idx = subkey.getSelectedIndex() == -1 ? 0 : subkey.getSelectedIndex();
					detail.setSubKey(keyEnum.getSubKeys()[idx]);
				} else {
					subkey.setEnabled(false);
					detail.setSubKey(null);
				}
				Model190GIPUZKOA2017DetailPanel.enableOrDisableIlPanel(key, subkey, ilPanel);
				Model190GIPUZKOA2017DetailPanel.enableOrDisableAdditionalDataPanel(key, subkey, additionalDataPanel);
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(0, 2, new MediumLabel(AON.MSG.subkey()));
		tab2.getFlexCellFormatter().setRowSpan(0, 2, 4);
		
		tab2.setWidget(0, 3, subkey);
		tab2.getFlexCellFormatter().setRowSpan(0, 3, 4);

		tab2.setWidget(0, 4, new MediumLabel(AON.MSG.money()));
		tab2.getFlexCellFormatter().setRowSpan(0, 4, 2);
		
		tab2.setWidget(0, 5, new MediumLabel(AON.MSG.perception()));
		tab2.setWidget(0, 6, new MediumLabel(AON.MSG.retention()));
		tab2.setWidget(0, 7, new MediumLabel(AON.MSG.accrualYear()));
		
		
		DoubleBox perception = new DoubleBox();
		perception.setValue(detail.getPerception());
		perception.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setPerception(perception.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(1, 0, perception);
		
		DoubleBox retention = new DoubleBox();
		retention.setValue(detail.getRetention());
		retention.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setRetention(retention.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(1, 1, retention);

		IntegerBox accrualYear = new IntegerBox();
		accrualYear.setMaxLength(4);
		accrualYear.setVisibleLength(4);
		accrualYear.setValue(detail.getAccrualYear());
		accrualYear.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setAccrualYear(accrualYear.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(1, 2, accrualYear);
		
		tab2.setWidget(2, 0, new MediumLabel(AON.MSG.inKind()));
		tab2.getFlexCellFormatter().setRowSpan(2, 0, 2);
		
		tab2.setWidget(2, 1, new MediumLabel(AON.MSG.inKindPerception()));
		tab2.setWidget(2, 2, new MediumLabel(AON.MSG.inKindDeposit()));
		tab2.setWidget(2, 3, new MediumLabel(AON.MSG.inKindOutputDeposit()));
		
		DoubleBox inKindPerception = new DoubleBox();
		inKindPerception.setValue(detail.getInKindPerception());
		inKindPerception.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setInKindPerception(inKindPerception.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(3, 0, inKindPerception);
		
		DoubleBox inKindDeposit = new DoubleBox();
		inKindDeposit.setValue(detail.getInKindDeposit());
		inKindDeposit.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setInKindDeposit(inKindDeposit.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(3, 1, inKindDeposit);
		
		DoubleBox inKindOutputDeposit = new DoubleBox();
		inKindOutputDeposit.setValue(detail.getInKindOutputDeposit());
		inKindOutputDeposit.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setInKindOutputDeposit(inKindOutputDeposit.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(3, 2, inKindOutputDeposit);

		
		FlexTable tab3 = new FlexTable();
		tab3.getColumnFormatter().setWidth(0, "160px");
		tab3.getColumnFormatter().setWidth(1, "150px");
		tab3.getColumnFormatter().setWidth(2, "150px");
		tab3.getColumnFormatter().setWidth(3, "150px");
		tab3.getColumnFormatter().setWidth(4, "auto");
		tab3.setStyleName(AON.AON_CSS.aonWidthAll());
		tab3.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab3.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab3.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab3.getFlexCellFormatter().setColSpan(0, 0, 5);
		tab3.setWidget(0, 0, new InlineLabel("Percepciones derivadas de incapacidad laboral."));

		tab3.setWidget(1, 0, new Label());
		tab3.setWidget(1, 1, new MediumLabel(AON.MSG.perceptionValoration()));
		tab3.setWidget(1, 2, new MediumLabel(AON.MSG.retentionIncome()));
		tab3.setWidget(1, 3, new MediumLabel(AON.MSG.inKindOutputDeposit()));
		tab3.setWidget(1, 4, new Label());
		
		tab3.setWidget(2, 0, new MediumLabel(AON.MSG.money()));
		tab3.getCellFormatter().addStyleName(2, 0, AON.AON_CSS.aonTextRight());

		DoubleBox perceptionIL = new DoubleBox();
		perceptionIL.setValue(detail.getPerceptionIL());
		perceptionIL.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setPerceptionIL(perceptionIL.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab3.setWidget(2, 1, perceptionIL);
		
		DoubleBox retentionIL = new DoubleBox();
		retentionIL.setValue(detail.getRetentionIL());
		retentionIL.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setRetentionIL(retentionIL.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab3.setWidget(2, 2, retentionIL);
		
		tab3.setWidget(3, 0, new MediumLabel(AON.MSG.inKind()));
		tab3.getCellFormatter().addStyleName(3, 0, AON.AON_CSS.aonTextRight());		
		
		DoubleBox inKindPerceptionIL = new DoubleBox();
		inKindPerceptionIL.setValue(detail.getInKindPerceptionIL());
		inKindPerceptionIL.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setInKindPerceptionIL(inKindPerceptionIL.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab3.setWidget(3, 1, inKindPerceptionIL);

		DoubleBox inKindDepositIL = new DoubleBox();
		inKindDepositIL.setValue(detail.getInKindDepositIL());
		inKindDepositIL.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setInKindDepositIL(inKindDepositIL.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab3.setWidget(3, 2, inKindDepositIL);

		DoubleBox inKindOutputDepositIL = new DoubleBox();
		inKindOutputDepositIL.setValue(detail.getInKindOutputDepositIL());
		inKindOutputDepositIL.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setInKindOutputDepositIL(inKindOutputDepositIL.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab3.setWidget(3, 3, inKindOutputDepositIL);

		Model190GIPUZKOA2017DetailPanel.enableOrDisableIlPanel(key, subkey, ilPanel);
		ilPanel.add(tab3);
		panel.add(ilPanel);
		
		
		FlexTable tab4 = new FlexTable();
		tab4.getColumnFormatter().setWidth( 0, "100px");
		tab4.getColumnFormatter().setWidth( 1, "100px");
		tab4.getColumnFormatter().setWidth( 2, "150px");
		tab4.getColumnFormatter().setWidth( 3, "150px");
		tab4.getColumnFormatter().setWidth( 4, "150px");
		tab4.getColumnFormatter().setWidth( 5, "auto");
		tab4.setStyleName(AON.AON_CSS.aonWidthAll());
		tab4.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab4.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab4.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab4.getFlexCellFormatter().setColSpan(0, 0, 6);
		tab4.setWidget(0, 0, new InlineLabel(AON.MSG.additionalData()));
		
		tab4.setWidget(1, 0, new MediumLabel(AON.MSG.disability()));
		tab4.setWidget(1, 1, new MediumLabel(AON.MSG.contract()));
		tab4.setWidget(1, 2, new MediumLabel(AON.MSG.applicableReduction()));
		tab4.setWidget(1, 3, new MediumLabel(AON.MSG.deducibleExpense()));
		tab4.setWidget(1, 4, new MediumLabel(AON.MSG.compensatoryPension()));
		tab4.setWidget(1, 5, new MediumLabel( AonStringUtils.abbreviate(AON.MSG.descendant(),34) ));
		
		ListBox disability = new ListBox();
		disability.setWidth("40px");
		disability.addItem("0");
		disability.addItem("1");
		disability.addItem("2");
		disability.addItem("3");
		disability.setSelectedIndex(detail.getDisability());
		disability.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				detail.setDisability((byte) disability.getSelectedIndex());
				callback.onValueChanged(detail);
			}
		});
		tab4.setWidget(2, 0, disability);
		
		
		ListBox contract = new ListBox();
		contract.setWidth("40px");
		contract.addItem("-");
		contract.addItem("1");
		contract.addItem("2");
		contract.addItem("3");
		contract.addItem("4");
		contract.setSelectedIndex(detail.getContract());
		contract.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				detail.setContract((byte) contract.getSelectedIndex());
				callback.onValueChanged(detail);
			}
		});
		tab4.setWidget(2, 1, contract);

		DoubleBox applicableReduction = new DoubleBox();
		applicableReduction.setValue(detail.getApplicableReduction());
		applicableReduction.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setApplicableReduction(applicableReduction.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab4.setWidget(2, 2, applicableReduction);

		
		DoubleBox deducibleExpense = new DoubleBox();
		deducibleExpense.setValue(detail.getDeducibleExpense());
		deducibleExpense.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setDeducibleExpense(deducibleExpense.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab4.setWidget(2, 3, deducibleExpense);

		
		DoubleBox compensatoryPension = new DoubleBox();
		compensatoryPension.setValue(detail.getCompensatoryPension());
		compensatoryPension.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setCompensatoryPension(compensatoryPension.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab4.setWidget(2, 4, compensatoryPension);
		
		IntegerBox otherDescendent = new IntegerBox();
		otherDescendent.setMaxLength(1);
		otherDescendent.setVisibleLength(1);
		otherDescendent.setValue(detail.getOtherDescendent());
		otherDescendent.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setOtherDescendent( AonNumberUtils.toByte( otherDescendent.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab4.setWidget(2, 5, otherDescendent);

		additionalDataPanel.add(tab4);
		panel.add(additionalDataPanel);
		enableOrDisableAdditionalDataPanel(key, subkey, additionalDataPanel);

		scroll.setWidget(panel);
		setWidget(scroll);
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
		document.selectAll();
		document.setFocus(true);
	}

	@Override
	public void setTabIndex(int index) {
		tabIndex = index;
	}

	private static void setValue(ListBox key, ListBox subKey, Mod190Detail detail) {
		if (AonStringUtils.isBlank( detail.getKey())) {
			detail.setKey(Mod1902016Key.A.toString());
		}
		Mod1902016Key keyEnum = Mod1902016Key.valueOf(detail.getKey());
		key.setSelectedIndex(keyEnum.ordinal());
		subKey.clear();
		if (keyEnum.hasSubkeys()) {
			subKey.setEnabled(true);
			for (int i = 0; i < keyEnum.getSubKeys().length; i++) {
				subKey.addItem(keyEnum.getSubKeys()[i]);
				if (keyEnum.getSubKeys()[i].equals(detail.getSubKey())) {
					subKey.setSelectedIndex(i);
				}
			}
			subKey.setEnabled(true);
		} else {
			subKey.setEnabled(false);
		}
	}

	private static void enableOrDisableAdditionalDataPanel(ListBox key, ListBox subKey, Panel  panel) {
		Mod1902016Key keyEnum = Mod1902016Key.values()[key.getSelectedIndex()];
		String subk = ((subKey.getSelectedIndex() == -1) ? null : subKey.getValue(subKey.getSelectedIndex()));
		panel.setVisible( 
				    Mod1902016Key.A == keyEnum
				|| (Mod1902016Key.B == keyEnum && "01".equals(subk))
				|| (Mod1902016Key.B == keyEnum && "02".equals(subk))
				|| (Mod1902016Key.B == keyEnum && "04".equals(subk))
				||  Mod1902016Key.C == keyEnum
				|| (Mod1902016Key.E == keyEnum && "01".equals(subk))
				|| (Mod1902016Key.E == keyEnum && "02".equals(subk))
			);
	}
	
	private static void enableOrDisableIlPanel(ListBox key, ListBox subKey, Panel  panel) {
		Mod1902016Key keyEnum = Mod1902016Key.values()[key.getSelectedIndex()];
		String subk = ((subKey.getSelectedIndex() == -1) ? null : subKey.getValue(subKey.getSelectedIndex()));
		panel.setVisible( 
				    Mod1902016Key.A == keyEnum
				|| (Mod1902016Key.B == keyEnum && "01".equals(subk))
			);
	}
	
}
