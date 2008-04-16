package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.aon.cms.Article;
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
import com.code.aon.ui.cms.velocity.attribute.ArticleHandler;
import com.code.aon.ui.cms.velocity.utils.MonthContent;

public class ArticleCalendarGenerator extends Generator {
	
	public static void generate() {
		List<ITransferObject> articleList;
		List<ITransferObject> articleDetailList;
		Map<String, MonthContent> months;
		List<MonthContent> monthsList;
		Object[] monthArray;
		ArrayList<ArticleDetail> dayArticleDetailList;
		ArrayList<ArticleHandler> index_ahlist = new ArrayList<ArticleHandler>();
		try {
			IManagerBean articleBean = BeanManager.getManagerBean(Article.class);
			IManagerBean articleDetailBean = BeanManager.getManagerBean(ArticleDetail.class);

			GregorianCalendar currentDate = new GregorianCalendar();
			GregorianCalendar diaryIndexDate = null;

			Criteria articleDetailCriteria;
			
			Article article;
			ArticleDetail articleDetail;
			
			Criteria articleCriteria = new Criteria();
			articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ACTIVE), true);
			articleCriteria.addEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), ArticleType.EVENTS);
			Expression nullableExpr = ExpressionUtilities.getNullExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE));
            Expression greaterExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_EXPIRE_DATE), new Date());
            articleCriteria.addExpression(ExpressionUtilities.getOrExpression(nullableExpr, greaterExpr));
            articleCriteria.addLessThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_PUBLISH_DATE), new Date());
            articleCriteria.addNotNullExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_INIT_DATE));
            GregorianCalendar firstDay = new GregorianCalendar();
            firstDay.set(Calendar.DATE, 1);
            firstDay.add(Calendar.MONTH, -1);
            GregorianCalendar lastDay = new GregorianCalendar();
            lastDay.add(Calendar.MONTH, 1);
            lastDay.set(Calendar.DATE, MonthContent.diasDelMes(lastDay.get(Calendar.MONTH), lastDay.get(Calendar.YEAR)));
            articleCriteria.addGreaterThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_INIT_DATE),firstDay.getTime());
            articleCriteria.addLessThanOrEqualExpression(articleBean.getFieldName(ICMSAlias.ARTICLE_INIT_DATE),lastDay.getTime());
            articleCriteria.addOrder(articleBean.getFieldName(ICMSAlias.ARTICLE_INIT_DATE));
			articleList = (List<ITransferObject>)articleBean.getList(articleCriteria);
			months = new HashMap<String, MonthContent>();
			monthsList = new ArrayList<MonthContent>();
			
			init(firstDay.getTime(),months,monthsList);
			
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
					Date initDate = article.getInitDate();
					
					if (currentDate.getTime().compareTo(initDate)>=0
							&& index_ahlist.size()<6){
						ArticleHandler ahandler = new ArticleHandler(articleDetail);
						index_ahlist.add(ahandler);
						if (diaryIndexDate == null){
							diaryIndexDate = new GregorianCalendar();
							diaryIndexDate.setTime(initDate);
						}
					}
					
					MonthContent monthContent = months.get(MonthContent.parseDateCode(initDate));
					if (monthContent==null){
						monthContent = MonthContent.instantiate(initDate);
						months.put(monthContent.getCode(), monthContent);
						monthsList.add(monthContent);
					}

					
					monthContent.assign(initDate,articleDetail);
					Date endDate = article.getEndDate();
					if (endDate != null){
						GregorianCalendar initCalendar = new GregorianCalendar();
						initCalendar.setTime(initDate);
						GregorianCalendar endCalendar = new GregorianCalendar();
						endCalendar.setTime(endDate);
						while (initCalendar.compareTo(endCalendar)<0){
							initCalendar.add(Calendar.DATE, 1);
							monthContent.assign(initCalendar.getTime(),articleDetail);
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
						if (pos>0){
							vu.put("previous_article_diary", (monthsList.get(pos-1)).getCalendar());
							vu.put("previous_article_diary_month", (monthsList.get(pos-1)).getMonth());
							vu.put("previous_article_diary_year", (monthsList.get(pos-1)).getYear());
						}
						vu.put("article_diary", monthContent.getCalendar());
						vu.put("article_diary_month", monthContent.getMonth());
						vu.put("article_diary_year", monthContent.getYear());
						if ((pos+1) < monthsList.size()){
							vu.put("next_article_diary", (monthsList.get(pos+1)).getCalendar());
							vu.put("next_article_diary_year", (monthsList.get(pos+1)).getYear());
							vu.put("next_article_diary_month", (monthsList.get(pos+1)).getMonth());
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

			
			vu.put("article_list", index_ahlist);

			try{
				vu.put("previous_article_diary", (monthsList.get(0)).getCalendar());
				vu.put("previous_article_diary_year", (monthsList.get(0)).getYear());
				vu.put("previous_article_diary_month", (monthsList.get(0)).getMonth());
			}catch (Exception e) {
			}
			try{
				vu.put("article_diary", (monthsList.get(1)).getCalendar());
				vu.put("article_diary_year", (monthsList.get(1)).getYear());
				vu.put("article_diary_month", (monthsList.get(1)).getMonth());
			}catch (Exception e) {
			}
			try{
				vu.put("next_article_diary", (monthsList.get(2)).getCalendar());
				vu.put("next_article_diary_year", (monthsList.get(2)).getYear());
				vu.put("next_article_diary_month", (monthsList.get(2)).getMonth());
			}catch (Exception e) {
			}

			VelocityUtil.addMessage(" Generando diario indice.", VelocityUtil.INFO);
			generate(vu, Templates.DIARY, DIARY_INDEX_PAGE);
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
			monthArray = null;
			months = null;
			dayArticleDetailList = null;
		}
	}
	
	private static void init(Date initDate, 
			Map<String, MonthContent> months,
			List<MonthContent> monthsList){
		MonthContent monthContent = MonthContent.instantiate(initDate);
		
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
	
	public static String DIARY_INDEX_PAGE = "diary_index";

}
