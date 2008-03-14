package com.code.aon.ui.cms.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Bulletin;
import com.code.aon.cms.BulletinArticle;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.GridController;

public class BulletinArticleController extends GridController {

	private Bulletin currentBulletin;
	
	public Bulletin getCurrentBulletin() {
		return currentBulletin;
	}

	public void setCurrentBulletin(Bulletin currentBulletin) {
		this.currentBulletin = currentBulletin;
	}

	@SuppressWarnings("unchecked")
	private void move( BulletinArticle f, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = f.getPosition();
		int newPosition = oldPosition + movement;
		f.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.BULLETIN_ARTICLE_ID), ""+f.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			BulletinArticle obj = (BulletinArticle)list.get(0);
			obj.setPosition(newPosition);
			getManagerBean().update(obj);
		}
    	List<BulletinArticle> listObjects = (List<BulletinArticle>) this.model.getWrappedData();
    	BulletinArticle fMoved = listObjects.get( newPosition );
		fMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.BULLETIN_ARTICLE_ID), ""+fMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			BulletinArticle obj = (BulletinArticle)list.get(0);
			obj.setPosition(oldPosition);
			getManagerBean().update(obj);
		}
		listObjects.set( newPosition, f);
		listObjects.set( oldPosition, fMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	move((BulletinArticle) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	move((BulletinArticle) this.model.getRowData(), 1);    	
    }

	public void reorderObjects() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.BULLETIN_ARTICLE_BULLETIN_ID), "" + getCurrentBulletin().getId());
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.BULLETIN_ARTICLE_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			BulletinArticle f = (BulletinArticle)list.get(i);
			int oldPosition = f.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				f.setPosition(newPosition);
				getManagerBean().update(f);
			}
		}
	}
	
	@Override
	public void initializeModel() {
		super.clearCheckList();
		try {
			reorderObjects();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		super.initializeModel();
	}
}