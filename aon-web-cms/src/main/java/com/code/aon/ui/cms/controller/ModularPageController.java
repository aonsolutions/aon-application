package com.code.aon.ui.cms.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.ModularPage;
import com.code.aon.cms.ModularPageDetail;
import com.code.aon.cms.ModularPageOption;
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
import com.code.aon.ui.form.FormUtil;

public class ModularPageController extends BasicI18nController implements ICMSConstants, Constants {

	private String title;
	
	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void onSelectOptions(ActionEvent event) throws ManagerBeanException, ExpressionException {
		ModularPageOptionController mpc = (ModularPageOptionController)FormUtil.getController(MODULAR_PAGE_OPTION);
		IManagerBean mpBean = BeanManager.getManagerBean(ModularPageOption.class);
		ModularPage modularPage = (ModularPage) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(mpBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_MODULAR_PAGE_ID), "" + modularPage.getId());
		criteria.addOrder(mpBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_POSITION));
		mpc.setCurrentModularPage(modularPage);
		mpc.setCriteria(criteria);
		mpc.onSearch(event);
	}

	public void defaultHomepageChanged(ActionEvent event) throws ManagerBeanException, ExpressionException {
		ModularPage modularPage = (ModularPage) model.getRowData();
		disableHomepage();
		modularPage.setHomepage(true);
		modularPage.setActive(true);
		IManagerBean bean = BeanManager.getManagerBean(ModularPage.class);
		bean.update(modularPage);
	}
	
	@SuppressWarnings("unchecked")
	private void disableHomepage() throws ManagerBeanException, ExpressionException {
		IManagerBean bean = BeanManager.getManagerBean(ModularPage.class);
		List<ITransferObject> list = (List<ITransferObject>)model.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			ModularPage modularPage = (ModularPage)list.get(i);
			modularPage.setHomepage(false);
			bean.update(modularPage);
		}
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		ModularPage mp = (ModularPage)this.model.getRowData();
		mp.setActive(active);
		getManagerBean().update(mp);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		ModularPageDetail mpd = (ModularPageDetail)getModelRowdataI18n();
		if (mpd != null) label = mpd.getLabel();
		return label;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		setTitle(null);
		super.onEditSearch(event);
	}

	public void completeDetailCriteria(String alias_value, String value) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ModularPageDetail.class);
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(bean.getFieldName(alias_value), value);
			List objects = (List<ITransferObject>)bean.getList(criteria);
			String alias = getFieldName(ICMSAlias.MODULAR_PAGE_ID);
			Expression expr = null;
			for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
				if (expr==null)
					expr = ExpressionUtilities.getExpression(((ModularPageDetail) iterator.next()).getModular_page().getId().toString(),alias);
				else
					expr = ExpressionUtilities.getOrExpression(expr, ExpressionUtilities.getExpression(((ModularPageDetail) iterator.next()).getModular_page().getId().toString(),alias));
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
		if (getTitle() != null && getTitle().length()>0) {
			completeDetailCriteria(ICMSAlias.MODULAR_PAGE_DETAIL_LABEL, getTitle());
		}
	}

	// -------------------------------------------------
	// Getters y setters para los campos de la búsqueda.
	// -------------------------------------------------
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


}
