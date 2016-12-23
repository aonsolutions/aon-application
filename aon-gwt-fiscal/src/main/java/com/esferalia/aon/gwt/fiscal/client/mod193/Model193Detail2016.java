package com.esferalia.aon.gwt.fiscal.client.mod193;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.type.Mod193Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
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
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class Model193Detail2016 extends ResizeComposite {

	public static final ProvidesKey<Mod193Detail> MOD193_DETAIL_PROVIDES_KEY = new ProvidesKey<Mod193Detail>() {
		@Override
		public Object getKey(Mod193Detail mod193Detail) {
			return mod193Detail == null ? null : mod193Detail.getId();
		}
	};

	interface Model193Detail2013Binder extends
			UiBinder<Widget, Model193Detail2016> {
	}

	private static Model193Detail2013Binder MODEL193_DETAIL_2013_BINDER = GWT
			.create(Model193Detail2013Binder.class);

	public class KeyListBox extends ListBox {

		public KeyListBox() {
			nature = new ListBox();
			nature.setWidth("45px");

			setWidth("40px");
			for (Mod193Key key : Mod193Key.values()) {
				this.addItem(key.getValue());
			}

			addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					nature.clear();
					Mod193Key keyEnum = Mod193Key.values()[getSelectedIndex()];
					if (keyEnum.hasNatures()) {
						nature.setEnabled(true);
						for (int i = 0; i < keyEnum.getNatures().length; i++) {
							nature.addItem(keyEnum.getNatures()[i]);
						}
					} else {
						nature.setEnabled(false);
					}
				}
			});
		}

		public boolean hasNatures() {
			Mod193Key keyEnum = Mod193Key.values()[getSelectedIndex()];
			return keyEnum.hasNatures();
		}

		public ListBox getNature() {
			return nature;
		}

		public void setValue(String key, String nature) {
			Mod193Key keyEnum = Mod193Key.valueOf(key);
			setSelectedIndex(keyEnum.ordinal());
			getNature().clear();
			if (hasNatures()) {
				getNature().setEnabled(true);
				for (int i = 0; i < keyEnum.getNatures().length; i++) {
					getNature().addItem(keyEnum.getNatures()[i]);
					if (keyEnum.getNatures()[i].equals(nature)) {
						getNature().setSelectedIndex(i);
					}
				}
				getNature().setEnabled(true);
			} else {
				getNature().setEnabled(false);
			}
		}
	}

	private Mod193 currentMod193;
	private Mod193DetailDataProvider dataProvider;
	private SingleSelectionModel<Mod193Detail> detailModel;
	

	@UiField
	Button deleteDetailButton;
	@UiField
	Button restoreDeletedButton;
	@UiField
	Button newDetailButton;

	@UiField(provided=true)
	CellList<Mod193Detail> detailList;
	@UiField
	ScrollPanel detailListPanel;

	@UiField
	CheckBox pending;
	@UiField
	DocumentTextBox receiverDocument;
	@UiField
	DocumentTextBox representativeDocument;
	@UiField
	TextBox fullName;

	@UiField(provided = true)
	KeyListBox key;
	@UiField(provided = true)
	ListBox nature;
	@UiField
	CheckBox intermediaryPayment;
	@UiField
	ProvinceListBox province;

	@UiField
	ListBox keyCode;
	@UiField
	TextBox issuingCode;
	@UiField
	IntegerBox accrualYear;

	
	@UiField
	ListBox payment;
	@UiField
	ListBox codeType;
	@UiField
	TextBox accountCode;
	
	@UiField
	CheckBox inKind;
	@UiField
	DoubleBox lenderAmount;
	@UiField
	DoubleBox reduction;
	
	@UiField
	DoubleBox retentionBase;
	@UiField
	DoubleBox percent;
	@UiField
	DoubleBox retention;
	
	@UiField
	DateBoxEx loanStartDate;
	@UiField
	DateBoxEx loanDueDate;
	@UiField
	DoubleBox penalization;
	@UiField
	DoubleBox compensation;
	@UiField
	DoubleBox guarantee;
	
	public Model193Detail2016() {
		key = new KeyListBox();
		
		Mod193DetailCell mod193DetailCell = new Mod193DetailCell();
		detailList = new CellList<Mod193Detail>(mod193DetailCell, MOD193_DETAIL_PROVIDES_KEY);
		detailList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		detailList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		detailModel = new SingleSelectionModel<Mod193Detail>(MOD193_DETAIL_PROVIDES_KEY);
		detailModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				selectDetail();
			}
			
		});
		detailList.setSelectionModel(detailModel);
		detailList.setEmptyListWidget(new HTML(AON.MSG.noData()));
		
		dataProvider = new Mod193DetailDataProvider(MOD193_DETAIL_PROVIDES_KEY);
		dataProvider.addDataDisplay(detailList);
		detailList.setVisible(true);
		
		Widget ui = MODEL193_DETAIL_2013_BINDER.createAndBindUi(this);
		initWidget(ui);
		
		keyCode.addItem("-");
		keyCode.addItem("1");
		keyCode.addItem("2");
		keyCode.addItem("3");
		
		payment.addItem("-");
		payment.addItem("1");
		payment.addItem("2");
		payment.addItem("3");

		codeType.addItem("-","");
		codeType.addItem("C","C");
		codeType.addItem("O","O");
		codeType.addItem("P","P");
	}

	public void setMod193(Mod193 mod193) {
		this.currentMod193 = mod193;
		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),true);
	}
	private Mod193Detail getDetail() {
		return detailModel.getSelectedObject();
	}
	
	public void selectDetail() {
		pending.setValue( getDetail().isPending() );
		receiverDocument.setValue(getDetail().getDocument());
		representativeDocument.setValue(getDetail().getRepresentativeDocument());
		fullName.setValue(getDetail().getName());
		
		key.setValue(getDetail().getKey(), getDetail().getNature());
		intermediaryPayment.setValue( getDetail().isIntermediaryPayment() );
		province.setSelectedIndex(getDetail().getProvince());
		
		keyCode.setSelectedIndex(getDetail().getKeyCode());
		issuingCode.setValue(getDetail().getIssuingCode());
		accrualYear.setValue(getDetail().getAccrualYear());
		
		payment.setSelectedIndex(getDetail().getPayment());
		if (AonStringUtils.equals("C", getDetail().getCodeType())) {
			codeType.setSelectedIndex(1);	
		} else if (AonStringUtils.equals("0", getDetail().getCodeType())) {
			codeType.setSelectedIndex(2);
		} else if (AonStringUtils.equals("P", getDetail().getCodeType())) {
			codeType.setSelectedIndex(3);
		} else {
			codeType.setSelectedIndex(0);
		}
		accountCode.setValue(getDetail().getAccountCode());
		
		inKind.setValue(getDetail().isInKind());
		lenderAmount.setValue(getDetail().getLenderAmount());
		reduction.setValue(getDetail().getReduction());
		
		retentionBase.setValue(getDetail().getRetentionBase());
		percent.setValue(getDetail().getPercent());
		retention.setValue(getDetail().getRetention());
		
		loanStartDate.setValue(getDetail().getLoanStartDate());
		loanDueDate.setValue(getDetail().getLoanDueDate());
		compensation.setValue(getDetail().getCompensation());
		penalization.setValue(getDetail().getPenalization());
		guarantee.setValue(getDetail().getGuarantee());
		
		restoreDeletedButton.setVisible(getDetail().isDeleted());
		deleteDetailButton.setVisible(!getDetail().isDeleted());
		enablePendingStatus();
	}

	private void enablePendingStatus() {
		
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

	@UiHandler("pending")
	void onChangePending(ClickEvent event) {
		getDetail().setPending(pending.getValue());
		if (pending.getValue()) {
			
		}
		getDetail().setDirty(true);
		enablePendingStatus();
	}

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
	}

	@UiHandler("accrualYear")
	void onChangeAccrualYear(ChangeEvent event) {
		try {
			getDetail().setAccrualYear(accrualYear.getValue());
			markAsDirty();
		} catch (NumberFormatException e) {
			accrualYear.addStyleName(AON.AON_RESOURCES.css()
					.aonTextBoxError());
		}
	}

	@UiHandler("province")
	void onChangeProvince(ChangeEvent event) {
		getDetail().setProvince(province.getSelectedIndex());
		markAsDirty();
	}

	@UiHandler("key")
	void onChangeKey(ChangeEvent event) {
		getDetail().setKey(Mod193Key.values()[key.getSelectedIndex()].getValue());
		markAsDirty();
	}

	@UiHandler("nature")
	void onChangeNature(ChangeEvent event) {
		String subk = ((key.getNature().getSelectedIndex() == -1) 
				? null 
				: key.getNature().getValue(key.getNature().getSelectedIndex()));
		getDetail().setNature(subk);
		markAsDirty();
	}
	@UiHandler("intermediaryPayment")
	void onChangeIntermediaryPayment(ClickEvent event) {
		getDetail().setIntermediaryPayment(intermediaryPayment.getValue());
		markAsDirty();
	}
	@UiHandler("keyCode")
	void onChangeKeyCode(ChangeEvent event) {
		getDetail().setKeyCode( (byte) keyCode.getSelectedIndex() );
		markAsDirty();
	}
	@UiHandler("issuingCode")
	void onChangeIssuingCode(ChangeEvent event) {
		getDetail().setIssuingCode( issuingCode.getValue() );
		markAsDirty();
	}
	@UiHandler("payment")
	void onChangePayment(ChangeEvent event) {
		getDetail().setPayment( (byte) payment.getSelectedIndex() );
		markAsDirty();
	}
	@UiHandler("codeType")
	void onChangeCodeType(ChangeEvent event) {
		getDetail().setCodeType( codeType.getValue( codeType.getSelectedIndex()) );
		markAsDirty();
	}
	@UiHandler("accountCode")
	void onChangeAccountCode(ChangeEvent event) {
		getDetail().setAccountCode( accountCode.getValue() );
		markAsDirty();
	}
	@UiHandler("inKind")
	void onChangeInKind(ClickEvent event) {
		getDetail().setInKind(inKind.getValue());
		markAsDirty();
	}
	@UiHandler("lenderAmount")
	void onChangeLenderAmount(ChangeEvent event) {
		getDetail().setLenderAmount( lenderAmount.getValue() );
		markAsDirty();
	}
	@UiHandler("reduction")
	void onChangeReduction(ChangeEvent event) {
		getDetail().setReduction( reduction.getValue() );
		markAsDirty();
	}
	@UiHandler("retentionBase")
	void onChangeRetentionBase(ChangeEvent event) {
		getDetail().setRetentionBase( retentionBase.getValue() );
		markAsDirty();
	}
	@UiHandler("percent")
	void onChangePercent(ChangeEvent event) {
		getDetail().setPercent( percent.getValue() );
		markAsDirty();
	}
	@UiHandler("retention")
	void onChangeRetention(ChangeEvent event) {
		getDetail().setRetention( retention.getValue() );
		markAsDirty();
	}
	@UiHandler("loanStartDate")
	void onChangeloanStartDate(ValueChangeEvent<Date> event) {
		getDetail().setLoanStartDate( loanStartDate.getValue() );
		markAsDirty();
	}
	@UiHandler("loanDueDate")
	void onChangeloanDueDate(ValueChangeEvent<Date> event) {
		getDetail().setLoanDueDate( loanDueDate.getValue() );
		markAsDirty();
	}
	@UiHandler("compensation")
	void onChangeCompensation(ChangeEvent event) {
		getDetail().setCompensation( compensation.getValue() );
		markAsDirty();
	}
	@UiHandler("penalization")
	void onChangePenalization(ChangeEvent event) {
		getDetail().setPenalization(penalization.getValue() );
		markAsDirty();
	}
	@UiHandler("guarantee")
	void onChangeGuarantee(ChangeEvent event) {
		getDetail().setGuarantee( guarantee.getValue() );
		markAsDirty();
	}
	
	static class Mod193DetailCell extends AbstractCell<Mod193Detail> {
		@Override
		public void render(Cell.Context context, Mod193Detail value,
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
		int newKey = (currentMod193.getDetails().size() + 1) * (-1);
		final Mod193Detail perceptor = new Mod193Detail();
		perceptor.setId(newKey);
		perceptor.setKey("A");
		perceptor.setType( Mod193Detail.DETAIL_TYPE);
		currentMod193.getDetails().add(perceptor);
		detailList.setRowCount(detailList.getRowCount() + 1);
		detailList.setPageSize(detailList.getRowCount());
		detailList.redraw();
		selectInList(currentMod193.getDetails().size() - 1);
		selectDetail();
	}
	
	private void markAsDirty() {
		if (!getDetail().isDirty()) {
			getDetail().setDirty(true);
			detailList.redraw();		
		}
	}
	
	private void selectInList(int i) {
		detailModel.setSelected(currentMod193.getDetails().get(i),true);
		detailList.getRowElement(i).scrollIntoView();
		detailListPanel.scrollToLeft();
	}
	
	class Mod193DetailDataProvider extends AsyncDataProvider<Mod193Detail> {

		public Mod193DetailDataProvider(
				ProvidesKey<Mod193Detail> detailProvidesKey) {
			super(detailProvidesKey);
		}

		@Override
		protected void onRangeChanged(HasData<Mod193Detail> display) {
			if (currentMod193 != null && currentMod193.getId() != null) {
				if (currentMod193.getDetails().size() == 0) {
					newPerceptor();					
				} else {
					updateRowCount(currentMod193.getDetails().size(), true);
					updateRowData(0, currentMod193.getDetails());
					detailList.setPageSize(currentMod193.getDetails().size());
					selectInList(0);
					selectDetail();
				}
			}
		}
	}
}
