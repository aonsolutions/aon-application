package com.code.aon.ui.product.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Tax;
import com.code.aon.product.TaxDetail;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class TaxDetailControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		TaxDetail taxDetail = (TaxDetail) event.getController().getTo();
		if (taxDetail.getStartDate().after(taxDetail.getEndDate())) {
			throw new ControllerListenerException("Fecha Final incorrecta. No puede ser anterior a la Inicial.");
		}

		try {
			IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxBean.getFieldName(IProductAlias.TAX_ID), taxDetail.getTax().getId());
			criteria.addLessThanOrEqualExpression(taxBean.getFieldName(IProductAlias.TAX_START_DATE), taxDetail.getEndDate());
			if (taxBean.getCount(criteria) > 0) {
				throw new ControllerListenerException("Las Fechas indicadas no son correctas. Se solapan con algún otro periodo.");
			}

			IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
			criteria = new Criteria();
			criteria.addEqualExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_TAX_ID), taxDetail.getTax().getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_ID), taxDetail.getId()));
			criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_START_DATE), taxDetail.getEndDate());
			criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_END_DATE), taxDetail.getStartDate());
			if (taxDetailBean.getCount(criteria) > 0) {
				throw new ControllerListenerException("Las Fechas indicadas no son correctas. Se solapan con algún otro periodo.");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
	
}