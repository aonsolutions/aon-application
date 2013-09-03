package com.code.aon.ui.config.event;

import static com.code.aon.ui.config.controller.ConfigConstants.BUNDLE_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.CONFIG_DATE_OVERLAP;
import static com.code.aon.ui.config.controller.ConfigConstants.CONFIG_INVALID_END_DATE;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TaxDetailControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		TaxDetail taxDetail = (TaxDetail) event.getController().getTo();
		if (taxDetail.getStartDate().after(taxDetail.getEndDate())) {
			throw new ControllerListenerException(AonUtil.getMessage(BUNDLE_NAME, CONFIG_INVALID_END_DATE));
		}

		try {
			IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxBean.getFieldName(IEntityAlias.TAX_ID), taxDetail.getTax().getId());
			criteria.addLessThanOrEqualExpression(taxBean.getFieldName(IEntityAlias.TAX_START_DATE), taxDetail.getEndDate());
			if (taxBean.getCount(criteria) > 0) {
				throw new ControllerListenerException(AonUtil.getMessage(BUNDLE_NAME, CONFIG_DATE_OVERLAP));
			}

			IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
			criteria = new Criteria();
			criteria.addEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_TAX_ID), taxDetail.getTax().getId());
			criteria.addNotEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_ID), taxDetail.getId());
			criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_START_DATE), taxDetail.getEndDate());
			criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_END_DATE), taxDetail.getStartDate());
			if (taxDetailBean.getCount(criteria) > 0) {
				throw new ControllerListenerException(AonUtil.getMessage(BUNDLE_NAME, CONFIG_DATE_OVERLAP));
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
	
}