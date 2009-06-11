package com.code.aon.ui.accounting.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.accounting.BalanceDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;

public class BalanceDetailController extends LinesController {
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException {
    	moveMenuOption( (BalanceDetail) getSelectedTO(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException {
    	moveMenuOption( (BalanceDetail) getSelectedTO(), 1);    	
    }	
	
	@SuppressWarnings("unchecked")
	private void moveMenuOption( BalanceDetail balanceDetail, int movement ) throws ManagerBeanException {
		int oldPosition = balanceDetail.getSortKey();
		int newPosition = oldPosition + movement;
		balanceDetail.setSortKey( newPosition );
		getManagerBean().update( balanceDetail );
    	List<BalanceDetail> list = (List<BalanceDetail>) getModel().getWrappedData();
    	BalanceDetail movedMenuOption = list.get( newPosition );
		movedMenuOption.setSortKey( oldPosition );
		getManagerBean().update( movedMenuOption );
		list.set( newPosition, balanceDetail );
		list.set( oldPosition, movedMenuOption );
	}
}