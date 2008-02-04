package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.ModularPage;
import com.code.aon.cms.ModularPageOption;
import com.code.aon.cms.ModularPageOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class ModularPageOptionController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	private ModularPage currentModularPage;
	
	public ModularPage getCurrentModularPage() {
		return currentModularPage;
	}

	public void setCurrentModularPage(ModularPage currentModularPage) {
		this.currentModularPage = currentModularPage;
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "modular_page_option_form");
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
		ModularPageOption mp = (ModularPageOption)this.model.getRowData();
		mp.setActive(active);
		getManagerBean().update(mp);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "";
		ModularPageOptionDetail mpd = getCurrentDetail();
		if (mpd != null) label = mpd.getLabel();
		return label;
	}

	private ModularPageOptionDetail getCurrentDetail() throws ManagerBeanException {
		ModularPageOptionDetail mpd = null;
		ModularPageOption mp = (ModularPageOption)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_DETAIL_MODULAR_PAGE_OPTION_ID), mp.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			mpd = (ModularPageOptionDetail)list.get(0);
		}
		return mpd;
	}



	@SuppressWarnings("unchecked")
	private void move( ModularPageOption so, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = so.getPosition();
		int newPosition = oldPosition + movement;
		so.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_ID), ""+so.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			ModularPageOption option = (ModularPageOption)list.get(0);
			option.setPosition(newPosition);
			getManagerBean().update(option);
		}
    	List<ModularPageOption> listObjects = (List<ModularPageOption>) this.model.getWrappedData();
		ModularPageOption soMoved = listObjects.get( newPosition );
		soMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_ID), ""+soMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			ModularPageOption option = (ModularPageOption)list.get(0);
			option.setPosition(oldPosition);
			getManagerBean().update(option);
		}
		listObjects.set( newPosition, so);
		listObjects.set( oldPosition, soMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((ModularPageOption) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((ModularPageOption) this.model.getRowData(), 1);    	
    }

	public void reorderObjects() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBean().getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_MODULAR_PAGE_ID),currentModularPage.getId());
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			ModularPageOption so = (ModularPageOption)list.get(i);
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
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_MODULAR_PAGE_ID), "" + getCurrentModularPage().getId());
			criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)getManagerBean().getList(criteria);
			if (list.size() > 0) {
				ModularPageOption so = (ModularPageOption)list.get(0);
				position = so.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
		return position;
	}

	public boolean isVisibleIdent() {
		ModularPageOption to = (ModularPageOption)getTo();
		if (to != null && to.getType() != null) {
			return true;
		}
		return false;
	}

	public List<SelectItem> getIdents() throws ManagerBeanException, ExpressionException {
		ModularPageOption mo = (ModularPageOption)getTo();
		List<SelectItem> idents = new LinkedList<SelectItem>();
		if (mo.getType().equals(ModularPageOptionType.GENERIC)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getGenericPageList();
		else if (mo.getType().equals(ModularPageOptionType.BANNER)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getBannerList();
		else if (mo.getType().equals(ModularPageOptionType.ARTICLE)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleList();
		else if (mo.getType().equals(ModularPageOptionType.ARTICLE_NEWS)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleCategoryList();
		else if (mo.getType().equals(ModularPageOptionType.ARTICLE_EVENTS)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleCategoryList();
		else if (mo.getType().equals(ModularPageOptionType.ARTICLE_SERVICES)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleCategoryList();
		else if (mo.getType().equals(ModularPageOptionType.ARTICLE_OTHER)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleCategoryList();
		else if (mo.getType().equals(ModularPageOptionType.DIRECT_ACCESS_GROUP)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getDirectAccessGroupList();
		else if (mo.getType().equals(ModularPageOptionType.DOWNLOADS)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getDownloadCategoryList();
		else if (mo.getType().equals(ModularPageOptionType.DIRECT_ACCESS)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getDirectAccessList();
		else if (mo.getType().equals(ModularPageOptionType.LINK_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getLinkCategoryList();
		return idents;
	}

}