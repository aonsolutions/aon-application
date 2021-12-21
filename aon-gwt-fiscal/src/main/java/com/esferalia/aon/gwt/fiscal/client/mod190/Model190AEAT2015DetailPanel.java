package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190AEATDetail2015.IModel190DetailCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Mod1902015Key;
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

public class Model190AEAT2015DetailPanel extends SimpleLayoutPanel implements Focusable {
	
	private int tabIndex; 
	private DocumentTextBox document;
	
	public Model190AEAT2015DetailPanel(Mod190Detail detail, IModel190DetailCallback callback) {
		FlowPanel additionalDataPanel = new FlowPanel();
		
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
		
		AonTextBox name = new AonTextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
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
		
		tab2.setStyleName(AON.CSS.aonWidthAll());
		tab2.addStyleName(AON.CSS.aonNowrap());
		
		
		tab2.setWidget(0, 0, new Model190SmallerLabel(AON.MSG.key()));
		tab2.getFlexCellFormatter().setRowSpan(0, 0, 4);
		
		final ListBox subkey = new ListBox();
		subkey.setWidth("45px");

		final ListBox key = new ListBox();
		key.setWidth("40px");
		for (Mod1902015Key k : Mod1902015Key.values()) {
			key.addItem(k.getValue());
		}
		
		Model190AEAT2015DetailPanel.setValue(key, subkey, detail);
		
		key.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				subkey.clear();
				Mod1902015Key keyEnum = Mod1902015Key.values()[key.getSelectedIndex()];
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
				Model190AEAT2015DetailPanel.enableOrDisableAdditionalDataPanel(key, subkey, additionalDataPanel);
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(0, 1, key);
		tab2.getFlexCellFormatter().setRowSpan(0, 1, 4);
		
		subkey.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Mod1902015Key keyEnum = Mod1902015Key.values()[key.getSelectedIndex()];
				if (keyEnum.hasSubkeys()) {
					int idx = subkey.getSelectedIndex() == -1 ? 0 : subkey.getSelectedIndex();
					detail.setSubKey(keyEnum.getSubKeys()[idx]);
				} else {
					subkey.setEnabled(false);
					detail.setSubKey(null);
				}
				Model190AEAT2015DetailPanel.enableOrDisableAdditionalDataPanel(key, subkey, additionalDataPanel);
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(0, 2, new Model190SmallerLabel(AON.MSG.subkey()));
		tab2.getFlexCellFormatter().setRowSpan(0, 2, 4);
		
		tab2.setWidget(0, 3, subkey);
		tab2.getFlexCellFormatter().setRowSpan(0, 3, 4);

		tab2.setWidget(0, 4, new Model190SmallerLabel(AON.MSG.money()));
		tab2.getFlexCellFormatter().setRowSpan(0, 4, 2);
		
		tab2.setWidget(0, 5, new Model190SmallerLabel(AON.MSG.perception()));
		tab2.setWidget(0, 6, new Model190SmallerLabel(AON.MSG.retention()));
		tab2.setWidget(0, 7, new Label());
		tab2.setWidget(0, 8, new Model190SmallerLabel(AON.MSG.accrualYear()));
		
		
		AonDoubleBox perception = new AonDoubleBox();
		perception.setValue(detail.getPerception());
		perception.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setPerception(perception.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(1, 0, perception);
		
		AonDoubleBox retention = new AonDoubleBox();
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
		
		AonIntegerBox accrualYear = new AonIntegerBox();
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
		
		tab2.setWidget(2, 0, new Model190SmallerLabel(AON.MSG.inKind()));
		tab2.getFlexCellFormatter().setRowSpan(2, 0, 2);
		
		tab2.setWidget(2, 1, new Model190SmallerLabel(AON.MSG.inKindPerception()));
		tab2.setWidget(2, 2, new Model190SmallerLabel(AON.MSG.inKindDeposit()));
		tab2.setWidget(2, 3, new Model190SmallerLabel(AON.MSG.inKindOutputDeposit()));
		tab2.setWidget(2, 4, new Label());
		
		AonDoubleBox inKindPerception = new AonDoubleBox();
		inKindPerception.setValue(detail.getInKindPerception());
		inKindPerception.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setInKindPerception(inKindPerception.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(3, 0, inKindPerception);
		
		AonDoubleBox inKindDeposit = new AonDoubleBox();
		inKindDeposit.setValue(detail.getInKindDeposit());
		inKindDeposit.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setInKindDeposit(inKindDeposit.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(3, 1, inKindDeposit);
		
		AonDoubleBox inKindOutputDeposit = new AonDoubleBox();
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
		ceutaMelilla.setValue(detail.isCeutaMelilla());
		ceutaMelilla.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				detail.setCeutaMelilla(ceutaMelilla.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(3, 3, ceutaMelilla);
		
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
		tab4.setStyleName(AON.CSS.aonWidthAll());
		tab4.addStyleName(AON.CSS.aonNowrap());
		
		tab4.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab4.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab4.getFlexCellFormatter().setColSpan(0, 0, 10);
		tab4.setWidget(0, 0, new InlineLabel(AON.MSG.additionalData()));
		
		tab4.setWidget(1, 0, new Model190SmallerLabel(AON.MSG.birthYear()));
		AonIntegerBox birthYear = new AonIntegerBox();
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
		
		tab4.setWidget(1, 2, new Model190SmallerLabel(AON.MSG.familySituation()));
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
		
		tab4.setWidget(1, 4, new Model190SmallerLabel(AON.MSG.spouseDocument()));
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
		
		tab4.setWidget(1, 6, new Model190SmallerLabel(AON.MSG.disability()));
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
		
		tab4.setWidget(1, 8, new Model190SmallerLabel(AON.MSG.contract()));
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
		tab5.setStyleName(AON.CSS.aonWidthAll());
		tab5.addStyleName(AON.CSS.aonNowrap());
		
		CheckBox workActivityExtension = new CheckBox(AON.MSG.geographicMobility2014());
		workActivityExtension.setValue(detail.isWorkActivityExtension());
		workActivityExtension.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				detail.setWorkActivityExtension(workActivityExtension.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(0, 0, workActivityExtension);
		tab5.getFlexCellFormatter().setColSpan(0, 0, 2);

		CheckBox geographicMobility = new CheckBox(AON.MSG.geographicMobility());
		geographicMobility.setValue(detail.isGeographicMobility());
		geographicMobility.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				detail.setGeographicMobility(geographicMobility.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(0, 1, geographicMobility);
		tab5.getFlexCellFormatter().setColSpan(0, 1, 2);

		CheckBox homeLoanCommunnication = new CheckBox(AON.MSG.homeLoanCommunnication());
		homeLoanCommunnication.setValue(detail.isHomeLoanCommunnication());
		homeLoanCommunnication.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				detail.setHomeLoanCommunnication(homeLoanCommunnication.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(0, 2, homeLoanCommunnication);
		tab5.getFlexCellFormatter().setColSpan(0, 2, 2);

		tab5.setWidget(1, 0, new Label());
		tab5.setWidget(1, 1, new Model190SmallerLabel(AON.MSG.applicableReduction()));
		tab5.setWidget(1, 2, new Model190SmallerLabel(AON.MSG.deducibleExpense()));
		tab5.setWidget(1, 3, new Model190SmallerLabel(AON.MSG.compensatoryPension()));
		tab5.setWidget(1, 4, new Model190SmallerLabel(AON.MSG.foodAnnuality()));
		tab5.setWidget(1, 5, new Label());
		

		tab5.setWidget(2, 0, new Label());

		AonDoubleBox applicableReduction = new AonDoubleBox();
		applicableReduction.setValue(detail.getApplicableReduction());
		applicableReduction.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setApplicableReduction(applicableReduction.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(2, 1, applicableReduction);

		AonDoubleBox deducibleExpense = new AonDoubleBox();
		deducibleExpense.setValue(detail.getDeducibleExpense());
		deducibleExpense.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setDeducibleExpense(deducibleExpense.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(2, 2, deducibleExpense);
		
		AonDoubleBox compensatoryPension = new AonDoubleBox();
		compensatoryPension.setValue(detail.getCompensatoryPension());
		compensatoryPension.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setCompensatoryPension(compensatoryPension.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(2, 3, compensatoryPension);

		AonDoubleBox foodAnnuality = new AonDoubleBox();
		foodAnnuality.setValue(detail.getFoodAnnuality());
		foodAnnuality.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setFoodAnnuality(foodAnnuality.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab5.setWidget(2, 4, foodAnnuality);
		
		tab5.setWidget(2, 5, new Label());
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
		tab6.setStyleName(AON.CSS.aonWidthAll());
		tab6.addStyleName(AON.CSS.aonNowrap());

		Model190SmallerLabel descendant = new Model190SmallerLabel( AonStringUtils.abbreviate(AON.MSG.descendant(),34) );
		descendant.addStyleName(AON.CSS.aonBold());
		tab6.setWidget(0, 0, descendant );
		tab6.setWidget(0, 1, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(0, 2, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(0, 3, new Label());
		tab6.setWidget(0, 4, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(0, 5, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(0, 6, new Label());
		tab6.setWidget(0, 7, new Model190SmallerLabel(AON.MSG.first()));
		tab6.setWidget(0, 8, new Model190SmallerLabel(AON.MSG.second()));
		tab6.setWidget(0, 9, new Model190SmallerLabel(AON.MSG.third()));

		tab6.setWidget(1, 0, new Model190SmallerLabel(AON.MSG.lessThan3()));
		tab6.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTextRight());
		AonIntegerBox lessThan3Descendent = new AonIntegerBox();
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
		
		AonIntegerBox lessThan3DescendentRatio = new AonIntegerBox();
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

		tab6.setWidget(1, 3, new Model190SmallerLabel(AON.MSG.remainder()));
		tab6.getCellFormatter().setStyleName(1, 3, AON.CSS.aonTextRight());
		AonIntegerBox otherDescendent = new AonIntegerBox();
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
		
		AonIntegerBox otherDescendentRatio = new AonIntegerBox();
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

		tab6.setWidget(1, 6, new Model190SmallerLabel(AON.MSG.first3Calculation()));
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

		Model190SmallerLabel disabilityDescendant = new Model190SmallerLabel( AonStringUtils.abbreviate(AON.MSG.disabilityDescendant(),34) );
		disabilityDescendant.addStyleName(AON.CSS.aonBold());
		tab6.setWidget(2, 0, disabilityDescendant );
		tab6.setWidget(2, 1, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(2, 2, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(2, 3, new Label());
		tab6.setWidget(2, 4, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(2, 5, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(2, 6, new Label());
		tab6.setWidget(2, 7, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(2, 8, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(2, 9, new Label());

		tab6.setWidget(3, 0, new Model190SmallerLabel(AON.MSG.moreThan33lessThan65()));
		tab6.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTextRight());
		AonIntegerBox disabilityDescendent33 = new AonIntegerBox();
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
		
		AonIntegerBox disabilityDescendent33Ratio = new AonIntegerBox();
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

		tab6.setWidget(3, 3, new Model190SmallerLabel(AON.MSG.reducedMovilitiy()));
		tab6.getCellFormatter().setStyleName(3, 3, AON.CSS.aonTextRight());
		AonIntegerBox disabilityDescendentDependence = new AonIntegerBox();
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
		
		AonIntegerBox disabilityDescendentDependenceRatio = new AonIntegerBox();
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
		
		tab6.setWidget(3, 6, new Model190SmallerLabel(AON.MSG.moreThan65()));
		tab6.getCellFormatter().setStyleName(3, 6, AON.CSS.aonTextRight());
		AonIntegerBox disabilityDescendent65 = new AonIntegerBox();
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
		
		AonIntegerBox disabilityDescendent65Ratio = new AonIntegerBox();
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

		Model190SmallerLabel ascendantLabel = new Model190SmallerLabel( AonStringUtils.abbreviate(AON.MSG.ascendant(),34) );
		ascendantLabel.addStyleName(AON.CSS.aonBold());
		tab6.setWidget(4, 0, ascendantLabel );
		tab6.setWidget(4, 1, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(4, 2, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(4, 3, new Label());
		tab6.setWidget(4, 4, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(4, 5, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(4, 6, new Label());
		tab6.setWidget(4, 7, new Label());
		tab6.setWidget(4, 8, new Label());
		tab6.setWidget(4, 9, new Label());

		tab6.setWidget(5, 0, new Model190SmallerLabel(AON.MSG.lessThan75()));
		tab6.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTextRight());
		AonIntegerBox lessThan75Ascendant = new AonIntegerBox();
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
		
		AonIntegerBox lessThan75AscendantRatio = new AonIntegerBox();
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

		tab6.setWidget(5, 3, new Model190SmallerLabel(AON.MSG.greatherThan75()));
		tab6.getCellFormatter().setStyleName(5, 3, AON.CSS.aonTextRight());
		AonIntegerBox ascendant = new AonIntegerBox();
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
		
		AonIntegerBox ascendantRatio = new AonIntegerBox();
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
		
		Model190SmallerLabel disabilityAscendant = new Model190SmallerLabel( AonStringUtils.abbreviate(AON.MSG.disabilityAscendant(),34) );
		disabilityAscendant.addStyleName(AON.CSS.aonBold());
		tab6.setWidget(6, 0, disabilityAscendant );
		tab6.setWidget(6, 1, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(6, 2, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(6, 3, new Label());
		tab6.setWidget(6, 4, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(6, 5, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(6, 6, new Label());
		tab6.setWidget(6, 7, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(6, 8, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(6, 9, new Label());
		additionalDataPanel.add(tab6);

		tab6.setWidget(7, 0, new Model190SmallerLabel(AON.MSG.moreThan33lessThan65()));
		tab6.getCellFormatter().setStyleName(7, 0, AON.CSS.aonTextRight());
		AonIntegerBox disabilityAscendant33 = new AonIntegerBox();
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
		
		AonIntegerBox disabilityAscendant33Ratio = new AonIntegerBox();
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

		tab6.setWidget(7, 3, new Model190SmallerLabel(AON.MSG.reducedMovilitiy()));
		tab6.getCellFormatter().setStyleName(7, 3, AON.CSS.aonTextRight());
		AonIntegerBox disabilityAscendantDependence = new AonIntegerBox();
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
		
		AonIntegerBox disabilityAscendantDependenceRatio = new AonIntegerBox();
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

		tab6.setWidget(7, 6, new Model190SmallerLabel(AON.MSG.moreThan65()));
		tab6.getCellFormatter().setStyleName(7, 6, AON.CSS.aonTextRight());
		AonIntegerBox disabilityAscendant65 = new AonIntegerBox();
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
		
		AonIntegerBox disabilityAscendant65Ratio = new AonIntegerBox();
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
			detail.setKey(Mod1902015Key.A.toString());
			detail.setSubKey("01");
		}
		Mod1902015Key keyEnum = Mod1902015Key.valueOf(detail.getKey());
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
		Mod1902015Key keyEnum = Mod1902015Key.values()[key.getSelectedIndex()];
		String subk = ((subKey.getSelectedIndex() == -1) ? null : subKey.getValue(subKey.getSelectedIndex()));
		panel.setVisible( 
			    Mod1902015Key.A == keyEnum
			|| (Mod1902015Key.B == keyEnum && "01".equals(subk))
			|| (Mod1902015Key.B == keyEnum && "02".equals(subk))
			|| (Mod1902015Key.B == keyEnum && "04".equals(subk))
			||  Mod1902015Key.C == keyEnum
			|| (Mod1902015Key.E == keyEnum && "01".equals(subk))
			|| (Mod1902015Key.E == keyEnum && "02".equals(subk))
			);
	}
}
