package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.ArticleConfig;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class ArticleCategoryController extends BasicI18nController implements ICMSConstants, Constants {

	public void onInit(ActionEvent event){
		((GeneratorConfigController)AonUtil.getRegisteredBean(GENERATOR_CONFIG)).initSection(ArticleConfig.class);
	}
	
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
		String label = NO_VALUE_LABEL;
		ArticleCategoryDetail articleCategoryDetail = (ArticleCategoryDetail)getModelRowdataI18n();
		if (articleCategoryDetail != null) label = articleCategoryDetail.getLabel();
		return label;
	}

	public String getBack(){
		if (FormUtil.getController(ARTICLE).getTo()==null)
			return ARTICLE_LIST;
		return ARTICLE_FORM;
	}

}