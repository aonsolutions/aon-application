package com.code.aon.ui.cms.controller;

import java.util.List;

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
import com.code.aon.ui.cms.util.FileUtil;
import com.code.aon.ui.cms.velocity.ArticleGenerator;
import com.code.aon.ui.util.AonUtil;

public class ArticleController extends GridI18nController {

	private ArticleCategory currentArticleCategory;
	
	private boolean richTextEnabled = false;

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
    	move((Article) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
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
	
	// TAB
	private ArticleType currentType = ArticleType.SERVICES;

	public ArticleType getCurrentType() {
		return currentType;
	}
	
	public boolean isEventType() {
		return (currentType == ArticleType.EVENTS)?true:false;
	}
	
	public String getTabID0(){
		return ArticleType.SERVICES.getName();
	}

	public String getTabID1(){
		return ArticleType.EVENTS.getName();
	}

	public String getTabID2(){
		return ArticleType.NEWS.getName();
	}

	public String getTabID3(){
		return ArticleType.OTHER.getName();
	}

	public void iniTab() throws ManagerBeanException, ExpressionException{
		currentType = ArticleType.SERVICES;
		changeArticleList();
	}
	
	private void changeArticleList() throws ManagerBeanException, ExpressionException {
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ARTICLE_ARTICLE_CATEGORY_ID), "" + getCurrentArticleCategory().getId());
		criteria.addEqualExpression(getManagerBean().getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), currentType);
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.ARTICLE_POSITION));
		setCriteria(criteria);
		onSearch(null);
		clearCheckList();
	}
	
	public void processTabChange(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		if ((""+event.getNewValue()).equals(ArticleType.SERVICES.getName())){
			currentType = ArticleType.SERVICES;
		}else if((""+event.getNewValue()).equals(ArticleType.EVENTS.getName())){
			currentType = ArticleType.EVENTS;
		}else if((""+event.getNewValue()).equals(ArticleType.NEWS.getName())){
			currentType = ArticleType.NEWS;
		}else{
			currentType = ArticleType.OTHER;
		} 
		changeArticleList();
	}

	public void onGenerateCurrentArticle(ActionEvent event) throws ManagerBeanException, ExpressionException {
		GeneratorStatusController status = (GeneratorStatusController)AonUtil.getRegisteredBean("generator_status");
		status.onInit(event);
		//Copy css and js files from current template
		FileUtil.copyDir(ControllerUtil.getCssTemplatePath(), ControllerUtil.getPreviewPath());
		FileUtil.copyDir(ControllerUtil.getJsTemplatePath(), ControllerUtil.getPreviewPath());
		
		ArticleGenerator.generateArticle((Article) this.getTo());
		
		status.finalized();
	}

}