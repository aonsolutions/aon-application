package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Link;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.LinkDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class LinkController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	private LinkCategory currentLinkCategory;
	
	public LinkCategory getCurrentLinkCategory() {
		return currentLinkCategory;
	}

	public void setCurrentLinkCategory(LinkCategory currentLinkCategory) {
		this.currentLinkCategory = currentLinkCategory;
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "link_form");
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
		Link l = (Link)this.model.getRowData();
		l.setActive(active);
		getManagerBean().update(l);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "";
		LinkDetail ld = getCurrentDetail();
		if (ld != null) label = ld.getLabel();
		return label;
	}

	private LinkDetail getCurrentDetail() throws ManagerBeanException {
		LinkDetail ld = null;
		Link l = (Link)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.LINK_DETAIL_LINK_ID), l.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.LINK_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			ld = (LinkDetail)list.get(0);
		}
		return ld;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	@SuppressWarnings("unchecked")
	private void move( Link l, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = l.getPosition();
		int newPosition = oldPosition + movement;
		l.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.LINK_ID), ""+l.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			Link link = (Link)list.get(0);
			link.setPosition(newPosition);
			getManagerBean().update(link);
		}
    	List<Link> listObjects = (List<Link>) this.model.getWrappedData();
		Link lMoved = listObjects.get( newPosition );
		lMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.LINK_ID), ""+lMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			Link link = (Link)list.get(0);
			link.setPosition(oldPosition);
			getManagerBean().update(link);
		}
		listObjects.set( newPosition, l);
		listObjects.set( oldPosition, lMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((Link) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((Link) this.model.getRowData(), 1);    	
    }

	public void reorderObjects() throws ManagerBeanException{
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.LINK_LINK_CATEGORY_ID), "" + getCurrentLinkCategory().getId());
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.LINK_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Link l = (Link)list.get(i);
			int oldPosition = l.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				l.setPosition(newPosition);
				getManagerBean().update(l);
			}
		}
	}

}