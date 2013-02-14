package com.code.aon.ui.groupware.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.ProcessDetail;
import com.code.aon.ui.form.LinesController;

public class ProcessDetailController extends LinesController {

	@SuppressWarnings("unchecked")
	private void moveMenuOption( ProcessDetail processDetail, int movement ) throws ManagerBeanException {
		int oldPosition = processDetail.getPosition();
		int newPosition = oldPosition + movement;
		if (newPosition > 0) {
			List<ProcessDetail> list = (List<ProcessDetail>) getModel().getWrappedData();
			if (newPosition+1 <= list.size() ) {
				processDetail.setPosition( newPosition );
				getManagerBean().update( processDetail );
		    	ProcessDetail movedMenuOption = list.get( newPosition );
				movedMenuOption.setPosition( oldPosition );
				getManagerBean().update( movedMenuOption );
				list.set( newPosition, processDetail );
				list.set( oldPosition, movedMenuOption );
			}
		}
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException {
    	moveMenuOption( (ProcessDetail) getSelectedTO(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException {
    	moveMenuOption( (ProcessDetail) getSelectedTO(), 1);    	
    }
}