package com.code.aon.ui.cms.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Link;
import com.code.aon.cms.LinkDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.cms.Constants;


public class LinkController extends BasicI18nController implements Constants {

	private String label;
	
	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
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
		Link l = (Link)this.model.getRowData();
		l.setActive(active);
		getManagerBean().update(l);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		LinkDetail ld = (LinkDetail)getModelRowdataI18n();
		if (ld != null) label = ld.getLabel();
		return label;
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		setLabel(null);
		super.onEditSearch(event);
	}

	public void completeDetailCriteria(String alias_value, String value) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(LinkDetail.class);
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(bean.getFieldName(alias_value), value);
			List objects = (List<ITransferObject>)bean.getList(criteria);
			String alias = getFieldName(ICMSAlias.LINK_ID);
			Expression expr = null;
			for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
				if (expr==null)
					expr = ExpressionUtilities.getExpression(((LinkDetail) iterator.next()).getLink().getId().toString(),alias);
				else
					expr = ExpressionUtilities.getOrExpression(expr, ExpressionUtilities.getExpression(((LinkDetail) iterator.next()).getLink().getId().toString(),alias));
			}
			if (expr != null)
				getCriteria().addExpression(expr);
			else
				getCriteria().addExpression(alias, "-1");
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
	}

	public void completeCriteria() throws ManagerBeanException {
		if (getLabel() != null && getLabel().length()>0) {
			completeDetailCriteria(ICMSAlias.LINK_DETAIL_LABEL, getLabel());
		}
	}

	// -------------------------------------------------
	// Getters y setters para los campos de la búsqueda.
	// -------------------------------------------------

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}