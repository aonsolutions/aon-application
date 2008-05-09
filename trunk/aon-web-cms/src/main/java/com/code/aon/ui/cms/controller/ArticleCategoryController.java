package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.ArticleCategoryDetail;
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


public class ArticleCategoryController extends BasicI18nController {

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
    	move((ArticleCategory) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	move((ArticleCategory) this.model.getRowData(), 1);    	
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

	public void onGenerateCurrentArticleCategory(ActionEvent event) throws ManagerBeanException, ExpressionException {
		GeneratorStatusController status = (GeneratorStatusController)AonUtil.getRegisteredBean("generator_status");
		status.onInit(event);
		//Copy css and js files from current template
		FileUtil.copyDir(ControllerUtil.getCssTemplatePath(), ControllerUtil.getPreviewPath());
		FileUtil.copyDir(ControllerUtil.getJsTemplatePath(), ControllerUtil.getPreviewPath());
		
		ArticleType[] types_ = ArticleType.values();
		for (int i = 0; i < types_.length; i++) {
			ArticleGenerator.generate(types_[i],(ArticleCategory) this.getTo());
		}
		
		status.finalized();
	}

}