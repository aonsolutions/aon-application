package com.code.aon.ui.cms.event;

import com.code.aon.cms.ArticleDocument;
import com.code.aon.ui.cms.controller.ArticleDocumentController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ArticleDocumentControllerListener extends ControllerAdapter {

	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ArticleDocumentController c = (ArticleDocumentController)event.getController();
		ArticleDocument ar = (ArticleDocument)c.getTo();
		ar.setArticle(c.getCurrentArticle());
	}

}
