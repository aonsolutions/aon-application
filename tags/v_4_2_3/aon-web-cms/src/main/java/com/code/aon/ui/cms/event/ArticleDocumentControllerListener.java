package com.code.aon.ui.cms.event;

import com.code.aon.cms.ArticleDocument;
import com.code.aon.cms.ArticleDocumentDetail;
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

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ArticleDocumentController c = (ArticleDocumentController)event.getController();
		ArticleDocument ar = (ArticleDocument)c.getTo();
		ArticleDocumentDetail ard = (ArticleDocumentDetail)c.getToI18n();
		if (ar.getAlias()==null || ar.getAlias().trim().equals("")){
			ar.setAlias(ard.getFile());
		}
		if (ard.getTitle()==null || ard.getTitle().trim().equals("")){
			ard.setTitle(ard.getFile());
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		ArticleDocumentController c = (ArticleDocumentController)event.getController();
		ArticleDocument ar = (ArticleDocument)c.getTo();
		ArticleDocumentDetail ard = (ArticleDocumentDetail)c.getToI18n();
		if (ar.getAlias()==null || ar.getAlias().trim().equals("")){
			ar.setAlias(ard.getFile());
		}
		if (ard.getTitle()==null || ard.getTitle().trim().equals("")){
			ard.setTitle(ard.getFile());
		}
	}
}
