package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.ArticleConfig;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.util.AonUtil;


public class ArticleCategoryController extends BasicI18nController {

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.ARTICLE_CATEGORY_POSITION);

    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveUp(this);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveDown(this);
    }

	protected void afterRemoveSelected(){
		try {
			orderedControllerSupport.reorderObjects(this);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	public void onInit(ActionEvent event){
		((GeneratorConfigController)AonUtil.getRegisteredBean("generator_config")).initSection(ArticleConfig.class);
		this.onSearch(event);
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
		ArticleCategory articleCategory = (ArticleCategory)this.model.getRowData();
		articleCategory.setActive(active);
		getManagerBean().update(articleCategory);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		ArticleCategoryDetail articleCategoryDetail = (ArticleCategoryDetail)getModelRowdataI18n();
		if (articleCategoryDetail != null) label = articleCategoryDetail.getLabel();
		return label;
	}

	public void onSelectArticles(ActionEvent event) throws ManagerBeanException, ExpressionException {
		ArticleController fc = (ArticleController)AonUtil.getController("article");
		IManagerBean moBean = BeanManager.getManagerBean(Article.class);
		ArticleCategory articleCategory = (ArticleCategory) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_CATEGORY_ID), "" + articleCategory.getId());
		criteria.addExpression(moBean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), "" + fc.getCurrentType());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.ARTICLE_POSITION));
		fc.setCurrentArticleCategory(articleCategory);
		fc.setCriteria(criteria);
		fc.onSearch(event);
		fc.iniTab();
	}
	
	// ORDER ALIAS
	private boolean positionOrdered = true;
	
	public boolean isPositionOrdered() {
		return positionOrdered;
	}

	public void onAliasOrdered(ActionEvent event) throws ManagerBeanException, ExpressionException {
		Criteria criteria = new Criteria();
		IManagerBean bean = BeanManager.getManagerBean(ArticleCategory.class);
		criteria.addOrder(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_ACTIVE));
		criteria.addOrder(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_ALIAS));
		this.setCriteria(criteria);
		this.onSearch(event);
		positionOrdered = false;
	}
	
	public void onPosition(ActionEvent event) throws ManagerBeanException, ExpressionException {
		Criteria criteria = new Criteria();
		IManagerBean bean = BeanManager.getManagerBean(ArticleCategory.class);
		criteria.addOrder(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_POSITION));
		this.setCriteria(criteria);
		this.onSearch(event);
		positionOrdered = true;
	}
	// END ORDER ALIAS

}