package com.esferalia.aon.gwt.common.client.widget.solutions;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Country2ListBox;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class AonBankAccountBox extends SimplePanel implements HasValueChangeHandlers<BankAccount>, HasKeyUpHandlers, HasEnabled {
	
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
	
	public AonBankAccountBox() {
		this(null);
	}
	
	public AonBankAccountBox(BankAccount ba) {
		setValue(ba);
		
		FlowPanel dataPanel = new FlowPanel();
		dataPanel.setStyleName(AON.CSS.aonDisplayGridCellInner());
		dataPanel.addStyleName(AON.CSS.aonNowrap());
		
		this.setWidget(dataPanel);
		
		InlineLabel ibanLabel = new InlineLabel(ibanMode?"IBAN:":"CCC:");
		ibanLabel.setStyleName(AON.CSS.aonInnerLabel());
		ibanLabel.addStyleName(AON.CSS.aonTextUnderline());
		ibanLabel.addStyleName(AON.CSS.aonClickable());
		ibanLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ibanMode = !ibanMode;
				ibanLabel.setText(ibanMode?"IBAN:":"CCC:");
				if (!ibanMode) {
					AonBankAccountBox.this.bankAccount.setCountry(Country.ES);
				}
				enableWidgets();
				validate();
			}
		});
		dataPanel.add(ibanLabel);

		
		countryBox.setValue(this.bankAccount.getCountry());
		countryBox.setVisible(ibanMode);
		countryBox.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				AonBankAccountBox.this.bankAccount.setCountry(countryBox.getValue());
				enableWidgets();
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(countryBox);
		
		enableWidgets();

		check.setStyleName(AON.CSS.aonInputText());
		check.addStyleName(AON.CSS.aonMarginLeftSep());
		check.setMaxLength(2);
		check.setVisibleLength(2);
		check.setValue( this.bankAccount.getCheck());
		check.setVisible(ibanMode);
		check.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setCheck(check.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(check);

		ccc0.setStyleName(AON.CSS.aonBold());
		ccc0.setVisible(!ibanMode);
		dataPanel.add(ccc0);
		
		ccc1.setStyleName(AON.CSS.aonInputText());
		ccc1.addStyleName(AON.CSS.aonMarginLeftSep());
		ccc1.setMaxLength(4);
		ccc1.setVisibleLength(4);
		ccc1.setValue( this.bankAccount.getCCC1());
		ccc1.setVisible(!ibanMode);
		ccc1.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setCCC1(ccc1.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(ccc1);

		ccc2.setStyleName(AON.CSS.aonInputText());
		ccc2.addStyleName(AON.CSS.aonMarginLeftSep());
		ccc2.setMaxLength(4);
		ccc2.setVisibleLength(4);
		ccc2.setValue( this.bankAccount.getCCC2());
		ccc2.setVisible(!ibanMode);
		ccc2.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setCCC2(ccc2.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(ccc2);

		ccc3.setStyleName(AON.CSS.aonInputText());
		ccc3.addStyleName(AON.CSS.aonMarginLeftSep());
		ccc3.setMaxLength(2);
		ccc3.setVisibleLength(2);
		ccc3.setValue( this.bankAccount.getCCC3());
		ccc3.setVisible(!ibanMode);
		ccc3.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setCCC3(ccc3.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(ccc3);

		ccc4.setStyleName(AON.CSS.aonInputText());
		ccc4.addStyleName(AON.CSS.aonMarginLeftSep());
		ccc4.setMaxLength(10);
		ccc4.setVisibleLength(10);
		ccc4.setValue( this.bankAccount.getCCC4());
		ccc4.setVisible(!ibanMode);
		ccc4.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setCCC4(ccc4.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(ccc4);
//-------
		bban1.setStyleName(AON.CSS.aonInputText());
		bban1.addStyleName(AON.CSS.aonMarginLeftSep());
		bban1.setMaxLength(4);
		bban1.setVisibleLength(4);
		bban1.setValue( this.bankAccount.getBban1());
		bban1.setVisible(ibanMode);
		bban1.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setBban1(bban1.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(bban1);

		bban2.setStyleName(AON.CSS.aonInputText());
		bban2.addStyleName(AON.CSS.aonMarginLeftSep());
		bban2.setMaxLength(4);
		bban2.setVisibleLength(4);
		bban2.setValue( this.bankAccount.getBban2());
		bban2.setVisible(ibanMode);
		bban2.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setBban2(bban2.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(bban2);

		bban3.setStyleName(AON.CSS.aonInputText());
		bban3.addStyleName(AON.CSS.aonMarginLeftSep());
		bban3.setMaxLength(4);
		bban3.setVisibleLength(4);
		bban3.setValue( this.bankAccount.getBban3());
		bban3.setVisible(ibanMode);
		bban3.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setBban3(bban3.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(bban3);

		bban4.setStyleName(AON.CSS.aonInputText());
		bban4.addStyleName(AON.CSS.aonMarginLeftSep());
		bban4.setMaxLength(4);
		bban4.setVisibleLength(4);
		bban4.setValue( this.bankAccount.getBban4());
		bban4.setVisible(ibanMode);
		bban4.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setBban4(bban4.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(bban4);

		bban5.setStyleName(AON.CSS.aonInputText());
		bban5.addStyleName(AON.CSS.aonMarginLeftSep());
		bban5.setMaxLength(4);
		bban5.setVisibleLength(4);
		bban5.setValue( this.bankAccount.getBban5());
		bban5.setVisible(ibanMode);
		bban5.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setBban5(bban5.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(bban5);

		bban6.setStyleName(AON.CSS.aonInputText());
		bban6.addStyleName(AON.CSS.aonMarginLeftSep());
		bban6.setMaxLength(4);
		bban6.setVisibleLength(4);
		bban6.setValue( this.bankAccount.getBban6());
		bban6.setVisible(ibanMode);
		bban6.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setBban6(bban6.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(bban6);

		bban7.setStyleName(AON.CSS.aonInputText());
		bban7.addStyleName(AON.CSS.aonMarginLeftSep());
		bban7.setMaxLength(4);
		bban7.setVisibleLength(4);
		bban7.setValue( this.bankAccount.getBban7());
		bban7.setVisible(ibanMode);
		bban7.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setBban7(bban7.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(bban7);

		bban8.setStyleName(AON.CSS.aonInputText());
		bban8.addStyleName(AON.CSS.aonMarginLeftSep());
		bban8.setMaxLength(2);
		bban8.setVisibleLength(2);
		bban8.setValue( this.bankAccount.getBban8());
		bban8.setVisible(ibanMode);
		bban8.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AonBankAccountBox.this.bankAccount.setBban8(bban6.getValue());
				validate();
				ValueChangeEvent.<BankAccount>fire(AonBankAccountBox.this, AonBankAccountBox.this.bankAccount);
			}
		});
		dataPanel.add(bban8);
		
		FlowPanel iconPanel = new FlowPanel();
		iconPanel.setStyleName(AON.CSS.aonNowrap());
		iconPanel.setStyleName(AON.CSS.aonInline());
		okIcon = new InlineLabel();
		okIcon.setStyleName(AON.CSS.aonIconLabel());
		okIcon.addStyleName(AON.CSS.aonIconValid());
		okIcon.addStyleName(AON.CSS.aonMarginLeftSep());
		iconPanel.add(okIcon);

		koIcon = new InlineLabel();
		koIcon.setStyleName(AON.CSS.aonIconLabel());
		koIcon.addStyleName(AON.CSS.aonIconInvalid());
		koIcon.addStyleName(AON.CSS.aonMarginLeftSep());
		iconPanel.add(koIcon);
		dataPanel.add(iconPanel);
		
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
		countryBox.setValue(this.bankAccount==null?null:this.bankAccount.getCountry() );
		check.setValue(this.bankAccount==null?null:this.bankAccount.getCheck() );
		bban1.setValue( this.bankAccount==null?null:this.bankAccount.getBban1() );
		bban2.setValue( this.bankAccount==null?null:this.bankAccount.getBban2() );
		bban3.setValue( this.bankAccount==null?null:this.bankAccount.getBban3() );
		bban4.setValue( this.bankAccount==null?null:this.bankAccount.getBban4() );
		bban5.setValue( this.bankAccount==null?null:this.bankAccount.getBban5() );
		bban6.setValue( this.bankAccount==null?null:this.bankAccount.getBban6() );
		bban7.setValue( this.bankAccount==null?null:this.bankAccount.getBban7() );
		ccc1.setValue( this.bankAccount==null?null:this.bankAccount.getCCC1() );
		ccc2.setValue( this.bankAccount==null?null:this.bankAccount.getCCC2() );
		ccc3.setValue( this.bankAccount==null?null:this.bankAccount.getCCC3() );
		ccc4.setValue( this.bankAccount==null?null:this.bankAccount.getCCC4() );
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
