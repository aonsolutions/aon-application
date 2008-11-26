package com.code.aon.ui.product.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Catalogue;
import com.code.aon.product.TariffCatalogue;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class TariffCatalogueController extends LinesController {

	@SuppressWarnings("unchecked")
	public void catalogueData(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			IManagerBean catalogueBean = BeanManager.getManagerBean(Catalogue.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(catalogueBean.getFieldName(IProductAlias.CATALOGUE_ID), event.getNewValue());
			Iterator iter = catalogueBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				Catalogue catalogue = (Catalogue) iter.next();
				((TariffCatalogue) this.getTo()).setCatalogue(catalogue);
			}
		}
	}
	
}