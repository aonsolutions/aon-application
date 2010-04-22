package com.code.aon.ui.cms.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.Activity;
import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.Brand;
import com.code.aon.cms.DirectAccessGroupDetail;
import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.DownloadDetail;
import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.GenericPageDetail;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.MenuOption;
import com.code.aon.cms.ModularPage;
import com.code.aon.cms.ProductCategory;
import com.code.aon.cms.SportCategory;
import com.code.aon.cms.SportClub;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.CollectionsController;
import com.code.aon.ui.cms.controller.ICMSConstants;
import com.code.aon.ui.cms.velocity.ActivityGenerator;
import com.code.aon.ui.cms.velocity.AlbumGenerator;
import com.code.aon.ui.cms.velocity.ArticleCalendarGenerator;
import com.code.aon.ui.cms.velocity.DownloadsGenerator;
import com.code.aon.ui.cms.velocity.FaqGenerator;
import com.code.aon.ui.cms.velocity.HiruGenerator;
import com.code.aon.ui.cms.velocity.LinkGenerator;
import com.code.aon.ui.cms.velocity.ProductGenerator;
import com.code.aon.ui.cms.velocity.SportGenerator;
import com.code.aon.ui.util.AonUtil;

public class MenuOptionUtil implements ICMSConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(MenuOptionUtil.class);

	private static final SelectItem LEVEL_TOP = getSelectItem(ContentLevel.TOP);

	private static final SelectItem LEVEL_SECTION = getSelectItem(ContentLevel.SECTION);
	
	private static final SelectItem LEVEL_CATEGORY = getSelectItem(ContentLevel.CATEGORY);
	
	private static final SelectItem LEVEL_ELEMENT = getSelectItem(ContentLevel.ELEMENT);

	private static SelectItem getSelectItem(ContentLevel level) {
		return new SelectItem( level, level.getName(AonUtil.getCurrentLocale()) );
	}
		
	public static boolean isVisibleLevel(PageType type) {
		if (type != null) {
			switch ( type ) {
				case ALBUM_IMAGES:
				case ARTICLE_EVENTS:
				case ARTICLE_NEWS:
				case ARTICLE_OTHER:
				case ARTICLE_SERVICES:
				case BRANDS:
				case DOWNLOAD:
				case FAQ:
				case LINK:
				case PRODUCT_CATEGORIES:
				case SPORT:
				case ACTIVITY:
					return true;
			}
		}
		return false;
	}

	public static boolean isVisibleIdent(PageType type, ContentLevel level) {
		if (type != null) {
			switch ( type ) {
				case MENU:
				case GENERIC:
				case MODULAR:
				case DIRECT_ACCESS:
					return true;
				case ACTIVITY:					
				case BRANDS:
					return level == ContentLevel.ELEMENT;
				case LINK:
				case FAQ:
				case ALBUM_IMAGES:
				case DOWNLOAD:
				case SPORT:
					if ( (level == ContentLevel.SECTION)
						|| (level == ContentLevel.CATEGORY)
						|| (level == ContentLevel.ELEMENT) ) {
						return true;
					}
					break;
				case ARTICLE_NEWS:
				case ARTICLE_EVENTS:
				case ARTICLE_SERVICES:
				case ARTICLE_OTHER:
				case PRODUCT_CATEGORIES:
					if ( (level == ContentLevel.CATEGORY)
						|| (level == ContentLevel.ELEMENT) ) {
						return true;
					}
					break;
			}
		}
		return false;
	}

	public static boolean isNewWindow(PageType type, ContentLevel level) {
		if (type != null) {
			if (type == PageType.EXTERNAL) return true;
			if (type == PageType.DOWNLOAD &&
					level == ContentLevel.ELEMENT) return true;
		}
		return false;
	}
	
	public static boolean isVisibleUrl(PageType type, ContentLevel level) {
		if (type != null) {
			if (type == PageType.EXTERNAL ) return true;
		}
		return false;
	}

	public static List<SelectItem> getLevels(PageType type) throws ManagerBeanException {
		List<SelectItem> levels = new LinkedList<SelectItem>();
		if (type != null) {
			switch ( type ) {
				case FAQ:
				case LINK:					
					levels.add(LEVEL_TOP);
					if (! getIdents(type, ContentLevel.CATEGORY).isEmpty() ) {
						levels.add(LEVEL_CATEGORY);
					}
					if (! getIdents(type, ContentLevel.SECTION).isEmpty() ) {
						levels.add(LEVEL_SECTION);
					}
					break;
				case ALBUM_IMAGES:
				case DOWNLOAD:
				case SPORT:					
					levels.add(LEVEL_TOP);
					if (! getIdents(type, ContentLevel.CATEGORY).isEmpty() ) {
						levels.add(LEVEL_CATEGORY);
					}
					if (! getIdents(type, ContentLevel.ELEMENT).isEmpty() ) {
						levels.add(LEVEL_ELEMENT);
					}					
					break;
				case ARTICLE_EVENTS:
				case ARTICLE_NEWS:
				case ARTICLE_OTHER:
				case ARTICLE_SERVICES:					
				case BRANDS:
				case PRODUCT_CATEGORIES:					
					if (! getIdents(type, ContentLevel.CATEGORY).isEmpty() ) {
						levels.add(LEVEL_CATEGORY);
					}
					if (! getIdents(type, ContentLevel.ELEMENT).isEmpty() ) {
						levels.add(LEVEL_ELEMENT);
					}					
					break;
				case ACTIVITY:
					levels.add(LEVEL_TOP);
					if (! getIdents(type, ContentLevel.ELEMENT).isEmpty() ) {
						levels.add(LEVEL_ELEMENT);
					}					
					break;
			}
		}
		return levels;
	}

	public static ContentLevel getDefaultLevel(PageType type) throws ManagerBeanException {
		switch ( type ) {
			case ARTICLE_EVENTS:
			case ARTICLE_NEWS:
			case ARTICLE_OTHER:
			case ARTICLE_SERVICES:					
			case BRANDS:
			case PRODUCT_CATEGORIES:					
				if (! getIdents(type, ContentLevel.CATEGORY).isEmpty() ) {
					return ContentLevel.CATEGORY;
				}
				if (! getIdents(type, ContentLevel.ELEMENT).isEmpty() ) {
					return ContentLevel.ELEMENT;
				}					
			default:
				return ContentLevel.TOP;
		}
	}
	
	public static List<SelectItem> getIdents(PageType type, ContentLevel level) throws ManagerBeanException {
		List<SelectItem> idents = Collections.emptyList();
		if ( type != null ) {
			CollectionsController collections = (CollectionsController) AonUtil.getRegisteredBean(COLLECTIONS);
			switch ( type ) {
				case GENERIC:
					idents = collections.getGenericPageList();
					break;
				case MODULAR:
					idents = collections.getModularPageList();
					break;
				case DIRECT_ACCESS:
					idents = collections.getDirectAccessGroupList();
					break;
				case MENU:
					idents = collections.getMenuList();
					break;
				case FAQ:
					if (ContentLevel.SECTION == level){
						idents = collections.getSectionList(true);
					} else if (ContentLevel.CATEGORY == level ){
						idents = collections.getFaqCategoryList();
					}
					break;
				case LINK:
					if (ContentLevel.SECTION == level){
						idents = collections.getSectionList(true);
					} else if (ContentLevel.CATEGORY == level ){
						idents = collections.getLinkCategoryList();
					}
					break;
				case ALBUM_IMAGES:
					if (ContentLevel.CATEGORY == level ){
						idents = collections.getAlbumCategoryList();
					} else if (ContentLevel.ELEMENT == level ){
						idents = collections.getAlbumList();
					}
					break;
				case DOWNLOAD:
					if (ContentLevel.CATEGORY == level ){
						idents = collections.getDownloadCategoryList();
					} else if (ContentLevel.ELEMENT == level ){
						idents = collections.getDownloadList();
					}
					break;
				case SPORT:
					if (ContentLevel.CATEGORY == level ){
						idents = collections.getSportCategoryList();
					} else if (ContentLevel.ELEMENT == level ){
						idents = collections.getSportClubList();
					}
					break;				
				case ARTICLE_NEWS:
					if (ContentLevel.CATEGORY == level ){
						idents = collections.getArticleCategoryList();
					} else if (ContentLevel.ELEMENT == level ){
						idents = collections.getArticleByTypeList(ArticleType.NEWS);
					}
					break;
				case ARTICLE_EVENTS:
					if (ContentLevel.CATEGORY == level ){
						idents = collections.getArticleCategoryList();
					} else if (ContentLevel.ELEMENT == level ){
						idents = collections.getArticleByTypeList(ArticleType.EVENTS);
					}
					break;
				case ARTICLE_SERVICES:
					if (ContentLevel.CATEGORY == level ){
						idents = collections.getArticleCategoryList();
					} else if (ContentLevel.ELEMENT == level ){
						idents = collections.getArticleByTypeList(ArticleType.SERVICES);
					}
					break;
				case ARTICLE_OTHER:
					if (ContentLevel.CATEGORY == level ){
						idents = collections.getArticleCategoryList();
					} else if (ContentLevel.ELEMENT == level ){
						idents = collections.getArticleByTypeList(ArticleType.OTHER);
					}
					break;
				case PRODUCT_CATEGORIES:
					if (ContentLevel.CATEGORY == level ){
						idents = collections.getParentCategories();
					} else if (ContentLevel.ELEMENT == level ){
						idents = collections.getCategories();
					}
					break;
				case BRANDS:
					if (ContentLevel.CATEGORY == level ){
						idents = collections.getParentCategories();
					} else if (ContentLevel.ELEMENT == level ){
						idents = collections.getBrandList(true);
					}
					break;
				case ACTIVITY:
					if (ContentLevel.ELEMENT == level ){
						idents = collections.getActivityList();
					}
					break;
			}
		}
		return idents;
	}

	public static String getMenuOptionLink(Integer ident, PageType pageType, ContentLevel level, String url) {
		switch ( pageType ) {
			case EXTERNAL:
				if (! StringUtils.isBlank(url) ) {
					return url;
				}
				return null;
			case GENERIC:
				try {
					IManagerBean genericBean = BeanManager.getManagerBean(GenericPageDetail.class);
					Criteria criteria_detail = new Criteria();
					criteria_detail.addEqualExpression(genericBean.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_GENERIC_PAGE_ID), ident);
					criteria_detail.addEqualExpression(genericBean.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
					List<ITransferObject> ld = (List<ITransferObject>)genericBean.getList(criteria_detail);
					if (ld.size() > 0) {
						GenericPageDetail gpd = (GenericPageDetail)ld.get(0);
						String link = Templates.GENERIC.getHtmlName();
						link = link.replaceAll("%NAME%", gpd.getGeneric_page().getAlias());
						return link;
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			case DIRECT_ACCESS:
				try {
					IManagerBean directAccessBean = BeanManager.getManagerBean(DirectAccessGroupDetail.class);
					Criteria criteria_detail = new Criteria();
					criteria_detail.addEqualExpression(directAccessBean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_DIRECT_ACCESS_GROUP_ID), ident);
					criteria_detail.addEqualExpression(directAccessBean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
					List<ITransferObject> ld = (List<ITransferObject>)directAccessBean.getList(criteria_detail);
					if (ld.size() > 0) {
						DirectAccessGroupDetail obj = (DirectAccessGroupDetail)ld.get(0);
						String link = Templates.DIRECT_ACCESS.getHtmlName();
						link = link.replaceAll("%NAME%", obj.getDirectAccessGroup().getAlias());
						return link;
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			case MENU:
				try {
					IManagerBean moBean = BeanManager.getManagerBean(MenuOption.class);
					Criteria criteria_detail = new Criteria();
					criteria_detail.addEqualExpression(moBean.getFieldName(ICMSAlias.MENU_OPTION_MENU_ID), ident);
					criteria_detail.addEqualExpression(moBean.getFieldName(ICMSAlias.MENU_OPTION_ACTIVE), true);
					List<ITransferObject> ld = (List<ITransferObject>)moBean.getList(criteria_detail);
					if (ld.size() > 0) {
						MenuOption mo = (MenuOption)ld.get(0);
						String link = Templates.MENU.getHtmlName();
						link = link.replaceAll("%NAME%", mo.getMenu().getAlias());
						return link;
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			case LINK:
				try {
					String linkCategory = Templates.LINK.getHtmlName();
					switch (level) {
						case TOP:
							linkCategory = linkCategory.replaceAll("%NAME%", LinkGenerator.LINK_CATEGORY_LIST_PAGE);
							return linkCategory;
						case SECTION:
							linkCategory = linkCategory.replaceAll("%NAME%", LinkGenerator.LINK_CATEGORY_BY_SECTION_PAGE + ident);
							return linkCategory;
						case CATEGORY:
							IManagerBean bean = BeanManager.getManagerBean(LinkCategory.class);
							Criteria criteria = new Criteria();
							criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_CATEGORY_ID), ident);
							criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_CATEGORY_ACTIVE), true);
							List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
							if (l.size() > 0) {
								LinkCategory lc = (LinkCategory)l.get(0);
								String link = Templates.LINK.getHtmlName();
								link = link.replaceAll("%NAME%", lc.getAlias());
								return link;
							}
							break;
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			case FAQ:
				try {
					String faqCategory = Templates.FAQ.getHtmlName();
					switch ( level ) {
						case TOP:
							faqCategory = faqCategory.replaceAll("%NAME%", FaqGenerator.FAQ_CATEGORY_LIST_PAGE);
							return faqCategory;
						case SECTION:
							faqCategory = faqCategory.replaceAll("%NAME%", FaqGenerator.FAQ_CATEGORY_BY_SECTION_PAGE + ident);
							return faqCategory;
						case CATEGORY:
							IManagerBean bean = BeanManager.getManagerBean(FaqCategory.class);
							Criteria criteria = new Criteria();
							criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_CATEGORY_ID), ident);
							criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_CATEGORY_ACTIVE), true);
							List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
							if (l.size() > 0) {
								FaqCategory fc = (FaqCategory)l.get(0);
								String faq = Templates.FAQ.getHtmlName();
								faq = faq.replaceAll("%NAME%", fc.getAlias());
								return faq;
							}
							break;
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			case MODULAR:
				try {
					IManagerBean modularBean = BeanManager.getManagerBean(ModularPage.class);
					Criteria criteria_detail = new Criteria();
					criteria_detail.addEqualExpression(modularBean.getFieldName(ICMSAlias.MODULAR_PAGE_ID), ident);
					List<ITransferObject> ld = (List<ITransferObject>)modularBean.getList(criteria_detail);
					if (ld.size() > 0) {
						ModularPage mp = (ModularPage)ld.get(0);
						if (mp.isHomepage()){
							String link = Templates.HOME.getHtmlName();
							return link;
						}else{
							String link = Templates.MODULAR.getHtmlName();
							link = link.replaceAll("%NAME%", mp.getAlias());
							return link;
						}
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			case ALBUM_IMAGES:
				try {
					switch (level) {
						case TOP:
							String category = Templates.ALBUM_CATEGORY.getHtmlName();
							category = category.replaceAll("%NAME%", AlbumGenerator.ALBUM_LIST_PAGE);
							return category;
						case CATEGORY:
							{
								IManagerBean bean = BeanManager.getManagerBean(AlbumCategory.class);
								Criteria criteria = new Criteria();
								criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ID), ident);
								criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ACTIVE), true);
								List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
								if (l.size() > 0) {
									AlbumCategory ac = (AlbumCategory)l.get(0);
									String album = Templates.ALBUM_CATEGORY.getHtmlName();
									album = album.replaceAll("%NAME%", ac.getAlias());
									return album;
								}
								break;
							}
						case ELEMENT:
							{
								IManagerBean bean = BeanManager.getManagerBean(Album.class);
								Criteria criteria = new Criteria();
								criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_ID), ident);
								criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_ACTIVE), true);
								List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
								if (l.size() > 0) {
									Album a = (Album)l.get(0);
									String album = Templates.ALBUM.getHtmlName();
									album = album.replaceAll("%NAME%", a.getAlias());
									return album;
								}
								break;
							}
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			case ARTICLE_EVENTS:
			case ARTICLE_NEWS:
			case ARTICLE_SERVICES:
			case ARTICLE_OTHER:
				try {
					if (ContentLevel.CATEGORY.equals(level)){
						IManagerBean bean = BeanManager.getManagerBean(ArticleCategory.class);
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_ID), ident);
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_ACTIVE), true);
						List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
						if (l.size() > 0) {
							ArticleCategory ac = (ArticleCategory)l.get(0);
							String article;
							if (pageType == PageType.ARTICLE_NEWS){
								article = Templates.ARTICLE_NEWS.getHtmlName();
								article = article.replaceAll("%NAME%", ArticleType.NEWS.getName()+"_"+ac.getAlias());
							}else if (pageType == PageType.ARTICLE_EVENTS){
								article = Templates.ARTICLE_EVENTS.getHtmlName();
								article = article.replaceAll("%NAME%", ArticleType.EVENTS.getName()+"_"+ac.getAlias());
							}else if (pageType == PageType.ARTICLE_SERVICES){
								article = Templates.ARTICLE_SERVICES.getHtmlName();
								article = article.replaceAll("%NAME%", ArticleType.SERVICES.getName()+"_"+ac.getAlias());
							}else{
								article = Templates.ARTICLE_OTHER.getHtmlName();
								article = article.replaceAll("%NAME%", ArticleType.OTHER.getName()+"_"+ac.getAlias());
							}
							return article;
						}
					}else if (ContentLevel.ELEMENT.equals(level)){
						IManagerBean bean = BeanManager.getManagerBean(Article.class);
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_ID), ident);
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_ACTIVE), true);
						List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
						if (l.size() > 0) {
							Article a = (Article)l.get(0);
							String article;
							if (pageType == PageType.ARTICLE_NEWS)
								article = Templates.ARTICLE_NEWS.getHtmlName();
							else if (pageType == PageType.ARTICLE_EVENTS)
								article = Templates.ARTICLE_EVENTS.getHtmlName();
							else if (pageType == PageType.ARTICLE_SERVICES)
								article = Templates.ARTICLE_SERVICES.getHtmlName();
							else 
								article = Templates.ARTICLE_OTHER.getHtmlName();
							article = article.replaceAll("%NAME%", a.getAlias());
							return article;
						}
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			case DOWNLOAD:
				try {
					if (ContentLevel.TOP.equals(level)){
						String downloadCategory = Templates.DOWNLOADS.getHtmlName();
						downloadCategory = downloadCategory.replaceAll("%NAME%", DownloadsGenerator.DOWNLOAD_CATEGORY_LIST_PAGE);
						return downloadCategory;
					}else if (ContentLevel.CATEGORY.equals(level)){
						IManagerBean bean = BeanManager.getManagerBean(DownloadCategory.class);
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_ID), ident);
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_ACTIVE), true);
						List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
						if (l.size() > 0) {
							DownloadCategory dc = (DownloadCategory)l.get(0);
							String link = Templates.DOWNLOADS.getHtmlName();
							link = link.replaceAll("%NAME%", dc.getAlias());
							return link;
						}
					}else if (ContentLevel.ELEMENT.equals(level)){
						IManagerBean bean = BeanManager.getManagerBean(DownloadDetail.class);
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_DETAIL_DOWNLOAD_ID), ident);
						List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
						if (l.size() > 0) {
							DownloadDetail d = (DownloadDetail)l.get(0);
							String link = ControllerUtil.getDocumentsPath() + d.getFile();
							return link;
						}
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			case DIARY:
				String dlink = Templates.DIARY.getHtmlName();
				dlink = dlink.replaceAll("%NAME%", ArticleCalendarGenerator.DIARY_INDEX_PAGE);
				return dlink;
			case PRODUCT_CATEGORIES:
				try {
					if (ContentLevel.TOP.equals(level)){
						String link = Templates.PRODUCT_CATEGORY.getHtmlName();
						link = link.replaceAll("%NAME%", ProductGenerator.MAIN_PAGE);
						return link;
					} else if (ContentLevel.CATEGORY.equals(level) || ContentLevel.ELEMENT.equals(level)){
						IManagerBean bean = BeanManager.getManagerBean(ProductCategory.class);
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_ID), ident);
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_ACTIVE), true);
						List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
						if (l.size() > 0) {
							ProductCategory pc = (ProductCategory)l.get(0);
							String link = Templates.PRODUCT_CATEGORY.getHtmlName();
							link = link.replaceAll("%NAME%", pc.getAlias());
							return link;
						}
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			case BRANDS:
				try {
					if (ContentLevel.TOP.equals(level)){
						String link = Templates.BRAND.getHtmlName();
						link = link.replaceAll("%NAME%", ProductGenerator.MAIN_PAGE);
						return link;
					}else if (ContentLevel.CATEGORY.equals(level) || ContentLevel.ELEMENT.equals(level)){
						IManagerBean bean = BeanManager.getManagerBean(Brand.class);
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BRAND_ID), ident);
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BRAND_ACTIVE), true);
						List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
						if (l.size() > 0) {
							Brand b = (Brand)l.get(0);
							String link = Templates.BRAND.getHtmlName();
							link = link.replaceAll("%NAME%", b.getAlias());
							return link;
						}
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			case HIRU:
				String hlink = Templates.HIRU_COURSES.getHtmlName();
				hlink = hlink.replaceAll("%NAME%", HiruGenerator.COURSES_HTML);
				return hlink;
			case SPORT:
				try {
					if (ContentLevel.TOP.equals(level)){
						String page = Templates.SPORT.getHtmlName();
						page = page.replaceAll("%NAME%", SportGenerator.MAIN_PAGE);
						return page;
					}else if (ContentLevel.CATEGORY.equals(level)){
						IManagerBean bean = BeanManager.getManagerBean(SportCategory.class);
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SPORT_CATEGORY_ID), ident);
						List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
						if (l.size() > 0) {
							SportCategory sc = (SportCategory)l.get(0);
							String link = Templates.SPORT.getHtmlName();
							link = link.replaceAll("%NAME%", SportGenerator.CATEGORY + sc.getAlias());
							return link;
						}
					}else if (ContentLevel.ELEMENT.equals(level)){
						IManagerBean bean = BeanManager.getManagerBean(SportClub.class);
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SPORT_CLUB_ID), ident);
						List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
						if (l.size() > 0) {
							SportClub s = (SportClub)l.get(0);
							String link = Templates.SPORT.getHtmlName();
							link = link.replaceAll("%NAME%", SportGenerator.CLUB +s.getId());
							return link;
						}
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			case ACTIVITY:
				try {
					if (ContentLevel.TOP.equals(level)){
						String activityLink = Templates.ACTIVITY.getHtmlName();
						activityLink = activityLink.replaceAll("%NAME%", ActivityGenerator.ACTIVITY_LIST_PAGE);
						return activityLink;
					}else if (ContentLevel.ELEMENT.equals(level)){
						IManagerBean bean = BeanManager.getManagerBean(Activity.class);
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ACTIVITY_ID), ident);
						List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
						if (l.size() > 0) {
							Activity a = (Activity)l.get(0);
							String activityLink = Templates.ACTIVITY.getHtmlName();
							activityLink = activityLink.replaceAll("%NAME%", "ACTIVITY_" + a.getId());
							return activityLink;
						}
					}
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;				
		}		
		return null;
	}
	
}
