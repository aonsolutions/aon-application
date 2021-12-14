package com.esferalia.aon.gwt.fiscal.client.widget;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Country2ListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;

public class BankAccountBox extends SimplePanel implements HasValueChangeHandlers<BankAccount>, HasKeyUpHandlers, HasEnabled {
	
	private Country2ListBox countryBox = new Country2ListBox();
	private AonTextBox check = new AonTextBox();
	
	private AonTextBox bban1 = new AonTextBox();
	private AonTextBox bban2 = new AonTextBox();
	private AonTextBox bban3 = new AonTextBox();
	private AonTextBox bban4 = new AonTextBox();
	private AonTextBox bban5 = new AonTextBox();
	private AonTextBox bban6 = new AonTextBox();
	private AonTextBox bban7 = new AonTextBox();
	private AonTextBox bban8 = new AonTextBox();
	
	private InlineLabel ccc0 = new InlineLabel();
	private AonTextBox ccc1 = new AonTextBox();
	private AonTextBox ccc2 = new AonTextBox();
	private AonTextBox ccc3 = new AonTextBox();
	private AonTextBox ccc4 = new AonTextBox();
	
	private InlineLabel okIcon = new InlineLabel(); 
	private InlineLabel koIcon = new InlineLabel();
	
	private BankAccount bankAccount;
	private boolean ibanMode = true;
	
	public BankAccountBox() {
		this(null);
	}
	
	public BankAccountBox(BankAccount ba) {
		setValue(ba);
		
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonWidthAll());
		tab.getColumnFormatter().setWidth(0, "50px;");
		tab.getColumnFormatter().setWidth(1,"auto");
		int row = 0; 
		int col = 0;
		
		tab.addStyleName(AON.CSS.aonWidthAll());
		this.setWidget(tab);
		
		FlowPanel dataPanel = new FlowPanel();
		
		InlineLabel ibanLabel = new InlineLabel(ibanMode?"IBAN:":"CCC:");
		ibanLabel.setStyleName(AON.CSS.aonInnerLabel());
		ibanLabel.addStyleName(AON.CSS.aonClickableLabel());
		ibanLabel.addClickHandler(event -> {
			ibanMode = !ibanMode;
			ibanLabel.setText(ibanMode?"IBAN:":"CCC:");
			if (!ibanMode) {
				BankAccountBox.this.bankAccount.setCountry(Country.ES);
			}
			enableWidgets();
			validate();
		});
		tab.setWidget(row, col, ibanLabel);
		col++;

		dataPanel.setStyleName(AON.CSS.aonNowrap());
		countryBox.setValue(this.bankAccount.getCountry());
		countryBox.setVisible(ibanMode);
		countryBox.addChangeHandler( event -> {
			BankAccountBox.this.bankAccount.setCountry(countryBox.getValue());
			enableWidgets();
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(countryBox);
		
		enableWidgets();

		check.addStyleName(AON.CSS.aonMarginLeft());
		check.setMaxLength(2);
		check.setVisibleLength(2);
		check.setValue( this.bankAccount.getCheck());
		check.setVisible(ibanMode);
		check.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setCheck(check.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(check);

		ccc0.setStyleName(AON.CSS.aonBold());
		ccc0.setVisible(!ibanMode);
		dataPanel.add(ccc0);
		
		ccc1.addStyleName(AON.CSS.aonMarginLeft());
		ccc1.setMaxLength(4);
		ccc1.setVisibleLength(4);
		ccc1.setValue( this.bankAccount.getCCC1());
		ccc1.setVisible(!ibanMode);
		ccc1.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setCCC1(ccc1.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(ccc1);

		ccc2.addStyleName(AON.CSS.aonMarginLeft());
		ccc2.setMaxLength(4);
		ccc2.setVisibleLength(4);
		ccc2.setValue( this.bankAccount.getCCC2());
		ccc2.setVisible(!ibanMode);
		ccc2.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setCCC2(ccc2.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(ccc2);

		ccc3.addStyleName(AON.CSS.aonMarginLeft());
		ccc3.setMaxLength(2);
		ccc3.setVisibleLength(2);
		ccc3.setValue( this.bankAccount.getCCC3());
		ccc3.setVisible(!ibanMode);
		ccc3.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setCCC3(ccc3.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(ccc3);

		ccc4.addStyleName(AON.CSS.aonMarginLeft());
		ccc4.setMaxLength(10);
		ccc4.setVisibleLength(10);
		ccc4.setValue( this.bankAccount.getCCC4());
		ccc4.setVisible(!ibanMode);
		ccc4.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setCCC4(ccc4.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(ccc4);
//-------
		bban1.addStyleName(AON.CSS.aonMarginLeft());
		bban1.setMaxLength(4);
		bban1.setVisibleLength(4);
		bban1.setValue( this.bankAccount.getBban1());
		bban1.setVisible(ibanMode);
		bban1.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setBban1(bban1.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(bban1);

		bban2.addStyleName(AON.CSS.aonMarginLeft());
		bban2.setMaxLength(4);
		bban2.setVisibleLength(4);
		bban2.setValue( this.bankAccount.getBban2());
		bban2.setVisible(ibanMode);
		bban2.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setBban2(bban2.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(bban2);

		bban3.addStyleName(AON.CSS.aonMarginLeft());
		bban3.setMaxLength(4);
		bban3.setVisibleLength(4);
		bban3.setValue( this.bankAccount.getBban3());
		bban3.setVisible(ibanMode);
		bban3.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setBban3(bban3.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(bban3);

		bban4.addStyleName(AON.CSS.aonMarginLeft());
		bban4.setMaxLength(4);
		bban4.setVisibleLength(4);
		bban4.setValue( this.bankAccount.getBban4());
		bban4.setVisible(ibanMode);
		bban4.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setBban4(bban4.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(bban4);

		bban5.addStyleName(AON.CSS.aonMarginLeft());
		bban5.setMaxLength(4);
		bban5.setVisibleLength(4);
		bban5.setValue( this.bankAccount.getBban5());
		bban5.setVisible(ibanMode);
		bban5.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setBban5(bban5.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(bban5);

		bban6.addStyleName(AON.CSS.aonMarginLeft());
		bban6.setMaxLength(4);
		bban6.setVisibleLength(4);
		bban6.setValue( this.bankAccount.getBban6());
		bban6.setVisible(ibanMode);
		bban6.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setBban6(bban6.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(bban6);

		bban7.addStyleName(AON.CSS.aonMarginLeft());
		bban7.setMaxLength(4);
		bban7.setVisibleLength(4);
		bban7.setValue( this.bankAccount.getBban7());
		bban7.setVisible(ibanMode);
		bban7.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setBban7(bban7.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(bban7);

		bban8.addStyleName(AON.CSS.aonMarginLeft());
		bban8.setMaxLength(2);
		bban8.setVisibleLength(2);
		bban8.setValue( this.bankAccount.getBban8());
		bban8.setVisible(ibanMode);
		bban8.addValueChangeHandler(event -> {
			BankAccountBox.this.bankAccount.setBban8(bban6.getValue());
			validate();
			ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, BankAccountBox.this.bankAccount);
		});
		dataPanel.add(bban8);
		
		FlowPanel iconPanel = new FlowPanel();
		iconPanel.setStyleName(AON.CSS.aonNowrap());
		iconPanel.setStyleName(AON.CSS.aonInline());
		okIcon = new InlineLabel();
		okIcon.setStyleName(AON.CSS.aonIconLabel());
		okIcon.addStyleName(AON.CSS.aonIconValid());
		okIcon.addStyleName(AON.CSS.aonMarginLeft());
		iconPanel.add(okIcon);

		koIcon = new InlineLabel();
		koIcon.setStyleName(AON.CSS.aonIconLabel());
		koIcon.addStyleName(AON.CSS.aonIconInvalid());
		koIcon.addStyleName(AON.CSS.aonMarginLeft());
		iconPanel.add(koIcon);
		dataPanel.add(iconPanel);
		tab.setWidget(row, col, dataPanel);
		
		row++;
		col = 0;
		
		validate();
		enableWidgets();
	}
	
	protected void enableWidgets() {
		if (ibanMode) {
			countryBox.setVisible(true);
			int ibanLength = countryBox.getValue() == null? 0 : countryBox.getValue().getIbanLength();
			check.setVisible(countryBox.getValue() != null);
			bban1.setVisible(ibanLength > 4);
			bban2.setVisible(ibanLength > 8);
			bban3.setVisible(ibanLength > 12);
			bban4.setVisible(ibanLength > 16);
			bban5.setVisible(ibanLength > 20);
			bban6.setVisible(ibanLength > 24);
			bban7.setVisible(ibanLength > 28);
			bban8.setVisible(ibanLength > 32);
			ccc0.setVisible(false);
			ccc1.setVisible(false);
			ccc2.setVisible(false);
			ccc3.setVisible(false);
			ccc4.setVisible(false);
		} else {
			countryBox.setVisible(false);
			check.setVisible(false);
			bban1.setVisible(false);
			bban2.setVisible(false);
			bban3.setVisible(false);
			bban4.setVisible(false);
			bban5.setVisible(false);
			bban6.setVisible(false);
			bban7.setVisible(false);
			bban8.setVisible(false);
			ccc0.setVisible(true);
			ccc1.setVisible(true);
			ccc2.setVisible(true);
			ccc3.setVisible(true);
			ccc4.setVisible(true);
		}
	}

	protected void validate() {
		if (!ibanMode) {
			this.bankAccount.setCheck(this.bankAccount.calculateIbanControlDigit());
			check.setValue(this.bankAccount.getCheck());
			ccc0.setText( this.bankAccount.getCountry() +  AonStringUtils.defaultString(this.bankAccount.getCheck()));
		}
		boolean valid = this.bankAccount.isValidBankAccount();
		okIcon.setVisible(valid);
		koIcon.setVisible(!valid);
	}

	public BankAccount getValue() {
		return this.bankAccount;
	}
	
	public void setValue(BankAccount ba) {
		this.bankAccount = (ba==null?new BankAccount():ba);
		if (this.bankAccount.getCountry() == null) {
			this.bankAccount.setCountry(Country.ES);
		}
		countryBox.setValue(this.bankAccount.getCountry());
		check.setValue(this.bankAccount.getCheck());
		bban1.setValue(this.bankAccount.getBban1());
		bban2.setValue(this.bankAccount.getBban2());
		bban3.setValue(this.bankAccount.getBban3());
		bban4.setValue(this.bankAccount.getBban4());
		bban5.setValue(this.bankAccount.getBban5());
		bban6.setValue(this.bankAccount.getBban6());
		bban7.setValue(this.bankAccount.getBban7());
		ccc1.setValue(this.bankAccount.getCCC1());
		ccc2.setValue(this.bankAccount.getCCC2());
		ccc3.setValue(this.bankAccount.getCCC3());
		ccc4.setValue(this.bankAccount.getCCC4());
		validate();
	}
	@Override
	public boolean isEnabled() {
		return bban1.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		countryBox.setEnabled(enabled);
		check.setEnabled(enabled);
		ccc1.setEnabled(enabled);
		ccc2.setEnabled(enabled);
		ccc3.setEnabled(enabled);
		ccc4.setEnabled(enabled);
		bban1.setEnabled(enabled);
		bban2.setEnabled(enabled);
		bban3.setEnabled(enabled);
		bban4.setEnabled(enabled);
		bban5.setEnabled(enabled);
		bban6.setEnabled(enabled);
		bban7.setEnabled(enabled);
		bban8.setEnabled(enabled);
	}

	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<BankAccount> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		countryBox.addKeyUpHandler(handler);
		check.addKeyUpHandler(handler);
		ccc1.addKeyUpHandler(handler);
		ccc2.addKeyUpHandler(handler);
		ccc3.addKeyUpHandler(handler);
		ccc4.addKeyUpHandler(handler);
		bban1.addKeyUpHandler(handler);
		bban2.addKeyUpHandler(handler);
		bban3.addKeyUpHandler(handler);
		bban4.addKeyUpHandler(handler);
		bban5.addKeyUpHandler(handler);
		bban6.addKeyUpHandler(handler);
		bban7.addKeyUpHandler(handler);
		bban8.addKeyUpHandler(handler);
		return super.addHandler(handler, KeyUpEvent.getType());
	}
}
