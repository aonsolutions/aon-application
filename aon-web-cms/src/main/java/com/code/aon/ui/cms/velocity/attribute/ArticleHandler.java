package com.code.aon.ui.cms.velocity.attribute;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleDetail;
import com.code.aon.cms.ArticleDocument;
import com.code.aon.cms.ArticleDocumentDetail;
import com.code.aon.cms.ArticleRelated;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.velocity.ArticleGenerator;

public class ArticleHandler {
	
	private static final Logger LOGGER = Logger.getLogger(ArticleHandler.class.getName());

	private String title;

	private String subtitle;

	private String content;
	
	private String url;

	private String image;

	private String thumbnail;

	private String image_info;
	
	private String alt;

	private String alt_thumbnail;
	
	private Article article;
	
	private String publishDate;

	private String expireDate;

	private String initDate;

	private String endDate;

	private ArrayList<ArticleHandler> relateds;

	private ArrayList<ArticleDocumentHandler> documents;
	
	private SimpleDateFormat formatter;

	public ArticleHandler (ArticleDetail ad) {
		this.title = ad.getTitle();
		this.subtitle = ad.getSubtitle();
		this.content = ad.getContent();
		this.article = ad.getArticle();
		this.alt = ad.getAlt(); 
		this.alt_thumbnail = ad.getAlt_thumbnail(); 
		this.image_info = ad.getImage_info();
		this.image = this.article.getImage(); 
		this.thumbnail = this.article.getThumbnail();
		
		
		Locale locale = ControllerUtil.getCurrentLanguage().getLanguage().getLocale();
		if ("eu".equals(locale.getLanguage()))
			formatter = new SimpleDateFormat ("dd/MM/yy");
		else
			formatter = (SimpleDateFormat)SimpleDateFormat.getDateInstance(DateFormat.SHORT,locale);

		this.publishDate = "";
		if (this.article.getPublishDate()!=null)
			this.publishDate = formatter.format(this.article.getPublishDate());
		this.expireDate = "";
		if (this.article.getExpireDate()!=null)
			this.expireDate = formatter.format(this.article.getExpireDate());
		this.initDate = "";
		if (this.article.getInitDate()!=null)
			this.initDate = formatter.format(this.article.getInitDate());
		this.endDate = "";
		if (this.article.getEndDate()!=null)
			this.endDate = formatter.format(this.article.getEndDate());
		
		this.url = ArticleGenerator.getTemplate(this.article.getArticleType().ordinal()).getHtmlName();
		this.url = this.url.replaceAll("%NAME%", ad.getArticle().getAlias());
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public String getContent() {
		return content;
	}

	public String getUrl() {
		return url;
	}
	
	public String getImage() {
		return image;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public String getImage_info() {
		return image_info;
	}

	public String getAlt() {
		return alt;
	}

	public String getAlt_thumbnail() {
		return alt_thumbnail;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public String getInitDate() {
		return initDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public ArrayList<ArticleHandler> getRelateds() {
		if (relateds == null){
			relateds = new ArrayList<ArticleHandler>();
			try{
				IManagerBean articleRelatedBean = BeanManager.getManagerBean(ArticleRelated.class);
				Criteria articleRelatedCriteria = new Criteria();
				articleRelatedCriteria.addEqualExpression(articleRelatedBean.getFieldName(ICMSAlias.ARTICLE_RELATED_ARTICLE_PARENT_ID), article.getId());
				List<ITransferObject> articleRelatedList = (List<ITransferObject>)articleRelatedBean.getList(articleRelatedCriteria);
				for (int k=0; k < articleRelatedList.size(); k++) {
					ArticleRelated related = (ArticleRelated)articleRelatedList.get(k);
					IManagerBean articleDetailBean = BeanManager.getManagerBean(ArticleDetail.class);
					Criteria articleDetailCriteria = new Criteria();
					articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_ARTICLE_ID), related.getArticleRelated().getId());
					articleDetailCriteria.addEqualExpression(articleDetailBean.getFieldName(ICMSAlias.ARTICLE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
					List<ITransferObject> articleDetailList = (List<ITransferObject>)articleDetailBean.getList(articleDetailCriteria);
					if (articleDetailList.size() > 0) {
						ArticleDetail articleDetail = (ArticleDetail)articleDetailList.get(0);
						ArticleHandler ahandler = new ArticleHandler(articleDetail);
						relateds.add(ahandler);
					}
				}
			}catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th);
			}
		}
		return relateds;
	}

	public ArrayList<ArticleDocumentHandler> getDocuments() {
		if (documents == null){
			documents = new ArrayList<ArticleDocumentHandler>();
			try{
				IManagerBean articleDocumentBean = BeanManager.getManagerBean(ArticleDocument.class);
				Criteria articleDocumentCriteria = new Criteria();
				articleDocumentCriteria.addEqualExpression(articleDocumentBean.getFieldName(ICMSAlias.ARTICLE_DOCUMENT_ARTICLE_ID), article.getId());
				articleDocumentCriteria.addOrder(articleDocumentBean.getFieldName(ICMSAlias.ARTICLE_DOCUMENT_ID), false);
				List<ITransferObject> articleDocumentList = (List<ITransferObject>)articleDocumentBean.getList(articleDocumentCriteria);
				for (int k=0; k < articleDocumentList.size(); k++) {
					ArticleDocument articleDocument = (ArticleDocument)articleDocumentList.get(k);
					IManagerBean articleDocumentDetailBean = BeanManager.getManagerBean(ArticleDocumentDetail.class);
					Criteria articleDocumentDetailCriteria = new Criteria();
					articleDocumentDetailCriteria.addEqualExpression(articleDocumentDetailBean.getFieldName(ICMSAlias.ARTICLE_DOCUMENT_DETAIL_ARTICLE_DOCUMENT_ID), articleDocument.getId());
					articleDocumentDetailCriteria.addEqualExpression(articleDocumentDetailBean.getFieldName(ICMSAlias.ARTICLE_DOCUMENT_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
					List<ITransferObject> articleDocumentDetailList = (List<ITransferObject>)articleDocumentDetailBean.getList(articleDocumentDetailCriteria);
					if (articleDocumentDetailList.size() > 0) {
						ArticleDocumentDetail articleDocumentDetail = (ArticleDocumentDetail)articleDocumentDetailList.get(0);
						ArticleDocumentHandler adhandler = new ArticleDocumentHandler(articleDocumentDetail);
						documents.add(adhandler);
					}
				}
			}catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th);
			}
		}
		return documents;
	}
	
	
}
