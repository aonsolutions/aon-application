package com.code.aon.ui.cms.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.cms.util.MenuOptionUtil;
import com.code.aon.ui.util.AonUtil;


public class DirectAccessController extends BasicI18nController {

	private String label;
	
	private String description;
	
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
		DirectAccess da = (DirectAccess)this.model.getRowData();
		da.setActive(active);
		getManagerBean().update(da);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		DirectAccessDetail dad = (DirectAccessDetail)getModelRowdataI18n();
		if (dad != null) label = dad.getLabel();
		return label;
	}

	public String getI18nUrl() throws ManagerBeanException {
		String url = "- NO VALUE -";
		DirectAccessDetail dad = (DirectAccessDetail)getModelRowdataI18n();
		if (dad != null) url = dad.getUrl();
		return url;
	}

	public boolean isVisibleLevel() {
		DirectAccess mo = (DirectAccess)getTo();
		if (mo != null) {
			return MenuOptionUtil.isVisibleLevel(mo.getType());
		}
		return false;
	}

	public boolean isVisibleIdent() {
		DirectAccess mo = (DirectAccess)getTo();
		if (mo != null) {
			return MenuOptionUtil.isVisibleIdent(mo.getType(), mo.getLevel());
		}
		return false;
	}

	public boolean isVisibleUrl() {
		DirectAccess mo = (DirectAccess)getTo();
		if (mo != null) {
			return MenuOptionUtil.isVisibleUrl(mo.getType(),mo.getLevel());
		}
		return false;
	}

	public List<SelectItem> getLevels() throws ManagerBeanException, ExpressionException {
		DirectAccess mo = (DirectAccess)getTo();
		return MenuOptionUtil.getLevels(mo.getType());
	}

	public List<SelectItem> getIdents() throws ManagerBeanException, ExpressionException {
		DirectAccess mo = (DirectAccess)getTo();
		return MenuOptionUtil.getIdents(mo.getType(),mo.getLevel());
	}

	public void onDelImage(ActionEvent event) {
		DirectAccess current = (DirectAccess)getTo();
		current.setImage(null);
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		DirectAccess current = (DirectAccess)getTo();
		current.setImage(image);
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		setLabel(null);
		setDescription(null);
		super.onEditSearch(event);
	}

	public void completeDetailCriteria(String alias_value, String value) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(DirectAccessDetail.class);
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(bean.getFieldName(alias_value), value);
			List objects = (List<ITransferObject>)bean.getList(criteria);
			String alias = getFieldName(ICMSAlias.DIRECT_ACCESS_ID);
			Expression expr = null;
			for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
				if (expr==null)
					expr = ExpressionUtilities.getExpression(((DirectAccessDetail) iterator.next()).getDirectAccess().getId().toString(),alias);
				else
					expr = ExpressionUtilities.getOrExpression(expr, ExpressionUtilities.getExpression(((DirectAccessDetail) iterator.next()).getDirectAccess().getId().toString(),alias));
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
			completeDetailCriteria(ICMSAlias.DIRECT_ACCESS_DETAIL_LABEL, getLabel());
		}
		if (getDescription() != null && getDescription().length()>0) {
			completeDetailCriteria(ICMSAlias.DIRECT_ACCESS_DETAIL_DESCRIPTION, getDescription());
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}