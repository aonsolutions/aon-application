package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleDetail;
import com.code.aon.cms.ArticleDocument;
import com.code.aon.cms.ArticleRelated;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;

public class ArticleController extends BasicI18nController {

	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	private boolean richTextEnabled = false;

	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event){
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		Article f = (Article)this.model.getRowData();
		f.setActive(active);
		getManagerBean().update(f);
	}
	
	public String getI18nTitle() throws ManagerBeanException {
		String label = "- NO VALUE -";
		ArticleDetail fd = (ArticleDetail)getModelRowdataI18n();
		if (fd != null) label = fd.getTitle();
		return label;
	}

	public String getI18nSubtitle() throws ManagerBeanException {
		String label = "- NO VALUE -";
		ArticleDetail fd = (ArticleDetail)getModelRowdataI18n();
		if (fd != null) label = fd.getSubtitle();
		return label;
	}

	public String getI18nContent() throws ManagerBeanException {
		String label = "- NO VALUE -";
		ArticleDetail fd = (ArticleDetail)getModelRowdataI18n();
		if (fd != null) label = fd.getContent();
		return label;
	}

	public void onDelImage(ActionEvent event) {
		Article current = (Article)getTo();
		current.setImage(null);
	}
	
	public void onDelThumbnail(ActionEvent event) {
		Article current = (Article)getTo();
		current.setThumbnail(null);
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		Article current = (Article)getTo();
		current.setImage(image);
	}

	public void onSelectThumbnail(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		Article current = (Article)getTo();
		current.setThumbnail(image);
	}

	public void onSelectRelatedArticles(ActionEvent event) throws ManagerBeanException, ExpressionException {
		ArticleRelatedController c = (ArticleRelatedController)AonUtil.getController("articleRelated");
		IManagerBean moBean = BeanManager.getManagerBean(ArticleRelated.class);
		Article article = (Article) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.ARTICLE_RELATED_ARTICLE_PARENT_ID), "" + article.getId());
		c.setCurrentArticle(article);
		c.setCriteria(criteria);
		c.onSearch(event);
	}

	public void onSelectArticleDocuments(ActionEvent event) throws ManagerBeanException, ExpressionException {
		ArticleDocumentController c = (ArticleDocumentController)AonUtil.getController("articleDocument");
		IManagerBean moBean = BeanManager.getManagerBean(ArticleDocument.class);
		Article article = (Article) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.ARTICLE_DOCUMENT_ARTICLE_ID), "" + article.getId());
		c.setCurrentArticle(article);
		c.setCriteria(criteria);
		c.onSearch(event);
	}
	
	public boolean isDiary() {
		try{
			return (((Article)this.getTo()).getArticleType() == ArticleType.EVENTS)?true:false;
		}catch (Exception e) {
			return false;
		}
	}
	
}