package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.Sidebar;
import com.code.aon.cms.SidebarOption;
import com.code.aon.cms.SidebarOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;
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
	
	public boolean isVisibleIdent() {
		SidebarOption to = (SidebarOption)getTo();
		if (to != null && to.getType() != null) {
			return true;
		}
		return false;
	}

	public List<SelectItem> getIdents() throws ManagerBeanException, ExpressionException {
		SidebarOption mo = (SidebarOption)getTo();
		List<SelectItem> idents = new LinkedList<SelectItem>();
		if (mo.getType().equals(SidebarType.GENERIC)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getGenericPageList();
		else if (mo.getType().equals(SidebarType.MENU)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getMenuSideList();
		else if (mo.getType().equals(SidebarType.LINK)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getLinkCategoryList();
		else if (mo.getType().equals(SidebarType.BANNER)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getBannerList();
		else if (mo.getType().equals(SidebarType.BANNER_GROUP)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getBannerGroupList();
		else if (mo.getType().equals(SidebarType.ARTICLE)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleList();
		else if (mo.getType().equals(SidebarType.DIRECT_ACCESS)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getDirectAccessGroupList();
		else if (mo.getType().equals(SidebarType.ARTICLE_EVENTS_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleCategoryList();
		else if (mo.getType().equals(SidebarType.ARTICLE_NEWS_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleCategoryList();
		else if (mo.getType().equals(SidebarType.ARTICLE_OTHER_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleCategoryList();
		else if (mo.getType().equals(SidebarType.ARTICLE_SERVICES_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleCategoryList();
		else if (mo.getType().equals(SidebarType.DOWNLOAD_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getDownloadCategoryList();
		return idents;
	}

}