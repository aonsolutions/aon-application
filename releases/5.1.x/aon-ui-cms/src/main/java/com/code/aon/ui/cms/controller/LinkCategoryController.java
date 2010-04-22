package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.Link;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.LinkConfig;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class LinkCategoryController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	@Override
	public void onReset(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean("generator_config")).initSection(LinkConfig.class);
		super.onReset(event);
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "link_category_form");
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
		LinkCategory linkCategory = (LinkCategory)this.model.getRowData();
		linkCategory.setActive(active);
		getManagerBean().update(linkCategory);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "";
		LinkCategoryDetail linkCategoryDetail = getCurrentDetail();
		if (linkCategoryDetail != null) label = linkCategoryDetail.getLabel();
		return label;
	}

	private LinkCategoryDetail getCurrentDetail() throws ManagerBeanException {
		LinkCategoryDetail linkCategoryDetail = null;
		LinkCategory linkCategory = (LinkCategory)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.LINK_CATEGORY_DETAIL_LINK_CATEGORY_ID), linkCategory.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.LINK_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			linkCategoryDetail = (LinkCategoryDetail)list.get(0);
		}
		return linkCategoryDetail;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	@SuppressWarnings("unchecked")
	private void move( LinkCategory linkCategory, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = linkCategory.getPosition();
		int newPosition = oldPosition + movement;
		linkCategory.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.LINK_CATEGORY_ID), ""+linkCategory.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			LinkCategory linkCat = (LinkCategory)list.get(0);
			linkCat.setPosition(newPosition);
			getManagerBean().update(linkCat);
		}
    	List<LinkCategory> listObjects = (List<LinkCategory>) this.model.getWrappedData();
    	LinkCategory linkCategoryMoved = listObjects.get( newPosition );
    	linkCategoryMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.LINK_CATEGORY_ID), ""+linkCategoryMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			LinkCategory linkCat = (LinkCategory)list.get(0);
			linkCat.setPosition(oldPosition);
			getManagerBean().update(linkCat);
		}
		listObjects.set( newPosition, linkCategory);
		listObjects.set( oldPosition, linkCategoryMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((LinkCategory) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((LinkCategory) this.model.getRowData(), 1);    	
    }
    
	public void onSelectLinks(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true;
		LinkController lc = (LinkController)AonUtil.getController("link");
		IManagerBean moBean = BeanManager.getManagerBean(Link.class);
		LinkCategory linkCategory = (LinkCategory) this.getSelectedTO();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.LINK_LINK_CATEGORY_ID), "" + linkCategory.getId());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.LINK_POSITION));
		lc.setCurrentLinkCategory(linkCategory);
		lc.setCriteria(criteria);
		lc.onSearch(event);
	}

	public void reorderObjects() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.LINK_CATEGORY_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			LinkCategory l = (LinkCategory)list.get(i);
			int oldPosition = l.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				l.setPosition(newPosition);
				getManagerBean().update(l);
			}
		}
	}


}