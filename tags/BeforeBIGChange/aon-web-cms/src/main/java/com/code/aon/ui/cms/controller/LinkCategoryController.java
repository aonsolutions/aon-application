package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Link;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.LinkConfig;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.util.AonUtil;


public class LinkCategoryController extends BasicI18nController {

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.LINK_CATEGORY_POSITION);
	
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

}