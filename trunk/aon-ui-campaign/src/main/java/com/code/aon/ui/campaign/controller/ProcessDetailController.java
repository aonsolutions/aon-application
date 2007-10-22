package com.code.aon.ui.campaign.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.campaign.ProcessDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class ProcessDetailController extends LinesController {

	@SuppressWarnings("unchecked")
	private void moveMenuOption( ProcessDetail processDetail, int movement ) throws ManagerBeanException {
		int oldPosition = processDetail.getPosition();
		int newPosition = oldPosition + movement;
		processDetail.setPosition( newPosition );
		getManagerBean().update( processDetail );
    	List<ProcessDetail> list = (List<ProcessDetail>) getModel().getWrappedData();
    	ProcessDetail movedMenuOption = list.get( newPosition );
		movedMenuOption.setPosition( oldPosition );
		getManagerBean().update( movedMenuOption );
		list.set( newPosition, processDetail );
		list.set( oldPosition, movedMenuOption );
	}
	
	@SuppressWarnings("unused")
    public void onMoveUp(ActionEvent event) throws ManagerBeanException {
    	moveMenuOption( (ProcessDetail) getSelectedTO(), -1);
    }

	@SuppressWarnings("unused")
    public void onMoveDown(ActionEvent event) throws ManagerBeanException {
    	moveMenuOption( (ProcessDetail) getSelectedTO(), 1);    	
    }
	
	@SuppressWarnings("unchecked")
	public void onWorkGroupChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_ID), event.getNewValue());
			Iterator iter = workGroupBean.getList(criteria).iterator();
			if(iter.hasNext()){
				WorkGroup workGroup = (WorkGroup)iter.next();
				((ProcessDetail)this.getTo()).setWorkgroup(workGroup);
			}
		}
	}
}