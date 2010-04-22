package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.Menu;
import com.code.aon.cms.MenuOption;
import com.code.aon.cms.MenuOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.MenuHandler;
import com.code.aon.ui.cms.velocity.attribute.MenuOptionHandler;

public class MenuGenerator extends Generator {
	
	public static ArrayList<MenuOptionHandler> getMenuOptionList(Menu menu) {
		ArrayList<MenuOptionHandler> list = new ArrayList<MenuOptionHandler>();

		try {
			IManagerBean moBean = BeanManager.getManagerBean(MenuOption.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(moBean.getFieldName(ICMSAlias.MENU_OPTION_MENU_ID), menu.getId());
			criteria.addEqualExpression(moBean.getFieldName(ICMSAlias.MENU_OPTION_ACTIVE), true);
			criteria.addOrder(moBean.getFieldName(ICMSAlias.MENU_OPTION_POSITION));
			List<ITransferObject> l = (List<ITransferObject>)moBean.getList(criteria);
			for (int i = 0; i < l.size(); i++) {
				MenuOption mo = (MenuOption)l.get(i);
				IManagerBean modBean = BeanManager.getManagerBean(MenuOptionDetail.class);
				Criteria criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(modBean.getFieldName(ICMSAlias.MENU_OPTION_DETAIL_MENU_OPTION_ID), mo.getId());
				criteria_detail.addEqualExpression(modBean.getFieldName(ICMSAlias.MENU_OPTION_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> ld = (List<ITransferObject>)modBean.getList(criteria_detail);
				if (ld.size() > 0) {
					MenuOptionDetail mod = (MenuOptionDetail)ld.get(0);
					MenuOptionHandler moh = new MenuOptionHandler(mod);
					//if (moh.getUrl() != null) list.add(moh);
					list.add(moh);
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static ArrayList<MenuOptionHandler> getMenuOptionList(Integer menu) {
		try {
			IManagerBean menuBean = BeanManager.getManagerBean(Menu.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(menuBean.getFieldName(ICMSAlias.MENU_ID), menu);
			List<ITransferObject> l = (List<ITransferObject>)menuBean.getList(criteria);
			if (l.size() > 0) {
				Menu m = (Menu)l.get(0);
				return getMenuOptionList(m);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null; 
	}

	public static void generate() {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.addMessage("Iniciando proceso de generación", VelocityUtil.INFO);
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Buscando plantilla seleccionada '" + ControllerUtil.getCurrentConfig().getTemplate() + "' ...", VelocityUtil.INFO);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando páginas de menú... ", VelocityUtil.INFO);
		try {
			IManagerBean bean = BeanManager.getManagerBean(Menu.class);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(null);
			for (int i=0; i < l.size(); i++) {
				Menu menu = (Menu)l.get(i);
				ArrayList<MenuOptionHandler> menu_list = getMenuOptionList(menu);
				if (menu_list != null && menu_list.size() > 0) {  
					vu.put("menu_list", menu_list);
					vu.addMessage(" Generando Menu " + menu.getAlias() + ".", VelocityUtil.INFO);
					generate(vu, Templates.MENU, menu.getAlias());
					vu.remove("menu_list");
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		vu.finalize();
		vu = null;
	}
	
	public static Object getMenuHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Menu.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.MENU_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if  (l.size()>0) {
				Menu menu = (Menu)l.get(0);
				MenuHandler mh = new MenuHandler(menu);
				return mh;
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

}
