package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerTextBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.fiscal.IrpfResult;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model190Detail2014 extends ResizeComposite {

	interface Model190Detail2013Binder extends
			UiBinder<Widget, Model190Detail2014> {
	}

	private static Model190Detail2013Binder MODEL190_DETAIL_2013_BINDER = GWT
			.create(Model190Detail2013Binder.class);

	static interface ICallBack {
		void redrawList(Mod190Detail detail);
	}

	public class KeyListBox extends ListBox {

		public KeyListBox() {
			subkey = new ListBox();
			subkey.setWidth("45px");

			setWidth("40px");
			for (Mod190Key key : Mod190Key.values()) {
				this.addItem(key.getValue());
			}

			addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					subkey.clear();
					Mod190Key keyEnum = Mod190Key.values()[getSelectedIndex()];
					if (keyEnum.hasSubkeys()) {
						subkey.setEnabled(true);
						for (int i = 0; i < keyEnum.getSubKeys().length; i++) {
							subkey.addItem(keyEnum.getSubKeys()[i]);
						}
					} else {
						subkey.setEnabled(false);
					}
				}
			});
		}

		public boolean hasSubkeys() {
			Mod190Key keyEnum = Mod190Key.values()[getSelectedIndex()];
			return keyEnum.hasSubkeys();
		}

		public ListBox getSubkey() {
			return subkey;
		}

		public void setValue(String key, String subKey) {
			Mod190Key keyEnum = Mod190Key.valueOf(key);
			setSelectedIndex(keyEnum.ordinal());
			getSubkey().clear();
			if (hasSubkeys()) {
				getSubkey().setEnabled(true);
				for (int i = 0; i < keyEnum.getSubKeys().length; i++) {
					getSubkey().addItem(keyEnum.getSubKeys()[i]);
					if (keyEnum.getSubKeys()[i].equals(subKey)) {
						getSubkey().setSelectedIndex(i);
					}
				}
				getSubkey().setEnabled(true);
			} else {
				getSubkey().setEnabled(false);
			}
		}
	}

	Mod190Detail detail;
	private ICallBack callback;

	@UiField
	Panel additionalDataPanel;

	@UiField
	Button deleteDetailButton;
	@UiField
	Button restoreDeletedButton;

	@UiField
	DocumentTextBox receiverDocument;
	@UiField
	DocumentTextBox representativeDocument;
	@UiField
	TextBox fullName;
	@UiField
	IntegerTextBox accrualYear;
	@UiField
	ProvinceListBox province;
	@UiField
	CheckBox ceutaMelilla;
	@UiField(provided = true)
	KeyListBox key;
	@UiField(provided = true)
	ListBox subkey;
	@UiField
	DoubleTextBox perception;
	@UiField
	DoubleTextBox retention;
	@UiField
	DoubleTextBox inKindPerception;
	@UiField
	DoubleTextBox inKindDeposit;
	@UiField
	DoubleTextBox inKindOutputDeposit;
	@UiField
	IntegerTextBox birthYear;
	@UiField
	ListBox familySituation;
	@UiField
	DocumentTextBox spouseDocument;
	@UiField
	ListBox disability;
	@UiField
	ListBox contract;
	@UiField
	CheckBox workActivityExtension;
	@UiField
	CheckBox geographicMobility;
	@UiField
	CheckBox homeLoanCommunnication;
	@UiField
	DoubleTextBox applicableReduction;
	@UiField
	DoubleTextBox deducibleExpense;
	@UiField
	DoubleTextBox compensatoryPension;
	@UiField
	DoubleTextBox foodAnnuality;

	@UiField
	IntegerTextBox lessThan3Descendent;
	@UiField
	IntegerTextBox lessThan3DescendentRatio;
	@UiField
	IntegerTextBox otherDescendent;
	@UiField
	IntegerTextBox otherDescendentRatio;
	@UiField
	ListBox firstChildCalculation;
	@UiField
	ListBox secondChildCalculation;
	@UiField
	ListBox thirdChildCalculation;

	@UiField
	IntegerTextBox disabilityDescendent33;
	@UiField
	IntegerTextBox disabilityDescendent33Ratio;
	@UiField
	IntegerTextBox disabilityDescendentDependence;
	@UiField
	IntegerTextBox disabilityDescendentDependenceRatio;
	@UiField
	IntegerTextBox disabilityDescendent65;
	@UiField
	IntegerTextBox disabilityDescendent65Ratio;
	@UiField
	IntegerTextBox lessThan75Ascendant;
	@UiField
	IntegerTextBox lessThan75AscendantRatio;
	@UiField
	IntegerTextBox ascendant;
	@UiField
	IntegerTextBox ascendantRatio;
	@UiField
	IntegerTextBox disabilityAscendant33;
	@UiField
	IntegerTextBox disabilityAscendant33Ratio;
	@UiField
	IntegerTextBox disabilityAscendantDependence;
	@UiField
	IntegerTextBox disabilityAscendantDependenceRatio;
	@UiField
	IntegerTextBox disabilityAscendant65;
	@UiField
	IntegerTextBox disabilityAscendant65Ratio;

	public Model190Detail2014() {
		key = new KeyListBox();
		
		Widget ui = MODEL190_DETAIL_2013_BINDER.createAndBindUi(this);
		initWidget(ui);


		familySituation.addItem("-");
		familySituation.addItem("1");
		familySituation.addItem("2");
		familySituation.addItem("3");

		disability.addItem("0");
		disability.addItem("1");
		disability.addItem("2");
		disability.addItem("3");

		contract.addItem("-");
		contract.addItem("1");
		contract.addItem("2");
		contract.addItem("3");
		contract.addItem("4");

		firstChildCalculation.addItem("-");
		firstChildCalculation.addItem("1");
		firstChildCalculation.addItem("2");

		secondChildCalculation.addItem("-");
		secondChildCalculation.addItem("1");
		secondChildCalculation.addItem("2");

		thirdChildCalculation.addItem("-");
		thirdChildCalculation.addItem("1");
		thirdChildCalculation.addItem("2");
	}

	public void setCallback(ICallBack callback) {
		this.callback = callback;
	}

	public void setDetail(Mod190Detail detail) {
		this.detail = detail;

		receiverDocument.setValue(detail.getDocument());
		representativeDocument.setValue(detail.getRepresentativeDocument());
		fullName.setValue(detail.getName());
		accrualYear.setValue(detail.getAccrualYear());
		province.setSelectedIndex(detail.getProvince());
		key.setValue(detail.getKey(), detail.getSubKey());
		perception.setValue(detail.getPerception());
		retention.setValue(detail.getRetention());
		inKindPerception.setValue(detail.getInKindPerception());
		inKindDeposit.setValue(detail.getInKindDeposit());
		inKindOutputDeposit.setValue(detail.getInKindOutputDeposit());

		IrpfData irpfData = detail.getIrpfData();
		if (irpfData != null) {
			ceutaMelilla.setValue(irpfData.isCeutaMelilla());
			birthYear.setValue(irpfData.getBirthYear());
			familySituation.setSelectedIndex(irpfData.getFamilySituation());
			spouseDocument.setValue(irpfData.getSpouseDocument());
			disability.setSelectedIndex(irpfData.getDisability());
			contract.setSelectedIndex(irpfData.getContract());
			workActivityExtension.setValue(irpfData.isWorkActivityExtension());
			geographicMobility.setValue(irpfData.isGeographicMobility());
		}
		IrpfResult irpfResult = detail.getIrpfResult();
		if (irpfResult != null) {
			applicableReduction.setValue(irpfResult.getApplicableReduction());
			deducibleExpense.setValue(irpfResult.getDeducibleExpense());
			compensatoryPension.setValue(irpfResult.getCompensatoryPension());
			foodAnnuality.setValue(irpfResult.getFoodAnnuality());
			homeLoanCommunnication.setValue(irpfResult
					.isHomeLoanCommunnication());
			lessThan3Descendent.setValue(irpfResult.getLessThan3Descendent());
			lessThan3DescendentRatio.setValue(irpfResult
					.getLessThan3DescendentRatio());
			otherDescendent.setValue(irpfResult.getOtherDescendent());
			otherDescendentRatio.setValue(irpfResult.getOtherDescendentRatio());
			firstChildCalculation.setSelectedIndex(irpfResult
					.getFirstChildCalculation());
			secondChildCalculation.setSelectedIndex(irpfResult
					.getSecondChildCalculation());
			thirdChildCalculation.setSelectedIndex(irpfResult
					.getThirdChildCalculation());
			disabilityDescendent33.setValue(irpfResult
					.getDisabilityDescendent33());
			disabilityDescendent33Ratio.setValue(irpfResult
					.getDisabilityDescendent33Ratio());
			disabilityDescendentDependence.setValue(irpfResult
					.getDisabilityDescendentDependence());
			disabilityDescendentDependenceRatio.setValue(irpfResult
					.getDisabilityDescendentDependenceRatio());
			disabilityDescendent65.setValue(irpfResult
					.getDisabilityDescendent65());
			disabilityDescendent65Ratio.setValue(irpfResult
					.getDisabilityDescendent65Ratio());
			lessThan75Ascendant.setValue(irpfResult.getLessThan75Ascendant());
			lessThan75AscendantRatio.setValue(irpfResult
					.getLessThan75AscendantRatio());
			ascendant.setValue(irpfResult.getAscendant());
			ascendantRatio.setValue(irpfResult.getAscendantRatio());
			disabilityAscendant33.setValue(irpfResult
					.getDisabilityAscendant33());
			disabilityAscendant33Ratio.setValue(irpfResult
					.getDisabilityAscendant33Ratio());
			disabilityAscendantDependence.setValue(irpfResult
					.getDisabilityAscendantDependence());
			disabilityAscendantDependenceRatio.setValue(irpfResult
					.getDisabilityAscendantDependenceRatio());
			disabilityAscendant65.setValue(irpfResult
					.getDisabilityAscendant65());
			disabilityAscendant65Ratio.setValue(irpfResult
					.getDisabilityAscendant65Ratio());
		}
		restoreDeletedButton.setVisible(detail.isDeleted());
		deleteDetailButton.setVisible(!detail.isDeleted());
		enableOrDisableAdditionalDataPanel();
	}

	private void enableOrDisableAdditionalDataPanel() {
		Mod190Key keyEnum = Mod190Key.values()[key.getSelectedIndex()];
		String subk = ((key.getSubkey().getSelectedIndex() == -1) ? null : key
				.getSubkey().getValue(key.getSubkey().getSelectedIndex()));
		if (Mod190Key.A == keyEnum || Mod190Key.C == keyEnum
				|| Mod190Key.D == keyEnum
				|| (Mod190Key.B == keyEnum && "01".equals(subk))
				|| (Mod190Key.B == keyEnum && "02".equals(subk))) {
			additionalDataPanel.setVisible(true);
		} else {
			additionalDataPanel.setVisible(false);
		}
	}

	@UiHandler("deleteDetailButton")
	void onDeleteDetailButtonClick(ClickEvent event) {
		detail.setDeleted(true);
		restoreDeletedButton.setVisible(true);
		deleteDetailButton.setVisible(false);
		callback.redrawList(detail);
	}

	@UiHandler("restoreDeletedButton")
	void onRestoreDeletedButtonClick(ClickEvent event) {
		detail.setDeleted(false);
		if (!detail.isDirty()) {
			restoreDeletedButton.setVisible(false);
			deleteDetailButton.setVisible(true);
			callback.redrawList(detail);
		}
	};

	@UiHandler("receiverDocument")
	void onChangeReceiverDocument(ChangeEvent event) {
		detail.setDocument(receiverDocument.getValue());
		detail.setDirty(true);
	}

	@UiHandler("representativeDocument")
	void onChangeRepresentativeDocument(ChangeEvent event) {
		detail.setRepresentativeDocument(representativeDocument.getValue());
		detail.setDirty(true);
	}

	@UiHandler("fullName")
	void onChangeFullName(ChangeEvent event) {
		detail.setName(fullName.getValue());
		detail.setDirty(true);
	}

	@UiHandler("accrualYear")
	void onChangeAccrualYear(ChangeEvent event) {
		try {
			detail.setAccrualYear(accrualYear.getIntValue());
			detail.setDirty(true);
		} catch (NumberFormatException e) {
			accrualYear.addStyleName(Model190.AON_RESOURCES.css()
					.aonTextBoxError());
		}
	}

	@UiHandler("province")
	void onChangeProvince(ChangeEvent event) {
		detail.setProvince(province.getSelectedIndex());
		detail.setDirty(true);
	}

	@UiHandler("ceutaMelilla")
	void onChangeCeutaMelilla(ClickEvent event) {
		detail.getIrpfData().setCeutaMelilla(ceutaMelilla.getValue());
		detail.setDirty(true);
	}

	@UiHandler("key")
	void onChangeKey(ChangeEvent event) {
		detail.setKey(Mod190Key.values()[key.getSelectedIndex()].getValue());
		detail.setDirty(true);
		enableOrDisableAdditionalDataPanel();
	}

	@UiHandler("subkey")
	void onChangeSubkey(ChangeEvent event) {
		String subk = ((key.getSubkey().getSelectedIndex() == -1) ? null : key
				.getSubkey().getValue(key.getSubkey().getSelectedIndex()));
		detail.setSubKey(subk);
		detail.setDirty(true);
		enableOrDisableAdditionalDataPanel();
	}

	@UiHandler("perception")
	void onChangePerception(ChangeEvent event) {
		detail.setPerception(perception.getDoubleValue());
		detail.setDirty(true);
	}

	@UiHandler("retention")
	void onChangeRetention(ChangeEvent event) {
		detail.setRetention(retention.getDoubleValue());
		detail.setDirty(true);
	}

	@UiHandler("inKindPerception")
	void onChangeValuation(ChangeEvent event) {
		detail.setInKindPerception(inKindPerception.getDoubleValue());
		detail.setDirty(true);
	}

	@UiHandler("inKindDeposit")
	void onChangeInKindDeposit(ChangeEvent event) {
		detail.setInKindDeposit(inKindDeposit.getDoubleValue());
		detail.setDirty(true);
	}

	@UiHandler("inKindOutputDeposit")
	void onChangeInKindOutputDeposit(ChangeEvent event) {
		detail.setInKindOutputDeposit(inKindOutputDeposit.getDoubleValue());
		detail.setDirty(true);
	}

	@UiHandler("birthYear")
	void onChangeBirthYear(ChangeEvent event) {
		detail.getIrpfData().setBirthYear(birthYear.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("familySituation")
	void onChangeFamilySituation(ChangeEvent event) {
		detail.getIrpfData().setFamilySituation(
				(byte) familySituation.getSelectedIndex());
		detail.setDirty(true);
	}

	@UiHandler("spouseDocument")
	void onChangeSpouseDocument(ChangeEvent event) {
		detail.getIrpfData().setSpouseDocument(spouseDocument.getValue());
		detail.setDirty(true);
	}

	@UiHandler("disability")
	void onChangeDisability(ChangeEvent event) {
		detail.getIrpfData()
				.setDisability((byte) disability.getSelectedIndex());
		detail.setDirty(true);
	}

	@UiHandler("contract")
	void onChangeContract(ChangeEvent event) {
		detail.getIrpfData().setContract((byte) contract.getSelectedIndex());
		detail.setDirty(true);
	}

	@UiHandler("workActivityExtension")
	void onChangeWorkActivityExtension(ClickEvent event) {
		detail.getIrpfData().setWorkActivityExtension(
				workActivityExtension.getValue());
		detail.setDirty(true);
	}

	@UiHandler("geographicMobility")
	void onChangeGeographicMobility(ClickEvent event) {
		detail.getIrpfData().setGeographicMobility(
				geographicMobility.getValue());
		detail.setDirty(true);
	}

	@UiHandler("homeLoanCommunnication")
	void onChangeHomeLoanCommunnication(ClickEvent event) {
		detail.getIrpfResult().setHomeLoanCommunnication(
				homeLoanCommunnication.getValue());
		detail.setDirty(true);
	}

	@UiHandler("applicableReduction")
	void onChangeApplicableReduction(ChangeEvent event) {
		detail.getIrpfResult().setApplicableReduction(
				applicableReduction.getDoubleValue());
		detail.setDirty(true);
	}

	@UiHandler("deducibleExpense")
	void onChangeDeducibleExpense(ChangeEvent event) {
		detail.getIrpfResult().setDeducibleExpense(
				deducibleExpense.getDoubleValue());
		detail.setDirty(true);
	}

	@UiHandler("compensatoryPension")
	void onChangeCompensatoryPension(ChangeEvent event) {
		detail.getIrpfResult().setCompensatoryPension(
				compensatoryPension.getDoubleValue());
		detail.setDirty(true);
	}

	@UiHandler("foodAnnuality")
	void onChangeFoodAnnuality(ChangeEvent event) {
		detail.getIrpfResult().setFoodAnnuality(foodAnnuality.getDoubleValue());
		detail.setDirty(true);
	}

	@UiHandler("lessThan3Descendent")
	void onChangeLessThan3Descendent(ChangeEvent event) {
		detail.getIrpfResult().setLessThan3Descendent(
				(byte) lessThan3Descendent.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("lessThan3DescendentRatio")
	void onChangeLessThan3DescendentRatio(ChangeEvent event) {
		detail.getIrpfResult().setLessThan3DescendentRatio(
				(byte) lessThan3DescendentRatio.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("otherDescendent")
	void onChangeOtherDescendent(ChangeEvent event) {
		detail.getIrpfResult().setOtherDescendent(
				(byte) otherDescendent.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("otherDescendentRatio")
	void onChangeOtherDescendentRatio(ChangeEvent event) {
		detail.getIrpfResult().setOtherDescendentRatio(
				(byte) otherDescendentRatio.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("firstChildCalculation")
	void onChangeFirstChildCalculation(ChangeEvent event) {
		detail.getIrpfResult().setFirstChildCalculation(
				(byte) firstChildCalculation.getSelectedIndex());
		detail.setDirty(true);
	}

	@UiHandler("secondChildCalculation")
	void onChangeSecondChildCalculation(ChangeEvent event) {
		detail.getIrpfResult().setSecondChildCalculation(
				(byte) secondChildCalculation.getSelectedIndex());
		detail.setDirty(true);
	}

	@UiHandler("thirdChildCalculation")
	void onChangeThirdChildCalculation(ChangeEvent event) {
		detail.getIrpfResult().setThirdChildCalculation(
				(byte) thirdChildCalculation.getSelectedIndex());
		detail.setDirty(true);
	}

	@UiHandler("disabilityDescendent33")
	void onChangeDisabilityDescendent33(ChangeEvent event) {
		detail.getIrpfResult().setDisabilityDescendent33(
				(byte) disabilityDescendent33.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("disabilityDescendent33Ratio")
	void onChangeDisabilityDescendent33Ratio(ChangeEvent event) {
		detail.getIrpfResult().setDisabilityDescendent33Ratio(
				(byte) disabilityDescendent33Ratio.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("disabilityDescendentDependence")
	void onChangeDisabilityDescendentDependence(ChangeEvent event) {
		detail.getIrpfResult().setDisabilityDescendentDependence(
				(byte) disabilityDescendentDependence.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("disabilityDescendentDependenceRatio")
	void onChangeDisabilityDescendentDependenceRatio(ChangeEvent event) {
		detail.getIrpfResult().setDisabilityDescendentDependenceRatio(
				(byte) disabilityDescendentDependenceRatio.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("disabilityDescendent65")
	void onChangeDisabilityDescendent65(ChangeEvent event) {
		detail.getIrpfResult().setDisabilityDescendent65(
				(byte) disabilityDescendent65.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("disabilityDescendent65Ratio")
	void onChangeDisabilityDescendent65Ratio(ChangeEvent event) {
		detail.getIrpfResult().setDisabilityDescendent65Ratio(
				(byte) disabilityDescendent65Ratio.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("lessThan75Ascendant")
	void onChangeLessThan75Ascendant(ChangeEvent event) {
		detail.getIrpfResult().setLessThan75Ascendant(
				(byte) lessThan75Ascendant.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("lessThan75AscendantRatio")
	void onChangeLessThan75AscendantRatio(ChangeEvent event) {
		detail.getIrpfResult().setLessThan75AscendantRatio(
				(byte) lessThan75AscendantRatio.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("ascendant")
	void onChangeAscendant(ChangeEvent event) {
		detail.getIrpfResult().setAscendant((byte) ascendant.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("ascendantRatio")
	void onChangeAscendantRatio(ChangeEvent event) {
		detail.getIrpfResult().setAscendantRatio((byte) ascendantRatio.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("disabilityAscendant33")
	void onChangeDisabilityAscendant33(ChangeEvent event) {
		detail.getIrpfResult().setDisabilityAscendant33(
				(byte) disabilityAscendant33.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("disabilityAscendant33Ratio")
	void onChangeDisabilityAscendant33Ratio(ChangeEvent event) {
		detail.getIrpfResult().setDisabilityAscendant33Ratio(
				(byte) disabilityAscendant33Ratio.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("disabilityAscendantDependence")
	void onChangeDisabilityAscendantDependence(ChangeEvent event) {
		detail.getIrpfResult().setDisabilityAscendantDependence(
				(byte) disabilityAscendantDependence.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("disabilityAscendantDependenceRatio")
	void onChangeDisabilityAscendantDependenceRatio(ChangeEvent event) {
		detail.getIrpfResult().setDisabilityAscendantDependenceRatio(
				(byte) disabilityAscendantDependenceRatio.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("disabilityAscendant65")
	void onChangeDisabilityAscendant65(ChangeEvent event) {
		detail.getIrpfResult().setDisabilityAscendant65(
				(byte) disabilityAscendant65.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("disabilityAscendant65Ratio")
	void onChangeDisabilityAscendant65Ratio(ChangeEvent event) {
		detail.getIrpfResult().setDisabilityAscendant65Ratio(
				(byte) disabilityAscendant65Ratio.getIntValue());
		detail.setDirty(true);
	}

}
