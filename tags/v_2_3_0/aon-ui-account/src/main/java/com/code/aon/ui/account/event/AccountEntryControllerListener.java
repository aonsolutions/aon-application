package com.code.aon.ui.account.event;

import com.code.aon.account.AccountEntry;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountEntryControllerListener extends ControllerAdapter {

    private int index;
    

    @Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
        AccountEntry to = (AccountEntry)event.getController().getTo();
        to.setType(AccountEntryType.MANUAL);
    }

    @Override
    public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
        try {
            index = event.getController().getModel().getRowIndex();
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e);
        }
    }

    @Override
    public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
        try {
            int rowCount = event.getController().getModel().getRowCount();
            if (index > (rowCount-1)) {
                index = rowCount - 1;
            }
            if (rowCount > 0) {
                event.getController().getModel().setRowIndex(index);
                event.getController().onSelect(null);
            }
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e);
        }
    }

    @Override
    public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
        try {
            Integer id = ((AccountEntry)event.getController().getTo()).getId();
            IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(entryBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ID), id);
            event.getController().setCriteria(criteria);
            event.getController().onSearch(null);
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e);
        }
    }
}
