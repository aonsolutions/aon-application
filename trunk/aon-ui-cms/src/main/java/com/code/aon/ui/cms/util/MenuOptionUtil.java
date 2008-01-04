package com.code.aon.ui.cms.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.CollectionsController;
import com.code.aon.ui.util.AonUtil;

public class MenuOptionUtil {


	public static boolean isVisibleLevel(PageType type) {
		if (type != null) {
			if (type.equals(PageType.LINK)) return true;
			if (type.equals(PageType.FAQ)) return true;
		}
		return false;
	}

	public static boolean isVisibleIdent(PageType type, ContentLevel level) {
		if (type != null) {
			if (type.equals(PageType.MENU)) return true;
			if (type.equals(PageType.GENERIC)) return true;
			if (type.equals(PageType.LINK)){
				if (ContentLevel.CATEGORY.equals(level))
					return true;
			}
			if (type.equals(PageType.FAQ)){
				if (ContentLevel.CATEGORY.equals(level))
					return true;
			}
			if (type.equals(PageType.MODULAR)) return true;
		}
		return false;
	}

	public static boolean isVisibleUrl(PageType type) {
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
		if (type.equals(PageType.GENERIC)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getGenericPageList();
		else if (type.equals(PageType.MODULAR)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getModularPageList();
		else if (type.equals(PageType.MENU)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getMenuList();
		else if (type.equals(PageType.FAQ)){
			if (ContentLevel.CATEGORY.equals(level)){
				idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getFaqCategoryList();
			}
		}else if (type.equals(PageType.LINK)){
			if (ContentLevel.CATEGORY.equals(level)){
				idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getLinkCategoryList();
			}
		}
		return idents;
	}

}
