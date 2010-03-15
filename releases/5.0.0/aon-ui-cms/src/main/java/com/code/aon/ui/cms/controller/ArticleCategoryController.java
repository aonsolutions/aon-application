package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class ArticleCategoryController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "article_category_form");
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
		ArticleCategory articleCategory = (ArticleCategory)this.model.getRowData();
		articleCategory.setActive(active);
		getManagerBean().update(articleCategory);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "";
		ArticleCategoryDetail articleCategoryDetail = getCurrentDetail();
		if (articleCategoryDetail != null) label = articleCategoryDetail.getLabel();
		return label;
	}

	private ArticleCategoryDetail getCurrentDetail() throws ManagerBeanException {
		ArticleCategoryDetail articleCategoryDetail = null;
		ArticleCategory articleCategory = (ArticleCategory)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_ARTICLE_CATEGORY_ID), articleCategory.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			articleCategoryDetail = (ArticleCategoryDetail)list.get(0);
		}
		return articleCategoryDetail;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	@SuppressWarnings("unchecked")
	private void move( ArticleCategory articleCategory, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = articleCategory.getPosition();
		int newPosition = oldPosition + movement;
		articleCategory.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ARTICLE_CATEGORY_ID), ""+articleCategory.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			ArticleCategory articleCat = (ArticleCategory)list.get(0);
			articleCat.setPosition(newPosition);
			getManagerBean().update(articleCat);
		}
    	List<ArticleCategory> listObjects = (List<ArticleCategory>) this.model.getWrappedData();
    	ArticleCategory articleCategoryMoved = listObjects.get( newPosition );
    	articleCategoryMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ARTICLE_CATEGORY_ID), ""+articleCategoryMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			ArticleCategory articleCat = (ArticleCategory)list.get(0);
			articleCat.setPosition(oldPosition);
			getManagerBean().update(articleCat);
		}
		listObjects.set( newPosition, articleCategory);
		listObjects.set( oldPosition, articleCategoryMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((ArticleCategory) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((ArticleCategory) this.model.getRowData(), 1);    	
    }
    
	public void onSelectArticles(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true;
		ArticleController fc = (ArticleController)AonUtil.getController("article");
		IManagerBean moBean = BeanManager.getManagerBean(Article.class);
		ArticleCategory articleCategory = (ArticleCategory) this.getSelectedTO();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_CATEGORY_ID), "" + articleCategory.getId());
		criteria.addExpression(moBean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), "" + fc.getCurrentType());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.ARTICLE_POSITION));
		fc.setCurrentArticleCategory(articleCategory);
		fc.setCriteria(criteria);
		fc.onSearch(event);
		fc.onInit(event);
	}

	public void reorderObjects() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.ARTICLE_CATEGORY_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			ArticleCategory f = (ArticleCategory)list.get(i);
			int oldPosition = f.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				f.setPosition(newPosition);
				getManagerBean().update(f);
			}
		}
	}

}