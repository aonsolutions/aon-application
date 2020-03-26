package com.esferalia.aon.gwt.fiscal.client.widget;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Country2ListBox;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class BankAccountBox extends SimplePanel implements HasValueChangeHandlers<BankAccount>, HasEnabled {
	
	public static class BankAccountBoxOptions {
		private BankAccount bankAccount;
		private boolean aliasEditable;
		
		public boolean isAliasEditable() {
			return aliasEditable;
		}
		public BankAccountBoxOptions setAliasEditable(boolean aliasEditable) {
			this.aliasEditable = aliasEditable;
			return this;
		}
		
		public BankAccount getBankAccount() {
			return bankAccount;
		}
		public BankAccountBoxOptions setBankAccount(BankAccount bankAccount) {
			this.bankAccount = bankAccount;			
			return this;
		}
	}
	private Country2ListBox countryBox = new Country2ListBox();
	private TextBox check = new TextBox();
	
	private TextBox bban1 = new TextBox();
	private TextBox bban2 = new TextBox();
	private TextBox bban3 = new TextBox();
	private TextBox bban4 = new TextBox();
	private TextBox bban5 = new TextBox();
	private TextBox bban6 = new TextBox();
	private TextBox bban7 = new TextBox();
	private TextBox bban8 = new TextBox();
	
	private InlineLabel ccc0 = new InlineLabel();
	private TextBox ccc1 = new TextBox();
	private TextBox ccc2 = new TextBox();
	private TextBox ccc3 = new TextBox();
	private TextBox ccc4 = new TextBox();
	
	private InlineLabel okIcon = new InlineLabel(); 
	private InlineLabel koIcon = new InlineLabel();
	
	private BankAccount bankAccount;
	private boolean ibanMode = true;
	
	public BankAccountBox(BankAccountBoxOptions options) {
		bankAccount = options.getBankAccount();
		if (bankAccount == null) {
			bankAccount = new BankAccount();
		}
		if (bankAccount.getCountry() == null) {
			bankAccount.setCountry(Country.ES);
		}
		
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonWidthAll());
		tab.getColumnFormatter().setWidth(0, "1%");
		tab.getColumnFormatter().setWidth(1,"auto");
		int row = 0; 
		int col = 0;
		
		tab.addStyleName(AON.AON_CSS.aonWidthAll());
		this.setWidget(tab);
		
		FlowPanel dataPanel = new FlowPanel();
		
		InlineLabel ibanLabel = new InlineLabel(ibanMode?"IBAN:":"CCC:");
		ibanLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		ibanLabel.addStyleName(AON.AON_CSS.aonClickableLabel());
		ibanLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ibanMode = !ibanMode;
				ibanLabel.setText(ibanMode?"IBAN:":"CCC:");
				if (!ibanMode) {
					bankAccount.setCountry(Country.ES);
				}
				enableWidgets();
				validate();
			}
		});
		tab.setWidget(row, col, ibanLabel);
		col++;

		dataPanel.setStyleName(AON.AON_CSS.aonNowrap());
		countryBox.setValue(bankAccount.getCountry());
		countryBox.setVisible(ibanMode);
		countryBox.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				bankAccount.setCountry(countryBox.getValue());
				enableWidgets();
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(countryBox);
		
		enableWidgets();

		check.setStyleName(AON.AON_CSS.aonInputText());
		check.addStyleName(AON.AON_CSS.aonMarginLeft5());
		check.setMaxLength(2);
		check.setVisibleLength(2);
		check.setValue( bankAccount.getCheck());
		check.setVisible(ibanMode);
		check.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setCheck(check.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(check);

		ccc0.setStyleName(AON.AON_CSS.aonBold());
		ccc0.setVisible(!ibanMode);
		dataPanel.add(ccc0);
		
		ccc1.setStyleName(AON.AON_CSS.aonInputText());
		ccc1.addStyleName(AON.AON_CSS.aonMarginLeft5());
		ccc1.setMaxLength(4);
		ccc1.setVisibleLength(4);
		ccc1.setValue( bankAccount.getCCC1());
		ccc1.setVisible(!ibanMode);
		ccc1.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setCCC1(ccc1.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(ccc1);

		ccc2.setStyleName(AON.AON_CSS.aonInputText());
		ccc2.addStyleName(AON.AON_CSS.aonMarginLeft5());
		ccc2.setMaxLength(4);
		ccc2.setVisibleLength(4);
		ccc2.setValue( bankAccount.getCCC2());
		ccc2.setVisible(!ibanMode);
		ccc2.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setCCC2(ccc2.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(ccc2);

		ccc3.setStyleName(AON.AON_CSS.aonInputText());
		ccc3.addStyleName(AON.AON_CSS.aonMarginLeft5());
		ccc3.setMaxLength(2);
		ccc3.setVisibleLength(2);
		ccc3.setValue( bankAccount.getCCC3());
		ccc3.setVisible(!ibanMode);
		ccc3.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setCCC3(ccc3.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(ccc3);

		ccc4.setStyleName(AON.AON_CSS.aonInputText());
		ccc4.addStyleName(AON.AON_CSS.aonMarginLeft5());
		ccc4.setMaxLength(10);
		ccc4.setVisibleLength(10);
		ccc4.setValue( bankAccount.getCCC4());
		ccc4.setVisible(!ibanMode);
		ccc4.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setCCC4(ccc4.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(ccc4);
//-------
		bban1.setStyleName(AON.AON_CSS.aonInputText());
		bban1.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban1.setMaxLength(4);
		bban1.setVisibleLength(4);
		bban1.setValue( bankAccount.getBban1());
		bban1.setVisible(ibanMode);
		bban1.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban1(bban1.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(bban1);

		bban2.setStyleName(AON.AON_CSS.aonInputText());
		bban2.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban2.setMaxLength(4);
		bban2.setVisibleLength(4);
		bban2.setValue( bankAccount.getBban2());
		bban2.setVisible(ibanMode);
		bban2.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban2(bban2.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(bban2);

		bban3.setStyleName(AON.AON_CSS.aonInputText());
		bban3.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban3.setMaxLength(4);
		bban3.setVisibleLength(4);
		bban3.setValue( bankAccount.getBban3());
		bban3.setVisible(ibanMode);
		bban3.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban3(bban3.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(bban3);

		bban4.setStyleName(AON.AON_CSS.aonInputText());
		bban4.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban4.setMaxLength(4);
		bban4.setVisibleLength(4);
		bban4.setValue( bankAccount.getBban4());
		bban4.setVisible(ibanMode);
		bban4.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban4(bban4.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(bban4);

		bban5.setStyleName(AON.AON_CSS.aonInputText());
		bban5.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban5.setMaxLength(4);
		bban5.setVisibleLength(4);
		bban5.setValue( bankAccount.getBban5());
		bban5.setVisible(ibanMode);
		bban5.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban5(bban5.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(bban5);

		bban6.setStyleName(AON.AON_CSS.aonInputText());
		bban6.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban6.setMaxLength(4);
		bban6.setVisibleLength(4);
		bban6.setValue( bankAccount.getBban6());
		bban6.setVisible(ibanMode);
		bban6.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban6(bban6.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(bban6);

		bban7.setStyleName(AON.AON_CSS.aonInputText());
		bban7.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban7.setMaxLength(4);
		bban7.setVisibleLength(4);
		bban7.setValue( bankAccount.getBban7());
		bban7.setVisible(ibanMode);
		bban7.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban7(bban7.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(bban7);

		bban8.setStyleName(AON.AON_CSS.aonInputText());
		bban8.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban8.setMaxLength(2);
		bban8.setVisibleLength(2);
		bban8.setValue( bankAccount.getBban8());
		bban8.setVisible(ibanMode);
		bban8.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban8(bban6.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(BankAccountBox.this, bankAccount);
			}
		});
		dataPanel.add(bban8);
		
		FlowPanel iconPanel = new FlowPanel();
		iconPanel.setStyleName(AON.AON_CSS.aonNowrap());
		iconPanel.setStyleName(AON.AON_CSS.aonInline());
		okIcon = new InlineLabel();
		okIcon.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		okIcon.addStyleName(AON.AON_CSS.aonIconValidate());
		okIcon.addStyleName(AON.AON_CSS.aonMarginLeft5());
		iconPanel.add(okIcon);

		koIcon = new InlineLabel();
		koIcon.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		koIcon.addStyleName(AON.AON_CSS.aonIconIncorrect());
		koIcon.addStyleName(AON.AON_CSS.aonMarginLeft5());
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
			bankAccount.setCheck(bankAccount.calculateIbanControlDigit());
			check.setValue(bankAccount.getCheck());
			ccc0.setText( bankAccount.getCountry() +  AonStringUtils.defaultString(bankAccount.getCheck()));
		}
		boolean valid = bankAccount.isValidBankAccount();
		okIcon.setVisible(valid);
		koIcon.setVisible(!valid);
	}

	public BankAccount getValue() {
		return this.bankAccount;
	}
	
	public void setValue(BankAccount bankAccount) {
		countryBox.setValue(bankAccount==null?null:bankAccount.getCountry() );
		check.setValue(bankAccount==null?null:bankAccount.getCheck() );
		bban1.setValue( bankAccount==null?null:bankAccount.getBban1() );
		bban2.setValue( bankAccount==null?null:bankAccount.getBban2() );
		bban3.setValue( bankAccount==null?null:bankAccount.getBban3() );
		bban4.setValue( bankAccount==null?null:bankAccount.getBban4() );
		bban5.setValue( bankAccount==null?null:bankAccount.getBban5() );
		bban6.setValue( bankAccount==null?null:bankAccount.getBban6() );
		bban7.setValue( bankAccount==null?null:bankAccount.getBban7() );
		bban8.setValue( bankAccount==null?null:bankAccount.getBban8() );
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
}
