package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleDocument;
import com.code.aon.cms.ArticleDocumentDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.faces.component.inputfile.InputFile;

public class ArticleDocumentController extends GridI18nController {

	private Article currentArticle;

	private boolean cancelOnSelect = false;

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			loadCurrentLanguage();
		}
		cancelOnSelect = false;
	}

	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true;
	}

	public Article getCurrentArticle() {
		return currentArticle;
	}

	public void setCurrentArticle(Article currentArticle) {
		this.currentArticle = currentArticle;
	}

	public ArticleDocumentDetail getCurrentRowDetail() throws ManagerBeanException{
		ArticleDocument ad = (ArticleDocument)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ARTICLE_DOCUMENT_DETAIL_ARTICLE_DOCUMENT_ID), ad.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ARTICLE_DOCUMENT_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			return (ArticleDocumentDetail)list.get(0);
		}
		return null;
	}
	
	public void onUpload(ActionEvent event){
		InputFile inputFile = (InputFile)event.getSource();
		((ArticleDocumentDetail)this.getToI18n()).setFile(inputFile.getFile().getName());
	}
	
	private String currentPath;

	public String getCurrentPath() {
		return ControllerUtil.getDocumentsPath()+"/article_documents";
	}
}
