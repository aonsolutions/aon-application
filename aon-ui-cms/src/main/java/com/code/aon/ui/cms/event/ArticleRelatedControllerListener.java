package com.code.aon.ui.cms.event;

import com.code.aon.cms.ArticleRelated;
import com.code.aon.ui.cms.controller.ArticleRelatedController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ArticleRelatedControllerListener extends ControllerAdapter {

	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ArticleRelatedController c = (ArticleRelatedController)event.getController();
		ArticleRelated ar = (ArticleRelated)event.getController().getTo();
		ar.setArticleParent(c.getCurrentArticle());
	}
	
}
