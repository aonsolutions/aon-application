package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.fiscal.IrpfResult;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Mod1902014Key;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class OLD_Model190Detail2014 extends ResizeComposite {

	public static final ProvidesKey<Mod190Detail> MOD190_DETAIL_PROVIDES_KEY = new ProvidesKey<Mod190Detail>() {
		@Override
		public Object getKey(Mod190Detail det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
	};
	
	interface Model190Detail2013Binder extends
			UiBinder<Widget, OLD_Model190Detail2014> {
	}

	private static Model190Detail2013Binder MODEL190_DETAIL_2013_BINDER = GWT
			.create(Model190Detail2013Binder.class);

	public class KeyListBox extends ListBox {

		public KeyListBox() {
			subkey = new ListBox();
			subkey.setWidth("45px");

			setWidth("40px");
			for (Mod1902014Key key : Mod1902014Key.values()) {
				this.addItem(key.getValue());
			}

			addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					subkey.clear();
					Mod1902014Key keyEnum = Mod1902014Key.values()[getSelectedIndex()];
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
			Mod1902014Key keyEnum = Mod1902014Key.values()[getSelectedIndex()];
			return keyEnum.hasSubkeys();
		}

		public ListBox getSubkey() {
			return subkey;
		}

		public void setValue(String key, String subKey) {
			Mod1902014Key keyEnum = Mod1902014Key.valueOf(key);
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

	private Mod190 currentMod190;
	private Mod190DetailDataProvider dataProvider;
	private SingleSelectionModel<Mod190Detail> detailModel;
	
	@UiField
	Panel additionalDataPanel;

	@UiField
	Button deleteDetailButton;
	@UiField
	Button restoreDeletedButton;
	@UiField
	Button newDetailButton;

	@UiField(provided=true)
	CellList<Mod190Detail> detailList;
	
	@UiField
	DocumentTextBox receiverDocument;
	@UiField
	DocumentTextBox representativeDocument;
	@UiField
	TextBox fullName;
	
	@UiField
	IntegerBox accrualYear;
	@UiField
	ProvinceListBox province;
	@UiField
	CheckBox ceutaMelilla;
	@UiField(provided = true)
	KeyListBox key;
	@UiField(provided = true)
	ListBox subkey;
	@UiField
	DoubleBox perception;
	@UiField
	DoubleBox retention;
	@UiField
	DoubleBox inKindPerception;
	@UiField
	DoubleBox inKindDeposit;
	@UiField
	DoubleBox inKindOutputDeposit;
	@UiField
	IntegerBox birthYear;
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
	DoubleBox applicableReduction;
	@UiField
	DoubleBox deducibleExpense;
	@UiField
	DoubleBox compensatoryPension;
	@UiField
	DoubleBox foodAnnuality;

	@UiField
	IntegerBox lessThan3Descendent;
	@UiField
	IntegerBox lessThan3DescendentRatio;
	@UiField
	IntegerBox otherDescendent;
	@UiField
	IntegerBox otherDescendentRatio;
	@UiField
	ListBox firstChildCalculation;
	@UiField
	ListBox secondChildCalculation;
	@UiField
	ListBox thirdChildCalculation;

	@UiField
	IntegerBox disabilityDescendent33;
	@UiField
	IntegerBox disabilityDescendent33Ratio;
	@UiField
	IntegerBox disabilityDescendentDependence;
	@UiField
	IntegerBox disabilityDescendentDependenceRatio;
	@UiField
	IntegerBox disabilityDescendent65;
	@UiField
	IntegerBox disabilityDescendent65Ratio;
	@UiField
	IntegerBox lessThan75Ascendant;
	@UiField
	IntegerBox lessThan75AscendantRatio;
	@UiField
	IntegerBox ascendant;
	@UiField
	IntegerBox ascendantRatio;
	@UiField
	IntegerBox disabilityAscendant33;
	@UiField
	IntegerBox disabilityAscendant33Ratio;
	@UiField
	IntegerBox disabilityAscendantDependence;
	@UiField
	IntegerBox disabilityAscendantDependenceRatio;
	@UiField
	IntegerBox disabilityAscendant65;
	@UiField
	IntegerBox disabilityAscendant65Ratio;

	public OLD_Model190Detail2014() {
		key = new KeyListBox();
		
		Mod190DetailCell mod190DetailCell = new Mod190DetailCell();
		detailList = new CellList<Mod190Detail>(mod190DetailCell, MOD190_DETAIL_PROVIDES_KEY);
		detailList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		detailList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		detailModel = new SingleSelectionModel<Mod190Detail>(MOD190_DETAIL_PROVIDES_KEY);
		detailModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				selectDetail();
			}
			
		});
		detailList.setSelectionModel(detailModel);
		detailList.setEmptyListWidget(new HTML(AON.MSG.noData()));
		
		dataProvider = new Mod190DetailDataProvider(MOD190_DETAIL_PROVIDES_KEY);
		dataProvider.addDataDisplay(detailList);
		detailList.setVisible(true);

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

	public void setMod190(Mod190 mod190) {
		this.currentMod190 = mod190;
		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),true);
	}
	private Mod190Detail getDetail() {
		return detailModel.getSelectedObject();
	}
	
	public void selectDetail() {
		receiverDocument.setValue(getDetail().getDocument());
		representativeDocument.setValue(getDetail().getRepresentativeDocument());
		fullName.setValue(getDetail().getName());
		accrualYear.setValue(getDetail().getAccrualYear());
		province.setSelectedIndex(getDetail().getProvince());
		key.setValue(getDetail().getKey(), getDetail().getSubKey());
		perception.setValue(getDetail().getPerception());
		retention.setValue(getDetail().getRetention());
		inKindPerception.setValue(getDetail().getInKindPerception());
		inKindDeposit.setValue(getDetail().getInKindDeposit());
		inKindOutputDeposit.setValue(getDetail().getInKindOutputDeposit());
		ceutaMelilla.setValue(getDetail().isCeutaMelilla());
		birthYear.setValue(getDetail().getBirthYear());
		familySituation.setSelectedIndex(getDetail().getFamilySituation());
		spouseDocument.setValue(getDetail().getSpouseDocument());
		disability.setSelectedIndex(getDetail().getDisability());
		contract.setSelectedIndex(getDetail().getContract());
		workActivityExtension.setValue(getDetail().isWorkActivityExtension());
		geographicMobility.setValue(getDetail().isGeographicMobility());
		applicableReduction.setValue(getDetail().getApplicableReduction());
		deducibleExpense.setValue(getDetail().getDeducibleExpense());
		compensatoryPension.setValue(getDetail().getCompensatoryPension());
		foodAnnuality.setValue(getDetail().getFoodAnnuality());
		homeLoanCommunnication.setValue(getDetail().isHomeLoanCommunnication());
		lessThan3Descendent.setValue(getDetail().getLessThan3Descendent());
		lessThan3DescendentRatio.setValue(getDetail().getLessThan3DescendentRatio());
		otherDescendent.setValue(getDetail().getOtherDescendent());
		otherDescendentRatio.setValue(getDetail().getOtherDescendentRatio());
		firstChildCalculation.setSelectedIndex(getDetail().getFirstChildCalculation());
		secondChildCalculation.setSelectedIndex(getDetail().getSecondChildCalculation());
		thirdChildCalculation.setSelectedIndex(getDetail().getThirdChildCalculation());
		disabilityDescendent33.setValue(getDetail().getDisabilityDescendent33());
		disabilityDescendent33Ratio.setValue(getDetail().getDisabilityDescendent33Ratio());
		disabilityDescendentDependence.setValue(getDetail().getDisabilityDescendentDependence());
		disabilityDescendentDependenceRatio.setValue(getDetail().getDisabilityDescendentDependenceRatio());
		disabilityDescendent65.setValue(getDetail().getDisabilityDescendent65());
		disabilityDescendent65Ratio.setValue(getDetail().getDisabilityDescendent65Ratio());
		lessThan75Ascendant.setValue(getDetail().getLessThan75Ascendant());
		lessThan75AscendantRatio.setValue(getDetail().getLessThan75AscendantRatio());
		ascendant.setValue(getDetail().getAscendant());
		ascendantRatio.setValue(getDetail().getAscendantRatio());
		disabilityAscendant33.setValue(getDetail().getDisabilityAscendant33());
		disabilityAscendant33Ratio.setValue(getDetail().getDisabilityAscendant33Ratio());
		disabilityAscendantDependence.setValue(getDetail().getDisabilityAscendantDependence());
		disabilityAscendantDependenceRatio.setValue(getDetail().getDisabilityAscendantDependenceRatio());
		disabilityAscendant65.setValue(getDetail().getDisabilityAscendant65());
		disabilityAscendant65Ratio.setValue(getDetail().getDisabilityAscendant65Ratio());
		restoreDeletedButton.setVisible(getDetail().isDeleted());
		deleteDetailButton.setVisible(!getDetail().isDeleted());
		enableOrDisableAdditionalDataPanel();
	}

	private void enableOrDisableAdditionalDataPanel() {
		Mod1902014Key keyEnum = Mod1902014Key.values()[key.getSelectedIndex()];
		String subk = ((key.getSubkey().getSelectedIndex() == -1) ? null : key
				.getSubkey().getValue(key.getSubkey().getSelectedIndex()));
		additionalDataPanel.setVisible( 
				    Mod1902014Key.A == keyEnum 
				||  Mod1902014Key.C == keyEnum
				||  Mod1902014Key.D == keyEnum
				|| (Mod1902014Key.E == keyEnum && "01".equals(subk))
				|| (Mod1902014Key.B == keyEnum && "01".equals(subk))
				|| (Mod1902014Key.B == keyEnum && "02".equals(subk)))
				;
	}

	@UiHandler("deleteDetailButton")
	void onDeleteDetailButtonClick(ClickEvent event) {
		getDetail().setDeleted(true);
		restoreDeletedButton.setVisible(true);
		deleteDetailButton.setVisible(false);
		detailList.redraw();
	}

	@UiHandler("restoreDeletedButton")
	void onRestoreDeletedButtonClick(ClickEvent event) {
		getDetail().setDeleted(false);
		if (!getDetail().isDirty()) {
			restoreDeletedButton.setVisible(false);
			deleteDetailButton.setVisible(true);
			detailList.redraw();
		}
	};

	@UiHandler("receiverDocument")
	void onChangeReceiverDocument(ChangeEvent event) {
		getDetail().setDocument(receiverDocument.getValue());
		markAsDirty();
	}

	@UiHandler("representativeDocument")
	void onChangeRepresentativeDocument(ChangeEvent event) {
		getDetail().setRepresentativeDocument(representativeDocument.getValue());
		markAsDirty();
	}

	@UiHandler("fullName")
	void onChangeFullName(ChangeEvent event) {
		getDetail().setName(fullName.getValue());
		markAsDirty();
		detailList.redraw();
	}

	@UiHandler("accrualYear")
	void onChangeAccrualYear(ChangeEvent event) {
		try {
			getDetail().setAccrualYear(accrualYear.getValue());
			markAsDirty();
		} catch (NumberFormatException e) {
			accrualYear.addStyleName(AON.AON_CSS.aonTextBoxError());
		}
	}

	@UiHandler("province")
	void onChangeProvince(ChangeEvent event) {
		getDetail().setProvince(province.getSelectedIndex());
		markAsDirty();
	}

	@UiHandler("ceutaMelilla")
	void onChangeCeutaMelilla(ClickEvent event) {
		getDetail().setCeutaMelilla(ceutaMelilla.getValue());
		markAsDirty();
	}

	@UiHandler("key")
	void onChangeKey(ChangeEvent event) {
		getDetail().setKey(Mod1902014Key.values()[key.getSelectedIndex()].getValue());
		markAsDirty();
		enableOrDisableAdditionalDataPanel();
	}

	@UiHandler("subkey")
	void onChangeSubkey(ChangeEvent event) {
		String subk = ((key.getSubkey().getSelectedIndex() == -1) ? null : key
				.getSubkey().getValue(key.getSubkey().getSelectedIndex()));
		getDetail().setSubKey(subk);
		markAsDirty();
		enableOrDisableAdditionalDataPanel();
	}

	@UiHandler("perception")
	void onChangePerception(ChangeEvent event) {
		getDetail().setPerception(perception.getValue());
		markAsDirty();
	}

	@UiHandler("retention")
	void onChangeRetention(ChangeEvent event) {
		getDetail().setRetention(retention.getValue());
		markAsDirty();
	}

	@UiHandler("inKindPerception")
	void onChangeValuation(ChangeEvent event) {
		getDetail().setInKindPerception(inKindPerception.getValue());
		markAsDirty();
	}

	@UiHandler("inKindDeposit")
	void onChangeInKindDeposit(ChangeEvent event) {
		getDetail().setInKindDeposit(inKindDeposit.getValue());
		markAsDirty();
	}

	@UiHandler("inKindOutputDeposit")
	void onChangeInKindOutputDeposit(ChangeEvent event) {
		getDetail().setInKindOutputDeposit(inKindOutputDeposit.getValue());
		markAsDirty();
	}

	@UiHandler("birthYear")
	void onChangeBirthYear(ChangeEvent event) {
		getDetail().setBirthYear(birthYear.getValue());
		markAsDirty();
	}

	@UiHandler("familySituation")
	void onChangeFamilySituation(ChangeEvent event) {
		getDetail().setFamilySituation(
				(byte) familySituation.getSelectedIndex());
		markAsDirty();
	}

	@UiHandler("spouseDocument")
	void onChangeSpouseDocument(ChangeEvent event) {
		getDetail().setSpouseDocument(spouseDocument.getValue());
		markAsDirty();
	}

	@UiHandler("disability")
	void onChangeDisability(ChangeEvent event) {
		getDetail().setDisability((byte) disability.getSelectedIndex());
		markAsDirty();
	}

	@UiHandler("contract")
	void onChangeContract(ChangeEvent event) {
		getDetail().setContract((byte) contract.getSelectedIndex());
		markAsDirty();
	}

	@UiHandler("workActivityExtension")
	void onChangeWorkActivityExtension(ClickEvent event) {
		getDetail().setWorkActivityExtension(workActivityExtension.getValue());
		markAsDirty();
	}

	@UiHandler("geographicMobility")
	void onChangeGeographicMobility(ClickEvent event) {
		getDetail().setGeographicMobility(
				geographicMobility.getValue());
		markAsDirty();
	}

	@UiHandler("homeLoanCommunnication")
	void onChangeHomeLoanCommunnication(ClickEvent event) {
		getDetail().setHomeLoanCommunnication(homeLoanCommunnication.getValue());
		markAsDirty();
	}

	@UiHandler("applicableReduction")
	void onChangeApplicableReduction(ChangeEvent event) {
		getDetail().setApplicableReduction(applicableReduction.getValue());
		markAsDirty();
	}

	@UiHandler("deducibleExpense")
	void onChangeDeducibleExpense(ChangeEvent event) {
		getDetail().setDeducibleExpense(deducibleExpense.getValue());
		markAsDirty();
	}

	@UiHandler("compensatoryPension")
	void onChangeCompensatoryPension(ChangeEvent event) {
		getDetail().setCompensatoryPension(compensatoryPension.getValue());
		markAsDirty();
	}

	@UiHandler("foodAnnuality")
	void onChangeFoodAnnuality(ChangeEvent event) {
		getDetail().setFoodAnnuality(foodAnnuality.getValue());
		markAsDirty();
	}

	@UiHandler("lessThan3Descendent")
	void onChangeLessThan3Descendent(ChangeEvent event) {
		getDetail().setLessThan3Descendent(AonNumberUtils.toByte( lessThan3Descendent.getValue()));
		markAsDirty();
	}

	@UiHandler("lessThan3DescendentRatio")
	void onChangeLessThan3DescendentRatio(ChangeEvent event) {
		getDetail().setLessThan3DescendentRatio(AonNumberUtils.toByte( lessThan3DescendentRatio.getValue()));
		markAsDirty();
	}

	@UiHandler("otherDescendent")
	void onChangeOtherDescendent(ChangeEvent event) {
		getDetail().setOtherDescendent(AonNumberUtils.toByte( otherDescendent.getValue()));
		markAsDirty();
	}

	@UiHandler("otherDescendentRatio")
	void onChangeOtherDescendentRatio(ChangeEvent event) {
		getDetail().setOtherDescendentRatio(AonNumberUtils.toByte( otherDescendentRatio.getValue()));
		markAsDirty();
	}

	@UiHandler("firstChildCalculation")
	void onChangeFirstChildCalculation(ChangeEvent event) {
		getDetail().setFirstChildCalculation(AonNumberUtils.toByte( firstChildCalculation.getSelectedIndex()));
		markAsDirty();
	}

	@UiHandler("secondChildCalculation")
	void onChangeSecondChildCalculation(ChangeEvent event) {
		getDetail().setSecondChildCalculation(AonNumberUtils.toByte( secondChildCalculation.getSelectedIndex()));
		markAsDirty();
	}

	@UiHandler("thirdChildCalculation")
	void onChangeThirdChildCalculation(ChangeEvent event) {
		getDetail().setThirdChildCalculation(AonNumberUtils.toByte( thirdChildCalculation.getSelectedIndex()));
		markAsDirty();
	}

	@UiHandler("disabilityDescendent33")
	void onChangeDisabilityDescendent33(ChangeEvent event) {
		getDetail().setDisabilityDescendent33(AonNumberUtils.toByte( disabilityDescendent33.getValue()));
		markAsDirty();
	}

	@UiHandler("disabilityDescendent33Ratio")
	void onChangeDisabilityDescendent33Ratio(ChangeEvent event) {
		getDetail().setDisabilityDescendent33Ratio(AonNumberUtils.toByte( disabilityDescendent33Ratio.getValue()));
		markAsDirty();
	}

	@UiHandler("disabilityDescendentDependence")
	void onChangeDisabilityDescendentDependence(ChangeEvent event) {
		getDetail().setDisabilityDescendentDependence(AonNumberUtils.toByte( disabilityDescendentDependence.getValue()));
		markAsDirty();
	}

	@UiHandler("disabilityDescendentDependenceRatio")
	void onChangeDisabilityDescendentDependenceRatio(ChangeEvent event) {
		getDetail().setDisabilityDescendentDependenceRatio(AonNumberUtils.toByte( disabilityDescendentDependenceRatio.getValue()));
		markAsDirty();
	}

	@UiHandler("disabilityDescendent65")
	void onChangeDisabilityDescendent65(ChangeEvent event) {
		getDetail().setDisabilityDescendent65(AonNumberUtils.toByte( disabilityDescendent65.getValue()));
		markAsDirty();
	}

	@UiHandler("disabilityDescendent65Ratio")
	void onChangeDisabilityDescendent65Ratio(ChangeEvent event) {
		getDetail().setDisabilityDescendent65Ratio(AonNumberUtils.toByte( disabilityDescendent65Ratio.getValue()));
		markAsDirty();
	}

	@UiHandler("lessThan75Ascendant")
	void onChangeLessThan75Ascendant(ChangeEvent event) {
		getDetail().setLessThan75Ascendant(AonNumberUtils.toByte( lessThan75Ascendant.getValue()));
		markAsDirty();
	}

	@UiHandler("lessThan75AscendantRatio")
	void onChangeLessThan75AscendantRatio(ChangeEvent event) {
		getDetail().setLessThan75AscendantRatio(AonNumberUtils.toByte( lessThan75AscendantRatio.getValue()));
		markAsDirty();
	}

	@UiHandler("ascendant")
	void onChangeAscendant(ChangeEvent event) {
		getDetail().setAscendant(AonNumberUtils.toByte( ascendant.getValue()));
		markAsDirty();
	}

	@UiHandler("ascendantRatio")
	void onChangeAscendantRatio(ChangeEvent event) {
		getDetail().setAscendantRatio(AonNumberUtils.toByte( ascendantRatio.getValue()));
		markAsDirty();
	}

	@UiHandler("disabilityAscendant33")
	void onChangeDisabilityAscendant33(ChangeEvent event) {
		getDetail().setDisabilityAscendant33(
				AonNumberUtils.toByte( disabilityAscendant33.getValue()));
		markAsDirty();
	}

	@UiHandler("disabilityAscendant33Ratio")
	void onChangeDisabilityAscendant33Ratio(ChangeEvent event) {
		getDetail().setDisabilityAscendant33Ratio(AonNumberUtils.toByte( disabilityAscendant33Ratio.getValue()));
		markAsDirty();
	}

	@UiHandler("disabilityAscendantDependence")
	void onChangeDisabilityAscendantDependence(ChangeEvent event) {
		getDetail().setDisabilityAscendantDependence(AonNumberUtils.toByte( disabilityAscendantDependence.getValue()));
		markAsDirty();
	}

	@UiHandler("disabilityAscendantDependenceRatio")
	void onChangeDisabilityAscendantDependenceRatio(ChangeEvent event) {
		getDetail().setDisabilityAscendantDependenceRatio(AonNumberUtils.toByte( disabilityAscendantDependenceRatio.getValue()));
		markAsDirty();
	}

	@UiHandler("disabilityAscendant65")
	void onChangeDisabilityAscendant65(ChangeEvent event) {
		getDetail().setDisabilityAscendant65(AonNumberUtils.toByte( disabilityAscendant65.getValue()));
		markAsDirty();
	}

	@UiHandler("disabilityAscendant65Ratio")
	void onChangeDisabilityAscendant65Ratio(ChangeEvent event) {
		getDetail().setDisabilityAscendant65Ratio(AonNumberUtils.toByte( disabilityAscendant65Ratio.getValue()));
		markAsDirty();
	}

	static class Mod190DetailCell extends AbstractCell<Mod190Detail> {
		@Override
		public void render(Cell.Context context, Mod190Detail value,
				SafeHtmlBuilder sb) {
			if (value == null) {
				return;
			}
			sb.appendHtmlConstant("<div style='");
			if (value.isDirty()) {
				sb.appendHtmlConstant("font-weight:bold;");
			}
			if (value.isDeleted()) {
				sb.appendHtmlConstant("text-decoration:line-through");
			}
			sb.appendHtmlConstant("' class='");
			sb.appendHtmlConstant( AON.AON_CSS.aonLinkListItem());
			sb.appendHtmlConstant("'>");
			sb.appendEscaped(AonStringUtils.isEmpty(value.getName()) ?  AON.MSG.newPerceptor() : value.getName());
			if (value.isDirty()) {
				sb.appendEscaped(" *");
			}
			sb.appendHtmlConstant("</div>");
		}
	}

	@UiHandler("newDetailButton")
	void onNewDetailButtonClick(ClickEvent event) {
		newPerceptor();
	}
	
	private void newPerceptor(){
		currentMod190.getDetails().add(
			new Mod190Detail()
				.setKey("A")
				.setDirty(true)
				.setTempId((currentMod190.getDetails().size() + 1)  * (-1))
			);
		detailList.setRowCount(detailList.getRowCount() + 1);
		detailList.setPageSize(detailList.getRowCount());
		detailList.redraw();
		selectInList(currentMod190.getDetails().size() - 1);
		selectDetail();
	}
	private void markAsDirty() {
		if (!getDetail().isDirty()) {
			getDetail().setDirty(true);
			detailList.redraw();		
		}
	}
	
	private void selectInList(int i) {
		detailModel.setSelected(currentMod190.getDetails().get(i),true);
		detailList.getRowElement(i).scrollIntoView();
	}
	
	class Mod190DetailDataProvider extends AsyncDataProvider<Mod190Detail> {

		public Mod190DetailDataProvider(
				ProvidesKey<Mod190Detail> detailProvidesKey) {
			super(detailProvidesKey);
		}

		@Override
		protected void onRangeChanged(HasData<Mod190Detail> display) {
			if (currentMod190 != null && currentMod190.getId() != null) {
				if (currentMod190.getDetails().size() == 0) {
					newPerceptor();					
				} else {
					updateRowCount(currentMod190.getDetails().size(), true);
					updateRowData(0, currentMod190.getDetails());
					detailList.setPageSize(currentMod190.getDetails().size());
					selectInList(0);
					selectDetail();
				}
			}
		}
	}
}
