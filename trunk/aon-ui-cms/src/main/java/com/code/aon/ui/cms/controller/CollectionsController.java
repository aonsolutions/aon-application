package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.Banner;
import com.code.aon.cms.Brand;
import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.Footer;
import com.code.aon.cms.GenericPage;
import com.code.aon.cms.Header;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.Menu;
import com.code.aon.cms.ModularPage;
import com.code.aon.cms.Section;
import com.code.aon.cms.Sidebar;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.LanguageMenuType;
import com.code.aon.cms.enumeration.MenuType;
import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.cms.enumeration.SidebarSide;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;

public class CollectionsController {

	
	public List<SelectItem> getLanguageTypes() throws ManagerBeanException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item;;
		for (LanguageMenuType type : LanguageMenuType.values()) {
			String name = type.getName(locale);
			item = new SelectItem(type, name);
			types.add(item);
		}
		return types;
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
		List<ITransferObject> list = (List<ITransferObject>)brandBean.getList(criteria);
		SelectItem item;
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
		List<ITransferObject> list = (List<ITransferObject>)menuBean.getList(null);
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

	public List<SelectItem> getMenuList(MenuType type) throws ManagerBeanException {
		List<SelectItem> menus = new LinkedList<SelectItem>();
		IManagerBean menuBean = BeanManager.getManagerBean(Menu.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(menuBean.getFieldName(ICMSAlias.MENU_TYPE), type);
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
		List<ITransferObject> list = (List<ITransferObject>)sidebarBean.getList(null);
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
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item;
		for (SidebarType sidebarType : SidebarType.values()) {
			String name = sidebarType.getName(locale);
			item = new SelectItem(sidebarType, name);
			types.add(item);
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
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item = new SelectItem("", "");
		types.add(item);
		for (PageType pageType : PageType.values()) {
			String name = pageType.getName(locale);
			item = new SelectItem(pageType, name);
			types.add(item);
		}
		return types;
	}

	public List<SelectItem> getHeaderList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Header.class);
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(null);
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
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(null);
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
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(null);
		for (int i = 0; i < list.size(); i++) {
			Section section = (Section)list.get(i);
			int id = section.getId();
			String name = section.getAlias();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}

	public List<SelectItem> getModularPageOptionTypes() throws ManagerBeanException, ExpressionException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item;
		for (ModularPageOptionType pageType : ModularPageOptionType.values()) {
			String name = pageType.getName(locale);
			item = new SelectItem(pageType, name);
			types.add(item);
		}
		return types;
	}

	public List<SelectItem> getArticleList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Article.class);
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(null);
		for (int i = 0; i < list.size(); i++) {
			Article article = (Article)list.get(i);
			int id = article.getId();
			String name = article.getAlias();
			SelectItem item = new SelectItem(id, name);
			itemList.add(item);
		}
		return itemList;
	}

	public List<SelectItem> getBannerList() throws ManagerBeanException {
		List<SelectItem> itemList = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Banner.class);
		List<ITransferObject> list = (List<ITransferObject>)bean.getList(null);
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

	public List<SelectItem> getDirectAccessList() throws ManagerBeanException {
		List<SelectItem> accesses = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(DirectAccessGroup.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_ACTIVE), true);
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
	
	public List<SelectItem> getAlbumCategoryList() throws ManagerBeanException {
		List<SelectItem> select_list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(AlbumCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ACTIVE), true);
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

}
