package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Faq;
import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.FaqDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class FaqController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	private FaqCategory currentFaqCategory;
	
	public FaqCategory getCurrentFaqCategory() {
		return currentFaqCategory;
	}

	public void setCurrentFaqCategory(FaqCategory currentFaqCategory) {
		this.currentFaqCategory = currentFaqCategory;
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "faq_form");
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
		Faq f = (Faq)this.model.getRowData();
		f.setActive(active);
		getManagerBean().update(f);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nAnswer() throws ManagerBeanException {
		String label = "";
		FaqDetail fd = getCurrentDetail();
		if (fd != null) label = fd.getAnswer();
		return label;
	}

	public String getI18nQuestion() throws ManagerBeanException {
		String label = "";
		FaqDetail fd = getCurrentDetail();
		if (fd != null) label = fd.getQuestion();
		return label;
	}

	private FaqDetail getCurrentDetail() throws ManagerBeanException {
		FaqDetail fd = null;
		Faq f = (Faq)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.FAQ_DETAIL_FAQ_ID), f.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.FAQ_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			fd = (FaqDetail)list.get(0);
		}
		return fd;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	@SuppressWarnings("unchecked")
	private void move( Faq f, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = f.getPosition();
		int newPosition = oldPosition + movement;
		f.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.FAQ_ID), ""+f.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			Faq faq = (Faq)list.get(0);
			faq.setPosition(newPosition);
			getManagerBean().update(faq);
		}
    	List<Faq> listObjects = (List<Faq>) this.model.getWrappedData();
		Faq fMoved = listObjects.get( newPosition );
		fMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.FAQ_ID), ""+fMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			Faq faq = (Faq)list.get(0);
			faq.setPosition(oldPosition);
			getManagerBean().update(faq);
		}
		listObjects.set( newPosition, f);
		listObjects.set( oldPosition, fMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((Faq) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((Faq) this.model.getRowData(), 1);    	
    }

	public void reorderObjects() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.FAQ_FAQ_CATEGORY_ID), "" + getCurrentFaqCategory().getId());
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.FAQ_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Faq f = (Faq)list.get(i);
			int oldPosition = f.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				f.setPosition(newPosition);
				getManagerBean().update(f);
			}
		}
	}

}