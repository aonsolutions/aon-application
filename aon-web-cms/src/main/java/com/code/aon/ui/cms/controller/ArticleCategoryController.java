package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.ArticleConfig;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class ArticleCategoryController extends BasicI18nController {

	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}


	public void onInit(ActionEvent event){
		((GeneratorConfigController)AonUtil.getRegisteredBean("generator_config")).initSection(ArticleConfig.class);
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

	public String getBack(){
		if (FormUtil.getController("article").getTo()==null)
			return "article_list";
		return "article_form";
	}

}