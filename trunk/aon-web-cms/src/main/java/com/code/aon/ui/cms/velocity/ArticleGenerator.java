package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.ArticleConfig;
import com.code.aon.cms.ArticleDetail;
import com.code.aon.cms.Section;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.cms.controller.GeneratorConfigController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.ArticleCategoryHandler;
import com.code.aon.ui.cms.velocity.attribute.ArticleHandler;

public class ArticleGenerator extends Generator {

	public static final String ARTICLE_LIST_PAGE = "article_categories";
	
	public void generateArticle(Article article){
		try{
			IManagerBean articleDetailBean = BeanManager.getManagerBean(ArticleDetail.class);
			Criteria articleDetailCriteria = new Criteria();
			articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), article.getId());
			articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List<ITransferObject> articleDetailList = articleDetailBean.getList(articleDetailCriteria);
			if (articleDetailList.isEmpty()) {
				logger.warning(" Articulo " + article.getAlias() + " de la categoria " + article.getArticleCategory().getAlias() + " no internacionalizado.");
			} else {
				ArticleDetail articleDetail = (ArticleDetail)articleDetailList.get(0);
				String backURL = getBackURL(article.getArticleType(),article.getArticleCategory());
				Templates templates = getTemplate(article.getArticleType());
				VelocityUtil vu = context.initVelocityUtil();
				changeArticleContext(vu, article.getArticleCategory());
				generateArticle(vu, templates, backURL, articleDetail);		
			}
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		}
	}
	
	private void changeArticleCategoryContext(VelocityUtil vu, ArticleCategory articleCategory) throws ManagerBeanException{
		if (articleCategory.getSection()!=null){
			context.changeSection(vu, articleCategory.getSection());
		}else{
			Section configSection = GeneratorConfigController.currentSection(ArticleConfig.class);
			context.changeSection(vu, configSection);
		}
	}

	private void changeArticleContext(VelocityUtil vu, ArticleCategory articleCategory) throws ManagerBeanException{
		if (articleCategory.getElementSection()!=null){
			context.changeSection(vu, articleCategory.getElementSection());
		}else{
			if (articleCategory.getSection()!=null){
				context.changeSection(vu, articleCategory.getSection());
			}else{
				Section configSection = GeneratorConfigController.currentSection(ArticleConfig.class);
				context.changeSection(vu, configSection);
			}
		}
	}
	
	private String getBackURL(ArticleType articleType, ArticleCategory articleCategory){
		Templates templates = getTemplate(articleType);
		String back_url = templates.getHtmlName();
		back_url = back_url.replaceAll("%NAME%", articleType.getName()+"_"+articleCategory.getAlias());
		return back_url; 
	}
	
	private void generateArticle(
			VelocityUtil vu,
			Templates templates,
			String back_url,
			ArticleDetail articleDetail) {
		ArticleHandler ahandler = new ArticleHandler(articleDetail);
		vu.put(BACK_URL_KEY, back_url);
		vu.put(ARTICLE_KEY, ahandler);
		logger.info(" Generando article " + articleDetail.getArticle().getAlias() + ".");
		generate(vu, templates, articleDetail.getArticle().getAlias());
		vu.remove(ARTICLE_KEY);
		vu.remove(BACK_URL_KEY);
	}
	
	public void generate(ArticleType articleType, ArticleCategory selectedArticleCategory) {
		try {
			IManagerBean articleCategoryBean = BeanManager.getManagerBean(ArticleCategory.class);
			IManagerBean articleCategoryDetailBean = BeanManager.getManagerBean(ArticleCategoryDetail.class);
			IManagerBean articleBean = BeanManager.getManagerBean(Article.class);
			IManagerBean articleDetailBean = BeanManager.getManagerBean(ArticleDetail.class);
			
			Criteria articleCategoryCriteria = new Criteria();
			
			articleCategoryCriteria.addEqualExpression(articleCategoryBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_ACTIVE), true);
			if (selectedArticleCategory!=null)
				articleCategoryCriteria.addEqualExpression(articleCategoryBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_ID), selectedArticleCategory.getId());
			articleCategoryCriteria.addOrder(articleCategoryBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_POSITION));
			List<ITransferObject> articleCategoryList = (List<ITransferObject>)articleCategoryBean.getList(articleCategoryCriteria);
			ArrayList<ArticleCategoryHandler> achlist = new ArrayList<ArticleCategoryHandler>(); 
			for (int j=0; j < articleCategoryList.size(); j++) {
				VelocityUtil vu = context.initVelocityUtil();

				ArticleCategory articleCategory = (ArticleCategory)articleCategoryList.get(j);
				Criteria articleCategoryDetailCriteria = new Criteria();
				articleCategoryDetailCriteria.addEqualExpression(articleCategoryDetailBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_ARTICLE_CATEGORY_ID), articleCategory.getId());
				articleCategoryDetailCriteria.addEqualExpression(articleCategoryDetailBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> articleCategoryDetailList = (List<ITransferObject>)articleCategoryDetailBean.getList(articleCategoryDetailCriteria);
				if (articleCategoryDetailList.isEmpty()) {
					logger.warning(" Categoria de articulos " + articleCategory.getAlias() + " no internacionalizada.");
				} else {
					ArticleCategoryDetail articleCategoryDetail = (ArticleCategoryDetail)articleCategoryDetailList.get(0);
					Templates templates = getTemplate(articleType);
					Criteria articleCriteria = new Criteria();
					articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_CATEGORY_ID), articleCategory.getId());
					articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ACTIVE), true);
					articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), articleType);
					Expression nullableExpr = ExpressionUtilities.getNullExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE));
		            Expression greaterExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE), new Date());
		            articleCriteria.addExpression(ExpressionUtilities.getOrExpression(nullableExpr, greaterExpr));
					articleCriteria.addLessThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_PUBLISH_DATE), new Date());
					articleCriteria.addOrder(articleBean.getFieldName(ICMSAlias.ARTICLE_POSITION));
					articleCriteria.addOrder(articleBean.getFieldName(ICMSAlias.ARTICLE_PUBLISH_DATE),false);
					List<ITransferObject> articleList = (List<ITransferObject>)articleBean.getList(articleCriteria);
					ArrayList<ArticleHandler> ahlist = new ArrayList<ArticleHandler>();
					if (!articleList.isEmpty()){
						String back_url = getBackURL(articleType, articleCategory);
						changeArticleContext(vu, articleCategory);
						for (int i=0; i < articleList.size(); i++) {
							Article article = (Article)articleList.get(i);
							Criteria articleDetailCriteria = new Criteria();
							articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), article.getId());
							articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
							List<ITransferObject> articleDetailList = (List<ITransferObject>)articleDetailBean.getList(articleDetailCriteria);
							if (articleDetailList.isEmpty()) {
								logger.warning(" Articulo " + article.getAlias() + " de la categoria " + articleCategory.getAlias() + " no internacionalizado.");
							}else{
								ArticleDetail articleDetail = (ArticleDetail)articleDetailList.get(0);
								ArticleHandler ahandler = new ArticleHandler(articleDetail);
								ahlist.add(ahandler);
								vu.put(BACK_URL_KEY, back_url);
								vu.put(ARTICLE_KEY, ahandler);
								logger.info(" Generando article " + article.getAlias() + ".");
								generate(vu, templates, article.getAlias());
								vu.remove(ARTICLE_KEY);
								vu.remove(BACK_URL_KEY);
							}
						}
						changeArticleCategoryContext(vu, articleCategory);
						
						ArticleCategoryHandler achandler = new ArticleCategoryHandler(articleCategoryDetail,articleType,ahlist);
						vu.put(ARTICLE_CATEGORY_KEY, achandler);
						vu.put(ARTICLE_LIST_KEY, ahlist);
						logger.info(" Generando list de article.");
						generate(vu, templates,articleType.getName()+"_"+articleCategory.getAlias());
						vu.remove(ARTICLE_CATEGORY_KEY);
						vu.remove(ARTICLE_LIST_KEY);
						achlist.add(achandler);
					}
					ahlist = null;
				}	
			}
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		}
	}

	public void generate(ArticleType articleType) {
		generate( articleType, null );
	}
	
	public static Templates getTemplate(ArticleType art_type) {
		switch (art_type) {
		case NEWS:
			return Templates.ARTICLE_NEWS;
		case SERVICES:
			return Templates.ARTICLE_SERVICES;
		case EVENTS:
			return Templates.ARTICLE_EVENTS;
		case OTHER:
			return Templates.ARTICLE_OTHER;
		default:
			return Templates.ARTICLE_OTHER;
		}
	}

	public static Object getArticleHandler(Integer ident, String message) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Article.class);
			Article a = (Article) bean.get(ident);
			if ( a == null ){
				getLogger().error( message + " REFERENCIA UN ARTICULO ("+ident+") INEXISTENTE");
				return null;
			}
			
			if (a.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(ArticleDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				if (ld.isEmpty()){
					getLogger().warning(" Articulo " + a.getAlias() + " no internacionalizada.");
				}else{
					ArticleDetail ad = (ArticleDetail)ld.get(0);
					ArticleHandler ah = new ArticleHandler(ad);
					return ah;
				}
			}
		} catch (ManagerBeanException e) {
			getLogger().error(e.getMessage());
		}
		return null;
	}

	public static Object getArticleCategoryHandler(Integer ident, ArticleType type, String message) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ArticleCategory.class);
			ArticleCategory ac = (ArticleCategory) bean.get(ident);
			if ( ac == null ){
				getLogger().error( message + " REFERENCIA UNA CATEGORIA DE ARTICULO ("+ident+") INEXISTENTE");
				return null;
			}
			bean = BeanManager.getManagerBean(ArticleCategoryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_ARTICLE_CATEGORY_ID), ident);
			List<ITransferObject> lcd = (List<ITransferObject>)bean.getList(criteria);
			if (lcd.isEmpty()){
				getLogger().warning(" Categoria de Articulo " + ac.getAlias() + " no internacionalizada.");
				return null;
			}
			ArticleCategoryDetail acd = (ArticleCategoryDetail) lcd.get(0);

			bean = BeanManager.getManagerBean(Article.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_CATEGORY_ID), ident);
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), type);
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_ACTIVE), true);
			Expression nullableExpr = ExpressionUtilities.getNullExpression(bean.getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE));
            Expression greaterExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE), new Date());
            criteria.addExpression(ExpressionUtilities.getOrExpression(nullableExpr, greaterExpr));
			criteria.addLessThanOrEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_PUBLISH_DATE), new Date());
			criteria.addOrder(bean.getFieldName(ICMSAlias.ARTICLE_POSITION));
			criteria.addOrder(bean.getFieldName(ICMSAlias.ARTICLE_PUBLISH_DATE),false);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			Iterator<ITransferObject> iter = l.iterator();
			ArrayList<ArticleHandler> ahlist = new ArrayList<ArticleHandler>();
			while (iter.hasNext()){
				Article a = (Article)iter.next();
				bean = BeanManager.getManagerBean(ArticleDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), a.getId());
				List<ITransferObject> ld = (List<ITransferObject>)bean.getList(criteria);
				if (ld.isEmpty()){
					getLogger().warning(" Articulo " + a.getAlias() + " de categoria " + ac.getAlias() + " no internacionalizada.");
				}else{
					ArticleDetail ad = (ArticleDetail)ld.get(0);
					ArticleHandler ah = new ArticleHandler(ad);
					ahlist.add(ah);
				}
			}
			ArticleCategoryHandler ach = new ArticleCategoryHandler(acd,type,ahlist);
			return ach;
		} catch (ManagerBeanException e) {
			getLogger().error(e.getMessage());
		}
		return null;
	}
	
}
