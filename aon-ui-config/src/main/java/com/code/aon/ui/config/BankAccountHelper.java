package com.code.aon.ui.config;

import java.io.Serializable;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Country;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IBankAccountContainerProvider;
import com.code.aon.config.util.BankUtil;
import com.code.aon.ui.config.event.BankAccountValidationListener;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class BankAccountHelper implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private IBankAccountContainerProvider bankAccountProvider;
	
	private IController controller;
	
	private boolean showNewBankAccountWindow;
	
	private BankAccount saveBankAccount;
	
	private String checkDigits;
	
	private String accountNumber;
	
	public BankAccountHelper( IBankAccountContainerProvider bankAccountProvider ) {
		this.bankAccountProvider = bankAccountProvider;
	}
	
	public BankAccountHelper(IController controller) {
		this.controller = controller;
	}
	
	public IBankAccountContainer getTo() {
		if ( bankAccountProvider != null ) {
			return this.bankAccountProvider.getBankAccountContainer();	
		}
		return (IBankAccountContainer) controller.getTo();
	}
	
	private BankAccount getBankAccount() {
		return getTo().getBankAccount();		
	}
	
	public void onBankAccountData(ActionEvent event) {
		BankUtil.fillBankAccountData(getTo());
	}

	public void onBankAccountData2(ActionEvent event) {
		IBankAccountContainer bac = getTo();
		if ( (bac.getBankAccount().getCountry() != null) &&
				(bac.getBankAccount().getBankCodeLength() > 4) ) {
			BankUtil.fillBankAccountData(bac);		
		}
	}
	
	public void onShowBankAccountWindow( ActionEvent event ) {
		setShowNewBankAccountWindow(true);
		BankAccount account = getBankAccount();
		if ( account != null ) {
			setCheckDigits(StringUtils.substring(account.getBban3(), 0, 2));
			StringBuffer number = new StringBuffer(StringUtils.substring(account.getBban3(), 2));
			if (StringUtils.isNotBlank(account.getBban4())) {
				number.append(account.getBban4()); 
				if (StringUtils.isNotBlank(account.getBban5())) {
					number.append(account.getBban5());
				}		
			}
			setAccountNumber(number.toString());			
		} else {
			setCheckDigits(null);
			setAccountNumber(null);
		}
		backupBankAccount();
	}
	
	private void backupBankAccount() {
		saveBankAccount = new BankAccount();
		BankAccount account = getBankAccount();
		if ( account != null ) {
			saveBankAccount.setCheck(account.getCheck());
			saveBankAccount.setBban1(account.getBban1());
			saveBankAccount.setBban2(account.getBban2());
			saveBankAccount.setBban3(account.getBban3());
			saveBankAccount.setBban4(account.getBban4());
			saveBankAccount.setBban5(account.getBban5());					
		}
	}

	private void restoreBankAccount() {
		BankAccount account = getBankAccount();
		account.setCheck(saveBankAccount.getCheck());
		account.setBban1(saveBankAccount.getBban1());
		account.setBban2(saveBankAccount.getBban2());
		account.setBban3(saveBankAccount.getBban3());
		account.setBban4(saveBankAccount.getBban4());
		account.setBban5(saveBankAccount.getBban5());		
	}
	
	public void onSaveCCC( ActionEvent event ) throws ControllerListenerException {
		BankAccount account = getBankAccount();
		account.setCountry(Country.ES);
		String bban3 = checkDigits + StringUtils.substring(accountNumber, 0, 2);
		account.setBban3(bban3);
		account.setBban4(StringUtils.substring(accountNumber, 2, 6));
		account.setBban5(StringUtils.substring(accountNumber, 6));
		account.setBban6(StringUtils.EMPTY);
		account.setBban7(StringUtils.EMPTY);
		account.setBban8(StringUtils.EMPTY);
		String check = account.calculateIbanControlDigit();
		account.setCheck(check);
		try {
			BankAccountValidationListener.checkBankAccount(getTo(), false);	
		} catch ( ControllerListenerException e ) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);			
		}
		if ( account.isValidBankAccount() ) {
			BankUtil.fillBankAccountData(getTo());
			setShowNewBankAccountWindow(false);	
		}
	}
	
	public void onCancelCCC( ActionEvent event ) {
		restoreBankAccount();
		setShowNewBankAccountWindow(false);
	}
	
	public String getCheckDigits() {
		return checkDigits;
	}

	public void setCheckDigits(String checkDigits) {
		this.checkDigits = checkDigits;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public boolean isShowNewBankAccountWindow() {
		return showNewBankAccountWindow;
	}

	public void setShowNewBankAccountWindow(boolean showNewBankAccountWindow) {
		this.showNewBankAccountWindow = showNewBankAccountWindow;
	}
	
}
