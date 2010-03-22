package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Footer;
import com.code.aon.cms.Section;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class FooterController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "footer_form");
			loadCurrentLanguage();
		}
		cancelOnSelect = false;
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public void onAccept(ActionEvent event) {
		try {
			Footer footer = (Footer)getTo();
			IManagerBean bean = BeanManager.getManagerBean(Footer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FOOTER_DEFAULT_), true);
			List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
			if (list.size() == 0) {
				footer.setDefault_(true);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		super.onAccept(event);
	}

	public void defaultChanged(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		boolean selected = ((Boolean)event.getNewValue()).booleanValue();
		Footer footer = (Footer) model.getRowData();
		if (selected) {
			footer.setDefault_(true);
			updateDefault(footer);
		}
		cancelOnSelect = true;
	}
	
	@SuppressWarnings("unchecked")
	private void updateDefault(Footer defaultFooter) throws ManagerBeanException, ExpressionException {
		IManagerBean bean = BeanManager.getManagerBean(Footer.class);
		List<ITransferObject> list = (List<ITransferObject>)model.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			Footer footer = (Footer)list.get(i);
			if (defaultFooter != footer)
				footer.setDefault_(false);
			bean.update(footer);
		}
	}

}