package com.code.aon.ui.groupware.event;

import java.util.Date;

import com.code.aon.config.User;
import com.code.aon.groupware.Note;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class NoteControllerListener extends ControllerAdapter {

    @Override
    public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
    	User user = UserUtils.getInstance().getLoggedUser();
    	if (user != null) {
    		Note note = (Note)event.getController().getTo();
        	note.setOwner(user);
        	note.setDate(new Date());
    	} else {
    		throw new ControllerListenerException("No user has been logged in");
    	}
    }

}