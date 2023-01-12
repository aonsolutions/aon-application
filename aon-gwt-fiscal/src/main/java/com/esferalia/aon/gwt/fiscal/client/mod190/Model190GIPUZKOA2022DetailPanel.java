package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190GIPUZKOADetail2022.IModel190DetailCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Mod1902022GipuzkoaKey;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model190GIPUZKOA2022DetailPanel extends SimpleLayoutPanel implements Focusable {
	
	private int tabIndex; 
	private DocumentTextBox document;
	
	public Model190GIPUZKOA2022DetailPanel(Mod190Detail detail, IModel190DetailCallback callback) {
		FlowPanel additionalDataPanel = new FlowPanel();
		FlowPanel ilPanel = new FlowPanel();
		
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		
		FlexTable tab1 = new FlexTable();
		panel.add(tab1);
		tab1.getColumnFormatter().setWidth(0, "100px");
		tab1.getColumnFormatter().setWidth(1, "100px");
		tab1.getColumnFormatter().setWidth(2, "300px");
		tab1.getColumnFormatter().setWidth(3, "auto");
		tab1.setStyleName(AON.CSS.aonWidthAll());
		tab1.addStyleName(AON.CSS.aonNowrap());
		
		tab1.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab1.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab1.getFlexCellFormatter().setColSpan(0, 0, 4);
		tab1.setWidget(0, 0, new InlineLabel(AON.MSG.perceptionData()));

 		tab1.setWidget(1, 0, new Model190SmallerLabel(AON.MSG.receiverDocument()));
		tab1.setWidget(1, 1, new Model190SmallerLabel(AON.MSG.representativeDocument()));
		tab1.setWidget(1, 2, new Model190SmallerLabel(AON.MSG.fullName()));
		tab1.setWidget(1, 3, new Model190SmallerLabel(AON.MSG.province()));
		
		document = new DocumentTextBox();
		document.setValue(detail.getDocument());
		document.addValueChangeHandler(event -> {
			detail.setDocument(document.getValue());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(2, 0, document);
		
		DocumentTextBox representativeDocument = new DocumentTextBox();
		representativeDocument.setValue(detail.getRepresentativeDocument());
		representativeDocument.addValueChangeHandler(event -> {
			detail.setRepresentativeDocument(representativeDocument.getValue());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(2, 1, representativeDocument);
		
		AonTextBox name = new AonTextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setValue(detail.getName());
		name.addValueChangeHandler(event -> {
			detail.setName(name.getValue());
			callback.onNameChanged(detail);
		});
		tab1.setWidget(2, 2, name);
		
		ProvinceListBox province = new ProvinceListBox();
		province.setSelectedIndex(detail.getProvince());
		province.addChangeHandler( event -> {
			detail.setProvince(province.getSelectedIndex());
			callback.onValueChanged(detail);
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
		
		tab2.setStyleName(AON.CSS.aonWidthAll());
		tab2.addStyleName(AON.CSS.aonNowrap());
		
		
		tab2.setWidget(0, 0, new Model190SmallerLabel(AON.MSG.key()));
		tab2.getFlexCellFormatter().setRowSpan(0, 0, 4);
		
		final ListBox subkey = new ListBox();
		subkey.setWidth("45px");

		final ListBox key = new ListBox();
		key.setWidth("40px");
		for (Mod1902022GipuzkoaKey k : Mod1902022GipuzkoaKey.values()) {
			key.addItem(k.getDescription(),k.getValue());
		}
		
		Model190GIPUZKOA2022DetailPanel.setValue(key, subkey, detail);
		
		key.addChangeHandler(event -> {
			subkey.clear();
			Mod1902022GipuzkoaKey keyEnum = Mod1902022GipuzkoaKey.values()[key.getSelectedIndex()];
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
			Model190GIPUZKOA2022DetailPanel.enableOrDisableIlPanel(key, subkey, ilPanel);
			Model190GIPUZKOA2022DetailPanel.enableOrDisableAdditionalDataPanel(key, subkey, additionalDataPanel);
			callback.onValueChanged(detail);
		});
		tab2.setWidget(0, 1, key);
		tab2.getFlexCellFormatter().setRowSpan(0, 1, 4);
		
		subkey.addChangeHandler(event -> {
			Mod1902022GipuzkoaKey keyEnum = Mod1902022GipuzkoaKey.values()[key.getSelectedIndex()];
			if (keyEnum.hasSubkeys()) {
				int idx = subkey.getSelectedIndex() == -1 ? 0 : subkey.getSelectedIndex();
				detail.setSubKey(keyEnum.getSubKeys()[idx]);
			} else {
				subkey.setEnabled(false);
				detail.setSubKey(null);
			}
			Model190GIPUZKOA2022DetailPanel.enableOrDisableIlPanel(key, subkey, ilPanel);
			Model190GIPUZKOA2022DetailPanel.enableOrDisableAdditionalDataPanel(key, subkey, additionalDataPanel);
			callback.onValueChanged(detail);
		});
		tab2.setWidget(0, 2, new Model190SmallerLabel(AON.MSG.subkey()));
		tab2.getFlexCellFormatter().setRowSpan(0, 2, 4);
		
		tab2.setWidget(0, 3, subkey);
		tab2.getFlexCellFormatter().setRowSpan(0, 3, 4);

		tab2.setWidget(0, 4, new Model190SmallerLabel(AON.MSG.money()));
		tab2.getFlexCellFormatter().setRowSpan(0, 4, 2);
		
		tab2.setWidget(0, 5, new Model190SmallerLabel(AON.MSG.perception()));
		tab2.setWidget(0, 6, new Model190SmallerLabel(AON.MSG.retention()));
		tab2.setWidget(0, 7, new Model190SmallerLabel(AON.MSG.accrualYear()));
		
		
		AonDoubleBox perception = new AonDoubleBox();
		perception.setValue(detail.getPerception());
		perception.addValueChangeHandler(event -> {
			detail.setPerception(perception.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(1, 0, perception);
		
		AonDoubleBox retention = new AonDoubleBox();
		retention.setValue(detail.getRetention());
		retention.addValueChangeHandler(event -> {
			detail.setRetention(retention.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(1, 1, retention);

		AonIntegerBox accrualYear = new AonIntegerBox();
		accrualYear.setMaxLength(4);
		accrualYear.setVisibleLength(4);
		accrualYear.setValue(detail.getAccrualYear());
		accrualYear.addValueChangeHandler(event -> {
			detail.setAccrualYear(accrualYear.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(1, 2, accrualYear);
		
		tab2.setWidget(2, 0, new Model190SmallerLabel(AON.MSG.inKind()));
		tab2.getFlexCellFormatter().setRowSpan(2, 0, 2);
		
		tab2.setWidget(2, 1, new Model190SmallerLabel(AON.MSG.inKindPerception()));
		tab2.setWidget(2, 2, new Model190SmallerLabel(AON.MSG.inKindDeposit()));
		tab2.setWidget(2, 3, new Model190SmallerLabel(AON.MSG.inKindOutputDeposit()));
		
		AonDoubleBox inKindPerception = new AonDoubleBox();
		inKindPerception.setValue(detail.getInKindPerception());
		inKindPerception.addValueChangeHandler(event -> {
			detail.setInKindPerception(inKindPerception.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(3, 0, inKindPerception);
		
		AonDoubleBox inKindDeposit = new AonDoubleBox();
		inKindDeposit.setValue(detail.getInKindDeposit());
		inKindDeposit.addValueChangeHandler(event -> {
			detail.setInKindDeposit(inKindDeposit.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(3, 1, inKindDeposit);
		
		AonDoubleBox inKindOutputDeposit = new AonDoubleBox();
		inKindOutputDeposit.setValue(detail.getInKindOutputDeposit());
		inKindOutputDeposit.addValueChangeHandler(event -> {
			detail.setInKindOutputDeposit(inKindOutputDeposit.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(3, 2, inKindOutputDeposit);

		
		FlexTable tab3 = new FlexTable();
		tab3.getColumnFormatter().setWidth(0, "160px");
		tab3.getColumnFormatter().setWidth(1, "150px");
		tab3.getColumnFormatter().setWidth(2, "150px");
		tab3.getColumnFormatter().setWidth(3, "150px");
		tab3.getColumnFormatter().setWidth(4, "auto");
		tab3.setStyleName(AON.CSS.aonWidthAll());
		tab3.addStyleName(AON.CSS.aonNowrap());
		
		tab3.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab3.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab3.getFlexCellFormatter().setColSpan(0, 0, 5);
		tab3.setWidget(0, 0, new InlineLabel("Percepciones derivadas de incapacidad laboral (s\u00F3lo para percepciones de las claves A, B.01"));

		tab3.setWidget(1, 0, new Label());
		tab3.setWidget(1, 1, new Model190SmallerLabel(AON.MSG.perceptionValoration()));
		tab3.setWidget(1, 2, new Model190SmallerLabel(AON.MSG.retentionIncome()));
		tab3.setWidget(1, 3, new Model190SmallerLabel(AON.MSG.inKindOutputDeposit()));
		tab3.setWidget(1, 4, new Label());
		
		tab3.setWidget(2, 0, new Label());
		AonDoubleBox perceptionIL = new AonDoubleBox();
		perceptionIL.setValue(detail.getPerceptionIL());
		perceptionIL.addValueChangeHandler(event -> {
			detail.setPerceptionIL(perceptionIL.getValue());
			callback.onValueChanged(detail);
		});
		tab3.setWidget(2, 1, perceptionIL);
		
		
		AonDoubleBox retentionIL = new AonDoubleBox();
		retentionIL.setValue(detail.getRetentionIL());
		retentionIL.addValueChangeHandler(event -> {
			detail.setRetentionIL(retentionIL.getValue());
			callback.onValueChanged(detail);
		});
		tab3.setWidget(2, 2, retentionIL);
		
		AonDoubleBox outputRetentionIL = new AonDoubleBox();
		outputRetentionIL.setValue(detail.getOutputRetentionIL());
		outputRetentionIL.addValueChangeHandler(event -> {
			detail.setOutputRetentionIL(outputRetentionIL.getValue());
			callback.onValueChanged(detail);
		});
		tab3.setWidget(2, 3, outputRetentionIL);
		Model190GIPUZKOA2022DetailPanel.enableOrDisableIlPanel(key, subkey, ilPanel);
		ilPanel.add(tab3);
		panel.add(ilPanel);
		
		
		FlexTable tab4 = new FlexTable();
		tab4.getColumnFormatter().setWidth( 0, "100px");
		tab4.getColumnFormatter().setWidth( 1, "100px");
		tab4.getColumnFormatter().setWidth( 2, "150px");
		tab4.getColumnFormatter().setWidth( 3, "150px");
		tab4.getColumnFormatter().setWidth( 4, "150px");
		tab4.getColumnFormatter().setWidth( 5, "auto");
		tab4.setStyleName(AON.CSS.aonWidthAll());
		tab4.addStyleName(AON.CSS.aonNowrap());
		
		tab4.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab4.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab4.getFlexCellFormatter().setColSpan(0, 0, 6);
		tab4.setWidget(0, 0, new InlineLabel(AON.MSG.additionalData()));
		
		tab4.setWidget(1, 0, new Model190SmallerLabel(AON.MSG.disability()));
		tab4.setWidget(1, 1, new Model190SmallerLabel(AON.MSG.contract()));
		tab4.setWidget(1, 2, new Model190SmallerLabel(AON.MSG.applicableReduction()));
		tab4.setWidget(1, 3, new Model190SmallerLabel(AON.MSG.deducibleExpense()));
		tab4.setWidget(1, 4, new Model190SmallerLabel(AON.MSG.compensatoryPension()));
		tab4.setWidget(1, 5, new Model190SmallerLabel( AonStringUtils.abbreviate(AON.MSG.descendant(),34) ));
		
		ListBox disability = new ListBox();
		disability.setWidth("40px");
		disability.addItem("0");
		disability.addItem("1");
		disability.addItem("2");
		disability.addItem("3");
		disability.setSelectedIndex(detail.getDisability());
		disability.addChangeHandler(event -> {
			detail.setDisability((byte) disability.getSelectedIndex());
			callback.onValueChanged(detail);
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
		contract.addChangeHandler(event -> {
			detail.setContract((byte) contract.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(2, 1, contract);

		AonDoubleBox applicableReduction = new AonDoubleBox();
		applicableReduction.setValue(detail.getApplicableReduction());
		applicableReduction.addValueChangeHandler(event -> {
			detail.setApplicableReduction(applicableReduction.getValue());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(2, 2, applicableReduction);

		
		AonDoubleBox deducibleExpense = new AonDoubleBox();
		deducibleExpense.setValue(detail.getDeducibleExpense());
		deducibleExpense.addValueChangeHandler(event -> {
			detail.setDeducibleExpense(deducibleExpense.getValue());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(2, 3, deducibleExpense);

		
		AonDoubleBox compensatoryPension = new AonDoubleBox();
		compensatoryPension.setValue(detail.getCompensatoryPension());
		compensatoryPension.addValueChangeHandler(event -> {
			detail.setCompensatoryPension(compensatoryPension.getValue());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(2, 4, compensatoryPension);
		
		AonIntegerBox otherDescendent = new AonIntegerBox();
		otherDescendent.setMaxLength(1);
		otherDescendent.setVisibleLength(1);
		otherDescendent.setValue(detail.getOtherDescendent());
		otherDescendent.addValueChangeHandler(event -> {
			detail.setOtherDescendent( AonNumberUtils.toByte( otherDescendent.getValue()));
			callback.onValueChanged(detail);
		});
		tab4.setWidget(2, 5, otherDescendent);

		
		tab4.setWidget(3, 0, new Model190SmallerLabel("Tit. unidad conviv."));
		ListBox titConvivivencia = new ListBox();
		titConvivivencia.setWidth("40px");
		titConvivivencia.addItem("----");
		titConvivivencia.addItem("1 - El perceptor es el titular de la unidad de convivencia.");
		titConvivivencia.addItem("2 - El perceptor NO es el titular de la unidad de convivencia."); 
		titConvivivencia.setSelectedIndex(detail.getTitConvivencia());
		titConvivivencia.addChangeHandler(event -> {
			detail.setTitConvivencia((byte) titConvivivencia.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(4,0 , titConvivivencia);
		
		tab4.setWidget(3, 1, new Model190SmallerLabel(AON.MSG.spouseDocument()));
		AonDocumentTextBox spouseDocument = new AonDocumentTextBox();
		spouseDocument.setVisibleLength(9);
		spouseDocument.setMaxLength(9);

		spouseDocument.setValue(detail.getSpouseDocument());
		spouseDocument.addValueChangeHandler(event -> {
			detail.setSpouseDocument(spouseDocument.getValue());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(4, 1, spouseDocument);

		tab4.setWidget(3, 2, new Model190SmallerLabel("Compl. ayuda infancia"));
		ListBox compInfancia = new ListBox();
		compInfancia.setWidth("40px");
		compInfancia.addItem("----");
		compInfancia.addItem("1 - La prestaci\u00F3n incluye cuant\u00EDas complemento de ayuda para la infancia previsto en el IMV");
		compInfancia.addItem("2 - La prestaci\u00F3n NO incluye cuant\u00EDas complemento de ayuda para la infancia previsto en el IMV"); 
		compInfancia.setSelectedIndex(detail.getCompInfancia());
		compInfancia.addChangeHandler(event -> {
			detail.setCompInfancia((byte) compInfancia.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(4, 2, compInfancia);
		
		
		
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
			detail.setKey(Mod1902022GipuzkoaKey.A.toString());
		}
		Mod1902022GipuzkoaKey keyEnum = Mod1902022GipuzkoaKey.valueOf(detail.getKey());
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
		Mod1902022GipuzkoaKey keyEnum = Mod1902022GipuzkoaKey.values()[key.getSelectedIndex()];
		String subk = ((subKey.getSelectedIndex() == -1) ? null : subKey.getValue(subKey.getSelectedIndex()));
		panel.setVisible( 
					Mod1902022GipuzkoaKey.A == keyEnum
				|| (Mod1902022GipuzkoaKey.B == keyEnum && "01".equals(subk))
				|| (Mod1902022GipuzkoaKey.B == keyEnum && "02".equals(subk))
				|| (Mod1902022GipuzkoaKey.B == keyEnum && "04".equals(subk))
				||  Mod1902022GipuzkoaKey.C == keyEnum
				|| (Mod1902022GipuzkoaKey.E == keyEnum && "01".equals(subk))
				|| (Mod1902022GipuzkoaKey.L == keyEnum && "29".equals(subk))
			);
	}
	
	private static void enableOrDisableIlPanel(ListBox key, ListBox subKey, Panel  panel) {
		Mod1902022GipuzkoaKey keyEnum = Mod1902022GipuzkoaKey.values()[key.getSelectedIndex()];
		String subk = ((subKey.getSelectedIndex() == -1) ? null : subKey.getValue(subKey.getSelectedIndex()));
		panel.setVisible( 
				Mod1902022GipuzkoaKey.A == keyEnum
				|| (Mod1902022GipuzkoaKey.B == keyEnum && "01".equals(subk))
			);
	}
	
}
