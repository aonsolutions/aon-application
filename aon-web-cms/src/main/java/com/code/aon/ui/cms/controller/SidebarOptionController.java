package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.Sidebar;
import com.code.aon.cms.SidebarOption;
import com.code.aon.cms.SidebarOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.cms.controller.support.IOrderedControllerListener;
import com.code.aon.ui.util.AonUtil;


public class SidebarOptionController extends BasicI18nController implements IOrderedControllerListener, ICMSConstants, Constants {

	private static final Logger LOGGER = Logger.getLogger(SidebarOptionController.class.getName());
	
	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.SIDEBAR_OPTION_POSITION);

	private Sidebar currentSidebar;
	
	public Sidebar getCurrentSidebar() {
		return currentSidebar;
	}

	public void setCurrentSidebar(Sidebar currentSidebar) {
		this.currentSidebar = currentSidebar;
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
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
		SidebarOption so = (SidebarOption)this.model.getRowData();
		so.setActive(active);
		getManagerBean().update(so);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		SidebarOptionDetail sod = (SidebarOptionDetail)getModelRowdataI18n();
		if (sod != null) label = sod.getLabel();
		return label;
	}

	public boolean isVisibleIdent() {
		SidebarOption to = (SidebarOption)getTo();
		if (to == null || to.getType() == null) {
			return false;
		}
		if (to.getType()==SidebarType.DIARY_CALENDAR) {
			return false;
		}
		return true;
	}

	public List<SelectItem> getIdents() throws ManagerBeanException, ExpressionException {
		SidebarOption mo = (SidebarOption)getTo();
		List<SelectItem> idents = new LinkedList<SelectItem>();
		if (mo.getType().equals(SidebarType.GENERIC)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getGenericPageList();
		else if (mo.getType().equals(SidebarType.MENU)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getMenuSideList();
		else if (mo.getType().equals(SidebarType.LINK)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getLinkCategoryList();
		else if (mo.getType().equals(SidebarType.BANNER)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getBannerList();
		else if (mo.getType().equals(SidebarType.BANNER_GROUP)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getBannerGroupList();
		else if (mo.getType().equals(SidebarType.ARTICLE)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getArticleList();
		else if (mo.getType().equals(SidebarType.DIRECT_ACCESS)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getDirectAccessGroupList();
		else if (mo.getType().equals(SidebarType.ARTICLE_EVENTS_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getArticleCategoryList();
		else if (mo.getType().equals(SidebarType.ARTICLE_NEWS_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getArticleCategoryList();
		else if (mo.getType().equals(SidebarType.ARTICLE_OTHER_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getArticleCategoryList();
		else if (mo.getType().equals(SidebarType.ARTICLE_SERVICES_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getArticleCategoryList();
		else if (mo.getType().equals(SidebarType.DOWNLOAD_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getDownloadCategoryList();
		else if (mo.getType().equals(SidebarType.ALBUM_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean(COLLECTIONS)).getAlbumCategoryList();
		return idents;
	}

    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveUp(this);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveDown(this);
    }

	public void fireBeforeUseCriteria(Criteria criteria) {
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.SIDEBAR_OPTION_SIDEBAR_ID), "" + getCurrentSidebar().getId());
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	protected void afterRemoveSelected(){
		try {
			orderedControllerSupport.reorderObjects(this);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}