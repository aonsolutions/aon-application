package com.code.aon.ui.groupware.event;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.groupware.Note;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.NoteController;

public class NoteControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(NoteControllerListener.class.getName());

    @Override
    public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
    	NoteController noteController = (NoteController)event.getController();
        try {
        	User user = UserUtils.getLoggedUser();

            if(user!=null){
            	noteController.getCriteria().addEqualExpression(noteController.getFieldName(IGroupWareAlias.NOTE_OWNER_ID), user.getId());
            }else{
            	throw new ControllerListenerException("No user has been logged in");
            }
            noteController.getCriteria().addOrder(noteController.getManagerBean().getFieldName(IGroupWareAlias.NOTE_DATE));
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error initializing Note Model", e);
        }
    }
    
    @Override
    public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
    	User user = UserUtils.getLoggedUser();
    	if(user != null){
    		Note note = (Note)event.getController().getTo();
        	note.setOwner(user);
        	note.setDate(new Date());
    	}else{
    		throw new ControllerListenerException("No user has been logged in");
    	}
    }
    
    @Override
    public void beforeBeanReset(ControllerEvent event) throws ControllerListenerException {
    	NoteController noteController = (NoteController)event.getController();
    	try {
    		Criteria criteria = noteController.getCriteria();
    		if(noteController.getFromDate() != null){
    			criteria.addGreaterThanOrEqualExpression(noteController.getFieldName(IGroupWareAlias.NOTE_DATE),noteController.getFromDate());
    		}
    		if(noteController.getToDate() != null){
    			criteria.addLessThanOrEqualExpression(noteController.getFieldName(IGroupWareAlias.NOTE_DATE),noteController.getToDate());
    		}
    		noteController.setCriteria(criteria);
    	} catch (ManagerBeanException e) {
    		throw new ControllerListenerException("Error creating criteria",e);
    	}
    }
}