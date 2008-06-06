package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Bulletin;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.form.GridController;

public class BulletinArticleController extends GridController{

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.BULLETIN_ARTICLE_POSITION);

	private Bulletin currentBulletin;
	
	public Bulletin getCurrentBulletin() {
		return currentBulletin;
	}

	public void setCurrentBulletin(Bulletin currentBulletin) {
		this.currentBulletin = currentBulletin;
	}

	@Override
	public void initializeModel() {
		super.clearCheckList();
		super.initializeModel();
	}

    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveUp(this);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveDown(this);
    }

	protected void afterRemoveSelected(){
		try {
			orderedControllerSupport.reorderObjects(this);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

}