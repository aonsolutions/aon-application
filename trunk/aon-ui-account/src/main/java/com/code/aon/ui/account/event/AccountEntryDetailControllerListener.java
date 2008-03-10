package com.code.aon.ui.account.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountEntryDetailControllerListener extends ControllerAdapter {

    @Override
    public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
    	event.getController().setModel(null);
    }
}