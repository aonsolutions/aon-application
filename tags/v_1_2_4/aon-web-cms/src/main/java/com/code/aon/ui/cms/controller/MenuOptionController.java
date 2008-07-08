package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.Menu;
import com.code.aon.cms.MenuOption;
import com.code.aon.cms.MenuOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.cms.controller.support.IOrderedControllerListener;
import com.code.aon.ui.cms.util.MenuOptionUtil;


public class MenuOptionController extends BasicI18nController implements IOrderedControllerListener{

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.MENU_OPTION_POSITION);

	private Menu currentMenu;
	
	public Menu getCurrentMenu() {
		return currentMenu;
	}

	public void setCurrentMenu(Menu currentMenu) {
		this.currentMenu = currentMenu;
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event){
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		MenuOption mo = (MenuOption)this.model.getRowData();
		mo.setActive(active);
		getManagerBean().update(mo);
	}
	
	public void onSetSeparator(ActionEvent event) throws ManagerBeanException {
		setSeparator(true);
	}
	
	public void onUnsetSeparator(ActionEvent event) throws ManagerBeanException {
		setSeparator(false);
	}
	
	private void setSeparator(boolean separator) throws ManagerBeanException {
		MenuOption mo = (MenuOption)this.getSelectedTO();
		mo.setSeparator(separator);
		getManagerBean().update(mo);
	}


	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		MenuOptionDetail mod = (MenuOptionDetail)getModelRowdataI18n();
		if (mod != null) label = mod.getLabel();
		return label;
	}

	public String getI18nUrl() throws ManagerBeanException {
		String url = "- NO VALUE -";
		MenuOptionDetail mod = (MenuOptionDetail)getModelRowdataI18n();
		if (mod != null) url = mod.getUrl();
		return url;
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

    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveUp(this);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveDown(this);
    }

	protected void afterRemoveSelected(){
		try {
			orderedControllerSupport.reorderObjects(this);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	public void fireBeforeUseCriteria(Criteria criteria) {
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.MENU_OPTION_MENU_ID), "" + getCurrentMenu().getId());
		} catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
	}

}