package com.code.aon.ui.cms.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

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
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.CollectionsController;
import com.code.aon.ui.cms.controller.ICMSConstants;
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

	private static final Logger LOGGER = Logger.getLogger(MenuOptionUtil.class.getName());
	
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
					return true;
			}
		}
		return false;
	}

	public static boolean isVisibleIdent(PageType type, ContentLevel level) {
		if (type != null) {
			if (type.equals(PageType.MENU)) return true;
			if (type.equals(PageType.GENERIC)) return true;
			if (type.equals(PageType.LINK)){
				if (ContentLevel.SECTION.equals(level))
					return true;
				if (ContentLevel.CATEGORY.equals(level))
					return true;
				if (ContentLevel.ELEMENT.equals(level))
					return true;
			}
			if (type.equals(PageType.FAQ)){
				if (ContentLevel.SECTION.equals(level))
					return true;
				if (ContentLevel.CATEGORY.equals(level))
					return true;
				if (ContentLevel.ELEMENT.equals(level))
					return true;
			}
			if (type.equals(PageType.MODULAR)) return true;
			if (type.equals(PageType.DIRECT_ACCESS)) return true;
			if (type.equals(PageType.ALBUM_IMAGES)){
				if (ContentLevel.SECTION.equals(level))
					return true;
				if (ContentLevel.CATEGORY.equals(level))
					return true;
				if (ContentLevel.ELEMENT.equals(level))
					return true;
			}
			if (type.equals(PageType.ARTICLE_NEWS) ||
					type.equals(PageType.ARTICLE_EVENTS) ||
					type.equals(PageType.ARTICLE_SERVICES) ||
					type.equals(PageType.ARTICLE_OTHER) ){
				if (ContentLevel.TOP.equals(level))
					return true;
				if (ContentLevel.SECTION.equals(level))
					return true;
				if (ContentLevel.CATEGORY.equals(level))
					return true;
				if (ContentLevel.ELEMENT.equals(level))
					return true;
			}
			if (type.equals(PageType.DOWNLOAD)){
				if (ContentLevel.SECTION.equals(level))
					return true;
				if (ContentLevel.CATEGORY.equals(level))
					return true;
				if (ContentLevel.ELEMENT.equals(level))
					return true;
			}
			if (type.equals(PageType.SPORT)){
				if (ContentLevel.SECTION.equals(level))
					return true;
				if (ContentLevel.CATEGORY.equals(level))
					return true;
				if (ContentLevel.ELEMENT.equals(level))
					return true;
			}
			if (type.equals(PageType.PRODUCT_CATEGORIES)){
				if (ContentLevel.CATEGORY.equals(level))
					return true;
				if (ContentLevel.ELEMENT.equals(level))
					return true;
			}
			if (type.equals(PageType.BRANDS)){
				if (ContentLevel.ELEMENT.equals(level))
					return true;
			}
		}
		return false;
	}

	public static boolean isNewWindow(PageType type, ContentLevel level) {
		if (type != null) {
			if (type.equals(PageType.EXTERNAL)) return true;
			if (type.equals(PageType.DOWNLOAD) &&
					level.equals(ContentLevel.ELEMENT)) return true;
		}
		return false;
	}
	
	public static boolean isVisibleUrl(PageType type, ContentLevel level) {
		if (type != null) {
			if (type.equals(PageType.EXTERNAL)) return true;
		}
		return false;
	}

	public static List<SelectItem> getLevels(PageType type) {
		List<SelectItem> levels = new LinkedList<SelectItem>();
		if (type != null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			SelectItem item = new SelectItem("", "");
			levels.add(item);
			for (ContentLevel contentLevel : ContentLevel.values()) {
				String name = contentLevel.getName(locale);
				item = new SelectItem(contentLevel, name);
				levels.add(item);
			}
		}
		return levels;
	}

	public static List<SelectItem> getIdents(PageType type, ContentLevel level) throws ManagerBeanException {
		List<SelectItem> idents = new LinkedList<SelectItem>();
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
				if (ContentLevel.SECTION.equals(level)){
					idents = collections.getSectionList();
				} else if (ContentLevel.CATEGORY.equals(level)){
					idents = collections.getFaqCategoryList();
				} else if (ContentLevel.ELEMENT.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				}
				break;
			case LINK:
				if (ContentLevel.SECTION.equals(level)){
					idents = collections.getSectionList();
				} else if (ContentLevel.CATEGORY.equals(level)){
					idents = collections.getLinkCategoryList();
				} else if (ContentLevel.ELEMENT.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				}
				break;
			case ALBUM_IMAGES:
				if (ContentLevel.SECTION.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				} else if (ContentLevel.CATEGORY.equals(level)){
					idents = collections.getAlbumCategoryList();
				} else if (ContentLevel.ELEMENT.equals(level)){
					idents = collections.getAlbumList();
				}
				break;
			case ARTICLE_NEWS:
				if (ContentLevel.TOP.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				} else if (ContentLevel.SECTION.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				} else if (ContentLevel.CATEGORY.equals(level)){
					idents = collections.getArticleCategoryList();
				} else if (ContentLevel.ELEMENT.equals(level)){
					idents = collections.getArticleByTypeList(ArticleType.NEWS);
				}
				break;
			case ARTICLE_EVENTS:
				if (ContentLevel.TOP.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				} else if (ContentLevel.SECTION.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				} else if (ContentLevel.CATEGORY.equals(level)){
					idents = collections.getArticleCategoryList();
				} else if (ContentLevel.ELEMENT.equals(level)){
					idents = collections.getArticleByTypeList(ArticleType.EVENTS);
				}
				break;
			case ARTICLE_SERVICES:
				if (ContentLevel.TOP.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				} else if (ContentLevel.SECTION.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				} else if (ContentLevel.CATEGORY.equals(level)){
					idents = collections.getArticleCategoryList();
				} else if (ContentLevel.ELEMENT.equals(level)){
					idents = collections.getArticleByTypeList(ArticleType.SERVICES);
				}
				break;
			case ARTICLE_OTHER:
				if (ContentLevel.TOP.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				} else if (ContentLevel.SECTION.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				} else if (ContentLevel.CATEGORY.equals(level)){
					idents = collections.getArticleCategoryList();
				} else if (ContentLevel.ELEMENT.equals(level)){
					idents = collections.getArticleByTypeList(ArticleType.OTHER);
				}
				break;
			case DOWNLOAD:
				if (ContentLevel.TOP.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				} else if (ContentLevel.SECTION.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				} else if (ContentLevel.CATEGORY.equals(level)){
					idents = collections.getDownloadCategoryList();
				} else if (ContentLevel.ELEMENT.equals(level)){
					idents = collections.getDownloadList();
				}
				break;
			case SPORT:
				if (ContentLevel.SECTION.equals(level)){
					idents.add(new SelectItem(null,"NO VALID"));
				} else if (ContentLevel.CATEGORY.equals(level)){
					idents = collections.getSportCategoryList();
				} else if (ContentLevel.ELEMENT.equals(level)){
					idents = collections.getSportClubList();
				}
				break;
			case PRODUCT_CATEGORIES:
				try {
					if (ContentLevel.TOP.equals(level)){
						idents.add(new SelectItem(null,"NO VALID"));
					} else if (ContentLevel.SECTION.equals(level)){
						idents.add(new SelectItem(null,"NO VALID"));
					} else if (ContentLevel.CATEGORY.equals(level)){
						idents = collections.getParentCategories();
					} else if (ContentLevel.ELEMENT.equals(level)){
						idents = collections.getCategories();
					}
				} catch (ExpressionException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
				break;
			case BRANDS:
				try {
					if (ContentLevel.TOP.equals(level)){
						idents.add(new SelectItem(null,"NO VALID"));
					} else if (ContentLevel.SECTION.equals(level)){
						idents.add(new SelectItem(null,"NO VALID"));
					} else if (ContentLevel.CATEGORY.equals(level)){
						idents = collections.getParentCategories();
					} else if (ContentLevel.ELEMENT.equals(level)){
						idents = collections.getBrands();
					}
				} catch (ExpressionException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
				break;
		}
		return idents;
	}

	public static String getMenuOptionLink(Integer ident, PageType pageType, ContentLevel level, String url) {
		if (pageType == PageType.EXTERNAL) {
			if (url != null && !url.trim().equals("")) return url;
			return null;
		}
		if (pageType == PageType.GENERIC) {
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
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
		if (pageType == PageType.DIRECT_ACCESS) {
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
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
		if (pageType == PageType.MENU) {
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
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
		if (pageType == PageType.LINK) {
			try {
				if (ContentLevel.TOP.equals(level)){
					String linkCategory = Templates.LINK.getHtmlName();
					linkCategory = linkCategory.replaceAll("%NAME%", LinkGenerator.LINK_CATEGORY_LIST_PAGE);
					return linkCategory;
				} else if (ContentLevel.SECTION.equals(level)){
					String linkCategory = Templates.LINK.getHtmlName();
					linkCategory = linkCategory.replaceAll("%NAME%", LinkGenerator.LINK_CATEGORY_BY_SECTION_PAGE + ident);
					return linkCategory;
				} else if (ContentLevel.CATEGORY.equals(level)){
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
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
		if (pageType == PageType.FAQ) {
			try {
				if (ContentLevel.TOP.equals(level)){
					String faqCategory = Templates.FAQ.getHtmlName();
					faqCategory = faqCategory.replaceAll("%NAME%", FaqGenerator.FAQ_CATEGORY_LIST_PAGE);
					return faqCategory;
				}else if (ContentLevel.SECTION.equals(level)){
					String faqCategory = Templates.FAQ.getHtmlName();
					faqCategory = faqCategory.replaceAll("%NAME%", FaqGenerator.FAQ_CATEGORY_BY_SECTION_PAGE + ident);
					return faqCategory;
				}else if (ContentLevel.CATEGORY.equals(level)){
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
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
		if (pageType == PageType.MODULAR) {
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
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
		if (pageType == PageType.ALBUM_IMAGES) {
			try {
				if (ContentLevel.TOP.equals(level)){
					String category = Templates.ALBUM_CATEGORY.getHtmlName();
					category = category.replaceAll("%NAME%", AlbumGenerator.ALBUM_LIST_PAGE);
					return category;
				} else if (ContentLevel.CATEGORY.equals(level)){
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
				} else if (ContentLevel.ELEMENT.equals(level)){
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
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
		if (pageType == PageType.ARTICLE_NEWS ||
				pageType == PageType.ARTICLE_EVENTS ||
				pageType == PageType.ARTICLE_SERVICES ||
				pageType == PageType.ARTICLE_OTHER ) {
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
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
		if (pageType == PageType.DOWNLOAD) {
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
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
		if (pageType == PageType.DIARY) {
			String link = Templates.DIARY.getHtmlName();
			link = link.replaceAll("%NAME%", ArticleCalendarGenerator.DIARY_INDEX_PAGE);
			return link;
		}
		if (pageType == PageType.PRODUCT_CATEGORIES) {
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
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
		if (pageType == PageType.BRANDS) {
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
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
		if (pageType == PageType.HIRU) {
			String link = Templates.HIRU_COURSES.getHtmlName();
			link = link.replaceAll("%NAME%", HiruGenerator.COURSES_HTML);
			return link;
		}
		if (pageType == PageType.SPORT) {
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
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}		
		return null;
	}
	
}
