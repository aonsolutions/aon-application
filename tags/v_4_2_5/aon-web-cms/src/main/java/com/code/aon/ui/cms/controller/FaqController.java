package com.code.aon.ui.cms.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Faq;
import com.code.aon.cms.FaqDetail;
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

public class FaqController extends BasicI18nController implements Constants {

	private String question;
	
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
		Faq f = (Faq)this.model.getRowData();
		f.setActive(active);
		getManagerBean().update(f);
	}
	
	public String getI18nAnswer() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		FaqDetail fd = (FaqDetail)getModelRowdataI18n();
		if (fd != null) label = fd.getAnswer();
		return label;
	}

	public String getI18nQuestion() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		FaqDetail fd = (FaqDetail)getModelRowdataI18n();
		if (fd != null) label = fd.getQuestion();
		return label;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		setQuestion(null);
		super.onEditSearch(event);
	}

	public void completeDetailCriteria(String alias_value, String value) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FaqDetail.class);
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(bean.getFieldName(alias_value), value);
			List objects = (List<ITransferObject>)bean.getList(criteria);
			String alias = getFieldName(ICMSAlias.FAQ_ID);
			Expression expr = null;
			for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
				if (expr==null)
					expr = ExpressionUtilities.getExpression(((FaqDetail) iterator.next()).getFaq().getId().toString(),alias);
				else
					expr = ExpressionUtilities.getOrExpression(expr, ExpressionUtilities.getExpression(((FaqDetail) iterator.next()).getFaq().getId().toString(),alias));
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
		if (getQuestion() != null && getQuestion().length()>0) {
			completeDetailCriteria(ICMSAlias.FAQ_DETAIL_QUESTION, getQuestion());
		}
	}

	// -------------------------------------------------
	// Getters y setters para los campos de la búsqueda.
	// -------------------------------------------------

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

}