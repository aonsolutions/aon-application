package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.BulletinArticle;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.BulletinArticleController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class BulletinArticleControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		BulletinArticleController fc = (BulletinArticleController)event.getController();
		BulletinArticle to = (BulletinArticle)event.getController().getTo();
		to.setBulletin(fc.getCurrentBulletin());
		to.setPosition(getLastPosition(fc));
	}

	private int getLastPosition(BulletinArticleController fc) {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addExpression(fc.getManagerBean().getFieldName(ICMSAlias.BULLETIN_ARTICLE_BULLETIN_ID), "" + fc.getCurrentBulletin().getId());
			criteria.addOrder(fc.getManagerBean().getFieldName(ICMSAlias.BULLETIN_ARTICLE_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)fc.getManagerBean().getList(criteria);
			if (list.size() > 0) {
				BulletinArticle f = (BulletinArticle)list.get(0);
				position = f.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
		return position;
	}

}
