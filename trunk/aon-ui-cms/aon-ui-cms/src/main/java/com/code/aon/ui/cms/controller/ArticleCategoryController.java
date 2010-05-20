package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.ArticleConfig;
import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ReferenceChecker;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class ArticleCategoryController extends BasicI18nController implements ICMSConstants, Constants {

	public void onInit(ActionEvent event){
		((GeneratorConfigController)AonUtil.getRegisteredBean(GENERATOR_CONFIG)).initSection(ArticleConfig.class);
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

	public boolean isUsed() throws ManagerBeanException {
		Integer id = ((ArticleCategory) getTo()).getId();
		return ReferenceChecker.isInModulaPage(id, ModularPageOptionType.ARTICLE_EVENTS, ModularPageOptionType.ARTICLE_NEWS,
				ModularPageOptionType.ARTICLE_SERVICES, ModularPageOptionType.ARTICLE_OTHER) ||
			ReferenceChecker.isInSideBar(id, SidebarType.ARTICLE_EVENTS_CATEGORY, SidebarType.ARTICLE_NEWS_CATEGORY,
				SidebarType.ARTICLE_SERVICES_CATEGORY, SidebarType.ARTICLE_OTHER_CATEGORY) ||
			ReferenceChecker.isInDirectAccess(id, ContentLevel.CATEGORY, PageType.ARTICLE_EVENTS,
				PageType.ARTICLE_NEWS, PageType.ARTICLE_SERVICES, PageType.ARTICLE_OTHER);
	}	

}