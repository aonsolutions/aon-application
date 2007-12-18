package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.Sidebar;
import com.code.aon.cms.SidebarOption;
import com.code.aon.cms.SidebarOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.SidebarSide;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class SidebarOptionController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	private Sidebar currentSidebar;
	
	public Sidebar getCurrentSidebar() {
		return currentSidebar;
	}

	public void setCurrentSidebar(Sidebar currentSidebar) {
		this.currentSidebar = currentSidebar;
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "sidebar_option_form");
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
		SidebarOption so = (SidebarOption)this.model.getRowData();
		so.setActive(active);
		getManagerBean().update(so);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "";
		SidebarOptionDetail sod = getCurrentDetail();
		if (sod != null) label = sod.getLabel();
		return label;
	}

	private SidebarOptionDetail getCurrentDetail() throws ManagerBeanException {
		SidebarOptionDetail sod = null;
		SidebarOption so = (SidebarOption)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.SIDEBAR_OPTION_DETAIL_SIDEBAR_OPTION_ID), so.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.SIDEBAR_OPTION_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			sod = (SidebarOptionDetail)list.get(0);
		}
		return sod;
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

	@SuppressWarnings("unchecked")
	private void move( SidebarOption so, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = so.getPosition();
		int newPosition = oldPosition + movement;
		so.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.SIDEBAR_OPTION_ID), ""+so.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			SidebarOption option = (SidebarOption)list.get(0);
			option.setPosition(newPosition);
			getManagerBean().update(option);
		}
    	List<SidebarOption> listObjects = (List<SidebarOption>) this.model.getWrappedData();
		SidebarOption soMoved = listObjects.get( newPosition );
		soMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.SIDEBAR_OPTION_ID), ""+soMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			SidebarOption option = (SidebarOption)list.get(0);
			option.setPosition(oldPosition);
			getManagerBean().update(option);
		}
		listObjects.set( newPosition, so);
		listObjects.set( oldPosition, soMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((SidebarOption) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((SidebarOption) this.model.getRowData(), 1);    	
    }

	public void reorderObjects() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBean().getFieldName(ICMSAlias.SIDEBAR_OPTION_SIDEBAR_ID),currentSidebar.getId());
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.SIDEBAR_OPTION_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			SidebarOption so = (SidebarOption)list.get(i);
			int oldPosition = so.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				so.setPosition(newPosition);
				getManagerBean().update(so);
			}
		}
	}

	public int getLastPosition() {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.SIDEBAR_OPTION_SIDEBAR_ID), "" + getCurrentSidebar().getId());
			criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.SIDEBAR_OPTION_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)getManagerBean().getList(criteria);
			if (list.size() > 0) {
				SidebarOption so = (SidebarOption)list.get(0);
				position = so.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
		return position;
	}

}