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
	
	public static List<MenuOptionHandler> getMenuOptionList(Menu menu) {
		List<MenuOptionHandler> list = new ArrayList<MenuOptionHandler>();
		if ( menu != null ) {
			try {
				IManagerBean moBean = BeanManager.getManagerBean(MenuOption.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(moBean.getFieldName(ICMSAlias.MENU_OPTION_MENU_ID), menu.getId());
				criteria.addEqualExpression(moBean.getFieldName(ICMSAlias.MENU_OPTION_ACTIVE), true);
				criteria.addOrder(moBean.getFieldName(ICMSAlias.MENU_OPTION_POSITION));
				List<ITransferObject> l = (List<ITransferObject>)moBean.getList(criteria);
				if (l.isEmpty())
					getLogger().warning("El menu "+menu.getAlias()+" no tiene opciones");
				for (int i = 0; i < l.size(); i++) {
					MenuOption mo = (MenuOption)l.get(i);
					IManagerBean modBean = BeanManager.getManagerBean(MenuOptionDetail.class);
					Criteria criteria_detail = new Criteria();
					criteria_detail.addEqualExpression(modBean.getFieldName(ICMSAlias.MENU_OPTION_DETAIL_MENU_OPTION_ID), mo.getId());
					criteria_detail.addEqualExpression(modBean.getFieldName(ICMSAlias.MENU_OPTION_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
					List<ITransferObject> ld = (List<ITransferObject>)modBean.getList(criteria_detail);
					if (ld.isEmpty()) {
						getLogger().warning("La opcion de menu "+mo.getAlias()+" no esta internacionalizada");
					}else{
						MenuOptionDetail mod = (MenuOptionDetail)ld.get(0);
						MenuOptionHandler moh = new MenuOptionHandler(mod);
						list.add(moh);
					}
				}
			} catch (ManagerBeanException e) {
				getLogger().error(e.getMessage());
			}
		}
		return list;
	}
	
	public static List<MenuOptionHandler> getMenuOptionList(Integer id) {
		MenuHandler mh = getMenuHandler(id);
		if ( mh != null ) {
			return mh.getList();
		}
		return null; 
	}
	
	public static MenuHandler getMenuHandler(Integer id) {
		try {
			IManagerBean menuBean = BeanManager.getManagerBean(Menu.class);
			Menu menu = (Menu) menuBean.get(id);
			if ( menu == null ) {
				getLogger().error("EL MENU "+id+" REFERENCIADO NO EXISTE");
			}else{
				MenuHandler mh = new MenuHandler(menu);
				return mh;
			}
		} catch (ManagerBeanException e) {
			getLogger().error(e.getMessage());
		}
		return null;
	}	

	public void generate() {
		VelocityUtil vu = context.initVelocityUtil();	
		try {
			IManagerBean bean = BeanManager.getManagerBean(Menu.class);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(null);
			for (int i=0; i < l.size(); i++) {
				Menu menu = (Menu)l.get(i);
				List<MenuOptionHandler> menu_list = getMenuOptionList(menu);
				if (menu_list != null && menu_list.size() > 0) {  
					vu.put(MENU_LIST_KEY, menu_list);
					logger.info(" Generando Menu " + menu.getAlias() + ".");
					generate(vu, Templates.MENU, menu.getAlias());
					vu.remove(MENU_LIST_KEY);
				}
			}
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		}
	}

}
