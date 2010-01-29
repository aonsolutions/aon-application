package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.ArticleConfig;
import com.code.aon.cms.ArticleDetail;
import com.code.aon.cms.Diary;
import com.code.aon.cms.DiaryDetail;
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
import com.code.aon.ui.cms.velocity.attribute.DiaryCalendarHandler;
import com.code.aon.ui.cms.velocity.attribute.DiaryCategoriesHandler;
import com.code.aon.ui.cms.velocity.attribute.NextArticlesHandler;
import com.code.aon.ui.cms.velocity.utils.MonthContent;

public class ArticleCalendarGenerator extends Generator {
	
	private static final Logger LOGGER = Logger.getLogger(ArticleCalendarGenerator.class.getName());
	
	public static void generate() {
		List<ITransferObject> articleList;
		List<ITransferObject> articleDetailList;
		Map<String, MonthContent> months;
		List<MonthContent> monthsList;
		ArrayList<ArticleDetail> dayArticleDetailList;
		ArrayList<ArticleHandler> index_ahlist = new ArrayList<ArticleHandler>();
		DiaryCategoriesHandler dch = null;
		try {
			
			IManagerBean articleDetailBean = BeanManager.getManagerBean(ArticleDetail.class);

			GregorianCalendar currentDate = new GregorianCalendar();
			GregorianCalendar diaryIndexDate = null;

			Criteria articleDetailCriteria;
			
			Article article;
			ArticleDetail articleDetail;
			
	        GregorianCalendar firstDay = new GregorianCalendar();
	        firstDay.set(Calendar.DATE, 1);
	        firstDay.add(Calendar.MONTH, -1);
	        GregorianCalendar lastDay = new GregorianCalendar();
	        lastDay.add(Calendar.MONTH, 1);
	        lastDay.set(Calendar.DATE, MonthContent.diasDelMes(lastDay.get(Calendar.MONTH), lastDay.get(Calendar.YEAR)));

	        Diary diary = getDiary();
	        if (diary !=null)
	        	articleList = ArticleCalendarGenerator.getEventList(firstDay, lastDay, diary.isPastEvents());
	        else
	        	articleList = ArticleCalendarGenerator.getEventList(firstDay, lastDay, true);
	        	
			months = new HashMap<String, MonthContent>();
			monthsList = new ArrayList<MonthContent>();
			
			init(firstDay.getTime(),months,monthsList);
			Map<Integer,ArticleCategoryHandler> map = new HashMap<Integer,ArticleCategoryHandler>(); 
			for (int i=0; i < articleList.size(); i++) {
				article = (Article)articleList.get(i);
				ArticleCalendarGenerator.addArticleCategoryHandler(map, article.getArticleCategory());
				articleDetailCriteria = new Criteria();
				articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), article.getId());
				articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				articleDetailList = (List<ITransferObject>)articleDetailBean.getList(articleDetailCriteria);
				if (articleDetailList.isEmpty()) {
					VelocityUtil.addMessage(" Articulo " + article.getAlias() + " de la categoria " + article.getArticleCategory().getAlias() + " no internacionalizado.", VelocityUtil.WARN);
				}else{
					articleDetail = (ArticleDetail)articleDetailList.get(0);
					Date initDate = article.getInitDate();
					
					if (currentDate.getTime().compareTo(initDate)<=0
							&& index_ahlist.size()<6){
						ArticleHandler ahandler = new ArticleHandler(articleDetail);
						index_ahlist.add(ahandler);
						if (diaryIndexDate == null){
							diaryIndexDate = new GregorianCalendar();
							diaryIndexDate.setTime(initDate);
						}
					}
					
					Date endDate = article.getEndDate();
					if (endDate == null){
						assignArticleToDate(initDate,articleDetail,months,monthsList);
					}else{
						GregorianCalendar initCalendar = new GregorianCalendar();
						initCalendar.setTime(initDate);
						GregorianCalendar endCalendar = new GregorianCalendar();
						endCalendar.setTime(endDate);
						while (initCalendar.compareTo(endCalendar)<=0){
							assignArticleToDate(initCalendar.getTime(),
									articleDetail,months,monthsList);
							initCalendar.add(Calendar.DATE, 1);
						}
					}
				}
			}
			
			Section configSection = GeneratorConfigController.currentSection(ArticleConfig.class);
			
			VelocityUtil vu = new VelocityUtil();
			CommonGenerator.getCommonGenerator().init(vu);
			vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
			vu.initialize();

			CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);

			for (int pos=0; pos < monthsList.size(); ++pos){
				MonthContent monthContent = monthsList.get(pos);
				
				
				Object[] array = monthContent.getValues();
				for (int i = 0;i < array.length; i++){
					dayArticleDetailList = (ArrayList<ArticleDetail>)array[i];
					ArrayList<ArticleHandler> ahlist = new ArrayList<ArticleHandler>();
					if (dayArticleDetailList!=null){
						for (int j=0; j < dayArticleDetailList.size(); j++) {
							articleDetail = dayArticleDetailList.get(j);
							ArticleHandler ahandler = new ArticleHandler(articleDetail);
							ahlist.add(ahandler);
						}
						vu.put("article_list", ahlist);
						
						try{
							vu.put("previous_article_diary", (monthsList.get(0)).getCalendar());
							vu.put("previous_article_diary_year", (monthsList.get(0)).getYear());
							vu.put("previous_article_diary_month", (monthsList.get(0)).getMonth());
						}catch (Throwable th) {
							LOGGER.log(Level.SEVERE, th.getMessage(), th);
						}
						try{
							vu.put("article_diary", (monthsList.get(1)).getCalendar());
							vu.put("article_diary_year", (monthsList.get(1)).getYear());
							vu.put("article_diary_month", (monthsList.get(1)).getMonth());
						}catch (Throwable th) {
							LOGGER.log(Level.SEVERE, th.getMessage(), th);
						}
						try{
							vu.put("next_article_diary", (monthsList.get(2)).getCalendar());
							vu.put("next_article_diary_year", (monthsList.get(2)).getYear());
							vu.put("next_article_diary_month", (monthsList.get(2)).getMonth());
						}catch (Throwable th) {
							LOGGER.log(Level.SEVERE, th.getMessage(), th);
						}
						
						GregorianCalendar calendar = new GregorianCalendar();
						calendar.set(Calendar.YEAR, monthContent.getYear());
						calendar.set(Calendar.MONTH, monthContent.getMonth());
						calendar.set(Calendar.DATE, i+1);
						vu.put("current_date", calendar.get(Calendar.DATE)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR));
						String name = calendar.get(Calendar.YEAR)+"_"+calendar.get(Calendar.MONTH)+"_"+calendar.get(Calendar.DATE);
						VelocityUtil.addMessage(" Generando diario "+name+".", VelocityUtil.INFO);
						generate(vu, Templates.DIARY, name);
						vu.remove("article_list");
						vu.remove("current_date");
						vu.remove("article_diary");
						vu.remove("previous_article_diary");
						vu.remove("next_article_diary");
						vu.remove("article_diary_month");
						vu.remove("article_diary_year");
						vu.remove("previous_article_diary_month");
						vu.remove("previous_article_diary_year");
						vu.remove("next_article_diary_month");
						vu.remove("next_article_diary_year");
					}
					ahlist = null;
				}
			}

			ArrayList<ArticleCategoryHandler> achlist = new ArrayList<ArticleCategoryHandler>();
			for (Iterator<ArticleCategoryHandler> iterator = map.values().iterator(); iterator.hasNext();) {
				achlist.add(iterator.next());
			}
			dch = assignDiaryCategories(achlist);
			
			vu.put("diaryCategories", dch);
			vu.put("article_list", index_ahlist);
			
			try{
				vu.put("previous_article_diary", (monthsList.get(0)).getCalendar());
				vu.put("previous_article_diary_year", (monthsList.get(0)).getYear());
				vu.put("previous_article_diary_month", (monthsList.get(0)).getMonth());
			}catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th);
			}
			try{
				vu.put("article_diary", (monthsList.get(1)).getCalendar());
				vu.put("article_diary_year", (monthsList.get(1)).getYear());
				vu.put("article_diary_month", (monthsList.get(1)).getMonth());
			}catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th);
			}
			try{
				vu.put("next_article_diary", (monthsList.get(2)).getCalendar());
				vu.put("next_article_diary_year", (monthsList.get(2)).getYear());
				vu.put("next_article_diary_month", (monthsList.get(2)).getMonth());
			}catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th);
			}

			VelocityUtil.addMessage(" Generando diario indice.", VelocityUtil.INFO);
			generate(vu, Templates.DIARY, DIARY_INDEX_PAGE);
			vu.remove("diaryCategories");
			vu.remove("article_list");
			vu.remove("article_diary");
			vu.remove("previous_article_diary");
			vu.remove("next_article_diary");
			vu.remove("article_diary_year");
			vu.remove("article_diary_month");
			vu.remove("previous_article_diary_year");
			vu.remove("previous_article_diary_month");
			vu.remove("next_article_diary_year");
			vu.remove("next_article_diary_month");
			vu.finalize();
			vu = null;		
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		} finally {
			articleList = null;
			articleDetailList = null;
			months = null;
			dayArticleDetailList = null;
		}
	}

	private static void addArticleCategoryHandler(Map<Integer,ArticleCategoryHandler> map,ArticleCategory articleCategory) throws ManagerBeanException{
		IManagerBean articleCategoryDetailBean = BeanManager.getManagerBean(ArticleCategoryDetail.class);
		if (map.get(articleCategory.getId())==null){
			Criteria articleCategoryDetailCriteria = new Criteria();
			articleCategoryDetailCriteria.addEqualExpression(articleCategoryDetailBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_ARTICLE_CATEGORY_ID), articleCategory.getId());
			articleCategoryDetailCriteria.addEqualExpression(articleCategoryDetailBean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List<ITransferObject> articleCategoryDetailList = (List<ITransferObject>)articleCategoryDetailBean.getList(articleCategoryDetailCriteria);
			if (articleCategoryDetailList.isEmpty()) {
				VelocityUtil.addMessage(" Categoria de articulos " + articleCategory.getAlias() + " no internacionalizada.", VelocityUtil.WARN);
			}else{
				ArticleCategoryDetail articleCategoryDetail = (ArticleCategoryDetail)articleCategoryDetailList.get(0);
				ArticleCategoryHandler achandler = new ArticleCategoryHandler(articleCategoryDetail,ArticleType.EVENTS,null);
				map.put(articleCategory.getId(), achandler);
			}
		}
	}

	private static Diary getDiary() throws ManagerBeanException{
		IManagerBean diaryBean = BeanManager.getManagerBean(Diary.class);
		List diaryLst = diaryBean.getList(null);
		if (!diaryLst.isEmpty()){
			return (Diary)diaryLst.get(0);
		}
		return null;
	}
	
	private static DiaryCategoriesHandler assignDiaryCategories(ArrayList<ArticleCategoryHandler> achlist) throws ManagerBeanException{
		IManagerBean diaryBean = BeanManager.getManagerBean(Diary.class);
		List diaryLst = diaryBean.getList(null);
		if (!diaryLst.isEmpty()){
			Diary diary = (Diary)diaryLst.get(0);
			IManagerBean diaryDetailBean = BeanManager.getManagerBean(DiaryDetail.class);
			Criteria diaryDetailCriteria = new Criteria();
			diaryDetailCriteria.addEqualExpression(diaryDetailBean.getFieldName(ICMSAlias.DIARY_DETAIL_DIARY_ID), diary.getId());
			diaryDetailCriteria.addEqualExpression(diaryDetailBean.getFieldName(ICMSAlias.DIARY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List diaryDetailLst = diaryDetailBean.getList(diaryDetailCriteria);
			DiaryDetail diaryDetail = null;
			if (!diaryDetailLst.isEmpty()){
				diaryDetail = (DiaryDetail)diaryDetailLst.get(0);
			}
			return new DiaryCategoriesHandler(diaryDetail==null?"":diaryDetail.getContent(),diary.isCategories(),achlist);
		}
		return null;
	}

	private static void assignArticleToDate(Date date_,
			ArticleDetail articleDetail,
			Map<String, MonthContent> months,
			List<MonthContent> monthsList){
		MonthContent monthContent = months.get(MonthContent.parseDateCode(date_));
		if (monthContent==null){
			monthContent = MonthContent.instantiate(date_);
			months.put(monthContent.getCode(), monthContent);
			monthsList.add(monthContent);
		}
		monthContent.assign(date_,articleDetail);
	}

	private static void init(Date initDate, 
			Map<String, MonthContent> months,
			List<MonthContent> monthsList){
		MonthContent monthContent = MonthContent.instantiate(initDate);
		months.put(monthContent.getCode(), monthContent);
		monthsList.add(monthContent);
		
        GregorianCalendar nextDay = new GregorianCalendar();
        nextDay.setTime(initDate);
        nextDay.add(Calendar.MONTH, 1);
        
		monthContent = MonthContent.instantiate(nextDay.getTime());
		months.put(monthContent.getCode(), monthContent);
		monthsList.add(monthContent);

        nextDay.add(Calendar.MONTH, 1);

		monthContent = MonthContent.instantiate(nextDay.getTime());
		months.put(monthContent.getCode(), monthContent);
		monthsList.add(monthContent);
	}
	
	private static List<ITransferObject> getEventList(GregorianCalendar from, GregorianCalendar to, boolean pastEvents) throws ManagerBeanException{
		if (from.compareTo(to)>0)
			return new ArrayList<ITransferObject>();
		IManagerBean articleBean = BeanManager.getManagerBean(Article.class);
		Criteria articleCriteria = new Criteria();
		articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ACTIVE), true);
		articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), ArticleType.EVENTS);
		
        articleCriteria.addLessThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_PUBLISH_DATE), to.getTime());
        articleCriteria.addNotNullExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_INIT_DATE));
        articleCriteria.addLessThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_INIT_DATE), to.getTime());

        if (pastEvents){
    		Expression expirenotnullableExpr = ExpressionUtilities.getNotNullExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE));
            Expression expiregreaterequalExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE), new Date());
    		Expression and_1 = ExpressionUtilities.getAndExpression(expirenotnullableExpr, expiregreaterequalExpr);

    		Expression expirenullableExpr = ExpressionUtilities.getNullExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE));
            Expression endgreaterequalExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_END_DATE), from.getTime());
    		Expression and_2 = ExpressionUtilities.getAndExpression(expirenullableExpr, endgreaterequalExpr);
            
            articleCriteria.addExpression(ExpressionUtilities.getOrExpression(and_1, and_2));
        }else{
        	articleCriteria.addGreaterThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_END_DATE), new Date());
        }

        articleCriteria.addOrder(articleBean.getFieldName(ICMSAlias.ARTICLE_INIT_DATE));
        articleCriteria.getExpression().toString();
		return (List<ITransferObject>)articleBean.getList(articleCriteria);		
	}
	
	private static ArrayList<ArticleHandler> recoverHandlers(List<ITransferObject> articleList){
		List<ITransferObject> articleDetailList;
		ArrayList<ArticleHandler> list = new ArrayList<ArticleHandler>();
		Article article;
		ArticleDetail articleDetail;
		try{
			IManagerBean articleDetailBean = BeanManager.getManagerBean(ArticleDetail.class);
			Criteria articleDetailCriteria;
			
			ArticleHandler ahandler;
			for (int i=0; i < articleList.size(); i++) {
				article = (Article)articleList.get(i);
				articleDetailCriteria = new Criteria();
				articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), article.getId());
				articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				articleDetailList = (List<ITransferObject>)articleDetailBean.getList(articleDetailCriteria);
				if (articleDetailList.isEmpty()) {
					VelocityUtil.addMessage(" Articulo " + article.getAlias() + " de la categoria " + article.getArticleCategory().getAlias() + " no internacionalizado.", VelocityUtil.WARN);
				}else{
					articleDetail = (ArticleDetail)articleDetailList.get(0);
					ahandler = new ArticleHandler(articleDetail);
					list.add(ahandler);
				}
			}
			ahandler = null;
		
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		} finally {
			article = null;
			articleDetail = null;
			articleDetailList = null;
		}
		return list;
	}
	
	public static String DIARY_INDEX_PAGE = "diary_index";
	
	public static Object getNextArticlesHandler() {
		List<ITransferObject> articleList;
		ArrayList<ArticleHandler> day_ahlist = null;
		ArrayList<ArticleHandler> week_ahlist = null;
		ArrayList<ArticleHandler> month_ahlist = null;
		
		GregorianCalendar currentDate = new GregorianCalendar();
		
		try {
	        GregorianCalendar firstDay = new GregorianCalendar();
	        GregorianCalendar lastDay = new GregorianCalendar();
			articleList = ArticleCalendarGenerator.getEventList(firstDay, lastDay,false);
			day_ahlist = recoverHandlers(articleList);
			
	        lastDay = new GregorianCalendar();
	        lastDay.add(Calendar.DATE, 7);
			articleList = ArticleCalendarGenerator.getEventList(firstDay, lastDay,false);
			week_ahlist = recoverHandlers(articleList);
			
	        lastDay = new GregorianCalendar();
	        lastDay.add(Calendar.DATE, 31);
			articleList = ArticleCalendarGenerator.getEventList(firstDay, lastDay,false);
			month_ahlist = recoverHandlers(articleList);
			
			NextArticlesHandler nah = new NextArticlesHandler(currentDate.getTime(),
					day_ahlist,week_ahlist,month_ahlist);

			return nah;
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		} finally {
			articleList = null;
		}
		return null;
	}

	public static Object getDiaryCalendarHandler() {
		List<ITransferObject> articleList;
		List<ITransferObject> articleDetailList;
		Map<String, MonthContent> months;
		List<MonthContent> monthsList;
		try {
			
			IManagerBean articleDetailBean = BeanManager.getManagerBean(ArticleDetail.class);

			Criteria articleDetailCriteria;
			
			Article article;
			ArticleDetail articleDetail;
			
	        GregorianCalendar firstDay = new GregorianCalendar();
	        firstDay.set(Calendar.DATE, 1);
	        firstDay.add(Calendar.MONTH, -1);
	        GregorianCalendar lastDay = new GregorianCalendar();
	        lastDay.add(Calendar.MONTH, 1);
	        lastDay.set(Calendar.DATE, MonthContent.diasDelMes(lastDay.get(Calendar.MONTH), lastDay.get(Calendar.YEAR)));

	        Diary diary = getDiary();
	        if (diary !=null)
	        	articleList = ArticleCalendarGenerator.getEventList(firstDay, lastDay, diary.isPastEvents());
	        else
	        	articleList = ArticleCalendarGenerator.getEventList(firstDay, lastDay, true);
	        	
			months = new HashMap<String, MonthContent>();
			monthsList = new ArrayList<MonthContent>();
			
			init(firstDay.getTime(),months,monthsList);
			Map<Integer,ArticleCategoryHandler> map = new HashMap<Integer,ArticleCategoryHandler>(); 
			for (int i=0; i < articleList.size(); i++) {
				article = (Article)articleList.get(i);
				ArticleCalendarGenerator.addArticleCategoryHandler(map, article.getArticleCategory());
				articleDetailCriteria = new Criteria();
				articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), article.getId());
				articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				articleDetailList = (List<ITransferObject>)articleDetailBean.getList(articleDetailCriteria);
				if (articleDetailList.isEmpty()) {
					VelocityUtil.addMessage(" Articulo " + article.getAlias() + " de la categoria " + article.getArticleCategory().getAlias() + " no internacionalizado.", VelocityUtil.WARN);
				}else{
					articleDetail = (ArticleDetail)articleDetailList.get(0);
					Date initDate = article.getInitDate();
					Date endDate = article.getEndDate();
					if (endDate == null){
						assignArticleToDate(initDate,articleDetail,months,monthsList);
					}else{
						GregorianCalendar initCalendar = new GregorianCalendar();
						initCalendar.setTime(initDate);
						GregorianCalendar endCalendar = new GregorianCalendar();
						endCalendar.setTime(endDate);
						while (initCalendar.compareTo(endCalendar)<=0){
							assignArticleToDate(initCalendar.getTime(),
									articleDetail,months,monthsList);
							initCalendar.add(Calendar.DATE, 1);
						}
					}
				}
			}
			DiaryCalendarHandler diaryCalendarHandler = new DiaryCalendarHandler(monthsList);
			return diaryCalendarHandler;
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		} finally {
			articleList = null;
			articleDetailList = null;
			months = null;
		}
		return null;
	}

}
