package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

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
import com.code.aon.ui.util.AonUtil;


public class LinkCategoryController extends BasicI18nController {

	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean("generator_config")).initSection(LinkConfig.class);
		super.onSearch(event);
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event)  {
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
		LinkCategory linkCategory = (LinkCategory)this.model.getRowData();
		linkCategory.setActive(active);
		getManagerBean().update(linkCategory);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		LinkCategoryDetail linkCategoryDetail = (LinkCategoryDetail)getModelRowdataI18n();
		if (linkCategoryDetail != null) label = linkCategoryDetail.getLabel();
		return label;
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
    	move((LinkCategory) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	move((LinkCategory) this.model.getRowData(), 1);    	
    }
    
	public void onSelectLinks(ActionEvent event) throws ManagerBeanException, ExpressionException {
		LinkController lc = (LinkController)AonUtil.getController("link");
		IManagerBean moBean = BeanManager.getManagerBean(Link.class);
		LinkCategory linkCategory = (LinkCategory) this.getTo();
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