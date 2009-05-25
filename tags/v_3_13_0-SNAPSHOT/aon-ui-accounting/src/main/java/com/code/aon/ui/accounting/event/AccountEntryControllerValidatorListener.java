package com.code.aon.ui.accounting.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountEntryControllerValidatorListener extends ControllerAdapter {

    private static final Logger LOGGER = Logger.getLogger(AccountEntryControllerValidatorListener.class.getName());
    
    @Override
    public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
        //executeAccountEntryValidations((AccountEntry)event.getController().getTo());
    }

    @Override
    public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        //executeAccountEntryValidations((AccountEntry)event.getController().getTo());
    }

    private void executeAccountEntryValidations(AccountEntry entry) throws ControllerListenerException {
        if (!validateEntryDateInPeriod(entry)) {
            throw new ControllerListenerException("La Fecha del Asiento no está dentro del Periodo");
        }
    }

    private boolean validateEntryDateInPeriod(AccountEntry entry) {
        try {
            IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(periodBean.getFieldName(IAccountingAlias.PERIOD_ID), entry.getAccountPeriod());
            Period period = (Period)periodBean.getList(criteria).get(0);
            return (entry.getEntryDate().compareTo(period.getInitiationDate()) >= 0 && entry.getEntryDate().compareTo(period.getDeadline()) <= 0);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining Period from Account Entry", e);
        }
        return false;
        //return true;
    }
}
