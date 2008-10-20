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

	public static void generateArticle(Article article){
		try{
			IManagerBean articleDetailBean = BeanManager.getManagerBean(ArticleDetail.class);
			Criteria articleDetailCriteria = new Criteria();
			articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), article.getId());
			articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List articleDetailList = (List<ITransferObject>)articleDetailBean.getList(articleDetailCriteria);
			if (articleDetailList.isEmpty()) {
				VelocityUtil.addMessage(" Articulo " + article.getAlias() + " de la categoria " + article.getArticleCategory().getAlias() + " no internacionalizado.", VelocityUtil.WARN);
			}else{
				ArticleDetail articleDetail = (ArticleDetail)articleDetailList.get(0);
				String backURL = ArticleGenerator.getBackURL(article.getArticleType(),article.getArticleCategory());
				Templates templates = ArticleGenerator.getTemplate(article.getArticleType().ordinal());
				VelocityUtil vu = ArticleGenerator.initVelocity();
				ArticleGenerator.chargeArticleContext(vu, article.getArticleCategory());
				ArticleGenerator.generateArticle(vu, templates, backURL, articleDetail);
				vu.finalize();
				vu = null;		
			}
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		} finally {
		}
	}
	
	private static VelocityUtil initVelocity(){
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		return vu;
	}

	private static void chargeArticleCategoryContext(VelocityUtil vu, ArticleCategory articleCategory) throws ManagerBeanException{
		if (articleCategory.getSection()!=null){
			CommonGenerator.getCommonGenerator().chargeContext(vu, articleCategory.getSection());
		}else{
			Section configSection = GeneratorConfigController.currentSection(ArticleConfig.class);
			CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
		}
	}

	private static void chargeArticleContext(VelocityUtil vu, ArticleCategory articleCategory) throws ManagerBeanException{
		if (articleCategory.getElementSection()!=null){
			CommonGenerator.getCommonGenerator().chargeContext(vu, articleCategory.getElementSection());
		}else{
			if (articleCategory.getSection()!=null){
				CommonGenerator.getCommonGenerator().chargeContext(vu, articleCategory.getSection());
			}else{
				Section configSection = GeneratorConfigController.currentSection(ArticleConfig.class);
				CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
			}
		}
	}
	
	private static String getBackURL(ArticleType articleType, ArticleCategory articleCategory){
		Templates templates = ArticleGenerator.getTemplate(articleType.ordinal());
		String back_url = templates.getHtmlName();
		back_url = back_url.replaceAll("%NAME%", articleType.getName()+"_"+articleCategory.getAlias());
		return back_url; 
	}
	
	private static void generateArticle(
			VelocityUtil vu,
			Templates templates,
			String back_url,
			ArticleDetail articleDetail) {
		ArticleHandler ahandler = new ArticleHandler(articleDetail);
		vu.put("back_url", back_url);
		vu.put("article", ahandler);
		VelocityUtil.addMessage(" Generando article " + articleDetail.getArticle().getAlias() + ".", VelocityUtil.INFO);
		generate(vu, templates, articleDetail.getArticle().getAlias());
		vu.remove("article");
		vu.remove("back_url");
	}
	
	public static void generate(ArticleType articleType, ArticleCategory selectedArticleCategory) {
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
				
				System.gc();
				VelocityUtil vu = ArticleGenerator.initVelocity();

				articleCategory = (ArticleCategory)articleCategoryList.get(j);
				articleCategoryDetailCriteria = new Criteria();
				articleCategoryDetailCriteria.addEqualExpression(articleCategoryDetailBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_ARTICLE_CATEGORY_ID), articleCategory.getId());
				articleCategoryDetailCriteria.addEqualExpression(articleCategoryDetailBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				articleCategoryDetailList = (List<ITransferObject>)articleCategoryDetailBean.getList(articleCategoryDetailCriteria);
				if (articleCategoryDetailList.isEmpty()) {
					VelocityUtil.addMessage(" Categoria de articulos " + articleCategory.getAlias() + " no internacionalizada.", VelocityUtil.WARN);
				}else{
					articleCategoryDetail = (ArticleCategoryDetail)articleCategoryDetailList.get(0);
					boolean emptyCategory = true;
					templates = ArticleGenerator.getTemplate(articleType.ordinal());
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
						ArticleGenerator.chargeArticleContext(vu, articleCategory);
						for (int i=0; i < articleList.size(); i++) {
							article = (Article)articleList.get(i);
							articleDetailCriteria = new Criteria();
							articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), article.getId());
							articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
							articleDetailList = (List<ITransferObject>)articleDetailBean.getList(articleDetailCriteria);
							if (articleDetailList.isEmpty()) {
								VelocityUtil.addMessage(" Articulo " + article.getAlias() + " de la categoria " + articleCategory.getAlias() + " no internacionalizado.", VelocityUtil.WARN);
							}else{
								articleDetail = (ArticleDetail)articleDetailList.get(0);
								ArticleHandler ahandler = new ArticleHandler(articleDetail);
								ahlist.add(ahandler);
								vu.put("back_url", back_url);
								vu.put("article", ahandler);
								VelocityUtil.addMessage(" Generando article " + article.getAlias() + ".", VelocityUtil.INFO);
								generate(vu, templates, article.getAlias());
								vu.remove("article");
								vu.remove("back_url");
							}
						}
						ArticleGenerator.chargeArticleCategoryContext(vu, articleCategory);
						
						ArticleCategoryHandler achandler = new ArticleCategoryHandler(articleCategoryDetail,articleType,ahlist);
						vu.put("article_category", achandler);
						vu.put("article_list", ahlist);
						VelocityUtil.addMessage(" Generando list de article.", VelocityUtil.INFO);
						generate(vu, templates,articleType.getName()+"_"+articleCategory.getAlias());
						vu.remove("article_category");
						vu.remove("article_list");
						achlist.add(achandler);
					}
					ahlist = null;
					if (emptyCategory){
						VelocityUtil.addMessage("La categoria de articulos " + articleCategory.getAlias() + " no tiene " + articleType.getName() + ".", VelocityUtil.WARN);
					}
				}
				vu.finalize();
				vu = null;		
			}
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		} finally {
			articleCategoryList = null;
			articleCategoryDetailList = null;
			articleList = null;
			articleDetailList = null;
		}
	}

	public static void generate(ArticleType articleType) {
		ArticleGenerator.generate(articleType,null);
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
			if (l.isEmpty()){
				VelocityUtil.addMessage("ARTICULO "+ident+" REFERENCIADO NO EXISTE !!!", VelocityUtil.WARN);
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
					VelocityUtil.addMessage(" Articulo " + a.getAlias() + " no internacionalizada.", VelocityUtil.WARN);
				}else{
					ArticleDetail ad = (ArticleDetail)ld.get(0);
					ArticleHandler ah = new ArticleHandler(ad);
					return ah;
				}
			}
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
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
				VelocityUtil.addMessage("CATEGORIA DE ARTICULO "+ident+" REFERENCIADA NO EXISTE !!!", VelocityUtil.WARN);
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
				VelocityUtil.addMessage(" Categoria de Articulo " + ac.getAlias() + " no internacionalizada.", VelocityUtil.WARN);
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
					VelocityUtil.addMessage(" Articulo " + a.getAlias() + " de categoria " + ac.getAlias() + " no internacionalizada.", VelocityUtil.WARN);
				}else{
					ArticleDetail ad = (ArticleDetail)ld.get(0);
					ArticleHandler ah = new ArticleHandler(ad);
					ahlist.add(ah);
				}
			}
			ArticleCategoryHandler ach = new ArticleCategoryHandler(acd,type,ahlist);
			return ach;
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
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
