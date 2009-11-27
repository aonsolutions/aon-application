package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.ArticleDetail;
import com.code.aon.cms.Diary;
import com.code.aon.cms.DiaryDetail;
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
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.ArticleCategoryHandler;
import com.code.aon.ui.cms.velocity.attribute.ArticleHandler;
import com.code.aon.ui.cms.velocity.attribute.DiaryCalendarHandler;
import com.code.aon.ui.cms.velocity.attribute.DiaryCategoriesHandler;
import com.code.aon.ui.cms.velocity.attribute.NextArticlesHandler;
import com.code.aon.ui.cms.velocity.utils.MonthContent;

public class ArticleCalendarGenerator extends Generator {

	private final static Logger LOGGER = LoggerFactory.getLogger(ArticleCalendarGenerator.class);

	public static final String DIARY_INDEX_PAGE = "diary_index";

	private static final String NEXT_ARTICLE_DIARY_MONTH_KEY = "next_article_diary_month";

	private static final String NEXT_ARTICLE_DIARY_YEAR_KEY = "next_article_diary_year";

	private static final String PREVIOUS_ARTICLE_DIARY_MONTH_KEY = "previous_article_diary_month";

	private static final String PREVIOUS_ARTICLE_DIARY_YEAR_KEY = "previous_article_diary_year";

	private static final String ARTICLE_DIARY_MONTH_KEY = "article_diary_month";

	private static final String ARTICLE_DIARY_YEAR_KEY = "article_diary_year";

	private static final String NEXT_ARTICLE_DIARY_KEY = "next_article_diary";

	private static final String PREVIOUS_ARTICLE_DIARY_KEY = "previous_article_diary";

	private static final String ARTICLE_DIARY_KEY = "article_diary";

	private static final String DIARY_CATEGORIES_KEY = "diaryCategories";
	
	public void generate() {
		try {		
			IManagerBean articleDetailBean = BeanManager.getManagerBean(ArticleDetail.class);

			GregorianCalendar currentDate = new GregorianCalendar();
			GregorianCalendar diaryIndexDate = null;

	        GregorianCalendar firstDay = new GregorianCalendar();
	        firstDay.set(Calendar.DATE, 1);
	        firstDay.add(Calendar.MONTH, -1);
	        GregorianCalendar lastDay = new GregorianCalendar();
	        lastDay.add(Calendar.MONTH, 1);
	        lastDay.set(Calendar.DATE, MonthContent.diasDelMes(lastDay.get(Calendar.MONTH), lastDay.get(Calendar.YEAR)));

	        Diary diary = getDiary();
	        List<ITransferObject> articleList; 
	        if (diary !=null)
	        	articleList = ArticleCalendarGenerator.getEventList(firstDay, lastDay, diary.isPastEvents());
	        else
	        	articleList = ArticleCalendarGenerator.getEventList(firstDay, lastDay, true);
	        	
	        Map<String, MonthContent> months = new HashMap<String, MonthContent>();
	        List<MonthContent> monthsList = new ArrayList<MonthContent>();
			
			init(firstDay.getTime(),months,monthsList);
			Map<Integer,ArticleCategoryHandler> map = new HashMap<Integer,ArticleCategoryHandler>(); 
			List<ArticleHandler> index_ahlist = new ArrayList<ArticleHandler>();
			for (int i=0; i < articleList.size(); i++) {
				Article article = (Article) articleList.get(i);
				addArticleCategoryHandler(map, article.getArticleCategory());
				Criteria articleDetailCriteria = new Criteria();
				articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), article.getId());
				articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> articleDetailList = articleDetailBean.getList(articleDetailCriteria);
				if (articleDetailList.isEmpty()) {
					logger.warning("Articulo " + article.getAlias() + " de la categoria " + article.getArticleCategory().getAlias() + " no internacionalizado.");
				}else{
					ArticleDetail articleDetail = (ArticleDetail)articleDetailList.get(0);
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
			
			VelocityUtil vu = context.initVelocityUtil();

			changeDiaryElementContext( vu, diary );

			for (int pos=0; pos < monthsList.size(); ++pos){
				MonthContent monthContent = monthsList.get(pos);
				
				Object[] array = monthContent.getValues();
				for (int i = 0;i < array.length; i++){
					List<ArticleDetail> dayArticleDetailList = (List<ArticleDetail>)array[i];
					List<ArticleHandler> ahlist = new ArrayList<ArticleHandler>();
					if (dayArticleDetailList!=null){
						for (int j=0; j < dayArticleDetailList.size(); j++) {
							ArticleDetail articleDetail = dayArticleDetailList.get(j);
							ArticleHandler ahandler = new ArticleHandler(articleDetail);
							ahlist.add(ahandler);
						}
						vu.put(ARTICLE_LIST_KEY, ahlist);
						
						try{
							vu.put(PREVIOUS_ARTICLE_DIARY_KEY, (monthsList.get(0)).getCalendar());
							vu.put(PREVIOUS_ARTICLE_DIARY_YEAR_KEY, (monthsList.get(0)).getYear());
							vu.put(PREVIOUS_ARTICLE_DIARY_MONTH_KEY, (monthsList.get(0)).getMonth());
						}catch (Throwable th) {
							LOGGER.error(th.getMessage(), th);
						}
						try{
							vu.put(ARTICLE_DIARY_KEY, (monthsList.get(1)).getCalendar());
							vu.put(ARTICLE_DIARY_YEAR_KEY, (monthsList.get(1)).getYear());
							vu.put(ARTICLE_DIARY_MONTH_KEY, (monthsList.get(1)).getMonth());
						}catch (Throwable th) {
							LOGGER.error(th.getMessage(), th);
						}
						try{
							vu.put(NEXT_ARTICLE_DIARY_KEY, (monthsList.get(2)).getCalendar());
							vu.put(NEXT_ARTICLE_DIARY_YEAR_KEY, (monthsList.get(2)).getYear());
							vu.put(NEXT_ARTICLE_DIARY_MONTH_KEY, (monthsList.get(2)).getMonth());
						}catch (Throwable th) {
							LOGGER.error(th.getMessage(), th);
						}
						
						GregorianCalendar calendar = new GregorianCalendar();
						calendar.set(Calendar.YEAR, monthContent.getYear());
						calendar.set(Calendar.MONTH, monthContent.getMonth());
						calendar.set(Calendar.DATE, i+1);
						vu.put("current_date", calendar.get(Calendar.DATE)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR));
						String name = calendar.get(Calendar.YEAR)+"_"+calendar.get(Calendar.MONTH)+"_"+calendar.get(Calendar.DATE);
						logger.info(" Generando diario "+name+".");
						generate(vu, Templates.DIARY, name);
						vu.remove(ARTICLE_LIST_KEY);
						vu.remove("current_date");
						vu.remove(ARTICLE_DIARY_KEY);
						vu.remove(PREVIOUS_ARTICLE_DIARY_KEY);
						vu.remove(NEXT_ARTICLE_DIARY_KEY);
						vu.remove(ARTICLE_DIARY_MONTH_KEY);
						vu.remove(ARTICLE_DIARY_YEAR_KEY);
						vu.remove(PREVIOUS_ARTICLE_DIARY_MONTH_KEY);
						vu.remove(PREVIOUS_ARTICLE_DIARY_YEAR_KEY);
						vu.remove(NEXT_ARTICLE_DIARY_MONTH_KEY);
						vu.remove(NEXT_ARTICLE_DIARY_YEAR_KEY);
					}
					ahlist = null;
				}
			}

			ArrayList<ArticleCategoryHandler> achlist = new ArrayList<ArticleCategoryHandler>();
			for (Iterator<ArticleCategoryHandler> iterator = map.values().iterator(); iterator.hasNext();) {
				achlist.add(iterator.next());
			}
			DiaryCategoriesHandler dch = assignDiaryCategories(diary, achlist);
			
			vu.put(DIARY_CATEGORIES_KEY, dch);
			vu.put(ARTICLE_LIST_KEY, index_ahlist);
			
			try{
				vu.put(PREVIOUS_ARTICLE_DIARY_KEY, (monthsList.get(0)).getCalendar());
				vu.put(PREVIOUS_ARTICLE_DIARY_YEAR_KEY, (monthsList.get(0)).getYear());
				vu.put(PREVIOUS_ARTICLE_DIARY_MONTH_KEY, (monthsList.get(0)).getMonth());
			}catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
			}
			try{
				vu.put(ARTICLE_DIARY_KEY, (monthsList.get(1)).getCalendar());
				vu.put(ARTICLE_DIARY_YEAR_KEY, (monthsList.get(1)).getYear());
				vu.put(ARTICLE_DIARY_MONTH_KEY, (monthsList.get(1)).getMonth());
			}catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
			}
			try{
				vu.put(NEXT_ARTICLE_DIARY_KEY, (monthsList.get(2)).getCalendar());
				vu.put(NEXT_ARTICLE_DIARY_YEAR_KEY, (monthsList.get(2)).getYear());
				vu.put(NEXT_ARTICLE_DIARY_MONTH_KEY, (monthsList.get(2)).getMonth());
			}catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
			}

			logger.info("Generando diario indice.");
			changeDiaryContext(vu, diary);
			generate(vu, Templates.DIARY, DIARY_INDEX_PAGE);
			vu.remove(DIARY_CATEGORIES_KEY);
			vu.remove(ARTICLE_LIST_KEY);
			vu.remove(ARTICLE_DIARY_KEY);
			vu.remove(PREVIOUS_ARTICLE_DIARY_KEY);
			vu.remove(NEXT_ARTICLE_DIARY_KEY);
			vu.remove(ARTICLE_DIARY_YEAR_KEY);
			vu.remove(ARTICLE_DIARY_MONTH_KEY);
			vu.remove(PREVIOUS_ARTICLE_DIARY_YEAR_KEY);
			vu.remove(PREVIOUS_ARTICLE_DIARY_MONTH_KEY);
			vu.remove(NEXT_ARTICLE_DIARY_YEAR_KEY);
			vu.remove(NEXT_ARTICLE_DIARY_MONTH_KEY);	
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		}
	}
	
	private void changeDiaryElementContext(VelocityUtil vu, Diary diary) {
		if ( diary != null ) {
			if (diary.getElementSection()!=null){
				context.changeSection(vu, diary.getElementSection());
			} else {
				changeDiaryContext(vu, diary);
			}			
		} else {
			context.changeDefaultSection(vu);
		}
	}	

	private void changeDiaryContext(VelocityUtil vu, Diary diary) {
		if ( (diary != null) && (diary.getSection()!=null) ) {
			context.changeSection(vu, diary.getSection());
		} else {
			context.changeDefaultSection(vu);
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
				getLogger().warning(" Categoria de articulos " + articleCategory.getAlias() + " no internacionalizada.");
			}else{
				ArticleCategoryDetail articleCategoryDetail = (ArticleCategoryDetail)articleCategoryDetailList.get(0);
				ArticleCategoryHandler achandler = new ArticleCategoryHandler(articleCategoryDetail,ArticleType.EVENTS,null);
				map.put(articleCategory.getId(), achandler);
			}
		}
	}

	private static Diary getDiary() throws ManagerBeanException{
		IManagerBean diaryBean = BeanManager.getManagerBean(Diary.class);
		List<ITransferObject> diaryLst = diaryBean.getList(null);
		if (!diaryLst.isEmpty()){
			return (Diary)diaryLst.get(0);
		}
		return null;
	}
	
	private static DiaryCategoriesHandler assignDiaryCategories(Diary diary, ArrayList<ArticleCategoryHandler> achlist) throws ManagerBeanException{
		if ( diary != null ) {
			IManagerBean diaryDetailBean = BeanManager.getManagerBean(DiaryDetail.class);
			Criteria diaryDetailCriteria = new Criteria();
			diaryDetailCriteria.addEqualExpression(diaryDetailBean.getFieldName(ICMSAlias.DIARY_DETAIL_DIARY_ID), diary.getId());
			diaryDetailCriteria.addEqualExpression(diaryDetailBean.getFieldName(ICMSAlias.DIARY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List<ITransferObject> diaryDetailLst = diaryDetailBean.getList(diaryDetailCriteria);
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
					getLogger().warning(" Articulo " + article.getAlias() + " de la categoria " + article.getArticleCategory().getAlias() + " no internacionalizado.");
				}else{
					articleDetail = (ArticleDetail)articleDetailList.get(0);
					ahandler = new ArticleHandler(articleDetail);
					list.add(ahandler);
				}
			}
			ahandler = null;
		
		} catch (ManagerBeanException e) {
			getLogger().error(e.getMessage());
		} finally {
			article = null;
			articleDetail = null;
			articleDetailList = null;
		}
		return list;
	}
	
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
			getLogger().error(e.getMessage());
		} finally {
			articleList = null;
		}
		return null;
	}

	public static Object getDiaryCalendarHandler() {
		try {
			
			IManagerBean articleDetailBean = BeanManager.getManagerBean(ArticleDetail.class);

	        GregorianCalendar firstDay = new GregorianCalendar();
	        firstDay.set(Calendar.DATE, 1);
	        firstDay.add(Calendar.MONTH, -1);
	        GregorianCalendar lastDay = new GregorianCalendar();
	        lastDay.add(Calendar.MONTH, 1);
	        lastDay.set(Calendar.DATE, MonthContent.diasDelMes(lastDay.get(Calendar.MONTH), lastDay.get(Calendar.YEAR)));

	        Diary diary = getDiary();
			List<ITransferObject> articleList;
	        if (diary !=null)
	        	articleList = ArticleCalendarGenerator.getEventList(firstDay, lastDay, diary.isPastEvents());
	        else
	        	articleList = ArticleCalendarGenerator.getEventList(firstDay, lastDay, true);
	        	
	        Map<String, MonthContent> months = new HashMap<String, MonthContent>();
	        List<MonthContent> monthsList = new ArrayList<MonthContent>();
			
			init(firstDay.getTime(),months,monthsList);
			Map<Integer,ArticleCategoryHandler> map = new HashMap<Integer,ArticleCategoryHandler>(); 
			for (int i=0; i < articleList.size(); i++) {
				Article article = (Article)articleList.get(i);
				addArticleCategoryHandler(map, article.getArticleCategory());
				Criteria articleDetailCriteria = new Criteria();
				articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), article.getId());
				articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> articleDetailList = (List<ITransferObject>)articleDetailBean.getList(articleDetailCriteria);
				if (articleDetailList.isEmpty()) {
					getLogger().warning(" Articulo " + article.getAlias() + " de la categoria " + article.getArticleCategory().getAlias() + " no internacionalizado.");
				}else{
					ArticleDetail articleDetail = (ArticleDetail)articleDetailList.get(0);
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
			getLogger().error(e.getMessage());
		}
		return null;
	}

}
