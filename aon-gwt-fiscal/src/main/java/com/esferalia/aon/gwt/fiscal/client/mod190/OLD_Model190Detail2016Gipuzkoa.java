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
import com.esferalia.aon.occam.api.model.type.Mod1902016Key;
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

public class OLD_Model190Detail2016Gipuzkoa extends ResizeComposite {

	public static final ProvidesKey<Mod190Detail> MOD190_DETAIL_PROVIDES_KEY = new ProvidesKey<Mod190Detail>() {
		@Override
		public Object getKey(Mod190Detail det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
	};
	
	interface Model190Detail2013Binder extends
			UiBinder<Widget, OLD_Model190Detail2016Gipuzkoa> {
	}

	private static Model190Detail2013Binder MODEL190_DETAIL_2013_BINDER = GWT
			.create(Model190Detail2013Binder.class);

	public class KeyListBox extends ListBox {

		public KeyListBox() {
			subkey = new ListBox();
			subkey.setWidth("45px");

			setWidth("40px");
			for (Mod1902016Key key : Mod1902016Key.values()) {
				this.addItem(key.getValue());
			}

			addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					subkey.clear();
					Mod1902016Key keyEnum = Mod1902016Key.values()[getSelectedIndex()];
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
			Mod1902016Key keyEnum = Mod1902016Key.values()[getSelectedIndex()];
			return keyEnum.hasSubkeys();
		}

		public ListBox getSubkey() {
			return subkey;
		}

		public void setValue(String key, String subKey) {
			Mod1902016Key keyEnum = Mod1902016Key.valueOf(key);
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
	DoubleBox perceptionIL;
	@UiField
	DoubleBox retentionIL;
	@UiField
	DoubleBox outputRetentionIL;
	@UiField
	ListBox disability;
	@UiField
	ListBox contract;
	@UiField
	DoubleBox applicableReduction;
	@UiField
	DoubleBox deducibleExpense;
	@UiField
	DoubleBox compensatoryPension;
	@UiField
	IntegerBox otherDescendent;

	public OLD_Model190Detail2016Gipuzkoa() {
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

		disability.addItem("0");
		disability.addItem("1");
		disability.addItem("2");
		disability.addItem("3");

		contract.addItem("-");
		contract.addItem("1");
		contract.addItem("2");
		contract.addItem("3");
		contract.addItem("4");
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
		perceptionIL.setValue(getDetail().getPerceptionIL());
		retentionIL.setValue(getDetail().getRetentionIL());
		outputRetentionIL.setValue(getDetail().getOutputRetentionIL());
		disability.setSelectedIndex(getDetail().getDisability());
		contract.setSelectedIndex(getDetail().getContract());
		applicableReduction.setValue(getDetail().getApplicableReduction());
		deducibleExpense.setValue(getDetail().getDeducibleExpense());
		compensatoryPension.setValue(getDetail().getCompensatoryPension());
		otherDescendent.setValue(getDetail().getOtherDescendent());
		restoreDeletedButton.setVisible(getDetail().isDeleted());
		deleteDetailButton.setVisible(!getDetail().isDeleted());
		enableOrDisableAdditionalDataPanel();
	}

	private void enableOrDisableAdditionalDataPanel() {
		Mod1902016Key keyEnum = Mod1902016Key.values()[key.getSelectedIndex()];
		String subk = ((key.getSubkey().getSelectedIndex() == -1) ? null : key
				.getSubkey().getValue(key.getSubkey().getSelectedIndex()));
		additionalDataPanel.setVisible( 
				    Mod1902016Key.A == keyEnum
				|| (Mod1902016Key.B == keyEnum && "01".equals(subk))
				|| (Mod1902016Key.B == keyEnum && "02".equals(subk))
				|| (Mod1902016Key.B == keyEnum && "04".equals(subk))
				||  Mod1902016Key.C == keyEnum
				|| (Mod1902016Key.E == keyEnum && "01".equals(subk))
				|| (Mod1902016Key.E == keyEnum && "02".equals(subk))
				)
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

	@UiHandler("key")
	void onChangeKey(ChangeEvent event) {
		getDetail().setKey(Mod1902016Key.values()[key.getSelectedIndex()].getValue());
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

	@UiHandler("perceptionIL")
	void onChangePerceptionIL(ChangeEvent event) {
		getDetail().setPerceptionIL(perceptionIL.getValue());
		markAsDirty();
	}

	@UiHandler("retentionIL")
	void onChangeRetentionIL(ChangeEvent event) {
		getDetail().setRetentionIL(retentionIL.getValue());
		markAsDirty();
	}

	@UiHandler("outputRetentionIL")
	void onChangeOutputRetentionIL(ChangeEvent event) {
		getDetail().setOutputRetentionIL(outputRetentionIL.getValue());
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

	@UiHandler("otherDescendent")
	void onChangeOtherDescendent(ChangeEvent event) {
		getDetail().setOtherDescendent(AonNumberUtils.toByte( otherDescendent.getValue()));
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
