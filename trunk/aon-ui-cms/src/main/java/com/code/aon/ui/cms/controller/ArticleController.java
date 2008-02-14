package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
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
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;


public class ArticleController extends GridI18nController {

	private boolean cancelOnSelect = false;

	private ArticleCategory currentArticleCategory;
	
	private boolean richTextEnabled = true;

	public void onInit(ActionEvent event){
		((GalleryController)AonUtil.getRegisteredBean("gallery")).onInit(event);
	}

	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}

	public ArticleCategory getCurrentArticleCategory() {
		return currentArticleCategory;
	}

	public void setCurrentArticleCategory(ArticleCategory currentArticleCategory) {
		this.currentArticleCategory = currentArticleCategory;
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException, ExpressionException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "article_form");
			loadCurrentLanguage();
		}
		cancelOnSelect = false;
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		Article f = (Article)this.model.getRowData();
		f.setActive(active);
		getManagerBean().update(f);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nTitle() throws ManagerBeanException {
		String label = "";
		ArticleDetail fd = getCurrentDetail();
		if (fd != null) label = fd.getTitle();
		return label;
	}

	public String getI18nSubtitle() throws ManagerBeanException {
		String label = "";
		ArticleDetail fd = getCurrentDetail();
		if (fd != null) label = fd.getSubtitle();
		return label;
	}

	public String getI18nContent() throws ManagerBeanException {
		String label = "";
		ArticleDetail fd = getCurrentDetail();
		if (fd != null) label = fd.getContent();
		return label;
	}

	private ArticleDetail getCurrentDetail() throws ManagerBeanException {
		ArticleDetail fd = null;
		Article f = (Article)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), f.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			fd = (ArticleDetail)list.get(0);
		}
		return fd;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	@SuppressWarnings("unchecked")
	private void move( Article f, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = f.getPosition();
		int newPosition = oldPosition + movement;
		f.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ARTICLE_ID), ""+f.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			Article faq = (Article)list.get(0);
			faq.setPosition(newPosition);
			getManagerBean().update(faq);
		}
    	List<Article> listObjects = (List<Article>) this.model.getWrappedData();
		Article fMoved = listObjects.get( newPosition );
		fMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ARTICLE_ID), ""+fMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			Article faq = (Article)list.get(0);
			faq.setPosition(oldPosition);
			getManagerBean().update(faq);
		}
		listObjects.set( newPosition, f);
		listObjects.set( oldPosition, fMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((Article) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((Article) this.model.getRowData(), 1);    	
    }

	public void reorderObjects() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ARTICLE_ARTICLE_CATEGORY_ID), "" + getCurrentArticleCategory().getId());
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
		criteria.addEqualExpression(getManagerBean().getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), currentType);
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.ARTICLE_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Article f = (Article)list.get(i);
			int oldPosition = f.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				f.setPosition(newPosition);
				getManagerBean().update(f);
			}
		}
	}

	private boolean imageSelectionVisible;
	private int type_;
	
	public void onShowImages(ActionEvent event) {
		imageSelectionVisible = true;
		type_ = 0;
	}
	
	public void onCloseImages(ActionEvent event) {
		imageSelectionVisible = false; 
	}
	
	public void onShowThumbnail(ActionEvent event) {
		imageSelectionVisible = true; 
		type_ = 1;
	}
	
	public void onCloseThumbnail(ActionEvent event) {
		imageSelectionVisible = false; 
	}
	
	public void onDelImage(ActionEvent event) {
		Article current = (Article)getTo();
		current.setImage(null);
	}
	
	public void onDelThumbnail(ActionEvent event) {
		Article current = (Article)getTo();
		current.setThumbnail(null);
	}
	
	public boolean isImageSelectionVisible(){
		return imageSelectionVisible && type_== 0;
	}

	public boolean isThumbnailSelectionVisible(){
		return imageSelectionVisible && type_== 1;
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		Article current = (Article)getTo();
		if (type_ ==0){
			current.setImage(image);
		}else{
			current.setThumbnail(image);
		}
	}

	public void onSelectRelatedArticles(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true;
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
		cancelOnSelect = true;
		ArticleDocumentController c = (ArticleDocumentController)AonUtil.getController("articleDocument");
		IManagerBean moBean = BeanManager.getManagerBean(ArticleDocument.class);
		Article article = (Article) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.ARTICLE_DOCUMENT_ARTICLE_ID), "" + article.getId());
		c.setCurrentArticle(article);
		c.setCriteria(criteria);
		c.onSearch(event);
	}
	
	// TAB
	private ArticleType currentType = ArticleType.SERVICES;

	public ArticleType getCurrentType() {
		return currentType;
	}

	public int getCurrentTab() {
		if (currentType == ArticleType.SERVICES) return 0;
		else if (currentType == ArticleType.EVENTS) return 1;
		else if (currentType == ArticleType.NEWS) return 2;
		else if (currentType == ArticleType.OTHER) return 3;
		return 0;
	}

	private void changeArticleList() throws ManagerBeanException, ExpressionException {
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ARTICLE_ARTICLE_CATEGORY_ID), "" + getCurrentArticleCategory().getId());
		criteria.addEqualExpression(getManagerBean().getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), currentType);
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.ARTICLE_POSITION));
		setCriteria(criteria);
		onSearch(null);
		onInit(null);
		clearCheckList();
	}

	public void processTabChange(TabChangeEvent event) throws AbortProcessingException, ManagerBeanException, ExpressionException {
		switch (event.getNewTabIndex()) {
		case 0:
			currentType = ArticleType.SERVICES;
			break;
		case 1:
			currentType = ArticleType.EVENTS;
			break;
		case 2:
			currentType = ArticleType.NEWS;
			break;
		case 3:
			currentType = ArticleType.OTHER;
			break;
		default:
			break;
		}
		changeArticleList();
	}


}