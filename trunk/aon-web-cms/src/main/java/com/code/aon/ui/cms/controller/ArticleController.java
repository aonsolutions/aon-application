package com.code.aon.ui.cms.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class ArticleController extends BasicI18nController {

	private String title;

	private String subtitle;

	private Date dateFromPublish;
	
	private Date dateToPublish;

	private Date dateFromExpire;
	
	private Date dateToExpire;

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
		ArticleRelatedController c = (ArticleRelatedController)FormUtil.getController("articleRelated");
		IManagerBean moBean = BeanManager.getManagerBean(ArticleRelated.class);
		Article article = (Article) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.ARTICLE_RELATED_ARTICLE_PARENT_ID), "" + article.getId());
		c.setCurrentArticle(article);
		c.setCriteria(criteria);
		c.onSearch(event);
	}

	public void onSelectArticleDocuments(ActionEvent event) throws ManagerBeanException, ExpressionException {
		ArticleDocumentController c = (ArticleDocumentController)FormUtil.getController("articleDocument");
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
	
	@Override
	public void onEditSearch(ActionEvent event) {
		setTitle(null);
		setSubtitle(null);
		setDateFromPublish(null);
		setDateToPublish(null);
		setDateFromExpire(null);
		setDateToExpire(null);
		super.onEditSearch(event);
	}

	public void completeDetailCriteria(String alias_value, String value) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ArticleDetail.class);
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(bean.getFieldName(alias_value), value);
			List objects = (List<ITransferObject>)bean.getList(criteria);
			String alias = getFieldName(ICMSAlias.ARTICLE_ID);
			Expression expr = null;
			for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
				if (expr==null)
					expr = ExpressionUtilities.getExpression(((ArticleDetail) iterator.next()).getArticle().getId().toString(),alias);
				else
					expr = ExpressionUtilities.getOrExpression(expr, ExpressionUtilities.getExpression(((ArticleDetail) iterator.next()).getArticle().getId().toString(),alias));
			}
			if (expr != null)
				getCriteria().addExpression(expr);
			else
				getCriteria().addExpression(alias, "-1");
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
	}

	public void completeCriteria() throws ManagerBeanException {
		if (getTitle() != null && getTitle().length()>0) {
			completeDetailCriteria(ICMSAlias.ARTICLE_DETAIL_TITLE, getTitle());
		}
		if (getSubtitle() != null && getSubtitle().length()>0) {
			completeDetailCriteria(ICMSAlias.ARTICLE_DETAIL_SUBTITLE, getSubtitle());
		}
		if (getDateFromPublish() != null) {
			String alias = getFieldName(ICMSAlias.ARTICLE_PUBLISH_DATE); 
			getCriteria().addGreaterThanOrEqualExpression(alias, getDateFromPublish());
		}
		if (getDateToPublish() != null) {
			String alias = getFieldName(ICMSAlias.ARTICLE_PUBLISH_DATE);
			getCriteria().addLessThanOrEqualExpression(alias, getDateToPublish());
		}
		if (getDateFromExpire() != null) {
			String alias = getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE); 
			getCriteria().addGreaterThanOrEqualExpression(alias, getDateFromExpire());
		}
		if (getDateToExpire() != null) {
			String alias = getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE);
			getCriteria().addLessThanOrEqualExpression(alias, getDateToExpire());
		}
	}

	// -------------------------------------------------
	// Getters y setters para los campos de la búsqueda.
	// -------------------------------------------------
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public Date getDateFromPublish() {
		return dateFromPublish;
	}

	public void setDateFromPublish(Date dateFromPublish) {
		this.dateFromPublish = dateFromPublish;
	}

	public Date getDateToPublish() {
		return dateToPublish;
	}

	public void setDateToPublish(Date dateToPublish) {
		this.dateToPublish = dateToPublish;
	}

	public Date getDateFromExpire() {
		return dateFromExpire;
	}

	public void setDateFromExpire(Date dateFromExpire) {
		this.dateFromExpire = dateFromExpire;
	}

	public Date getDateToExpire() {
		return dateToExpire;
	}

	public void setDateToExpire(Date dateToExpire) {
		this.dateToExpire = dateToExpire;
	}
	
}