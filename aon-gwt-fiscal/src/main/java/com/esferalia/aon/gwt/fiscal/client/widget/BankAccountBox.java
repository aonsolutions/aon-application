package com.esferalia.aon.gwt.fiscal.client.widget;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Country2ListBox;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class BankAccountBox extends SimplePanel implements HasSelectionHandlers<BankAccount>, HasEnabled {
	
	public static class BankAccountBoxOptions {
		private BankAccount bankAccount;
		private boolean aliasEditable;
		private boolean BICEditable;
		
		
		public boolean isAliasEditable() {
			return aliasEditable;
		}
		public BankAccountBoxOptions setAliasEditable(boolean aliasEditable) {
			this.aliasEditable = aliasEditable;
			return this;
		}
		
		public boolean isBICEditable() {
			return BICEditable;
		}
		public BankAccountBoxOptions setBICEditable(boolean bICEditable) {
			this.BICEditable = bICEditable;
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
	private TextBox bic = new TextBox();
	private InlineLabel okIcon = new InlineLabel(); 
	private InlineLabel koIcon = new InlineLabel();
	
	private BankAccount bankAccount;
	
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
		
		InlineLabel ibanLabel = new InlineLabel("IBAN");
		ibanLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, ibanLabel);
		col++;
		
		FlowPanel dataPanel = new FlowPanel();
		dataPanel.setStyleName(AON.AON_CSS.aonNowrap());
		
		countryBox.addStyleName(AON.AON_CSS.aonMarginLeft5());
		countryBox.setValue(bankAccount.getCountry());
		countryBox.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				bankAccount.setCountry(countryBox.getValue());
				enableWidgets();
				validate();
			}
		});
		dataPanel.add(countryBox);
		
		enableWidgets();

		check.setStyleName(AON.AON_CSS.aonInputText());
		check.addStyleName(AON.AON_CSS.aonMarginLeft5());
		check.setMaxLength(2);
		check.setVisibleLength(2);
		check.setValue( bankAccount.getCheck());
		check.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setCheck(check.getValue());
				validate();
			}
		});
		dataPanel.add(check);

		bban1.setStyleName(AON.AON_CSS.aonInputText());
		bban1.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban1.setMaxLength(4);
		bban1.setVisibleLength(4);
		bban1.setValue( bankAccount.getBban1());
		bban1.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban1(bban1.getValue());
				validate();
			}
		});
		dataPanel.add(bban1);

		bban2.setStyleName(AON.AON_CSS.aonInputText());
		bban2.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban2.setMaxLength(4);
		bban2.setVisibleLength(4);
		bban2.setValue( bankAccount.getBban2());
		bban2.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban2(bban2.getValue());
				validate();
			}
		});
		dataPanel.add(bban2);

		bban3.setStyleName(AON.AON_CSS.aonInputText());
		bban3.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban3.setMaxLength(4);
		bban3.setVisibleLength(4);
		bban3.setValue( bankAccount.getBban3());
		bban3.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban3(bban3.getValue());
				validate();
			}
		});
		dataPanel.add(bban3);

		bban4.setStyleName(AON.AON_CSS.aonInputText());
		bban4.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban4.setMaxLength(4);
		bban4.setVisibleLength(4);
		bban4.setValue( bankAccount.getBban4());
		bban4.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban4(bban4.getValue());
				validate();
			}
		});
		dataPanel.add(bban4);

		bban5.setStyleName(AON.AON_CSS.aonInputText());
		bban5.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban5.setMaxLength(4);
		bban5.setVisibleLength(4);
		bban5.setValue( bankAccount.getBban5());
		bban5.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban5(bban5.getValue());
				validate();
			}
		});
		dataPanel.add(bban5);

		bban6.setStyleName(AON.AON_CSS.aonInputText());
		bban6.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban6.setMaxLength(4);
		bban6.setVisibleLength(4);
		bban6.setValue( bankAccount.getBban6());
		bban6.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban6(bban6.getValue());
				validate();
			}
		});
		dataPanel.add(bban6);

		bban7.setStyleName(AON.AON_CSS.aonInputText());
		bban7.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban7.setMaxLength(4);
		bban7.setVisibleLength(4);
		bban7.setValue( bankAccount.getBban7());
		bban7.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban7(bban7.getValue());
				validate();
			}
		});
		dataPanel.add(bban7);

		bban8.setStyleName(AON.AON_CSS.aonInputText());
		bban8.addStyleName(AON.AON_CSS.aonMarginLeft5());
		bban8.setMaxLength(2);
		bban8.setVisibleLength(2);
		bban8.setValue( bankAccount.getBban8());
		bban8.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				bankAccount.setBban8(bban6.getValue());
				validate();
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
		
//		if (options.isBICEditable()) {
		InlineLabel bicLabel = new InlineLabel("BIC");
		bicLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(row, col, bicLabel);
		col++;
		
		bic.setStyleName(AON.AON_CSS.aonInputText());
		bic.addStyleName(AON.AON_CSS.aonMarginLeft());
		bic.setMaxLength(11);
		bic.setVisibleLength(10);
		tab.setWidget(row, col, bic);
		tab.getFlexCellFormatter().setColSpan(row, col, 4);
//		}
		
		validate();
	}
	
	protected void enableWidgets() {
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
	}

	protected void validate() {
		boolean valid = bankAccount.isValidBankAccount();
		okIcon.setVisible(valid);
		koIcon.setVisible(!valid);
	}

	public BankAccount getValue() {
		return this.bankAccount;
	}
	
	public String getBic() {
		return bic.getValue();
	}
	
	public void setValue(BankAccount bankAccount, String bic2 ) {
		setValue(bankAccount);
		bic.setValue( bic2 );		
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
		bban1.setEnabled(enabled);
		bban2.setEnabled(enabled);
		bban3.setEnabled(enabled);
		bban4.setEnabled(enabled);
		bban5.setEnabled(enabled);
		bban6.setEnabled(enabled);
		bban7.setEnabled(enabled);
		bban8.setEnabled(enabled);
		bic.setEnabled(enabled);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<BankAccount> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
