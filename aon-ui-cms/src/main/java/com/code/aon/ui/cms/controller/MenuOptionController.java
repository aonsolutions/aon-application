package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.Menu;
import com.code.aon.cms.MenuOption;
import com.code.aon.cms.MenuOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.controller.support.IOrderedControllerListener;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.cms.util.MenuOptionUtil;


public class MenuOptionController extends BasicI18nController implements IOrderedControllerListener, Constants {

	private final static Logger LOGGER = LoggerFactory.getLogger(MenuOptionController.class);
	
	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.MENU_OPTION_POSITION);

	private Menu currentMenu;
	
	public Menu getCurrentMenu() {
		return currentMenu;
	}

	public void setCurrentMenu(Menu currentMenu) {
		this.currentMenu = currentMenu;
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
		String label = NO_VALUE_LABEL;
		MenuOptionDetail mod = (MenuOptionDetail)getModelRowdataI18n();
		if (mod != null) label = mod.getLabel();
		return label;
	}

	public String getI18nUrl() throws ManagerBeanException {
		String url = NO_VALUE_LABEL;
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

	public List<SelectItem> getIdents() throws ManagerBeanException {
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
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void fireBeforeUseCriteria(Criteria criteria) {
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.MENU_OPTION_MENU_ID), "" + getCurrentMenu().getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public void onTypeChange( ValueChangeEvent event ) throws ManagerBeanException {
		PageType type = (PageType) event.getNewValue();
		MenuOption mo = (MenuOption)getTo();
		if ( type == null ) {
			mo.setLevel( null );
			mo.setIdent( null );			
		} else {
			mo.setLevel( MenuOptionUtil.getDefaultLevel(type) );
			mo.setIdent( null );			
		}
	}
	
}