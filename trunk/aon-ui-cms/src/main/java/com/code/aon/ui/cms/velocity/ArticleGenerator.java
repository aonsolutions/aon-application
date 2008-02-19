package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.code.aon.cms.AlbumConfig;
import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.ArticleConfig;
import com.code.aon.cms.ArticleDetail;
import com.code.aon.cms.ArticleDocument;
import com.code.aon.cms.ArticleDocumentDetail;
import com.code.aon.cms.ArticleRelated;
import com.code.aon.cms.Section;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.cms.enumeration.MenuType;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.GeneratorConfigController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.ArticleCategoryHandler;
import com.code.aon.ui.cms.velocity.attribute.ArticleDocumentHandler;
import com.code.aon.ui.cms.velocity.attribute.ArticleHandler;

public class ArticleGenerator extends Generator {
	
	public static void generate(VelocityUtil vu) {
		List<ITransferObject> articleCategoryList;
		List<ITransferObject> articleCategoryDetailList;
		List<ITransferObject> articleList;
		List<ITransferObject> articleDetailList;
		try {
			Section configSection = GeneratorConfigController.currentSection(ArticleConfig.class);
			IManagerBean articleCategoryBean = BeanManager.getManagerBean(ArticleCategory.class);
			IManagerBean articleCategoryDetailBean = BeanManager.getManagerBean(ArticleCategoryDetail.class);
			IManagerBean articleBean = BeanManager.getManagerBean(Article.class);
			IManagerBean articleDetailBean = BeanManager.getManagerBean(ArticleDetail.class);
			
			ArticleType[] values = ArticleType.values();
			Templates templates;
			
			Criteria articleCategoryCriteria = new Criteria();
			Criteria articleCategoryDetailCriteria;
			Criteria articleCriteria;
			Criteria articleDetailCriteria;
			
			ArticleCategory articleCategory;
			ArticleCategoryDetail articleCategoryDetail;
			Article article;
			ArticleDetail articleDetail;
			
			articleCategoryCriteria.addEqualExpression(articleCategoryBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_ACTIVE), true);
			articleCategoryList = (List<ITransferObject>)articleCategoryBean.getList(articleCategoryCriteria);
			ArrayList<ArticleCategoryHandler> achlist = new ArrayList<ArticleCategoryHandler>(); 
			for (int j=0; j < articleCategoryList.size(); j++) {
				articleCategory = (ArticleCategory)articleCategoryList.get(j);
				articleCategoryDetailCriteria = new Criteria();
				articleCategoryDetailCriteria.addEqualExpression(articleCategoryDetailBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_ARTICLE_CATEGORY_ID), articleCategory.getId());
				articleCategoryDetailCriteria.addEqualExpression(articleCategoryDetailBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				articleCategoryDetailList = (List<ITransferObject>)articleCategoryDetailBean.getList(articleCategoryDetailCriteria);
				if (articleCategoryDetailList.size() > 0) {
					articleCategoryDetail = (ArticleCategoryDetail)articleCategoryDetailList.get(0);
					for (int art_type = 0; art_type < values.length; art_type++){
						templates = ArticleGenerator.getTemplate(art_type);
						articleCriteria = new Criteria();
						articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_CATEGORY_ID), articleCategory.getId());
						articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ACTIVE), true);
						articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), values[art_type]);
						articleList = (List<ITransferObject>)articleBean.getList(articleCriteria);
						ArrayList<ArticleHandler> ahlist = new ArrayList<ArticleHandler>();
						if (!articleList.isEmpty()){
							CommonGenerator.getCommonGenerator().chargeContext(vu, articleCategory.getElementSection());
							for (int i=0; i < articleList.size(); i++) {
								article = (Article)articleList.get(i);
								articleDetailCriteria = new Criteria();
								articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), article.getId());
								articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
								articleDetailList = (List<ITransferObject>)articleDetailBean.getList(articleDetailCriteria);
								if (articleDetailList.size() > 0) {
									articleDetail = (ArticleDetail)articleDetailList.get(0);
									ArticleHandler ahandler = new ArticleHandler(articleDetail);
									ahlist.add(ahandler);
									vu.put("article", ahandler);
									vu.addMessage(" Generando article " + article.getAlias() + ".", VelocityUtil.INFO);
									generate(vu, templates, article.getAlias());
									vu.remove("article");
								}
							}
							CommonGenerator.getCommonGenerator().chargeContext(vu, articleCategory.getSection());
							ArticleCategoryHandler achandler = new ArticleCategoryHandler(articleCategoryDetail,ahlist);
							vu.put("article_category", achandler);
							vu.put("article_list", ahlist);
							vu.addMessage(" Generando list de article.", VelocityUtil.INFO);
							generate(vu, templates, values[art_type].getName()+"_"+articleCategory.getAlias());
							vu.remove("article_category");
							vu.remove("article_list");
							achlist.add(achandler);
						}
						ahlist = null;
					}
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} finally {
			articleCategoryList = null;
			articleCategoryDetailList = null;
			articleList = null;
			articleDetailList = null;
		}
	}
	
	public static Templates getTemplate(int art_type) {
		switch (art_type) {
		case 0:
			return Templates.ARTICLE_NEWS;
		case 1:
			return Templates.ARTICLE_SERVICES;
		case 2:
			return Templates.ARTICLE_EVENTS;
		case 3:
			return Templates.ARTICLE_OTHER;
		default:
			return Templates.ARTICLE_OTHER;
		}
	}

	public static Object getArticleHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Article.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			
			Article a = (Article)l.get(0);
			if (a.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(ArticleDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				ArticleDetail ad = (ArticleDetail)ld.get(0);
				ArticleHandler ah = new ArticleHandler(ad);
				return ah;
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getArticleCategoryHandler(Integer ident, ArticleType type) {
		List<ITransferObject> l;
		List<ITransferObject> ld;
		List<ITransferObject> lcd;
		Iterator<ITransferObject> iter;
		try {
			IManagerBean bean = BeanManager.getManagerBean(Article.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_CATEGORY_ID), ident);
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), type);
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_ACTIVE), true);
			l = (List<ITransferObject>)bean.getList(criteria);
			iter = l.iterator();
			ArrayList<ArticleHandler> ahlist = new ArrayList<ArticleHandler>();
			Article ac;
			while (iter.hasNext()){
				ac = (Article)iter.next();
				bean = BeanManager.getManagerBean(ArticleDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), ac.getId());
				ld = (List<ITransferObject>)bean.getList(criteria);
				if (!ld.isEmpty()){
					ArticleDetail ad = (ArticleDetail)ld.get(0);
					ArticleHandler ah = new ArticleHandler(ad);
					ahlist.add(ah);
				}
			}
			bean = BeanManager.getManagerBean(ArticleCategoryDetail.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_ARTICLE_CATEGORY_ID), ident);
			lcd = (List<ITransferObject>)bean.getList(criteria);
			ArticleCategoryDetail acd = (ArticleCategoryDetail) lcd.get(0);
			ArticleCategoryHandler ach = new ArticleCategoryHandler(acd,ahlist);
			return ach;
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} finally {
			l = null;
			ld = null;
			lcd = null;
			iter = null;
		}
		return null;
	}

	public static String ARTICLE_LIST_PAGE = "article_categories";
	
}
