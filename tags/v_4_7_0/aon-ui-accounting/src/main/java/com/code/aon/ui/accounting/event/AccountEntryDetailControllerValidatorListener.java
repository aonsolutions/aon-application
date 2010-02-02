package com.code.aon.ui.accounting.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountEntryDetailControllerValidatorListener extends ControllerAdapter {

    @Override
    public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
        if (((AccountEntryDetail)event.getController().getTo()).getBalancingAccount() == null) {
            ((AccountEntryDetail)event.getController().getTo()).setBalancingAccount(new Account());
        }
    }

    @Override
    public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
        executeAccountEntryDetailValidations((AccountEntryDetail)event.getController().getTo());
    }

    @Override
    public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        executeAccountEntryDetailValidations((AccountEntryDetail)event.getController().getTo());
    }

    private void executeAccountEntryDetailValidations(AccountEntryDetail entryDetail) throws ControllerListenerException {
        if (!isAccountEmpty(entryDetail.getBalancingAccount()) && !validateDifferentAccountsInEntry(entryDetail)) {
            throw new ControllerListenerException("La Cuenta y la Contrapartida no pueden ser iguales.");
        }
        if (!validateAccountIsEntryEnabled(entryDetail.getAccount())) {
            throw new ControllerListenerException("La Cuenta " + entryDetail.getAccount().getId() + " no permite apuntes.");
        }
        if (!isAccountEmpty(entryDetail.getBalancingAccount()) && !validateAccountIsEntryEnabled(entryDetail.getBalancingAccount())) {
            throw new ControllerListenerException("La Cuenta " + entryDetail.getBalancingAccount().getId() + " no permite apuntes.");
        }
        if (!validateDebitAndCreditWithValue(entryDetail)) {
            throw new ControllerListenerException("Debe y Haber no pueden ser ambos diferentes de cero.");
        }

        if (isAccountEmpty(entryDetail.getBalancingAccount())) {
            entryDetail.setBalancingAccount(null);
        }
        
        if (entryDetail.getConcept() != null) {
        	if (entryDetail.getConcept().length() > 32) {
        		entryDetail.setConcept( StringUtils.abbreviate(entryDetail.getConcept(), 32) );
        	}
        }
    }

    private boolean isAccountEmpty(Account account) {
        return (account == null || account.getId() == null || account.getId().equals(""));
    }

    private boolean validateDifferentAccountsInEntry(AccountEntryDetail entryDetail) {
        return (!entryDetail.getAccount().getId().equals(entryDetail.getBalancingAccount().getId()));
    }

    private boolean validateAccountIsEntryEnabled(Account account) {
        return account.isEntryEnabled();
    }

    private boolean validateDebitAndCreditWithValue(AccountEntryDetail entryDetail) {
        return (entryDetail.getDebit() == 0 || entryDetail.getCredit() == 0);
    }
}
