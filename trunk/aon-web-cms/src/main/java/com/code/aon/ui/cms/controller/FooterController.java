package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.cms.Footer;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.util.ExpressionException;


public class FooterController extends BasicI18nController {

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public void defaultChanged(ActionEvent event) throws ManagerBeanException, ExpressionException {
		Footer footer = (Footer) model.getRowData();
		footer.setDefault_(true);
		updateDefault(footer);
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