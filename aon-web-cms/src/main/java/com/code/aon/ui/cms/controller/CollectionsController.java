package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.code.aon.cms.Activity;
import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.Banner;
import com.code.aon.cms.BannerCategory;
import com.code.aon.cms.Brand;
import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.Download;
import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.Footer;
import com.code.aon.cms.GenericPage;
import com.code.aon.cms.Header;
import com.code.aon.cms.HiruOrganizerCentre;
import com.code.aon.cms.Language;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.Menu;
import com.code.aon.cms.ModularPage;
import com.code.aon.cms.ProductCategory;
import com.code.aon.cms.Section;
import com.code.aon.cms.Sidebar;
import com.code.aon.cms.SportCategory;
import com.code.aon.cms.SportClub;
import com.code.aon.cms.SportNationality;
import com.code.aon.cms.SportPlayer;
import com.code.aon.cms.SportPosition;
import com.code.aon.cms.SportSeason;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.HiruCourseSubject;
import com.code.aon.cms.enumeration.LanguageMenuType;
import com.code.aon.cms.enumeration.MenuType;
import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.cms.enumeration.ModularType;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.cms.enumeration.SidebarSide;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;

public class CollectionsController {

	
	public List<SelectItem> getLanguageTypes() throws ManagerBeanException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item;
		for (LanguageMenuType type : LanguageMenuType.values()) {
			String name = type.getName(locale);
			item = new SelectItem(type, name);
			types.add(item);
		}
		return types;
	}
	
	public List<SelectItem> getLanguages() throws ManagerBeanException {
		List<SelectItem> languajes = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Language.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.LANGUAGE_POSITION));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		SelectItem item;
		for (int i = 0; i < list.size(); i++) {
			Language object = (Language)list.get(i);
			int id = object.getId();
			String name = object.getDescription();
			item = new SelectItem(id, name);
			languajes.add(item);
		}
		return languajes;
	}
	
	public List<SelectItem> getArticleTypes() throws ManagerBeanException {
		List<SelectItem> articleTypes = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item;
		for (ArticleType articleType : ArticleType.values()) {
			String name = articleType.getName(locale);
			item = new SelectItem(articleType, name);
			articleTypes.add(item);
		}
		return articleTypes;
	}

	public List<SelectItem> getBrands() throws ManagerBeanException, ExpressionException {
		List<SelectItem> brands = new LinkedList<SelectItem>();
		IManagerBean brandBean = BeanManager.getManagerBean(Brand.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(brandBean.getFieldName(ICMSAlias.BRAND_ACTIVE), true);
		criteria.addOrder(brandBean.getFieldName(ICMSAlias.BRAND_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)brandBean.getList(criteria);
		SelectItem item = new SelectItem(null, "");
		brands.add(item);
		for (int i = 0; i < list.size(); i++) {
			Brand brand = (Brand)list.get(i);
			int id = brand.getId();
			String name = brand.getAlias();
			item = new SelectItem(id, name);
			brands.add(item);
		}
		return brands;
	}

	public List<SelectItem> getFaqCategoryList() throws ManagerBeanException {
		List<SelectItem> faqCategory = new LinkedList<SelectItem>();
		IManagerBean faqCategoryBean = BeanManager.getManagerBean(FaqCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(faqCategoryBean.getFieldName(ICMSAlias.FAQ_CATEGORY_ACTIVE), true);
		criteria.addOrder(faqCategoryBean.getFieldName(ICMSAlias.FAQ_CATEGORY_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)faqCategoryBean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			FaqCategory gp = (FaqCategory)list.get(i);
			int id = gp.getId();
			String name = gp.getAlias();
			SelectItem item = new SelectItem(id, name);
			faqCategory.add(item);
		}
		return faqCategory;
	}

	public List<SelectItem> getGenericPageList() throws ManagerBeanException {
		List<SelectItem> generics = new LinkedList<SelectItem>();
		IManagerBean genericBean = BeanManager.getManagerBean(GenericPage.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(genericBean.getFieldName(ICMSAlias.GENERIC_PAGE_ACTIVE), true);
		criteria.addOrder(genericBean.getFieldName(ICMSAlias.GENERIC_PAGE_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)genericBean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			GenericPage gp = (GenericPage)list.get(i);
			int id = gp.getId();
			String name = gp.getAlias();
			SelectItem item = new SelectItem(id, name);
			generics.add(item);
		}
		return generics;
	}

	public List<SelectItem> getLinkCategoryList() throws ManagerBeanException {
		List<SelectItem> linkCategory = new LinkedList<SelectItem>();
		IManagerBean linkCategoryBean = BeanManager.getManagerBean(LinkCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(linkCategoryBean.getFieldName(ICMSAlias.LINK_CATEGORY_ACTIVE), true);
		criteria.addOrder(linkCategoryBean.getFieldName(ICMSAlias.LINK_CATEGORY_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)linkCategoryBean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			LinkCategory lc = (LinkCategory)list.get(i);
			int id = lc.getId();
			String name = lc.getAlias();
			SelectItem item = new SelectItem(id, name);
			linkCategory.add(item);
		}
		return linkCategory;
	}

	public List<SelectItem> getMenuTypes() throws ManagerBeanException {
		List<SelectItem> menuTypes = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item = new SelectItem("", "");
		menuTypes.add(item);
		for (MenuType menuType : MenuType.values()) {
			String name = menuType.getName(locale);
			item = new SelectItem(menuType, name);
			menuTypes.add(item);
		}
		return menuTypes;
	}

	public List<SelectItem> getMenuList() throws ManagerBeanException {
		List<SelectItem> menus = new LinkedList<SelectItem>();
		IManagerBean menuBean = BeanManager.getManagerBean(Menu.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(menuBean.getFieldName(ICMSAlias.MENU_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)menuBean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Menu menu = (Menu)list.get(i);
			int id = menu.getId();
			String name = menu.getAlias();
			SelectItem item = new SelectItem(id, name);
			menus.add(item);
		}
		return menus;
	}

	public List<SelectItem> getMenuSideList() throws ManagerBeanException {
		return getMenuList(MenuType.SIDEBAR);
	}

	public List<SelectItem> getMenuTopList() throws ManagerBeanException {
		return getMenuList(MenuType.TOP);
	}

	public List<SelectItem> getMenuFootList() throws ManagerBeanException {
		return getMenuList(MenuType.FOOT);
	}

	public List<SelectItem> getMenuInnerList() throws ManagerBeanException {
		return getMenuList(MenuType.INNER);
	}

	public List<SelectItem> getMenuList(MenuType type) throws ManagerBeanException {
		List<SelectItem> menus = new LinkedList<SelectItem>();
		IManagerBean menuBean = BeanManager.getManagerBean(Menu.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(menuBean.getFieldName(ICMSAlias.MENU_TYPE), type);
		criteria.addOrder(menuBean.getFieldName(ICMSAlias.MENU_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)menuBean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Menu menu = (Menu)list.get(i);
			int id = menu.getId();
			String name = menu.getAlias();
			SelectItem item = new SelectItem(id, name);
			menus.add(item);
		}
		return menus;
	}

	public List<SelectItem> getSidebarList() throws ManagerBeanException {
		List<SelectItem> menus = new LinkedList<SelectItem>();
		IManagerBean sidebarBean = BeanManager.getManagerBean(Sidebar.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(sidebarBean.getFieldName(ICMSAlias.SIDEBAR_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)sidebarBean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Sidebar sidebar = (Sidebar)list.get(i);
			int id = sidebar.getId();
			String name = sidebar.getAlias();
			SelectItem item = new SelectItem(id, name);
			menus.add(item);
		}
		return menus;
	}

	public List<SelectItem> getSidebarTypes() throws ManagerBeanException, ExpressionException {
		DomainUtilities domainUtilities = (DomainUtilities)AonUtil.getRegisteredBean(DomainUtilities.NAME);
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item = new SelectItem("", "");
		types.add(item);
		for (SidebarType sidebarType : SidebarType.values()) {
			if (domainUtilities.hasSidebarType(sidebarType)){
				String name = sidebarType.getName(locale);
				item = new SelectItem(sidebarType, name);
				types.add(item);
			}
		}
		return types;
	}

	public List<SelectItem> getSidebarSides() throws ManagerBeanException, ExpressionException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item;
		for (SidebarSide sidebarSide : SidebarSide.values()) {
			String name = sidebarSide.getName(locale);
			item = new SelectItem(sidebarSide, name);
			types.add(item);
		}
		return types;
	}

	public List<SelectItem> getContentLevels() throws ManagerBeanException, ExpressionException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item;
		for (ContentLevel contentLevel : ContentLevel.values()) {
			String name = contentLevel.getName(locale);
			item = new SelectItem(contentLevel, name);
			types.add(item);
		}
		return types;
	}
	
	public List<SelectItem> getPageTypes() throws ManagerBeanException, ExpressionException {
		DomainUtilities domainUtilities = (DomainUtilities)AonUtil.getRegisteredBean(DomainUtilities.NAME);
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item = new SelectItem("", "");
		types.add(item);
		for (PageType pageType : PageType.values()) {
			if (domainUtilities.hasPageType(pageType)){
				String name = pageType.getName(locale);
				item = new SelectItem(pageType, name);
				types.add(item);
			}
		}
		return types;
	}

	public List<SelectItem> getHeaderList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Header.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.HEADER_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Header header = (Header)list.get(i);
			int id = header.getId();
			String name = header.getAlias();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}

	public List<SelectItem> getFooterList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Footer.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.FOOTER_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Footer footer = (Footer)list.get(i);
			int id = footer.getId();
			String name = footer.getAlias();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}

	public List<SelectItem> getSectionList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Section.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.SECTION_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Section section = (Section)list.get(i);
			int id = section.getId();
			String name = section.getAlias();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}

	public List<SelectItem> getModularTypes() throws ManagerBeanException, ExpressionException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item = new SelectItem("", "");
		types.add(item);
		for (ModularType modularType : ModularType.values()) {
			String name = modularType.getName(locale);
			item = new SelectItem(modularType, name);
			types.add(item);
		}
		return types;
	}

	public List<SelectItem> getModularPageOptionTypes() throws ManagerBeanException, ExpressionException {
		DomainUtilities domainUtilities = (DomainUtilities)AonUtil.getRegisteredBean(DomainUtilities.NAME);
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item = new SelectItem("", "");
		types.add(item);
		for (ModularPageOptionType pageType : ModularPageOptionType.values()) {
			if (domainUtilities.hasModularPageOptionType(pageType)){
				String name = pageType.getName(locale);
				item = new SelectItem(pageType, name);
				types.add(item);
			}
		}
		return types;
	}

	public List<SelectItem> getArticleList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Article.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.ARTICLE_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Article article = (Article)list.get(i);
			int id = article.getId();
			String name = article.getAlias();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}

	public List<SelectItem> getBannerGroupList() throws ManagerBeanException {
		List<SelectItem> select_list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(BannerCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BANNER_CATEGORY_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(ICMSAlias.BANNER_CATEGORY_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			BannerCategory ac = (BannerCategory)list.get(i);
			int id = ac.getId();
			String name = ac.getAlias();
			SelectItem item = new SelectItem(id, name);
			select_list.add(item);
		}
		return select_list;
	}
	
	public List<SelectItem> getBannerList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Banner.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.BANNER_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Banner banner = (Banner)list.get(i);
			int id = banner .getId();
			String name = banner.getAlias();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}
	
	public List<SelectItem> getModularPageList() throws ManagerBeanException {
		List<SelectItem> modular = new LinkedList<SelectItem>();
		IManagerBean modularBean = BeanManager.getManagerBean(ModularPage.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(modularBean.getFieldName(ICMSAlias.MODULAR_PAGE_ACTIVE), true);
		criteria.addOrder(modularBean.getFieldName(ICMSAlias.MODULAR_PAGE_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)modularBean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			ModularPage mp = (ModularPage)list.get(i);
			int id = mp.getId();
			String name = mp.getAlias();
			SelectItem item = new SelectItem(id, name);
			modular.add(item);
		}
		return modular;
	}

	public List<SelectItem> getDirectAccessGroupList() throws ManagerBeanException {
		List<SelectItem> accesses = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(DirectAccessGroup.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			DirectAccessGroup da = (DirectAccessGroup)list.get(i);
			int id = da.getId();
			String name = da.getAlias();
			SelectItem item = new SelectItem(id, name);
			accesses.add(item);
		}
		return accesses;
	}
	
	public List<SelectItem> getDirectAccessList() throws ManagerBeanException {
		List<SelectItem> accesses = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(DirectAccess.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			DirectAccess da = (DirectAccess)list.get(i);
			int id = da.getId();
			String name = da.getAlias();
			SelectItem item = new SelectItem(id, name);
			accesses.add(item);
		}
		return accesses;
	}
	
	public List<SelectItem> getAlbumCategoryList() throws ManagerBeanException {
		List<SelectItem> select_list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(AlbumCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			AlbumCategory ac = (AlbumCategory)list.get(i);
			int id = ac.getId();
			String name = ac.getAlias();
			SelectItem item = new SelectItem(id, name);
			select_list.add(item);
		}
		return select_list;
	}

	public List<SelectItem> getAlbumList() throws ManagerBeanException {
		List<SelectItem> select_list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Album.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(ICMSAlias.ALBUM_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Album a = (Album)list.get(i);
			int id = a.getId();
			String name = a.getAlias();
			SelectItem item = new SelectItem(id, name);
			select_list.add(item);
		}
		return select_list;
	}

	public List<SelectItem> getArticleCategoryList() throws ManagerBeanException {
		List<SelectItem> select_list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(ArticleCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(ICMSAlias.ARTICLE_CATEGORY_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			ArticleCategory ac = (ArticleCategory)list.get(i);
			int id = ac.getId();
			String name = ac.getAlias();
			SelectItem item = new SelectItem(id, name);
			select_list.add(item);
		}
		return select_list;
	}
	
	public List<SelectItem> getArticleByTypeList(ArticleType type) throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Article.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_ARTICLE_TYPE), type);
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ARTICLE_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(ICMSAlias.ARTICLE_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Article article = (Article)list.get(i);
			int id = article.getId();
			String name = article.getAlias();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}

	public List<SelectItem> getDownloadCategoryList() throws ManagerBeanException {
		List<SelectItem> accesses = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(DownloadCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			DownloadCategory da = (DownloadCategory)list.get(i);
			int id = da.getId();
			String name = da.getAlias();
			SelectItem item = new SelectItem(id, name);
			accesses.add(item);
		}
		return accesses;
	}

	public List<SelectItem> getDownloadList() throws ManagerBeanException {
		List<SelectItem> objects = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Download.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(ICMSAlias.DOWNLOAD_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Download d = (Download)list.get(i);
			int id = d.getId();
			String name = d.getAlias();
			SelectItem item = new SelectItem(id, name);
			objects.add(item);
		}
		return objects;
	}

	public List<SelectItem> getAvailableParentSectionList() throws ManagerBeanException {
		SectionController sectionController = (SectionController)AonUtil.getRegisteredBean("section");
		Section current = (Section)sectionController.getTo();
		
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Section.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.SECTION_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		Section section;
		for (int i = 0; i < list.size(); i++) {
			section = (Section)list.get(i);
			if (checkParent(current,section)){
				int id = section.getId();
				String name = section.getAlias();
				SelectItem item = new SelectItem(id, name);
				itemList.add(item);
			}
		}
		return itemList;
	}

	private boolean checkParent(Section current,Section parent){
		if (parent.getId().equals(current.getId()))
			return false;
		if (parent.getParent_()!=null)
			return checkParent(current,parent.getParent_());
		return true;
	}
	
	public List<SelectItem> getActivityList() throws ManagerBeanException {
		List<SelectItem> activityList = new LinkedList<SelectItem>();
		IManagerBean activityBean = BeanManager.getManagerBean(Activity.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(activityBean.getFieldName(ICMSAlias.ACTIVITY_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)activityBean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Activity a = (Activity)list.get(i);
			int id = a.getId();
			String name = a.getAlias();
			SelectItem item = new SelectItem(id, name);
			activityList.add(item);
		}
		return activityList;
	}

	public List<SelectItem> getHiruOrganizerCentreList() throws ManagerBeanException {
		List<SelectItem> select_list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(HiruOrganizerCentre.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.HIRU_ORGANIZER_CENTRE_NAME));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			HiruOrganizerCentre ac = (HiruOrganizerCentre)list.get(i);
			int id = ac.getId();
			String name = ac.getName();
			SelectItem item = new SelectItem(id, name);
			select_list.add(item);
		}
		return select_list;
	}
	
	public List<SelectItem> getHiruCourseSubjects() throws ManagerBeanException {
		List<SelectItem> hiruCourseSubjects = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item;
		for (HiruCourseSubject hiruCourseSubject : HiruCourseSubject.values()) {
			String name = hiruCourseSubject.getName(locale);
			item = new SelectItem(hiruCourseSubject, name);
			hiruCourseSubjects.add(item);
		}
		return hiruCourseSubjects;
	}

	public List<SelectItem> getParentCategories() throws ManagerBeanException, ExpressionException {
		List<SelectItem> categories = new LinkedList<SelectItem>();
		IManagerBean categoryBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(categoryBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_ACTIVE),true);
		criteria.addNullExpression(categoryBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_PARENT_ID));
		List<ITransferObject> list = (List<ITransferObject>)categoryBean.getList(criteria);
		SelectItem item = new SelectItem(null,"------");
		categories.add(item);
		for (int i = 0; i < list.size(); i++) {
			ProductCategory pcd = (ProductCategory)list.get(i);
			item = new SelectItem(pcd.getId(),pcd.getAlias());
			categories.add(item);
		}
		return categories;
	}
	
	public List<SelectItem> getCategories() throws ManagerBeanException, ExpressionException {
		List<SelectItem> categories = new LinkedList<SelectItem>();
		IManagerBean categoryBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(categoryBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_ACTIVE),true);
		criteria.addNullExpression(categoryBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_PARENT_ID));
		List<ITransferObject> list = (List<ITransferObject>)categoryBean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			SelectItem item;
			ProductCategory pcd = (ProductCategory)list.get(i);
			IManagerBean categorySubCatBean = BeanManager.getManagerBean(ProductCategory.class);
			Criteria criteriaSubCat = new Criteria();
			criteriaSubCat.addEqualExpression(categorySubCatBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_ACTIVE),true);
			criteriaSubCat.addExpression(categorySubCatBean.getFieldName(ICMSAlias.PRODUCT_CATEGORY_PARENT_ID), ""+pcd.getId());
			List<ITransferObject> listSubCat = (List<ITransferObject>)categorySubCatBean.getList(criteriaSubCat);
			SelectItem[] subList = new SelectItem[listSubCat.size()];
			for (int j = 0; j < listSubCat.size(); j++) {
				SelectItem subItem;
				ProductCategory pcdSubCat = (ProductCategory)listSubCat.get(j);
				subItem = new SelectItem(pcdSubCat.getId(),"-"+pcdSubCat.getAlias());
				subList[j] = subItem; 
			}
			item = new SelectItemGroup(pcd.getAlias(),pcd.getAlias(),true,subList);
			categories.add(item);
		}
		return categories;
	}

	public List<SelectItem> getSportCategoryList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(SportCategory.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.SPORT_CATEGORY_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			SportCategory object = (SportCategory)list.get(i);
			int id = object.getId();
			String name = object.getAlias();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}
	
	public List<SelectItem> getSportSeasonList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(SportSeason.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.SPORT_SEASON_DESCRIPTION));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			SportSeason object = (SportSeason)list.get(i);
			int id = object.getId();
			String name = object.getDescription();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}
	
	public List<SelectItem> getSportClubList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(SportClub.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.SPORT_CLUB_DESCRIPTION));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			SportClub object = (SportClub)list.get(i);
			int id = object.getId();
			String name = object.getDescription();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}
	
	public List<SelectItem> getSportPositionList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(SportPosition.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.SPORT_POSITION_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			SportPosition object = (SportPosition)list.get(i);
			int id = object.getId();
			String name = object.getAlias();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}
	
	public List<SelectItem> getSportNationalityList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(SportNationality.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.SPORT_NATIONALITY_ALIAS));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			SportNationality object = (SportNationality)list.get(i);
			int id = object.getId();
			String name = object.getAlias();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}
	
	public List<SelectItem> getSportPlayerList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(SportPlayer.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(ICMSAlias.SPORT_PLAYER_NAME));
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			SportPlayer object = (SportPlayer)list.get(i);
			int id = object.getId();
			String name = object.getName();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}
}
