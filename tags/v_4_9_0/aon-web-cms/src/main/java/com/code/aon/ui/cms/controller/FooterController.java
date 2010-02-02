package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Footer;
import com.code.aon.cms.FooterBannerCategory;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.FormUtil;

public class FooterController extends BasicI18nController implements ICMSConstants {

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

	public void onSelectBannerCategories(ActionEvent event) throws ManagerBeanException, ExpressionException {
		FooterBannerCategoryController c = (FooterBannerCategoryController)FormUtil.getController(FOOTER_BANNNER_CATEGORY);
		IManagerBean moBean = BeanManager.getManagerBean(FooterBannerCategory.class);
		Footer footer = (Footer) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.FOOTER_BANNER_CATEGORY_FOOTER_ID), "" + footer.getId());
		c.setCurrentFooter(footer);
		c.setCriteria(criteria);
		c.onSearch(event);
	}

}