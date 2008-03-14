package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Bulletin;
import com.code.aon.cms.BulletinArticle;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;


public class BulletinController extends BasicI18nController {

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(new ActionEvent(event.getComponent()));
		loadCurrentLanguage();
	}

	public void onSelectArticles(ActionEvent event) throws ManagerBeanException, ExpressionException {
		BulletinArticleController c = (BulletinArticleController)AonUtil.getController("bulletin_article");
		IManagerBean moBean = BeanManager.getManagerBean(BulletinArticle.class);
		Bulletin bulletin = (Bulletin) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.BULLETIN_ARTICLE_BULLETIN_ID), "" + bulletin.getId());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.BULLETIN_ARTICLE_POSITION));
		c.setCurrentBulletin(bulletin);
		c.setCriteria(criteria);
		c.onSearch(event);
	}


}