package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.Faq;
import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.GenericPage;
import com.code.aon.cms.Link;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.Menu;
import com.code.aon.cms.MenuOption;
import com.code.aon.cms.MenuOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.MenuOptionUtil;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class MenuOptionController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	private Menu currentMenu;
	
	public Menu getCurrentMenu() {
		return currentMenu;
	}

	public void setCurrentMenu(Menu currentMenu) {
		this.currentMenu = currentMenu;
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "menu_option_form");
			loadCurrentLanguage();
		}
		cancelOnSelect = false;
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		MenuOption mo = (MenuOption)this.model.getRowData();
		mo.setActive(active);
		getManagerBean().update(mo);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public void onSetSeparator(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true;
		setSeparator(true);
	}
	
	public void onUnsetSeparator(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		setSeparator(false);
	}
	
	private void setSeparator(boolean separator) throws ManagerBeanException {
		MenuOption mo = (MenuOption)this.getSelectedTO();
		mo.setSeparator(separator);
		getManagerBean().update(mo);
	}


	public String getI18nLabel() throws ManagerBeanException {
		String label = "";
		MenuOptionDetail mod = getCurrentDetail();
		if (mod != null) label = mod.getLabel();
		return label;
	}

	public String getI18nUrl() throws ManagerBeanException {
		String url = "";
		MenuOptionDetail mod = getCurrentDetail();
		if (mod != null) url = mod.getUrl();
		return url;
	}

	private MenuOptionDetail getCurrentDetail() throws ManagerBeanException {
		MenuOptionDetail mod = null;
		MenuOption mo = (MenuOption)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.MENU_OPTION_DETAIL_MENU_OPTION_ID), mo.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.MENU_OPTION_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			mod = (MenuOptionDetail)list.get(0);
		}
		return mod;
	}

	public boolean isVisibleLevel() {
		MenuOption mo = (MenuOption)getTo();
		if (mo != null) {
			return MenuOptionUtil.isVisibleLevel(mo.getType());
		}
		return false;
	}

	public boolean isVisibleIdent() {
		MenuOption mo = (MenuOption)getTo();
		if (mo != null) {
			return MenuOptionUtil.isVisibleIdent(mo.getType(), mo.getLevel());
		}
		return false;
	}

	public boolean isVisibleUrl() {
		MenuOption mo = (MenuOption)getTo();
		if (mo != null) {
			return MenuOptionUtil.isVisibleUrl(mo.getType(),mo.getLevel());
		}
		return false;
	}

	public List<SelectItem> getLevels() throws ManagerBeanException, ExpressionException {
		MenuOption mo = (MenuOption)getTo();
		return MenuOptionUtil.getLevels(mo.getType());
	}

	public List<SelectItem> getIdents() throws ManagerBeanException, ExpressionException {
		MenuOption mo = (MenuOption)getTo();
		return MenuOptionUtil.getIdents(mo.getType(),mo.getLevel());
	}

	@SuppressWarnings("unchecked")
	private void move( MenuOption mo, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = mo.getPosition();
		int newPosition = oldPosition + movement;
		mo.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.MENU_OPTION_ID), ""+mo.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			MenuOption option = (MenuOption)list.get(0);
			option.setPosition(newPosition);
			getManagerBean().update(option);
		}
    	List<MenuOption> listObjects = (List<MenuOption>) this.model.getWrappedData();
		MenuOption moMoved = listObjects.get( newPosition );
		moMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.MENU_OPTION_ID), ""+moMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			MenuOption option = (MenuOption)list.get(0);
			option.setPosition(oldPosition);
			getManagerBean().update(option);
		}
		listObjects.set( newPosition, mo);
		listObjects.set( oldPosition, moMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((MenuOption) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((MenuOption) this.model.getRowData(), 1);    	
    }

	public void reorderObjects() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBean().getFieldName(ICMSAlias.MENU_OPTION_MENU_ID),currentMenu.getId());
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.MENU_OPTION_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			MenuOption mo = (MenuOption)list.get(i);
			int oldPosition = mo.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				mo.setPosition(newPosition);
				getManagerBean().update(mo);
			}
		}
	}

	public int getLastPosition() {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.MENU_OPTION_MENU_ID), "" + getCurrentMenu().getId());
			criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.MENU_OPTION_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)getManagerBean().getList(criteria);
			if (list.size() > 0) {
				MenuOption mo = (MenuOption)list.get(0);
				position = mo.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
		return position;
	}

}