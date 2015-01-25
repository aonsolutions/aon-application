package com.esferalia.aon.gwt.fiscal.client.mod193;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerTextBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model193Detail2014 extends ResizeComposite {

	interface Model193Detail2013Binder extends
			UiBinder<Widget, Model193Detail2014> {
	}

	private static Model193Detail2013Binder MODEL193_DETAIL_2013_BINDER = GWT
			.create(Model193Detail2013Binder.class);

	static interface ICallBack {
		void redrawList(Mod193Detail detail);
	}

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

	Mod193Detail detail;
	private ICallBack callback;

	@UiField
	Button deleteDetailButton;
	@UiField
	Button restoreDeletedButton;

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
	IntegerTextBox accrualYear;

	
	@UiField
	ListBox payment;
	@UiField
	ListBox codeType;
	@UiField
	TextBox accountCode;
	
	@UiField
	CheckBox inKind;
	@UiField
	DoubleTextBox lenderAmount;
	@UiField
	DoubleTextBox reduction;
	
	@UiField
	DoubleTextBox retentionBase;
	@UiField
	DoubleTextBox percent;
	@UiField
	DoubleTextBox retention;
	
	@UiField
	DateBoxEx loanStartDate;
	@UiField
	DateBoxEx loanDueDate;
	@UiField
	DoubleTextBox compensation;
	@UiField
	DoubleTextBox guarantee;
	
	public Model193Detail2014() {
		key = new KeyListBox();
		
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

	public void setCallback(ICallBack callback) {
		this.callback = callback;
	}

	public void setDetail(Mod193Detail detail) {
		this.detail = detail;

		pending.setValue( detail.isPending() );
		receiverDocument.setValue(detail.getDocument());
		representativeDocument.setValue(detail.getRepresentativeDocument());
		fullName.setValue(detail.getName());
		
		key.setValue(detail.getKey(), detail.getNature());
		intermediaryPayment.setValue( detail.isIntermediaryPayment() );
		province.setSelectedIndex(detail.getProvince());
		
		keyCode.setSelectedIndex(detail.getKeyCode());
		issuingCode.setValue(detail.getIssuingCode());
		accrualYear.setValue(detail.getAccrualYear());
		
		payment.setSelectedIndex(detail.getPayment());
		if (AonStringUtils.equals("C", detail.getCodeType())) {
			codeType.setSelectedIndex(1);	
		} else if (AonStringUtils.equals("0", detail.getCodeType())) {
			codeType.setSelectedIndex(2);
		} else if (AonStringUtils.equals("P", detail.getCodeType())) {
			codeType.setSelectedIndex(3);
		} else {
			codeType.setSelectedIndex(0);
		}
		accountCode.setValue(detail.getAccountCode());
		
		inKind.setValue(detail.isInKind());
		lenderAmount.setValue(detail.getLenderAmount());
		reduction.setValue(detail.getReduction());
		
		retentionBase.setValue(detail.getRetentionBase());
		percent.setValue(detail.getPercent());
		retention.setValue(detail.getRetention());
		
		loanStartDate.setValue(detail.getLoanStartDate());
		loanDueDate.setValue(detail.getLoanDueDate());
		compensation.setValue(detail.getCompensation());
		guarantee.setValue(detail.getGuarantee());
		
		restoreDeletedButton.setVisible(detail.isDeleted());
		deleteDetailButton.setVisible(!detail.isDeleted());
		
		enablePendingStatus();
	}

	private void enablePendingStatus() {
		
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

	@UiHandler("pending")
	void onChangePending(ClickEvent event) {
		detail.setPending(pending.getValue());
		if (pending.getValue()) {
			
		}
		detail.setDirty(true);
		enablePendingStatus();
	}

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
			accrualYear.addStyleName(Model193.AON_RESOURCES.css()
					.aonTextBoxError());
		}
	}

	@UiHandler("province")
	void onChangeProvince(ChangeEvent event) {
		detail.setProvince(province.getSelectedIndex());
		detail.setDirty(true);
	}

	@UiHandler("key")
	void onChangeKey(ChangeEvent event) {
		detail.setKey(Mod193Key.values()[key.getSelectedIndex()].getValue());
		detail.setDirty(true);
	}

	@UiHandler("nature")
	void onChangeNature(ChangeEvent event) {
		String subk = ((key.getNature().getSelectedIndex() == -1) 
				? null 
				: key.getNature().getValue(key.getNature().getSelectedIndex()));
		detail.setNature(subk);
		detail.setDirty(true);
	}
	@UiHandler("intermediaryPayment")
	void onChangeIntermediaryPayment(ClickEvent event) {
		detail.setIntermediaryPayment(intermediaryPayment.getValue());
		detail.setDirty(true);
	}
	@UiHandler("keyCode")
	void onChangeKeyCode(ChangeEvent event) {
		detail.setKeyCode( (byte) keyCode.getSelectedIndex() );
		detail.setDirty(true);
	}
	@UiHandler("issuingCode")
	void onChangeIssuingCode(ChangeEvent event) {
		detail.setIssuingCode( issuingCode.getValue() );
		detail.setDirty(true);
	}
	@UiHandler("payment")
	void onChangePayment(ChangeEvent event) {
		detail.setPayment( (byte) payment.getSelectedIndex() );
		detail.setDirty(true);
	}
	@UiHandler("codeType")
	void onChangeCodeType(ChangeEvent event) {
		detail.setCodeType( codeType.getValue( codeType.getSelectedIndex()) );
		detail.setDirty(true);
	}
	@UiHandler("accountCode")
	void onChangeAccountCode(ChangeEvent event) {
		detail.setAccountCode( accountCode.getValue() );
		detail.setDirty(true);
	}
	@UiHandler("inKind")
	void onChangeInKind(ClickEvent event) {
		detail.setInKind(inKind.getValue());
		detail.setDirty(true);
	}
	@UiHandler("lenderAmount")
	void onChangeLenderAmount(ChangeEvent event) {
		detail.setLenderAmount( lenderAmount.getDoubleValue() );
		detail.setDirty(true);
	}
	@UiHandler("reduction")
	void onChangeReduction(ChangeEvent event) {
		detail.setReduction( reduction.getDoubleValue() );
		detail.setDirty(true);
	}
	@UiHandler("retentionBase")
	void onChangeRetentionBase(ChangeEvent event) {
		detail.setRetentionBase( retentionBase.getDoubleValue() );
		detail.setDirty(true);
	}
	@UiHandler("percent")
	void onChangePercent(ChangeEvent event) {
		detail.setPercent( percent.getDoubleValue() );
		detail.setDirty(true);
	}
	@UiHandler("retention")
	void onChangeRetention(ChangeEvent event) {
		detail.setRetention( retention.getDoubleValue() );
		detail.setDirty(true);
	}
	@UiHandler("loanStartDate")
	void onChangeloanStartDate(ValueChangeEvent<Date> event) {
		detail.setLoanStartDate( loanStartDate.getValue() );
		detail.setDirty(true);
	}
	@UiHandler("loanDueDate")
	void onChangeloanDueDate(ValueChangeEvent<Date> event) {
		detail.setLoanDueDate( loanDueDate.getValue() );
		detail.setDirty(true);
	}
	@UiHandler("compensation")
	void onChangeCompensation(ChangeEvent event) {
		detail.setCompensation( compensation.getDoubleValue() );
		detail.setDirty(true);
	}
	@UiHandler("guarantee")
	void onChangeGuarantee(ChangeEvent event) {
		detail.setGuarantee( guarantee.getDoubleValue() );
		detail.setDirty(true);
	}
	
}
