package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190AEATDetail2017.IModel190DetailCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Mod1902016Key;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
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

public class Model190AEAT2017DetailPanel extends SimpleLayoutPanel implements Focusable {
	
	private static class MediumLabel extends InlineLabel {
		private MediumLabel(String label) {
			super(label);
			setStyleName(AON.AON_CSS.aonFontMedium());
		}
	}
	private int tabIndex; 
	private DocumentTextBox document;
	
	public Model190AEAT2017DetailPanel(Mod190Detail detail, IModel190DetailCallback callback) {
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
		tab2.getColumnFormatter().setWidth(7, "150px");
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
		
		Model190AEAT2017DetailPanel.setValue(key, subkey, detail);
		
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
				Model190AEAT2017DetailPanel.enableOrDisableIlPanel(key, subkey, ilPanel);
				Model190AEAT2017DetailPanel.enableOrDisableAdditionalDataPanel(key, subkey, additionalDataPanel);
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
				Model190AEAT2017DetailPanel.enableOrDisableIlPanel(key, subkey, ilPanel);
				Model190AEAT2017DetailPanel.enableOrDisableAdditionalDataPanel(key, subkey, additionalDataPanel);
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
		tab2.setWidget(0, 7, new Label());
		tab2.setWidget(0, 8, new MediumLabel(AON.MSG.accrualYear()));
		
		
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

		tab2.setWidget(1, 2, new Label());
		
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
		tab2.setWidget(1, 3, accrualYear);
		
		tab2.setWidget(2, 0, new MediumLabel(AON.MSG.inKind()));
		tab2.getFlexCellFormatter().setRowSpan(2, 0, 2);
		
		tab2.setWidget(2, 1, new MediumLabel(AON.MSG.inKindPerception()));
		tab2.setWidget(2, 2, new MediumLabel(AON.MSG.inKindDeposit()));
		tab2.setWidget(2, 3, new MediumLabel(AON.MSG.inKindOutputDeposit()));
		tab2.setWidget(2, 4, new Label());
		
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

		CheckBox ceutaMelilla = new CheckBox(AON.MSG.ceutaMelillaAbbrv());
		ceutaMelilla.setStyleName(AON.AON_CSS.aonFontMedium());
		ceutaMelilla.setValue(detail.isCeutaMelilla());
		ceutaMelilla.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				detail.setCeutaMelilla(ceutaMelilla.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(3, 3, ceutaMelilla);
		
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
		tab3.setWidget(0, 0, new InlineLabel("Percepciones derivadas de incapacidad laboral (s\u00F3lo para percepciones de las claves A, B.01"));

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
		
		Model190AEAT2017DetailPanel.enableOrDisableIlPanel(key, subkey, ilPanel);
		ilPanel.add(tab3);
		panel.add(ilPanel);
		
		FlexTable tab4 = new FlexTable();
		tab4.getColumnFormatter().setWidth( 0, "110px");
		tab4.getColumnFormatter().setWidth( 1, "80px");
		tab4.getColumnFormatter().setWidth( 2, "100px");
		tab4.getColumnFormatter().setWidth( 3, "50px");
		tab4.getColumnFormatter().setWidth( 4, "80px");
		tab4.getColumnFormatter().setWidth( 5, "100px");
		tab4.getColumnFormatter().setWidth( 6, "100px");
		tab4.getColumnFormatter().setWidth( 7, "50px");
		tab4.getColumnFormatter().setWidth( 8, "120px");
		tab4.getColumnFormatter().setWidth( 9, "auto");
		tab4.setStyleName(AON.AON_CSS.aonWidthAll());
		tab4.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab4.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab4.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab4.getFlexCellFormatter().setColSpan(0, 0, 10);
		tab4.setWidget(0, 0, new InlineLabel(AON.MSG.additionalData()));
		
		tab4.setWidget(1, 0, new MediumLabel(AON.MSG.birthYear()));
		IntegerBox birthYear = new IntegerBox();
		birthYear.setMaxLength(4);
		birthYear.setVisibleLength(4);
		birthYear.setValue(detail.getBirthYear());
		birthYear.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setBirthYear(birthYear.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab4.setWidget(1, 1, birthYear);
		
		tab4.setWidget(1, 2, new MediumLabel(AON.MSG.familySituation()));
		ListBox familySituation = new ListBox();
		familySituation.setWidth("40px");
		familySituation.addItem("-");
		familySituation.addItem("1");
		familySituation.addItem("2");
		familySituation.addItem("3");
		familySituation.setSelectedIndex(detail.getFamilySituation());
		familySituation.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				detail.setFamilySituation((byte) familySituation.getSelectedIndex());
				callback.onValueChanged(detail);
			}
		});
		tab4.setWidget(1, 3, familySituation);
		
		tab4.setWidget(1, 4, new MediumLabel(AON.MSG.spouseDocument()));
		DocumentTextBox spouseDocument = new DocumentTextBox();
		spouseDocument.setVisibleLength(9);
		spouseDocument.setMaxLength(9);

		spouseDocument.setValue(detail.getSpouseDocument());
		spouseDocument.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setSpouseDocument(spouseDocument.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab4.setWidget(1, 5, spouseDocument);
		
		tab4.setWidget(1, 6, new MediumLabel(AON.MSG.disability()));
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
		tab4.setWidget(1, 7, disability);
		
		tab4.setWidget(1, 8, new MediumLabel(AON.MSG.contract()));
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
		tab4.setWidget(1, 9, contract);
		
		additionalDataPanel.add(tab4);
		
		FlexTable tab5 = new FlexTable();
		tab5.getColumnFormatter().setWidth(0, "150px");
		tab5.getColumnFormatter().setWidth(1, "150px");
		tab5.getColumnFormatter().setWidth(2, "150px");
		tab5.getColumnFormatter().setWidth(3, "170px");
		tab5.getColumnFormatter().setWidth(4, "150px");
		tab5.getColumnFormatter().setWidth(5, "auto");
		tab5.setStyleName(AON.AON_CSS.aonWidthAll());
		tab5.addStyleName(AON.AON_CSS.aonNowrap());
		
		CheckBox geographicMobility = new CheckBox(AON.MSG.geographicMobility());
		geographicMobility.setStyleName(AON.AON_CSS.aonFontMedium());
		geographicMobility.setValue(detail.isGeographicMobility());
		geographicMobility.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				detail.setGeographicMobility(geographicMobility.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(0, 0, geographicMobility);

		tab5.setWidget(0, 1, new MediumLabel(AON.MSG.applicableReduction()));
		tab5.setWidget(0, 2, new MediumLabel(AON.MSG.deducibleExpense()));
		tab5.setWidget(0, 3, new MediumLabel(AON.MSG.compensatoryPension()));
		tab5.setWidget(0, 4, new MediumLabel(AON.MSG.foodAnnuality()));
		tab5.setWidget(0, 5, new Label());
		

		CheckBox homeLoanCommunnication = new CheckBox(AON.MSG.homeLoanCommunnication());
		homeLoanCommunnication.setStyleName(AON.AON_CSS.aonFontMedium());
		homeLoanCommunnication.setValue(detail.isHomeLoanCommunnication());
		homeLoanCommunnication.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				detail.setHomeLoanCommunnication(homeLoanCommunnication.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(1, 0, geographicMobility);

		DoubleBox applicableReduction = new DoubleBox();
		applicableReduction.setValue(detail.getApplicableReduction());
		applicableReduction.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setApplicableReduction(applicableReduction.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(1, 1, applicableReduction);

		DoubleBox deducibleExpense = new DoubleBox();
		deducibleExpense.setValue(detail.getDeducibleExpense());
		deducibleExpense.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setDeducibleExpense(deducibleExpense.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(1, 2, deducibleExpense);
		
		DoubleBox compensatoryPension = new DoubleBox();
		compensatoryPension.setValue(detail.getCompensatoryPension());
		compensatoryPension.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setCompensatoryPension(compensatoryPension.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(1, 3, compensatoryPension);

		DoubleBox foodAnnuality = new DoubleBox();
		foodAnnuality.setValue(detail.getFoodAnnuality());
		foodAnnuality.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setFoodAnnuality(foodAnnuality.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(1, 4, foodAnnuality);
		
		tab5.setWidget(1, 5, new Label());
		additionalDataPanel.add(tab5);
		
		FlexTable tab6 = new FlexTable();
		tab6.getColumnFormatter().setWidth(0, "220px");
		tab6.getColumnFormatter().setWidth(1, "60px");
		tab6.getColumnFormatter().setWidth(2, "60px");
		tab6.getColumnFormatter().setWidth(3, "50px");
		tab6.getColumnFormatter().setWidth(4, "60px");
		tab6.getColumnFormatter().setWidth(5, "60px");
		tab6.getColumnFormatter().setWidth(6, "150px");
		tab6.getColumnFormatter().setWidth(7, "40px");
		tab6.getColumnFormatter().setWidth(8, "40px");
		tab6.getColumnFormatter().setWidth(9, "auto");
		tab6.setStyleName(AON.AON_CSS.aonWidthAll());
		tab6.addStyleName(AON.AON_CSS.aonNowrap());

		MediumLabel descendant = new MediumLabel( AonStringUtils.abbreviate(AON.MSG.descendant(),34) );
		descendant.addStyleName(AON.AON_CSS.aonBold());
		tab6.setWidget(0, 0, descendant );
		tab6.setWidget(0, 1, new MediumLabel(AON.MSG.total()));
		tab6.setWidget(0, 2, new MediumLabel(AON.MSG.byInteger()));
		tab6.setWidget(0, 3, new Label());
		tab6.setWidget(0, 4, new MediumLabel(AON.MSG.total()));
		tab6.setWidget(0, 5, new MediumLabel(AON.MSG.byInteger()));
		tab6.setWidget(0, 6, new Label());
		tab6.setWidget(0, 7, new MediumLabel(AON.MSG.first()));
		tab6.setWidget(0, 8, new MediumLabel(AON.MSG.second()));
		tab6.setWidget(0, 9, new MediumLabel(AON.MSG.third()));

		tab6.setWidget(1, 0, new MediumLabel(AON.MSG.lessThan3()));
		tab6.getCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonTextRight());
		IntegerBox lessThan3Descendent = new IntegerBox();
		lessThan3Descendent.setMaxLength(1);
		lessThan3Descendent.setVisibleLength(1);
		lessThan3Descendent.setValue(detail.getLessThan3Descendent());
		lessThan3Descendent.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setLessThan3Descendent( AonNumberUtils.toByte( lessThan3Descendent.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(1, 1, lessThan3Descendent);
		
		IntegerBox lessThan3DescendentRatio = new IntegerBox();
		lessThan3DescendentRatio.setMaxLength(1);
		lessThan3DescendentRatio.setVisibleLength(1);
		lessThan3DescendentRatio.setValue(detail.getLessThan3DescendentRatio());
		lessThan3DescendentRatio.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setLessThan3DescendentRatio( AonNumberUtils.toByte( lessThan3DescendentRatio.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(1, 2, lessThan3DescendentRatio);

		tab6.setWidget(1, 3, new MediumLabel(AON.MSG.remainder()));
		tab6.getCellFormatter().setStyleName(1, 3, AON.AON_CSS.aonTextRight());
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
		tab6.setWidget(1, 4, otherDescendent);
		
		IntegerBox otherDescendentRatio = new IntegerBox();
		otherDescendentRatio.setMaxLength(1);
		otherDescendentRatio.setVisibleLength(1);
		otherDescendentRatio.setValue(detail.getOtherDescendentRatio());
		otherDescendentRatio.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setOtherDescendentRatio( AonNumberUtils.toByte( otherDescendentRatio.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(1, 5, otherDescendentRatio);

		tab6.setWidget(1, 6, new MediumLabel(AON.MSG.first3Calculation()));
		ListBox firstChildCalculation = new ListBox();
		firstChildCalculation.setWidth("40px");
		firstChildCalculation.addItem("-");
		firstChildCalculation.addItem("1");
		firstChildCalculation.addItem("2");
		firstChildCalculation.setSelectedIndex(detail.getFirstChildCalculation());
		firstChildCalculation.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				detail.setFirstChildCalculation(AonNumberUtils.toByte( firstChildCalculation.getSelectedIndex()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(1, 7, firstChildCalculation);
		
		ListBox secondChildCalculation = new ListBox();
		secondChildCalculation.setWidth("40px");
		secondChildCalculation.addItem("-");
		secondChildCalculation.addItem("1");
		secondChildCalculation.addItem("2");
		secondChildCalculation.setSelectedIndex(detail.getSecondChildCalculation());
		secondChildCalculation.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				detail.setSecondChildCalculation(AonNumberUtils.toByte( secondChildCalculation.getSelectedIndex()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(1, 8, secondChildCalculation);

		ListBox thirdChildCalculation = new ListBox();
		thirdChildCalculation.setWidth("40px");
		thirdChildCalculation.addItem("-");
		thirdChildCalculation.addItem("1");
		thirdChildCalculation.addItem("2");
		thirdChildCalculation.setSelectedIndex(detail.getThirdChildCalculation());
		thirdChildCalculation.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				detail.setThirdChildCalculation(AonNumberUtils.toByte( thirdChildCalculation.getSelectedIndex()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(1, 9, thirdChildCalculation);

		MediumLabel disabilityDescendant = new MediumLabel( AonStringUtils.abbreviate(AON.MSG.disabilityDescendant(),34) );
		disabilityDescendant.addStyleName(AON.AON_CSS.aonBold());
		tab6.setWidget(2, 0, disabilityDescendant );
		tab6.setWidget(2, 1, new MediumLabel(AON.MSG.total()));
		tab6.setWidget(2, 2, new MediumLabel(AON.MSG.byInteger()));
		tab6.setWidget(2, 3, new Label());
		tab6.setWidget(2, 4, new MediumLabel(AON.MSG.total()));
		tab6.setWidget(2, 5, new MediumLabel(AON.MSG.byInteger()));
		tab6.setWidget(2, 6, new Label());
		tab6.setWidget(2, 7, new MediumLabel(AON.MSG.total()));
		tab6.setWidget(2, 8, new MediumLabel(AON.MSG.byInteger()));
		tab6.setWidget(2, 9, new Label());

		tab6.setWidget(3, 0, new MediumLabel(AON.MSG.moreThan33lessThan65()));
		tab6.getCellFormatter().setStyleName(3, 0, AON.AON_CSS.aonTextRight());
		IntegerBox disabilityDescendent33 = new IntegerBox();
		disabilityDescendent33.setMaxLength(2);
		disabilityDescendent33.setVisibleLength(2);
		disabilityDescendent33.setValue(detail.getDisabilityDescendent33());
		disabilityDescendent33.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setDisabilityDescendent33( AonNumberUtils.toByte( disabilityDescendent33.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(3, 1, disabilityDescendent33);
		
		IntegerBox disabilityDescendent33Ratio = new IntegerBox();
		disabilityDescendent33Ratio.setMaxLength(2);
		disabilityDescendent33Ratio.setVisibleLength(2);
		disabilityDescendent33Ratio.setValue(detail.getDisabilityDescendent33Ratio());
		disabilityDescendent33Ratio.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setDisabilityDescendent33Ratio( AonNumberUtils.toByte( disabilityDescendent33Ratio.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(3, 2, disabilityDescendent33Ratio);

		tab6.setWidget(3, 3, new MediumLabel(AON.MSG.reducedMovilitiy()));
		tab6.getCellFormatter().setStyleName(3, 3, AON.AON_CSS.aonTextRight());
		IntegerBox disabilityDescendentDependence = new IntegerBox();
		disabilityDescendentDependence.setMaxLength(2);
		disabilityDescendentDependence.setVisibleLength(2);
		disabilityDescendentDependence.setValue(detail.getDisabilityDescendentDependence());
		disabilityDescendentDependence.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setDisabilityDescendentDependence( AonNumberUtils.toByte( disabilityDescendentDependence.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(3, 4, disabilityDescendentDependence);
		
		IntegerBox disabilityDescendentDependenceRatio = new IntegerBox();
		disabilityDescendentDependenceRatio.setMaxLength(2);
		disabilityDescendentDependenceRatio.setVisibleLength(2);
		disabilityDescendentDependenceRatio.setValue(detail.getDisabilityDescendentDependenceRatio());
		disabilityDescendentDependenceRatio.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setDisabilityDescendentDependenceRatio( AonNumberUtils.toByte( disabilityDescendentDependenceRatio.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(3, 5, disabilityDescendentDependenceRatio);
		
		tab6.setWidget(3, 6, new MediumLabel(AON.MSG.moreThan65()));
		tab6.getCellFormatter().setStyleName(3, 6, AON.AON_CSS.aonTextRight());
		IntegerBox disabilityDescendent65 = new IntegerBox();
		disabilityDescendent65.setMaxLength(2);
		disabilityDescendent65.setVisibleLength(2);
		disabilityDescendent65.setValue(detail.getDisabilityDescendent65());
		disabilityDescendent65.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setDisabilityDescendent65( AonNumberUtils.toByte( disabilityDescendent65.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(3, 7, disabilityDescendent65);
		
		IntegerBox disabilityDescendent65Ratio = new IntegerBox();
		disabilityDescendent65Ratio.setMaxLength(2);
		disabilityDescendent65Ratio.setVisibleLength(2);
		disabilityDescendent65Ratio.setValue(detail.getDisabilityDescendent65Ratio());
		disabilityDescendent65Ratio.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setDisabilityDescendent65Ratio( AonNumberUtils.toByte( disabilityDescendent65Ratio.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(3, 8, disabilityDescendent65Ratio);
		tab6.setWidget(3, 9, new Label());

		MediumLabel ascendantLabel = new MediumLabel( AonStringUtils.abbreviate(AON.MSG.ascendant(),34) );
		ascendantLabel.addStyleName(AON.AON_CSS.aonBold());
		tab6.setWidget(4, 0, ascendantLabel );
		tab6.setWidget(4, 1, new MediumLabel(AON.MSG.total()));
		tab6.setWidget(4, 2, new MediumLabel(AON.MSG.byInteger()));
		tab6.setWidget(4, 3, new Label());
		tab6.setWidget(4, 4, new MediumLabel(AON.MSG.total()));
		tab6.setWidget(4, 5, new MediumLabel(AON.MSG.byInteger()));
		tab6.setWidget(4, 6, new Label());
		tab6.setWidget(4, 7, new Label());
		tab6.setWidget(4, 8, new Label());
		tab6.setWidget(4, 9, new Label());

		tab6.setWidget(5, 0, new MediumLabel(AON.MSG.lessThan75()));
		tab6.getCellFormatter().setStyleName(5, 0, AON.AON_CSS.aonTextRight());
		IntegerBox lessThan75Ascendant = new IntegerBox();
		lessThan75Ascendant.setMaxLength(1);
		lessThan75Ascendant.setVisibleLength(1);
		lessThan75Ascendant.setValue(detail.getLessThan75Ascendant());
		lessThan75Ascendant.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setLessThan75Ascendant( AonNumberUtils.toByte( lessThan75Ascendant.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(5, 1, lessThan75Ascendant);
		
		IntegerBox lessThan75AscendantRatio = new IntegerBox();
		lessThan75AscendantRatio.setMaxLength(1);
		lessThan75AscendantRatio.setVisibleLength(1);
		lessThan75AscendantRatio.setValue(detail.getLessThan75AscendantRatio());
		lessThan75AscendantRatio.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setLessThan75AscendantRatio( AonNumberUtils.toByte( lessThan75AscendantRatio.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(5, 2, lessThan75AscendantRatio);

		tab6.setWidget(5, 3, new MediumLabel(AON.MSG.greatherThan75()));
		tab6.getCellFormatter().setStyleName(5, 3, AON.AON_CSS.aonTextRight());
		IntegerBox ascendant = new IntegerBox();
		ascendant.setMaxLength(1);
		ascendant.setVisibleLength(1);
		ascendant.setValue(detail.getAscendant());
		ascendant.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setAscendant( AonNumberUtils.toByte( ascendant.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(5, 4, ascendant);
		
		IntegerBox ascendantRatio = new IntegerBox();
		ascendantRatio.setMaxLength(1);
		ascendantRatio.setVisibleLength(1);
		ascendantRatio.setValue(detail.getAscendantRatio());
		ascendantRatio.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setAscendantRatio( AonNumberUtils.toByte( ascendantRatio.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(5, 5, ascendantRatio);
		
		tab6.setWidget(5, 6, new Label());
		tab6.setWidget(5, 7, new Label());
		tab6.setWidget(5, 8, new Label());
		
		MediumLabel disabilityAscendant = new MediumLabel( AonStringUtils.abbreviate(AON.MSG.disabilityAscendant(),34) );
		disabilityAscendant.addStyleName(AON.AON_CSS.aonBold());
		tab6.setWidget(6, 0, disabilityAscendant );
		tab6.setWidget(6, 1, new MediumLabel(AON.MSG.total()));
		tab6.setWidget(6, 2, new MediumLabel(AON.MSG.byInteger()));
		tab6.setWidget(6, 3, new Label());
		tab6.setWidget(6, 4, new MediumLabel(AON.MSG.total()));
		tab6.setWidget(6, 5, new MediumLabel(AON.MSG.byInteger()));
		tab6.setWidget(6, 6, new Label());
		tab6.setWidget(6, 7, new MediumLabel(AON.MSG.total()));
		tab6.setWidget(6, 8, new MediumLabel(AON.MSG.byInteger()));
		tab6.setWidget(6, 9, new Label());
		additionalDataPanel.add(tab6);

		tab6.setWidget(7, 0, new MediumLabel(AON.MSG.moreThan33lessThan65()));
		tab6.getCellFormatter().setStyleName(7, 0, AON.AON_CSS.aonTextRight());
		IntegerBox disabilityAscendant33 = new IntegerBox();
		disabilityAscendant33.setMaxLength(1);
		disabilityAscendant33.setVisibleLength(1);
		disabilityAscendant33.setValue(detail.getDisabilityAscendant33());
		disabilityAscendant33.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setDisabilityAscendant33( AonNumberUtils.toByte( disabilityAscendant33.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(7, 1, disabilityAscendant33);
		
		IntegerBox disabilityAscendant33Ratio = new IntegerBox();
		disabilityAscendant33Ratio.setMaxLength(1);
		disabilityAscendant33Ratio.setVisibleLength(1);
		disabilityAscendant33Ratio.setValue(detail.getDisabilityAscendant33Ratio());
		disabilityAscendant33Ratio.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setDisabilityAscendant33Ratio( AonNumberUtils.toByte( disabilityAscendant33Ratio.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(7, 2, disabilityAscendant33Ratio);

		tab6.setWidget(7, 3, new MediumLabel(AON.MSG.reducedMovilitiy()));
		tab6.getCellFormatter().setStyleName(7, 3, AON.AON_CSS.aonTextRight());
		IntegerBox disabilityAscendantDependence = new IntegerBox();
		disabilityAscendantDependence.setMaxLength(1);
		disabilityAscendantDependence.setVisibleLength(1);
		disabilityAscendantDependence.setValue(detail.getDisabilityAscendantDependence());
		disabilityAscendantDependence.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setDisabilityAscendantDependence( AonNumberUtils.toByte( disabilityAscendantDependence.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(7, 4, disabilityAscendantDependence);
		
		IntegerBox disabilityAscendantDependenceRatio = new IntegerBox();
		disabilityAscendantDependenceRatio.setMaxLength(1);
		disabilityAscendantDependenceRatio.setVisibleLength(1);
		disabilityAscendantDependenceRatio.setValue(detail.getDisabilityAscendantDependenceRatio());
		disabilityAscendantDependenceRatio.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setDisabilityAscendantDependenceRatio( AonNumberUtils.toByte( disabilityAscendantDependenceRatio.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(7, 5, disabilityAscendantDependenceRatio);

		tab6.setWidget(7, 6, new MediumLabel(AON.MSG.moreThan65()));
		tab6.getCellFormatter().setStyleName(7, 6, AON.AON_CSS.aonTextRight());
		IntegerBox disabilityAscendant65 = new IntegerBox();
		disabilityAscendant65.setMaxLength(1);
		disabilityAscendant65.setVisibleLength(1);
		disabilityAscendant65.setValue(detail.getDisabilityAscendant65());
		disabilityAscendant65.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setDisabilityAscendant65( AonNumberUtils.toByte( disabilityAscendant65.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(7, 7, disabilityAscendant65);
		
		IntegerBox disabilityAscendant65Ratio = new IntegerBox();
		disabilityAscendant65Ratio.setMaxLength(1);
		disabilityAscendant65Ratio.setVisibleLength(1);
		disabilityAscendant65Ratio.setValue(detail.getDisabilityAscendant65Ratio());
		disabilityAscendant65Ratio.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setDisabilityAscendant65Ratio( AonNumberUtils.toByte( disabilityAscendant65Ratio.getValue()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(7, 8, disabilityAscendant65Ratio);
		tab6.setWidget(7, 9, new Label());
		
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
