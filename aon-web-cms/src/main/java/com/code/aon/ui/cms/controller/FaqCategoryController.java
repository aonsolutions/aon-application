package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Faq;
import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.FaqCategoryDetail;
import com.code.aon.cms.FaqConfig;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.util.AonUtil;


public class FaqCategoryController extends BasicI18nController{

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.FAQ_CATEGORY_POSITION);
	
	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean("generator_config")).initSection(FaqConfig.class);
		super.onSearch(event);
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
		FaqCategory faqCategory = (FaqCategory)this.model.getRowData();
		faqCategory.setActive(active);
		getManagerBean().update(faqCategory);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		FaqCategoryDetail faqCategoryDetail = (FaqCategoryDetail)getModelRowdataI18n();
		if (faqCategoryDetail != null) label = faqCategoryDetail.getLabel();
		return label;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}
    
	public void onSelectFaqs(ActionEvent event) throws ManagerBeanException, ExpressionException {
		FaqController fc = (FaqController)AonUtil.getController("faq");
		IManagerBean moBean = BeanManager.getManagerBean(Faq.class);
		FaqCategory faqCategory = (FaqCategory) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.FAQ_FAQ_CATEGORY_ID), "" + faqCategory.getId());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.FAQ_POSITION));
		fc.setCurrentFaqCategory(faqCategory);
		fc.setCriteria(criteria);
		fc.onSearch(event);
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