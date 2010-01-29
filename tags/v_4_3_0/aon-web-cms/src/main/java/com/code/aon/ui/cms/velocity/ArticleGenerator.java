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
				chargeArticleContext(vu, article.getArticleCategory());
				generateArticle(vu, templates, backURL, articleDetail);		
			}
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		}
	}
	
	private void chargeArticleCategoryContext(VelocityUtil vu, ArticleCategory articleCategory) throws ManagerBeanException{
		if (articleCategory.getSection()!=null){
			context.changeSection(vu, articleCategory.getSection());
		}else{
			Section configSection = GeneratorConfigController.currentSection(ArticleConfig.class);
			context.changeSection(vu, configSection);
		}
	}

	private void chargeArticleContext(VelocityUtil vu, ArticleCategory articleCategory) throws ManagerBeanException{
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
		List<ITransferObject> articleCategoryList;
		List<ITransferObject> articleCategoryDetailList;
		List<ITransferObject> articleList;
		List<ITransferObject> articleDetailList;
		try {
			IManagerBean articleCategoryBean = BeanManager.getManagerBean(ArticleCategory.class);
			IManagerBean articleCategoryDetailBean = BeanManager.getManagerBean(ArticleCategoryDetail.class);
			IManagerBean articleBean = BeanManager.getManagerBean(Article.class);
			IManagerBean articleDetailBean = BeanManager.getManagerBean(ArticleDetail.class);
			
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
			if (selectedArticleCategory!=null)
				articleCategoryCriteria.addEqualExpression(articleCategoryBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_ID), selectedArticleCategory.getId());
			articleCategoryCriteria.addOrder(articleCategoryBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_POSITION));
			articleCategoryList = (List<ITransferObject>)articleCategoryBean.getList(articleCategoryCriteria);
			ArrayList<ArticleCategoryHandler> achlist = new ArrayList<ArticleCategoryHandler>(); 
			for (int j=0; j < articleCategoryList.size(); j++) {
				VelocityUtil vu = context.initVelocityUtil();

				articleCategory = (ArticleCategory)articleCategoryList.get(j);
				articleCategoryDetailCriteria = new Criteria();
				articleCategoryDetailCriteria.addEqualExpression(articleCategoryDetailBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_ARTICLE_CATEGORY_ID), articleCategory.getId());
				articleCategoryDetailCriteria.addEqualExpression(articleCategoryDetailBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				articleCategoryDetailList = (List<ITransferObject>)articleCategoryDetailBean.getList(articleCategoryDetailCriteria);
				if (articleCategoryDetailList.isEmpty()) {
					logger.warning(" Categoria de articulos " + articleCategory.getAlias() + " no internacionalizada.");
				}else{
					articleCategoryDetail = (ArticleCategoryDetail)articleCategoryDetailList.get(0);
					boolean emptyCategory = true;
					templates = getTemplate(articleType);
					articleCriteria = new Criteria();
					articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_CATEGORY_ID), articleCategory.getId());
					articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ACTIVE), true);
					articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), articleType);
					Expression nullableExpr = ExpressionUtilities.getNullExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE));
		            Expression greaterExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE), new Date());
		            articleCriteria.addExpression(ExpressionUtilities.getOrExpression(nullableExpr, greaterExpr));
					articleCriteria.addLessThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_PUBLISH_DATE), new Date());
					articleCriteria.addOrder(articleBean.getFieldName(ICMSAlias.ARTICLE_POSITION));
					articleList = (List<ITransferObject>)articleBean.getList(articleCriteria);
					ArrayList<ArticleHandler> ahlist = new ArrayList<ArticleHandler>();
					if (!articleList.isEmpty()){
						emptyCategory = false;
						String back_url = getBackURL(articleType, articleCategory);
						chargeArticleContext(vu, articleCategory);
						for (int i=0; i < articleList.size(); i++) {
							article = (Article)articleList.get(i);
							articleDetailCriteria = new Criteria();
							articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), article.getId());
							articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
							articleDetailList = (List<ITransferObject>)articleDetailBean.getList(articleDetailCriteria);
							if (articleDetailList.isEmpty()) {
								logger.warning(" Articulo " + article.getAlias() + " de la categoria " + articleCategory.getAlias() + " no internacionalizado.");
							}else{
								articleDetail = (ArticleDetail)articleDetailList.get(0);
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
						chargeArticleCategoryContext(vu, articleCategory);
						
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
					if (emptyCategory){
						logger.warning("La categoria de articulos " + articleCategory.getAlias() + " no tiene " + articleType.getName() + ".");
					}
				}	
			}
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		} finally {
			articleCategoryList = null;
			articleCategoryDetailList = null;
			articleList = null;
			articleDetailList = null;
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

	public static Object getArticleHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Article.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.isEmpty()){
				getLogger().warning("ARTICULO "+ident+" REFERENCIADO NO EXISTE !!!");
				return null;
			}
			
			Article a = (Article)l.get(0);
			if (a.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(ArticleDetail.class);
				criteria = new Criteria();
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

	public static Object getArticleCategoryHandler(Integer ident, ArticleType type) {
		List<ITransferObject> l;
		List<ITransferObject> ld;
		List<ITransferObject> lc;
		List<ITransferObject> lcd;
		Iterator<ITransferObject> iter;
		try {
			IManagerBean bean = BeanManager.getManagerBean(ArticleCategory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_ID), ident);
			ArticleCategory ac = null;
			lc = bean.getList(criteria);
			if (lc.isEmpty()){
				getLogger().warning("CATEGORIA DE ARTICULO "+ident+" REFERENCIADA NO EXISTE !!!");
				return null;
			}else{
				ac = (ArticleCategory) lc.get(0);
			}
			bean = BeanManager.getManagerBean(ArticleCategoryDetail.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_ARTICLE_CATEGORY_ID), ident);
			lcd = (List<ITransferObject>)bean.getList(criteria);
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
			criteria.addOrder(bean.getFieldName(ICMSAlias.ARTICLE_PUBLISH_DATE),false);
			l = (List<ITransferObject>)bean.getList(criteria);
			iter = l.iterator();
			ArrayList<ArticleHandler> ahlist = new ArrayList<ArticleHandler>();
			Article a;
			while (iter.hasNext()){
				a = (Article)iter.next();
				bean = BeanManager.getManagerBean(ArticleDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), a.getId());
				ld = (List<ITransferObject>)bean.getList(criteria);
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
		} finally {
			l = null;
			ld = null;
			lcd = null;
			iter = null;
		}
		return null;
	}
	
}
